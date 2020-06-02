package com.cttexpress.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "apiClientRequest")
@XmlType(propOrder = {"subject", "audience", "scopes", "status", "renewClientSecret", "renewKeyPair"})
public class ApiClientRequestUpdate {
    @XmlElement(name = "subject")
    protected String subject;
    @XmlElement(name = "audience")
    protected String audience;
    @XmlElement(name = "scopes")
    protected String[] scopes;
    @XmlElement(name = "tokenExpirationMinutes")
    protected int tokenExpirationMinutes;
    @XmlElement(name = "status")
    protected String status;
    @XmlElement(name = "renewClientSecret")
    protected boolean renewClientSecret;
    @XmlElement(name = "renewKeyPair")
    protected boolean renewKeyPair;

    public ApiClientRequestUpdate() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public int getTokenExpirationMinutes() {
        return tokenExpirationMinutes;
    }

    public void setTokenExpirationMinutes(int tokenExpirationMinutes) {
        this.tokenExpirationMinutes = tokenExpirationMinutes;
    }

    public String[] getScopes() {
        return scopes;
    }

    public void setScopes(String[] scopes) {
        this.scopes = scopes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isRenewClientSecret() {
        return renewClientSecret;
    }

    public void setRenewClientSecret(boolean renewClientSecret) {
        this.renewClientSecret = renewClientSecret;
    }

    public boolean isRenewKeyPair() {
        return renewKeyPair;
    }

    public void setRenewKeyPair(boolean renewKeyPair) {
        this.renewKeyPair = renewKeyPair;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
