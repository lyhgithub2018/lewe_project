package com.lewe.web.controller.common;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.lewe.bean.hospital.Hospital;
import com.lewe.bean.report.ReportInfo;
import com.lewe.bean.sys.SysFile;
import com.lewe.dao.hospital.HospitalMapper;
import com.lewe.dao.report.ReportInfoMapper;
import com.lewe.dao.sys.SysFileMapper;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.BizCode;
import com.lewe.util.common.DateUtil;
import com.lewe.util.common.PropertiesUtil;
import com.lewe.util.common.StringUtils;
import com.lewe.util.common.mei.nian.MeiNianReportUtil;
import com.lewe.web.util.BASE64DecodedMultipartFile;

/**
 * 文件上传的控制器
 * @author 小辉
 *
 */
@Controller
@RequestMapping("/uploadFile/")
public class UploadFileController extends BaseController {
	
	@Autowired
	private SysFileMapper sysFileMapper;
	@ResponseBody
	@RequestMapping("uploadImg")
	public ApiResult uploadImg(HttpServletRequest request,@RequestParam(required=false, name="imageFile")MultipartFile imageFile, HttpServletResponse response) {
		ApiResult result = new ApiResult();
		try {
			request.setCharacterEncoding("utf-8"); // 设置编码
			if (imageFile != null) {
				//文件上传路径
				String uploadPath = PropertiesUtil.getApiPropertyByKey("upload.image.path");
				File dir = new File(uploadPath);
				if (!dir.exists()) {
					dir.mkdir();
				}
				// 文件原始名称
				String originalFilename = imageFile.getOriginalFilename();
				// 文件类型
				String fileType = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
				if(StringUtils.isBlank(fileType)) {
					fileType = "jpg";
				}
				// 自己定义文件唯一名称
				String fileName = DateUtil.getDays() + System.currentTimeMillis() + "." + fileType;
				// 目标路径
				String destPath = uploadPath + fileName;
				File localFile = new File(destPath);
				// 图片上传到服务器
				imageFile.transferTo(localFile);
				// 上传成功之后将文件信息保存到数据库中
				SysFile sysFile = new SysFile();
				sysFile.setName(originalFilename);
				sysFile.setSize(imageFile.getSize());
				sysFile.setType(fileType);
				// 文件访问路径
				sysFile.setUrl(PropertiesUtil.getApiPropertyByKey("upload.image.url") + fileName);
				sysFile.setCreateTime(new Date());
				sysFileMapper.insertSelective(sysFile);
				result.setData(sysFile);
			} 
		} catch (Exception e) {
			log.error("上传图片发生异常", e);
		}
		return result;
	}
	
	@Autowired
	private ReportInfoMapper reportInfoMapper;
	@Autowired
	private HospitalMapper hospitalMapper;
	/**
	 * 前端生成的报告PDF文件及图片调用该接口将报告上传到服务器
	 * @param base64str base64字符串格式的文件
	 * @param reportInfoId 报告信息主键id
	 * @param type  1:PDF报告  2:图片报告
	 * @return
	 */
	@ResponseBody
	@RequestMapping("uploadReport")
	public ApiResult uploadReport(HttpServletRequest request,String base64str,Integer type,Long reportInfoId, HttpServletResponse response) {
		// response.setHeader("Access-Control-Allow-Origin", "*");
        // response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        // response.setHeader("Access-Control-Allow-Credentials", "true"); //支持cookie跨域
		// response.setContentType("application/json;charset=UTF-8");
		
		String origin = request.getHeader("Origin");
        if(StringUtils.isBlank(origin)){
            origin = "*";
        }
        origin.replaceAll("http://", "");
        origin.replaceAll("https://", "");

        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods","GET,POST");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,xm-api-type,App-Key,Access-Token,wechat-id,Cookie,token,Content-Type,Sign");
        response.setHeader("Access-Control-Allow-Credentials","true");
		response.setContentType("application/json;charset=UTF-8");
		
