#!/usr/bin/env python3
"""
DrugMall 患者端 Frontend 全面测试脚本
测试所有页面和功能，记录发现的错误
"""

from playwright.sync_api import sync_playwright, Page, Browser, ConsoleMessage
import json
from datetime import datetime

# 配置
BASE_URL = "http://localhost:3003"
TEST_RESULTS = []


def log_error(route: str, error_type: str, description: str, suggestion: str = ""):
    """记录错误"""
    error = {
        "route": route,
        "error_type": error_type,
        "description": description,
        "suggestion": suggestion,
        "timestamp": datetime.now().isoformat()
    }
    TEST_RESULTS.append(error)
    print(f"\n[错误] {route}")
    print(f"  类型: {error_type}")
    print(f"  描述: {description}")
    if suggestion:
        print(f"  建议: {suggestion}")


def capture_console_logs(page: Page):
    """捕获控制台日志"""
    logs = []
    def handle_console(msg: ConsoleMessage):
        logs.append(f"[{msg.type}] {msg.text}")
    page.on("console", handle_console)
    return logs


def test_page_load(page: Page, route: str, name: str):
    """测试页面是否能正常加载"""
    try:
        page.goto(f"{BASE_URL}{route}", wait_until="networkidle", timeout=15000)
        page.wait_for_load_state("networkidle", timeout=10000)

        # 检查是否有 Vue 错误
        error_count = page.locator("text=/error|Error|ERROR/i").count()

        # 截图保存
        screenshot_path = f"d:/aiProject/workspace-opc/DrugMall/test_screenshots/{name.replace('/', '_')}.png"
        page.screenshot(path=screenshot_path, full_page=True)

        return True, error_count
    except Exception as e:
        return False, str(e)


def test_home_page(page: Page):
    """测试首页"""
    print("\n========== 测试首页 (/home) ==========")
    route = "/home"

    success, result = test_page_load(page, route, "home")
    if not success:
        log_error(route, "页面加载错误", f"页面无法加载: {result}")
        return

    try:
        # 检查关键元素
        # 1. 检查搜索框
        search_input = page.locator("input[placeholder*='搜索'], .search-input, [class*='search']").first
        if search_input.count() == 0:
            log_error(route, "功能缺失", "未找到搜索框", "检查搜索组件是否正确渲染")

        # 2. 检查轮播图/ Banner
        banner = page.locator(".banner, .swiper, .carousel, [class*='banner']").first
        if banner.count() == 0:
            log_error(route, "功能缺失", "未找到轮播图/Banner区域", "检查首页Banner组件")

        # 3. 检查分类导航
        category_nav = page.locator(".category, [class*='category'], .nav").first
        if category_nav.count() == 0:
            log_error(route, "功能缺失", "未找到分类导航", "检查分类导航组件")

        # 4. 检查药品列表
        drug_list = page.locator(".drug-list, [class*='drug'], [class*='product'], .goods-list").first
        if drug_list.count() == 0:
            log_error(route, "功能缺失", "未找到药品展示列表", "检查药品列表组件是否正确加载数据")

        # 5. 检查底部导航
        tabbar = page.locator(".tabbar, [class*='tabbar'], .bottom-nav").first
        if tabbar.count() == 0:
            log_error(route, "功能缺失", "未找到底部导航栏", "检查Tabbar组件")

        # 6. 检查是否有错误提示
        error_msg = page.locator(".el-message--error, .error-message, [class*='error']").first
        if error_msg.count() > 0 and error_msg.is_visible():
            text = error_msg.text_content()
            log_error(route, "运行时错误", f"页面显示错误信息: {text}")

        print("首页测试完成")

    except Exception as e:
        log_error(route, "测试执行错误", f"测试过程中发生异常: {str(e)}")


