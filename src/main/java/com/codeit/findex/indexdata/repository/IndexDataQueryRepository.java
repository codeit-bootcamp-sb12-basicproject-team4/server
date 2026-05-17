package com.codeit.findex.indexdata.repository;

import com.codeit.findex.indexdata.entity.IndexData;
import java.util.List;
import java.util.UUID;

public interface IndexDataQueryRepository {
  List<IndexData> findAllByFindexIdWithFindex(UUID findexId);
}
