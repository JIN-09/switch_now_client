package kr.co.switchnow.switch_now_client.Util;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import kr.co.switchnow.switch_now_client.SQLite.ChatDatabaseHandler;
import kr.co.switchnow.switch_now_client.SQLite.DatabaseManager;

/**
 * Created by ceo on 2017-06-26.
 */

public class DatabaseContext extends MultiDexApplication {

    private static Context mContext;
    private static ChatDatabaseHandler chatDatabaseHandler;

    public void onCreate(){
        super.onCreate();

        mContext = this.getApplicationContext();
        chatDatabaseHandler = new ChatDatabaseHandler();
        DatabaseManager.initializeInstance(chatDatabaseHandler);

        MultiDex.install(getmContext());
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(getmContext());
    }

    public static Context getmContext(){

        return mContext;
    }


}
