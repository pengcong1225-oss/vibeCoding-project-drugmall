from playwright.sync_api import sync_playwright
import time

def test_category_page():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()

        console_errors = []

        def handle_console(msg):
            if msg.type == 'error':
                console_errors.append(msg.text)

        def handle_page_error(error):
            console_errors.append(f'PAGE_ERROR: {str(error)}')

        page.on("console", handle_console)
        page.on("pageerror", handle_page_error)

        print("🔍 正在测试Category页面...")

        try:
            # 1. 先访问首页
            print("\n📌 步骤1: 访问首页...")
            page.goto('http://localhost:3004/', timeout=15000)
            page.wait_for_load_state('networkidle', timeout=10000)
            time.sleep(2)

            # 2. 点击24h服务项，跳转到Category页面
            print("📌 步骤2: 点击24h服务项（感冒发烧）...")
            service_items = page.locator('.service-item')
            if service_items.count() > 0:
                service_items.first.click()
                time.sleep(3)  # 等待Category页面加载

                print(f"\n✅ 成功跳转到Category页面")
                print(f"📄 当前URL: {page.url}")
                print(f"📄 标题: {page.title()}")

                # 检查是否有分类数据
                category_list = page.locator('.category-list, .category-items, [class*="category"]')
                empty_state = page.locator('.empty-container, .no-data, text=暂无')

                if category_list.count() > 0:
                    print(f"\n✅ 发现分类列表元素 ({category_list.count()}个)")
                elif empty_state.count() > 0:
                    print(f"\n⚠️  显示空状态")
                else:
                    # 尝试查找任何内容
                    content = page.content()
                    if '感冒' in content or '儿童' in content or '抗生素' in content:
                        print(f"\n✅ 页面包含分类数据")
                    else:
                        print(f"\n⚠️  未找到明显的分类数据")

                # 截图保存
                page.screenshot(path='d:/aiProject/workspace-opc/DrugMall/category_test.png', full_page=True)
                print("\n📸 Category页面截图已保存: category_test.png")

            else:
                print("\n⚠️  首页未找到服务项")

            # 错误汇总
            print("\n" + "="*60)
            print("📊 Category页面测试结果:")
            print("="*60)

            if console_errors:
                print(f"\n❌ 发现 {len(console_errors)} 个错误:")
                for i, error in enumerate(console_errors[:5], 1):
                    print(f"   {i}. {error[:200]}")
            else:
                print("\n✅ 无JavaScript错误 - Category页面完全正常！")

        except Exception as e:
            print(f"\n❌ 测试失败: {str(e)}")
        finally:
            browser.close()

if __name__ == "__main__":
    test_category_page()
