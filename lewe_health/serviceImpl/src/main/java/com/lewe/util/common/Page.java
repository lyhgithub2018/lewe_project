package com.lewe.util.common;
import java.io.Serializable;

/**
 * @Description: 分页工具类
 * @author 小辉
 * @date 2018年10月25日
 */
public class Page implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int pageNo; //当前页数
	
	private int pageSize; //每页显示记录条数
	
	private int totalCount; //总记录条数
	
	private int totalPageCount; //总页数
	
	private int startIndex; //查询起始索引
	
	private boolean hasFirst; //是否有首页
	
	private boolean hasPre; //是否有前一页
	
	private boolean hasNext; //是否有下一页
	
	private boolean hasLast; //是否有最后一页
	
	public Page(Integer pageNo, Integer pageSize,Integer totalCount) {
		if(pageNo==null||pageNo==0) {
			pageNo = 1;
		}
		if(pageSize==null||pageSize==0) {
			pageSize = 10;
		}
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.startIndex = getStartIndex();
        this.totalPageCount = getTotalPageCount();
	}
	
	/**
	 * 计算总页数 
	 * @param 
	 */
	public int getTotalPageCount() {
		totalPageCount = totalCount / pageSize;
		return (totalCount % pageSize == 0) ? totalPageCount : totalPageCount + 1;
	}

	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getStartIndex() {
		return (pageNo - 1) * pageSize;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	/**
	 * 
	 * @Title: isHasFirst 
	 * @Description: 是否是第一页
	 * @param 
	 * @return boolean    返回类型 
	 * @throws
	 */
	public boolean isHasFirst() {
		return (pageNo == 1) ? false : true;
	}

	public void setHasFirst(boolean hasFirst) {
		this.hasFirst = hasFirst;
	}

	/**
	 * 
	 * @Title: isHasPre 
	 * @Description: 是否有首页
	 * @param 
	 * @return boolean    返回类型 
	 * @throws
	 */
	public boolean isHasPre() {
		// 如果有首页就有前一页，因为有首页就说明不是第一页
		return isHasFirst() ? true : false;
	}

	public void setHasPre(boolean hasPre) {
		this.hasPre = hasPre;
	}

	/**
	 * 
	 * @Title: isHasNext 
	 * @Description: 是否有下一页 
	 * @param 
	 * @return boolean    返回类型 
	 * @throws
	 */
	public boolean isHasNext() {
		return isHasLast() ? true : false;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	/**
	 * 
	 * @Title: isHasLast 
	 * @Description: 是否有尾页
	 * @param  
	 * @return boolean    返回类型 
	 * @throws
	 */
	public boolean isHasLast() {
		// 如果不是最后一页就有尾页
		return (pageNo == getTotalPageCount()) ? false : true;
	}

	public void setHasLast(boolean hasLast) {
		this.hasLast = hasLast;
	}
	
	@Override
	public String toString() {
		return "Page [pageNo=" + pageNo + ", pageSize=" + pageSize + ", totalCount=" + totalCount
				+ ", totalPageCount=" + totalPageCount + ", startIndex=" + startIndex + ", hasFirst=" + hasFirst
				+ ", hasPre=" + hasPre + ", hasNext=" + hasNext + ", hasLast=" + hasLast + "]";
	}
}
