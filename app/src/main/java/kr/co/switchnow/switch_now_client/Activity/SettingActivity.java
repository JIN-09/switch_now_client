package kr.co.switchnow.switch_now_client.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.switchnow.switch_now_client.R;


public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    TextView setting_title;
    Dialog dialog = null;
    TextView dialogText;
    Button yes_save;
    Button no_save;
    BaseActivity baseActivity;
    TextView version_info;
    TextView advanced_setting_time;
    TextView advanced_setting_extension;
    TextView customer_support_terms_and_privacy;
    TextView customer_support_customer_opinion;
    TextView customer_support_customer_drop_out;


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
        getSupportActionBar().setCustomView(R.layout.setting_title);



        setting_title = (TextView) findViewById(R.id.setting_title_bar);
        Typeface face = Typeface.createFromAsset(getAssets(), "ENORBITB.TTF");
        setting_title.setTypeface(face);
        setContentView(R.layout.activity_setting);
        version_info = (TextView) findViewById(R.id.version_info);
        advanced_setting_time = (TextView) findViewById(R.id.advanced_setting_time);
        advanced_setting_extension = (TextView) findViewById(R.id.advanced_setting_extension);
        customer_support_terms_and_privacy = (TextView) findViewById(R.id.terms_and_privacy);
        customer_support_customer_opinion = (TextView) findViewById(R.id.customer_opinion);
        customer_support_customer_drop_out = (TextView) findViewById(R.id.customer_drop_out);


        version_info.setOnClickListener(this);
        advanced_setting_extension.setOnClickListener(this);
        advanced_setting_time.setOnClickListener(this);
        customer_support_customer_drop_out.setOnClickListener(this);
        customer_support_customer_opinion.setOnClickListener(this);
        customer_support_terms_and_privacy.setOnClickListener(this);


        baseActivity = new BaseActivity(this);
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

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_dialog_save_yes: {
                dialog.dismiss();
                Toast.makeText(this, "저장 완료!", Toast.LENGTH_LONG).show();

                finish();
                break;
            }

            case R.id.btn_dialog_save_no: {

                dialog.dismiss();
                finish();
                break;
            }

            case R.id.version_info: {

                Intent intent = new Intent(this, VersionInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                baseActivity.slideInFromRight(this, this.findViewById(android.R.id.content));

                break;
            }


            case R.id.advanced_setting_time: {

                Intent intent = new Intent(this, TimeSettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                baseActivity.slideInFromRight(this, this.findViewById(android.R.id.content));

                break;
            }

            case R.id.advanced_setting_extension: {
                Intent intent = new Intent(this, ExtensionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                baseActivity.slideInFromRight(this, this.findViewById(android.R.id.content));

                break;
            }

            case R.id.terms_and_privacy: {

                Intent intent = new Intent(this, PrivacyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                baseActivity.slideInFromRight(this, this.findViewById(android.R.id.content));

                break;
            }

            case R.id.customer_opinion: {

                Intent intent = new Intent(this, OpinionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                baseActivity.slideInFromRight(this, this.findViewById(android.R.id.content));

                break;
            }

            case R.id.customer_drop_out: {

                Intent intent = new Intent(this, DropOutActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                baseActivity.slideInFromRight(this, this.findViewById(android.R.id.content));

                break;
            }

        }
    }


    public void onBackPressed() {
//        saveDialog("설정을 저장하시겠습니까?");
        this.finish();
        Intent intent = new Intent(this, InteractionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        baseActivity.fadeOut(this, this.findViewById(android.R.id.content));

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


}
