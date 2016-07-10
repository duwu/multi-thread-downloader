package util;

public class Utils {
	public static String getFileName(String path) {
		int index = path.lastIndexOf("/");
		return path.substring(index+1);
	}
	
	public static String convertByte(long byteData){
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
	
	public static String convertTime(int second) {
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
}
