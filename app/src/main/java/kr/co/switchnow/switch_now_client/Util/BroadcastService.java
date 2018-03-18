package kr.co.switchnow.switch_now_client.Util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.isSwitchOn;
import static kr.co.switchnow.switch_now_client.Activity.ReadyActivity.SwitchTime;
import static kr.co.switchnow.switch_now_client.Util.ServiceThread.serviceSocket;


/**
 * Created by ceo on 2017-04-09.
 */

public class BroadcastService extends Service {

    private final static String TAG = "BroadcastService";
    public static final String COUNTDOWN_BR = "kr.co.switchnow.switch_now_client.countdown_br";

    private SharedPreferences.Editor tEditor;
    private SharedPreferences time_value;
    String switch_value;
    int switch_time;
    int switch_time_mill;


    Intent bi = new Intent(COUNTDOWN_BR);
    CountDownTimer cdt = null;

    @Override
    public void onCreate() {
        super.onCreate();

        time_value = getSharedPreferences(SwitchTime, Context.MODE_PRIVATE);
        Log.i(TAG, "Starting timer...");
        switch_value ="";
        switch_time = 0;
        switch_time_mill=0;
        isSwitchOn = false;
        loadTime();

        cdt = new CountDownTimer(switch_time_mill, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(TAG, "Countdown seconds remaining___: " + millisUntilFinished / 1000);
                bi.putExtra("countdown", millisUntilFinished);
                bi.putExtra("isSwitchOn", isSwitchOn);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {

                isSwitchOn = false;
                try {

                    if(serviceSocket!=null){
                        serviceSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Timer finished");
            }
        };
        cdt.start();

    }

    public void loadTime(){

        switch_time = time_value.getInt("switch_time", 0);
        switch_time_mill = time_value.getInt("switch_time_mill", 0);
        switch_value = time_value.getString("switch_value", "");
        isSwitchOn = time_value.getBoolean("isSwitchOn", false);
    }

    public void onDestroy() {

        cdt.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
