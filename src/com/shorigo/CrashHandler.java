package com.shorigo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;

import com.shorigo.utils.Constants;

/**
 * 程序异常奔溃处理
 * 
 * @author peidongxu
 * 
 */
@SuppressLint("SimpleDateFormat")
public class CrashHandler implements UncaughtExceptionHandler {
	private Context mContext;
	private static CrashHandler instance;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();
	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	CrashHandler() {
	}

	public static CrashHandler getInstance() {
		if (instance == null) {
			synchronized (CrashHandler.class) {
				instance = new CrashHandler();
			}
		}
		return instance;
	}

	/**
	 * 初始化异常捕获
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// 保存错误日志文件
		saveCatchInfo2File(ex);
		// 退出程序
		AppManager.getAppManager().AppExit(mContext);
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCatchInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			// 文件名称
			String fileName = "crash-" + time + "-" + timestamp + ".log";
			// 文件路径
			String file_dir = Constants.path + Constants._log;
			File dir = new File(file_dir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(file_dir + File.separator + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes());
			fos.close();
			return fileName;
		} catch (Exception e) {
		}
		return null;
	}

}
