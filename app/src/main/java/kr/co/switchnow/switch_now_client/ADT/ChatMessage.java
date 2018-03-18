package kr.co.switchnow.switch_now_client.ADT;

/**
 * Created by ceo on 2017-06-19.
 */

public class ChatMessage {

    public String roomId;
    public String sender_Id;
    public String sender_name;
    public String TextCotents;
    public int read_status;
    public String send_time;
    public int msgType;
    public String opponent_img_url;


    public ChatMessage(){

    }

    public ChatMessage(String roomId, String sender_Id, String sender_name, String TextContents, int readStatus, String sendTime, int msg_type){

        this.sender_name = sender_name;
        this.roomId = roomId;
        this.sender_Id = sender_Id;
        this.TextCotents = TextContents;
        this.read_status = readStatus;
        this.send_time = sendTime;
        this.msgType = msg_type;

    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setSender_Id(String sender_Id) {
        this.sender_Id = sender_Id;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public void setTextCotents(String textCotents) {
        TextCotents = textCotents;
    }

    public void setRead_status(int read_status) {
        this.read_status = read_status;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public void setOpponent_img_url(String opponent_img_url) {
        this.opponent_img_url = opponent_img_url;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getSender_Id() {
        return sender_Id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public String getTextCotents() {
        return TextCotents;
    }

    public int getRead_status() {
        return read_status;
    }

    public String getSend_time() {
        return send_time;
    }

    public int getMsgType() {
        return msgType;
    }

    public String getOpponent_img_url() {
        return opponent_img_url;
    }
}
