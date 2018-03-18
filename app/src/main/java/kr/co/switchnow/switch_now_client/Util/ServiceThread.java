package kr.co.switchnow.switch_now_client.Util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import kr.co.switchnow.switch_now_client.ADT.ChatRoom;
import kr.co.switchnow.switch_now_client.Activity.PrivateChatActivity;
import kr.co.switchnow.switch_now_client.R;


import static kr.co.switchnow.switch_now_client.Activity.ConversationActivity_.chattingListViewMsg;
import static kr.co.switchnow.switch_now_client.Activity.ConversationActivity_.isConvScreen;
import static kr.co.switchnow.switch_now_client.Activity.ConversationActivity_.mListViewChatAdapter;
import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.chatMessageDBManager;
import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.chatRoomDBManager;
import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.isSwitchOn;
import static kr.co.switchnow.switch_now_client.Activity.PrivateChatActivity.isChatScreen;
import static kr.co.switchnow.switch_now_client.Activity.PrivateChatActivity.mChatContentsAdapater;
import static kr.co.switchnow.switch_now_client.Activity.PrivateChatActivity.opponent_id;
import static kr.co.switchnow.switch_now_client.Activity.PrivateChatActivity.opponent_name;
import static kr.co.switchnow.switch_now_client.Activity.PrivateChatActivity.room_id_private_chatRoom;
import static kr.co.switchnow.switch_now_client.Activity.loginActivity.userSession;
import static kr.co.switchnow.switch_now_client.Adapter.ListViewChatAdapter.chatRoomListdata;

/**
 * Created by ceo on 2017-06-30.
 */

public class ServiceThread extends Thread {

    Handler networkHandler;
    boolean isRun = true;

    private static final int NOTIFICATION_ID = 1;

    public static final int CHAT_PORT_NUM = 9999;
    public static final String CHAT_SERVER_IP = "115.71.232.209";
    public static final int CONNECTION_TIMEOUT = 10000;
    public static Socket serviceSocket;

    private BufferedReader BfReadin = null;
    private NotificationManager notificationManager;
    private Notification notification;
    private SharedPreferences session;

    String fromSessionName;
    String fromSessionStatusMessage;
    String fromSessionEmail;
    String fromSessionProfileImgLink;
    String fromSessionMobileNum;
    Boolean fromSessionFirstFlag;
    Boolean fromSessionFlag;
    Boolean fromSessionProfileEdit;
    String msgFeature;
    String roomIdForChat;
    Context mContext;
    String message_timestamp;
    int chat_room_readStatus;
    int chat_room_msgType;
    String LastMessageForSetRoomLastMessage;
    boolean enterRoomFirst;
    PendingIntent chatStartIntent;
    int counter;
    Runnable mRunnable;


    public ServiceThread(Handler handler) {
        counter = 0;
        this.networkHandler = handler;
        fromSessionProfileImgLink = "";
        fromSessionName = "";
        fromSessionStatusMessage = "";
        fromSessionProfileImgLink = "";
        fromSessionEmail = "";
        fromSessionFirstFlag = false;
        fromSessionFlag = false;
        fromSessionProfileEdit = false;
        fromSessionMobileNum = "";
        session = mContext.getApplicationContext().getSharedPreferences(userSession, Context.MODE_PRIVATE);
        loadDataFromSession();
        msgFeature = "";
        roomIdForChat = "";
        message_timestamp = "";
        chat_room_readStatus = 0;
        chat_room_msgType = 0;
        LastMessageForSetRoomLastMessage = "";
        enterRoomFirst = true;

    }

