package com.contacts.sundeep.readcontacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends Activity {

    private TextView txt;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusHandler statusHandler = new StatusHandler(this);

        txt = (TextView) findViewById(R.id.txt);
        txt.setText("Reading contacts...");

        new ReadContactsTask(this, statusHandler).execute();

    }

    private void updateStatus(String obj) {
        txt.setText(txt.getText() + "\n" + obj);
    }


    public static class StatusHandler extends Handler {
        private final WeakReference<MainActivity> activity;

        public StatusHandler(MainActivity service) {
            activity = new WeakReference<MainActivity>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = this.activity.get();
            if (activity != null) {
                activity.updateStatus((String) msg.obj);

                if (msg.arg1 == -1) {
                    activity.sendBroadcast();
                }
            }
        }

    }

    private void sendBroadcast() {
        Intent intent = new Intent("sun.con.show_contacts");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }
}
