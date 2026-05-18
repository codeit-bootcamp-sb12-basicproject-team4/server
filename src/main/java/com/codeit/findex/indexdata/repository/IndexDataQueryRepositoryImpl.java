package com.codeit.findex.indexdata.repository;

import com.codeit.findex.global.common.PeriodType;
import com.querydsl.core.types.Projections;
import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.entity.QIndexData;
import com.codeit.findex.indexinfo.entity.QFindex;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IndexDataQueryRepositoryImpl implements IndexDataQueryRepository {

  private final JPAQueryFactory queryFactory;

  private static final QIndexData indexData = QIndexData.indexData;
  private static final QFindex findex = QFindex.findex;

  @Override
  public List<IndexData> findAllByFindexIdWithFindex(UUID findexId) {
    return queryFactory
        .selectFrom(indexData)
        .join(indexData.findex, findex).fetchJoin()
        .where(indexData.findex.id.eq(findexId))
        .orderBy(indexData.baseDate.asc())
        .fetch();
  }

  @Override
  public List<IndexPerformanceDto> findPerformanceRanking(
      UUID indexInfoId,
      PeriodType periodType,
      Integer limit
  ) {

    LocalDate latestDate = queryFactory
        .select(indexData.baseDate.max())
        .from(indexData)
        .fetchOne();

    if (latestDate == null) {
      throw new IllegalStateException("지수 데이터가 존재하지 않습니다.");
    }

    LocalDate beforeDate = switch (periodType) {
      case DAILY -> latestDate.minusDays(1);
      case WEEKLY -> latestDate.minusWeeks(1);
      case MONTHLY -> latestDate.minusMonths(1);
    };

    QIndexData current = new QIndexData("current");
    QIndexData before = new QIndexData("before");

    return queryFactory
        .select(
            Projections.constructor(
                IndexPerformanceDto.class,
                findex.id,
                findex.indexClassification,
                findex.indexName,
                current.closePrice
                    .subtract(before.closePrice)
                    .doubleValue(),
                current.closePrice
                    .subtract(before.closePrice)
                    .divide(before.closePrice)
                    .multiply(100)
                    .doubleValue(),
                current.closePrice.doubleValue(),
                before.closePrice.doubleValue()
            )
        )
        .from(current)
        .join(current.findex, findex)
        .join(before)
        .on(
            before.findex.id.eq(current.findex.id),
            before.baseDate.eq(beforeDate)
        )
        .where(
            current.baseDate.eq(latestDate),
            indexInfoId != null
                ? current.findex.id.eq(indexInfoId)
                : null
        )
        .orderBy(
            current.closePrice
                .subtract(before.closePrice)
                .divide(before.closePrice)
                .multiply(100)
                .desc()
        )
        .limit(limit)
        .fetch();
  }
}
