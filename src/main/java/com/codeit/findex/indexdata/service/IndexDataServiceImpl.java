package com.codeit.findex.indexdata.service;

import com.codeit.findex.global.common.PeriodType;
import com.codeit.findex.global.common.UnitPeriodType;
import com.codeit.findex.global.common.SourceType;
import com.codeit.findex.indexdata.dto.ChartDataPoint;
import com.codeit.findex.indexdata.dto.CursorPageResponseIndexDataDto;
import com.codeit.findex.indexdata.dto.IndexChartDto;
import com.codeit.findex.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataUpdateRequest;
import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
import com.codeit.findex.indexdata.dto.RankedIndexPerformanceDto;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.mapper.IndexDataMapper;
import com.codeit.findex.indexdata.repository.IndexDataRepository;
import com.codeit.findex.indexinfo.entity.Findex;
import com.codeit.findex.indexinfo.repository.IndexinfoRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexDataServiceImpl implements IndexDataService {

  private final IndexDataRepository indexDataRepository;
  private final IndexDataMapper indexDataMapper;
  private final IndexinfoRepository indexinfoRepository;

  @Override
  public IndexChartDto getIndexChart(UUID indexInfoId, PeriodType periodType) {

    List<IndexData> rawChartData = indexDataRepository.findAllByFindexIdWithFindex(indexInfoId);

    if (rawChartData.isEmpty()) {
      throw new NoSuchElementException("해당 지수에 대한 데이터가 존재하지 않습니다");
    }

    List<IndexData> chartData = aggregateByPeriod(rawChartData, periodType);
    Findex findex = chartData.get(0).getFindex();

    List<ChartDataPoint> dataPoints = indexDataMapper.toChartDataPointList(chartData);

    List<ChartDataPoint> ma5DataPoints = calculateMovingAverage(chartData, 5);

    List<ChartDataPoint> ma20DataPoints = calculateMovingAverage(chartData, 20);

    return indexDataMapper.toIndexChartDto(
        findex,
        periodType,
        dataPoints,
        ma5DataPoints,
        ma20DataPoints
    );
  }

  @Override
  public List<RankedIndexPerformanceDto> getIndexPerformanceRank(
      UUID indexInfoId,
      UnitPeriodType unitPeriodType,
      Integer limit
  ) {

    List<IndexPerformanceDto> performances =
        indexDataRepository.findPerformanceRanking(
            indexInfoId,
            unitPeriodType,
            limit
        );

    List<RankedIndexPerformanceDto> result = new ArrayList<>();

    for (int i = 0; i < performances.size(); i++) {
      result.add(
          new RankedIndexPerformanceDto(
              performances.get(i),
              i + 1
          )
      );
    }

    return result;
  }

  @Override
  @Transactional
  public IndexDataDto updateIndexData(
      UUID id,
      IndexDataUpdateRequest request
  ) {

    IndexData indexData = indexDataRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(
            "수정할 지수 데이터를 찾을 수 없습니다."
        ));

    IndexData updatedData = indexDataMapper.toEntity(request, indexData);
    IndexData savedData = indexDataRepository.save(updatedData);

    return indexDataMapper.toDto(savedData);
  }


  @Override
  @Transactional
  public void deleteIndexData(UUID id) {

    if (!indexDataRepository.existsById(id)) {
      throw new EntityNotFoundException(
          "삭제할 지수 데이터를 찾을 수 없습니다."
      );
    }

    indexDataRepository.deleteById(id);
  }

  private List<IndexData> aggregateByPeriod(
      List<IndexData> data,
      PeriodType periodType
  ) {

    if (periodType == PeriodType.MONTHLY) {
      return data;
    }

    Map<String, IndexData> grouped =
        data.stream()
            .collect(Collectors.toMap(
                d -> getGroupKey(d.getBaseDate(), periodType),
                Function.identity(),
                (a, b) ->
                    a.getBaseDate().isAfter(b.getBaseDate())
                        ? a
                        : b,

                LinkedHashMap::new
            ));

    return new ArrayList<>(grouped.values());
  }

  private String getGroupKey(
      LocalDate date,
      PeriodType periodType
  ) {

    switch (periodType) {

      case MONTHLY:
        return date.getYear()
            + "-"
            + date.getMonthValue();

      case QUARTERLY:
        int quarter =
            (date.getMonthValue() - 1) / 3 + 1;

        return date.getYear()
            + "-Q"
            + quarter;

      case YEARLY:
        return String.valueOf(date.getYear());

      default:
        return date.toString();
    }
  }


  private List<ChartDataPoint> calculateMovingAverage(
      List<IndexData> data,
      int period
  ) {

    List<ChartDataPoint> result = new ArrayList<>();

    for (int i = 0; i < data.size(); i++) {

      LocalDate date = data.get(i).getBaseDate();
      double currentPrice =
          data.get(i)
              .getClosePrice()
              .doubleValue();

      if (i >= period - 1) {

        double sum = 0;

        for (int j = i - (period - 1); j <= i; j++) {
          sum += data.get(j)
              .getClosePrice()
              .doubleValue();
        }

        result.add(
            new ChartDataPoint(
                date,
                sum / period
            )
        );

      } else {

        result.add(
            new ChartDataPoint(
                date,
                currentPrice
            )
        );
      }
    }

    return result;
  }

  @Override
  public CursorPageResponseIndexDataDto getIndexDataList(UUID indexInfoId, LocalDate startDate,
      LocalDate endDate, UUID idAfter, String cursor, String sortField, String sortDirection,
      int size) {

    if (!"asc".equalsIgnoreCase(sortDirection) && !"desc".equalsIgnoreCase(sortDirection)) {
      throw new IllegalArgumentException("Invalid value '" + sortDirection
          + "' for orders given; Has to be either 'desc' or 'asc' (case insensitive)");
    }
    if (size <= 0) {
      throw new IllegalArgumentException("Page size must not be less than one");
    }

    Long totalElements = indexDataRepository.countByFilters(
        indexInfoId, startDate, endDate, idAfter, cursor);

    Slice<IndexData> indexDataSlice = indexDataRepository.findAllWithCursor(
        indexInfoId, startDate, endDate, idAfter, cursor, sortField, sortDirection, size);

    List<IndexDataDto> indexDataDtoList = indexDataSlice.getContent().stream()
        .map(indexDataMapper::toIndexDataDto)
        .toList();

    int lastIndex = indexDataDtoList.size();
    return new CursorPageResponseIndexDataDto(indexDataDtoList,
        indexDataDtoList.isEmpty() ? null : String.valueOf(
            indexDataDtoList.get(lastIndex - 1).baseDate()),
        indexDataDtoList.isEmpty() ? null : indexDataDtoList.get(lastIndex - 1).id(),
        size, totalElements, indexDataSlice.hasNext());
  }

  @Override
  @Transactional
  public IndexDataDto create(IndexDataCreateRequest request) {
    Findex findex = indexinfoRepository.findById(request.indexInfoId())
        .orElseThrow(() -> new EntityNotFoundException(
            "지수 정보를 찾을 수 없습니다. ID:  " + request.indexInfoId()));
    if (indexDataRepository.existsByFindexIdAndBaseDate(findex.getId(), request.baseDate())) {
      throw new IllegalStateException("이미 해당 날짜에 데이터가 존재합니다: " + request.baseDate());
    }

    IndexData indexData = new IndexData(findex, request.baseDate(), SourceType.USER,
        request.marketPrice(), request.closingPrice(), request.highPrice(), request.lowPrice(),
        request.versus(), request.fluctuationRate(), request.tradingQuantity(),
        request.tradingPrice(), request.marketTotalAmount());
    IndexData savedIndexData = indexDataRepository.save(indexData);
    return indexDataMapper.toIndexDataDto(savedIndexData);
  }

  @Override
  public List<IndexPerformanceDto> getFavoriteIndexPerformance(UnitPeriodType unitPeriodType) {
    List<Findex> favoriteFindex = indexinfoRepository.findAllByFavoriteTrue();

    List<List<IndexData>> period = new ArrayList<>();
    if (unitPeriodType == UnitPeriodType.DAILY) {
      period = favoriteFindex.stream()
          .map(findex -> indexDataRepository.findAllByFindexInAndBaseDateBetween(
              findex, LocalDate.now().minusDays(1), LocalDate.now()))
          .toList();
    } else if (unitPeriodType == UnitPeriodType.WEEKLY) {
      period = favoriteFindex.stream()
          .map(findex -> indexDataRepository.findAllByFindexInAndBaseDateBetween(
              findex, LocalDate.now().minusWeeks(1), LocalDate.now()))
          .toList();
    } else if (unitPeriodType == UnitPeriodType.MONTHLY) {
      period = favoriteFindex.stream()
          .map(findex -> indexDataRepository.findAllByFindexInAndBaseDateBetween(
              findex, LocalDate.now().minusMonths(1), LocalDate.now()))
          .toList();
    }
    List<IndexPerformanceDto> indexPerformanceDtoList = new ArrayList<>();
    for (List<IndexData> indexDataList : period) {
      if (indexDataList.size() == 1) {
        indexPerformanceDtoList.add(
            indexDataMapper.toIndexPerformanceDto(indexDataList.get(0), null));
      } else if (indexDataList.size() > 1) {
        int size = indexDataList.size();
        indexPerformanceDtoList.add(indexDataMapper.toIndexPerformanceDto(indexDataList.get(0),
            indexDataList.get(size - 1)));
      }
    }
    return indexPerformanceDtoList.stream()
        .sorted(Comparator.comparing(IndexPerformanceDto::fluctuationRate).reversed())
        .toList();
  }

  @Override
  public byte[] downloadIndexData(UUID indexInfoId, LocalDate startDate, LocalDate endDate,
      String sortField, String sortDirection) {
    if (!"desc".equalsIgnoreCase(sortDirection) && !"asc".equalsIgnoreCase(sortDirection)) {
      throw new IllegalArgumentException("Invalid value '" + sortDirection
          + "' for orders given; Has to be either 'desc' or 'asc' (case insensitive)");
    }
    List<IndexData> indexDataDtoList = indexDataRepository.findAllByCondition(indexInfoId,
        startDate, endDate, sortField, sortDirection);
    String[] header = {"기준일자", "시가", "종가", "고가", "저가", "전일대비등락", "등락률", "거래량", "거래대금", "시가총액"};

    try {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      bytes.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

      Writer writer = new OutputStreamWriter(bytes, StandardCharsets.UTF_8);
      CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
          .setHeader(header)
          .build());

      for (IndexData data : indexDataDtoList) {
        printer.printRecord(indexDataMapper.toCsv(data));
      }

      printer.flush();
      return bytes.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("CSV 파일 생성 중 오류가 발생했습니다.", e);
    }
  }
}

