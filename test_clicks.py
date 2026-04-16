from playwright.sync_api import sync_playwright
import time

def test_click_interactions():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()

        click_errors = []
        console_logs = []

        def handle_console(msg):
            if msg.type == 'error':
                console_logs.append(msg.text)

        def handle_page_error(error):
            click_errors.append(str(error))

        page.on("console", handle_console)
        page.on("pageerror", handle_page_error)

        print("🔍 正在测试所有交互功能...")

        try:
            page.goto('http://localhost:3004/', timeout=15000)
            page.wait_for_load_state('networkidle', timeout=10000)
            time.sleep(2)

            # 测试1: 点击24h服务项
            print("\n📌 测试1: 点击24h服务项...")
            service_items = page.locator('.service-item')
            if service_items.count() > 0:
                service_items.first.click()
                time.sleep(1)
                print(f"   ✅ 点击成功 (共{service_items.count()}个服务项)")
            else:
                print("   ⚠️  未找到服务项")

            # 测试2: 切换Tab
            print("\n📌 测试2: 切换Tab到'问医生'...")
            tab_doctor = page.locator('text=问医生').first
            if tab_doctor.count() > 0:
                tab_doctor.click()
                time.sleep(1)
                print(f"   ✅ Tab切换成功")
            else:
                print("   ⚠️  未找到问医生Tab")

            # 测试3: 点击Banner区域
            print("\n📌 测试3: 点击Banner轮播...")
            banner = page.locator('.banner-container, .carousel-wrapper').first
            if banner.count() > 0:
                banner.click()
                time.sleep(1)
                print(f"   ✅ Banner点击成功")
            else:
                print("   ⚠️  未找到Banner")

            # 测试4: 点击商品卡片
            print("\n📌 测试4: 点击瀑布流商品卡片...")
            product_cards = page.locator('.product-card')
            if product_cards.count() > 0:
                product_cards.first.click()
                time.sleep(1)
                print(f"   ✅ 商品卡片点击成功 (共{product_cards.count()}个)")
            else:
                print("   ⚠️  未找到商品卡片")

            # 测试5: 点击药店卡片
            print("\n📌 测试5: 点击附近药店...")
            pharmacy_cards = page.locator('.pharmacy-card')
            if pharmacy_cards.count() > 0:
                pharmacy_cards.first.click()
                time.sleep(1)
                print(f"   ✅ 药店卡片点击成功 (共{pharmacy_cards.count()}个)")
            else:
                print("   ⚠️  未找到药店卡片")

            # 测试6: 点击搜索框
            print("\n📌 测试6: 点击搜索框...")
            search_input = page.locator('input[placeholder*="搜索"]')
            if search_input.count() > 0:
                search_input.click()
                time.sleep(1)
                print(f"   ✅ 搜索框点击成功")
            else:
                print("   ⚠️  未找到搜索框")

            # 汇总错误
            print("\n" + "="*60)
            print("📊 测试结果汇总:")
            print("="*60)

            if click_errors:
                print(f"\n❌ 发现 {len(click_errors)} 个页面错误:")
                for i, error in enumerate(click_errors, 1):
                    print(f"\n   错误 #{i}:")
                    print(f"   {error[:300]}")
            else:
                print("\n✅ 无页面JavaScript错误")

            if console_logs:
                print(f"\n⚠️  控制台错误日志 ({len(console_logs)}条):")
                for log in console_logs[:5]:
                    print(f"   - {log[:200]}")

            # 截图保存最终状态
            page.screenshot(path='d:/aiProject/workspace-opc/DrugMall/click_test_result.png', full_page=True)
            print("\n📸 最终状态截图已保存: click_test_result.png")

        except Exception as e:
            print(f"\n❌ 测试失败: {str(e)}")
        finally:
            browser.close()

if __name__ == "__main__":
    test_click_interactions()
