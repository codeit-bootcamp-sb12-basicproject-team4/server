package com.codeit.findex.indexdata.repository;

import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.entity.QIndexData;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

  private static final QIndexData indexData = QIndexData.indexData;
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Slice<IndexData> findAllSlice(UUID indexInfoId, LocalDate startDate, LocalDate endDate,
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

    List<IndexData> rows = jpaQueryFactory.selectFrom(indexData)
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
      String cursor, String sortField, String sortDirection, int size) {
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

    return jpaQueryFactory.select(indexData.id.count())
        .from(indexData)
        .join(indexData.findex)
        .where(where)
        .fetchOne();
  }
}
