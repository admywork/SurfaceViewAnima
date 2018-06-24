package com.klaus.surfaceviewanima.Left;

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

import com.klaus.surfaceviewanima.Fireworm.FirewormBean;
import com.klaus.surfaceviewanima.IEffectDraw;
import com.klaus.surfaceviewanima.R;

import java.util.ArrayList;
import java.util.Random;

public class LeftDraw implements IEffectDraw {

    private int maxNum = 10;

    private static final int ADD_FIREWORM = 0;
    private final LeftDraw.LeftDrawHandler mFirewormHandler;
    private Random mRandom = new Random();
    private Bitmap[] mBitmap = new Bitmap[5];

    private class LeftDrawHandler extends Handler {

        public LeftDrawHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ADD_FIREWORM:
                    addFirewormBean(mBitmap[mRandom.nextInt(mBitmap.length)]);
                    break;
            }
        }
    }

    private ArrayList<FirewormBean> mLeftBeanList;
    private Context mContext;

    public LeftDraw(Context context) {
        mContext = context;
        HandlerThread mHandlerThread = new HandlerThread("LeftDraw");
        mHandlerThread.start();
        mLeftBeanList = new ArrayList<>();
        mFirewormHandler = new LeftDraw.LeftDrawHandler(mHandlerThread.getLooper());
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.star, new BitmapFactory.Options());
        for (int i = 0; i < mBitmap.length; i++) {
            mBitmap[i]=getScale(bitmap);
        }
        mFirewormHandler.sendEmptyMessageDelayed(ADD_FIREWORM, mRandom.nextInt(2000));
    }

    public void addFirewormBean(Bitmap mBitmap) {
        if (mLeftBeanList.size() < maxNum) {
            mLeftBeanList.add(new FirewormBean(mContext, mBitmap));
            mFirewormHandler.sendEmptyMessageDelayed(ADD_FIREWORM, mRandom.nextInt(2000));
        }
    }

    @Override
    public void onDraw(Canvas canvas, Paint paint) {
        for (int i = 0; i < mLeftBeanList.size(); i++) {
            mLeftBeanList.get(i).onDraw(canvas, paint);
        }
    }

    @Override
    public void onSizeChanged(Context context, int w, int h) {

    }

    private Bitmap getScale(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        float scale = mRandom.nextFloat()+0.3f;
        matrix.postScale(scale, scale);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }
}
