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
import com.lewe.bean.customer.CustomerAccount;
import com.lewe.bean.customer.query.CustomerInfoQuery;
import com.lewe.bean.customer.vo.CustomerInfoVo;
import com.lewe.bean.hospital.Hospital;
import com.lewe.bean.hospital.HospitalGroup;
import com.lewe.bean.report.ExpressInfo;
import com.lewe.bean.report.ReportIllness;
import com.lewe.bean.report.ReportInfo;
import com.lewe.bean.report.ReportSymptom;
import com.lewe.bean.sys.Account;
import com.lewe.bean.sys.Channel;
import com.lewe.dao.customer.CustomerAccountMapper;
import com.lewe.dao.hospital.HospitalGroupMapper;
import com.lewe.dao.hospital.HospitalMapper;
import com.lewe.dao.report.ExpressInfoMapper;
import com.lewe.dao.report.ReportIllnessMapper;
import com.lewe.dao.report.ReportInfoMapper;
import com.lewe.dao.report.ReportSymptomMapper;
import com.lewe.dao.sys.AccountMapper;
import com.lewe.dao.sys.ChannelMapper;
import com.lewe.service.customer.ICustomerManageService;
import com.lewe.service.sys.ISysLogService;
import com.lewe.serviceImpl.customer.bo.CustomerInfoBo;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.BizCode;
import com.lewe.util.common.DateUtil;
import com.lewe.util.common.JedisUtil;
import com.lewe.util.common.Page;
import com.lewe.util.common.StringUtils;
import com.lewe.util.common.constants.AccountType;
import com.lewe.util.common.constants.ReportStatus;

@Service("customerManageService")
public class CustomerManageServiceImpl implements ICustomerManageService {

	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private CustomerAccountMapper customerAccountMapper;
	@Autowired
	private ReportInfoMapper reportInfoMapper;
	@Autowired
	private ChannelMapper channelMapper;
	@Autowired
	private HospitalMapper hospitalMapper;
	@Autowired
	private ExpressInfoMapper expressInfoMapper;
	@Autowired
	private ReportIllnessMapper reportIllnessMapper;
	@Autowired
	private ReportSymptomMapper reportSymptomMapper;

	@Autowired
	private HospitalGroupMapper hospitalGroupMapper;

	public static final Logger logger = LoggerFactory.getLogger(CustomerManageServiceImpl.class);

