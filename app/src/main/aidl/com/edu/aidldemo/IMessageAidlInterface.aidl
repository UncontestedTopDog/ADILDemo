package com.edu.aidldemo;

import com.edu.aidldemo.Message;

interface IMessageAidlInterface {
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void addMessage(in Message message);

    List<Message> getMessageList();

}
