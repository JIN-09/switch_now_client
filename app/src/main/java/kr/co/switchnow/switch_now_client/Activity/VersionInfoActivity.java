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

public class VersionInfoActivity extends AppCompatActivity {

    BaseActivity baseActivity;
    TextView version_info_title;


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
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(R.layout.version_info_title);


        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.switchLime), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        version_info_title = (TextView) findViewById(R.id.ver_info_txt_title);
        Typeface face = Typeface.createFromAsset(getAssets(), "ENORBITB.TTF");
        version_info_title.setTypeface(face);
        baseActivity = new BaseActivity(this);
        setContentView(R.layout.activity_version_info);
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
