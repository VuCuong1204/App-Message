package com.example.chat.Model;

public class InfoUser {
    private String Iduser;
    private String Gioitinh;
    private String Namsinh;
    private String Diachi;
    private String Gioithieu;

    public InfoUser() {
    }

    public InfoUser(String iduser, String gioitinh, String namsinh, String diachi, String gioithieu) {
        Iduser = iduser;
        Gioitinh = gioitinh;
        Namsinh = namsinh;
        Diachi = diachi;
        Gioithieu = gioithieu;
    }

    public String getIduser() {
        return Iduser;
    }

    public void setIduser(String iduser) {
        Iduser = iduser;
    }

    public String getGioitinh() {
        return Gioitinh;
    }

    public void setGioitinh(String gioitinh) {
        Gioitinh = gioitinh;
    }

    public String getNamsinh() {
        return Namsinh;
    }

    public void setNamsinh(String namsinh) {
        Namsinh = namsinh;
    }

    public String getDiachi() {
        return Diachi;
    }

    public void setDiachi(String diachi) {
        Diachi = diachi;
    }

    public String getGioithieu() {
        return Gioithieu;
    }

    public void setGioithieu(String gioithieu) {
        Gioithieu = gioithieu;
    }


}
