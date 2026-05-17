package com.codeit.findex.indexdata.controller;

import com.codeit.findex.indexdata.dto.IndexChartDto;
import com.codeit.findex.indexdata.service.IndexDataService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
      @RequestParam(value = "periodType", defaultValue = "DAILY") String periodType
  ){
    IndexChartDto indexChartDto = indexDataService.getIndexChart(id, periodType);
    return ResponseEntity.ok(indexChartDto);
  }

}
