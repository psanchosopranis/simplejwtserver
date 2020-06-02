package com.cttexpress.models;


import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;

@XmlRootElement(name = "tokenIntrospectResponse")
@XmlType(propOrder={"active","validSignature","remarks","jti","iss","sub","aud","iat","nbf","exp","scp","cid","token_type"})
public class TokenIntrospectResponse {
    @XmlElement(name ="active")
    protected boolean active;
    @XmlElement(name ="validSignature")
    protected boolean validSignature;
    @XmlElement(name ="remarks")
    protected String remarks;
    @XmlElement(name ="jti")
    protected String jti;
    @XmlElement(name ="iss")
    protected String iss;
    @XmlElement(name ="sub")
    protected String sub;
    @XmlElement(name ="aud")
    protected String aud;
    @XmlElement(name ="iat")
    protected long iat;
    @XmlElement(name ="nbf")
    protected long nbf;
    @XmlElement(name ="exp")
    protected long exp;
    @XmlElement(name ="scp")
    protected ArrayList<String> scp;
    @XmlElement(name ="cid")
    protected String cid;
    @XmlElement(name ="token_type")
    protected String tokenType;

    public TokenIntrospectResponse() {
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isValidSignature() {
        return validSignature;
    }

    public void setValidSignature(boolean validSignature) {
        this.validSignature = validSignature;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public long getIat() {
        return iat;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public long getNbf() {
        return nbf;
    }

    public void setNbf(long nbf) {
        this.nbf = nbf;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public ArrayList<String> getScp() {
        return scp;
    }

    public void setScp(ArrayList<String> scp) {
        this.scp = scp;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
