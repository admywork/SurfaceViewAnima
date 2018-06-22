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
import com.klaus.surfaceviewanima.EffectsManager;
import com.klaus.surfaceviewanima.IEffectBean;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by klaus on 2018/6/20.
 */
public class FirewormBean extends IEffectBean {

    private String TAG = getClass().getSimpleName();

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

    /**
     * 起点,控制点0,控制点1,终点,当前坐标
     */
    public Point mStartPoint, mControlPoint0, mControlPoint1, mEndPoint, mCurrentPoint;

    /**
     * 透明度
     */
    public int alpha = 255;
    /**
     * icon
     */
    private Bitmap mBitmap;
    /**
     * 产生随机数
     */
    private Random random;
    /**
     * 动画是否结束
     */
    private boolean isEnd;
    /**
     * 动画生命
     */
    private int maxTime = 8;
    private int minTime = 3;
    private int lifeTime;

    private ArrayList<Point> mPathPointList;
    private int currentIndex;

    public FirewormBean(Context context, Bitmap bitmap) {
        mScreenHeight = CommonUtils.getScreenHeight(context);
        mScreenWidth = CommonUtils.getScreenWidth(context);
        mWidth = mScreenWidth / 6;
        random = new Random();
        mBitmap = bitmap;
        reset();
    }


    public void reset() {
        lifeTime = random.nextInt(maxTime - minTime + 1) + minTime;
        mStartPoint = new Point(random.nextInt(mScreenWidth), mScreenHeight);
        mCurrentPoint = mStartPoint;
        mEndPoint = new Point(random.nextInt(mWidth) + mStartPoint.x - mWidth / 2, 0 - mBitmap.getHeight());
        mRect0 = new Rect(mStartPoint.x - mWidth, mScreenHeight / 4 * 3 - mWidth / 2, mStartPoint.x, mScreenHeight / 4 * 3 + mWidth / 2);
        mRect1 = new Rect(mStartPoint.x, mScreenHeight / 4 - mWidth / 2, mStartPoint.x + mWidth, mScreenHeight / 4 + mWidth / 2);
        mControlPoint0 = getPointFromRect(mRect0);
        mControlPoint1 = getPointFromRect(mRect1);
        mPathPointList = getPathPointList(mStartPoint, mEndPoint, mControlPoint0, mControlPoint1);
        isEnd = false;
    }

    public void onDraw(Canvas canvas, Paint paint) {
        if (mBitmap != null && !isEnd) {
            paint.setAlpha(alpha);
            currentIndex++;
            if (currentIndex > mPathPointList.size() - 1) {
                isEnd = true;
                reset();
                return;
            }
            mCurrentPoint = mPathPointList.get(currentIndex);
            canvas.drawBitmap(mBitmap, mCurrentPoint.x, mCurrentPoint.y, paint);
        }
    }


    private Point getPointFromRect(Rect rect) {
        int x = random.nextInt(rect.width()) + rect.left;
        int y = random.nextInt(rect.height()) + rect.top;
        return new Point(x, y);
    }

    private ArrayList<Point> getPathPointList(Point startPoint, Point endPoint, Point controlPoint0, Point controlPoint1) {
        ArrayList<Point> arrayList = new ArrayList<>();
        float timeLeft;
        float time;
        for (int i = 0; i < EffectsManager.framerate * lifeTime; i++) {
            Point point = new Point();
            timeLeft = (EffectsManager.framerate * lifeTime - i) * 1.0f / (EffectsManager.framerate * lifeTime);
            time = i / (EffectsManager.framerate * lifeTime * 1.0f);
            point.x = (int) (timeLeft * timeLeft * timeLeft * (startPoint.x)
                    + 3 * timeLeft * timeLeft * time * (controlPoint0.x)
                    + 3 * timeLeft * time * time * (controlPoint1.x)
                    + time * time * time * (endPoint.x));

            point.y = (int) (timeLeft * timeLeft * timeLeft * (startPoint.y)
                    + 3 * timeLeft * timeLeft * time * (controlPoint0.y)
                    + 3 * timeLeft * time * time * (controlPoint1.y)
                    + time * time * time * (endPoint.y));
            arrayList.add(point);
        }
        return arrayList;
    }
}
