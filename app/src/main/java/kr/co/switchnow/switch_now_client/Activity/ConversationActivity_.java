package kr.co.switchnow.switch_now_client.Activity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.switchnow.switch_now_client.ADT.ChatMessage;
import kr.co.switchnow.switch_now_client.ADT.ChatRoom;
import kr.co.switchnow.switch_now_client.ADT.ListData;
import kr.co.switchnow.switch_now_client.Adapter.CustomDrawerAdapter;
import kr.co.switchnow.switch_now_client.Adapter.ListViewChatAdapter;
import kr.co.switchnow.switch_now_client.R;
import kr.co.switchnow.switch_now_client.Util.ActivitySwipeDetector;
import kr.co.switchnow.switch_now_client.Util.BroadcastService;
import kr.co.switchnow.switch_now_client.Util.CustomDialog;
import kr.co.switchnow.switch_now_client.Util.LocationInfo;
import kr.co.switchnow.switch_now_client.Util.MessageReceiver;
import kr.co.switchnow.switch_now_client.Util.SwipeInterface;

import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.CounterforNewFriends;
import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.chatMessageDBManager;
import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.chatRoomDBManager;
import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.dListView;
import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.isSwitchOn;
import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.mCustomDrawerAdapter;
import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.tmpChatRoomList;
import static kr.co.switchnow.switch_now_client.Activity.ReadyActivity.SwitchTime;
import static kr.co.switchnow.switch_now_client.Activity.ReadyActivity.sendToServerLatitude;
import static kr.co.switchnow.switch_now_client.Activity.ReadyActivity.sendToServerLongitude;
import static kr.co.switchnow.switch_now_client.Activity.ReadyActivity.userSetting;
import static kr.co.switchnow.switch_now_client.Activity.loginActivity.userSession;
import static kr.co.switchnow.switch_now_client.Adapter.ListViewChatAdapter.chatRoomListdata;


public class ConversationActivity_ extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, SwipeInterface {
    static protected ListView cListView = null;
    public static ListViewChatAdapter mListViewChatAdapter = null;

    static ArrayList<String> dFriendList;
    static ArrayList<String> dBlockList;

    private ArrayList<ListData> drawerObjects = null;
    private ArrayList<ChatRoom> objects = null;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    public static final int CHAT_PORT_NUM = 9999;
    public static final String CHAT_SERVER_IP = "115.71.232.209";


    public static boolean isConvScreen;


    private DrawerLayout mParentLayout;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences.Editor tEditor;
    private SharedPreferences session;
    private SharedPreferences setting;
    private SharedPreferences time_value;
    TextView conversation_title_text;
    BaseActivity baseActivity;
    CircleImageView profile_img;
    ImageView miniCamera;
    TextView profile_name_text_nav;
    TextView profile_status_text_nav;
    String fromSessionName;
    String fromSessionStatusMessage;
    String fromSessionEmail;
    String fromSessionProfileImgLink;
    String fromSessionMobileNum;
    Boolean fromSessionFirstFlag;
    Boolean fromSessionFlag;
    Boolean fromSessionProfileEdit;

    TextView revCounter_conv;
    TextView time_start_conv;
    protected ProgressBar progressBar_conv;
    protected int progressStatus_conv;
    String switch_value;
    int switch_time;
    int switch_time_mill;

    protected Uri mCurrentPhotoPath;
    protected LocationInfo locationInfo;


    Boolean isOnceOpend;
    String Json;
    ArrayList<String> contactlist;

    NavigationView navigationView;
    static TextView navigation_msg_drawer;
    TextView manageFriendText;
    CountDownTimer countDownTimer;
    RelativeLayout NavHeader = null;

    String newFriendListToServer;
    String newBlockListToServer;

    public static TextView chattingListViewMsg;
    String msgFeature;
    String roomIdForChat;

