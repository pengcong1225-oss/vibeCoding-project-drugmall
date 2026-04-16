package com.drugmall.service.impl;

import com.drugmall.config.MockDataService;
import com.drugmall.dto.DrugQueryDTO;
import com.drugmall.service.DrugService;
import com.drugmall.vo.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 药品服务实现
 */
@Slf4j
@Service
public class DrugServiceImpl implements DrugService {

    @Autowired
    private MockDataService mockDataService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResultVO<DrugVO> getDrugList(DrugQueryDTO queryDTO) {
        List<DrugVO> drugs = getAllDrugs();

        // 根据分类筛选
        if (queryDTO.getCategoryId() != null) {
            drugs = drugs.stream()
                    .filter(d -> d.getCategoryId() != null && d.getCategoryId().equals(queryDTO.getCategoryId()))
                    .collect(Collectors.toList());
        }

        // 根据关键词搜索
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            String keyword = queryDTO.getKeyword().toLowerCase();
            drugs = drugs.stream()
                    .filter(d -> d.getName().toLowerCase().contains(keyword) ||
                            (d.getDisease() != null && d.getDisease().toLowerCase().contains(keyword)))
                    .collect(Collectors.toList());
        }

        // 根据是否处方药筛选
        if (queryDTO.getIsRx() != null) {
            drugs = drugs.stream()
                    .filter(d -> d.getIsRx().equals(queryDTO.getIsRx()))
                    .collect(Collectors.toList());
        }

        // 排序
        if (queryDTO.getSort() != null) {
            switch (queryDTO.getSort()) {
                case "price_asc":
                    drugs.sort((a, b) -> a.getPrice().compareTo(b.getPrice()));
                    break;
                case "price_desc":
                    drugs.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
                    break;
                case "sales":
                    drugs.sort((a, b) -> b.getSales() - a.getSales());
                    break;
                case "new":
                    drugs.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
                    break;
                default:
                    break;
            }
        }

