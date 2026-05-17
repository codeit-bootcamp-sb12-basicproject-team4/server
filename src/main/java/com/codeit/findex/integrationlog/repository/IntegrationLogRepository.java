package com.codeit.findex.integrationlog.repository;

import com.codeit.findex.integrationlog.entity.IntegrationLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationLogRepository extends JpaRepository<IntegrationLog, UUID>, IntegrationLogRepositoryCustom {

}
