package com.lewe.util.common.mei.nian;

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
	//private static final String tokenApi = "https://api.pacs.health-100.cn/amol-back/oauth/token?client_id=amol_client_tpp&client_secret=amol_secret_tpp&grant_type=password&username=tpp_health&password=yizhen_tpp@1130";
	//private static final String addInfoApi = "https://api.pacs.health-100.cn/amol-back/tppCheck/addInfo?access_token=";
	private static final String tokenApi = "http://beta.pacs.health-100.cn/amol-back/oauth/token?client_id=amol_client_tpp&client_secret=amol_secret_tpp&grant_type=password&username=tpp_health&password=yizhen_tpp@1130";
	private static final String addInfoApi = "http://beta.pacs.health-100.cn/amol-back/tppCheck/addInfo?access_token=";
	//private static final String uploadFileApi = "https://api.pacs.health-100.cn/amol-back/tppCheck/uploadFile?access_token=XX";
	/**
	 * 获取token
	 * @return
	 */
	public static String getAccessToken() {
		/*JedisUtil redis = JedisUtil.getInstance();
		String key = "meiNianReportAccessToken";
		String accessToken = redis.get(key);
		if(StringUtils.isNotBlank(accessToken)) {
			return accessToken;
		}*/
		String accessToken = "";
		String responStr = HttpUtil.doGet(tokenApi);
		if(StringUtils.isNotBlank(responStr)) {
			JSONObject result = JSONObject.parseObject(responStr);
			accessToken = result.getString("access_token");
			//expires_in token剩余有效时间
			int expiresIn = result.getIntValue("expires_in");
			if(accessToken!=null) {
				//redis.set(key, accessToken, expiresIn-1800);
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
		System.out.println("param:"+param);
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
	
	public static void main(String[] args) {
		JSONObject json = new JSONObject();
		json.put("hospitalName", "小辉测试门店");
		json.put("hospitalCode", "XIAOTEST01");
		json.put("name", "张三");
		json.put("sex", "F");
		json.put("vid", "1F5A6GE5GE5");
		json.put("itemId", "FAEGERG");
		json.put("itemName", "全渠道呼气检测");
		json.put("reportNum", "1");
		addCheckInfo(json);
	}
}
