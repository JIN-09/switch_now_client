package kr.co.switchnow.switch_now_client.ADT;

/**
 * Created by ceo on 2017-06-16.
 */

public class ChatRoom {

    public String roomId;
    public String user_id;
    public String chat_with;
    public int room_status;
    public String updated_time;
    public String last_message;
    public String isOnline;
    public String userName;
    public String user_img_url;


    public ChatRoom(){

    }


    public ChatRoom(String roomId, String userId, String chatWith, int roomStatus, String updatedTime, String lastMessage, String userName){
        this.roomId = roomId;
        this.user_id = userId;
        this.chat_with = chatWith;
        this.room_status = roomStatus;
        this.updated_time = updatedTime;
        this.last_message = lastMessage;
        this.userName = userName;
    }

    public void setlastMessage(String last_message){

        this.last_message = last_message;

    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setChat_with(String chat_with) {
        this.chat_with = chat_with;
    }

    public void setRoom_status(int room_status) {
        this.room_status = room_status;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getChat_with() {
        return chat_with;
    }

    public int getRoom_status() {
        return room_status;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public String getLast_message() {
        return last_message;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public String getUserName() {
        return userName;
    }

    public String getUser_img_url() {
        return user_img_url;
    }

    public void setUser_img_url(String user_img_url) {
        this.user_img_url = user_img_url;
    }
}
