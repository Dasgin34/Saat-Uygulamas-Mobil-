package com.example.saatuygulamam;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class KronometreActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private Button startButton, stopButton, resetButton;
    private ImageView arrowImageView;
    private boolean running;
    private RotateAnimation rotateAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kronometre);

        chronometer = findViewById(R.id.chronometer);
        startButton = findViewById(R.id.baslat);
        stopButton = findViewById(R.id.durdur);
        resetButton = findViewById(R.id.sıfırla);
        arrowImageView = findViewById(R.id.arrow_imageview);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChronometer();
                startArrowAnimation();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopChronometer();
                stopArrowAnimation();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetChronometer();
            }
        });
    }

    private void startChronometer() {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            running = true;
        }
    }

    private void stopChronometer() {
        if (running) {
            chronometer.stop();
            running = false;
        }
    }

    private void resetChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    private void startArrowAnimation() {
        if (rotateAnimation == null) {
            rotateAnimation = new RotateAnimation(0, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.setDuration(13050); // Dönme süresi (milisaniye)
            rotateAnimation.setRepeatCount(Animation.INFINITE); // Sonsuz tekrar
            arrowImageView.startAnimation(rotateAnimation);
        } else {
            rotateAnimation.reset();
            arrowImageView.startAnimation(rotateAnimation);
        }
    }

    private void stopArrowAnimation() {
        if (rotateAnimation != null) {
            rotateAnimation.cancel();
        }
    }

}

