package core;

import model.CompleteInfo;
import model.ProgressInfo;

/**
 * 下载过程中的监听者接口
 * @author zj
 *
 */
public interface DownloadListener {
	
	/**
	 * 任务取消时的回调
	 */
	void onCancel();
	
	/**
	 * 收到进度信息时的回调
	 * @param progressInfo
	 */
	void onProgress(ProgressInfo progressInfo);
	
	/**
	 * 下载出错时的回调
	 * @param errorMsg
	 */
	void onError(String errorMsg);
	
	/**
	 * 下载成功时的回调
	 */
	void onSuccess(CompleteInfo completeInfo);
}
