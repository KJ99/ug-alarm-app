package pl.aib.clockalarm.task;

import android.content.Context;
import android.os.AsyncTask;

import pl.aib.clockalarm.db.Database;
import pl.aib.clockalarm.db.entity.Alarm;

public class AlarmDeleter extends AsyncTask<Alarm, Alarm, Boolean> {
    private final Database mDb;

    public AlarmDeleter(Context context) {
        mDb = Database.getInstance(context);
    }

    @Override
    protected Boolean doInBackground(Alarm... alarms) {
        boolean result;
        try {
            mDb.alarmDao().delete(alarms);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
}
