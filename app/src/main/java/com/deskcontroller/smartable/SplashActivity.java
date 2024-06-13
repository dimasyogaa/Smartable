package com.deskcontroller.smartable;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

Animation app_splash, btt;
ImageView app_logo;
ImageView app_subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        //load animation
        app_splash = AnimationUtils.loadAnimation(this, R.anim.app_splash);
        btt = AnimationUtils.loadAnimation(this, R.anim.btt);

        //load element
        app_logo = findViewById(R.id.applogo);
        app_subtitle = findViewById(R.id.app_title);

        //run animation
        app_logo.startAnimation(app_splash);
        app_subtitle.startAnimation(btt);

        //setting timer untuk 2 detik
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //merubah activity ke activity lain
                Intent gogetstarted = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(gogetstarted);
                finish();

            }
        }, 2000); // 2000 ms = 2 s
    }
}
