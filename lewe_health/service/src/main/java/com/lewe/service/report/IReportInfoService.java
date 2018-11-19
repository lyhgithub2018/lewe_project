package com.lewe.service.report;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.sys.Account;

public interface IReportInfoService {

	/**
	 * B端报告统计列表查询
	 * @param reportInfoQuery
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	JSONObject reportCountList(String reportInfoQuery, Account loginAccount, Object apiResult);

	/**
	 * B端用量统计列表
	 * @param usedCountQuery
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	JSONObject usedCountList(String usedCountQuery, Account loginAccount, Object apiResult);

	/**
	 * 导出报告统计列表(生成Excel文件)
	 * @param reportInfoQuery
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	HSSFWorkbook exportReportCountList(String reportInfoQuery, Account loginAccount, Object apiResult);

	/**
	 * 导出用量统计列表(生成Excel文件)
	 * @param reportInfoQuery
	 * @param loginAccount
	 * @param apiResult
	 * @return
	 */
	HSSFWorkbook exportUsedCountList(String usedCountQuery, Account loginAccount, Object apiResult);

}
