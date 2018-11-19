package com.lewe.dao.report;

import java.util.List;
import java.util.Map;

import com.lewe.bean.report.vo.UsedCountInfo;

/**
 * 用量统计列表查询
 * @author 小辉
 *
 */
public interface UsedCountMapper {
	/**
	 * 用量统计列表的总数
	 * @param paramMap
	 * @return
	 */
	Integer selectUsedCountByMap(Map<String, Object> paramMap);

	/**
	 * 用量统计列表查询(包含分页)
	 * @param paramMap
	 * @return
	 */
	List<UsedCountInfo> selectUsedCountListByMap(Map<String, Object> paramMap);
}
