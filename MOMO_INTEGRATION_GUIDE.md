# 💳 Tích Hợp Thanh Toán MoMo

## 📋 Tổng Quan

Hệ thống Washify hỗ trợ 2 phương thức thanh toán:

### 1. CASH (Tiền mặt)
- ✅ Thanh toán tại quầy
- ✅ COD (Cash On Delivery)

### 2. MOMO (Ví điện tử MoMo)
- ✅ Thanh toán qua app MoMo
- ✅ Thanh toán qua QR Code
- ✅ Webhook/IPN callback tự động cập nhật trạng thái
- ✅ Signature verification bảo mật
- ✅ Refund (hoàn tiền)

---

## 🔧 Cấu Hình

### 1. Database Migration

Đã thêm script migration: `V2__Add_MoMo_Payment_Support.sql`

```sql
-- Thêm các cột mới vào bảng payments
ALTER TABLE payments
ADD COLUMN transaction_id VARCHAR(255),
ADD COLUMN payment_url VARCHAR(500),
ADD COLUMN qr_code TEXT,
ADD COLUMN gateway_response TEXT;

-- Cập nhật enum payment_method
ALTER TABLE payments
MODIFY COLUMN payment_method ENUM('CASH', 'MOMO');
```

### 2. Application Properties

File: `src/main/resources/application.properties`

```properties
# MoMo Test Environment (Sandbox)
momo.partner-code=MOMOIQA420180417
momo.access-key=SvDmj2cOTYZmQQ3H
momo.secret-key=PPuDXq1KowPT1ftR8DvlQTHhC03aul17
momo.endpoint=https://test-payment.momo.vn/v2/gateway/api/create

# Callback URLs
momo.redirect-url=http://localhost:3000/payment/result
momo.ipn-url=http://localhost:8080/api/payments/momo/webhook
```

**Lưu ý**: 
- Đây là thông tin **test** của MoMo sandbox
- Production cần đăng ký tài khoản MoMo Business
- IPN URL phải là **public URL** (dùng ngrok cho local dev)

---

## 🚀 Cách Sử Dụng

### 1. Tạo Thanh Toán MoMo

**Endpoint**: `POST /api/payments`

**Request Body**:
```json
{
  "orderId": 1,
  "paymentMethod": "MOMO",
  "amount": 180000.00
}
```

**Response**:
```json
{
  "success": true,
  "message": "Tạo thanh toán thành công",
  "data": {
    "id": 1,
    "orderId": 1,
    "orderCode": "WF202510210001",
    "paymentMethod": "MOMO",
    "paymentStatus": "PENDING",
    "amount": 180000.00,
    "transactionId": "WF202510210001_1698012345678",
    "paymentUrl": "https://test-payment.momo.vn/gw_payment/transactionProcessor?...",
    "qrCode": "https://test-payment.momo.vn/qr/WF202510210001_1698012345678.png"
  }
}
```

### 2. Frontend Xử Lý

**Option 1: Redirect sang MoMo Web**
```javascript
// Redirect user sang paymentUrl
window.location.href = response.data.paymentUrl;
```

**Option 2: Hiển thị QR Code**
```javascript
// Hiển thị QR code để user scan bằng app MoMo
<img src={response.data.qrCode} alt="MoMo QR Code" />
```

**Option 3: Deep Link (Mobile)**
```javascript
// Mở app MoMo trực tiếp
window.location.href = `momo://payment/${response.data.transactionId}`;
```

### 3. MoMo Callback Flow

```
1. User thanh toán trên MoMo
   ↓
2. MoMo gửi IPN callback → POST /api/payments/momo/webhook
   ↓
3. Backend verify signature
   ↓
4. Cập nhật payment status → PAID
   ↓
5. Cập nhật order status → CONFIRMED
   ↓
6. MoMo redirect user → {momo.redirect-url}?orderId=xxx&status=success
```

### 4. Frontend Xử Lý Callback

**URL**: `http://localhost:3000/payment/result?orderId=WF202510210001&status=success`

```javascript
// Frontend check payment status
const params = new URLSearchParams(window.location.search);
const orderId = params.get('orderId');
const status = params.get('status');

if (status === 'success') {
  // Gọi API để lấy thông tin payment mới nhất
  const payment = await fetch(`/api/payments/order/${orderId}`);
  
  if (payment.paymentStatus === 'PAID') {
    // Hiển thị success message
    toast.success('Thanh toán thành công!');
  }
} else {
  // Thanh toán thất bại
  toast.error('Thanh toán thất bại');
}
```

