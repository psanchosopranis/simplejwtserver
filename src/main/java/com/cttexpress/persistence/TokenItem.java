package com.cttexpress.persistence;

import com.cttexpress.models.TokenResponse;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

public class TokenItem {
    protected String id;
    protected String tokenType;
    protected int expiresIn;
    protected String accessToken;
    protected String scopes;
    protected String clientId;
    protected String pubkeyb64;
    protected String pubkeyAsPem;
    protected String keypairId;

    private TokenItem() {
        // Not accesible
    }

    public TokenItem(
            String id,
            String tokenType,
            int expiresIn,
            String accessToken,
            String scopes,
            String clientId,
            String pubkeyb64,
            String pubkeyAsPem,
            String keypairId) {
        this.id = id;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.accessToken = accessToken;
        this.scopes = scopes;
        this.clientId = clientId;
        this.pubkeyb64 = pubkeyb64;
        this.pubkeyAsPem = pubkeyAsPem;
        this.keypairId = keypairId;
    }

    public TokenItem (TokenResponse tokenResponse,
                      String id,
                      String clientId,
                      String pubkeyb64,
                      String pubkeyAsPem,
                      String keypairId) {
        this.id = id;
        this.tokenType = tokenResponse.getTokenType();
        this.expiresIn = tokenResponse.getExpiresIn();
        this.accessToken = tokenResponse.getAccessToken();
        this.scopes = tokenResponse.getScopes();
        this.clientId = clientId;
        this.pubkeyb64 = pubkeyb64;
        this.pubkeyAsPem = pubkeyAsPem;
        this.keypairId = keypairId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPubkeyb64() {
        return pubkeyb64;
    }

    public void setPubkeyb64(String pubkeyb64) {
        this.pubkeyb64 = pubkeyb64;
    }

    public String getPubkeyAsPem() {
        return pubkeyAsPem;
    }

    public void setPubkeyAsPem(String pubkeyAsPem) {
        this.pubkeyAsPem = pubkeyAsPem;
    }

    public String getKeypairId() {
        return keypairId;
    }

    public void setKeypairId(String keypairId) {
        this.keypairId = keypairId;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
