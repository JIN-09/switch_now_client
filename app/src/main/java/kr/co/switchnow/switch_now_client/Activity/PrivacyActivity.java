package kr.co.switchnow.switch_now_client.Activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import kr.co.switchnow.switch_now_client.R;


public class PrivacyActivity extends AppCompatActivity {

    TextView privacy_title_txt;
    BaseActivity baseActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int switchDarkgrey = getResources().getColor(R.color.switchDarkgrey);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(switchDarkgrey);
            getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.switchDarkgrey));
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(R.layout.privacy_title);
        setContentView(R.layout.activity_privacy);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.switchLime), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        privacy_title_txt = (TextView) findViewById(R.id.privacy_title_txt);
        Typeface face = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            face = getResources().getFont(R.font.enorbit);
        }else{
            face = Typeface.createFromAsset(getAssets(), "enotbit.TTF");
        }
        privacy_title_txt.setTypeface(face);



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


    public void onBackPressed(){
//        saveDialog("설정을 저장하시겠습니까?");
        baseActivity.finish();
        Intent intent = new Intent(this, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        baseActivity.slideOutToRight(this, this.findViewById(android.R.id.content));
    }


}
