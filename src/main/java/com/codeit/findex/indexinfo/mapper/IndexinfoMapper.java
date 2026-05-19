package com.codeit.findex.indexinfo.mapper;

import com.codeit.findex.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.indexinfo.dto.IndexInfoDto;
import com.codeit.findex.indexinfo.dto.IndexInfoSummaryDto;
import com.codeit.findex.indexinfo.entity.Findex;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndexinfoMapper {

  List<IndexInfoDto> toDtoList(List<Findex> findexList);

  @Mapping(source = "basePointInTime", target = "basePntm")
  Findex toEntity(IndexInfoCreateRequest request);

  IndexInfoDto toDto(Findex findex);

  List<IndexInfoSummaryDto> toSummaryDtoList(List<Findex> findexList);

}