package com.klaus.surfaceviewanima.Left;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import com.klaus.surfaceviewanima.CommonUtils;
import com.klaus.surfaceviewanima.EffectsManager;
import com.klaus.surfaceviewanima.IEffectBean;

import java.util.ArrayList;
import java.util.Random;

public class FlowerBean extends IEffectBean {

    private String TAG = getClass().getSimpleName();

    /**
     * 第一个控制点的区域
     */
    private Rect mRect0;

    /**
     * 第二个控制点的区域
     */
    private Rect mRect1;

    private int mScreenHeight;

    private int mScreenWidth;

    private int currentDegree;
    private int degreeAdd;


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
    private Bitmap mOriginBitmap;
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
    private int maxTime = 12;
    private int minTime = 10;
    private int lifeTime;
    private Paint mPaint;

    private ArrayList<Point> mPathPointList;
    private int currentIndex;
    private Matrix matrix;

    public FlowerBean(Context context, Bitmap bitmap) {
        mScreenHeight = CommonUtils.getScreenHeight(context);
        mScreenWidth = CommonUtils.getScreenWidth(context);
        random = new Random();
        mOriginBitmap = bitmap;
        mPaint = new Paint();
        matrix = new Matrix();
        reset();
    }


    public void reset() {
        currentDegree = random.nextInt(360);
        degreeAdd = random.nextInt(10);
        mBitmap = getScale(mOriginBitmap);
        mPaint.setAlpha(random.nextInt(100) + 155);
        if (random.nextFloat() < 0.3) {
            mStartPoint = new Point(random.nextInt(mScreenWidth / 4 * 3) + mScreenWidth / 4, 0);
        } else {
            mStartPoint = new Point(mScreenWidth, random.nextInt(mScreenHeight / 4 * 3));
        }
        mEndPoint = new Point((mStartPoint.x - mScreenWidth * 4 / 3) + (-mScreenWidth / 4 + random.nextInt(mScreenWidth / 2)), mStartPoint.y + mScreenHeight / 4 * 3 + (-mScreenHeight / 4 + random.nextInt(mScreenHeight / 2)));
        lifeTime = random.nextInt(maxTime - minTime + 1) + minTime;
        currentIndex = 0;
        mRect0 = new Rect(mEndPoint.x, mStartPoint.y, mStartPoint.x, mEndPoint.y);
        mControlPoint0 = getPointFromRect(mRect0);
        mControlPoint1 = getPointFromRect(mRect0);
        mPathPointList = getPathPointList(mStartPoint, mEndPoint, mControlPoint0, mControlPoint1);
        isEnd = false;
    }

    private int i = 1;

    public void onDraw(Canvas canvas, Paint paint) {
        if (mBitmap != null && !isEnd) {
            currentIndex++;
            if (currentIndex > mPathPointList.size() - 1) {
                isEnd = true;
                reset();
                return;
            }
            mCurrentPoint = mPathPointList.get(currentIndex);
            if (mCurrentPoint.x < -mBitmap.getWidth() - 1 && mCurrentPoint.y > mScreenHeight + mBitmap.getHeight() + 1) {
                isEnd = true;
                reset();
                return;
            }
            currentDegree += degreeAdd;
            canvas.save();
            canvas.rotate(currentDegree, mCurrentPoint.x + mBitmap.getWidth() / 2, mCurrentPoint.y + mBitmap.getHeight() / 2);
            canvas.drawBitmap(mBitmap, mCurrentPoint.x, mCurrentPoint.y, paint);
            canvas.restore();
        }
    }


    private Point getPointFromRect(Rect rect) {
        if (rect.width() <= 0 || rect.height() <= 0) {
            Log.e(TAG, rect.left + "--" + rect.top + "--" + rect.right + "--" + rect.bottom);
        }
        int x = random.nextInt(Math.abs(rect.width()) + 1) + rect.left;
        int y = random.nextInt(Math.abs(rect.height()) + 1) + rect.top;

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

    private Bitmap getScale(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        float scale = random.nextFloat() + 1.0f;
        matrix.postScale(scale, scale);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }
}
