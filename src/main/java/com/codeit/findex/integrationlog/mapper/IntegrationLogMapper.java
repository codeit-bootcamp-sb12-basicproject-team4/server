package com.codeit.findex.integrationlog.mapper;

import com.codeit.findex.indexinfo.entity.Findex;
import com.codeit.findex.indexinfo.repository.IndexinfoRepository;
import com.codeit.findex.integrationlog.dto.IndexResponse;
import com.codeit.findex.integrationlog.entity.IntegrationLog;
import java.time.Instant;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", imports = { Instant.class })
public abstract class IntegrationLogMapper {

  @Autowired
  protected IndexinfoRepository indexinfoRepository;

  @Mapping(target = "id", ignore = true) // 로그의 PK는 자동생성
  @Mapping(target = "findex", source = "indexInfoId", qualifiedByName = "toFindexProxy")
  @Mapping(target = "jobTime", source = "jobTime")
  public abstract IntegrationLog toEntity(IndexResponse dto);

  @Named("toFindexProxy")
  protected Findex toFindexProxy(UUID id) {
    return indexinfoRepository.getReferenceById(id);
  }
}
