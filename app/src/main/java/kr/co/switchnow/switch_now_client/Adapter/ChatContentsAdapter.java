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

import kr.co.switchnow.switch_now_client.ADT.ChatMessage;
import kr.co.switchnow.switch_now_client.Activity.PrivateChatActivity;
import kr.co.switchnow.switch_now_client.R;


import static kr.co.switchnow.switch_now_client.Activity.InteractionActivity.chatMessageDBManager;
import static kr.co.switchnow.switch_now_client.Activity.PrivateChatActivity.mChatContentsAdapater;

/**
 * Created by ceo on 2017-06-19.
 */

public class ChatContentsAdapter extends BaseAdapter {

    private static final int MESSAGE_FROM_USER = 0;
    private static final int MESSAGE_FROM_OPPONENT = 1;
    private static final int TIME_STAMP = 2;

    static protected Context mContext = null;
    protected LayoutInflater mInflater;
    public static ArrayList<ChatMessage> messageListData = new ArrayList<>();
    URL imageURL;


    public ChatContentsAdapter(Context context, ArrayList<ChatMessage> objects) {
        super();
        this.mContext = context;
        this.messageListData = objects;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {

        return messageListData.size();
    }

    @Override
    public Object getItem(int position) {

        return messageListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static void dataChange() {

        mChatContentsAdapater.notifyDataSetChanged();

    }

    public int getItemViewType(int position) {

        return messageListData.get(position).msgType;
    }

    public int getViewTypeCount() {

        return 3;
    }


    public void addMessage(String roomId, String sender_id, String sender_name, String msg, int readStatus, String sendTime, int msgType) {
        ChatMessage addInfo;
        addInfo = new ChatMessage();
        addInfo.roomId = roomId;
        addInfo.sender_Id = sender_id;
        addInfo.sender_name = sender_name;
        addInfo.TextCotents = msg;
        addInfo.read_status = readStatus;
        addInfo.send_time = sendTime;
        addInfo.msgType = msgType;
        if (addInfo.msgType == MESSAGE_FROM_OPPONENT) {
            try {
                imageURL = new URL("http://115.71.232.209/client/profile/" + addInfo.sender_Id + ".png");
                addInfo.opponent_img_url = String.valueOf(imageURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        messageListData.add(addInfo);
        chatMessageDBManager.addMessageData(addInfo);
        dataChange();
    }


    public void remove(int position) {

        messageListData.remove(position);
        dataChange();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PrivateChatActivity.userMessageViewHolder myMsgHolder = null;
        PrivateChatActivity.opponentMessageViewHolder opponentMsgHolder = null;
        PrivateChatActivity.timeStampViewHolder timeMsgHolder = null;
        int msgType = getItemViewType(position);

        final ChatMessage chatMessage_data = messageListData.get(position);

        switch (msgType) {

            case MESSAGE_FROM_USER: {

                if (convertView == null) {
                    mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = mInflater.inflate(R.layout.message_item_from_user, null);

                    myMsgHolder = new PrivateChatActivity.userMessageViewHolder();
                    myMsgHolder.userMessage = (TextView) convertView.findViewById(R.id.msg_contents_user);
                    myMsgHolder.userTimestamp = (TextView) convertView.findViewById(R.id.user_msg_timestamp);

                    convertView.setTag(myMsgHolder);
                } else {
                    myMsgHolder = (PrivateChatActivity.userMessageViewHolder) convertView.getTag();
                }

                myMsgHolder.userMessage.setText(chatMessage_data.TextCotents);
                myMsgHolder.userTimestamp.setText(chatMessage_data.send_time);

                break;
            }

            case MESSAGE_FROM_OPPONENT: {

                if (convertView == null) {
                    mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = mInflater.inflate(R.layout.message_item_from_opponent, null);
                    opponentMsgHolder = new PrivateChatActivity.opponentMessageViewHolder();
                    opponentMsgHolder.profile_img_opponent = (ImageView) convertView.findViewById(R.id.profile_image_chat_opponent);
                    opponentMsgHolder.opponent_name = (TextView) convertView.findViewById(R.id.name_txt_chat_opponent);
                    opponentMsgHolder.opponent_message = (TextView) convertView.findViewById(R.id.msg_contents_opponent);
                    opponentMsgHolder.opponent_timestamp = (TextView) convertView.findViewById(R.id.opponent_msg_timestamp);

                    convertView.setTag(opponentMsgHolder);
                } else {
                    opponentMsgHolder = (PrivateChatActivity.opponentMessageViewHolder) convertView.getTag();
                }
                opponentMsgHolder.opponent_name.setText(chatMessage_data.sender_name);
                opponentMsgHolder.opponent_message.setText(chatMessage_data.TextCotents);
                opponentMsgHolder.opponent_timestamp.setText(chatMessage_data.send_time);
                Glide.with(mContext.getApplicationContext()).load(chatMessage_data.opponent_img_url).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).into(opponentMsgHolder.profile_img_opponent);


                break;
            }

            case TIME_STAMP: {

                if (convertView == null) {
                    mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = mInflater.inflate(R.layout.message_timestamp_for_chat_window, null);
                    timeMsgHolder = new PrivateChatActivity.timeStampViewHolder();
                    timeMsgHolder.timestamp_line = (View) convertView.findViewById(R.id.msg_contents_timeline);
                    timeMsgHolder.timestamp_txt = (TextView) convertView.findViewById(R.id.msg_contents_timestamp);


                    convertView.setTag(timeMsgHolder);
                } else {

                    timeMsgHolder = (PrivateChatActivity.timeStampViewHolder) convertView.getTag();
                }

                timeMsgHolder.timestamp_txt.setText(chatMessage_data.send_time);
                break;
            }

        }


        return convertView;
    }
}