		ApiResult result = new ApiResult();
		try {
			if(base64str==null) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("上传文件不存在,请检查");
				return result;
			}
			if(type==null||reportInfoId==null) {
				result.setCode(BizCode.PARAM_EMPTY);
				result.setMessage("缺少参数");
				return result;
			}
			//将base64转成MultipartFile
			MultipartFile reportFile = BASE64DecodedMultipartFile.base64ToMultipart(base64str);
			request.setCharacterEncoding("utf-8"); // 设置编码
			//文件上传路径
			String uploadPath = PropertiesUtil.getApiPropertyByKey("upload.reportFile.path");
			File dir = new File(uploadPath);
			if (!dir.exists()) {
				dir.mkdir();
			}
			// 文件原始名称
			String originalFilename = reportFile.getOriginalFilename();
			// 文件类型
			String fileType = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
			if(StringUtils.isBlank(fileType)) {
				fileType = "jpg";
			}
			// 自己定义文件唯一名称
			String fileName = DateUtil.getDays() + System.currentTimeMillis() + "." + fileType;
			//美年端的文件名单独处理
			ReportInfo reportInfo = reportInfoMapper.selectByPrimaryKey(reportInfoId);
			if(reportInfo!=null) {
				Hospital hospital = hospitalMapper.selectByPrimaryKey(reportInfo.getHospitalId());
				if(hospital!=null && hospital.getHospitalName().contains("美年")) {
					//美年端用sampleCode+itemId保证报告文件名的唯一性
					fileName = reportInfo.getSampleCode()+"10160"+ "." + fileType;
				}
			}
			// 目标路径
			String destPath = uploadPath + fileName;
			File localFile = new File(destPath);
			// 图片上传到服务器
			reportFile.transferTo(localFile);

			// 上传成功之后将文件信息保存到数据库中
			SysFile sysFile = new SysFile();
			sysFile.setName(originalFilename);
			sysFile.setSize(reportFile.getSize());
			sysFile.setType(fileType);

			// 文件访问路径
			sysFile.setUrl(PropertiesUtil.getApiPropertyByKey("upload.reportFile.url") + fileName);
			sysFile.setCreateTime(new Date());
			sysFileMapper.insertSelective(sysFile);

			ReportInfo update = new ReportInfo();
			update.setId(reportInfoId);
			String pdfId = "";
			String picIds = "";
			
			if(type==1) {
				pdfId = sysFile.getId()+"";
				update.setReportPdfIds(pdfId);//报告PDF文件id
			}else if(type==2) {
				if(reportInfo!=null) {
					//报告图片只有第一张是前端动态生成的,需要调用该接口将其上传至服务器
					//报告图片区分儿童版和成人版,默认的6张图片已上传至服务器指定位置.
					if(reportInfo.getSampleAge()<18) {//儿童版
						picIds = sysFile.getId()+",1,2,3";
					}else {//成人版
						picIds = sysFile.getId()+",4,5,6";
					}
					update.setReportPictureIds(sysFile.getId()+",4,5,6");//报告图片文件id
				}
			}
			reportInfoMapper.updateByPrimaryKeySelective(update);
			result.setData(sysFile);
			InputStream inputStream = reportFile.getInputStream();
            if(inputStream!=null) {
            	inputStream.close();
            }
            
            //判断如果是美年端的报告,则需要调用美年的上传文件接口
            if(reportInfo!=null) {
            	Hospital hospital = hospitalMapper.selectByPrimaryKey(reportInfo.getHospitalId());
            	if(hospital!=null && hospital.getHospitalName().contains("美年")) {
            		if(type==1) {
            			//上传PDF到美年端
            			String param = "vid="+reportInfo.getSampleCode()+"&itemId=10160&fileType=pdf";
            			MeiNianReportUtil.uploadFile(param, sysFile.getUrl());
            		}else if(type==2) {
            			if(picIds!=null) {
            				String[] arr = picIds.split("\\,");
            				for (String id : arr) {
            					SysFile eachFile = sysFileMapper.selectByPrimaryKey(Long.valueOf(id));
            					if(eachFile!=null) {
            						//上传图片到美年端
            						String param = "vid="+reportInfo.getSampleCode()+"&itemId=10160&fileType="+eachFile.getType();
            						MeiNianReportUtil.uploadFile(param, eachFile.getUrl());
            					}
							}
            			}
            		}
            	}
            }
		} catch (Exception e) {
			log.error("上传报告文件发生异常", e);
		}
		return result;
	}
}
