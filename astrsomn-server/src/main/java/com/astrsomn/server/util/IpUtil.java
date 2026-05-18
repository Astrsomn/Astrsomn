package com.astrsomn.server.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {

    private static final String UNKNOWN = "unknown";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String X_REAL_IP = "X-Real-IP";
    private static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
    private static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";

    public static String getClientIp(HttpServletRequest request) {

        String ip = request.getHeader(X_FORWARDED_FOR);

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(X_REAL_IP);
        }

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(PROXY_CLIENT_IP);
        }

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(WL_PROXY_CLIENT_IP);
        }

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }


        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    public static boolean isInternalIp(String ip) {

        if (ip == null || ip.isEmpty()) {
            return false;
        }


        if ("127.0.0.1".equals(ip) || "localhost".equalsIgnoreCase(ip)) {
            return true;
        }


        if (ip.startsWith("10.")) {
            return true;
        }


        if (ip.startsWith("172.")) {
            String[] parts = ip.split("\\.");
            if (parts.length >= 2) {
                int secondOctet = Integer.parseInt(parts[1]);
                if (secondOctet >= 16 && secondOctet <= 31) {
                    return true;
                }
            }
        }


        if (ip.startsWith("192.168.")) {
            return true;
        }

        return false;
    }
}
