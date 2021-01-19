package pl.aib.clockalarm.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.ExecutionException;

import pl.aib.clockalarm.R;
import pl.aib.clockalarm.db.entity.Alarm;
import pl.aib.clockalarm.listener.AlarmsListListener;
import pl.aib.clockalarm.task.AlarmDeleter;

public class AlarmListViewHolder extends RecyclerView.ViewHolder {

    private AlarmsListListener mListListener;

    public AlarmListViewHolder(@NonNull View itemView, AlarmsListListener listListener) {
        super(itemView);
        mListListener = listListener;
    }

    public void setData(Alarm alarm) {
        TextView hourLabel = this.itemView.findViewById(R.id.hour_label);
        TextView titleLabel = this.itemView.findViewById(R.id.title_label);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch activeSwitch = this.itemView.findViewById(R.id.is_active_switch);
        ImageButton menuButton = this.itemView.findViewById(R.id.menu_button);

        menuButton.setOnClickListener(Fifa -> mListListener.onAlarmMenuCalled(Fifa, alarm));
        hourLabel.setText(alarm.getHour());
        titleLabel.setText(alarm.getTitle());
        activeSwitch.setChecked(alarm.isActive());

        activeSwitch.setOnCheckedChangeListener((v, isChecked) -> {
            mListListener.onAlarmStateChangeRequested(alarm, isChecked);
        });

        this.itemView.setOnClickListener(v -> mListListener.onAlarmSelected(alarm));

        this.itemView.setOnLongClickListener(v -> {
            mListListener.onAlarmMenuCalled(v, alarm);
            return true;
        });

    }
}