def test_category_page(page: Page):
    """测试分类页"""
    print("\n========== 测试分类页 (/category) ==========")
    route = "/category"

    success, result = test_page_load(page, route, "category")
    if not success:
        log_error(route, "页面加载错误", f"页面无法加载: {result}")
        return

    try:
        # 1. 检查分类列表
        category_list = page.locator(".category-list, [class*='category-list'], .sidebar").first
        if category_list.count() == 0:
            log_error(route, "功能缺失", "未找到分类列表", "检查分类列表组件")

        # 2. 检查子分类
        sub_category = page.locator(".sub-category, [class*='sub']").first
        if sub_category.count() == 0:
            log_error(route, "功能缺失", "未找到子分类区域", "检查子分类组件")

        # 3. 检查筛选功能
        filter_btn = page.locator(".filter, [class*='filter']").first

        print("分类页测试完成")

    except Exception as e:
        log_error(route, "测试执行错误", f"测试过程中发生异常: {str(e)}")


def test_drug_detail_page(page: Page):
    """测试药品详情页"""
    print("\n========== 测试药品详情页 (/drug/:id) ==========")
    route = "/drug/1"

    success, result = test_page_load(page, route, "drug_detail")
    if not success:
        log_error(route, "页面加载错误", f"页面无法加载: {result}")
        return

    try:
        # 1. 检查药品基本信息
        drug_name = page.locator(".drug-name, [class*='name'], h1").first
        if drug_name.count() == 0:
            log_error(route, "功能缺失", "未找到药品名称", "检查药品信息展示")

        # 2. 检查价格显示
        price = page.locator(".price, [class*='price']").first
        if price.count() == 0:
            log_error(route, "功能缺失", "未找到价格显示", "检查价格组件")

        # 3. 检查规格选择
        spec = page.locator(".spec, [class*='spec'], .specification").first

        # 4. 检查加入购物车按钮
        add_cart_btn = page.locator("button:has-text('加入购物车'), .add-cart, [class*='cart']").first
        if add_cart_btn.count() == 0:
            log_error(route, "功能缺失", "未找到加入购物车按钮", "检查购物车按钮组件")

        # 5. 检查药品详情
        detail = page.locator(".detail, [class*='detail'], .description").first

        # 6. 检查图片展示
        images = page.locator(".drug-image, [class*='image'], .gallery").first

        print("药品详情页测试完成")

    except Exception as e:
        log_error(route, "测试执行错误", f"测试过程中发生异常: {str(e)}")


def test_cart_page(page: Page):
    """测试购物车页面"""
    print("\n========== 测试购物车 (/cart) ==========")
    route = "/cart"

    success, result = test_page_load(page, route, "cart")
    if not success:
        log_error(route, "页面加载错误", f"页面无法加载: {result}")
        return

    try:
        # 1. 检查购物车列表
        cart_list = page.locator(".cart-list, [class*='cart-list'], .cart-item").first

        # 2. 检查全选功能
        select_all = page.locator(".select-all, [class*='select-all']").first

        # 3. 检查数量修改
        quantity = page.locator(".quantity, [class*='quantity'], .num-input").first

        # 4. 检查删除按钮
        delete_btn = page.locator(".delete, [class*='delete']").first

        # 5. 检查结算按钮
        checkout_btn = page.locator("button:has-text('结算'), .checkout, [class*='checkout']").first

        # 6. 检查总价显示
        total_price = page.locator(".total, [class*='total']").first

        print("购物车页测试完成")

    except Exception as e:
        log_error(route, "测试执行错误", f"测试过程中发生异常: {str(e)}")


