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
import kr.co.switchnow.switch_now_client.Fragment.BlockListFragment;
import kr.co.switchnow.switch_now_client.R;

import static kr.co.switchnow.switch_now_client.Fragment.BlockListFragment.bfFriendList;
import static kr.co.switchnow.switch_now_client.Fragment.BlockListFragment.mBlockListForFragmentAdapter;

/**
 * Created by ceo on 2017-06-13.
 */

public class BlockListForFragmentAdapter extends BaseAdapter{


    static protected Context mContext = null;
    protected LayoutInflater minflater;
    static protected ArrayList<ListData> fbListData = new ArrayList<>();
    URL imageURL;


    public BlockListForFragmentAdapter(Context context, ArrayList<ListData> objects){
        super();
        this.mContext = context;
        this.fbListData = objects;
        minflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {

        return fbListData.size();
    }

    @Override
    public Object getItem(int position) {

        return fbListData.get(position);
    }

    @Override
    public long getItemId(int position) {


        return position;
    }

    public static void dataChange(){

        mBlockListForFragmentAdapter.notifyDataSetChanged();
    }

    public void addItem(String email_id, String gender, String userName, String mobile_number, String status){

        ListData bfData;
        bfData = new ListData();
        bfData.m_email_id = email_id;
        bfData.m_gender = gender;
        bfData.m_userName = userName;
        bfData.m_status = status;
        bfData.m_mobile_number = mobile_number;
        try {
            imageURL = new URL("http://115.71.232.209/client/profile/" + bfData.m_email_id + ".png");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "image URL----------------->" + imageURL);
        bfData.m_pic_URL = String.valueOf(imageURL);
        fbListData.add(bfData);
        Log.d("TAG", "Addinfo-----------email----->" + bfData.m_email_id);
        Log.d("TAG", "Addinfo-----------name--->" + bfData.m_userName);
        dataChange();

    }

    public void remove(int position){
        fbListData.remove(position);
        dataChange();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BlockListFragment.bViewHolder bHolder = null;
        final ListData mData = fbListData.get(position);
        final BlockListFragment caller = new BlockListFragment();

        if(convertView == null){
            minflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = minflater.inflate(R.layout.blocked_fragment_item, null);
            bHolder = new BlockListFragment.bViewHolder();
            bHolder.m_Profile = (ImageView) convertView.findViewById(R.id.profile_image_block_fragment);
            bHolder.m_NameText = (TextView) convertView.findViewById(R.id.user_name_block_fragment);
            bHolder.m_Add_btn = (ImageView) convertView.findViewById(R.id.add_friend_btn);

            bHolder.m_Add_btn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    int getPosition = (Integer) v.getTag();
                    ListData m_Data = fbListData.get(getPosition);
                    bfFriendList.add(m_Data.m_email_id);
                    remove(getPosition);
                    Log.d("TAG", "email _id --------------------------->" + m_Data.m_email_id);
                    Log.d("TAG", "FRIENDLIST--------------------------->" + bfFriendList.toString());
                    Log.d("TAG", "LISTVIEW DATA SIZE------------------->" + fbListData.size());
                    if(fbListData.size()==0 ){
                        caller.showMsg();
                    }
                }

            });

            convertView.setTag(bHolder);

        } else {

            bHolder = (BlockListFragment.bViewHolder) convertView.getTag();

        }

        Glide.with(mContext.getApplicationContext()).load(mData.m_pic_URL).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(bHolder.m_Profile);
        bHolder.m_NameText.setText(mData.m_userName);
        bHolder.m_Add_btn.setTag(position);


        return convertView;
    }
}
