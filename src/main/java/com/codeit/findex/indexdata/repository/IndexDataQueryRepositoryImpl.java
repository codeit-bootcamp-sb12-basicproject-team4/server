package com.codeit.findex.indexdata.repository;

import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.entity.QIndexData;
import com.codeit.findex.indexinfo.entity.QFindex;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

}
