package com.codeit.findex.integrationlog.controller;

import com.codeit.findex.integrationlog.dto.IndexResponse;
import com.codeit.findex.integrationlog.dto.IntegrationLogPageResponse;
import com.codeit.findex.integrationlog.dto.IntegrationLogSearchRequest;
import com.codeit.findex.integrationlog.dto.IndexdataIntegrationRequest;
import com.codeit.findex.integrationlog.service.IntegrationFacade;
import com.codeit.findex.integrationlog.service.IntegrationLogService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sync-jobs")
public class IntegrationController {
  private final IntegrationFacade integrationFacade;
  private final IntegrationLogService integrationLogService; //연동 작업 목록 조회

  @GetMapping
  public ResponseEntity<IntegrationLogPageResponse> searchSyncJobs(
      @ModelAttribute IntegrationLogSearchRequest searchRequest
  ) {
    return ResponseEntity.ok(integrationLogService.search(searchRequest));
  }

  @PostMapping("/index-infos")
  public ResponseEntity<List<IndexResponse>> syncIndexInfo(HttpServletRequest request) {
    String clientIp = getClientIp(request);

    List<IndexResponse> results = integrationFacade.syncIndexInfo(clientIp);
    if (results.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(results);
  }

  @PostMapping("/index-data")
  public ResponseEntity<List<IndexResponse>> syncIndexData(
      @RequestBody IndexdataIntegrationRequest syncRequest,
      HttpServletRequest request
  ) {
    String clientIp = getClientIp(request);

    List<IndexResponse> results = integrationFacade.syncIndexData(syncRequest, clientIp);
    if (results.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(results);
  }

  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}
