package com.lewe.serviceImpl.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.check.CheckItem;
import com.lewe.bean.report.ReportIllness;
import com.lewe.bean.report.ReportInfo;
import com.lewe.bean.report.vo.UsedCountInfo;
import com.lewe.bean.sys.Account;
import com.lewe.bean.sys.Channel;
import com.lewe.bean.sys.SysFile;
import com.lewe.dao.check.CheckItemMapper;
import com.lewe.dao.report.ReportIllnessMapper;
import com.lewe.dao.report.ReportInfoMapper;
import com.lewe.dao.report.UsedCountMapper;
import com.lewe.dao.sys.AccountMapper;
import com.lewe.dao.sys.ChannelMapper;
import com.lewe.dao.sys.SysFileMapper;
import com.lewe.service.report.IReportInfoService;
import com.lewe.serviceImpl.report.bo.ReportCountExcel;
import com.lewe.serviceImpl.report.bo.UsedCountExcel;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.BizCode;
import com.lewe.util.common.DateUtil;
import com.lewe.util.common.Page;
import com.lewe.util.common.StringUtils;
import com.lewe.util.common.constants.AccountType;
import com.lewe.util.common.constants.ReportStatus;
import com.lewe.util.common.excel.ExcelUtil;

@Service("reportInfoService")
public class ReportInfoServiceImpl implements IReportInfoService{
	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private CheckItemMapper checkItemMapper;
	@Autowired
	private ReportInfoMapper reportInfoMapper;
	@Autowired
	private ReportIllnessMapper reportIllnessMapper;
	@Autowired
	private SysFileMapper sysFileMapper;

