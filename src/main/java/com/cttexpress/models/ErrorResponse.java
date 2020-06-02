package com.cttexpress.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "errorResponse")
@XmlType(propOrder={"errorId", "errorAlias", "errorMessage"})
public class ErrorResponse {
    @XmlElement(name = "errorId")
    protected String errorId;
    @XmlElement(name = "errorAlias")
    protected String errorAlias;
    @XmlElement(name = "errorMessage")
    protected String errorMessage;

    public ErrorResponse() {
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public String getErrorAlias() {
        return errorAlias;
    }

    public void setErrorAlias(String errorAlias) {
        this.errorAlias = errorAlias;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
