package com.edu.aidldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private IMessageAidlInterface mInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mInterface = IMessageAidlInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mInterface = null;
            }
        };

        Intent intent = new Intent();
        intent.setAction("com.edu.aidldemo.MessageService");
        intent.setPackage(getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Message message = new Message(123,"Test");
                    mInterface.addMessage(message);
                    Log.i("Test",message.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<Message> messageList = mInterface.getMessageList();
                    Log.i("Test",messageList.size()+" ");
                    for (Message message : messageList){
                        Log.i("Test",message.toString());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
