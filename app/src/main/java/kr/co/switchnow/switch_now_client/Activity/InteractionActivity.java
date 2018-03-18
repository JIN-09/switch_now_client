package kr.co.switchnow.switch_now_client.Activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Location;
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
import android.view.WindowManager;
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
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.switchnow.switch_now_client.ADT.ChatRoom;
import kr.co.switchnow.switch_now_client.ADT.ListData;
import kr.co.switchnow.switch_now_client.Adapter.CustomDrawerAdapter;
import kr.co.switchnow.switch_now_client.Adapter.ListViewOnlineAdapter;
import kr.co.switchnow.switch_now_client.R;
import kr.co.switchnow.switch_now_client.SQLite.ChatMessageDBManager;
import kr.co.switchnow.switch_now_client.SQLite.ChatRoomDBManager;
import kr.co.switchnow.switch_now_client.Util.ActivitySwipeDetector;
import kr.co.switchnow.switch_now_client.Util.BroadcastService;
import kr.co.switchnow.switch_now_client.Util.CustomDialog;
import kr.co.switchnow.switch_now_client.Util.MessageReceiver;
import kr.co.switchnow.switch_now_client.Util.SwipeInterface;
import okhttp3.OkHttpClient;

import static kr.co.switchnow.switch_now_client.Activity.ReadyActivity.SwitchTime;
import static kr.co.switchnow.switch_now_client.Activity.ReadyActivity.userSetting;
import static kr.co.switchnow.switch_now_client.Activity.loginActivity.userSession;
import static kr.co.switchnow.switch_now_client.Util.ServiceThread.serviceSocket;


public class InteractionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, SwipeInterface {


    static protected ListView oListView = null;
    public static ListViewOnlineAdapter mOnlineAdapter = null;
    public static ListView dListView = null;
    public static CustomDrawerAdapter mCustomDrawerAdapter = null;


    public static ArrayList<String> dFriendList;
    public static ArrayList<String> dBlockList;
    public static ArrayList<ChatRoom> tmpChatRoomList;


    public static ArrayList<ChatRoom> fromServiceChatRoomList;


    private Socket socketForChatInter;
    private PrintWriter printWriterOut;

    public static ChatRoomDBManager chatRoomDBManager = null;
    public static ChatMessageDBManager chatMessageDBManager = null;

    private ArrayList<ListData> drawerObjects = null;
    private ArrayList<ListData> objects = null;
    private ArrayList<ListData> tempListForSorting = null;
    ListData tempData = null;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private SharedPreferences.Editor mEditor;
    private SharedPreferences.Editor tEditor;
    private SharedPreferences session;
    private SharedPreferences setting;
    private SharedPreferences time_value;
    private DrawerLayout mParentLayout;

    CircleImageView profile_img;
    ImageView miniCamera;
    TextView profile_name_text_nav;
    TextView profile_status_text_nav;
    TextView interaction_title_text;
    String fromSessionName;
    String fromSessionStatusMessage;
    String fromSessionEmail;
    String fromSessionProfileImgLink;
    String fromSessionMobileNum;
    Boolean fromSessionFirstFlag;
    Boolean fromSessionFlag;
    Boolean fromSessionProfileEdit;
    TextView revCounter_inter;
    protected ProgressBar progressBar_inter;
    protected int progressStatus_inter;
    BaseActivity baseActivity;
    TextView time_start;
    String switch_value;
    int switch_time;
    int switch_time_mill;
    public static boolean isSwitchOn;

    String totalJSON;
    float DistancFromMe;
    CountDownTimer countDownTimer;
    public static int CounterforNewFriends;
    int onlineFriendListNumber;

    float myLati;
    float myLongi;

    RelativeLayout NavHeader = null;
    NavigationView navigationView;
    TextView manageFriendText;
    static TextView navigation_msg_drawer;
    Boolean isOnceOpend;
    String Json;
    ArrayList<String> contactlist;

