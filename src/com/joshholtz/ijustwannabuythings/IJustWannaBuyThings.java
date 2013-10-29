package com.joshholtz.ijustwannabuythings;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;

public class IJustWannaBuyThings {
	
	public final static String LOG_TAG = "IJustWannaBuyThings";

	public final static int REQUEST_BUY_INTENT = 1001;
	
	public static final int BILLING_RESPONSE_RESULT_OK = 0;
    public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
    public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
    public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
    public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;
    
    private Activity activity;
    private IJustWannaBuyThingsListener listener;

    public IJustWannaBuyThings(Activity activity, IJustWannaBuyThingsListener listener) {
    	this.activity = activity;
    	this.listener = listener;
    };
    
	/**
	 * Call this in your onCreate
	 * @param context
	 */
	public void onCreate() {
		this.activity.bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"), mServiceConn, Context.BIND_AUTO_CREATE);
	}

	/**
	 * Call this in your onActivityResult
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) { 
		if (requestCode == REQUEST_BUY_INTENT) {           
			int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
			String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
			String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

			if (resultCode == Activity.RESULT_OK) {
				try {
					JSONObject jo = new JSONObject(purchaseData);
					String sku = jo.getString("productId");
					Log.d("IJustWannaBuyThings", "You have bought the " + sku + ". Excellent choice, adventurer!");
					listener.onBuyAThing(responseCode, null);
				}
				catch (JSONException e) {
					Log.d("IJustWannaBuyThings", "Failed to parse purchase data.");
					e.printStackTrace();
					listener.onBuyAThing(-1, null);
				}
			} else {
				listener.onBuyAThing(-1, null);
			}
		}
	}

	/**
	 * Call this in your onDestroy
	 * @param context
	 */
	public void onDestroy() {
		if (mServiceConn != null) {
			this.activity.unbindService(mServiceConn);
		}   
	}
	
	/**
	 * 
	 * @author josh
	 *
	 */
	public abstract static class IJustWannaBuyThingsListener {
		public abstract void onQueryAllTheThings(int responseCode, ArrayList<JSONObject> responseList);
		public abstract void onBuyAThing(int responseCode, JSONObject purchasedData);
	}
	
	/**
	 * 
	 * @param context
	 * @param skuList
	 * @param listener
	 */
	public void queryAllTheThings(List<String> skuList) {
		final Bundle querySkus = new Bundle();
		querySkus.putStringArrayList("ITEM_ID_LIST", new ArrayList<String>(skuList));

		AsyncTask<Void, Void, Bundle> asyncTask = new AsyncTask<Void, Void, Bundle>() {

			@Override
			protected Bundle doInBackground(Void... params) {
				try {
					Bundle skuDetails = mService.getSkuDetails(3, activity.getPackageName(), "inapp", querySkus);
					return skuDetails;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Bundle skuDetails) {
				int responseCode = -1;
				ArrayList<JSONObject> responseList = new ArrayList<JSONObject>();
				
				if (skuDetails != null) {
					Log.d("IJustWannaBuyThings", "skuDetails response - " + skuDetails.getInt("RESPONSE_CODE"));
					responseCode = skuDetails.getInt("RESPONSE_CODE");
					
					if (responseCode == 0) {
						ArrayList<String> responseListString = skuDetails.getStringArrayList("DETAILS_LIST");
						for (String thisResponse : responseListString) {
							try {
								JSONObject object = new JSONObject(thisResponse);
								responseList.add(object);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							
						}
					}
				}
				
				listener.onQueryAllTheThings(responseCode, responseList);
			}
			
		};
		
		asyncTask.execute();
		
	}
	
	
	
	/**
	 * 
	 * @author josh
	 *
	 */
	public abstract static class BuyAThingListener {
		
	}
	
	/**
	 * 
	 * @param sku
	 * @param listener
	 */
	public void buyAThing(final String sku) {
		AsyncTask<Void, Void, Bundle> asyncTask = new AsyncTask<Void, Void, Bundle>() {

			@Override
			protected Bundle doInBackground(Void... params) {
				try {
					Bundle buyIntentBundle = mService.getBuyIntent(3, activity.getPackageName(), sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
					Log.d("IJustWannaBuyThings", "skuDetails response - " + buyIntentBundle.getInt("RESPONSE_CODE"));
					int response = buyIntentBundle.getInt("RESPONSE_CODE");
					if (response == 0) {
						return buyIntentBundle;
					} else {
						return null;
					}

				} catch (RemoteException e) {
					e.printStackTrace();
				}
				return null;
				
			}
			
			@Override
			protected void onPostExecute(Bundle buyIntentBundle) {
				if (buyIntentBundle != null) {
					PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
					try {
						activity.startIntentSenderForResult(pendingIntent.getIntentSender(),
								REQUEST_BUY_INTENT, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
								   Integer.valueOf(0));
					} catch (SendIntentException e) {
						e.printStackTrace();
					}
				} else {
					listener.onBuyAThing(-1, null);
				}
			}
			
		};
		
		asyncTask.execute();
	}

	/**
	 * Our service connection - and stuff
	 */
	private IInAppBillingService mService;
	private ServiceConnection mServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IInAppBillingService.Stub.asInterface(service);
		}
	};

}
