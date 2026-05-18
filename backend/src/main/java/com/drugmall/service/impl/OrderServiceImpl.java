package com.drugmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.dto.*;
import com.drugmall.entity.Order;
import com.drugmall.entity.OrderItem;
import com.drugmall.entity.Refund;
import com.drugmall.entity.Logistics;
import com.drugmall.mapper.OrderMapper;
import com.drugmall.mapper.OrderItemMapper;
import com.drugmall.mapper.RefundMapper;
import com.drugmall.mapper.LogisticsMapper;
import com.drugmall.mapper.CartItemMapper;
import com.drugmall.mapper.AddressMapper;
import com.drugmall.entity.Address;
import com.drugmall.entity.CartItem;
import com.drugmall.service.OrderService;
import com.drugmall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
 *
 * 订单状态码定义:
 *   0 = 待支付 (pending)
 *   1 = 已支付/待发货 (paid)
 *   2 = 已发货/待收货 (shipped)
 *   3 = 已完成 (completed)
 *   4 = 已取消 (cancelled)
 *   5 = 退款/售后中 (refunding)
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private RefundMapper refundMapper;

    @Autowired
    private LogisticsMapper logisticsMapper;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private com.drugmall.mapper.DrugMapper drugMapper;

    @Autowired
    private AddressMapper addressMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResultVO<OrderVO> getOrderList(String userId, OrderQueryDTO queryDTO) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        
        // 根据状态筛选
        if (queryDTO.getStatus() != null && !queryDTO.getStatus().isEmpty()) {
            wrapper.eq(Order::getStatus, queryDTO.getStatus());
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(Order::getCreateTime);
        
        // 分页查询
        int page = queryDTO.getPage() != null ? queryDTO.getPage() : 1;
        int size = queryDTO.getSize() != null ? queryDTO.getSize() : 10;
        Page<Order> orderPage = new Page<>(page, size);
        Page<Order> resultPage = orderMapper.selectPage(orderPage, wrapper);
        
        // 转换为VO
        List<OrderVO> voList = resultPage.getRecords().stream()
                .map(this::convertToOrderVO)
                .collect(Collectors.toList());
        
        return PageResultVO.of(voList, resultPage.getTotal(), page, size);
    }

    @Override
    public OrderVO createOrder(String userId, CreateOrderDTO createDTO) {
        // 支持购物车下单和直接购买两种模式
        boolean isDirectBuy = createDTO.getCartItemIds() == null || createDTO.getCartItemIds().isEmpty();
        log.info("创建订单: userId={}, addressId={}, mode={}", userId, createDTO.getAddressId(),
                isDirectBuy ? "直接购买(drugId=" + createDTO.getDrugId() + ")" : "购物车下单");

        // 生成订单号
        String orderNo = "ORD" + System.currentTimeMillis();

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(Long.parseLong(userId));
        order.setStatus(0);  // 0=pending待支付
        order.setTotalAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setFreightAmount(new BigDecimal("5"));
        order.setPayAmount(new BigDecimal("5"));
        order.setAddressId(Long.parseLong(createDTO.getAddressId()));
        order.setRemark(createDTO.getRemark());
        order.setCreateTime(LocalDateTime.now());

        orderMapper.insert(order);

        // 创建订单项
        if (isDirectBuy && createDTO.getDrugId() != null) {
            // 直接购买模式
            com.drugmall.entity.Drug drug = drugMapper.selectById(Long.parseLong(createDTO.getDrugId()));
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(Long.parseLong(createDTO.getDrugId()));
            item.setProductName(drug != null ? drug.getProductName() : "未知药品");
            item.setProductImage(drug != null ? drug.getMainImage() : "");
            item.setSpecification(createDTO.getSpecificationId());
            item.setPrice(drug != null ? drug.getPrice() : BigDecimal.ZERO);
            item.setQuantity(createDTO.getQuantity() != null ? createDTO.getQuantity() : 1);
            item.setTotalAmount(item.getPrice() != null && item.getQuantity() != null
                    ? item.getPrice().multiply(new BigDecimal(item.getQuantity())) : BigDecimal.ZERO);
            item.setCreateTime(LocalDateTime.now());
            orderItemMapper.insert(item);
        } else if (!isDirectBuy && createDTO.getCartItemIds() != null) {
            // 购物车下单模式：从 CartItem 获取真实 productId，从 Drug 获取名称和价格
            for (String cartItemId : createDTO.getCartItemIds()) {
                CartItem cartItem = cartItemMapper.selectById(Long.parseLong(cartItemId));
                if (cartItem == null) continue;

                com.drugmall.entity.Drug drug = drugMapper.selectById(cartItem.getProductId());
                OrderItem item = new OrderItem();
                item.setOrderId(order.getId());
                item.setProductId(cartItem.getProductId());
                item.setProductName(drug != null ? drug.getProductName() : "未知药品");
                item.setProductImage(drug != null ? drug.getMainImage() : "");
                item.setPrice(drug != null ? drug.getPrice() : BigDecimal.ZERO);
                item.setQuantity(cartItem.getQuantity() != null ? cartItem.getQuantity() : 1);
                item.setTotalAmount(item.getPrice() != null && item.getQuantity() != null
                        ? item.getPrice().multiply(new BigDecimal(item.getQuantity())) : BigDecimal.ZERO);
                item.setCreateTime(LocalDateTime.now());
                orderItemMapper.insert(item);
            }
        }

        OrderVO orderVO = convertToOrderVO(order);
        orderVO.setItems(new ArrayList<>());
        return orderVO;
    }

    @Override
    public OrderVO getOrderDetail(String userId, String orderId) {
        Order order = orderMapper.selectById(Long.parseLong(orderId));
        if (order == null) {
            return null;
        }
        
        OrderVO orderVO = convertToOrderVO(order);
        
        // 加载订单项
        LambdaQueryWrapper<OrderItem> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(OrderItem::getOrderId, Long.parseLong(orderId));
        List<OrderItem> items = orderItemMapper.selectList(detailWrapper);
        orderVO.setItems(items.stream()
                .map(this::convertToOrderItemVO)
                .collect(Collectors.toList()));
        
        return orderVO;
    }

    @Override
    public void cancelOrder(String userId, String orderId) {
        Order order = orderMapper.selectById(Long.parseLong(orderId));
        if (order != null) {
            order.setStatus(4);  // 4=cancelled已取消
            order.setCancelTime(LocalDateTime.now());
            orderMapper.updateById(order);
        }
        log.info("取消订单: {}", orderId);
    }

    @Override
    public void deleteOrder(String userId, String orderId) {
        orderMapper.deleteById(Long.parseLong(orderId));
        log.info("删除订单: {}", orderId);
    }

    @Override
    public void confirmOrder(String userId, String orderId) {
        Order order = orderMapper.selectById(Long.parseLong(orderId));
        if (order != null) {
            order.setStatus(3);  // 3=completed已完成
            order.setReceiveTime(LocalDateTime.now());
            orderMapper.updateById(order);
        }
        log.info("确认收货: {}", orderId);
    }

    @Override
    public List<String> reorder(String userId, String orderId) {
        log.info("再次购买: {}", orderId);
        LambdaQueryWrapper<OrderItem> reorderWrapper = new LambdaQueryWrapper<>();
        reorderWrapper.eq(OrderItem::getOrderId, Long.parseLong(orderId));
        List<OrderItem> items = orderItemMapper.selectList(reorderWrapper);
        List<String> cartItemIds = new ArrayList<>();
        if (items != null) {
            for (OrderItem item : items) {
                cartItemIds.add(String.valueOf(item.getId()));
            }
        }
        return cartItemIds;
    }

    @Override
    public PayResultVO payOrder(String userId, PayOrderDTO payDTO) {
        log.info("支付订单: {}, 支付方式: {}", payDTO.getOrderId(), payDTO.getPayType());
        
        Order order = orderMapper.selectById(Long.parseLong(payDTO.getOrderId()));
        if (order != null) {
            order.setStatus(1);  // 1=paid已支付
            order.setPayTime(LocalDateTime.now());
            order.setPayType(Integer.parseInt(payDTO.getPayType()));
            order.setPayAmount(order.getPayAmount());  // 使用已有的payAmount
            orderMapper.updateById(order);
        }
        
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
        Order order = orderMapper.selectById(Long.parseLong(orderId));
        if (order == null) return "unknown";
        
        // 将Integer状态码转换为文本
        switch (order.getStatus()) {
            case 0: return "pending";
            case 1: return "paid";
            case 2: return "shipped";
            case 3: return "completed";
            case 4: return "cancelled";
            case 5: return "refunding";
            default: return "unknown";
        }
    }

    @Override
    public List<OrderVO.LogisticsInfoVO> getLogisticsInfo(String userId, String orderId) {
        LambdaQueryWrapper<Logistics> logisticsWrapper = new LambdaQueryWrapper<>();
        logisticsWrapper.eq(Logistics::getOrderId, Long.parseLong(orderId));
        List<Logistics> logisticsList = logisticsMapper.selectList(logisticsWrapper);
        
        if (logisticsList == null || logisticsList.isEmpty()) {
            // 返回默认物流信息
            List<OrderVO.LogisticsInfoVO> defaultLogistics = new ArrayList<>();
            
            OrderVO.LogisticsInfoVO info1 = new OrderVO.LogisticsInfoVO();
            info1.setTime(LocalDateTime.now().format(DATE_TIME_FORMATTER));
            info1.setContent("您的订单已签收，感谢您使用DrugMall");
            info1.setStatus("completed");
            defaultLogistics.add(info1);

            OrderVO.LogisticsInfoVO info2 = new OrderVO.LogisticsInfoVO();
            info2.setTime(LocalDateTime.now().minusHours(2).format(DATE_TIME_FORMATTER));
            info2.setContent("您的订单正在派送中，快递员：张师傅，电话：13800138001");
            info2.setStatus("delivering");
            defaultLogistics.add(info2);
            
            return defaultLogistics;
        }
        
        return logisticsList.stream()
                .map(l -> {
                    OrderVO.LogisticsInfoVO vo = new OrderVO.LogisticsInfoVO();
                    vo.setTime(l.getTime() != null ? l.getTime().format(DATE_TIME_FORMATTER) : "");
                    vo.setContent(l.getContent());
                    vo.setStatus(l.getStatus());
                    return vo;
                })
                .sorted((a, b) -> b.getTime().compareTo(a.getTime()))
                .collect(Collectors.toList());
    }

    @Override
    public RefundInfoVO applyRefund(String userId, RefundApplyDTO refundDTO) {
        log.info("申请退款: {}", refundDTO.getOrderId());
        
        Refund refund = new Refund();
        refund.setRefundNo("RFN" + System.currentTimeMillis());
        refund.setOrderId(Long.parseLong(refundDTO.getOrderId()));
        refund.setUserId(Long.parseLong(userId));
        refund.setStatus("pending");
        refund.setReason(refundDTO.getReason());
        refund.setDescription(refundDTO.getDescription());
        refund.setImages(refundDTO.getImages() != null ? 
                String.join(",", refundDTO.getImages()) : null);
        refund.setRefundAmount(refundDTO.getRefundAmount());
        refund.setCreateTime(LocalDateTime.now());
        
        refundMapper.insert(refund);
        return convertToRefundInfoVO(refund);
    }

    @Override
    public RefundInfoVO getRefundInfo(String userId, String orderId) {
        LambdaQueryWrapper<Refund> refundWrapper = new LambdaQueryWrapper<>();
        refundWrapper.eq(Refund::getOrderId, Long.parseLong(orderId));
        Refund refund = refundMapper.selectOne(refundWrapper);
        if (refund != null) {
            return convertToRefundInfoVO(refund);
        }
        
        // 返回默认退款信息（如果没有找到）
        RefundInfoVO defaultRefund = new RefundInfoVO();
        defaultRefund.setId("REF" + System.currentTimeMillis());
        defaultRefund.setOrderId(orderId);
        defaultRefund.setStatus("completed");
        defaultRefund.setReason("商品质量问题");
        defaultRefund.setRefundAmount(new BigDecimal("15.80"));
        defaultRefund.setActualRefundAmount(new BigDecimal("15.80"));
        defaultRefund.setCreateTime(LocalDateTime.now().minusDays(2));
        defaultRefund.setCompleteTime(LocalDateTime.now());
        return defaultRefund;
    }

    @Override
    public void cancelRefund(String userId, String orderId) {
        LambdaQueryWrapper<Refund> cancelRefundWrapper = new LambdaQueryWrapper<>();
        cancelRefundWrapper.eq(Refund::getOrderId, Long.parseLong(orderId));
        Refund refund = refundMapper.selectOne(cancelRefundWrapper);
        if (refund != null) {
            refund.setStatus("cancelled");
            refundMapper.updateById(refund);
        }
        log.info("取消退款: {}", orderId);
    }

    @Override
    public List<OrderItemVO> getPendingReviews(String userId) {
        // 查询已完成但未评价的订单项
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getReviewStatus, "pending");
        
        List<OrderItem> items = orderItemMapper.selectList(wrapper);
        
        // 过滤属于该用户的订单项（需要关联订单表）
        List<OrderItemVO> result = new ArrayList<>();
        for (OrderItem item : items) {
            Order order = orderMapper.selectById(item.getOrderId());
            if (order != null && order.getUserId().equals(Long.parseLong(userId)) && 
                order.getStatus() != null && order.getStatus() == 3) {  // 3=completed
                OrderItemVO vo = convertToOrderItemVO(item);
                vo.setOrderId(String.valueOf(item.getOrderId()));
                result.add(vo);
            }
        }
        
        return result;
    }

    @Override
    public void submitReview(String userId, SubmitReviewDTO reviewDTO) {
        log.info("提交评价: orderId={}, itemId={}", reviewDTO.getOrderId(), reviewDTO.getItemId());
        
        // 更新订单项的评价状态
        OrderItem item = orderItemMapper.selectById(Long.parseLong(reviewDTO.getItemId()));
        if (item != null) {
            item.setReviewStatus("reviewed");
            orderItemMapper.updateById(item);
        }
    }

    @Override
    public OrderStatsVO getOrderStatistics(String userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        
        List<Order> orders = orderMapper.selectList(wrapper);
        
        int totalCount = orders.size();
        int pendingPayment = 0;
        int pendingShipment = 0;
        int pendingReceipt = 0;
        int pendingReview = 0;
        int afterSale = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Order order : orders) {
            Integer status = order.getStatus();
            if (status == null) continue;
            
            switch (status) {
                case 0:  // pending
                    pendingPayment++;
                    break;
                case 1:  // paid
                    pendingShipment++;
                    break;
                case 2:  // shipped (假设)
                    pendingReceipt++;
                    break;
                case 3:  // completed
                    pendingReview++;
                    break;
                case 4:  // refunding (假设)
                    afterSale++;
                    break;
            }
            if (order.getTotalAmount() != null) {
                totalAmount = totalAmount.add(order.getTotalAmount());
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
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        
        List<Order> orders = orderMapper.selectList(wrapper);
        
        int pending = 0, paid = 0, shipped = 0, completed = 0, refunding = 0;

        for (Order order : orders) {
            Integer status = order.getStatus();
            if (status == null) continue;
            
            switch (status) {
                case 0:  // pending
                    pending++;
                    break;
                case 1:  // paid
                    paid++;
                    break;
                case 2:  // shipped
                    shipped++;
                    break;
                case 3:  // completed
                    completed++;
                    break;
                case 4:  // refunding
                    refunding++;
                    break;
            }
        }

        List<OrderStatusCountVO> counts = new ArrayList<>();
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

    private RefundInfoVO convertToRefundInfoVO(Refund refund) {
        if (refund == null) {
            return null;
        }
        RefundInfoVO vo = new RefundInfoVO();
        BeanUtils.copyProperties(refund, vo);
        // 将图片字符串转换为列表
        if (refund.getImages() != null && !refund.getImages().isEmpty()) {
            vo.setImages(List.of(refund.getImages().split(",")));
        }
        return vo;
    }

    private OrderVO convertToOrderVO(Order order) {
        if (order == null) {
            return null;
        }
        OrderVO vo = new OrderVO();
        // 手动映射（避免 BeanUtils 类型不匹配）
        vo.setId(order.getId() != null ? String.valueOf(order.getId()) : null);
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId() != null ? String.valueOf(order.getUserId()) : null);
        vo.setStatus(getStatusText(order.getStatus()));
        vo.setStatusText(getStatusText(order.getStatus()));
        vo.setTotalAmount(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
        vo.setDeliveryFee(order.getFreightAmount());
        vo.setDiscountAmount(order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO);
        vo.setPayableAmount(order.getPayAmount());
        vo.setPaidAmount(order.getPayAmount());
        vo.setAddressId(order.getAddressId() != null ? String.valueOf(order.getAddressId()) : null);
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setCancelTime(order.getCancelTime());
        vo.setConfirmTime(order.getReceiveTime());
        vo.setDeliveryTime(order.getDeliveryTime());

        // 加载订单项
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId, order.getId());
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
        if (items != null && !items.isEmpty()) {
            vo.setItems(items.stream()
                    .map(this::convertToOrderItemVO)
                    .collect(Collectors.toList()));
            int totalQty = items.stream().mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0).sum();
            vo.setTotalQuantity(totalQty);
        } else {
            vo.setItems(new ArrayList<>());
            vo.setTotalQuantity(0);
        }

        // 加载收货地址信息
        if (order.getAddressId() != null) {
            var address = addressMapper.selectById(order.getAddressId());
            if (address != null) {
                vo.setReceiverName(address.getName());
                vo.setReceiverPhone(address.getPhone());
                vo.setReceiverAddress(address.getProvince() + address.getCity()
                        + address.getDistrict() + address.getDetail());
            }
        }

        return vo;
    }

    private String getStatusText(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已取消";
            case 5: return "退款中";
            default: return "未知";
        }
    }

    private OrderItemVO convertToOrderItemVO(OrderItem item) {
        if (item == null) {
            return null;
        }
        OrderItemVO vo = new OrderItemVO();
        vo.setId(item.getId() != null ? String.valueOf(item.getId()) : null);
        vo.setOrderId(item.getOrderId() != null ? String.valueOf(item.getOrderId()) : null);
        vo.setDrugId(item.getProductId() != null ? String.valueOf(item.getProductId()) : null);
        vo.setName(item.getProductName());
        vo.setSpecification(item.getSpecification());
        vo.setImage(item.getProductImage());
        vo.setPrice(item.getPrice());
        vo.setQuantity(item.getQuantity());
        vo.setSubtotal(item.getTotalAmount());
        vo.setReviewStatus(item.getReviewStatus());
        return vo;
    }
}
