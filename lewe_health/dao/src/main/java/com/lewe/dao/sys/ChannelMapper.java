package com.lewe.dao.sys;

import java.util.List;
import java.util.Map;

import com.lewe.bean.sys.Channel;

public interface ChannelMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Channel record);

    int insertSelective(Channel record);

    Channel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Channel record);

    int updateByPrimaryKey(Channel record);

	List<Channel> selectByMap(Map<String, Object> map);
}