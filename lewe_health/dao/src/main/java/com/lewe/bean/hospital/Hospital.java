package com.lewe.bean.hospital;

import java.io.Serializable;
import java.util.Date;

public class Hospital implements Serializable {
    private Long id;

    private String hospitalName;

    private String hospitalCode;

    private Integer channelId;

    private Byte isHospital;

    private String checkItemIds;

    private String provinceCode;

    private String cityCode;

    private String countyCode;

    private String areaCodeName;

    private String detailAddress;

    private String logoUrl;

    private Byte reportNeedAduit;

    private Date createTime;

    private Date updateTime;

    private Long creatorId;

    private Byte isDel;

    private String showFieldIds;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }
 
    /**
     * @return the showFieldIds
     */
    public String getShowFieldIds() {
        return showFieldIds;
    }

    /**
     * @param showFieldIds the showFieldIds to set
     */
    public void setShowFieldIds(String showFieldIds) {
        this.showFieldIds = showFieldIds;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName == null ? null : hospitalName.trim();
    }

    public String getHospitalCode() {
        return hospitalCode;
    }

    public void setHospitalCode(String hospitalCode) {
        this.hospitalCode = hospitalCode == null ? null : hospitalCode.trim();
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Byte getIsHospital() {
        return isHospital;
    }

    public void setIsHospital(Byte isHospital) {
        this.isHospital = isHospital;
    }

    public String getCheckItemIds() {
        return checkItemIds;
    }

    public void setCheckItemIds(String checkItemIds) {
        this.checkItemIds = checkItemIds == null ? null : checkItemIds.trim();
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode == null ? null : provinceCode.trim();
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode == null ? null : cityCode.trim();
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode == null ? null : countyCode.trim();
    }

    public String getAreaCodeName() {
        return areaCodeName;
    }

    public void setAreaCodeName(String areaCodeName) {
        this.areaCodeName = areaCodeName == null ? null : areaCodeName.trim();
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress == null ? null : detailAddress.trim();
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl == null ? null : logoUrl.trim();
    }

    public Byte getReportNeedAduit() {
        return reportNeedAduit;
    }

    public void setReportNeedAduit(Byte reportNeedAduit) {
        this.reportNeedAduit = reportNeedAduit;
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
    //以下两个字段在后台门店列表中展示用
    //渠道名
    private String channelName;
    //可检测的项目
    private String checkItemNames;

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getCheckItemNames() {
		return checkItemNames;
	}

	public void setCheckItemNames(String checkItemNames) {
		this.checkItemNames = checkItemNames;
	}
    
}