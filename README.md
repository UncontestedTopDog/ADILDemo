#新建AIDL File
1.在main文件夹下新建一个.aidl文件，明明为IMessageAidlInterface。

![新建aidl文件.png](https://upload-images.jianshu.io/upload_images/3661454-a9336c9be555e845.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
```
package com.example.myapplication;
interface IMessageAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,double aDouble, String aString);
}
```
2.在该aidl文件的目录下，新建一个对象类，命名为Message。
在Message中实现Parcelable 的接口，并定义好CREATOR。

```
public class Message implements Parcelable {

    private String msg;
    private int msgId;

    public Message(String msg, int msgId) {
        this.msg = msg;
        this.msgId = msgId;
    }
    //省略了相关的get/set方法。
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

    public static final Parcelable.Creator<Message> CREATOR =
            new Parcelable.Creator<Message>() {
                @Override
                public Message createFromParcel(Parcel source) {
                    return new Message(source);
                }

                @Override
                public Message[] newArray(int size) {
                    return new Message[size];
                }
            };
```

3.实现IMessageAidlInterface的方法，简单地实现两个添加消息和获取消息的方法。
ps:这里有一个要注意的，虽然这个IMessageAidlInterface.adil以及Message.java是同一个目录下的，但是还是需要添加
```
import com.example.myapplication.Message; 
```
才能引用到对用的库。
添加addMessage 方法，参数中需要添加in。
添加getMessageList 方法。

```
package com.example.myapplication;

import com.example.myapplication.Message;

interface IMessageAidlInterface {
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);

    void addMessage(in Message message);

    List<Message> getMessageList();
}
```
4.我们实现了一个Message.java就需要创建一个对应的同名的.aidl
文件(Message.aidl)。
其中，我们Message.aidl只需要添加
```
parcelable Message;
```
总体代码
```
package com.example.myapplication;
parcelable Message;
```
5.添加sourceSets，在module的build.gradle文件下添加。
```
android {
   //省略其他部分
    sourceSets {
        main {
            manifest.srcFile 'src/main/AndroidManifest.xml'
            java.srcDirs = ['src/main/java', 'src/main/aidl'] //添加aidl目录
            resources.srcDirs = ['src/main/java', 'src/main/aidl']//添加aidl目录
            aidl.srcDirs = ['src/main/aidl'] //指定aidl的文件路径
            res.srcDirs = ['src/main/res']
            assets.srcDirs = ['src/main/assets']
        }
    }
}
```
6.目前AIDL部分基本完成，我们只需要make project 就能生成相关的java文件
![IMessageAidlInterface.java文件](https://upload-images.jianshu.io/upload_images/3661454-64bc737206e2e935.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
7. 进程间通信的AIDL已经实现了，现在需要的是将其调用使用起来。
7.1创建一个Service，并另外开启一个进程。
其中Service代码为
```public class MessageService extends Service {
    private List<Message> messageList = new ArrayList<>();
    // 实现了AIDL的抽象函数，并作为onBind返回的IBinder
    private IMessageAidlInterface.Stub binder = new IMessageAidlInterface.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                               double aDouble, String aString) throws RemoteException {
        }

        @Override
        public void addMessage(Message message) throws RemoteException {
            if (!messageList.contains(message)) {
                messageList.add(message);
            }
        }

        @Override
        public List<Message> getMessageList() throws RemoteException {
            return messageList;
        }
    };

    public MessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 返回该AIDL的抽象类
        return binder;
    }
}
```
修改AndroidManifest。添加android:process=":remote"设置为新的进程。
```
        <service
            android:name=".aidl.MessageService"
            android:process=":remote">
            <intent-filter>
                <category android:name="android.intent.category.DEFAULT" />
                <action android:name="com.example.myapplication.aidl.MessageService" />
            </intent-filter>
        </service>
```
7.2 在activity中打开service
添加成员变量
```
private IMessageAidlInterface messageAidlInterface;
```
在onCreate中添加
```
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                messageAidlInterface = IMessageAidlInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                messageAidlInterface = null;
            }
        };
        Intent intent = new Intent();
        intent.setAction("com.example.myapplication.aidl.MessageService");
        intent.setPackage(getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
```
获取IMessageAidlInterface之后就能调用之前定义的两个方法了。
```
messageAidlInterface.addMessage(new Message("Test111", 123));
List<Message> messageList = messageAidlInterface.getMessageList();
```
8. 这样就能实现进程间的通信了。
9. [github源码地址](https://github.com/UncontestedTopDog/ADILDemo)











