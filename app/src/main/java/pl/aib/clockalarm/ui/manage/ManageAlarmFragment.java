package pl.aib.clockalarm.ui.manage;

import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import kotlin.reflect.KFunction;
import pl.aib.clockalarm.R;
import pl.aib.clockalarm.db.entity.Alarm;
import pl.aib.clockalarm.task.AlarmSaver;
import pl.aib.clockalarm.task.AlarmUpdater;
import pl.aib.clockalarm.task.SingleAlarmFetcher;
import pl.aib.clockalarm.utility.Scheduler;

public class ManageAlarmFragment extends Fragment {

    private ManageAlarmViewModel mViewModel;

    private EditText mTitleInput;
    private TimePicker mHourInput;
    private Button mSubmit;
    private Button mCancel;
    private int mAlarmId;
    private Alarm mAlarm;
    private int mSuccessMessageId;
    private int mFailMessageId;
    private TextView mScreenTitle;

    public static ManageAlarmFragment newInstance() {
        return new ManageAlarmFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.manage_alarm_fragment, container, false);
        mTitleInput = root.findViewById(R.id.title_input);
        mHourInput = root.findViewById(R.id.hour_input);
        mSubmit = root.findViewById(R.id.save_button);
        mCancel = root.findViewById(R.id.cancel_button);
        mCancel.setOnClickListener(this::popMe);
        mSubmit.setOnClickListener(this::onSubmit);
        mScreenTitle = root.findViewById(R.id.header);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAlarmId = ManageAlarmFragmentArgs.fromBundle(getArguments()).getAlarmId();
        try {
            initAlarm();
            initResultMessages();
            initTitleInput();
            initHourInput();
            mScreenTitle.setText(mAlarmId > 0 ? R.string.update_alarm_title : R.string.create_alarm_title);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            popMe(view);
        }

        super.onViewCreated(view, savedInstanceState);
    }

    private void initResultMessages() {
        if(mAlarmId >= 0) {
            mSuccessMessageId = R.string.update_success_message;
            mFailMessageId = R.string.update_fail_message;
        } else {
            mSuccessMessageId = R.string.insert_success_message;
            mFailMessageId = R.string.insert_fail_message;
        }
    }

    private void initHourInput() {
        if(mAlarm != null && mAlarm.getHour() != null) {
            String[] parts = mAlarm.getHour().split(":");
            mHourInput.setHour(Integer.parseInt(parts[0]));
            mHourInput.setMinute(Integer.parseInt(parts[1]));
        }
    }

    private void initTitleInput() {
        if(mAlarm != null && mAlarm.getTitle() != null) {
            mTitleInput.setText(mAlarm.getTitle());
        } else {
            mTitleInput.setText(R.string.default_alarm_title);
        }
    }

    private void initAlarm() throws NoSuchAlgorithmException {
        if(mAlarmId >= 0) {
            mAlarm = fetchAlarmFromDatabase(mAlarmId);
        } else {
            mAlarm = new Alarm();
            mAlarm.setActive(true);
            mAlarm.setKey(generateAlarmKey());
            mAlarm.setBroadcastId((int) (Math.random() * 1000));

        }
    }

    private String generateAlarmKey() throws NoSuchAlgorithmException {
        long now = Calendar.getInstance().getTimeInMillis();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String message = UUID.randomUUID().toString().concat(String.valueOf(now));
        digest.update(message.getBytes());
        StringBuilder builder = new StringBuilder();
        for(byte b : digest.digest()) {
            builder.append(Integer.toHexString(0xFF & b));
        }
        return builder.toString();
    }

    private Alarm fetchAlarmFromDatabase(int id) {
        SingleAlarmFetcher fetcher = new SingleAlarmFetcher(requireContext());
        fetcher.execute(id);
        try {
            mAlarm = fetcher.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            popMe(requireView());
        }
        return mAlarm;
    }

    private void popMe(View view) {
        Navigation.findNavController(view).popBackStack();
    }

    private void onSubmit(View submitter) {
        String inputError = getInputError();
        if(inputError != null) {
            mTitleInput.setError(inputError);
        } else {
            fillAlarmDataWithInputs();
            boolean result = saveAlarm();
            finalize(submitter, result);
        }
    }

    private void finalize(View submitter, boolean result) {
        int message = result ? mSuccessMessageId : mFailMessageId;
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        popMe(submitter);
    }

    private boolean saveAlarm() {
        boolean result;
        if(mAlarmId > 0) {
            result = updateAlarm();
        } else {
            Scheduler scheduler = new Scheduler(requireContext());
            result = insertAlarm() && scheduler.scheduleAlarm(mAlarm);
        }
        return result;
    }

    private boolean updateAlarm() {
        boolean result;
        AlarmUpdater updater = new AlarmUpdater(requireContext());
        updater.execute(mAlarm);
        try {
            result = updater.get();
        } catch (ExecutionException | InterruptedException e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    private boolean insertAlarm() {
        boolean result;
        AlarmSaver saver = new AlarmSaver(requireContext());
        saver.execute(mAlarm);
        try {
            result = saver.get();
        } catch (ExecutionException | InterruptedException e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    private void fillAlarmDataWithInputs() {
        String hourString = mHourInput.getHour() < 10
                ? "0".concat(String.valueOf(mHourInput.getHour()))
                : String.valueOf(mHourInput.getHour());

        String minuteString = mHourInput.getMinute() < 10
                ? "0".concat(String.valueOf(mHourInput.getMinute()))
                : String.valueOf(mHourInput.getMinute());


        mAlarm.setTitle(mTitleInput.getText().toString().trim());
        mAlarm.setHour(hourString.concat(":").concat(minuteString));

    }

    @Nullable
    private String getInputError() {
        String error = null;
        if(mTitleInput.getText().toString().trim().isEmpty()) {
            error = getString(R.string.title_empty_error);
        }
        return error;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ManageAlarmViewModel.class);
        // TODO: Use the ViewModel
    }

}
