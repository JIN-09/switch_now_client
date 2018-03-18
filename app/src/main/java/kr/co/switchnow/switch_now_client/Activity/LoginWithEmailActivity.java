package kr.co.switchnow.switch_now_client.Activity;

import android.app.Dialog;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.co.switchnow.switch_now_client.R;
import kr.co.switchnow.switch_now_client.Util.CustomDialog;

import static kr.co.switchnow.switch_now_client.Activity.loginActivity.userSession;


public class LoginWithEmailActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, TextWatcher {


    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    TextView login_with_email_title;
    EditText login_id_input;
    EditText login_pswd_input;
    Button login_btn_with_email;
    private static final String TAG_NAME = "name";

    TextView profile_login_name_text;
    TextView login_id_check;
    TextView login_pswd_check;

    private SharedPreferences.Editor mEditor;
    private SharedPreferences session;
    protected Uri mCurrentPhotoPath;

    String idCheckedFromServer;
    String pswdCheckedFromServer;

    Boolean idCheckedFlag;
    Boolean pswdCheckedFlag;
    Boolean isReadyToLogin;

    CircleImageView profile_img_login;
    String fromSessionName;
    String fromSessionStatusMessage;
    String fromSessionEmail;
    String fromSessionProfileImgLink;
    String fromSessionOriginalImgLink;
    String fromSessionMobileNum;
    Boolean fromSessionFirstFlag;
    Boolean fromSessionFlag;
    Boolean fromSessionProfileEdit;


    String login_id;
    String login_pswd;
    URL imageURL;
    String myJSON;

    Dialog verifyDialog = null;
    TextView verify_dialog_text;
    Button verify_dialog_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int switchDarkgrey = getResources().getColor(R.color.switchDarkgrey);

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
        getSupportActionBar().setCustomView(R.layout.login_email_title);


        setContentView(R.layout.activity_register);
        login_with_email_title = (TextView) findViewById(R.id.login_with_email_title);
        Typeface face = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            face = getResources().getFont(R.font.enorbit);
        }else{
            face = Typeface.createFromAsset(getAssets(), "enorbit.ttf");
        }

