package com.lewe.web.controller.common;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.report.ReportInfo;
import com.lewe.bean.sys.SysFile;
import com.lewe.dao.report.ReportInfoMapper;
import com.lewe.dao.sys.SysFileMapper;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.BizCode;
import com.lewe.util.common.PropertiesUtil;
import com.lewe.util.common.StringUtils;
import com.lewe.web.util.ZipUtil;

@Controller
@RequestMapping("/downLoad/")
public class DownLoadController {
	@Autowired
	private SysFileMapper sysFileMapper;
	@Autowired
	private ReportInfoMapper reportInfoMapper;
	@RequestMapping("downLoadReport")
	public void downLoadReport(HttpServletRequest request,Long reportInfoId,String reportInfoIds, Integer type,HttpServletResponse response) throws Exception {
		if(type==null) {
			ApiResult result = new ApiResult();
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数type为空");
			String reslutJSON = JSONObject.toJSONString(result);
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("text/html;charset=UTF-8");
			try {
				response.getWriter().write(reslutJSON);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			response.setContentType("application/octet-stream;charset=UTF-8");  
			//该参数不为空,说明是要批量下载PDF
			if(StringUtils.isNotBlank(reportInfoIds)) {
			    String downFileName = new String("全肠道菌群无创吹气检查报告单PDF.zip".getBytes("gbk"), "iso8859-1");
			    response.setHeader("Content-Disposition","attachment; filename="+downFileName);
			    ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
			    try {
					String[] arr = reportInfoIds.split("\\,");
					if(arr!=null && arr.length>0) {
						for (String reportId : arr) {
							ReportInfo reportInfo = reportInfoMapper.selectByPrimaryKey(Long.valueOf(reportId));
							if(reportInfo!=null) {
								String domainName = PropertiesUtil.getApiPropertyByKey("domain.name");
								//前端项目路径
								String leweWebPath = PropertiesUtil.getApiPropertyByKey("lewe.web.path");
				                SysFile sysfile = sysFileMapper.selectByPrimaryKey(Long.valueOf(reportInfo.getReportPdfIds()));
								String url = sysfile.getUrl();
								//将文件的网络路径转换成本地路径读取
								if(url.startsWith(domainName)) {
									url = url.replaceAll(domainName, leweWebPath);
								}
								ZipUtil.doCompress(url, zipOut);
								response.flushBuffer();
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					zipOut.close();
				}
			//单条记录下载PDF或单条记录下载图片文件
			}else {
				//每份报告的PDF文件只有1个,图片文件有4个
				ReportInfo reportInfo = reportInfoMapper.selectByPrimaryKey(reportInfoId);
				if(type==1) {//下载PDF
					SysFile sysfile = null;
					if(reportInfo!=null) {
						if(reportInfo.getReportPdfIds()!=null) {
							sysfile = sysFileMapper.selectByPrimaryKey(Long.valueOf(reportInfo.getReportPdfIds()));
						}
					}
					if(sysfile !=null) {
						String url = sysfile.getUrl();
						response.reset();
						ServletOutputStream outputStream = null;
						try {
							String downFileName = new String((reportInfo.getSampleName()+"的检测报告PDF.zip").getBytes("gbk"), "iso8859-1");
						    response.setHeader("Content-Disposition","attachment; filename="+downFileName);
							File file = new File(url);
							if(url.contains("http")) {
								//通过网络文件路径获取输入流
								//https://aijutong.com/upload/reportFile/ertong01.png
								/*InputStream inStream = UrlUtil.urlToIO(url);
								int len;
								byte[] byteArr = new byte[1024];
								while ((len = inStream.read(byteArr)) > 0) {
									response.getOutputStream().write(byteArr, 0, len);
								}
								inStream.close();*/
								
								String domainName = PropertiesUtil.getApiPropertyByKey("domain.name");
								//前端项目路径
								String leweWebPath = PropertiesUtil.getApiPropertyByKey("lewe.web.path");
				                SysFile pdfFile = sysFileMapper.selectByPrimaryKey(Long.valueOf(reportInfo.getReportPdfIds()));
								url = pdfFile.getUrl();
								//将文件的网络路径转换成本地路径读取
								if(url.startsWith(domainName)) {
									url = url.replaceAll(domainName, leweWebPath);
									file = new File(url);
								}
								if(file.exists()) {
									outputStream = response.getOutputStream();
									byte[] byteArray = FileUtils.readFileToByteArray(file);
									outputStream.write(byteArray);
								}
							}else {
								//通过本地路径文件的读取方式
								if(file.exists()) {
									outputStream = response.getOutputStream();
									byte[] byteArray = FileUtils.readFileToByteArray(file);
									outputStream.write(byteArray);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
				            if(outputStream!=null) {
				            	IOUtils.closeQuietly(outputStream);
				            }
				        }
					}
				}else if(type==2){//下载图片(4个图片压缩在一起下载)
					String reportPictureIds = reportInfo.getReportPictureIds();
					String downFileName = new String((reportInfo.getSampleName()+"的检测报告图片.zip").getBytes("gbk"), "iso8859-1");
				    response.setHeader("Content-Disposition","attachment; filename="+downFileName);
				    ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
				    String domainName = PropertiesUtil.getApiPropertyByKey("domain.name");
				    //前端项目路径
				    String leweWebPath = PropertiesUtil.getApiPropertyByKey("lewe.web.path");
				    try {
						String[] arr = reportPictureIds.split("\\,");
						if(arr!=null && arr.length>0) {
							for (String picId : arr) {
								if(picId!=null) {
									SysFile sysfile = sysFileMapper.selectByPrimaryKey(Long.valueOf(picId));
									String url = sysfile.getUrl();
									//将文件的网络路径转换成本地路径读取
									if(url.startsWith(domainName)) {
										url = url.replaceAll(domainName, leweWebPath);
									}
									ZipUtil.doCompress(url, zipOut);
									response.flushBuffer();
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}finally {
						zipOut.close();
					}
				}
			}
		}
	}
}
