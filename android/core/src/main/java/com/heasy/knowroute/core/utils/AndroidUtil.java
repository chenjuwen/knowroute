package com.heasy.knowroute.core.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.heasy.knowroute.core.R;

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

    public static Dialog showLoadingDialog(Context context){
        return showLoadingDialog(context, "");
    }

    public static Dialog showLoadingDialog(Context context, String message){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_loading, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.loadingImageView);
        TextView textView = (TextView) view.findViewById(R.id.loadingTextView);

        if(StringUtil.isNotEmpty(message)) {
            textView.setText(message);
            textView.setVisibility(View.VISIBLE);
        }else{
            textView.setVisibility(View.GONE);
        }

        //加载图片到ImageView
        Glide.with(context)
                .load(R.drawable.loading)
                .into(imageView);

        Dialog loadingDialog = new Dialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        loadingDialog.show();

        return loadingDialog;
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
