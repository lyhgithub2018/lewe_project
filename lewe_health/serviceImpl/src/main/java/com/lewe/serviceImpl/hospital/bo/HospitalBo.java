package com.lewe.serviceImpl.hospital.bo;

import java.util.ArrayList;
import java.util.List;

import com.lewe.bean.hospital.Hospital;
import com.lewe.bean.hospital.HospitalLinkman;

public class HospitalBo extends Hospital{
	private static final long serialVersionUID = 1L;
	public List<HospitalLinkman> linkManList = new ArrayList<HospitalLinkman>();
	public List<HospitalLinkman> getLinkManList() {
		return linkManList;
	}
	public void setLinkManList(List<HospitalLinkman> linkManList) {
		this.linkManList = linkManList;
	}
	
}
