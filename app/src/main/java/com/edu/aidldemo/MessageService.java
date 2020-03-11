package com.edu.aidldemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

public class MessageService extends Service {

    private List<Message> mMessageList = new ArrayList<>();

    private IMessageAidlInterface.Stub binder = new IMessageAidlInterface.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                               double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void addMessage(Message message) throws RemoteException {
            if (!mMessageList.contains(message))
                mMessageList.add(message);
        }

        @Override
        public List<Message> getMessageList() throws RemoteException {
            return mMessageList;
        }
    };

    public MessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
