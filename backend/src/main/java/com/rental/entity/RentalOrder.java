package com.rental.entity;

import com.rental.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RentalOrder {
    private Long id;
    private String orderNo;
    private Long productId;
    private String productName;
    private Long userId;
    private String userName;
    private BigDecimal deposit;
    private BigDecimal dailyRate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime actualReturnTime;
    private Integer rentalDays;
    private Integer overdueDays;
    private String freezeStatus;
    private String freezeSerialNo;
    private BigDecimal rentalAmount;
    private BigDecimal overdueAmount;
    private BigDecimal damageAmount;
    private BigDecimal refundAmount;
    private OrderStatus status;
    private String damageDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public BigDecimal getDeposit() { return deposit; }
    public void setDeposit(BigDecimal deposit) { this.deposit = deposit; }
    public BigDecimal getDailyRate() { return dailyRate; }
    public void setDailyRate(BigDecimal dailyRate) { this.dailyRate = dailyRate; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public LocalDateTime getActualReturnTime() { return actualReturnTime; }
    public void setActualReturnTime(LocalDateTime actualReturnTime) { this.actualReturnTime = actualReturnTime; }
    public Integer getRentalDays() { return rentalDays; }
    public void setRentalDays(Integer rentalDays) { this.rentalDays = rentalDays; }
    public Integer getOverdueDays() { return overdueDays; }
    public void setOverdueDays(Integer overdueDays) { this.overdueDays = overdueDays; }
    public String getFreezeStatus() { return freezeStatus; }
    public void setFreezeStatus(String freezeStatus) { this.freezeStatus = freezeStatus; }
    public String getFreezeSerialNo() { return freezeSerialNo; }
    public void setFreezeSerialNo(String freezeSerialNo) { this.freezeSerialNo = freezeSerialNo; }
    public BigDecimal getRentalAmount() { return rentalAmount; }
    public void setRentalAmount(BigDecimal rentalAmount) { this.rentalAmount = rentalAmount; }
    public BigDecimal getOverdueAmount() { return overdueAmount; }
    public void setOverdueAmount(BigDecimal overdueAmount) { this.overdueAmount = overdueAmount; }
    public BigDecimal getDamageAmount() { return damageAmount; }
    public void setDamageAmount(BigDecimal damageAmount) { this.damageAmount = damageAmount; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getDamageDescription() { return damageDescription; }
    public void setDamageDescription(String damageDescription) { this.damageDescription = damageDescription; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
