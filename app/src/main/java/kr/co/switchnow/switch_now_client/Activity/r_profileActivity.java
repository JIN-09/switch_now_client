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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
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

import de.hdodenhof.circleimageview.CircleImageView;
import kr.co.switchnow.switch_now_client.R;

import static kr.co.switchnow.switch_now_client.Activity.loginActivity.userSession;


public class r_profileActivity extends AppCompatActivity implements View.OnClickListener {


    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;
    static final int REQUEST_IMAGE_CROP = 3;
    protected Uri mCurrentPhotoPath;
    protected Uri originalPhotoPath;
    TextView myProfile_title;
    CircleImageView profile_img;
    ImageView mini_camera;
    TextView profile_name_text;
    EditText profile_name_edit;
    EditText profile_status_edit;
    Button save_profile_btn;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences session;
    String fromSessionName;
    String fromSessionStatusMessage;
    String fromSessionEmail;
    String fromSessionProfileImgLink;
    String fromSessionOriginalImgLink;
    String fromSessionMobileNum;
    Boolean fromSessionFirstFlag;
    Boolean fromSessionFlag;
    Boolean fromSessionProfileEdit;

    Dialog dialog = null;
    Boolean isEditProfileImg;
    Bitmap profile_img_file;
    TextView dialogText;
    Button yes_save;
    Button no_save;
    Dialog picSelector = null;
    Button from_library;
    Button take_pic;

    URL imageURL = null;
    String encodedProfileImag;

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
            getSupportActionBar().setCustomView(R.layout.profile_title);

        session = getSharedPreferences(userSession, Context.MODE_PRIVATE);
        mEditor = session.edit();
        fromSessionName = "";
        fromSessionStatusMessage = "";
        fromSessionProfileImgLink = "";
        fromSessionEmail = "";
        fromSessionMobileNum = "";
        profile_img_file = null;
        isEditProfileImg = false;
        fromSessionFirstFlag = false;
        fromSessionFlag = false;
        fromSessionProfileEdit = false;

        loadDataFromSession();



        setContentView(R.layout.activity_r_profile);

