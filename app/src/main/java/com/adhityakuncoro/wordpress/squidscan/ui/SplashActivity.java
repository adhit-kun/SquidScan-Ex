package com.adhityakuncoro.wordpress.squidscan.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.adhityakuncoro.wordpress.squidscan.R;
import com.wang.avi.AVLoadingIndicatorView;

public class SplashActivity extends AppCompatActivity {

    private ImageView imageLogo;
    private TextView textnya;

    private final int delay_Splash = 2500;
    private AVLoadingIndicatorView avi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setBackgroundDrawable(null);
        initializeViews();
        animateLogo();
        goToMainPage();
    }

    private void initializeViews() {
        imageLogo = findViewById(R.id.image_view_logo);
        textnya = findViewById(R.id.name);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
    }

    private void animateLogo() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_without_duration);

        fadeInAnimation.setDuration(delay_Splash);

        imageLogo.startAnimation(fadeInAnimation);
        textnya.startAnimation(fadeInAnimation);

        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.setIndicator("BallPulseIndicator");

    }

    private void goToMainPage() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, IntroActivity.class));
            finish();
        }, delay_Splash * 2);

    }

}
