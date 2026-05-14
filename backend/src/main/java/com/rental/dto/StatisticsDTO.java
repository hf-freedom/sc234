package com.rental.dto;

import java.math.BigDecimal;

public class StatisticsDTO {
    private Long totalOrders;
    private Long totalProducts;
    private Integer rentalCount;
    private Double overdueRate;
    private Double damageRate;
    private BigDecimal totalDamageAmount;
    private BigDecimal totalRentalAmount;

    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }
    public Long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(Long totalProducts) { this.totalProducts = totalProducts; }
    public Integer getRentalCount() { return rentalCount; }
    public void setRentalCount(Integer rentalCount) { this.rentalCount = rentalCount; }
    public Double getOverdueRate() { return overdueRate; }
    public void setOverdueRate(Double overdueRate) { this.overdueRate = overdueRate; }
    public Double getDamageRate() { return damageRate; }
    public void setDamageRate(Double damageRate) { this.damageRate = damageRate; }
    public BigDecimal getTotalDamageAmount() { return totalDamageAmount; }
    public void setTotalDamageAmount(BigDecimal totalDamageAmount) { this.totalDamageAmount = totalDamageAmount; }
    public BigDecimal getTotalRentalAmount() { return totalRentalAmount; }
    public void setTotalRentalAmount(BigDecimal totalRentalAmount) { this.totalRentalAmount = totalRentalAmount; }
}
