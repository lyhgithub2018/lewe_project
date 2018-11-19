package com.lewe.web.controller;



import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.sys.Account;
import com.lewe.service.report.IReportInfoService;
import com.lewe.util.common.ApiResult;
import com.lewe.web.controller.common.BaseController;

@Controller
@RequestMapping("/dataCount/")
public class DataCountController extends BaseController{
	@Autowired
	private IReportInfoService reportInfoService;
	/**
	 * 报告统计列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping("reportCountList")
	public ApiResult reportCountList(HttpServletRequest request,String reportInfoQuery,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			JSONObject json = reportInfoService.reportCountList(reportInfoQuery,loginAccount,result);
			result.setData(json);
		}
		return result;
	}
	/**
	 * 预览报告
	 * @return
	 */
	@ResponseBody
	@RequestMapping("previewReport")
	public ApiResult previewReport(HttpServletRequest request,Long reportInfoId,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			JSONObject json = reportInfoService.previewReport(reportInfoId,loginAccount,result);
			result.setData(json);
		}
		return result;
	}
	/**
	 * 导出报告统计列表
	 * @param request
	 * @param response
	 */
	@RequestMapping("exportReportCountList")
	public void exportReportCountList(HttpServletRequest request,String reportInfoQuery,HttpServletResponse response) {
		try {
			ApiResult result = new ApiResult();
			Account loginAccount = getSessionAccount(request,result);
			if(loginAccount!=null) {
				HSSFWorkbook excel = reportInfoService.exportReportCountList(reportInfoQuery,loginAccount,result);
				if(excel!=null) {
					response.setHeader("content-disposition", "attachment;filename="+ new String((excel.getSheetName(0)).getBytes("gbk"), "iso8859-1") + ".xls");
					response.setContentType("multipart/form-data");
					OutputStream ouputStream = response.getOutputStream();
					excel.write(ouputStream);
					ouputStream.flush();
					ouputStream.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 用量统计列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping("usedCountList")
	public ApiResult usedCountList(HttpServletRequest request,String usedCountQuery,HttpServletResponse response) {
		ApiResult result = new ApiResult();
		Account loginAccount = getSessionAccount(request,result);
		if(loginAccount!=null) {
			JSONObject json = reportInfoService.usedCountList(usedCountQuery,loginAccount,result);
			result.setData(json);
		}
		return result;
	}
	
	/**
	 * 导出用量统计列表
	 * @param request
	 * @param response
	 */
	@RequestMapping("exportUsedCountList")
	public void exportUsedCountList(HttpServletRequest request,String reportInfoQuery,HttpServletResponse response) {
		try {
			ApiResult result = new ApiResult();
			Account loginAccount = getSessionAccount(request,result);
			if(loginAccount!=null) {
				HSSFWorkbook excel = reportInfoService.exportUsedCountList(reportInfoQuery,loginAccount,result);
				if(excel!=null) {
					response.setHeader("content-disposition", "attachment;filename="+ new String((excel.getSheetName(0)).getBytes("gbk"), "iso8859-1") + ".xls");
					response.setContentType("multipart/form-data");
					OutputStream ouputStream = response.getOutputStream();
					excel.write(ouputStream);
					ouputStream.flush();
					ouputStream.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
