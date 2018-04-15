package com.shorigo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;

/**
 * 时间工具类
 * 
 * @author peidongxu
 * 
 */
public class TimeUtil {
	/**
	 * 时间戳转换成字符窜
	 * 
	 * @param yyyy
	 *            -MM-dd HH:mm:ss
	 * @param 时间戳
	 */
	public static String getData(String fotmat, String l) {
		return new java.text.SimpleDateFormat(fotmat).format(new java.util.Date(Long.parseLong(l) * 1000));
	}

	/**
	 * 字符串转成时间戳
	 * 
	 * @param 格式yyyy
	 *            -MM-dd HH:mm:ss
	 * @param 字符
	 */
	public static long getDataUnix(String fotmat, String str) {
		long date = 0;
		if (str != null && !TextUtils.isEmpty(str)) {
			try {
				date = new java.text.SimpleDateFormat(fotmat).parse(str).getTime() / 1000;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	/**
	 * 获取当前时间戳字符串
	 * 
	 * @return
	 * @param "yyyy-MM-dd HH:mm:ss":获取的当前的时间的格式
	 */
	public static String getData() {
		return String.valueOf(System.currentTimeMillis());
	}

	/**
	 * 获取当前时间并转换成字符串
	 * 
	 * @return
	 * @param "yyyy-MM-dd HH:mm:ss":获取的当前的时间的格式
	 */
	public static String getCurrertDataUnix(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String formatStr = sdf.format(new Date());
		return String.valueOf(getDataUnix(format, formatStr));
	}
	
	/**
	 * 获取当前时间并转换成字符串
	 * 
	 * @return
	 * @param "yyyy-MM-dd HH:mm:ss":获取的当前的时间的格式
	 */
	public static String getCurrertData(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String formatStr = sdf.format(new Date());
		return formatStr;
	}

	/**
	 * 将时间戳转为代表"距现在多久之前"的字符串
	 * 
	 * @param timeStr
	 *            时间戳
	 * @return
	 */
	public static String getStandardDate(String timeStr) {
		String string = RelativeDateFormat.format(new Date(Long.parseLong(timeStr) * 1000L));
		return string;
	}

	/**
	 * 获取两个时间相差的天数
	 * 
	 * @param beginTime
	 *            开始的时间
	 * @param endTime
	 *            结束的时间
	 * @return 时间天数
	 */
	public static int getDateMillisecond(String beginTime, String endTime) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// 输入日期的格式
		Date date1 = null;
		try {
			date1 = simpleDateFormat.parse(beginTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date date2 = null;
		try {
			date2 = simpleDateFormat.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		GregorianCalendar cal1 = new GregorianCalendar();
		GregorianCalendar cal2 = new GregorianCalendar();
		cal1.setTime(date1);
		cal2.setTime(date2);

		int day = (int) ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / 1000 / 60 / 60 / 24);
		return day;
	}

	/**
	 * 根据传入时间设置倒计时
	 * 
	 * @param handler
	 *            回调
	 * @param index
	 *            传入的时间(1000毫秒==1秒)
	 * @param handerNum
	 *            回传标志
	 */
	public static void initTiming(final Handler handler, final int time, final int handerNum) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SystemClock.sleep(time);
				handler.sendEmptyMessage(handerNum);
			}
		}).start();
	}

	/**
	 * 获取当前时间并转换成时间戳
	 * 
	 * @return
	 * @param "yyyy-MM-dd HH:mm:ss":获取的当前的时间的格式
	 */
	public static long getUnix() {
		long date = 0;
		Date str = new Date();
		if (str != null) {
			date = str.getTime() / 1000;
		}
		return date;
	}

	/**
	 * 倒计时 返回XXX小时XX分钟XX秒
	 * 
	 * @param date
	 *            传入时间如2016年02月22日
	 * @return
	 */
	public static String getCountdown(String str) {
		String dateStr = null;
		long unix = getDataUnix("yyyy-MM-dd", str) + 24 * 60 * 60;
		long unix2 = getUnix();
		if (unix > unix2) {
			long hours = (unix - unix2) / 3600;
			long minutes = ((unix - unix2) - (hours * 3600)) / 60;
			long seconds = ((unix - unix2) - (hours * 3600) - (minutes * 60));
			dateStr = hours + "小时" + minutes + "分钟" + seconds + "秒";
		} else {
			dateStr = "0小时0分钟0秒";
		}

		return dateStr;
	}

	/**
	 * 倒计时 返回具体天数
	 * 
	 * @param date
	 *            传入时间如2016年02月22日
	 * @return
	 */
	public static String getCountdownDay(String date) {
		String dateStr = null;
		long unix = getDataUnix("yyyy-MM-dd", date) + 24 * 60 * 60;
		long unix2 = getUnix();
		if (unix > unix2) {
			dateStr = String.valueOf((unix - unix2) / (24 * 60 * 60));
		} else {
			dateStr = "0";
		}
		return dateStr;
	}

}
