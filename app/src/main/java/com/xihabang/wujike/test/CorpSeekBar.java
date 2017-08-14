package com.xihabang.wujike.test;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

/**
 * Created by wxmylife on 2017/8/14 0014.
 */

public class CorpSeekBar extends View {
    private static String TAG = "VideoSliceSeekBar";

    private static final int DRAG_OFFSET = 50;


    enum SELECT_THUMB {
        SELECT_THUMB_NONE,
        SELECT_THUMB_LEFT,
        SELECT_THUMB_MORE_LEFT,
        SELECT_THUMB_RIGHT,
        SELECT_THUMB_MORE_RIGHT,
        SELECT_THUMB_CENTER
    }


    //params
    private Bitmap thumbSlice;
    private Bitmap thumbSliceRight;
    private int progressMinDiff = 15; //percentage
    private int progressHalfHeight = 0;
    private int thumbPadding = 0;
    private float maxValue = 100f;

    private int progressMinDiffPixels;
    private int thumbSliceLeftX, thumbSliceRightX, thumbMaxSliceRightx;
    private float thumbSliceLeftValue, thumbSliceRightValue;
    private Paint paintThumb = new Paint();
    private SELECT_THUMB selectedThumb;
    private SELECT_THUMB lastSelectedThumb = SELECT_THUMB.SELECT_THUMB_NONE;
    private int thumbSliceHalfWidth;
    private SeekBarChangeListener scl;
    private int resSweepLeft = R.mipmap.icon_sweep_left, resSweepRight = R.mipmap.icon_sweep_right;
    private int resBackground = R.color.color_bg;
    private int resPaddingColor = android.R.color.holo_red_dark;

    private boolean blocked;
    private boolean isInited;

    private boolean isTouch = false;
    private boolean isDefaultSeekTotal;
    private int prevX;
    private int downX;

    private int screenWidth;

    private int lastDrawLeft;
    private int lastDrawRight;

    private static final int PADDING_BOTTOM_TOP = 5;
    private static final int PADDING_LEFT_RIGHT = 5;
    private static int MERGIN_PADDING = 20;

    private Rect viewRect = new Rect();

    private Scroller mScroller;

    private GestureDetectorCompat gestureDetector;

