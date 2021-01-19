package pl.aib.clockalarm.utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import pl.aib.clockalarm.FireAlarmReceiver;
import pl.aib.clockalarm.db.entity.Alarm;
import pl.aib.clockalarm.task.AlarmKeyFetcher;
import pl.aib.clockalarm.task.SingleAlarmFetcher;

public class Scheduler {
    private final Context mContext;
    private final AlarmManager mAlarmManager;

    public Scheduler(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public boolean scheduleAlarm(Alarm alarm) {
        boolean isSuccess;
        try {
            processScheduling(alarm);
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return  isSuccess;
    }

    public boolean cancelAlarm(Alarm alarm) {
        boolean result = true;
        try {
            PendingIntent action = buildPendingIntent(alarm);
            mAlarmManager.cancel(action);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public static void scheduleSnooze(Context context, String title, long timeInMillis) {
        long now = Calendar.getInstance().getTimeInMillis();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent action = new Intent(context, FireAlarmReceiver.class);
        action.putExtra("ALARM_TITLE", title);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, action, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, now + timeInMillis, pendingIntent);
    }

    private void processScheduling(Alarm alarm) throws ExecutionException, InterruptedException {
        if (alarm.getId() <= 0) {
            AlarmKeyFetcher fetcher = new AlarmKeyFetcher(mContext);
            fetcher.execute(alarm.getKey());
            alarm = fetcher.get();
        }
        Log.d("ALARM SCHEDULER", "CREATE ALARM");
        PendingIntent pendingIntent = buildPendingIntent(alarm);
        Calendar initialTime = buildFireTime(alarm);
        long dayTime = 24 * 60 * 60 * 1000;
        Log.d("ALARM SCHEDULER", "TRY TO SCHEDULE ALARM");
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, initialTime.getTimeInMillis(), dayTime, pendingIntent);
        Log.d("ALARM SCHEDULER", "scheduled to " + formatDate(initialTime));

    }



    private String formatDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return dateFormat.format(calendar.getTime());
    }

    private Calendar buildFireTime(Alarm alarm) {
        String[] parts = alarm.getHour().split(":");
        int requestedHour = Integer.parseInt(parts[0]);
        int requestedMinute = Integer.parseInt(parts[1]);
        Calendar calendar = resolveInitialDate(requestedHour, requestedMinute);
        calendar.set(Calendar.HOUR_OF_DAY, requestedHour);
        calendar.set(Calendar.MINUTE, requestedMinute);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }



    private Calendar resolveInitialDate(int requestedHour, int requestedMinute) {
        Calendar calendar = Calendar.getInstance();
        boolean isPast = calendar.get(Calendar.HOUR) > requestedHour || calendar.get(Calendar.MINUTE) >= requestedMinute;
        if(isPast) {
            calendar.setTimeInMillis(calendar.getTimeInMillis() + (24 * 60 * 60 * 1000));
        }
        return calendar;
    }

    private Intent buildReceiverIntent(Alarm alarm) {
        Intent intent = new Intent(mContext, FireAlarmReceiver.class);
        intent.putExtra("ALARM_KEY", alarm.getKey());
        return intent;
    }

    private PendingIntent buildPendingIntent(Alarm alarm) {
        return PendingIntent.getBroadcast(mContext, alarm.getId(), buildReceiverIntent(alarm), 0);
    }
}
