package com.xihabang.wujike.test;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static android.R.attr.width;

/**
 * Created by wxmylife on 2017/8/10 0010.
 */

public class TimeActivity extends AppCompatActivity {


    /** 是否满足视频的最少播放时长 */
    private boolean isMeet = false;


    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        TimeCount timeCount = new TimeCount(30000 - old, 50);
        timeCount.start();// 开始计时

        // startRecord();

        old = now + old;

        if (old >= VIDEO_TIME * 1000) {
            isMeet = true;
        }

        timeCount.cancel();
    }


    /** 录制了多少秒 */
    private int now;
    /** 每次录制结束时是多少秒 */
    private int old;


    /**
     * 定义一个倒计时的内部类
     *
     * @author
     * @version 1.0
     * @Description
     * @date 2015-5-25
     * @Copyright: Copyright (c) 2015 Shenzhen Utoow Technology Co., Ltd.
     * All rights reserved.
     */
    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }


        @Override
        public void onFinish() {// 计时完毕时触发

        }


        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            now = (int) (30000 - millisUntilFinished - old);
            if ((old > 0 && old > VIDEO_TIME * 1000) || (old == 0 && now > VIDEO_TIME * 1000)) {
                // img_enter.setEnabled(true);
            }
            if (linear_seekbar.getChildCount() > 0) {
                ImageView img = (ImageView) linear_seekbar.getChildAt(
                    linear_seekbar.getChildCount() - 1);
                LinearLayout.LayoutParams layoutParams
                    = (LinearLayout.LayoutParams) img.getLayoutParams();
                layoutParams.width = (int) (((float) now / 1000f) * (width / VIDEO_TIME_END)) + 1;
                img.setLayoutParams(layoutParams);
            }
        }
    }


    /** 录制进度控件 */
    private LinearLayout linear_seekbar;

    /** 视频最大支持15秒 */
    public static final int VIDEO_TIME_END = 15;
    /** 视频最少必须5秒 */
    public static final int VIDEO_TIME = 5;
}
