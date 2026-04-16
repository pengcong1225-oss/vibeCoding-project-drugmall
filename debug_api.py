from playwright.sync_api import sync_playwright
import json

def debug_api_response():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()

        api_responses = []

        def handle_response(response):
            if '/home/render/page' in response.url:
                try:
                    body = response.json()
                    api_responses.append({
                        'url': response.url,
                        'status': response.status,
                        'body': body
                    })
                except:
                    pass

        page.on("response", handle_response)

        print("🔍 正在测试API调用...")

        try:
            page.goto('http://localhost:3004/', timeout=15000)
            page.wait_for_load_state('networkidle', timeout=10000)

            if api_responses:
                print(f"\n✅ 捕获到 {len(api_responses)} 个API响应")
                for i, resp in enumerate(api_responses, 1):
                    print(f"\n{'='*60}")
                    print(f"📡 响应 #{i}")
                    print(f"URL: {resp['url']}")
                    print(f"状态码: {resp['status']}")
                    print(f"响应体结构:")
                    print(json.dumps(resp['body'], indent=2, ensure_ascii=False, default=str)[:1000])
                    print(f"\n{'='*60}")

                    if resp['status'] == 200 and 'data' in resp['body']:
                        data = resp['body']['data']
                        print(f"\n✅ API成功 - data字段包含:")
                        if isinstance(data, dict):
                            print(f"   - pageId: {data.get('pageId')}")
                            print(f"   - pageName: {data.get('pageName')}")
                            sections = data.get('sections', [])
                            print(f"   - 模块数量: {len(sections)}")
                            for section in sections[:3]:
                                print(f"      * {section.get('sectionType')} (visible={section.get('visible')})")
                    else:
                        print(f"\n❌ API异常 - 完整响应:")
                        print(json.dumps(resp['body'], indent=2, ensure_ascii=False, default=str))
            else:
                print("\n⚠️  未捕获到API响应")

        except Exception as e:
            print(f"\n❌ 测试失败: {str(e)}")
        finally:
            browser.close()

if __name__ == "__main__":
    debug_api_response()
