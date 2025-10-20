# PHASE 3 - Test Script for Remaining Endpoints
# 12 endpoints: Notifications (3), Batch (4), Operational (5)

$baseUrl = "http://localhost:8080/api"
$token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MzQ3MzI4MDMsImV4cCI6MTczNDgxOTIwM30.g_X7fvGvB8hfE-Ey6Ym8kMRnlPqQs8DWrpbBBbdqN1M"
$headers = @{ "Authorization" = $token; "Content-Type" = "application/json" }

Write-Host "`n===== PHASE 3 REMAINING ENDPOINTS TEST =====" -ForegroundColor Cyan
$passed = 0
$failed = 0

# 1. GET All Notifications
Write-Host "`n1. GET /api/notifications" -ForegroundColor Yellow
try {
    $r = Invoke-RestMethod -Uri ($baseUrl + '/notifications?page=0&size=5') -Method GET -Headers $headers
    Write-Host "   PASS - Found $($r.data.totalElements) notifications" -ForegroundColor Green
    $passed++
} catch {Write-Host "   FAIL - $_" -ForegroundColor Red; $failed++}

# 2. GET User Notifications
Write-Host "`n2. GET /api/notifications/user/1" -ForegroundColor Yellow
try {
    $r = Invoke-RestMethod -Uri ($baseUrl + '/notifications/user/1?page=0&size=5') -Method GET -Headers $headers
    Write-Host "   PASS - Found $($r.data.totalElements) notifications" -ForegroundColor Green
    $passed++
} catch {Write-Host "   FAIL - $_" -ForegroundColor Red; $failed++}

# 3. POST Create Notification
Write-Host "`n3. POST /api/notifications" -ForegroundColor Yellow
try {
    $body = '{"userId":1,"title":"Test","message":"Test","type":"SYSTEM"}'
    $r = Invoke-RestMethod -Uri ($baseUrl + '/notifications') -Method POST -Headers $headers -Body $body
    Write-Host "   PASS - Created ID $($r.data.id)" -ForegroundColor Green
    $passed++
} catch {Write-Host "   FAIL - $_" -ForegroundColor Red; $failed++}

# 4. PATCH Batch Update Order Status
Write-Host "`n4. PATCH /api/orders/batch/status" -ForegroundColor Yellow
try {
    $body = '{"orderIds":[1,2,3],"status":"PROCESSING"}'
    $r = Invoke-RestMethod -Uri ($baseUrl + '/orders/batch/status') -Method PATCH -Headers $headers -Body $body
    Write-Host "   PASS - Updated $($r.data) orders" -ForegroundColor Green
    $passed++
} catch {Write-Host "   FAIL - $_" -ForegroundColor Red; $failed++}

# 5. DELETE Batch Cancel Orders  
Write-Host "`n5. DELETE /api/orders/batch" -ForegroundColor Yellow
try {
    $body = '{"orderIds":[4,5]}'
    $r = Invoke-RestMethod -Uri ($baseUrl + '/orders/batch') -Method DELETE -Headers $headers -Body $body
    Write-Host "   PASS - Cancelled $($r.data) orders" -ForegroundColor Green
    $passed++
} catch {Write-Host "   FAIL - $_" -ForegroundColor Red; $failed++}

# 6. PATCH Batch Activate Users
Write-Host "`n6. PATCH /api/users/batch/activate" -ForegroundColor Yellow
try {
    $body = '{"userIds":[1,2]}'
    $r = Invoke-RestMethod -Uri ($baseUrl + '/users/batch/activate') -Method PATCH -Headers $headers -Body $body
    Write-Host "   PASS - Activated $($r.data) users" -ForegroundColor Green
    $passed++
} catch {Write-Host "   FAIL - $_" -ForegroundColor Red; $failed++}

# 7. PATCH Batch Deactivate Users
Write-Host "`n7. PATCH /api/users/batch/deactivate" -ForegroundColor Yellow
try {
    $body = '{"userIds":[3,4]}'
    $r = Invoke-RestMethod -Uri ($baseUrl + '/users/batch/deactivate') -Method PATCH -Headers $headers -Body $body
    Write-Host "   PASS - Deactivated $($r.data) users" -ForegroundColor Green
    $passed++
} catch {Write-Host "   FAIL - $_" -ForegroundColor Red; $failed++}

# 8. GET Reviews by Service
Write-Host "`n8. GET /api/reviews/service/1" -ForegroundColor Yellow
try {
    $r = Invoke-RestMethod -Uri ($baseUrl + '/reviews/service/1') -Method GET -Headers $headers
    Write-Host "   PASS - Found $($r.data.Count) reviews" -ForegroundColor Green
    $passed++
} catch {Write-Host "   FAIL - $_" -ForegroundColor Red; $failed++}

# 9. GET Reviews by User
Write-Host "`n9. GET /api/reviews/user/1" -ForegroundColor Yellow
try {
    $r = Invoke-RestMethod -Uri ($baseUrl + '/reviews/user/1') -Method GET -Headers $headers
    Write-Host "   PASS - Found $($r.data.Count) reviews" -ForegroundColor Green
    $passed++
} catch {Write-Host "   FAIL - $_" -ForegroundColor Red; $failed++}

# 10. GET Reviews by Rating
Write-Host "`n10. GET /api/reviews/rating/5" -ForegroundColor Yellow
try {
    $r = Invoke-RestMethod -Uri ($baseUrl + '/reviews/rating/5') -Method GET -Headers $headers
    Write-Host "   PASS - Found $($r.data.Count) 5-star reviews" -ForegroundColor Green
    $passed++
} catch {Write-Host "   FAIL - $_" -ForegroundColor Red; $failed++}

# 11. GET Search Branches
Write-Host "`n11. GET /api/branches/search" -ForegroundColor Yellow
try {
    $r = Invoke-RestMethod -Uri ($baseUrl + '/branches/search?isActive=true') -Method GET -Headers $headers
    Write-Host "   PASS - Found $($r.data.Count) branches" -ForegroundColor Green
    $passed++
} catch {Write-Host "   FAIL - $_" -ForegroundColor Red; $failed++}

# 12. GET Nearby Branches
Write-Host "`n12. GET /api/branches/nearby" -ForegroundColor Yellow
try {
    $r = Invoke-RestMethod -Uri ($baseUrl + '/branches/nearby?lat=10.762622&lng=106.660172&radius=10') -Method GET -Headers $headers
    Write-Host "   PASS - Found $($r.data.Count) nearby branches" -ForegroundColor Green
    $passed++
} catch {Write-Host "   FAIL - $_" -ForegroundColor Red; $failed++}

# Summary
$total = $passed + $failed
Write-Host "`n===== TEST SUMMARY =====" -ForegroundColor Cyan
Write-Host "Total: $total | Passed: $passed | Failed: $failed" -ForegroundColor White
Write-Host "Success Rate: $([math]::Round(($passed/$total)*100,2))%`n" -ForegroundColor $(if($failed -eq 0){"Green"}else{"Yellow"})