	public JSONObject getCustomerInfoList(String customerInfoQuery, Account loginAccount, Object apiResult) {
		JSONObject json = new JSONObject();
		CustomerInfoQuery query = null;
		if (StringUtils.isNotBlank(customerInfoQuery)) {
			query = JSONObject.parseObject(customerInfoQuery, CustomerInfoQuery.class);
		} else {
			query = new CustomerInfoQuery();
		}

		List<Long> hosIdList = this.getUserHostList(loginAccount);

		// 这里做权限判定
		if (hosIdList == null) {
			// 主账号
		} else if (hosIdList.size() == 0) {
			// 无权限
			hosIdList.add(0L);
			query.setHospitalIdList(hosIdList);
		} else {
			query.setHospitalIdList(hosIdList);
		}

		String keyword = query.getKeyword();
		if (StringUtils.isNotBlank(keyword)) {
			keyword = keyword.replaceAll("\\s*", "");
			query.setKeyword("%" + keyword + "%");
		} else {
			query.setKeyword(null);
		}
		if (StringUtils.isBlank(query.getBeginDate())) {
			query.setBeginDate(null);
		}
		if (StringUtils.isBlank(query.getEndDate())) {
			query.setEndDate(null);
		}

		// 判断是否有illnessId和illnessDegree这两个参数查询
		Map<String, Object> map = new HashMap<String, Object>();
		if (query.getIllnessId() != null) {
			map.put("illnessId", query.getIllnessId());
		}
		if (query.getIllnessDegree() != null) {
			map.put("illnessDegree", query.getIllnessDegree());
		}

		if (map.size() > 0) {
			List<ReportIllness> illnessList = reportIllnessMapper.selectListByMap(map);
			if (illnessList != null && illnessList.size() > 0) {
				List<Long> reportInfoIdList = new ArrayList<Long>();
				for (ReportIllness reportIllness : illnessList) {
					reportInfoIdList.add(reportIllness.getReportInfoId());
				}
				query.setReportInfoIdList(reportInfoIdList);
			}
		}

		logger.error(JSON.toJSONString(query));

		Integer totalCount = reportInfoMapper.selectCountByCustomerInfoQuery(query);
		if (totalCount == null || totalCount == 0) {
			json.put("page", null);
			json.put("customerInfoList", null);
			return json;
		}
		Page page = new Page(query.getPageNo(), query.getPageSize(), totalCount);
		query.setStartIndex(page.getStartIndex());
		List<ReportInfo> list = reportInfoMapper.selectListByCustomerInfoQuery(query);

		List<JSONObject> customerInfoList = new ArrayList<JSONObject>();
		for (ReportInfo reportInfo : list) {
			JSONObject customerInfo = new JSONObject();
			customerInfo.put("id", reportInfo.getId());
			customerInfo.put("customerPhone", reportInfo.getCustomerPhone());// 用户手机号
			customerInfo.put("sampleCode", reportInfo.getSampleCode());// 采样编号
			customerInfo.put("sampleName", reportInfo.getSampleName());// 采样者姓名
			customerInfo.put("samplePhone", reportInfo.getSamplePhone());// 采样者手机号
			if (reportInfo.getSampleSex() != null) {// 采样者性别
				if (reportInfo.getSampleSex() == 1) {
					customerInfo.put("sampleSex", "男");
				} else if (reportInfo.getSampleSex() == 2) {
					customerInfo.put("sampleSex", "女");
				}
			}
			customerInfo.put("sampleAge", reportInfo.getSampleAge());// 采样者年龄
			customerInfo.put("submitTime", reportInfo.getSubmitTime() == null ? null
					: DateUtil.formatDate(reportInfo.getSubmitTime(), "yyyy-MM-dd HH:mm:ss"));// 采样提交时间
			if (reportInfo.getSubmitWay() != null) {// 提交方式 1:用户提交 2:辅助提交
				if (reportInfo.getSubmitWay() == 1) {
					customerInfo.put("submitWay", "用户提交");
				} else if (reportInfo.getSubmitWay() == 2) {
					customerInfo.put("submitWay", "辅助提交");
				}
			}
			customerInfo.put("hospitalScanTime", reportInfo.getHospitalScanTime() == null ? null
					: DateUtil.formatDate(reportInfo.getHospitalScanTime(), "yyyy-MM-dd HH:mm:ss"));// 门店扫码日期
			customerInfo.put("checkTime", reportInfo.getCheckTime() == null ? null
					: DateUtil.formatDate(reportInfo.getCheckTime(), "yyyy-MM-dd HH:mm:ss"));// 检测时间
			customerInfo.put("reportStatus", reportInfo.getReportStatus());// 报告状态
			customerInfo.put("reportStatusName", ReportStatus.getDescByStatus(reportInfo.getReportStatus()));// 状态描述
			customerInfoList.add(customerInfo);
		}
		json.put("page", page);
		json.put("customerInfoList", customerInfoList);
		return json;
	}

	@Autowired
	private ISysLogService sysLogService;

