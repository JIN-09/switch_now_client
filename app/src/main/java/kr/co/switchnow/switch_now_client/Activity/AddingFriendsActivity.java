package kr.co.switchnow.switch_now_client.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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

import kr.co.switchnow.switch_now_client.ADT.ListData;
import kr.co.switchnow.switch_now_client.Adapter.ListViewFriendAdapter;
import kr.co.switchnow.switch_now_client.R;
import kr.co.switchnow.switch_now_client.Util.CustomDialog;

import static kr.co.switchnow.switch_now_client.Activity.loginActivity.userSession;

public class AddingFriendsActivity extends AppCompatActivity implements View.OnClickListener {


    static protected ListView mListView = null;
    public static ListViewFriendAdapter mFriendAdapter = null;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private ArrayList<ListData> objects = null;
    private SharedPreferences session;
    public static ArrayList<String> friendList;
    public static ArrayList<String> BlockList;
    TextView adding_friends_title;
    static TextView navigation_msg;
    ArrayList<String> contactlist;
    String[] ContactArray;
    String friendListJSON;
    String Json;
    int Counter;
    static Button nextStep;
    String fromSessionMobileNum;
    String fromSessionEmail;
    String friendListToServer;
    String blockListToServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int switchDarkgrey = getResources().getColor(R.color.switchDarkgrey);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(switchDarkgrey);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.switchDarkgrey));
        }
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setCustomView(R.layout.adding_friends_title);

        setContentView(R.layout.activity_adding_friends);
        adding_friends_title = (TextView) findViewById(R.id.adding_friends_title_txt);
        Typeface face = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            face = getResources().getFont(R.font.enorbit);
        }else{
            face = Typeface.createFromAsset(getAssets(), "enorbit.TTF");
        }

        adding_friends_title.setTypeface(face);

        mListView = (ListView) findViewById(R.id.add_friend_listview);
        objects = new ArrayList<>();
        mFriendAdapter = new ListViewFriendAdapter(this, objects);
        mListView.setAdapter(mFriendAdapter);
        mListView.setCacheColorHint(Color.TRANSPARENT);
        session = getSharedPreferences(userSession, Context.MODE_PRIVATE);

        nextStep = (Button) findViewById(R.id.save_friend_btn);
        nextStep.setOnClickListener(this);

        navigation_msg = (TextView) findViewById(R.id.navigation_msg);
        //android.permission.READ_CONTACTS"
        Counter = 0;
        ContactArray =null;
        Json = "";
        fromSessionMobileNum ="";
        fromSessionEmail = "";



        retrieveContactNumber();

        Log.d("TAG","LIST VALUE------------------------------------->"+ contactlist.toString());
        ContactArray = new String[contactlist.size()];
        getJSON();
        Log.d("TAG","ARR VALUE--------------------------------------->"+ Json);
        loadDataFromSession();
        friendList = new ArrayList<>();
        BlockList = new ArrayList<>();
        new AsyncGetFriendList().execute(Json, fromSessionMobileNum);
    }



    public void onBackPressed() {
        CustomDialog alert = new CustomDialog();
        alert.exitDialog(this, "Switch를 종료하시겠습니까?");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.save_friend_btn: {

                getTwoListsString();
                new AsyncSendTwoLists().execute(friendListToServer, blockListToServer, fromSessionEmail);

//
                Intent intent = new Intent(AddingFriendsActivity.this, ReadyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                startActivity(intent);

                break;
            }

        }

    }

    public void showNavi(){

        navigation_msg.setVisibility(View.VISIBLE);
        navigation_msg.bringToFront();

    }


    public void setBtnTxt(){

        nextStep.setText("시작하기");

    }

    public void getTwoListsString(){

        friendListToServer = new Gson().toJson(friendList);
        blockListToServer = new Gson().toJson(BlockList);

    }


    public void loadDataFromSession(){

        fromSessionEmail = session.getString("user_Fb_id", "");
        fromSessionMobileNum = session.getString("userMobile", "");
        Log.d("TAG","fromSessionMobileNum--------------------->"+ fromSessionMobileNum);
    }



    public static class ViewHolder {

        public ImageView m_Profile = null;
        public TextView m_NameText = null;
        public ImageView mAdd_btn = null;
        public ImageView mBlock_btn = null;

    }


    public ArrayList<String> retrieveContactNumber() {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID, // 연락처 ID -> 사진 정보 가져오는데 사용
                ContactsContract.CommonDataKinds.Phone.NUMBER,        // 연락처
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

        String[] selectionArgs = null;

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";


        Cursor contactCursor = getApplicationContext().getContentResolver().query(uri, projection, null,selectionArgs, sortOrder);

        contactlist = new ArrayList<>();

        if (contactCursor.moveToFirst()) {

            do {

                String phonenumber = contactCursor.getString(1).replace("+82","0").replace("-", "").replace(" ", "").replace("/", "");

                contactlist.add(phonenumber);

                Log.d("TAG","phoneNumber---------------------------------->"+phonenumber);
                Counter++;
            } while (contactCursor.moveToNext());
            Log.d("TAG","Number of Numbers--------------------------------->"+ Counter);
        }

        return contactlist;
    }

    public void getJSON(){

        Json = new Gson().toJson(contactlist);
        ContactArray = contactlist.toArray(ContactArray);
    }


    private class AsyncGetFriendList extends AsyncTask<String, String, String> {
        HttpURLConnection conn =null;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://115.71.232.209/client/get_friendList.php");

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
                        .appendQueryParameter("_mobile_numbers", params[0])
                        .appendQueryParameter("_my_number", params[1]);



                Log.d("TAG", "GSON TO SEND----------------------> " + Json);
                Log.d("TAG", "My NUMBER ------------------------> " + fromSessionMobileNum);

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

            Log.d("TAG", "result_String-------->" + result);

            if (result.equalsIgnoreCase("available_id")) {
//                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();


            } else if (result.equalsIgnoreCase("login")) {

                Toast.makeText(getApplicationContext(), "하 이  ", Toast.LENGTH_LONG).show();

            }else {

                friendListJSON = result;
                showAddingFriendList(friendListJSON);
            }
        }

    }


    public void showAddingFriendList(String jsonValue){

        try{
            JSONObject jsonObject = new JSONObject(jsonValue);
            JSONArray friendCandidate = jsonObject.getJSONArray("json");

            for(int i = 0; i<friendCandidate.length(); i++){
                JSONObject getItem = friendCandidate.getJSONObject(i);
                String email = getItem.getString("id");
                String gender = getItem.getString("gender");
                String userName = getItem.getString("name");
                String mNumber = getItem.getString("mobile");
                String status = getItem.getString("msg");

                mFriendAdapter.addItem(email, gender, userName, mNumber, status);

                Log.d("TAG", "addITEM---------------------->"+ email);
            }


        }catch (JSONException e){

            e.printStackTrace();
            Log.d("TAG", "JSON Errr-------------->" +e );

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


                Log.d("TAG", "Friend List ----------------------> " + friendListToServer);
                Log.d("TAG", "Block List  ----------------------> " + blockListToServer);
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

            }else {


            }
        }

    }



}
