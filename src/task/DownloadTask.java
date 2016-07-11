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
	private Downloader downloader;
	
	private long downloadedBytes; //记录本线程下载的全部字节数
	private boolean finished = false; //是否正常执行完的标记
	
	/**
	 * 记录异常退出时的异常信息，如果为null表示没有异常退出；
	 * 否则，表示异常退出，ProgressTask线程将据此判断下载是否出错
	 */
	private String exceptionInfo = null; 
	
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
		RandomAccessFile accessFile = null;
		try {
			accessFile = new RandomAccessFile(saveFile, "rwd");
			accessFile.seek(startByte);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(5000);
			con.setReadTimeout(10000);
			con.setRequestMethod("GET");
			println("下载起始位置：" + startByte +" - " + endByte);
			//多线程下载的关键
			con.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
			int responseCode = con.getResponseCode();
			if (responseCode == 206) { // 200表示请求全部成功，206表示请求分段数据成功
				InputStream inStream = con.getInputStream();
				byte[] buffer = new byte[8192];
				int len = 0;
				boolean hasRead = false;
				boolean hasWrite = false;
				while((len = inStream.read(buffer)) != -1 && (!downloader.isCanceled())){
					if (!hasRead) {
						println("读到了数据");
						hasRead = true;
					}
					//TODO 当文件很大时，只有第一个线程能写数据，其他线程在这里读到数据后，写入不了
					accessFile.write(buffer, 0, len);
					if (!hasWrite) {
						println("写入了数据");
						hasWrite = true;
					}
					downloadedBytes += len;
				}
				Utils.closeQuietly(inStream);
			} else {
				exceptionInfo = "返回码不是206";
				println(exceptionInfo);
				return;
			}
		} catch (Exception e) {
			exceptionInfo = e.getMessage();
			println("出现异常：" + exceptionInfo);
			return;
		} finally {
			Utils.closeQuietly(accessFile);
		}
		if(downloader.isCanceled())
			println("已取消");
		else {
			println("下载完毕，共下载：" + Utils.formatByte(downloadedBytes));
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
	
	public void println(String msg) {
		System.out.println("线程【" + taskId + "】：" + msg);
	}
	
	public String getExceptionInfo() {
		return exceptionInfo;
	}
}