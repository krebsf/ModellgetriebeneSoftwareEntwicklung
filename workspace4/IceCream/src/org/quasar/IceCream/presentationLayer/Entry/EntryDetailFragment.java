/**********************************************************************
* Filename: Entry.java
* Created: 2016/01/30
* @author Lu�s Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream.presentationLayer.Entry;

import org.quasar.IceCream.R;
import org.quasar.IceCream.utils.Transactions;
import org.quasar.IceCream.utils.Command;
import org.quasar.IceCream.utils.CommandType;
import org.quasar.IceCream.utils.CommandTargetLayer;
import org.quasar.IceCream.utils.UtilNavigate;
import org.quasar.IceCream.utils.DetailFragment;
import org.quasar.IceCream.utils.PropertyChangeEvent;
import org.quasar.IceCream.utils.PropertyChangeListener;
import org.quasar.IceCream.businessLayer.Entry;

import android.view.ViewGroup;
import android.widget.EditText;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;
import android.widget.DatePicker;
import android.view.View.OnClickListener;
import org.quasar.IceCream.businessLayer.CalendarDate;
import android.view.View;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import org.quasar.IceCream.presentationLayer.CalendarDate.CalendarDateDetailFragment;

public class EntryDetailFragment extends Fragment implements PropertyChangeListener, DetailFragment{
	public static final String ARG_VIEW_DETAIL = "detail";
	public static final String ARG_VIEW_NEW = "new";
	public static final String ARG_VIEW_EDIT = "edit";
	public String ARG_VIEW = "";
	private Fragment fragment;
	private View rootView = null;
	private View activeView = null;
	private Entry entry = null;
	private int entryID = 0;
	private final String ENTRYID = "ENTRYID";
	private CalendarDateDetailFragment dateView;
	private EditText actualView;
	private EditText varianceView;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public EntryDetailFragment()
	{
	}
	
	
	@Override
	public void onResume() 
	{
		super.onResume();
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
		{
			entry = Entry.getEntry(savedInstanceState.getInt(ENTRYID));
			ARG_VIEW = savedInstanceState.getString("ARG_VIEW");
		}
		else
		{
			if(getArguments() != null)
			{
				if (getArguments().containsKey(ARG_VIEW_DETAIL))
				{
				Transactions.addSpecialCommand(new Command(EntryDetailFragment.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					entry = Entry.getEntry(getArguments().getInt(ARG_VIEW_DETAIL));
					if(entry != null)
						entryID = entry.ID();
					ARG_VIEW = ARG_VIEW_DETAIL;
				}
				if(getArguments().containsKey(ARG_VIEW_EDIT))
				{
				Transactions.addSpecialCommand(new Command(EntryDetailFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					entry = Entry.getEntry(getArguments().getInt(ARG_VIEW_EDIT));
					if(entry != null)
						entryID = entry.ID();
					ARG_VIEW = ARG_VIEW_EDIT;
				}
				if(getArguments().containsKey(ARG_VIEW_NEW))
				{
				Transactions.addSpecialCommand(new Command(EntryDetailFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					ARG_VIEW = ARG_VIEW_NEW;
				}
				dateView = new CalendarDateDetailFragment();
			}
		}
		fragment = this;
		Entry.getAccess().setChangeListener(this);
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(entry != null)
		{
			outState.putInt(ENTRYID, entry.ID());
			outState.putString("ARG_VIEW", ARG_VIEW);
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Entry.getAccess().removeChangeListener(this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		if(getArguments() == null || !getArguments().containsKey(ARG_VIEW_DETAIL))
		{
			if (!(activity instanceof Callbacks))
			{
				throw new IllegalStateException("Activity must implement fragment's callbacks.");
			}
			mCallbacks = (Callbacks) activity;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		if(container == null && entry == null)
		{
			return inflater.inflate(R.layout.default_blank_fragment, container, false);
		}
		if(getArguments() != null)
		{
			if (getArguments().containsKey(ARG_VIEW_DETAIL))
			{
				if (!getArguments().containsKey("IsChildFragment"))
					rootView = inflater.inflate(R.layout.entry_view_detail, container, false);
				setViewDetailData();
			}
			if(getArguments().containsKey(ARG_VIEW_NEW) || getArguments().containsKey(ARG_VIEW_EDIT))
			{
				if (!getArguments().containsKey("IsChildFragment"))
					rootView = inflater.inflate(R.layout.entry_view_insertupdate, container, false);
				setViewNewOrEditData();
				if (!getArguments().containsKey("IsChildFragment"))
				{
					rootView.findViewById(R.id.okButton).setOnClickListener(ClickListener);
					rootView.findViewById(R.id.cancelButton).setOnClickListener(ClickListener);
					if(getArguments().containsKey(ARG_VIEW_NEW))
					{
						((TextView) rootView.findViewById(R.id.okButton)).setText("Create");
					}
					else
					{
						((TextView) rootView.findViewById(R.id.okButton)).setText("Update");
					}
				}
			}
		}
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		dateView = new CalendarDateDetailFragment();
		if(getArguments().containsKey(ARG_VIEW_DETAIL))
		{
			if(entry != null && entry.date() != null)
				UtilNavigate.replaceFragment(fragment, dateView, R.id.calendardate_detail_container_date, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, entry.date().ID(), true, R.id.calendardate_detail_container_date));
			else
				UtilNavigate.replaceFragment(fragment, dateView, R.id.calendardate_detail_container_date, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, 0, true, R.id.calendardate_detail_container_date));
		}
		if(getArguments().containsKey(ARG_VIEW_EDIT))
		{
			if(entry != null && entry.date() != null)
				UtilNavigate.replaceFragment(fragment, dateView, R.id.calendardate_detail_container_date, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_EDIT, entry.date().ID(), true, R.id.calendardate_detail_container_date));
			else
				UtilNavigate.replaceFragment(fragment, dateView, R.id.calendardate_detail_container_date, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_EDIT, 0, true, R.id.calendardate_detail_container_date));
		}
		if(getArguments().containsKey(ARG_VIEW_NEW))
		{
			UtilNavigate.replaceFragment(fragment, dateView, R.id.calendardate_detail_container_date, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_NEW, 0, true, R.id.calendardate_detail_container_date));
		}
	}
	
	public void replaceObject(final String ARG_VIEW, final Entry newEntry)
	{
		 getActivity().runOnUiThread(new Runnable() {
			public void run() {
				entry = newEntry;
				entryID = newEntry.ID();
				if(ARG_VIEW.equals(ARG_VIEW_DETAIL))
				{
					fragment.onSaveInstanceState(UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, newEntry.ID()));
					setViewDetailData();
					dateView.replaceObject(ARG_VIEW, entry.date());
				}
				if(ARG_VIEW.equals(ARG_VIEW_EDIT))
				{
					fragment.onSaveInstanceState(UtilNavigate.setFragmentBundleArguments(ARG_VIEW_EDIT, newEntry.ID()));
					setViewNewOrEditData();
					dateView.replaceObject(ARG_VIEW, entry.date());
				}
				if(ARG_VIEW.equals(ARG_VIEW_NEW))
				{
					fragment.onSaveInstanceState(UtilNavigate.setFragmentBundleArguments(ARG_VIEW_NEW, 0));
				}
			}
		});
	}
	
	@Override
	public void hide()
	{
		if(getView() != null)
			((View) getView().getParent()).setVisibility(android.view.View.GONE);
	}
	
	@Override
	public void show()
	{
		if(getView() != null)
			((View) getView().getParent()).setVisibility(android.view.View.VISIBLE);
	}
	
	@Override
	public boolean isGone()
	{
		if(((View) getView().getParent()).getVisibility() == android.view.View.GONE)
			return true;
		else
			return false;
	}
	
	private void confirmActiveView()
	{
		activeView = rootView;
		if(getArguments() != null && getArguments().containsKey("IsChildFragment"))
			activeView = getParentFragment().getView().findViewById(getArguments().getInt("container"));
	}
	
	public void setInput()
	{
		actualView = (EditText) activeView.findViewById(R.id.entry_insertupdate_actual_value);
	}
	
	public  View setViewDetailData()
	{
		confirmActiveView();
		if (entry != null)
		{
			((TextView) activeView.findViewById(R.id.entry_detail_actual_value)).setText("" + entry.actual());
			((TextView) activeView.findViewById(R.id.entry_detail_variance_value)).setText("" + entry.variance());
			setVariancColor();
		}
		return rootView;
	}
	
	//Author 
	//Tobias Franz
	public void setVariancColor(){
		TextView varianceTextView  = (TextView) activeView.findViewById(R.id.entry_detail_variance_value);
		
		if(Math.abs(entry.variance()) > 5){
			varianceTextView.setTextColor(Color.parseColor("#FF0000"));
		}else
		{
			varianceTextView.setTextColor(Color.parseColor("#00FF00"));
		}
		
	}
	
	
	public void ActionViewDetail()
	{
		confirmActiveView();
		if(entry != null)
		{
			
		}
	}
	
	public View setViewNewOrEditData()
	{
		confirmActiveView();
		if (entry != null)
		{
			setInput();
			actualView.setText("" + entry.actual());
			varianceView.setText(" "+ entry.variance());
		}
		return rootView;
	}
	
	public Entry ActionViewNew()
	{
		confirmActiveView();
		setInput();
		int temp_actual = 0;

		try{
			temp_actual = Integer.parseInt(actualView.getText().toString());
		}catch(NumberFormatException e){
			UtilNavigate.showWarning(getActivity(), "Number Format Error", "Error in input:\n" + actualView.getText().toString());
			return null;
		}
		
		CalendarDate temp_date = dateView.ActionViewNew();
		
		return new Entry(temp_actual, temp_date, -1 );
	}
	
	public Entry ActionViewEdit()
	{
		if (entry != null)
		{
			confirmActiveView();
			dateView.ActionViewEdit().setID();
			int temp_actual = 0;

			if (entry.actual() != temp_actual)
				entry.setActual(temp_actual);
			return entry;
		}
		else
		{
			return null;
		}
	}
	
	public interface Callbacks
	{
		public void onDetailOK(boolean isNew, Entry newEntry);
		public void onDetailCancel();
	}
	
	private Callbacks mCallbacks = new Callbacks()
	{
		public void onDetailOK(boolean isNew, Entry newEntry) {	}
		public void onDetailCancel() {	}
	};
	
	OnClickListener ClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(v.getId() == R.id.okButton)
			{
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				if(getActivity().getCurrentFocus() != null)
					inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				if(getArguments().containsKey(ARG_VIEW_NEW))
				{
					entry = ActionViewNew();
					if(entry != null)
					{
						mCallbacks.onDetailOK(true, entry);
					}
				}
				if(getArguments().containsKey(ARG_VIEW_EDIT))
				{
					if(ActionViewEdit() != null)
						mCallbacks.onDetailOK(false, entry);
				}
			}
			if(v.getId() == R.id.cancelButton)
			{
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				if(getActivity().getCurrentFocus() != null)
					inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				mCallbacks.onDetailCancel();
			}
		}
	};
	
	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				switch(event.getCommandType())
				{
					case INSERT:
						//no need to change anything
					break;
					case INSERT_ASSOCIATION:
						setViewDetailData();
					break;
					case UPDATE:
						

						if(entryID == event.getOldObjectID())
							entry = (Entry) event.getNewObject();
							entryID = entry.ID();
							if (getArguments().containsKey(ARG_VIEW_DETAIL))
								setViewDetailData();
							else if(getArguments().containsKey(ARG_VIEW_EDIT))
								setViewNewOrEditData();
					break;
					case DELETE:
						if(entryID == event.getOldObjectID())
						{
							FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
							ft.remove(fragment);
							ft.commit();
						}
					break;
					case DELETE_ASSOCIATION:
					break;
					default:
					break;
				}
			}
		});
	}
	
}
