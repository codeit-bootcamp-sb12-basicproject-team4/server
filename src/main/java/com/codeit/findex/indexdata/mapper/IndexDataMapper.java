package com.codeit.findex.indexdata.mapper;

import com.codeit.findex.global.common.PeriodType;
import com.codeit.findex.indexdata.dto.ChartDataPoint;
import com.codeit.findex.indexdata.dto.IndexChartDto;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexinfo.entity.Findex;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndexDataMapper {
  @Mapping(target = "date", source = "indexData.baseDate")
  @Mapping(target = "value", source = "indexData.closePrice")
  ChartDataPoint toChartDataPoint(IndexData indexData);

  List<ChartDataPoint> toChartDataPointList(List<IndexData> indexDataList);


  @Mapping(target = "indexInfoID", source = "findex.id")
  @Mapping(target = "indexClassification", source = "findex.indexClassification")
  @Mapping(target = "indexName", source = "findex.indexName")
  @Mapping(target = "periodType", source = "periodType")
  @Mapping(target = "dataPoints", source = "dataPoints")
  @Mapping(target = "ma5DataPoints", source = "ma5DataPoints")
  @Mapping(target = "ma20DataPoints", source = "ma20DataPoints")
  IndexChartDto toIndexChartDto(
      Findex findex,
      PeriodType periodType,
      List<ChartDataPoint> dataPoints,
      List<ChartDataPoint> ma5DataPoints,
      List<ChartDataPoint> ma20DataPoints
  );
}
