package com.codeit.findex.indexdata.controller;

import com.codeit.findex.global.common.PeriodType;
import com.codeit.findex.indexdata.dto.IndexChartDto;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataUpdateRequest;
import com.codeit.findex.indexdata.dto.RankedIndexPerformanceDto;
import com.codeit.findex.indexdata.service.IndexDataService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.codeit.findex.indexdata.dto.CursorPageResponseIndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
import com.codeit.findex.indexdata.service.IndexDataService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data")
public class IndexDataController implements IndexDataApi {

  private final IndexDataService indexDataService;

  @Override
  @GetMapping("/{id}/chart")
  public ResponseEntity<IndexChartDto> getIndexChart(
      @PathVariable UUID id,
      @RequestParam(value = "periodType", defaultValue = "DAILY") PeriodType periodType
  ){
    IndexChartDto indexChartDto = indexDataService.getIndexChart(id, periodType);
    return ResponseEntity.ok(indexChartDto);
  }

  @Override
  @GetMapping("/performance/rank")
  public ResponseEntity<List<RankedIndexPerformanceDto>> getIndexPerformanceRank(
      @RequestParam(required = false) UUID indexInfoId,
      @RequestParam(defaultValue = "DAILY") PeriodType periodType,
      @RequestParam(defaultValue = "10") Integer limit
  ) {
    List<RankedIndexPerformanceDto> result = indexDataService.getIndexPerformanceRank(indexInfoId, periodType, limit);
    return ResponseEntity.ok(result);  }

  @Override
  @PatchMapping("/{id}")
  public ResponseEntity<IndexDataDto> updateIndexData(
      @PathVariable UUID id,
      @Valid @RequestBody IndexDataUpdateRequest request
  ) {
    IndexDataDto result = indexDataService.updateIndexData(id, request);
    return ResponseEntity.ok(result);

  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteIndexData(
      @PathVariable UUID id
  ) {
    indexDataService.deleteIndexData(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping()
  public ResponseEntity<CursorPageResponseIndexDataDto> getIndexDataList(
      @RequestParam(required = false) UUID indexInfoId,
      @RequestParam(required = false) LocalDate startDate,
      @RequestParam(required = false) LocalDate endDate,
      @RequestParam(required = false) UUID idAfter,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "baseDate", required = false) String sortField,
      @RequestParam(defaultValue = "desc", required = false) String sortDirection,
      @RequestParam(defaultValue = "10", required = false) int size) {

    return ResponseEntity.ok(
        indexDataService.getIndexDataList(indexInfoId, startDate, endDate, idAfter, cursor,
            sortField, sortDirection, size));
  }

  @Override
  @PostMapping()
  public ResponseEntity<IndexDataDto> create(@RequestBody @Valid IndexDataCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(indexDataService.create(request));
  }

  @Override
  @GetMapping("/performance/favorite")
  public ResponseEntity<List<IndexPerformanceDto>> getFavoriteIndexPerformance(
      @RequestParam(defaultValue = "DAILY", required = false) PeriodType periodType) {
    return ResponseEntity.ok(indexDataService.getFavoriteIndexPerformance(periodType));
  }

  @Override
  @GetMapping("/export/csv")
  public ResponseEntity<byte[]> downloadIndexData(
      @RequestParam(required = false) UUID indexInfoId,
      @RequestParam(required = false) LocalDate startDate,
      @RequestParam(required = false) LocalDate endDate,
      @RequestParam(defaultValue = "baseDate", required = false) String sortField,
      @RequestParam(defaultValue = "desc", required = false) String sortDirection) {
    byte[] response = indexDataService.downloadIndexData(indexInfoId, startDate, endDate, sortField,
        sortDirection);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,  "attachment; filename=\"index-data.csv\"")
        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(response.length))
        .contentType(MediaType.parseMediaType("text/csv"))
        .body(response);
  }
}
