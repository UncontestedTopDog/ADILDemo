package com.edu.aidldemo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Message implements Parcelable {
    private int msgId;
    private String msg;

    public Message(int msgId, String msg) {
        this.msgId = msgId;
        this.msg = msg;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @NonNull
    @Override
    public String toString() {
        return "{\"msg\":" + msg + ",\"msgId\":" + msgId + "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(msgId);
        dest.writeString(msg);
    }

    protected Message(Parcel in) {
        msgId = in.readInt();
        msg = in.readString();
    }

    public static final Parcelable.Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
