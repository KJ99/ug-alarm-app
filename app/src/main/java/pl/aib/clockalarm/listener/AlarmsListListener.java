package pl.aib.clockalarm.listener;

import android.view.View;

import pl.aib.clockalarm.db.entity.Alarm;

public interface AlarmsListListener {
    void onAlarmSelected(Alarm alarm);
    void onAlarmMenuCalled(View caller, Alarm alarm);
    void onAlarmStateChangeRequested(Alarm alarm, boolean value);
}
