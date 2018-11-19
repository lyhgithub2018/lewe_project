package com.lewe.dao.check;

import java.util.List;

import com.lewe.bean.check.Substrate;

public interface SubstrateMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Substrate record);

    int insertSelective(Substrate record);

    Substrate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Substrate record);

    int updateByPrimaryKey(Substrate record);
    /**
     * 获取所有底物列表
     * @return
     */
	List<Substrate> selectList();
}