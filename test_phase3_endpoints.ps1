# ============================================
# Phase 3 Endpoints Testing Script
# Total: 24 endpoints to test
# ============================================

$baseUrl = "http://localhost:8080/api"
$token = ""

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "PHASE 3 ENDPOINTS TESTING - 24 ENDPOINTS" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

# Helper function to test endpoints
function Test-Endpoint {
    param(
        [string]$Method,
        [string]$Url,
        [string]$Description,
        [object]$Body = $null,
        [hashtable]$Headers = @{}
    )
    
    Write-Host "Testing: $Description" -ForegroundColor Yellow
    Write-Host "  $Method $Url" -ForegroundColor Gray
    
    try {
        $params = @{
            Method = $Method
            Uri = $Url
            Headers = $Headers
            ErrorAction = 'Stop'
        }
        
        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json -Depth 10)
            $params.ContentType = 'application/json'
        }
        
        $response = Invoke-RestMethod @params
        Write-Host "  ✓ SUCCESS" -ForegroundColor Green
        Write-Host "  Response: $($response | ConvertTo-Json -Compress -Depth 2)" -ForegroundColor Gray
        Write-Host ""
        return $true
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "  ✗ FAILED (Status: $statusCode)" -ForegroundColor Red
        Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
        return $false
    }
}

# Statistics
$stats = @{
    Total = 24
    Passed = 0
    Failed = 0
}

Write-Host "=== GROUP 1: ORDER STATISTICS - 3 endpoints ===" -ForegroundColor Cyan
Write-Host ""

# 1. GET /api/orders/statistics
if (Test-Endpoint -Method "GET" -Url "$baseUrl/orders/statistics" -Description "1. Order Statistics") {
    $stats.Passed++
} else {
    $stats.Failed++
}

# 2. GET /api/orders/revenue
if (Test-Endpoint -Method "GET" -Url "$baseUrl/orders/revenue?startDate=2024-01-01" -Description "2. Order Revenue by Date Range") {
    $stats.Passed++
} else {
    $stats.Failed++
}

# 3. GET /api/orders/top-customers
if (Test-Endpoint -Method "GET" -Url "$baseUrl/orders/top-customers?limit=5" -Description "3. Top Customers") {
    $stats.Passed++
} else {
    $stats.Failed++
}

Write-Host "=== GROUP 2: ORDER SEARCH - 4 endpoints ===" -ForegroundColor Cyan
Write-Host ""

# 4. GET /api/orders/search
if (Test-Endpoint -Method "GET" -Url "$baseUrl/orders/search?status=PENDING" -Description "4. Search Orders") {
    $stats.Passed++
} else {
    $stats.Failed++
}

# 5. GET /api/orders/advanced-search
if (Test-Endpoint -Method "GET" -Url "$baseUrl/orders/advanced-search?minTotal=100&maxTotal=1000&page=0&size=10" -Description "5. Advanced Search Orders") {
    $stats.Passed++
} else {
    $stats.Failed++
}

# 6. GET /api/orders/search/customer/{customerId}
if (Test-Endpoint -Method "GET" -Url "$baseUrl/orders/search/customer/1?page=0&size=10" -Description "6. Search Orders by Customer") {
    $stats.Passed++
} else {
    $stats.Failed++
}

# 7. GET /api/orders/search/date-range
if (Test-Endpoint -Method "GET" -Url "$baseUrl/orders/search/date-range?startDate=2024-01-01&endDate=2024-12-31&page=0&size=10" -Description "7. Search Orders by Date Range") {
    $stats.Passed++
} else {
    $stats.Failed++
}

Write-Host "=== GROUP 3: SERVICE SEARCH (2 endpoints) ===" -ForegroundColor Cyan
Write-Host ""

# 8. GET /api/services/advanced-search
if (Test-Endpoint -Method "GET" -Url "$baseUrl/services/advanced-search?name=wash&page=0&size=10" -Description "8. Advanced Search Services") {
    $stats.Passed++
} else {
    $stats.Failed++
}

# 9. GET /api/services/search/price-range
if (Test-Endpoint -Method "GET" -Url "$baseUrl/services/search/price-range?minPrice=10&maxPrice=100&page=0&size=10" -Description "9. Search Services by Price Range") {
    $stats.Passed++
} else {
    $stats.Failed++
}

Write-Host "=== GROUP 4: USER SEARCH (3 endpoints) ===" -ForegroundColor Cyan
Write-Host ""

# 10. GET /api/users/search
if (Test-Endpoint -Method "GET" -Url "$baseUrl/users/search?keyword=admin&page=0&size=10" -Description "10. Search Users") {
    $stats.Passed++
} else {
    $stats.Failed++
}

# 11. GET /api/users/search/role
if (Test-Endpoint -Method "GET" -Url "$baseUrl/users/search/role?roleName=CUSTOMER&page=0&size=10" -Description "11. Search Users by Role") {
    $stats.Passed++
} else {
    $stats.Failed++
}

# 12. GET /api/users/active
if (Test-Endpoint -Method "GET" -Url "$baseUrl/users/active?isActive=true&page=0&size=10" -Description "12. Search Active Users") {
    $stats.Passed++
} else {
    $stats.Failed++
}

Write-Host "=== GROUP 5: SHIPMENT STATISTICS (1 endpoint) ===" -ForegroundColor Cyan
Write-Host ""

