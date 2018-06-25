package com.klaus.surfaceviewanima.Fireworm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.klaus.surfaceviewanima.IEffectDraw;
import com.klaus.surfaceviewanima.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by klaus on 2018/6/20.
 */
public class FirewormDraw implements IEffectDraw {

    private int maxNum = 10;

    private static final int ADD_FIREWORM = 0;
    private final FirewormHandler mFirewormHandler;
    private Random mRandom = new Random();
    private Bitmap mBitmap;

    private class FirewormHandler extends Handler {

        public FirewormHandler(Looper looper) {
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

    private ArrayList<FirewormBean> mFirewormBeanList;
    private Context mContext;

    public FirewormDraw(Context context) {
        mContext = context;
        HandlerThread mHandlerThread = new HandlerThread("FirewormDraw");
        mHandlerThread.start();
        mFirewormBeanList = new ArrayList<>();
        mFirewormHandler = new FirewormHandler(mHandlerThread.getLooper());
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_fireworm, new BitmapFactory.Options());
        mFirewormHandler.sendEmptyMessageDelayed(ADD_FIREWORM, mRandom.nextInt(2000));
    }

    public void addFirewormBean(Bitmap mBitmap) {
        if (mFirewormBeanList.size() < maxNum) {
            mFirewormBeanList.add(new FirewormBean(mContext, mBitmap));
            mFirewormHandler.sendEmptyMessageDelayed(ADD_FIREWORM, mRandom.nextInt(2000));
        }
    }

    @Override
    public void onDraw(Canvas canvas, Paint paint) {
        for (int i = 0; i < mFirewormBeanList.size(); i++) {
            mFirewormBeanList.get(i).onDraw(canvas, paint);
        }
    }

    @Override
    public void onSizeChanged(Context context, int w, int h) {

    }
}
