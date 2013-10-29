# IJustWannaBuyThings
Sick of implementing raw Android In-app Billing APIs? Sick of using other developer's APIs that make heavy use of dependencies?

Me too.

## Not the most suckiest Android In-app Billing wrapper
I can't guarantee that this is the most complete or advance Android In-app Billing helper but it simple works for me - so suck it other helpers.

### Subclassing IJustWannaBuyThingsActivity

We didn't get this far yet - we just startd tonight

````java

// Give me a break

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

````java

public class MainActivity extends Activity {

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

}


````
