package com.drugmall.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.admin.entity.User;
import com.drugmall.admin.vo.PageResult;

public interface UserService {
    PageResult<User> getUserList(int pageNum, int pageSize, String keyword, Integer status);
    User getUserDetail(Long id);
    boolean updateUserStatus(Long id, Integer status);
}
