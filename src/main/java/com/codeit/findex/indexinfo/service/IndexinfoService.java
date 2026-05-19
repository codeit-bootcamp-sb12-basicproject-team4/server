package com.codeit.findex.indexinfo.service;

import com.codeit.findex.global.common.SourceType;
import com.codeit.findex.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.indexinfo.dto.IndexInfoDto;
import com.codeit.findex.indexinfo.dto.IndexInfoSummaryDto;
import com.codeit.findex.indexinfo.dto.IndexInfoUpdateRequest;
import com.codeit.findex.indexinfo.entity.Findex;
import com.codeit.findex.indexinfo.mapper.IndexinfoMapper;
import com.codeit.findex.indexinfo.repository.IndexinfoRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.processing.Find;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class IndexinfoService {

  private final IndexinfoRepository indexinfoRepository;
  private final IndexinfoMapper indexinfoMapper;

  @Transactional(readOnly = true)
  public List<IndexInfoDto> getIndexInfoList() {
    List<Findex> findexList = indexinfoRepository.findAll();
    return indexinfoMapper.toDtoList(findexList);
  }

  @Transactional
  public IndexInfoDto createIndexInfo(IndexInfoCreateRequest request) {
    Findex findex = indexinfoMapper.toEntity(request);
    findex.setSourceType(SourceType.USER);
    Findex savedFindex = indexinfoRepository.save(findex);
    return indexinfoMapper.toDto(savedFindex);
  }

//  @Transactional
//  public IndexInfoDto createIndexInfo(IndexInfoCreateRequest request, SourceType sourceType) {
//    Findex findex = indexinfoMapper.toEntity(request);
//    findex.setSourceType(sourceType);
//    Findex savedFindex = indexinfoRepository.save(findex);
//    return indexinfoMapper.toDto(savedFindex);
//  }

  @Transactional(readOnly = true)
  public IndexInfoDto getIndexInfo(UUID id) {
    Findex findex = indexinfoRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 지수 정보가 존재하지 않습니다. id: " + id));
    return indexinfoMapper.toDto(findex);
  }

  @Transactional
  public void deleteIndexInfo(UUID id) {
    Findex findex = indexinfoRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 지수 정보가 존재하지 않습니다. id: " + id));
    indexinfoRepository.delete(findex);
  }

  @Transactional
  public void updateIndexInfo(UUID id, IndexInfoUpdateRequest request) {
    Findex findex = indexinfoRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 지수 정보가 존재하지 않습니다. id: " + id));

    if (request.getEmployedItemsCount() == null) {
      throw new IllegalArgumentException("채용 종목 수는 필수 입력 항목입니다.");
    }
    if (request.getBasePointInTime() == null) {
      throw new IllegalArgumentException("기준 시점은 필수 입력 항목입니다.");
    }
    if (request.getBaseIndex() == null) {
      throw new IllegalArgumentException("기준 지수는 필수 입력 항목입니다.");
    }
    if (request.getFavorite() == null) {
      throw new IllegalArgumentException("즐겨찾기 여부는 필수 입력 항목입니다.");
    }

    findex.setItemsCount(request.getEmployedItemsCount());
    findex.setBasePntm(request.getBasePointInTime());
    findex.setBaseIndex(request.getBaseIndex());
    findex.setFavorite(request.getFavorite());
  }

  @Transactional(readOnly = true)
  public List<IndexInfoSummaryDto> getIndexInfoSummaryList() {
    List<Findex> findexList = indexinfoRepository.findAll();
    return indexinfoMapper.toSummaryDtoList(findexList);
  }
}
