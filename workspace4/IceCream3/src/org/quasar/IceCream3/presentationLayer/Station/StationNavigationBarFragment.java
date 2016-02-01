/**********************************************************************
* Filename: Station.java
* Created: 2016/01/31
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream3.presentationLayer.Station;

import org.quasar.IceCream3.R;
import org.quasar.IceCream3.utils.Transactions;
import org.quasar.IceCream3.utils.Command;
import org.quasar.IceCream3.utils.CommandType;
import org.quasar.IceCream3.utils.CommandTargetLayer;
import org.quasar.IceCream3.MasterActivity;
import org.quasar.IceCream3.utils.UtilNavigate;
import org.quasar.IceCream3.utils.PropertyChangeEvent;
import org.quasar.IceCream3.utils.PropertyChangeListener;
import org.quasar.IceCream3.utils.NavigationBarFragment;
import org.quasar.IceCream3.businessLayer.Station;
import org.quasar.IceCream3.presentationLayer.Entry.EntryActivity;
import org.quasar.IceCream3.presentationLayer.Address.AddressActivity;

import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.graphics.drawable.TransitionDrawable;

public class StationNavigationBarFragment extends Fragment implements PropertyChangeListener, NavigationBarFragment{

	private View rootView = null;
	private Station clickedStation = null;
	private int clickedStationID;
	private final String STATIONID = "STATIONID";
	private TextView number_objects;
	private View recordsView;
	private View placeView;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public StationNavigationBarFragment()
	{
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
		{
			clickedStation = Station.getStation(savedInstanceState.getInt(STATIONID));
		}
		Station.getAccess().setChangeListener(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(clickedStation != null)
		{
			outState.putInt(STATIONID, clickedStation.ID());
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
		
		recordsView = (View) getActivity().findViewById(R.id.station_navigationbar_association_records);
		recordsView.setOnClickListener(ClickListener);
		recordsView.setOnLongClickListener(LongClickListener);
		
		placeView = (View) getActivity().findViewById(R.id.station_navigationbar_association_place);
		placeView.setOnClickListener(ClickListener);
		placeView.setOnLongClickListener(LongClickListener);
		
		setViewingObject(clickedStation);
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.station_view_navigationbar, container, false);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
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
	
	public void setViewingObject(Object station)
	{
		if(station instanceof Station)
		{
			clickedStation = (Station) station;
			if(station != null)
				clickedStationID = ((Station) station).ID();
			refreshNavigationBar(clickedStation);
		}
		if(station == null)
		{
			refreshNavigationBar(null);
		}
	}
	
	public void refreshNavigationBar(Station station)
	{
		if(station != null)
		{
			number_objects = (TextView) rootView.findViewById(R.id.station_navigationbar_association_records_numberobjects);
			if(station.records().isEmpty())
			{
				prepareView(recordsView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(recordsView, true);
				setNumberAssociation(number_objects, clickedStation.records().size());
			}
			
			number_objects = (TextView) rootView.findViewById(R.id.station_navigationbar_association_place_numberobjects);
			if(station.place() == null)
			{
				prepareView(placeView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(placeView, true);
				setNumberAssociation(number_objects, 1);
			}
			
			objectValidation();
		}
		else
		{
			number_objects = (TextView) rootView.findViewById(R.id.station_navigationbar_association_records_numberobjects);
			prepareView(recordsView, false);
			setNumberAssociation(number_objects, 0);
			recordsView.setBackgroundResource(R.drawable.navigationbar_selector);
			
			number_objects = (TextView) rootView.findViewById(R.id.station_navigationbar_association_place_numberobjects);
			prepareView(placeView, false);
			setNumberAssociation(number_objects, 0);
			placeView.setBackgroundResource(R.drawable.navigationbar_selector);
			
		}
	}
	
	public void prepareView(View view, boolean enable)
	{
		if(enable)
		{
			view.setClickable(true);
			view.setAlpha((float) 1);
		}
		else
		{
			view.setClickable(false);
			view.setAlpha((float) 0.5);
		}
	}
	
	public void setNumberAssociation(TextView view, int numberObjects)
	{
		view.setText("( " + numberObjects + " )");
	}
	
	public void objectValidation()
	{
		if(clickedStation.validRecords())
		{
			recordsView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			recordsView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
		if(clickedStation.validPlace())
		{
			placeView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			placeView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
	}
	
	OnClickListener ClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			Transactions.addSpecialCommand(new Command(StationNavigationBarFragment.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isClickable())
				{
					if(clickedStation != null)
					{
						if(view.getId() == R.id.station_navigationbar_association_records)
						{
							UtilNavigate.toActivity(getActivity(), EntryActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("STATIONObject", clickedStation.ID(), "AssociationEndMultiplicityKey", -1, "Station_EntryAssociation", "Station_EntryAssociation"));
						}
						if(view.getId() == R.id.station_navigationbar_association_place)
						{
							UtilNavigate.toActivity(getActivity(), AddressActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("STATIONObject", clickedStation.ID(), "AssociationEndMultiplicityKey", 1, "Station_AddressAssociation", "Station_AddressAssociation"));
						}
					}
				}
				else
				{
					if(view.getId() == R.id.station_navigationbar_association_records)
					{
						if(clickedStation != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Station does not have any records.\nYou can do a longclick to add new records to this Station");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Station selected.\nFirts you must select an Station");
					}
					if(view.getId() == R.id.station_navigationbar_association_place)
					{
						if(clickedStation != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Station does not have any place.\nYou can do a longclick to add new place to this Station");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Station selected.\nFirts you must select an Station");
					}
				}
			}
		}
	};
	
	OnLongClickListener LongClickListener = new OnLongClickListener()
	{
		@Override
		public boolean onLongClick(View view)
		{
			Transactions.addSpecialCommand(new Command(StationNavigationBarFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isLongClickable())
				{
					if(clickedStation != null)
					{
						if(view.getId() == R.id.station_navigationbar_association_records)
						{
							UtilNavigate.toActivityForResult(getActivity(), EntryActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("STATIONObject", clickedStation.ID(), "AssociationEndMultiplicityKey", -1, "Station_EntryAssociation", "Station_EntryAssociation"), MasterActivity.CREATION_CODE);
						}
						if(view.getId() == R.id.station_navigationbar_association_place)
						{
							UtilNavigate.toActivityForResult(getActivity(), AddressActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("STATIONObject", clickedStation.ID(), "AssociationEndMultiplicityKey", -1, "Station_AddressAssociation", "Station_AddressAssociation"), MasterActivity.CREATION_CODE);
						}
					}else
						UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Station selected.\nFirts you must select an Station");
				}
			}
			return false;
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
						setViewingObject((Station) event.getNewObject());
					break;
					case INSERT_ASSOCIATION:
						setViewingObject((Station) event.getNewObject());
					break;
					case UPDATE:
						//no need to change anything
					break;
					case DELETE:
						if(clickedStationID == event.getOldObjectID())
							setViewingObject(null);
					break;
					case DELETE_ASSOCIATION:
						if(clickedStationID == event.getOldObjectID())
							setViewingObject((Station) event.getNewObject());
					break;
					default:
					break;
				}
			}
		});
	}
	
}
