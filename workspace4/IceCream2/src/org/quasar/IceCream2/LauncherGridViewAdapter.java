package org.quasar.IceCream2;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LauncherGridViewAdapter extends BaseAdapter
{

	private ArrayList<String> listType;
	private ArrayList<Integer> listFlag;
	private Activity activity;

	public LauncherGridViewAdapter(Activity activity, ArrayList<String> listType, ArrayList<Integer> listFlag)
	{
		super();
		this.listType = listType;
		this.listFlag = listFlag;
		this.activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder view;
		LayoutInflater inflator = activity.getLayoutInflater();

		if (convertView == null)
		{
			view = new ViewHolder();
			convertView = inflator.inflate(R.layout.icecream_launcher_gridview_row, null);

			view.txtViewTitle = (TextView) convertView.findViewById(R.id.textView1);
			view.imgViewFlag = (ImageView) convertView.findViewById(R.id.imageView1);

			convertView.setTag(view);
		}
		else
		{
			view = (ViewHolder) convertView.getTag();
		}

		view.txtViewTitle.setText(listType.get(position));
		view.imgViewFlag.setImageResource(listFlag.get(position));

		return convertView;
	}
	
	@Override
	public int getCount()
	{
		return listType.size();
	}

	@Override
	public String getItem(int position)
	{
		return listType.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	public static class ViewHolder
	{
		public ImageView imgViewFlag;
		public TextView txtViewTitle;
	}

}
