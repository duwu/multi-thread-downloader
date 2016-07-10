package task;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import core.Downloader;
import util.Utils;

/**
 * 下载任务线程
 * @author zj
 *
 */
public class DownloadTask extends Thread {
	
	private int taskId;
	private URL url;
	private long startByte;
	private long endByte;
	private File saveFile;
	private long downloadedBytes;
	private Downloader downloader;
	private boolean finished = false;
	
	/**
	 * 实例化下载任务线程类
	 * @param taskId 任务Id，从0开始依次编号
	 * @param url 下载地址
	 * @param startByte 本线程下载的起始字节位置
	 * @param endByte 本线程下载的截止字节位置 
	 * @param saveFile 保存地址
	 * @param downloader 下载器实例，线程是在下载器实例中创建并由它管理的
	 */
	public DownloadTask(int taskId, URL url, long startByte, long endByte, File saveFile, Downloader downloader) {
		this.taskId = taskId;
		this.startByte = startByte;
		this.endByte = endByte;
		this.url = url;
		this.saveFile = saveFile;
		this.downloader = downloader;
		this.downloadedBytes = 0;
	}
	
	@Override
	public void run() {
		try {
			RandomAccessFile accessFile = new RandomAccessFile(saveFile, "rw");
			accessFile.seek(startByte);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(5000);
			con.setReadTimeout(10000);
			con.setRequestMethod("GET");
			System.out.println("线程【" + taskId + "】下载起始位置：" 
					+ startByte +" - " + endByte);
			//多线程下载的关键
			con.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
			if (con.getResponseCode() == 206) {
				InputStream inStream = con.getInputStream();
				byte[] buffer = new byte[8192];
				int len = 0;
				while((len = inStream.read(buffer)) != -1 && (!downloader.isCanceled())){
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
		if(downloader.isCanceled())
			System.out.println("线程【" + taskId + "】已取消");
		else {
			System.out.println("线程【" + taskId + "】下载完毕，共下载：" 
					+ Utils.formatByte(downloadedBytes));
			finished = true;
		}
		
	}
	
	/**
	 * 返回本线程已下载字节数
	 * @return
	 */
	public long getDownloadedBytes() {
		return downloadedBytes;
	}
	
	/**
	 * 返回本线程是否已完成，如果线程是因为取消任务而结束的，返回的不是true而是false
	 * 这意味着只有当线程的下载任务正常下载完才会返回true
	 * @return
	 */
	public boolean isFinished() {
		return finished;
	}
	
	public int getTaskId() {
		return taskId;
	}
}