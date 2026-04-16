package com.drugmall.im.service;

import com.drugmall.im.config.TencentIMConfig;
import com.drugmall.im.service.impl.IMUserSigServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

/**
 * 腾讯云IM REST API调用服务
 * 封装对腾讯云IM Server端REST API的调用
 */
@Slf4j
@Service
public class TencentIMRestService {

    private static final String BASE_URL = "https://console.tim.qq.com/v4";

    @Autowired
    private TencentIMConfig imConfig;

    @Autowired
    private IMUserSigService userSigService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    /**
     * 生成管理员UserSig（用于REST API鉴权）
     */
    private String getAdminUserSig() {
        return userSigService.generateUserSig(imConfig.getAdminIdentifier(), "admin");
    }

    /**
     * 构建REST API请求URL
     */
    private String buildUrl(String serviceName, String command) {
        String adminSig = getAdminUserSig();
        return String.format(
                "%s/%s/%s?sdkappid=%d&identifier=%s&usersig=%s&random=%d&contenttype=json",
                BASE_URL, serviceName, command,
                imConfig.getSdkAppId(),
                imConfig.getAdminIdentifier(),
                adminSig,
                random.nextInt(999999999)
        );
    }

    /**
     * 发送POST请求到腾讯IM REST API
     */
    private JsonNode post(String serviceName, String command, ObjectNode body) {
        String url = buildUrl(serviceName, command);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class
            );

            JsonNode result = objectMapper.readTree(response.getBody());
            int errorCode = result.path("ErrorCode").asInt(-1);
            if (errorCode != 0) {
                log.error("腾讯IM REST API调用失败: service={}/{}, errorCode={}, errorInfo={}",
                        serviceName, command, errorCode, result.path("ErrorInfo").asText());
            }
            return result;
        } catch (Exception e) {
            log.error("腾讯IM REST API调用异常: service={}/{}", serviceName, command, e);
            throw new RuntimeException("腾讯IM API调用失败", e);
        }
    }

    /**
     * 导入单个账号
     * https://cloud.tencent.com/document/product/269/1608
     */
    public boolean accountImport(String userId, String nickname, String faceUrl) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("UserID", userId);
        if (nickname != null) body.put("Nick", nickname);
        if (faceUrl != null) body.put("FaceUrl", faceUrl);

        JsonNode result = post("im_open_login_svc", "account_import", body);
        return result.path("ErrorCode").asInt(-1) == 0;
    }

    /**
     * 发送单聊消息（管理员身份）
     * https://cloud.tencent.com/document/product/269/2282
     */
    public JsonNode sendC2CMessage(String fromUserId, String toUserId, String msgType, String content) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("SyncOtherMachine", 1);
        body.put("From_Account", fromUserId);
        body.put("To_Account", toUserId);

        // 构建MsgBody
        ArrayNode msgBody = objectMapper.createArrayNode();
        ObjectNode msgElement = objectMapper.createObjectNode();

        if ("text".equals(msgType) || "TIMTextElem".equals(msgType)) {
            msgElement.put("MsgType", "TIMTextElem");
            ObjectNode msgContent = objectMapper.createObjectNode();
            msgContent.put("Text", content);
            msgElement.set("MsgContent", msgContent);
        } else {
            // 默认当作文本处理
            msgElement.put("MsgType", "TIMTextElem");
            ObjectNode msgContent = objectMapper.createObjectNode();
            msgContent.put("Text", content);
            msgElement.set("MsgContent", msgContent);
        }

        msgBody.add(msgElement);
        body.set("MsgBody", msgBody);

        return post("openim", "sendmsg", body);
    }

    /**
     * 查询单聊漫游消息
     * https://cloud.tencent.com/document/product/269/42794
     */
    public JsonNode getRoamMsg(String fromUserId, String toUserId, int maxCnt, long minTime, long maxTime) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("From_Account", fromUserId);
        body.put("To_Account", toUserId);
        body.put("MaxCnt", maxCnt);
        body.put("MinTime", minTime);
        body.put("MaxTime", maxTime);

        return post("openim", "admin_getroammsg", body);
    }

    /**
     * 查询最近联系人
     * https://cloud.tencent.com/document/product/269/62118
     */
    public JsonNode getRecentContacts(String userId) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("From_Account", userId);

        return post("recentcontact", "get_list", body);
    }

    /**
     * 设置单聊消息已读
     * https://cloud.tencent.com/document/product/269/12498
     */
    public boolean setMsgRead(String reportAccount, String peerAccount) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("Report_Account", reportAccount);
        body.put("Peer_Account", peerAccount);

        JsonNode result = post("openim", "admin_set_msg_read", body);
        return result.path("ErrorCode").asInt(-1) == 0;
    }
}
