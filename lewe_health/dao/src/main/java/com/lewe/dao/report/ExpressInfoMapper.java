package com.lewe.dao.report;

import java.util.List;
import java.util.Map;

import com.lewe.bean.report.ExpressInfo;

public interface ExpressInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ExpressInfo record);

    int insertSelective(ExpressInfo record);

    ExpressInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ExpressInfo record);

    int updateByPrimaryKey(ExpressInfo record);

	Integer selectCountByMap(Map<String, Object> map);

	List<ExpressInfo> selectListByMap(Map<String, Object> map);
}