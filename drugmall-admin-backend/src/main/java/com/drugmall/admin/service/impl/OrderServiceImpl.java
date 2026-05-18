package com.drugmall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.admin.entity.Order;
import com.drugmall.admin.mapper.OrderMapper;
import com.drugmall.admin.service.OrderService;
import com.drugmall.admin.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    @Override
    public PageResult<Order> getOrderList(int pageNum, int pageSize, String keyword, Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Order::getOrderNo, keyword)
                    .or().like(Order::getRemark, keyword));
        }

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        wrapper.eq(Order::getIsDeleted, 0);
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> page = new Page<>(pageNum, pageSize);
        Page<Order> result = orderMapper.selectPage(page, wrapper);

        return PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize);
    }

    @Override
    public Order getOrderDetail(Long id) {
        return orderMapper.selectById(id);
    }
}
