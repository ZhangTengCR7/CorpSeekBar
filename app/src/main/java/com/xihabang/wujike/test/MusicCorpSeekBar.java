package com.xihabang.wujike.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by wxmylife on 2017/8/10 0010.
 */

public class MusicCorpSeekBar extends View {
    private static final int DRAG_OFFSET = 20;
    private Bitmap thumbSliceLeft;
    private Bitmap thumbSliceRight;

    private Paint paintThumb = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final int resSweepLeft = R.mipmap.icon_sweep_left;
    private final int resSweepRight = R.mipmap.icon_sweep_right;

    private final int resBackground = R.color.color_bg;
    private final int resPadding = R.color.padding_color;

    private int screenWidth;//屏幕宽度
    private float maxValue = 100f;
    private int thumbSliceHalfWidth;

    private int thumbSliceLeftX, thumbSliceRightX, thumbMaxSliceRightx;//代表a.b在控件最终长度
    private float thumbSliceLeftValue, thumbSliceRightValue;

    private static final int PADDING_LEFT_RIGHT = 5;
    private static final int PADDING_BOTTOM_TOP = 10;
    private static int MERGIN_PADDING = 20;

    private SELECT_THUMB selectedThumb;

    private boolean isInited;
    private boolean isTouch;
    private boolean blocked;
    private boolean isDefaultSeekTotal;

    private int progressMinDiffPixels;
    private int progressMinDiff = 15;//进度最小百分比
    private int thumbPadding = 0;

    private int prevX;
    private int downX;//按下起始X点


    private SeekBarChangeListener changeListener;


    enum SELECT_THUMB {
        SELECT_THUMB_NONE,
        SELECT_THUMB_LEFT,
        SELECT_THUMB_MORE_LEFT,
        SELECT_THUMB_RIGHT,
        SELECT_THUMB_MORE_RIGHT,
        SELECT_THUMB_CENTER,
        SELECT_THUMB_CENTER_LEFT,
        SELECT_THUMB_CENTER_RIGHT
    }


    public MusicCorpSeekBar(Context context) {
        super(context);
        initValue(context);
    }