	@Transactional
	public int addOrUpdateCustomerInfo(Integer operateType, String customerInfo, Account loginAccount,
			Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (operateType == null) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数operateType为空");
			return 0;
		}
		if (StringUtils.isNotBlank(customerInfo)) {
			CustomerInfoBo customerInfoBo = JSONObject.parseObject(customerInfo, CustomerInfoBo.class);
			if (StringUtils.isBlank(customerInfoBo.getSamplePhone())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者手机号");
				return 0;
			}
			if (StringUtils.isBlank(customerInfoBo.getSampleCode())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者编码");
				return 0;
			}
			if (StringUtils.isBlank(customerInfoBo.getSampleName())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者姓名");
				return 0;
			}
			if (StringUtils.isBlank(customerInfoBo.getSampleSex())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请选择采样者性别");
				return 0;
			}
			if (StringUtils.isBlank(customerInfoBo.getSampleAge())) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("请填写采样者年龄");
				return 0;
			}
			// 用采样者手机号生成C端客户账号
			CustomerAccount customerAccount = customerAccountMapper.selectByPhone(customerInfoBo.getSamplePhone());
			if (customerAccount == null) {
				customerAccount = new CustomerAccount();
				customerAccount.setPhone(customerInfoBo.getSamplePhone());
				customerAccount.setCreateTime(new Date());
				customerAccountMapper.insertSelective(customerAccount);
			}

			ReportInfo reportInfo = new ReportInfo();
			BeanUtils.copyProperties(customerInfoBo, reportInfo);