        myProfile_title = (TextView) findViewById(R.id.mytext);
        Typeface face = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            face = getResources().getFont(R.font.enorbit);
        }else{
            face = Typeface.createFromAsset(getAssets(), "enotbit.TTF");
        }
        myProfile_title.setTypeface(face);

        profile_img = (CircleImageView) findViewById(R.id.profile_image);
        mini_camera = (ImageView) findViewById(R.id.mini_camera_image);
        profile_name_text = (TextView) findViewById(R.id.profile_name_text);
        profile_name_edit = (EditText) findViewById(R.id.profile_name_edit);
        profile_status_edit = (EditText) findViewById(R.id.profile_status_edit);
        save_profile_btn = (Button) findViewById(R.id.save_profile_btn);
        save_profile_btn.setOnClickListener(this);
        profile_img.setOnClickListener(this);

        if (!fromSessionProfileImgLink.contentEquals("") && fromSessionProfileEdit == false) {

            Log.d("페북 이미지 URL or 서버 URL", fromSessionProfileImgLink);
            Picasso.with(getApplicationContext()).load(fromSessionProfileImgLink).memoryPolicy(MemoryPolicy.NO_CACHE ).networkPolicy(NetworkPolicy.NO_CACHE).into(profile_img);

        } else if (!fromSessionProfileImgLink.contentEquals("") && fromSessionProfileEdit == true) {

            Log.d("서버 URL------------>", fromSessionProfileImgLink);
            Picasso.with(getApplicationContext()).load(fromSessionProfileImgLink).memoryPolicy(MemoryPolicy.NO_CACHE ).networkPolicy(NetworkPolicy.NO_CACHE).into(profile_img);
        }


        profile_name_text.setText(fromSessionName);

        profile_name_edit.setText(fromSessionName);
        if (fromSessionStatusMessage != null) {
            profile_status_edit.setText(fromSessionStatusMessage);
        }


        mEditor.clear();
    }


    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.save_profile_btn: {

                saveDataToServer();

                break;
            }


            case R.id.profile_image: {

                picSelectorDialog();

                break;
            }

            case R.id.btn_dialog_save_yes: {
                dialog.dismiss();
                saveDataToServer();
                break;
            }

            case R.id.btn_dialog_save_no: {

                dialog.dismiss();
                isEditProfileImg = false;
                r_profileActivity.this.finish();
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


    public void onBackPressed() {

        saveDialog("프로필을 저장하시겠습니까?");

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


    public void saveDialog(String msg) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_save);
        dialogText = (TextView) dialog.findViewById(R.id.text_dialog_save);
        dialogText.setText(msg);
        yes_save = (Button) dialog.findViewById(R.id.btn_dialog_save_yes);
        no_save = (Button) dialog.findViewById(R.id.btn_dialog_save_no);
        yes_save.setOnClickListener(this);
        no_save.setOnClickListener(this);
        dialog.show();
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


    public void saveDataToServer() {
        fromSessionName = profile_name_edit.getText().toString();
        fromSessionStatusMessage = profile_status_edit.getText().toString();
        saveSession();
        if (isEditProfileImg) {
            new AsyncUpdateProfile().execute(fromSessionName, fromSessionEmail, fromSessionStatusMessage, encodedProfileImag);
        } else {
            new AsyncUpdateProfile().execute(fromSessionName, fromSessionEmail, fromSessionStatusMessage);
        }
    }


    private class AsyncUpdateProfile extends AsyncTask<String, String, String> {
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
                url = new URL("http://115.71.232.209/client/update_profile.php");

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
                if (isEditProfileImg) {
                    builder = new Uri.Builder()

                            .appendQueryParameter("_name", params[0])
                            .appendQueryParameter("_mail", params[1])
                            .appendQueryParameter("_status", params[2])
                            .appendQueryParameter("_img'", params[3]);

                } else {
                    builder = new Uri.Builder()

                            .appendQueryParameter("_name", params[0])
                            .appendQueryParameter("_mail", params[1])
                            .appendQueryParameter("_status", params[2]);

                }
                Log.d("TAG", "User mail------------>" + fromSessionEmail);
                Log.d("TAG", "User_name----------->" + fromSessionName);
                Log.d("TAG", "Status Message-------------->" + fromSessionStatusMessage);
                Log.d("TAG", "Image File ------> " + encodedProfileImag);

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

            if (result.equalsIgnoreCase("updated")) {
                Toast.makeText(r_profileActivity.this, "저장 완료!", Toast.LENGTH_LONG).show();


                saveSession();
                finish();


            } else if (result.equalsIgnoreCase("updated_with_img")) {

                try {
                    imageURL = new URL("http://115.71.232.209/client/profile/" + fromSessionEmail + ".png");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Log.d("TAG", "image URL-------------->" + imageURL);
                fromSessionProfileImgLink = String.valueOf(imageURL);
                Picasso.with(getApplicationContext()).load(fromSessionProfileImgLink).memoryPolicy(MemoryPolicy.NO_CACHE ).networkPolicy(NetworkPolicy.NO_CACHE).into(profile_img);

                saveSession();
                Toast.makeText(r_profileActivity.this, "저장 완료!!", Toast.LENGTH_LONG).show();



                finish();


            } else {
                Toast.makeText(getApplicationContext(), "Critical Error", Toast.LENGTH_LONG).show();
            }
        }

    }


    public void saveSession() {
        mEditor.clear();
        fromSessionName = profile_name_edit.getText().toString();
        fromSessionStatusMessage = profile_status_edit.getText().toString();

        if (isEditProfileImg) {

            fromSessionProfileEdit = isEditProfileImg;
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

    public void loadDataFromSession() {

        fromSessionProfileImgLink = session.getString("profile_img_link", "");
        fromSessionStatusMessage = session.getString("userStatusMessage", "");
        fromSessionName = session.getString("userName", "");
        fromSessionMobileNum = session.getString("userMobile", "");
        fromSessionEmail = session.getString("user_Fb_id", "");
        fromSessionFirstFlag = session.getBoolean("isFirstLogin", false);
        fromSessionFlag = session.getBoolean("Session", false);
        fromSessionProfileEdit = session.getBoolean("profile_edit_flag", false);

    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void onPause() {
        super.onPause();
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
        cropPictureIntent.putExtra("outputX", profile_img.getWidth());
        cropPictureIntent.putExtra("outputY", profile_img.getHeight());
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
                Picasso.with(this).load(mCurrentPhotoPath).into(profile_img);
                isEditProfileImg = true;

                encodedProfileImag = getStringImage(profile_img_file);

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
                options.outHeight = profile_img.getHeight();
                options.outWidth = profile_img.getWidth();
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
