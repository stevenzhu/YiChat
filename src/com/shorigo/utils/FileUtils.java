package com.shorigo.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.shorigo.yichat.R;

/**
 * 
 ****************************************** 
 * @文件名称 : FileUtils.java
 * @文件描述 : 文件工具类
 ****************************************** 
 */
public class FileUtils {

	/**
	 * 返回是否有SD卡
	 * 
	 * @return
	 */
	public static boolean getSdState() {
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 保存图片
	 * 
	 * @param bitmap
	 * @param path
	 */
	public static void SaveBitmap(Bitmap bitmap, String path) {
		if (bitmap == null) {
			return;
		}
		File file = new File(path);
		if (file.exists())
			file.delete();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// 将Bitmap对象写入本地路径中，Unity在去相同的路径来读取这个文件
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		bitmap.compress(format, 80, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件
	 */
	public static void deleteFile(String path) {
		// 删除缓存
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 删除缓存文件
	 */
	public static void deleteCacheFile() {
		String[] arrPath = new String[] { Constants.path + Constants._image, Constants.path + Constants._audio, Constants.path + Constants._video, Constants.path + Constants._anex };
		for (int i = 0; i < arrPath.length; i++) {
			// 删除缓存
			File file = new File(arrPath[i]);
			if (file.exists()) {
				if (file.isFile()) {
					file.delete();
				} else if (file.isDirectory()) {
					File files_clild[] = file.listFiles();
					for (int j = 0; j < files_clild.length; j++) {
						files_clild[j].delete();
					}
				}
			}
		}
	}

	/**
	 * 获取缓存大小
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String getTotalCacheSize(Context context) throws Exception {
		File file = new File(Constants.path + Constants._image);
		long cacheSize = getFolderSize(file);
		return getFormatSize(cacheSize);
	}

	/**
	 * 获取文件大小
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static long getFolderSize(File file) throws Exception {
		long size = 0;
		try {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				// 如果下面还有文件
				if (fileList[i].isDirectory()) {
					size = size + getFolderSize(fileList[i]);
				} else {
					size = size + fileList[i].length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	/**
	 * 格式化单位
	 * 
	 * @param size
	 * @return
	 */
	public static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			// return size + "Byte";
			return "0K";
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "K";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "M";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
	}

	/**
	 * 创建缓存文件夹
	 */
	public static void createCacheFolder() {
		// 获取跟目录
		File sdDir = null;
		// 判断sd卡是否存在
		if (getSdState()) {
			sdDir = Environment.getExternalStorageDirectory();
		}

		if (sdDir != null) {
			Constants.path = sdDir.toString();
		} else {
			Constants.path = Environment.getExternalStorageState();
		}

		File file;

		file = new File(Constants.path, Constants._image);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(Constants.path, Constants._audio);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(Constants.path, Constants._video);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(Constants.path, Constants._anex);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(Constants.path, Constants._log);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 保存图片到相册
	 */
	public static void saveImageToGallery(Context context, Bitmap bmp) {
		// 首先保存图片
		String fileName = System.currentTimeMillis() + ".png";
		File file = new File(Constants.path + Constants._image, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 最后通知图库更新
		MediaScannerConnection.scanFile(context, new String[] { file.getAbsolutePath() }, null, null);
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
	}

	/**
	 * 获取图片
	 * 
	 * @param url
	 *            图片链接
	 * @return
	 */
	public static Bitmap downloadImage(Context context, String url) {
		if (url == null || "".equals(url)) {
			return null;
		}

		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为ture只获取图片大小
		opts.inJustDecodeBounds = true;
		// 返回为空
		BitmapFactory.decodeFile(url, opts);
		int width = opts.outWidth;
		opts.inSampleSize = width / context.getResources().getDimensionPixelSize(R.dimen.image_size);
		opts.inSampleSize = opts.inSampleSize > 1 ? opts.inSampleSize : 1;
		opts.inJustDecodeBounds = false;
		opts.inPreferredConfig = Config.RGB_565;

		return BitmapFactory.decodeFile(url, opts);
	}

}
