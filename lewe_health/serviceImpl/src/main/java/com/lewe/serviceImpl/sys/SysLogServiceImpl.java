package com.lewe.serviceImpl.sys;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.sys.Account;
import com.lewe.bean.sys.SysLog;
import com.lewe.dao.sys.SysLogMapper;
import com.lewe.service.sys.ISysLogService;
import com.lewe.util.common.DateUtil;
import com.lewe.util.common.Page;
import com.lewe.util.common.StringUtils;

@Service("sysLogService")
public class SysLogServiceImpl implements ISysLogService{
	@Autowired
	private SysLogMapper sysLogMapper;

	@Transactional
	public void addSysLog(Account loginAccount, String content, Date operateTime) {
		SysLog sysLog = new SysLog();
		sysLog.setAccountId(loginAccount.getId());
		sysLog.setAccountName(loginAccount.getName());
		sysLog.setAccountPhone(loginAccount.getAccount());
		sysLog.setContent(content);
		sysLog.setOperateTime(operateTime);
		sysLog.setCreateTime(new Date());
		sysLogMapper.insertSelective(sysLog);
	}

	public JSONObject getLogList(String keyword, String operateDate, Integer pageNo, Integer pageSize,Account loginAccount, Object ApiResult) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(StringUtils.isNotBlank(keyword)) {
			keyword = keyword.replaceAll("\\s*", ""); 
			map.put("keyword", "%"+keyword+"%");
		}else {
			map.put("keyword", null);
		}
		Integer totalCount = sysLogMapper.selectCountByMap(map);
		JSONObject json = new JSONObject();
		if(totalCount==null||totalCount==0) {
			json.put("page", null);
			json.put("logList", null);
			return json;
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		List<JSONObject> logList = new ArrayList<JSONObject>();
		List<SysLog> list = sysLogMapper.selectListByMap(map);
		for (SysLog sysLog : list) {
			JSONObject log = new JSONObject();
			log.put("id", sysLog.getId());
			log.put("account", sysLog.getAccountPhone());
			log.put("accountName", sysLog.getAccountName());
			log.put("operateContent", sysLog.getContent());
			log.put("operateTime", DateUtil.formatDate(sysLog.getOperateTime(), "yyyy-MM-dd HH:mm:ss"));
			logList.add(log);
		}
		json.put("page", page);
		json.put("logList", logList);
		return json;
	}
}
