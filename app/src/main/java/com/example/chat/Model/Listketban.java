package com.example.chat.Model;

public class Listketban {
    String idUser;
    String titleAdd;

    public Listketban(String idUser, String titleAdd) {
        this.idUser = idUser;
        this.titleAdd = titleAdd;
    }

    public Listketban() {
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getTitleAdd() {
        return titleAdd;
    }

    public void setTitleAdd(String titleAdd) {
        this.titleAdd = titleAdd;
    }
}
