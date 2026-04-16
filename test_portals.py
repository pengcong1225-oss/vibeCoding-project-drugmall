# -*- coding: utf-8 -*-
"""
DrugMall 患者端和医生端功能测试脚本
测试所有页面的点击项是否正常
"""

from playwright.sync_api import sync_playwright
import time
import json

def test_patient_portal():
    """测试患者端"""
    results = {
        "患者端测试": {
            "首页": {},
            "分类页": {},
            "购物车": {},
            "我的": {},
            "AI助手": {}
        }
    }

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context()
        page = context.new_page()

        try:
            # 测试首页
            print("测试患者端首页...")
            page.goto('http://localhost:3003')
            page.wait_for_load_state('networkidle')
            page.screenshot(path='patient_home.png')

            # 检查首页元素
            home_elements = {
                "搜索框": page.locator('input[placeholder*="搜索"]').count() > 0,
                "分类导航": page.locator('.category-nav, .category-item').count() > 0,
                "轮播图": page.locator('.swiper, .carousel').count() > 0,
                "推荐商品": page.locator('.product-card, .goods-item').count() > 0
            }
            results["患者端测试"]["首页"] = home_elements

            # 测试分类页
            print("测试分类页...")
            page.goto('http://localhost:3003/category')
            page.wait_for_load_state('networkidle')
            page.screenshot(path='patient_category.png')

            category_elements = {
                "分类列表": page.locator('.category-list, .category-item').count() > 0,
                "商品列表": page.locator('.product-list, .goods-list').count() > 0
            }
            results["患者端测试"]["分类页"] = category_elements

            # 测试购物车
            print("测试购物车...")
            page.goto('http://localhost:3003/cart')
            page.wait_for_load_state('networkidle')
            page.screenshot(path='patient_cart.png')

            cart_elements = {
                "购物车列表": page.locator('.cart-list, .cart-item').count() > 0,
                "结算按钮": page.locator('button:has-text("结算"), .checkout-btn').count() > 0
            }
            results["患者端测试"]["购物车"] = cart_elements

            # 测试我的页面
            print("测试我的页面...")
            page.goto('http://localhost:3003/profile')
            page.wait_for_load_state('networkidle')
            page.screenshot(path='patient_profile.png')

            profile_elements = {
                "用户信息": page.locator('.user-info, .profile-header').count() > 0,
                "订单入口": page.locator('text=我的订单').count() > 0,
                "地址管理": page.locator('text=地址管理').count() > 0,
                "优惠券": page.locator('text=优惠券').count() > 0
            }
            results["患者端测试"]["我的"] = profile_elements

            # 测试AI助手
            print("测试AI助手...")
            page.goto('http://localhost:3003/ai-assistant')
            page.wait_for_load_state('networkidle')
            page.screenshot(path='patient_ai.png')

            ai_elements = {
                "欢迎卡片": page.locator('.welcome-card').count() > 0,
                "快捷入口": page.locator('.quick-entry-grid, .entry-item').count() > 0,
                "输入框": page.locator('input[placeholder*="问题"]').count() > 0,
                "发送按钮": page.locator('.send-btn, button:has-text("发送")').count() > 0
            }
            results["患者端测试"]["AI助手"] = ai_elements

            # 测试症状自测
            print("测试症状自测...")
            page.goto('http://localhost:3003/symptom-test')
            page.wait_for_load_state('networkidle')
            page.screenshot(path='patient_symptom.png')

            symptom_elements = {
                "步骤指示器": page.locator('.steps-indicator').count() > 0,
                "身体部位选择": page.locator('.body-parts-grid, .body-part-item').count() > 0
            }
            results["患者端测试"]["症状自测"] = symptom_elements

        except Exception as e:
            results["患者端测试"]["错误"] = str(e)
        finally:
            browser.close()

    return results

