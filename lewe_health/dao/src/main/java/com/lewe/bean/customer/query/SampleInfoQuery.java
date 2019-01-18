package com.lewe.bean.customer.query;

import java.util.List;

/**
 * 检测样品信息列表 和 审核信息列表查询对象
 * @author 小辉
 *
 */
public class SampleInfoQuery {
	private String keyword;//关键字
	private String keyword1;//关键字
	private String keyword2;//关键字
	private String keyword3;//关键字

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
	/**
	 * 该参数的作用是不查询submitQuestionnaire字段值为0的数据
	 */
	private String submitQuestionnaireNotQuery0 = "yes";
	
	private List<Long> hospitalIdList;
	
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
	public String getSubmitQuestionnaireNotQuery0() {
		return submitQuestionnaireNotQuery0;
	}
	public void setSubmitQuestionnaireNotQuery0(String submitQuestionnaireNotQuery0) {
		this.submitQuestionnaireNotQuery0 = submitQuestionnaireNotQuery0;
	}
	public List<Long> getHospitalIdList() {
		return hospitalIdList;
	}
	public void setHospitalIdList(List<Long> hospitalIdList) {
		this.hospitalIdList = hospitalIdList;
	}


	/**
	 * @return the keyword3
	 */
	public String getKeyword3() {
		return keyword3;
	}

	/**
	 * @param keyword3 the keyword3 to set
	 */
	public void setKeyword3(String keyword3) {
		this.keyword3 = keyword3;
	}

	/**
	 * @return the keyword2
	 */
	public String getKeyword2() {
		return keyword2;
	}

	/**
	 * @param keyword2 the keyword2 to set
	 */
	public void setKeyword2(String keyword2) {
		this.keyword2 = keyword2;
	}

	/**
	 * @return the keyword1
	 */
	public String getKeyword1() {
		return keyword1;
	}

	/**
	 * @param keyword1 the keyword1 to set
	 */
	public void setKeyword1(String keyword1) {
		this.keyword1 = keyword1;
	}
	
	
}
