package com.codeit.findex.indexdata.mapper;

import com.codeit.findex.global.common.PeriodType;
import com.codeit.findex.indexdata.dto.ChartDataPoint;
import com.codeit.findex.indexdata.dto.IndexChartDto;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataUpdateRequest;
import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexinfo.entity.Findex;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
  @Mapping(target = "id", source = "entity.id")
  @Mapping(target = "findex", source = "entity.findex")
  @Mapping(target = "baseDate", source = "entity.baseDate")
  @Mapping(target = "sourceType", source = "entity.sourceType")
  @Mapping(target = "createdAt", source = "entity.createdAt")
  @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
  @Mapping(target = "marketPrice", source = "request.marketPrice")
  @Mapping(target = "closePrice", source = "request.closingPrice")
  @Mapping(target = "highPrice", source = "request.highPrice")
  @Mapping(target = "lowPrice", source = "request.lowPrice")
  @Mapping(target = "versus", source = "request.versus")
  @Mapping(target = "fluctuationRate", source = "request.fluctuationRate")
  @Mapping(target = "tradingQuantity", source = "request.tradingQuantity")
  @Mapping(target = "tradingPrice", source = "request.tradingPrice")
  @Mapping(target = "marketTotalamount", source = "request.marketTotalAmount")
  IndexData toEntity(IndexDataUpdateRequest request, IndexData entity);

  @Mapping(target = "indexInfoId", source = "indexData.findex.id")
  @Mapping(target = "closingPrice", source = "indexData.closePrice")
  @Mapping(target = "marketTotalAmount", source = "indexData.marketTotalamount")
  IndexDataDto toDto(IndexData indexData);


  @Mapping(target = "id", source = "indexData.id")
  @Mapping(target = "indexInfoId", source = "indexData.findex.id")
  @Mapping(target = "closingPrice", source = "indexData.closePrice")
  @Mapping(target = "marketTotalAmount", source = "indexData.marketTotalamount")
  IndexDataDto toIndexDataDto(IndexData indexData);

  default IndexPerformanceDto toIndexPerformanceDto(IndexData indexData, IndexData previousIndexData){
    BigDecimal versus;
    BigDecimal fluctuationRate;
    BigDecimal beforePrice;
    if(previousIndexData == null){
      versus = BigDecimal.ZERO;
      fluctuationRate = BigDecimal.ZERO;
      beforePrice = BigDecimal.ZERO;
    }else{
      versus = indexData.getClosePrice().subtract(previousIndexData.getClosePrice());
      fluctuationRate = versus.divide(previousIndexData.getClosePrice(), 6, RoundingMode.HALF_UP)
          .multiply(BigDecimal.valueOf(100))
          .setScale(2, RoundingMode.HALF_UP);
      beforePrice = previousIndexData.getClosePrice();
    }
    return new IndexPerformanceDto(
        indexData.getFindex().getId(),
        indexData.getFindex().getIndexClassification(),
        indexData.getFindex().getIndexName(),
        versus,
        fluctuationRate,
        indexData.getClosePrice(),
        beforePrice);
  }

  default List<Object> toCsv(IndexData indexData){
    return List.of(
        indexData.getBaseDate(),
        indexData.getMarketPrice(),
        indexData.getClosePrice(),
        indexData.getHighPrice(),
        indexData.getLowPrice(),
        indexData.getVersus(),
        indexData.getFluctuationRate(),
        indexData.getTradingQuantity(),
        indexData.getTradingPrice(),
        indexData.getMarketTotalamount()
    );
  }
}