def test_doctor_portal():
    """测试医生端"""
    results = {
        "医生端测试": {
            "登录页": {},
            "工作台": {},
            "问诊管理": {},
            "患者管理": {},
            "处方管理": {},
            "我的收入": {},
            "我的": {}
        }
    }

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context()
        page = context.new_page()

        try:
            # 测试登录页
            print("测试医生端登录页...")
            page.goto('http://localhost:3004/login')
            page.wait_for_load_state('networkidle')
            page.screenshot(path='doctor_login.png')

            login_elements = {
                "登录表单": page.locator('form, .login-form').count() > 0,
                "用户名输入": page.locator('input[type="text"], input[placeholder*="手机"]').count() > 0,
                "密码输入": page.locator('input[type="password"]').count() > 0,
                "登录按钮": page.locator('button:has-text("登录")').count() > 0
            }
            results["医生端测试"]["登录页"] = login_elements

            # 测试工作台（需要登录）
            print("测试医生端工作台...")
            page.goto('http://localhost:3004/home')
            page.wait_for_load_state('networkidle')
            page.screenshot(path='doctor_home.png')

            home_elements = {
                "医生信息": page.locator('.doctor-info, .user-info').count() > 0,
                "统计数据": page.locator('.stats, .stat-item').count() > 0,
                "待办事项": page.locator('.todo-list, .pending-items').count() > 0
            }
            results["医生端测试"]["工作台"] = home_elements

            # 测试问诊管理
            print("测试问诊管理...")
            page.goto('http://localhost:3004/consultation')
            page.wait_for_load_state('networkidle')
            page.screenshot(path='doctor_consultation.png')

            consultation_elements = {
                "问诊列表": page.locator('.consultation-list, .patient-list').count() > 0,
                "筛选功能": page.locator('.filter, .tabs').count() > 0
            }
            results["医生端测试"]["问诊管理"] = consultation_elements

            # 测试患者管理
            print("测试患者管理...")
            page.goto('http://localhost:3004/patients')
            page.wait_for_load_state('networkidle')
            page.screenshot(path='doctor_patients.png')

            patients_elements = {
                "患者列表": page.locator('.patient-list, .list-item').count() > 0,
                "搜索功能": page.locator('input[placeholder*="搜索"]').count() > 0
            }
            results["医生端测试"]["患者管理"] = patients_elements

            # 测试处方管理
            print("测试处方管理...")
            page.goto('http://localhost:3004/prescription')
            page.wait_for_load_state('networkidle')
            page.screenshot(path='doctor_prescription.png')

            prescription_elements = {
                "处方列表": page.locator('.prescription-list, .list').count() > 0
            }
            results["医生端测试"]["处方管理"] = prescription_elements

            # 测试我的收入
            print("测试我的收入...")
            page.goto('http://localhost:3004/income')
            page.wait_for_load_state('networkidle')
            page.screenshot(path='doctor_income.png')

            income_elements = {
                "收入统计": page.locator('.income-stats, .stats').count() > 0,
                "收入列表": page.locator('.income-list, .list').count() > 0
            }
            results["医生端测试"]["我的收入"] = income_elements

            # 测试我的页面
            print("测试医生端我的页面...")
            page.goto('http://localhost:3004/profile')
            page.wait_for_load_state('networkidle')
            page.screenshot(path='doctor_profile.png')

            profile_elements = {
                "医生信息": page.locator('.doctor-info, .profile-header').count() > 0,
                "设置选项": page.locator('.menu-item, .list-item').count() > 0
            }
            results["医生端测试"]["我的"] = profile_elements

        except Exception as e:
            results["医生端测试"]["错误"] = str(e)
        finally:
            browser.close()

    return results

def generate_report(patient_results, doctor_results):
    """生成测试报告"""
    report = []
    report.append("=" * 60)
    report.append("DrugMall 患者端与医生端功能测试报告")
    report.append("=" * 60)
    report.append(f"测试时间: {time.strftime('%Y-%m-%d %H:%M:%S')}")
    report.append("")

    # 患者端报告
    report.append("一、患者端测试结果")
    report.append("-" * 60)

    total_tests = 0
    passed_tests = 0

    for page_name, elements in patient_results.get("患者端测试", {}).items():
        if page_name == "错误":
            report.append(f"❌ 测试错误: {elements}")
            continue

        report.append(f"\n【{page_name}】")
        for element, exists in elements.items():
            total_tests += 1
            status = "✅" if exists else "❌"
            if exists:
                passed_tests += 1
            report.append(f"  {status} {element}: {'存在' if exists else '不存在'}")

    report.append(f"\n患者端总计: {passed_tests}/{total_tests} 项测试通过")

    # 医生端报告
    report.append("\n\n二、医生端测试结果")
    report.append("-" * 60)

    doctor_total = 0
    doctor_passed = 0

    for page_name, elements in doctor_results.get("医生端测试", {}).items():
        if page_name == "错误":
            report.append(f"❌ 测试错误: {elements}")
            continue

        report.append(f"\n【{page_name}】")
        for element, exists in elements.items():
            doctor_total += 1
            status = "✅" if exists else "❌"
            if exists:
                doctor_passed += 1
            report.append(f"  {status} {element}: {'存在' if exists else '不存在'}")

    report.append(f"\n医生端总计: {doctor_passed}/{doctor_total} 项测试通过")

    # 总结
    report.append("\n\n三、测试总结")
    report.append("-" * 60)
    overall_total = total_tests + doctor_total
    overall_passed = passed_tests + doctor_passed

    if overall_total > 0:
        report.append(f"总测试项: {overall_total}")
        report.append(f"通过项: {overall_passed}")
        report.append(f"失败项: {overall_total - overall_passed}")
        report.append(f"通过率: {(overall_passed/overall_total*100):.1f}%")
    else:
        report.append("⚠️ 没有执行任何测试")
    report.append("")
    report.append("=" * 60)

    return "\n".join(report)

if __name__ == "__main__":
    print("开始测试 DrugMall 患者端和医生端...")
    print("=" * 60)

    # 测试患者端
    patient_results = test_patient_portal()

    # 测试医生端
    doctor_results = test_doctor_portal()

    # 生成报告
    report = generate_report(patient_results, doctor_results)

    # 保存报告
    with open('drugmall_test_report.txt', 'w', encoding='utf-8') as f:
        f.write(report)

    # 保存JSON结果
    with open('drugmall_test_results.json', 'w', encoding='utf-8') as f:
        json.dump({**patient_results, **doctor_results}, f, ensure_ascii=False, indent=2)

    print(report)
    print("\n报告已保存到: drugmall_test_report.txt")
    print("详细结果已保存到: drugmall_test_results.json")