---

## 🔐 Bảo Mật

### Signature Verification

MoMo sử dụng HMAC SHA256 để ký request/response:

```java
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

String signature = generateHmacSHA256(rawSignature, secretKey);
```

Backend tự động verify signature khi nhận webhook từ MoMo.

---

## 📝 Payment Status Flow

```
PENDING    → Vừa tạo payment, chờ user thanh toán
    ↓
PAID       → User đã thanh toán thành công (webhook từ MoMo)
    
FAILED     → Thanh toán thất bại hoặc user hủy
```

---

## 🧪 Testing

### 1. Test với MoMo Sandbox

**Test Card Info** (MoMo Test Environment):
- Phone: `0399999999`
- Name: `NGUYEN VAN A`
- OTP: `123456`

### 2. Test Webhook với Ngrok

```bash
# Cài ngrok
npm install -g ngrok

# Expose local port 8080
ngrok http 8080

# Copy URL và cập nhật vào application.properties
momo.ipn-url=https://abc123.ngrok.io/api/payments/momo/webhook
```

### 3. Manual Test Webhook

```bash
curl -X POST http://localhost:8080/api/payments/momo/webhook \
  -H "Content-Type: application/json" \
  -d '{
    "partnerCode": "MOMOIQA420180417",
    "orderId": "WF202510210001_1698012345678",
    "requestId": "WF202510210001_1698012345678",
    "amount": "180000",
    "orderInfo": "Thanh toán đơn hàng WF202510210001",
    "resultCode": "0",
    "message": "Success",
    "transId": "2834759284",
    "payType": "qr",
    "responseTime": "1698012345678",
    "extraData": "",
    "signature": "..."
  }'
```

---

## 🔄 Refund (Hoàn Tiền)

**Endpoint**: `POST /api/payments/{id}/refund?reason=Customer request`

**Auth**: ADMIN, MANAGER only

```java
// Service sẽ tự động:
// 1. Cập nhật payment status → FAILED
// 2. Cập nhật order status → CANCELLED
// 3. Gọi MoMo refund API (TODO: implement)
```

---

## 📊 Statistics & Monitoring

### Query Payments by Method

```
GET /api/payments/method/MOMO
```

### Payment Statistics

```
GET /api/payments/statistics
```

Response:
```json
{
  "totalRevenue": 12345678.00,
  "totalPaid": 234,
  "totalPending": 12,
  "totalFailed": 56,
  "cashPayments": 100,
  "momoPayments": 134
}
```

---

## 🚨 Troubleshooting

### 1. IPN Webhook không nhận được

**Nguyên nhân**:
- Local development URL không public
- Firewall block incoming request

**Giải pháp**:
- Dùng ngrok để expose localhost
- Hoặc deploy lên server có public IP

### 2. Signature không hợp lệ

**Nguyên nhân**:
- Secret key sai
- Thứ tự params trong rawSignature không đúng

**Giải pháp**:
- Kiểm tra lại secret key trong application.properties
- Xem log để debug rawSignature

### 3. Payment status không tự động cập nhật

**Nguyên nhân**:
- Webhook chưa được gọi
- Error trong webhook handler

**Giải pháp**:
- Check MoMo dashboard xem IPN có được gọi không
- Xem log backend để debug error

---

## 📚 References

- [MoMo API Documentation](https://developers.momo.vn/)
- [MoMo Test Credentials](https://developers.momo.vn/v3/#/docs/aiov2/?id=test-credentials)
- [MoMo Business Registration](https://business.momo.vn/)

---

## 🎯 Next Steps

### TODO:
- [ ] Implement actual HTTP call to MoMo API (hiện tại đang mock)
- [ ] Implement MoMo refund API
- [ ] Implement query transaction status API
- [ ] Add retry mechanism for failed webhooks
- [ ] Add payment transaction logs
- [ ] Add unit tests for MoMoPaymentService

### Future Enhancements:
- [ ] Add installment payment support (trả góp)
- [ ] Add payment analytics dashboard
- [ ] Add promotional discount integration

---

**Version**: 1.0  
**Last Updated**: 2025-10-21  
**Author**: Washify Development Team
