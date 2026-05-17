package com.codeit.findex.integrationlog.repository;

import com.codeit.findex.integrationlog.dto.IntegrationLogSearchRequest;
import com.codeit.findex.integrationlog.entity.IntegrationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IntegrationLogRepositoryCustom {
  Page<IntegrationLog> searchPageSorted(IntegrationLogSearchRequest request, Pageable pageable);
}
