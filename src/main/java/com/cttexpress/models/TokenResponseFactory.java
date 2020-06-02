package com.cttexpress.models;

public class TokenResponseFactory {

    public static TokenResponse getInstance(String tokenType, int expiresIn, String accessToken, String scopes) {

        TokenResponse instance = new TokenResponse();
        instance.tokenType = tokenType;
        instance.expiresIn = expiresIn;
        instance.accessToken = accessToken;
        instance.scopes = scopes;
        return instance;
    }
}
