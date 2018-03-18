package kr.co.switchnow.switch_now_client.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import kr.co.switchnow.switch_now_client.Adapter.BlockListForFragmentAdapter;
import kr.co.switchnow.switch_now_client.R;

import static kr.co.switchnow.switch_now_client.Activity.loginActivity.userSession;

/**
 * Created by ceo on 2017-04-30.
 */

public class BlockListFragment extends BaseFragment {

    public static final String ITEM_TEXT = "차단목록";

    static protected ListView fbListView = null;
    public static BlockListForFragmentAdapter mBlockListForFragmentAdapter = null;

    private ArrayList<ListData> objects = null;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private SharedPreferences session;

    public static ArrayList<String> bfFriendList;
    static TextView msgOfCurrentBlockList;

    String fromSessionName;
    String fromSessionStatusMessage;
    String fromSessionEmail;
    String fromSessionProfileImgLink;
    String fromSessionMobileNum;
    Boolean fromSessionFirstFlag;
    Boolean fromSessionFlag;
    Boolean fromSessionProfileEdit;
    String blockListCounter;



    public BlockListFragment(){

    }

    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        objects = new ArrayList<>();
        session = this.getActivity().getSharedPreferences(userSession, Context.MODE_PRIVATE);
        fromSessionProfileImgLink = "";
        fromSessionName = "";
        fromSessionStatusMessage = "";
        fromSessionProfileImgLink = "";
        fromSessionEmail = "";
        fromSessionMobileNum = "";
        fromSessionFirstFlag = false;
        fromSessionFlag = false;
        fromSessionProfileEdit = false;

        blockListCounter = "";

        loadDataFromSession();
        bfFriendList = new ArrayList<>();
        new AsyncGetCurrentBlockList().execute(fromSessionEmail);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        View view = inflater.inflate(R.layout.block_list_fragment, container, false);
        fbListView = (ListView) view.findViewById(R.id.blocked_friend_listview);
        msgOfCurrentBlockList = (TextView) view.findViewById(R.id.blockListMsg);
        mBlockListForFragmentAdapter = new BlockListForFragmentAdapter(getActivity(), objects);
        fbListView.setAdapter(mBlockListForFragmentAdapter);
        return view;
    }

    @Override
    public String getFragmentName() {
        return ITEM_TEXT;
    }

    @Override
    public String getFragmentContentsCounter() {
        return blockListCounter;
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

    public static class  bViewHolder {

        public ImageView m_Profile = null;
        public TextView m_NameText = null;
        public ImageView m_Add_btn = null;

    }

    public void showMsg() {
        msgOfCurrentBlockList.setVisibility(View.VISIBLE);
        msgOfCurrentBlockList.bringToFront();
    }


    private class AsyncGetCurrentBlockList extends AsyncTask<String, String, String> {
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
                url = new URL("http://115.71.232.209/client/get_current_blockList.php");

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

            Log.d("TAG", "current_Block_result_String----------->" + result);

            if (result.equalsIgnoreCase("available_id")) {

            } else if (result.equalsIgnoreCase("")) {
                showMsg();

            } else {

                showCurrentBlockList(result);
            }
        }

    }


    public void showCurrentBlockList(String jsonValue){

        try{
            JSONObject currentBlockObj = new JSONObject(jsonValue);
            JSONArray currentBlockArr = currentBlockObj.getJSONArray("current_Block");

            blockListCounter = String.valueOf(currentBlockArr.length());

            for (int i = 0; i < currentBlockArr.length(); i++) {
                JSONObject getItem = currentBlockArr.getJSONObject(i);
                String email = getItem.getString("id");
                String gender = getItem.getString("gender");
                String userName = getItem.getString("name");
                String mNumber = getItem.getString("mobile");
                String status = getItem.getString("msg");


                mBlockListForFragmentAdapter.addItem(email, gender, userName, mNumber, status);
                Log.d("TAG", "BlockList_addITEM---------------------->" + email);

            }


        } catch (JSONException e){
            e.printStackTrace();
            Log.d("TAG", "JSON Errr-------------->" + e);

        }


    }






}
