package com.drugmall.service.impl;

import com.drugmall.service.StoreService;
import com.drugmall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 门店/药店服务实现类（Mock数据模式）
 *
 * @author DrugMall Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class StoreServiceImpl implements StoreService {

    @Override
    public List<StoreListVO> getStoreList() {
        log.info("获取药店列表");
        return buildMockStoreList();
    }

    @Override
    public StoreDetailVO getStoreDetail(String storeId) {
        log.info("获取药店详情: storeId={}", storeId);
        List<StoreListVO> stores = buildMockStoreList();

        for (StoreListVO store : stores) {
            if (store.getId().equals(storeId)) {
                // 转换为详情VO并添加完整信息
                StoreDetailVO detail = convertToDetailVO(store);
                detail.setDrugs(getStoreDrugs(storeId));
                return detail;
            }
        }

        return null;
    }

    @Override
    public List<StoreDrugVO> getStoreDrugs(String storeId) {
        log.info("获取门店药品: storeId={}", storeId);

        switch (storeId) {
            case "1":
                return buildStore1Drugs();
            case "2":
                return buildStore2Drugs();
            case "3":
                return buildStore3Drugs();
            default:
                return new ArrayList<>();
        }
    }

    /**
     * 构建Mock药店列表
     */
    private List<StoreListVO> buildMockStoreList() {
        List<StoreListVO> stores = new ArrayList<>();

        // 1. 海王星辰健康药房(朝阳店)
        stores.add(StoreListVO.builder()
                .id("1")
                .name("海王星辰健康药房(朝阳店)")
                .logo("")
                .logoText("海王")
                .logoColor("#FFD700")
                .rating(4.8)
                .monthlySales(1200)
                .distance(0.8)
                .deliveryTime(25)
                .address("北京市朝阳区建国路88号SOHO现代城底商")
                .phone("010-85861234")
                .isOpen(true)
                .businessHours("08:00-22:00")
                .tags(Arrays.asList(
                        StoreListVO.TagItem.builder().text("医保定点").type("primary").build(),
                        StoreListVO.TagItem.builder().text("24小时").type("success").build(),
                        StoreListVO.TagItem.builder().text("连锁品牌").type("info").build()
                ))
                .products(Arrays.asList(
                        StoreListVO.SimpleDrug.builder().id("p1").name("阿莫西林").price(new BigDecimal("15.80")).bgColor("#E3F2FD").build(),
                        StoreListVO.SimpleDrug.builder().id("p2").name("布洛芬").price(new BigDecimal("12.50")).bgColor("#FFF3E0").build(),
                        StoreListVO.SimpleDrug.builder().id("p3").name("维生素C").price(new BigDecimal("8.90")).bgColor("#E8F5E9").build()
                ))
                .build());

        // 2. 老百姓大药房(海淀店)
        stores.add(StoreListVO.builder()
                .id("2")
                .name("老百姓大药房(海淀店)")
                .logo("")
                .logoText("百姓")
                .logoColor("#4CAF50")
                .rating(4.9)
                .monthlySales(2300)
                .distance(1.2)
                .deliveryTime(35)
                .address("北京市海淀区中关村大街27号中关村大厦1层")
                .phone("010-82651234")
                .isOpen(true)
                .businessHours("07:30-22:30")
                .tags(Arrays.asList(
                        StoreListVO.TagItem.builder().text("连锁品牌").type("primary").build(),
                        StoreListVO.TagItem.builder().text("满39免配送").type("warning").build(),
                        StoreListVO.TagItem.builder().text("正品保证").type("success").build()
                ))
                .products(Arrays.asList(
                        StoreListVO.SimpleDrug.builder().id("p4").name("感冒灵").price(new BigDecimal("18.50")).bgColor("#FFEBEE").build(),
                        StoreListVO.SimpleDrug.builder().id("p5").name("创可贴").price(new BigDecimal("5.90")).bgColor("#F3E5F5").build()
                ))
                .build());

        // 3. 叮当快药
        stores.add(StoreListVO.builder()
                .id("3")
                .name("叮当快药(国贸店)")
                .logo("")
                .logoText("叮当")
                .logoColor("#2196F3")
                .rating(4.7)
                .monthlySales(3500)
                .distance(0.5)
                .deliveryTime(28)
                .address("北京市朝阳区建国门外大街1号国贸商城B1层")
                .phone("400-0123-456")
                .isOpen(true)
                .businessHours("24小时营业")
                .tags(Arrays.asList(
                        StoreListVO.TagItem.builder().text("28分钟达").type("success").build(),
                        StoreListVO.TagItem.builder().text("自营").type("primary").build(),
                        StoreListVO.TagItem.builder().text("夜间配送").type("info").build()
                ))
                .products(Arrays.asList(
                        StoreListVO.SimpleDrug.builder().id("p6").name("口罩").price(new BigDecimal("9.90")).bgColor("#E1F5FE").build(),
                        StoreListVO.SimpleDrug.builder().id("p7").name("消毒液").price(new BigDecimal("25.00")).bgColor("#E0F2F1").build()
                ))
                .build());

        return stores;
    }

    /**
     * 转换为详情VO
     */
    private StoreDetailVO convertToDetailVO(StoreListVO store) {
        return StoreDetailVO.builder()
                .id(store.getId())
                .name(store.getName())
                .logo(store.getLogo())
                .logoText(store.getLogoText())
                .logoColor(store.getLogoColor())
                .rating(store.getRating())
                .monthlySales(store.getMonthlySales())
                .distance(store.getDistance())
                .deliveryTime(store.getDeliveryTime())
                .tags(store.getTags())
                .address(store.getAddress())
                .phone(store.getPhone())
                .isOpen(store.getIsOpen())
                .businessHours(store.getBusinessHours())
                .products(store.getProducts())
                .description("专业药品零售连锁企业，提供处方药、非处方药、医疗器械、保健品等全品类商品。拥有执业药师团队，提供专业的用药咨询服务。")
                .businessScope("中成药、化学药制剂、抗生素、生化药品、生物制品（除疫苗）、医疗器械、保健食品")
                .certifications(Arrays.asList(
                        "药品经营许可证",
                        "GSP认证证书",
                        "医保定点零售药店",
                        "互联网药品信息服务资格证"
                ))
                .servicePromises(Arrays.asList(
                        "正品保证 假一赔十",
                        "药师咨询 专业指导",
                        "隐私保护 安全配送",
                        "7天无理由退换"
                ))
                .totalProducts(getStoreDrugs(store.getId()).size())
                .build();
    }

    /**
     * 海王星辰在售药品
     */
    private List<StoreDrugVO> buildStore1Drugs() {
        List<StoreDrugVO> drugs = new ArrayList<>();

        drugs.add(StoreDrugVO.builder()
                .id("d001").name("阿莫西林胶囊").specification("0.25g*20粒")
                .manufacturer("珠海联邦制药股份有限公司")
                .price(new BigDecimal("15.80")).originalPrice(new BigDecimal("22.00"))
                .stock(256).isRx(true).approvalNumber("国药准字H20067454")
                .image("").imageColor("#E3F2FD").imageText("阿莫")
                .sales(520).discount(28).deliveryTime(25)
                .category("抗感染").tags(Arrays.asList("热销", "处方药"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d002").name("布洛芬缓释胶囊").specification("0.3g*12粒")
                .manufacturer("中美天津史克制药有限公司")
                .price(new BigDecimal("12.50")).originalPrice(new BigDecimal("18.00"))
                .stock(189).isRx(false).approvalNumber("国药准字H10900089")
                .image("").imageColor("#FFF3E0").imageText("布洛")
                .sales(380).discount(30).deliveryTime(25)
                .category("解热镇痛").tags(Arrays.asList("畅销", "OTC"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d003").name("维生素C泡腾片").specification("1g*10片")
                .manufacturer("拜耳医药保健有限公司")
                .price(new BigDecimal("8.90")).originalPrice(new BigDecimal("15.00"))
                .stock(320).isRx(false).approvalNumber("国药准字J20140147")
                .image("").imageColor("#E8F5E9").imageText("维C")
                .sales(650).discount(40).deliveryTime(25)
                .category("维生素").tags(Arrays.asList("促销", "增强免疫"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d004").name("连花清瘟胶囊").specification("0.35g*24粒")
                .manufacturer("石家庄以岭药业股份有限公司")
                .price(new BigDecimal("14.50")).originalPrice(new BigDecimal("24.00"))
                .stock(145).isRx(false).approvalNumber("国药准字Z20040063")
                .image("").imageColor("#FFEBEE").imageText("连花")
                .sales(890).discount(39).deliveryTime(25)
                .category("清热解毒").tags(Arrays.asList("爆款", "抗病毒"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d005").name("感冒灵颗粒").specification("10g*9袋")
                .manufacturer("华润三九医药股份有限公司")
                .price(new BigDecimal("15.90")).originalPrice(new BigDecimal("22.00"))
                .stock(278).isRx(false).approvalNumber("国药准字Z44021833")
                .image("").imageColor("#FFF8E1").imageText("感冒")
                .sales(720).discount(27).deliveryTime(25)
                .category("感冒用药").tags(Arrays.asList("家庭常备", "OTC"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d006").name("板蓝根颗粒").specification("10g*20袋")
                .manufacturer("广州白云山和记黄埔中药有限公司")
                .price(new BigDecimal("12.50")).originalPrice(new BigDecimal("18.00"))
                .stock(350).isRx(false).approvalNumber("国药准字Z44021395")
                .image("").imageColor("#E1F5FE").imageText("板蓝")
                .sales(450).discount(30).deliveryTime(25)
                .category("清热解毒").tags(Arrays.asList("预防", "OTC"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d007").name("头孢克肟分散片").specification("0.1g*6片")
                .manufacturer("广州白云山制药总厂")
                .price(new BigDecimal("28.60")).originalPrice(new BigDecimal("38.00"))
                .stock(98).isRx(true).approvalNumber("国药准字H20051478")
                .image("").imageColor("#F3E5F5").imageText("头孢")
                .sales(210).discount(24).deliveryTime(25)
                .category("抗感染").tags(Arrays.asList("处方药", "广谱抗菌"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d008").name("蒙脱石散").specification("3g*10袋")
                .manufacturer("博福-益普生(天津)制药有限公司")
                .price(new BigDecimal("18.90")).originalPrice(new BigDecimal("26.00"))
                .stock(167).isRx(false).approvalNumber("国药准字H20000690")
                .image("").imageColor("#E0F2F1").imageText("蒙脱")
                .sales(340).discount(27).deliveryTime(25)
                .category("消化系统").tags(Arrays.asList("止泻", "儿童可用"))
                .build());

        return drugs;
    }

    /**
     * 老百姓大药房在售药品
     */
    private List<StoreDrugVO> buildStore2Drugs() {
        List<StoreDrugVO> drugs = new ArrayList<>();

        drugs.add(StoreDrugVO.builder()
                .id("d009").name("999感冒灵颗粒").specification("10g*9袋")
                .manufacturer("华润三九医药股份有限公司")
                .price(new BigDecimal("18.50")).originalPrice(new BigDecimal("25.00"))
                .stock(234).isRx(false).approvalNumber("国药准字Z44021833")
                .image("").imageColor("#FFEBEE").imageText("999")
                .sales(680).discount(26).deliveryTime(35)
                .category("感冒用药").tags(Arrays.asList("知名品牌", "OTC"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d010").name("云南白药创可贴").specification("1.5cm*2.3cm*100片")
                .manufacturer("云南白药集团无锡药业有限公司")
                .price(new BigDecimal("5.90")).originalPrice(new BigDecimal("12.00"))
                .stock(567).isRx(false).approvalNumber("滇械注准20152160001")
                .image("").imageColor("#F3E5F5").imageText("创可")
                .sales(920).discount(50).deliveryTime(35)
                .category("外用药品").tags(Arrays.asList("居家必备", "特价"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d011").name("复方丹参滴丸").specification("27mg*180丸")
                .manufacturer("天士力医药集团股份有限公司")
                .price(new BigDecimal("32.80")).originalPrice(new BigDecimal("42.00"))
                .stock(123).isRx(false).approvalNumber("国药准字Z10950111")
                .image("").imageColor("#E3F2FD").imageText("丹参")
                .sales(290).discount(21).deliveryTime(35)
                .category("心脑血管").tags(Arrays.asList("慢病用药", "中成药"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d012").name("氯雷他定片").specification("10mg*6片")
                .manufacturer("西安杨森制药有限公司")
                .price(new BigDecimal("16.80")).originalPrice(new BigDecimal("24.00"))
                .stock(198).isRx(false).approvalNumber("国药准字H20070030")
                .image("").imageColor("#FFF3E0").imageText("氯雷")
                .sales(420).discount(30).deliveryTime(35)
                .category("抗过敏").tags(Arrays.asList("过敏季必备", "OTC"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d013").name("奥美拉唑肠溶胶囊").specification("20mg*14粒")
                .manufacturer("阿斯利康制药有限公司")
                .price(new BigDecimal("45.00")).originalPrice(new BigDecimal("58.00"))
                .stock(87).isRx(false).approvalNumber("国药准字H20046479")
                .image("").imageColor("#E8F5E9").imageText("奥美")
                .sales(310).discount(22).deliveryTime(35)
                .category("消化系统").tags(Arrays.asList("胃病常备", "原研药"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d014").name("二甲双胍缓释片").specification("0.5g*30片")
                .manufacturer("中美上海施贵宝制药有限公司")
                .price(new BigDecimal("38.50")).originalPrice(new BigDecimal("52.00"))
                .stock(156).isRx(true).approvalNumber("国药准字H20023370")
                .image("").imageColor("#FFEBEE").imageText("双胍")
                .sales(280).discount(26).deliveryTime(35)
                .category("糖尿病").tags(Arrays.asList("处方药", "降糖首选"))
                .build());

        return drugs;
    }

    /**
     * 叮当快药在售药品
     */
    private List<StoreDrugVO> buildStore3Drugs() {
        List<StoreDrugVO> drugs = new ArrayList<>();

        drugs.add(StoreDrugVO.builder()
                .id("d015").name("医用外科口罩").specification("50只/盒")
                .manufacturer("稳健医疗用品股份有限公司")
                .price(new BigDecimal("9.90")).originalPrice(new BigDecimal("19.90"))
                .stock(999).isRx(false).approvalNumber("粤械注准20202140129")
                .image("").imageColor("#E1F5FE").imageText("口罩")
                .sales(1580).discount(50).deliveryTime(28)
                .category("防护用品").tags(Arrays.asList("防疫必备", "特价"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d016").name("75%酒精消毒液").specification("500ml/瓶")
                .manufacturer("利尔康医疗科技股份有限公司")
                .price(new BigDecimal("25.00")).originalPrice(new BigDecimal("35.00"))
                .stock(456).isRx(false).approvalNumber("鲁卫消证字(2020)第0001号")
                .image("").imageColor("#E0F2F1").imageText("酒精")
                .sales(890).discount(28).deliveryTime(28)
                .category("消毒用品").tags(Arrays.asList("杀菌消毒", "家庭必备"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d017").name("体温计电子").specification("1支装")
                .manufacturer("欧姆龙(大连)有限公司")
                .price(new BigDecimal("59.00")).originalPrice(new BigDecimal("89.00"))
                .stock(234).isRx(false).approvalNumber("辽械注准20202070198")
                .image("").imageColor("#FFF8E1").imageText("体温")
                .sales(560).discount(33).deliveryTime(28)
                .category("医疗器械").tags(Arrays.asList("精准测温", "家用"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d018").name("碘伏消毒棉签").specification("50支/盒")
                .manufacturer("江苏鱼跃医疗设备股份有限公司")
                .price(new BigDecimal("15.80")).originalPrice(new BigDecimal("25.00"))
                .stock(378).isRx(false).approvalNumber("苏械注准20182140452")
                .image("").imageColor("#FFEBEE").imageText("碘伏")
                .sales(430).discount(36).deliveryTime(28)
                .category("护理用品").tags(Arrays.asList("便携", "伤口处理"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d019").name("布洛芬混悬液").specification("100ml:2g")
                . manufacturer("上海强生制药有限公司")
                .price(new BigDecimal("22.80")).originalPrice(new BigDecimal("32.00"))
                .stock(167).isRx(false).approvalNumber("国药准字H19991011")
                .image("").imageColor("#F3E5F5").imageText("美林")
                .sales(380).discount(28).deliveryTime(28)
                .category("解热镇痛").tags(Arrays.asList("儿童适用", "退烧必备"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d020").name("盐酸氨溴索口服溶液").specification("100ml:0.6g")
                .manufacturer("勃林格殷格翰(中国)投资有限公司")
                .price(new BigDecimal("35.60")).originalPrice(new BigDecimal("48.00"))
                .stock(145).isRx(false).approvalNumber("国药准字H20030360")
                .image("").imageColor("#E3F2FD").imageText("氨溴")
                .sales(270).discount(25).deliveryTime(28)
                .category("呼吸系统").tags(Arrays.asList("化痰止咳", "原研药"))
                .build());

        drugs.add(StoreDrugVO.builder()
                .id("d021").name("开塞露").specification("20ml*20支")
                .manufacturer("金耀药业(湖北)有限公司")
                .price(new BigDecimal("12.90")).originalPrice(new BigDecimal("18.00"))
                .stock(289).isRx(false).approvalNumber("国药准字H12021336")
                .image("").imageColor("#E8F5E9").imageText("开塞")
                .sales(520).discount(28).deliveryTime(28)
                .category("消化系统").tags(Arrays.asList("通便", "成人儿童均可用"))
                .build());

        return drugs;
    }
}
