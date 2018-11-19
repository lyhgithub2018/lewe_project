package com.lewe.bean.customer.query;


/**
 * 检测样品信息列表 和 审核信息列表查询对象
 * @author 小辉
 *
 */
public class SampleInfoQuery {
	private String keyword;//关键字
	private Long hospitalId;//门店id
	private Byte sex;//性别
	private Integer age;//年龄
	private String beginDate;//查询开始日期
	private String endDate;//查询结束日期
	private Byte checkStatus;//检测状态  0:待检测 1:已检测
	private Byte auditStatus;//审核状态  0:待审核 1:已通过 2:待修改
	private Integer pageNo;
	private Integer pageSize;
	private Integer startIndex;
	private Integer hospitalGroupId;//门店组id
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Long getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Long hospitalId) {
		this.hospitalId = hospitalId;
	}
	public Byte getSex() {
		return sex;
	}
	public void setSex(Byte sex) {
		this.sex = sex;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public Byte getCheckStatus() {
		return checkStatus;
	}
	public void setCheckStatus(Byte checkStatus) {
		this.checkStatus = checkStatus;
	}
	public Integer getPageNo() {
		return pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}
	public Integer getHospitalGroupId() {
		return hospitalGroupId;
	}
	public void setHospitalGroupId(Integer hospitalGroupId) {
		this.hospitalGroupId = hospitalGroupId;
	}
	public Byte getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(Byte auditStatus) {
		this.auditStatus = auditStatus;
	}
	
}
