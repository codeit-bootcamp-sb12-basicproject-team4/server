package com.codeit.findex.indexdata.service;

import com.codeit.findex.global.common.PeriodType;
import com.codeit.findex.indexdata.dto.ChartDataPoint;
import com.codeit.findex.indexdata.dto.IndexChartDto;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataUpdateRequest;
import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
import com.codeit.findex.indexdata.dto.RankedIndexPerformanceDto;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.mapper.IndexDataMapper;
import com.codeit.findex.indexdata.repository.IndexDataRepository;
import com.codeit.findex.indexinfo.entity.Findex;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexDataServiceImpl implements IndexDataService {

  private final IndexDataRepository indexDataRepository;
  private final IndexDataMapper indexDataMapper;

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
      PeriodType periodType,
      Integer limit
  ) {

    List<IndexPerformanceDto> performances =
        indexDataRepository.findPerformanceRanking(
            indexInfoId,
            periodType,
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

    return indexDataMapper.toDto(savedData);  }


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

    if (periodType == PeriodType.DAILY) {
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

}

