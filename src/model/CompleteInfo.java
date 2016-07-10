package model;

/**
 * 下载结束后返回总的统计信息
 * @author zj
 *
 */
public class CompleteInfo {
	private long bytes; //总字节数
	private int cost; //总下载时间
	private long speed; //平均下载速度，即平均每秒下载的字节数
	private long startTime;
	private long finishTime;
	public long getBytes() {
		return bytes;
	}
	public void setBytes(long bytes) {
		this.bytes = bytes;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public long getSpeed() {
		return speed;
	}
	public void setSpeed(long speed) {
		this.speed = speed;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}
	
	
}
