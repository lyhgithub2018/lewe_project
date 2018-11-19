package com.lewe.util.common.excel;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import com.lewe.serviceImpl.report.bo.ReportCountExcel;
import com.lewe.serviceImpl.report.bo.UsedCountExcel;
import com.lewe.util.common.StringUtils;

/** 
 * @ClassName: ExcelUtil
 * @Description: excel工具类
 * @date 2018年11月15日
 *  
 */

public class ExcelUtil {

    public static ArrayList<ArrayList<Object>> readRows(Sheet sheet,
            int startRowIndex, int rowCount) {
        ArrayList<ArrayList<Object>> rowList = new ArrayList<ArrayList<Object>>();
        
        for (int i = 0; i <= (startRowIndex + rowCount); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                break;
            }

            // 获取列数
            int column = sheet.getRow(0).getPhysicalNumberOfCells();
            ArrayList<Object> cellList = new ArrayList<Object>();
            for (int j = 0; j < column; j++) {
            	if (null != row.getCell(j)) {
            		cellList.add(readCell(row.getCell(j)));
            	}else{
            		cellList.add("");
            	} 
            }
            int emptyNum = 0;
            for (Object object : cellList) {
				if(object==null||"".equals(object)){
					emptyNum++;
				}
			}
            if(emptyNum<column){
            	 rowList.add(cellList);
            }
           
        }
        
        return rowList;
    }
    
    public static ArrayList<ArrayList<Object>> readRows(Sheet sheet) {
        int rowCount = sheet.getLastRowNum();
        return readRows(sheet, 0, rowCount);
    }

    /**
     * 从Excel读Cell
     * 
     * @param cell
     * @return
     */
    private static Object readCell(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING:
            String str = cell.getRichStringCellValue().getString();
            return str == null ? "" : str.trim();

        case Cell.CELL_TYPE_NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else {
            	Object inputValue = null;// 单元格值
                Long longVal = Math.round(cell.getNumericCellValue());
                Double doubleVal = cell.getNumericCellValue();
                if(Double.parseDouble(longVal + ".0") == doubleVal){   //判断是否含有小数位.0
                    inputValue = longVal;
                }
                else{
                    inputValue = doubleVal;
                }
                DecimalFormat df = new DecimalFormat("#.##");    //格式化为二位小数，按自己需求选择；
                return String.valueOf(df.format(inputValue)); 
            }

        case Cell.CELL_TYPE_BOOLEAN:
            return cell.getBooleanCellValue();

        case Cell.CELL_TYPE_FORMULA:
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else {
                return cell.getCellFormula();
            }

        case Cell.CELL_TYPE_BLANK:
            return "";

        default:
            System.err.println("Data error for cell of excel: " + cell.getCellType());
            return "";
        }
    }
    
    /**
     * 
     * @Title: exportExcel 
     * @Description: 生成并返回工作簿对象
     * @param title
     * @param headers
     * @param dataList
     * @return 
     * @throws
     */
    @SuppressWarnings("deprecation")
	public static <T> HSSFWorkbook exportExcel(String title, LinkedHashMap<String, String> headers, List<T> dataList,
			int[] changeTitleFont, Map<String, Object> dropDownMap) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth((short) 25);

		// 生成一个标题样式
		HSSFCellStyle headStyle = workbook.createCellStyle();
		// 设置样式
		// style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		headStyle.setFillPattern(HSSFCellStyle.NO_FILL);
		headStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		headStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		headStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		headStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		headStyle.setVerticalAlignment((short) 20);
		// 生成一个字体
		HSSFFont headFont = workbook.createFont();
		// headFont.setColor(HSSFColor.VIOLET.index);
		headFont.setFontHeightInPoints((short) 16);
		headFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式
		headStyle.setFont(headFont);

		// 生成一个表头样式
		HSSFCellStyle titleStyle = workbook.createCellStyle();
		// 设置样式
		// style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
