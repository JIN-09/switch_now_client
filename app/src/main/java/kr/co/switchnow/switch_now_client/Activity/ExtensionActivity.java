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

import com.shawnlin.numberpicker.NumberPicker;

import kr.co.switchnow.switch_now_client.R;

import static kr.co.switchnow.switch_now_client.Activity.ReadyActivity.userSetting;

public class ExtensionActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    TextView extension_title_txt;
    BaseActivity baseActivity;

    private Switch activateAlarm;
    private Switch activateExtension;
    private SharedPreferences.Editor sEditor;
    private SharedPreferences setting;

    TextView alarm_time_setting;
    TextView extension_time_setting;
    TextView alarm_time_value;
    TextView extension_time_value;


    Dialog timeSetterDialog = null;

    Boolean fromSettingExtensionFlag;
    Boolean fromSettingAlarmFlag;
    Boolean fromSettingCustomizeFlag;

    String fromSettingExtensionValue;
    String fromSettingAlarmnValue;
    String fromSettingTime1Value;
    String fromSettingTime2Value;
    String fromSettingTime3Value;
    String fromSettingTime4Value;

    NumberPicker timePicker;
    TextView text_dialog_timepicker;
    Button btn_dialog_cancel;
    Button btn_dialog_setting;

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
        getSupportActionBar().setCustomView(R.layout.extension_title);
        setContentView(R.layout.activity_extension);


        setting = getSharedPreferences(userSetting, Context.MODE_PRIVATE);
        sEditor = setting.edit();

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.switchLime), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        extension_title_txt = (TextView) findViewById(R.id.extension_title_txt);
        Typeface face = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            face = getResources().getFont(R.font.enorbit);
        }else{
            face = Typeface.createFromAsset(getAssets(), "enotbit.TTF");
        }
        extension_title_txt.setTypeface(face);
        baseActivity = new BaseActivity(this);

        alarm_time_setting = (TextView) findViewById(R.id.alarm_time_setting);
        extension_time_setting = (TextView) findViewById(R.id.extension_time_setting);
        alarm_time_setting.setOnClickListener(this);
        extension_time_setting.setOnClickListener(this);
        alarm_time_setting.setClickable(false);
        extension_time_setting.setClickable(false);

        alarm_time_value = (TextView) findViewById(R.id.alarm_time_value);
        extension_time_value = (TextView) findViewById(R.id.extension_time_value);



        activateAlarm = (Switch) findViewById(R.id.alarm_setting_trigger);
        activateExtension = (Switch) findViewById(R.id.extension_setting_trigger);
        activateAlarm.setOnCheckedChangeListener(this);
        activateExtension.setOnCheckedChangeListener(this);


        fromSettingExtensionFlag = false;
        fromSettingAlarmFlag = false;
        fromSettingCustomizeFlag = false;

        fromSettingExtensionValue = "";
        fromSettingAlarmnValue = "";
        fromSettingTime1Value = "";
        fromSettingTime2Value = "";
        fromSettingTime3Value = "";
        fromSettingTime4Value = "";
        loadSetting();
        if(fromSettingAlarmFlag){
            activateAlarm.setChecked(true);
            alarm_time_value.setText(fromSettingAlarmnValue);

        }
        if(fromSettingExtensionFlag){
            activateExtension.setChecked(true);
            extension_time_value.setText(fromSettingExtensionValue);
        }

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
//        saveDialog("설정을 저장하시겠습니까?");
        saveSetting();
        baseActivity.finish();
        Intent intent = new Intent(this, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        baseActivity.slideOutToRight(this, this.findViewById(android.R.id.content));
    }


    public void saveSetting() {
        sEditor.clear();
        sEditor.putBoolean("customizeFlag", fromSettingCustomizeFlag);
        sEditor.putBoolean("extensionFlag", fromSettingExtensionFlag);
        sEditor.putBoolean("alarmFlag", fromSettingAlarmFlag);
        sEditor.putString("time_1_value", fromSettingTime1Value);
        sEditor.putString("time_2_value", fromSettingTime2Value);
        sEditor.putString("time_3_value", fromSettingTime3Value);
        sEditor.putString("time_4_value", fromSettingTime4Value);
        sEditor.putString("extension_time", fromSettingExtensionValue);
        sEditor.putString("alarm_time", fromSettingAlarmnValue);
        sEditor.commit();
    }

    public void loadSetting() {

        fromSettingCustomizeFlag = setting.getBoolean("customizeFlag", false);
        fromSettingExtensionFlag = setting.getBoolean("extensionFlag", false);
        fromSettingAlarmFlag = setting.getBoolean("alarmFlag", false);
        fromSettingTime1Value = setting.getString("time_1_value", "");
        fromSettingTime2Value = setting.getString("time_2_value", "");
        fromSettingTime3Value = setting.getString("time_3_value", "");
        fromSettingTime4Value = setting.getString("time_4_value", "");
        fromSettingExtensionValue = setting.getString("extension_time", "");
        fromSettingAlarmnValue = setting.getString("alarm_time", "");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.alarm_time_setting: {
                timeSettingDialog("알람시간을 설정해주세요.");
                alarmSetting();
                tempFlag=1;
                break;
            }


            case R.id.extension_time_setting: {
                timeSettingDialog("연장시간을 설정해주세요.");
                extensionSetting();
                tempFlag=2;
                break;
            }

            case R.id.btn_dialog_timepicker_setting :{
                getValueFromNumberPicker();

                break;
            }

            case R.id.btn_dialog_timepicker_cancel : {
                timeSetterDialog.dismiss();


                break;
            }

        }


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {

            case R.id.alarm_setting_trigger: {

                if (activateAlarm.isChecked()) {
                    alarm_time_setting.setClickable(true);
                    if (!fromSettingAlarmFlag) {
                        Toast.makeText(this, "알람시간을 설정해주세요.", Toast.LENGTH_LONG).show();
                    }
                    fromSettingAlarmFlag = true;

                } else {
                    alarm_time_setting.setClickable(false);
                    alarm_time_value.setText("NN");
                    fromSettingAlarmFlag = false;
                    Toast.makeText(this, "시간을 기본값으로 변경합니다.", Toast.LENGTH_LONG).show();
                }

                break;
            }

            case R.id.extension_setting_trigger: {

                if (activateExtension.isChecked()) {
                    extension_time_setting.setClickable(true);
                    if (!fromSettingExtensionFlag) {
                        Toast.makeText(this, "연장시간을 설정해주세요.", Toast.LENGTH_LONG).show();
                    }
                    fromSettingExtensionFlag = true;

                } else {
                    extension_time_setting.setClickable(false);
                    extension_time_value.setText("NN");
                    fromSettingExtensionFlag = false;
                    Toast.makeText(this, "시간을 기본값으로 변경합니다.", Toast.LENGTH_LONG).show();
                }

                break;
            }

        }
    }


    public void timeSettingDialog(String comment){
        timeSetterDialog = new Dialog(this);
        timeSetterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        timeSetterDialog.setContentView(R.layout.custom_dialog_numberpicker);
        timePicker = (NumberPicker) timeSetterDialog.findViewById(R.id.horizontal_number_picker);
        btn_dialog_cancel = (Button) timeSetterDialog.findViewById(R.id.btn_dialog_timepicker_cancel);
        btn_dialog_setting = (Button) timeSetterDialog.findViewById(R.id.btn_dialog_timepicker_setting);
        text_dialog_timepicker = (TextView) timeSetterDialog.findViewById(R.id.text_dialog_timepicker);
        text_dialog_timepicker.setText(comment);
        btn_dialog_cancel.setOnClickListener(this);
        btn_dialog_setting.setOnClickListener(this);
    }


    public void extensionSetting(){
        timePicker.setMinValue(3);
        timePicker.setMaxValue(24);
        String[] timeValues = new String[22];

        for (int i = 0; i < timeValues.length; i++) {
            String number = Integer.toString((i + 3) * 5);
            timeValues[i] = number;
        }

        timePicker.setDisplayedValues(timeValues);
        timeSetterDialog.show();
    }

    public void alarmSetting(){
        timePicker.setMinValue(3);
        timePicker.setMaxValue(10);

        timeSetterDialog.show();
    }

    public void getValueFromNumberPicker(){
        tempTime = 0;
        String temp_time = "";

        if(tempFlag==1){
            tempTime = (timePicker.getValue());
            if (tempTime != 0) {
                temp_time = Integer.toString(tempTime);
            }
            alarm_time_value.setText(temp_time);
            fromSettingAlarmnValue = alarm_time_value.getText().toString();
        }else if(tempFlag==2){
            tempTime = 5 * (timePicker.getValue());
            if (tempTime != 0) {
                temp_time = Integer.toString(tempTime);
            }
            extension_time_value.setText(temp_time);
            fromSettingExtensionValue = extension_time_value.getText().toString();
        }

        timeSetterDialog.dismiss();
        tempTime = 0;
        tempFlag = 0;
    }

    public void onPause(){
        super.onPause();
        saveSetting();
    }

}
