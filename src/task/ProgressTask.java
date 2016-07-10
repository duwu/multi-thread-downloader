package task;

import core.Downloader;
import model.CompleteInfo;
import model.ProgressInfo;
import util.Utils;

/**
 * 统计下载进度信息的线程
 * @author zj
 *
 */
public class ProgressTask extends Thread {
	private Downloader downloader;
	private long lastTimeBytes = 0; 
	private long totalBytes;
	private long startTime; 
	
	public ProgressTask(Downloader downloader, long totalBytes, long startTime) {
		this.downloader = downloader;
		this.totalBytes = totalBytes;
		this.startTime = startTime;
		this.setDaemon(true);
	}

	@Override
	public void run() {
		while (!downloader.isCanceled()) {
			long currentBytes = 0;
			
			//判断所有下载线程是否都已完成
			boolean finished = true;
			for (DownloadTask task : downloader.getTaskList()) {
				if (!task.isFinished()) {
					finished = false;
				}
				currentBytes += task.getDownloadedBytes();
				System.out.println("线程【" + task.getTaskId() + "】已下载 : " + Utils.formatByte(task.getDownloadedBytes()));
			}
			System.out.println("=====================================");
			int progress = (int) (currentBytes * 100 / totalBytes);
			long speed = (currentBytes - lastTimeBytes);
			
			lastTimeBytes = currentBytes;
			
			//进度信息对象
			ProgressInfo progressInfo = new ProgressInfo();
			progressInfo.setRemainingBytes((totalBytes - currentBytes));
			progressInfo.setDownloadedBytes(currentBytes);
			progressInfo.setTotalBytes(totalBytes);
			progressInfo.setCostSeconds((int)((System.currentTimeMillis() - startTime) / 1000));
			if (speed != 0) {
				progressInfo.setRemainingSeconds((int) ((totalBytes - currentBytes) / speed));
			} else {
				progressInfo.setRemainingSeconds(-1); // -1表示未知
			}
			progressInfo.setProgress(progress);
			progressInfo.setSpeed(speed);
			this.downloader.getListener().onProgress(progressInfo);
			
			// 如果下载完成，退出循环
			if (finished) {
				CompleteInfo completeInfo = new CompleteInfo();
				completeInfo.setBytes(totalBytes);
				completeInfo.setStartTime(startTime);
				completeInfo.setFinishTime(System.currentTimeMillis());
				completeInfo.setCost((int)((completeInfo.getFinishTime() - startTime) / 1000));
				completeInfo.setSpeed(totalBytes / completeInfo.getCost());
				this.downloader.getListener().onSuccess(completeInfo);
				break;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("统计线程结束");
	}
}