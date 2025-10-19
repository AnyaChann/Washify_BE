package com.washify.apis.enums;

/**
 * Enum cho loại người dùng/vai trò
 */
public enum RoleType {
    ADMIN("Quản trị viên"),
    MANAGER("Quản lý chi nhánh"),
    STAFF("Nhân viên"),
    CUSTOMER("Khách hàng"),
    SHIPPER("Người giao hàng");

    private final String displayName;

    RoleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
