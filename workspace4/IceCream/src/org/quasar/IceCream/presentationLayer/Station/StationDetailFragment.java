/**********************************************************************
* Filename: Station.java
* Created: 2016/01/30
* @author Lu�s Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream.presentationLayer.Station;

import org.quasar.IceCream.R;
import org.quasar.IceCream.utils.Transactions;
import org.quasar.IceCream.utils.Command;
import org.quasar.IceCream.utils.CommandType;
import org.quasar.IceCream.utils.CommandTargetLayer;
import org.quasar.IceCream.utils.UtilNavigate;
import org.quasar.IceCream.utils.DetailFragment;
import org.quasar.IceCream.utils.PropertyChangeEvent;
import org.quasar.IceCream.utils.PropertyChangeListener;
import org.quasar.IceCream.businessLayer.Station;

import android.view.ViewGroup;
import android.widget.EditText;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.support.v4.app.Fragment;
import android.app.Activity;

public class StationDetailFragment extends Fragment implements PropertyChangeListener, DetailFragment{
	public static final String ARG_VIEW_DETAIL = "detail";
	public static final String ARG_VIEW_NEW = "new";
	public static final String ARG_VIEW_EDIT = "edit";
	public String ARG_VIEW = "";
	private Fragment fragment;
	private View rootView = null;
	private View activeView = null;
	private Station station = null;
	private int stationID = 0;
	private final String STATIONID = "STATIONID";
	private EditText nameView;
	private EditText targetView;
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public StationDetailFragment()
	{
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
		{
			station = Station.getStation(savedInstanceState.getInt(STATIONID));
			ARG_VIEW = savedInstanceState.getString("ARG_VIEW");
		}
		else
		{
			if(getArguments() != null)
			{
				if (getArguments().containsKey(ARG_VIEW_DETAIL))
				{
				Transactions.addSpecialCommand(new Command(StationDetailFragment.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					station = Station.getStation(getArguments().getInt(ARG_VIEW_DETAIL));
					if(station != null)
						stationID = station.ID();
					ARG_VIEW = ARG_VIEW_DETAIL;
				}
				if(getArguments().containsKey(ARG_VIEW_EDIT))
				{
				Transactions.addSpecialCommand(new Command(StationDetailFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					station = Station.getStation(getArguments().getInt(ARG_VIEW_EDIT));
					if(station != null)
						stationID = station.ID();
					ARG_VIEW = ARG_VIEW_EDIT;
				}
				if(getArguments().containsKey(ARG_VIEW_NEW))
				{
				Transactions.addSpecialCommand(new Command(StationDetailFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					ARG_VIEW = ARG_VIEW_NEW;
				}
			}
		}
		fragment = this;
		Station.getAccess().setChangeListener(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(station != null)
		{
			outState.putInt(STATIONID, station.ID());
			outState.putString("ARG_VIEW", ARG_VIEW);
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Station.getAccess().removeChangeListener(this);
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
		if(container == null && station == null)
		{
			return inflater.inflate(R.layout.default_blank_fragment, container, false);
		}
		if(getArguments() != null)
		{
			if (getArguments().containsKey(ARG_VIEW_DETAIL))
			{
				if (!getArguments().containsKey("IsChildFragment"))
					rootView = inflater.inflate(R.layout.station_view_detail, container, false);
				setViewDetailData();
			}
			if(getArguments().containsKey(ARG_VIEW_NEW) || getArguments().containsKey(ARG_VIEW_EDIT))
			{
				if (!getArguments().containsKey("IsChildFragment"))
					rootView = inflater.inflate(R.layout.station_view_insertupdate, container, false);
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
		if(getArguments().containsKey(ARG_VIEW_DETAIL))
		{
		}
		if(getArguments().containsKey(ARG_VIEW_EDIT))
		{
		}
		if(getArguments().containsKey(ARG_VIEW_NEW))
		{
		}
	}
	
	public void replaceObject(final String ARG_VIEW, final Station newStation)
	{
		 getActivity().runOnUiThread(new Runnable() {
			public void run() {
				station = newStation;
				stationID = newStation.ID();
				if(ARG_VIEW.equals(ARG_VIEW_DETAIL))
				{
					fragment.onSaveInstanceState(UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, newStation.ID()));
					setViewDetailData();
				}
				if(ARG_VIEW.equals(ARG_VIEW_EDIT))
				{
					fragment.onSaveInstanceState(UtilNavigate.setFragmentBundleArguments(ARG_VIEW_EDIT, newStation.ID()));
					setViewNewOrEditData();
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
		nameView = (EditText) activeView.findViewById(R.id.station_insertupdate_name_value);
		targetView = (EditText) activeView.findViewById(R.id.station_insertupdate_target_value);
	}
	
	public View setViewDetailData()
	{
		confirmActiveView();
		if (station != null)
		{
			((TextView) activeView.findViewById(R.id.station_detail_name_value)).setText("" + station.name());
		}
		return rootView;
	}
	
	public void ActionViewDetail()
	{
		confirmActiveView();
		if(station != null)
		{
			
		}
	}
	
	public View setViewNewOrEditData()
	{
		confirmActiveView();
		if (station != null)
		{
			setInput();
			nameView.setText("" + station.name());
			targetView.setText("" + station.target());
		}
		return rootView;
	}
	
	public Station ActionViewNew()
	{
		confirmActiveView();
		setInput();
		String temp_name = nameView.getText().toString();

		if (temp_name.equals("")){
			UtilNavigate.showWarning(getActivity(), "Text input error", "Error in input of name:\nThe input must have some text");
			return null;
		}
		
		int temp_target = 0;

		try{
			temp_target = Integer.parseInt(targetView.getText().toString());
		}catch(NumberFormatException e){
			UtilNavigate.showWarning(getActivity(), "Number Format Error", "Error in input:\n" + targetView.getText().toString());
			return null;
		}
		
		return new Station(temp_name, -1, temp_target);
	}
	
	public Station ActionViewEdit()
	{
		if (station != null)
		{
			confirmActiveView();
			String temp_name = nameView.getText().toString();

			if (!station.name().equals(nameView.getText().toString()))
				station.setName(temp_name);
			int temp_target = 0;

			if (station.target() != temp_target)
				station.setTarget(temp_target);
			return station;
		}
		else
		{
			return null;
		}
	}
	
	public interface Callbacks
	{
		public void onDetailOK(boolean isNew, Station newStation);
		public void onDetailCancel();
	}
	
	private Callbacks mCallbacks = new Callbacks()
	{
		public void onDetailOK(boolean isNew, Station newStation) {	}
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
					station = ActionViewNew();
					if(station != null)
					{
						mCallbacks.onDetailOK(true, station);
					}
				}
				if(getArguments().containsKey(ARG_VIEW_EDIT))
				{
					if(ActionViewEdit() != null)
						mCallbacks.onDetailOK(false, station);
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
					break;
					case UPDATE:
						if(stationID == event.getOldObjectID())
							station = (Station) event.getNewObject();
							stationID = station.ID();
							if (getArguments().containsKey(ARG_VIEW_DETAIL))
								setViewDetailData();
							else if(getArguments().containsKey(ARG_VIEW_EDIT))
								setViewNewOrEditData();
					break;
					case DELETE:
						if(stationID == event.getOldObjectID())
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
