package model;

/**
 * 进度信息类
 * @author zj
 *
 */
public class ProgressInfo {
	private long downloadedBytes; //已下载字节数
	private long remainingBytes; //剩余字节数
	private long totalBytes; //总字节数
	private int costSeconds; //已下载时间
	private int remainingSeconds; //剩余时间
	private int progress; //进度值
	private long speed; //下载速度，即每秒下载的字节数
	
	
	public long getSpeed() {
		return speed;
	}

	public void setSpeed(long speed) {
		this.speed = speed;
	}

	public long getTotalBytes() {
		return totalBytes;
	}

	public void setTotalBytes(long totalBytes) {
		this.totalBytes = totalBytes;
	}

	public int getProgress() {
		return progress;
	}
	
	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	public long getDownloadedBytes() {
		return downloadedBytes;
	}
	
	public void setDownloadedBytes(long downloadedBytes) {
		this.downloadedBytes = downloadedBytes;
	}
	
	public long getRemainingBytes() {
		return remainingBytes;
	}
	
	public void setRemainingBytes(long remainingBytes) {
		this.remainingBytes = remainingBytes;
	}
	
	public int getCostSeconds() {
		return costSeconds;
	}
	
	public void setCostSeconds(int costSeconds) {
		this.costSeconds = costSeconds;
	}
	public int getRemainingSeconds() {
		return remainingSeconds;
	}
	
	public void setRemainingSeconds(int remainingSeconds) {
		this.remainingSeconds = remainingSeconds;
	}
	
	
}
