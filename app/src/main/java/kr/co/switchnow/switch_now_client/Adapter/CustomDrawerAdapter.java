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
import kr.co.switchnow.switch_now_client.Activity.InteractionActivity;
import kr.co.switchnow.switch_now_client.Activity.InteractionActivity.DrawerViewHolder;
import kr.co.switchnow.switch_now_client.R;


import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.CounterforNewFriends;
import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.dBlockList;
import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.dFriendList;
import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.mCustomDrawerAdapter;


/**
 * Created by ceo on 2017-04-26.
 */

public class CustomDrawerAdapter extends BaseAdapter {

    static protected Context mContext = null;
    protected LayoutInflater minflater;
    static protected ArrayList<ListData> dListData = new ArrayList<>();
    URL imageURL;



    public CustomDrawerAdapter (Context context, ArrayList<ListData> objects){
        super();
        this.mContext = context;
        this.dListData = objects;
        minflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {

        return dListData.size();
    }

    @Override
    public Object getItem(int position) {
        return dListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static void dataChange(){

        mCustomDrawerAdapter.notifyDataSetChanged();

    }

    public void addItem(String email_id, String gender, String userName, String mobile_number, String status) {
        ListData addInfo;
        addInfo = new ListData();
        addInfo.m_email_id = email_id;
        addInfo.m_gender = gender;
        addInfo.m_userName = userName;
        addInfo.m_status = status;
        addInfo.m_mobile_number = mobile_number;
        try {
            imageURL = new URL("http://115.71.232.209/client/profile/" + addInfo.m_email_id + ".png");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "image URL----------------->" + imageURL);
        addInfo.m_pic_URL = String.valueOf(imageURL);
        dListData.add(addInfo);
        Log.d("TAG", "Addinfo-----------email----->" + addInfo.m_email_id);
        Log.d("TAG", "Addinfo-----------name--->" + addInfo.m_userName);
        dataChange();
    }

    public void remove(int position) {
        dListData.remove(position);
        dataChange();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        InteractionActivity.DrawerViewHolder dHolder = null;
        final InteractionActivity caller = new InteractionActivity();
        final ListData mData = dListData.get(position);

        if(convertView == null){
            minflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = minflater.inflate(R.layout.new_friends_item, null);
            dHolder = new DrawerViewHolder();
            dHolder.m_Profile = (ImageView) convertView.findViewById(R.id.profile_image_listView_adding);
            dHolder.m_NameText = (TextView) convertView.findViewById(R.id.user_name_listView_adding);
            dHolder.mAdd_btn = (ImageView) convertView.findViewById(R.id.add_friend_btn);
            dHolder.mBlock_btn = (ImageView) convertView.findViewById(R.id.block_friend_btn);

            dHolder.mAdd_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int getPosition = (Integer) v.getTag();
                    ListData m_Data = dListData.get(getPosition);
                    dFriendList.add(m_Data.m_email_id);
                    CounterforNewFriends++;
                    remove(getPosition);

                    Log.d("TAG", "email _id --------------------------->" + m_Data.m_email_id);
                    Log.d("TAG", "FRIENDLIST--------------------------->" + dFriendList.toString());
                    Log.d("TAG", "LISTVIEW DATA SIZE------------------->" + dListData.size());
                    if(dListData.size()==0){
                        caller.showMsg();
                    }

                }
            });

            dHolder.mBlock_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int getPosition = (Integer) v.getTag();
                    ListData m_Data = dListData.get(getPosition);

                    dBlockList.add(m_Data.m_email_id);
                    CounterforNewFriends++;
                    remove(getPosition);
                    Log.d("TAG", "email _id --------------------------->" + m_Data.m_email_id);
                    Log.d("TAG", "FRIENDLIST--------------------------->" + dBlockList.toString());
                    Log.d("TAG", "LISTVIEW DATA SIZE------------------->" + dListData.size());
                    if(dListData.size()==0 ){
                        caller.showMsg();
                    }

                }
            });

            convertView.setTag(dHolder);

        } else {

            dHolder = (DrawerViewHolder) convertView.getTag();

        }

        Glide.with(mContext.getApplicationContext()).load(mData.m_pic_URL).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(dHolder.m_Profile);
        dHolder.m_NameText.setText(mData.m_userName);
        dHolder.mAdd_btn.setTag(position);
        dHolder.mBlock_btn.setTag(position);

        return convertView;
    }
}
