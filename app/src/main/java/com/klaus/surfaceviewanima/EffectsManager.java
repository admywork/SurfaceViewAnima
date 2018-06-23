package com.klaus.surfaceviewanima;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.SurfaceHolder;

import com.klaus.surfaceviewanima.Fireworm.FirewormDraw;

/**
 * Created by klaus on 2018/6/20.
 */
public class EffectsManager {

    public static int framerate=50;

    public static final int FIREWORM_EFFECTS = 1;

    private static EffectsManager mInstances;
    private EffectView mLiveEffectsView;
    private SurfaceHolder mSurfaceHolder;
    private IEffectDraw mEffectDraw;
    private DrawThread mDrawThread;

    private EffectsManager() {

    }

    public static synchronized EffectsManager getInstance() {
        if (mInstances == null) {
            mInstances = new EffectsManager();
        }
        return mInstances;
    }

    public void setEffectView(EffectView effectView) {
        mLiveEffectsView = effectView;
        mSurfaceHolder = mLiveEffectsView.getHolder();
    }


    public void setEffect(int effect, Context context) {
        switch (effect) {
            case FIREWORM_EFFECTS:
                mEffectDraw = new FirewormDraw(context);
                break;
        }
        mDrawThread=new DrawThread();
        mDrawThread.start();
    }

    public void stopEffect(){
        mEffectDraw=null;
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
    }

    /**
     * 绘制线程
     */
    private class DrawThread extends Thread {

        private Paint paint=new Paint();

        @Override
        public void run() {
            Canvas canvas = null;
            paint.setAntiAlias(true);
            // 无限循环绘制
            while (mEffectDraw!=null) {
                try {
                    synchronized (mSurfaceHolder) {
                        canvas = mSurfaceHolder.lockCanvas();
                        if (canvas != null) {
                            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                            mEffectDraw.onDraw(canvas,paint);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null && mSurfaceHolder != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try {
                    /**用于控制绘制帧率*/
                    Thread.sleep(1000/framerate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
