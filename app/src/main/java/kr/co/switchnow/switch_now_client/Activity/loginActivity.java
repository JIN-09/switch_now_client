package kr.co.switchnow.switch_now_client.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.stetho.Stetho;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import kr.co.switchnow.switch_now_client.R;


public class loginActivity extends Activity implements View.OnClickListener {


    private static final int REQUEST_READ_PHONE_STATE = 0;
    private static final int REQUEST_READ_CONTACT = 1;

//    private CallbackManager callbackManager;

    private String gender, full_name, email_id, mobileNum, profile_img_link, facebook_id, about;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    public static final String userSession = "userPrefs";
    public static int GET_DEVICE_NUMBER = 0;
    public static int GET_CONTACT_NUMBER = 0;
    public static int GET_FINE_LOCATION = 0;
    public static int GET_COARSE_LOCATION = 0;

    private SharedPreferences.Editor mEditor;
    private SharedPreferences session;
    private boolean isFirstLogin;
    private boolean sessionFlag;
    String tmpName;
    String tmpMobile;
    String tmpUserId;
    URL imageURL = null;
    String encodedImg;
    private Target loadtarget;
    Boolean tmpIsFirstLog;
    Bitmap profile_img_file;
    Button register_btn;
    Button login_with_email_btn;
//    LoginButton fbLoginBtn;
    ImageView fbLoginFake;

    TelephonyManager telephonManager;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int switchDarkgrey = getResources().getColor(R.color.switchDarkgrey);
        Stetho.initializeWithDefaults(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(switchDarkgrey);
        }

//        startActivity(new Intent(this, SplashActivity.class));
        session = getSharedPreferences(userSession, Context.MODE_PRIVATE);
        mEditor = session.edit();


        if (NetworkConnection() == false) {
            NotConnected_showAlert();
        }

        about = facebook_id = mobileNum = gender = profile_img_link = full_name = email_id = "";
        telephonManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        GET_DEVICE_NUMBER = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        GET_CONTACT_NUMBER = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        if (GET_DEVICE_NUMBER != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else if (GET_CONTACT_NUMBER != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACT);
        }

        loadSession();
        isFirstLogin = false;
        sessionFlag = false;
//        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);


        register_btn = (Button) findViewById(R.id.register_with_email_btn);
//        fbLoginFake = (ImageView) findViewById(R.id.fb_login_FAKE);
//        fbLoginFake.setOnClickListener(this);
//        fbLoginBtn = (LoginButton) findViewById(R.id.fb_login);



        register_btn.setPaintFlags(register_btn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        register_btn.setOnClickListener(this);
        login_with_email_btn = (Button) findViewById(R.id.login_with_email_btn);
        login_with_email_btn.setOnClickListener(this);

        mEditor.clear();
    }

//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_READ_PHONE_STATE:
//                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    mobileNum = telephonManager.getLine1Number();
//                }
//                break;
//            case REQUEST_READ_CONTACT: {
//
//
//                break;
//            }
//            default:
//                break;
//        }
//    }


    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.register_with_email_btn: {
                registerWithEmail();
                break;

            }

