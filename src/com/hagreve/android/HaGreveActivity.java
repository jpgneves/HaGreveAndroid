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
package com.hagreve.android;

import com.hagreve.android.fragments.HaGreveListFragment;
import com.hagreve.android.lib.Strike;
import com.hagreve.android.service.HaGreveUpdateService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class HaGreveActivity extends FragmentActivity {
	
	private static final int PREF_ACTIVITY_CODE = 0x1;
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	HaGreveListFragment fragment = (HaGreveListFragment)getSupportFragmentManager().findFragmentById(R.id.list_fragment);
        	Parcelable[] items = intent.getParcelableArrayExtra("strikes");
        	Strike[] strikes = new Strike[items.length];
        	for(int i = 0; i < items.length; i++) {
        		strikes[i] = (Strike)items[i];
        	}
        	
        	fragment.updateList(strikes);
        }
    };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	toggleTheme(false);
    	maybeStartUpdateService();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	    switch (item.getItemId()) {
	        case R.id.refresh:
	        	HaGreveListFragment fragment = (HaGreveListFragment)getSupportFragmentManager().findFragmentById(R.id.list_fragment);
	        	fragment.refreshItems(item);
	            break;
	        case R.id.options:
	        	Intent intent = new Intent(this, HaGrevePreferencesActivity.class);
	        	startActivityForResult(intent, PREF_ACTIVITY_CODE);
	        	break;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    return true;
	}
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	if(requestCode == PREF_ACTIVITY_CODE) {
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    		toggleTheme(true);
    		toggleService(prefs.getBoolean("pref_conn_autoupdate_toggle", false));
    	}
    }
    
    private void toggleTheme(boolean from_prefs) {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	boolean use_light_theme = prefs.getBoolean("pref_theme", true);
    	int theme = use_light_theme ? R.style.LightThemeSelector : R.style.DarkThemeSelector;
    	setTheme(theme);
    	
    	// Ugly ugly hack
    	if(from_prefs) {
    		Intent intent = getIntent();
    		finish();
    		startActivity(intent);
    	}
    }
    
    private void maybeStartUpdateService() {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	toggleService(prefs.getBoolean("pref_conn_autoupdate_toggle", false));
    }
    
    private void toggleService(boolean run) {
    	Intent intent = new Intent(this, HaGreveUpdateService.class);
    	if(run && !isServiceRunning()) {
    		registerReceiver(broadcastReceiver, new IntentFilter(HaGreveUpdateService.BROADCAST_ACTION));
    		startService(intent);
    	} else if(!run && isServiceRunning()) {
    		unregisterReceiver(broadcastReceiver);
    		stopService(intent);
    	} else if(run && isServiceRunning()) {
    		// Restart service to use new refresh time
    		stopService(intent);
    		registerReceiver(broadcastReceiver, new IntentFilter(HaGreveUpdateService.BROADCAST_ACTION));
    		startService(intent);
    	}
    }
    
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.hagreve.android.service.HaGreveUpdateService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}