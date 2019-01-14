package com.lewe.serviceImpl.customer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.check.Substrate;
import com.lewe.bean.customer.CustomerAccount;
import com.lewe.bean.customer.Fans;
import com.lewe.bean.customer.vo.CustomerFansInfo;
import com.lewe.bean.hospital.Hospital;
import com.lewe.bean.hospital.HospitalDoctor;
import com.lewe.bean.hospital.HospitalRoom;
import com.lewe.bean.report.ReportCheckData;
import com.lewe.bean.report.ReportIllness;
import com.lewe.bean.report.ReportInfo;
import com.lewe.bean.report.ReportSymptom;
import com.lewe.bean.sys.Account;
import com.lewe.bean.sys.Illness;
import com.lewe.bean.sys.Symptom;
import com.lewe.dao.check.SubstrateMapper;
import com.lewe.dao.customer.CustomerAccountMapper;
import com.lewe.dao.customer.FansMapper;
import com.lewe.dao.hospital.HospitalDoctorMapper;
import com.lewe.dao.hospital.HospitalMapper;
import com.lewe.dao.hospital.HospitalRoomMapper;
import com.lewe.dao.report.ReportCheckDataMapper;
import com.lewe.dao.report.ReportIllnessMapper;
import com.lewe.dao.report.ReportInfoMapper;
import com.lewe.dao.report.ReportSymptomMapper;
import com.lewe.dao.sys.IllnessMapper;
import com.lewe.dao.sys.SymptomMapper;
import com.lewe.service.customer.ICustomerService;
import com.lewe.service.sys.ISysLogService;
import com.lewe.serviceImpl.customer.bo.Questionnaire1Bo;
import com.lewe.serviceImpl.customer.bo.Questionnaire2Bo;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.BizCode;
import com.lewe.util.common.DateUtil;
import com.lewe.util.common.JedisUtil;
import com.lewe.util.common.Page;
import com.lewe.util.common.PropertiesUtil;
import com.lewe.util.common.StringUtils;
import com.lewe.util.common.ToolsUtil;
import com.lewe.util.common.aliyun.SMSUtil;
import com.lewe.util.common.constants.ReportStatus;

@Service("customerService")
public class CustomerServiceImpl implements ICustomerService {

	@Autowired
	private ReportInfoMapper reportInfoMapper;
	@Autowired
	private CustomerAccountMapper customerAccountMapper;
	@Autowired
	private HospitalMapper hospitalMapper;
	@Autowired
	private ReportSymptomMapper reportSymptomMapper;

	public static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

