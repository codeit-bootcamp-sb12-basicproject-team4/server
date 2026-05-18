package com.codeit.findex.indexdata.service;

import com.codeit.findex.global.common.PeriodType;
import com.codeit.findex.global.common.SourceType;
import com.codeit.findex.indexdata.dto.CursorPageResponseIndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexDataServiceImpl implements IndexDataService {

  private final IndexDataRepository indexDataRepository;
  private final IndexinfoRepository indexinfoRepository;
  private final IndexDataMapper indexDataMapper;

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
    indexDataRepository.findByIdAndBaseDate(findex.getId(), request.baseDate())
        .orElseThrow(
            () -> new IllegalArgumentException("이미 해당 날짜에 데이터가 존재합니다: " + request.baseDate()));

    IndexData indexData = new IndexData(findex, request.baseDate(), SourceType.USER,
        request.marketPrice(), request.closingPrice(), request.highPrice(), request.lowPrice(),
        request.versus(), request.fluctuationRate(), request.tradingQuantity(),
        request.tradingPrice(), request.marketTotalAmount());
    IndexData savedIndexData = indexDataRepository.save(indexData);
    return indexDataMapper.toIndexDataDto(savedIndexData);
  }

  @Override
  public List<IndexPerformanceDto> getFavoriteIndexPerformance(PeriodType periodType) {
    List<Findex> favoriteFindex = indexinfoRepository.findAllByFavoriteTrue();
    log.info("favoriteFindex: {}", favoriteFindex.get(0).getIndexName());
    List<List<IndexData>> period = new ArrayList<>();
    if(periodType == PeriodType.DAILY){
      period = favoriteFindex.stream()
          .map(findex -> indexDataRepository.findAllByFindexInAndBaseDateBetween(
              findex, LocalDate.now().minusDays(1), LocalDate.now()))
          .toList();
    }else if(periodType == PeriodType.WEEKLY){
      period = favoriteFindex.stream()
          .map(findex -> indexDataRepository.findAllByFindexInAndBaseDateBetween(
              findex, LocalDate.now().minusWeeks(1), LocalDate.now()))
          .toList();
    }else if(periodType == PeriodType.MONTHLY){
      period = favoriteFindex.stream()
          .map(findex -> indexDataRepository.findAllByFindexInAndBaseDateBetween(
              findex, LocalDate.now().minusMonths(1), LocalDate.now()))
          .toList();
    }
    List<IndexPerformanceDto> indexPerformanceDtoList = new ArrayList<>();
    for(List<IndexData> indexDataList : period){
      if (indexDataList.size() == 1){
        indexPerformanceDtoList.add(indexDataMapper.toIndexPerformanceDto(indexDataList.get(0), null));
      } else if (indexDataList.size() > 1) {
        int size = indexDataList.size();
        indexPerformanceDtoList.add(indexDataMapper.toIndexPerformanceDto(indexDataList.get(0), indexDataList.get(size-1)));
      }
    }
    return indexPerformanceDtoList.stream()
        .sorted(Comparator.comparing(IndexPerformanceDto::fluctuationRate).reversed())
        .toList();
  }

  @Override
  public byte[] downloadIndexData(UUID indexInfoId, LocalDate startDate, LocalDate endDate,
      String sortField, String sortDirection) {
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
