package com.github.davsx.llearn.activities.Settings;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.service.CardNotification.CardNotificationAlarmService;
import com.github.davsx.llearn.service.Settings.SettingsService;

import javax.inject.Inject;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    @Inject
    SettingsService settingsService;

    private CheckBox checkBoxWordOfTheDayEnable;
    private Button buttonNotificationFrom;
    private Button buttonNotificationTo;
    private Button buttonNotificationInterval;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("   Settings");
        actionBar.setIcon(R.mipmap.ic_launcher_icon);

        checkBoxWordOfTheDayEnable = findViewById(R.id.checkbox_word_of_the_day_enable);
        buttonNotificationFrom = findViewById(R.id.button_notification_from);
        buttonNotificationTo = findViewById(R.id.button_notification_to);
        buttonNotificationInterval = findViewById(R.id.button_notification_interval);

        buttonNotificationFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotificationFromTimePickerDialog();
            }
        });
        buttonNotificationTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotificationToTimePickerDialog();
            }
        });
        buttonNotificationInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotificationIntervalTimePickerDialog();
            }
        });
        checkBoxWordOfTheDayEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsService.setCardNotificationEnabled(isChecked);
            }
        });

        findViewById(R.id.button_reset_alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardNotificationAlarmService.resetAlarm(SettingsActivity.this, settingsService);
            }
        });

        updateViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CardNotificationAlarmService.setNextAlarm(this, settingsService);
    }

    private void showNotificationIntervalTimePickerDialog() {
        TimePickerDialog dialog = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setNotificationInterval(hourOfDay, minute);
            }
        }, settingsService.getCardNotificationIntervalHour(), settingsService.getCardNotificationIntervalMinute(), true);
        dialog.show();
    }

    private void showNotificationToTimePickerDialog() {
        TimePickerDialog dialog = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setNotificationTo(hourOfDay, minute);
            }
        }, settingsService.getCardNotificationToHour(), settingsService.getCardNotificationToMinute(), true);
        dialog.show();
    }

    private void showNotificationFromTimePickerDialog() {
        TimePickerDialog dialog = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setNotificationFrom(hourOfDay, minute);
            }
        }, settingsService.getCardNotificationFromHour(), settingsService.getCardNotificationFromMinute(), true);
        dialog.show();
    }

    private void setNotificationInterval(int hour, int minute) {
        settingsService.setCardNotificationInterval(hour, minute);
        updateViews();
    }

    private void setNotificationFrom(int hour, int minute) {
        Integer notificationToHour = settingsService.getCardNotificationToHour();
        Integer notificationToMinute = settingsService.getCardNotificationToMinute();
        String notificationToString = String.format(Locale.getDefault(), "%02d:%02d", notificationToHour,
                notificationToMinute);

        if (hour < notificationToHour || (hour == notificationToHour && minute < notificationToMinute)) {
            settingsService.setCardNotificationFrom(hour, minute);
        } else {
            Toast.makeText(this, "Notification start hour must be smaller than " + notificationToString,
                    Toast.LENGTH_SHORT).show();
        }

        updateViews();
    }

    private void setNotificationTo(int hour, int minute) {
        int notificationFromHour = settingsService.getCardNotificationFromHour();
        int notificationFromMinute = settingsService.getCardNotificationFromMinute();
        String notificationFromString = String.format(Locale.getDefault(), "%02d:%02d", notificationFromHour,
                notificationFromMinute);

        if (hour > notificationFromHour || (hour == notificationFromHour && minute > notificationFromMinute)) {
            settingsService.setCardNotificationTo(hour, minute);
        } else {
            Toast.makeText(this, "Notification end hour must be bigger than " + notificationFromString,
                    Toast.LENGTH_SHORT).show();
        }

        updateViews();
    }

    private void updateViews() {
        buttonNotificationFrom.setText(String.format(Locale.getDefault(), "%02d:%02d",
                settingsService.getCardNotificationFromHour(),
                settingsService.getCardNotificationFromMinute()));

        buttonNotificationTo.setText(String.format(Locale.getDefault(), "%02d:%02d",
                settingsService.getCardNotificationToHour(),
                settingsService.getCardNotificationToMinute()));

        buttonNotificationInterval.setText(String.format(Locale.getDefault(), "%02d:%02d",
                settingsService.getCardNotificationIntervalHour(),
                settingsService.getCardNotificationIntervalMinute()));

        checkBoxWordOfTheDayEnable.setChecked(settingsService.getCardNotificationEnabled());
    }

}