    public MusicCorpSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initValue(context);
    }


    public MusicCorpSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValue(context);
    }


    public void setSeekBarChangeListener(SeekBarChangeListener listener) {
        this.changeListener = listener;
    }


    private void initValue(Context context) {

        thumbSliceLeft = BitmapFactory.decodeResource(getResources(), resSweepLeft);
        thumbSliceRight = BitmapFactory.decodeResource(getResources(), resSweepRight);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;

        //？？？？？？？
        int itemWidth = screenWidth / 15;
        float ratio = (float) itemWidth / (float) thumbSliceLeft.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(ratio, ratio);
        thumbSliceLeft = Bitmap.createBitmap(thumbSliceLeft, 0, 0, thumbSliceLeft.getWidth(),
            thumbSliceLeft.getHeight(), matrix, false);
        thumbSliceRight = Bitmap.createBitmap(thumbSliceRight, 0, 0, thumbSliceRight.getWidth(),
            thumbSliceRight.getHeight(), matrix, false);

        invalidate();
    }


    @Override public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!isInited) {
            isInited = true;
            init();
        }
    }


    private void init() {
        //如果图标的高大于控件高度，将图标高度赋值给控件高度
        if (thumbSliceLeft.getHeight() > getHeight()) {
            getLayoutParams().height = thumbSliceLeft.getHeight();
        }
        //半个图标宽度
        thumbSliceHalfWidth = thumbSliceLeft.getWidth() / 2;

        progressMinDiffPixels = calculateCorrds(progressMinDiff) - 2 * thumbSliceHalfWidth;

        selectedThumb = SELECT_THUMB.SELECT_THUMB_NONE;

        //设置左边图标进度
        setLeftProgress(0);
        //设置右边图标进度
        setRightProgress(100);
        setThumbMaxSliceRightx(screenWidth);
        invalidate();

    }


    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // TODO: 2017/8/11 0011 画出进度条 
        paintThumb.setColor(getResources().getColor(R.color.colorPrimary));
        canvas.drawRect(0, 0, getWidth(), thumbSliceLeft.getHeight(), paintThumb);

        int drawLeft = thumbSliceLeftX;
        int drawRight = thumbSliceRightX;

        paintThumb.setColor(getResources().getColor(resPadding));

        canvas.drawRect(drawLeft + thumbSliceLeft.getWidth() - PADDING_LEFT_RIGHT, 0f,
            drawRight + PADDING_LEFT_RIGHT, PADDING_BOTTOM_TOP, paintThumb);

        canvas.drawRect(drawLeft + thumbSliceRight.getWidth() - PADDING_LEFT_RIGHT,
            thumbSliceRight.getHeight() - PADDING_BOTTOM_TOP, drawRight + PADDING_LEFT_RIGHT,
            thumbSliceRight.getHeight(), paintThumb);

        paintThumb.setColor(getResources().getColor(resBackground));
        paintThumb.setAlpha((int) (255 * 0.9));

        // canvas.drawRect(0, 0, drawLeft + PADDING_LEFT_RIGHT, thumbSliceLeft.getHeight(), paintThumb);
        // canvas.drawRect(drawRight + thumbSliceRight.getWidth() - PADDING_LEFT_RIGHT, 0, getWidth(),
        //     thumbSliceLeft.getHeight(), paintThumb);

        canvas.drawBitmap(thumbSliceLeft, drawLeft, 0, paintThumb);
        canvas.drawBitmap(thumbSliceRight, drawRight, 0, paintThumb);
    }
    private float x = 0, y = 0;
    @Override public boolean onTouchEvent(MotionEvent event) {
        if (!blocked) {
            int touchX = (int) event.getX();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getRawX();
                    y = event.getRawY();
                    Log.e("TVT", "--------->>>>ACTION_DOWN");
                    // if (touchX <= thumbSliceLeftX + thumbSliceHalfWidth * 2 + DRAG_OFFSET) {
                    if (touchX <= thumbSliceLeftX + thumbSliceHalfWidth * 2) {
                        if (touchX >= thumbSliceLeftX) {
                            Log.e("TVT", "--------->>>>SELECT_THUMB_LEFT");
                            selectedThumb = SELECT_THUMB.SELECT_THUMB_LEFT;
                        } else {
                            Log.e("TVT", "--------->>>>SELECT_THUMB_MORE_LEFT");
                            selectedThumb = SELECT_THUMB.SELECT_THUMB_MORE_LEFT;
                        }
                        // } else if (touchX >= thumbSliceRightX - thumbSliceHalfWidth * 2 - DRAG_OFFSET) {
                    } else if (touchX >= thumbSliceRightX - thumbSliceHalfWidth * 2-DRAG_OFFSET) {

                        if (touchX > thumbSliceRightX+thumbSliceHalfWidth*2+DRAG_OFFSET) {
                            Log.e("TVT", "--------->>>>SELECT_THUMB_MORE_RIGHT");
                            selectedThumb = SELECT_THUMB.SELECT_THUMB_MORE_RIGHT;
                        } else if (touchX >= thumbSliceRightX - thumbSliceHalfWidth * 2-DRAG_OFFSET &&
                            touchX <= thumbSliceRightX+thumbSliceHalfWidth*2+DRAG_OFFSET) {
                            Log.e("TVT", "--------->>>>SELECT_THUMB_RIGHT");
                            selectedThumb = SELECT_THUMB.SELECT_THUMB_RIGHT;
                        }

                        // if (touchX <= thumbSliceRightX- thumbSliceHalfWidth * 2 ) {
                        //     Log.e("TVT", "--------->>>>SELECT_THUMB_RIGHT");
                        //     selectedThumb = SELECT_THUMB.SELECT_THUMB_RIGHT;
                        // } else {
                        //     Log.e("TVT", "--------->>>>SELECT_THUMB_MORE_RIGHT");
                        //     selectedThumb = SELECT_THUMB.SELECT_THUMB_MORE_RIGHT;
                        // }
                    } else {
                        Log.e("TVT", "--------->>>>中间区域");
                        selectedThumb = SELECT_THUMB.SELECT_THUMB_CENTER;
                    }
                    downX = touchX;
                    prevX = touchX;
                    if (changeListener != null) {
                        changeListener.onSeekStart();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.e("TVT", "--------->>>>ACTION_MOVE");
                    if (selectedThumb == SELECT_THUMB.SELECT_THUMB_LEFT) {
                        Log.e("TVT", "----221----->>>>SELECT_THUMB_LEFT");
                        thumbSliceLeftX = touchX;
                    } else if (selectedThumb == SELECT_THUMB.SELECT_THUMB_RIGHT) {
                        Log.e("TVT", "------224--->>>>SELECT_THUMB_RIGHT");
                        thumbSliceRightX = touchX;
                    } else if (selectedThumb == SELECT_THUMB.SELECT_THUMB_MORE_RIGHT) {
                        Log.e("TVT", "--------->>>>SELECT_THUMB_MORE_RIGHT");
                        Log.e("TVT", "--------->>>>touchX----" + touchX);
                        Log.e("TVT", "--------->>>>prevX-----" + prevX);
                        int distance = touchX - prevX;
                        // thumbSliceRightX += distance;
                        int left = getLeft() + distance;
                        int right = getRight() + distance;
                        int top = getTop();
                        // int bottom = getBottom();
                        // float x=event.getX();
                        // float translationX=getTranslationX();
                        // float deltaX=(x-tX)+translationX;
                        //
                        // setTranslationX(deltaX);
                        //
                        // tX=event.getX();
                        float rawX = event.getRawX();
                        float rawY = event.getRawY();

                        float translationX = getTranslationX();
                        float translationY = getTranslationY();

                        float deltaX = (rawX - x) + translationX;
                        float deltaY = (rawY - y) + translationY;
                        final int[] location = new int[2];
                        getLocationOnScreen(location);

                        if (location[0]+deltaX>0){
                            setTranslation(deltaX, deltaY);
                        }else {
                            setTranslationX(-location[0]);
                        }


                        int xwww=location[0];

                        x = event.getRawX();
                        y = event.getRawY();

                        // setTranslationX(distance);
                        // offsetLeftAndRight(left);
                        // scrollTo(distance,getScrollY());
                        // requestLayout();
                        // layout(left, top, right, bottom);
                        // setTranslationX(distance);
                        // AdditiveAnimator.animate(this).x(distance).y(getY()).setDuration(1000).start();
                        // offsetLeftAndRight(distance);
                        // AdditiveAnimator.animate(animatedView).x(event.getX()).y(event.getY()).setDuration(1000).start();
                    } else if (selectedThumb == SELECT_THUMB.SELECT_THUMB_MORE_LEFT) {
                        Log.e("TVT", "-----246---->>>>SELECT_THUMB_MORE_LEFT");
                        int distance = touchX - prevX;
                        //
                        // float x=event.getX();
                        // float translationX=getTranslationX();
                        // float deltaX=(x-tX)+translationX;
                        //
                        // setTranslationX(deltaX);
                        //
                        // tX=event.getX();
                        // thumbSliceLeftX += distance;
                        // int left = getLeft() + distance;
                        // int right = getRight() + distance;
                        // int top = getTop();
                        // int bottom = getBottom();
                        // offsetLeftAndRight(distance);
                        // layout(left,top,right,bottom);
                        // this.setX(left);
                        // requestLayout();
                        // layout(left, top, right, bottom);
                    } else if (selectedThumb == SELECT_THUMB.SELECT_THUMB_CENTER) {
                        Log.e("TVT", "-----259---->>>>SELECT_THUMB_CENTER");
                        int distance = touchX - prevX;
                        if (thumbSliceLeftX==0||thumbSliceRightX==thumbMaxSliceRightx){

                            center = thumbSliceRightX - thumbSliceLeftX;


                            if (thumbSliceLeftX == 0 ) {
                                if (touchX>prevX){
                                    selectedThumb=SELECT_THUMB.SELECT_THUMB_CENTER_RIGHT;
                                    // thumbSliceRightX += distance;
                                    // thumbSliceLeftX += distance;
                                }
                                thumbSliceRightX = thumbSliceLeftX + center;
                            }
                            if (thumbSliceRightX == thumbMaxSliceRightx ) {
                                if (touchX<prevX){
                                    selectedThumb=SELECT_THUMB.SELECT_THUMB_CENTER_LEFT;
                                    // thumbSliceRightX += distance;
                                    // thumbSliceLeftX += distance;
                                }
                                Log.e("TVT", "----右边----->>>>---" );
                                thumbSliceLeftX = thumbSliceRightX - center;
                            }

                        }else {
                            thumbSliceRightX += distance;
                            thumbSliceLeftX += distance;
                        }

                    }else if (selectedThumb==SELECT_THUMB.SELECT_THUMB_CENTER_LEFT){
                        Log.e("TVT", "---center-左边----->>>>---" );
                        int distance = touchX - prevX;
                        thumbSliceRightX += distance;
                        thumbSliceLeftX += distance;
                    }else if (selectedThumb==SELECT_THUMB.SELECT_THUMB_CENTER_RIGHT){
                        Log.e("TVT", "--center--右边----->>>>---" );
                        int distance = touchX - prevX;
                        thumbSliceRightX += distance;
                        thumbSliceLeftX += distance;
                    }

                    //>>>>>>>

                    if (adjustSliceXY(touchX)) {
                        break;
                    }

                    prevX = touchX;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:

                    downX = touchX;
                    adjustSliceXY(touchX);
                    //>>>>>>>>>
                    selectedThumb = SELECT_THUMB.SELECT_THUMB_NONE;
                    if (changeListener != null) {
                        changeListener.onSeekEnd();
                    }
                    break;
            }

            if (touchX != downX) {
                isTouch = true;
                notifySeekBarValueChanged();
            }

        }
        return true;
    }

    private void setTranslation(float deltaX, float deltaY) {
        // 正数往右，负数往左
        setTranslationX(deltaX);
        // setTranslationY(deltaY);
    }

    private int center;

    private boolean adjustSliceXY(int touchX) {

        boolean isNoneArea = false;




        int thumbSliceDistance = thumbSliceRightX - thumbSliceLeftX;
        Log.e("TVT", "adjustSliceXY-------------thumbSliceRightX----" + thumbSliceRightX);
        Log.e("TVT", "adjustSliceXY-------------thumbSliceLeftX----" + thumbSliceLeftX);
        Log.e("TVT", "adjustSliceXY-------------thumbSliceDistance----" + thumbSliceDistance);
        Log.e("TVT", "adjustSliceXY---------progressMinDiffPixels------" + progressMinDiffPixels);
        if (thumbSliceDistance <= progressMinDiffPixels
            && selectedThumb == SELECT_THUMB.SELECT_THUMB_MORE_RIGHT
            && touchX <= downX || thumbSliceDistance <= progressMinDiffPixels
            && selectedThumb == SELECT_THUMB.SELECT_THUMB_MORE_LEFT
            && touchX >= downX) {
            isNoneArea = true;
        }

        if (thumbSliceDistance <= progressMinDiffPixels
            && selectedThumb == SELECT_THUMB.SELECT_THUMB_RIGHT
            && touchX <= downX || thumbSliceDistance <= progressMinDiffPixels
            && selectedThumb == SELECT_THUMB.SELECT_THUMB_LEFT
            && touchX >= downX) {

            isNoneArea = true;
        }

        Log.e("TVT", "--------->>>>isNoneArea---" + isNoneArea);
        if (isNoneArea) {
            if (selectedThumb == SELECT_THUMB.SELECT_THUMB_RIGHT ||
                selectedThumb == SELECT_THUMB.SELECT_THUMB_MORE_RIGHT) {
                thumbSliceRightX = thumbSliceLeftX + progressMinDiffPixels;
            } else if (selectedThumb == SELECT_THUMB.SELECT_THUMB_LEFT ||
                selectedThumb == SELECT_THUMB.SELECT_THUMB_MORE_LEFT) {
                thumbSliceLeftX = thumbSliceRightX - progressMinDiffPixels;
            }
            return true;
        }

        // if (touchX>thumbMaxSliceRightx&&(selectedThumb==SELECT_THUMB.SELECT_THUMB_CENTER)){
        //     Log.e("TVT",thumbMaxSliceRightx+"-------thumbMaxSliceRightx");
        //     thumbSliceRightX=thumbMaxSliceRightx;
        //     thumbSliceLeftX=
        //     return true;
        //
        // }

        if (touchX > thumbMaxSliceRightx && (selectedThumb == SELECT_THUMB.SELECT_THUMB_RIGHT ||
            selectedThumb == SELECT_THUMB.SELECT_THUMB_MORE_RIGHT)) {
            thumbSliceRightX = thumbMaxSliceRightx;
            return true;
        }

        if (thumbSliceRightX >= (getWidth() - thumbSliceHalfWidth * 2) - MERGIN_PADDING) {
            thumbSliceRightX = getWidth() - thumbSliceHalfWidth * 2;
        }

        if (thumbSliceLeftX < MERGIN_PADDING) {
            thumbSliceLeftX = 0;
        }

        return false;
    }


    public void setProgress(int leftProgress, int rightProgress) {
        if (rightProgress - leftProgress >= progressMinDiff) {
            thumbSliceLeftX = calculateCorrds(leftProgress);
            thumbSliceRightX = calculateCorrds(rightProgress);
        }
        notifySeekBarValueChanged();
    }


    public void setLeftProgress(int progress) {
        if (progress <= thumbSliceRightValue - progressMinDiff) {
            thumbSliceLeftX = calculateCorrds(progress);
        }
        notifySeekBarValueChanged();
    }


    public void setRightProgress(int progress) {
        if (progress >= thumbSliceLeftValue + progressMinDiff) {
            thumbSliceRightX = calculateCorrds(progress);
            if (!isDefaultSeekTotal) {
                isDefaultSeekTotal = true;
            }
        }
        notifySeekBarValueChanged();
    }


    public void setThumbMaxSliceRightx(int maxRightThumb) {
        this.thumbMaxSliceRightx = maxRightThumb;
    }


    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }


    public void setThumbPadding(int thumbPadding) {
        this.thumbPadding = thumbPadding;
        invalidate();
    }


    public void setSliceBlocked(boolean isBLock) {
        this.blocked = isBLock;
        invalidate();
    }


    private void notifySeekBarValueChanged() {
        if (thumbSliceLeftX < thumbPadding) {
            thumbSliceLeftX = thumbPadding;
        }

        if (thumbSliceRightX < thumbPadding) {
            thumbSliceRightX = thumbPadding;
        }

        if (thumbSliceLeftX > getWidth() - thumbPadding) {
            thumbSliceLeftX = getWidth() - thumbPadding;
        }

        if (thumbSliceRightX > getWidth() - thumbPadding) {
            thumbSliceRightX = getWidth() - thumbPadding;
        }

        invalidate();

        //计算进度值
        if (changeListener != null) {
            calculateThumbValue();
            //如果触摸
            if (isTouch) {
                if (selectedThumb == SELECT_THUMB.SELECT_THUMB_LEFT ||
                    selectedThumb == SELECT_THUMB.SELECT_THUMB_MORE_LEFT) {
                    changeListener.SeekBarValueChanged(thumbSliceLeftValue, thumbSliceRightValue,
                        0);
                } else if (selectedThumb == SELECT_THUMB.SELECT_THUMB_RIGHT ||
                    selectedThumb == SELECT_THUMB.SELECT_THUMB_MORE_RIGHT) {
                    changeListener.SeekBarValueChanged(thumbSliceLeftValue, thumbSliceRightValue,
                        1);
                } else {
                    changeListener.SeekBarValueChanged(thumbSliceLeftValue, thumbSliceRightValue,
                        2);
                }
            }
        }

        isTouch = false;
    }


    private int calculateCorrds(int progress) {
        //控件实际总长/最大值*进度=最终长度
        return (int) ((getWidth() - thumbSliceHalfWidth * 2) / maxValue * progress);
    }


    private void calculateThumbValue() {
        if (getWidth() == 0) {
            return;
        }
        thumbSliceLeftValue = maxValue * thumbSliceLeftX / (getWidth() - thumbSliceHalfWidth * 2);
        thumbSliceRightValue = maxValue * thumbSliceRightX / (getWidth() - thumbSliceHalfWidth * 2);
    }


    public float getLeftProgress() {
        return thumbSliceLeftValue;
    }


    public float getRightProgress() {
        return thumbSliceRightValue;
    }


    public interface SeekBarChangeListener {
        void SeekBarValueChanged(float leftThumb, float rightThumb, int whitchSide);
        void onSeekStart();
        void onSeekEnd();
    }



}
