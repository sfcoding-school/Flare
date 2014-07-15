package com.sfcoding.flare.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sfcoding.flare.Activity.MainActivity;
import com.sfcoding.flare.Data.Group;
import com.sfcoding.flare.Data.Person;
import com.sfcoding.flare.R;
import com.sfcoding.flare.Support.JsonIO;

import org.json.JSONException;

import java.lang.reflect.Field;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString(),"");
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString(),"");
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.e("intent", "notifica");


                String sender_id = extras.getString("id_fb");
                //se chi mi ha inviato la notifica è tra gli amici scelti

                Person sender = Group.searchById(sender_id);
                if (sender != null) {
                    Log.e("intent", "notifica accettata");
                    //aggiorno la posizione dell utente che mi ha fatto richiesta
                    sender.updatePos(Double.parseDouble(extras.getString("lat")), Double.parseDouble(extras.getString("lng")));
                    try {
                        //salvo gli amici
                        JsonIO.saveFriends(Group.Friends, getApplicationContext(), "friends");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (MainActivity.handlerService != null) {

                        Bundle bmsg = new Bundle();
                        Message m = new Message();
                        //1=aggiorna i marker
                           /* bmsg.putInt("op",1);

                            m.setData(bmsg);
                            MainActivity.handlerService.sendMessage(m);*/
                        bmsg.putString("sender_id", sender_id);
                        if (extras.getString("tipo").equals("richiesta")){
                            bmsg.putInt("dialog", 1);
                            sendNotification(sender.getName() + " ha richiesto la tua posizione",sender_id);}
                        else{ bmsg.putInt("dialog", 0);
                            sendNotification(sender.getName() + " ti ha fornito la sua posizione","");
                        }
                        m.setData(bmsg);
                        MainActivity.handlerService.sendMessage(m);
                    }


                } else Log.e("intent", "notifica rifiutata");
            }

        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg,String id_fb) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class).putExtra("id_fb",id_fb), PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.flare)
                        .setContentTitle("Flare Position Request")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}