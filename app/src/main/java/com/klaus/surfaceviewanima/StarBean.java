package com.klaus.surfaceviewanima;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import java.util.Random;

public class StarBean {

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
    private Bitmap bitmap;
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
    public boolean isEnd = false;//是否结束

    public StarBean(Context context, int resId) {
        mScreenHeight = CommonUtils.getScreenHeight(context);
        mScreenWidth = CommonUtils.getScreenWidth(context);
        mWidth = mScreenWidth / 6;
        random = new Random();
        bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        mStartPoint = new Point(random.nextInt(mScreenWidth), mScreenHeight);
        mEndPoint = new Point(random.nextInt(mWidth) + mStartPoint.x - mWidth / 2, 0-bitmap.getHeight());
        mRect0 = new Rect(mStartPoint.x - mWidth, mScreenHeight / 4 * 3 - mWidth / 2, mStartPoint.x, mScreenHeight / 4 * 3 + mWidth / 2);
        mRect1 = new Rect(mStartPoint.x, mScreenHeight / 4 - mWidth / 2, mStartPoint.x+mWidth, mScreenHeight / 4 + mWidth / 2);
        mControlPoint0 = getPointFromRect(mRect0);
        mControlPoint1 = getPointFromRect(mRect1);
        init(mStartPoint,mControlPoint0,mControlPoint1,mEndPoint);
    }

    private void init(Point startPoint, Point controlPoint0, Point controlPoint1, Point endPoint) {
//        贝塞尔曲线移动动画
        moveAnim = ValueAnimator.ofObject(new BezierEvaluator(mControlPoint0, mControlPoint1),mStartPoint,mEndPoint);
        moveAnim.setDuration(12000);
        moveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentPoint = (Point) animation.getAnimatedValue();
            }
        });
        moveAnim.start();

        alphaAnim = ValueAnimator.ofInt(0, 255).setDuration(new Random().nextInt(2000) + 1000);
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alpha = (int) animation.getAnimatedValue();
            }
        });
        alphaAnim.setRepeatMode(ValueAnimator.REVERSE);
        alphaAnim.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnim.start();
    }

//    public void pause(){
//        if(moveAnim !=null&& moveAnim.isRunning()){
//            moveAnim.pause();
//        }
//        if(zoomAnim !=null&& zoomAnim.isRunning()){
//            zoomAnim.pause();
//        }
//    }
//
//    public void resume(){
//        if(moveAnim !=null&& moveAnim.isPaused()){
//            moveAnim.resume();
//        }
//        if(zoomAnim !=null&& zoomAnim.isPaused()){
//            zoomAnim.resume();
//        }
//    }

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

    /**
     * 主要绘制函数
     */
    public void draw(Canvas canvas, Paint p) {
        if (bitmap != null && alpha > 0) {
            p.setAlpha(alpha);
//            matrix.setScale(sf, sf, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
//            matrix.postTranslate(bitmap.getWidth() , bitmap.getHeight() );
            canvas.drawBitmap(bitmap, mCurrentPoint.x,mCurrentPoint.y, p);
            Log.e(TAG,"x:"+mCurrentPoint.x+"---"+"y:"+mCurrentPoint.y);
        } else {
            isEnd = true;
        }
    }

    public boolean isArriveTop() {
        return mCurrentPoint.y == 0;
    }

    public class BezierEvaluator implements TypeEvaluator<Point> {

        private Point point1;
        private Point point2;

        public BezierEvaluator(Point point1,Point point2){
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

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    private Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }
}