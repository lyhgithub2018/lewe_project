package com.lewe.bean.report.vo;

/**
 * 用量统计信息对象
 * @author 小辉
 *
 */
public class UsedCountInfo {
    private Integer channelId;//渠道id
    private String channelName;//渠道名称
    private Long hospitalId;//机构id
    private String hospitalName;//机构名称
    private String areaCodeName;//地区名称
    private String queryDate;//查询日期
    private Integer checkItemId;//检测项目的id
    private String checkItemName;//检测项目的名称
    private Integer hospitalConfirmCount;//门店确认数量
    private Integer expressCount;//物流数
    private Integer checkIngCount;//检测中数量
    private Integer checkFinshCount;//检测完成数量
	public Integer getChannelId() {
		return channelId;
	}
	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public Long getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Long hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	public String getAreaCodeName() {
		return areaCodeName;
	}
	public void setAreaCodeName(String areaCodeName) {
		this.areaCodeName = areaCodeName;
	}
	public String getQueryDate() {
		return queryDate;
	}
	public void setQueryDate(String queryDate) {
		this.queryDate = queryDate;
	}
	public Integer getCheckItemId() {
		return checkItemId;
	}
	public void setCheckItemId(Integer checkItemId) {
		this.checkItemId = checkItemId;
	}
	public String getCheckItemName() {
		return checkItemName;
	}
	public void setCheckItemName(String checkItemName) {
		this.checkItemName = checkItemName;
	}
	public Integer getHospitalConfirmCount() {
		return hospitalConfirmCount;
	}
	public void setHospitalConfirmCount(Integer hospitalConfirmCount) {
		this.hospitalConfirmCount = hospitalConfirmCount;
	}
	public Integer getExpressCount() {
		return expressCount;
	}
	public void setExpressCount(Integer expressCount) {
		this.expressCount = expressCount;
	}
	public Integer getCheckIngCount() {
		return checkIngCount;
	}
	public void setCheckIngCount(Integer checkIngCount) {
		this.checkIngCount = checkIngCount;
	}
	public Integer getCheckFinshCount() {
		return checkFinshCount;
	}
	public void setCheckFinshCount(Integer checkFinshCount) {
		this.checkFinshCount = checkFinshCount;
	}
    
}
