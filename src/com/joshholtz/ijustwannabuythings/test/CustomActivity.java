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

public class CustomActivity extends Activity {

	private IJustWannaBuyThings iJustWannaBuyThings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Need to call IJustWannaBuyThings onCreate
		iJustWannaBuyThings = new IJustWannaBuyThings(this, listener);
		iJustWannaBuyThings.onCreate();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Need to call IJustWannaBuyThings onActivityResult
		if (requestCode == IJustWannaBuyThings.REQUEST_BUY_INTENT) {
			iJustWannaBuyThings.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// Need to call IJustWannaBuyThings onDestroy
		iJustWannaBuyThings.onDestroy();
	}

	/**
	 * This is our listener for all things I just wanna buy
	 */
	IJustWannaBuyThingsListener listener = new IJustWannaBuyThingsListener() {

		@Override
		public void onConnect() {}

		@Override
		public void onDisconnect() {}
		
		@Override
		public void onQueryAllTheThings(int responseCode, ArrayList<JSONObject> responseList) {
			// Just displaying the response
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
			// Just displaying the response
			if (responseCode == IJustWannaBuyThings.BILLING_RESPONSE_RESULT_OK) {
				Log.d(IJustWannaBuyThings.LOG_TAG, "BuyAThing - " + purchasedData.toString());
			} else {
				Log.d(IJustWannaBuyThings.LOG_TAG, "BuyAThing error occured - " + responseCode);
			}
		}

		@Override
		public void onWhatsMine(int responseCode, ArrayList<String> ownedSkus, ArrayList<JSONObject> purchaseDataList, ArrayList<String> signatureList) {
			// Just displaying owned skus
			for (String ownedSku : ownedSkus) {
				Log.d(IJustWannaBuyThings.LOG_TAG, "Owned SKU - " + ownedSku);
			}
			
			// Just display purchaseDataList
			for (JSONObject purchaseData : purchaseDataList) {
				Log.d(IJustWannaBuyThings.LOG_TAG, "Purchase Data - " + purchaseData);
			}
		}
		
	};
	
	/**
	 * An onClick handler to show all products
	 * @param view
	 */
	public void onClickShowMeThings(View view) {
		// Queries information about the supplied skus
		iJustWannaBuyThings.queryAllTheThings(Arrays.asList(new String[]{"bacon"}));
	}
	
	/**
	 * An onClick handler to buy a bacon
	 * @param view
	 */
	public void onClickBuyMeBacon(View view) {
		// Starts the purchase for a sku
		iJustWannaBuyThings.buyAThing("bacon");
	}
	
	/**
	 * An onClick handler to get what I bought
	 * @param view
	 */
	public void onClickWhatsMine(View view) {
		// Starts request to get whats mine
		iJustWannaBuyThings.whatsMine();
	}

}
