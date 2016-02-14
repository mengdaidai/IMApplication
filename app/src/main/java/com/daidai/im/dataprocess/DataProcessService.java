package com.daidai.im.dataprocess;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.daidai.im.R;
import com.daidai.im.activity.MainWeixin;
import com.daidai.im.util.MyApplication;

public class DataProcessService extends Service {

    ClientNetWork client_network;

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(client_network).start();
        Notification notification = new Notification(R.drawable.ic_launcher,
                "有通知到来", System.currentTimeMillis());
        Intent notificationIntent = new Intent(this,MainWeixin.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(this, "IM", "这是通知的内容",
                pendingIntent);
        startForeground(1, notification);
    }

    public DataProcessService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
