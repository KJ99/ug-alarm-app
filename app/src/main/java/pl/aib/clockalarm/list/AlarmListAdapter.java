package pl.aib.clockalarm.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.aib.clockalarm.R;
import pl.aib.clockalarm.db.entity.Alarm;
import pl.aib.clockalarm.listener.AlarmsListListener;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListViewHolder>{

    private List<Alarm> mList;

    @NonNull
    private Context mContext;

    private AlarmsListListener mListListener;

    public AlarmListAdapter(@NonNull Context context, AlarmsListListener listListener) {
        mList = new ArrayList<>();
        mContext = context;
        mListListener = listListener;
    }

    public void updateWholeList(List<Alarm> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlarmListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.alarm_item, parent, false);
        return new AlarmListViewHolder(view, mListListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmListViewHolder holder, int position) {
        holder.setData(mList.get(position));
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }
}
