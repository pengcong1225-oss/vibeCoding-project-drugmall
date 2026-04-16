package com.drugmall.common;

/**
 * AI助手常量类
 *
 * @author DrugMall Team
 */
public class AIConstants {

    /**
     * 百川API请求头 - Content-Type
     */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * 百川API请求头 - Authorization
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * 百川API请求头值前缀
     */
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * JSON内容类型
     */
    public static final String CONTENT_TYPE_JSON = "application/json";

    /**
     * 用户角色
     */
    public static final String ROLE_USER = "user";

    /**
     * 助手角色
     */
    public static final String ROLE_ASSISTANT = "assistant";

    /**
     * 系统角色
     */
    public static final String ROLE_SYSTEM = "system";

    /**
     * 流式响应数据前缀
     */
    public static final String STREAM_DATA_PREFIX = "data:";

    /**
     * 流式响应结束标记
     */
    public static final String STREAM_DONE = "[DONE]";

    /**
     * 默认对话历史最大长度
     */
    public static final int MAX_HISTORY_LENGTH = 20;

    /**
     * 响应完成原因 - 正常结束
     */
    public static final String FINISH_REASON_STOP = "stop";

    /**
     * 响应完成原因 - 长度限制
     */
    public static final String FINISH_REASON_LENGTH = "length";

    /**
     * 响应完成原因 - 内容过滤
     */
    public static final String FINISH_REASON_CONTENT_FILTER = "content_filter";
}
