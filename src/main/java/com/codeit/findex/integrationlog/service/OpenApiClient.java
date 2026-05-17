package com.codeit.findex.integrationlog.service;

import com.codeit.findex.integrationlog.dto.OpenApiIndex;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenApiClient {
  private final ObjectMapper objectMapper;

  @Value("${openapi.key}")
  private String serviceKey;
  @Value("${openapi.endpoint}")
  private String baseUrl;

  public List<OpenApiIndex> fetchIndexInfo(String baseDate) {
    URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
        .path("/getStockMarketIndex")
        .queryParam("serviceKey", serviceKey)
        .queryParam("resultType", "json")
        .queryParam("numOfRows", 161)
        .queryParam("basDt", baseDate)
        .build(true) // 서비스키 인코딩 유지
        .toUri();

    try {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
          .uri(uri)
          .GET()
          .timeout(Duration.ofSeconds(15))
          .build();

      log.info("OpenAPI 요청 시작 - 기준일자: {}, URL: {}", baseDate, uri);
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        log.error("API 요청 실패 - Status Code: {}", response.statusCode());
        throw new RuntimeException("API 서버 응답 에러");
      }

      JsonNode root = objectMapper.readTree(response.body());

      JsonNode itemNode = root.at("/response/body/items/item");

      if (itemNode.isMissingNode() || !itemNode.isArray()) {
        log.warn("해당 날짜({})에 대한 데이터가 존재하지 않습니다.", baseDate);
        return Collections.emptyList();
      }

      return objectMapper.readerForListOf(OpenApiIndex.class).readValue(itemNode);

    } catch (Exception e) {
      log.error("OpenAPI 데이터 수집 중 예외 발생: {}", e.getMessage());
      throw new RuntimeException("OpenAPI 통신 오류", e);
    }
  }

}
