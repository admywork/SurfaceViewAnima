package com.klaus.surfaceviewanima.Fireworm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

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
     * 萤火虫左右飘动最大的距离
     */
    private int mPathMaxWidth;

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
    private ArrayList<Integer> mAlphaList;
    private int currentIndex;

    public FirewormBean(Context context, Bitmap bitmap) {
        mScreenHeight = CommonUtils.getScreenHeight(context);
        mScreenWidth = CommonUtils.getScreenWidth(context);
        mPathMaxWidth = mScreenWidth / 6;
        random = new Random();
        mBitmap = bitmap;
        reset();
    }


    public void reset() {
        mStartPoint = new Point(random.nextInt(mScreenWidth), random.nextInt(mScreenHeight / 4 * 3) + mScreenHeight / 4);
        mEndPoint = new Point(random.nextInt(mPathMaxWidth) + mStartPoint.x - mPathMaxWidth / 2, random.nextInt(mStartPoint.y));
        lifeTime = random.nextInt(maxTime - minTime + 1) + minTime;
        float heightPercentage = (mStartPoint.y - mEndPoint.y) / (mScreenHeight * 1.0f);
        Log.e(TAG, heightPercentage + "");
        if (heightPercentage <= 0.2) {
            lifeTime = 2;
        } else if (0.2 < heightPercentage && heightPercentage <= 0.4) {
            lifeTime = 4;
        } else if (0.4 < heightPercentage && heightPercentage <= 0.6) {
            lifeTime = 6;
        } else if (0.6 < heightPercentage && heightPercentage <= 0.8) {
            lifeTime = 8;
        } else if (0.8 < heightPercentage && heightPercentage <= 1.0) {
            lifeTime = 10;
        }
        currentIndex = 0;
        mRect0 = new Rect(mStartPoint.x - (Math.abs(mStartPoint.x - mEndPoint.x)) / 2,
                (mStartPoint.y + mEndPoint.y) / 2,
                mStartPoint.x + (Math.abs(mStartPoint.x - mEndPoint.x)) / 2,
                mStartPoint.y);
        mRect1 = new Rect(mEndPoint.x - (Math.abs(mStartPoint.x - mEndPoint.x)) / 2,
                mEndPoint.y,
                mEndPoint.x + (Math.abs(mStartPoint.x - mEndPoint.x)) / 2,
                (mStartPoint.y + mEndPoint.y) / 2);
        mControlPoint0 = getPointFromRect(mRect0);
        mControlPoint1 = getPointFromRect(mRect1);
        mPathPointList = getPathPointList(mStartPoint, mEndPoint, mControlPoint0, mControlPoint1);
        mAlphaList = getAlphaList();
        isEnd = false;
    }


    private ArrayList<Integer> getAlphaList() {
        ArrayList<Integer> list = new ArrayList<>();
        int times = 1;
        switch (lifeTime) {
            case 2:
                times = random.nextInt(1) + 1;
                break;
            case 4:
                times = random.nextInt(2) + 1;
                break;
            case 6:
                times = random.nextInt(3) + 1;
                break;
            case 8:
                times = random.nextInt(4) + 1;
                break;
            case 10:
                times = random.nextInt(5) + 1;
                break;
        }
        times++;
        int maxAlpha = random.nextInt(30) + 225;//最大透明值;
        int minAlpha = random.nextInt(155) + 100;//最小透明值;
        float result = mPathPointList.size() * 1.00f / (times * 2);//透明度完成一次变化需要经过多少点;其中第一次由0变换到minAlpha和最后一次由minAlpha变换到0需要做特别的计算
        float add = maxAlpha * 1.00f / result;
        float firstAdd = minAlpha / result;
        float alpha = 0.00f;
        int j = 0;
        //第一次由0至最小透明度
        for (int i = 0; i < result; i++) {
            alpha = alpha + firstAdd;
            list.add((int) alpha);
        }
        boolean isAdd = true;
        for (int i = (int) result; i < mPathPointList.size() - result; i++) {
            if (isAdd) {
                alpha = alpha + add;
                if (alpha >= maxAlpha) {
                    list.add(maxAlpha);
                    isAdd = false;
                } else {
                    list.add((int) alpha);
                }
            } else {
                alpha = alpha - add;
                if (alpha < minAlpha) {
                    list.add(minAlpha);
                    isAdd = true;
                } else {
                    list.add((int) alpha);
                }
            }
        }
        //最后一次由最大透明度到0
        for (int i = (int) (mPathPointList.size() - result); i < mPathPointList.size(); i++) {
            alpha = alpha - firstAdd;
            if (alpha <= 0) {
                list.add(0);
            } else {
                list.add((int) alpha);
            }
        }
        return list;
    }

    public void onDraw(Canvas canvas, Paint paint) {
        if (mBitmap != null && !isEnd) {
            currentIndex++;
            if (currentIndex > mPathPointList.size() - 1) {
                isEnd = true;
                reset();
                return;
            }
            paint.setAlpha(mAlphaList.get(currentIndex));
            mCurrentPoint = mPathPointList.get(currentIndex);
            canvas.drawBitmap(mBitmap, mCurrentPoint.x, mCurrentPoint.y, paint);
        }
    }


    private Point getPointFromRect(Rect rect) {
        if (rect.width() <= 0) {
            Log.e(TAG, rect.left + "--" + rect.top + "--" + rect.right + "--" + rect.bottom);
        }
        int x = random.nextInt(rect.width() + 1) + rect.left;
        int y = random.nextInt(rect.height() + 1) + rect.top;

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
