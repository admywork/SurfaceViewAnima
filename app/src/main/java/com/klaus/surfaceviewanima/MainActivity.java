package com.klaus.surfaceviewanima;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private EffectView mEffectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEffectView=findViewById(R.id.ev);
        EffectsManager.getInstance().setEffectView(mEffectView);
        EffectsManager.getInstance().setEffect(EffectsManager.FIREWORM_EFFECTS,this);
    }
}
