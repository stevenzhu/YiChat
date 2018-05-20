package utils;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public final class MD5 {
  public interface Callback {
    void onComplete(byte[] md5);
  }

  private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

  private MD5() {}

  public static String digestToString(final byte[] md5) {
    if (md5 == null) {
      return "";
    }

    final StringBuilder sb = new StringBuilder(32);
    for (byte b : md5) {
      sb.append(HEX_DIGITS[(b >> 4) & 0x0f]);
      sb.append(HEX_DIGITS[b & 0x0f]);
    }
    return sb.toString();
  }

  public static byte[] bytesMD5(final byte[] bytes) {
    byte[] md5 = null;
    try {
      final MessageDigest digest = MessageDigest.getInstance("MD5");
      digest.update(bytes);
      md5 = digest.digest();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return md5;
  }

  public static byte[] fileMD5(final File file) {
    InputStream inputStream = null;
    byte[] md5 = null;
    try {
      final MessageDigest digest = MessageDigest.getInstance("MD5");
      inputStream = new FileInputStream(file.getAbsolutePath());
      final byte[] buffer = new byte[4 * 1024];
      int readBytes;
      while ((readBytes = inputStream.read(buffer)) > 0) {
        digest.update(buffer, 0, readBytes);
      }

      md5 = digest.digest();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return md5;
  }

  public static void asyncBytesMD5(final byte[] bytes, final Callback callback) {
    final AsyncTask<byte[], Void, byte[]> task = new AsyncTask<byte[], Void, byte[]>() {
      @Override
      protected byte[] doInBackground(byte[]... params) {
        return bytesMD5(params[0]);
      }

      @Override
      protected void onPostExecute(byte[] s) {
        callback.onComplete(s);
      }
    };
    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bytes);
  }

  public static void asyncFileMD5(final File file, final Callback callback) {
    final AsyncTask<File, Void, byte[]> task = new AsyncTask<File, Void, byte[]>() {
      @Override
      protected byte[] doInBackground(File... params) {
        return fileMD5(params[0]);
      }

      @Override
      protected void onPostExecute(byte[] s) {
        callback.onComplete(s);
      }
    };
    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, file);
  }

  
}
