package com.codeit.findex.indexdata.service;

import com.codeit.findex.global.common.SourceType;
import com.codeit.findex.indexdata.dto.CursorPageResponseIndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.mapper.IndexDataMapper;
import com.codeit.findex.indexdata.repository.IndexDataRepository;
import com.codeit.findex.indexinfo.entity.Findex;
import com.codeit.findex.indexinfo.repository.IndexinfoRepository;
import jakarta.persistence.EntityNotFoundException;
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
  private final IndexinfoRepository indexinfoRepository;
  private final IndexDataMapper indexDataMapper;

  @Override
  public CursorPageResponseIndexDataDto findIndexData(UUID indexInfoId, LocalDate startDate,
      LocalDate endDate, UUID idAfter, String cursor, String sortField, String sortDirection,
      int size) {

    if (!"asc".equalsIgnoreCase(sortDirection) && !"desc".equalsIgnoreCase(sortDirection)) {
      throw new IllegalArgumentException("Invalid value '" + sortDirection
          + "' for orders given; Has to be either 'desc' or 'asc' (case insensitive)");
    }
    if (size <= 0) {
      throw new IllegalArgumentException("Page size must not be less than one");
    }

    Long totalElements = indexDataRepository.countByFilters(
        indexInfoId, startDate, endDate, idAfter, cursor, sortField, sortDirection, size);

    Slice<IndexData> indexDataSlice = indexDataRepository.findAllSlice(
        indexInfoId, startDate, endDate, idAfter, cursor, sortField, sortDirection, size);

    List<IndexDataDto> indexDataDtoList = indexDataSlice.getContent().stream()
        .map(indexDataMapper::toIndexDataDto)
        .toList();

    int lastIndex = indexDataDtoList.size();
    return new CursorPageResponseIndexDataDto(indexDataDtoList,
        indexDataDtoList.isEmpty() ? null : String.valueOf(
            indexDataDtoList.get(lastIndex - 1).baseDate()),
        indexDataDtoList.isEmpty() ? null : indexDataDtoList.get(lastIndex - 1).id(),
        size, totalElements, indexDataSlice.hasNext());
  }

  @Override
  @Transactional
  public IndexDataDto create(IndexDataCreateRequest request) {
    Findex findex = indexinfoRepository.findById(request.indexInfoId())
        .orElseThrow(() -> new EntityNotFoundException(
            "지수 정보를 찾을 수 없습니다. ID:  " + request.indexInfoId()));
    indexDataRepository.findByIdAndBaseDate(findex.getId(), request.baseDate())
        .orElseThrow(() -> new IllegalArgumentException("이미 해당 날짜에 데이터가 존재합니다: " + request.baseDate()));

    IndexData indexData = new IndexData(findex, request.baseDate(), SourceType.USER,
        request.marketPrice(), request.closingPrice(), request.highPrice(), request.lowPrice(),
        request.versus(), request.fluctuationRate(), request.tradingQuantity(),
        request.tradingPrice(), request.marketTotalAmount());
    IndexData savedIndexData = indexDataRepository.save(indexData);
    return indexDataMapper.toIndexDataDto(savedIndexData);
  }
}
