package org.quasar.IceCream.utils;

import android.app.Activity;
import android.view.View;

public interface ListViewHolder {

	public View setNewViewHolder(Activity context, View convertView);
	public View setViewHolderContent(View convertView, Object object);
	public int getViewHolder();
	
}
