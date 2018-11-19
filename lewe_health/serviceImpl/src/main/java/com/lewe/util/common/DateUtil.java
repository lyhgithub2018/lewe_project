package com.lewe.util.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * @Description 日期时间工具类
 * @author  小辉
 * 2018年10月31日
 */
public class DateUtil {
	private static final String dayFormat="yyyy-MM-dd";
	private static final String monthFormat="yyyy-MM";
	//private static final String dateTimeFormat="yyyy-MM-dd HH:mm:ss";
	private final static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
	private final static SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
	private final static SimpleDateFormat sdfDays = new SimpleDateFormat("yyyyMMdd");
	private final static SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static SimpleDateFormat sdfTimes = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;

    private static final String ONE_SECOND_AGO = "刚刚";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "昨天";

	/**
	 * 获取YYYY格式
	 * @return
	 */
	public static String getSdfTimes() {
		return sdfTimes.format(new Date());
	}
	
	/**
	 * 获取YYYY格式
	 * @return
	 */
	public static String getYear() {
		return sdfYear.format(new Date());
	}

	/**
	 * 获取YYYY-MM-DD格式
	 * @return
	 */
	public static String getDay() {
		return sdfDay.format(new Date());
	}
	
	/**
	 * 获取YYYYMMDD格式
	 * @return
	 */
	public static String getDays(){
		return sdfDays.format(new Date());
	}

	/**
	 * 获取YYYY-MM-DD HH:mm:ss格式
	 * @return
	 */
	public static String getTime() {
		return sdfTime.format(new Date());
	}

	/**
	* @Title: compareDate
	* @Description: TODO(日期比较，如果s>=e 返回true 否则返回false)
	* @param s
	* @param e
	* @return boolean  
	* @throws
	* @author fh
	 */
	public static boolean compareDate(String s, String e) {
		if(fomatDate(s)==null||fomatDate(e)==null){
			return false;
		}
		return fomatDate(s).getTime() >=fomatDate(e).getTime();
	}

	/**
	 * 格式化日期
	 * @return
	 */
	public static Date fomatDate(String date) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return fmt.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 制定日期格式进行格式化日期
	 * @return
	 */
	public static Date fomatDatesWithPattern(String date,String pattern) {
		DateFormat fmt = new SimpleDateFormat(pattern);
		try {
			return fmt.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 校验日期是否合法
	 * @return
	 */
	public static boolean isValidDateWithPattern(String s,String pattern) {
		DateFormat fmt = new SimpleDateFormat(pattern);
		try {
			fmt.parse(s);
			return true;
		} catch (Exception e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			return false;
		}
	}
	/**
	 * 校验日期是否合法
	 * @return
	 */
	public static boolean isValidDate(String s) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			fmt.parse(s);
			return true;
		} catch (Exception e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			return false;
		}
	}
	
