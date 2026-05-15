package com.codeit.findex.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI swagger() {
    return new OpenAPI()
        .info(new io.swagger.v3.oas.models.info.Info()
            .title("Findex API")
            .description("가볍고 빠른 외부 API 연동 금융 분석 도구 API 문서"))
        .servers(List.of(new Server()
            .url("http://localhost:8080")
            .description("로컬 서버")));
  }
}
