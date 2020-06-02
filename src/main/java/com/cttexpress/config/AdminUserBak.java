package com.cttexpress.config;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

public class AdminUserBak {
    private static String USER_NAME;
    private static String USER_PASSWORD;
    private static String AUTH_BASIC;

    static {
        USER_NAME = "admin";
        USER_PASSWORD = "changeit";
    }

    public static boolean isAdmin(String basicAuthString) {
        String basicAuthStringDecoded = null;
        try {
            basicAuthStringDecoded = new String(Base64.decodeBase64(basicAuthString), "utf-8");
        } catch (UnsupportedEncodingException e) {
            basicAuthStringDecoded = new String(Base64.decodeBase64(basicAuthString));
        }
        String[] userAndPassword = basicAuthStringDecoded.split(":");
        if (userAndPassword.length != 2) return false;
        return userAndPassword[0].equalsIgnoreCase(USER_NAME) && userAndPassword[1].equalsIgnoreCase(USER_PASSWORD);
    }
}
