package com.klaus.surfaceviewanima;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private StarView mStarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStarView=findViewById(R.id.star_view);
        mStarView.start();
        mStarView.addXin(new StarBean(this,R.drawable.es_icon_default));
    }
}