def test_order_confirm_page(page: Page):
    """测试订单确认页"""
    print("\n========== 测试订单确认页 (/order/confirm) ==========")
    route = "/order/confirm"

    success, result = test_page_load(page, route, "order_confirm")
    if not success:
        log_error(route, "页面加载错误", f"页面无法加载: {result}")
        return

    try:
        # 1. 检查地址选择
        address = page.locator(".address, [class*='address']").first

        # 2. 检查商品列表
        goods_list = page.locator(".goods-list, [class*='goods']").first

        # 3. 检查优惠券
        coupon = page.locator(".coupon, [class*='coupon']").first

        # 4. 检查配送方式
        delivery = page.locator(".delivery, [class*='delivery']").first

        # 5. 检查提交订单按钮
        submit_btn = page.locator("button:has-text('提交订单'), .submit, [class*='submit']").first

        print("订单确认页测试完成")

    except Exception as e:
        log_error(route, "测试执行错误", f"测试过程中发生异常: {str(e)}")


def test_order_list_page(page: Page):
    """测试订单列表页"""
    print("\n========== 测试订单列表 (/order/list) ==========")
    route = "/order/list"

    success, result = test_page_load(page, route, "order_list")
    if not success:
        log_error(route, "页面加载错误", f"页面无法加载: {result}")
        return

    try:
        # 1. 检查订单列表
        order_list = page.locator(".order-list, [class*='order-list'], .order-item").first

        # 2. 检查订单状态标签
        status_tabs = page.locator(".tabs, [class*='tabs'], .status-tab").first

        # 3. 检查订单操作按钮
        action_btns = page.locator(".action, [class*='action']").first

        print("订单列表页测试完成")

    except Exception as e:
        log_error(route, "测试执行错误", f"测试过程中发生异常: {str(e)}")


def test_user_page(page: Page):
    """测试用户中心页"""
    print("\n========== 测试用户中心 (/user) ==========")
    route = "/user"

    success, result = test_page_load(page, route, "user")
    if not success:
        log_error(route, "页面加载错误", f"页面无法加载: {result}")
        return

    try:
        # 1. 检查用户信息
        user_info = page.locator(".user-info, [class*='user-info'], .avatar").first

        # 2. 检查菜单列表
        menu_list = page.locator(".menu, [class*='menu'], .menu-item").first
        if menu_list.count() == 0:
            log_error(route, "功能缺失", "未找到用户菜单", "检查用户中心菜单组件")

        # 3. 检查订单入口
        order_entry = page.locator("text=/我的订单|订单/i").first

        # 4. 检查地址管理入口
        address_entry = page.locator("text=/地址|收货地址/i").first

        print("用户中心页测试完成")

    except Exception as e:
        log_error(route, "测试执行错误", f"测试过程中发生异常: {str(e)}")


def test_store_detail_page(page: Page):
    """测试门店详情页"""
    print("\n========== 测试门店详情 (/store/:id) ==========")
    route = "/store/1"

    success, result = test_page_load(page, route, "store_detail")
    if not success:
        log_error(route, "页面加载错误", f"页面无法加载: {result}")
        return

    try:
        # 1. 检查门店信息
        store_info = page.locator(".store-info, [class*='store']").first

        # 2. 检查商品列表
        goods_list = page.locator(".goods-list, [class*='goods']").first

        print("门店详情页测试完成")

    except Exception as e:
        log_error(route, "测试执行错误", f"测试过程中发生异常: {str(e)}")


def test_address_page(page: Page):
    """测试收货地址页"""
    print("\n========== 测试收货地址 (/address) ==========")
    route = "/address"

    success, result = test_page_load(page, route, "address")
    if not success:
        log_error(route, "页面加载错误", f"页面无法加载: {result}")
        return

    try:
        # 1. 检查地址列表
        address_list = page.locator(".address-list, [class*='address-list'], .address-item").first

        # 2. 检查添加地址按钮
        add_btn = page.locator("button:has-text('添加'), .add, [class*='add']").first

        print("收货地址页测试完成")

    except Exception as e:
        log_error(route, "测试执行错误", f"测试过程中发生异常: {str(e)}")


