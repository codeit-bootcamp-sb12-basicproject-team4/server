package com.codeit.findex.indexdata.service;

import com.codeit.findex.indexdata.dto.CursorPageResponseIndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.mapper.IndexDataMapper;
import com.codeit.findex.indexdata.repository.IndexDataRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexDataServiceImpl implements IndexDataService {

  private final IndexDataRepository indexDataRepository;
  private final IndexDataMapper indexDataMapper;

  @Override
  public CursorPageResponseIndexDataDto getIndexDataList(UUID indexInfoId, LocalDate startDate,
      LocalDate endDate, UUID idAfter, String cursor, String sortField, String sortDirection,
      int size) {
    Long totalElements = indexDataRepository.countByFilters(
        indexInfoId, startDate, endDate, idAfter, cursor, sortField, sortDirection, size);

    Slice<IndexData> indexDataSlice = indexDataRepository.findAllSlice(
        indexInfoId, startDate, endDate, idAfter, cursor, sortField, sortDirection, size);

    List<IndexDataDto> indexDataDtoList = indexDataSlice.getContent().stream()
        .map(indexDataMapper::toIndexDataDto)
        .toList();

    int lastIndex = indexDataDtoList.size();
    log.info("Fetched {} ", indexDataDtoList);

    return new CursorPageResponseIndexDataDto(indexDataDtoList,
        indexDataDtoList.isEmpty() ? null : String.valueOf(
            indexDataDtoList.get(lastIndex - 1).baseDate()),
        indexDataDtoList.isEmpty() ? null : indexDataDtoList.get(lastIndex - 1).id(),
        size, totalElements, indexDataSlice.hasNext());
  }
}
