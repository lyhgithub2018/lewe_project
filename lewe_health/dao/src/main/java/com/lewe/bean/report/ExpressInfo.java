package com.lewe.bean.report;

import java.io.Serializable;
import java.util.Date;

public class ExpressInfo implements Serializable {
    private Long id;

    private Integer channelId;

    private Long hospitalId;

    private Integer hospitalGroupId;

    private Long operatorId;

    private String expressName;

    private String expressCode;

    private Date expressTime;

    private String reportInfoids;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getHospitalGroupId() {
        return hospitalGroupId;
    }

    public void setHospitalGroupId(Integer hospitalGroupId) {
        this.hospitalGroupId = hospitalGroupId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName == null ? null : expressName.trim();
    }

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode == null ? null : expressCode.trim();
    }

    public Date getExpressTime() {
        return expressTime;
    }

    public void setExpressTime(Date expressTime) {
        this.expressTime = expressTime;
    }

    public String getReportInfoids() {
        return reportInfoids;
    }

    public void setReportInfoids(String reportInfoids) {
        this.reportInfoids = reportInfoids == null ? null : reportInfoids.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}