			// 查询采样编号是否已经绑定过
			ReportInfo reportDB = reportInfoMapper.selectBySampleCode(customerInfoBo.getSampleCode());
			if (operateType == 1) {// 新增
				if (reportDB != null) {
					result.setCode(BizCode.DATA_EXIST);
					result.setMessage("采样者编号【" + customerInfoBo.getSampleCode() + "】已绑定,请核对后重新填写！");
					return 0;
				}
				reportInfo.setHospitalId(loginAccount.getHospitalId());
				reportInfo.setHospitalGroupId(loginAccount.getHospitalGroupId());
				reportInfo.setReportStatus(ReportStatus.USER_BIND.getValue());
				reportInfo.setSubmitWay((byte) 2);// 提交方式 1：用户提交 2：辅助提交
				reportInfo.setSubmitTime(new Date());
				reportInfo.setSampleCode(customerInfoBo.getSampleCode().trim());
				reportInfo.setCustomerId(customerAccount.getId());
				reportInfo.setCustomerPhone(customerInfoBo.getSamplePhone());
				reportInfo.setId(null);
				reportInfo.setSubmitQuestionnaire((byte) 2);
				reportInfoMapper.insertSelective(reportInfo);

				// 保存采样症状数据
				List<ReportSymptom> reportSymptomList = customerInfoBo.getReportSymptomList();
				for (ReportSymptom symptom : reportSymptomList) {
					symptom.setId(null);
					symptom.setReportInfoId(reportInfo.getId());
					reportSymptomMapper.insertSelective(symptom);
				}
				String content = "新增了一个客户,姓名:" + customerInfoBo.getSampleName() + ",编号:"
						+ customerInfoBo.getSampleCode();
				sysLogService.addSysLog(loginAccount, content, new Date());
			} else if (operateType == 2) {// 修改
				if (StringUtils.isBlank(customerInfoBo.getId())) {
					result.setCode(BizCode.PARAM_EMPTY);
					result.setMessage("参数id不可为空");
					return 0;
				}
				if (reportDB != null && reportDB.getId().longValue() != customerInfoBo.getId().longValue()) {
					result.setCode(BizCode.DATA_EXIST);
					result.setMessage("采样者编号【" + customerInfoBo.getSampleCode() + "】已绑定,请核对后重新填写！");
					return 0;
				}
				reportInfo.setId(customerInfoBo.getId());
				reportInfo.setCustomerId(customerAccount.getId());
				reportInfo.setCustomerPhone(customerInfoBo.getSamplePhone());
				reportInfoMapper.updateByPrimaryKeySelective(reportInfo);
				// 删除旧的症状数据
				reportSymptomMapper.deleteByReportInfoId(customerInfoBo.getId());
				// 插入新数据
				List<ReportSymptom> reportSymptomList = customerInfoBo.getReportSymptomList();
				for (ReportSymptom symptom : reportSymptomList) {
					symptom.setId(null);
					symptom.setReportInfoId(reportInfo.getId());
					reportSymptomMapper.insertSelective(symptom);
				}
				String content = "修改了" + customerInfoBo.getSampleName() + "的客户信息";
				sysLogService.addSysLog(loginAccount, content, new Date());
			}
		}
		return 1;
	}

	public CustomerInfoVo getCustomerInfo(Long customerInfoId, Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (customerInfoId == null) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数customerInfoId为空");
			return null;
		}
		ReportInfo reportInfo = reportInfoMapper.selectByPrimaryKey(customerInfoId);
		CustomerInfoVo infoVo = new CustomerInfoVo();
		if (reportInfo != null) {
			BeanUtils.copyProperties(reportInfo, infoVo);
			// 查询症状列表
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("reportInfoId", customerInfoId);
			List<ReportSymptom> symptomList = reportSymptomMapper.selectListByMap(map);
			infoVo.setReportSymptomList(symptomList);
			infoVo.setReportStatusName(ReportStatus.getDescByStatus(reportInfo.getReportStatus()));
			Hospital hospital = hospitalMapper.selectByPrimaryKey(reportInfo.getHospitalId());
			infoVo.setHospitalName(hospital == null ? "" : hospital.getHospitalName());
			if (reportInfo.getSubmitWay() == 1) {
				infoVo.setSubmitWayName("用户提交");
			} else if (reportInfo.getSubmitWay() == 2) {
				infoVo.setSubmitWayName("辅助提交");
			}
		}
		return infoVo;
	}

	public int checkIsHadScan(String scanCode, Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (StringUtils.isBlank(scanCode)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数scanCode为空");
			return 0;
		}
		ReportInfo reportInfo = reportInfoMapper.selectBySampleCode(scanCode.trim());
		if (reportInfo != null) {
			String key = "lewe_scanSampleCode:" + scanCode;
			JedisUtil redis = JedisUtil.getInstance();

			Byte reportStatus = reportInfo.getReportStatus();
			if (reportStatus != null && reportStatus >= ReportStatus.HOSPITAL_SCAN.getValue()) {
				result.setCode(BizCode.DATA_EXIST);
				result.setMessage("编号【" + scanCode + "】已经扫描过");
				return 1;// 已扫描过
			}
			if (reportStatus == null || reportStatus < 2) {
				redis.set(key, "1");
				redis.setExpire(key, 300);
				return 2;// 未扫描过
			}
		} else {
			result.setCode(BizCode.DATA_NOT_FOUND);
			result.setMessage("未查询到编号【" + scanCode + "】对应的客户信息");
			return 3;// 编号无对应客户
		}
		return 3;
	}

	@Transactional
	public JSONObject submitScanCodes(String codeArray, Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (StringUtils.isBlank(codeArray)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("无任何扫描编号,请确认后再操作");
			return null;
		}
		int n = 0;// 统计未提交问卷调查的数量
		String[] arr = codeArray.split("\\,");
		JSONObject json = new JSONObject();
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (String code : arr) {
			if (StringUtils.isBlank(code)) {
				continue;
			}
			ReportInfo reportInfo = reportInfoMapper.selectBySampleCode(code.trim());

			if (reportInfo != null) {

				ReportInfo update = new ReportInfo();
				update.setId(reportInfo.getId());
				update.setHospitalScanTime(new Date());

				if (reportInfo.getHospitalId() == null || (loginAccount.getHospitalId() != null
						&& !loginAccount.getHospitalId().equals(reportInfo.getHospitalId()))) {
					update.setHospitalId(loginAccount.getHospitalId());
				}

				if (reportInfo.getReportStatus() == null 
						|| ReportStatus.USER_BIND.getValue() == reportInfo.getReportStatus()) {
					update.setReportStatus(ReportStatus.HOSPITAL_SCAN.getValue());
				}

				reportInfoMapper.updateByPrimaryKeySelective(update);

				// 是否提交了调查问卷 0:否 1:提交了问卷信息1 2:提交了问卷信息2
				Byte submitQuestionnaire = reportInfo.getSubmitQuestionnaire();
				if (submitQuestionnaire == null || submitQuestionnaire < 2) {
					n++;
				}

				JSONObject info = new JSONObject();
				info.put("id", reportInfo.getId());
				info.put("sampleCode", reportInfo.getSampleCode());
				info.put("sampleName", reportInfo.getSampleName());
				json.put("submitTime", reportInfo.getSubmitTime() == null ? null
						: DateUtil.formatDate(reportInfo.getSubmitTime(), "yyyy-MM-dd"));
				jsonList.add(info);
			}
		}
		json.put("noSubmitCount", n);
		json.put("customerInfos", jsonList);
		result.setData(json);
		result.setMessage("有" + n + "个用户没有提交问卷调查");
		String content = "扫描了检测编号";
		sysLogService.addSysLog(loginAccount, content, new Date());
		return json;
	}

	public JSONObject expressInfoList(String keyword, Integer channelId, Integer pageNo, Integer pageSize,
			Account loginAccount, Object apiResult) {
		JSONObject json = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();

		if (channelId != null) {
			map.put("channelId", channelId);
		}

		List<Long> hospitalIds = this.getUserHostList(loginAccount);
		List<Long> hospitalIdList = null;

		if (StringUtils.isNotBlank(keyword)) {
			hospitalIdList = new ArrayList<Long>();
			keyword = keyword.replaceAll("\\s*", "");
			map.put("keyword", "%" + keyword + "%");

			// 若关键词不为空,则按照机构名查询一下(产品需求快递信息列表的搜索关键字需要查询快递单号和机构名称)
			List<Hospital> hospitalList = hospitalMapper.selectListByMap(map);
			for (Hospital hospital : hospitalList) {
				hospitalIdList.add(hospital.getId());
			}
			map.remove("keyword");
			if (hospitalIdList.size() == 0) {
				hospitalIdList.add(0L);
			}
		}

		// 这里做权限判定
		if (hospitalIds == null) {
			hospitalIds = hospitalIdList;
		} else if (hospitalIds.size() == 0) {
			hospitalIds.add(0L); // 无权限
		} else if (hospitalIdList != null) {
			// 权限交叉计算
			List<Long> tmpList = new ArrayList<Long>();
			for (Long hospitalId : hospitalIds) {
				if (hospitalIdList.contains(hospitalId)) {
					tmpList.add(hospitalId);
				}
			}
			if (tmpList.size() == 0) {
				tmpList.add(0L);
			}
			hospitalIds = tmpList;
		}

		map.put("isDel", 0);
		map.put("hospitalIdList", hospitalIds);

		Integer totalCount = expressInfoMapper.selectCountByMap(map);
		if (totalCount == null || totalCount == 0) {
			json.put("page", null);
			json.put("expressInfoList", null);
			return json;
		}

		Page page = new Page(pageNo, pageSize, totalCount);
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		List<ExpressInfo> list = expressInfoMapper.selectListByMap(map);

		// 收集报表id
		List<Long> reportIdList = new ArrayList<Long>();
		for (ExpressInfo express : list) {
			if (StringUtils.isBlank(express.getReportInfoids())) {
				continue;
			}

			String[] arr = express.getReportInfoids().split("\\,");
			if (arr == null || arr.length == 0) {
				continue;
			}
			for (String id : arr) {
				if (StringUtils.isBlank(id)) {
					continue;
				}
				reportIdList.add(Long.parseLong(id));
			}
		}

		// 打包
		Map<Long, ReportInfo> reportMap = new HashMap<Long, ReportInfo>();
		if (reportIdList.size() > 0) {
			Map<String, Object> reportInfQueryMap = new HashMap<String, Object>();
			reportInfQueryMap.put("hospitalIdList", reportInfQueryMap);
			List<ReportInfo> reportListTmp = reportInfoMapper.selectListByMap(reportInfQueryMap);

			for (ReportInfo varReportInfo : reportListTmp) {
				reportMap.put(varReportInfo.getId(), varReportInfo);
			}
		}

		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		Map<Integer, Channel> channelMap = new HashMap<Integer, Channel>();
		Map<Long, Hospital> hospitalMap = new HashMap<Long, Hospital>();
		Map<Long, Account> accountMap = new HashMap<Long, Account>();

		for (ExpressInfo express : list) {
			JSONObject expressInfo = new JSONObject();
			expressInfo.put("id", express.getId());
			expressInfo.put("expressName", express.getExpressName());// 快递名称
			expressInfo.put("expressCode", express.getExpressCode());// 快递单号
			expressInfo.put("expressTime", DateUtil.formatDate(express.getExpressTime(), "yyyy-MM-dd HH:mm:ss"));// 邮寄时间
			expressInfo.put("createTime", DateUtil.formatDate(express.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));// 创建时间

			// 查询渠道，机构，操作人名称
			if (!channelMap.containsKey(express.getChannelId())) {
				Channel channel = channelMapper.selectByPrimaryKey(express.getChannelId());
				channelMap.put(express.getChannelId(), channel);
			}
			Channel channel = channelMap.get(express.getChannelId());
			expressInfo.put("channelId", express.getChannelId());
			expressInfo.put("channelName", channel == null ? "" : channel.getName());

			// 查询渠道，机构，操作人名称
			if (!hospitalMap.containsKey(express.getHospitalId())) {
				Hospital hospital = hospitalMapper.selectByPrimaryKey(express.getHospitalId());
				hospitalMap.put(express.getHospitalId(), hospital);
			}
			Hospital hospital = hospitalMap.get(express.getHospitalId());
			expressInfo.put("hospitalId", express.getHospitalId());
			expressInfo.put("hospitalName", hospital == null ? "" : hospital.getHospitalName());

			// 查询渠道，机构，操作人名称
			if (!accountMap.containsKey(express.getOperatorId())) {
				Account account = accountMapper.selectByPrimaryKey(express.getOperatorId());
				accountMap.put(express.getOperatorId(), account);
			}
			Account account = accountMap.get(express.getOperatorId());
			expressInfo.put("operatorName", account == null ? "" : account.getName());// 操作人

			// 查询当前快递所包含的客户信息
			String reportInfoids = express.getReportInfoids();
			StringBuilder customerCodes = new StringBuilder();
			StringBuilder customerPhones = new StringBuilder();
			if (StringUtils.isNotBlank(reportInfoids)) {
				String[] arr = reportInfoids.split("\\,");
				if (arr != null && reportInfoids.length() > 0) {
					int n = 1;
					for (String id : arr) {
						if (StringUtils.isBlank(id)) {
							continue;
						}
						if (reportMap.containsKey(Long.parseLong(id))) {
							ReportInfo reportInfo = reportMap.get(Long.parseLong(id));

							if ((n == 1 && arr.length > 1) || n < arr.length) {
								customerCodes.append(reportInfo.getSampleCode()).append(",");
								customerPhones.append(reportInfo.getSamplePhone()).append(",");
							} else {
								customerCodes.append(reportInfo.getSampleCode());
								customerPhones.append(reportInfo.getSamplePhone());
							}
						}
						n++;
					}
				}
			}

			expressInfo.put("customerCodes", customerCodes.toString());// 客户编号(采样者编号)
			expressInfo.put("customerPhones", customerPhones.toString());// 客户手机号(采样者本人手机号)
			jsonList.add(expressInfo);
		}
		json.put("page", page);
		json.put("expressInfoList", jsonList);
		return json;
	}

	/**
	 * 获取某个人的机构权限
	 * 
	 * @param loginAccount
	 * @return 如果是主账号，返回 null 如果是没有权限 返回空
	 */
	public List<Long> getUserHostList(Account loginAccount) {

		if (loginAccount.getAccountType() == AccountType.SUPERADMIN.getValue()) {
			logger.error(JSON.toJSONString(loginAccount));
			return null;
		}

		// 如果不是主账号，需要考虑机构的权限问题 机构组》机构》个人
		List<Long> hospitalIdList = new ArrayList<Long>();

		// 如果机构组不为空，需要读取机构组下面的机构
		if (loginAccount.getHospitalGroupId() != null) {
			// 读取机构组的信息
			HospitalGroup hospitalGroup = hospitalGroupMapper.selectByPrimaryKey(loginAccount.getHospitalGroupId());

			// 解析机构组信息
			if (hospitalGroup != null && hospitalGroup.getHospitalIds() != null
					&& !StringUtils.isBlank(hospitalGroup.getHospitalIds())) {
				String[] hospitalList = hospitalGroup.getHospitalIds().split(",");
				for (int hospitalIndex = 0; hospitalIndex < hospitalList.length; hospitalIndex++) {
					hospitalIdList.add(Long.parseLong(hospitalList[hospitalIndex]));
				}
			}
		}

		// 加上自己的机构权限
		if (StringUtils.isNotBlank(loginAccount.getHospitalId())) {
			hospitalIdList.add(loginAccount.getHospitalId());
		}
		logger.error(JSON.toJSONString(hospitalIdList));

		return hospitalIdList;
	}

	/**
	 * 获取某个人的机构权限
	 * 
	 * @param loginAccount
	 * @return 如果是主账号，返回 null 如果是没有权限 返回空
	 */
	public List<Integer> getUserChannelList(Account loginAccount) {

		if (loginAccount.getAccountType() == AccountType.SUPERADMIN.getValue()) {
			logger.error(JSON.toJSONString(loginAccount));
			return null;
		}

		// 如果不是主账号，需要考虑机构的权限问题 机构组》机构》个人
		List<Long> hospitalIdList = new ArrayList<Long>();

		// 如果机构组不为空，需要读取机构组下面的机构
		if (loginAccount.getHospitalGroupId() != null) {
			// 读取机构组的信息
			HospitalGroup hospitalGroup = hospitalGroupMapper.selectByPrimaryKey(loginAccount.getHospitalGroupId());

			// 解析机构组信息
			if (hospitalGroup != null && hospitalGroup.getHospitalIds() != null
					&& !StringUtils.isBlank(hospitalGroup.getHospitalIds())) {
				String[] hospitalList = hospitalGroup.getHospitalIds().split(",");
				for (int hospitalIndex = 0; hospitalIndex < hospitalList.length; hospitalIndex++) {
					hospitalIdList.add(Long.parseLong(hospitalList[hospitalIndex]));
				}
			}
		}

		// 加上自己的机构权限
		if (StringUtils.isNotBlank(loginAccount.getHospitalId())) {
			hospitalIdList.add(loginAccount.getHospitalId());
		}

		if(hospitalIdList.isEmpty()){
			return  new ArrayList<Integer>();
		}

		List<Integer> channelIdList = new ArrayList<Integer>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("idList", hospitalIdList);
		List<Hospital> hosList = hospitalMapper.selectListByMap(map);
		for (Hospital var : hosList) {
			if(var.getChannelId() != null){
				channelIdList.add(var.getChannelId());
			}
		}

		return channelIdList;
	}


	public int addExpressInfo(String expressName, String expressCode, String customerInfoIds, Integer channelId,
			Long hospitalId, Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (StringUtils.isBlank(expressName)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入快递名称");
			return 0;
		}
		if (StringUtils.isBlank(expressCode)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入快递单号");
			return 0;
		}
		if (StringUtils.isBlank(customerInfoIds)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择客户信息");
			return 0;
		}
		// 若未传参,则默认当前账号的门店id和渠道id
		if (hospitalId == null) {
			hospitalId = loginAccount.getHospitalId();
		}
		if (channelId == null) {
			Hospital hospital = hospitalMapper.selectByPrimaryKey(hospitalId);
			if (hospital != null) {
				channelId = hospital.getChannelId();
			}
		}
		ExpressInfo expressInfo = new ExpressInfo();
		expressInfo.setChannelId(channelId);
		expressInfo.setHospitalId(hospitalId);
		expressInfo.setCreateTime(new Date());
		expressInfo.setExpressName(expressName);
		expressInfo.setExpressCode(expressCode);
		expressInfo.setExpressTime(new Date());
		expressInfo.setOperatorId(loginAccount.getId());
		expressInfo.setReportInfoids(customerInfoIds);
		expressInfoMapper.insertSelective(expressInfo);
		// 然后将报告信息主表冗余的3个字段也赋值(快递名称,快递单号,及单号绑定时间)
		String[] arr = customerInfoIds.split("\\,");
		if (arr != null && customerInfoIds.length() > 0) {
			for (String id : arr) {
				ReportInfo update = new ReportInfo();
				update.setId(Long.valueOf(id));
				update.setExpressName(expressName);
				update.setExpressCode(expressCode);
				update.setReportStatus(ReportStatus.EXPRESS_CODE.getValue());
				update.setExpressTime(new Date());
				reportInfoMapper.updateByPrimaryKeySelective(update);
			}
		}
		String content = "新增了一条快递信息,快递单号:" + expressCode;
		sysLogService.addSysLog(loginAccount, content, new Date());
		return 1;
	}

	public JSONObject expressInfoDetail(Long expressId, Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (StringUtils.isBlank(expressId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数expressId为空");
			return null;
		}
		JSONObject json = new JSONObject();
		ExpressInfo express = expressInfoMapper.selectByPrimaryKey(expressId);
		if (express != null) {
			json.put("id", express.getId());
			json.put("expressName", express.getExpressName());// 快递名称
			json.put("expressCode", express.getExpressCode());// 快递单号
			Channel channel = channelMapper.selectByPrimaryKey(express.getChannelId());
			json.put("channelId", express.getChannelId());
			json.put("channelName", channel == null ? "" : channel.getName());
			Hospital hospital = hospitalMapper.selectByPrimaryKey(express.getHospitalId());
			json.put("hospitalId", express.getHospitalId());
			json.put("hospitalName", hospital == null ? "" : hospital.getHospitalName());

			// 查询当前快递所包含的客户信息列表
			List<JSONObject> jsonList = new ArrayList<JSONObject>();
			String reportInfoids = express.getReportInfoids();
			if (StringUtils.isNotBlank(reportInfoids)) {
				String[] arr = reportInfoids.split("\\,");
				if (arr != null && reportInfoids.length() > 0) {
					for (String reportId : arr) {
						ReportInfo reportInfo = reportInfoMapper.selectByPrimaryKey(Long.valueOf(reportId));
						if (reportInfo != null) {
							JSONObject jsonInfo = new JSONObject();
							jsonInfo.put("customerName", reportInfo.getSampleName());// 客户姓名(采样者姓名)
							jsonInfo.put("customerCode", reportInfo.getSampleCode());// 客户编号(采样者编号)
							jsonList.add(jsonInfo);
						}
					}
				}
			}
			json.put("customerList", jsonList);
		}
		return json;
	}

	public List<JSONObject> customerSelectList(Account loginAccount, Object apiResult) {
		Map<String, Object> map = new HashMap<String, Object>();

		if (loginAccount.getAccountType() != AccountType.SUPERADMIN.getValue()) {
			map.put("hospitalId", loginAccount.getHospitalId());
		}

		map.put("reportStatus", ReportStatus.HOSPITAL_SCAN.getValue());
		List<ReportInfo> list = reportInfoMapper.selectListByMap(map);
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (ReportInfo reportInfo : list) {
			JSONObject json = new JSONObject();
			json.put("id", reportInfo.getId());
			json.put("sampleCode", reportInfo.getSampleCode());
			json.put("sampleName", reportInfo.getSampleName());
			json.put("submitTime", reportInfo.getSubmitTime() == null ? null
					: DateUtil.formatDate(reportInfo.getSubmitTime(), "yyyy-MM-dd"));
			jsonList.add(json);
		}
		return jsonList;
	}

}
