package com.lewe.util.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.FileSystemResource;

/**
 * @Description: 读取配置文件工具类
 * @author 小辉
 * @date 2018年10月24日
 * 
 */
public class PropertiesUtil {

    /**
     * 
     * @Title: outputvalusofProperty 
     * @Description: 输出name属性文件内容
     * @param name
     * @throws IOException 
     * @throws
     */
    public static void outputvalusofProperty(String name) throws IOException{
        InputStream in = PropertiesUtil.class.getResourceAsStream(name);
        Properties pro = new Properties();
        pro.load(in);

        for (String key:pro.stringPropertyNames()){
            System.out.println("key:"+key+" "+"value:"+pro.getProperty(key));
        }
    }


    /**
     * 
     * @Title: getProperty 
     * @Description: 获取filename属性文件中，关键字为key的值 
     * @param filename
     * @param key
     * @return
     * @throws IOException 
     * @throws
     */
    public static String getProperty(String filename,String key) {
    	// 读取资源文件
    	FileSystemResource file = new FileSystemResource(filename);
        Properties pro = new Properties();
        try {
        	InputStream in = file.getInputStream();
			pro.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}

        if(pro.containsKey(key))
            return pro.getProperty(key);
        else 
            return null;
    }
    
    /**
     * 
     * @Description: 根据key获取值
     * @param key
     * @return
     * @throws IOException 
     * @throws
     */
    public static String getApiPropertyByKey(String key) {
    	
    	InputStream in = PropertiesUtil.class.getResourceAsStream("/property/api.properties");
        Properties pro = new Properties();
        try {
            pro.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(pro.containsKey(key))
            return pro.getProperty(key);
        else 
            return null;
    }
}
