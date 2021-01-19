package pl.aib.clockalarm.task;

import android.content.Context;
import android.os.AsyncTask;

import pl.aib.clockalarm.db.Database;
import pl.aib.clockalarm.db.entity.Alarm;

public class AlarmKeyFetcher extends AsyncTask<String, Alarm, Alarm> {

    private final Database mDb;

    public AlarmKeyFetcher(Context context) {
        mDb = Database.getInstance(context);
    }

    @Override
    protected Alarm doInBackground(String... keys) {
        String key = keys.length > 0 ? keys[0] : "";
        return mDb.alarmDao().findOneByKey(key);
    }
}
