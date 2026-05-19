package com.codeit.findex.indexdata.entity;


import com.codeit.findex.global.common.BaseUpdatableEntity;
import com.codeit.findex.global.common.SourceType;
import com.codeit.findex.indexinfo.entity.Findex;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "index_data", uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_index_data_findex_date",
        columnNames = {"findex_id", "base_date"}
    )
})
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IndexData extends BaseUpdatableEntity {

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "findex_id", nullable = false)
  private Findex findex;

  @NotNull
  @Column(name = "base_date", nullable = false)
  private LocalDate baseDate;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "source_type", nullable = false, length = 20)
  private SourceType sourceType;

  @NotNull
  @Column(name = "market_price", nullable = false, precision = 18, scale = 2)
  private BigDecimal marketPrice;

  @NotNull
  @Column(name = "close_price", nullable = false, precision = 18, scale = 2)
  private BigDecimal closePrice;

  @NotNull
  @Column(name = "high_price", nullable = false, precision = 18, scale = 2)
  private BigDecimal highPrice;

  @NotNull
  @Column(name = "low_price", nullable = false, precision = 18, scale = 2)
  private BigDecimal lowPrice;

  @NotNull
  @Column(name = "versus", nullable = false, precision = 18, scale = 2)
  private BigDecimal versus;

  @NotNull
  @Column(name = "fluctuation_rate", nullable = false, precision = 5, scale = 2)
  private BigDecimal fluctuationRate;

  @Column(name = "trading_quantity")
  private Long tradingQuantity;

  @Column(name = "trading_price")
  private Long tradingPrice;

  @Column(name = "market_totalamount")
  private Long marketTotalamount;

  public void updateFromOpenApi(
      BigDecimal marketPrice,
      BigDecimal closePrice,
      BigDecimal highPrice,
      BigDecimal lowPrice,
      BigDecimal versus,
      BigDecimal fluctuationRate,
      Long tradingQuantity,
      Long tradingPrice,
      Long marketTotalamount
  ) {
    this.sourceType = SourceType.OPEN_API;
    this.marketPrice = marketPrice;
    this.closePrice = closePrice;
    this.highPrice = highPrice;
    this.lowPrice = lowPrice;
    this.versus = versus;
    this.fluctuationRate = fluctuationRate;
    this.tradingQuantity = tradingQuantity;
    this.tradingPrice = tradingPrice;
    this.marketTotalamount = marketTotalamount;
  }
}
