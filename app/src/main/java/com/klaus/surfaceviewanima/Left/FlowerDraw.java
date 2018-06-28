package com.klaus.surfaceviewanima.Left;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.klaus.surfaceviewanima.IEffectDraw;
import com.klaus.surfaceviewanima.R;

import java.util.ArrayList;
import java.util.Random;

public class FlowerDraw implements IEffectDraw {

    private int maxNum = 15;

    private static final int ADD_FIREWORM = 0;
    private final FlowerDraw.FlowerHandler mFirewormHandler;
    private Random mRandom = new Random();
    private Bitmap mBitmap;

    private class FlowerHandler extends Handler {

        public FlowerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ADD_FIREWORM:
                    addFirewormBean(mBitmap);
                    break;
            }
        }
    }

    private ArrayList<FlowerBean> mFlowerBeanList;
    private Context mContext;

    public FlowerDraw(Context context) {
        mContext = context;
        HandlerThread mHandlerThread = new HandlerThread("FlowerDraw");
        mHandlerThread.start();
        mFlowerBeanList = new ArrayList<>();
        mFirewormHandler = new FlowerDraw.FlowerHandler(mHandlerThread.getLooper());
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_flower, new BitmapFactory.Options());
        mFirewormHandler.sendEmptyMessageDelayed(ADD_FIREWORM, mRandom.nextInt(2000));
    }

    public void addFirewormBean(Bitmap mBitmap) {
        if (mFlowerBeanList.size() < maxNum) {
            mFlowerBeanList.add(new FlowerBean(mContext, mBitmap));
            mFirewormHandler.sendEmptyMessageDelayed(ADD_FIREWORM, mRandom.nextInt(2000));
        }
    }

    @Override
    public void onDraw(Canvas canvas, Paint paint) {
        for (int i = 0; i < mFlowerBeanList.size(); i++) {
            mFlowerBeanList.get(i).onDraw(canvas, paint);
        }
    }

    @Override
    public void onSizeChanged(Context context, int w, int h) {

    }
}
