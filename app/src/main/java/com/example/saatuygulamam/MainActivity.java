package com.example.saatuygulamam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    private TextView textViewClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewClock = findViewById(R.id.textViewClock);

        // Saati güncellemek için bir Handler kullanın
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateClock();
                handler.postDelayed(this, 1000); // Her saniyede bir güncelle
            }
        });

        Button kronometreButton = findViewById(R.id.kronometre);
        kronometreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, KronometreActivity.class);
                startActivity(intent);
            }
        });

        Button zamanlayiciButton = findViewById(R.id.zamanlayici);
        zamanlayiciButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zamanlayıcı butonuna tıklandığında ZamanlayiciActivity'e geçiş yap
                Intent intent = new Intent(MainActivity.this, ZamanlayiciActivity.class);
                startActivity(intent);
            }
        });

        Button alarmButton = findViewById(R.id.alarm);
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Alarm butonuna tıklandığında AlarmActivity'e geçiş yap
                Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
                startActivity(intent);
            }
        });
    }

    // Saati güncellemek için yardımcı metot
    private void updateClock() {
        // Türkiye saati için zaman dilimini ayarlayın
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul"));

        // Şu anki saat ve dakika bilgilerini alın
        String currentTime = dateFormat.format(new Date());

        // TextView'e saat bilgisini gösterin
        textViewClock.setText("Saat: " + currentTime);
    }
}
