package com.cttexpress.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "pubkeyResponse")
@XmlType(propOrder={"keyId","sigalg","pubkeyb4","pubkeyAsPem"})
public class PubkeyResponse {
    @XmlElement(name ="keyId")
    protected String keyId;
    @XmlElement(name = "sigalg")
    protected String sigalg;
    @XmlElement(name = "pubkeyb4")
    protected String pubkeyb4;
    @XmlElement(name = "pubkeyAsPem")
    protected String pubkeyAsPem;

    public PubkeyResponse() {
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getSigalg() {
        return sigalg;
    }

    public void setSigalg(String sigalg) {
        this.sigalg = sigalg;
    }

    public String getPubkeyb4() {
        return pubkeyb4;
    }

    public void setPubkeyb4(String pubkeyb4) {
        this.pubkeyb4 = pubkeyb4;
    }

    public String getPubkeyAsPem() {
        return pubkeyAsPem;
    }

    public void setPubkeyAsPem(String pubkeyAsPem) {
        this.pubkeyAsPem = pubkeyAsPem;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
