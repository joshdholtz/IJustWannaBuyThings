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
		Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        // This line is requried to compile in Android 5.0 (L)
        intent.setPackage("com.android.vending");
        
        this.activity.bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
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
					JSONObject object = new JSONObject(purchaseData);
					String sku = object.getString("productId");
					Log.d("IJustWannaBuyThings", "You have bought the " + sku + ". Excellent choice, adventurer!");
					listener.onBuyAThing(responseCode, object);
				}
				catch (JSONException e) {
					Log.d("IJustWannaBuyThings", "Failed to parse purchase data.");
					e.printStackTrace();
					listener.onBuyAThing(-1, new JSONObject());
				}
			} else {
				listener.onBuyAThing(-1, new JSONObject());
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
		public abstract void onConnect();
		public abstract void onDisconnect();
		public abstract void onQueryAllTheThings(int responseCode, ArrayList<JSONObject> responseList);
		public abstract void onBuyAThing(int responseCode, JSONObject purchasedData);
		public abstract void onWhatsMine(int responseCode, ArrayList<String> ownedSkus, ArrayList<JSONObject> purchaseDataList, ArrayList<String> signatureList);
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
	 * @param sku
	 * @param listener
	 */
	public void buyAThing(final String sku) {
		this.buyAThing(sku, "");
	}
	
	public void buyAThing(final String sku, final String developerPayload) {
		AsyncTask<Void, Void, Bundle> asyncTask = new AsyncTask<Void, Void, Bundle>() {

			@Override
			protected Bundle doInBackground(Void... params) {
				try {
					Bundle buyIntentBundle = mService.getBuyIntent(3, activity.getPackageName(), sku, "inapp", developerPayload);
					Log.d("IJustWannaBuyThings", "buyAThing response - " + buyIntentBundle.getInt("RESPONSE_CODE"));
					return buyIntentBundle;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				return null;

			}

			@Override
			protected void onPostExecute(Bundle buyIntentBundle) {
				if (buyIntentBundle != null) {
					int responseCode = -1;
					responseCode = buyIntentBundle.getInt("RESPONSE_CODE");
					
					PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
					if (responseCode ==0) {
						try {
							if (activity == null) {
								Log.d("IJustWannaBuyThings", "activity is null");
							} else {
								Log.d("IJustWannaBuyThings", "activity " + activity.toString());
							}
							
							activity.startIntentSenderForResult(pendingIntent.getIntentSender(), REQUEST_BUY_INTENT, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
							return;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					listener.onBuyAThing(responseCode, new JSONObject());
					
				} else {
					listener.onBuyAThing(-1, new JSONObject());
				}
			}

		};

		asyncTask.execute();
	}

	public void whatsMine() {
		ArrayList<String> ownedSkus = new ArrayList<String>();
		ArrayList<JSONObject> purchaseDataList = new ArrayList<JSONObject>();
		ArrayList<String> signatureList = new ArrayList<String>();
		this.whatsMine(ownedSkus, purchaseDataList, signatureList);
	}

	private void whatsMine(final ArrayList<String> ownedSkus, final ArrayList<JSONObject> purchaseDataList, final ArrayList<String> signatureList) {
		AsyncTask<Void, Void, Bundle> asyncTask = new AsyncTask<Void, Void, Bundle>() {

			@Override
			protected Bundle doInBackground(Void... params) {
				try {
					Bundle ownedItems = mService.getPurchases(3, activity.getPackageName(), "inapp", null);
					return ownedItems;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				return null;

			}

			@Override
			protected void onPostExecute(Bundle ownedItems) {
				int responseCode = -1;

				if (ownedItems != null) {
					responseCode = ownedItems.getInt("RESPONSE_CODE");

					ArrayList<String> ownedSkusString = 
							ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
					ArrayList<String> purchaseDataListString = 
							ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
					ArrayList<String> signatureListString = 
							ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE");
					String continuationToken = 
							ownedItems.getString("INAPP_CONTINUATION_TOKEN");

					for (int i = 0; i < purchaseDataListString.size(); ++i) {
						String purchaseData = purchaseDataListString.get(i);
						try {
							JSONObject object = new JSONObject(purchaseData);
							purchaseDataList.add(object);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						String signature = null;
						if (signatureListString != null) {
							signature = signatureListString.get(i);
							if (signature != null) {
								signatureList.add(signature);
							}
						}
						String sku = null;
						if (ownedSkusString != null) {
							sku = ownedSkusString.get(i);
							if (sku != null) {
								ownedSkus.add(sku);
							}
						}

					} 
					
					if (continuationToken != null) {
						whatsMine(ownedSkus, purchaseDataList, signatureList);
					} else {
						listener.onWhatsMine(responseCode, ownedSkus, purchaseDataList, signatureList);
					}

				} else {
					listener.onWhatsMine(responseCode, ownedSkus, purchaseDataList, signatureList);
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
			listener.onDisconnect();
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IInAppBillingService.Stub.asInterface(service);
			listener.onConnect();
		}
	};

}
