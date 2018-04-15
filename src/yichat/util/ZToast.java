package yichat.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.shorigo.MyApplication;


/**
 * 支持在子线程调用(toast默认只能在主线程调，否则报错)
 * Created by cheng on 2017/6/7.
 */

public class ZToast {

    //1.我的课程.上传:无网络，请确认网络设置
    public static final String Tip_NetErr="网络异常!";

    public static void showNetErr(){
        show(Tip_NetErr);
    }

    public static void show(int resID){
        show(MyApplication.main.getString(resID));
    }
    public static void show(String msg){
        if(msg==null){msg="";}
        //主线程,直接上
        if(ZUtil.isInMainThread()){
            toast(msg);
            return;
        }
        hd.sendMessage(hd.obtainMessage(Wt_Toast,msg));
    }

    static final int Wt_Toast=1000;
    public static Handler hd=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Wt_Toast:
                    toast((String)msg.obj);
                    break;
            }
        }
    };
    public static void postDelayed(Runnable r, long delayMillis){
        hd.postDelayed(r,delayMillis);
    }

    static void toast(String msg){
        Toast.makeText(MyApplication.main, msg, Toast.LENGTH_SHORT).show();
    }


}
