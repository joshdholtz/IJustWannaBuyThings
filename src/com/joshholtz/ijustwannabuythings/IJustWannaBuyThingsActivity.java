package com.joshholtz.ijustwannabuythings;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.joshholtz.ijustwannabuythings.IJustWannaBuyThings.IJustWannaBuyThingsListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public abstract class IJustWannaBuyThingsActivity extends Activity {
	
	private IJustWannaBuyThings iJustWannaBuyThings;

	public abstract void onQueryAllTheThings(int responseCode, ArrayList<JSONObject> responseList);
	public abstract void onBuyAThing(int responseCode, JSONObject purchasedData);
	public abstract void onWhatsMine(int responseCode, ArrayList<String> ownedSkus, ArrayList<JSONObject> purchaseDataList, ArrayList<String> signatureList);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
	
	public IJustWannaBuyThings getIJustWannaBuyThings() {
		return this.iJustWannaBuyThings;
	}
	
	
	public void queryAllTheThings(List<String> skuList) {
		iJustWannaBuyThings.queryAllTheThings(skuList);
	}
	
	public void buyAThing(String sku) {
		this.buyAThing(sku, "");
	}
	
	public void buyAThing(String sku, String developerPayload) {
		iJustWannaBuyThings.buyAThing(sku, developerPayload);
	}

	public void whatsMine() {
		iJustWannaBuyThings.whatsMine();
	}
	
	/**
	 * This is our listener for all things I just wanna buy
	 */
	IJustWannaBuyThingsListener listener = new IJustWannaBuyThingsListener() {

		@Override
		public void onQueryAllTheThings(int responseCode, ArrayList<JSONObject> responseList) {
			IJustWannaBuyThingsActivity.this.onQueryAllTheThings(responseCode, responseList);
		}

		@Override
		public void onBuyAThing(int responseCode, JSONObject purchasedData) {
			IJustWannaBuyThingsActivity.this.onBuyAThing(responseCode, purchasedData);
		}

		@Override
		public void onWhatsMine(int responseCode, ArrayList<String> ownedSkus, ArrayList<JSONObject> purchaseDataList, ArrayList<String> signatureList) {
			IJustWannaBuyThingsActivity.this.onWhatsMine(responseCode, ownedSkus, purchaseDataList, signatureList);
		}
		
	};
}
