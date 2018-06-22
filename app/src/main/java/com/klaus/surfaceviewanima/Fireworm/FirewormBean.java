package com.klaus.surfaceviewanima.Fireworm;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.klaus.surfaceviewanima.CommonUtils;
import com.klaus.surfaceviewanima.IEffectBean;

import java.util.Random;

/**
 * Created by klaus on 2018/6/20.
 */
public class FirewormBean extends IEffectBean {

    private String TAG=getClass().getSimpleName();

    /**
     * 第一个控制点的区域
     */
    private Rect mRect0;

    /**
     * 第二个控制点的区域,需要根据第一个控制点的位置来计算第二个控制点
     */
    private Rect mRect1;

    private int mScreenHeight;

    private int mScreenWidth;

    /**
     * 控制区域的宽度(正方形)
     */
    private int mWidth;

    private int mHeight;

    /**
     * 起点,控制点0,控制点1,终点,当前坐标
     */
    public Point mStartPoint, mControlPoint0, mControlPoint1, mEndPoint, mCurrentPoint;

    /**
     * 移动动画
     */
    private ValueAnimator moveAnim;
    /**
     * 透明动画
     */
    private ValueAnimator alphaAnim;
    /**
     * 透明度
     */
    public int alpha = 255;
    /**
     * icon
     */
    private Bitmap mBitmap;
    /**
     * 绘制bitmap的矩阵  用来做缩放和移动的
     */
    private Matrix matrix = new Matrix();
    /**
     * 缩放系数
     */
    private float sf = 0;
    /**
     * 产生随机数
     */
    private Random random;
    /**
     * 动画是否结束
     */
    private boolean isEnd;

    public FirewormBean(Context context, Bitmap bitmap) {
        mScreenHeight = CommonUtils.getScreenHeight(context);
        mScreenWidth = CommonUtils.getScreenWidth(context);
        mWidth = mScreenWidth / 6;
        random = new Random();
        mBitmap=bitmap;
        reset();
    }

    int leftBroadPoint;
    int rightBroadPoint;
    boolean isGoLeft;


    public void reset() {

        isEnd=false;
        mStartPoint = new Point(random.nextInt(mScreenWidth), mScreenHeight);
        mCurrentPoint=mStartPoint;
        leftBroadPoint=mCurrentPoint.x-mWidth/2;
        rightBroadPoint=mCurrentPoint.x+mWidth/2;
        isGoLeft=random.nextBoolean();
        mEndPoint = new Point(random.nextInt(mWidth) + mStartPoint.x - mWidth / 2, 0-mBitmap.getHeight());
        mRect0 = new Rect(mStartPoint.x - mWidth, mScreenHeight / 4 * 3 - mWidth / 2, mStartPoint.x, mScreenHeight / 4 * 3 + mWidth / 2);
        mRect1 = new Rect(mStartPoint.x, mScreenHeight / 4 - mWidth / 2, mStartPoint.x+mWidth, mScreenHeight / 4 + mWidth / 2);
        mControlPoint0 = getPointFromRect(mRect0);
        mControlPoint1 = getPointFromRect(mRect1);
        //贝塞尔曲线移动动画
//        moveAnim = ValueAnimator.ofObject(new BezierEvaluator(mControlPoint0, mControlPoint1),mStartPoint,mEndPoint);
//        moveAnim.setDuration(new Random().nextInt(3000) + 10000);
//        moveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                if (Looper.myLooper() == Looper.getMainLooper()) { // UI线程
//                    Log.e(TAG,"UI线程");
//                } else { // 非UI线程
//                    Log.e(TAG,"非UI线程");
//                }
//                mCurrentPoint = (Point) animation.getAnimatedValue();
//            }
//        });

        alphaAnim = ValueAnimator.ofInt(random.nextInt(80), 200+random.nextInt(55)).setDuration(new Random().nextInt(1000) + 1000);
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alpha = (int) animation.getAnimatedValue();
            }
        });
        alphaAnim.setRepeatMode(ValueAnimator.REVERSE);
        alphaAnim.setRepeatCount(ValueAnimator.INFINITE);
//        moveAnim.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                alphaAnim.cancel();
//                reset();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        moveAnim.start();
        alphaAnim.start();
    }

    public void stop() {
        if (moveAnim != null) {
            moveAnim.cancel();
            moveAnim = null;
        }
        if (alphaAnim != null) {
            alphaAnim.cancel();
            alphaAnim = null;
        }
    }


    public void onDraw(Canvas canvas, Paint paint) {
        if (mBitmap != null && !isEnd) {
//            canvas.save();
            paint.setAlpha(alpha);
//            matrix.postTranslate(bitmap.getWidth() , bitmap.getHeight() );
            canvas.drawBitmap(mBitmap, mCurrentPoint.x,mCurrentPoint.y, paint);
            mCurrentPoint.y=  mCurrentPoint.y-2;
            if(isGoLeft){
                mCurrentPoint.x=  mCurrentPoint.x-1;
            }else{
                mCurrentPoint.x=  mCurrentPoint.x+2;
            }
            if(isGoLeft && mCurrentPoint.x<leftBroadPoint){
                isGoLeft=false;
            }
            if(!isGoLeft && mCurrentPoint.x>rightBroadPoint){
                isGoLeft=true;
            }
            if(mCurrentPoint.y<=0){
                reset();
            }
//            canvas.restore();
        }
    }

    public class BezierEvaluator implements TypeEvaluator<Point> {

        private Point point1;
        private Point point2;

        public BezierEvaluator(Point point1, Point point2){
            this.point1 = point1;
            this.point2 = point2;
        }

        @Override
        public Point evaluate(float time, Point startValue,
                              Point endValue) {

            float timeLeft = 1.0f - time;
            Point point = new Point();//结果

            point.x = (int) (timeLeft * timeLeft * timeLeft * (startValue.x)
                    + 3 * timeLeft * timeLeft * time * (point1.x)
                    + 3 * timeLeft * time * time * (point2.x)
                    + time * time * time * (endValue.x));

            point.y = (int) (timeLeft * timeLeft * timeLeft * (startValue.y)
                    + 3 * timeLeft * timeLeft * time * (point1.y)
                    + 3 * timeLeft * time * time * (point2.y)
                    + time * time * time * (endValue.y));
            return point;
        }
    }

    private Point getPointFromRect(Rect rect) {
        int x = random.nextInt(rect.width()) + rect.left;
        int y = random.nextInt(rect.height()) + rect.top;
        return new Point(x, y);
    }
}