    Dialog deleteRoomDialog;
    TextView deleteRoomDialogTxt;
    Button deleteRoomDialogYes;
    Button deleteRoomDialogNo;
    String DeleteRoomID;
    ChatRoom deleteRoom;
    ChatMessage deleteMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_conversation_);
        baseActivity = new BaseActivity(this);
        int switchLime = getResources().getColor(R.color.switchLime);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.conversation_title);
        conversation_title_text = (TextView) findViewById(R.id.conversation_title_txt);
        Typeface face = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            face = getResources().getFont(R.font.enorbit);
        }else{
            face = Typeface.createFromAsset(getAssets(), "enotbit.TTF");
        }
        conversation_title_text.setTypeface(face);


        isOnceOpend = false;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_conv);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (isOnceOpend == true) {
                    drawerObjects.clear();
                    newFriendListToServer = new Gson().toJson(dFriendList);
                    newBlockListToServer = new Gson().toJson(dBlockList);
                    Log.d("TAG", "Counter Size------------------------> " + CounterforNewFriends);
                    if (CounterforNewFriends > 0) {
                        new AsyncSendTwoLists().execute(newFriendListToServer, newBlockListToServer, fromSessionEmail);
                    } else {
                        return;
                    }
                } else {

                    return;

                }
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                profile_status_text_nav.setText(fromSessionStatusMessage);
                profile_name_text_nav.setText(fromSessionName);
                navigation_msg_drawer.setVisibility(View.INVISIBLE);
                dBlockList = new ArrayList<>();
                dFriendList = new ArrayList<>();
                retrieveContactNumber();
                Log.d("TAG", "ContatctListData--------------------->" + contactlist.toString());
                Json = new Gson().toJson(contactlist);
                Log.d("TAG", "JSON LIST---------------------------->" + Json);
                CounterforNewFriends = 0;
                new AsyncGetNewFriendList().execute(fromSessionEmail, Json, fromSessionMobileNum);
                Log.d("TAG", "Counter Size------------------------> " + CounterforNewFriends);
                isOnceOpend = true;

            }


        };
        toggle.getDrawerArrowDrawable().setColorFilter(getResources().getColor(R.color.switchLime), PorterDuff.Mode.SRC_ATOP);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        session = getSharedPreferences(userSession, Context.MODE_PRIVATE);
        time_value = getSharedPreferences(SwitchTime, Context.MODE_PRIVATE);
        setting = getSharedPreferences(userSetting, Context.MODE_PRIVATE);

        mEditor = session.edit();
        tEditor = time_value.edit();

        navigationView = (NavigationView) findViewById(R.id.nav_view_conv);
        navigationView.setNavigationItemSelectedListener(this);


        profile_img = (CircleImageView) navigationView.findViewById(R.id.profile_image_nav);
        profile_name_text_nav = (TextView) navigationView.findViewById(R.id.profile_name_text_nav);
        profile_status_text_nav = (TextView) navigationView.findViewById(R.id.profile_status_text_nav);
        navigation_msg_drawer = (TextView) navigationView.findViewById(R.id.navigation_msg_drawer);

        navigationView.setNavigationItemSelectedListener(this);

        manageFriendText = (TextView) navigationView.findViewById(R.id.drawer_manage_friend_txt);
        manageFriendText.setOnClickListener(this);

        dListView = (ListView) navigationView.findViewById(R.id.add_friend_listview_drawer);
        drawerObjects = new ArrayList<>();
        mCustomDrawerAdapter = new CustomDrawerAdapter(this, drawerObjects);
        dListView.setAdapter(mCustomDrawerAdapter);


        fromSessionProfileImgLink = "";
        fromSessionName = "";
        fromSessionStatusMessage = "";
        fromSessionProfileImgLink = "";
        fromSessionEmail = "";
        fromSessionFirstFlag = false;
        fromSessionFlag = false;
        fromSessionProfileEdit = false;
        fromSessionMobileNum = "";


        switch_value = "";
        switch_time = 0;
        switch_time_mill = 0;
        isSwitchOn = false;

        msgFeature = "";
        roomIdForChat = "";

        //
        loadTime();
        time_start_conv = (TextView) findViewById(R.id.time_start_cov);
        time_start_conv.setText(switch_value + "min.");
        time_start_conv.setTextColor(switchLime);
        countDownTimer(switch_time_mill);


        loadDataFromSession();
        getLocationInfo();

        Log.d("TAG", "user_name---->" + fromSessionName);
        Log.d("TAG", "user_img_link---->" + fromSessionProfileImgLink);
        Log.d("TAG", "LATITUDE----------------------> " + sendToServerLatitude);
        Log.d("TAG", "LONGITUDE---------------------> " + sendToServerLongitude);

        dListView = null;
