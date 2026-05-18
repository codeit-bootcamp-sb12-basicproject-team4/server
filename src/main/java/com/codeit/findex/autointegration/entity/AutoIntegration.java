package com.codeit.findex.autointegration.entity;

import com.codeit.findex.indexinfo.entity.Findex;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@SuperBuilder
@Table(name = "auto_integration")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AutoIntegration {

  @Id
  @Column(name = "findex_id")
  private UUID findexId;

  @NotNull
  @Column(name = "is_active", nullable = false)
  private Boolean isActive;

  @NotNull
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @NotNull
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "findex_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Findex findex;

  public void updateActive(Boolean isActive) {
    this.isActive = isActive;
    this.updatedAt = Instant.now();
  }
}
