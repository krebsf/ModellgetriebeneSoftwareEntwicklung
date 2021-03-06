/**********************************************************************
* Filename: Entry.java
* Created: 2016/01/31
* @author Lu�s Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream3.presentationLayer.Entry;

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
import org.quasar.IceCream3.businessLayer.Entry;
import org.quasar.IceCream3.presentationLayer.Station.StationActivity;

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

public class EntryNavigationBarFragment extends Fragment implements PropertyChangeListener, NavigationBarFragment{

	private View rootView = null;
	private Entry clickedEntry = null;
	private int clickedEntryID;
	private final String ENTRYID = "ENTRYID";
	private TextView number_objects;
	private View stationView;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public EntryNavigationBarFragment()
	{
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
		{
			clickedEntry = Entry.getEntry(savedInstanceState.getInt(ENTRYID));
		}
		Entry.getAccess().setChangeListener(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(clickedEntry != null)
		{
			outState.putInt(ENTRYID, clickedEntry.ID());
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
		
		stationView = (View) getActivity().findViewById(R.id.entry_navigationbar_association_station);
		stationView.setOnClickListener(ClickListener);
		stationView.setOnLongClickListener(LongClickListener);
		
		setViewingObject(clickedEntry);
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.entry_view_navigationbar, container, false);
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
	
	public void setViewingObject(Object entry)
	{
		if(entry instanceof Entry)
		{
			clickedEntry = (Entry) entry;
			if(entry != null)
				clickedEntryID = ((Entry) entry).ID();
			refreshNavigationBar(clickedEntry);
		}
		if(entry == null)
		{
			refreshNavigationBar(null);
		}
	}
	
	public void refreshNavigationBar(Entry entry)
	{
		if(entry != null)
		{
			number_objects = (TextView) rootView.findViewById(R.id.entry_navigationbar_association_station_numberobjects);
			if(entry.station() == null)
			{
				prepareView(stationView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(stationView, true);
				setNumberAssociation(number_objects, 1);
			}
			
			objectValidation();
		}
		else
		{
			number_objects = (TextView) rootView.findViewById(R.id.entry_navigationbar_association_station_numberobjects);
			prepareView(stationView, false);
			setNumberAssociation(number_objects, 0);
			stationView.setBackgroundResource(R.drawable.navigationbar_selector);
			
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
		if(clickedEntry.validStation())
		{
			stationView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			stationView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
	}
	
	OnClickListener ClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			Transactions.addSpecialCommand(new Command(EntryNavigationBarFragment.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isClickable())
				{
					if(clickedEntry != null)
					{
						if(view.getId() == R.id.entry_navigationbar_association_station)
						{
							UtilNavigate.toActivity(getActivity(), StationActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("ENTRYObject", clickedEntry.ID(), "AssociationEndMultiplicityKey", 1, "Station_EntryAssociation", "Station_EntryAssociation"));
						}
					}
				}
				else
				{
					if(view.getId() == R.id.entry_navigationbar_association_station)
					{
						if(clickedEntry != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Entry does not have any station.\nYou can do a longclick to add new station to this Entry");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Entry selected.\nFirts you must select an Entry");
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
			Transactions.addSpecialCommand(new Command(EntryNavigationBarFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isLongClickable())
				{
					if(clickedEntry != null)
					{
						if(view.getId() == R.id.entry_navigationbar_association_station)
						{
							UtilNavigate.toActivityForResult(getActivity(), StationActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("ENTRYObject", clickedEntry.ID(), "AssociationEndMultiplicityKey", -1, "Station_EntryAssociation", "Station_EntryAssociation"), MasterActivity.CREATION_CODE);
						}
					}else
						UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Entry selected.\nFirts you must select an Entry");
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
						setViewingObject((Entry) event.getNewObject());
					break;
					case INSERT_ASSOCIATION:
						setViewingObject((Entry) event.getNewObject());
					break;
					case UPDATE:
						setViewingObject((Entry) event.getNewObject());
					break;
					case DELETE:
						if(clickedEntryID == event.getOldObjectID())
							setViewingObject(null);
					break;
					case DELETE_ASSOCIATION:
						if(clickedEntryID == event.getOldObjectID())
							setViewingObject((Entry) event.getNewObject());
					break;
					default:
					break;
				}
			}
		});
	}
	
}
