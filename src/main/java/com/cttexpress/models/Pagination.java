package com.cttexpress.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "pagination")
@XmlType(propOrder={"pageLimit", "pageOffsets"})
public class Pagination {
    @XmlElement(name = "pageLimit")
    protected int pageLimit;
    @XmlElement(name = "pageOffsets")
    protected PageOffsets pageOffsets;

    public Pagination() {
    }

    public int getPageLimit() {
        return pageLimit;
    }

    public void setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
    }

    public PageOffsets getPageOffsets() {
        return pageOffsets;
    }

    public void setPageOffsets(PageOffsets pageOffsets) {
        this.pageOffsets = pageOffsets;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
