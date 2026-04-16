# DrugMall 前后端连通性测试脚本

Write-Host "======================================" -ForegroundColor Green
Write-Host "DrugMall 前后端连通性测试" -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green
Write-Host ""

$backendUrl = "http://localhost:8080/api"
$frontendUrl = "http://localhost:3002"

# 测试结果存储
$testResults = @()

function Test-ApiEndpoint {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Endpoint,
        [string]$Description
    )
    
    Write-Host "测试: $Description" -ForegroundColor Cyan
    Write-Host "  端点: $Endpoint" -ForegroundColor Gray
    
    try {
        $response = Invoke-WebRequest -Uri "$backendUrl$Endpoint" -Method $Method -UseBasicParsing -TimeoutSec 10
        $statusCode = $response.StatusCode
        $content = $response.Content
        
        # 解析JSON响应
        try {
            $jsonResponse = $content | ConvertFrom-Json
            if ($jsonResponse.code -eq 200 -or $jsonResponse.code -eq 0) {
                Write-Host "  结果: 通过 (HTTP $statusCode, Code: $($jsonResponse.code))" -ForegroundColor Green
                $testResults += [PSCustomObject]@{
                    API = $Name
                    Endpoint = $Endpoint
                    Method = $Method
                    Status = "通过"
                    HTTPStatus = $statusCode
                    ResponseCode = $jsonResponse.code
                    Message = "成功"
                }
                return $true
            } else {
                Write-Host "  结果: 失败 (HTTP $statusCode, Code: $($jsonResponse.code))" -ForegroundColor Yellow
                $testResults += [PSCustomObject]@{
                    API = $Name
                    Endpoint = $Endpoint
                    Method = $Method
                    Status = "失败"
                    HTTPStatus = $statusCode
                    ResponseCode = $jsonResponse.code
                    Message = $jsonResponse.message
                }
                return $false
            }
        } catch {
            Write-Host "  结果: 响应解析失败 (HTTP $statusCode)" -ForegroundColor Yellow
            $testResults += [PSCustomObject]@{
                API = $Name
                Endpoint = $Endpoint
                Method = $Method
                Status = "失败"
                HTTPStatus = $statusCode
                ResponseCode = "N/A"
                Message = "响应解析失败"
            }
            return $false
        }
    } catch {
        Write-Host "  结果: 错误 - 请求失败" -ForegroundColor Red
        $testResults += [PSCustomObject]@{
            API = $Name
            Endpoint = $Endpoint
            Method = $Method
            Status = "错误"
            HTTPStatus = "N/A"
            ResponseCode = "N/A"
            Message = "请求失败"
        }
        return $false
    }
}

function Test-FrontendProxy {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Endpoint,
        [string]$Description
    )
    
    Write-Host "测试: $Description (通过前端代理)" -ForegroundColor Cyan
    Write-Host "  端点: $frontendUrl/api/v1$Endpoint" -ForegroundColor Gray
    
    try {
        $response = Invoke-WebRequest -Uri "$frontendUrl/api/v1$Endpoint" -Method $Method -UseBasicParsing -TimeoutSec 10
        $statusCode = $response.StatusCode
        Write-Host "  结果: 代理正常 (HTTP $statusCode)" -ForegroundColor Green
        return $true
    } catch {
        if ($_.Exception.Response) {
            $statusCode = [int]$_.Exception.Response.StatusCode
            if ($statusCode -eq 404) {
                Write-Host "  结果: 代理配置正确，后端返回404 (HTTP $statusCode)" -ForegroundColor Yellow
            } else {
                Write-Host "  结果: HTTP $statusCode" -ForegroundColor Yellow
            }
        } else {
            Write-Host "  结果: 错误 - 请求失败" -ForegroundColor Red
        }
        return $false
    }
}

# 测试后端服务是否运行
Write-Host "1. 检查后端服务状态..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$backendUrl/user/info" -Method GET -UseBasicParsing -TimeoutSec 5
    Write-Host "   后端服务运行正常" -ForegroundColor Green
} catch {
    Write-Host "   后端服务可能未运行或存在错误" -ForegroundColor Red
}
Write-Host ""

# 测试前端服务是否运行
Write-Host "2. 检查前端服务状态..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$frontendUrl" -Method GET -UseBasicParsing -TimeoutSec 5
    Write-Host "   前端服务运行正常 (HTTP $($response.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "   前端服务可能未运行" -ForegroundColor Red
}
Write-Host ""

# 测试API连通性
Write-Host "3. 测试后端API连通性..." -ForegroundColor Yellow
Write-Host ""

# 用户模块
Test-ApiEndpoint -Name "用户信息" -Method "GET" -Endpoint "/user/info" -Description "获取用户信息"
Test-ApiEndpoint -Name "用户登录" -Method "POST" -Endpoint "/user/login" -Description "用户登录"

# 地址模块
Test-ApiEndpoint -Name "地址列表" -Method "GET" -Endpoint "/addresses" -Description "获取地址列表"

# 购物车模块
Test-ApiEndpoint -Name "购物车列表" -Method "GET" -Endpoint "/cart" -Description "获取购物车列表"

# 订单模块
Test-ApiEndpoint -Name "订单列表" -Method "GET" -Endpoint "/orders" -Description "获取订单列表"

# 药品模块
Test-ApiEndpoint -Name "药品列表" -Method "GET" -Endpoint "/drugs" -Description "获取药品列表"
Test-ApiEndpoint -Name "药品分类" -Method "GET" -Endpoint "/drugs/categories" -Description "获取药品分类"

Write-Host ""
Write-Host "4. 测试前端代理连通性..." -ForegroundColor Yellow
Write-Host ""

Test-FrontendProxy -Name "用户信息(代理)" -Method "GET" -Endpoint "/user/info" -Description "通过前端代理获取用户信息"

Write-Host ""
Write-Host "======================================" -ForegroundColor Green
Write-Host "测试汇总" -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green

$passed = ($testResults | Where-Object { $_.Status -eq "通过" }).Count
$failed = ($testResults | Where-Object { $_.Status -eq "失败" }).Count
$errors = ($testResults | Where-Object { $_.Status -eq "错误" }).Count

Write-Host "通过: $passed, 失败: $failed, 错误: $errors" -ForegroundColor Yellow
Write-Host ""

# 输出详细结果
$testResults | Format-Table -AutoSize

# 导出结果到JSON
$testResults | ConvertTo-Json -Depth 3 | Out-File -FilePath "d:\aiProject\workspace-opc\DrugMall\docs\测试文档\connectivity-test-results.json" -Encoding UTF8
Write-Host "详细结果已保存到: docs\测试文档\connectivity-test-results.json" -ForegroundColor Gray
