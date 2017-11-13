package com.ritvikkar.shoppinglist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        final Animation anim = AnimationUtils.loadAnimation(
                SplashActivity.this,R.anim.show_anim);
        final ImageView ivLogo = findViewById(R.id.ivLogo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ivLogo.startAnimation(anim);
                postAnimation(anim);
            }
        }, SPLASH_TIME_OUT);
    }

    private void postAnimation(Animation anim) {
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
