package com.lewe.dao.sys.vo;

import java.util.List;


/**   
 * @Description:系统菜单树
 * @author 作者:小辉
 * @date 2018年10月30日
 * @version 1.0   
 */
public class MenuTree {
	
	private Integer id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 级别
	 */
	private Byte level;
	
	private Integer parentId;
	/**
	 * 页面路径
	 */
	private String pathName;
	/**
	 * 子类别
	 */
	private List<MenuTree> children;
	
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

	public Byte getLevel() {
		return level;
	}

	public void setLevel(Byte level) {
		this.level = level;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
	public List<MenuTree> getChildren() {
		return children;
	}

	public void setChildren(List<MenuTree> children) {
		this.children = children;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	//重写equals()方法:判断名称和id都相同;
    @Override
    public boolean equals(Object obj) {
    	MenuTree menu = (MenuTree) obj;
        return menu.id == this.id;
    }
    //重写hashcode()方法:
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
