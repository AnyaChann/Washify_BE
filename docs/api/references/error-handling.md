# ⚠️ Error Handling Guide

## Frontend Error Handling Best Practices

### 1. Always Check `success` Field

```javascript
fetch('/api/orders', {
  headers: { 'Authorization': 'Bearer ' + token }
})
.then(res => res.json())
.then(data => {
  if (data.success) {
    // Handle success
    console.log(data.message);
    processData(data.data);
  } else {
    // Handle error
    showError(data.message);
    console.error(data.error);
  }
})
.catch(err => {
  // Handle network error
  showError('Lỗi kết nối. Vui lòng thử lại.');
  console.error(err);
});
```

---

### 2. Display User-Friendly Messages

Use `message` field for user display:

```javascript
function showError(message) {
  // Show toast/notification
  toast.error(message);
  
  // Or show in UI
  document.getElementById('error-message').textContent = message;
}
```

---

### 3. Log Technical Details

Use `error` field for debugging:

```javascript
if (!data.success) {
  console.error('Error Details:', data.error);
  // Send to error tracking service
  Sentry.captureMessage(data.error);
}
```

---

## Common Error Scenarios

### 1. Authentication Errors (401)

**Scenario**: Token expired or invalid

```json
{
  "success": false,
  "message": "Token không hợp lệ hoặc đã hết hạn",
  "error": "JWT expired",
  "timestamp": "2025-10-21T10:30:00"
}
```

**Handling**:
```javascript
if (response.status === 401) {
  // Clear token
  localStorage.removeItem('jwt_token');
  
  // Redirect to login
  window.location.href = '/login';
}
```

---

### 2. Authorization Errors (403)

**Scenario**: User không có quyền

```json
{
  "success": false,
  "message": "Bạn không có quyền thực hiện thao tác này",
  "error": "Access Denied",
  "timestamp": "2025-10-21T10:30:00"
}
```

**Handling**:
```javascript
if (response.status === 403) {
  showError('Bạn không có quyền truy cập. Vui lòng liên hệ quản trị viên.');
  // Redirect to home or dashboard
  window.location.href = '/dashboard';
}
```

---

### 3. Validation Errors (400)

**Scenario**: Input không hợp lệ

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

**Handling**:
```javascript
if (data.errors) {
  // Show field-specific errors
  Object.keys(data.errors).forEach(field => {
    const inputElement = document.getElementById(field);
    const errorElement = document.getElementById(field + '-error');
    
    if (inputElement) {
      inputElement.classList.add('invalid');
    }
    
    if (errorElement) {
      errorElement.textContent = data.errors[field];
    }
  });
}
```

---

### 4. Not Found Errors (404)

**Scenario**: Resource không tồn tại

```json
{
  "success": false,
  "message": "Đơn hàng không tồn tại",
  "error": "Order not found with id: 999",
  "timestamp": "2025-10-21T10:30:00"
}
```

**Handling**:
```javascript
if (response.status === 404) {
  showError(data.message);
  // Redirect to list page
  window.location.href = '/orders';
}
```

---

### 5. Conflict Errors (409)

**Scenario**: Resource đã tồn tại

```json
{
  "success": false,
  "message": "Email đã được sử dụng",
  "error": "Duplicate entry for email: test@example.com",
  "timestamp": "2025-10-21T10:30:00"
}
```

**Handling**:
```javascript
if (response.status === 409) {
  showError(data.message);
  // Highlight conflicting field
  document.getElementById('email').classList.add('invalid');
}
```

---

### 6. Server Errors (500)

**Scenario**: Lỗi server không mong muốn

```json
{
  "success": false,
  "message": "Đã xảy ra lỗi. Vui lòng thử lại sau.",
  "error": "Internal Server Error",
  "timestamp": "2025-10-21T10:30:00"
}
```

**Handling**:
```javascript
if (response.status === 500) {
  showError('Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.');
  
  // Log for debugging
  console.error('Server Error:', data.error);
  
  // Report to error tracking
  Sentry.captureException(new Error(data.error));
}
```

---

### 7. Network Errors

**Scenario**: Mất kết nối internet

```javascript
fetch('/api/orders')
  .catch(err => {
    if (err.name === 'TypeError' && err.message === 'Failed to fetch') {
      showError('Không có kết nối internet. Vui lòng kiểm tra và thử lại.');
    }
  });
```

---

