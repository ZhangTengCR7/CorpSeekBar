package com.xihabang.wujike.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wxmylife on 2017/8/10 0010.
 */

public class ViewActivity extends AppCompatActivity {

    private CorpSeekBar corpSeekBar;

    // private ScrollerLayout layout;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        corpSeekBar= (CorpSeekBar) findViewById(R.id.seekbar);
        // layout= (ScrollerLayout) findViewById(R.id.scrollLayout);
        corpSeekBar.setSeekBarChangeListener(new CorpSeekBar.SeekBarChangeListener() {
            @Override
            public void SeekBarValueChanged(float leftThumb, float rightThumb, int whitchSide) {

            }


            @Override
            public void SeeKBarChange(int distance, int viewLeft, int leftIconX, int rightIconX) {
                // LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) corpSeekBar.getLayoutParams();
                //
                //
                //
                // layoutParams.leftMargin +=distance;
                // layoutParams.rightMargin -=distance;
                //
                //
                // if (layoutParams.leftMargin+viewLeft+leftIconX<=0){
                //     layoutParams.leftMargin=-(viewLeft+leftIconX);
                // }
                // // if (layoutParams.rightMargin)
                //
                // corpSeekBar.requestLayout();
                // corpSeekBar.setLayoutParams(layoutParams);
            }


            @Override public void onSeekStart() {

            }


            @Override public void onSeekEnd() {

            }
        });

    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

}
