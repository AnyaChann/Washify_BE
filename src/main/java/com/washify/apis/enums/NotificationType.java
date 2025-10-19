package com.washify.apis.enums;

/**
 * Enum cho loại thông báo
 */
public enum NotificationType {
    ORDER_CREATED("Đơn hàng mới"),
    ORDER_CONFIRMED("Đơn hàng đã xác nhận"),
    ORDER_PROCESSING("Đơn hàng đang xử lý"),
    ORDER_READY("Đơn hàng sẵn sàng"),
    ORDER_DELIVERING("Đơn hàng đang giao"),
    ORDER_COMPLETED("Đơn hàng hoàn thành"),
    ORDER_CANCELLED("Đơn hàng bị hủy"),
    PAYMENT_SUCCESS("Thanh toán thành công"),
    PAYMENT_FAILED("Thanh toán thất bại"),
    PROMOTION("Khuyến mãi"),
    SYSTEM("Hệ thống"),
    OTHER("Khác");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
