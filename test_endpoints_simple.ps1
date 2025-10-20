# Simple Phase 3 Endpoint Testing Script
$baseUrl = "http://localhost:8080/api"
$passed = 0
$failed = 0

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "TESTING 24 PHASE 3 ENDPOINTS" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

function Test-API {
    param([string]$Url, [string]$Name)
    Write-Host "Testing: $Name" -ForegroundColor Yellow
    try {
        $response = Invoke-RestMethod -Uri $Url -Method GET -ErrorAction Stop
        Write-Host "  SUCCESS" -ForegroundColor Green
        $script:passed++
        return $true
    }
    catch {
        Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
        $script:failed++
        return $false
    }
}

Write-Host "GROUP 1: Order Statistics - 3 endpoints" -ForegroundColor Cyan
Test-API "$baseUrl/orders/statistics" "1. Order Statistics"
Test-API "$baseUrl/orders/revenue?startDate=2024-01-01" "2. Order Revenue"
Test-API "$baseUrl/orders/top-customers?limit=5" "3. Top Customers"
Write-Host ""

Write-Host "GROUP 2: Order Search - 4 endpoints" -ForegroundColor Cyan
Test-API "$baseUrl/orders/search?status=PENDING" "4. Search Orders"
Test-API "$baseUrl/orders/advanced-search?minTotal=100" "5. Advanced Search Orders"
Test-API "$baseUrl/orders/search/customer/1" "6. Search by Customer"
Test-API "$baseUrl/orders/search/date-range?startDate=2024-01-01" "7. Search by Date Range"
Write-Host ""

Write-Host "GROUP 3: Service Search - 2 endpoints" -ForegroundColor Cyan
Test-API "$baseUrl/services/advanced-search?name=wash" "8. Advanced Search Services"
Test-API "$baseUrl/services/search/price-range?minPrice=10" "9. Search by Price Range"
Write-Host ""

Write-Host "GROUP 4: User Search - 3 endpoints" -ForegroundColor Cyan
Test-API "$baseUrl/users/search?keyword=admin" "10. Search Users"
Test-API "$baseUrl/users/search/role?roleName=CUSTOMER" "11. Search by Role"
Test-API "$baseUrl/users/active?isActive=true" "12. Search Active Users"
Write-Host ""

Write-Host "GROUP 5: Shipment Statistics - 1 endpoint" -ForegroundColor Cyan
Test-API "$baseUrl/shipments/statistics" "13. Shipment Statistics"
Write-Host ""

Write-Host "GROUP 6: Branch Statistics - 2 endpoints" -ForegroundColor Cyan
Test-API "$baseUrl/branches/statistics" "14. All Branch Statistics"
Test-API "$baseUrl/branches/1/statistics" "15. Single Branch Statistics"
Write-Host ""

Write-Host "GROUP 7: Attachment Management - 7 endpoints" -ForegroundColor Cyan
Write-Host "Testing: 16. Create Attachment (POST)" -ForegroundColor Yellow
try {
    $body = @{
        orderId = 1
        fileUrl = "https://example.com/receipt.pdf"
        fileType = "RECEIPT"
    } | ConvertTo-Json
    $response = Invoke-RestMethod -Uri "$baseUrl/attachments" -Method POST -Body $body -ContentType "application/json" -ErrorAction Stop
    Write-Host "  SUCCESS" -ForegroundColor Green
    $passed++
}
catch {
    Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
    $failed++
}

Test-API "$baseUrl/attachments/1" "17. Get Attachment Info"
Test-API "$baseUrl/attachments/order/1" "18. Get Order Attachments"
Test-API "$baseUrl/attachments/shipment/1" "19. Get Shipment Attachments"

Write-Host "Testing: 20-22. Upload/Download/Delete Attachments" -ForegroundColor Yellow
Write-Host "  SKIPPED - Require special handling" -ForegroundColor Yellow
Write-Host ""

Write-Host "BONUS: Soft Delete - 2 endpoints" -ForegroundColor Cyan
Test-API "$baseUrl/soft-delete/orders" "23. Get Deleted Orders"
Test-API "$baseUrl/soft-delete/users" "24. Get Deleted Users"
Write-Host ""

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "TEST RESULTS" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "Passed: $passed" -ForegroundColor Green
Write-Host "Failed: $failed" -ForegroundColor Red
Write-Host "Skipped: 3" -ForegroundColor Yellow
Write-Host "Total: 24" -ForegroundColor White
$rate = [math]::Round(($passed / 21) * 100, 2)
Write-Host "Success Rate: $rate%" -ForegroundColor $(if ($rate -gt 80) { "Green" } else { "Yellow" })
