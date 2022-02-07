package com.example.chat.Model;

import java.io.Serializable;

public class Chat implements Serializable {

    String sender;
    String receiver;
    String message;
    boolean isseen;
    String seen;
    int soluong;
    String infor;

    public Chat(String sender, String receiver, String message, boolean isseen, String seen, int soluong, String infor) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.seen = seen;
        this.soluong = soluong;
        this.infor = infor;
    }

    public Chat(){}

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public String getInfor() {
        return infor;
    }

    public void setInfor(String infor) {
        this.infor = infor;
    }
}
