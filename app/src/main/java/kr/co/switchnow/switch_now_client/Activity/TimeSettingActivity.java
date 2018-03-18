package kr.co.switchnow.switch_now_client.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.shawnlin.numberpicker.NumberPicker;

import kr.co.switchnow.switch_now_client.R;

import static kr.co.switchnow.switch_now_client.Activity.ReadyActivity.userSetting;


public class TimeSettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private Switch activateCustomize;
    private Boolean isCustomize;
    TextView setting_time_title;
    BaseActivity baseActivity;
    private SharedPreferences.Editor sEditor;
    private SharedPreferences setting;
    TextView time_setting_mode;
    TextView time_1_setting;
    TextView time_2_setting;
    TextView time_3_setting;
    TextView time_4_setting;
    ToggleButton time_1_button;
    ToggleButton time_2_button;
    ToggleButton time_3_button;
    ToggleButton time_4_button;

    TextView time_1_value;
    TextView time_2_value;
    TextView time_3_value;
    TextView time_4_value;


    Boolean fromSettingExtensionFlag;
    Boolean fromSettingAlarmFlag;


    String fromSettingExtensionValue;
    String fromSettingAlarmnValue;
    String fromSettingTime1Value;
    String fromSettingTime2Value;
    String fromSettingTime3Value;
    String fromSettingTime4Value;


    TextView text_dialog_timepicker;
    Button btn_dialog_cancel;
    Button btn_dialog_setting;
    Dialog timeSetterDialog = null;
    NumberPicker timerPicker;
    int tempTime;
    int tempFlag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int switchDarkgrey = getResources().getColor(R.color.switchDarkgrey);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(switchDarkgrey);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.switchDarkgrey));
        }
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(R.layout.time_setting_title);
        setContentView(R.layout.activity_time_setting);

        isCustomize = false;
        setting = getSharedPreferences(userSetting, Context.MODE_PRIVATE);
        sEditor = setting.edit();
        tempTime = 0;
        tempFlag = 0;


        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.switchLime), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        setting_time_title = (TextView) findViewById(R.id.time_setting_title);
        Typeface face = Typeface.createFromAsset(getAssets(), "ENORBITB.TTF");
        setting_time_title.setTypeface(face);

        activateCustomize = (Switch) findViewById(R.id.time_setting_trigger);
        activateCustomize.setOnCheckedChangeListener(this);
        time_setting_mode = (TextView) findViewById(R.id.time_setting_mode);

        time_1_setting = (TextView) findViewById(R.id.time_1_setting);
        time_2_setting = (TextView) findViewById(R.id.time_2_setting);
        time_3_setting = (TextView) findViewById(R.id.time_3_setting);
        time_4_setting = (TextView) findViewById(R.id.time_4_setting);

        time_1_button = (ToggleButton) findViewById(R.id.time_1_button);
        time_2_button = (ToggleButton) findViewById(R.id.time_2_button);
        time_3_button = (ToggleButton) findViewById(R.id.time_3_button);
        time_4_button = (ToggleButton) findViewById(R.id.time_4_button);


        time_1_setting.setOnClickListener(this);
        time_2_setting.setOnClickListener(this);
        time_3_setting.setOnClickListener(this);
        time_4_setting.setOnClickListener(this);


        time_1_setting.setEnabled(false);
        time_2_setting.setEnabled(false);
        time_3_setting.setEnabled(false);
        time_4_setting.setEnabled(false);


        time_1_value = (TextView) findViewById(R.id.time_1_value);
        time_2_value = (TextView) findViewById(R.id.time_2_value);
        time_3_value = (TextView) findViewById(R.id.time_3_value);
        time_4_value = (TextView) findViewById(R.id.time_4_value);

        fromSettingExtensionFlag = false;
        fromSettingAlarmFlag = false;
        fromSettingExtensionValue = "";
        fromSettingAlarmnValue = "";
        fromSettingTime1Value = "";
        fromSettingTime2Value = "";
        fromSettingTime3Value = "";
        fromSettingTime4Value = "";

        loadSetting();
        if (isCustomize) {
            activateCustomize.setChecked(true);
            time_setting_mode.setText("Customize");
            time_1_value.setText(fromSettingTime1Value);
            time_2_value.setText(fromSettingTime2Value);
            time_3_value.setText(fromSettingTime3Value);
            time_4_value.setText(fromSettingTime4Value);
            time_1_button.setChecked(true);
            time_2_button.setChecked(true);
            time_3_button.setChecked(true);
            time_4_button.setChecked(true);
        }


        baseActivity = new BaseActivity(this);
        sEditor.clear();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home: {
                onBackPressed();
                return true;
            }


        }

        return false;
    }

    public void onBackPressed() {
        saveSetting();
        baseActivity.finish();
        Intent intent = new Intent(this, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        baseActivity.slideOutToRight(this, this.findViewById(android.R.id.content));
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView.isChecked()) {
            time_setting_mode.setText("Customize");
            time_1_setting.setEnabled(true);
            time_2_setting.setEnabled(true);
            time_3_setting.setEnabled(true);
            time_4_setting.setEnabled(true);

            if (!isCustomize) {
                Toast.makeText(this, "시간을 설정해주세요.", Toast.LENGTH_LONG).show();
            }
            isCustomize = true;

        } else {

            time_setting_mode.setText("Default");
            time_1_setting.setEnabled(false);
            time_2_setting.setEnabled(false);
            time_3_setting.setEnabled(false);
            time_4_setting.setEnabled(false);

            time_1_value.setText("15");
            time_2_value.setText("30");
            time_3_value.setText("45");
            time_4_value.setText("60");

            time_1_button.setChecked(false);
            time_2_button.setChecked(false);
            time_3_button.setChecked(false);
            time_4_button.setChecked(false);

            isCustomize = false;
            Toast.makeText(this, "시간을 기본값으로 변경합니다.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.time_1_setting: {
                timeSettingDialog("Time 1의 시간을 설정해주세요.");
                tempFlag = 1;


                break;
            }

            case R.id.time_2_setting: {
                timeSettingDialog("Time 2의 시간을 설정해주세요.");
                tempFlag = 2;


                break;
            }

            case R.id.time_3_setting: {
                timeSettingDialog("Time 3의 시간을 설정해주세요.");
                tempFlag = 3;

                break;
            }

            case R.id.time_4_setting: {
                timeSettingDialog("Time 4의 시간을 설정해주세요.");
                tempFlag = 4;

                break;
            }

            case R.id.btn_dialog_timepicker_setting: {
                getValueFromNumberPicker();

                break;
            }

            case R.id.btn_dialog_timepicker_cancel: {
                timeSetterDialog.dismiss();

                break;
            }

        }


    }


    public void onPause() {
        super.onPause();
        saveSetting();
    }


    public void saveSetting() {
        sEditor.clear();
        sEditor.putBoolean("customizeFlag", isCustomize);
        sEditor.putBoolean("extensionFlag", fromSettingExtensionFlag);
        sEditor.putBoolean("alarmFlag", fromSettingAlarmFlag);
        sEditor.putString("time_1_value", time_1_value.getText().toString());
        sEditor.putString("time_2_value", time_2_value.getText().toString());
        sEditor.putString("time_3_value", time_3_value.getText().toString());
        sEditor.putString("time_4_value", time_4_value.getText().toString());
        sEditor.putString("extension_time", fromSettingExtensionValue);
        sEditor.putString("alarm_time", fromSettingAlarmnValue);
        sEditor.commit();
    }

    public void loadSetting() {

        isCustomize = setting.getBoolean("customizeFlag", false);
        fromSettingExtensionFlag = setting.getBoolean("extensionFlag", false);
        fromSettingAlarmFlag = setting.getBoolean("alarmFlag", false);
        fromSettingTime1Value = setting.getString("time_1_value", "");
        fromSettingTime2Value = setting.getString("time_2_value", "");
        fromSettingTime3Value = setting.getString("time_3_value", "");
        fromSettingTime4Value = setting.getString("time_4_value", "");
        fromSettingExtensionValue = setting.getString("extension_time", "");
        fromSettingAlarmnValue = setting.getString("alarm_time", "");
    }

    public void timeSettingDialog(String comment) {
        timeSetterDialog = new Dialog(this);
        timeSetterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        timeSetterDialog.setContentView(R.layout.custom_dialog_numberpicker);
        timerPicker = (NumberPicker) timeSetterDialog.findViewById(R.id.horizontal_number_picker);
        btn_dialog_cancel = (Button) timeSetterDialog.findViewById(R.id.btn_dialog_timepicker_cancel);
        btn_dialog_setting = (Button) timeSetterDialog.findViewById(R.id.btn_dialog_timepicker_setting);
        timerPicker.setMinValue(3);
        timerPicker.setMaxValue(24);
        String[] timeValues = new String[22];

        for (int i = 0; i < timeValues.length; i++) {
            String number = Integer.toString((i + 3) * 5);
            timeValues[i] = number;
        }

        timerPicker.setDisplayedValues(timeValues);

        text_dialog_timepicker = (TextView) timeSetterDialog.findViewById(R.id.text_dialog_timepicker);
        text_dialog_timepicker.setText(comment);
//        timerPicker.setOnValueChangedListener(this);
        btn_dialog_cancel.setOnClickListener(this);
        btn_dialog_setting.setOnClickListener(this);

        timeSetterDialog.show();
    }


    public void getValueFromNumberPicker() {
        tempTime = 0;
        tempTime = 5 * (timerPicker.getValue());
        String temp_time = "";
        if (tempTime != 0) {
            temp_time = Integer.toString(tempTime);
        }

        if (tempFlag == 1) {
            time_1_value.setText(temp_time);
            time_1_button.setChecked(true);
        } else if (tempFlag == 2) {
            time_2_value.setText(temp_time);
            time_2_button.setChecked(true);
        } else if (tempFlag == 3) {
            time_3_value.setText(temp_time);
            time_3_button.setChecked(true);
        } else if (tempFlag == 4) {
            time_4_value.setText(temp_time);
            time_4_button.setChecked(true);
        }

        timeSetterDialog.dismiss();
        tempTime = 0;
        tempFlag = 0;
    }
}
