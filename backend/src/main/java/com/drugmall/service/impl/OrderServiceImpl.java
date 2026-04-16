package com.drugmall.service.impl;

import com.drugmall.config.MockDataService;
import com.drugmall.dto.*;
import com.drugmall.service.OrderService;
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
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private MockDataService mockDataService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResultVO<OrderVO> getOrderList(String userId, OrderQueryDTO queryDTO) {
        JsonNode ordersData = mockDataService.getOrders();
        List<OrderVO> orders = new ArrayList<>();

        if (ordersData != null && ordersData.isArray()) {
            for (JsonNode order : ordersData) {
                if (order.get("userId").asText().equals(userId)) {
                    OrderVO vo = convertToOrderVO(order);
                    // 根据状态筛选
                    if (queryDTO.getStatus() == null || vo.getStatus().equals(queryDTO.getStatus())) {
                        orders.add(vo);
                    }
                }
            }
        }

        // 分页
        int page = queryDTO.getPage() != null ? queryDTO.getPage() : 1;
        int size = queryDTO.getSize() != null ? queryDTO.getSize() : 10;
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, orders.size());

        List<OrderVO> pageList = fromIndex < orders.size() ? orders.subList(fromIndex, toIndex) : new ArrayList<>();

        return PageResultVO.of(pageList, (long) orders.size(), page, size);
    }

    @Override
    public OrderVO createOrder(String userId, CreateOrderDTO createDTO) {
        log.info("创建订单: userId={}, addressId={}", userId, createDTO.getAddressId());

        // 生成订单号
        String orderNo = "ORD" + System.currentTimeMillis();

        OrderVO order = new OrderVO();
        order.setId(orderNo);
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setStatus("pending");
        order.setStatusText("待支付");
        order.setTotalQuantity(0);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setDrugAmount(BigDecimal.ZERO);
        order.setDeliveryFee(new BigDecimal("5"));
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setCouponAmount(BigDecimal.ZERO);
        order.setPayableAmount(new BigDecimal("5"));
        order.setPaidAmount(BigDecimal.ZERO);
        order.setDeliveryType(createDTO.getDeliveryType());
        order.setAddressId(createDTO.getAddressId());
        order.setCreateTime(LocalDateTime.now());
        order.setExpireTime(LocalDateTime.now().plusMinutes(30));
        order.setRemark(createDTO.getRemark());
        order.setItems(new ArrayList<>());

        return order;
    }

    @Override
    public OrderVO getOrderDetail(String userId, String orderId) {
        JsonNode ordersData = mockDataService.getOrders();
        if (ordersData != null && ordersData.isArray()) {
            for (JsonNode order : ordersData) {
                if (order.get("id").asText().equals(orderId)) {
                    return convertToOrderVO(order);
                }
            }
        }
        return null;
    }

    @Override
    public void cancelOrder(String userId, String orderId) {
        log.info("取消订单: {}", orderId);
    }

    @Override
    public void deleteOrder(String userId, String orderId) {
        log.info("删除订单: {}", orderId);
    }

    @Override
    public void confirmOrder(String userId, String orderId) {
        log.info("确认收货: {}", orderId);
    }

    @Override
    public void reorder(String userId, String orderId) {
        log.info("再次购买: {}", orderId);
    }

    @Override
    public PayResultVO payOrder(String userId, PayOrderDTO payDTO) {
        log.info("支付订单: {}, 支付方式: {}", payDTO.getOrderId(), payDTO.getPayType());
        PayResultVO result = new PayResultVO();
        result.setSuccess(true);
        result.setOrderId(payDTO.getOrderId());
        result.setPayType(payDTO.getPayType());
        result.setPayTime(LocalDateTime.now());
        result.setTransactionId("TXN" + System.currentTimeMillis());
        return result;
    }

    @Override
    public String getPayStatus(String userId, String orderId) {
        OrderVO order = getOrderDetail(userId, orderId);
        return order != null ? order.getStatus() : "unknown";
    }

    @Override
    public List<OrderVO.LogisticsInfoVO> getLogisticsInfo(String userId, String orderId) {
        JsonNode logisticsData = mockDataService.getLogistics();
        List<OrderVO.LogisticsInfoVO> logistics = new ArrayList<>();

        if (logisticsData != null && logisticsData.isArray()) {
            for (JsonNode item : logisticsData) {
                if (item.get("orderId").asText().equals(orderId)) {
                    OrderVO.LogisticsInfoVO info = new OrderVO.LogisticsInfoVO();
                    info.setTime(item.has("time") ? item.get("time").asText() : "");
                    info.setContent(item.get("content").asText());
                    info.setStatus(item.has("status") ? item.get("status").asText() : "");
                    logistics.add(info);
                }
            }
        }

        // 如果没有找到物流信息，返回默认数据
        if (logistics.isEmpty()) {
            OrderVO.LogisticsInfoVO info1 = new OrderVO.LogisticsInfoVO();
            info1.setTime(LocalDateTime.now().format(DATE_TIME_FORMATTER));
            info1.setContent("您的订单已签收，感谢您使用DrugMall");
            info1.setStatus("completed");
            logistics.add(info1);

            OrderVO.LogisticsInfoVO info2 = new OrderVO.LogisticsInfoVO();
            info2.setTime(LocalDateTime.now().minusHours(2).format(DATE_TIME_FORMATTER));
            info2.setContent("您的订单正在派送中，快递员：张师傅，电话：13800138001");
            info2.setStatus("delivering");
            logistics.add(info2);
        }

        return logistics;
    }

    @Override
    public RefundInfoVO applyRefund(String userId, RefundApplyDTO refundDTO) {
        log.info("申请退款: {}", refundDTO.getOrderId());
        RefundInfoVO refund = new RefundInfoVO();
        refund.setId("REF" + System.currentTimeMillis());
        refund.setOrderId(refundDTO.getOrderId());
        refund.setStatus("pending");
        refund.setReason(refundDTO.getReason());
        refund.setDescription(refundDTO.getDescription());
        refund.setImages(refundDTO.getImages());
        refund.setRefundAmount(refundDTO.getRefundAmount());
        refund.setCreateTime(LocalDateTime.now());
        return refund;
    }

    @Override
    public RefundInfoVO getRefundInfo(String userId, String orderId) {
        JsonNode refundsData = mockDataService.getRefunds();
        if (refundsData != null && refundsData.isArray()) {
            for (JsonNode refund : refundsData) {
                if (refund.get("orderId").asText().equals(orderId)) {
                    return convertToRefundInfoVO(refund);
                }
            }
        }

        // 返回默认退款信息
        RefundInfoVO refund = new RefundInfoVO();
        refund.setId("REF" + System.currentTimeMillis());
        refund.setOrderId(orderId);
        refund.setStatus("completed");
        refund.setReason("商品质量问题");
        refund.setRefundAmount(new BigDecimal("15.80"));
        refund.setActualRefundAmount(new BigDecimal("15.80"));
        refund.setCreateTime(LocalDateTime.now().minusDays(2));
        refund.setCompleteTime(LocalDateTime.now());
        return refund;
    }

    @Override
    public void cancelRefund(String userId, String orderId) {
        log.info("取消退款: {}", orderId);
    }

    @Override
    public List<OrderItemVO> getPendingReviews(String userId) {
        List<OrderItemVO> items = new ArrayList<>();

        // 从订单中获取待评价商品
        JsonNode ordersData = mockDataService.getOrders();
        if (ordersData != null && ordersData.isArray()) {
            for (JsonNode order : ordersData) {
                if (order.get("userId").asText().equals(userId) &&
                    "completed".equals(order.get("status").asText())) {
                    if (order.has("items")) {
                        for (JsonNode item : order.get("items")) {
                            OrderItemVO vo = convertToOrderItemVO(item);
                            vo.setOrderId(order.get("id").asText());
                            vo.setReviewStatus("pending");
                            items.add(vo);
                        }
                    }
                }
            }
        }

        // 如果没有找到，返回模拟数据
        if (items.isEmpty()) {
            OrderItemVO item = new OrderItemVO();
            item.setId("1");
            item.setOrderId("ORD20241201001");
            item.setDrugId("2");
            item.setName("布洛芬缓释胶囊");
            item.setSpecification("0.3g*20粒");
            item.setManufacturer("中美史克");
            item.setImage("");
            item.setPrice(new BigDecimal("15.80"));
            item.setQuantity(1);
            item.setIsRx(false);
            item.setSubtotal(new BigDecimal("15.80"));
            item.setReviewStatus("pending");
            items.add(item);
        }

        return items;
    }

    @Override
    public void submitReview(String userId, SubmitReviewDTO reviewDTO) {
        log.info("提交评价: orderId={}, itemId={}", reviewDTO.getOrderId(), reviewDTO.getItemId());
    }

    @Override
    public OrderStatsVO getOrderStatistics(String userId) {
        JsonNode ordersData = mockDataService.getOrders();
        int totalCount = 0;
        int pendingPayment = 0;
        int pendingShipment = 0;
        int pendingReceipt = 0;
        int pendingReview = 0;
        int afterSale = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (ordersData != null && ordersData.isArray()) {
            for (JsonNode order : ordersData) {
                if (order.get("userId").asText().equals(userId)) {
                    totalCount++;
                    String status = order.get("status").asText();
                    switch (status) {
                        case "pending":
                            pendingPayment++;
                            break;
                        case "paid":
                            pendingShipment++;
                            break;
                        case "shipped":
                            pendingReceipt++;
                            break;
                        case "completed":
                            pendingReview++;
                            break;
                        case "refunding":
                            afterSale++;
                            break;
                    }
                    if (order.has("totalAmount")) {
                        totalAmount = totalAmount.add(new BigDecimal(order.get("totalAmount").asText()));
                    }
                }
            }
        }

        OrderStatsVO stats = new OrderStatsVO();
        stats.setTotalCount(totalCount);
        stats.setPendingPayment(pendingPayment);
        stats.setPendingShipment(pendingShipment);
        stats.setPendingReceipt(pendingReceipt);
        stats.setPendingReview(pendingReview);
        stats.setAfterSale(afterSale);
        stats.setTotalAmount(totalAmount);
        return stats;
    }

    @Override
    public List<OrderStatusCountVO> getOrderStatusCounts(String userId) {
        List<OrderStatusCountVO> counts = new ArrayList<>();

        JsonNode ordersData = mockDataService.getOrders();
        int pending = 0, paid = 0, shipped = 0, completed = 0, refunding = 0;

        if (ordersData != null && ordersData.isArray()) {
            for (JsonNode order : ordersData) {
                if (order.get("userId").asText().equals(userId)) {
                    String status = order.get("status").asText();
                    switch (status) {
                        case "pending":
                            pending++;
                            break;
                        case "paid":
                            paid++;
                            break;
                        case "shipped":
                            shipped++;
                            break;
                        case "completed":
                            completed++;
                            break;
                        case "refunding":
                            refunding++;
                            break;
                    }
                }
            }
        }

        counts.add(createStatusCount("pending", "待支付", pending));
        counts.add(createStatusCount("paid", "待发货", paid));
        counts.add(createStatusCount("shipped", "待收货", shipped));
        counts.add(createStatusCount("completed", "待评价", completed));
        counts.add(createStatusCount("refunding", "售后", refunding));
        return counts;
    }

    private OrderStatusCountVO createStatusCount(String status, String statusName, Integer count) {
        OrderStatusCountVO vo = new OrderStatusCountVO();
        vo.setStatus(status);
        vo.setStatusName(statusName);
        vo.setCount(count);
        return vo;
    }

    private RefundInfoVO convertToRefundInfoVO(JsonNode refund) {
        if (refund == null) {
            return null;
        }
        RefundInfoVO vo = new RefundInfoVO();
        vo.setId(getTextValue(refund, "id", ""));
        vo.setOrderId(getTextValue(refund, "orderId", ""));
        vo.setStatus(getTextValue(refund, "status", ""));
        vo.setReason(getTextValue(refund, "reason", ""));
        vo.setDescription(getTextValue(refund, "description", null));
        vo.setRefundAmount(getDecimalValue(refund, "refundAmount", BigDecimal.ZERO));
        vo.setActualRefundAmount(refund.has("actualRefundAmount") && !refund.get("actualRefundAmount").isNull() ?
                new BigDecimal(refund.get("actualRefundAmount").asText()) : null);

        if (refund.has("createTime") && !refund.get("createTime").isNull()) {
            try {
                vo.setCreateTime(LocalDateTime.parse(refund.get("createTime").asText(), DATE_TIME_FORMATTER));
            } catch (Exception e) {
                vo.setCreateTime(LocalDateTime.now());
            }
        }
        if (refund.has("completeTime") && !refund.get("completeTime").isNull()) {
            try {
                vo.setCompleteTime(LocalDateTime.parse(refund.get("completeTime").asText(), DATE_TIME_FORMATTER));
            } catch (Exception e) {
                vo.setCompleteTime(null);
            }
        }
        return vo;
    }

    private String getTextValue(JsonNode node, String field, String defaultValue) {
        if (node.has(field) && !node.get(field).isNull()) {
            return node.get(field).asText();
        }
        return defaultValue;
    }

    private BigDecimal getDecimalValue(JsonNode node, String field, BigDecimal defaultValue) {
        if (node.has(field) && !node.get(field).isNull()) {
            return new BigDecimal(node.get(field).asText());
        }
        return defaultValue;
    }

    private OrderVO convertToOrderVO(JsonNode order) {
        if (order == null) {
            return null;
        }
        OrderVO vo = new OrderVO();
        vo.setId(getTextValue(order, "id", ""));
        vo.setOrderNo(getTextValue(order, "orderNo", ""));
        vo.setUserId(getTextValue(order, "userId", ""));
        vo.setStatus(getTextValue(order, "status", ""));
        vo.setStatusText(getTextValue(order, "statusText", ""));
        vo.setTotalQuantity(getIntValue(order, "totalQuantity", 0));
        vo.setTotalAmount(getDecimalValue(order, "totalAmount", BigDecimal.ZERO));
        vo.setDrugAmount(getDecimalValue(order, "drugAmount", BigDecimal.ZERO));
        vo.setDeliveryFee(getDecimalValue(order, "deliveryFee", BigDecimal.ZERO));
        vo.setDiscountAmount(getDecimalValue(order, "discountAmount", BigDecimal.ZERO));
        vo.setCouponAmount(getDecimalValue(order, "couponAmount", BigDecimal.ZERO));
        vo.setPayableAmount(getDecimalValue(order, "payableAmount", BigDecimal.ZERO));
        vo.setPaidAmount(getDecimalValue(order, "paidAmount", BigDecimal.ZERO));
        vo.setDeliveryType(getTextValue(order, "deliveryType", ""));
        vo.setAddressId(getTextValue(order, "addressId", null));
        vo.setReceiverName(getTextValue(order, "addressName", null));
        vo.setReceiverPhone(getTextValue(order, "addressPhone", null));
        vo.setReceiverAddress(getTextValue(order, "addressFull", null));
        vo.setLogisticsNo(getTextValue(order, "logisticsNo", null));
        vo.setLogisticsCompany(getTextValue(order, "logisticsCompany", null));
        vo.setPayType(getTextValue(order, "payType", null));
        vo.setRemark(getTextValue(order, "remark", null));

        // 商品项
        if (order.has("items") && !order.get("items").isNull()) {
            List<OrderItemVO> items = new ArrayList<>();
            for (JsonNode item : order.get("items")) {
                OrderItemVO itemVO = convertToOrderItemVO(item);
                if (itemVO != null) {
                    items.add(itemVO);
                }
            }
            vo.setItems(items);
        }

        // 时间
        if (order.has("createTime") && !order.get("createTime").isNull()) {
            vo.setCreateTime(parseDateTime(order.get("createTime").asText()));
        }
        if (order.has("payTime") && !order.get("payTime").isNull()) {
            vo.setPayTime(parseDateTime(order.get("payTime").asText()));
        }
        if (order.has("deliveryTime") && !order.get("deliveryTime").isNull()) {
            vo.setDeliveryTime(parseDateTime(order.get("deliveryTime").asText()));
        }
        if (order.has("completeTime") && !order.get("completeTime").isNull()) {
            vo.setCompleteTime(parseDateTime(order.get("completeTime").asText()));
        }
        if (order.has("expireTime") && !order.get("expireTime").isNull()) {
            vo.setExpireTime(parseDateTime(order.get("expireTime").asText()));
        }

        return vo;
    }

    private OrderItemVO convertToOrderItemVO(JsonNode item) {
        if (item == null) {
            return null;
        }
        OrderItemVO vo = new OrderItemVO();
        vo.setId(getTextValue(item, "id", ""));
        vo.setDrugId(getTextValue(item, "drugId", ""));
        vo.setName(getTextValue(item, "drugName", ""));
        vo.setSpecification(getTextValue(item, "specification", ""));
        vo.setManufacturer(getTextValue(item, "manufacturer", ""));
        vo.setImage(getTextValue(item, "image", ""));
        vo.setImageColor(getTextValue(item, "imageColor", ""));
        vo.setImageText(getTextValue(item, "imageText", ""));
        vo.setPrice(getDecimalValue(item, "price", BigDecimal.ZERO));
        vo.setQuantity(getIntValue(item, "quantity", 1));
        vo.setIsRx(item.has("isRx") && !item.get("isRx").isNull() ? item.get("isRx").asBoolean() : false);
        BigDecimal price = getDecimalValue(item, "price", BigDecimal.ZERO);
        Integer quantity = getIntValue(item, "quantity", 1);
        vo.setSubtotal(price.multiply(new BigDecimal(quantity)));
        return vo;
    }

    private Integer getIntValue(JsonNode node, String field, Integer defaultValue) {
        if (node.has(field) && !node.get(field).isNull()) {
            return node.get(field).asInt();
        }
        return defaultValue;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
}
