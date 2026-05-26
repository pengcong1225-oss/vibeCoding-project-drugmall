package com.drugmall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drugmall.admin.entity.User;
import com.drugmall.admin.mapper.UserMapper;
import com.drugmall.admin.service.UserService;
import com.drugmall.admin.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public PageResult<User> getUserList(int pageNum, int pageSize, String keyword, Integer status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword)
                    .or().like(User::getRealName, keyword));
        }

        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }

        wrapper.eq(User::getIsDeleted, 0);
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> result = userMapper.selectPage(page, wrapper);

        return PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize);
    }

    @Override
    public User getUserDetail(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public Long createUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        }
        user.setIsDeleted(0);
        if (user.getStatus() == null) user.setStatus(1);
        userMapper.insert(user);
        return user.getId();
    }
    public boolean updateUser(User user) {
        return userMapper.updateById(user) > 0;
    }
    public boolean deleteUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setIsDeleted(1);
        return userMapper.updateById(user) > 0;
    }
    public boolean updateUserStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        return userMapper.updateById(user) > 0;
    }
}
