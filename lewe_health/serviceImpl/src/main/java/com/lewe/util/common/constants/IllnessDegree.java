package com.lewe.util.common.constants;


/**
 * 疾病或症状的程度
 * 注：0:无 1:轻微 2:中等 3:严重
 * @author 小辉
 */
public enum IllnessDegree {
	NOTHAVE((byte)0, "无"), 
	SLIGHT((byte)1, "轻微"), 
	MEDIUM((byte)2, "中等"),
	SERIOUS((byte)3,"严重");
	
	private final byte value;
	private final String desc;

	private IllnessDegree(byte value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	public byte getValue() {
		return value;
	}

	public String getDesc() {
		return desc;
	}
}