//        mCustomDrawerAdapter = null;

        revCounter_conv = (TextView) findViewById(R.id.revCounter_conv);
        revCounter_conv.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
        progressBar_conv = (ProgressBar) findViewById(R.id.progress_bar_conv);
        progressBar_conv.setRotation(180);
        progressStatus_conv = 0;

        chattingListViewMsg = (TextView) findViewById(R.id.conversation_Listview_msg);

        mParentLayout = (DrawerLayout) findViewById(R.id.drawer_layout_conv);
        cListView = (ListView) findViewById(R.id.conversation_switchOn_listview);
        objects = new ArrayList<>();

        cListView.setOnItemClickListener(new ListViewItemClickListener());
        cListView.setOnItemLongClickListener(new ListViewItemLongClickListener());
        mListViewChatAdapter = new ListViewChatAdapter(this, objects);
        cListView.setAdapter(mListViewChatAdapter);
        chatRoomListdata.clear();
        fetchChatRoomData();

        ActivitySwipeDetector swipe = new ActivitySwipeDetector(this, this);
        mParentLayout.setOnTouchListener(swipe);
        cListView.setOnTouchListener(swipe);

        profile_name_text_nav.setText(fromSessionName);
        profile_status_text_nav.setText(fromSessionStatusMessage);
        NavHeader = (RelativeLayout) navigationView.findViewById(R.id.nav_header_background);
        Glide.with(getApplicationContext()).load(fromSessionProfileImgLink).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).bitmapTransform(new BlurTransformation(getApplicationContext())).into(new ViewTarget<RelativeLayout, GlideDrawable>(NavHeader) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                RelativeLayout view = this.view;
                view.setBackground(resource);
            }
        });


        if (fromSessionProfileImgLink != null && fromSessionProfileEdit == false) {

            Log.d("페북 이미지 URL", fromSessionProfileImgLink);
            Picasso.with(getApplicationContext()).load(fromSessionProfileImgLink).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(profile_img);

        } else if (fromSessionProfileImgLink != null && fromSessionProfileEdit == true) {

            Picasso.with(getApplicationContext()).load(fromSessionProfileImgLink).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(profile_img);
        }

        profile_img.setOnClickListener(this);
        startService(new Intent(this, BroadcastService.class));

        boolean check = isMyServiceRunning(MessageReceiver.class);
        Log.d("TAG", "Service------------------Running Check------------------> : " + check);
        if (!check) {
            startService(new Intent(this, MessageReceiver.class));
        }