            case R.id.login_with_email_btn: {
                loginWithEmail();
                break;
            }


//            case R.id.fb_login_FAKE: {
//                fbLoginBtn.performClick();
//
//            }


//            case R.id.fb_login: {
//                facebookLoginOnClick();
//
//
//                break;
//
//            }

        }

    }


    public void saveSession() {
        mEditor.clear();
        mEditor.putString("userName", full_name);
        mEditor.putString("userMobile", mobileNum);
        mEditor.putString("user_Fb_id", email_id);
        mEditor.putString("profile_img_link", profile_img_link);
        mEditor.putBoolean("isFirstLogin", isFirstLogin);
        mEditor.putBoolean("Session", sessionFlag);
        mEditor.commit();
    }


    public void loadSession() {
        boolean tmpSessionFlag;

        tmpSessionFlag = session.getBoolean("Session", false);
        Log.d("TAG", "임시_세션 플래그 --- > " + tmpSessionFlag);

        if (tmpSessionFlag != false) {
            tmpName = session.getString("userName", "");
            tmpMobile = session.getString("userMobile", "");
            tmpUserId = session.getString("user_Fb_id", "");
            tmpIsFirstLog = session.getBoolean("isFirstLogin", false);
        }


    }

    public void registerWithEmail() {
        Intent intent = new Intent(loginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void loginWithEmail() {
        Intent intent = new Intent(loginActivity.this, LoginWithEmailActivity.class);
        startActivity(intent);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

//    public void facebookLoginOnClick() {
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        FacebookSdk.addLoggingBehavior(LoggingBehavior.GRAPH_API_DEBUG_INFO);
//        FacebookSdk.addLoggingBehavior(LoggingBehavior.DEVELOPER_ERRORS);
//        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
//        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_RAW_RESPONSES);
//        callbackManager = CallbackManager.Factory.create();

//        LoginManager.getInstance().logInWithReadPermissions(loginActivity.this,
//                Arrays.asList("public_profile", "email"));
//        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>(){
//
//            @Override
//            public void onSuccess(final LoginResult result) {
//
//                GraphRequest request = GraphRequest.newMeRequest(result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
//
//                    @Override
//                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        Log.d("TAG", "페이스북 오브젝트 결과---------->" + object.toString());
//                        Log.d("TAG", "페이스북 로그인 결과" + response.toString());
//                        Log.i("TAG", "AccessToken: " + result.getAccessToken().getToken());
//
//                        try {
//                            Log.d("TAG", "TEST---------------------------------_-_------->");
//                            email_id = object.getString("email");
//                            gender = object.getString("gender");
//                            full_name = object.getString("name");
//                            facebook_id = object.getString("id");
////                            about = object.getString("about");
//
//                            Log.d("TAG", "USER EMAIL------------------------->" + email_id);
//
//                            profile_img_link = "https://graph.facebook.com/" + facebook_id + "/picture?type=large";
//                            Log.d("TAG", "페이스북 이메일 -> " + email_id);
//                            Log.d("TAG", "페이스북 이름 -> " + full_name);
//                            Log.d("TAG", "페이스북 성별 -> " + gender);
//                            Log.d("TAG", "모바일 -> " + mobileNum);
//                            Log.d("TAG", "프로필 -> " + profile_img_link);
//                            Log.d("TAG", "about----->" + about);
//
//
//                            setResult(RESULT_OK);
////
//
//                            new DownloadImage().execute(profile_img_link);
//                            Log.d("TAG", "Encoded img---------------->" + encodedImg);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.d("TAG", "JSON OBJECT ERRR------------------------->" + e.toString());
//                        }
//
//                    }
//                });
////                Bundle parameters = new Bundle();
////                parameters.putString("fields", "id,name,email,gender,picture,birthday,location");
////                request.setParameters(parameters);
////
////                request.executeAsync();
//
//            }
//
////            @Override
////            public void onError(FacebookException error) {
////                Log.v("TAG", "------------------------------LoginActivity" + error.getCause().toString());
////                finish();
////            }
//
//            @Override
//            public void onCancel() {
//                Log.v("TAG", "LoginActivity--------------------->cancel");
//            }
//        });
//    }


//    private class AsyncLogin extends AsyncTask<String, String, String> {
//        ProgressDialog pdLoading = new ProgressDialog(loginActivity.this);
//        HttpURLConnection conn;
//        URL url = null;
//        Uri.Builder builder;
//
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//
//            try {
//
//                // Enter URL address where your php file resides
//                url = new URL("http://115.71.232.209/client/login.member.php");
//
//            } catch (MalformedURLException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                Log.d("TAG", "URL---------------->error" + url);
//                return "exception";
//            }
//            try {
//                // Setup HttpURLConnection class to send and receive data from php and mysql
//                conn = (HttpURLConnection) url.openConnection();
//                conn.setReadTimeout(READ_TIMEOUT);
//                conn.setConnectTimeout(CONNECTION_TIMEOUT);
//                conn.setRequestMethod("POST");
//
//                // setDoInput and setDoOutput method depict handling of both send and receive
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//                conn.setUseCaches(false);
//
//                // Append parameters to URL (full_name, profile_image, email_id, gender, mobileNum);
//
//                builder = new Uri.Builder()
//                        .appendQueryParameter("_name", params[0])
//                        .appendQueryParameter("_p_image", params[1])
//                        .appendQueryParameter("_email", params[2])
//                        .appendQueryParameter("_gender", params[3])
//                        .appendQueryParameter("_mobile", params[4]);
//
//
//                String query = builder.build().getEncodedQuery();
//
//                // Open connection for sending data
//                OutputStream os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//                writer.write(query);
//                writer.flush();
//                writer.close();
//                os.close();
//                conn.connect();
//
//            } catch (IOException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//                Log.d("TAG", "connection---------------->error" + url);
//                return "exception";
//            }
//
//            try {
//                int response_code = conn.getResponseCode();
//
//                // Check if successful connection made
//                if (response_code == HttpURLConnection.HTTP_OK) {
//
//                    // Read data sent from server
//                    InputStream input = conn.getInputStream();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//                    StringBuilder result = new StringBuilder();
//                    String line;
//                    Log.d("TAG", "response---------------->true" + response_code);
//                    while ((line = reader.readLine()) != null) {
//                        result.append(line);
//                    }
//
//                    // Pass data to onPostExecute method
//                    return (result.toString());
//
//                } else {
//
//                    return ("unsuccessful");
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                return "exception";
//            } finally {
//                conn.disconnect();
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//            //this method will be running on UI thread
//            try {
//                imageURL = new URL("http://115.71.232.209/client/profile/" + email_id + ".png");
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//
//            pdLoading.dismiss();
//            Log.d("TAG", "onPostExecute----------------->" + result);
//            if (result.equalsIgnoreCase("true")) {
//                Intent intent = new Intent(loginActivity.this, AddingFriendsActivity.class);
//
//                Toast.makeText(loginActivity.this, "Welcome to switch! " + full_name, Toast.LENGTH_LONG).show();
//                isFirstLogin = true;
//
//                sessionFlag = true;
//                profile_img_link = String.valueOf(imageURL);
//                saveSession();
//                startActivity(intent);
//                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//                loginActivity.this.finish();
//
//            } else if (result.equalsIgnoreCase("false")) {
//
//                // If username and password does not match display a error message
//                Toast.makeText(loginActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
//
//            } else if (result.equalsIgnoreCase("login")) {
//
//                Intent intent = new Intent(loginActivity.this, ReadyActivity.class);
//
//                Toast.makeText(loginActivity.this, "Welcom Back " + full_name, Toast.LENGTH_LONG).show();
//                isFirstLogin = false;
//                profile_img_link = String.valueOf(imageURL);
//                sessionFlag = true;
//                saveSession();
//                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//                startActivity(intent);
//                loginActivity.this.finish();
//            }
//        }
//
//    }


//    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//        }
//
//        @Override
//        protected Bitmap doInBackground(String... URL) {
//
//            String imageURL = URL[0];
//
//            Bitmap bitmap = null;
//            try {
//                // Download Image from URL
//                InputStream input = new java.net.URL(imageURL).openStream();
//                // Decode Bitmap
//                bitmap = BitmapFactory.decodeStream(input);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return bitmap;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            encodedImg = getStringImage(result);
//
//            new AsyncLogin().execute(full_name, encodedImg, email_id, gender, mobileNum);
//
//        }
//    }


    public void onResume(){
        super.onResume();

    }

    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();

    }


    private void NotConnected_showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity.this);
        builder.setTitle("네트워크 연결 오류");
        builder.setMessage("사용 가능한 무선네트워크가 없습니다.\n" + "먼저 무선네트워크 연결상태를 확인해 주세요.")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish(); // exit
                        //application 프로세스를 강제 종료
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private boolean NetworkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
        boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
        boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if ((isWifiAvailable && isWifiConnect) || (isMobileAvailable && isMobileConnect)) {
            return true;
        } else {
            return false;
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
        return encodedImage;
    }


}
