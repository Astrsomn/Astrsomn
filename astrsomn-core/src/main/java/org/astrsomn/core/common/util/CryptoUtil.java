package org.astrsomn.core.common.util;

import cn.hutool.crypto.symmetric.AES;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptoUtil {

    private static AES aes;

    /**
     * 设置 AES 实例（由 CryptoConfig 调用）
     */
    public static void setAes(AES aes) {
        CryptoUtil.aes = aes;
    }

    /**
     * 加密字符串
     */
    public static String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return plaintext;
        }
        try {
            if (aes == null) {
                throw new IllegalStateException("AES not initialized");
            }
            return aes.encryptBase64(plaintext);
        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * 解密字符串
     */
    public static String decrypt(String ciphertext) {
        if (ciphertext == null || ciphertext.isEmpty()) {
            return ciphertext;
        }
        try {
            if (aes == null) {
                throw new IllegalStateException("AES not initialized");
            }
            return aes.decryptStr(ciphertext);
        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /**
     * 生成随机密钥
     */
    public static String generateKey() {
        return IdUtil.simpleUUID();
    }

    /**
     * 脱敏处理
     */
    public static String mask(String value) {
        if (value == null || value.length() <= 4) {
            return "****";
        }
        int len = value.length();
        int showLen = Math.min(4, len / 4);
        return value.substring(0, showLen) + "****" + value.substring(len - showLen);
    }
}
