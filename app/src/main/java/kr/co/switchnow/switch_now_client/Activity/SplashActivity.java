package kr.co.switchnow.switch_now_client.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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

import static kr.co.switchnow.switch_now_client.Activity.ReadyActivity.SwitchTime;
import static kr.co.switchnow.switch_now_client.Activity.loginActivity.userSession;

/**
 * Created by ceo on 2017-03-13.
 */

public class SplashActivity extends Activity {

    private SharedPreferences.Editor mEditor;
    private SharedPreferences.Editor tEditor;
    private SharedPreferences session;
    private SharedPreferences time_value;
    private boolean isFirstLogin;
    private boolean sessionFlag;

    String tmpName;
    String tmpMobile;
    String tmpUserId;
    Boolean tmpIsFirstLog;
    Boolean isSwitchOn;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isSwitchOn = false;
        session = getSharedPreferences(userSession, Context.MODE_PRIVATE);
        mEditor = session.edit();
        time_value = getSharedPreferences(SwitchTime, Context.MODE_PRIVATE);

        loadTime();
        loadSession();

        initialize();
    }

    private void initialize() {
        int switchDarkgrey = getResources().getColor(R.color.switchDarkgrey);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(switchDarkgrey);
        }


        setContentView(R.layout.splash_screen);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {


                overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                if (!isSwitchOn) {
                    if (sessionFlag == true) {
                        new AsyncSession().execute(tmpUserId, tmpMobile);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        start();

                    } else {
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        start_login();

                    }
                } else {
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    switchOn();
                }
                finish();// 액티비티 종료
            }
        };

        handler.sendEmptyMessageDelayed(0, 3100);    // ms, 3초후 종료시킴

    }


    //
    public void loadSession() {
        boolean tmpSessionFlag;

        tmpSessionFlag = session.getBoolean("Session", false);
        Log.d("TAG", "임시_세션 플래그 --- > " + tmpSessionFlag);

        if (tmpSessionFlag != false) {
            sessionFlag = tmpSessionFlag;
            tmpName = session.getString("userName", "");
            tmpMobile = session.getString("userMobile", "");
            tmpUserId = session.getString("user_Fb_id", "");
            tmpIsFirstLog = session.getBoolean("isFirstLogin", false);
        }

        mEditor.clear();
    }


    void start() {
        Intent intent = new Intent(SplashActivity.this, ReadyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        Toast.makeText(SplashActivity.this, "Welcom Back", Toast.LENGTH_LONG).show();
//        startActivity(new Intent(this, ReadyActivity.class));
    }

    void start_login() {
        Intent intent = new Intent(SplashActivity.this, loginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }


    void switchOn() {
        Intent intent = new Intent(SplashActivity.this, InteractionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }


    private class AsyncSession extends AsyncTask<String, String, String> {
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
                url = new URL("http://115.71.232.209/client/checked_session.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
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
                        .appendQueryParameter("_mobile", params[1]);

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

            //this method will be running on UI thread


            if (result.equalsIgnoreCase("confirmed")) {

                Log.d("TAG", "로그인 업데이트 확인 ---------> " + sessionFlag);

            } else if (result.equalsIgnoreCase("false")) {

                // If username and password does not match display a error message


            } else if (result.equalsIgnoreCase("login")) {


            }
        }

    }


    public void loadTime() {
        isSwitchOn = time_value.getBoolean("isSwitchOn", false);
    }


}


