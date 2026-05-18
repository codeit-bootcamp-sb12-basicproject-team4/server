package com.codeit.findex.autointegration.repository;

import com.codeit.findex.autointegration.entity.AutoIntegration;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AutoIntegrationRepository extends JpaRepository<AutoIntegration, UUID> {


  @Query("SELECT a FROM AutoIntegration a " +
      "WHERE (:findexId IS NULL OR a.findexId = :findexId) " +
      "AND (:isActive IS NULL OR a.isActive = :isActive) " +
      "AND (:lastId IS NULL OR CAST(a.findexId AS string) > :lastId) " +
      "ORDER BY a.findexId ASC")
  List<AutoIntegration> findByFiltersAndCursor(
      @Param("findexId") UUID findexId,
      @Param("isActive") Boolean isActive,
      @Param("lastId") String lastId,
      Pageable pageable
  );


  List<AutoIntegration> findByIsActiveTrue();
}