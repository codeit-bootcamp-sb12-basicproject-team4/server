package com.codeit.findex.indexdata.controller;

import com.codeit.findex.indexdata.dto.CursorPageResponseIndexDataDto;
import com.codeit.findex.indexdata.service.IndexDataService;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data")
public class IndexDataController implements IndexDataApi {

  private final IndexDataService indexDataService;

  @Override
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

    return ResponseEntity.ok(indexDataService.getIndexDataList(indexInfoId, startDate, endDate, idAfter, cursor,
        sortField, sortDirection, size));
  }
}