    public void stopForever() {
        synchronized (this) {
            isSwitchOn = false;
            try {
                serviceSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void run() {

        try {
            if (BfReadin != null) {
                BfReadin.close();
            }
        } catch (Exception e) {
            Log.e("TAG", "Socket 접속상태------------->>>>" + e.getMessage());
            if (serviceSocket != null) {
                try {
                    serviceSocket.close();
                } catch (IOException socketErr) {
                    socketErr.printStackTrace();
                }

            }
        }


        while (isSwitchOn) {

            try {
                serviceSocket = null;
                serviceSocket = new Socket(CHAT_SERVER_IP, CHAT_PORT_NUM);
                Log.d("TAG_SERVICE", "socket--current--state----------is Closed?------------->" + serviceSocket.isClosed());


                BfReadin = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream(), "UTF-8"));
                Log.d("TAG_SERVICE", "SWITCH FLAG ON OF STATE------------------------------------------>" + isSwitchOn);
                String protocol = "";

                protocol = BfReadin.readLine();


                @SuppressLint("SimpleDateFormat") SimpleDateFormat nowData = new SimpleDateFormat("HH:mm");
                final String currentTime = nowData.format(new Date());
                final URL[] imageURL = new URL[1];


                Log.d("SERVICE_TAG", "CONTENTS OF MESSAGE-----------------------------------------------> " + protocol);
                StringTokenizer stz = new StringTokenizer(protocol, "/");
                final String msgFeat = stz.nextToken();
                final String userId = stz.nextToken();
                final String chatwith = stz.nextToken();
                final String userName = stz.nextToken();
                final String msgfrom = stz.nextToken();


                try {
                    imageURL[0] = new URL("http://115.71.232.209/client/profile/" + userId + ".png");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Intent notificationIntent = new Intent(mContext.getApplicationContext(), PrivateChatActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .putExtra("room_id", chatwith + userId)
                        .putExtra("opponent_id", userId)
                        .putExtra("opponent_name", userName)
                        .putExtra("opponent_imgUrl", imageURL)
                        .putExtra("lastMessage", msgfrom);
                chatStartIntent = PendingIntent.getActivity(mContext.getApplicationContext(), NOTIFICATION_ID, notificationIntent
                        , PendingIntent.FLAG_UPDATE_CURRENT);

                notificationManager = (NotificationManager) mContext.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                notification = new Notification.Builder(mContext.getApplicationContext()).setContentTitle(userName + "님으로 부터의 메시지")
                        .setContentText(msgfrom)
                        .setSmallIcon(R.drawable.switch_logo)
                        .setDefaults(Notification.DEFAULT_ALL | 0)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setVibrate(new long[0])
                        .setContentIntent(chatStartIntent)
                        .build();


                final String finalProtocol = protocol;


                networkHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("TAG_SERVICE", "Receive Msg at service-------in... Handler---------->" + finalProtocol);
                        if (isConvScreen) {
                            if (chatwith.contentEquals(fromSessionEmail)) {
                                msgFeature = "";
                                if (roomCheck(chatwith, userId).contentEquals("roomCt")) {

                                    msgFeature = "roomCt";
                                    roomIdForChat = chatwith + userId;
                                    int roomStatus = 1;

                                    String oppnent_image_url = String.valueOf(imageURL[0]);
                                    mListViewChatAdapter.insertItem(roomIdForChat, chatwith, userId, roomStatus, currentTime, msgfrom, userName);
                                    chatRoomDBManager.insertRoomData(roomIdForChat, chatwith, userId, roomStatus, currentTime, msgfrom, userName, oppnent_image_url);
                                    chatMessageDBManager.insertMessageData(roomIdForChat, userId, userName, msgfrom, 1, currentTime, 1, oppnent_image_url);
                                    Log.d("TAG_SERVICE", "check Listview size ----------------------->" + chatRoomListdata.size());
                                    if (chatRoomListdata.size() > 0) {
                                        chattingListViewMsg.setVisibility(View.GONE);
                                    }
                                    mListViewChatAdapter.dataChange();


                                } else if (roomCheck(chatwith, userId).contentEquals("talkIn")) {
                                    msgFeature = "talkIn";
                                    String previousMsg = "";
                                    String previousTimestamp = "";
                                    ChatRoom dataForCheck;

                                    if (chatRoomListdata.size() == 1) {
                                        dataForCheck = chatRoomListdata.get(0);
                                        if (dataForCheck.roomId.contentEquals(roomIdForChat)) {
                                            dataForCheck.setlastMessage(msgfrom);
                                            dataForCheck.setUpdated_time(currentTime);
                                            chatRoomDBManager.removeChatRoomData(dataForCheck);
                                            chatRoomDBManager.addRoomData(dataForCheck);
                                            chatMessageDBManager.insertMessageData(dataForCheck.roomId, dataForCheck.user_id, dataForCheck.userName, msgfrom, 1, currentTime, 1, dataForCheck.user_img_url);
                                        }
                                        mListViewChatAdapter.notifyDataSetChanged();

                                    } else {
                                        for (int j = chatRoomListdata.size() - 1; 0 <= j; j--) {
                                            dataForCheck = chatRoomListdata.get(j);

                                            if (dataForCheck.roomId.contentEquals(roomIdForChat)) {
                                                dataForCheck.setlastMessage(msgfrom);
                                                dataForCheck.setUpdated_time(currentTime);

                                                mListViewChatAdapter.remove(j);
                                                chatRoomListdata.add(0, dataForCheck);
                                                chatRoomDBManager.removeChatRoomData(dataForCheck);
                                                chatRoomDBManager.addRoomData(dataForCheck);
                                                chatMessageDBManager.insertMessageData(dataForCheck.roomId, dataForCheck.user_id, dataForCheck.userName, msgfrom, 1, currentTime, 1, dataForCheck.user_img_url);
                                                break;
                                            }


                                        }
                                        mListViewChatAdapter.notifyDataSetChanged();
                                    }
                                    Log.d("TAG_SERVICE", "latest msg-------------------->" + msgfrom);
                                    Log.d("TAG_SERVICE", "Msg to MessageArrListFromServer---------->" + previousMsg);
                                }

                            }

                        } else if (isChatScreen) {

                            chat_room_msgType = 0;
                            SimpleDateFormat nowTime = new SimpleDateFormat("HH:mm");

                            Log.d("TAG_SERVICE", "opponentID---------------------------->" + opponent_id);
                            message_timestamp = nowTime.format(new Date());
                            if (userId.contentEquals(fromSessionEmail)) {
                                // addMessage(String roomId, String sender_id, String sender_name, String msg, int readStatus, String sendTime, int msgType) {
                                chat_room_msgType = 0;
                                room_id_private_chatRoom = fromSessionEmail + opponent_id;
                                mChatContentsAdapater.addMessage(room_id_private_chatRoom, fromSessionEmail, fromSessionName, msgfrom, chat_room_readStatus, message_timestamp, chat_room_msgType);
                                updateLastMessageToChatRoomlist(msgfrom);

                            } else if (!userId.contentEquals(fromSessionEmail) && chatwith.contentEquals(fromSessionEmail) && room_id_private_chatRoom.contentEquals(fromSessionEmail + userId)) {
                                chat_room_msgType = 1;
                                if (enterRoomFirst) {

                                    mChatContentsAdapater.addMessage(room_id_private_chatRoom, opponent_id, opponent_name, msgfrom, chat_room_readStatus, message_timestamp, chat_room_msgType);
                                    enterRoomFirst = false;
                                    updateLastMessageToChatRoomlist(msgfrom);

                                } else {
                                    room_id_private_chatRoom = fromSessionEmail + userId;
                                    mChatContentsAdapater.addMessage(room_id_private_chatRoom, userId, userName, msgfrom, chat_room_readStatus, message_timestamp, chat_room_msgType);
                                    updateLastMessageToChatRoomlist(msgfrom);
                                }

                            } else {
                                Log.d("TAG_SERVICE", "ERR MESSAGE IN CHATROOM--------------------->" + finalProtocol);
                            }

                        } else {
                            // 그 외에서 메시지를 받았을 경우...
                            if (!userId.contentEquals(fromSessionEmail) && chatwith.contentEquals(fromSessionEmail)) {
                                notificationManager.notify(NOTIFICATION_ID, notification);
                                chat_room_msgType = 1;
                                msgFeature = "roomCt";
                                roomIdForChat = chatwith + userId;
                                int roomStatus = 1;
                                try {
                                    imageURL[0] = new URL("http://115.71.232.209/client/profile/" + userId + ".png");
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                String oppnent_image_url = String.valueOf(imageURL[0]);

                                if (roomCheck(chatwith, userId).contentEquals("roomCt")) {

                                    chatRoomDBManager.insertRoomData(roomIdForChat, chatwith, userId, roomStatus, currentTime, msgfrom, userName, oppnent_image_url);
                                    chatMessageDBManager.insertMessageData(roomIdForChat, userId, userName, msgfrom, 1, currentTime, 1, oppnent_image_url);
                                    Log.d("TAG_SERVICE", "check Listview size ----------------------->" + chatRoomListdata.size());
                                    if (chatRoomListdata.size() > 0) {
                                        chattingListViewMsg.setVisibility(View.GONE);
                                    }


                                } else if (enterRoomFirst) {
                                    ChatRoom forCheck;
                                    forCheck = new ChatRoom();
                                    forCheck.setRoomId(roomIdForChat);
                                    chatRoomDBManager.removeChatRoomData(forCheck);
                                    Log.d("TAG_SERVICE", "ADD DATA to where ROOM ID is--------------------->" + roomIdForChat);
                                    chatRoomDBManager.insertRoomData(roomIdForChat, chatwith, userId, roomStatus, currentTime, msgfrom, userName, oppnent_image_url);
                                    chatMessageDBManager.insertMessageData(roomIdForChat, opponent_id, userName, msgfrom, chat_room_readStatus, currentTime, chat_room_msgType, oppnent_image_url);
                                    enterRoomFirst = false;

                                } else {
                                    ChatRoom forCheck;
                                    forCheck = new ChatRoom();
                                    forCheck.setRoomId(roomIdForChat);
                                    Log.d("TAG_SERVICE", "for delete roomData-------->" + roomIdForChat);
                                    chatRoomDBManager.removeChatRoomData(forCheck);
                                    chatRoomDBManager.insertRoomData(roomIdForChat, chatwith, userId, roomStatus, currentTime, msgfrom, userName, oppnent_image_url);
                                    room_id_private_chatRoom = fromSessionEmail + userId;
                                    chatMessageDBManager.insertMessageData(roomIdForChat, opponent_id, userName, msgfrom, chat_room_readStatus, currentTime, chat_room_msgType, oppnent_image_url);

                                }
                            }


                        }
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    public void loadDataFromSession() {

        fromSessionProfileImgLink = session.getString("profile_img_link", "");
        fromSessionStatusMessage = session.getString("userStatusMessage", "");
        fromSessionName = session.getString("userName", "");
        fromSessionEmail = session.getString("user_Fb_id", "");
        fromSessionMobileNum = session.getString("userMobile", "");
        fromSessionFirstFlag = session.getBoolean("isFirstLogin", false);
        fromSessionFlag = session.getBoolean("Session", false);
        fromSessionProfileEdit = session.getBoolean("profile_edit_flag", false);

    }

    public String roomCheck(String user_id, String chat_with) {

        msgFeature = ""; //1. 방개설일 경우 - roomCt  2. 방개설이 되어 있을 경우 - talkIn;
        roomIdForChat = "";
        boolean find = false;
        ChatRoom tmpData;
        Log.d("TAG_SERVICE", "ChatROOMLIST_DATA SIZE-----------------------" + chatRoomDBManager.getAllChatRoomData().size());
        Log.d("TAG_SERVICE", "ROOM CHECK ----------------------------------> userID: " + user_id + "// chat_with: " + chat_with);
        if (chatRoomDBManager.getAllChatRoomData().size() == 0) {

            msgFeature = "roomCt";
            roomIdForChat = user_id + chat_with;

        } else if (chatRoomDBManager.getAllChatRoomData().size() > 0) {

            for (int i = chatRoomDBManager.getAllChatRoomData().size() - 1; 0 <= i; i--) {

                tmpData = chatRoomDBManager.getAllChatRoomData().get(i);
                Log.d("TAG_SERVICE", "Check from Existed CHATLIST ROOM_ID--------------------->" + tmpData.roomId);

                if (tmpData.roomId.contentEquals(user_id + chat_with)) {
                    msgFeature = "talkIn";
                    roomIdForChat = user_id + chat_with;
                    find = true;
                    break;
                } else if (tmpData.roomId.contentEquals(chat_with + user_id)) {
                    msgFeature = "talkIn";
                    roomIdForChat = chat_with + user_id;
                    find = true;
                    break;
                }
            }
        }

        if (msgFeature.contentEquals("")) {
            msgFeature = "roomCt";
            roomIdForChat = user_id + chat_with;
        }

        Log.d("TAG_SERVICE", "Retrun MESSAGE FEAUTRE----------------------------->" + msgFeature);
        return msgFeature;
    }

    public void updateLastMessageToChatRoomlist(String msg) {

        SimpleDateFormat nowData = new SimpleDateFormat("dd일 HH:mm");
        final String currentTime = nowData.format(new Date());

        ChatRoom dataForCheck;
        for (int j = chatRoomDBManager.getAllChatRoomData().size() - 1; 0 <= j; j--) {
            dataForCheck = chatRoomDBManager.getAllChatRoomData().get(j);

            if (dataForCheck.roomId.contentEquals(room_id_private_chatRoom)) {
                dataForCheck.setlastMessage(msg);
                dataForCheck.setUpdated_time(currentTime);
                chatRoomDBManager.removeChatRoomData(dataForCheck);
                chatRoomDBManager.addRoomData(dataForCheck);
            }
        }
    }


}
