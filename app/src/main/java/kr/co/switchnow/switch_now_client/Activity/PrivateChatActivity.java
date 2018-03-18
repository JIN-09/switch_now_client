package kr.co.switchnow.switch_now_client.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.co.switchnow.switch_now_client.ADT.ChatMessage;
import kr.co.switchnow.switch_now_client.ADT.ChatRoom;
import kr.co.switchnow.switch_now_client.Adapter.ChatContentsAdapter;
import kr.co.switchnow.switch_now_client.R;
import kr.co.switchnow.switch_now_client.SQLite.ChatMessageDBManager;
import kr.co.switchnow.switch_now_client.Util.CustomDialog;
import kr.co.switchnow.switch_now_client.Util.MessageReceiver;

import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.chatMessageDBManager;
import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.chatRoomDBManager;
import static kr.co.switchnow.switch_now_client.Activity.loginActivity.userSession;
import static kr.co.switchnow.switch_now_client.Adapter.ChatContentsAdapter.messageListData;
import static kr.co.switchnow.switch_now_client.Adapter.ListViewChatAdapter.chatRoomListdata;
import static kr.co.switchnow.switch_now_client.Util.ServiceThread.CHAT_PORT_NUM;
import static kr.co.switchnow.switch_now_client.Util.ServiceThread.CHAT_SERVER_IP;
import static kr.co.switchnow.switch_now_client.Util.ServiceThread.serviceSocket;


public class PrivateChatActivity extends AppCompatActivity implements View.OnClickListener {

    static protected ListView messageListView = null;
    public static ChatContentsAdapter mChatContentsAdapater = null;

    private ArrayList<ChatMessage> objects = null;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private PrintWriter printWriterOut;

    private SharedPreferences session;

    public static boolean isChatScreen;

    TextView private_chat_title;


    String fromSessionName;
    String fromSessionStatusMessage;
    String fromSessionEmail;
    String fromSessionProfileImgLink;
    String fromSessionMobileNum;
    Boolean fromSessionFirstFlag;
    Boolean fromSessionFlag;
    Boolean fromSessionProfileEdit;

    EditText messageContentsBox;
    ImageView msgSendBtn;
    ImageView findMidSpot;
    Intent receiveChatData;


    String msgFeature;
    public static String room_id_private_chatRoom;
    public static String opponent_id;
    public static String opponent_name;
    public static String opponent_imgUrl;
    public static String room_last_message;
    String message_timestamp;
    int chat_room_readStatus;
    int chat_room_msgType;
    String LastMessageForSetRoomLastMessage;