def test_patient_page(page: Page):
    """测试就诊人管理页"""
    print("\n========== 测试就诊人管理 (/patient) ==========")
    route = "/patient"

    success, result = test_page_load(page, route, "patient")
    if not success:
        log_error(route, "页面加载错误", f"页面无法加载: {result}")
        return

    try:
        # 1. 检查就诊人列表
        patient_list = page.locator(".patient-list, [class*='patient']").first

        # 2. 检查添加就诊人按钮
        add_btn = page.locator("button:has-text('添加'), .add").first

        print("就诊人管理页测试完成")

    except Exception as e:
        log_error(route, "测试执行错误", f"测试过程中发生异常: {str(e)}")


def test_prescription_page(page: Page):
    """测试处方列表页"""
    print("\n========== 测试处方列表 (/prescription) ==========")
    route = "/prescription"

    success, result = test_page_load(page, route, "prescription")
    if not success:
        log_error(route, "页面加载错误", f"页面无法加载: {result}")
        return

    try:
        # 1. 检查处方列表
        prescription_list = page.locator(".prescription-list, [class*='prescription']").first

        # 2. 检查处方状态
        status = page.locator(".status, [class*='status']").first

        print("处方列表页测试完成")

    except Exception as e:
        log_error(route, "测试执行错误", f"测试过程中发生异常: {str(e)}")


def test_inquiry_page(page: Page):
    """测试在线问诊页"""
    print("\n========== 测试在线问诊 (/inquiry) ==========")
    route = "/inquiry"

    success, result = test_page_load(page, route, "inquiry")
    if not success:
        log_error(route, "页面加载错误", f"页面无法加载: {result}")
        return

    try:
        # 1. 检查医生列表
        doctor_list = page.locator(".doctor-list, [class*='doctor'], .doctor-item").first

        # 2. 检查筛选条件
        filter = page.locator(".filter, [class*='filter']").first

        # 3. 检查搜索功能
        search = page.locator("input[placeholder*='搜索'], .search").first

        print("在线问诊页测试完成")

    except Exception as e:
        log_error(route, "测试执行错误", f"测试过程中发生异常: {str(e)}")


def test_search_page(page: Page):
    """测试搜索页"""
    print("\n========== 测试搜索页 (/search) ==========")
    route = "/search"

    success, result = test_page_load(page, route, "search")
    if not success:
        log_error(route, "页面加载错误", f"页面无法加载: {result}")
        return

    try:
        # 1. 检查搜索框
        search_input = page.locator("input[type='search'], .search-input").first
        if search_input.count() == 0:
            log_error(route, "功能缺失", "未找到搜索输入框", "检查搜索组件")

        # 2. 检查搜索历史
        history = page.locator(".history, [class*='history']").first

        # 3. 检查热门搜索
        hot_search = page.locator(".hot, [class*='hot']").first

        print("搜索页测试完成")

    except Exception as e:
        log_error(route, "测试执行错误", f"测试过程中发生异常: {str(e)}")


def test_login_page(page: Page):
    """测试登录页"""
    print("\n========== 测试登录页 (/login) ==========")
    route = "/login"

    success, result = test_page_load(page, route, "login")
    if not success:
        log_error(route, "页面加载错误", f"页面无法加载: {result}")
        return

    try:
        # 1. 检查手机号输入框
        phone_input = page.locator("input[type='tel'], input[placeholder*='手机']").first
        if phone_input.count() == 0:
            log_error(route, "功能缺失", "未找到手机号输入框", "检查登录表单")

        # 2. 检查验证码输入框
        code_input = page.locator("input[placeholder*='验证码'], input[type='number']").first

        # 3. 检查登录按钮
        login_btn = page.locator("button:has-text('登录'), .login-btn").first
        if login_btn.count() == 0:
            log_error(route, "功能缺失", "未找到登录按钮", "检查登录按钮")

        print("登录页测试完成")

    except Exception as e:
        log_error(route, "测试执行错误", f"测试过程中发生异常: {str(e)}")


