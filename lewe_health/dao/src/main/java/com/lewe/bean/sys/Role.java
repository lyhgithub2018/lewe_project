package com.lewe.bean.sys;

import java.io.Serializable;
import java.util.Date;

public class Role implements Serializable {
    private Integer id;

    private String name;

    private String menuIds;

    private Byte isDel;

    private Date createTime;

    private Date updateTime;

    private Long creatorId;

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

    public String getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(String menuIds) {
        this.menuIds = menuIds == null ? null : menuIds.trim();
    }

    public Byte getIsDel() {
        return isDel;
    }

    public void setIsDel(Byte isDel) {
        this.isDel = isDel;
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
    /**
     * 角色拥有的菜单名称
     */
    private String menuNames;

	public String getMenuNames() {
		return menuNames;
	}
	public void setMenuNames(String menuNames) {
		this.menuNames = menuNames;
	}
}