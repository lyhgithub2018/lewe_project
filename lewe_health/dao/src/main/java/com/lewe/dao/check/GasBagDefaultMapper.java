package com.lewe.dao.check;

import java.util.List;

import com.lewe.bean.check.GasBagDefault;

public interface GasBagDefaultMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GasBagDefault record);

    int insertSelective(GasBagDefault record);

    GasBagDefault selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GasBagDefault record);

    int updateByPrimaryKey(GasBagDefault record);
    
    List<GasBagDefault> selectAllList();
}