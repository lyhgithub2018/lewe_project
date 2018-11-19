package com.lewe.serviceImpl.check.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lewe.bean.report.ReportCheckData;
import com.lewe.bean.report.ReportIllness;
import com.lewe.bean.report.ReportSymptom;

/**
 * 检测信息提交对象
 * @author 小辉
 *
 */
public class CheckInfoBo {
	private Long id;
	
	/**
	 * 采样者编号
	 */
	private String sampleCode;
	
	/**
	 * 采样者姓名
	 */
	private String sampleName;
	
	/**
	 * 采样者本人手机号
	 */
	private String samplePhone;
	
	/**
	 * 采样者性别 1：男 2：女
	 */
	private Byte sampleSex;
	
	/**
	 * 采样者年龄
	 */
	private Integer sampleAge;
	
	/**
	 * 采样者生日
	 */
	private Date sampleBirthday;
	
	/**
	 * 采样者身高(单位:cm)
	 */
	private Integer sampleHeight;
	
	/**
	 * 采样提交时间
	 */
	private Date submitTime;
	
	/**
	 * 送检门店/机构id
	 */
	private Long hospitalId;
	
	/**
	 * 送检科室id
	 */
	private Long hospitalRoomId;
	
	/**
	 * 送检医生id
	 */
	private Long hospitalDoctorId;
	
	/**
	 * 检测项目id
	 */
	private Integer checkItemId;
	
	/**
	 * 检测设备id
	 */
	private Integer checkDeviceId;
	
	/**
	 * 检测底物id
	 */
	private Integer checkSubstrateId;
	
	/**
	 * 检测底物剂量(单位：ml)
	 */
	private Integer checkSubstrateDosage;
	
	/**
	 * 检测员账号id
	 */
	private Long checkAccountId;
	
	/**
	 * 近一个月是否服用抗生素 0:否 1:是
	 */
	private Byte takeAntibiotics;
	
	/**
	 * 所服用抗生素的药品名称
	 */
	private String antibioticsName;
	
	/**
	 * 幽门螺旋杆菌检测 0:未检测 1:阳性 2:阴性
	 */
	private Byte helicobacterPyloriCheck;
	
	/**
	 * 幽门螺旋杆菌检测结果
	 */
	private String hpCheckResult;
	
	/**
	 * 胃镜/肠镜检测 0:未检测 1:胃镜 2:肠镜
	 */
	private Byte gastroscopeEnteroscopyCheck;
	
	/**
	 * 胃镜/肠镜检测结果
	 */
	private String geCheckResult;
	
	/**
	 * 食物/药物过敏 0:否 1:是
	 */
	private Byte foodMedicineAllergy;
	
	/**
	 * 过敏物品名称
	 */
	private String allergyFood;
	
	/**
	 * 困扰健康的问题
	 */
	private String besetHealthProblem;
	
	/**
	 * 疾病列表
	 */
	private List<ReportIllness> illnessList = new ArrayList<ReportIllness>();
	/**
	 * 症状列表
	 */
	private List<ReportSymptom> symptomList = new ArrayList<ReportSymptom>();
	
