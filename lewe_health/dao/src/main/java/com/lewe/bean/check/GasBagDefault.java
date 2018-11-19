package com.lewe.bean.check;

import java.io.Serializable;

public class GasBagDefault implements Serializable {
    private Long id;

    private Integer timeSpace;

    private Integer checkPoint;

    private Integer checkTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}