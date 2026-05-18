package com.drugmall.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 用户上下文工具类
 * 用于获取当前登录用户信息
 */
@Slf4j
@Component
public class UserContext {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_PHONE_HEADER = "X-User-Phone";

    /**
     * 获取当前用户ID
     * @return 用户ID，如果未登录则返回Optional.empty()
     */
    public static Optional<Long> getCurrentUserId() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return Optional.empty();
            }
            
            HttpServletRequest request = attributes.getRequest();
            String userId = request.getHeader(USER_ID_HEADER);
            
            if (userId != null && !userId.isEmpty()) {
                return Optional.of(Long.parseLong(userId));
            }
            
            return Optional.empty();
        } catch (Exception e) {
            log.warn("获取当前用户ID失败", e);
            return Optional.empty();
        }
    }

    /**
     * 获取当前用户手机号
     * @return 用户手机号，如果未登录则返回Optional.empty()
     */
    public static Optional<String> getCurrentUserPhone() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return Optional.empty();
            }
            
            HttpServletRequest request = attributes.getRequest();
            String phone = request.getHeader(USER_PHONE_HEADER);
            
            if (phone != null && !phone.isEmpty()) {
                return Optional.of(phone);
            }
            
            return Optional.empty();
        } catch (Exception e) {
            log.warn("获取当前用户手机号失败", e);
            return Optional.empty();
        }
    }

    /**
     * 获取当前用户ID字符串格式（用于兼容现有代码）
     * @return 用户ID字符串，格式为 "USER{id}"
     */
    public static String getCurrentUserIdString() {
        return getCurrentUserId()
            .map(id -> "USER" + id)
            .orElse("USER001"); // 默认值，用于开发环境
    }

    /**
     * 检查用户是否已登录
     * @return true-已登录，false-未登录
     */
    public static boolean isLoggedIn() {
        return getCurrentUserId().isPresent();
    }
}
