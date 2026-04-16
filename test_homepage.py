from playwright.sync_api import sync_playwright
import time

with sync_playwright() as p:
    browser = p.chromium.launch(headless=True)
    page = browser.new_page(viewport={'width': 375, 'height': 812})
    
    # 监听控制台日志
    page.on("console", lambda msg: print(f"CONSOLE [{msg.type}]: {msg.text}"))
    page.on("pageerror", lambda err: print(f"PAGE ERROR: {err}"))
    
    print("正在访问 http://localhost:3003/...")
    page.goto('http://localhost:3003/')
    
    # 等待页面加载
    page.wait_for_load_state('networkidle', timeout=10000)
    time.sleep(3)
    
    # 截图查看页面状态
    page.screenshot(path='d:/aiProject/workspace-opc/DrugMall/homepage_final.png', full_page=True)
    print("截图已保存到 homepage_final.png")
    
    # 检查页面内容
    content = page.content()
    print(f"\n页面内容长度: {len(content)} 字符")
    
    # 检查关键元素
    elements_to_check = [
        '.banner-section',
        '.search-section',
        '.category-tabs', 
        '.service-section',
        '.doctor-section',
        '.subsidy-section',
        '.nearby-section'
    ]
    
    print("\n元素检查:")
    for selector in elements_to_check:
        count = page.locator(selector).count()
        print(f"  {selector}: {'✓' if count > 0 else '✗'}")
    
    # 获取页面标题
    title = page.title()
    print(f"\n页面标题: {title}")
    
    browser.close()
    print("\n测试完成")
