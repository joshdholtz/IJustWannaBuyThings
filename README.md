# IJustWannaBuyThings
Sick of implementing raw Android In-app Billing APIs? Sick of using other developer's APIs that make heavy use of dependencies?

Me too.

## Not the most suckiest Android In-app Billing wrapper
I can't guarantee that this is the most complete or advanced Android In-app Billing helper but it simply works for me - so suck it other helpers.

<b>Note:</b> Consumables and subscriptions are not included in this library yet

## Setup

1. Download JAR into your Android project's libs directory - [justwannabuythings-0.0.1.jar](https://github.com/joshdholtz/IJustWannaBuyThings/raw/master/builds/ijustwannabuythings-0.0.1.jar)
2. Add `<uses-permission android:name="com.android.vending.BILLING" />` to Manifest.xml

### Subclassing IJustWannaBuyThingsActivity - (The easiest way)

- Key components for setting up - override these methods
  - onQueryAllTheThings(int responseCode, ArrayList<JSONObject> responseList)
  - onBuyAThing(int responseCode, JSONObject purchasedData)
  - onWhatsMine(int responseCode, ArrayList<String> ownedSkus, ArrayList<JSONObject> purchaseDataList, ArrayList<String> signatureList) {
- Key components for interacting
  - this.queryAllTheThings(Arrays.asList(new String[]{"bacon"}));
  - this.buyAThing("bacon");
  - this.whatsMine();

````java

public class SubclassedActivity extends IJustWannaBuyThingsActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
	public void onQueryAllTheThings(int responseCode, ArrayList<JSONObject> responseList) {
		Log.d("IJustWannaBuyThings", Arrays.toString(responseList.toArray()));
	}


	@Override
	public void onBuyAThing(int responseCode, JSONObject purchasedData) {
		 Log.d("IJustWannaBuyThings", purchasedData.toString());
	}


	@Override
	public void onWhatsMine(int responseCode, ArrayList<String> ownedSkus, ArrayList<JSONObject> purchaseDataList, ArrayList<String> signatureList) {
		Log.d("IJustWannaBuyThings", Arrays.toString(ownedSkus.toArray()));
	}
	
	
	/**
	 * An onClick handler to show all products
	 * @param view
	 */
	public void onClickShowMeThings(View view) {
		// Queries information about the supplied skus
		this.queryAllTheThings(Arrays.asList(new String[]{"bacon"}));
	}
	
	/**
	 * An onClick handler to buy a bacon
	 * @param view
	 */
	public void onClickBuyMeBacon(View view) {
		// Starts the purchase for a sku
		this.buyAThing("bacon");
	}
	
	/**
	 * An onClick get what I bought
	 * @param view
	 */
	public void onClickWhatsMine(View view) {
		// Starts request to get whats mine
		this.whatsMine();
	}

}

````

### Implementing in your own Activity

- Key components for setting up
  - iJustWannaBuyThings = new IJustWannaBuyThings(this, listener);
  - iJustWannaBuyThings.onCreate();
  - iJustWannaBuyThings.onActivityResult(requestCode, resultCode, data);
  - iJustWannaBuyThings.onDestroy();
- Key components for interacting
  - iJustWannaBuyThings.queryAllTheThings(Arrays.asList(new String[]{"bacon"}));
  - iJustWannaBuyThings.buyAThing("bacon");
  - iJustWannaBuyThings.whatsMine();

````java

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


````
