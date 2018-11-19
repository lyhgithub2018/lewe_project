package com.lewe.dao.report;

import java.util.List;
import java.util.Map;

import com.lewe.bean.report.ReportSymptom;

public interface ReportSymptomMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ReportSymptom record);

    int insertSelective(ReportSymptom record);

    ReportSymptom selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ReportSymptom record);

    int updateByPrimaryKey(ReportSymptom record);

    /**
     * 通过报告id删除采样症状数据
     * @param id
     * @return
     */
	int deleteByReportInfoId(Long id);

	List<ReportSymptom> selectListByMap(Map<String, Object> map);
}