package com.heasy.knowroute.activity;

import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Administrator on 2020/10/13.
 */
abstract class BaseActivity extends AppCompatActivity {
    /**
     * 隐藏状态栏
     */
    protected void hideStatusBar(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * 隐藏actionBar
     */
    protected void hideActionBar(){
		if(getSupportActionBar() != null){
			getSupportActionBar().hide();
		}
    }
}
