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

package com.hagreve.android.service;

import java.util.Timer;
import java.util.TimerTask;

import com.hagreve.android.lib.HaGreveApi;
import com.hagreve.android.lib.Strike;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class HaGreveUpdateService extends Service {

	private static long UPDATE_INTERVAL;
	private static final String DEFAULT_INTERVAL = "300000";
	private static Timer timer;
	private static final String LOG_TAG = "HaGreveUpdateService";
	public static final String BROADCAST_ACTION = "com.hagreve.android.service.update";
	
	private Intent updateIntent;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		UPDATE_INTERVAL = Long.valueOf(prefs.getString("pref_conn_autoupdate_interval", DEFAULT_INTERVAL));
		timer = new Timer();
		updateIntent = new Intent(BROADCAST_ACTION);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)  {
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				checkForUpdates();
			}
		}, UPDATE_INTERVAL, UPDATE_INTERVAL);
		
		return START_STICKY;
	}

	private void checkForUpdates() {
		ConnectivityManager conman = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo netinfo = conman.getActiveNetworkInfo();
		if(netinfo != null && netinfo.isConnected()) {
			Strike[] strikes = HaGreveApi.getStrikes();
			if(strikes.length > 0) {
				Log.d(LOG_TAG, "Greves actualizadas pelo serviço");
				updateIntent.putExtra("strikes", strikes);
				sendBroadcast(updateIntent);
			}
		} else {
			Log.d(LOG_TAG, "No network connection available");
		}
	}

}
