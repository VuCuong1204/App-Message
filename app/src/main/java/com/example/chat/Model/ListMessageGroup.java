package com.example.chat.Model;

public class ListMessageGroup {

     String senduser;
     String messagegroup;
     String username;
     String profileUser;
     String Thoigian;

    public ListMessageGroup(String senduser, String messagegroup, String username, String profileUser,String thoigian) {
        this.senduser = senduser;
        this.messagegroup = messagegroup;
        this.username = username;
        this.profileUser = profileUser;
        this.Thoigian = thoigian;
    }

    public ListMessageGroup() {
    }

    public String getSenduser() {
        return senduser;
    }

    public void setSenduser(String senduser) {
        this.senduser = senduser;
    }

    public String getMessagegroup() {
        return messagegroup;
    }

    public void setMessagegroup(String messagegroup) {
        this.messagegroup = messagegroup;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileUser() {
        return profileUser;
    }

    public void setProfileUser(String profileUser) {
        this.profileUser = profileUser;
    }

    public String getThoigian() {
        return Thoigian;
    }

    public void setThoigian(String thoigian) {
        Thoigian = thoigian;
    }
}