        // 分页
        int page = queryDTO.getPage() != null ? queryDTO.getPage() : 1;
        int size = queryDTO.getSize() != null ? queryDTO.getSize() : 10;
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, drugs.size());

        List<DrugVO> pageList = fromIndex < drugs.size() ? drugs.subList(fromIndex, toIndex) : new ArrayList<>();

        return PageResultVO.of(pageList, (long) drugs.size(), page, size);
    }

    @Override
    public DrugDetailVO getDrugDetail(String drugId) {
        List<DrugVO> drugs = getAllDrugs();
        DrugVO drug = drugs.stream()
                .filter(d -> d.getId().equals(drugId))
                .findFirst()
                .orElse(null);

        if (drug == null) {
            return null;
        }

        DrugDetailVO detail = new DrugDetailVO();
        detail.setDrug(drug);
        detail.setRelatedDrugs(getRelatedDrugs(drugId, 4));
        detail.setRecommendedDrugs(getRecommendedDrugs(null, 4));
        detail.setReviews(getDrugReviews(drugId, 1, 5).getList());
        detail.setFaqs(getDrugFAQs(drugId));
        return detail;
    }

    @Override
    public List<DrugCategoryVO> getCategories() {
        // ✅ 修复：直接返回完整的分类Mock数据，确保name字段不为null
        List<DrugCategoryVO> categories = new ArrayList<>();

        // 一级分类
        String[][] categoryData = {
            {"1", "感冒用药", "cold", null, "0", "治疗感冒发烧、头痛咳嗽等症状", "1"},
            {"2", "抗生素", "antibiotic", null, "1", "抗菌消炎类药物", "1"},
            {"3", "维生素补钙", "vitamin", null, "2", "维生素、矿物质补充剂", "1"},
            {"4", "消化系统", "digestive", null, "3", "肠胃消化、肝病药物", "1"},
            {"5", "心血管", "cardiovascular", null, "4", "高血压、心脏病用药", "1"},
            {"6", "皮肤外用", "skin", null, "5", "皮肤病、外伤用药", "1"},
            {"7", "妇科用药", "gynecology", null, "6", "女性专用药品", "1"},
            {"8", "儿童用药", "pediatric", null, "7", "儿童专用药品", "1"}
        };

        for (String[] cat : categoryData) {
            DrugCategoryVO vo = new DrugCategoryVO();
            vo.setId(cat[0]);
            vo.setName(cat[1]);
            vo.setIcon(cat[2]);
            vo.setImage(cat[3]);
            vo.setSort(Integer.parseInt(cat[4]));
            vo.setDescription(cat[5]);
            vo.setLevel(1);
            vo.setStatus(1);
            categories.add(vo);
        }

        return categories;
    }

    @Override
    public List<DrugVO> getHotDrugs(Integer limit) {
        List<DrugVO> drugs = getAllDrugs();
        return drugs.stream()
                .sorted((a, b) -> b.getSales() - a.getSales())
                .limit(limit != null ? limit : 8)
                .collect(Collectors.toList());
    }

    @Override
    public List<DrugVO> getNewDrugs(Integer limit) {
        List<DrugVO> drugs = getAllDrugs();
        return drugs.stream()
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .limit(limit != null ? limit : 8)
                .collect(Collectors.toList());
    }

    @Override
    public List<DrugVO> getRecommendedDrugs(String userId, Integer limit) {
        // 模拟推荐，返回热门药品
        return getHotDrugs(limit);
    }

    @Override
    public List<DrugVO> getRelatedDrugs(String drugId, Integer limit) {
        List<DrugVO> drugs = getAllDrugs();
        return drugs.stream()
                .filter(d -> !d.getId().equals(drugId))
                .limit(limit != null ? limit : 4)
                .collect(Collectors.toList());
    }

    @Override
    public PageResultVO<DrugReviewVO> getDrugReviews(String drugId, Integer page, Integer size) {
        JsonNode reviewsData = mockDataService.getReviews();
        List<DrugReviewVO> reviews = new ArrayList<>();

        if (reviewsData != null && reviewsData.isArray()) {
            for (JsonNode review : reviewsData) {
                if (review.get("drugId").asText().equals(drugId)) {
                    reviews.add(convertToDrugReviewVO(review));
                }
            }
        }

        // 如果没有找到评价数据，返回模拟数据
        if (reviews.isEmpty()) {
            DrugReviewVO review1 = new DrugReviewVO();
            review1.setId("1");
            review1.setUserId("1");
            review1.setUserName("用户1");
            review1.setDrugId(drugId);
            review1.setOrderId("ORD20241201001");
            review1.setRating(5);
            review1.setContent("药品效果很好，物流也很快，包装完好！");
            review1.setIsAnonymous(false);
            review1.setIsRecommended(true);
            review1.setHelpfulCount(10);
            review1.setCreateTime(LocalDateTime.now().minusDays(5));
            reviews.add(review1);

            DrugReviewVO review2 = new DrugReviewVO();
            review2.setId("2");
            review2.setUserId("2");
            review2.setUserName("用户2");
            review2.setDrugId(drugId);
            review2.setOrderId("ORD20241201002");
            review2.setRating(4);
            review2.setContent("效果不错，就是价格有点贵。");
            review2.setIsAnonymous(false);
            review2.setIsRecommended(true);
            review2.setHelpfulCount(5);
            review2.setCreateTime(LocalDateTime.now().minusDays(10));
            reviews.add(review2);
        }

        int p = page != null ? page : 1;
        int s = size != null ? size : 10;
        int fromIndex = (p - 1) * s;
        int toIndex = Math.min(fromIndex + s, reviews.size());

        List<DrugReviewVO> pageList = fromIndex < reviews.size() ? reviews.subList(fromIndex, toIndex) : new ArrayList<>();

        return PageResultVO.of(pageList, (long) reviews.size(), p, s);
    }

    @Override
    public List<DrugFAQVO> getDrugFAQs(String drugId) {
        JsonNode faqsData = mockDataService.getFaqs();
        List<DrugFAQVO> faqs = new ArrayList<>();

        if (faqsData != null && faqsData.isArray()) {
            for (JsonNode faq : faqsData) {
                if (faq.get("drugId").asText().equals(drugId)) {
                    DrugFAQVO vo = new DrugFAQVO();
                    vo.setId(faq.get("id").asText());
                    vo.setQuestion(faq.get("question").asText());
                    vo.setAnswer(faq.get("answer").asText());
                    vo.setSort(faq.has("sort") ? faq.get("sort").asInt() : 0);
                    faqs.add(vo);
                }
            }
        }

        // 如果没有找到FAQ数据，返回默认FAQ
        if (faqs.isEmpty()) {
            DrugFAQVO faq1 = new DrugFAQVO();
            faq1.setId("1");
            faq1.setQuestion("这个药品有什么副作用？");
            faq1.setAnswer("常见副作用包括恶心、呕吐、腹泻等，如有不适请及时就医。");
            faq1.setSort(1);
            faqs.add(faq1);

            DrugFAQVO faq2 = new DrugFAQVO();
            faq2.setId("2");
            faq2.setQuestion("孕妇可以使用吗？");
            faq2.setAnswer("孕妇慎用，请在医生指导下使用。");
            faq2.setSort(2);
            faqs.add(faq2);

            DrugFAQVO faq3 = new DrugFAQVO();
            faq3.setId("3");
            faq3.setQuestion("需要处方吗？");
            faq3.setAnswer("本品为处方药，需要凭医生处方购买。");
            faq3.setSort(3);
            faqs.add(faq3);
        }

        faqs.sort((a, b) -> a.getSort() - b.getSort());
        return faqs;
    }

    @Override
    public List<SearchSuggestionVO> getSearchSuggestions(String keyword) {
        List<SearchSuggestionVO> suggestions = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            // 从药品数据中搜索
            List<DrugVO> drugs = getAllDrugs();
            String lowerKeyword = keyword.toLowerCase();

            for (DrugVO drug : drugs) {
                if (drug.getName().toLowerCase().contains(lowerKeyword)) {
                    SearchSuggestionVO s = new SearchSuggestionVO();
                    s.setKeyword(drug.getName());
                    s.setType("drug");
                    s.setCount(drug.getSales());
                    suggestions.add(s);

                    if (suggestions.size() >= 5) {
                        break;
                    }
                }
            }

            // 添加通用建议
            if (suggestions.size() < 5) {
                SearchSuggestionVO s1 = new SearchSuggestionVO();
                s1.setKeyword(keyword + "胶囊");
                s1.setType("drug");
                s1.setCount(100);
                suggestions.add(s1);

                SearchSuggestionVO s2 = new SearchSuggestionVO();
                s2.setKeyword(keyword + "片");
                s2.setType("drug");
                s2.setCount(80);
                suggestions.add(s2);
            }
        }

        return suggestions;
    }

    @Override
    public List<HotSearchVO> getHotSearches(Integer limit) {
        // 返回热门搜索关键词
        List<HotSearchVO> hotSearches = new ArrayList<>();

        HotSearchVO hot1 = new HotSearchVO();
        hot1.setKeyword("感冒药");
        hot1.setHeat(9999);
        hot1.setIsNew(false);
        hot1.setIsHot(true);
        hotSearches.add(hot1);

        HotSearchVO hot2 = new HotSearchVO();
        hot2.setKeyword("退烧药");
        hot2.setHeat(8888);
        hot2.setIsNew(false);
        hot2.setIsHot(true);
        hotSearches.add(hot2);

        HotSearchVO hot3 = new HotSearchVO();
        hot3.setKeyword("维生素C");
        hot3.setHeat(6666);
        hot3.setIsNew(true);
        hot3.setIsHot(true);
        hotSearches.add(hot3);

        HotSearchVO hot4 = new HotSearchVO();
        hot4.setKeyword("口罩");
        hot4.setHeat(5555);
        hot4.setIsNew(false);
        hot4.setIsHot(false);
        hotSearches.add(hot4);

        HotSearchVO hot5 = new HotSearchVO();
        hot5.setKeyword("血压计");
        hot5.setHeat(4444);
        hot5.setIsNew(true);
        hot5.setIsHot(false);
        hotSearches.add(hot5);

        int max = limit != null ? limit : 10;
        return hotSearches.stream().limit(max).collect(Collectors.toList());
    }

    private List<DrugVO> getAllDrugs() {
        // 从购物车数据中提取药品信息作为模拟数据
        JsonNode cartsData = mockDataService.getCarts();
        List<DrugVO> drugs = new ArrayList<>();

        if (cartsData != null && cartsData.isArray()) {
            for (JsonNode cart : cartsData) {
                DrugVO vo = new DrugVO();
                vo.setId(cart.get("drugId").asText());
                vo.setName(cart.get("drugName").asText());
                vo.setSpecification(cart.has("specification") ? cart.get("specification").asText() : "");
                vo.setManufacturer(cart.has("manufacturer") ? cart.get("manufacturer").asText() : "");
                vo.setPrice(new BigDecimal(cart.get("price").asText()));
                vo.setOriginalPrice(cart.has("originalPrice") ? new BigDecimal(cart.get("originalPrice").asText()) : null);
                vo.setImage(cart.has("image") ? cart.get("image").asText() : "");
                vo.setImageColor(cart.has("imageColor") ? cart.get("imageColor").asText() : null);
                vo.setImageText(cart.has("imageText") ? cart.get("imageText").asText() : null);
                vo.setIsRx(cart.has("isRx") ? cart.get("isRx").asBoolean() : false);
                vo.setCategoryId(cart.has("categoryId") ? cart.get("categoryId").asText() : "1");
                vo.setCategoryName(cart.has("categoryName") ? cart.get("categoryName").asText() : "感冒药");
                vo.setStock(cart.has("stock") ? cart.get("stock").asInt() : 100);
                vo.setSales((int) (Math.random() * 1000) + 100);
                vo.setStatus(1);
                vo.setCreateTime(LocalDateTime.now().minusDays((int) (Math.random() * 365)));
                drugs.add(vo);
            }
        }

        // 从订单数据中提取更多药品
        JsonNode ordersData = mockDataService.getOrders();
        if (ordersData != null && ordersData.isArray()) {
            for (JsonNode order : ordersData) {
                if (order.has("items")) {
                    for (JsonNode item : order.get("items")) {
                        String drugId = item.get("drugId").asText();
                        // 检查是否已存在
                        boolean exists = drugs.stream().anyMatch(d -> d.getId().equals(drugId));
                        if (!exists) {
                            DrugVO vo = new DrugVO();
                            vo.setId(drugId);
                            vo.setName(item.get("drugName").asText());
                            vo.setSpecification(item.has("specification") ? item.get("specification").asText() : "");
                            vo.setManufacturer(item.has("manufacturer") ? item.get("manufacturer").asText() : "");
                            vo.setPrice(new BigDecimal(item.get("price").asText()));
                            vo.setImage(item.has("image") ? item.get("image").asText() : "");
                            vo.setImageColor(item.has("imageColor") ? item.get("imageColor").asText() : null);
                            vo.setImageText(item.has("imageText") ? item.get("imageText").asText() : null);
                            vo.setIsRx(item.has("isRx") ? item.get("isRx").asBoolean() : false);
                            vo.setCategoryId("1");
                            vo.setCategoryName("感冒药");
                            vo.setStock(100);
                            vo.setSales((int) (Math.random() * 1000) + 100);
                            vo.setStatus(1);
                            vo.setCreateTime(LocalDateTime.now().minusDays((int) (Math.random() * 365)));
                            drugs.add(vo);
                        }
                    }
                }
            }
        }

        return drugs;
    }

    private DrugReviewVO convertToDrugReviewVO(JsonNode review) {
        if (review == null) {
            return null;
        }
        DrugReviewVO vo = new DrugReviewVO();
        vo.setId(getTextValue(review, "id", ""));
        vo.setUserId(getTextValue(review, "userId", ""));
        vo.setUserName(getTextValue(review, "userName", ""));
        vo.setUserAvatar(getTextValue(review, "userAvatar", ""));
        vo.setDrugId(getTextValue(review, "drugId", ""));
        vo.setOrderId(getTextValue(review, "orderId", ""));
        vo.setRating(getIntValue(review, "rating", 5));
        vo.setContent(getTextValue(review, "content", ""));

        // 图片
        if (review.has("images") && !review.get("images").isNull()) {
            JsonNode imagesNode = review.get("images");
            List<String> images = new ArrayList<>();
            if (imagesNode.isArray()) {
                for (JsonNode img : imagesNode) {
                    if (img != null && !img.isNull()) {
                        images.add(img.asText());
                    }
                }
            }
            vo.setImages(images);
        }

        // 标签
        if (review.has("tags") && !review.get("tags").isNull()) {
            JsonNode tagsNode = review.get("tags");
            List<String> tags = new ArrayList<>();
            if (tagsNode.isArray()) {
                for (JsonNode tag : tagsNode) {
                    if (tag != null && !tag.isNull()) {
                        tags.add(tag.asText());
                    }
                }
            }
            vo.setTags(tags);
        }

        vo.setIsAnonymous(getBooleanValue(review, "isAnonymous", false));
        vo.setIsRecommended(getBooleanValue(review, "isRecommended", true));
        vo.setHelpfulCount(getIntValue(review, "helpfulCount", 0));

        if (review.has("createTime") && !review.get("createTime").isNull()) {
            try {
                vo.setCreateTime(LocalDateTime.parse(review.get("createTime").asText(), DATE_TIME_FORMATTER));
            } catch (Exception e) {
                vo.setCreateTime(LocalDateTime.now());
            }
        }

        // 商家回复
        if (review.has("reply") && !review.get("reply").isNull()) {
            JsonNode replyNode = review.get("reply");
            DrugReviewVO.ReplyVO reply = new DrugReviewVO.ReplyVO();
            reply.setContent(getTextValue(replyNode, "content", ""));
            if (replyNode.has("createTime") && !replyNode.get("createTime").isNull()) {
                try {
                    reply.setCreateTime(LocalDateTime.parse(replyNode.get("createTime").asText(), DATE_TIME_FORMATTER));
                } catch (Exception e) {
                    reply.setCreateTime(LocalDateTime.now());
                }
            }
            vo.setReply(reply);
        }

        return vo;
    }

    private String getTextValue(JsonNode node, String field, String defaultValue) {
        if (node.has(field) && !node.get(field).isNull()) {
            return node.get(field).asText();
        }
        return defaultValue;
    }

    private Integer getIntValue(JsonNode node, String field, Integer defaultValue) {
        if (node.has(field) && !node.get(field).isNull()) {
            return node.get(field).asInt();
        }
        return defaultValue;
    }

    private Boolean getBooleanValue(JsonNode node, String field, Boolean defaultValue) {
        if (node.has(field) && !node.get(field).isNull()) {
            return node.get(field).asBoolean();
        }
        return defaultValue;
    }
}
