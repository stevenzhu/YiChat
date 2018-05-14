package utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

public class FileUtil {

    public static String getBytesAsBase64String(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static String getFileAsBase64String(String path) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(path);
            int length = fileInputStream.available();

            int bytesRead = 0;
            int bytesToRead = length;
            byte[] input = new byte[bytesToRead];
            while (bytesRead < bytesToRead) {
                int result = fileInputStream.read(input, bytesRead, bytesToRead - bytesRead);
                if (result == -1)
                    break;
                bytesRead += result;
            }

            if (bytesRead == length) {
                return Base64.encodeToString(input, Base64.NO_WRAP);
            }

        } catch (Exception ex) {

        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception ex) {

                }
            }
        }

        return "";
    }

    public static void AppendBytesToFile(byte[] msg, String filePath) {
        RandomAccessFile rf = null;

        try {
            rf = new RandomAccessFile(filePath, "rw");
            if (rf.length() > 1024000) {
                File file = new File(filePath);
                file.delete();
                rf.close();
                rf = new RandomAccessFile(filePath, "rw");
            }

            if (rf.length() > 0) {
                rf.seek(rf.length());
            }

            rf.write(msg);
            //rf.writeBytes("\r\n");

        } catch (Exception ex) {

        } finally {
            try {
                if (rf != null) {
                    rf.close();
                }
            } catch (final IOException e) {
            }
        }
    }

    public static File getExternalSDCardDirectory() {
        try {
            File innerDir = Environment.getExternalStorageDirectory();
            File firstExtSdCard = innerDir;

            File rootDir = innerDir.getParentFile();
            File[] files = rootDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.getName().contains("sd") && file.compareTo(innerDir) != 0) {
                        if (file.canWrite()) {
                            firstExtSdCard = file;
                            break;
                        }
                    }
                }
            }

            return firstExtSdCard;
        } catch (Exception ex) {
            return new File("/sdcard");
        }
    }

    public static String GetSDCardPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = getExternalSDCardDirectory();
        } else {
            return "/sdcard";
        }
        return sdDir.toString();
    }

    public static String getFileMD5(File file) {
        if (!file.exists() || !file.isFile()) {
            return "";
        }

        byte[] buffer = new byte[2048];
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream in = new FileInputStream(file);
            while (true) {
                int len = in.read(buffer, 0, 2048);
                if (len != -1) {
                    digest.update(buffer, 0, len);
                } else {
                    break;
                }
            }
            in.close();

            byte[] md5Bytes = digest.digest();
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();

            //String hash = new BigInteger(1,digest.digest()).toString(16);
            //return hash;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }


    //得到正确路径
    @SuppressLint("NewApi")
    public static String getPath(Context context, Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;//sdk版本是否大于4.4

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                return Environment.getExternalStorageDirectory() + "/" + split[1];
                //if ("primary".equalsIgnoreCase(type)||"9016-4EF8") {
                //   return Environment.getExternalStorageDirectory() + "/" + split[1];
                // }
            } else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /*得到传入文件的大小*/
    public static long getFileSizes(File f) throws Exception {

        long s = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s = fis.available();
            fis.close();
        } else {
            f.createNewFile();
            System.out.println("文件夹不存在");
        }

        return s;
    }

    /**
     * 转换文件大小成KB  M等
     */
    public static String FormentFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String getFileName(File file) {
        return file.getName();
    }

    public static String getFileSuffix(File file) {
        return file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase();
    }

    public static boolean fileIsExists(String localPath) {
        try {
            if (localPath == null || "".equals(localPath)) {
                return false;
            }
            File f = new File(localPath);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }
}
