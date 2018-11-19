package com.lewe.bean.hospital;

import java.io.Serializable;
import java.util.Date;

public class HospitalGroup implements Serializable {
    private Integer id;

    private String name;

    private String hospitalIds;

    private Date createTime;

    private Date updateTime;

    private Long creatorId;

    private Byte isDel;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getHospitalIds() {
        return hospitalIds;
    }

    public void setHospitalIds(String hospitalIds) {
        this.hospitalIds = hospitalIds == null ? null : hospitalIds.trim();
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
    /**
     * 后台门店组列表中，可看门店信息字段(页面显示用)
     */
    private String hospitalNames;

	public String getHospitalNames() {
		return hospitalNames;
	}

	public void setHospitalNames(String hospitalNames) {
		this.hospitalNames = hospitalNames;
	}
    
}