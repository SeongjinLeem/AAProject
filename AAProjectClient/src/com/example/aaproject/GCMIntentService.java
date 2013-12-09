package com.example.aaproject;

import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService{
	
	public GCMIntentService(){} 
	
	// registration or unregistration 
	@Override
	protected void onError(Context context, String errorId) {
		Log.d("test", "errorId : "+errorId);
		
	}

	@Override
	protected void onMessage(Context arg0, Intent intent) {
		
		Vibrator vi = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
	    vi.vibrate(500);
		
	    Bundle bundle = intent.getExtras();
		 
		Iterator <String> iterator = bundle.keySet().iterator();
		while ( iterator.hasNext() ){
			String key = iterator.next();
			String value = bundle.get(key).toString();
			Log.d("testtt", "key : "+key+", value : "+value);
		}
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

