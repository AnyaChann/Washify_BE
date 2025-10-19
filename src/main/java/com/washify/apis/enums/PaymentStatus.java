package com.washify.apis.enums;

/**
 * Enum cho trạng thái thanh toán
 */
public enum PaymentStatus {
    PENDING("Chờ thanh toán"),
    PROCESSING("Đang xử lý"),
    COMPLETED("Đã thanh toán"),
    FAILED("Thất bại"),
    REFUNDED("Đã hoàn tiền"),
    CANCELLED("Đã hủy");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
