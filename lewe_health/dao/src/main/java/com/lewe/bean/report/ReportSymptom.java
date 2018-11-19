package com.lewe.bean.report;

import java.io.Serializable;

public class ReportSymptom implements Serializable {
    private Integer id;

    private Long reportInfoId;

    private Integer symptomId;

    private Integer symptomDegree;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getReportInfoId() {
        return reportInfoId;
    }

    public void setReportInfoId(Long reportInfoId) {
        this.reportInfoId = reportInfoId;
    }

    public Integer getSymptomId() {
        return symptomId;
    }

    public void setSymptomId(Integer symptomId) {
        this.symptomId = symptomId;
    }

    public Integer getSymptomDegree() {
        return symptomDegree;
    }

    public void setSymptomDegree(Integer symptomDegree) {
        this.symptomDegree = symptomDegree;
    }
}