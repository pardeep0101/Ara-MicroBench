package com.example.xaradrim.benchmark_example;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.xaradrim.benchmark_example.Tests.DataLogging.AttributeGenerator;

import java.net.URISyntaxException;

/**
 * Created by Pardeep on 8/6/15.
 */
public class MyService extends Service {
    private final IBinder ARABinder = new MyLocalBinder();
    AttributeGenerator at1 = null;
    private static final String TAG = "ARA SERVICE";
    boolean workdone=false;
    public MyService(){

        Log.d("ARA Service", "construction");
    }
    @Override
    public IBinder onBind(Intent intent) {
        return ARABinder;
    }

    public void getAttributeList(String name){
        at1.addAttributeList(name);
        Log.d(TAG, "Attribute List added");
    }
    public void getIntentParameter(Intent b){
        at1.prepareAttributes(b);
        Log.d(TAG, "Attribute paremeters added, running DC thread.");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        at1 = AttributeGenerator.getInstance();
        Log.d(TAG, "Service created");
    }

    String observerType;
    Intent b;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "Service started ");
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        SharedPreferences sp = getSharedPreferences("service preference",   0);

        if(intent !=null) {

            observerType = intent.getStringExtra("ObserverType");
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("observerType", observerType);
            editor.putString("intentMA", intent.toUri(0));

            Log.d(TAG,"running intent from not null");

            if (observerType != null) {
                getAttributeList(observerType);
            }
            b=intent;
            getIntentParameter(intent);
            editor.commit();
        }
        else{
            String intentURI = sp.getString("intentMA",null);
            String obsvType = sp.getString("observerType",null);
            getAttributeList(obsvType);
            try {
                getIntentParameter(Intent.parseUri(intentURI,0));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "running intent from null");
        }
        startInForeground();
        return START_STICKY;
    }
    public class MyLocalBinder extends Binder {

        MyService getService(){
            return MyService.this;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

            at1.emptyAttributeList();
            Log.d(TAG, "Service stopped, stopping DC thread..");

    }
    Notification foregroundNotification;
    final int notificationID=1;

    void startInForeground(){
        int notificationIcon = R.mipmap.ic_stat_ara;
        String notificationText = "Running ARA DC in the background";
        long notificationTimeStamp = System.currentTimeMillis();
        foregroundNotification = new Notification(notificationIcon,notificationText
        ,notificationTimeStamp);

        String notificationTitleText = "ARA Benchmark Service";
        String notificationBodyText = "Its running DC (Data captureing) process in background";

        Intent myActivityIntent = new Intent(this,MainActivity.class);
        PendingIntent startMyActivitypendingInetnt = PendingIntent.getActivity(this,
                0, myActivityIntent,0);
        foregroundNotification.setLatestEventInfo(this,notificationTitleText,
                notificationBodyText,startMyActivitypendingInetnt);

        startForeground(notificationID, foregroundNotification);

    }

}
