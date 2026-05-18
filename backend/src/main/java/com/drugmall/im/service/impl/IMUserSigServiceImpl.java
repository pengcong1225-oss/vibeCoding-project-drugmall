package com.drugmall.im.service.impl;

import com.drugmall.im.config.TencentIMConfig;
import com.drugmall.im.service.IMUserSigService;
import com.drugmall.im.service.TencentIMRestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.Deflater;

/**
 * IM UserSig生成服务实现
 * 支持Mock模式和真实腾讯TLS签名两种模式
 * 真实模式使用腾讯云官方 TLSSigAPIv2 算法
 */
@Slf4j
@Service
public class IMUserSigServiceImpl implements IMUserSigService {

    @Autowired
    private TencentIMConfig imConfig;

    @Autowired
    private TencentIMRestService tencentIMRestService;

    private final ConcurrentHashMap<String, Boolean> importedAccounts = new ConcurrentHashMap<>();

    @Override
    public String generateUserSig(String userId, String userType) {
        String imUserId;
        if ("admin".equals(userType)) {
            imUserId = userId;
        } else {
            imUserId = generateIMUserId(userType, userId);
        }

        if (imConfig.getMockMode()) {
            return generateMockUserSig(imUserId);
        } else {
            // 管理员账号不需要导入，直接生成UserSig
            if (!"admin".equals(userType)) {
                importAccountIfNeeded(imUserId);
            }
            return generateRealUserSig(imUserId, imConfig.getExpireTime());
        }
    }

    private void importAccountIfNeeded(String imUserId) {
        if (importedAccounts.containsKey(imUserId)) {
            return;
        }
        try {
            boolean success = tencentIMRestService.accountImport(imUserId, null, null);
            if (success) {
                importedAccounts.put(imUserId, true);
                log.info("导入IM账号成功: {}", imUserId);
            } else {
                log.warn("导入IM账号失败或账号已存在: {}", imUserId);
                // 即使导入失败，也标记为已处理，避免重复尝试
                importedAccounts.put(imUserId, false);
            }
        } catch (Exception e) {
            log.warn("导入IM账号异常: {}, 错误: {}，将继续生成UserSig", imUserId, e.getMessage());
            // 标记为已处理，避免重复尝试导致持续报错
            importedAccounts.put(imUserId, false);
        }
    }

    @Override
    public String generateIMUserId(String userType, String businessId) {
        return userType.toLowerCase() + "_" + businessId;
    }

    /**
     * Mock模式：生成简化的UserSig（用于开发测试）
     */
    private String generateMockUserSig(String userId) {
        try {
            long currentTime = System.currentTimeMillis() / 1000;

            String mockData = String.format(
                "{\"TLS.identifier\":\"%s\",\"TLS.appid\":%d,\"TLS.expire\":%d,\"TLS.time\":%d,\"TLS.mock\":true}",
                userId,
                imConfig.getSdkAppId(),
                imConfig.getExpireTime(),
                currentTime
            );

            String mockSig = "mock_sig_" + userId + "_" + currentTime;
            String combined = mockData + "." + mockSig;

            return Base64.getEncoder().encodeToString(combined.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Mock模式生成UserSig失败: userId={}", userId, e);
            return "";
        }
    }

    /**
     * 真实模式：使用腾讯云TLSSigAPIv2算法生成UserSig
     * 算法步骤：
     * 1. 构造待签名字符串
     * 2. HMAC-SHA256签名
     * 3. 构建JSON文档
     * 4. zlib压缩
     * 5. Base64 URL安全编码
     */
    private String generateRealUserSig(String userId, long expire) {
        try {
            long currTime = System.currentTimeMillis() / 1000;

            // 1. 构造HMAC签名内容
            String contentToBeSigned = "TLS.identifier:" + userId + "\n"
                    + "TLS.sdkappid:" + imConfig.getSdkAppId() + "\n"
                    + "TLS.time:" + currTime + "\n"
                    + "TLS.expire:" + expire + "\n";

            // 2. HMAC-SHA256签名
            byte[] sigBytes = hmacSha256(contentToBeSigned, imConfig.getSecretKey());
            String sig = Base64.getEncoder().encodeToString(sigBytes);

            // 3. 构建JSON文档（使用手动拼接保证字段顺序一致性）
            String sigDoc = "{"
                    + "\"TLS.ver\":\"2.0\","
                    + "\"TLS.identifier\":\"" + userId + "\","
                    + "\"TLS.sdkappid\":" + imConfig.getSdkAppId() + ","
                    + "\"TLS.expire\":" + expire + ","
                    + "\"TLS.time\":" + currTime + ","
                    + "\"TLS.sig\":\"" + sig + "\""
                    + "}";

            // 4. zlib压缩
            Deflater compressor = new Deflater();
            compressor.setInput(sigDoc.getBytes(StandardCharsets.UTF_8));
            compressor.finish();
            byte[] compressedBytes = new byte[2048];
            int compressedLength = compressor.deflate(compressedBytes);
            compressor.end();

            // 5. Base64编码并转为URL安全格式
            String userSig = Base64.getEncoder().encodeToString(
                    Arrays.copyOfRange(compressedBytes, 0, compressedLength)
            );
            userSig = userSig.replace('+', '*').replace('/', '-').replace('=', '_');

            log.info("真实模式生成UserSig成功: userId={}", userId);
            return userSig;
        } catch (Exception e) {
            log.error("真实模式生成UserSig失败: userId={}", userId, e);
            throw new RuntimeException("UserSig生成失败", e);
        }
    }

    /**
     * HMAC-SHA256签名
     */
    private byte[] hmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"
        );
        mac.init(secretKeySpec);
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }
}
