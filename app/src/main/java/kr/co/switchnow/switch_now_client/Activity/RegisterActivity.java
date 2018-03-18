package kr.co.switchnow.switch_now_client.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.co.switchnow.switch_now_client.R;
import kr.co.switchnow.switch_now_client.Util.CustomDialog;

import static kr.co.switchnow.switch_now_client.Activity.loginActivity.userSession;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, View.OnFocusChangeListener, TextWatcher {


    private SharedPreferences.Editor mEditor;
    private SharedPreferences session;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;
    static final int REQUEST_IMAGE_CROP = 3;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    protected Uri mCurrentPhotoPath;
    protected Uri originalPhotoPath;

    TextView register_title;
    CustomDialog alert;
    CircleImageView register_profile_img;
    EditText register_id_edit;
    EditText register_pswd_edit;
    EditText register_pswdConf_edit;
    EditText register_userName_edit;
    EditText register_mobile_edit;
    RadioGroup genderGroup;


    Button register_btn;

    TextView register_id_disp;
    TextView register_pswd_disp;
    TextView register_pswdConf_disp;
    TextView register_userName_disp;
    TextView register_mobile_disp;
    TextView register_gender_disp;

    TextView register_id_check;
    TextView register_pswd_check;
    TextView register_pswdConf_check;
    TextView register_mobile_check;
    TextView register_userName_check;
    TextView register_gender_check;

    Boolean isIdChecked;
    Boolean isPswdChecked;
    Boolean isPswdConfChecked;
    Boolean isNameChecked;
    Boolean isMobileChecked;
    Boolean isGenderChecked;
    Boolean isProfilePicChecked;
    Boolean isReadyToRegister;

    String register_id;
    String register_pswd;
    String register_pswdConf;
    String register_userName;
    String register_mobileNum;
    String register_gender;

    Dialog picSelector = null;
    Button from_library;
    Button take_pic;

    Bitmap profile_img_file;

    String fromSessionName;
    String fromSessionMobileNum;
    String fromSessionStatusMessage;
    String fromSessionEmail;
    String fromSessionProfileImgLink;
    String fromSessionOriginalImgLink;
    Boolean fromSessionFirstFlag;
    Boolean fromSessionFlag;
    Boolean fromSessionProfileEdit;

    Boolean isEditProfileImg;

    String isCheckIdFromServer;
    String isCheckMobileFromServer;
    String encodedProfileImag;
    URL imageURL = null;


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
        getSupportActionBar().setCustomView(R.layout.register_title);


        session = getSharedPreferences(userSession, Context.MODE_PRIVATE);
        mEditor = session.edit();


        fromSessionName = "";
        fromSessionStatusMessage = "";
        fromSessionProfileImgLink = "";
        fromSessionMobileNum = "";
        fromSessionEmail = "";
        profile_img_file = null;
        isEditProfileImg = false;
        fromSessionFirstFlag = false;
        fromSessionFlag = false;
        fromSessionProfileEdit = false;

        isCheckIdFromServer = "";
        isCheckMobileFromServer = "";
        encodedProfileImag = "";

        alert = null;

        setContentView(R.layout.activity_register);
        register_title = (TextView) findViewById(R.id.regiter_title_bar);
        Typeface face = Typeface.createFromAsset(getAssets(), "ENORBITB.TTF");
        register_title.setTypeface(face);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        register_profile_img = (CircleImageView) findViewById(R.id.profile_img_register);
        register_id_edit = (EditText) findViewById(R.id.register_id_edit);
        register_pswd_edit = (EditText) findViewById(R.id.register_pswd_edit);
        register_pswdConf_edit = (EditText) findViewById(R.id.register_pswdConf_edit);
        register_userName_edit = (EditText) findViewById(R.id.register_userName_edit);
        register_mobile_edit = (EditText) findViewById(R.id.register_mobile_edit);
        genderGroup = (RadioGroup) findViewById(R.id.register_gender_radioButton_group);
        genderGroup.setOnCheckedChangeListener(this);

        register_btn = (Button) findViewById(R.id.register_btn);

        register_btn.setOnClickListener(this);
        register_profile_img.setOnClickListener(this);

        register_id_edit.setOnFocusChangeListener(this);
        register_pswd_edit.setOnFocusChangeListener(this);
        register_pswdConf_edit.setOnFocusChangeListener(this);
//        register_userName_edit.setOnFocusChangeListener(this);
        register_mobile_edit.setOnFocusChangeListener(this);
        register_userName_edit.addTextChangedListener(this);

        register_id_disp = (TextView) findViewById(R.id.register_id_disp);
        register_pswd_disp = (TextView) findViewById(R.id.register_pswd_disp);
        register_pswdConf_disp = (TextView) findViewById(R.id.register_pswdConf_disp);
        register_userName_disp = (TextView) findViewById(R.id.register_userName_disp);
        register_mobile_disp = (TextView) findViewById(R.id.register_mobile_disp);
        register_gender_disp = (TextView) findViewById(R.id.register_gender_disp);


        register_id_check = (TextView) findViewById(R.id.register_id_checked);
        register_pswd_check = (TextView) findViewById(R.id.register_pswd_checked);
        register_pswdConf_check = (TextView) findViewById(R.id.register_pswdConf_checked);
        register_mobile_check = (TextView) findViewById(R.id.register_mobile_checked);
        register_userName_check = (TextView) findViewById(R.id.register_userName_checked);
        register_gender_check = (TextView) findViewById(R.id.register_gender_checked);


        register_id = "";
        register_pswd = "";
        register_pswdConf = "";
        register_userName = "";
        register_mobileNum = "";
        register_gender = "";


        register_mobileNum = tm.getLine1Number();
        register_mobile_edit.setText(register_mobileNum);


        isIdChecked = false;
        isPswdChecked = false;
        isPswdConfChecked = false;
        isNameChecked = false;
        isMobileChecked = false;
        isGenderChecked = false;
        isProfilePicChecked = false;
        isReadyToRegister = false;


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


    public void saveSession() {
        mEditor.clear();
        fromSessionName = register_userName_edit.getText().toString();

        if (isEditProfileImg) {
            fromSessionProfileImgLink = mCurrentPhotoPath.toString();
            fromSessionProfileEdit = isEditProfileImg;
        }
        if (originalPhotoPath != null) {
            fromSessionProfileImgLink = originalPhotoPath.toString();
        }

        mEditor.putString("user_Fb_id", fromSessionEmail);
        mEditor.putString("userName", fromSessionName);
        mEditor.putString("userMobile", fromSessionMobileNum);
        mEditor.putString("userStatusMessage", fromSessionStatusMessage);
        mEditor.putString("profile_img_link", fromSessionProfileImgLink);
        mEditor.putString("original_img_link", fromSessionOriginalImgLink);
        mEditor.putBoolean("isFirstLogin", fromSessionFirstFlag);
        mEditor.putBoolean("Session", fromSessionFlag);
        mEditor.putBoolean("profile_edit_flag", fromSessionProfileEdit);

        mEditor.commit();
    }


    public void onBackPressed() {
        CustomDialog alert = new CustomDialog();
        alert.exitDialog(this, "회원가입을 종료하시겠습니까?");


    }

    public void readyToRegister() {

        if (isIdChecked) {

            if (isPswdChecked) {

                if (isPswdConfChecked) {

                    if (isMobileChecked) {

                        if (isNameChecked) {

                            if (isGenderChecked) {
                                if (isProfilePicChecked) {
                                    isReadyToRegister = true;
                                    register_btn.setBackgroundResource(R.drawable.rounded_yellow);
                                    register_btn.setText("가입하기");
                                } else {
                                    register_btn.setText("프로필 사진을 등록해주세요.");
                                }

                            } else {

                                register_btn.setText("성별을 선택해주세요.");
                            }

                        } else {

                            register_btn.setText("이름을 입력해주세요.");

                        }

                    } else {

                        register_btn.setText("단말기 번호를 입력해주세요.");
                    }

                } else {

                    register_btn.setText("비밀번호 확인을 해주세요.");
                }

            } else {
                register_btn.setText("비밀번호를 입력해주세요.");
            }

        } else {
            register_btn.setText("아이디를 입력해주세요.");
        }

    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.register_btn: {
                readyToRegister();
                if (isReadyToRegister) {
                    new AsyncRegister().execute(register_id, register_pswd, register_userName, register_mobileNum, register_gender, isCheckIdFromServer, isCheckMobileFromServer, encodedProfileImag);
                } else {

                }

                break;
            }

            case R.id.profile_img_register: {

                picSelectorDialog();

                break;
            }

            case R.id.btn_dialog_pic_library: {
                picSelector.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                break;
            }

            case R.id.btn_dialog_pic_take: {
                picSelector.dismiss();
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                mCurrentPhotoPath = createImageFile_();

                if (intent.resolveActivity(getPackageManager()) != null) {

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoPath);
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }

                break;
            }

        }

    }


    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

        switch (checkedId) {


            case R.id.register_gender_male: {

                register_gender = ((RadioButton) findViewById(genderGroup.getCheckedRadioButtonId())).getText().toString();
                isGenderChecked = true;
                register_gender_check.setVisibility(View.VISIBLE);
                readyToRegister();
                break;
            }

            case R.id.register_gender_female: {

                register_gender = ((RadioButton) findViewById(genderGroup.getCheckedRadioButtonId())).getText().toString();
                isGenderChecked = true;
                register_gender_check.setVisibility(View.VISIBLE);
                readyToRegister();
                break;
            }

        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        switch (v.getId()) {

            case R.id.register_id_edit: {

                if (!hasFocus) {

                    register_id = register_id_edit.getText().toString();
                    if (isEmailValid(register_id)) {
                        isCheckIdFromServer = "pre";
                        new AsyncRegister().execute(register_id, register_pswd, register_userName, register_mobileNum, register_gender, isCheckIdFromServer, isCheckMobileFromServer, encodedProfileImag);

                    } else {

                        Toast.makeText(this, "E-mail 형식을 다시 한번 확인해주세요.", Toast.LENGTH_LONG).show();
                    }
                    readyToRegister();
                }

                break;
            }

            case R.id.register_pswd_edit: {

                if (!hasFocus) {

                    register_pswd = register_pswd_edit.getText().toString();
                    if (register_pswd.length() >= 6) {
                        register_pswd_check.setVisibility(View.VISIBLE);
                        isPswdChecked = true;


                    } else {
                        Toast.makeText(this, "비밀번호는 6자 이상이어야만 합니다.", Toast.LENGTH_LONG).show();
                    }

                    readyToRegister();
                }

                break;
            }

            case R.id.register_pswdConf_edit: {

                if (!hasFocus) {
                    register_pswdConf = register_pswdConf_edit.getText().toString();
                    if (register_pswd.contentEquals(register_pswdConf) && (!register_pswdConf.contentEquals(""))) {
                        register_pswdConf_check.setVisibility(View.VISIBLE);
                        isPswdConfChecked = true;

                    } else {
                        Toast.makeText(this, "비밀번호를 다시 한번 확인해주세요.", Toast.LENGTH_LONG).show();
                    }
                    readyToRegister();

                }


                break;
            }

//            case R.id.register_userName_edit: {
//
//                if (!hasFocus) {
//                    register_userName = register_userName_edit.getText().toString();
//
//                    if (!register_userName.contentEquals("")) {
//                        register_userName_check.setVisibility(View.VISIBLE);
//                        isNameChecked = true;
//
//                    } else {
//                        register_userName_check.setVisibility(View.INVISIBLE);
//                        isNameChecked = false;
//                        Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_LONG).show();
//                    }
//
//
//                } else {
//
//                }
//                readyToRegister();
//
//                break;
//            }

            case R.id.register_mobile_edit: {

                if (!hasFocus) {
                    isCheckMobileFromServer = "pre";
                    register_mobileNum = register_mobile_edit.getText().toString();
                    fromSessionMobileNum = register_mobileNum;
                    new AsyncRegister().execute(register_id, register_pswd, register_userName, register_mobileNum, register_gender, isCheckIdFromServer, isCheckMobileFromServer, encodedProfileImag);


                    readyToRegister();
                }


                break;
            }

        }

    }

    public void picSelectorDialog() {
        picSelector = new Dialog(this);
        picSelector.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picSelector.setContentView(R.layout.custom_dialog_pic_seletcor);
        from_library = (Button) picSelector.findViewById(R.id.btn_dialog_pic_library);
        take_pic = (Button) picSelector.findViewById(R.id.btn_dialog_pic_take);
        from_library.setOnClickListener(this);
        take_pic.setOnClickListener(this);
        picSelector.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        register_userName = register_userName_edit.getText().toString();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!register_userName.contentEquals("")) {
            register_userName_check.setVisibility(View.VISIBLE);
            isNameChecked = true;

        } else {
            register_userName_check.setVisibility(View.INVISIBLE);
            isNameChecked = false;
            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_LONG).show();
        }

        readyToRegister();
    }


    private class AsyncRegister extends AsyncTask<String, String, String> {
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
                url = new URL("http://115.71.232.209/client/register_with_email.php");

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
                        .appendQueryParameter("_name", params[2])
                        .appendQueryParameter("_mobile", params[3])
                        .appendQueryParameter("_gender", params[4])
                        .appendQueryParameter("_checkID", params[5])
                        .appendQueryParameter("_checkMobile", params[6])
                        .appendQueryParameter("_image", params[7]);

                Log.d("TAG", "이메일 값-------------->" + register_id);
                Log.d("TAG", "pswd 값-------------->" + register_pswd);
                Log.d("TAG", "name 값-------------->" + register_userName);
                Log.d("TAG", "mobile -------------->" + register_mobileNum);
                Log.d("TAG", "gender -------------->" + register_gender);
                Log.d("TAG", "checkID -------------->" + isCheckIdFromServer);
                Log.d("TAG", "checkMobile -------------->" + isCheckMobileFromServer);
                Log.d("TAG", "Image File --------> " + encodedProfileImag);
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
                Toast.makeText(getApplicationContext(), "사용가능한 ID입니다.", Toast.LENGTH_LONG).show();
                isCheckIdFromServer = "post";
                isIdChecked = true;
                register_id_check.setVisibility(View.VISIBLE);


            } else if (result.equalsIgnoreCase("duplicate_id")) {
                Toast.makeText(getApplicationContext(), "이미등록된 ID입니다.", Toast.LENGTH_LONG).show();


            } else if (result.equalsIgnoreCase("usedMobile")) {
                Toast.makeText(getApplicationContext(), "이미등록된 단말기 번호입니다.", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("available_mobile")) {
                Toast.makeText(getApplicationContext(), "사용가능한 단말기 번호입니다.", Toast.LENGTH_LONG).show();
                isCheckMobileFromServer = "post";
                isMobileChecked = true;
                register_mobile_check.setVisibility(View.VISIBLE);

            } else if (result.equalsIgnoreCase("register")) {
                Intent intent = new Intent(RegisterActivity.this, LoginWithEmailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                Toast.makeText(getApplicationContext(), "Registered! Please Login", Toast.LENGTH_LONG).show();
                fromSessionEmail = register_id;
                fromSessionFirstFlag = false;
                fromSessionFlag = false;
                fromSessionProfileEdit = true;
                saveSession();
                startActivity(intent);

            } else if (result.equalsIgnoreCase("false")) {
                Toast.makeText(getApplicationContext(), "Data pass check again.", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getApplicationContext(), "Critical Error", Toast.LENGTH_LONG).show();
            }
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

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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


    private Uri createImageFile_() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        //저장 위치는 Android/data/앱패키지/picture/
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        Uri uri = Uri.fromFile(new File(storageDir, imageFileName));
        mCurrentPhotoPath = uri;
        return uri;
    }

    public void saveExifFile(Bitmap imageBitmap, String savePath) {
        FileOutputStream fos = null;
        File saveFile = null;

        try {
            saveFile = new File(savePath);
            fos = new FileOutputStream(saveFile);
            //원본형태를 유지해서 이미지 저장
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (FileNotFoundException e) {


        }
    }

    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees) {
        Bitmap retBitmap = bitmap;

        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth(), (float) bitmap.getHeight());

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    retBitmap = converted;
                    bitmap.recycle();
                    bitmap = null;
                }
            } catch (OutOfMemoryError ex) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return retBitmap;
    }

    public File getImageFile(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(uri, projection, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");

        if (mCursor == null || mCursor.getCount() < 1) {
            return null;
        }

        int idxColumn = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(idxColumn);


        return new File(path);
    }

    public boolean createTempFile(File copyFile, File oriFile) {
        boolean result = true;
        try {
            InputStream inputStream = new FileInputStream(oriFile);
            OutputStream outputStream = new FileOutputStream(copyFile);

            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            result = false;
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    private void cropImage() {
        Intent cropPictureIntent = new Intent("com.android.camera.action.CROP");
        cropPictureIntent.setDataAndType(mCurrentPhotoPath, "image/*");

        // Crop한 이미지를 저장할 Path
        cropPictureIntent.putExtra("output", mCurrentPhotoPath);

        //이미지 편집 크기 제한
        //crop box X and Y rate
        cropPictureIntent.putExtra("aspectX", 10);
        cropPictureIntent.putExtra("aspectY", 10);
        //indicate output X and Y
        cropPictureIntent.putExtra("outputX", register_profile_img.getWidth());
        cropPictureIntent.putExtra("outputY", register_profile_img.getHeight());
        cropPictureIntent.putExtra("scale", true);
        cropPictureIntent.putExtra("return-data", true);
        // Return Data를 사용하면 번들 용량 제한으로 크기가 큰 이미지는 넘겨 줄 수 없다.
        startActivityForResult(cropPictureIntent, REQUEST_IMAGE_CROP);

    }


    protected void onActivityResult(int request_code, int result_code, Intent data) {
        super.onActivityResult(request_code, result_code, data);

        if (result_code != RESULT_OK) {
            return;
        }
        switch (request_code) {
            case REQUEST_IMAGE_CROP: {
                String cropImagePath = mCurrentPhotoPath.getPath();

                profile_img_file = BitmapFactory.decodeFile(cropImagePath);
                Picasso.with(this).load(mCurrentPhotoPath).into(register_profile_img);

                try {
                    imageURL = new URL("http://115.71.232.209/client/profile/" + register_id + ".png");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Log.d("TAG", "image URL-------------->" + imageURL);
                fromSessionProfileImgLink = String.valueOf(imageURL);

                isEditProfileImg = true;
                Boolean isBitmap = true;
                readyToRegister();

                if (isBitmap) {
                    encodedProfileImag = getStringImage(profile_img_file);
                    isProfilePicChecked = true;
                } else {
                    Log.d("TAG", "프로필 사진오류-" + mCurrentPhotoPath);
                }


                break;
            }
            case REQUEST_IMAGE_CAPTURE: {


                Uri getImagePath = data.getData();
                originalPhotoPath = getImagePath;
                File oriFile = getImageFile(getImagePath);
                mCurrentPhotoPath = createImageFile_();
                File copyFile = new File(mCurrentPhotoPath.getPath());
                createTempFile(copyFile, oriFile);
                cropImage();
                break;

            }
            case REQUEST_TAKE_PHOTO: {


                String photoPath = mCurrentPhotoPath.getPath();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                options.outHeight = register_profile_img.getHeight();
                options.outWidth = register_profile_img.getWidth();
                originalPhotoPath = Uri.parse(photoPath);
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
//               try{
                // 기본 카메라 모듈을 이용해 촬영할 경우 가끔씩 이미지가
                // 회전되어 출력되는 경우가 존재하여
                // 이미지를 상황에 맞게 회전시킨다
                try {
                    ExifInterface exif = new ExifInterface(photoPath);
                    int exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);

                    //회전된 이미지를 다시 회전시켜 정상 출력
                    bitmap = rotate(bitmap, exifDegree);

                    //회전시킨 이미지를 저장
                    saveExifFile(bitmap, photoPath);

                    //비트맵 메모리 반환
//                imageBitmap.recycle();
                } catch (IOException e) {
                    e.getStackTrace();
                }
                saveExifFile(bitmap, photoPath);
//
                //이미지편집 호출
                cropImage();

                break;

            }
        }

    }


}
