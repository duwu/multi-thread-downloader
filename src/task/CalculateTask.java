package task;

import java.util.List;

import core.DownloadListener;
import model.CompleteInfo;
import model.ProgressInfo;

public class CalculateTask implements Runnable {
	private long lastTimeBytes = 0; 
	private DownloadListener listener; //下载过程的监听者
	private long totalBytes;
	private List<DownloadTask> taskList;
	private long startTime; 
	private boolean running;
	
	public CalculateTask(DownloadListener listener, List<DownloadTask> taskList, long totalBytes, long startTime) {
		this.listener = listener;
		this.totalBytes = totalBytes;
		this.taskList = taskList;
		this.startTime = startTime;
		this.running = true;
	}

	public void stop() {
		this.running = false;
	}

	@Override
	public void run() {
		while (running) {
			long currentBytes = 0;
			
			//判断所有下载线程是否都已完成
			boolean finished = true;
			for (DownloadTask task : taskList) {
				if (!task.isFinished()) {
					finished = false;
				}
				currentBytes += task.getDownloadedBytes();
			}
		
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
			this.listener.onProgress(progressInfo);
			
			// 如果下载完成，退出循环
			if (finished) {
				CompleteInfo completeInfo = new CompleteInfo();
				completeInfo.setBytes(totalBytes);
				completeInfo.setStartTime(startTime);
				completeInfo.setFinishTime(System.currentTimeMillis());
				completeInfo.setCost((int)((completeInfo.getFinishTime() - startTime) / 1000));
				completeInfo.setSpeed(totalBytes / completeInfo.getCost());
				listener.onSuccess(completeInfo);
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