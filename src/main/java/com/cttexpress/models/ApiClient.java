package com.cttexpress.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "apiClient")
@XmlType(propOrder={
        "clientId",
        "clientSecret",
        "subject",
        "scopes",
        "audience",
        "keypairId",
        "privkeyb4",
        "pubkeyb4",
        "sigalg",
        "tokenExpirationMinutes",
        "since",
        "status"})
public class ApiClient {
    @XmlElement(name = "clientId")
    protected String clientId;
    @XmlElement(name = "clientSecret")
    protected String clientSecret;
    @XmlElement(name = "subject")
    protected String subject;
    @XmlElement(name = "scopes")
    protected String[] scopes;
    @XmlElement(name = "audience")
    protected String audience;
    @XmlElement(name = "keypairId")
    protected String keypairId;
    @XmlElement(name = "privkeyb4")
    protected String privkeyb4;
    @XmlElement(name = "pubkeyb4")
    protected String pubkeyb4;
    @XmlElement(name = "sigalg")
    protected String sigalg;
    @XmlElement(name = "tokenExpirationMinutes")
    protected int tokenExpirationMinutes;
    @XmlElement(name = "since")
    protected String since;
    @XmlElement(name = "status")
    protected String status;

    public ApiClient() {
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
