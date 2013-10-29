package com.joshholtz.ijustwannabuythings.test;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

import com.joshholtz.ijustwannabuythings.IJustWannaBuyThingsActivity;
import com.joshholtz.ijustwannabuythings.R;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SubclassedActivity extends IJustWannaBuyThingsActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
	public void onConnect() {
		
	}

	@Override
	public void onDisconnect() {
		
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
