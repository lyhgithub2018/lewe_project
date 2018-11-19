package com.lewe.serviceImpl.sys;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.sys.Illness;
import com.lewe.bean.sys.Symptom;
import com.lewe.dao.sys.IllnessMapper;
import com.lewe.dao.sys.SymptomMapper;
import com.lewe.service.sys.IillnessService;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.BizCode;
import com.lewe.util.common.Page;
import com.lewe.util.common.StringUtils;

@Service("iillnessService")
public class IllnessServiceImpl implements IillnessService{

	@Autowired
	private IllnessMapper illnessMapper;
	@Autowired
	private SymptomMapper symptomMapper;
	@Transactional
	public int saveIllness(String illnessId,String name, Long accountId, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if(StringUtils.isBlank(name)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入疾病名称");
			return 0;
		}
		Illness illness = new Illness();
		illness.setName(name);
		if(StringUtils.isBlank(illnessId)) {//新增
			illness.setCreatorId(accountId);
			illness.setCreateTime(new Date());
			illnessMapper.insertSelective(illness);
		}else {//修改
			illness.setId(Integer.valueOf(illnessId));
			illness.setUpdateTime(new Date());
			illnessMapper.updateByPrimaryKeySelective(illness);
		}
		return 1;
	}
	@Transactional
	public int delIllness(Integer illnessId, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if(illnessId==null) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择要删除的疾病");
			return 0;
		}
		Illness illness = new Illness();
		illness.setId(illnessId);
		illness.setUpdateTime(new Date());
		illness.setIsDel((byte)1);
		illnessMapper.updateByPrimaryKeySelective(illness);
		return 1;
	}
	public JSONObject getIllnessList(Integer pageNo, Integer pageSize, Object apiResult) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("isDel", 0);
		Integer totalCount = illnessMapper.selectCountByMap(map);
		JSONObject json = new JSONObject();
		if(totalCount==null||totalCount==0) {
			json.put("page", null);
			json.put("illnessList", null);
			return json;
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		map.put("orderById", "yes");//传该参数默认按照id降序
		List<Illness> list = illnessMapper.selectListByMap(map);
		json.put("page", page);
		json.put("illnessList", list);
		return json;
	}
	
	@Transactional
	public int saveSymptom(String symptomId,String name, Long accountId, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if(StringUtils.isBlank(name)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入症状名称");
			return 0;
		}
		Symptom symptom = new Symptom();
		symptom.setName(name);
		if(StringUtils.isBlank(symptomId)) {//新增
			symptom.setCreatorId(accountId);
			symptom.setCreateTime(new Date());
			symptomMapper.insertSelective(symptom);
		}else {//修改
			symptom.setId(Integer.valueOf(symptomId));
			symptom.setUpdateTime(new Date());
			symptomMapper.updateByPrimaryKeySelective(symptom);
		}
		return 1;
	}
	@Transactional
	public int delSymptom(String symptomId, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if(StringUtils.isBlank(symptomId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择要删除的症状");
			return 0;
		}
		Symptom symptom = new Symptom();
		symptom.setId(Integer.valueOf(symptomId));
		symptom.setUpdateTime(new Date());
		symptom.setIsDel((byte)1);
		symptomMapper.updateByPrimaryKeySelective(symptom);
		return 1;
	}
	public JSONObject getSymptomList(Integer pageNo, Integer pageSize) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("isDel", 0);
		Integer totalCount = symptomMapper.selectCountByMap(map);
		JSONObject json = new JSONObject();
		if(totalCount==null||totalCount==0) {
			json.put("page", null);
			json.put("symptomList", null);
			return json;
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		map.put("orderById", "yes");//传该参数默认按照id降序
		List<Symptom> list = symptomMapper.selectListByMap(map);
		json.put("page", page);
		json.put("symptomList", list);
		return json;
	}
	
}
