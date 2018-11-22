package com.lewe.bean.customer.query;

import java.util.List;

/**
 * 客户信息列表查询对象
 * @author 小辉
 *
 */
public class CustomerInfoQuery {
	
	private String keyword;//关键字
	private Long hospitalId;//门店id
	private Byte sex;//性别
	private Integer age;//年龄
	private String beginDate;//查询开始日期
	private String endDate;//查询结束日期
	private Byte reportStatus;//报告状态 1：用户绑定 2：门店扫码 3：邮寄单号 4：乐为扫码 5：结果生成
	private Integer illnessId;//疾病id
	private Byte illnessDegree;//疾病程度 0:无 1:轻微 2:中等 3:严重
	
	private Integer pageNo;
	private Integer pageSize;
	private Integer startIndex;
	private Integer hospitalGroupId;//门店组id
	/**
	 * 通过疾病或疾病程度查询到报告id,最终是为了查询报告列表(客户信息列表)
	 */
	/**
	 * 该参数的作用是不查询submitQuestionnaire字段值为0的数据
	 */
	private String submitQuestionnaireNotQuery0 = "yes";
	private List<Long> reportInfoIdList;
	
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
	public Byte getReportStatus() {
		return reportStatus;
	}
	public void setReportStatus(Byte reportStatus) {
		this.reportStatus = reportStatus;
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
	public List<Long> getReportInfoIdList() {
		return reportInfoIdList;
	}
	public void setReportInfoIdList(List<Long> reportInfoIdList) {
		this.reportInfoIdList = reportInfoIdList;
	}
	public String getSubmitQuestionnaireNotQuery0() {
		return submitQuestionnaireNotQuery0;
	}
	public void setSubmitQuestionnaireNotQuery0(String submitQuestionnaireNotQuery0) {
		this.submitQuestionnaireNotQuery0 = submitQuestionnaireNotQuery0;
	}
	
}
