package com.xihabang.wujike.test;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private static final int START = 0;
    private static final int PAUSE = 1;
    private static final int DELETE = 2;
    private TextView textView;



    private LinkedList<Integer> mBreakPointList = new LinkedList<>();

    private Handler handler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case START:
                    time++;

                    textView.setText(formatTime(time) + "");
                    handler.sendEmptyMessageDelayed(START, 10);
                    break;
                case PAUSE:
                    mBreakPointList.add(time);
                    handler.removeMessages(START);
                    break;
                case DELETE:
                    if (mBreakPointList.size() != 1) {
                        mBreakPointList.removeLast();
                        time = mBreakPointList.getLast();
                        textView.setText(formatTime(time) + "");
                    } else {
                        textView.setText(0 + "");
                    }
                    break;
            }
        }
    };

    private int time;


    private String formatTime(int time) {
        int mm = time / 60;
        int ss = time % 60;
        return  mm+":"+ss;
        // return mm + "."+mFormatter.format(ss)+"s";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text);
    }


    public void onTimeStart(View view) {
        handler.sendEmptyMessage(START);
    }


    public void onTimePause(View view) {
        handler.sendEmptyMessage(PAUSE);
    }


    public void onTimeDelete(View view) {
        handler.sendEmptyMessage(DELETE);
    }
}
