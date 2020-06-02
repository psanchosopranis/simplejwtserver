package com.cttexpress.config;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

public class AdminUser {
    private String id;
    private String password;
    private String role;

    public AdminUser() {
        this.id = "admin";
        this.password = "changeit";
        this.role = "ADMIN";
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
