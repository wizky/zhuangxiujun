package com.campingfun.vacancyhunter.campsitehunter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.Registration;
import com.microsoft.windowsazure.mobileservices.RegistrationCallback;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

/**
 * Created by wayliu on 9/2/2014.
 */
public class MyHandler extends NotificationsHandler {
//    @com.google.gson.annotations.SerializedName("handle")
//    private static String mHandle;
    private static final String TAG = MyHandler.class.getSimpleName();

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;
//
//    public static String getHandle() {
//        return mHandle;
//    }
//
//    public static final void setHandle(String handle) {
//        mHandle = handle;
//    }

    @Override
    public void onRegistered(Context context, String gcmRegistrationId) {
        super.onRegistered(context, gcmRegistrationId);
        CampsiteDetails activity = (CampsiteDetails) context;

        activity.mClient.getPush().register(gcmRegistrationId, null, new RegistrationCallback() {
            @Override
            public void onRegister(Registration registration, Exception exception) {
                if (exception != null) {
                    Log.e(TAG, exception.toString());
                }
            }
        });
    }

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        String nhMessage = bundle.getString("message");

        sendNotification(nhMessage, bundle);
    }

    private void sendNotification(String msg, Bundle bundle) {
        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(ctx, CampsiteDetails.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.replaceExtras(bundle);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        builder.setSound(alarmSound);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSound(alarmSound)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Hurry, Campground Is Available Now!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg + " has vacancy, please reserve your spot.")
                        .addExtras(bundle)
                        .setTicker("Hurry, Campground Is Available Now!");

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

//    @Override
//    public void onRegistered(Context context, String gcmRegistrationId) {
//        super.onRegistered(context, gcmRegistrationId);
//        setHandle(gcmRegistrationId);
//    }

//    @Override
//    public void onReceive(Context context, Bundle bundle) {
//        super.onReceive(context, bundle);
//    }
}