def test_ai_assistant_page(page: Page):
    """测试AI助手页"""
    print("\n========== 测试AI助手 (/ai-assistant) ==========")
    route = "/ai-assistant"

    success, result = test_page_load(page, route, "ai_assistant")
    if not success:
        log_error(route, "页面加载错误", f"页面无法加载: {result}")
        return

    try:
        # 1. 检查聊天界面
        chat = page.locator(".chat, [class*='chat'], .message-list").first

        # 2. 检查输入框
        input_box = page.locator("input, textarea").first

        # 3. 检查发送按钮
        send_btn = page.locator("button:has-text('发送'), .send").first

        print("AI助手页测试完成")

    except Exception as e:
        log_error(route, "测试执行错误", f"测试过程中发生异常: {str(e)}")


def main():
    """主测试函数"""
    print("=" * 60)
    print("DrugMall 患者端 Frontend 全面测试")
    print("=" * 60)
    print(f"测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"目标地址: {BASE_URL}")
    print("=" * 60)

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context(
            viewport={"width": 375, "height": 812},  # 移动端视口
            user_agent="Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)"
        )

        # 测试公开页面（无需登录）
        print("\n\n【第一阶段】测试公开页面")
        print("-" * 60)

        page = context.new_page()
        test_home_page(page)
        page.close()

        page = context.new_page()
        test_category_page(page)
        page.close()

        page = context.new_page()
        test_drug_detail_page(page)
        page.close()

        page = context.new_page()
        test_search_page(page)
        page.close()

        page = context.new_page()
        test_store_detail_page(page)
        page.close()

        page = context.new_page()
        test_login_page(page)
        page.close()

        # 测试需要登录的页面
        print("\n\n【第二阶段】测试需要登录的页面")
        print("-" * 60)

        page = context.new_page()
        test_cart_page(page)
        page.close()

        page = context.new_page()
        test_order_confirm_page(page)
        page.close()

        page = context.new_page()
        test_order_list_page(page)
        page.close()

        page = context.new_page()
        test_user_page(page)
        page.close()

        page = context.new_page()
        test_address_page(page)
        page.close()

        page = context.new_page()
        test_patient_page(page)
        page.close()

        page = context.new_page()
        test_prescription_page(page)
        page.close()

        page = context.new_page()
        test_inquiry_page(page)
        page.close()

        page = context.new_page()
        test_ai_assistant_page(page)
        page.close()

        browser.close()

    # 输出测试报告
    print("\n\n" + "=" * 60)
    print("测试报告")
    print("=" * 60)

    if len(TEST_RESULTS) == 0:
        print("\n✅ 所有页面测试通过，未发现错误！")
    else:
        print(f"\n❌ 共发现 {len(TEST_RESULTS)} 个错误:\n")

        # 按错误类型分组
        errors_by_type = {}
        for error in TEST_RESULTS:
            error_type = error["error_type"]
            if error_type not in errors_by_type:
                errors_by_type[error_type] = []
            errors_by_type[error_type].append(error)

        for error_type, errors in errors_by_type.items():
            print(f"\n【{error_type}】({len(errors)}个)")
            for i, error in enumerate(errors, 1):
                print(f"  {i}. 路由: {error['route']}")
                print(f"     描述: {error['description']}")
                if error['suggestion']:
                    print(f"     建议: {error['suggestion']}")

    # 保存详细报告到文件
    report_path = "d:/aiProject/workspace-opc/DrugMall/test_report.json"
    with open(report_path, "w", encoding="utf-8") as f:
        json.dump({
            "test_time": datetime.now().isoformat(),
            "base_url": BASE_URL,
            "total_errors": len(TEST_RESULTS),
            "errors": TEST_RESULTS
        }, f, ensure_ascii=False, indent=2)

    print(f"\n\n详细报告已保存到: {report_path}")
    print("=" * 60)


if __name__ == "__main__":
    main()
