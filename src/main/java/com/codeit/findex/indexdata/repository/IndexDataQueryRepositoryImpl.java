package com.codeit.findex.indexdata.repository;

import com.codeit.findex.global.common.PeriodType;
import com.querydsl.core.types.Projections;
import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.entity.QIndexData;
import com.codeit.findex.indexinfo.entity.QFindex;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
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
      return List.of();
    }

    LocalDate beforeDate = switch (periodType) {
      case DAILY -> latestDate.minusDays(1);
      case WEEKLY -> latestDate.minusWeeks(1);
      case MONTHLY -> latestDate.minusMonths(1);
    };

    QIndexData current = new QIndexData("current");
    QIndexData before = new QIndexData("before");

    NumberTemplate<BigDecimal> fluctuationRateExpr =
        Expressions.numberTemplate(
            BigDecimal.class,
            "ROUND((({0} - {1}) * 100 / {1}), 2)",
            current.closePrice,
            before.closePrice
        );

    return queryFactory
        .select(
            Projections.constructor(
                IndexPerformanceDto.class,
                findex.id,
                findex.indexClassification,
                findex.indexName,
                current.closePrice.subtract(before.closePrice),
                fluctuationRateExpr,
                current.closePrice,
                before.closePrice
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
        .orderBy(fluctuationRateExpr.desc())
        .limit(limit)
        .fetch();
  }
}
