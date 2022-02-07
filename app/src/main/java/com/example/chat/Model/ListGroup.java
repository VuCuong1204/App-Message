package com.example.chat.Model;

public class ListGroup {
    String IdUser;
    String Chucvu;

    public ListGroup(String idUser, String chucvu) {
        IdUser = idUser;
        Chucvu = chucvu;
    }

    public ListGroup() {
    }

    public String getIdUser() {
        return IdUser;
    }

    public void setIdUser(String idUser) {
        IdUser = idUser;
    }

    public String getChucvu() {
        return Chucvu;
    }

    public void setChucvu(String chucvu) {
        Chucvu = chucvu;
    }
}
