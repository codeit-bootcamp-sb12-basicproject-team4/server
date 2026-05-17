package com.codeit.findex.indexinfo.repository;

import com.codeit.findex.indexinfo.entity.Findex;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexinfoRepository extends JpaRepository<Findex, UUID> {

  boolean existsByIndexClassificationAndIndexName(String indexClassification, String indexName);

  Optional<Findex> findByIndexClassificationAndIndexName(String indexClassification, String indexName);
}
