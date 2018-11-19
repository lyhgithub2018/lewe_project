package com.lewe.bean.report;

import java.io.Serializable;

public class ReportIllness implements Serializable {
    private Integer id;

    private Long reportInfoId;

    private Integer illnessId;

    private Byte illnessDegree;

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

    public Integer getIllnessId() {
        return illnessId;
    }

    public void setIllnessId(Integer illnessId) {
        this.illnessId = illnessId;
    }

    public Byte getIllnessDegree() {
        return illnessDegree;
    }

    public void setIllnessDegree(Byte illnessDegree) {
        this.illnessDegree = illnessDegree;
    }
}