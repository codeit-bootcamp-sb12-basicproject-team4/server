package com.codeit.findex.indexdata.mapper;

import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.entity.IndexData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndexDataMapper {

  @Mapping(target = "id", source = "indexData.id")
  @Mapping(target = "indexInfoId", source = "indexData.findex.id")
  @Mapping(target = "closingPrice", source = "indexData.closePrice")
  @Mapping(target = "marketTotalAmount", source = "indexData.marketTotalamount")
  IndexDataDto toIndexDataDto(IndexData indexData);
}
