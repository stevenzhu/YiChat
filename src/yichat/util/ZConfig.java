package yichat.util;

import android.text.TextUtils;

/**
 * Created by cheng on 2017/11/30.
 */

public class ZConfig {

    public static boolean isDebug=true;
    //假装-app.getEx无效
    public static boolean mockAppEx_notAvail=false;
    static{
        //debug
//        ZStore.getThis().set_isFirst_JiYi(true);

        //发布模式
        if(!ZUtil.isDebugMode()){
            isDebug=false;
            mockAppEx_notAvail=false;
        }
    }

    //------

    public static final String baseUrl="http://www.hanlinyuanonline.com";

    //"验证码"倒计时"总s数"
    public static final int CodeTimeDef=60;

    public static final int DefWordCnt_PerChp=15;

    public static final int Delay_ShowKb=100;


}
