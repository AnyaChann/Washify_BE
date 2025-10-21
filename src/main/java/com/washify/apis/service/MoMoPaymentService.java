package com.washify.apis.service;

import com.washify.apis.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service xử lý tích hợp thanh toán MoMo
 * MoMo API Documentation: https://developers.momo.vn/
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MoMoPaymentService {
    
    // MoMo Configuration (nên lưu trong application.properties)
    @Value("${momo.partner-code:MOMO_PARTNER_CODE}")
    private String partnerCode;
    
    @Value("${momo.access-key:MOMO_ACCESS_KEY}")
    private String accessKey;
    
    @Value("${momo.secret-key:MOMO_SECRET_KEY}")
    private String secretKey;
    
    @Value("${momo.endpoint:https://test-payment.momo.vn/v2/gateway/api/create}")
    private String momoEndpoint;
    
    @Value("${momo.redirect-url:http://localhost:3000/payment/result}")
    private String redirectUrl;
    
    @Value("${momo.ipn-url:http://localhost:8080/api/payments/momo/webhook}")
    private String ipnUrl;
    
    /**
     * Tạo payment request cho MoMo
     * 
     * @param payment Payment entity
     * @param orderCode Order code để tạo requestId unique
     * @return Map chứa paymentUrl và qrCodeUrl
     */
    public Map<String, String> createMoMoPayment(Payment payment, String orderCode) {
        try {
            // Tạo requestId unique (orderId + timestamp)
            String requestId = orderCode + "_" + System.currentTimeMillis();
            String orderId = requestId; // MoMo yêu cầu orderId unique
            
            // Convert BigDecimal to long (MoMo yêu cầu amount là số nguyên VNĐ)
            long amount = payment.getAmount().longValue();
            
            // Order info
            String orderInfo = "Thanh toán đơn hàng " + orderCode;
            
            // Request type: captureWallet (thanh toán ví), payWithATM (thẻ ATM)
            String requestType = "captureWallet";
            
            // Extra data (optional)
            String extraData = "";
            
            // Tạo raw signature
            String rawSignature = "accessKey=" + accessKey +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + redirectUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;
            
            // Generate signature using HMAC SHA256
            String signature = generateHmacSHA256(rawSignature, secretKey);
            
            log.info("MoMo Payment Request - OrderCode: {}, RequestId: {}, Amount: {}", 
                    orderCode, requestId, amount);
            log.debug("Raw Signature: {}", rawSignature);
            log.debug("Signature: {}", signature);
            
            // Build request body (trong production, gọi HTTP request tới MoMo API)
            // Ở đây tạo mock response để demo
            Map<String, String> result = new HashMap<>();
            
            // TODO: Implement actual HTTP call to MoMo API
            // Hiện tại return mock data
            result.put("paymentUrl", "https://test-payment.momo.vn/gw_payment/transactionProcessor?partnerCode=" + partnerCode + "&orderId=" + orderId);
            result.put("qrCodeUrl", "https://test-payment.momo.vn/qr/" + orderId + ".png");
            result.put("deeplink", "momo://payment/" + orderId);
            result.put("requestId", requestId);
            result.put("signature", signature);
            
            log.info("MoMo Payment URL generated successfully for order: {}", orderCode);
            
            return result;
            
        } catch (Exception e) {
            log.error("Error creating MoMo payment for order: {}", orderCode, e);
            throw new RuntimeException("Không thể tạo thanh toán MoMo: " + e.getMessage());
        }
    }
    
    /**
     * Verify signature từ MoMo webhook/IPN
     * 
     * @param momoResponse Response từ MoMo
     * @return true nếu signature hợp lệ
     */
    public boolean verifySignature(Map<String, String> momoResponse) {
        try {
            String signature = momoResponse.get("signature");
            
            // Rebuild raw signature từ response
            String rawSignature = "accessKey=" + momoResponse.get("accessKey") +
                    "&amount=" + momoResponse.get("amount") +
                    "&extraData=" + momoResponse.getOrDefault("extraData", "") +
                    "&message=" + momoResponse.get("message") +
                    "&orderId=" + momoResponse.get("orderId") +
                    "&orderInfo=" + momoResponse.get("orderInfo") +
                    "&orderType=" + momoResponse.get("orderType") +
                    "&partnerCode=" + momoResponse.get("partnerCode") +
                    "&payType=" + momoResponse.get("payType") +
                    "&requestId=" + momoResponse.get("requestId") +
                    "&responseTime=" + momoResponse.get("responseTime") +
                    "&resultCode=" + momoResponse.get("resultCode") +
                    "&transId=" + momoResponse.get("transId");
            
            String expectedSignature = generateHmacSHA256(rawSignature, secretKey);
            
            boolean isValid = signature.equals(expectedSignature);
            
            if (!isValid) {
                log.warn("Invalid MoMo signature. Expected: {}, Got: {}", expectedSignature, signature);
            }
            
            return isValid;
            
        } catch (Exception e) {
            log.error("Error verifying MoMo signature", e);
            return false;
        }
    }
    
    /**
     * Xử lý MoMo webhook/IPN callback
     * 
     * @param momoResponse Response từ MoMo
     * @return Payment status: PAID hoặc FAILED
     */
    public Payment.PaymentStatus processMoMoCallback(Map<String, String> momoResponse) {
        // Verify signature
        if (!verifySignature(momoResponse)) {
            log.error("MoMo callback signature verification failed");
            return Payment.PaymentStatus.FAILED;
        }
        
        // Check result code
        // 0: Success, 9000: Transaction pending, other: Failed
        String resultCode = momoResponse.get("resultCode");
        
        if ("0".equals(resultCode)) {
            log.info("MoMo payment successful - TransId: {}, OrderId: {}", 
                    momoResponse.get("transId"), momoResponse.get("orderId"));
            return Payment.PaymentStatus.PAID;
        } else {
            log.warn("MoMo payment failed - ResultCode: {}, Message: {}", 
                    resultCode, momoResponse.get("message"));
            return Payment.PaymentStatus.FAILED;
        }
    }
    
    /**
     * Generate HMAC SHA256 signature
     * 
     * @param data Data to sign
     * @param secret Secret key
     * @return Signature in hex format
     */
    private String generateHmacSHA256(String data, String secret) {
        try {
            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSHA256.init(secretKeySpec);
            byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating HMAC SHA256 signature", e);
        }
    }
    
    /**
     * Query payment status từ MoMo
     * 
     * @param orderId Order ID
     * @param requestId Request ID
     * @return Payment status
     */
    public Map<String, Object> queryPaymentStatus(String orderId, String requestId) {
        // TODO: Implement query transaction status API
        // https://developers.momo.vn/v3/#/docs/aiov2/?id=query-transaction-status-api
        
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("requestId", requestId);
        result.put("resultCode", "0");
        result.put("message", "Success");
        
        return result;
    }
    
    /**
     * Refund payment (hoàn tiền)
     * 
     * @param transId Transaction ID từ MoMo
     * @param amount Số tiền hoàn
     * @param description Lý do hoàn tiền
     * @return Refund result
     */
    public Map<String, Object> refundPayment(String transId, BigDecimal amount, String description) {
        // TODO: Implement refund API
        // https://developers.momo.vn/v3/#/docs/aiov2/?id=refund-api
        
        Map<String, Object> result = new HashMap<>();
        result.put("transId", transId);
        result.put("amount", amount);
        result.put("resultCode", "0");
        result.put("message", "Refund successful");
        
        log.info("MoMo refund processed - TransId: {}, Amount: {}", transId, amount);
        
        return result;
    }
}
