package com.lewe.service.sys;

import com.alibaba.fastjson.JSONObject;

/**
 * 疾病和症状相关的service
 * @author 小辉
 *
 */
public interface IillnessService {
	/**
	 * 保存疾病
	 * @param name
	 * @param accountId
	 * @param apiResult
	 * @return
	 */
	int saveIllness(String illnessId,String name, Long accountId, Object apiResult);
	/**
	 * 删除疾病
	 * @param illnessId
	 * @return
	 */
	int delIllness(Integer illnessId,Object apiResult);
	/**
	 * 查询疾病列表
	 * @param pageNo
	 * @param pageSize
	 * @param apiResult
	 * @return
	 */
	JSONObject getIllnessList(Integer pageNo, Integer pageSize, Object apiResult);

	/**
	 * 保存症状
	 * @param name
	 * @param accountId
	 * @param apiResult
	 * @return
	 */
	int saveSymptom(String symptomId,String name, Long accountId, Object apiResult);
	/**
	 * 删除症状
	 * @param symptomId
	 * @param apiResult
	 * @return
	 */
	int delSymptom(String symptomId, Object apiResult);
	/**
	 * 症状列表
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	JSONObject getSymptomList(Integer pageNo, Integer pageSize);

}
