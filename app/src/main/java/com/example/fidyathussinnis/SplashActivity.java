package com.example.fidyathussinnis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private ImageView imgSplashLogo;
    private TextView tvSplashTitle, tvSplashSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imgSplashLogo = findViewById(R.id.imgSplashLogo);
        tvSplashTitle = findViewById(R.id.tvSplashTitle);
        tvSplashSubtitle = findViewById(R.id.tvSplashSubtitle);

        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        imgSplashLogo.startAnimation(topAnim);
        tvSplashTitle.startAnimation(bottomAnim);
        tvSplashSubtitle.startAnimation(bottomAnim);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }, 2500);
    }
}