    String newFriendListToServer;
    String newBlockListToServer;

    String msgFeature;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int switchLime = getResources().getColor(R.color.switchLime);
        setContentView(R.layout.activity_interaction);
        baseActivity = new BaseActivity(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Stetho.initializeWithDefaults(this);

        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();


        chatRoomDBManager = new ChatRoomDBManager();
        chatMessageDBManager = new ChatMessageDBManager();


        switch_value = "";
        switch_time = 0;
        switch_time_mill = 0;


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.interaction_title);
        interaction_title_text = (TextView) findViewById(R.id.interaction_title_txt);
        Typeface face = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            face = getResources().getFont(R.font.enorbit);
        }else{
            face = Typeface.createFromAsset(getAssets(), "enotbit.TTF");
        }
        interaction_title_text.setTypeface(face);
        mParentLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        isOnceOpend = false;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        fromSessionProfileImgLink = "";
        fromSessionName = "";
        fromSessionStatusMessage = "";
        fromSessionProfileImgLink = "";
        fromSessionEmail = "";
        fromSessionMobileNum = "";
        fromSessionFirstFlag = false;
        fromSessionFlag = false;
        fromSessionProfileEdit = false;

        totalJSON = "";
        session = getSharedPreferences(userSession, Context.MODE_PRIVATE);
        loadDataFromSession();


        oListView = (ListView) findViewById(R.id.interaction_switchOn_listview);
        objects = new ArrayList<>();
        mOnlineAdapter = new ListViewOnlineAdapter(this, objects, fromSessionName);
        oListView.setAdapter(mOnlineAdapter);


        ActivitySwipeDetector swipe = new ActivitySwipeDetector(this, this);
        mParentLayout.setOnTouchListener(swipe);
        oListView.setOnTouchListener(swipe);


        setting = getSharedPreferences(userSetting, Context.MODE_PRIVATE);
        time_value = getSharedPreferences(SwitchTime, Context.MODE_PRIVATE);

        mEditor = session.edit();
        tEditor = time_value.edit();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        revCounter_inter = (TextView) findViewById(R.id.revCounter_inter);
        revCounter_inter.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));
        progressBar_inter = (ProgressBar) findViewById(R.id.progress_bar_inter);
        progressBar_inter.setRotation(180);
        progressStatus_inter = 0;


        profile_img = (CircleImageView) navigationView.findViewById(R.id.profile_image_nav);
        miniCamera = (ImageView) navigationView.findViewById(R.id.mini_camera_image);
        profile_name_text_nav = (TextView) navigationView.findViewById(R.id.profile_name_text_nav);
        profile_status_text_nav = (TextView) navigationView.findViewById(R.id.profile_status_text_nav);
        navigationView.setNavigationItemSelectedListener(this);

        manageFriendText = (TextView) navigationView.findViewById(R.id.drawer_manage_friend_txt);
        manageFriendText.setOnClickListener(this);

        navigation_msg_drawer = (TextView) navigationView.findViewById(R.id.navigation_msg_drawer);

        dListView = (ListView) navigationView.findViewById(R.id.add_friend_listview_drawer);
        drawerObjects = new ArrayList<>();
        mCustomDrawerAdapter = new CustomDrawerAdapter(this, drawerObjects);
        dListView.setAdapter(mCustomDrawerAdapter);


        //
        loadTime();

        time_start = (TextView) findViewById(R.id.time_start);
        time_start.setText(switch_value + "min.");
        time_start.setTextColor(switchLime);
        countDownTimer(switch_time_mill);


        Log.d("TAG", "user_name---->" + fromSessionName);
        Log.d("TAG", "user_img_link---->" + fromSessionProfileImgLink);

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
        onlineFriendListNumber = 0;
        tempListForSorting = new ArrayList<>();
        profile_img.setOnClickListener(this);
        startService(new Intent(this, BroadcastService.class));
        boolean check = isMyServiceRunning(MessageReceiver.class);

        Log.d("TAG", "Service------------------Running Check------------------> : " + check);
        if (!check) {
            startService(new Intent(this, MessageReceiver.class));
        }
        new AsyncGetOnlineList().execute(fromSessionEmail);

        tmpChatRoomList = new ArrayList<>();
        fromServiceChatRoomList = new ArrayList<>();

        miniCamera.bringToFront();
        msgFeature = "";


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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            countDownTimer.cancel();
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

    public void HoldMessageToChat(String message, String recepient, String recepientName, String MyId, String myName) {
//        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();


        Log.d("TAG", "MessageTo----> " + recepient + "----------------------->" + message + "from--------------->" + MyId + " / " + myName);
        String RoomId = MyId + recepient;
        int roomstatus = 1;
        SimpleDateFormat nowData = new SimpleDateFormat("dd일 HH:mm");
        String currentTime = nowData.format(new Date());
        URL imageURL;
        String img_url;

        ChatRoom tmpChatRoom;
        tmpChatRoom = new ChatRoom();
        tmpChatRoom.roomId = RoomId;
        tmpChatRoom.user_id = MyId;
        tmpChatRoom.chat_with = recepient;
        tmpChatRoom.room_status = roomstatus;
        tmpChatRoom.updated_time = currentTime;
        tmpChatRoom.last_message = message;
        tmpChatRoom.userName = recepientName;


        try {
            imageURL = new URL("http://115.71.232.209/client/profile/" + tmpChatRoom.chat_with + ".png");
            img_url = String.valueOf(imageURL);
            tmpChatRoom.user_img_url = img_url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        if (sentMessageToServer(tmpChatRoom.user_id, myName, tmpChatRoom.chat_with, tmpChatRoom.last_message)) {
//            tmpChatRoomList.add(tmpChatRoom);
            if (chatRoomDBManager.getChatRoomData(tmpChatRoom.roomId) == null) {
                chatRoomDBManager.addRoomData(tmpChatRoom);
                chatMessageDBManager.insertMessageData(tmpChatRoom.roomId, tmpChatRoom.user_id, myName, tmpChatRoom.last_message, 1, currentTime, 0, tmpChatRoom.user_img_url);
            } else {
                chatRoomDBManager.removeChatRoomData(tmpChatRoom);
                chatRoomDBManager.addRoomData(tmpChatRoom);
                chatMessageDBManager.insertMessageData(tmpChatRoom.roomId, tmpChatRoom.user_id, myName, tmpChatRoom.last_message, 1, currentTime, 0, tmpChatRoom.user_img_url);
            }
        }

    }

    public boolean sentMessageToServer(String user_id, String myName, String chat_with, String message) {
        msgFeature = "sentFromNote";
        try {

            printWriterOut = new PrintWriter(new BufferedOutputStream(serviceSocket.getOutputStream()), true);

            Log.d("TAG", "SENDING MESSAGE------------------------->" + msgFeature + "/" + user_id + "/" + chat_with + "/" + fromSessionName + "/" + message);
            printWriterOut.println(msgFeature + "/" + user_id + "/" + chat_with + "/" + myName + "/" + message); //msgFeature/userid/chatwith/roomID/ + msg


            return true;

        } catch (IOException e) {

            e.printStackTrace();
            Log.d("TAG", "SENDING MESSAGE ERR------------>" + e);

            return false;
        }

    }

    public static class ViewHolder {

        public ImageView m_Profile = null;
        public TextView m_NameText = null;
        public ImageView mMessage_btn = null;
        public ImageView mCall_btn = null;

    }

    public static class SectionHolder {

        public TextView DistanceSeperator = null;

    }


    public static class DrawerViewHolder {

        public ImageView m_Profile = null;
        public TextView m_NameText = null;
        public ImageView mAdd_btn = null;
        public ImageView mBlock_btn = null;

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
                Intent intent = new Intent(InteractionActivity.this, ManageFriendsActivity.class);
                startActivity(intent);

                break;
            }
        }
    }


    public void reload() {
        loadDataFromSession();
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
        miniCamera.bringToFront();
    }


    public void showMsg() {

        navigation_msg_drawer.setVisibility(View.VISIBLE);
        navigation_msg_drawer.bringToFront();

    }


    public void onResume() {
        super.onResume();
        reload();

        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));


    }

    @Override
    public void onPause() {
        super.onPause();
//        saveTime();
        unregisterReceiver(br);
        Log.i("TAG", "_______________________________Unregistered broacast receiver");
    }

    @Override
    public void onStop() {
        try {
//            registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
//        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
//        stopService(new Intent(this, BroadcastService.class));

        Log.i("TAG", "Stopped service");
        boolean check = isMyServiceRunning(MessageReceiver.class);
        Log.d("TAG", "Service------------------Running Check------------------> : " + check);

        super.onDestroy();
    }


    @Override
    public void onLeftToRight(View v) {

    }

    @Override
    public void onRightToLeft(View v) {
        baseActivity.finish();

        Intent intent = new Intent(this, ConversationActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        saveTime();
//        try {
//            socketForChatInter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        startActivity(intent);
        countDownTimer.cancel();
        baseActivity.slideInFromRight(this, this.findViewById(android.R.id.content));
        Toast.makeText(this, "CONVERSATION LIST", Toast.LENGTH_SHORT).show();

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
                progressStatus_inter = (int) (((totalTime) - millisUntilFinished) / (double) totalTime * 100);


                progressBar_inter.setProgress(progressStatus_inter);
                revCounter_inter.setText(hms);
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
                isSwitchOn = false;
                saveTime();
                try {
                    if (serviceSocket != null) {
                        serviceSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                revCounter_inter.setText("00:00:00");
            }
        }.start();

    }


    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            isSwitchOn = intent.getBooleanExtra("isSwitchOn", false);
            Log.i("TAG", "Countdown seconds remaining: " + millisUntilFinished / 1000);
            saveTime();
            countDownTimer.cancel();
            switch_time_mill = (int) millisUntilFinished;
            countDownTimer(switch_time_mill);
        }
    }


    private class AsyncGetOnlineList extends AsyncTask<String, String, String> {
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
                url = new URL("http://115.71.232.209/client/get_online_member.php");

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
                        .appendQueryParameter("_email", params[0]);

                Log.d("TAG", "email----------------------> " + fromSessionEmail);

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

            } else if (result.equalsIgnoreCase("login")) {

            } else {


                inputJSONtoSectionListView(result);
            }
        }

    }

    public void inputJSONtoSectionListView(String JSON) {
        Log.d("TAG", "TOTAL JSON VALUE------------------------------------->" + JSON);

        Boolean distance500m = false;
        Boolean distance1km = false;
        Boolean distance3km = false;
        Boolean distance5km = false;
        Boolean distance10km = false;
        Boolean distanceAbove10km = false;

        try {
            JSONObject mainObject = new JSONObject(JSON);
            JSONArray friendList = mainObject.getJSONArray("json");
            Log.d("TAG", "friendList------------------------------------>" + friendList.toString());
            JSONArray myLocation = mainObject.getJSONArray("my_location");
            Log.d("TAG", "myLocation---------------------------------->" + myLocation.toString());
            JSONArray locationInfo = mainObject.getJSONArray("onlineInfo");
            Log.d("TAG", "location Info---------------------------------->" + locationInfo.toString());

            JSONObject getMyLocation = myLocation.getJSONObject(0);
            String myTempLati = getMyLocation.getString("lati");
            String myTempLongi = getMyLocation.getString("longi");

            myLati = Float.parseFloat(myTempLati);
            myLongi = Float.parseFloat(myTempLongi);

            Log.d("TAG", "myLati---------------------->" + myLati);
            Log.d("TAG", "myLongi---------------------->" + myLongi);


            onlineFriendListNumber = friendList.length();
            for (int i = 0; i < friendList.length(); i++) {
                JSONObject getFriendList = friendList.getJSONObject(i);
                String email = getFriendList.getString("id");
//
                for (int j = 0; j < locationInfo.length(); j++) {
                    JSONObject getLocationInfo = locationInfo.getJSONObject(j);
                    String email_loc = getLocationInfo.getString("id");
                    if (email.contentEquals(email_loc)) {
                        String lati = getLocationInfo.getString("lati");
                        String longi = getLocationInfo.getString("longi");
                        Log.d("TAG", "LATI--------------------------------->" + lati);
                        Log.d("TAG", "LONGI--------------------------------->" + longi);

                        float lati_ = Float.parseFloat(lati);
                        float longi_ = Float.parseFloat(longi);
                        calculateDistance(lati_, longi_);
                        break;
                    } else {
                        continue;
                    }
                }
//
                String gender = getFriendList.getString("gender");
                String userName = getFriendList.getString("name");
                String mNumber = getFriendList.getString("mobile");
                String status = getFriendList.getString("msg");

                Log.d("TAG", "UserID of Add Item ---------------------->" + email);
                Log.d("TAG", "Distance value---------------------->" + DistancFromMe);

//                String email_id, String gender, String userName, String mobile_number, String status, float distance
                Log.d("TAG", "online friend number------------------------------>" + onlineFriendListNumber);
                Log.d("TAG", "tempList Size------------------------------------->" + tempListForSorting.size());

                if (addDataForTemp(email, gender, userName, mNumber, status, DistancFromMe)) {
                    SortingPorcess();
                    int k;
                    int contents = 0;
                    int size = tempListForSorting.size();
                    for (k = 0; k < size; k++) {
                        ListData checkData = tempListForSorting.get(contents);
                        Log.d("TAG", "DISTANCE SORTING----------------->" + checkData.m_distance);

                        if (checkData.m_distance <= 500) {
                            if (!distance500m) {
                                mOnlineAdapter.addSectionHeaderItem("500m", k);
                                k += 1;
                                size += 1;
                                mOnlineAdapter.addItem(checkData.m_email_id, checkData.m_gender, checkData.m_userName, checkData.m_mobile_number, checkData.m_status, checkData.m_distance, fromSessionEmail);
                                contents += 1;
                                distance500m = true;
                            } else {
                                mOnlineAdapter.addItem(checkData.m_email_id, checkData.m_gender, checkData.m_userName, checkData.m_mobile_number, checkData.m_status, checkData.m_distance, fromSessionEmail);
                                contents += 1;
                            }

                        } else if (checkData.m_distance > 500 && checkData.m_distance <= 1000) {

                            if (!distance1km) {
                                mOnlineAdapter.addSectionHeaderItem("1km", k);
                                k += 1;
                                size += 1;
                                mOnlineAdapter.addItem(checkData.m_email_id, checkData.m_gender, checkData.m_userName, checkData.m_mobile_number, checkData.m_status, checkData.m_distance, fromSessionEmail);
                                contents += 1;
                                distance1km = true;
                            } else {
                                mOnlineAdapter.addItem(checkData.m_email_id, checkData.m_gender, checkData.m_userName, checkData.m_mobile_number, checkData.m_status, checkData.m_distance, fromSessionEmail);
                                contents += 1;
                            }


                        } else if (checkData.m_distance > 1000 && checkData.m_distance <= 3000) {

                            if (!distance3km) {
                                mOnlineAdapter.addSectionHeaderItem("3km", k);
                                k += 1;
                                size += 1;
                                mOnlineAdapter.addItem(checkData.m_email_id, checkData.m_gender, checkData.m_userName, checkData.m_mobile_number, checkData.m_status, checkData.m_distance, fromSessionEmail);
                                contents += 1;
                                distance3km = true;
                            } else {
                                mOnlineAdapter.addItem(checkData.m_email_id, checkData.m_gender, checkData.m_userName, checkData.m_mobile_number, checkData.m_status, checkData.m_distance, fromSessionEmail);
                                contents += 1;
                            }

                        } else if (checkData.m_distance > 3000 && checkData.m_distance <= 5000) {

                            if (!distance5km) {
                                mOnlineAdapter.addSectionHeaderItem("5km", k);
                                k += 1;
                                size += 1;
                                mOnlineAdapter.addItem(checkData.m_email_id, checkData.m_gender, checkData.m_userName, checkData.m_mobile_number, checkData.m_status, checkData.m_distance, fromSessionEmail);
                                contents += 1;
                                distance5km = true;
                            } else {
                                mOnlineAdapter.addItem(checkData.m_email_id, checkData.m_gender, checkData.m_userName, checkData.m_mobile_number, checkData.m_status, checkData.m_distance, fromSessionEmail);
                                contents += 1;
                            }


                        } else if (checkData.m_distance > 5000 && checkData.m_distance <= 10000) {

                            if (!distance10km) {

                                mOnlineAdapter.addSectionHeaderItem("10km", k);
                                k += 1;
                                size += 1;
                                mOnlineAdapter.addItem(checkData.m_email_id, checkData.m_gender, checkData.m_userName, checkData.m_mobile_number, checkData.m_status, checkData.m_distance, fromSessionEmail);
                                contents += 1;
                                distance10km = true;

                            } else {

                                mOnlineAdapter.addItem(checkData.m_email_id, checkData.m_gender, checkData.m_userName, checkData.m_mobile_number, checkData.m_status, checkData.m_distance, fromSessionEmail);
                                contents += 1;
                            }

                        } else {

                            if (!distanceAbove10km) {
                                mOnlineAdapter.addSectionHeaderItem("Beyond 10km", k);
                                k += 1;
                                size += 1;
                                mOnlineAdapter.addItem(checkData.m_email_id, checkData.m_gender, checkData.m_userName, checkData.m_mobile_number, checkData.m_status, checkData.m_distance, fromSessionEmail);
                                contents += 1;
                                distanceAbove10km = false;

                            } else {
                                mOnlineAdapter.addItem(checkData.m_email_id, checkData.m_gender, checkData.m_userName, checkData.m_mobile_number, checkData.m_status, checkData.m_distance, fromSessionEmail);
                                contents += 1;
                            }

                        }


                    }

                }


            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("TAG", "ERR----------JSON VALUE--------->" + e);
        }


    }


    public void calculateDistance(float lati, float longi) {
        DistancFromMe = 0;
        Location location_A = new Location("point Me");
        location_A.setLatitude(myLati);
        location_A.setLongitude(myLongi);

        Location location_B = new Location("point freind");
        location_B.setLatitude(lati);
        location_B.setLongitude(longi);

        DistancFromMe = location_A.distanceTo(location_B);

    }


    //                String email_id, String gender, String userName, String mobile_number, String status, float distance
    public void SortingPorcess() {
        Collections.sort(tempListForSorting, new Comparator<ListData>() {
            @Override
            public int compare(ListData o1, ListData o2) {
                return (o1.m_distance < o2.m_distance) ? -1 : (o1.m_distance > o2.m_distance) ? 1 : 0;
            }
        });
    }

    public boolean addDataForTemp(String email, String gender, String userName, String mNum, String status, float Dis) {
        tempData = new ListData();

        tempData.m_email_id = email;
        tempData.m_gender = gender;
        tempData.m_userName = userName;
        tempData.m_mobile_number = mNum;
        tempData.m_status = status;
        tempData.m_distance = Dis;

        tempListForSorting.add(tempData);

        if (tempListForSorting.size() == onlineFriendListNumber) {

            return true;

        } else {

            return false;
        }

//        return false;
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


}
