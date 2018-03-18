package kr.co.switchnow.switch_now_client.Adapter;

import android.content.Context;
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

import kr.co.switchnow.switch_now_client.ADT.ChatRoom;
import kr.co.switchnow.switch_now_client.Activity.ConversationActivity_;
import kr.co.switchnow.switch_now_client.R;


import static kr.co.switchnow.switch_now_client.Activity.ConversationActivity_.mListViewChatAdapter;


/**
 * Created by ceo on 2017-06-14.
 */

public class ListViewChatAdapter extends BaseAdapter {

    static protected Context mContext = null;
    protected LayoutInflater minflater;
    public static ArrayList<ChatRoom> chatRoomListdata = new ArrayList<>();
    URL imageURL;

    public ListViewChatAdapter(Context context, ArrayList<ChatRoom> objects) {
        super();
        this.mContext = context;
        this.chatRoomListdata = objects;
        minflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {


        return chatRoomListdata.size();
    }

    @Override
    public Object getItem(int position) {

        return chatRoomListdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void dataChange() {

        mListViewChatAdapter.notifyDataSetChanged();
    }

    public void addItem(String roomId, String userId, String chatWith, int roomStatus, String updatedTime, String lastMessage, String userName) {
        ChatRoom addInfo;
        addInfo = new ChatRoom();
        addInfo.roomId = roomId;
        addInfo.user_id = userId;
        addInfo.chat_with = chatWith;
        addInfo.room_status = roomStatus;
        addInfo.updated_time = updatedTime;
        addInfo.last_message = lastMessage;
//        addInfo.userStatus = userStatus;
        addInfo.userName = userName;

        try {
            imageURL = new URL("http://115.71.232.209/client/profile/" + addInfo.chat_with + ".png");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        Log.d("TAG", "image URL----------------->" + imageURL);
        addInfo.user_img_url = String.valueOf(imageURL);
        chatRoomListdata.add(addInfo);
        dataChange();
    }


    public void insertItem(String roomId, String userId, String chatWith, int roomStatus, String updatedTime, String lastMessage, String userName){

        ChatRoom insertInfo;
        insertInfo = new ChatRoom();
        insertInfo.roomId = roomId;
        insertInfo.user_id = userId;
        insertInfo.chat_with = chatWith;
        insertInfo.room_status = roomStatus;
        insertInfo.updated_time = updatedTime;
        insertInfo.last_message = lastMessage;
//        addInfo.userStatus = userStatus;
        insertInfo.userName = userName;

        try {
            imageURL = new URL("http://115.71.232.209/client/profile/" + insertInfo.chat_with + ".png");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        Log.d("TAG", "image URL----------------->" + imageURL);
        insertInfo.user_img_url = String.valueOf(imageURL);
        chatRoomListdata.add(0,insertInfo);

        dataChange();
    }




    public void remove(int position) {
        chatRoomListdata.remove(position);
        dataChange();
    }





    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ConversationActivity_.ChatRoomViewHolder ChatItemHolder = null;

        final ChatRoom mData = chatRoomListdata.get(position);

        if (convertView == null) {
            minflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = minflater.inflate(R.layout.chat_room_item, null);
            ChatItemHolder = new ConversationActivity_.ChatRoomViewHolder();
            ChatItemHolder.m_Profile = (ImageView) convertView.findViewById(R.id.profile_image_listView_chat_room_list);
            ChatItemHolder.m_NameText = (TextView) convertView.findViewById(R.id.user_name_listView_chat_room_list);
            ChatItemHolder.m_lastMeg = (TextView) convertView.findViewById(R.id.last_message_listView_chat_room_list);
            ChatItemHolder.m_TimeStamp = (TextView) convertView.findViewById(R.id.chat_room_updated_timestamp);
            ChatItemHolder.m_RoomStatus = (ImageView) convertView.findViewById(R.id.roomCheckedMark);


            convertView.setTag(ChatItemHolder);
        } else {
            ChatItemHolder = (ConversationActivity_.ChatRoomViewHolder) convertView.getTag();
        }

        Glide.with(mContext.getApplicationContext()).load(mData.user_img_url).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(ChatItemHolder.m_Profile);

        ChatItemHolder.m_NameText.setText(mData.userName);
        ChatItemHolder.m_lastMeg.setText(mData.last_message);
        ChatItemHolder.m_TimeStamp.setText(mData.updated_time);
        ChatItemHolder.m_RoomStatus.setVisibility(View.VISIBLE);

        return convertView;
    }
}
