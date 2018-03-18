package kr.co.switchnow.switch_now_client.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kr.co.switchnow.switch_now_client.Fragment.BaseFragment;


/**
 * Created by ceo on 2017-04-30.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private final static String TAG = ViewPagerAdapter.class.getSimpleName();

    private final List<Fragment> mTabItems = new ArrayList<>();
    private final List<String> mTabTitle = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mTabItems.get(position);
    }

    @Override
    public int getCount() {
        return mTabItems.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitle.get(position);
    }

    public void addTabPage(BaseFragment fragment) {
        Log.d(TAG, "addTabPage() - Adding fragment: " + fragment.getFragmentName());

        mTabItems.add(fragment);
        String titleNCount = fragment.getFragmentName();
//                + " " + fragment.getFragmentContentsCounter();

        mTabTitle.add(titleNCount);
//        mTabTitle.add(fragment.getFragmentContentsCounter());
        notifyDataSetChanged();
    }

 }
