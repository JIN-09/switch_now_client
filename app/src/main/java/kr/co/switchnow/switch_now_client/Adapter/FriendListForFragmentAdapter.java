package kr.co.switchnow.switch_now_client.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import kr.co.switchnow.switch_now_client.ADT.ListData;
import kr.co.switchnow.switch_now_client.Fragment.FriendListFragment;
import kr.co.switchnow.switch_now_client.R;

import static kr.co.switchnow.switch_now_client.Fragment.FriendListFragment.ffBlockList;
import static kr.co.switchnow.switch_now_client.Fragment.FriendListFragment.mFriendListForFragmentAdapter;


/**
 * Created by ceo on 2017-06-12.
 */

public class FriendListForFragmentAdapter extends BaseAdapter {

    static protected Context mContext = null;
    protected LayoutInflater minflater;
    static protected ArrayList<ListData> ffListData = new ArrayList<>();
    URL imageURL;


    public FriendListForFragmentAdapter(Context context, ArrayList<ListData> objects){
        super();
        this.mContext = context;
        this.ffListData = objects;
        minflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {


        return ffListData.size();
    }

    @Override
    public Object getItem(int position) {

        return ffListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static void dataChange(){

        mFriendListForFragmentAdapter.notifyDataSetChanged();

    }


    public void addItem(String email_id, String gender, String userName, String mobile_number, String status) {
        ListData ffData;
        ffData = new ListData();
        ffData.m_email_id = email_id;
        ffData.m_gender = gender;
        ffData.m_userName = userName;
        ffData.m_status = status;
        ffData.m_mobile_number = mobile_number;
        try {
            imageURL = new URL("http://115.71.232.209/client/profile/" + ffData.m_email_id + ".png");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "image URL----------------->" + imageURL);
        ffData.m_pic_URL = String.valueOf(imageURL);
        ffListData.add(ffData);
        Log.d("TAG", "Addinfo-----------email----->" + ffData.m_email_id);
        Log.d("TAG", "Addinfo-----------name--->" + ffData.m_userName);
        dataChange();

    }

    public void remove(int position){
        ffListData.remove(position);
        dataChange();
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FriendListFragment.fViewHolder fHolder = null;
        final ListData mData = ffListData.get(position);
        final FriendListFragment caller = new FriendListFragment();

        if(convertView == null){
            minflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = minflater.inflate(R.layout.friend_fragment_item, null);
            fHolder = new FriendListFragment.fViewHolder();
            fHolder.m_Profile = (ImageView) convertView.findViewById(R.id.profile_image_friend_fragment);
            fHolder.m_NameText = (TextView) convertView.findViewById(R.id.user_name_friend_fragment);
            fHolder.mShutDown_btn = (ImageView) convertView.findViewById(R.id.block_friend_btn);


            fHolder.mShutDown_btn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    int getPosition = (Integer) v.getTag();
                    ListData m_Data = ffListData.get(getPosition);
                    ffBlockList.add(m_Data.m_email_id);

                    remove(getPosition);

                    Log.d("TAG", "email _id --------------------------->" + m_Data.m_email_id);
                    Log.d("TAG", "FRIENDLIST--------------------------->" + ffBlockList.toString());
                    Log.d("TAG", "LISTVIEW DATA SIZE------------------->" + ffListData.size());
                    if(ffListData.size()==0 ){
                        caller.showMsg();
                    }


                }
            });

            convertView.setTag(fHolder);

        } else {

            fHolder = (FriendListFragment.fViewHolder) convertView.getTag();

        }

        Glide.with(mContext.getApplicationContext()).load(mData.m_pic_URL).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(fHolder.m_Profile);
        fHolder.m_NameText.setText(mData.m_userName);
        fHolder.mShutDown_btn.setTag(position);

        return convertView;
    }



}