//        addChatList();
        if (chatRoomListdata.size() == 0) {

            showConversationListViewMsg();

        } else {

        }

        isConvScreen = true;
        mEditor.clear();
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


    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_conv);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            CustomDialog alert = new CustomDialog();
            alert.exitDialog(this, "Switch를 종료하시겠습니까?");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.interaction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public void reload() {
        loadDataFromSession();
        isConvScreen = true;

        profile_name_text_nav.setText(fromSessionName);

        if (fromSessionProfileImgLink != null && fromSessionProfileEdit == false) {

            Log.d("페북 이미지 URL", fromSessionProfileImgLink);
            Picasso.with(getApplicationContext()).load(fromSessionProfileImgLink).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(profile_img);

        } else if (fromSessionProfileImgLink != null && fromSessionProfileEdit == true) {


            Picasso.with(getApplicationContext()).load(fromSessionProfileImgLink).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(profile_img);
        }
        NavHeader = (RelativeLayout) navigationView.findViewById(R.id.nav_header_background);
        Glide.with(getApplicationContext()).load(fromSessionProfileImgLink).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).bitmapTransform(new BlurTransformation(getApplicationContext())).into(new ViewTarget<RelativeLayout, GlideDrawable>(NavHeader) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                RelativeLayout view = this.view;
                view.setBackground(resource);
            }
        });

    }


    public void onResume() {
        super.onResume();
        reload();

//        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));

    }

    @Override
    public void onPause() {
        super.onPause();
        isConvScreen = false;
//        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
//        unregisterReceiver(br);
        Log.i("TAG", "Unregistered broacast receiver");
    }

    @Override
    public void onStop() {
        try {
//            unregisterReceiver(br);
//            registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));

        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
//        stopService(new Intent(this, BroadcastService.class));
//        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
        isConvScreen = false;
        super.onDestroy();
    }


    public void loadTime() {
        isSwitchOn = time_value.getBoolean("isSwitchOn", false);
        switch_time = time_value.getInt("switch_time", 0);
        switch_time_mill = time_value.getInt("switch_time_mill", 0);
        switch_value = time_value.getString("switch_value", "");

    }

    public void saveTime() {
        tEditor.clear();
        tEditor.putInt("switch_time", switch_time);
        tEditor.putString("switch_value", switch_value);
        tEditor.putInt("switch_time_mill", switch_time_mill);
        tEditor.putBoolean("isSwitchOn", isSwitchOn);
        tEditor.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.profile_image_nav: {

                startActivity(new Intent(this, r_profileActivity.class));
                break;

            }

            case R.id.drawer_manage_friend_txt: {
                Intent intent = new Intent(ConversationActivity_.this, ManageFriendsActivity.class);
                startActivity(intent);

                break;
            }

            case R.id.btn_dialog_delete_yes: {

                chatRoomDBManager.removeChatRoomData(deleteRoom);
                chatMessageDBManager.removeChatMessageData(deleteMessage);
                Toast.makeText(getApplicationContext(), "대화내역이 모두 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                break;
            }

            case R.id.btn_dialog_delete_no: {


                break;
            }


        }
    }


    @Override
    public void onLeftToRight(View v) {
        baseActivity.finish();
        Intent intent = new Intent(this, InteractionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        saveTime();

        if (!sendToServerLatitude.contentEquals("") && !sendToServerLongitude.contentEquals("")) {
            new AsyncIsOnline().execute(fromSessionEmail, sendToServerLongitude, sendToServerLatitude);
        }

        isConvScreen = false;
        startActivity(intent);
        countDownTimer.cancel();

        baseActivity.slideInFromLeft(this, this.findViewById(android.R.id.content));
        Toast.makeText(this, "FRIENDS LIST", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRightToLeft(View v) {


    }

    private void countDownTimer(int time) {

        countDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long millis = millisUntilFinished;

                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                switch_time_mill = (int) millis;
                int totalTime = switch_time * 60 * 1000;
                progressStatus_conv = (int) (((totalTime) - millisUntilFinished) / (double) totalTime * 100);

                progressBar_conv.setProgress(progressStatus_conv);
                revCounter_conv.setText(hms);
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
                isSwitchOn = false;
                saveTime();
                revCounter_conv.setText("00:00:00");
            }
        }.start();

    }


    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            isSwitchOn = intent.getBooleanExtra("isSwitchOn", false);
            Log.i("TAG", "Countdown seconds remaining: " + millisUntilFinished / 1000);
            switch_time_mill = (int) millisUntilFinished;
            saveTime();
            countDownTimer(switch_time_mill);
        }
    }

    public void getLocationInfo() {

        locationInfo = new LocationInfo(ConversationActivity_.this);

        if (locationInfo.canGetLocation()) {
            sendToServerLatitude = String.valueOf(locationInfo.getLatitude());
            sendToServerLongitude = String.valueOf(locationInfo.getLongitude());
            if(locationInfo.progress.isShowing()){
                locationInfo.progress.dismiss();
            }
        }
    }

    public void showMsg() {

        navigation_msg_drawer.setVisibility(View.VISIBLE);
        navigation_msg_drawer.bringToFront();

    }

    public void showConversationListViewMsg() {

        chattingListViewMsg.setVisibility(View.VISIBLE);
        chattingListViewMsg.bringToFront();
    }


    public void fetchChatRoomData() {


        Log.d("TAG", "FETCH_CHATROOM DATA------------SIZE---->" + chatRoomDBManager.getAllChatRoomData().size());
        if (chatRoomDBManager.getAllChatRoomData().size() != 0) {
            ChatRoom data;
            Log.d("TAG", "------------------------ADD DATA FROM SQLITE TO CHATROOM LISTVIEW----");
            for (int i = 0; i < chatRoomDBManager.getAllChatRoomData().size(); i++) {
                data = chatRoomDBManager.getAllChatRoomData().get(i);
                chatRoomListdata.add(data);
            }

        }
    }


    public void addChatList() {

        ChatRoom tmpData;
        for (int i = tmpChatRoomList.size() - 1; 0 <= i; i--) {
            roomIdForChat = "";
            tmpData = tmpChatRoomList.get(i);

            Log.d("TAG", "tmpListData, Added to------------------------------->" + tmpData.chat_with);
            Log.d("TAG", "tmpListData, From ---------------------------------->" + tmpData.user_id);

            if (roomCheck(tmpData.user_id, tmpData.chat_with).contentEquals("roomCt")) {
                mListViewChatAdapter.insertItem(tmpData.roomId, tmpData.user_id, tmpData.chat_with, tmpData.room_status, tmpData.updated_time, tmpData.last_message, tmpData.userName);
                chatRoomDBManager.addRoomData(tmpData);
                chatMessageDBManager.insertMessageData(tmpData.roomId, tmpData.user_id, tmpData.userName, tmpData.last_message, tmpData.room_status, tmpData.updated_time, 0, tmpData.user_img_url);

            } else if (roomCheck(tmpData.user_id, tmpData.chat_with).contentEquals("talkIn")) {
                // 메시지 ListArr에 내용물 저장 및 전송
                String previousMsg = "";
                String previousTimestamp = "";
                ChatRoom dataForCheck;
                for (int j = chatRoomListdata.size() - 1; 0 <= j; j--) {
                    dataForCheck = chatRoomListdata.get(j);

                    if (dataForCheck.roomId.contentEquals(roomIdForChat)) {
                        previousMsg = tmpData.last_message;
                        previousTimestamp = tmpData.updated_time;
                        dataForCheck.setlastMessage(tmpData.last_message);
                        dataForCheck.setUpdated_time(tmpData.updated_time);
                        mListViewChatAdapter.remove(j);
                        chatRoomListdata.add(0, dataForCheck);
                        chatRoomDBManager.removeChatRoomData(dataForCheck);
                        chatRoomDBManager.addRoomData(dataForCheck);
                        int readStatus = 1;
                        chatMessageDBManager.insertMessageData(tmpData.roomId, tmpData.user_id, tmpData.userName, previousMsg, readStatus, previousTimestamp, 0, tmpData.user_img_url);
                        break;
                    }

                }

                mListViewChatAdapter.notifyDataSetChanged();
                Log.d("TAG", "Msg to MessageArrList---------->" + previousMsg);

            } else {
                Log.d("TAG", "MsgFeatErr--------------------------------------->" + msgFeature);
            }
        }


        tmpChatRoomList.clear();
    }

    public String roomCheck(String user_id, String chat_with) {

        msgFeature = ""; //1. 방개설일 경우 - roomCt  2. 방개설이 되어 있을 경우 - talkIn;
        roomIdForChat = "";
        boolean find = false;
        ChatRoom tmpData;
        Log.d("TAG", "ChatROOMLIST_DATA SIZE-----------------------" + chatRoomListdata.size());
        Log.d("TAG", "ROOM CHECK ----------------------------------> userID: " + user_id + "// chat_with: " + chat_with);
        if (chatRoomListdata.size() == 0) {

            msgFeature = "roomCt";
            roomIdForChat = user_id + chat_with;

        } else if (chatRoomListdata.size() > 0) {

            for (int i = chatRoomListdata.size() - 1; 0 <= i; i--) {

                tmpData = chatRoomListdata.get(i);
//                Log.d("TAG", "Check from Existed CHATLIST ROOM_ID--------------------->" + tmpData.roomId);

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

        Log.d("TAG", "Retrun MESSAGE FEAUTRE----------------------------->" + msgFeature);
        return msgFeature;
    }


    public static class ChatRoomViewHolder {

        public ImageView m_Profile = null;
        public TextView m_NameText = null;
        public TextView m_lastMeg = null;
        public TextView m_TimeStamp = null;
        public ImageView m_RoomStatus = null;
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


    public ArrayList<String> retrieveContactNumber() {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID, // 연락처 ID -> 사진 정보 가져오는데 사용
                ContactsContract.CommonDataKinds.Phone.NUMBER,        // 연락처
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

        String[] selectionArgs = null;

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";


        Cursor contactCursor = getApplicationContext().getContentResolver().query(uri, projection, null, selectionArgs, sortOrder);

        contactlist = new ArrayList<>();

        if (contactCursor.moveToFirst()) {

            do {

                String phonenumber = contactCursor.getString(1).replace("+82", "0").replace("-", "").replace(" ", "").replace("/", "");

                contactlist.add(phonenumber);

                Log.d("TAG", "phoneNumber---------------------------------->" + phonenumber);

            } while (contactCursor.moveToNext());
        }

        return contactlist;
    }


    private class AsyncGetNewFriendList extends AsyncTask<String, String, String> {
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
                url = new URL("http://115.71.232.209/client/get_new_friendList.php");

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
                        .appendQueryParameter("_json", params[1])
                        .appendQueryParameter("_mobile", params[2]);

                Log.d("TAG", "email----------------------> " + fromSessionEmail);
                Log.d("TAG", "numbers--------------------> " + Json);
                Log.d("TAG", "my number------------------> " + fromSessionMobileNum);
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

            Log.d("TAG", "result_String----------->" + result);

            if (result.equalsIgnoreCase("available_id")) {

            } else if (result.equalsIgnoreCase("")) {

                showMsg();

            } else {

                showNewFriendList(result);

            }
        }

    }


    public void showNewFriendList(String jsonValue) {

        try {
            JSONObject newFriendObj = new JSONObject(jsonValue);
            JSONArray newFriendArr = newFriendObj.getJSONArray("new_friend");

            for (int i = 0; i < newFriendArr.length(); i++) {
                JSONObject getItem = newFriendArr.getJSONObject(i);
                String email = getItem.getString("id");
                String gender = getItem.getString("gender");
                String userName = getItem.getString("name");
                String mNumber = getItem.getString("mobile");
                String status = getItem.getString("msg");

                mCustomDrawerAdapter.addItem(email, gender, userName, mNumber, status);
                Log.d("TAG", "addITEM---------------------->" + email);

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("TAG", "JSON Errr-------------->" + e);
        }

    }


    private class AsyncSendTwoLists extends AsyncTask<String, String, String> {
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
                url = new URL("http://115.71.232.209/client/update_relations.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                Log.d("TAG", "URL------------------------>error" + url);
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

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("_friend_list", params[0])
                        .appendQueryParameter("_block_list", params[1])
                        .appendQueryParameter("_email", params[2]);


                Log.d("TAG", "Friend List ----------------------> " + newFriendListToServer);
                Log.d("TAG", "Block List  ----------------------> " + newBlockListToServer);
                Log.d("TAG", "My email  ------------------------> " + fromSessionEmail);

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
                Log.d("TAG", "connection------------------------->error" + url);
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
                    Log.d("TAG", "response--------------------> true " + response_code);
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

            Log.d("TAG", "result_String------------------------> " + result);

            if (result.equalsIgnoreCase("failure")) {
//                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();


            } else if (result.equalsIgnoreCase("login")) {

                Toast.makeText(getApplicationContext(), "하 이  ", Toast.LENGTH_LONG).show();

            } else {


            }
        }

    }

    private class ListViewItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent chatStartIntent = new Intent(ConversationActivity_.this, PrivateChatActivity.class);
            String tossRoomid = chatRoomListdata.get(position).roomId;
            String chatWith = chatRoomListdata.get(position).chat_with;
            String chatWithName = chatRoomListdata.get(position).userName;
            String chatWithImgUrl = chatRoomListdata.get(position).user_img_url;
            String lastMessage = chatRoomListdata.get(position).last_message;
            chatStartIntent.putExtra("room_id", tossRoomid);
            chatStartIntent.putExtra("opponent_id", chatWith);
            chatStartIntent.putExtra("opponent_name", chatWithName);
            chatStartIntent.putExtra("opponent_imgUrl", chatWithImgUrl);
            chatStartIntent.putExtra("lastMessage", lastMessage);

            startActivity(chatStartIntent);

        }

    }

    public void deleteRoomDialog() {
        deleteRoomDialog = new Dialog(this);
        deleteRoomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteRoomDialog.setCancelable(false);
        deleteRoomDialog.setContentView(R.layout.custom_dialog_delete);
        deleteRoomDialogTxt = (TextView) deleteRoomDialog.findViewById(R.id.text_dialog_delete);

        String message = "대화를 삭제하시겠습니까?";
        deleteRoomDialogTxt.setText(message);
        deleteRoomDialogYes = (Button) deleteRoomDialog.findViewById(R.id.btn_dialog_delete_yes);
        deleteRoomDialogNo = (Button) deleteRoomDialog.findViewById(R.id.btn_dialog_delete_no);
        deleteRoomDialogYes.setOnClickListener(this);
        deleteRoomDialogNo.setOnClickListener(this);
        deleteRoomDialog.show();
    }


    private class ListViewItemLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            deleteRoom = new ChatRoom();
            deleteMessage = new ChatMessage();
            deleteRoom.roomId = chatRoomListdata.get(position).roomId;
            deleteMessage.roomId = chatRoomListdata.get(position).roomId;
            deleteRoomDialog();

            return false;

        }
    }
}
