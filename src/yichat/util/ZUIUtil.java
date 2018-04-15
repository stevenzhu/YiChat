package yichat.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by cheng on 2017/12/16.
 */

public class ZUIUtil {

    static ProgressDialog dlg;

    public static void showDlg(Context ctx){
        showDlg(ctx,"加载中...");
    }

    public static void showDlg(Context ctx,String str){
        if(dlg!=null){
            dlg.dismiss();
        }
        dlg=ProgressDialog.show(ctx,null,str);
        dlg.setCancelable(true);
        dlg.setCanceledOnTouchOutside(false);
    }

    public static void finishDlg(){
        if(dlg!=null){
            dlg.dismiss();
            dlg=null;
        }
    }

//    public static void showEditDlg(Context ac, String title,String hint, final IEditDlg onOK){
//        showEditDlg(ac,title,hint,"",onOK);
//    }

//    public static void showEditDlg(Context ac, String title,String hint, String defValue,final IEditDlg onOK){
//        AlertDialog.Builder bd=new AlertDialog.Builder(ac);
//        bd.setTitle(title);
//        //et
//        final EditText et=new AppCompatEditText(ac);
//        LinearLayout.LayoutParams loPa=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        et.setLayoutParams(loPa);
//        et.setSingleLine();
//        //ref
//        et.setHint(ZUtil.getStrNoNull(hint));
//        et.setText(ZUtil.getStrNoNull(defValue));
//        //lo
//        final LinearLayout lo=new LinearLayout(ac);
//        lo.addView(et);
//        lo.setPadding(60,50,60,50);
//        bd.setView(lo);
//        bd.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                onOK.onOk(et.getText().toString());
//            }
//        });
//        bd.setNegativeButton("取消", null);
//        final AlertDialog dlg=bd.show();
//        //事件
//        et.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if(ZUtil.isEnterUp(event)) {
//                    onOK.onOk(et.getText().toString());
//                    dlg.dismiss();
//                }
//                return false;
//            }
//        });
//        //软键盘
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ZUtil.endCursor(et);
//                ZUtil.showKb(et);
//            }
//        },100);
//    }

    public static void showDlg_confirm(Context ac,String msg, DialogInterface.OnClickListener onConfirm) {
        showDlg_confirm(ac,"提示",msg,onConfirm);
    }

    public static void showDlg_confirm(Context ac,int titleRes, int msgRes, DialogInterface.OnClickListener onConfirm) {
        showDlg_confirm(ac,ZUtil.getString(titleRes),ZUtil.getString(msgRes),onConfirm);
    }
    public static void showDlg_confirm(Context ac,String title, String msg, DialogInterface.OnClickListener onConfirm) {
        new AlertDialog.Builder(ac)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("确定", onConfirm)
                .setNegativeButton("取消",null)
                .show();
    }

    public static void showMenu(Context ac,CharSequence[] items, DialogInterface.OnClickListener cb){
        new android.app.AlertDialog.Builder(ac).setItems(items,cb).show();
    }


    //-inner-----
    public interface IEditDlg{
        void onOk(String etValue);
    }
}