//		titleStyle.setFillPattern(HSSFCellStyle.NO_FILL);
		titleStyle.setFillPattern(HSSFCellStyle.NO_FILL);
//		titleStyle.setFillBackgroundColor(HSSFColor.LIGHT_GREEN.index);
		titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		titleStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		// 生成一个字体
		HSSFFont font = workbook.createFont();
		// font.setColor(HSSFColor.VIOLET.index);
		font.setFontHeightInPoints((short) 14);
		// font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式
		titleStyle.setFont(font);

		// 生成并设置主体内容样式
		HSSFCellStyle contentStyle = workbook.createCellStyle();
		// style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		contentStyle.setFillPattern(HSSFCellStyle.NO_FILL);
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		// style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 生成另一个字体
		HSSFFont font2 = workbook.createFont();
		font2.setFontHeightInPoints((short) 14);
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		// 把字体应用到当前的样式
		contentStyle.setFont(font2);

		// 声明一个画图的顶级管理器
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		// 定义注释的大小和位置,详见文档
		HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
		// 设置注释内容
		comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
		// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
		comment.setAuthor("aiiju");

		try {
			fillSheet(sheet, dataList, headers, changeTitleFont, dropDownMap, 0, dataList.size() - 1, headStyle,
					titleStyle, contentStyle);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return workbook;
	}

    /**
     * 填充数据
     * @Title: fillSheet 
     * @Description: 将数据填充到excel表格中
     * @param sheet
     * @param dataList
     * @param headers
     * @param firstIndex
     * @param lastIndex
     * @throws Exception 
     */
    @SuppressWarnings("deprecation")
    private static <T> void fillSheet(HSSFSheet sheet, List<T> dataList, LinkedHashMap<String, String> headers,
    		int[] changeTitleFont, Map<String, Object> dropDownMap, int firstIndex, int lastIndex,  HSSFCellStyle headStyle,
            HSSFCellStyle titleStyle, HSSFCellStyle contentStyle) throws Exception {

        // 定义存放英文字段名和中文字段名的数组
        String[] enFields = new String[headers.size()];
        String[] cnFields = new String[headers.size()];

        // 填充数组
        int count = 0;
        for (Entry<String, String> entry : headers.entrySet()) {
            enFields[count] = entry.getKey();
            cnFields[count] = entry.getValue();
            count++;
        }

        // 填充表头
        HSSFRow row = sheet.createRow(0);
        for (short i = 0; i < cnFields.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(titleStyle);
            HSSFRichTextString text = new HSSFRichTextString(cnFields[i]);
            cell.setCellValue(text);
            // 把字体染成红色
            if (null != changeTitleFont && changeTitleFont.length > 0) {
            	changeLocalFontColor(sheet, cell, changeTitleFont, i);
            }
            
            if (null != dropDownMap && dropDownMap.size() > 0) {
            	setDropDownList(sheet, dropDownMap, i);
            }
        }

		// 填充内容
		for (int index = firstIndex; index <= lastIndex; index++) {
			// 创建行
			row = sheet.createRow(index + 1);
			// 获取单个对象
			T item = dataList.get(index);
			// 数据写入表格
			for (int i = 0; i < enFields.length; i++) {
				HSSFCell cell = row.createCell(i);
				cell.setCellStyle(contentStyle);
				Object objValue = getFieldValueByNameSequence(enFields[i], item);
				if (objValue instanceof Integer) {
					int value = ((Integer) objValue).intValue();
					cell.setCellValue(value);
				} else if (objValue instanceof String) {
					String s = (String) objValue;
					cell.setCellValue(s);
				} else if (objValue instanceof Double) {
					double d = ((Double) objValue).doubleValue();
					cell.setCellValue(d);
				} else if (objValue instanceof Float) {
					float f = ((Float) objValue).floatValue();
					cell.setCellValue(f);
				} else if (objValue instanceof Long) {
					long l = ((Long) objValue).longValue();
					cell.setCellValue(l);
				} else if (objValue instanceof Boolean) {
					boolean b = ((Boolean) objValue).booleanValue();
					cell.setCellValue(b);
				} else if (objValue instanceof Date) {
					Date d = (Date) objValue;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
					String dateStr = sdf.format(d);
					cell.setCellValue(dateStr);
				}
			}
		}
	}
    
    @SuppressWarnings("deprecation")
	public static <T> HSSFWorkbook exportExcelWithHeader(String title, LinkedHashMap<String, String> headers, List<T> dataList,
			int[] changeTitleFont, Map<String, Object> dropDownMap, Map<String, Object> params) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth((short) 25);

		// 生成一个标题样式
		HSSFCellStyle headStyle = workbook.createCellStyle();
		// 设置样式
		headStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
