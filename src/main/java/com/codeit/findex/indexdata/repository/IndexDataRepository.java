package com.codeit.findex.indexdata.repository;

import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexinfo.entity.Findex;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IndexDataRepository extends JpaRepository<IndexData, UUID>, IndexDataQueryRepository {

  Optional<IndexData> findByIdAndBaseDate(UUID id, @NotNull LocalDate localDate);

  @Query("""
          select i
          from IndexData i
          where i.findex = :favoriteFindex
          and i.baseDate between :localDate and :now
          order by i.baseDate desc
          """)
  List<IndexData> findAllByFindexInAndBaseDateBetween(Findex favoriteFindex, LocalDate localDate, LocalDate now);
}
