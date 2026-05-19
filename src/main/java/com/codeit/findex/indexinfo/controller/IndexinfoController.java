package com.codeit.findex.indexinfo.controller;

import com.codeit.findex.global.common.SourceType;
import com.codeit.findex.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.indexinfo.dto.IndexInfoDto;
import com.codeit.findex.indexinfo.dto.IndexInfoSummaryDto;
import com.codeit.findex.indexinfo.dto.IndexInfoUpdateRequest;
import com.codeit.findex.indexinfo.service.IndexinfoService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-infos")
public class IndexinfoController {

  private final IndexinfoService indexinfoService;

  @GetMapping
  public ResponseEntity<List<IndexInfoDto>> getIndexInfoList() {
    List<IndexInfoDto> indexInfoDtoList = indexinfoService.getIndexInfoList();
    return ResponseEntity.ok(indexInfoDtoList);
  }

  @PostMapping
  public ResponseEntity<IndexInfoDto> createIndexInfo(
      @RequestBody IndexInfoCreateRequest request) {
    IndexInfoDto response = indexinfoService.createIndexInfo(request);
//    IndexInfoDto response = indexinfoService.createIndexInfo(request, SourceType.USER);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<IndexInfoDto> getIndexInfo(@PathVariable UUID id) {
    IndexInfoDto indexInfoDto = indexinfoService.getIndexInfo(id);
    return ResponseEntity.ok(indexInfoDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteIndexInfo(@PathVariable UUID id) {
    indexinfoService.deleteIndexInfo(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateIndexInfo(
      @PathVariable UUID id, @RequestBody IndexInfoUpdateRequest request) {
    indexinfoService.updateIndexInfo(id, request);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/summaries")
  public ResponseEntity<List<IndexInfoSummaryDto>> getIndexInfoSummaryList() {
    List<IndexInfoSummaryDto> summaryList = indexinfoService.getIndexInfoSummaryList();
    return ResponseEntity.ok(summaryList);
  }
}
