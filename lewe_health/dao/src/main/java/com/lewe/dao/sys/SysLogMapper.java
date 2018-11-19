package com.lewe.dao.sys;

import java.util.List;
import java.util.Map;

import com.lewe.bean.sys.SysLog;

public interface SysLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SysLog record);

    int insertSelective(SysLog record);

    SysLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysLog record);

    int updateByPrimaryKey(SysLog record);

	Integer selectCountByMap(Map<String, Object> map);

	List<SysLog> selectListByMap(Map<String, Object> map);
}