	/**
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int getDiffYear(String startTime,String endTime) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			//long aa=0;
			int years=(int) (((fmt.parse(endTime).getTime()-fmt.parse(startTime).getTime())/ (1000 * 60 * 60 * 24))/365);
			return years;
		} catch (Exception e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			return 0;
		}
	}
	 
	/**
     * <li>功能描述：时间相减得到天数
     * @param beginDateStr
     * @param endDateStr
     * @return
     * long 
     * @author Administrator
     */
    public static int getDaySub(String beginDateStr,String endDateStr){
        int day=0;
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date beginDate = null;
        java.util.Date endDate = null;
        
            try {
				beginDate = format.parse(beginDateStr);
				endDate= format.parse(endDateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
            day=(int)((endDate.getTime()-beginDate.getTime())/(24*60*60*1000));
            //System.out.println("相隔的天数="+day);
        return day;
    }
    
    /**
     * 获取 n年n月 n 日 n 小时    时间后的日期
     * @param year 年
     * @param month 月
     * @param day 日
     * @param hour 是
     * @param minute 分
     * @param seconde 秒
     * @return 多长时间以后的时间
     */
    public static Date getTimeAfter(Date date,Integer year,Integer month,Integer day,Integer hour, Integer minute , Integer seconde){
    	Calendar calendar = Calendar.getInstance();
    	
    	// 如果设置了时间，设置成给定的时间。没有给定使用当前时间
    	if(date != null){
    		calendar.setTime(date);
    	}
    	// 年
    	if(year != null){
    		calendar.add(Calendar.YEAR, year);
    	}
    	// 月
    	if(month != null){
    		calendar.add(Calendar.MONTH, month);
    	}
    	
    	//日
    	if(day != null){
    		calendar.add(Calendar.DATE, day);
    	}
    	// 时
    	if(hour != null){
    		calendar.add(Calendar.HOUR, hour);
    	}
    	
    	// 分
    	if(minute != null){
    		calendar.add(Calendar.MINUTE, minute);
    	}
    	
    	// 秒
    	if(seconde != null){
    		calendar.add(Calendar.YEAR, seconde);
    	}
    	
		return calendar.getTime();
    }
    
    
    /**
     * 得到n天之后的日期
     * @param days
     * @return
     */
    public static String getAfterDayDate(String days) {
    	int daysInt = Integer.parseInt(days);
    	
        Calendar canlendar = Calendar.getInstance(); // java.util包
        canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();
        
        SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdfd.format(date);
        
        return dateStr;
    }
    /**
     * 得到n天之后的日期
     * @param days
     * @return
     */
    public static String getAfterDayDate2(String days) {
    	int daysInt = Integer.parseInt(days);
    	
    	Calendar canlendar = Calendar.getInstance(); // java.util包
    	canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
    	canlendar.add(Calendar.HOUR, 1); // 增加一个小时
    	Date date = canlendar.getTime();
    	
    	SimpleDateFormat sdfd = new SimpleDateFormat("MM月dd日 HH时");
    	String dateStr = sdfd.format(date);
    	
    	return dateStr;
    }
    
    /**
     * 得到n天之后是周几
     * @param days
     * @return
     */
    public static String getAfterDayWeek(String days) {
    	int daysInt = Integer.parseInt(days);
        Calendar canlendar = Calendar.getInstance(); // java.util包
        canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        String dateStr = sdf.format(date);
        return dateStr;
    }
    
    /**
     * 转换日期 str<==>date
     * @param obj
     * @param pattern 转换格式   不传，默认是 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Object transFormDate(Object obj,String pattern) {
    	if(pattern == null || "".equals(pattern)) {
    		pattern = "yyyy-MM-dd HH:mm:ss";
    	}
    	SimpleDateFormat sf = new SimpleDateFormat(pattern);
    	if(obj instanceof Date) {
    		Date date = (Date)obj;
    		return sf.format(date);
    	}else if(obj instanceof String) {
    		String dateStr = (String)obj;
    		try {
    			return sf.parse(dateStr);
    		} catch (ParseException e) {
    			e.printStackTrace();
    		}
    	}
    	return null;
    }
    
    /**e
     * 格林时间转换
     * @author 维斯
     * 2016年12月2日   上午11:44:21
     */
    public static String GLFormDate(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat ("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);  
		try {
			Date d = sdf.parse(pattern);
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  
			return sdf1.format(d); 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return pattern;
		
	}
    /**
     * 获取毫秒值
     * @param datetime
     * @param pattern 不传，默认是 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long getTimeMillis(String datetime,String pattern) {
    	if(pattern == null || "".equals(pattern)) {
    		pattern = "yyyy-MM-dd HH:mm:ss";
    	}
    	SimpleDateFormat sf = new SimpleDateFormat(pattern);
    	try {
			Date date = sf.parse(datetime);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return 0;
    }


	/**
	 * 获取当天日期
	 * @return
	 */
	public static String getCurDay(){
		Date d=new Date();
		SimpleDateFormat sf = new SimpleDateFormat(dayFormat);
		return sf.format(d);
	}
	
	/**
	 * 获取当天日期,格式：
	 * @return
	 */
	public static String getCurDay(String format){
		Date d=new Date();
		SimpleDateFormat sf = new SimpleDateFormat(format);
		return sf.format(d);
	}
	
	/**
	 * 获取当前月份  格式默认为YYYY-MM
	 * @return
	 */
	public static String getCurMonth(String pattern){
		Date d=new Date();
		SimpleDateFormat sf;
		if(StringUtils.isEmpty(pattern)){
			sf = new SimpleDateFormat(monthFormat);
		}else{
			sf = new SimpleDateFormat(pattern);
		}
		return sf.format(d);
	}
	/**
	 * 获取当前日期   格式默认为YYYY-MM
	 * @return
	 */
	public static String getCurDate(String pattern){
		Date d=new Date();
		SimpleDateFormat sf;
		if(StringUtils.isEmpty(pattern)){
			sf = new SimpleDateFormat(dayFormat);
		}else{
			sf = new SimpleDateFormat(pattern);
		}
		return sf.format(d);
	}
	
	/**
	 * 给定的时间   格式默认为yyyy-MM-dd
	 * @return
	 */
	public static String dateToString(Date d,String pattern){
		SimpleDateFormat sf;
		if(StringUtils.isEmpty(pattern)){
			sf = new SimpleDateFormat(dayFormat);
		}else{
			sf = new SimpleDateFormat(pattern);
		}
		return sf.format(d);
	}
    
    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }
    
    /**
     * 
     * @Title: isTheDay 
     * @Description: 是否是指定日期 
     * @param date
     * @param day
     * @return 
     * @throws
     */
    private static boolean isTheDay(final Date date, final Date day) {
        return date.getTime() >= DateUtil.dayBegin(day).getTime() 
                && date.getTime() <= DateUtil.dayEnd(day).getTime();
    }
    
    /**
     * 
     * @Title: dayBegin 
     * @Description: 获取指定日 00:00:00的时间
     * @param date
     * @return 
     * @throws
     */
    private static Date dayBegin(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    
    /**
     * 
     * @Title: dayEnd 
     * @Description: 取指定日 23:59:59的时间
     * @param date
     * @return 
     * @throws
     */
    private static Date dayEnd(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }
    
    /**
     * 
     * @Title: isToday 
     * @Description: 是否是今天
     * @param date
     * @return 
     * @throws
     */
    public static boolean isToday(final Date date) {
        return DateUtil.isTheDay(date, new Date());
    }
    
    /**
     * 
     * @Title: isThisWeek 
     * @Description: 判断日期是否为本周
     * @param time
     * @return 
     * @throws
     */
    private static boolean isThisWeek(long time)  
    {  
        Calendar calendar = Calendar.getInstance();  
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);  
        calendar.setTime(new Date(time));  
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);  
        if(paramWeek==currentWeek){  
           return true;  
        }  
        return false;  
    }
    
    /**
	 * 转换日期为字符串
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatDate(Date date ,String pattern) {
		SimpleDateFormat sf = new SimpleDateFormat(pattern);
		String result = null;
		try {
			result = sf.format(date);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
     * 
     * @Title: getWeek 
     * @Description: 获取日期是星期几
     * @param date
     * @return 
     * @throws
     */
    public static String getWeek(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String week = sdf.format(date);
        return week;
    }
    /**
     * 
     * @Title: getWeek 
     * @Description: 获取日期是星期几
     * @param date
     * @return 
     * @throws
     */
    public static String getNewWeek(Date date, Locale locale) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE",locale);
        String week = sdf.format(date);
        return week;
    }
    
    /**
     * 获取两个日期相差的天数
     * @param start  开始日期
     * @param end    结束日期
     * @return
     */
	public static int getDateCount(String start, String end) {
		long time1 = ((Date)transFormDate(start,"yyyy-MM-dd")).getTime();
		long time2 = ((Date)transFormDate(end,"yyyy-MM-dd")).getTime();
		long time3 = time2 - time1;
		int result = new Long(time3 / (1000*3600*24)).intValue();
		return result;
	}
	
	/**
	 * 获取任意的日期
	 * @param date 原点日期
	 * @param day  天数，正数表示当前日期的后n天，负数表示当前日期的前n天
	 * @param pattern
	 * @return
	 */
	public static String getAnyDate(String date,int day,String pattern) {
		if(day == 0) {
			return date;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime((Date)transFormDate(date, pattern));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+day);
		return formatDate(calendar.getTime() ,pattern);
	}
    
    /**
	 * 
	 * @Title: relativeDateFormat 
	 * @Description: 根据前端时间显示规则写的时间操作类
	 * @param date
	 * @param type	公告需要用到的时间转换方式--1    工作汇报用到的时间转换方式--2  暂定  3.给前端区分 今天，昨天，前天
	 * @return 
	 * @throws
	 */
	public static String relativeDateFormat(String dateStr,int type) {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date date = new Date();
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long delta = new Date().getTime() - date.getTime();
        
        
        if(type == 3){
        	if (delta < 24L * ONE_HOUR && isToday(date)) {
                return "今天  "+formatDate(date, "HH:mm") ;
            }
        	if (delta < 48L * ONE_HOUR) {
        		return "昨天  "+formatDate(date, "HH:mm");
        	}
        	if (delta > (72L * ONE_HOUR) ) {
        		return "前天  "+formatDate(date, "HH:mm");
        	}else {
        		return formatDate(date, "yyyy-MM-dd HH:mm");
        	}
        }
        
        
        if (delta < 0) {
            return "您穿越时空了呢！";
        }
        if (delta < 1L * ONE_MINUTE) {
            return ONE_SECOND_AGO;
        }
        if (delta < 59L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
	       
        if(type == 1){
        	if (delta < 24L * ONE_HOUR && isToday(date)) {
                long hours = toHours(delta);
                return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
            }
        	if (delta < 48L * ONE_HOUR) {
        		return ONE_DAY_AGO;
        	}
        	if (delta > (48L * ONE_HOUR) && isThisWeek(date.getTime())) {
        		String time = formatDate(date, " HH:mm");
        		return getWeek(date) + time;
        	}else {
        		return formatDate(date, "yyyy-MM-dd HH:mm");
        	}
        }else{
        	if (delta < 24L * ONE_HOUR && isToday(date)) {
                long hours = toHours(delta);
                return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
            }else if(isToday(date)){
            	return formatDate(date, "HH:mm");
            }else{
            	return formatDate(date, "MM月dd日 HH:mm");
            }
        }
    }
	 /**
	  * 获取两个时间相差的分钟数
	  * @param time1  较大的时间 格式如18:25
	  * @param time2  较小的时间 格式如18:20
	  * @return
	  */
	public static long getDifferMinute(String time1, String time2) {
		long minute = 0;
		try {
		   SimpleDateFormat dfs = new SimpleDateFormat("HH:mm");
		   Date d1 = dfs.parse(time1);
		   Date d2 = dfs.parse(time2);
		   long between = (d1.getTime()-d2.getTime())/1000;
		   minute = between/60;
		} catch (Exception e) {
			e.printStackTrace();
		}
	   return minute;
	}
	/**
	 * 获取下一天的日期
	 * @param date
	 * @return
	 */
	public static Date getNextDate(Date date) {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.add(Calendar.DAY_OF_MONTH, +1); //今天的时间加一天  
        date = calendar.getTime();  
        return date;          
    }
	
	/**
	 * 获取下一天的日期
	 * @param date
	 * @return
	 */
	public static int getToDay() {  
        Calendar calendar = Calendar.getInstance();  
        int toDay = calendar.get(Calendar.DATE);  
        return toDay;          
    }
	
	/**
	 * 自定义方法：获取某个日期是周几
	 * 返回数字：1-7
	 * @return
	 */
	public static int getDayofweek(String date){  
		Calendar cal = Calendar.getInstance();  
		if(StringUtils.isNotBlank(date)){
			Date fomatDate = fomatDate(date);
			//获取某天
			cal.setTime(fomatDate);  
		}else{
			//获取当天
			cal.setTime(new Date(System.currentTimeMillis()));  
		}
		int week = cal.get(Calendar.DAY_OF_WEEK);
		if(week>1){
			return week-1; 
		}else{
			return 7;  
		}
	}  
	
	/**
	 * 获取指定日期的当前月份有多少天
	 * @param date 格式仅支持:2017-06-28
	 * @return
	 * @throws ParseException
	 */
	public static int getMonthDays(String date) throws ParseException{
		int days = 0;
		if(StringUtils.isNotBlank(date)){
			String[] split = date.split("-");
			if(split!=null&&split.length>0){
				int year=0,month=0;
				year = Integer.parseInt(split[0]);
				month = Integer.parseInt(split[1]);
			    Calendar c = Calendar.getInstance();  
				c.set(Calendar.YEAR, year); //年
				c.set(Calendar.MONTH, month-1); //由于月份在Calendar中的索引是0-11,故将参数中的month-1
				days = c.getActualMaximum(Calendar.DAY_OF_MONTH);
				int weeks = c.getActualMaximum(Calendar.WEEK_OF_MONTH);
				System.out.println(month+"月份总天数：" + days);  
				System.out.println("周数：" + weeks);  
			}
		}
		return days;
	}
	/**
	 * 将指定日期由字符串格式转换为指定格式的Date类型
	 * @param date 如：2017-06-28 或2017-06-28 15:28:30  ...
	 * @param pattern 如：yyyy-MM-dd或yyyy-MM-dd HH:mm:ss ...
	 * @return
	 */
	public static Date formatDate(String date,String pattern) {
		DateFormat fmt = new SimpleDateFormat(pattern);
		try {
			return fmt.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 将指定字符串格式转换为指定格式的字符串格式
	 * @param date 如：2017-06-28 或2017-06-28 15:28:30  ...
	 * @param patternOne 传入的格式 如：yyyy-MM-dd或yyyy-MM-dd HH:mm:ss ...
	 * @param patternOne 想要格式化的日期的现有格式 如：yyyy-MM-dd或yyyy-MM-dd HH:mm:ss ...
	 * @return
	 */
	public static String formatString(String date,String patternOne,String patternTwo) {
		if(StringUtils.isBlank(date)){
			return "";
		}
		SimpleDateFormat fmtIn = new SimpleDateFormat(patternOne);
		SimpleDateFormat fmtOut = new SimpleDateFormat(patternTwo);
		try {
			 Date d = fmtIn.parse(date);
			 return fmtOut.format(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 计算指定日期当月有多少天
	 * @param signDate
	 * @return
	 */
	public static int getTotalDays(Date signDate){
		Calendar calendar = Calendar.getInstance();  
		calendar.setTime(signDate);
		int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		return days;
	}
	
	
	
	/**
	 * 格式化UTC格式时间字符串，返回自定义格式时间字符串
	 * @param signDate
	 * @return
	 * @throws Exception 
	 */
	public static String getFormatUTC(String date) throws Exception{
		if(date==null){
			return null;
		}
		date =date.replace("Z", " UTC");
		SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss.SSS Z");
		Date parse = SimpleDateFormat.parse(date);
		String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(parse);
		return format;
	}
	
	public static String changeDateFormat(String dateStr) {
		if(StringUtils.isBlank(dateStr)){
			return null;
		}
		dateStr = dateStr.trim();
		if(dateStr.contains(".")){
			dateStr = dateStr.replace(".", "-");
		}else if(dateStr.contains("/")){
			dateStr = dateStr.replace("/", "-");
		}else if(dateStr.contains("年")||dateStr.contains("月")||dateStr.contains("日")){
			dateStr = dateStr.replace("年", "-").replace("月", "-").replace("日", "");
		}
		return dateStr;
	}
	
	/**
	 * 把字符串转化成匹配相应格式的date日期类型
	 * 由于不知道参数的具体格式，进行多种模式的匹配，一旦匹配成功，返回匹配的结果
	 * @Description:
	 * @param dataStr 
	 * @return 日期类型
	 * @author: 小辉
	 * @time:2018年6月6日 下午17:30:30
	 */
	public static Date getStringToDate(String dataStr) {

		String pattern1  = "yyyy年MM月dd日";
		String pattern2 = "yyyy年MM月";
		String pattern3 = "yyyy-MM";
		String partern4 = "yyyy-MM-dd";
		if(StringUtils.isBlank(dataStr)){
			return null;
		}

		Date result = null;
		if (dataStr != null) {
			boolean b1 = DateUtil.isValidDateWithPattern(dataStr, pattern1);
			if(b1){
				result = DateUtil.fomatDatesWithPattern(dataStr, pattern1);
				return result;
			}
			
			boolean b2 = DateUtil.isValidDateWithPattern(dataStr, pattern2);
			if(b2){
				result = DateUtil.fomatDatesWithPattern(dataStr, pattern2);
				return result;
			}
			boolean b3 = DateUtil.isValidDateWithPattern(dataStr, pattern3);
			if(b3){
				result = DateUtil.fomatDatesWithPattern(dataStr, pattern3);
				return result;
			}
			
			boolean b4 = DateUtil.isValidDateWithPattern(dataStr, partern4);
			if(b4){
				result = DateUtil.fomatDatesWithPattern(dataStr, partern4);
				return result;
			}
		}

		return null;
	}
	
	/**
	 * 将本地的格林尼治时间转换成指定格式的时间
	 * @param time
	 * @param pattern
	 * @return
	 */
    public static String glnzTimeFormDateByPattern(String time,String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat ("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);  
		try {
			Date d = sdf.parse(time);
			SimpleDateFormat sdf1 = new SimpleDateFormat(pattern);  
			return sdf1.format(d); 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return pattern;
		
	}
    
    /**
     * 精确计算两个日期之间相差的天数
     * @param beginDate
     * @param endDate
     * @return
     */
	public static int getTimeDistance(String beginDate , String endDate ,String pattern) {
		
	    Calendar beginCalendar = Calendar.getInstance();
	    beginCalendar.setTime(formatDate(beginDate, pattern));
	    Calendar endCalendar = Calendar.getInstance();
	    endCalendar.setTime(formatDate(endDate, pattern));
	    long beginTime = beginCalendar.getTime().getTime();
	    long endTime = endCalendar.getTime().getTime();
	    if(endTime<beginTime) {
	    	//throw new BizException("5100", "日期范围错误");
	    }
	    int betweenDays = (int)((endTime - beginTime) / (1000 * 60 * 60 *24));//先算出两时间的毫秒数之差大于一天的天数
	    
	    endCalendar.add(Calendar.DAY_OF_MONTH, -betweenDays);//使endCalendar减去这些天数，将问题转换为两时间的毫秒数之差不足一天的情况
	    endCalendar.add(Calendar.DAY_OF_MONTH, -1);//再使endCalendar减去1天
	    if(beginCalendar.get(Calendar.DAY_OF_MONTH)==endCalendar.get(Calendar.DAY_OF_MONTH)) {//比较两日期的DAY_OF_MONTH是否相等
	        return betweenDays + 1;	//相等说明确实跨天了
	    }else {
	    	return betweenDays + 0;	//不相等说明确实未跨天
	    }
	}
	
	/**
	 * 获取指定日期n天后的日期
	 * @param date
	 * @param pattern
	 * @param day
	 * @return
	 */
	public static String getNextDay(String date,String pattern,int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(formatDate(date, pattern));
		calendar.add(Calendar.DATE, day);
		return formatDate(calendar.getTime(), pattern);
	}
	/**
	 * 通过出生日期计算年龄
	 * @param birthday
	 * @return
	 */
	public static int getAgeByBirth(Date birthday) {
        int age = 0;
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间
            Calendar birth = Calendar.getInstance();
            birth.setTime(birthday);
            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
                age = 0;
            } else {
                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                //若当前日期大于生日日期,则年龄加1
                if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
                    age += 1;
                }
            }
            return age;
        } catch (Exception e) {//兼容性更强,异常后返回数据
           return 0;
        }
    }
	
	public static int getAgeByBirth(String birthday) {
        int age = 0;
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间
            Calendar birth = Calendar.getInstance();
            birth.setTime(formatDate(birthday, "yyyy-MM-dd"));
            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
                age = 0;
            } else {
                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                //若当前日期大于生日日期,则年龄加1
                if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
                    age += 1;
                }
            }
            return age;
        } catch (Exception e) {//兼容性更强,异常后返回数据
           return 0;
        }
    }
}
