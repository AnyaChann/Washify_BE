# 📝 Common Response Formats

## Success Response

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2025-10-21T10:30:00"
}
```

**Fields:**
- `success`: `true` - Operation thành công
- `message`: Brief description of the result
- `data`: Response payload (object, array, or primitive)
- `timestamp`: ISO 8601 datetime

---

## Error Response

```json
{
  "success": false,
  "message": "Error message",
  "error": "Detailed error description",
  "timestamp": "2025-10-21T10:30:00"
}
```

**Fields:**
- `success`: `false` - Operation thất bại
- `message`: User-friendly error message
- `error`: Technical error details (for debugging)
- `timestamp`: ISO 8601 datetime

---

## Validation Error Response

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "email": "Email không hợp lệ",
    "password": "Mật khẩu phải có ít nhất 8 ký tự",
    "phone": "Số điện thoại không đúng định dạng"
  },
  "timestamp": "2025-10-21T10:30:00"
}
```

**Fields:**
- `success`: `false`
- `message`: General validation error message
- `errors`: Object mapping field names to error messages
- `timestamp`: ISO 8601 datetime

---

## HTTP Status Codes

### Success Codes
- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **204 No Content**: Success with no response body

### Client Error Codes
- **400 Bad Request**: Invalid request syntax or validation failed
- **401 Unauthorized**: Missing or invalid authentication token
- **403 Forbidden**: User doesn't have permission
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource already exists or conflict

### Server Error Codes
- **500 Internal Server Error**: Unexpected server error

---

## Pagination Response (Future Enhancement)

```json
{
  "success": true,
  "message": "Lấy danh sách thành công",
  "data": {
    "items": [ ... ],
    "page": 1,
    "size": 20,
    "totalItems": 150,
    "totalPages": 8,
    "hasNext": true,
    "hasPrevious": false
  },
  "timestamp": "2025-10-21T10:30:00"
}
```

**Note**: Pagination chưa được implement. Hiện tại API trả về toàn bộ data.

---

## Date/Time Format

- **Format**: ISO 8601 (`YYYY-MM-DDTHH:mm:ss`)
- **Timezone**: Asia/Ho_Chi_Minh (UTC+7)
- **Example**: `2025-10-21T10:30:00`

**Request Example:**
```json
{
  "pickupTime": "2025-10-22T14:00:00"
}
```

**Response Example:**
```json
{
  "createdAt": "2025-10-21T10:30:00",
  "updatedAt": "2025-10-21T11:45:00"
}
```

---

[← Back to Main Documentation](../README.md)
