package com.xihabang.wujike.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import static android.R.attr.width;

/**
 * Created by wxmylife on 2017/8/10 0010.
 */

public class ViewActivity extends AppCompatActivity {

    // /** 横向listview */
    // private MyRecyclerView recyclerView;
    // /** 封面图按钮 */
    // private ImageView img_bg;
    // /** 阴影色块left */
    // private ImageView img_left;
    // /** 阴影色块right */
    // private ImageView img_right;
    //
    // /** 列表适配器 */
    // private VideoNewCutAdapter adapter;
    //
    // /** 屏幕宽度 */
    // private int width;

    /** 左边拖动按钮 */
    private Button txt_left;
    /** 右边拖动按钮 */
    private Button txt_right;

    // /** 按下时X抽坐标 */
    private float DownX;
    //
    // /** 拖动条容器 */
    private ViewGroup.LayoutParams layoutParams_progress;
    // /** 阴影背景容器 */
    // private LayoutParams layoutParams_yin;
    // /** 拖动条的宽度 */
    // private int width_progress = 0;
    // /** 拖动条的间距 */
    // private int Margin_progress = 0;
    // /** 阴影框的宽度 */
    // private int width1_progress = 0;
    //
    // /** 不能超过右边多少 */
    // private int right_margin = 0;
    // /** 所有图片长度 */
    // private int img_widthAll = 0;
    // /** 最少保留的多少秒长度 */
    // private int last_length = 0;
    // /** 左边啦了多少 */
    // private int left_lenth = 0;
    // /** 滚动的长度 */
    // private int Scroll_lenth = 0;

    // private Handler handler = new Handler() {
    //     public void handleMessage(android.os.Message msg) {
    //         if (msg.what == 1) {
    //
    //             adapter.notifyItemInserted(msg.arg1);
    //
    //             if (msg.arg1 == 0) {
    //                 sendVideo(DisplayUtil.dip2px(VideoNewCutActivity.this, 60));
    //             }
    //
    //         } else if (msg.what == 2) {
    //
    //             img_widthAll = (int) (msg.arg1 * 1000 / picture);
    //
    //             last_length = (int) (MIN_TIME / picture);
    //
    //             if (img_widthAll < width) {
    //                 right_margin = width - img_widthAll;
    //                 LayoutParams layoutParams_right = (LayoutParams) img_right.getLayoutParams();
    //                 layoutParams_right.width = width - img_widthAll;
    //                 img_right.setLayoutParams(layoutParams_right);
    //
    //                 layoutParams_progress = (LayoutParams) relative1.getLayoutParams();
    //                 layoutParams_progress.width = img_widthAll;
    //                 layoutParams_progress.rightMargin = width - img_widthAll;
    //                 relative1.setLayoutParams(layoutParams_progress);
    //
    //                 txt_time.setText(msg.arg1 + ".0 s");
    //             } else {
    //                 img_widthAll = width;
    //                 layoutParams_progress = (LayoutParams) relative1.getLayoutParams();
    //                 layoutParams_progress.width = width;
    //                 relative1.setLayoutParams(layoutParams_progress);
    //
    //                 txt_time.setText((MAX_TIME / 1000) + ".0 s");
    //             }
    //         }
    //     };
    // };


    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        txt_left = (Button) findViewById(R.id.btn_left);
        txt_right = (Button) findViewById(R.id.btn_right);
        // // 创建一个线性布局管理器
        // LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // // 设置布局管理器
        // recyclerView.setLayoutManager(layoutManager);
        //
        // /** 一个屏幕1像素是多少毫秒 13.88888 */
        // picture = (float) MAX_TIME / (float) width;
        //
        // /** 1.66666 */
        // second_Z = (float) MAX_TIME / 1000f / ((float) width / (float) DisplayUtil.dip2px(VideoNewCutActivity.this, 60));

