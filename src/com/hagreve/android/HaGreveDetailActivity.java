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

import com.hagreve.android.fragments.HaGreveDetailFragment;
import com.hagreve.android.lib.Strike;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ShareCompat;
import android.support.v4.app.ShareCompat.IntentBuilder;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class HaGreveDetailActivity extends FragmentActivity {
	
	//private static final String LOG_TAG = "HaGreveDetailActivity";
	
	private Strike strike;
	private IntentBuilder ibuilder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		toggleTheme();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_layout);
		
		HaGreveDetailFragment fragment = (HaGreveDetailFragment)getSupportFragmentManager().findFragmentById(R.id.detail_fragment);
		strike = (Strike)(getIntent().getExtras().getParcelable("strike"));
		fragment.updateDetail(strike);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.detail_menu, menu);
		
		MenuItem item = menu.findItem(R.id.detail_share_button);
		ibuilder = IntentBuilder.from(this);
    	String message = "A ver se consigo chegar ao trabalho, apesar desta greve... " + strike.getSourceUrl() + " #hagreve";
		ibuilder.setText(message);
    	ibuilder.setType("text/plain");
		ShareCompat.configureMenuItem(item, ibuilder);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            intent = new Intent(this, HaGreveActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	        case R.id.detail_source_button:
	        	if(strike.getSourceUrl() != null) {
	        		intent = new Intent(Intent.ACTION_VIEW);
	        		intent.setDataAndType(Uri.parse(strike.getSourceUrl()), "text/html");
	        		startActivity(intent);
	        	}
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void toggleTheme() {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	boolean use_light_theme = prefs.getBoolean("pref_theme", false);
    	int theme = use_light_theme ?  R.style.LightThemeSelector : R.style.DarkThemeSelector;
    	setTheme(theme);
    }
}
