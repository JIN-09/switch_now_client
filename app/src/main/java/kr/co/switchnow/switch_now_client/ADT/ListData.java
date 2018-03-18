package kr.co.switchnow.switch_now_client.ADT;

/**
 * Created by ceo on 2017-04-16.
 */

public class ListData {

    public String m_email_id;
    public String m_gender;
    public String m_userName;
    public String m_mobile_number;
    public String m_pic_URL;
    public String m_status;
    public Float m_distance;
    public String m_section_text;
    public String myId;

    public ListData(){

    }


    public ListData(String email_id, String gender, String userName, String mobile_number, String status){

        this.m_email_id = email_id;
        this.m_gender = gender;
        this.m_userName = userName;
        this.m_mobile_number = mobile_number;
        this.m_status = status;

    }

    public ListData(String email_id, String gender, String userName, String mobile_number, String status, float distance){

        this.m_email_id = email_id;
        this.m_gender = gender;
        this.m_userName = userName;
        this.m_mobile_number = mobile_number;
        this.m_status = status;
        this.m_distance = distance;

    }

   public ListData(String email_id, String gender, String userName, String mobile_number, String status, float distance, String MyId){
       this.m_email_id = email_id;
       this.m_gender = gender;
       this.m_userName = userName;
       this.m_mobile_number = mobile_number;
       this.m_status = status;
       this.m_distance = distance;
       this.myId = MyId;
   }


    public String getEmail_id(){

        return this.m_email_id;
    }


    public String getSection_text(){

        return m_section_text;
    }




}
