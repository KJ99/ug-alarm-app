package pl.aib.clockalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class FireAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getCanonicalName(), "Alarm fired 2");
        Intent uiIntent = new Intent(context, AlarmActivity.class);
        uiIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        uiIntent.putExtra("ALARM_KEY", intent.getStringExtra("ALARM_KEY"));
        context.startActivity(uiIntent);
    }

}