//		headStyle.setFillPattern(HSSFCellStyle.NO_FILL);
		headStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		headStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		headStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		headStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		headStyle.setVerticalAlignment((short) 20);
		// 生成一个字体
		HSSFFont headFont = workbook.createFont();
		// headFont.setColor(HSSFColor.VIOLET.index);
		headFont.setFontHeightInPoints((short) 16);
		headFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式
		headStyle.setFont(headFont);

		// 生成一个表头样式
		HSSFCellStyle titleStyle = workbook.createCellStyle();
		// 设置样式
		// style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
//		titleStyle.setFillPattern(HSSFCellStyle.NO_FILL);
		titleStyle.setFillPattern(HSSFCellStyle.NO_FILL);
//		titleStyle.setFillBackgroundColor(HSSFColor.LIGHT_GREEN.index);
		titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		titleStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		// 生成一个字体
		HSSFFont font = workbook.createFont();
		// font.setColor(HSSFColor.VIOLET.index);
		font.setFontHeightInPoints((short) 14);
		// font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式
		titleStyle.setFont(font);

		// 生成并设置主体内容样式
		HSSFCellStyle contentStyle = workbook.createCellStyle();
		// style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		contentStyle.setFillPattern(HSSFCellStyle.NO_FILL);
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		// style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 生成另一个字体
		HSSFFont font2 = workbook.createFont();
		font2.setFontHeightInPoints((short) 14);
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		// 把字体应用到当前的样式
		contentStyle.setFont(font2);

		// 声明一个画图的顶级管理器
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		// 定义注释的大小和位置,详见文档
		HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
		// 设置注释内容
		comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
		// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
		comment.setAuthor("aiiju");

		try {
			fillSheetWithHeader(sheet, dataList, headers, changeTitleFont, dropDownMap, 0, dataList.size() - 1, headStyle,
					titleStyle, contentStyle, params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return workbook;
	}
    
    @SuppressWarnings("deprecation")
	private static <T> void fillSheetWithHeader(HSSFSheet sheet, List<T> dataList, LinkedHashMap<String, String> headers,
    		int[] changeTitleFont, Map<String, Object> dropDownMap, int firstIndex, int lastIndex,  HSSFCellStyle headStyle,
            HSSFCellStyle titleStyle, HSSFCellStyle contentStyle, Map<String, Object> params) throws Exception {

        // 定义存放英文字段名和中文字段名的数组
        String[] enFields = new String[headers.size()];
        String[] cnFields = new String[headers.size()];

        // 填充数组
        int count = 0;
        for (Entry<String, String> entry : headers.entrySet()) {
            enFields[count] = entry.getKey();
            cnFields[count] = entry.getValue();
            count++;
        }
        
        // 设置标题行
        HSSFRow row = sheet.createRow(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, cnFields.length - 1));
        row.createCell(0);
        row.setHeightInPoints(40);
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");
        String deptName = (String) params.get("deptName");
        sheet.getRow(0).getCell(0).setCellValue(deptName + " 调薪记录统计    " + startDate + "—" + endDate);
        sheet.getRow(0).getCell(0).setCellStyle(headStyle);

        // 填充表头
        row = sheet.createRow(1);
        for (short i = 0; i < cnFields.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(titleStyle);
            HSSFRichTextString text = new HSSFRichTextString(cnFields[i]);
            cell.setCellValue(text);
        }

		// 填充内容
		for (int index = firstIndex; index <= lastIndex; index++) {
			// 创建行
			row = sheet.createRow(index + 2);
			// 获取单个对象
			T item = dataList.get(index);
			// 数据写入表格
			for (int i = 0; i < enFields.length; i++) {
				HSSFCell cell = row.createCell(i);
				cell.setCellStyle(contentStyle);
				Object objValue = getFieldValueByNameSequence(enFields[i], item);
				String fieldValue = "";
                if (objValue instanceof Date) {
                    Date date = (Date) objValue;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    fieldValue = sdf.format(date);
                } else {
                    fieldValue = objValue == null ? "" : objValue.toString();
                }
                HSSFRichTextString richString = new HSSFRichTextString(fieldValue);
                cell.setCellValue(richString);
			}
		}
	}
    
    
    /**
     * 
     * @Title: getFieldValueByNameSequence 
     * @Description: 根据带路径或不带路径的属性名获取属性值。即接受简单属性名，如userName等，又接受带路径的属性名，如student.department.name等 
     * @param fieldNameSequence 
     * @param o 
     * @return 
     * @throws Exception 
     */
    private static Object getFieldValueByNameSequence(String fieldNameSequence, Object o) throws Exception {

        Object value = null;

        // 将fieldNameSequence进行拆分
        String[] attributes = fieldNameSequence.split("\\.");
        if (attributes.length == 1) {
            value = getFieldValueByName(fieldNameSequence, o);
        } else {
            // 根据属性名获取属性对象
            Object fieldObj = getFieldValueByName(attributes[0], o);
            String subFieldNameSequence = fieldNameSequence.substring(fieldNameSequence.indexOf(".") + 1);
            value = getFieldValueByNameSequence(subFieldNameSequence, fieldObj);
        }
        return value;
    }
    
    /**
     * 
     * @Title: getFieldValueByName 
     * @Description: 根据字段名获取字段值
     * @param fieldName
     * @param o
     * @return
     * @throws Exception 
     * @throws
     */
    private static Object getFieldValueByName(String fieldName, Object o) throws Exception {

        Object value = null;
        Field field = getFieldByName(fieldName, o.getClass());

        if (field != null) {
            field.setAccessible(true);
            value = field.get(o);
        } else {
            throw new Exception(o.getClass().getSimpleName() + "类不存在字段名 " + fieldName);
        }

        return value;
    }
    
    /**
     * 改变表头部分表格字体的颜色
     * @param sheet
     * @param cell
     * @param array 需要改变字体颜色的单元格序号数组
     * @param i 当前单元格的序号 0开始
     */
	private static void changeLocalFontColor(HSSFSheet sheet, HSSFCell cell, int[] array, int i) {
		if (org.apache.commons.lang.ArrayUtils.contains(array, i)) {
			HSSFFont font = sheet.getWorkbook().createFont();
			font.setColor(HSSFColor.RED.index);
			font.setFontHeightInPoints((short) 14);
			HSSFCellStyle arrayStyle = sheet.getWorkbook().createCellStyle();
			arrayStyle.setFont(font);
			arrayStyle.setFillPattern(HSSFCellStyle.NO_FILL);
//			arrayStyle.setFillBackgroundColor(HSSFColor.LIGHT_GREEN.index);
			arrayStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			arrayStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			arrayStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			arrayStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			arrayStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			// 把字体应用到当前的样式
			arrayStyle.setFont(font);
			cell.setCellStyle(arrayStyle);
		}
	}
    
    /**
     * 设置下拉列表
     * @param sheet
     * @param dropDownList
     * @param i
     */
    private static void setDropDownList(HSSFSheet sheet, Map<String, Object> dropDownMap, int i) {
    	if (dropDownMap.containsKey(String.valueOf(i))) {
    		CellRangeAddressList regions = new CellRangeAddressList(1, 65535, i, i);
            // 生成下拉框内容  
            DVConstraint constraint = DVConstraint.createExplicitListConstraint((String[]) dropDownMap.get(String.valueOf(i)));
            // 绑定下拉框和作用区域  
            HSSFDataValidation data_validation = new HSSFDataValidation(regions,constraint);
            sheet.addValidationData(data_validation);
    	}
    }
    

	/**
	 * 根据字段名获取字段
	 * 
	 * @param fieldName
	 *            字段名
	 * @param clazz
	 *            包含该字段的类
	 * @return
	 */
	private static Field getFieldByName(String fieldName, Class<?> clazz) {
		// 拿到本类的所有字段
		Field[] selfFields = clazz.getDeclaredFields();

		// 如果本类中存在该字段，则返回
		for (Field field : selfFields) {
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}

		// 否则，查看父类中是否存在此字段，如果有则返回
		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null && superClazz != Object.class) {
			return getFieldByName(fieldName, superClazz);
		}

		// 如果本类和父类都没有，则返回空
		return null;
	}

	/**
	 * 根据字段名给对象的字段赋值
	 * 
	 * @param fieldName
	 *            字段名
	 * @param fieldValue
	 *            字段值
	 * @param o
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private static void setFieldValueByName(String fieldName, Object fieldValue, Object o) throws Exception {

		Field field = getFieldByName(fieldName, o.getClass());
		if (field != null) {
			field.setAccessible(true);
			// 获取字段类型
			Class<?> fieldType = field.getType();

			// 根据字段类型给字段赋值
			if (String.class == fieldType) {
				field.set(o, String.valueOf(fieldValue));
			} else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
				field.set(o, Integer.parseInt(fieldValue.toString()));
			} else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
				field.set(o, Long.valueOf(fieldValue.toString()));
			} else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
				field.set(o, Float.valueOf(fieldValue.toString()));
			} else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
				field.set(o, Short.valueOf(fieldValue.toString()));
			} else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
				field.set(o, Double.valueOf(fieldValue.toString()));
			} else if (Character.TYPE == fieldType) {
				if ((fieldValue != null) && (fieldValue.toString().length() > 0)) {
					field.set(o, Character.valueOf(fieldValue.toString().charAt(0)));
				}
			} else if (Date.class == fieldType) {
				field.set(o, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fieldValue.toString()));
			} else {
				field.set(o, fieldValue);
			}
		} else {
			throw new Exception(o.getClass().getSimpleName() + "类不存在字段名 " + fieldName);
		}
	}
	
	
	
	/**
	 * 生成报告统计Excel报表
	 * @param bookTitle Excel文件的名称
	 * @param keyFields 字段名
	 * @param valueFields 列名
	 * @param dataList  数据
	 * @return
	 */
	public static HSSFWorkbook createReportCountExcel(String bookTitle, String[] keyFields, String[] valueFields,
			List<ReportCountExcel> dataList) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(bookTitle);
		// 设置表格列宽(不同的列设置不同的宽度)
		for (int i=0;i<valueFields.length;i++) {
			if(i==0||i==5||i==8) {
				sheet.setColumnWidth(i,16*512);
			}else {
				sheet.setColumnWidth(i,10*512);
			}
		}

		//1.列名样式
		HSSFCellStyle columnNameStyle = workbook.createCellStyle();
		columnNameStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		columnNameStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		columnNameStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		columnNameStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		columnNameStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
		columnNameStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中 
		//设置背景颜色
		columnNameStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); 
		columnNameStyle.setFillForegroundColor(HSSFColor.LEMON_CHIFFON.index);
		columnNameStyle.setFillBackgroundColor(HSSFColor.LEMON_CHIFFON.index);
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 14);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		columnNameStyle.setFont(font);

		//2.内容数据样式
		HSSFCellStyle contentStyle = workbook.createCellStyle();
		contentStyle.setFillPattern(HSSFCellStyle.NO_FILL);
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中 
		contentStyle.setWrapText(true);//设置自动换行
		HSSFFont font3 = workbook.createFont();
		font3.setFontHeightInPoints((short) 14);
		font3.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		contentStyle.setFont(font3);
		try {
			fillSheetWithDataAndStyle(sheet, dataList, keyFields, valueFields, 0, dataList.size(),columnNameStyle, contentStyle);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workbook;
	}
	
	/**
	 * 填充数据
	 * @param sheet
	 * @param dataList
	 * @param keyFields
	 * @param valueFields
	 * @param firstIndex
	 * @param lastIndex
	 * @param columnNameStyle
	 * @param contentStyle
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	private static <T> void fillSheetWithDataAndStyle(HSSFSheet sheet, List<T> dataList, String[] keyFields,
			String[] valueFields, int firstIndex, int lastIndex, HSSFCellStyle columnNameStyle, HSSFCellStyle contentStyle) throws Exception {
        
		HSSFRow row = sheet.createRow(0);
        //1.填充列名称
        for (short i = 0; i < valueFields.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(columnNameStyle);
            HSSFRichTextString text = new HSSFRichTextString(valueFields[i]);
            cell.setCellValue(text);
        }
		//2.填充内容
		for (int index = firstIndex; index < lastIndex; index++) {
			// 创建行(内容从第2行开始)
			row = sheet.createRow(index+1);
			// 获取单个对象
			T item = dataList.get(index);
			// 数据写入表格
			for (int i = 0; i < keyFields.length; i++) {
				HSSFCell cell = row.createCell(i);
				cell.setCellStyle(contentStyle);
				Object objValue = null;
				if(StringUtils.isNotBlank(keyFields[i])){
					objValue = getFieldValueByNameSequence(keyFields[i], item);
				}
				String fieldValue = objValue == null ? "" : objValue.toString();
				//将内容设置到单元格中
                HSSFRichTextString richString = new HSSFRichTextString(fieldValue);
                cell.setCellValue(richString);
			}
		}
	}

	/**
	 * 生成用量统计报表
	 * @param bookTitle
	 * @param keyFields
	 * @param valueFields
	 * @param dataList
	 * @return
	 */
	public static HSSFWorkbook createUsedCountExcel(String bookTitle, String[] keyFields, String[] valueFields,
		List<UsedCountExcel> dataList) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(bookTitle);
		// 设置表格列宽(不同的列设置不同的宽度)
		for (int i=0;i<valueFields.length;i++) {
			if(i==0||i==2||i==3) {
				sheet.setColumnWidth(i,14*512);
			}else if(i==1||i==4){
				sheet.setColumnWidth(i,16*512);
			}else {
				sheet.setColumnWidth(i,10*512);
			}
		}

		//1.列名样式
		HSSFCellStyle columnNameStyle = workbook.createCellStyle();
		columnNameStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		columnNameStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		columnNameStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		columnNameStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		columnNameStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
		columnNameStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中 
		//设置背景颜色
		columnNameStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); 
		columnNameStyle.setFillForegroundColor(HSSFColor.LEMON_CHIFFON.index);
		columnNameStyle.setFillBackgroundColor(HSSFColor.LEMON_CHIFFON.index);
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 14);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		columnNameStyle.setFont(font);

		//2.内容数据样式
		HSSFCellStyle contentStyle = workbook.createCellStyle();
		contentStyle.setFillPattern(HSSFCellStyle.NO_FILL);
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中 
		contentStyle.setWrapText(true);//设置自动换行
		HSSFFont font3 = workbook.createFont();
		font3.setFontHeightInPoints((short) 14);
		font3.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		contentStyle.setFont(font3);
		try {
			fillSheetWithDataAndStyle(sheet, dataList, keyFields, valueFields, 0, dataList.size(),columnNameStyle, contentStyle);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workbook;
	}
}
