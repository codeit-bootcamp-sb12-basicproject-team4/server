package com.codeit.findex.integrationlog.repository;

import com.codeit.findex.integrationlog.dto.IntegrationLogSearchRequest;
import com.codeit.findex.integrationlog.entity.IntegrationLog;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class IntegrationLogRepositoryImpl implements IntegrationLogRepositoryCustom {
  private final JPAQueryFactory queryFactory;

  @Override
  public Page<IntegrationLog> searchPageSorted(IntegrationLogSearchRequest request,
      Pageable pageable) {
    return null;
  }
}
