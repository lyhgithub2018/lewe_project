package com.lewe.util.common.mei.nian;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.lewe.util.common.HttpUtil;
import com.lewe.util.common.JedisUtil;
import com.lewe.util.common.StringUtils;

/**
 * 该类负责将美年端的报告信息传给他们
 * @author 小辉
 *
 */
public class MeiNianReportUtil {
	public static final Logger logger = LoggerFactory.getLogger(MeiNianReportUtil.class); 
	//美年正式环境接口
	//private static final String tokenApi = "https://api.pacs.health-100.cn/amol-back/oauth/token?client_id=amol_client_tpp&client_secret=amol_secret_tpp&grant_type=password&username=tpp_health&password=yizhen_tpp@1130";
	//private static final String addInfoApi = "https://api.pacs.health-100.cn/amol-back/tppCheck/addInfo?access_token=";
	//private static final String uploadFileApi = "https://api.pacs.health-100.cn/amol-back/tppCheck/uploadFile?access_token=";
	//美年测试环境接口
	private static final String tokenApi = "http://beta.pacs.health-100.cn/amol-back/oauth/token?client_id=amol_client_tpp&client_secret=amol_secret_tpp&grant_type=password&username=tpp_health&password=yizhen_tpp@1130";
	private static final String addInfoApi = "http://beta.pacs.health-100.cn/amol-back/tppCheck/addInfo?access_token=";
	private static final String uploadFileApi = "http://beta.pacs.health-100.cn/amol-back/tppCheck/uploadFile?access_token=";
	/**
	 * 获取token
	 * @return
	 */
	public static String getAccessToken() {
		JedisUtil redis = JedisUtil.getInstance();
		String key = "meiNianReportAccessToken";
		String accessToken = redis.get(key);
		if(StringUtils.isNotBlank(accessToken)) {
			return accessToken;
		}
		String responStr = HttpUtil.doGet(tokenApi);
		if(StringUtils.isNotBlank(responStr)) {
			JSONObject result = JSONObject.parseObject(responStr);
			accessToken = result.getString("access_token");
			//expires_in token剩余有效时间
			int expiresIn = result.getIntValue("expires_in");
			if(accessToken!=null) {
				redis.set(key, accessToken, expiresIn-1800);
			}
		}
		return accessToken;
	}
	/**
	 * 调用美年的保存检查信息接口
	 * @param paramJson
	 * @return
	 */
	public static String addCheckInfo(JSONObject paramJson) {
		String flag = "";
		String url = addInfoApi + getAccessToken();
		String param = "datas="+paramJson.toJSONString();
		logger.info("===============调用美年的保存检查信息参数：{}",param);
		String res = HttpUtil.doPost(url, param);
		if(StringUtils.isNotBlank(res)) {
			JSONObject result = JSONObject.parseObject(res);
			logger.info("===============调用美年的保存检查信息接口返回结果：{}",result.toJSONString());
			if(result.getBoolean("isSuccess") && result.getString("resultCode").equals("0")) {
				flag = "OK";
			}
		}
		return flag;
	}
	/**
	 * 上传文件
	 * @param paramJson
	 * @return
	 */
	public static String uploadFile(String param,String fileUrl) {
		String flag = "";
		try {
			CloseableHttpClient httpclient = HttpClientBuilder.create().build();
			CloseableHttpResponse response = null;
			try {
				String url = uploadFileApi + getAccessToken()+"&"+param;
				logger.info("===============调用美年的上传文件接口参数：{}",param);
				HttpPost httppost = new HttpPost(url);
				HttpEntity req = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE).addPart("file", new FileBody(new File(fileUrl))).build();
				httppost.setEntity(req);
				response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity);
				if(StringUtils.isNotBlank(result)) {
					JSONObject json = JSONObject.parseObject(result);
					logger.info("===============调用美年的文件上传接口返回结果：{}",json.toJSONString());
					if(json.getBoolean("isSuccess") && json.getString("resultCode").equals("0")) {
						flag = "OK";
					}
				}
			} finally {
				try {
					response.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	public static void main(String[] args) {
		JSONObject json = new JSONObject();
		json.put("hospitalName", "美年大健康广东东莞店");//门店名称
		json.put("hospitalCode", "44");//门店编码
		json.put("name", "熊细平");//姓名
		json.put("sex", "M");//性别
		json.put("vid", "4418335108");//采样编码
		json.put("itemId", "10160");//项目编码(固定)
		json.put("itemName", "肠道菌群无创检测");//项目名称(固定)
		//json.put("npMark", "阳性");//阴阳性标识
		json.put("reportNum", "1");//报告张数
		addCheckInfo(json);
		//测试上传文件
		String param = "vid=4418335108&itemId=10160&fileType=jpg";
		uploadFile(param,"D:/4418334407-1.jpg");
	}
}
