package util;

import java.io.Closeable;

public class Utils {
	public static String getFileName(String path) {
		if (path == null || path.length() == 0) {
			return null;
		}
		int index = path.lastIndexOf("/");
		return path.substring(index + 1);
	}
	
	public static String formatByte(long byteData){
		String result = byteData + " B";
		double temp;
		if (byteData >= 1024) {
			temp = byteData / 1024;
			result = String.format("%.1f", temp) + " KB";
			if (temp >= 1024) {
				temp = temp / 1024;
				result = String.format("%.1f", temp) + " MB";
			}
		}
		return result;
	}
	
	public static String formatTime(int second) {
		String result = second + "秒";
		int m;
		int h;
		int s;
		if (second >= 60) {
			m = second / 60;
			s = second % 60;
			result = m + "分" + s + "秒";
			if (m >= 60) {
				h = m / 60;
				m = m % 60;
				result = h + "小时" + m + "分" + s + "秒";
			}
		}
		return result;
	}
	
	public static void closeQuietly(Closeable obj) {
		if (obj == null) return;
		try {
			obj.close();
		} catch (Exception e) {
			//ignore exception
			System.out.println("关闭资源时出现异常，将忽略！异常信息：" + e.getMessage());
		}
	}
}