	private List<ReportCheckData> checkDataList = new ArrayList<ReportCheckData>();
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSampleCode() {
		return sampleCode;
	}
	public void setSampleCode(String sampleCode) {
		this.sampleCode = sampleCode;
	}
	public String getSampleName() {
		return sampleName;
	}
	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}
	public String getSamplePhone() {
		return samplePhone;
	}
	public void setSamplePhone(String samplePhone) {
		this.samplePhone = samplePhone;
	}
	public Byte getSampleSex() {
		return sampleSex;
	}
	public void setSampleSex(Byte sampleSex) {
		this.sampleSex = sampleSex;
	}
	public Integer getSampleAge() {
		return sampleAge;
	}
	public void setSampleAge(Integer sampleAge) {
		this.sampleAge = sampleAge;
	}
	public Date getSampleBirthday() {
		return sampleBirthday;
	}
	public void setSampleBirthday(Date sampleBirthday) {
		this.sampleBirthday = sampleBirthday;
	}
	public Integer getSampleHeight() {
		return sampleHeight;
	}
	public void setSampleHeight(Integer sampleHeight) {
		this.sampleHeight = sampleHeight;
	}
	public Date getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}
	public Long getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Long hospitalId) {
		this.hospitalId = hospitalId;
	}
	public Long getHospitalRoomId() {
		return hospitalRoomId;
	}
	public void setHospitalRoomId(Long hospitalRoomId) {
		this.hospitalRoomId = hospitalRoomId;
	}
	public Long getHospitalDoctorId() {
		return hospitalDoctorId;
	}
	public void setHospitalDoctorId(Long hospitalDoctorId) {
		this.hospitalDoctorId = hospitalDoctorId;
	}
	public Integer getCheckItemId() {
		return checkItemId;
	}
	public void setCheckItemId(Integer checkItemId) {
		this.checkItemId = checkItemId;
	}
	public Integer getCheckDeviceId() {
		return checkDeviceId;
	}
	public void setCheckDeviceId(Integer checkDeviceId) {
		this.checkDeviceId = checkDeviceId;
	}
	public Integer getCheckSubstrateId() {
		return checkSubstrateId;
	}
	public void setCheckSubstrateId(Integer checkSubstrateId) {
		this.checkSubstrateId = checkSubstrateId;
	}
	public Integer getCheckSubstrateDosage() {
		return checkSubstrateDosage;
	}
	public void setCheckSubstrateDosage(Integer checkSubstrateDosage) {
		this.checkSubstrateDosage = checkSubstrateDosage;
	}
	public Long getCheckAccountId() {
		return checkAccountId;
	}
	public void setCheckAccountId(Long checkAccountId) {
		this.checkAccountId = checkAccountId;
	}
	public Byte getTakeAntibiotics() {
		return takeAntibiotics;
	}
	public void setTakeAntibiotics(Byte takeAntibiotics) {
		this.takeAntibiotics = takeAntibiotics;
	}
	public String getAntibioticsName() {
		return antibioticsName;
	}
	public void setAntibioticsName(String antibioticsName) {
		this.antibioticsName = antibioticsName;
	}
	public Byte getHelicobacterPyloriCheck() {
		return helicobacterPyloriCheck;
	}
	public void setHelicobacterPyloriCheck(Byte helicobacterPyloriCheck) {
		this.helicobacterPyloriCheck = helicobacterPyloriCheck;
	}
	public String getHpCheckResult() {
		return hpCheckResult;
	}
	public void setHpCheckResult(String hpCheckResult) {
		this.hpCheckResult = hpCheckResult;
	}
	public Byte getGastroscopeEnteroscopyCheck() {
		return gastroscopeEnteroscopyCheck;
	}
	public void setGastroscopeEnteroscopyCheck(Byte gastroscopeEnteroscopyCheck) {
		this.gastroscopeEnteroscopyCheck = gastroscopeEnteroscopyCheck;
	}
	public String getGeCheckResult() {
		return geCheckResult;
	}
	public void setGeCheckResult(String geCheckResult) {
		this.geCheckResult = geCheckResult;
	}
	public Byte getFoodMedicineAllergy() {
		return foodMedicineAllergy;
	}
	public void setFoodMedicineAllergy(Byte foodMedicineAllergy) {
		this.foodMedicineAllergy = foodMedicineAllergy;
	}
	public String getAllergyFood() {
		return allergyFood;
	}
	public void setAllergyFood(String allergyFood) {
		this.allergyFood = allergyFood;
	}
	public String getBesetHealthProblem() {
		return besetHealthProblem;
	}
	public void setBesetHealthProblem(String besetHealthProblem) {
		this.besetHealthProblem = besetHealthProblem;
	}
	public List<ReportIllness> getIllnessList() {
		return illnessList;
	}
	public void setIllnessList(List<ReportIllness> illnessList) {
		this.illnessList = illnessList;
	}
	public List<ReportSymptom> getSymptomList() {
		return symptomList;
	}
	public void setSymptomList(List<ReportSymptom> symptomList) {
		this.symptomList = symptomList;
	}
	public List<ReportCheckData> getCheckDataList() {
		return checkDataList;
	}
	public void setCheckDataList(List<ReportCheckData> checkDataList) {
		this.checkDataList = checkDataList;
	}
	
}