    boolean isSocketOn;
    CustomDialog showMidSpotDialog;
    double m_lati, m_longi, o_lati, o_longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int switchDarkgrey = getResources().getColor(R.color.switchDarkgrey);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(switchDarkgrey);
            getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.switchDarkgrey));
            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getResources().getColor(R.color.switchLime), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(R.layout.private_chat_title);


        private_chat_title = (TextView) findViewById(R.id.private_title_txt);
        Typeface face = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            face = getResources().getFont(R.font.enorbit);
        }else{
            face = Typeface.createFromAsset(getAssets(), "enotbit.TTF");
        }
        private_chat_title.setTypeface(face);
        setContentView(R.layout.activity_private_chat);

        msgSendBtn = (ImageView) findViewById(R.id.send_btn_for_pChat);
        findMidSpot = (ImageView) findViewById(R.id.map_btn_for_mid_spot);
        messageContentsBox = (EditText) findViewById(R.id.message_txt_for_pChat);
        msgSendBtn.setOnClickListener(this);
        findMidSpot.setOnClickListener(this);

        session = getSharedPreferences(userSession, Context.MODE_PRIVATE);

        fromSessionProfileImgLink = "";
        fromSessionName = "";
        fromSessionStatusMessage = "";
        fromSessionProfileImgLink = "";
        fromSessionEmail = "";
        fromSessionFirstFlag = false;
        fromSessionFlag = false;
        fromSessionProfileEdit = false;
        fromSessionMobileNum = "";

        loadDataFromSession();
        chatMessageDBManager = new ChatMessageDBManager();

        messageListView = (ListView) findViewById(R.id.chat_listview);
        objects = new ArrayList<>();
        mChatContentsAdapater = new ChatContentsAdapter(this, objects);
        messageListView.setAdapter(mChatContentsAdapater);

        room_id_private_chatRoom = "";
        opponent_id = "";
        opponent_name = "";
        opponent_imgUrl = "";
        msgFeature = "";
        room_last_message = "";
        chat_room_msgType = 0;
        chat_room_readStatus = 0;
        message_timestamp = "";
        receiveChatData = getIntent();
        isSocketOn = false;


        isSocketOn = isMyServiceRunning(MessageReceiver.class);
        LastMessageForSetRoomLastMessage = "";
        fetchRoomData();
        messageListData.clear();
        fetchMessageData();
        m_lati = 0;
        m_longi = 0;
        o_lati = 0;
        o_longi = 0;
        new AsyncforHalfwayPoint().execute(fromSessionEmail, opponent_id);
        Log.d("TAG", "ROOMID___________________________________>" + room_id_private_chatRoom);
        checkSocket();
        isChatScreen = true;
    }

    public void fetchRoomData() {
        room_id_private_chatRoom = receiveChatData.getStringExtra("room_id");
        opponent_id = receiveChatData.getStringExtra("opponent_id");
        opponent_name = receiveChatData.getStringExtra("opponent_name");
        opponent_imgUrl = receiveChatData.getStringExtra("opponent_imgUrl");
        room_last_message = receiveChatData.getStringExtra("lastMessage");
    }

    public void fetchMessageData() {
        //SQLite로부터 데이터 긁어옴;

        Log.d("TAG", "Message DB SIZE----------------------------------->" + chatMessageDBManager.getAllChatMessageData(room_id_private_chatRoom).size());
        if (chatMessageDBManager.getAllChatMessageData(room_id_private_chatRoom).size() != 0) {
            ChatMessage data;
            for (int i = 0; i < chatMessageDBManager.getAllChatMessageData(room_id_private_chatRoom).size(); i++) {
                data = chatMessageDBManager.getAllChatMessageData(room_id_private_chatRoom).get(i);
                Log.d("TAG", "fetching message---------------------->" + data.TextCotents);
                messageListData.add(data);
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

        Intent intent = new Intent(this, InteractionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this will clear all the stack
        startActivity(intent);
        finish();

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.send_btn_for_pChat: {
                String messageToSend = messageContentsBox.getText().toString();
                sendMessageToServer(messageToSend);
                messageContentsBox.setText("");
                break;
            }

            case R.id.map_btn_for_mid_spot: {

                showMidSpotDialog = new CustomDialog();
                showMidSpotDialog.showMidSpotMap(this, m_lati, m_longi, o_lati, o_longi, opponent_name);

                break;
            }

        }

    }

    public static class userMessageViewHolder {

        public TextView userMessage = null;
        public TextView userTimestamp = null;
    }

    public static class opponentMessageViewHolder {

        public ImageView profile_img_opponent = null;
        public TextView opponent_name = null;
        public TextView opponent_message = null;
        public TextView opponent_timestamp = null;

    }

    public static class timeStampViewHolder {

        public TextView timestamp_txt = null;
        public View timestamp_line = null;

    }

    public boolean sendMessageToServer(String message) {

        try {
            printWriterOut = new PrintWriter(new BufferedOutputStream(serviceSocket.getOutputStream()), true);
            msgFeature = "talkIn";
            Log.d("TAG", "SENDING MESSAGE IN CHATROOM-------------------------------->" + msgFeature + "/" + fromSessionEmail + "/" + opponent_id + "/" + fromSessionName + "/" + message);
            printWriterOut.println(msgFeature + "/" + fromSessionEmail + "/" + opponent_id + "/" + fromSessionName + "/" + message);

            updateLastMessageToChatRoomlist(message);
//            printWriterOut.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG", "SENDING MESSAGE ERR IN CHATROOM------------>" + e);

            return false;
        }

    }

    public void updateLastMessageToChatRoomlist(String msg) {

        SimpleDateFormat nowData = new SimpleDateFormat("dd일 HH:mm");
        final String currentTime = nowData.format(new Date());

        ChatRoom dataForCheck;
        for (int j = chatRoomListdata.size() - 1; 0 <= j; j--) {
            dataForCheck = chatRoomListdata.get(j);

            if (dataForCheck.roomId.contentEquals(room_id_private_chatRoom)) {
                dataForCheck.setlastMessage(msg);
                dataForCheck.setUpdated_time(currentTime);
                chatRoomDBManager.removeChatRoomData(dataForCheck);
                chatRoomDBManager.addRoomData(dataForCheck);
            }
        }
    }


    public void checkSocket() {
        try {
            if (serviceSocket.getKeepAlive()) {

            } else {
                serviceSocket.close();
                serviceSocket = new Socket(CHAT_SERVER_IP, CHAT_PORT_NUM);
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void onResume() {
        super.onResume();
        isChatScreen = true;
        if (!isSocketOn) {
            startService(new Intent(this, MessageReceiver.class));
        }
        checkSocket();


    }

    public void onPause() {
        super.onPause();
        isChatScreen = false;

    }

    public void onStop() {
        super.onStop();
        isChatScreen = false;

    }

    public void onDestroy() {
        super.onDestroy();

        isChatScreen = false;
    }


    private class AsyncforHalfwayPoint extends AsyncTask<String, String, String> {
        HttpURLConnection conn = null;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://115.71.232.209/client/for_halfway_point.php");

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
                        .appendQueryParameter("_opponent_email", params[1]);

                Log.d("TAG", "email----------------------> " + fromSessionEmail);
                Log.d("TAG", "opp_email----------------------> " + opponent_id);

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
                    Log.d("TAG", "response------------------>true" + response_code);
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

            Log.d("TAG", "result_String-------------|>" + result);

            if (result.equalsIgnoreCase("available_id")) {

            } else if (result.equalsIgnoreCase("failure")) {

                Toast.makeText(getApplicationContext(), "FATAL ERR getting two positions", Toast.LENGTH_LONG).show();

            } else {

                getTwoPoints(result);
            }
        }

    }


    public void getTwoPoints(String JSON) {
        Log.d("TAG", "Position JSON VALUE------------------------------------->" + JSON);

        try {
            JSONObject mainObject = new JSONObject(JSON);
            JSONArray positionArr = mainObject.getJSONArray("json");
            Log.d("TAG", "position Arr------------------------------------>" + positionArr.toString());
            JSONObject positions = positionArr.getJSONObject(0);
            String m_lati_s = positions.getString("m_lati");
            String m_longi_s = positions.getString("m_longi");
            String o_lati_s = positions.getString("o_lati");
            String o_longi_s = positions.getString("o_longi");

            m_lati = Double.parseDouble(m_lati_s);
            m_longi = Double.parseDouble(m_longi_s);
            o_lati = Double.parseDouble(o_lati_s);
            o_longi = Double.parseDouble(o_longi_s);
            Log.d("TAG", "positions --------------------------------->" + m_lati + " , " + m_longi + " , " + o_lati + " , " + o_longi);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
