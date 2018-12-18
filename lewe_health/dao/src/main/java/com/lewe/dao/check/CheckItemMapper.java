package com.lewe.dao.check;

import java.util.List;
import java.util.Map;

import com.lewe.bean.check.CheckItem;

public interface CheckItemMapper {
    
    int deleteByPrimaryKey(Integer id);

    int insert(CheckItem record);

    int insertSelective(CheckItem record);

    CheckItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CheckItem record);

    int updateByPrimaryKey(CheckItem record);

	Integer selectCountWithSubstrate(Map<String, Object> map);

	List<CheckItem> selectListWithSubstrate(Map<String, Object> map);

	List<CheckItem> selectListByMap(Map<String, Object> map);

}