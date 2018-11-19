package com.lewe.util.common.constants;


/**
 * 报告状态
 * 注：对应report_info表中的report_status字段
 * @author 小辉
 */
public enum ReportStatus {
	//报告状态 1：用户绑定 2：门店扫码 3：邮寄单号 4：乐为扫码 5：结果生成
	USER_BIND((byte)1, "用户绑定"), 
	HOSPITAL_SCAN((byte)2, "门店扫码"), 
	EXPRESS_CODE((byte)3,"邮寄单号"),
	LEWE_SCAN((byte)4, "乐为扫码"), 
	RESULT_CREATE((byte)5, "结果生成"),;
	
	private final byte value;
	private final String desc;

	private ReportStatus(byte value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	public byte getValue() {
		return value;
	}

	public String getDesc() {
		return desc;
	}
	/**
	 * 通过值获取该值对应的文本含义
	 * @param status
	 * @return
	 */
	public static String getDescByStatus(byte status) {
		String desc = "";
		for (ReportStatus e : ReportStatus.values()) {
			if (e.getValue()==status) {
				desc = e.getDesc();
				break;
			}
		}
		return desc;
	}
}