## Loading States

### Show Loading Indicator

```javascript
function createOrder(orderData) {
  // Show loading
  document.getElementById('submit-btn').disabled = true;
  document.getElementById('loading-spinner').style.display = 'block';
  
  fetch('/api/orders', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
    },
    body: JSON.stringify(orderData)
  })
  .then(res => res.json())
  .then(data => {
    // Hide loading
    document.getElementById('submit-btn').disabled = false;
    document.getElementById('loading-spinner').style.display = 'none';
    
    if (data.success) {
      showSuccess('Đơn hàng đã được tạo thành công!');
    } else {
      showError(data.message);
    }
  })
  .catch(err => {
    // Hide loading on error
    document.getElementById('submit-btn').disabled = false;
    document.getElementById('loading-spinner').style.display = 'none';
    
    showError('Lỗi kết nối. Vui lòng thử lại.');
  });
}
```

---

## Request Timeout

### Set Timeout for Long Requests

```javascript
function fetchWithTimeout(url, options, timeout = 30000) {
  return Promise.race([
    fetch(url, options),
    new Promise((_, reject) =>
      setTimeout(() => reject(new Error('Request timeout')), timeout)
    )
  ]);
}

// Usage
fetchWithTimeout('/api/orders', {
  headers: { 'Authorization': 'Bearer ' + token }
}, 30000)
.then(res => res.json())
.then(data => {
  // Process data
})
.catch(err => {
  if (err.message === 'Request timeout') {
    showError('Yêu cầu quá lâu. Vui lòng thử lại.');
  }
});
```

---

## Retry Logic

### Auto-Retry on Network Failure

```javascript
async function fetchWithRetry(url, options, retries = 3) {
  for (let i = 0; i < retries; i++) {
    try {
      const response = await fetch(url, options);
      return await response.json();
    } catch (err) {
      if (i === retries - 1) throw err;
      
      // Wait before retry (exponential backoff)
      await new Promise(resolve => setTimeout(resolve, 1000 * Math.pow(2, i)));
    }
  }
}

// Usage
fetchWithRetry('/api/orders', {
  headers: { 'Authorization': 'Bearer ' + token }
})
.then(data => {
  // Process data
})
.catch(err => {
  showError('Không thể kết nối sau nhiều lần thử. Vui lòng kiểm tra kết nối.');
});
```

---

## Validation Before Submit

### Client-Side Validation

```javascript
function validateOrderForm(formData) {
  const errors = {};
  
  if (!formData.branchId) {
    errors.branchId = 'Vui lòng chọn chi nhánh';
  }
  
  if (!formData.items || formData.items.length === 0) {
    errors.items = 'Vui lòng chọn ít nhất một dịch vụ';
  }
  
  if (!formData.pickupAddress) {
    errors.pickupAddress = 'Vui lòng nhập địa chỉ lấy hàng';
  }
  
  if (!formData.pickupTime) {
    errors.pickupTime = 'Vui lòng chọn thời gian lấy hàng';
  }
  
  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
}

// Usage
function submitOrder(formData) {
  const validation = validateOrderForm(formData);
  
  if (!validation.isValid) {
    // Show errors
    Object.keys(validation.errors).forEach(field => {
      showFieldError(field, validation.errors[field]);
    });
    return;
  }
  
  // Proceed with API call
  createOrder(formData);
}
```

---

## Error Tracking

### Integration with Sentry

```javascript
// Initialize Sentry
Sentry.init({
  dsn: 'YOUR_SENTRY_DSN',
  environment: 'production'
});

// Capture API errors
fetch('/api/orders')
  .then(res => res.json())
  .then(data => {
    if (!data.success) {
      // Report to Sentry
      Sentry.captureMessage(data.error, {
        level: 'error',
        extra: {
          message: data.message,
          endpoint: '/api/orders',
          timestamp: data.timestamp
        }
      });
    }
  })
  .catch(err => {
    // Report network errors
    Sentry.captureException(err);
  });
```

---

## React Error Boundary

```javascript
class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
    Sentry.captureException(error);
  }

  render() {
    if (this.state.hasError) {
      return <h1>Đã xảy ra lỗi. Vui lòng tải lại trang.</h1>;
    }

    return this.props.children;
  }
}

// Usage
<ErrorBoundary>
  <App />
</ErrorBoundary>
```

---

[← Back to Main Documentation](../README.md)
