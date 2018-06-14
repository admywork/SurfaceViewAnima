package com.klaus.surfaceviewanima;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class StarView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    sendEmptyMessageDelayed(0,1000);
                    if(starBeans.size()<30){
                        addXin(new StarBean(mContext,R.drawable.es_icon_default));
                    }
                    break;
            }
        }
    };

    /**
     * 心的个数
     */
    private ArrayList<StarBean> starBeans = new ArrayList<>();
    private Paint p;
    /**
     * 负责绘制的工作线程
     */
    private DrawThread drawThread;

    private Context mContext;

    public StarView(Context context) {
        this(context, null);
    }

    public StarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        this.setZOrderOnTop(true);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setZOrderOnTop(true);
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        p = new Paint();
        p.setAntiAlias(true);
        drawThread = new DrawThread();
        mHandler.sendEmptyMessageDelayed(0,1000);
    }


    /**
     * 点赞动作  添加心的函数 控制画面最大心的个数
     */
    public void addXin(StarBean zanBean) {
        starBeans.add(zanBean);
        if (starBeans.size() > 40) {
            starBeans.remove(0);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (drawThread == null) {
            drawThread = new DrawThread();
        }
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (drawThread != null) {
            drawThread.isRun = false;
            drawThread = null;
        }
    }

    class DrawThread extends Thread {
        boolean isRun = true;

        @Override
        public void run() {
            super.run();
            /**绘制的线程 死循环 不断的跑动*/
            while (isRun) {
                Canvas canvas = null;
                try {
                    synchronized (surfaceHolder) {
                        canvas = surfaceHolder.lockCanvas();
                        /**清除画面*/
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        /**对所有icon进行遍历绘制*/
                        for (int i = 0; i < starBeans.size(); i++) {
                            if(starBeans.get(i).isArriveTop()){
                                starBeans.remove(i);
                                i--;
                                continue;
                            }
                            starBeans.get(i).draw(canvas, p);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try {
                    /**用于控制绘制帧率*/
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        if (drawThread != null) {

//            for (int i = 0; i < starBeans.size(); i++) {
//                starBeans.get(i).pause();
//            }
            for (int i = 0; i < starBeans.size(); i++) {
                starBeans.get(i).stop();
            }

            drawThread.isRun = false;
            drawThread = null;
        }

    }

    public void start() {
        if (drawThread == null) {
//            for (int i = 0; i < starBeans.size(); i++) {
//                starBeans.get(i).resume();
//            }
            drawThread = new DrawThread();
            drawThread.start();
        }
    }
}
