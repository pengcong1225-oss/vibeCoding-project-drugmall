# DrugMall 全服务启动脚本
# 端口：后端8080/8081，前端3003/3004/3005
# 端口占用时自动释放后重启

$ErrorActionPreference = "Continue"
$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

$ports = @(8080, 8081, 3003, 3004, 3005)
$services = @(
    @{Name="后端主服务"; Dir="backend"; Port=8080; Cmd="mvn spring-boot:run"; Type="backend"},
    @{Name="管理后端"; Dir="drugmall-admin-backend"; Port=8081; Cmd="mvn spring-boot:run"; Type="backend"},
    @{Name="商城端"; Dir="frontend"; Port=3003; Cmd="npm run dev"; Type="frontend"},
    @{Name="医生端"; Dir="frontend-doctor"; Port=3004; Cmd="npm run dev"; Type="frontend"},
    @{Name="管理端"; Dir="drugmall-admin"; Port=3005; Cmd="npm run dev"; Type="frontend"}
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  DrugMall 全服务启动" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 步骤1：释放所有被占用端口
Write-Host "[1/5] 检查并释放端口..." -ForegroundColor Yellow
foreach ($port in $ports) {
    $connections = netstat -ano | Select-String ":$port " | Select-String "LISTENING"
    if ($connections) {
        foreach ($conn in $connections) {
            $parts = $conn -split '\s+'
            $pid = $parts[-1]
            if ($pid -match '^\d+$') {
                Write-Host "  端口 $port 被 PID $pid 占用，正在释放..." -ForegroundColor Red
                taskkill /F /PID $pid 2>$null | Out-Null
                Start-Sleep -Seconds 1
            }
        }
    }
}
Write-Host "  端口检查完成" -ForegroundColor Green
Write-Host ""

# 步骤2：启动后端服务
Write-Host "[2/5] 启动后端服务..." -ForegroundColor Yellow
foreach ($svc in $services | Where-Object { $_.Type -eq "backend" }) {
    $svcDir = Join-Path $ProjectRoot $svc.Dir
    Write-Host "  启动 $($svc.Name) (端口 $($svc.Port))..." -ForegroundColor White
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$svcDir'; Write-Host '$($svc.Name) 启动中...'; $($svc.Cmd)" -WindowStyle Minimized
    Start-Sleep -Seconds 8
}
Write-Host "  后端服务启动中（等待15秒初始化）..." -ForegroundColor Green
Start-Sleep -Seconds 15
Write-Host ""

# 步骤3：启动前端服务
Write-Host "[3/5] 启动前端服务..." -ForegroundColor Yellow
foreach ($svc in $services | Where-Object { $_.Type -eq "frontend" }) {
    $svcDir = Join-Path $ProjectRoot $svc.Dir
    Write-Host "  启动 $($svc.Name) (端口 $($svc.Port))..." -ForegroundColor White
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$svcDir'; Write-Host '$($svc.Name) 启动中...'; $($svc.Cmd)" -WindowStyle Minimized
    Start-Sleep -Seconds 5
}
Write-Host "  前端服务启动中..." -ForegroundColor Green
Start-Sleep -Seconds 8
Write-Host ""

# 步骤4：验证所有端口
Write-Host "[4/5] 验证端口状态..." -ForegroundColor Yellow
$allOk = $true
foreach ($port in $ports) {
    $listening = netstat -ano | Select-String ":$port " | Select-String "LISTENING"
    if ($listening) {
        Write-Host "  端口 $port : LISTENING" -ForegroundColor Green
    } else {
        Write-Host "  端口 $port : 未就绪" -ForegroundColor Red
        $allOk = $false
    }
}
Write-Host ""

# 步骤5：输出访问地址
Write-Host "[5/5] 服务访问地址:" -ForegroundColor Cyan
Write-Host "  商城端:   http://localhost:3003/" -ForegroundColor White
Write-Host "  医生端:   http://localhost:3004/" -ForegroundColor White
Write-Host "  管理端:   http://localhost:3005/" -ForegroundColor White
Write-Host "  后端API:  http://localhost:8080/api/doc.html" -ForegroundColor White
Write-Host "  管理API:  http://localhost:8081/api/" -ForegroundColor White
Write-Host ""

if ($allOk) {
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  全部服务启动完成!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
} else {
    Write-Host "========================================" -ForegroundColor Yellow
    Write-Host "  部分服务可能仍在初始化，请稍后重试" -ForegroundColor Yellow
    Write-Host "========================================" -ForegroundColor Yellow
}