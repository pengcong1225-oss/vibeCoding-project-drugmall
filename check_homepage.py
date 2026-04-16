from playwright.sync_api import sync_playwright
import time

def check_homepage_errors():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()

        console_logs = []
        errors = []

        def handle_console(msg):
            console_logs.append(f"[{msg.type}] {msg.text}")

        def handle_page_error(error):
            errors.append(str(error))

        page.on("console", handle_console)
        page.on("pageerror", handle_page_error)

        print("🔍 正在访问首页: http://localhost:3004/")

        try:
            page.goto('http://localhost:3004/', timeout=15000)
            page.wait_for_load_state('networkidle', timeout=10000)
            time.sleep(2)

            print("\n✅ 页面加载完成")
            print(f"📄 标题: {page.title()}")
            print(f"🌐 URL: {page.url}")

            if errors:
                print("\n❌ 发现页面错误:")
                for i, error in enumerate(errors, 1):
                    print(f"  {i}. {error[:200]}")
            else:
                print("\n✅ 无JavaScript错误")

            error_logs = [log for log in console_logs if 'error' in log.lower()]
            if error_logs:
                print(f"\n⚠️  控制台错误日志 ({len(error_logs)}条):")
                for log in error_logs[:5]:
                    print(f"  - {log[:150]}")

            warning_logs = [log for log in console_logs if 'warning' in log.lower() or 'warn' in log.lower()]
            if warning_logs:
                print(f"\n⚠️  控制台警告日志 ({len(warning_logs)}条):")
                for log in warning_logs[:3]:
                    print(f"  - {log[:150]}")

            page.screenshot(path='d:/aiProject/workspace-opc/DrugMall/homepage_check.png', full_page=True)
            print("\n📸 截图已保存: homepage_check.png")

            html_content = page.content()
            if "加载失败" in html_content or "error" in html_content.lower():
                print("\n⚠️  页面显示有错误提示")
                error_element = page.locator('.error-container, .empty-container, [class*="error"]')
                if error_element.count() > 0:
                    print(f"   错误元素文本: {error_element.first.inner_text()}")

        except Exception as e:
            print(f"\n❌ 访问页面失败: {str(e)}")
        finally:
            browser.close()

if __name__ == "__main__":
    check_homepage_errors()