# 13. GET /api/shipments/statistics
if (Test-Endpoint -Method "GET" -Url "$baseUrl/shipments/statistics" -Description "13. Shipment Statistics") {
    $stats.Passed++
} else {
    $stats.Failed++
}

Write-Host "=== GROUP 6: BRANCH STATISTICS (2 endpoints) ===" -ForegroundColor Cyan
Write-Host ""

# 14. GET /api/branches/statistics
if (Test-Endpoint -Method "GET" -Url "$baseUrl/branches/statistics" -Description "14. Branch Statistics") {
    $stats.Passed++
} else {
    $stats.Failed++
}

# 15. GET /api/branches/{branchId}/statistics
if (Test-Endpoint -Method "GET" -Url "$baseUrl/branches/1/statistics" -Description "15. Single Branch Statistics") {
    $stats.Passed++
} else {
    $stats.Failed++
}

Write-Host "=== GROUP 7: ATTACHMENT MANAGEMENT (7 endpoints) ===" -ForegroundColor Cyan
Write-Host ""

# 16. POST /api/attachments (create from URL)
$attachmentBody = @{
    orderId = 1
    fileUrl = "https://example.com/receipt.pdf"
    fileType = "RECEIPT"
}
if (Test-Endpoint -Method "POST" -Url "$baseUrl/attachments" -Description "16. Create Attachment from URL" -Body $attachmentBody) {
    $stats.Passed++
} else {
    $stats.Failed++
}

# 17. GET /api/attachments/{id}
if (Test-Endpoint -Method "GET" -Url "$baseUrl/attachments/1" -Description "17. Get Attachment Info") {
    $stats.Passed++
} else {
    $stats.Failed++
}

# 18. GET /api/attachments/order/{orderId}
if (Test-Endpoint -Method "GET" -Url "$baseUrl/attachments/order/1" -Description "18. Get Order Attachments") {
    $stats.Passed++
} else {
    $stats.Failed++
}

# 19. GET /api/attachments/shipment/{shipmentId}
if (Test-Endpoint -Method "GET" -Url "$baseUrl/attachments/shipment/1" -Description "19. Get Shipment Attachments") {
    $stats.Passed++
} else {
    $stats.Failed++
}

# Note: Upload and Download endpoints need special handling
Write-Host "Testing: 20. Upload Attachment (MultipartFile)" -ForegroundColor Yellow
Write-Host "  POST $baseUrl/attachments/upload" -ForegroundColor Gray
Write-Host "  ⚠ SKIPPED - Requires multipart file upload (manual test recommended)" -ForegroundColor Yellow
Write-Host ""

Write-Host "Testing: 21. Download Attachment" -ForegroundColor Yellow
Write-Host "  GET $baseUrl/attachments/1/download" -ForegroundColor Gray
Write-Host "  ⚠ SKIPPED - Requires file existence (manual test recommended)" -ForegroundColor Yellow
Write-Host ""

# 22. DELETE /api/attachments/{id} (will likely fail without auth)
Write-Host "Testing: 22. Delete Attachment" -ForegroundColor Yellow
Write-Host "  DELETE $baseUrl/attachments/1" -ForegroundColor Gray
Write-Host "  ⚠ SKIPPED - Requires ADMIN/STAFF authentication" -ForegroundColor Yellow
Write-Host ""

# Additional endpoints from soft-delete feature (already tested in previous phase)
Write-Host "=== BONUS: SOFT DELETE ENDPOINTS (2 endpoints - previously implemented) ===" -ForegroundColor Cyan
Write-Host ""

# 23. GET /api/soft-delete/orders
if (Test-Endpoint -Method "GET" -Url "$baseUrl/soft-delete/orders?page=0&size=10" -Description "23. Get Deleted Orders") {
    $stats.Passed++
} else {
    $stats.Failed++
}

# 24. GET /api/soft-delete/users
if (Test-Endpoint -Method "GET" -Url "$baseUrl/soft-delete/users?page=0&size=10" -Description "24. Get Deleted Users") {
    $stats.Passed++
} else {
    $stats.Failed++
}

Write-Host ""
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "Total Endpoints: $($stats.Total)" -ForegroundColor White
Write-Host "Passed: $($stats.Passed)" -ForegroundColor Green
Write-Host "Failed: $($stats.Failed)" -ForegroundColor Red
Write-Host "Skipped: 3 (Upload, Download, Delete - require authentication/files)" -ForegroundColor Yellow
Write-Host ""

$successRate = [math]::Round(($stats.Passed / $stats.Total) * 100, 2)
Write-Host "Success Rate: $successRate%" -ForegroundColor $(if ($successRate -gt 80) { "Green" } else { "Yellow" })
Write-Host ""

Write-Host "Note: Some endpoints may fail due to:" -ForegroundColor Yellow
Write-Host "  - Empty database (no test data)" -ForegroundColor Gray
Write-Host "  - Missing authentication tokens" -ForegroundColor Gray
Write-Host "  - Invalid IDs used in tests" -ForegroundColor Gray
Write-Host "  - Data validation requirements" -ForegroundColor Gray
Write-Host ""
Write-Host "Recommendation: Add test data to database for more accurate testing" -ForegroundColor Cyan
