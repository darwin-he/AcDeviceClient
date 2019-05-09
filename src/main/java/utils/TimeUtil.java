package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author darwin_he
 * @date 2019/5/6 22:08
 */
public class TimeUtil {
	public static String getCurrentTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}
}
