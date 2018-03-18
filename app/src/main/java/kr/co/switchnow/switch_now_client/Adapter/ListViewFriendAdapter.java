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
import kr.co.switchnow.switch_now_client.Activity.AddingFriendsActivity;
import kr.co.switchnow.switch_now_client.R;


import static kr.co.switchnow.switch_now_client.Activity.AddingFriendsActivity.BlockList;
import static kr.co.switchnow.switch_now_client.Activity.AddingFriendsActivity.friendList;
import static kr.co.switchnow.switch_now_client.Activity.AddingFriendsActivity.mFriendAdapter;


/**
 * Created by ceo on 2017-04-16.
 */

public class ListViewFriendAdapter extends BaseAdapter {

    static protected Context mContext = null;
    protected LayoutInflater minflater;
    static protected ArrayList<ListData> mListData = new ArrayList<ListData>();

    URL imageURL;



    public ListViewFriendAdapter(Context context, ArrayList<ListData> objects) {
        super();
        this.mContext = context;
        this.mListData = objects;
        minflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {


        return mListData.size();
    }


    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static void dataChange() {

        mFriendAdapter.notifyDataSetChanged();
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
        mListData.add(addInfo);
        Log.d("TAG", "Addinfo-----------email----->" + addInfo.m_email_id);
        Log.d("TAG", "Addinfo-----------name--->" + addInfo.m_userName);
        dataChange();
    }

    public void remove(int position) {
        mListData.remove(position);
        dataChange();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        AddingFriendsActivity.ViewHolder holder = null;
        final AddingFriendsActivity caller = new AddingFriendsActivity();
//        View item = null;

        final ListData mData = mListData.get(position);

        if (convertView == null) {
            minflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = minflater.inflate(R.layout.adding_friends_item, null);
            holder = new AddingFriendsActivity.ViewHolder();
            holder.m_Profile = (ImageView) convertView.findViewById(R.id.profile_image_listView_adding);
            holder.m_NameText = (TextView) convertView.findViewById(R.id.user_name_listView_adding);
            holder.mAdd_btn = (ImageView) convertView.findViewById(R.id.add_friend_btn);
            holder.mBlock_btn = (ImageView) convertView.findViewById(R.id.block_friend_btn);


            final int[] Counter = {0};
            holder.mAdd_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int getPosition = (Integer) v.getTag();
                    ListData m_Data = mListData.get(getPosition);

                    friendList.add(m_Data.m_email_id);
                    Counter[0]++;
                    remove(getPosition);
                    Log.d("TAG", "email _id --------------------------->" + m_Data.m_email_id);
                    Log.d("TAG", "FRIENDLIST--------------------------->" + friendList.toString());
                    Log.d("TAG", "LISTVIEW DATA SIZE------------------->" + mListData.size());
                    if(mListData.size()==0){
                        caller.showNavi();
                    }

                    if(Counter[0]>1){
                        caller.setBtnTxt();
                    }
                }
            });

            holder.mBlock_btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int getPosition = (Integer) v.getTag();
                    ListData m_Data = mListData.get(getPosition);

                    BlockList.add(m_Data.m_email_id);
                    remove(getPosition);
                    Log.d("TAG", "email _id --------------------------->" + m_Data.m_email_id);
                    Log.d("TAG", "BLOCKLIST---------------------------->" + BlockList.toString());
                    Log.d("TAG", "LISTVIEW DATA SIZE------------------->" + mListData.size());
                    if(mListData.size()==0){
                        caller.showNavi();
                    }

                }

            });

            convertView.setTag(holder);

        } else {
            holder = (AddingFriendsActivity.ViewHolder) convertView.getTag();
        }


        Glide.with(mContext.getApplicationContext()).load(mData.m_pic_URL).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(holder.m_Profile);
        holder.m_NameText.setText(mData.m_userName);
        holder.mAdd_btn.setTag(position);
        holder.mBlock_btn.setTag(position);

        return convertView;
    }

}
