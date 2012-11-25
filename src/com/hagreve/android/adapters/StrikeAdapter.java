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
package com.hagreve.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hagreve.android.R;
import com.hagreve.android.lib.Strike;


public class StrikeAdapter<T> extends ArrayAdapter<T> {

	private LayoutInflater inflater;
	private int resource;
	
	public StrikeAdapter(Context context, int resourceId, T[] objects) {
		super(context, resourceId, objects);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		resource = resourceId;
	}
	
	public StrikeAdapter(Context context, int resourceId) {
		super(context, resourceId);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		resource = resourceId;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View root;
		
		// Because fuck it, that's why
		String[] short_months = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
		
		Strike item = (Strike)getItem(position);
		if(convertView == null) {
			root = inflater.inflate(resource, null);
		} else {
			root = convertView;
		}
		
		TextView tv = (TextView)root.findViewById(R.id.company_name);
		tv.setText(item.getCompanyName());
		
		tv = (TextView)root.findViewById(R.id.strike_description);
		tv.setText(item.getDescription());
		
		tv = (TextView)root.findViewById(R.id.strike_date_day);
		tv.setText(String.valueOf(item.getStartDate().getDate()));
		
		tv = (TextView)root.findViewById(R.id.end_date);
		if(item.isAllDay()) {
			tv.setText(R.string.all_day_text);
		} else {
			tv.setText(getContext().getString(R.string.until_text) + " " + item.getEndDateString().split(" ")[0]);
		}
		
		tv = (TextView)root.findViewById(R.id.strike_date_month);
		tv.setText(short_months[item.getStartDate().getMonth()]);
		
		ImageView canceled = (ImageView)root.findViewById(R.id.canceled_pic);
		
		if(item.isCanceled()) {
			canceled.setVisibility(View.VISIBLE);
		} else {
			canceled.setVisibility(View.INVISIBLE);
		}
		
		return root;
	}
}
