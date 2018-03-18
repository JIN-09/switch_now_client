package kr.co.switchnow.switch_now_client.Activity;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.switchnow.switch_now_client.Adapter.ViewPagerAdapter;
import kr.co.switchnow.switch_now_client.Fragment.BaseFragment;
import kr.co.switchnow.switch_now_client.Fragment.BlockListFragment;
import kr.co.switchnow.switch_now_client.Fragment.FriendListFragment;
import kr.co.switchnow.switch_now_client.R;


public class ManageFriendsActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    TextView manage_friend_title_txt;
    public ViewPager mViewPager;
    private TabLayout mTabLayout;
    public ViewPagerAdapter mViewPagerAdapter;
    FriendListFragment friendListFragment;
    BlockListFragment blockListFragment;
    ArrayList<Fragment> fragmentList;
    ArrayList<String> fragmentTitleList;
    static int friendList_Counter;
    static int blockList_Counter;

//    String[] tabTitle = {"친구목!!록", "차단목1!~록"};
//    int[] ContentsNum;

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
        getSupportActionBar().setCustomView(R.layout.manage_friend_title);
        setContentView(R.layout.activity_manage_friends);


        manage_friend_title_txt = (TextView) findViewById(R.id.manage_friend_title_txt);
        Typeface face = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            face = getResources().getFont(R.font.enorbit);
        }else{
            face = Typeface.createFromAsset(getAssets(), "enotbit.TTF");
        }
        manage_friend_title_txt.setTypeface(face);

        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0);
//
//        ContentsNum = new int[2];
//        ContentsNum[0] = friendList_Counter = 2;
//        ContentsNum[1] = blockList_Counter = 3;
//        fragmentList = new ArrayList<>();
//        fragmentTitleList = new ArrayList<>();
        friendListFragment = new FriendListFragment();
        blockListFragment = new BlockListFragment();

        setUpViewPager(mViewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);


        mTabLayout.setupWithViewPager(mViewPager);

        setupTabs();
        mViewPager.addOnPageChangeListener(this);

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

        ManageFriendsActivity.this.finish();
    }


    public void onResume() {
        super.onResume();
        mViewPagerAdapter.notifyDataSetChanged();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mViewPager.setCurrentItem(position, false);
        mTabLayout.getTabAt(position).select();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setUpViewPager(ViewPager viewPager){
        Log.d("TAG","setUPViewPager()");
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addTabPage(friendListFragment);
        mViewPagerAdapter.addTabPage(blockListFragment);

        mViewPager.setAdapter(mViewPagerAdapter);
    }


    public void setCustomTab(int tabPosition, BaseFragment baseFragment, int layout){

        View customView;
        TextView fragTitle;
        TextView fragContentCounter;

        String itemFragTitle = baseFragment.getFragmentName();
        String itemFragContentCounter = baseFragment.getFragmentContentsCounter();


        customView = getLayoutInflater().inflate(layout, null);
        fragTitle = (TextView) customView.findViewById(R.id.tab_titleFor);
        fragContentCounter = (TextView) customView.findViewById(R.id.tab_countFor);

        fragTitle.setText(itemFragTitle);
        fragContentCounter.setText(itemFragContentCounter);
        mTabLayout.getTabAt(tabPosition).setCustomView(customView);

    }

    public void setupTabs(){

        LinearLayout tabStrip = (LinearLayout) mTabLayout.getChildAt(0);

        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            mTabLayout.getTabAt(i).setCustomView(null);
        }

        for (int i = 0; i<tabStrip.getChildCount(); i++){

            BaseFragment frag = ((BaseFragment) mViewPagerAdapter.getItem(i));
            setCustomTab(i, frag, R.layout.custom_tab);

        }

    }


}
