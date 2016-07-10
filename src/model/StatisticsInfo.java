package model;

/**
 * 每个线程下载完成后返回的统计信息
 * @author zj
 *
 */
public class StatisticsInfo {
	private long totalBytes;
	private long startTime;
	private long endTime;
	
	/**
	 * 每个线程下载完成后返回的统计信息
	 * @param startTime 开始时间
	 */
	public StatisticsInfo(long startTime) {
		this.startTime = startTime;
	}
	
	/**
	 * 每个线程下载完成后返回的统计信息
	 * @param totalBytes 下载总字节数
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 */
	public StatisticsInfo(long totalBytes, long startTime, long endTime) {
		this.totalBytes = totalBytes;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public long getTotalBytes() {
		return totalBytes;
	}
	
	public void setTotalBytes(long totalBytes) {
		this.totalBytes = totalBytes;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public long getEndTime() {
		return endTime;
	}
	
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	} 
	
	
}
