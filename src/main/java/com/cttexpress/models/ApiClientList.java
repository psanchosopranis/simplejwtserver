package com.cttexpress.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "apiClientList")
@XmlType(propOrder={"data", "pagination"})
public class ApiClientList {
    @XmlElement(name = "data")
    protected ApiClient[] data;
    @XmlElement(name = "pagination")
    protected Pagination pagination;

    public ApiClientList() {
    }

    public ApiClient[] getData() {
        return data;
    }

    public void setData(ApiClient[] data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
