package com.lewe.serviceImpl.check.bo;

import java.util.ArrayList;
import java.util.List;

import com.lewe.bean.check.CheckItemSubstrate;

/**
 * 新增或编辑检测项目,用于接收前端参数
 * @author 小辉
 *
 */
public class CheckItemBo {
	 private Integer id;//检测项目id
	 private String name;//项目名称
	 //检测项目所对应的底物
	 private List<CheckItemSubstrate> substrateList = new ArrayList<CheckItemSubstrate>();
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<CheckItemSubstrate> getSubstrateList() {
		return substrateList;
	}
	public void setSubstrateList(List<CheckItemSubstrate> substrateList) {
		this.substrateList = substrateList;
	}
	 
}
