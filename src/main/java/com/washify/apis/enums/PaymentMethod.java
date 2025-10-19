package com.washify.apis.enums;

/**
 * Enum cho phương thức thanh toán
 */
public enum PaymentMethod {
    CASH("Tiền mặt"),
    BANK_TRANSFER("Chuyển khoản ngân hàng"),
    CREDIT_CARD("Thẻ tín dụng"),
    DEBIT_CARD("Thẻ ghi nợ"),
    E_WALLET("Ví điện tử"),
    MOMO("MoMo"),
    ZALOPAY("ZaloPay"),
    VNPAY("VNPay"),
    PAYPAL("PayPal");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
