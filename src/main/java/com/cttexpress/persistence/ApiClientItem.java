package com.cttexpress.persistence;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class ApiClientItem {

    protected String clientId;
    protected String clientSecret;
    protected String subject;
    protected String[] scopes;
    protected String audience;
    protected String keypairId;
    protected String privkeyb4;
    protected String pubkeyb4;
    protected String sigalg;
    protected int tokenExpirationMinutes;
    protected String since;
    protected String status;

    public ApiClientItem() {
        this.clientId = "n/a";
        this.clientSecret = "n/a";
        this.subject = "n/a";
        this.scopes = new String[]{};
        this.audience = "n/a";
        this.keypairId = "n/a";
        this.privkeyb4 = "n/a";
        this.pubkeyb4 = "n/a";
        this.sigalg = "n/a";
        this.tokenExpirationMinutes = 60;
        this.since = new Date().toString();
        this.status = "LOCKED";
    }

    public ApiClientItem(
            String clientId,
            String clientSecret,
            String subject,
            String[] scopes,
            String audience,
            String keypairId,
            String privkeyb4,
            String pubkeyb4,
            String sigalg,
            int tokenExpirationMinutes,
            String since,
            String status) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.subject = subject;
        this.scopes = scopes;
        this.audience = audience;
        this.keypairId = keypairId;
        this.privkeyb4 = privkeyb4;
        this.pubkeyb4 = pubkeyb4;
        this.sigalg = sigalg;
        this.tokenExpirationMinutes = tokenExpirationMinutes;
        this.since = since;
        this.status = status;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        //this.clientSecret = Crypt.crypt(new DigestUtils(SHA_256).digestAsHex(clientSecret));
        this.clientSecret = clientSecret;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String[] getScopes() {
        return scopes;
    }

    public void setScopes(String[] scopes) {
        this.scopes = scopes;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getKeypairId() {
        return keypairId;
    }

    public void setKeypairId(String keypairId) {
        this.keypairId = keypairId;
    }

    public String getPrivkeyb4() {
        return privkeyb4;
    }

    public void setPrivkeyb4(String privkeyb4) {
        this.privkeyb4 = privkeyb4;
    }

    public String getPubkeyb4() {
        return pubkeyb4;
    }

    public void setPubkeyb4(String pubkeyb4) {
        this.pubkeyb4 = pubkeyb4;
    }

    public String getSigalg() {
        return sigalg;
    }

    public void setSigalg(String sigalg) {
        this.sigalg = sigalg;
    }

    public int getTokenExpirationMinutes() {
        return tokenExpirationMinutes;
    }

    public void setTokenExpirationMinutes(int tokenExpirationMinutes) {
        this.tokenExpirationMinutes = tokenExpirationMinutes;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