    public CorpSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initValue(context);

    }


    public CorpSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initValue(context);
    }


    public CorpSeekBar(Context context) {
        super(context);
        initValue(context);
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!isInited) {
            isInited = true;
            init();
        }
    }

    private void initValue(Context context) {
        mScroller=new Scroller(context);
        gestureDetector = new GestureDetectorCompat(getContext(), new SimpleGestureListener());
        //        getStyleParam();
        thumbSlice = BitmapFactory.decodeResource(getResources(), resSweepLeft);
        thumbSliceRight = BitmapFactory.decodeResource(getResources(), resSweepRight);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int itemWidth = screenWidth / 15;
        float ratio = (float) itemWidth / (float) thumbSlice.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(ratio, ratio);
        thumbSlice = Bitmap.createBitmap(thumbSlice, 0, 0, thumbSlice.getWidth(),
            thumbSlice.getHeight(), matrix, false);
        thumbSliceRight = Bitmap.createBitmap(thumbSliceRight, 0, 0, thumbSliceRight.getWidth(),
            thumbSliceRight.getHeight(), matrix, false);
        invalidate();
    }


    private void init() {
        if (thumbSlice.getHeight() > getHeight()) {
            getLayoutParams().height = thumbSlice.getHeight();
        }

        thumbSliceHalfWidth = thumbSlice.getWidth() / 2;
        //        maxValue = (getWidth() -  thumbSliceHalfWidth * 4) / (float)getWidth() * maxValue;
        progressMinDiffPixels = calculateCorrds(progressMinDiff) - 2 * thumbPadding;

        selectedThumb = SELECT_THUMB.SELECT_THUMB_NONE;
        setLeftProgress(0);
        setRightProgress(100);
        setThumbMaxSliceRightx(getWidth());

        getGlobalVisibleRect(viewRect);
        invalidate();
    }


    public void setSeekBarChangeListener(SeekBarChangeListener scl) {
        this.scl = scl;
    }


    private boolean adjustSliceXY(int mx) {

        boolean isNoneArea = false;
        int thumbSliceDistance = thumbSliceRightX - thumbSliceLeftX;
        if (thumbSliceDistance <= progressMinDiffPixels
            && selectedThumb == SELECT_THUMB.SELECT_THUMB_MORE_RIGHT
            && mx <= downX || thumbSliceDistance <= progressMinDiffPixels
            && selectedThumb == SELECT_THUMB.SELECT_THUMB_MORE_LEFT
            && mx >= downX) {
            isNoneArea = true;
        }

        if (thumbSliceDistance <= progressMinDiffPixels
            && selectedThumb == SELECT_THUMB.SELECT_THUMB_RIGHT
            && mx <= downX || thumbSliceDistance <= progressMinDiffPixels
            && selectedThumb == SELECT_THUMB.SELECT_THUMB_LEFT
            && mx >= downX) {

            isNoneArea = true;
        }

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

        if (mx > thumbMaxSliceRightx && (selectedThumb == SELECT_THUMB.SELECT_THUMB_RIGHT ||
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

    private class SimpleGestureListener extends
        GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScroller.fling(mMove, 0, (int)velocityX, (int)velocityY, 0, getMeasuredWidth(), 0, 0);
            return true;
        }
    }
    private int mMove;


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int drawLeft = thumbSliceLeftX;
        int drawRight = thumbSliceRightX;

        //边线绘制
        paintThumb.setColor(getResources().getColor(resPaddingColor));
        canvas.drawRect(drawLeft + thumbSlice.getWidth()+mMove - PADDING_LEFT_RIGHT, 0f,
            drawRight + PADDING_LEFT_RIGHT+mMove, PADDING_BOTTOM_TOP, paintThumb);
        canvas.drawRect(drawLeft + thumbSlice.getWidth() - PADDING_LEFT_RIGHT+mMove,
            thumbSlice.getHeight() - PADDING_BOTTOM_TOP, drawRight + PADDING_LEFT_RIGHT+mMove,
            thumbSlice.getHeight(), paintThumb);

        paintThumb.setColor(getResources().getColor(resBackground));
        paintThumb.setAlpha((int) (255 * 0.9));

        //图标中间背景
        canvas.drawRect(drawLeft + thumbSlice.getWidth() - PADDING_LEFT_RIGHT+mMove, PADDING_BOTTOM_TOP,
            drawRight + PADDING_LEFT_RIGHT+mMove, thumbSlice.getHeight() - PADDING_LEFT_RIGHT,
            paintThumb);

        //左右背景
        canvas.drawRect(mMove, 0, drawLeft + PADDING_LEFT_RIGHT+mMove, thumbSlice.getHeight(), paintThumb);
        canvas.drawRect(drawRight + thumbSliceRight.getWidth() - PADDING_LEFT_RIGHT+mMove, 0, getWidth()+mMove,
            thumbSlice.getHeight(), paintThumb);

        //画左右图标
        canvas.drawBitmap(thumbSlice, drawLeft+mMove, 0, paintThumb);
        canvas.drawBitmap(thumbSliceRight, drawRight+mMove, 0, paintThumb);
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        if (!blocked) {
            int mx = (int) event.getX();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mScroller.abortAnimation();
                    if (mx <= thumbSliceLeftX + thumbSliceHalfWidth * 2) {
                        if (mx >= thumbSliceLeftX) {
                            selectedThumb = SELECT_THUMB.SELECT_THUMB_LEFT;
                        } else {
                            selectedThumb = SELECT_THUMB.SELECT_THUMB_MORE_LEFT;
                        }
                    } else if (mx >= thumbSliceRightX) {
                        if (mx > thumbSliceRightX + thumbSliceHalfWidth * 2 + DRAG_OFFSET) {
                            selectedThumb = SELECT_THUMB.SELECT_THUMB_MORE_RIGHT;
                        } else if (mx >= thumbSliceRightX &&
                            mx <= thumbSliceRightX + thumbSliceHalfWidth * 2) {
                            selectedThumb = SELECT_THUMB.SELECT_THUMB_RIGHT;
                        }

                    } else if (mx > thumbSliceLeftX + thumbSliceHalfWidth * 2 &&
                        mx < thumbSliceRightX) {
                        selectedThumb = SELECT_THUMB.SELECT_THUMB_CENTER;
                    }
                    downX = mx;
                    prevX = mx;
                    if (scl != null) {
                        scl.onSeekStart();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:

                    if (selectedThumb == SELECT_THUMB.SELECT_THUMB_LEFT) {
                        thumbSliceLeftX = mx;
                    } else if (selectedThumb == SELECT_THUMB.SELECT_THUMB_RIGHT) {
                        thumbSliceRightX = mx;
                    } else if (selectedThumb == SELECT_THUMB.SELECT_THUMB_MORE_RIGHT) {

                        Log.e(TAG, "rect.left------------" + viewRect.left);
                        Log.e(TAG, "rect.top------------" + viewRect.top);
                        Log.e(TAG, "rect.right------------" + viewRect.right);
                        Log.e(TAG, "rect.bottom------------" + viewRect.bottom);
                        Log.e(TAG, "getWidth------------" + getWidth());

                        int distance = mx - prevX;
                        // smoothScrollBy(distance,0);
                        if (scl != null) {
                            scl.SeeKBarChange(distance,viewRect.left,thumbSliceLeftX,thumbSliceRightX);
                        }
                       /* LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
                        layoutParams.leftMargin=getLeft()+distance;
                        layoutParams.rightMargin=screenWidth-getRight()-distance;
                        setLayoutParams(layoutParams);*/

                   /*     VelocityTracker velocityTracker=VelocityTracker.obtain();
                        velocityTracker.addMovement(event);

                        velocityTracker.computeCurrentVelocity(1000);
                        int xVelocity= (int) velocityTracker.getXVelocity();

                        velocityTracker.clear();
                        velocityTracker.recycle();*/

                        // 速度=（终点-起点）／时间段
                        // 平移动画TranslateAnimation
                        //
                       int translationX= (int) (getTranslationX()+distance);
                        setTranslationX(translationX);
                        // startAn(distance);
                        // smoothScrollTo(distance,0);
                        // smoothScrollBy(distance, 0);
                        // smoothScrollBy(xPosition - mLastX, 0);
                        // float rawX = event.getRawX();
                        // float deltaX = rawX - x;
                        // if (viewRect.left + deltaX+thumbSliceLeftX >= 0) {
                        //     setTranslationX(deltaX);
                        // }
                        // TODO: 2017/8/14 0014  View整体右移
                        // thumbSliceRightX += distance;
                    } else if (selectedThumb == SELECT_THUMB.SELECT_THUMB_MORE_LEFT) {
                        // TODO: 2017/8/14 0014  View整体左移
                        // float rawX = event.getRawX();
                        // float deltaX = rawX - x;
                        // if (viewRect.left+thumbSliceRightX+deltaX<=screenWidth-thumbSliceHalfWidth*2){
                            // offsetLeftAndRight((int) deltaX);
                            //  setTranslationX(deltaX);
                        // }
                        int distance = mx - prevX;
                        int translationX= (int) (getTranslationX()+distance);
                        setTranslationX(translationX);
                        // thumbSliceLeftX += distance;
                    } else if (selectedThumb == SELECT_THUMB.SELECT_THUMB_CENTER) {
                        int distance = mx - prevX;
                        if (thumbSliceLeftX + distance > 0 && thumbSliceRightX + distance <
                            thumbMaxSliceRightx - thumbSliceHalfWidth * 2) {
                            thumbSliceLeftX += distance;
                            thumbSliceRightX += distance;
                        }
                    }

                    if (adjustSliceXY(mx)) {
                        break;
                    }
                    prevX = mx;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:

                    downX = mx;
                    adjustSliceXY(mx);
                    selectedThumb = SELECT_THUMB.SELECT_THUMB_NONE;
                    if (scl != null) {
                        scl.onSeekEnd();
                    }
                    break;
            }

            if (mx != downX) {
                isTouch = true;
                notifySeekBarValueChanged();
            }
        }

        return true;
    }


    public void startAn(int value) {
        // if (value >= 30 && value <= 120) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(getMoveAnimation(value));
        set.start();
        // }

    }
    //
    // public void slideview(final float p1, final float p2) {
    //     TranslateAnimation animation = new TranslateAnimation(p1, p2, 0, 0);
    //     animation.setInterpolator(new OvershootInterpolator());
    //     animation.setDuration(1000);
    //     // animation.setStartOffset(delayMillis);
    //     animation.setAnimationListener(new Animation.AnimationListener() {
    //         @Override
    //         public void onAnimationStart(Animation animation) {
    //         }
    //
    //         @Override
    //         public void onAnimationRepeat(Animation animation) {
    //         }
    //
    //         @Override
    //         public void onAnimationEnd(Animation animation) {
    //             int left = getLeft()+(int)(p2-p1);
    //             int top = getTop();
    //             int width = getWidth();
    //             int height = getHeight();
    //             clearAnimation();
    //             layout(left, top, left+width, top+height);
    //         }
    //     });
    //     startAnimation(animation);
    // }




    public ValueAnimator getMoveAnimation(int value) {
        ValueAnimator animator = ValueAnimator.ofInt(mMove, value);
        animator.setDuration(1500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                mMove = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        return animator;
    }

    //调用此方法滚动到目标位置
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }


    //调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {
        Log.e(TAG, "smoothScrollBy:" + dx);
        //设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        // invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效
        // scrollBy(dx, 0);
        // mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    @Override
    public void computeScroll() {
        Log.e(TAG, "-----------computeScroll");
        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            //这里调用View的scrollTo()完成实际的滚动

            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // mMove = mScroller.getCurrX();
            // Log.e(TAG, "mMove:" + mMove);
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
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
        if (scl != null) {
            calculateThumbValue();

            if (isTouch) {
                if (selectedThumb == SELECT_THUMB.SELECT_THUMB_LEFT ||
                    selectedThumb == SELECT_THUMB.SELECT_THUMB_MORE_LEFT) {
                    scl.SeekBarValueChanged(thumbSliceLeftValue, thumbSliceRightValue, 0);
                } else if (selectedThumb == SELECT_THUMB.SELECT_THUMB_RIGHT ||
                    selectedThumb == SELECT_THUMB.SELECT_THUMB_MORE_RIGHT) {
                    scl.SeekBarValueChanged(thumbSliceLeftValue, thumbSliceRightValue, 1);
                } else {
                    scl.SeekBarValueChanged(thumbSliceLeftValue, thumbSliceRightValue, 2);
                }
            }
        }

        isTouch = false;
    }


    private void calculateThumbValue() {
        if (0 == getWidth()) {
            return;
        }
        thumbSliceLeftValue = maxValue * thumbSliceLeftX / (getWidth() - thumbSliceHalfWidth * 2);
        thumbSliceRightValue = maxValue * thumbSliceRightX / (getWidth() - thumbSliceHalfWidth * 2);
    }


    private int calculateCorrds(int progress) {
        return (int) ((getWidth() - thumbSliceHalfWidth * 2) / maxValue * progress);
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


    public float getLeftProgress() {
        return thumbSliceLeftValue;
    }


    public float getRightProgress() {
        return thumbSliceRightValue;
    }


    public void setProgress(int leftProgress, int rightProgress) {
        if (rightProgress - leftProgress >= progressMinDiff) {
            thumbSliceLeftX = calculateCorrds(leftProgress);
            thumbSliceRightX = calculateCorrds(rightProgress);
        }
        notifySeekBarValueChanged();
    }


    public void setSliceBlocked(boolean isBLock) {
        blocked = isBLock;
        invalidate();
    }


    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }


    public void setProgressMinDiff(int progressMinDiff) {
        this.progressMinDiff = progressMinDiff;
        progressMinDiffPixels = calculateCorrds(progressMinDiff);
    }


    public void setProgressHeight(int progressHeight) {
        this.progressHalfHeight = progressHalfHeight / 2;
        invalidate();
    }


    public void setThumbSlice(Bitmap thumbSlice) {
        this.thumbSlice = thumbSlice;
        init();
    }


    public void setThumbPadding(int thumbPadding) {
        this.thumbPadding = thumbPadding;
        invalidate();
    }


    public void setThumbMaxSliceRightx(int maxRightThumb) {
        this.thumbMaxSliceRightx = maxRightThumb;
    }


    public interface SeekBarChangeListener {
        void SeekBarValueChanged(float leftThumb, float rightThumb, int whitchSide);
        void SeeKBarChange(int distance,int viewLeft,int leftIconX,int rightIconX);
        void onSeekStart();
        void onSeekEnd();
    }

}
