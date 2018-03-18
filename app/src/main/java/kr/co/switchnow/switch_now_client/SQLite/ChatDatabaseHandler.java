package kr.co.switchnow.switch_now_client.SQLite;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import kr.co.switchnow.switch_now_client.Util.DatabaseContext;

import static kr.co.switchnow.switch_now_client.SQLite.ChatMessageDBManager.TABLE_CREATE_MESSAGE;
import static kr.co.switchnow.switch_now_client.SQLite.ChatMessageDBManager.TABLE_ChatMessage;
import static kr.co.switchnow.switch_now_client.SQLite.ChatRoomDBManager.TABLE_CREATE_ROOM;
import static kr.co.switchnow.switch_now_client.SQLite.ChatRoomDBManager.TABLE_ChatRoom;

/**
 * Created by ceo on 2017-06-16.
 */

public class ChatDatabaseHandler  extends SQLiteOpenHelper {

    public static final String TAG = ChatDatabaseHandler.class.getSimpleName().toString();
    private static final String DATABASE_NAME ="chat.db";
    private static final int DATABASE_VERSION = 1;


    public ChatDatabaseHandler(){
        super(DatabaseContext.getmContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_MESSAGE);
        db.execSQL(TABLE_CREATE_ROOM);
        Log.d(TAG, "Database tables created--------->"+TABLE_CREATE_ROOM);
        Log.d(TAG, "Message Database tables created--------->"+TABLE_CREATE_MESSAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ChatRoom);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ChatMessage);
        onCreate(db);
    }


}
