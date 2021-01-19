package pl.aib.clockalarm.task;

import android.content.Context;
import android.os.AsyncTask;

import pl.aib.clockalarm.db.Database;
import pl.aib.clockalarm.db.entity.Alarm;

public class AlarmSaver extends AsyncTask<Alarm, Alarm, Boolean> {

    private final Database mDb;

    public AlarmSaver(Context context) {
        mDb = Database.getInstance(context);
    }

    @Override
    protected Boolean doInBackground(Alarm... alarms) {
        boolean result;
        try {
            mDb.alarmDao().insert(alarms);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
}
