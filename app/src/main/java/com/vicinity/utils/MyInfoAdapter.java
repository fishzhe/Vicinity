package com.vicinity.utils;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.vicinity.R;

public class MyInfoAdapter implements InfoWindowAdapter{

	LayoutInflater inflater = null;
	public MyInfoAdapter(LayoutInflater inflater) {
		this.inflater = inflater;
	}
	@Override
	public View getInfoContents(Marker marker) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.info, null);
		
		TextView title = (TextView)v.findViewById(R.id.title);
		title.setText(marker.getTitle());
		TextView snippet = (TextView)v.findViewById(R.id.snippet);
		snippet.setText(marker.getSnippet());

        return v;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		// TODO Auto-generated method stub
		return null;
	}

}
