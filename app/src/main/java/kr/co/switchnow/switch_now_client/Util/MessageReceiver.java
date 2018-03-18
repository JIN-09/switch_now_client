package kr.co.switchnow.switch_now_client.Util;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static kr.co.switchnow.switch_now_client.Util.ServiceThread.serviceSocket;


/**
 * Created by ceo on 2017-06-29.
 */

public class MessageReceiver extends Service {

    public static final String TAG = MessageReceiver.class.getSimpleName();
    public static final String MessageReceiver = "kr.co.switchnow.switch_now_client.MessageReceiver";

    ServiceThread serviceThread;
    public static final int CHAT_PORT_NUM = 9999;
    public static final String CHAT_SERVER_IP = "115.71.232.209";
    public static final int CONNECTION_TIMEOUT = 10000;

    Handler handler;


    public void onCreate() {
        super.onCreate();
        Log.d("TAG_SERVICE", "-------------------------------------------------->Listen chat Message...");

//        handlerThread = new HandlerThread();
//        handlerThread.start();
//        looper = handlerThread.getLooper();

    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("TAG_SERVICE", "-------------------------onStartCommand() 실행됨");

        handler = new ServiceHandler();
        serviceThread = new ServiceThread(handler);
//        serviceThread = new ServiceThread();
        serviceThread.start();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        try {
            throw new UnsupportedEncodingException("Not yet Implemented");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void onDestroy() {
        super.onDestroy();
        serviceThread.stopForever();
        serviceThread = null;
        Log.d("TAG_SERVICE", "-----------------------------> Server Connection is Destroyed");
        try {
            serviceSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(android.os.Message msg) {
            synchronized (this){
                while (true) {
                    handler.sendEmptyMessage(100);
                    Log.d("TAG_SERVICE","looping............");
                }
            }
        }

        public ServiceHandler(){

        }

    }


}
