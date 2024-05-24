package com.example.saatuygulamam;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AlarmActivity extends AppCompatActivity {

    private List<String> alarmList; // Alarm saatlerini tutacak liste
    private LinearLayout alarmListLayout; // Alarm listesinin gösterileceği layout
    private ScrollView alarmScrollView; // Alarm listesini sarmalayan ScrollView

    private TextView textViewClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // Liste oluşturuluyor veya geri yüklendiği kontrol ediliyor
        if (savedInstanceState != null) {
            alarmList = savedInstanceState.getStringArrayList("alarmList");
        } else {
            alarmList = new ArrayList<>();
        }

        alarmListLayout = findViewById(R.id.alarmListLayout);
        alarmScrollView = findViewById(R.id.alarmScrollView);

        findViewById(R.id.btnSetAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Liste durumunun kaydedilmesi
        outState.putStringArrayList("alarmList", (ArrayList<String>) alarmList);
    }

    private void showTimePickerDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_time_picker, null);
        builder.setView(dialogView);

        final NumberPicker hourPicker = dialogView.findViewById(R.id.hourPicker);
        final NumberPicker minutePicker = dialogView.findViewById(R.id.minutePicker);

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);

        builder.setPositiveButton("Onayla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int hour = hourPicker.getValue();
                int minute = minutePicker.getValue();
                setAlarm(hour, minute);
            }
        });

        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void setAlarm(int hourOfDay, int minute) {
        // Alarm zamanını belirle
        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        alarmCalendar.set(Calendar.MINUTE, minute);
        alarmCalendar.set(Calendar.SECOND, 0);

        // Şu anki zamanı al
        Calendar currentCalendar = Calendar.getInstance();

        // Alarm zamanı şu anki zamandan önce ise bir gün sonraya ayarla
        if (alarmCalendar.before(currentCalendar)) {
            alarmCalendar.add(Calendar.DATE, 1);
        }

        // AlarmManager'ı başlat
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            alarmIntent.setAction("com.example.saatuygulamam.ALARM_TRIGGERED"); // Aksiyon ekle
            alarmIntent.putExtra("hour", hourOfDay); // Saat bilgisini gönder
            alarmIntent.putExtra("minute", minute); // Dakika bilgisini gönder
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Alarmı ayarla
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
        }

        // Kullanıcıya bilgi ver
        String alarmTime = String.format("%02d:%02d", hourOfDay, minute);
        Toast.makeText(this, "Alarm Ayarlandı: " + alarmTime, Toast.LENGTH_SHORT).show();

        // Alarm saatinin listeye eklenmesi ve liste güncellemesi
        alarmList.add(alarmTime);
        updateAlarmList();
    }





    // Güncellenmiş CardView oluşturma metodu
    private CardView createAlarmCardView(final String alarmTime) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(40, 20, 40, 20); // Daha az genişlik için kenar boşlukları artırıldı
        cardView.setLayoutParams(params);
        cardView.setCardElevation(8f);
        cardView.setRadius(16f);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tıklanan alarm saatine göre bir işlem yapabilirsiniz
                Toast.makeText(AlarmActivity.this, "Tıklanan Alarm: " + alarmTime, Toast.LENGTH_SHORT).show();
            }
        });

        TextView textView = new TextView(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.setMargins(16, 16, 16, 16); // Yazı alanının kenar boşlukları artırıldı
        textParams.gravity = android.view.Gravity.CENTER; // Yazıyı ortala
        textView.setLayoutParams(textParams);
        textView.setText(alarmTime);
        textView.setTextSize(20); // Yazı boyutu artırıldı
        textView.setTextColor(getResources().getColor(android.R.color.black));

        cardView.addView(textView);
        return cardView;
    }

    // Alarm listesini güncelleyen metod
    private void updateAlarmList() {
        // LinearLayout içine yeni CardView'leri ekleyerek alarm listesini güncelle
        alarmListLayout.removeAllViews(); // Önceki içeriği temizle

        for (String alarm : alarmList) {
            CardView cardView = createAlarmCardView(alarm);
            alarmListLayout.addView(cardView);

            // Alarm bilgisi ekleyerek CardView'in içine yerleştir
            TextView alarmInfo = new TextView(this);
            alarmInfo.setLayoutParams(new CardView.LayoutParams(
                    CardView.LayoutParams.WRAP_CONTENT,
                    CardView.LayoutParams.WRAP_CONTENT
            ));
            alarmInfo.setText("saate alarm kuruldu.");
            alarmInfo.setPadding(228, 32, 16, 16);
            cardView.addView(alarmInfo);


        }
    }
    // updateClock metodunu güncelleyin
    private void updateClock() {
        // Türkiye saati için zaman dilimini ayarlayın
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul"));

        // Şu anki saat ve dakika bilgilerini alın
        String currentTime = dateFormat.format(new Date());

        // TextView'e saat bilgisini gösterin
        textViewClock.setText("Saat: " + currentTime);

        // Belirlediğiniz alarm saatini kontrol edin ve uyarı gösterin
        String[] alarmTimeSplit = currentTime.split(":");
        int currentHour = Integer.parseInt(alarmTimeSplit[0]);
        int currentMinute = Integer.parseInt(alarmTimeSplit[1]);

        // Örnek olarak, saat 10:30'da alarm çalacak şeklinde düşünelim
        int alarmHour = 10;
        int alarmMinute = 30;

        // Saat ve dakika eşleştiğinde uyarı göster
        if (currentHour == alarmHour && currentMinute == alarmMinute) {
            // Uyarı gösterme işlemi
            showAlarmNotification();
        }
    }

    // Uyarı gösterme işlemini gerçekleştiren metod
    private void showAlarmNotification() {
        // Örnek olarak bir Toast mesajı gösterelim
        Toast.makeText(this, "Alarm Çalıyor!", Toast.LENGTH_LONG).show();
    }

}
