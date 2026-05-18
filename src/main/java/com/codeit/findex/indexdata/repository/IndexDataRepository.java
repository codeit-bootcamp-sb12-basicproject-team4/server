package com.codeit.findex.indexdata.repository;

import com.codeit.findex.indexdata.entity.IndexData;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexDataRepository extends JpaRepository<IndexData, UUID>, IndexDataQueryRepository {

  Optional<IndexData> findByIdAndBaseDate(UUID id, @NotNull LocalDate localDate);
}
