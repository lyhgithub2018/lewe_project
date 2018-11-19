package com.lewe.util.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * String工具
 * 主要对 StringUtils 的一些方法进行重写,达到更方便的使用
 *
 */
public class StringUtils extends org.apache.commons.lang.StringUtils{
	
	/**
	 * 一次性判断多个或单个对象为空。
	 * @param objects
	 * @return 只要有一个元素为Blank，则返回true
	 */
	public static boolean isBlank(Object... objects){
		Boolean result = Boolean.FALSE ;
		for (Object object : objects) {
			if(object == null || "".equals(object.toString().trim()) 
					|| "null".equals(object.toString().trim())
					|| "[null]".equals(object.toString().trim())
					|| "[]".equals(object.toString().trim())
					|| "{}".equals(object.toString().trim())
				){
				result = Boolean.TRUE ; 
				break ; 
			}
		}
		return result ; 
	}
	
	/**
	 * 一次性判断多个或单个对象不为空。
	 * @param objects
	 * @return 只要有一个元素不为Blank，则返回true
	 */
	public static boolean isNotBlank(Object...objects){
		return !isBlank(objects);
	}
	
	public static boolean isBlank(String...objects){
		Object[] object = objects ;
		return isBlank(object);
	}
	public static boolean isNotBlank(String...objects){
		Object[] object = objects ;
		return !isBlank(object);
	}
	public static boolean isBlank(String str){
		Object object = str ;
		return isBlank(object);
	}
	public static boolean isNotBlank(String str){
		Object object = str ;
		return !isBlank(object);
	}

	public static String checkNullToConvert(Object obj) {
		return StringUtils.isBlank(obj) ? "" : obj.toString();
	}
	
	/**
	 * 判断一个字符串在数组中存在几个
	 * @param baseStr
	 * @param strings
	 * @return
	 */
	public static int indexOf(String baseStr,String[] strings){
		
		if(null == baseStr || baseStr.length() == 0 || null == strings)
			return 0;
		int i = 0;
		for (String string : strings) {
			boolean result = baseStr.equals(string);
			i = result ? ++i : i;
		}
		return i ;
	}
	
	public static String trimToEmpty(Object str){
	  return (isBlank(str) ? "" : str.toString().trim());
	}
	
    /**
     * 把Map转换成get请求参数类型,如 {"name"=20,"age"=30} 转换后变成 name=20&age=30
     * @param map
     * @return
     */
    public static String mapToGet(Map<? extends Object,? extends Object> map){
    	String result = "" ;
    	if(map == null || map.size() ==0){
    		return result ;
    	}
    	Set<? extends Object> keys = map.keySet();
    	for (Object key : keys ) {
    		result += ((String)key + "=" + (String)map.get(key) + "&");
		}
    	
    	return isBlank(result) ? result : result.substring(0,result.length() - 1);
    }
    /**
     * 把一串参数字符串,转换成Map 如"?a=3&b=4" 转换为Map{a=3,b=4}
     * @param args
     * @return
     */
    public static Map<String, String> getToMap(String args){
    	if(isBlank(args)){
    		return new HashMap<String, String>() ;
    	}
    	args = args.trim();
    	//如果是?开头,把?去掉
    	if(args.startsWith("?")){
    		args = args.substring(1,args.length());
    	}
    	String[] argsArray = args.split("&");
    	
    	Map<String,String> result = new HashMap<String,String>();
    	for (String ag : argsArray) {
			if(!isBlank(ag) && ag.indexOf("=")>0){
				
				String[] keyValue = ag.split("=");
				//如果value或者key值里包含 "="号,以第一个"="号为主 ,如  name=0=3  转换后,{"name":"0=3"}, 如果不满足需求,请勿修改,自行解决.
					
				String key = keyValue[0];
				String value = "" ;
				for (int i = 1; i < keyValue.length; i++) {
					value += keyValue[i]  + "=";
				}
				value = value.length() > 0 ? value.substring(0,value.length()-1) : value ;
				result.put(key,value);
				
			}
		}
    	
    	return result ;
    }
    
