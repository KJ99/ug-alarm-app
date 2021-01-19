package pl.aib.clockalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TestReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent in = new Intent(context, TestActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.e("RECEIVER", "I'M HERE WITH ACTIVITY 15");
        Log.e("RECEIVER", "MY EXTRA " + String.valueOf(intent.getIntExtra("Testaroo", -1)));
        context.startActivity(in);
        Log.e("RECEIVER", "I'M HERE WITH ACTIVITY 20");
    }
}