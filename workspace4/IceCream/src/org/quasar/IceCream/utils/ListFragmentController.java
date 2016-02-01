package org.quasar.IceCream.utils;

import org.quasar.IceCream.R;
import org.quasar.IceCream.utils.ListAdapter;
import org.quasar.IceCream.utils.PropertyChangeEvent;
import org.quasar.IceCream.utils.PropertyChangeListener;
import org.quasar.IceCream.businessLayer.ModelMusts;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;

public class ListFragmentController extends ListFragment implements PropertyChangeListener {
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private Callbacks mCallbacks = new Callbacks() {
		public void onItemSelected(int listPos, Object objectHashCode, boolean twopane) {	}
		public void addToList(Class<?> source, Object oldValue, Object newValue){	}
	};
	private int mActivatedPosition = ListView.INVALID_POSITION;
	
	private int Choice_Mode = 0;
	private final String CHOICE_MODE = "CHOICE_MODE"; 
	
	private boolean TwoPane = true;
	private final String TWOPANE = "TWOPANE";
	
	private ListAdapter adapter;
	private final String LISTADAPTER = "LISTADAPTER";
	
	public interface Callbacks {
		public void onItemSelected(int listPos, Object objectHashCode, boolean twopane);
		public void addToList(Class<?> source, Object oldValue, Object newValue);
	}
	
	public void setListAdapter(ListAdapter adapter){
		super.setListAdapter(adapter);
		this.adapter = adapter;
	}
	
	public ListFragmentController(){
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		Log.i("list", "activity created - " + this.getId());
		if(savedInstanceState != null){
			Log.i("list", "activity created fragment saved - " + this.getId());
			adapter = (ListAdapter) savedInstanceState.get(LISTADAPTER);
			TwoPane = (Boolean) savedInstanceState.get(TWOPANE);
			setListAdapter(adapter);
//			adapter.setList((ArrayList<Object>) savedInstanceState.get(ADAPTERLIST));
			Choice_Mode = savedInstanceState.getInt(CHOICE_MODE);
			getListView().setChoiceMode(Choice_Mode);
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
		if (TwoPane)
			getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0,	View arg1, int position, long arg3) {
					mCallbacks.onItemSelected(position, getListAdapter().getItem(position), true);
					setActivatedPosition(position);
					return true;
			}
		});
		
	}
