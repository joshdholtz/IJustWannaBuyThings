package com.joshholtz.ijustwannabuythings.test;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.vending.billing.IInAppBillingService;
import com.joshholtz.ijustwannabuythings.IJustWannaBuyThings;
import com.joshholtz.ijustwannabuythings.IJustWannaBuyThings.IJustWannaBuyThingsListener;
import com.joshholtz.ijustwannabuythings.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	private IJustWannaBuyThings iJustWannaBuyThings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		iJustWannaBuyThings = new IJustWannaBuyThings(this, listener);
		iJustWannaBuyThings.onCreate();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		if (requestCode == IJustWannaBuyThings.REQUEST_BUY_INTENT) {
			iJustWannaBuyThings.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		iJustWannaBuyThings.onDestroy();
	}
	
	public void onClickShowMeThings(View view) {
		iJustWannaBuyThings.queryAllTheThings(Arrays.asList(new String[]{"bacon"}));
	}
	
	public void onClickBuyMeBacon(View view) {
		iJustWannaBuyThings.buyAThing("bacon");
	}

	IJustWannaBuyThingsListener listener = new IJustWannaBuyThingsListener() {

		@Override
		public void onQueryAllTheThings(int responseCode, ArrayList<JSONObject> responseList) {
			if (responseCode == IJustWannaBuyThings.BILLING_RESPONSE_RESULT_OK) {
				for (JSONObject obj : responseList) {
					Log.d(IJustWannaBuyThings.LOG_TAG, "QueryAllTheThings - " + obj.toString());
				}
			} else {
				Log.d(IJustWannaBuyThings.LOG_TAG, "QueryAllTheThings error occured - " + responseCode);
			}
		}

		@Override
		public void onBuyAThing(int responseCode, JSONObject purchasedData) {
			if (responseCode == IJustWannaBuyThings.BILLING_RESPONSE_RESULT_OK) {
				Log.d(IJustWannaBuyThings.LOG_TAG, "BuyAThing - " + purchasedData.toString());
			} else {
				Log.d(IJustWannaBuyThings.LOG_TAG, "BuyAThing error occured - " + responseCode);
			}
		}
		
	};

}
