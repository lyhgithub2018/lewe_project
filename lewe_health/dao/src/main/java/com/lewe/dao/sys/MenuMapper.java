package com.lewe.dao.sys;

import java.util.List;
import java.util.Map;

import com.lewe.bean.sys.Menu;
import com.lewe.dao.sys.vo.MenuTree;

public interface MenuMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Menu record);

    int insertSelective(Menu record);

    Menu selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Menu record);

    int updateByPrimaryKey(Menu record);

	List<MenuTree> selectAllChildrenByParam(Map<String, Object> param);

	MenuTree selectByMenuId(Integer menuId);
}