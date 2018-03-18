package kr.co.switchnow.switch_now_client.Activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import kr.co.switchnow.switch_now_client.R;
import kr.co.switchnow.switch_now_client.Util.CustomDialog;
import kr.co.switchnow.switch_now_client.Util.LocationInfo;
import kr.co.switchnow.switch_now_client.Util.MessageReceiver;
import okhttp3.OkHttpClient;

import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.isSwitchOn;
import static kr.co.switchnow.switch_now_client.Activity.loginActivity.userSession;


public class ReadyActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    public static final String userSetting = "settingPrefs";
    public static final String SwitchTime = "timePrefs";
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;


    private static final int REQUEST_ACCESS_LOCATION = 1;


    private SharedPreferences.Editor sEditor;
    private SharedPreferences.Editor tEditor;
    private SharedPreferences setting;
    private SharedPreferences time_value;
    private SharedPreferences session;

    private int switch_time;
    private int switch_time_mill;
    Boolean timeSelectedFlag;
    ToggleButton time1_tb;
    ToggleButton time2_tb;
    ToggleButton time3_tb;
    ToggleButton time4_tb;
    ToggleButton logo_tb;
    TextView time1_txt;
    TextView time2_txt;
    TextView time3_txt;
    TextView time4_txt;
    Button readyBtn;
    ImageButton profileBtn;
    BaseActivity baseActivity;
    int switch_lime_color;
    int switch_grey;
    int switchDarkgrey;
    int time_flag_for_switch;

    Boolean fromSettingCustomizeFlag;
    String fromSettingTime1Value;
    String fromSettingTime2Value;
    String fromSettingTime3Value;
    String fromSettingTime4Value;
    String switch_value;

    String fromSessionEmail;

    public static String sendToServerLongitude;
    public static String sendToServerLatitude;

    protected LocationInfo locationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switchDarkgrey = getResources().getColor(R.color.switchDarkgrey);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(switchDarkgrey);
        }

        switch_lime_color = getResources().getColor(R.color.switchLime);
        switch_grey = getResources().getColor(R.color.switchGrey);
        Stetho.initializeWithDefaults(this);

        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        time_value = getSharedPreferences(SwitchTime, Context.MODE_PRIVATE);
        setting = getSharedPreferences(userSetting, Context.MODE_PRIVATE);
        sEditor = setting.edit();
        tEditor = time_value.edit();

        setContentView(R.layout.activity_ready);
        this.overridePendingTransition(0, 0);
        time1_tb = (ToggleButton) this.findViewById(R.id.time_1_button);
        time2_tb = (ToggleButton) this.findViewById(R.id.time_2_button);
        time3_tb = (ToggleButton) this.findViewById(R.id.time_3_button);
        time4_tb = (ToggleButton) this.findViewById(R.id.time_4_button);
        logo_tb = (ToggleButton) this.findViewById(R.id.logo_white_toggle);
        time1_txt = (TextView) this.findViewById(R.id.time_1_text);
        time2_txt = (TextView) this.findViewById(R.id.time_2_text);
        time3_txt = (TextView) this.findViewById(R.id.time_3_text);
        time4_txt = (TextView) this.findViewById(R.id.time_4_text);
        readyBtn = (Button) this.findViewById(R.id.start_btn_ready);
        profileBtn = (ImageButton) this.findViewById(R.id.profile_btn);
        profileBtn.setOnClickListener(this);
        readyBtn.setOnClickListener(this);
        time1_tb.setOnCheckedChangeListener(this);
        time2_tb.setOnCheckedChangeListener(this);
        time3_tb.setOnCheckedChangeListener(this);
        time4_tb.setOnCheckedChangeListener(this);

        isSwitchOn = true;
        timeSelectedFlag = false;
        switch_time = 0;
        switch_value = "";
        switch_time_mill = 0;
        time_flag_for_switch = 0;
        baseActivity = new BaseActivity(this);

        fromSettingCustomizeFlag = false;
        fromSettingTime1Value = "";
        fromSettingTime2Value = "";
        fromSettingTime3Value = "";
        fromSettingTime4Value = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_LOCATION);
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_LOCATION);
            } else {

            }

        }

        //        loadSetting();
        if (fromSettingCustomizeFlag) {

            time1_txt.setText(fromSettingTime1Value + "min.");
            time2_txt.setText(fromSettingTime2Value + "min.");
            time3_txt.setText(fromSettingTime3Value + "min.");
            time4_txt.setText(fromSettingTime4Value + "min.");

        } else {
            fromSettingTime1Value = "15";
            fromSettingTime2Value = "30";
            fromSettingTime3Value = "45";
            fromSettingTime4Value = "60";
        }

        fromSessionEmail = "";

        sendToServerLongitude = null;
        sendToServerLatitude = null;
        session = getSharedPreferences(userSession, Context.MODE_PRIVATE);
        loadDataFromSession();
        getLocationInfo();


        sEditor.clear();
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_LOCATION: {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    loadSetting();
                    getLocationInfo();
                }

                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.profile_btn: {

                startActivity(new Intent(this, r_profileActivity.class));
                break;

            }


            case R.id.start_btn_ready: {

                Log.d("TAG", "시간설정---------->" + timeSelectedFlag);
                if (timeSelectedFlag) {
                    baseActivity.finish();
                    Intent intent = new Intent(this, InteractionActivity.class);
                    if (time_flag_for_switch == 1) {
                        switch_time = Integer.parseInt(fromSettingTime1Value);
                        switch_value = fromSettingTime1Value;
                    } else if (time_flag_for_switch == 2) {
                        switch_time = Integer.parseInt(fromSettingTime2Value);
                        switch_value = fromSettingTime2Value;
                    } else if (time_flag_for_switch == 3) {
                        switch_time = Integer.parseInt(fromSettingTime3Value);
                        switch_value = fromSettingTime3Value;
                    } else if (time_flag_for_switch == 4) {
                        switch_time = Integer.parseInt(fromSettingTime4Value);
                        switch_value = fromSettingTime4Value;
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    switch_time_mill = (switch_time * 1000 * 60);
                    isSwitchOn = true;
                    saveTime();
                    new AsyncIsOnline().execute(fromSessionEmail, sendToServerLongitude, sendToServerLatitude);
                    startService(new Intent(this, MessageReceiver.class));
                    boolean check = isMyServiceRunning(MessageReceiver.class);
                    Log.d("TAG", "Service------------------Running Check------------------> : " + check);

                    startActivity(intent);
                    ReadyActivity.this.finish();
                    baseActivity.fadeOut(this, this.findViewById(android.R.id.content));

                } else {
                    return;
                }


                break;
            }
        }


    }


    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {

            case R.id.time_1_button: {
                if (isChecked) {
                    time1_txt.setTextColor(switch_lime_color);
                    time2_tb.setChecked(false);
                    time3_tb.setChecked(false);
                    time4_tb.setChecked(false);
                    timeSelectedFlag = true;
                    readyBtn.setBackgroundResource(R.drawable.rounded_yellow);
                    readyBtn.setText("시작하기");
                    readyBtn.setTextColor(switchDarkgrey);
                    logo_tb.setChecked(true);
//                    String tempString = R.string.time_1.replaceAll("[^\\d]", "");
//                    total_switch_time = Integer.valueOf(tempString);
//                    Log.d("총 스위치 켜질 시간--------->", tempString);
                    time_flag_for_switch = 1;
                } else {
                    time1_txt.setTextColor(switch_grey);
                    timeSelectedFlag = false;
                    readyBtn.setBackgroundResource(R.drawable.rounded);
                    readyBtn.setText("시간을 선택해주세요.");
                    readyBtn.setTextColor(switchDarkgrey);
                    logo_tb.setChecked(false);
                    time_flag_for_switch = 0;
                }


                break;
            }

            case R.id.time_2_button: {
                if (isChecked) {
                    time2_txt.setTextColor(switch_lime_color);
                    time1_tb.setChecked(false);
                    time3_tb.setChecked(false);
                    time4_tb.setChecked(false);
                    timeSelectedFlag = true;
                    time_flag_for_switch = 2;
                    readyBtn.setBackgroundResource(R.drawable.rounded_yellow);
                    readyBtn.setText("시작하기");
                    readyBtn.setTextColor(switchDarkgrey);
                    logo_tb.setChecked(true);
//                    String tempString = R.string.time_2.replaceAll("[^\\d]", "");
//                    total_switch_time = Integer.valueOf(tempString);
//                    Log.d("총 스위치 켜질 시간--------->", tempString);

                } else {
                    time2_txt.setTextColor(switch_grey);
                    timeSelectedFlag = false;

                    readyBtn.setBackgroundResource(R.drawable.rounded);
                    readyBtn.setText("시간을 선택해주세요.");
                    readyBtn.setTextColor(switchDarkgrey);
                    logo_tb.setChecked(false);
                    time_flag_for_switch = 0;
                }


                break;
            }

            case R.id.time_3_button: {
                if (isChecked) {
                    time3_txt.setTextColor(switch_lime_color);
                    time1_tb.setChecked(false);
                    time2_tb.setChecked(false);
                    time4_tb.setChecked(false);
                    timeSelectedFlag = true;

                    readyBtn.setBackgroundResource(R.drawable.rounded_yellow);
                    readyBtn.setText("시작하기");
                    readyBtn.setTextColor(switchDarkgrey);
                    logo_tb.setChecked(true);
//                    String tempString = R.string.time_3.replaceAll("[^\\d]", "");
//                    total_switch_time = Integer.valueOf(tempString);
//                    Log.d("총 스위치 켜질 시간--------->", tempString);
                    time_flag_for_switch = 3;
                } else {
                    time3_txt.setTextColor(switch_grey);
                    timeSelectedFlag = false;
                    readyBtn.setBackgroundResource(R.drawable.rounded);
                    readyBtn.setText("시간을 선택해주세요.");
                    readyBtn.setTextColor(switchDarkgrey);
                    logo_tb.setChecked(false);
                    time_flag_for_switch = 0;
                }


                break;
            }

            case R.id.time_4_button: {
                if (isChecked) {
                    time4_txt.setTextColor(switch_lime_color);
                    time1_tb.setChecked(false);
                    time2_tb.setChecked(false);
                    time3_tb.setChecked(false);
                    timeSelectedFlag = true;
                    time_flag_for_switch = 4;
                    readyBtn.setBackgroundResource(R.drawable.rounded_yellow);
                    readyBtn.setText("시작하기");
                    readyBtn.setTextColor(switchDarkgrey);
                    logo_tb.setChecked(true);
//                    String tempString = R.string.time_4.replaceAll("[^\\d]", "");
//                    total_switch_time = Integer.valueOf(tempString);
//                    Log.d("총 스위치 켜질 시간--------->", tempString);

                } else {
                    time4_txt.setTextColor(switch_grey);
                    timeSelectedFlag = false;
                    readyBtn.setBackgroundResource(R.drawable.rounded);
                    readyBtn.setText("시간을 선택해주세요.");
                    readyBtn.setTextColor(switchDarkgrey);
                    logo_tb.setChecked(false);
                    time_flag_for_switch = 0;
                }

                break;
            }
        }
    }

    public void onBackPressed() {
        CustomDialog alert = new CustomDialog();
        alert.exitDialog(this, "Switch를 종료하시겠습니까?");
    }

    public void loadSetting() {

        fromSettingCustomizeFlag = setting.getBoolean("customizeFlag", false);
        fromSettingTime1Value = setting.getString("time_1_value", "");
        fromSettingTime2Value = setting.getString("time_2_value", "");
        fromSettingTime3Value = setting.getString("time_3_value", "");
        fromSettingTime4Value = setting.getString("time_4_value", "");

    }

    public void saveTime() {
        tEditor.clear();
        tEditor.putInt("switch_time", switch_time);
        tEditor.putString("switch_value", switch_value);
        tEditor.putInt("switch_time_mill", switch_time_mill);
        tEditor.putBoolean("isSwitchOn", isSwitchOn);
        tEditor.commit();
    }


    public void getLocationInfo() {

        locationInfo = new LocationInfo(ReadyActivity.this);
        if(locationInfo.canGetLocation()){
            sendToServerLatitude = String.valueOf(locationInfo.getLatitude());
            sendToServerLongitude = String.valueOf(locationInfo.getLongitude());
            if(locationInfo.progress.isShowing()){
                locationInfo.progress.dismiss();
            }
        }



        Log.d("TAG", "LATITUDE----------------------> " + sendToServerLatitude);
        Log.d("TAG", "LONGITUDE---------------------> " + sendToServerLongitude);
    }

    public void onResume() {
        super.onResume();
        getLocationInfo();

    }

    public void loadDataFromSession() {

        fromSessionEmail = session.getString("user_Fb_id", "");
    }


    private class AsyncIsOnline extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://115.71.232.209/client/online_signal.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                Log.d("TAG", "URL---------------->error" + url);
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);

                // Append parameters to URL (full_name, profile_image, email_id, gender, mobileNum);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("_email", params[0])
                        .appendQueryParameter("_longitude", params[1])
                        .appendQueryParameter("_latitude", params[2]);

                Log.d("TAG", "이메일 값----------------> " + fromSessionEmail);
                Log.d("TAG", "longitude 값--------------> " + sendToServerLongitude);
                Log.d("TAG", "latitude 값-------------->" + sendToServerLatitude);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                Log.d("TAG", "connection---------------->error" + url);
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    Log.d("TAG", "response----------------> true" + response_code);
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            Log.d("TAG", "result_String----------->" + result);

            if (result.equalsIgnoreCase("failure")) {


            } else if (result.equalsIgnoreCase("true")) {

                Log.d("TAG", "isONLINE-------------------------------->" + fromSessionEmail);

            } else {


            }
        }

    }


}
