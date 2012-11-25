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

import com.hagreve.android.R;
import com.hagreve.android.lib.Strike;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HaGreveDetailFragment extends Fragment {
	
	//private static final String LOG_TAG = "HaGreveDetailFragment";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.detail_fragment_layout, container, false);
	}
	
	public void updateDetail(Parcelable parcelable) {
		Strike strike = (Strike)parcelable;
		
		TextView tv = (TextView)this.getView().findViewById(R.id.detail_dates_start_text);
		tv.setText(strike.getStartDateString().split(" ")[0]);
		
		tv = (TextView)this.getView().findViewById(R.id.detail_dates_end_text);
		if(strike.isAllDay()) {
			tv.setText(R.string.all_day_text);
		} else {
			tv.setText(this.getActivity().getString(R.string.until_text) + " " + strike.getEndDateString().split(" ")[0]);
		}
		
		tv = (TextView)this.getView().findViewById(R.id.detail_companies_text);
		tv.setText(strike.getCompanyName());
		
		tv = (TextView)this.getView().findViewById(R.id.detail_description_text);
		tv.setText(strike.getDescription());

	}

}
