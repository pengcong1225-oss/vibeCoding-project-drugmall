package com.drugmall.admin.service;

import com.drugmall.admin.entity.Order;
import com.drugmall.admin.vo.PageResult;

public interface OrderService {
    PageResult<Order> getOrderList(int pageNum, int pageSize, String keyword, Integer status);
    Order getOrderDetail(Long id);
}