//	private View footerView;
//	private boolean loadingMore = false;
	public void hide(){
		if(getView() != null)
			((View) getView().getParent()).setVisibility(android.view.View.GONE);
	}
	public void show(){
		if(getView() != null)
			((View) getView().getParent()).setVisibility(android.view.View.VISIBLE);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if (mActivatedPosition != ListView.INVALID_POSITION)
			getListView().smoothScrollToPosition(mActivatedPosition);
		else
			getListView().smoothScrollToPosition(ListView.INVALID_POSITION);
		((ArrayAdapter<Object>) getListAdapter()).notifyDataSetChanged();
		
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)){
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
		
//		footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.default_view_listfooter_loader, null, false);
//
//		
//		
//		getListView().setOnScrollListener(new OnScrollListener(){
//			//useless here, skip!
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {}
//			
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//				int visibleItemCount, int totalItemCount) {
//				//what is the bottom iten that is visible
//				int lastInScreen = firstVisibleItem + visibleItemCount;            
//				 
//				//is the bottom item visible & not loading more already ? Load more !
//				if((getListAdapter() != null && lastInScreen == getListAdapter().getCount()) && !loadingMore){
//					final AsyncTask<Void, String, Collection<Object>> temp = adapter.loadMoreListItems(loadingMore);
//					if(temp != null){
//					loadingMore = true;
//					getListView().addFooterView(footerView);
//					getActivity().runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							try {
//								if (!temp.get().isEmpty()) {
//									try {
//										for (Object obj : temp.get())
//											adapter.add(obj);
//									} catch (InterruptedException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									} catch (ExecutionException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//								}
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (ExecutionException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							loadingMore = false;
//							getListView().removeFooterView(footerView);
//						}
//					});
//					
//					adapter.notifyDataSetChanged();
//					}
//				}	
//			}	
//		});
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof Callbacks))
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		mCallbacks = (Callbacks) activity;
		
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	public int getSelectedPosition(){
		return mActivatedPosition;
	}
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		if (mActivatedPosition != ListView.INVALID_POSITION)
			getListView().getAdapter().getView(mActivatedPosition, view, listView).setActivated(false);
		getListView().getAdapter().getView(position, view, listView).setActivated(true);
		mActivatedPosition = position;
		setActivatedPosition(position);
		mCallbacks.onItemSelected(position, getListAdapter().getItem(position), false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION)
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		outState.putSerializable(LISTADAPTER, adapter);
		outState.putBoolean(TWOPANE, TwoPane);
		outState.putInt(CHOICE_MODE, Choice_Mode);
		
	}

	public void setActivateOnItemClick(boolean activateOnItemClick) {
		Choice_Mode = activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE;
		getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	public void setActivatedPosition(int position) {
		switch (position) {
        case ListView.INVALID_POSITION:
        	getListView().setItemChecked(mActivatedPosition, false);
            break;
        default: 
        	getListView().setItemChecked(position, true);
        	getListView().smoothScrollToPosition(position);
        	break;
		}
		mActivatedPosition = position;
	}

	@SuppressWarnings("unchecked")
	public void setActivatedPosition(Object object) {
		if(object == null)
        	getListView().setItemChecked(mActivatedPosition, false);
        else
        	getListView().setItemChecked(((ArrayAdapter<Object>) getListAdapter()).getPosition(object), true);
		mActivatedPosition = ((ArrayAdapter<Object>) getListAdapter()).getPosition(object);
	}
	
	public void setActivatedLongClick(boolean twoPane) {
		this.TwoPane = twoPane;
	}
	
	@SuppressWarnings("unchecked")
	public void setSelection(Object object){
		getListView().setSelection(((ArrayAdapter<Object>) getListAdapter()).getPosition(object));
		getListView().smoothScrollToPosition(((ArrayAdapter<Object>) getListAdapter()).getPosition(object));
	}
	
	@SuppressWarnings("unchecked")
	public void add(Object object){
		((ArrayAdapter<Object>) getListAdapter()).add(object);
	}

	@SuppressWarnings("unchecked")
	public void remove(Object object) {
		((ArrayAdapter<Object>) getListAdapter()).remove(object);
	}

	@SuppressWarnings("unchecked")
	public void replace(Object oldObject, Object newObject) {
		Log.i("", "sel pos" + getListView().getSelectedItemPosition());
		Log.i("", "getpod pos" + getPosition(newObject));
		((ArrayAdapter<Object>) getListAdapter()).insert(newObject,getPosition(oldObject));
		remove(oldObject);
	}
	
	@SuppressWarnings("unchecked")
	public int getPosition(Object object) {
		return ((ArrayAdapter<Object>) getListAdapter()).getPosition(object);
	}
	
	public Object getSelectedObject() {
		return getListView().getSelectedItem();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(final PropertyChangeEvent event) {		
		getActivity().runOnUiThread(new Runnable(){
			@Override
			public void run() {
				switch(event.getCommandType()){
					case DELETE:
						remove(event.getOldObject());
						mActivatedPosition = ListView.INVALID_POSITION;
						break;
					case UPDATE:
						//não funciona bem não detecta o oldobject
						//no metodo tive de fazer pelo getselectedposition
//						replace(event.getOldObject(),event.getNewObject());
						((ArrayAdapter<Object>) getListAdapter()).notifyDataSetChanged();
						break;
					case INSERT:
						mCallbacks.addToList(((ModelMusts) event.getSource()).getType(), event.getNewObject(), event.getNewNeibor());
						break;
					case DELETE_ASSOCIATION:
						((ArrayAdapter<Object>) getListAdapter()).notifyDataSetChanged();
						break;
					case INSERT_ASSOCIATION:
						((ArrayAdapter<Object>) getListAdapter()).notifyDataSetChanged();
						break;
					default:
						break;
				}
			}
		});
	}
}
