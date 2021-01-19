package pl.aib.clockalarm.ui.main;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pl.aib.clockalarm.R;
import pl.aib.clockalarm.db.entity.Alarm;
import pl.aib.clockalarm.list.AlarmListAdapter;
import pl.aib.clockalarm.task.AlarmDeleter;
import pl.aib.clockalarm.task.AlarmListFetcher;

public class MainViewModel extends ViewModel {
    private MutableLiveData<List<Alarm>> mAlarms;
    public LiveData<List<Alarm>> getAlarms(Context context) {
        if(mAlarms == null) {
            mAlarms = new MutableLiveData<List<Alarm>>();
            refreshAlarmsList(context);
        }
        return mAlarms;
    }

    public void refreshAlarmsList(Context context) {
        AlarmListFetcher fetcher = new AlarmListFetcher(context);
        fetcher.execute();
        List<Alarm> data = new ArrayList<>();
        try {
            data = fetcher.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(context,  R.string.alarms_fetch_error, Toast.LENGTH_LONG).show();
        }
        mAlarms.setValue(data);

    }

    public boolean deleteAlarm(Context context, Alarm alarm) {
        List<Alarm> current = mAlarms.getValue();
        int alarmIndex = current.indexOf(alarm);
        AlarmDeleter deleter = new AlarmDeleter(context);
        deleter.execute(alarm);
        boolean result;
        try {
            result = current.remove(alarm) && deleter.get();
            mAlarms.setValue(current);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
}