        widgetListener();
    }


    private void widgetListener() {
        /** 左边拖动按钮 */
        // txt_left.setOnTouchListener(new View.OnTouchListener() {
        //
        //     @Override
        //     public boolean onTouch(View v, MotionEvent event) {
        //
        //         switch (event.getAction()) {
        //             case MotionEvent.ACTION_DOWN:
        //                 DownX = event.getRawX();
        //
        //                 layoutParams_progress = (LayoutParams) relative1.getLayoutParams();
        //                 layoutParams_yin = (LayoutParams) img_left.getLayoutParams();
        //
        //                 width_progress = layoutParams_progress.width;
        //                 Margin_progress = layoutParams_progress.leftMargin;
        //                 width1_progress = layoutParams_yin.width;
        //
        //                 break;
        //             case MotionEvent.ACTION_MOVE:
        //
        //                 LeftMoveLayout(event.getRawX() - DownX, event.getRawX());
        //
        //                 break;
        //             case MotionEvent.ACTION_UP:
        //
        //                 sendVideo();
        //
        //                 layoutParams_progress = null;
        //                 layoutParams_yin = null;
        //
        //                 break;
        //             default:
        //                 break;
        //         }
        //         return false;
        //     }
        // });
        //
        // /** 右边拖动按钮 */
        // txt_right.setOnTouchListener(new OnTouchListener() {
        //
        //     @Override
        //     public boolean onTouch(View v, MotionEvent event) {
        //         switch (event.getAction()) {
        //             case MotionEvent.ACTION_DOWN:
        //                 DownX = event.getRawX();
        //
        //                 layoutParams_progress = (LayoutParams) relative1.getLayoutParams();
        //                 layoutParams_yin = (LayoutParams) img_right.getLayoutParams();
        //
        //                 width_progress = layoutParams_progress.width;
        //                 Margin_progress = layoutParams_progress.rightMargin;
        //                 width1_progress = layoutParams_yin.width;
        //
        //                 break;
        //             case MotionEvent.ACTION_MOVE:
        //
        //                 RightMoveLayout(DownX - event.getRawX());
        //
        //                 break;
        //             case MotionEvent.ACTION_UP:
        //                 layoutParams_progress = null;
        //                 layoutParams_yin = null;
        //                 break;
        //
        //             default:
        //                 break;
        //         }
        //         return false;
        //     }
        // });

        /** 滚动监听 */
        // recyclerView.setOnItemScrollChangeListener(new OnItemScrollChangeListener() {
        //
        //     @Override
        //     public void onChange(View view, int position) {
        //         Scroll_lenth = position * view.getWidth() - view.getLeft();
        //
        //         if (Scroll_lenth <= 0) {
        //             Scroll_lenth = 0;
        //         }
        //
        //         //               sendVideo();//打开注释就是边滑动变更新视图
        //     }
        //
        //     @Override
        //     public void onChangeState(int state) {
        //         if (state == 0) {// 静止情况时候才调用
        //             sendVideo();
        //         }
        //     }
        // });
    }


    /**
     * 向右边啦
     *
     * @version 1.0
     * @createTime 2015年6月18日, 上午9:44:32
     * @updateTime 2015年6月18日, 上午9:44:32
     * @createAuthor 
     * @updateAuthor 
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    private void LeftMoveLayout(float MoveX, float X) {
        // if (layoutParams_progress != null && layoutParams_yin != null) {
        //     if (Margin_progress + (int) MoveX > 0 && width_progress - (int) MoveX > last_length) {
        //         layoutParams_progress.width = width_progress - (int) MoveX;
        //         layoutParams_progress.leftMargin = Margin_progress + (int) MoveX;
        //         layoutParams_yin.width = width1_progress + (int) MoveX;
        //
        //         relative1.setLayoutParams(layoutParams_progress);
        //         img_left.setLayoutParams(layoutParams_yin);
        //
        //         txt_time.setText((float) (Math.round((layoutParams_progress.width * picture / 1000) * 10)) / 10 + " s");
        //
        //         left_lenth = layoutParams_yin.width;
        //     }
        // }
    }


    /**
     * 向左边拉
     *
     * @version 1.0
     * @createTime 2015年6月18日, 上午9:45:16
     * @updateTime 2015年6月18日, 上午9:45:16
     * @createAuthor 
     * @updateAuthor 
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    private void RightMoveLayout(float MoveX) {
        //     if (layoutParams_progress != null && layoutParams_yin != null) {
        //         if (Margin_progress + (int) MoveX > right_margin && width_progress - (int) MoveX > last_length) {
        //             layoutParams_progress.width = width_progress - (int) MoveX;
        //             layoutParams_progress.rightMargin = Margin_progress + (int) MoveX;
        //             layoutParams_yin.width = width1_progress + (int) MoveX;
        //
        //             txt_time.setText((float) (Math.round((layoutParams_progress.width * picture / 1000) * 10)) / 10 + " s");
        //
        //             relative1.setLayoutParams(layoutParams_progress);
        //             img_right.setLayoutParams(layoutParams_yin);
        //         }
        //     }
        // }

    }
}
