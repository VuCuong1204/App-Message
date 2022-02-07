package com.example.chat.Model;

public class ChanUser {
    String IdChan;
    String IdBiChan;

    public ChanUser(String idChan, String idBiChan) {
        IdChan = idChan;
        IdBiChan = idBiChan;
    }

    public ChanUser() {
    }

    public String getIdChan() {
        return IdChan;
    }

    public void setIdChan(String idChan) {
        IdChan = idChan;
    }

    public String getIdBiChan() {
        return IdBiChan;
    }

    public void setIdBiChan(String idBiChan) {
        IdBiChan = idBiChan;
    }
}
