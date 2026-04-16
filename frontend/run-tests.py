#!/usr/bin/env python3
"""
DrugMall患者端前端页面测试脚本
启动开发服务器并测试所有页面
"""
import os
import sys
import time
import subprocess
import signal
from datetime import datetime
from playwright.sync_api import sync_playwright

# 配置
BASE_URL = "http://localhost:3000"
SCREENSHOT_DIR = "test-screenshots"
SERVER_START_TIMEOUT = 30  # 服务器启动超时时间

def log(message, level="INFO"):
    """打印日志"""
    timestamp = datetime.now().strftime("%H:%M:%S")
    print(f"[{timestamp}] [{level}] {message}")

def ensure_dir(path):
    """确保目录存在"""
    if not os.path.exists(path):
        os.makedirs(path)

def start_server():
    """启动前端开发服务器"""
    log("启动前端开发服务器...")
    
    # 使用npm run dev启动服务器
    process = subprocess.Popen(
        ["npm", "run", "dev"],
        cwd=os.path.dirname(os.path.abspath(__file__)),
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        shell=True
    )
    
    # 等待服务器启动
    start_time = time.time()
    while time.time() - start_time < SERVER_START_TIMEOUT:
        try:
            import urllib.request
            urllib.request.urlopen(BASE_URL, timeout=2)
            log("服务器已启动")
            return process
        except:
            time.sleep(1)
    
    log("服务器启动超时", "ERROR")
    process.terminate()
    return None

def stop_server(process):
    """停止服务器"""
    if process:
        log("停止服务器...")
        process.terminate()
        try:
            process.wait(timeout=5)
        except:
            process.kill()

def test_page(page, page_info, screenshot_dir):
    """测试单个页面"""
    result = {
        "name": page_info["name"],
        "path": page_info["path"],
        "status": "pending",
        "load_time": 0,
        "screenshot": "",
        "errors": [],
        "missing_elements": [],
        "console_errors": []
    }
    
    url = f"{BASE_URL}{page_info['path']}"
    log(f"测试页面: {page_info['name']} ({url})")
    
    try:
        # 记录开始时间
        start_time = datetime.now()
        
        # 导航到页面
        response = page.goto(url, wait_until="networkidle", timeout=30000)
        
        # 计算加载时间
        load_time = (datetime.now() - start_time).total_seconds()
        result["load_time"] = round(load_time, 2)
        
        # 检查响应状态
        if response:
            if response.status == 404:
                result["status"] = "404"
                result["errors"].append("页面返回404")
                log(f"  页面返回404", "ERROR")
            elif response.status >= 500:
                result["status"] = "error"
                result["errors"].append(f"服务器错误: {response.status}")
                log(f"  服务器错误: {response.status}", "ERROR")
            else:
                result["status"] = "ok"
                log(f"  页面加载成功 ({load_time:.2f}s)")
        
        # 检查白屏（页面是否有内容）
        body_text = page.locator("body").inner_text()
        if len(body_text.strip()) < 50:
            result["status"] = "blank"
            result["errors"].append("页面可能是白屏或内容过少")
            log(f"  警告: 页面内容过少，可能是白屏", "WARN")
        
        # 检查关键元素
        for selector in page_info.get("selectors", []):
            try:
                element = page.locator(selector).first
                if element.is_visible(timeout=2000):
                    log(f"  元素存在: {selector}")
                else:
                    result["missing_elements"].append(selector)
                    log(f"  元素不可见: {selector}", "WARN")
            except Exception as e:
                result["missing_elements"].append(selector)
                log(f"  元素缺失: {selector}", "WARN")
        
        # 截图
        screenshot_name = f"{page_info['name'].replace('/', '_').replace(':', '_')}.png"
        screenshot_path = os.path.join(screenshot_dir, screenshot_name)
        page.screenshot(path=screenshot_path, full_page=True)
        result["screenshot"] = screenshot_path
        log(f"  截图保存: {screenshot_path}")
        
    except Exception as e:
        result["status"] = "error"
        result["errors"].append(str(e))
        log(f"  测试失败: {str(e)}", "ERROR")
    
    return result

