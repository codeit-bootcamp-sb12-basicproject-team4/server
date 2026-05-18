package com.codeit.findex.indexdata.mapper;

import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
import com.codeit.findex.indexdata.entity.IndexData;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndexDataMapper {

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
