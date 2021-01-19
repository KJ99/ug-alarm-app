package pl.aib.clockalarm.task;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import java.util.List;

import pl.aib.clockalarm.db.Database;
import pl.aib.clockalarm.db.entity.Alarm;

public class AlarmListFetcher extends AsyncTask<Void, Alarm, List<Alarm>> {

    private final Database mDb;

    public AlarmListFetcher(Context context) {
        mDb = Database.getInstance(context);
    }

    @Override
    protected List<Alarm> doInBackground(Void... voids) {
        return mDb.alarmDao().findAll();
    }
}
