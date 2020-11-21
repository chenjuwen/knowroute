package com.heasy.knowroute.core.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Administrator on 2020/10/14.
 */
public class AndroidUtil {
    public static void showToast(Context context, String msg){
        showToast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String msg, int duration){
        Toast toast = Toast.makeText(context, msg, duration);
        toast.setGravity(Gravity.CENTER, 0, 0); //居中显示
        toast.show();
    }

    public static ProgressDialog showLoadingDialog(Context context, String message){
        ProgressDialog progressDialog = new ProgressDialog(context);

        //progressDialog.getWindow().getAttributes().gravity = Gravity.CENTER; //居中

        //对话框宽度
        //WindowManager.LayoutParams params = progressDialog.getWindow().getAttributes();
        //params.width = 200;
        //progressDialog.getWindow().setAttributes(params);

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); //环形进度条
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false); //点击外部返回
        progressDialog.setIndeterminate(false); //进度条是否明确
        progressDialog.setTitle("");
        progressDialog.setMessage(message);
        progressDialog.show();
        return progressDialog;
    }

    /**
     * 获取应用程序显示区域
     * @param activity
     * @return
     */
    public static Point getDisplaySize(Activity activity){
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        return point;
    }
}
