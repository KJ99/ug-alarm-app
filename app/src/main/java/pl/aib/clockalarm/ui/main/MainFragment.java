package pl.aib.clockalarm.ui.main;

import androidx.lifecycle.ViewModelProviders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import pl.aib.clockalarm.R;
import pl.aib.clockalarm.TestReceiver;
import pl.aib.clockalarm.db.entity.Alarm;
import pl.aib.clockalarm.list.AlarmListAdapter;
import pl.aib.clockalarm.listener.AlarmsListListener;
import pl.aib.clockalarm.task.AlarmUpdater;
import pl.aib.clockalarm.utility.Scheduler;

public class MainFragment extends Fragment implements AlarmsListListener {

    private MainViewModel mViewModel;
    private RecyclerView mList;
    private ImageButton mAddButton;


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment, container, false);
        mList = root.findViewById(R.id.list);
        mAddButton = root.findViewById(R.id.add_button);
        mAddButton.setOnClickListener(this::onAddButtonClicked);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initList();
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getAlarms(requireContext()).observe(getViewLifecycleOwner(), alarms -> {
            AlarmListAdapter adapter = (AlarmListAdapter) mList.getAdapter();
            if(adapter != null) {
                adapter.updateWholeList(alarms);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mViewModel != null) {
            mViewModel.refreshAlarmsList(requireContext());
        }
    }

    private void onAddButtonClicked(View view) {
        MainFragmentDirections.ManageAlarmAction action = MainFragmentDirections.manageAlarmAction();
        Navigation.findNavController(view).navigate(action);
    }

    private void initList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        AlarmListAdapter listAdapter = new AlarmListAdapter(requireContext(), this);

        mList.setHasFixedSize(false);
        mList.setLayoutManager(layoutManager);
        mList.setAdapter(listAdapter);

    }

    @Override
    public void onAlarmSelected(Alarm alarm) {
        MainFragmentDirections.ManageAlarmAction action = MainFragmentDirections.manageAlarmAction();
        action.setAlarmId(alarm.getId());
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onAlarmMenuCalled(View caller, Alarm alarm) {
        PopupMenu popup = new PopupMenu(requireContext(), caller);
        popup.inflate(R.menu.alarm_options);
        popup.setOnMenuItemClickListener(item -> this.onAlarmMenuItemSelected(item, alarm));
        popup.show();

    }

    public boolean onAlarmMenuItemSelected(MenuItem item, Alarm alarm) {
        if(item.getItemId() == R.id.delete_option) {
            boolean isSuccess = mViewModel.deleteAlarm(requireContext(), alarm);
            int toastMessage = isSuccess ? R.string.delete_success_message : R.string.delete_fail_message;
            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onAlarmStateChangeRequested(Alarm alarm, boolean value) {
        alarm.setActive(value);
        boolean result;
        AlarmUpdater updater = new AlarmUpdater(requireContext());
        updater.execute(alarm);
        try {
            result = updater.get() && changeAlarmStateInSystem(alarm, value);
        } catch (ExecutionException | InterruptedException e) {
            result = false;
            e.printStackTrace();
        }
        int x = result ? R.string.update_success_message : R.string.update_fail_message;
        Toast.makeText(requireContext(), x, Toast.LENGTH_LONG).show();
    }

    private boolean changeAlarmStateInSystem(Alarm alarm, boolean shouldBeActive) {
        Scheduler scheduler = new Scheduler(requireContext());
        boolean result;
        if(shouldBeActive) {
           result = scheduler.scheduleAlarm(alarm);
        }
        else {
            result = scheduler.cancelAlarm(alarm);
        }
        return result;
    }
}
