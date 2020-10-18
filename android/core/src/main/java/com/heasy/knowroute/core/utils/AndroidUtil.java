package com.heasy.knowroute.core.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Administrator on 2020/10/14.
 */
public class AndroidUtil {
    public static void showToast(Context context, String msg){
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0); //居中显示
        toast.show();
    }

    public static ProgressDialog showLoadingDialog(Context context, String message){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); //环形进度条
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false); //点击外部返回
        progressDialog.setIndeterminate(false); //进度条是否明确
        progressDialog.setTitle("");
        progressDialog.setMessage(message);
        progressDialog.show();
        return progressDialog;
    }
}
