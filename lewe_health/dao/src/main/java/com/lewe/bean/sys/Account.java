package com.lewe.bean.sys;

import java.io.Serializable;
import java.util.Date;

public class Account implements Serializable {
    private Long id;

    private String name;

    private String account;

    private String password;

    private Integer roleId;

    private Integer channelId;

    private Long hospitalId;

    private Integer hospitalGroupId;

    private Integer status;

    private Byte accountType;

    private Date createTime;

    private Date updateTime;

    private Long creatorId;

    private Byte isDel;

    private String showFieldIds;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Byte getAccountType() {
        return accountType;
    }

    public void setAccountType(Byte accountType) {
        this.accountType = accountType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Byte getIsDel() {
        return isDel;
    }

    public void setIsDel(Byte isDel) {
        this.isDel = isDel;
    }

    public String getShowFieldIds() {
        return showFieldIds;
    }

    public void setShowFieldIds(String showFieldIds) {
        this.showFieldIds = showFieldIds == null ? null : showFieldIds.trim();
    }
}