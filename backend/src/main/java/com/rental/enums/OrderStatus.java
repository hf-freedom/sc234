package com.rental.enums;

public enum OrderStatus {
    PENDING_PAYMENT("待支付"),
    DEPOSIT_FROZEN("押金已冻结"),
    RENTING("租赁中"),
    RETURNED("已归还"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