def generate_report(results, output_path):
    """生成测试报告"""
    report_lines = [
        "# DrugMall患者端前端页面测试报告",
        "",
        f"**测试时间**: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}",
        f"**测试环境**: {BASE_URL}",
        "",
        "## 测试汇总",
        "",
        "| 页面 | 状态 | 加载时间 | 缺失元素 | 错误 |",
        "|------|------|----------|----------|------|"
    ]
    
    status_count = {"ok": 0, "404": 0, "blank": 0, "error": 0, "pending": 0}
    
    for r in results:
        status_emoji = {
            "ok": "✅",
            "404": "❌",
            "blank": "⚠️",
            "error": "❌",
            "pending": "⏳"
        }.get(r["status"], "❓")
        
        status_count[r["status"]] = status_count.get(r["status"], 0) + 1
        
        missing = len(r["missing_elements"])
        errors = len(r["errors"])
        
        report_lines.append(
            f"| {r['name']} | {status_emoji} {r['status']} | {r['load_time']}s | {missing} | {errors} |"
        )
    
    # 汇总统计
    report_lines.extend([
        "",
        "### 统计",
        "",
        f"- 通过: {status_count['ok']} 个页面",
        f"- 404错误: {status_count['404']} 个页面",
        f"- 白屏: {status_count['blank']} 个页面",
        f"- 其他错误: {status_count['error']} 个页面",
        "",
        "---",
        "",
        "## 详细结果",
        ""
    ])
    
    # 详细结果
    for r in results:
        report_lines.extend([
            f"### {r['name']}",
            "",
            f"- **路径**: {r['path']}",
            f"- **状态**: {r['status']}",
            f"- **加载时间**: {r['load_time']}s",
            f"- **截图**: {r['screenshot']}",
            ""
        ])
        
        if r["missing_elements"]:
            report_lines.extend([
                "**缺失元素**:",
                ""
            ])
            for elem in r["missing_elements"]:
                report_lines.append(f"- `{elem}`")
            report_lines.append("")
        
        if r["errors"]:
            report_lines.extend([
                "**错误**:",
                ""
            ])
            for err in r["errors"]:
                report_lines.append(f"- {err}")
            report_lines.append("")
        
        report_lines.append("---")
    
    # 写入文件
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(report_lines))
    
    log(f"报告已生成: {output_path}")

def main():
    """主函数"""
    log("=" * 60)
    log("DrugMall患者端前端页面测试开始")
    log("=" * 60)
    
    # 创建截图目录
    screenshot_dir = os.path.join(os.path.dirname(__file__), SCREENSHOT_DIR)
    ensure_dir(screenshot_dir)
    
    # 测试页面列表
    pages = [
        {"name": "首页", "path": "/home", "selectors": [".home-page", ".banner", ".service-area"]},
        {"name": "药品详情页", "path": "/drug/1", "selectors": [".drug-detail", ".drug-info"]},
        {"name": "购物车", "path": "/cart", "selectors": [".cart-page", ".cart-list"]},
        {"name": "订单确认", "path": "/order/confirm", "selectors": [".order-confirm"]},
        {"name": "订单列表", "path": "/order/list", "selectors": [".order-list"]},
        {"name": "个人中心", "path": "/user", "selectors": [".user-page"]},
        {"name": "搜索页", "path": "/search", "selectors": [".search-page"]},
        {"name": "分类页", "path": "/category", "selectors": [".category-page"]},
        {"name": "药店详情", "path": "/store/1", "selectors": [".store-detail"]},
        {"name": "问诊页", "path": "/inquiry", "selectors": [".inquiry-page"]},
        {"name": "地址管理", "path": "/address", "selectors": [".address-list"]},
    ]
    
    results = []
    server_process = None
    
    try:
        # 启动服务器
        server_process = start_server()
        if not server_process:
            log("服务器启动失败，退出测试", "ERROR")
            return
        
        # 等待服务器完全就绪
        time.sleep(3)
        
        with sync_playwright() as p:
            # 启动浏览器
            browser = p.chromium.launch(headless=True)
            context = browser.new_context(
                viewport={'width': 1280, 'height': 800}
            )
            page = context.new_page()
            
            try:
                # 测试每个页面
                for page_info in pages:
                    result = test_page(page, page_info, screenshot_dir)
                    results.append(result)
                    log("")
                
            finally:
                browser.close()
    
    finally:
        # 停止服务器
        stop_server(server_process)
    
    # 生成报告
    report_path = os.path.join(os.path.dirname(__file__), 'test-report.md')
    generate_report(results, report_path)
    
    # 打印汇总
    log("=" * 60)
    log("测试完成")
    log("=" * 60)
    log(f"通过: {sum(1 for r in results if r['status'] == 'ok')}/{len(results)}")
    log(f"失败: {sum(1 for r in results if r['status'] != 'ok')}/{len(results)}")
    log(f"报告: {report_path}")
    
    return results

if __name__ == "__main__":
    main()
