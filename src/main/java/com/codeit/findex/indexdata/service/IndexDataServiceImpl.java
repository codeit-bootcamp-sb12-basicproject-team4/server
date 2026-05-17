package com.codeit.findex.indexdata.service;

import com.codeit.findex.indexdata.dto.ChartDataPoint;
import com.codeit.findex.indexdata.dto.IndexChartDto;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.mapper.IndexDataMapper;
import com.codeit.findex.indexdata.repository.IndexDataRepository;
import com.codeit.findex.indexinfo.entity.Findex;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
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
  public IndexChartDto getIndexChart(UUID indexInfoId, String periodType) {

    if (periodType == null || periodType.trim().isEmpty()) {
      throw new IllegalArgumentException("부서 코드는 필수입니다.");
    }

    List<IndexData> rawChartData = indexDataRepository.findAllByFindexIdWithFindex(indexInfoId);

    if (rawChartData.isEmpty()) {
      throw new NoSuchElementException("부서 코드는 필수입니다.");
    }

    Findex findex = rawChartData.get(0).getFindex();

    List<ChartDataPoint> dataPoints = indexDataMapper.toChartDataPointList(rawChartData);

    List<ChartDataPoint> ma5DataPoints = new ArrayList<>();
    List<ChartDataPoint> ma20DataPoints = new ArrayList<>();

    for (int i = 0; i < rawChartData.size(); i++) {
      IndexData current = rawChartData.get(i);
      LocalDate date = current.getBaseDate();
      double currentPrice = current.getClosePrice().doubleValue();

      if (i >= 4) {
        double sum = 0;
        for (int j = i - 4; j <= i; j++) {
          sum += rawChartData.get(j).getClosePrice().doubleValue();
        }
        ma5DataPoints.add(new ChartDataPoint(date, sum / 5.0));
      } else {
        ma5DataPoints.add(new ChartDataPoint(date, currentPrice));
      }

      if (i >= 19) {
        double sum = 0;
        for (int j = i - 19; j <= i; j++) {
          sum += rawChartData.get(j).getClosePrice().doubleValue();
        }
        ma20DataPoints.add(new ChartDataPoint(date, sum / 20.0));
      } else {
        ma20DataPoints.add(new ChartDataPoint(date, currentPrice));
      }
    }

    return indexDataMapper.toIndexChartDto(findex, periodType, dataPoints, ma5DataPoints, ma20DataPoints);
  }
  }
