package com.rental.dto;

import java.math.BigDecimal;

public class ReturnRequest {
    private Long orderId;
    private Boolean hasDamage;
    private BigDecimal damageAmount;
    private String damageDescription;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Boolean getHasDamage() { return hasDamage; }
    public void setHasDamage(Boolean hasDamage) { this.hasDamage = hasDamage; }
    public BigDecimal getDamageAmount() { return damageAmount; }
    public void setDamageAmount(BigDecimal damageAmount) { this.damageAmount = damageAmount; }
    public String getDamageDescription() { return damageDescription; }
    public void setDamageDescription(String damageDescription) { this.damageDescription = damageDescription; }
}
