/**********************************************************************
* Filename: Address.java
* Created: 2016/01/30
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream.presentationLayer.Address;

import org.quasar.IceCream.R;
import org.quasar.IceCream.utils.Transactions;
import org.quasar.IceCream.utils.Command;
import org.quasar.IceCream.utils.CommandType;
import org.quasar.IceCream.utils.CommandTargetLayer;
import org.quasar.IceCream.MasterActivity;
import org.quasar.IceCream.utils.UtilNavigate;
import org.quasar.IceCream.utils.PropertyChangeEvent;
import org.quasar.IceCream.utils.PropertyChangeListener;
import org.quasar.IceCream.utils.NavigationBarFragment;
import org.quasar.IceCream.businessLayer.Address;
import org.quasar.IceCream.presentationLayer.Station.StationActivity;

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

public class AddressNavigationBarFragment extends Fragment implements PropertyChangeListener, NavigationBarFragment{

	private View rootView = null;
	private Address clickedAddress = null;
	private int clickedAddressID;
	private final String ADDRESSID = "ADDRESSID";
	private TextView number_objects;
	private View stationView;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public AddressNavigationBarFragment()
	{
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
		{
			clickedAddress = Address.getAddress(savedInstanceState.getInt(ADDRESSID));
		}
		Address.getAccess().setChangeListener(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(clickedAddress != null)
		{
			outState.putInt(ADDRESSID, clickedAddress.ID());
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Address.getAccess().removeChangeListener(this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		stationView = (View) getActivity().findViewById(R.id.address_navigationbar_association_station);
		stationView.setOnClickListener(ClickListener);
		stationView.setOnLongClickListener(LongClickListener);
		
		setViewingObject(clickedAddress);
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.address_view_navigationbar, container, false);
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
	
	public void setViewingObject(Object address)
	{
		if(address instanceof Address)
		{
			clickedAddress = (Address) address;
			if(address != null)
				clickedAddressID = ((Address) address).ID();
			refreshNavigationBar(clickedAddress);
		}
		if(address == null)
		{
			refreshNavigationBar(null);
		}
	}
	
	public void refreshNavigationBar(Address address)
	{
		if(address != null)
		{
			number_objects = (TextView) rootView.findViewById(R.id.address_navigationbar_association_station_numberobjects);
			if(address.station() == null)
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
			number_objects = (TextView) rootView.findViewById(R.id.address_navigationbar_association_station_numberobjects);
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
		if(clickedAddress.validStation())
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
			Transactions.addSpecialCommand(new Command(AddressNavigationBarFragment.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isClickable())
				{
					if(clickedAddress != null)
					{
						if(view.getId() == R.id.address_navigationbar_association_station)
						{
							UtilNavigate.toActivity(getActivity(), StationActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("ADDRESSObject", clickedAddress.ID(), "AssociationEndMultiplicityKey", 1, "Station_AddressAssociation", "Station_AddressAssociation"));
						}
					}
				}
				else
				{
					if(view.getId() == R.id.address_navigationbar_association_station)
					{
						if(clickedAddress != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Address does not have any station.\nYou can do a longclick to add new station to this Address");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Address selected.\nFirts you must select an Address");
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
			Transactions.addSpecialCommand(new Command(AddressNavigationBarFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isLongClickable())
				{
					if(clickedAddress != null)
					{
						if(view.getId() == R.id.address_navigationbar_association_station)
						{
							UtilNavigate.toActivityForResult(getActivity(), StationActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("ADDRESSObject", clickedAddress.ID(), "AssociationEndMultiplicityKey", -1, "Station_AddressAssociation", "Station_AddressAssociation"), MasterActivity.CREATION_CODE);
						}
					}else
						UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Address selected.\nFirts you must select an Address");
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
						setViewingObject((Address) event.getNewObject());
					break;
					case INSERT_ASSOCIATION:
						setViewingObject((Address) event.getNewObject());
					break;
					case UPDATE:
						//no need to change anything
					break;
					case DELETE:
						if(clickedAddressID == event.getOldObjectID())
							setViewingObject(null);
					break;
					case DELETE_ASSOCIATION:
						if(clickedAddressID == event.getOldObjectID())
							setViewingObject((Address) event.getNewObject());
					break;
					default:
					break;
				}
			}
		});
	}
	
}
