package core;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import task.ProgressTask;
import task.DownloadTask;
import util.Utils;

/**
 * 下载器类，管理各个下载线程
 * @author zj
 *
 */
public class Downloader {

	private String url; //要下载资源的url地址
	private String saveDir; //保存文件的路径
	private int threadNum; //开启的线程数目
	private DownloadListener listener; //下载过程的监听者
	private List<DownloadTask> taskList; //下载任务类列表
	private ProgressTask progressTask; //统计下载进度的任务
	private boolean canceled; //取消下载的标记
	
	/**
	 * 创建下载器类
	 * @param url 要下载资源的url地址
	 * @param saveDir 保存文件的路径
	 * @param threadNum 开启的线程数目
	 * @param listener 下载过程的监听者
	 */
	public Downloader(String url, String saveDir, int threadNum, DownloadListener listener) {
		this.url = url;
		this.saveDir = saveDir;
		this.threadNum = threadNum;
		this.listener = listener;
		this.taskList = new ArrayList<DownloadTask>(threadNum);
	}
	
	/**
	 * 开始下载
	 * 先测试给定下载链接的连通性，如果可访问，则开启多线程开始下载，
	 * 并启动一个统计进度信息的线程，每秒给DownloadListener返回进度信息
	 */
	public void download() {
		try {
			URL url = new URL(this.url);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(5000); //设置连接超时时间
			con.setRequestMethod("GET"); //设置请求方法为get
			if (con.getResponseCode() == 200) { //如果请求成功
				long totalBytes = con.getContentLengthLong(); //获取资源总字节数
				System.out.println("文件总字节数：" + totalBytes);
				String filePath = this.saveDir + Utils.getFileName(this.url);
				File file = new File(filePath); //文件保存完整路径
				
				//创建一个空文件，并指定大小
				RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
				accessFile.setLength(totalBytes);
				accessFile.close();
				//计算每个线程的下载字节数
				long blockSize = (totalBytes % threadNum) == 0 ? (totalBytes / threadNum) : (totalBytes / threadNum) + 1;
				//启动各个下载线程
				for (int i = 0; i < threadNum; i++) {
					long startByte = i * blockSize;
					long endByte = (i + 1) * blockSize - 1;
					if (i == threadNum - 1) {
						endByte = Math.min(endByte, totalBytes - 1);
					}
					DownloadTask task = new DownloadTask(i, url, startByte, endByte, file, this);
					taskList.add(task);
					task.start();
				}
				//启动统计计算线程
				progressTask = new ProgressTask(this, totalBytes, System.currentTimeMillis());
				progressTask.start();
				
			} else { //url地址无法访问
				listener.onError("url地址[" + this.url + "]无法访问！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			listener.onError(e.getMessage());
		}
	}
	
	/**
	 * 任何一个DownloadTask执行过程出错，都将调用此方法通知Downloader结束其他的DownloadTask
	 * 这里必须开一个线程去执行，因为这个方法可能被下载线程调用，如果在下载线程中去执行则肯定会发生死锁
	 * 因为会出现这种情况：出错的下载线程等待全部下载线程（包括它自己）结束然后再接着往下执行
	 * @param e
	 */
	public void exception(final Exception e) {
		(new Thread() {
			@Override
			public void run() {
				cancelAndWaitAllTaskCanceled();
				listener.onError(e.getMessage());
			}
		}).start();
	}
	
	/**
	 * 取消下载，如果在下载线程或者进度线程中都不调用这个方法，此处可以不一个线程去执行，
	 * 否则，就必须要
	 */
	public void cancel() {
		(new Thread() {
			@Override
			public void run() {
				cancelAndWaitAllTaskCanceled();
				listener.onCancel();
			}
		}).start();
	}
	
	/**
	 * 取消统计线程和所有下载线程并阻塞至全部已取消
	 */
	private void cancelAndWaitAllTaskCanceled() {
		this.canceled = true;
		for (DownloadTask task : taskList) {
			try {
				task.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (this.progressTask != null) {
			try {
				this.progressTask.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public DownloadListener getListener() {
		return listener;
	}

	public List<DownloadTask> getTaskList() {
		return taskList;
	}

	public boolean isCanceled() {
		return canceled;
	}
	
	

}


