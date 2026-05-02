package com.astrsomn.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;


public class CryptoUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final int KEY_SIZE = 16;

    private static SecretKeySpec secretKey;

    public static void setKey(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] keyBytes = md.digest(key.getBytes(StandardCharsets.UTF_8));
            byte[] adjustedKey = new byte[KEY_SIZE];
            System.arraycopy(keyBytes, 0, adjustedKey, 0, Math.min(keyBytes.length, KEY_SIZE));
            secretKey = new SecretKeySpec(adjustedKey, ALGORITHM);
        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException("Failed to initialize encryption key", e);
        }
    }

    public static String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return plaintext;
        }
        if (secretKey == null) {
            throw new IllegalStateException("Encryption key not initialized");
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {

            throw new RuntimeException("Encryption failed", e);
        }
    }

    public static String decrypt(String ciphertext) {
        if (ciphertext == null || ciphertext.isEmpty()) {
            return ciphertext;
        }
        if (secretKey == null) {
            throw new IllegalStateException("Encryption key not initialized");
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(ciphertext);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {

            throw new RuntimeException("Decryption failed", e);
        }
    }

    public static String generateKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String mask(String value) {
        if (value == null || value.length() <= 4) {
            return "****";
        }
        int len = value.length();
        int showLen = Math.min(4, len / 4);
        return value.substring(0, showLen) + "****" + value.substring(len - showLen);
    }
}
