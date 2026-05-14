package com.rental.enums;

public enum ProductStatus {
    IDLE("空闲"),
    RENTED("已租出"),
    IN_REPAIR("维修中"),
    OFF_SHELF("下架");

    private String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
