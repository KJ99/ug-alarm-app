package pl.aib.clockalarm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import pl.aib.clockalarm.db.entity.Alarm;
import pl.aib.clockalarm.task.AlarmKeyFetcher;
import pl.aib.clockalarm.utility.Scheduler;

public class AlarmActivity extends AppCompatActivity {

    private String mAlarmKey;
    private String mAlternativeTitle;
    @Nullable
    private Alarm mAlarm = null;

    private TextView mHourLabel;
    private TextView mTitleLabel;
    private Button mDismissButton;
    private Button mSnoozeButton;

    private Ringtone mRingtone;
    private Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        mAlarmKey = getIntent().getStringExtra("ALARM_KEY");
        mAlternativeTitle = getIntent().getStringExtra("ALARM_TITLE");
        mHourLabel = findViewById(R.id.hour_label);
        mTitleLabel = findViewById(R.id.title_label);
        mDismissButton = findViewById(R.id.dismiss_button);
        mSnoozeButton = findViewById(R.id.snooze_button);
        mDismissButton.setOnClickListener(v -> finishAndRemoveTask());
        mSnoozeButton.setOnClickListener(v -> snooze());
    }


    @Override
    protected void onStart() {
        super.onStart();
        mRingtone = RingtoneManager.getRingtone(
                this,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        );
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(mAlarm == null) {
            AlarmKeyFetcher fetcher = new AlarmKeyFetcher(this);
            fetcher.execute(mAlarmKey);
            try {
                mAlarm = fetcher.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                setupUI();
                Handler handler = new Handler();
                handler.post(this::runSound);
                handler.post(this::runVibrations);
            }
        }
    }

    private void snooze() {
        long snoozeTime = 5 * 60 * 1000;
        Scheduler.scheduleSnooze(this, getAlarmTitle(), snoozeTime);
        Toast.makeText(this, "Snooze set to 5 minutes", Toast.LENGTH_LONG).show();
        finishAndRemoveTask();
    }

    private void runSound() {
        mRingtone.play();
    }

    private void runVibrations() {
        long vibrationTime = 30 * 1000;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mVibrator.vibrate(VibrationEffect.createOneShot(vibrationTime, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            mVibrator.vibrate(vibrationTime);
        }
    }

    @Override
    protected void onDestroy() {
        mRingtone.stop();
        mVibrator.cancel();
        super.onDestroy();
    }

    private void setupUI() {
        setupHour();
        setupTitle();
    }

    private void setupHour() {
        if(mAlarm != null) {
            mHourLabel.setText(mAlarm.getHour());
        } else {
            mHourLabel.setText(formatDate(Calendar.getInstance()));
        }
    }

    private void setupTitle() {
        mTitleLabel.setText(getAlarmTitle());
    }

    private String getAlarmTitle() {
        String title;
        if(mAlarm != null) {
            title = mAlarm.getTitle();
        } else if(mAlternativeTitle != null) {
            title = mAlternativeTitle;
        } else {
            title = getString(R.string.default_alarm_title);
        }
        return title;
    }

    private String formatDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
        return dateFormat.format(calendar.getTime());
    }

}