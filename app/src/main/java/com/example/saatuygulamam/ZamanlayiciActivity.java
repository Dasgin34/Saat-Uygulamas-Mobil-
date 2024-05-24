package com.example.saatuygulamam;

import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

public class ZamanlayiciActivity extends AppCompatActivity {

    private EditText hourEditText, minuteEditText, secondEditText;
    private Button startButton, pauseButton, resetButton;
    private TextView countdownText;
    private VideoView videoView;
    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private long timeLeftInMillis;
    private int videoPosition; // video duraklatıldığında pozisyonu saklamak için
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zamanlayici);

        hourEditText = findViewById(R.id.hourEditText);
        minuteEditText = findViewById(R.id.minuteEditText);
        secondEditText = findViewById(R.id.secondEditText);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);
        countdownText = findViewById(R.id.countdownText);
        videoView = findViewById(R.id.videoView);

        // Video dosyasının yolunu belirtin (raw klasöründen)
        videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.kumsaatissss);
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // Süre başladığında oynatmamak için
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(0.0f));
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerRunning) {
                    if (validateInput()) { // Girişleri kontrol et
                        startTimer();
                        videoView.start(); // Süreyi başlattığımızda videoyu da başlat
                    }
                } else {
                    Toast.makeText(ZamanlayiciActivity.this, "Timer zaten çalışıyor.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                videoPosition = videoView.getCurrentPosition(); // Video pozisyonunu kaydet
                videoView.pause(); // Duraklattığımızda videoyu duraklat
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
                resetInputs();
                videoPosition = 0; // Video pozisyonunu sıfırla
                videoView.seekTo(videoPosition); // Videoyu başa al
            }
        });
    }

    private void startTimer() {
        String hourString = hourEditText.getText().toString();
        String minuteString = minuteEditText.getText().toString();
        String secondString = secondEditText.getText().toString();

        int hours = hourString.isEmpty() ? 0 : Integer.parseInt(hourString);
        int minutes = minuteString.isEmpty() ? 0 : Integer.parseInt(minuteString);
        int seconds = secondString.isEmpty() ? 0 : Integer.parseInt(secondString);

        long millisInput = hours * 3600000 + minutes * 60000 + seconds * 1000;

        if (millisInput == 0) {
            Toast.makeText(this, "Lütfen bir zaman belirtin.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (timeLeftInMillis == 0) {
            timeLeftInMillis = millisInput;
        }

        // Videonun toplam süresini al
        MediaPlayer mediaPlayer = MediaPlayer.create(this, videoUri);
        final int videoDuration = mediaPlayer.getDuration();

        // Hız faktörünü hesapla
        float speedFactor = (float) videoDuration / (float) timeLeftInMillis;

        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speedFactor));

        startCountDown();
        timerRunning = true;
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                Toast.makeText(ZamanlayiciActivity.this, "Süre doldu!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
    }

    private void resetTimer() {
        countDownTimer.cancel(); // Timerı iptal et
        timeLeftInMillis = 0;
        updateCountDownText();
        timerRunning = false;
    }

    private void updateCountDownText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        countdownText.setText(timeLeftFormatted);
    }

    private void resetInputs() {
        hourEditText.setText("");
        minuteEditText.setText("");
        secondEditText.setText("");
    }

    private boolean validateInput() {
        String hourString = hourEditText.getText().toString();
        String minuteString = minuteEditText.getText().toString();
        String secondString = secondEditText.getText().toString();

        if (hourString.isEmpty() && minuteString.isEmpty() && secondString.isEmpty()) {
            Toast.makeText(this, "Lütfen bir zaman belirtin.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(videoPosition); // Video pozisyonunu geri yükle
    }
}
