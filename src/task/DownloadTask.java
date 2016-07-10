package task;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import core.Downloader;

/**
 * 下载任务类
 * @author zj
 *
 */
public class DownloadTask implements Runnable {
	
	private int taskId;
	private long blockSize;
	private URL url;
	private File file;
	private long downloadedBytes;
	private Downloader downloader;
	private boolean canceled = false;
	private boolean finished = false;
	
	public DownloadTask(int taskId, long block, URL url, File file, Downloader downloader) {
		this.taskId = taskId;
		this.blockSize = block;
		this.url = url;
		this.file = file;
		this.downloader = downloader;
		this.downloadedBytes = 0;
	}
	
	@Override
	public void run() {
		long start = taskId * blockSize;
		long end = (taskId + 1) * blockSize - 1;
		
		try {
			RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
			accessFile.seek(start);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(5000);
			con.setReadTimeout(10000);
			con.setRequestMethod("GET");
			//多线程下载的关键
			con.setRequestProperty("Range", "bytes=" + start + "-" + end);
			if (con.getResponseCode() == 206) {
				InputStream inStream = con.getInputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len = inStream.read(buffer)) != -1 && (!canceled)){
					accessFile.write(buffer, 0, len);
					downloadedBytes += len;
				}
				accessFile.close();
				inStream.close();
			}
		} catch (Exception e) {
			downloader.exception(e); //通知下载器结束全部下载线程
			e.printStackTrace();
		}
		if(canceled)
			System.out.println("第" + (taskId + 1) + "条线程已取消");
		else {
			System.out.println("第" + (taskId + 1) + "条线程下载完毕");
			finished = true;
		}
		
	}
	
	public long getDownloadedBytes() {
		return downloadedBytes;
	}
	
	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
	
	public boolean isFinished() {
		return finished;
	}
}