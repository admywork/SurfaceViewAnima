package com.klaus.surfaceviewanima;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public interface IEffectDraw {

    void onDraw(Canvas canvas, Paint paint);

    void onSizeChanged(Context context, int w, int h);

}