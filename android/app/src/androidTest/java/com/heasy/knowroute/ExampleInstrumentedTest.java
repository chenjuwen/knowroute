package com.heasy.knowroute;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.heasy.knowroute", appContext.getPackageName());
    }

    @Test
    public void testCount(){
        int seconds = 60;
        //倒计时
        new CountDownTimer(seconds * 1000 + 1050, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                System.out.println((millisUntilFinished / 1000 - 1) + "秒后重试");
            }

            @Override
            public void onFinish() {
                System.out.println("完成");
            }
        }.start();
    }
}
