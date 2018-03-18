package kr.co.switchnow.switch_now_client.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import kr.co.switchnow.switch_now_client.ADT.ChatRoom;


/**
 * Created by ceo on 2017-06-16.
 */

public class ChatRoomDBManager {

    private static final String DATABASE_NAME = "chat.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase roomDataBase;


    public static final String TABLE_ChatRoom = "chat_room";
    public static final String IDX = "idx_";
    public static final String RI = "room_id";
    public static final String UI = "user_id";
    public static final String CW = "chat_with";
    public static final String CWN = "chat_with_name";
    public static final String RS = "room_status";
    public static final String RU = "recent_update";
    public static final String LM = "last_message";
    public static final String IMG_U = "image_url";


    public static final String TABLE_CREATE_ROOM =
            "CREATE TABLE " + TABLE_ChatRoom + " (" +
                    IDX + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RI + " TEXT, " +
                    UI + " TEXT, " +
                    CW + " TEXT, " +
                    CWN + " TEXT, " +
                    RS + " INTEGER, " +
                    RU + " TEXT, " +
                    LM + " TEXT, " +
                    IMG_U + " TEXT " + ")";


    public void addRoomData(ChatRoom chatroomData) {

        roomDataBase = DatabaseManager.getInstance().openDatabase();
        ContentValues roomValues = new ContentValues();

        roomValues.put(RI, chatroomData.getRoomId());
        roomValues.put(UI, chatroomData.user_id);
        roomValues.put(CW, chatroomData.chat_with);
        roomValues.put(CWN, chatroomData.userName);
        roomValues.put(RS, chatroomData.room_status);
        roomValues.put(RU, chatroomData.updated_time);
        roomValues.put(LM, chatroomData.last_message);
        roomValues.put(IMG_U, chatroomData.user_img_url);

        try {
            roomDataBase.insertOrThrow(TABLE_ChatRoom, null, roomValues);
            Log.d("TAG", "-=------------------->ADDED_ROOMDATA");
            DatabaseManager.getInstance().closeDatabase();
        } catch (SQLException e) {
            Log.d("TAG", "---------------------------->Error " + e.getCause() + " " + e.getMessage());
        }

    }

    public void insertRoomData(String roomId, String user_id, String chat_with, int room_status, String updated_time, String last_message, String userName, String user_img_url) {
        SQLiteDatabase roomDataBase = DatabaseManager.getInstance().openDatabase();
        ContentValues roomValues = new ContentValues();

        roomValues.put(RI, roomId);
        roomValues.put(UI, user_id);
        roomValues.put(CW, chat_with);
        roomValues.put(CWN, userName);
        roomValues.put(RS, room_status);
        roomValues.put(RU, updated_time);
        roomValues.put(LM, last_message);
        roomValues.put(IMG_U, user_img_url);
        try {
            roomDataBase.insertOrThrow(TABLE_ChatRoom, null, roomValues);
            Log.d("TAG", "-=------------------->ADDED_ROOMDATA");
        } catch (SQLException e) {
            Log.d("TAG", "---------------------------->Error " + e.getCause() + " " + e.getMessage());
        }

        DatabaseManager.getInstance().closeDatabase();
    }


    public ChatRoom getChatRoomData(String RoomIdForChatroom) {
        ChatRoom fetchData = new ChatRoom();
        roomDataBase = DatabaseManager.getInstance().openDatabase();

        Cursor cursor = roomDataBase.query(TABLE_ChatRoom, new String[]{RI, UI, CW, CWN, RS, RU, LM, IMG_U}, RI + "=?", new String[]{RoomIdForChatroom}, null, null, null);
        if (cursor.moveToFirst()) {
            fetchData.setRoomId(cursor.getString(0));
            fetchData.setUser_id(cursor.getString(1));
            fetchData.setChat_with(cursor.getString(2));
            fetchData.setUserName(cursor.getString(3));
            fetchData.setRoom_status(cursor.getInt(4));
            fetchData.setUpdated_time(cursor.getString(5));
            fetchData.setlastMessage(cursor.getString(6));
            fetchData.setUser_img_url(cursor.getString(7));
        } while (cursor.moveToNext());

        DatabaseManager.getInstance().closeDatabase();
        return fetchData;
    }

    public String updateChatRoomData(ChatRoom chatroomData) {
        roomDataBase = DatabaseManager.getInstance().openDatabase();
        ContentValues roomValues = new ContentValues();

        roomValues.put(CWN, chatroomData.userName);
        roomValues.put(RS, chatroomData.room_status);
        roomValues.put(RU, chatroomData.updated_time);
        roomValues.put(LM, chatroomData.last_message);

        roomDataBase.update(TABLE_ChatRoom, roomValues, RI + " = ?", new String[]{chatroomData.roomId});
        Log.d("TAG", "UPDATE ROOM DATA------------------------>" + TABLE_ChatRoom);
        DatabaseManager.getInstance().closeDatabase();
        return chatroomData.roomId;
    }

    public void removeChatRoomData(ChatRoom chatroomData) {
        roomDataBase = DatabaseManager.getInstance().openDatabase();
        roomDataBase.delete(TABLE_ChatRoom, RI + " = ?", new String[]{chatroomData.roomId});

    }

    public ArrayList<ChatRoom> getAllChatRoomData() {
        ArrayList<ChatRoom> ChatRoomDataFromLocal = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ChatRoom + " ORDER BY " + IDX + " DESC";
        Log.d("TAG", "CHATROOM DATABASE-----------------------> query: " + selectQuery);
        roomDataBase = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = roomDataBase.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {
                ChatRoom chatRoomDataFromSQLite = new ChatRoom();
                chatRoomDataFromSQLite.setRoomId(cursor.getString(1));
                chatRoomDataFromSQLite.setUser_id(cursor.getString(2));
                chatRoomDataFromSQLite.setChat_with(cursor.getString(3));
                chatRoomDataFromSQLite.setUserName(cursor.getString(4));
                chatRoomDataFromSQLite.setRoom_status(cursor.getInt(5));
                chatRoomDataFromSQLite.setUpdated_time(cursor.getString(6));
                chatRoomDataFromSQLite.setlastMessage(cursor.getString(7));
                chatRoomDataFromSQLite.setUser_img_url(cursor.getString(8));


                ChatRoomDataFromLocal.add(chatRoomDataFromSQLite);
            } while (cursor.moveToNext());
        }

        DatabaseManager.getInstance().closeDatabase();
        return ChatRoomDataFromLocal;
    }


}
