package com.codeit.findex.indexdata.repository;

import com.codeit.findex.global.common.PeriodType;
import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.entity.QIndexData;
import com.codeit.findex.indexinfo.entity.QFindex;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
      case QUARTERLY -> latestDate.minusMonths(3);
      case YEARLY -> latestDate.minusYears(1);
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
  
  @Override
  public Slice<IndexData> findAllWithCursor(UUID indexInfoId, LocalDate startDate, LocalDate endDate,
      UUID idAfter, String cursor, String sortField, String sortDirection, int size) {
    BooleanBuilder where = new BooleanBuilder();
    if (indexInfoId != null) {
      where.and(indexData.findex.id.eq(indexInfoId));
    }
    if (startDate != null) {
      where.and(indexData.baseDate.goe(startDate));
    }
    if (endDate != null) {
      where.and(indexData.baseDate.loe(endDate));
    }
    if (idAfter != null) {
      if ("desc".equalsIgnoreCase(sortDirection)) {
        where.and(indexData.id.lt(idAfter));
      } else {
        where.and(indexData.id.gt(idAfter));
      }
    }
    if (cursor != null) {
      if ("desc".equalsIgnoreCase(sortDirection)){
        where.and(indexData.baseDate.lt(LocalDate.parse(cursor)));
      }else {
        where.and(indexData.baseDate.gt(LocalDate.parse(cursor)));
      }
    }

    OrderSpecifier<?> order;
    switch (sortField) {
      case "baseDate" -> order = "desc".equalsIgnoreCase(sortDirection) ? indexData.baseDate.desc()
          : indexData.baseDate.asc();
      case "marketPrice" -> order =
          "desc".equalsIgnoreCase(sortDirection) ? indexData.marketPrice.desc()
              : indexData.marketPrice.asc();
      case "closingPrice" -> order =
          "desc".equalsIgnoreCase(sortDirection) ? indexData.closePrice.desc()
              : indexData.closePrice.asc();
      case "highPrice" ->
          order = "desc".equalsIgnoreCase(sortDirection) ? indexData.highPrice.desc()
              : indexData.highPrice.asc();
      case "lowPrice" -> order = "desc".equalsIgnoreCase(sortDirection) ? indexData.lowPrice.desc()
          : indexData.lowPrice.asc();
      case "versus" -> order =
          "desc".equalsIgnoreCase(sortDirection) ? indexData.versus.desc() : indexData.versus.asc();
      case "fluctuationRate" -> order =
          "desc".equalsIgnoreCase(sortDirection) ? indexData.fluctuationRate.desc()
              : indexData.fluctuationRate.asc();
      case "tradingQuantity" -> order =
          "desc".equalsIgnoreCase(sortDirection) ? indexData.tradingQuantity.desc()
              : indexData.tradingQuantity.asc();
      case "tradingPrice" -> order =
          "desc".equalsIgnoreCase(sortDirection) ? indexData.tradingPrice.desc()
              : indexData.tradingPrice.asc();
      case "marketTotalAmount" -> order =
          "desc".equalsIgnoreCase(sortDirection) ? indexData.marketTotalamount.desc()
              : indexData.marketTotalamount.asc();
      default -> throw new IllegalArgumentException("Invalid sort field: " + sortField);
    }

    List<IndexData> rows = queryFactory.selectFrom(indexData)
        .join(indexData.findex).fetchJoin()
        .where(where)
        .orderBy(order)
        .limit(size + 1)
        .fetch();

    boolean hasNext = rows.size() > size;

    List<IndexData> indexDataList = hasNext ? rows.subList(0, size) : rows;

    return new SliceImpl<>(indexDataList, PageRequest.ofSize(size), hasNext);
  }

  @Override
  public Long countByFilters(UUID indexInfoId, LocalDate startDate, LocalDate endDate, UUID idAfter,
      String cursor) {
    BooleanBuilder where = new BooleanBuilder();
    if (indexInfoId != null) {
      where.and(indexData.findex.id.eq(indexInfoId));
    }
    if (startDate != null) {
      where.and(indexData.baseDate.goe(startDate));
    }
    if (endDate != null) {
      where.and(indexData.baseDate.loe(endDate));
    }

    return queryFactory.select(indexData.id.count())
        .from(indexData)
        .join(indexData.findex)
        .where(where)
        .fetchOne();
  }

  @Override
  public List<IndexData> findAllByCondition(UUID indexInfoId, LocalDate startDate,
      LocalDate endDate, String sortField, String sortDirection) {
    BooleanBuilder where = new BooleanBuilder();
    if (indexInfoId != null) {
      where.and(indexData.findex.id.eq(indexInfoId));
    }
    if (startDate != null) {
      where.and(indexData.baseDate.goe(startDate));
    }
    if (endDate != null) {
      where.and(indexData.baseDate.loe(endDate));
    }

    OrderSpecifier<?> order;
    switch (sortField) {
      case "baseDate" -> order = "desc".equalsIgnoreCase(sortDirection) ? indexData.baseDate.desc()
          : indexData.baseDate.asc();
      case "marketPrice" -> order =
          "desc".equalsIgnoreCase(sortDirection) ? indexData.marketPrice.desc()
              : indexData.marketPrice.asc();
      case "closingPrice" -> order =
          "desc".equalsIgnoreCase(sortDirection) ? indexData.closePrice.desc()
              : indexData.closePrice.asc();
      case "highPrice" ->
          order = "desc".equalsIgnoreCase(sortDirection) ? indexData.highPrice.desc()
              : indexData.highPrice.asc();
      case "lowPrice" -> order = "desc".equalsIgnoreCase(sortDirection) ? indexData.lowPrice.desc()
          : indexData.lowPrice.asc();
      case "versus" -> order =
          "desc".equalsIgnoreCase(sortDirection) ? indexData.versus.desc() : indexData.versus.asc();
      case "fluctuationRate" -> order =
          "desc".equalsIgnoreCase(sortDirection) ? indexData.fluctuationRate.desc()
              : indexData.fluctuationRate.asc();
      case "tradingQuantity" -> order =
          "desc".equalsIgnoreCase(sortDirection) ? indexData.tradingQuantity.desc()
              : indexData.tradingQuantity.asc();
      case "tradingPrice" -> order =
          "desc".equalsIgnoreCase(sortDirection) ? indexData.tradingPrice.desc()
              : indexData.tradingPrice.asc();
      case "marketTotalAmount" -> order =
          "desc".equalsIgnoreCase(sortDirection) ? indexData.marketTotalamount.desc()
              : indexData.marketTotalamount.asc();
      default -> throw new IllegalArgumentException("Invalid sort field: " + sortField);
    }

    return queryFactory.selectFrom(indexData)
        .join(indexData.findex).fetchJoin()
        .where(where)
        .orderBy(order)
        .fetch();
  }
}