    /**
	 * 转换成Unicode
	 * @param str
	 * @return
	 */
    public static String toUnicode(String str) {
        String as[] = new String[str.length()];
        String s1 = "";
        for (int i = 0; i < str.length(); i++) {
            as[i] = Integer.toHexString(str.charAt(i) & 0xffff);
            s1 = s1 + "\\u" + as[i];
        }
        return s1;
     }
    
    public static String getDoubleTOString(Double str){
		String money = str.toString();
		try {   
			Double.parseDouble(money);
		} catch (Exception e) {
			BigDecimal bDecimal = new BigDecimal(str);
			money = bDecimal.toPlainString();
		} 
		return money;
    	
    }
    
    /**
     * 把数组转换成Set集合 方便判断
     * @param objs
     * @return
     */
    public static TreeSet<String> arrayToSet(String[] objs){
    	TreeSet<String> result = new TreeSet<String>();
    	if(null ==objs){
    		return result ;
    	}
    	for (String obj:objs) {
    		result.add(obj);
    	}
    	return result;
    }
    
    /**
     * 判断字符串是否为数字
     * @param str
     * @return
     */
    public static boolean isNumber(String str){ 
       
       if(isBlank(str)){
    	   return false;
       }
	   Pattern pattern = Pattern.compile("[0-9]*"); 
	   Matcher isNum = pattern.matcher(str);
	   if( !isNum.matches() ){
	       return false; 
	   } 
	   return true; 
    }
    
    
	// 逗号分隔字符串按要求匹配字符串操作 ，type 1.移除 0，增
	public static  String  updateCommaString(String type,String content,String  matching){
		if(content==null){
			content="";
		}
		List<String> list = new ArrayList<String>(); 
		if(content.length()>0){
			Collections.addAll(list, content.split(","));
		}
		if(type.equals("1")&&!list.contains(matching)){
			list.add(matching);
		}
		if(type.equals("0")&&list.contains(matching)){
			list.remove(matching);
		}
		if(list.size()<=0){
			return "";
		}
		
		return list.toString().substring(1,list.toString().length()-1 ).replace(" ", "");
		
	}
	
    public static String conversionCharacter(String str){
    	return str.replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&#39;");
    }
    
    /** 
     * 去除字符串中所包含的空格（包括:空格(全角，半角)、制表符、换页符等） 
     * @param s 
     * @return 
     */  
    public static String removeAllBlank(String s){  
        String result = "";  
        if(null!=s && !"".equals(s)){  
            result = s.replaceAll("[　*| *| *|//s*]*", "");  
        }  
        return result;  
    }  
  
    /** 
     * 去除字符串中头部和尾部所包含的空格（包括:空格(全角，半角)、制表符、换页符等） 
     * @param s 
     * @return 
     */  
    public static String trim(String s){  
        String result = "";  
        if(null!=s && !"".equals(s)){  
            result = s.replaceAll("^[　*| *| *|//s*]*", "").replaceAll("[　*| *| *|//s*]*$", "");  
        }  
        return result;  
    }  
    
    /**
     * 去除字符串中的所有空字符串(基本上所有的空格都能去除掉)
     * @param str
     * @return
     */
    public static String trimAllBlank(String str) {
    	if(str==null) return null;
    	str = str.replaceAll("\\s*", "");
    	return str;
    }
    
    /**
     * 去除特殊的空白字符串
     * @param str
     * @return
     */
    public static String trimSpecialBlank(String str) {
    	if(str==null) return null;
    	//该示例中包含有特殊的空白字符如:'    互联网'(其他正常的去除空格的方法都不起作用)
		//这个空白字符的charAt值是160,因此这里做一个中间替换的方案将其替换成空字符串
    	str = str.replaceAll("\\s*", "").replace((char)160, 'r').replace("r", "");
    	return str;
    }
}
