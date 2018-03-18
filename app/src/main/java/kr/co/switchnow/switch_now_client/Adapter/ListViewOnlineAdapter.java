package kr.co.switchnow.switch_now_client.Adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
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
import kr.co.switchnow.switch_now_client.R;
import kr.co.switchnow.switch_now_client.Util.CustomDialog;


import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.mOnlineAdapter;


/**
 * Created by ceo on 2017-04-23.
 */

public class ListViewOnlineAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private ArrayList<Integer> sectionHeader = new ArrayList<>();

    static protected Context mContext = null;
    protected LayoutInflater minflater;
    static protected ArrayList<ListData> oListData = new ArrayList<ListData>();

    URL imageURL;
    String myName;


    public ListViewOnlineAdapter(Context context, ArrayList<ListData> objects, String myName) {
        super();
        this.mContext = context;
        this.oListData = objects;
        this.myName = myName;
        minflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return oListData.size();
    }

    @Override
    public Object getItem(int position) {
        return oListData.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    public void addSectionHeaderItem(final String item, int position) {

        ListData addSection;

        addSection = new ListData();
        addSection.m_section_text = item;
        sectionHeader.add(position);
        oListData.add(addSection);
        Log.d("TAG", "sectionHeader... contains.....----------> " + sectionHeader.toString());
        dataChange();
    }

    public int getItemViewType(int position) {

        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public void addItem(String email_id, String gender, String userName, String mobile_number, String status, float distance, String MyId) {
        ListData addInfo;
        addInfo = new ListData();
        addInfo.m_email_id = email_id;
        addInfo.m_gender = gender;
        addInfo.m_userName = userName;
        addInfo.m_status = status;
        addInfo.m_mobile_number = mobile_number;
        addInfo.myId = MyId;
        try {
            imageURL = new URL("http://115.71.232.209/client/profile/" + addInfo.m_email_id + ".png");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "image URL----------------->" + imageURL);
        addInfo.m_pic_URL = String.valueOf(imageURL);
        addInfo.m_distance = distance;
        Log.d("TAG", "Addinfo-----------email----->" + addInfo.m_email_id);
        Log.d("TAG", "Addinfo-----------name--->" + addInfo.m_userName);
        Log.d("TAG", "Addinfo-----------distance--->" + addInfo.m_distance);
        oListData.add(addInfo);
        Log.d("TAG", "LISTDATA---------------------->" + addInfo.toString());
        dataChange();
    }


    public static void dataChange() {

        mOnlineAdapter.notifyDataSetChanged();
    }

    public void remove(int position) {
        oListData.remove(position);
        dataChange();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InteractionActivity.ViewHolder holder = null;
        int rowType = this.getItemViewType(position);
        final ListData mData = oListData.get(position);
        InteractionActivity.SectionHolder sectionHolder = null;

        switch (rowType) {

            case TYPE_ITEM: {
                if (convertView == null) {
                    minflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = minflater.inflate(R.layout.interaction_item, null);
                    holder = new InteractionActivity.ViewHolder();
                    holder.m_NameText = (TextView) convertView.findViewById(R.id.user_name_listView_interaction);
                    holder.mCall_btn = (ImageView) convertView.findViewById(R.id.call_to_friend_btn);
                    holder.mMessage_btn = (ImageView) convertView.findViewById(R.id.message_to_friend_btn);
                    holder.m_Profile = (ImageView) convertView.findViewById(R.id.profile_image_listView_interaction);

                    holder.mCall_btn.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            int getPosition = (Integer) v.getTag();
                            ListData m_Data = oListData.get(getPosition);
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + m_Data.m_mobile_number));
                            if (ActivityCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }
                            mContext.startActivity(callIntent);

                        }

                    });

                    holder.mMessage_btn.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            int getPosition = (Integer) v.getTag();

                            ListData m_Data = oListData.get(getPosition);
                            CustomDialog customDialog = new CustomDialog();
                            customDialog.sendMessage(mContext, m_Data.m_email_id, m_Data.m_userName, m_Data.myId, myName);

                        }

                    });

                    holder.m_Profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int getPosition = (Integer) v.getTag(R.id.profile_image_listView_interaction);
                            ListData m_Data = oListData.get(getPosition);
                            CustomDialog customDialog = new CustomDialog();
                            customDialog.showProfile(mContext, m_Data.m_userName, m_Data.m_status, m_Data.m_pic_URL);

                        }
                    });


                    convertView.setTag(holder);
                } else {

                    holder = (InteractionActivity.ViewHolder) convertView.getTag();
                }

                Log.d("TAG", "ListView Data pic URL --------------------> " + mData.m_pic_URL);
                Glide.with(mContext.getApplicationContext()).load(mData.m_pic_URL).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).into(holder.m_Profile);
                holder.m_NameText.setText(mData.m_userName);
                holder.mMessage_btn.setTag(position);
                holder.m_Profile.setTag(R.id.profile_image_listView_interaction, position);
                holder.mCall_btn.setTag(position);

                return convertView;

            }


            case TYPE_SEPARATOR: {

                if (convertView == null) {

                    minflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = minflater.inflate(R.layout.section, null);
                    sectionHolder = new InteractionActivity.SectionHolder();
                    sectionHolder.DistanceSeperator = (TextView) convertView.findViewById(R.id.section_text);


                    convertView.setTag(sectionHolder);

                } else {

                    sectionHolder = (InteractionActivity.SectionHolder) convertView.getTag();

                }

                sectionHolder.DistanceSeperator.setText(mData.m_section_text);
//                RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.section_layout);
//                layout.getLayoutParams().width=90;

                return convertView;

            }

        }

        return convertView;
    }





}