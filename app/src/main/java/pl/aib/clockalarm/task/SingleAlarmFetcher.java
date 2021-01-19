package pl.aib.clockalarm.task;

import android.content.Context;
import android.os.AsyncTask;

import pl.aib.clockalarm.db.Database;
import pl.aib.clockalarm.db.entity.Alarm;

public class SingleAlarmFetcher extends AsyncTask<Integer, Alarm, Alarm> {

    private final Database mDb;

    public SingleAlarmFetcher(Context context) {
        mDb = Database.getInstance(context);
    }

    @Override
    protected Alarm doInBackground(Integer... ids) {
        int id = ids.length > 0 ? ids[0] : -1;
        return mDb.alarmDao().findOne(id);
    }
}
