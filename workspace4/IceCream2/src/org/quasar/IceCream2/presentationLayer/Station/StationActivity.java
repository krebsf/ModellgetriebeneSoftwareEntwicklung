/**********************************************************************
* Filename: Station.java
* Created: 2016/01/31
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream2.presentationLayer.Station;

import org.quasar.IceCream2.R;
import org.quasar.IceCream2.MasterActivity;
import org.quasar.IceCream2.utils.Command;
import org.quasar.IceCream2.utils.CommandType;
import org.quasar.IceCream2.utils.CommandTargetLayer;
import org.quasar.IceCream2.utils.UtilNavigate;
import org.quasar.IceCream2.utils.ListFragmentController;
import org.quasar.IceCream2.utils.Transactions;
import org.quasar.IceCream2.utils.ListAdapter;
import org.quasar.IceCream2.utils.PropertyChangeEvent;
import org.quasar.IceCream2.utils.PropertyChangeListener;
import org.quasar.IceCream2.utils.NavigationBarFragment;
import org.quasar.IceCream2.utils.DetailFragment;
import org.quasar.IceCream2.businessLayer.Station;
import org.quasar.IceCream2.businessLayer.Entry;
import org.quasar.IceCream2.businessLayer.Address;

import java.util.Set;
import java.util.Collection;

import android.widget.EditText;
import android.widget.ListView;
import android.view.Menu;
import android.os.Bundle;
import android.widget.TextView;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.app.Activity;
import android.util.Log;

public class StationActivity extends MasterActivity implements
			ListFragmentController.Callbacks,
			StationDetailFragment.Callbacks,
			PropertyChangeListener
{
	private boolean restarted = false;
	private boolean mTwoPane = false;
	private boolean showingDetail = false;
	private String AssociationEndMultiplicityKey = "AssociationEndMultiplicityKey";
	private int AssociationEndMultiplicity;
	private String AssociationEndNameKey = "AssociationEndNamekey";
	private String AssociationEndName;
	
	private Fragment navigation_bar;
	private ListFragmentController list_fragment;
	private Fragment detail_fragment;
	
	//Objects & Associations
	private Station clickedStation;
	private Entry entry;
	private final String ENTRYObject = "ENTRYObject";
	private final String Station_EntryAssociation = "Station_EntryAssociation";
	private Address address;
	private final String ADDRESSObject = "ADDRESSObject";
	private final String Station_AddressAssociation = "Station_AddressAssociation";
	
	public Bundle extras()
	{
		return getIntent().getExtras();
	}
	
	public void startActivity_ToONE(Station station)
	{
		if(!restarted)
		{
			if(ONCREATION)
				setDetailFragment(StationDetailFragment.ARG_VIEW_NEW, null);
			else
				setDetailFragment(StationDetailFragment.ARG_VIEW_DETAIL, station);
		}
	}
	
	public <T> void startActivity_ToMANY(Collection<T> subSet, Bundle savedInstanceState)
	{
		if(!restarted)
		{
			if(subSet != null)
			{
				list_fragment.setListAdapter(new ListAdapter(this, new StationListViewHolder(), subSet));
			}
			else
			{
				list_fragment.setListAdapter(new ListAdapter(this, new StationListViewHolder(), Station.allInstances()));
			}
		}
	}
	
	public void setDetailFragment(String View, Station station)
	{
		detail_fragment = new StationDetailFragment();
		if(station != null)
			UtilNavigate.replaceFragment(this, detail_fragment, R.id.station_detail_container, UtilNavigate.setFragmentBundleArguments(View,station.ID()));
		else
			UtilNavigate.replaceFragment(this, detail_fragment, R.id.station_detail_container, UtilNavigate.setFragmentBundleArguments(View,0));
		
		if(!mTwoPane && AssociationEndMultiplicity != ToONE)
		{
			list_fragment.hide();
			((DetailFragment) detail_fragment).show();
		}
		showingDetail = true;
	}
	
	public void orientationSettings(Station clickedStation)
	{
		if(!mTwoPane && AssociationEndMultiplicity != ToONE)
		{
			if(detail_fragment != null)
			{
				if(showingDetail)
				{
					list_fragment.hide();
					((DetailFragment) detail_fragment).show();
				}
				else
				{
					list_fragment.show();
					((DetailFragment) detail_fragment).hide();
				}
			}
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(AssociationEndMultiplicity != ToONE)
			outState.putBoolean(SHOWINGDetail, showingDetail);
		outState.putBoolean("RESTARTED", true);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if(extras() != null)
		{
			if(extras().containsKey(Station_EntryAssociation)
			 || extras().containsKey(Station_AddressAssociation)
			)
			{
				if(ONCREATION)
					AssociationEndMultiplicity = ToMANY;
				else 
					AssociationEndMultiplicity = extras().getInt(AssociationEndMultiplicityKey);
			}
		}
		else
		{
			//camed from launcher therefore AssociationEndMultiplicity = -1 (*) -> allInstances
			AssociationEndMultiplicity = ToMANY;
		}
		
		if (getResources().getBoolean(R.bool.has_two_panes) && AssociationEndMultiplicity != ToONE)
		{
			mTwoPane = true;
			setContentView(R.layout.station_layout_twopane);
		}
		else
			setContentView(R.layout.station_layout_onepane);
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if(savedInstanceState == null)
		{
			if (AssociationEndMultiplicity != ToONE)
			{
				list_fragment = new ListFragmentController();
				ft.add(R.id.station_list_container, list_fragment);
			}
			navigation_bar = new StationNavigationBarFragment();
			ft.add(R.id.station_navigationbar_container, navigation_bar);
			ft.commit();
		}
		else
		{
			if (AssociationEndMultiplicity != ToONE)
				list_fragment = (ListFragmentController) getSupportFragmentManager().findFragmentById(R.id.station_list_container);
			navigation_bar = (Fragment) getSupportFragmentManager().findFragmentById(R.id.station_navigationbar_container);
			detail_fragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.station_detail_container);
			showingDetail = savedInstanceState.getBoolean(SHOWINGDetail);
			restarted = savedInstanceState.getBoolean("RESTARTED");
		}
		
		
		if(ONCREATION)
			Transactions.StartTransaction();
		
		if(extras() != null)
		{
			if(extras().containsKey(Station_EntryAssociation))
			{
				entry = Entry.getEntry((Integer)extras().getInt(ENTRYObject));
				AssociationEndName = extras().getString(Station_EntryAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
				{
					mTwoPane = false;
					clickedStation = (Station)entry.station();
					startActivity_ToONE(clickedStation);
				}
			}
			if(extras().containsKey(Station_AddressAssociation))
			{
				address = Address.getAddress((Integer)extras().getInt(ADDRESSObject));
				AssociationEndName = extras().getString(Station_AddressAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
				{
					mTwoPane = false;
					clickedStation = (Station)address.station();
					startActivity_ToONE(clickedStation);
				}
			}
		}
		else
		{
			//camed from launcher therefore AssociationEndMultiplicity = -1 (*) -> allInstances
			startActivity_ToMANY(null, extras());
			AssociationEndMultiplicity = ToMANY;
		}
		
		if(AssociationEndMultiplicity != ToONE)
		{
			Station.getAccess().setChangeListener(list_fragment);
		}
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		if(AssociationEndMultiplicity != ToONE)
		{
			if (mTwoPane)
				list_fragment.setActivatedLongClick(false);
			else
				list_fragment.setActivatedLongClick(true);
			list_fragment.setActivateOnItemClick(true);
			
			if(list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
			{
				clickedStation = (Station) list_fragment.getListAdapter().getItem(list_fragment.getSelectedPosition());
				
			}
			
		}
		((NavigationBarFragment)navigation_bar).setViewingObject(clickedStation);
		orientationSettings(clickedStation);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Transactions.addSpecialCommand(new Command(StationActivity.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
		if(ONCREATION)
			Transactions.StartTransaction();
		restarted = false;
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
	}
	
	@Override
	public void onBackPressed()
	{
		if(AssociationEndMultiplicity != ToONE && !mTwoPane && detail_fragment != null && showingDetail)
		{
			((DetailFragment) detail_fragment).hide();
			((NavigationBarFragment) navigation_bar).show();
			list_fragment.show();
			showingDetail = false;
		}
		else
		{
			setResult(Activity.RESULT_CANCELED);
			Transactions.addSpecialCommand(new Command(StationActivity.class, CommandType.BACK, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			super.onBackPressed();
		}
	}
	
	@Override
	public void onDestroy()
	{
		Station.getAccess().removeChangeListener(list_fragment);
		super.onDestroy();
	}
	
	@Override
	public void onItemSelected(int listPos, Object object, boolean mSinglePanelongclick)
	{
		if(object instanceof Station)
		{
			this.clickedStation = (Station) object;
			((NavigationBarFragment)navigation_bar).setViewingObject(clickedStation);
			
			if (mTwoPane)
			{
				setDetailFragment(StationDetailFragment.ARG_VIEW_DETAIL, clickedStation);
			}
			else if(mSinglePanelongclick)
			{
				setDetailFragment(StationDetailFragment.ARG_VIEW_DETAIL, clickedStation);
			}
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_new:
				setDetailFragment(StationDetailFragment.ARG_VIEW_NEW, null);
				break;
			case R.id.menu_confirm_oncreation:
				if(ONCREATION)
				{
					if(list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
					{
						if(entry != null)
						{
							setResult(Activity.RESULT_OK, new Intent().putExtra(Station_EntryAssociation, clickedStation.ID()));
							finish();
						}
						if(address != null)
						{
							setResult(Activity.RESULT_OK, new Intent().putExtra(Station_AddressAssociation, clickedStation.ID()));
							finish();
						}
					}
					else
						UtilNavigate.showWarning(this, "Wrong action", "Error - In order to confirm a Station you must have a Station selected\nSolution - In order to finish the association action\n\t1 - select a Station\n\t2 - if no Station is available create a new one by means of the plus icon");
				}
				break;
			case R.id.menu_edit:
				if (AssociationEndMultiplicity == ToONE || list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
				{
					setDetailFragment(StationDetailFragment.ARG_VIEW_EDIT,	clickedStation);
				}
				break;
			case R.id.menu_delete:
				if(clickedStation != null)
				{
					if(Transactions.StartTransaction())
					{
						if (AssociationEndMultiplicity == ToMANY && list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
						{
							clickedStation.delete();
							list_fragment.show();
						}
						else
						{
							clickedStation.delete();
							finish();
						}
						clickedStation = null;
						if(!Transactions.StopTransaction())
							Transactions.ShowErrorMessage(this);
					}
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(Station_EntryAssociation))
		{
			clickedStation.insertAssociation((Entry) Entry.getEntry((Integer) data.getExtras().get(Station_EntryAssociation)), Station_EntryAssociation);
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
		}
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(Station_AddressAssociation))
		{
			clickedStation.insertAssociation((Address) Address.getAddress((Integer) data.getExtras().get(Station_AddressAssociation)), Station_AddressAssociation);
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
		}
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_CANCELED)
		{
			if (!ONCREATION)
			{
				if(!Transactions.StopTransaction())
					Transactions.ShowErrorMessage(this);
			}
		}
	}
	
	@Override
	public void onDetailOK(boolean isNew, Station newStation)
	{
		if(Transactions.StartTransaction())
		{
			if(isNew)
			{
				 newStation.insert();
				
				if(entry != null && AssociationEndName.equals(Station_EntryAssociation) && !Transactions.isCanceled())
				{
					entry.insertAssociation(newStation, Station_EntryAssociation);
					list_fragment.add(newStation);
				}
				if(address != null && AssociationEndName.equals(Station_AddressAssociation) && !Transactions.isCanceled())
				{
					address.insertAssociation(newStation, Station_AddressAssociation);
					list_fragment.add(newStation);
				}
			}
			else
			{
				 newStation.update();
			}
			
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
			else
			{
				clickedStation = newStation;
				setDetailFragment(StationDetailFragment.ARG_VIEW_DETAIL,clickedStation);
				if(AssociationEndMultiplicity == ToMANY)
				{
					list_fragment.setActivatedPosition(clickedStation);
					list_fragment.setSelection(clickedStation);
				}
			}
		}
		else
		{
			if(ONCREATION)
			{
				clickedStation = newStation;
				clickedStation.insert();
				if(entry != null)
				{
					setResult(Activity.RESULT_OK, new Intent().putExtra(Station_EntryAssociation, clickedStation.ID()));
					finish();
				}
				if(address != null)
				{
					setResult(Activity.RESULT_OK, new Intent().putExtra(Station_AddressAssociation, clickedStation.ID()));
					finish();
				}
			}
			else
			{
				//concurrency error
			}
		}
	}
	
	@Override
	public void onDetailCancel()
	{
	((NavigationBarFragment) navigation_bar).show();
		if (mTwoPane)
		{
			if (clickedStation != null)
				setDetailFragment(StationDetailFragment.ARG_VIEW_DETAIL,clickedStation);
			else
				getSupportFragmentManager().beginTransaction().detach(detail_fragment).commit();
		}
		else if(AssociationEndMultiplicity != ToONE)
		{
			((DetailFragment) detail_fragment).hide();
			list_fragment.show();
			showingDetail = false;
		}
		else
		{
			if (clickedStation != null)
				setDetailFragment(StationDetailFragment.ARG_VIEW_DETAIL,clickedStation);
			else
				getSupportFragmentManager().beginTransaction().detach(detail_fragment).commit();
		}
	}
	
	@Override
	public void addToList(Class<?> caller, Object object,  Object neibor)
	{
		if(caller == Station.class && entry == null && address == null)
			list_fragment.add(object);
		if(caller == Entry.class && entry != null && entry == neibor)
			list_fragment.add(object);
		if(caller == Address.class && address != null && address == neibor)
			list_fragment.add(object);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent)
	{
		//FALTA AKI
	}
	
}
