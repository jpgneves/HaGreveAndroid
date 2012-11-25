/*    Copyright 2012 João Neves, Carlos Fonseca, Filipe Cabecinhas

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.hagreve.android.fragments;

import java.util.Date;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hagreve.android.HaGreveDetailActivity;
import com.hagreve.android.R;
import com.hagreve.android.adapters.StrikeAdapter;
import com.hagreve.android.lib.HaGreveApi;
import com.hagreve.android.lib.Strike;


public class HaGreveListFragment extends ListFragment implements LoaderCallbacks<Strike[]> {

	private static final String LOG_TAG = "HaGreveListFragment";
	
	private StrikeAdapter<Strike> adapter = null;
	private MenuItem refreshItem;
	
	private View refreshView;

	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		refreshItems();
	}
	
	public void refreshItems() {
		refreshItems(null);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void refreshItems(MenuItem item) {
		ConnectivityManager conman = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo netinfo = conman.getActiveNetworkInfo();
		if(netinfo != null && netinfo.isConnected()) {
			try {
				//setListShown(false);
				if(item != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					refreshView = inflater.inflate(R.layout.action_refresh, null);
					refreshItem = item;
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						item.setActionView(refreshView);
					}
				}
				if(adapter == null) {
					adapter = new StrikeAdapter<Strike>(getActivity(), R.layout.strike_row);
					adapter.setNotifyOnChange(true);
					setListAdapter(adapter);
				}
				
				getLoaderManager().restartLoader(0, null, this);
			} catch (Exception e) {
				Log.e(LOG_TAG, e.toString());
			}
		} else {
			Log.d(LOG_TAG, "No network connection available");
			View progressbar = getView().findViewById(R.id.loading_progress);
			progressbar.setVisibility(View.INVISIBLE);
			TextView loading_text = (TextView)getView().findViewById(R.id.status_text);
			loading_text.setText(R.string.no_connection);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list_layout, container, false);
	}
	
	@Override
	public void onListItemClick(ListView list, View view, int position, long id) {
		showDetails(position);
	}
	
	private void showDetails(int position) {
		if(position < 0)
			return;
		
		if(getListAdapter().getCount() > 0 && position >= 0) {
			Intent intent = new Intent();
			intent.setClass(getActivity(), HaGreveDetailActivity.class);
			
			Strike item = (Strike)getListAdapter().getItem(position);
			
			intent.putExtra("strike", item);
			
			startActivity(intent);
		}
	}
	

	public Loader<Strike[]> onCreateLoader(int id, Bundle args) {
		return new StrikeLoader(getActivity());
	}


	public void onLoadFinished(Loader<Strike[]> loader, Strike[] items) {
		updateList(items);
	}


	public void onLoaderReset(Loader<Strike[]> arg0) {
		adapter.clear();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void updateList(Strike[] items) {
		View progressbar = getView().findViewById(R.id.loading_progress);
		TextView loading_text = (TextView)getView().findViewById(R.id.status_text);
		progressbar.setVisibility(View.INVISIBLE);
		if(items.length == 0) {
			loading_text.setText(R.string.no_strikes);
		} else {
			loading_text.setVisibility(View.INVISIBLE);
		}
		adapter.clear();
		Date today = new Date();
		for(int i = 0; i < items.length; i++) {
			// Guarantee we show a relevant start date, even if the strike is ongoing
			if(items[i].getStartDate().before(today))
				items[i].setStartDate(today);
			adapter.add(items[i]);
		}

		//setListShown(true);
		if(refreshItem != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			refreshItem.setActionView(null);
	}
}

class StrikeLoader extends AsyncTaskLoader<Strike[]> {
	
	public StrikeLoader(Context context) {
		super(context);
	}
	
	@Override
	public Strike[] loadInBackground() {
		return HaGreveApi.getStrikes();
	}
	
	protected void onStartLoading() {
		forceLoad();
	}
}