	@Transactional
	public Boolean bindSampleCode(String customerId, String sampleCode, Object apiResult, Integer hospitalGroupId) {
		ApiResult result = (ApiResult) apiResult;
		if (StringUtils.isBlank(customerId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数customerId为空");
			return false;
		}
		if (StringUtils.isBlank(sampleCode)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请填写采样报告编号");
			return false;
		}

		// 查询该编号是否已经绑定过
		ReportInfo report = reportInfoMapper.selectBySampleCode(sampleCode);
		if (report != null) {
			if (report.getReportStatus() == 1 && report.getSubmitQuestionnaire() < 2) {
				result.setMessage("请完善问卷信息！");
				return true;
			} else if (report.getReportStatus() < ReportStatus.RESULT_CREATE.getValue()
					&& report.getSubmitQuestionnaire() == 2) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("该编号已绑定,报告正在检测中！");
				return false;
			} else if (report.getReportStatus() == ReportStatus.RESULT_CREATE.getValue()) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("该编号已绑定,报告已生成！请到我的报告中查看");
				return false;
			}
		} else {
			ReportInfo reportInfo = new ReportInfo();
			reportInfo.setCustomerId(Long.valueOf(customerId));
			reportInfo.setSampleCode(sampleCode);
			logger.error(sampleCode);

			// 报告状态 1：用户绑定 2：门店扫码 3：邮寄单号 4：乐为扫码 5：结果生成
			reportInfo.setReportStatus(ReportStatus.USER_BIND.getValue());

			// 由于门店编号长度可能不固定,所以总共尝试3次,分别截取前两位,三位,四位编号,直到正确找到门店编号
			Hospital hospital = null;

			if (sampleCode.length() >= 7 && hospital == null) {
				hospital = hospitalMapper.selectByHospitalCode(sampleCode.substring(0, 7));
			}
			if (sampleCode.length() >= 6 && hospital == null) {
				hospital = hospitalMapper.selectByHospitalCode(sampleCode.substring(0, 6));
			}
			if (sampleCode.length() >= 5 && hospital == null) {
				hospital = hospitalMapper.selectByHospitalCode(sampleCode.substring(0, 5));
			}
			if (sampleCode.length() >= 4 && hospital == null) {
				hospital = hospitalMapper.selectByHospitalCode(sampleCode.substring(0, 4));
			}
			if (sampleCode.length() >= 3 && hospital == null) {
				hospital = hospitalMapper.selectByHospitalCode(sampleCode.substring(0, 3));
			}
			if (sampleCode.length() >= 2 && hospital == null) {
				hospital = hospitalMapper.selectByHospitalCode(sampleCode.substring(0, 2));
			}

			if (hospital != null) {
				reportInfo.setHospitalId(hospital.getId());
				reportInfo.setChannelId(hospital.getChannelId());
				logger.error(JSON.toJSONString(hospital));
			}

			// 若当前账号归属于某个门店组,则按门店组id查询
			if (hospitalGroupId != null) {
				reportInfo.setHospitalGroupId(hospitalGroupId);
			}

			reportInfoMapper.insertSelective(reportInfo);
		}
		return true;
	}

	@Transactional
	public int submitQuestionnaire(String questionnaire1, String questionnaire2, String sampleCode, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (StringUtils.isBlank(sampleCode)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数sampleCode不可为空");
			return 0;
		}
		Questionnaire1Bo basicInfo = null;
		if (StringUtils.isNotBlank(questionnaire1)) {

			// 问卷信息1(采样者基本信息)
			basicInfo = JSONObject.parseObject(questionnaire1, Questionnaire1Bo.class);
			if (StringUtils.isBlank(basicInfo.getSampleName())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者姓名");
				return 0;
			}
			if (StringUtils.isBlank(basicInfo.getSampleSex())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择采样者性别");
				return 0;
			}
			if (StringUtils.isBlank(basicInfo.getSampleBirthday())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择采样者出生日期");
				return 0;
			}
			if (StringUtils.isBlank(basicInfo.getSamplePhone())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者本人的手机号");
				return 0;
			}
		}

		// 查询该采样编号是否已经绑定过
		ReportInfo report = reportInfoMapper.selectBySampleCode(sampleCode);
		if (report == null) {
			result.setCode(BizCode.DATA_NOT_FOUND);
			result.setMessage("当前提交的采样者信息没有绑定采样编号,请绑定后再填写问卷信息！");
			return 0;
		} 
		
		// 绑定过,则更新保存相关字段
		// 生成采样者的报告基本信息字段
		if (basicInfo != null) {
			ReportInfo reportInfo = new ReportInfo();
			BeanUtils.copyProperties(basicInfo, reportInfo);

			// 通过出生日期计算出年龄
			int age = DateUtil.getAgeByBirth(basicInfo.getSampleBirthday());
			reportInfo.setSampleAge(age);


			CustomerAccount customerAccount = customerAccountMapper.selectByPrimaryKey(basicInfo.getCustomerId());
			if (customerAccount == null) {
				result.setCode(BizCode.DATA_NOT_FOUND);
				result.setMessage("未登录！");
				return 0;
			}

			reportInfo.setCustomerPhone(customerAccount.getPhone());

			//客户不存在，是别人的手机
			if(!customerAccount.getPhone().equals(basicInfo.getSamplePhone())){
				logger.error("customerAccount.getPhone()="+customerAccount.getPhone()+"//basicInfo.getSamplePhone()="+basicInfo.getSamplePhone());
				CustomerAccount customerAccountNew = customerAccountMapper.selectByPhone(basicInfo.getSamplePhone());

				if(customerAccountNew != null){
					reportInfo.setCustomerId(customerAccountNew.getId());
				}else {
					CustomerAccount accountInsert = new CustomerAccount();
					accountInsert.setPhone(basicInfo.getSamplePhone());
					accountInsert.setCreateTime(new Date());
					accountInsert.setFansId(0L);
					accountInsert.setStatus((byte) 1);
					customerAccountMapper.insertSelective(accountInsert);
					logger.error("accountInsert.getId()="+accountInsert.getId());
					reportInfo.setCustomerId(accountInsert.getId());
				}
			}
			logger.error("report.getId()="+report.getId());
			logger.error("reportInfo.getId()="+reportInfo.getCustomerId());


			reportInfo.setId(report.getId());
			reportInfo.setSubmitTime(new Date());
			reportInfo.setSubmitQuestionnaire((byte) 1);
			reportInfo.setSampleCode(sampleCode.trim());
			reportInfoMapper.updateByPrimaryKeySelective(reportInfo);
		}

		// 问卷信息2
		if (StringUtils.isNotBlank(questionnaire2)) {
			Questionnaire2Bo questionnaire2Bo = JSONObject.parseObject(questionnaire2, Questionnaire2Bo.class);
			// 症状列表
			List<ReportSymptom> reportSymptomList = questionnaire2Bo.getReportSymptomList();
			if (reportSymptomList != null && reportSymptomList.size() > 0) {
				for (ReportSymptom reportSymptom : reportSymptomList) {
					reportSymptom.setReportInfoId(report.getId());
					reportSymptom.setId(null);
					reportSymptomMapper.insertSelective(reportSymptom);
				}
				ReportInfo update = new ReportInfo();
				update.setId(report.getId());
				// 提交方式 1：用户提交 2：辅助提交
				update.setSubmitWay((byte) 1);
				// 是否提交了调查问卷 0:否 1:提交了问卷信息1 2:提交了问卷信息2
				update.setSubmitQuestionnaire((byte) 2);
				// 困扰健康的问题
				update.setBesetHealthProblem(questionnaire2Bo.getBesetHealthProblem());
				reportInfoMapper.updateByPrimaryKeySelective(update);
			}
		}
		return 1;
	}

	public String sendSmsCode(String phone, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;

		logger.info("sendCode:" + phone);
		if (StringUtils.isBlank(phone)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入手机号");
			logger.info("sendCode:" + result.getMessage());
			return result.getMessage();
		}
		if (!ToolsUtil.checkMobileNumber(phone.trim())) {
			result.setCode(BizCode.DATA_INCORRECT);
			result.setMessage("请输入正确的手机号");
			logger.info("sendCode:" + result.getMessage());
			return result.getMessage();
		}
		int smsCode = (int) ((Math.random() * 9 + 1) * 100000);

		String key = "customerAccount_smsCode:" + phone;
		String outTime = PropertiesUtil.getApiPropertyByKey("smsCode.outTime");
		String sendSMS = "OK";
		JedisUtil redis = JedisUtil.getInstance();
		String limitKey = "customerAccount_smsCode_limit:" + phone;

		if (redis.exists(limitKey)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("验证码发送过于频繁,请稍后再试");
			logger.info("sendCode:" + result.getMessage());
			return result.getMessage();
		} else {
			// sendSMS = SMSUtil.sendSMS(phone, message, null);我们公司自己的短信接口
			// 调用阿里云短信接口
			// 判断当前手机号是否注册
			CustomerAccount customerAccount = customerAccountMapper.selectByPhone(phone.trim());
			if (customerAccount == null) {// 注册短信
				sendSMS = SMSUtil.sendRegistSMS(phone.trim(), smsCode + "");
				logger.info("sendCode:" + sendSMS);
			} else {// 登录短信
				sendSMS = SMSUtil.sendLoginSMS(phone.trim(), smsCode + "");
				logger.info("sendCode login:" + sendSMS);
			}
		}

		if ("OK".equals(sendSMS)) {
			redis.hset(key, "phone", phone);
			redis.hset(key, "smsCode", smsCode + "");
			redis.setExpire(key, 60 * Integer.parseInt(outTime));// 验证码缓存5分钟
			// 存储一个短信发送限制的标识
			redis.set(limitKey, "1");
			redis.setExpire(limitKey, 60);// 1分钟
			return "验证码发送成功";
		} else {
			return "验证码发送失败," + sendSMS;
		}
	}

	@Transactional
	public CustomerAccount registOrLogin(String phone, String smsCode, Long fansId, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		/*
		 * if(fansId==null) { result.setCode(BizCode.PARAM_EMPTY);
		 * result.setMessage("缺少参数"); return null; }
		 */
		if (StringUtils.isBlank(phone)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入手机号");
			return null;
		}
		if (!ToolsUtil.checkMobileNumber(phone.trim())) {
			result.setCode(BizCode.DATA_INCORRECT);
			result.setMessage("请输入正确的手机号");
			return null;
		}
		if (StringUtils.isBlank(smsCode)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入验证码");
			return null;
		}
		// 1.校验验证码
		JedisUtil redis = JedisUtil.getInstance();
		String key = "customerAccount_smsCode:" + phone;
		String smsCodeR = redis.hget(key, "smsCode");
		if (StringUtils.isBlank(smsCodeR)) {
			result.setCode(BizCode.DATA_INCORRECT);
			result.setMessage("该验证码不存在或已过期");
			return null;
		}
		if (!smsCode.equals(smsCodeR)) {
			result.setCode(BizCode.DATA_INCORRECT);
			result.setMessage("您输入的验证码不正确");
			return null;
		}
		// 2.校验账号是否存在
		CustomerAccount account = customerAccountMapper.selectByPhone(phone);

		// 3.插入新账号
		if (account == null) {
			CustomerAccount customer = customerAccountMapper.selectByFansId(fansId);
			if (customer != null && !phone.equals(customer.getPhone())) {
				result.setCode(BizCode.DATA_EXIST);
				result.setMessage("该微信号已被账号:" + customer.getPhone() + "绑定");
				return null;
			}
			
			account = new CustomerAccount();
			account.setPhone(phone);
			account.setCreateTime(new Date());
			account.setFansId(fansId);
			account.setStatus((byte) 1);
			customerAccountMapper.insertSelective(account);
			result.setMessage("注册成功！");
		} else {
			if (account.getStatus() == 2) {
				result.setCode(BizCode.LOGIN_FAIL);
				result.setMessage("您的账号已被冻结,暂无法使用！");
				return null;
			}
			result.setMessage("登录成功！");
		}
		return account;
	}

	public JSONObject getUserManageList(String keyword, Byte status, Integer pageNo, Integer pageSize) {
		JSONObject json = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isDel", 0);
		if (StringUtils.isNotBlank(keyword)) {
			keyword = keyword.replaceAll("\\s*", "");
			map.put("keyword", "%" + keyword + "%");
		}
		Integer totalCount = customerAccountMapper.selectCountByMap(map);
		if (totalCount == null || totalCount == 0) {
			json.put("page", null);
			json.put("userManageList", null);
			return json;
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		List<CustomerFansInfo> list = customerAccountMapper.selectListByMap(map);
		String defualtAvatar = "https://aijutong.com/upload/image/default-avatar.png";
		for (CustomerFansInfo customerFansInfo : list) {
			if (StringUtils.isBlank(customerFansInfo.getHeadImgUrl())) {
				customerFansInfo.setHeadImgUrl(defualtAvatar);
			}
		}
		json.put("page", page);
		json.put("userManageList", list);
		return json;
	}

	public int updatePhone(Long id, String phone, Account account, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (StringUtils.isBlank(id)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数id为空");
			return 0;
		}
		if (StringUtils.isBlank(phone)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请填写手机号");
			return 0;
		}
		if (!ToolsUtil.checkMobileNumber(phone)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("手机号填写不正确");
			return 0;
		}
		CustomerAccount customerAccount = customerAccountMapper.selectByPhone(phone);
		if (customerAccount != null && id.longValue() != customerAccount.getId().longValue()) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("该手机号已存在,请重新填写");
			return 0;
		}
		CustomerAccount customerDB = customerAccountMapper.selectByPrimaryKey(id);
		CustomerAccount update = new CustomerAccount();
		update.setId(id);
		update.setPhone(phone);
		update.setUpdateTime(new Date());
		customerAccountMapper.updateByPrimaryKeySelective(update);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerId", id);
		map.put("customerPhone", phone);
		reportInfoMapper.updateCustomerPhoneByCustomerId(map);
		String content = "修改了一个账号信息,原手机号:" + customerDB.getPhone() + ",新手机号:" + phone;
		sysLogService.addSysLog(account, content, new Date());
		return 1;
	}

	@Autowired
	private ISysLogService sysLogService;

	public int freezeCustomer(Long id, Byte status, Account account, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (StringUtils.isBlank(id)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数id为空");
			return 0;
		}
		CustomerAccount update = new CustomerAccount();
		update.setId(id);
		update.setStatus(status);
		update.setUpdateTime(new Date());
		customerAccountMapper.updateByPrimaryKeySelective(update);
		String content = "";
		CustomerAccount customerAccount = customerAccountMapper.selectByPrimaryKey(id);

		if (customerAccount.getPhone() == null) {
			customerAccount.setPhone("无");
		}

		if (status == 1) {
			content = "解冻了一个账号,手机号:" + customerAccount.getPhone();
		} else if (status == 2) {
			content = "冻结了一个账号,手机号:" + customerAccount.getPhone();
		}
		sysLogService.addSysLog(account, content, new Date());
		return 1;
	}

	public JSONObject myReportList(Long customerId, String keyword, String beginDate, String endDate,
			Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (customerId == null) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数customerId为空");
			return null;
		}
		JSONObject json = new JSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(keyword)) {
			keyword = keyword.replaceAll("\\s*", "");// 去除所有空格
			paramMap.put("keyword", "%" + keyword + "%");// 编号,机构名
		}
		if (beginDate != null) {
			paramMap.put("beginDate", beginDate);
		}
		if (endDate != null) {
			paramMap.put("endDate", endDate);
		}
		paramMap.put("customerId", customerId);
		List<JSONObject> myReportList = new ArrayList<JSONObject>();
		List<ReportInfo> list = reportInfoMapper.selectListByMap(paramMap);

		for (ReportInfo reportInfo : list) {
			JSONObject report = new JSONObject();
			report.put("id", reportInfo.getId());
			report.put("sampleCode", reportInfo.getSampleCode());// 采样编号
			report.put("sampleName", reportInfo.getSampleName());// 采样者姓名
			if (reportInfo.getSampleSex() == 1) {
				report.put("sampleSex", "男");// 采样者性别
			} else if (reportInfo.getSampleSex() == 2) {
				report.put("sampleSex", "女");// 采样者性别
			}
			report.put("sampleAge", reportInfo.getSampleAge() + "岁");// 采样者年龄
			report.put("samplePhone", reportInfo.getSamplePhone());// 采样者手机号
			report.put("submitTime", reportInfo.getSubmitTime() == null ? null : DateUtil.formatDate(reportInfo.getSubmitTime(), "yyyy-MM-dd HH:mm:ss"));// 采样提交时间

			// && reportInfo.getSubmitQuestionnaire() < 2
			if (reportInfo.getReportStatus() < ReportStatus.HOSPITAL_SCAN.getValue()) {
				report.put("status", 0);
				report.put("statusDesc", "");
			} else if (reportInfo.getCheckStatus() == 0) {
				report.put("status", 1);
				report.put("statusDesc", "检测中");
			} else if (reportInfo.getAuditStatus() == 0) {
				report.put("status", 1);
				report.put("statusDesc", "审核中");
			} else {
				report.put("status", 2);
				report.put("statusDesc", "查看报告");
			}
			if (reportInfo.getSubmitQuestionnaire() != 0) {
				myReportList.add(report);
			}
		}
		json.put("myReportList", myReportList);
		return json;
	}

	@Autowired
	private ReportIllnessMapper reportIllnessMapper;
	@Autowired
	private HospitalRoomMapper hospitalRoomMapper;
	@Autowired
	private HospitalDoctorMapper hospitalDoctorMapper;
	@Autowired
	private ReportCheckDataMapper reportCheckDataMapper;
	@Autowired
	private IllnessMapper illnessMapper;
	@Autowired
	private SymptomMapper symptomMapper;
	@Autowired
	private SubstrateMapper substrateMapper;

	public JSONObject myReportInfo(Long reportId, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (reportId == null) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数reportId为空");
			return null;
		}
		JSONObject json = new JSONObject();
		ReportInfo reportInfo = reportInfoMapper.selectByPrimaryKey(reportId);
		if (reportInfo != null) {
			json.put("id", reportInfo.getId());
			json.put("reportName", reportInfo.getReportName());// 报告名称
			// 机构信息
			Hospital hospital = hospitalMapper.selectByPrimaryKey(reportInfo.getHospitalId());
			json.put("hospitalName", hospital == null ? null : hospital.getHospitalName());// 机构名称
			HospitalRoom hospitalRoom = hospitalRoomMapper.selectByPrimaryKey(reportInfo.getHospitalRoomId());
			json.put("roomName", hospitalRoom == null ? null : hospitalRoom.getRoomName());// 送检科室
			HospitalDoctor hospitalDoctor = hospitalDoctorMapper.selectByPrimaryKey(reportInfo.getHospitalDoctorId());
			json.put("doctorName", hospitalDoctor == null ? null : hospitalDoctor.getDoctorName());// 送检医生
			// 基础信息
			json.put("samplePhone", reportInfo.getSamplePhone());// 采样者手机号
			json.put("sampleCode", reportInfo.getSampleCode());// 采样者编号
			json.put("sampleName", reportInfo.getSampleName());// 采样者姓名
			if (reportInfo.getSampleSex() == 1) {
				json.put("sampleSex", "男");// 采样者性别
			} else if (reportInfo.getSampleSex() == 2) {
				json.put("sampleSex", "女");// 采样者性别
			}
			json.put("sampleAge", reportInfo.getSampleAge());// 采样者年龄
			json.put("sampleHeight", reportInfo.getSampleHeight());// 采样者身高
			json.put("sampleWeight", reportInfo.getSampleWeight());// 采样者体重
			json.put("submitTime", reportInfo.getSubmitTime() == null ? null
					: DateUtil.formatDate(reportInfo.getSubmitTime(), "yyyy-MM-dd"));// 采样时间
			json.put("analysisTime", reportInfo.getCheckTime() == null ? null
					: DateUtil.formatDate(reportInfo.getCheckTime(), "yyyy-MM-dd"));// 分析样本时间

			// 采样疾病信息
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("reportInfoId", reportId);
			map.put("illnessDegree", 1);// 此处只查询有疾病的数据
			List<ReportIllness> illnessList = reportIllnessMapper.selectListByMap(map);
			List<ReportSymptom> symptomList = reportSymptomMapper.selectListByMap(map);
			String illnessNames = "";// 疾病名称
			String symptomNames = "";// 症状名称
			for (ReportIllness reportIllness : illnessList) {
				Illness illness = illnessMapper.selectByPrimaryKey(reportIllness.getIllnessId());
				if (illness != null) {
					illnessNames = illnessNames + illness.getName() + "、";
				}
			}

			Map<Integer, String> mapShow = new HashMap<Integer, String>();

			// 1：轻度 2：中度 3：重度 4：阳性 5：阴性
			mapShow.put(1, "轻度");
			mapShow.put(2, "中度");
			mapShow.put(3, "重度");
			mapShow.put(4, "阳性");
			mapShow.put(5, "阴性");

			for (ReportSymptom reportSymptom : symptomList) {
				if (reportSymptom.getSymptomDegree() != 0) {
					Symptom symptom = symptomMapper.selectByPrimaryKey(reportSymptom.getSymptomId());
					if (symptom != null) {
						if (mapShow.containsKey(reportSymptom.getSymptomDegree())) {
							symptomNames = symptomNames + symptom.getName() + " ["
									+ mapShow.get(reportSymptom.getSymptomDegree()) + "]、";
						} else {
							symptomNames = symptomNames + symptom.getName() + "、";
						}
					}
				}
			}
			
			json.put("illnessNames", illnessNames);
			json.put("symptomNames", symptomNames);
			json.put("takeAntibiotics", reportInfo.getTakeAntibiotics());// 近一个月是否服用抗生素 0:否 1:是
			json.put("antibioticsName", reportInfo.getAntibioticsName());// 所服用抗生素的药品名称
			json.put("helicobacterPyloriCheck", reportInfo.getHelicobacterPyloriCheck());// 幽门螺旋杆菌检测 0:未检测 1:阳性 2:阴性
			json.put("hpCheckResult", reportInfo.getHpCheckResult());// 幽门螺旋杆菌检测结果
			json.put("gastroscopeEnteroscopyCheck", reportInfo.getGastroscopeEnteroscopyCheck());// 胃镜/肠镜检测 0:未检测 1:胃镜
																									// 2:肠镜
			json.put("geCheckResult", reportInfo.getGeCheckResult());// 胃镜/肠镜检测结果
			json.put("foodMedicineAllergy", reportInfo.getFoodMedicineAllergy());// 食物/药物过敏 0:否 1:是
			json.put("allergyFood", reportInfo.getAllergyFood());//
			// 服用底物和剂量
			Substrate substrate = substrateMapper.selectByPrimaryKey(reportInfo.getCheckSubstrateId());
			json.put("substrateName", substrate == null ? null : substrate.getName());// 底物名
			json.put("substrateDosage", reportInfo.getCheckSubstrateDosage() + "ml");// 剂量
			// List<CheckItemSubstrate> substrateList =
			// checkItemSubstrateMapper.selectListByItemId(reportInfo.getCheckItemId());
			// json.put("substrateList", substrateList);
			// 检测数据
			List<ReportCheckData> checkDataList = reportCheckDataMapper.selectListByMap(map);
			json.put("gasCheckResult", reportInfo.getGasCheckResult());// 气体检测结果描述
			json.put("reportResult", reportInfo.getReportResult());// 报告结果标识
			json.put("reportResultDescription", reportInfo.getReportResultDescription());// 报告结果描述
			json.put("reportDataAnalysis", reportInfo.getReportDataAnalysis());// 数据分析
			json.put("interventionSuggestion", reportInfo.getInterventionSuggestion());// 干预建议
			List<JSONObject> jsonList = new ArrayList<JSONObject>();
			for (ReportCheckData reportCheckData : checkDataList) {
				JSONObject checkData = new JSONObject();
				checkData.put("timeSpace", reportCheckData.getTimeSpace());// 时间间隔
				checkData.put("checkTime", reportCheckData.getCheckTime());// 检测时间
				checkData.put("h2Concentration", reportCheckData.getH2Concentration());// 氢气浓度
				checkData.put("ch4Concentration", reportCheckData.getCh4Concentration());// 甲烷浓度
				checkData.put("co2Concentration", reportCheckData.getCo2Concentration());// 二氧化碳浓度
				jsonList.add(checkData);
			}
			json.put("checkDataList", jsonList);// 检测数据
		}
		return json;
	}

	public CustomerAccount getCustomerAccountByFansId(Long fansId) {
		if (fansId != null) {
			CustomerAccount customer = customerAccountMapper.selectByFansId(fansId);
			return customer;
		}
		return null;
	}

	public JSONObject getQuestionnaire1Info(Long reportId, String sampleCode, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (reportId == null && StringUtils.isBlank(sampleCode)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("缺少参数");
			return null;
		}
		ReportInfo reportInfo = null;
		if (reportId != null) {
			reportInfo = reportInfoMapper.selectByPrimaryKey(reportId);
		} else {
			reportInfo = reportInfoMapper.selectBySampleCode(sampleCode);
		}
		JSONObject json = new JSONObject();
		if (reportInfo != null) {
			json.put("customerId", reportInfo.getCustomerId());
			json.put("sampleCode", reportInfo.getSampleCode());
			json.put("sampleName", reportInfo.getSampleName());
			json.put("samplePhone", reportInfo.getSamplePhone());
			json.put("sampleSex", reportInfo.getSampleSex());
			json.put("sampleBirthday", reportInfo.getSampleBirthday() == null ? null : DateUtil.formatDate(reportInfo.getSampleBirthday(), "yyyy-MM-dd"));
			json.put("sampleHeight", reportInfo.getSampleHeight());
			json.put("sampleWeight", reportInfo.getSampleWeight());
		}
		return json;
	}

	@Autowired
	private FansMapper fansMapper;

	public JSONObject getFansInfo(Long fansId, Long customerId, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (fansId == null) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数fansId为空");
			return null;
		}
		JedisUtil redis = JedisUtil.getInstance();
		String key = "lewe_myFansInfo:" + fansId;
		JSONObject json = null;
		String str = redis.get(key);
		if (StringUtils.isNotBlank(str)) {
			json = JSONObject.parseObject(str);
		}
		if (json != null) {
			return json;
		} else {
			json = new JSONObject();
		}
		Fans fans = fansMapper.selectByPrimaryKey(fansId);
		String defualtAvatar = "https://aijutong.com/upload/image/default-avatar.png";
		if (fans != null) {
			CustomerAccount customer = customerAccountMapper.selectByFansId(fansId);
			CustomerAccount customerAccount = customerAccountMapper.selectByPrimaryKey(customerId);
			String phone = customer == null ? null : customer.getPhone();
			if (StringUtils.isBlank(phone) && customerAccount != null) {
				phone = customerAccount.getPhone();
			}
			json.put("phone", phone);
			json.put("nickName", fans.getNickName());
			json.put("headImgUrl", fans.getHeadImgUrl() == null ? defualtAvatar : fans.getHeadImgUrl());
			redis.set(key, json.toJSONString());
			redis.setExpire(key, 3 * 24 * 3600);// 缓存3天

			// 此逻辑是为了处理用户先拒绝公众号授权之后 从C端'我的'页面重新授权后,将fansId与其账号进行关联。
			if (customer == null && customerAccount != null) {
				CustomerAccount update = new CustomerAccount();
				update.setId(customerAccount.getId());
				update.setFansId(fansId);
				customerAccountMapper.updateByPrimaryKey(update);
			}
		} else {
			json.put("phone", null);
			json.put("nickName", null);
			json.put("headImgUrl", defualtAvatar);
		}
		return json;
	}

}