//        if()

        login_with_email_title.setTypeface(face);

        setContentView(R.layout.activity_login_with_email);

        login_id_input = (EditText) findViewById(R.id.login_id_input);
        login_pswd_input = (EditText) findViewById(R.id.login_pswd_input);
        login_btn_with_email = (Button) findViewById(R.id.login_btn_with_email);
        login_btn_with_email.setOnClickListener(this);

        profile_img_login = (CircleImageView) findViewById(R.id.profile_img_login);

        profile_login_name_text = (TextView) findViewById(R.id.profile_login_name_text);
        login_id_check = (TextView) findViewById(R.id.login_id_checked);
        login_pswd_check = (TextView) findViewById(R.id.login_pswd_checked);

        login_id_input.setOnFocusChangeListener(this);
        login_pswd_input.setOnFocusChangeListener(this);
        login_pswd_input.addTextChangedListener(this);

        session = getSharedPreferences(userSession, Context.MODE_PRIVATE);
        mEditor = session.edit();

        idCheckedFromServer = "pre";
        pswdCheckedFromServer = "pre";

        idCheckedFlag = false;
        pswdCheckedFlag = false;

        fromSessionName = "";
        fromSessionStatusMessage = "";
        fromSessionProfileImgLink = "";
        fromSessionEmail = "";
        fromSessionMobileNum = "";
        fromSessionFirstFlag = false;
        fromSessionFlag = false;
        fromSessionProfileEdit = false;

        fromSessionOriginalImgLink = "";
        imageURL = null;
        myJSON = "";
        login_id = "";
        login_pswd = "";


        loadDataFromSession();


        if (!fromSessionEmail.equals("")) {

            login_id_input.setText(fromSessionEmail);
            login_btn_with_email.setText("비밀번호를 입력해주세요.");
            login_id_check.setVisibility(View.VISIBLE);
            login_id = fromSessionEmail;
            idCheckedFlag = true;
            idCheckedFromServer = "post";
        }

        if (!fromSessionProfileImgLink.equals("")) {

            Picasso.with(this).load(fromSessionProfileImgLink).into(profile_img_login);
        }

        if (!fromSessionName.equals("")) {

            profile_login_name_text.setText(fromSessionName);
            profile_login_name_text.setVisibility(View.VISIBLE);
        }


        mEditor.clear();
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
        CustomDialog alert = new CustomDialog();
        alert.exitDialog(this, "로그인을 종료하시겠습니까?");

    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.login_btn_with_email: {

                if (isReadyToLogin) {
                    new AsyncLoginWithEmail().execute(login_id, login_pswd, idCheckedFromServer, pswdCheckedFromServer);
                } else {

                    Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                break;
            }

            case R.id.btn_dialog_confirm: {

                verifyDialog.dismiss();
                break;
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

    }

    public void saveSession() {

        mEditor.clear();
        mEditor.putString("userName", fromSessionName);
        mEditor.putString("userMobile", fromSessionMobileNum);
        mEditor.putString("user_Fb_id", fromSessionEmail);
        mEditor.putString("profile_img_link", fromSessionProfileImgLink);
        mEditor.putString("userStatusMessage", fromSessionStatusMessage);
        mEditor.putBoolean("isFirstLogin", fromSessionFirstFlag);
        mEditor.putBoolean("Session", fromSessionFlag);
        mEditor.putBoolean("profile_edit_flag", fromSessionProfileEdit);


        mEditor.commit();

    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        switch (v.getId()) {

            case R.id.login_id_input: {

                if (idCheckedFlag == false && !hasFocus) {
                    login_id = login_id_input.getText().toString();

                    if (isEmailValid(login_id)) {
                        new AsyncLoginWithEmail().execute(login_id, login_pswd, idCheckedFromServer, pswdCheckedFromServer);

                    } else {

                        Toast.makeText(this, "E-mail 형식을 다시 한번 확인해주세요.", Toast.LENGTH_LONG).show();

                    }
                    readyToLogin();
                }

                break;
            }

//            case R.id.login_pswd_input: {
//
//                if (pswdCheckedFlag == false && !hasFocus) {
//
//                    login_pswd = login_pswd_input.getText().toString();
//                    if (login_pswd.length() >= 6 && (!login_pswd.contentEquals(""))) {
//
//                        login_pswd_check.setVisibility(View.VISIBLE);
//                        pswdCheckedFlag = true;
//                        pswdCheckedFromServer = "post";
//
//                    } else {
//
//                        Toast.makeText(this, "비밀번호는 6자이상이어야만 합니다.", Toast.LENGTH_LONG).show();
//                    }
//
//                    readyToLogin();
//                }
//
//                break;
//
//            }

        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        login_pswd = login_pswd_input.getText().toString();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (login_pswd.length() >= 6 && (!login_pswd.contentEquals(""))) {

            login_pswd_check.setVisibility(View.VISIBLE);
            pswdCheckedFlag = true;
            pswdCheckedFromServer = "post";

        } else {

            Toast.makeText(this, "비밀번호는 6자이상이어야만 합니다.", Toast.LENGTH_LONG).show();
        }

        readyToLogin();
    }


    private class AsyncLoginWithEmail extends AsyncTask<String, String, String> {
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
                url = new URL("http://115.71.232.209/client/login_with_email.php");

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
                        .appendQueryParameter("_pswd", params[1])
                        .appendQueryParameter("_checkID", params[2])
                        .appendQueryParameter("_checkPswd", params[3]);


                Log.d("TAG", "이메일 값--------------> " + login_id);
                Log.d("TAG", "pswd 값--------------> " + login_pswd);
                Log.d("TAG", "checkID --------------> " + idCheckedFromServer);

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
                    Log.d("TAG", "response---------------->true" + response_code);
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
                idCheckedFromServer = "post";
                idCheckedFlag = true;
                login_id_check.setVisibility(View.VISIBLE);
                readyToLogin();

                if (fromSessionProfileImgLink.equals("")) {
                    try {
                        imageURL = new URL("http://115.71.232.209/client/profile/" + login_id + ".png");
                        Log.d("TAG", "image URL-------------->" + imageURL);
                        fromSessionProfileImgLink = String.valueOf(imageURL);
                        Picasso.with(getApplicationContext()).load(fromSessionProfileImgLink).into(profile_img_login);

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }

                if (fromSessionName.equals("") && fromSessionMobileNum.equals("")) {
                    new AsyncLoginWithEmail().execute(login_id, login_pswd, idCheckedFromServer, pswdCheckedFromServer);
                }

            } else if (result.equalsIgnoreCase("login")) {
                Intent intent = new Intent(LoginWithEmailActivity.this, ReadyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                fromSessionEmail = login_id;
                fromSessionFirstFlag = false;
                fromSessionFlag = true;

                saveSession();
                Toast.makeText(getApplicationContext(), "Welcome back", Toast.LENGTH_LONG).show();

                startActivity(intent);

            } else if (result.equalsIgnoreCase("invalid_pass")) {
                Toast.makeText(getApplicationContext(), "비밀번호가 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                pswdCheckedFlag = false;
                login_pswd_check.setVisibility(View.INVISIBLE);

            } else if (result.equalsIgnoreCase("first")) {
                Intent intent = new Intent(LoginWithEmailActivity.this, AddingFriendsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                fromSessionEmail = login_id;
                fromSessionFirstFlag = true;
                fromSessionFlag = true;

                saveSession();
                Toast.makeText(getApplicationContext(), "Welcome to Switch", Toast.LENGTH_LONG).show();
                startActivity(intent);

            } else if (result.equalsIgnoreCase("isFacebookID")) {
                Toast.makeText(getApplicationContext(), "Facebook ID로 등록이 되어있습니다. Facebook으로 로그인해주세요.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginWithEmailActivity.this, loginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                startActivity(intent);

            } else if (result.equalsIgnoreCase("invalid_id")) {
                Toast.makeText(getApplicationContext(), "등록된 아이디가 아닙니다.", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("need_verify")) {
                verifyDialog("인증메일을 보냈습니다.\n메일을 인증해주세요.");
                Log.d("TAG", "--------------->user email && name--------------------->" + login_id);
                new AsyncVerifyEmail().execute(login_id);

            } else {
                myJSON = result;
                if (!myJSON.contentEquals("")) {
                    try {
                        setSession(myJSON);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }

            }
        }

    }


    public void setSession(String json) throws JSONException {

        JSONObject jsonObject = new JSONObject(json);
        JSONArray jArry = jsonObject.getJSONArray("json");
        JSONObject getUserInfo = jArry.getJSONObject(0);

        fromSessionName = getUserInfo.getString("name");
        fromSessionMobileNum = getUserInfo.getString("mobile");
        fromSessionStatusMessage = getUserInfo.getString("msg");

        Log.d("TAG", "NAME from Server---------->" + fromSessionName);
        Log.d("TAG", "mobile from Server------->" + fromSessionMobileNum);

        if (!fromSessionName.contentEquals("")) {

            profile_login_name_text.setText(fromSessionName);
            profile_login_name_text.setVisibility(View.VISIBLE);

        }


    }

    public void readyToLogin() {

        if (idCheckedFlag) {

            if (pswdCheckedFlag) {
                isReadyToLogin = true;
                login_btn_with_email.setBackgroundResource(R.drawable.rounded_yellow);
                login_btn_with_email.setText("로그인");

            } else {

                login_btn_with_email.setText("비밀번호를 입력해주세요.");

            }

        } else {
            login_btn_with_email.setText("아이디를 입력해주세요.");
        }

    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public void verifyDialog(String msg) {
        verifyDialog = new Dialog(this);
        verifyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        verifyDialog.setCancelable(false);
        verifyDialog.setContentView(R.layout.custom_dialog_need_verify);
        verify_dialog_text = (TextView) verifyDialog.findViewById(R.id.text_need_verify);
        verify_dialog_confirm = (Button) verifyDialog.findViewById(R.id.btn_dialog_confirm);
        verify_dialog_text.setText(msg);
        verify_dialog_confirm.setOnClickListener(this);
        verifyDialog.show();
    }


    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();

    }


    private class AsyncVerifyEmail extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        Uri.Builder builder;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://115.71.232.209/client/verify_email.php");

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

                builder = new Uri.Builder()

                        .appendQueryParameter("_email", params[0]);
//                        .appendQueryParameter("_mail", params[1])
//                        .appendQueryParameter("_status", params[2])
//                        .appendQueryParameter("_img", params[3]);


                Log.d("TAG", "User mail------------>" + login_id);
//                Log.d("TAG", "User_name----------->" + fromSessionName);
//                Log.d("TAG", "Status Message-------------->" + fromSessionStatusMessage);
//                Log.d("TAG", "Image File ------> " + encodedProfileImag);

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
                    Log.d("TAG", "response---------------->true" + response_code);
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

            if (result.equalsIgnoreCase("fail")) {

                Toast.makeText(LoginWithEmailActivity.this, "이메일 전송 안됨", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("sent")) {

                Toast.makeText(LoginWithEmailActivity.this, "이메일 전송완료!!", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getApplicationContext(), "Critical Error" + result, Toast.LENGTH_LONG).show();
                Log.d("TAG", "ERRORR------------------>" + result);
            }
        }

    }


}
