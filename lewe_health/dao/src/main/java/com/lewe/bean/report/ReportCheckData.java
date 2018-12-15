package com.lewe.bean.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ReportCheckData implements Serializable {
    private Long id;

    private Long reportInfoId;

    private Integer timeSpace;

    private Integer checkPoint;

    private Integer checkTime;

    private BigDecimal h2Concentration;

    private BigDecimal ch4Concentration;

    private BigDecimal co2Concentration;

    private Date createTime;

    private Long creatorId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportInfoId() {
        return reportInfoId;
    }

    public void setReportInfoId(Long reportInfoId) {
        this.reportInfoId = reportInfoId;
    }

    public Integer getTimeSpace() {
		return timeSpace;
	}

	public void setTimeSpace(Integer timeSpace) {
		this.timeSpace = timeSpace;
	}

	public Integer getCheckPoint() {
        return checkPoint;
    }

    public void setCheckPoint(Integer checkPoint) {
        this.checkPoint = checkPoint;
    }

    public Integer getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Integer checkTime) {
        this.checkTime = checkTime;
    }

    public BigDecimal getH2Concentration() {
        return h2Concentration;
    }

    public void setH2Concentration(BigDecimal h2Concentration) {
        this.h2Concentration = h2Concentration;
    }

    public BigDecimal getCh4Concentration() {
        return ch4Concentration;
    }

    public void setCh4Concentration(BigDecimal ch4Concentration) {
        this.ch4Concentration = ch4Concentration;
    }

    public BigDecimal getCo2Concentration() {
        return co2Concentration;
    }

    public void setCo2Concentration(BigDecimal co2Concentration) {
        this.co2Concentration = co2Concentration;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
}