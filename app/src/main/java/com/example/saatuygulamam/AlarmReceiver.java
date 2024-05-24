package com.example.saatuygulamam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("com.example.saatuygulamam.ALARM_TRIGGERED")) {
            int setHour = intent.getIntExtra("hour", 0);
            int setMinute = intent.getIntExtra("minute", 0);

            // Şu anki saati alın
            Calendar alarmTime = Calendar.getInstance();
            int alarmHour = alarmTime.get(Calendar.HOUR_OF_DAY);
            int alarmMinute = alarmTime.get(Calendar.MINUTE);

            // Alarm zamanıyla şu anki zamanı karşılaştır
            if (alarmHour == setHour && alarmMinute == setMinute) {
                // Alarm zamanı geldiğinde işlem yap
                showAlarmNotification(context);
            }
        }
    }

    // Alarm zamanına geldiğinde gösterilecek bildirim metodu
    private void showAlarmNotification(Context context) {
        // Burada bildirim gösterme işlemini gerçekleştirin
        Toast.makeText(context, "Alarm Çalıyor!", Toast.LENGTH_LONG).show();
        // Örneğin, bildirim ekranına bir bildirim gönderebilirsiniz
        // Bildirim gönderme kodunuzu buraya ekleyebilirsiniz
    }
}