	public JSONObject reportCountList(String reportInfoQuery, Account loginAccount, Object apiResult) {
		JSONObject json = new JSONObject();
		Map<String, Object> paramMap = null;
		if(StringUtils.isNotBlank(reportInfoQuery)) {
			JSONObject parseObject = JSONObject.parseObject(reportInfoQuery);
			String str = parseObject.getString("reportInfoQuery");
			paramMap = jsonToMap(str);
		}else {
			paramMap = new HashMap<String, Object>();
		}
		paramMap.put("reportStatus", ReportStatus.RESULT_CREATE.getValue());
		if(loginAccount!=null && loginAccount.getAccountType()!=AccountType.SUPERADMIN.getValue()) {
			paramMap.put("hospitalId", loginAccount.getHospitalId());
			//若当前账号归属于某个门店组,则按门店组id查询
			/*if(loginAccount.getHospitalGroupId()!=null) {
				paramMap.put("hospitalGroupId", loginAccount.getHospitalGroupId());
			}*/
		}
		String keyword = "";
		Object obj = paramMap.get("keyword");
		if(StringUtils.isNotBlank(obj)) {
			keyword = obj.toString().replaceAll("\\s*", ""); 
			paramMap.put("keyword", "%"+keyword+"%");
		}else {
			paramMap.put("keyword", null);
		}
		/*Object begin = paramMap.get("beginDate");
		Object end = paramMap.get("endDate");
		if(StringUtils.isBlank(begin)) {
			paramMap.put("beginDate", null);
		}
		if(StringUtils.isBlank(end)) {
			paramMap.put("endDate", null);
		}*/
		//判断是否有illnessId和illnessDegree这两个参数查询
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(paramMap.get("illnessId"))) {
			map.put("illnessId", paramMap.get("illnessId"));
		}
		if(StringUtils.isNotBlank(paramMap.get("illnessDegree"))) {
			map.put("illnessDegree", paramMap.get("illnessDegree"));
		}
		if(map.size()>0) {
			List<ReportIllness> illnessList = reportIllnessMapper.selectListByMap(map);
			if(illnessList!=null && illnessList.size()>0) {
				List<Long> reportInfoIdList = new ArrayList<Long>();
				for (ReportIllness reportIllness : illnessList) {
					reportInfoIdList.add(reportIllness.getReportInfoId());
				}
				map.put("reportInfoIdList", reportInfoIdList);
			}
		}
		Integer totalCount = reportInfoMapper.selectCountByMap(paramMap);
		if(totalCount==null||totalCount==0) {
			json.put("page", null);
			json.put("reportCountList", null);
			return json;
		}
		int pageNo = 1;
		int pageSize = 10;
		if(StringUtils.isNotBlank(paramMap.get("pageNo"))) {
			pageNo = Integer.valueOf(paramMap.get("pageNo").toString());
		}
		if(StringUtils.isNotBlank(paramMap.get("pageSize"))) {
			pageSize = Integer.valueOf(paramMap.get("pageSize").toString());
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		paramMap.put("startIndex", page.getStartIndex());
		List<ReportInfo> list = reportInfoMapper.selectListByMap(paramMap);
		List<JSONObject> reportCountList = new ArrayList<JSONObject>();
		for (ReportInfo reportInfo : list) {
			JSONObject sampleInfo = new JSONObject();
			sampleInfo.put("id", reportInfo.getId());
			sampleInfo.put("sampleCode", reportInfo.getSampleCode());//采样编号
			sampleInfo.put("sampleName", reportInfo.getSampleName());//采样者姓名
			sampleInfo.put("samplePhone", reportInfo.getSamplePhone());//采样者手机号
			if(reportInfo.getSampleSex()!=null) {//采样者性别
				if(reportInfo.getSampleSex()==1) {
					sampleInfo.put("sampleSex", "男");
				}else if(reportInfo.getSampleSex()==2) {
					sampleInfo.put("sampleSex", "女");
				}
			}
			sampleInfo.put("sampleAge", reportInfo.getSampleAge());//采样者年龄
			Account account = accountMapper.selectByPrimaryKey(reportInfo.getCheckAccountId());
			sampleInfo.put("checkerName", account==null?"":account.getName());//检测员
			sampleInfo.put("sysReportCode", reportInfo.getSysReportCode());//报告编号
			sampleInfo.put("reportCreateTime", reportInfo.getReportCreateTime()==null?null:DateUtil.formatDate(reportInfo.getReportCreateTime(), "yyyy-MM-dd"));//出报告时间
			//查询检测项目的名称
			CheckItem checkItem = checkItemMapper.selectByPrimaryKey(reportInfo.getCheckItemId());
			sampleInfo.put("checkItemName", checkItem==null?null:"【"+checkItem.getName()+"】");//检测项目名称
			reportCountList.add(sampleInfo);
		}
		json.put("page", page);
		json.put("reportCountList", reportCountList);
		return json;
	}
	/**
	 * 将json格式的参数转成map
	 * @param json
	 * @return
	 */
	private Map<String, Object> jsonToMap(String json) {
		if(StringUtils.isNotBlank(json)) {
			JSONObject jsonObject = JSONObject.parseObject(json);
			Map<String, Object> valueMap = new HashMap<String, Object>();
			valueMap.putAll(jsonObject);
			return valueMap;
		}else {
			return new HashMap<String, Object>();
		}
	}
	@Autowired
	private ChannelMapper channelMapper;
	@Autowired
	private UsedCountMapper usedCountMapper;
	public JSONObject usedCountList(String usedCountQuery, Account loginAccount, Object apiResult) {
		JSONObject json = new JSONObject();
		Map<String, Object> paramMap = null;
		if(StringUtils.isNotBlank(usedCountQuery)) {
			JSONObject parseObject = JSONObject.parseObject(usedCountQuery);
			String str = parseObject.getString("usedCountQuery");
			paramMap = jsonToMap(str);
		}else {
			paramMap = new HashMap<String, Object>();
		}
		if(loginAccount!=null && loginAccount.getAccountType()!=AccountType.SUPERADMIN.getValue()) {
			paramMap.put("hospitalId", loginAccount.getHospitalId());
			//若当前账号归属于某个门店组,则按门店组id查询
			/*if(loginAccount.getHospitalGroupId()!=null) {
				paramMap.put("hospitalGroupId", loginAccount.getHospitalGroupId());
			}*/
		}
		//查询日期
		Object queryDate = paramMap.get("queryDate");
		paramMap.put("queryDate", queryDate);
		if(StringUtils.isBlank(queryDate)) {
			//默认查询当天的
			paramMap.put("queryDate", DateUtil.getCurDate("yyyy-MM-dd"));
		}
		Integer totalCount = usedCountMapper.selectUsedCountByMap(paramMap);
		if(totalCount==null||totalCount==0) {
			json.put("page", null);
			json.put("usedCountList", null);
			return json;
		}
		int pageNo = 1;
		int pageSize = 10;
		if(StringUtils.isNotBlank(paramMap.get("pageNo"))) {
			pageNo = Integer.valueOf(paramMap.get("pageNo").toString());
		}
		if(StringUtils.isNotBlank(paramMap.get("pageSize"))) {
			pageSize = Integer.valueOf(paramMap.get("pageSize").toString());
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		paramMap.put("startIndex", page.getStartIndex());
		List<UsedCountInfo> list = usedCountMapper.selectUsedCountListByMap(paramMap);
		for (UsedCountInfo usedCount : list) {
			//渠道名称
			Channel channel = channelMapper.selectByPrimaryKey(usedCount.getChannelId());
			usedCount.setChannelName(channel==null?null:channel.getName());
			//检测项目名称
			CheckItem checkItem = checkItemMapper.selectByPrimaryKey(usedCount.getCheckItemId());
			usedCount.setCheckItemName(checkItem==null?null:checkItem.getName());
			
			//门店确认数(channel_id,hospital_id,check_item_id,hospital_scan_time四个字段查询)
			paramMap.put("hospitalScanTime", queryDate);
			usedCount.setHospitalConfirmCount(reportInfoMapper.selectCountByMap(paramMap));
			paramMap.remove("hospitalScanTime");
			//物流数(channel_id,hospital_id,check_item_id,expressTime四个字段查询)
			paramMap.put("expressTime", queryDate);//快递单号绑定时间
			usedCount.setExpressCount(reportInfoMapper.selectCountByMap(paramMap));
			paramMap.remove("expressTime");
			
			//检测中数量(channel_id,hospital_id,check_item_id,hospitalScanTime,checkStatus5个字段查询)
			paramMap.put("hospitalScanTime", queryDate);
			paramMap.put("checkStatus", 0);//检测状态 0：待检测 1：已检测
			usedCount.setCheckIngCount(reportInfoMapper.selectCountByMap(paramMap));
			paramMap.remove("hospitalScanTime");
			
			//检测完成数量(channel_id,hospital_id,check_item_id,checkTime,checkStatus5个字段查询)
			paramMap.put("checkTime", queryDate);
			paramMap.put("checkStatus", 1);
			usedCount.setCheckFinshCount(reportInfoMapper.selectCountByMap(paramMap));
			usedCount.setQueryDate(queryDate==null?null:queryDate.toString());
		}
		json.put("page", page);
		json.put("usedCountList", list);
		return json;
	}
	public HSSFWorkbook exportReportCountList(String reportInfoQuery, Account loginAccount, Object apiResult) {
		Map<String, Object> paramMap = null;
		if(StringUtils.isNotBlank(reportInfoQuery)) {
			JSONObject parseObject = JSONObject.parseObject(reportInfoQuery);
			String str = parseObject.getString("reportInfoQuery");
			paramMap = jsonToMap(str);
		}else {
			paramMap = new HashMap<String, Object>();
		}
		paramMap.put("reportStatus", ReportStatus.RESULT_CREATE.getValue());
		if(loginAccount!=null && loginAccount.getAccountType()!=AccountType.SUPERADMIN.getValue()) {
			paramMap.put("hospitalId", loginAccount.getHospitalId());
			//若当前账号归属于某个门店组,则按门店组id查询
			/*if(loginAccount.getHospitalGroupId()!=null) {
				paramMap.put("hospitalGroupId", loginAccount.getHospitalGroupId());
			}*/
		}
		String keyword = "";
		Object obj = paramMap.get("keyword");
		if(StringUtils.isNotBlank(obj)) {
			keyword = obj.toString().replaceAll("\\s*", ""); 
			paramMap.put("keyword", "%"+keyword+"%");
		}else {
			paramMap.put("keyword", null);
		}
		//判断是否有illnessId和illnessDegree这两个参数查询
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(paramMap.get("illnessId"))) {
			map.put("illnessId", paramMap.get("illnessId"));
		}
		if(StringUtils.isNotBlank(paramMap.get("illnessDegree"))) {
			map.put("illnessDegree", paramMap.get("illnessDegree"));
		}
		if(map.size()>0) {
			List<ReportIllness> illnessList = reportIllnessMapper.selectListByMap(map);
			if(illnessList!=null && illnessList.size()>0) {
				List<Long> reportInfoIdList = new ArrayList<Long>();
				for (ReportIllness reportIllness : illnessList) {
					reportInfoIdList.add(reportIllness.getReportInfoId());
				}
				map.put("reportInfoIdList", reportInfoIdList);
			}
		}
		List<ReportInfo> list = reportInfoMapper.selectListByMap(paramMap);
		List<ReportCountExcel> dataList = new ArrayList<ReportCountExcel>();
		for (ReportInfo reportInfo : list) {
			ReportCountExcel report = new ReportCountExcel();
			//查询检测项目的名称
			CheckItem checkItem = checkItemMapper.selectByPrimaryKey(reportInfo.getCheckItemId());
			report.setItemName(checkItem==null?null:checkItem.getName());
			report.setName(reportInfo.getSampleName());
			if(reportInfo.getSampleSex()==1) {
				report.setSex("男");
			}else if(reportInfo.getSampleSex()==2) {
				report.setSex("女");
			}
			report.setAge(reportInfo.getSampleAge());
			report.setPhone(reportInfo.getSamplePhone());
			report.setCode(reportInfo.getSampleCode());
			Account account = accountMapper.selectByPrimaryKey(reportInfo.getCheckAccountId());
			report.setCheckerName(account==null?"":account.getName());
			report.setReportTime(reportInfo.getReportCreateTime()==null?null:DateUtil.formatDate(reportInfo.getReportCreateTime(), "yyyy-MM-dd"));
			report.setReportCode(reportInfo.getSysReportCode());
			dataList.add(report);
		}
		// 定义excel列名称字段名
        String[] keyFields = {"itemName","name","sex","age","phone","code","checkerName","reportTime","reportCode"};
        String[] valueFields = {"检测项目","姓名","性别","年龄","电话","检测编号","检测员","报告时间","报告编号"};
		// 生成Excel文件
        HSSFWorkbook book = ExcelUtil.createReportCountExcel("检测报告统计",keyFields,valueFields,dataList);
		return book;
	}
	public HSSFWorkbook exportUsedCountList(String usedCountQuery, Account loginAccount, Object apiResult) {
		Map<String, Object> paramMap = null;
		if(StringUtils.isNotBlank(usedCountQuery)) {
			JSONObject parseObject = JSONObject.parseObject(usedCountQuery);
			String str = parseObject.getString("usedCountQuery");
			paramMap = jsonToMap(str);
		}else {
			paramMap = new HashMap<String, Object>();
		}
		if(loginAccount!=null && loginAccount.getAccountType()!=AccountType.SUPERADMIN.getValue()) {
			paramMap.put("hospitalId", loginAccount.getHospitalId());
			//若当前账号归属于某个门店组,则按门店组id查询
			/*if(loginAccount.getHospitalGroupId()!=null) {
				paramMap.put("hospitalGroupId", loginAccount.getHospitalGroupId());
			}*/
		}
		//查询日期
		Object queryDate = paramMap.get("queryDate");
		paramMap.put("queryDate", queryDate);
		if(StringUtils.isBlank(queryDate)) {
			//默认查询当天的
			paramMap.put("queryDate", DateUtil.getCurDate("yyyy-MM-dd"));
		}
		List<UsedCountExcel> dataList = new ArrayList<UsedCountExcel>();
		List<UsedCountInfo> list = usedCountMapper.selectUsedCountListByMap(paramMap);
		for (UsedCountInfo usedCount : list) {
			UsedCountExcel usedExcel = new UsedCountExcel();
			//渠道名称
			Channel channel = channelMapper.selectByPrimaryKey(usedCount.getChannelId());
			usedExcel.setChannelName(channel==null?null:channel.getName());
			//检测项目名称
			CheckItem checkItem = checkItemMapper.selectByPrimaryKey(usedCount.getCheckItemId());
			usedExcel.setCheckItemName(checkItem==null?null:checkItem.getName());
			usedExcel.setHospitalName(usedCount.getHospitalName());
			usedExcel.setAreaCodeName(usedCount.getAreaCodeName());
			//门店确认数
			paramMap.put("hospitalScanTime", queryDate);
			usedExcel.setHospitalConfirmCount(reportInfoMapper.selectCountByMap(paramMap));
			paramMap.remove("hospitalScanTime");
			
			//物流数
			paramMap.put("expressTime", queryDate);//快递单号绑定时间
			usedExcel.setExpressCount(reportInfoMapper.selectCountByMap(paramMap));
			paramMap.remove("expressTime");
			
			//检测中数量
			paramMap.put("hospitalScanTime", queryDate);
			paramMap.put("checkStatus", 0);//检测状态 0：待检测 1：已检测
			usedExcel.setCheckIngCount(reportInfoMapper.selectCountByMap(paramMap));
			paramMap.remove("hospitalScanTime");
			
			//检测完成数量
			paramMap.put("checkTime", queryDate);
			paramMap.put("checkStatus", 1);
			usedExcel.setCheckFinshCount(reportInfoMapper.selectCountByMap(paramMap));
			usedExcel.setQueryDate(queryDate==null?DateUtil.getCurDate("yyyy-MM-dd"):queryDate.toString());
			dataList.add(usedExcel);
		}
		// 定义excel列名称字段名
        String[] keyFields = {"channelName","hospitalName","areaCodeName","queryDate","checkItemName","hospitalConfirmCount","expressCount","checkIngCount","checkFinshCount"};
        String[] valueFields = {"渠道名称","机构名称","地区","时间","项目名称","门店确认","物流数","检测中","检测完成"};
		// 生成Excel文件
        HSSFWorkbook book = ExcelUtil.createUsedCountExcel("用量统计报表",keyFields,valueFields,dataList);
		return book;
	}
	public JSONObject previewReport(Long reportInfoId, Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult)apiResult;
		if(StringUtils.isBlank(reportInfoId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数reportInfoId为空");
			return null;
		}
		List<String> urlList = new ArrayList<String>();
		ReportInfo reportInfo = reportInfoMapper.selectByPrimaryKey(reportInfoId);
		if(reportInfo!=null) {
			String reportPictureIds = reportInfo.getReportPictureIds();
			if(reportPictureIds!=null) {
				String[] arr = reportPictureIds.split("\\,");
				if(arr!=null && arr.length>0) {
					for (String id : arr) {
						SysFile sysFile = sysFileMapper.selectByPrimaryKey(Long.valueOf(id));
						if(sysFile!=null) {
							urlList.add(sysFile.getUrl());
						}
					}
				}
			}
		}
		JSONObject json = new JSONObject();
		json.put("urlList", urlList);
		return json;
	}
}
