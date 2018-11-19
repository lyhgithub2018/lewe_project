package com.lewe.bean.check;

import java.io.Serializable;

public class CheckItemSubstrate implements Serializable {
    private Long id;

    private Integer checkItemId;

    private Integer substrateId;

    private String substrateName;

    private Integer substrateDosage;

    private Long creatorId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCheckItemId() {
        return checkItemId;
    }

    public void setCheckItemId(Integer checkItemId) {
        this.checkItemId = checkItemId;
    }

    public Integer getSubstrateId() {
        return substrateId;
    }

    public void setSubstrateId(Integer substrateId) {
        this.substrateId = substrateId;
    }

    public String getSubstrateName() {
        return substrateName;
    }

    public void setSubstrateName(String substrateName) {
        this.substrateName = substrateName == null ? null : substrateName.trim();
    }

    public Integer getSubstrateDosage() {
        return substrateDosage;
    }

    public void setSubstrateDosage(Integer substrateDosage) {
        this.substrateDosage = substrateDosage;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
}