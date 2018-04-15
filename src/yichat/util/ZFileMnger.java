package yichat.util;

import android.os.Environment;

import com.shorigo.MyApplication;

import java.io.File;
import java.util.Date;

/**
 * Created by cheng on 2017/7/21.
 */
//getFilesDir():/data/user/0/com.artofapi.solobeat/files,/data/data/<包>/files
//getExternalCacheDir():<pkg>/cache
//getExternalCacheDirs():<pkg>/cache
//getExternalMediaDirs():%Android%/media/<pkg>
//getExternalFilesDir(Environment.DIRECTORY_DCIM):%pkg%/files/DCIM
//getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS):%pkg%/files/Documents
//x(x_DOWNLOADS):x/Download,%pkg%/files/$MuMu共享文件夹/new.apk
//x(""):%pkg%/files
//---
//getPackageResourcePath():1>/data/app/com.superpowered.crossexample-1/base.apk
//---
//Environment.getExternalStorageDirectory():/storage/emulated/0
//Environment.getExternalStorageState():mounted
//Environment.getDataDirectory():/data
//Environment.getDownloadCacheDirectory():/cache
//Environment.getRootDirectory():/system,及子元素
//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS):%sd%/Documents
//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM):%sd%/DCIM

//注:1>卸载app后,getEx也会被清空
public class ZFileMnger {

    static MyApplication app=MyApplication.main;

    public static boolean isAppExAvail(){
        if(ZConfig.mockAppEx_notAvail){return false;}
        return app.getExternalFilesDir("")!=null;
    }

    public static File getEx(String type){
        File dir=app.getExternalFilesDir(type);
        if(ZConfig.mockAppEx_notAvail){dir=null;}
        if(dir==null){
//            apk
            dir=new File(Environment.getExternalStorageDirectory(),app.getPackageName()+File.separator+type);
        }
        if(!dir.exists()){
            dir.mkdirs();
        }
        return dir;
    }

    public static File getRecDir(){
        return getEx("rec");
    }

    //m4a(为aac)
    public static File newRecFile(String name_noSuf,boolean is_m4a_orWav){
//        String suf=is_m4a_orWav?".m4a":".wav";
        String suf="";
        return  new File(getRecDir(),name_noSuf+suf);
    }

    //不一定存在
    public static File getUpdateApk(){
        //位置:%Android%/data/com.artofapi.solobeat/files/Download/new.apk
//        return new File(app.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"new.apk");
        //debug,1>模拟器-装不了,干
//        File dir=new File(app.getFilesDir(),"apk");
//        if(!dir.exists()){
//            dir.mkdirs();
//        }
//        return new File(dir,"new.apk");

        //1>优先取app.getEx,2>若无效,则取Env.getEx
        File dir=getEx("apk");
        return new File(dir,"new.apk");
    }

    static File getCrashDir_x(){
        //1>getExternalFilesDir可能返回null(sd不可用时),2>一般不会
        //nativeCrash,%Android%/data/<包>/files/Documents/crash,1>getFilesDir:/data/user/0/com.artofapi.solobeat/files/crash
        //Env.Doc需api19(ad4.4)
        //File ret=app.getExternalFilesDir("crash");
        File ret=new File(app.getFilesDir(),"crash");
        if(!ret.exists()){
            ret.mkdirs();
        }
        return ret;
    }

    public static File getCrashFile(){
        return new File(app.getFilesDir(),"crash.log");
    }

    public static File getTmpDir(){
        return getEx("tmp");
    }

    public static File getTmpFile(){
        return new File(getTmpDir(),"tmp.jpg");
    }

    public static File getTmpFile_date(){
        return new File(getTmpDir(),new Date().getTime()+".jpg");
    }

    public static File getTmpFile_date(String dotExt){
        return new File(getTmpDir(),new Date().getTime()+dotExt);
    }
















}
