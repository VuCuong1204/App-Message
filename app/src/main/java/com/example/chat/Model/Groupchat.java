package com.example.chat.Model;

public class Groupchat {

    private String groupname;
    private String profilegroup;
    private String Idphong;

    public Groupchat(String groupname, String profilegroup, String idphong) {
        this.groupname = groupname;
        this.profilegroup = profilegroup;
        this.Idphong = idphong;
    }

    public Groupchat() {
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getProfilegroup() {
        return profilegroup;
    }

    public void setProfilegroup(String profilegroup) {
        this.profilegroup = profilegroup;
    }

    public String getIdphong() {
        return Idphong;
    }

    public void setIdphong(String idphong) {
        this.Idphong = idphong;
    }
}
