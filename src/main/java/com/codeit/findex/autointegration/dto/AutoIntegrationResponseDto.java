package com.codeit.findex.autointegration.dto;

import com.codeit.findex.autointegration.entity.AutoIntegration;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class AutoIntegrationResponseDto {
  private final UUID findexId;
  private final String indexName;
  private final String indexClassification;
  private final Boolean isActive;
  private final Instant updatedAt;

  public AutoIntegrationResponseDto(AutoIntegration entity) {
    this.findexId = entity.getFindexId();
    this.isActive = entity.getIsActive();
    this.updatedAt = entity.getUpdatedAt();


    if (entity.getFindex() != null) {
      this.indexName = entity.getFindex().getIndexName();
      this.indexClassification = entity.getFindex().getIndexClassification();
    } else {
      this.indexName = null;
      this.indexClassification = null;
    }
  }
}