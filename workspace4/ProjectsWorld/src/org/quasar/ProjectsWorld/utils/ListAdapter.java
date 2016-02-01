package org.quasar.ProjectsWorld.utils;

import java.io.Serializable;
import java.util.Collection;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ListAdapter extends ArrayAdapter<Object> implements Serializable {

	private transient Activity activity;
	private ListViewHolder holder;

	public ListAdapter(Context context, ListViewHolder view, Object object) {// ...2One
		super(context, view.getViewHolder());
		this.activity = (Activity) context;
		this.holder = view;

//		int i = 0;
//		for (Iterator<?> iterator = ((Iterable<?>) object).iterator();
//				iterator.hasNext() && i < itemsToLoad; i++) {
////				iterator.hasNext();){
//			this.add(iterator.next());
//		}

		this.addAll((Collection<? extends Object>) object);
//		for(Iterator<?> iterator = ((Collection<?>) object).iterator(); iterator.hasNext();){
//			list.add(iterator.next());
//			this.add(iterator.next());
//		}

	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = holder.setNewViewHolder(activity, convertView);
		
		return holder.setViewHolderContent(convertView, getItem(position));
	}

//	public AsyncTask<Void, String, Collection<Object>> loadMoreListItems(
//			boolean loadingMore) {
//		if (this.getCount() < ((Collection<Object>) object).size()) {
//			AsyncTask<Void, String, Collection<Object>> task = new AsyncTask<Void, String, Collection<Object>>() {
//				@Override
//				protected Collection<Object> doInBackground(Void... params) {
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//					}
//					List<Object> temp = new java.util.ArrayList<Object>(itemsToLoad);
//					Object item = new Object();
//					// Get 15 new listitems
//					for (Iterator<?> iterator = ((Iterable<?>) object)
//							.iterator(); iterator.hasNext();) {
//						item = iterator.next();
//						if (adapter.getPosition(item) < 0)// if adapter don't
//															// contains the item
//							temp.add(item);
//						if (temp.size() == itemsToLoad)
//							break;
//					}
//					return temp;
//				}
//			}.execute();
//			return task;
//		}
//		return null;
//	}
}
