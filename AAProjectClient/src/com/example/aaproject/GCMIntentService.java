package com.example.aaproject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.aaproject.main.MainFragmentActivity;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService{

	public GCMIntentService(){} 

	// registration or unregistration 
	@Override
	protected void onError(Context context, String errorId) {
		Log.d("test", "errorId : "+errorId);

	}

	@Override
	protected void onMessage(Context context, Intent intent) {

		Vibrator vi = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		vi.vibrate(500);

		Bundle bundle = intent.getExtras();

		Iterator <String> iterator = bundle.keySet().iterator();
		while ( iterator.hasNext() ){
			String key = iterator.next();
			String value = bundle.get(key).toString();
			Log.d("GCM", "key : "+key+", value : "+value);
		}

		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(),MainFragmentActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Sets an ID for the notification, so it can be updated
		int notifyID = 3377; 
		NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
		.setContentTitle("New Message")
		.setContentText("You've received new messages.")
		.setSmallIcon(R.drawable.ic_plusone_small_off_client).setAutoCancel(true).setContentIntent(pendingIntent);
		int numMessages = 0; 
		// Start of a loop that processes data and then notifies the user ...
		try {
			mNotifyBuilder.setContentText(URLDecoder.decode(bundle.getString("message"), "UTF-8"))
			.setNumber(++numMessages);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}     
		// Because the ID remains unchanged, the existing notification is
		// updated.     
		mNotificationManager.notify(
				notifyID,
				mNotifyBuilder.build()); 


	}

	@Override
	protected void onRegistered(Context arg0, String regId) {
		Log.d("test", "onRegistered - regId : "+regId);

	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub

	}

}

