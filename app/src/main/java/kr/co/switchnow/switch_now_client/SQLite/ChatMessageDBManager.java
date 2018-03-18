package kr.co.switchnow.switch_now_client.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import kr.co.switchnow.switch_now_client.ADT.ChatMessage;

import static kr.co.switchnow.switch_now_client.SQLite.ChatRoomDBManager.RI;

/**
 * Created by ceo on 2017-06-16.
 */

public class ChatMessageDBManager {


    public SQLiteDatabase messageDataBase;

    public static final String TABLE_ChatMessage = "chat_messages";
    public static final String IDX = "idx_";
    public static final String CHI = "chat_room_id";
    public static final String SI = "sender_id";
    public static final String SN = "sender_name";
    public static final String CT = "content_text";
    public static final String RS = "read_status";
    public static final String ST = "sent_time";
    public static final String MT = "msg_type";
    public static final String IMG_U = "image_url";


    public static final String TABLE_CREATE_MESSAGE =
            "CREATE TABLE " + TABLE_ChatMessage + " (" +
                    IDX + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CHI + " TEXT, " +
                    SI + " TEXT, " +
                    SN + " TEXT, " +
                    CT + " TEXT, " +
                    RS + " INTEGER, " +
                    ST + " TEXT, " +
                    MT + " INTEGER, " +
                    IMG_U + " TEXT " + ")";


    public void insertMessageData(String roomId, String sender_Id, String sender_name, String msg, int read_status, String send_time, int msgType, String img_url) {

        messageDataBase = DatabaseManager.getInstance().openDatabase();
        ContentValues messageValues = new ContentValues();

        messageValues.put(CHI, roomId);
        messageValues.put(SI, sender_Id);
        messageValues.put(SN, sender_name);
        messageValues.put(CT, msg);
        messageValues.put(RS, read_status);
        messageValues.put(ST, send_time);
        messageValues.put(MT, msgType);
        messageValues.put(IMG_U, img_url);
        try {
            messageDataBase.insertOrThrow(TABLE_ChatMessage, null, messageValues);
            Log.d("TAG", "-=------------------->ADDED_MESSAGEDATA");
        } catch (SQLException e) {
            Log.d("TAG", "---------------------------->Error " + e.getCause() + " " + e.getMessage());
        }

    }


    public void addMessageData(ChatMessage messageData) {

        messageDataBase = DatabaseManager.getInstance().openDatabase();
        ContentValues messageValues = new ContentValues();

        messageValues.put(CHI, messageData.roomId);
        messageValues.put(SI, messageData.sender_Id);
        messageValues.put(SN, messageData.sender_name);
        messageValues.put(CT, messageData.TextCotents);
        messageValues.put(RS, messageData.read_status);
        messageValues.put(ST, messageData.send_time);
        messageValues.put(MT, messageData.msgType);
        messageValues.put(IMG_U, messageData.opponent_img_url);
        try {
            messageDataBase.insertOrThrow(TABLE_ChatMessage, null, messageValues);
            Log.d("TAG", "-=------------------->ADDED_MESSAGEDATA");
        } catch (SQLException e) {
            Log.d("TAG", "---------------------------->Error " + e.getCause() + " " + e.getMessage());
        }

        DatabaseManager.getInstance().closeDatabase();
    }

    public ChatMessage getChatMessageData(String RoomIdForChatroom) {
        ChatMessage fetchData = new ChatMessage();
        messageDataBase = DatabaseManager.getInstance().openDatabase();

        Cursor cursor = messageDataBase.query(TABLE_ChatMessage, new String[]{CHI, SI, SN, CT, RS, ST, MT, IMG_U}, RI + "=?", new String[]{RoomIdForChatroom}, null, null, null);
        fetchData.setRoomId(cursor.getString(0));
        fetchData.setSender_Id(cursor.getString(1));
        fetchData.setSender_name(cursor.getString(2));
        fetchData.setTextCotents(cursor.getString(3));
        fetchData.setRead_status(cursor.getInt(4));
        fetchData.setSend_time(cursor.getString(5));
        fetchData.setMsgType(cursor.getInt(6));
        fetchData.setOpponent_img_url(cursor.getString(7));

        DatabaseManager.getInstance().closeDatabase();
        return fetchData;
    }

    public void updateChatMessageData() {


    }

    public void removeChatMessageData(ChatMessage chatMessageData) {
        messageDataBase = DatabaseManager.getInstance().openDatabase();
        messageDataBase.delete(TABLE_ChatMessage, RI + " = ?", new String[]{chatMessageData.roomId});
        DatabaseManager.getInstance().closeDatabase();
    }

    public ArrayList<ChatMessage> getAllChatMessageData(String RoomID) {

        ArrayList<ChatMessage> ChatMessageDataFromLocal = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ChatMessage + " WHERE " + CHI + " = ?";
        Log.d("TAG", "chatmessageDATA-----------QUERY----->" + selectQuery);
        messageDataBase = DatabaseManager.getInstance().openDatabase();
        String [] select = new String[]{ RoomID };
        Cursor cursor = messageDataBase.rawQuery(selectQuery, select);
//        Cursor cursor = messageDataBase.query(TABLE_ChatMessage, new String[]{CHI, SI, SN, CT, RS, ST, MT, IMG_U}, CHI + "=?", new String[]{RoomID}, null, null, null);


        if (cursor.moveToFirst()) {
            do {
                ChatMessage chatMessageDataFromSQLite = new ChatMessage();

                chatMessageDataFromSQLite.setRoomId(cursor.getString(1));
                chatMessageDataFromSQLite.setSender_Id(cursor.getString(2));
                chatMessageDataFromSQLite.setSender_name(cursor.getString(3));
                chatMessageDataFromSQLite.setTextCotents(cursor.getString(4));
                chatMessageDataFromSQLite.setRead_status(cursor.getInt(5));
                chatMessageDataFromSQLite.setSend_time(cursor.getString(6));
                chatMessageDataFromSQLite.setMsgType(cursor.getInt(7));
                chatMessageDataFromSQLite.setOpponent_img_url(cursor.getString(8));

                ChatMessageDataFromLocal.add(chatMessageDataFromSQLite);

            } while (cursor.moveToNext());
        }

        DatabaseManager.getInstance().closeDatabase();
        return ChatMessageDataFromLocal;
    }


}
