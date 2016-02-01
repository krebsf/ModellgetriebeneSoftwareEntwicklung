/**********************************************************************
* Filename: Entry.java
* Created: 2016/01/31
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream3.presentationLayer.Entry;

import org.quasar.IceCream3.R;
import org.quasar.IceCream3.MasterActivity;
import org.quasar.IceCream3.utils.Command;
import org.quasar.IceCream3.utils.CommandType;
import org.quasar.IceCream3.utils.CommandTargetLayer;
import org.quasar.IceCream3.utils.UtilNavigate;
import org.quasar.IceCream3.utils.ListFragmentController;
import org.quasar.IceCream3.utils.Transactions;
import org.quasar.IceCream3.utils.ListAdapter;
import org.quasar.IceCream3.utils.PropertyChangeEvent;
import org.quasar.IceCream3.utils.PropertyChangeListener;
import org.quasar.IceCream3.utils.NavigationBarFragment;
import org.quasar.IceCream3.utils.DetailFragment;
import org.quasar.IceCream3.businessLayer.Entry;
import org.quasar.IceCream3.businessLayer.Station;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;

import android.widget.EditText;
import android.widget.ListView;
import android.view.Menu;
import android.os.Bundle;
import android.widget.TextView;
import android.support.v4.app.FragmentTransaction;
import android.widget.DatePicker;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.app.Activity;
import android.util.Log;

public class EntryActivity extends MasterActivity implements
			ListFragmentController.Callbacks,
			EntryDetailFragment.Callbacks,
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
	private Entry clickedEntry;
	private Station station;
	private final String STATIONObject = "STATIONObject";
	private final String Station_EntryAssociation = "Station_EntryAssociation";
	
	public Bundle extras()
	{
		return getIntent().getExtras();
	}
	
	public void startActivity_ToONE(Entry entry)
	{
		if(!restarted)
		{
			if(ONCREATION)
				setDetailFragment(EntryDetailFragment.ARG_VIEW_NEW, null);
			else
				setDetailFragment(EntryDetailFragment.ARG_VIEW_DETAIL, entry);
		}
	}
	
	public <T> void startActivity_ToMANY(Collection<T> subSet, Bundle savedInstanceState)
	{
		if(!restarted)
		{
			if(subSet != null)
			{
				list_fragment.setListAdapter(new ListAdapter(this, new EntryListViewHolder(), subSet));
			}
			else
			{
				list_fragment.setListAdapter(new ListAdapter(this, new EntryListViewHolder(), Entry.allInstances()));
			}
		}
	}
	
	public void setDetailFragment(String View, Entry entry)
	{
		detail_fragment = new EntryDetailFragment();
		if(entry != null)
			UtilNavigate.replaceFragment(this, detail_fragment, R.id.entry_detail_container, UtilNavigate.setFragmentBundleArguments(View,entry.ID()));
		else
			UtilNavigate.replaceFragment(this, detail_fragment, R.id.entry_detail_container, UtilNavigate.setFragmentBundleArguments(View,0));
		
		if(!mTwoPane && AssociationEndMultiplicity != ToONE)
		{
			list_fragment.hide();
			((DetailFragment) detail_fragment).show();
		}
		showingDetail = true;
	}
	
	public void orientationSettings(Entry clickedEntry)
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
			setContentView(R.layout.entry_layout_twopane);
		}
		else
			setContentView(R.layout.entry_layout_onepane);
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if(savedInstanceState == null)
		{
			if (AssociationEndMultiplicity != ToONE)
			{
				list_fragment = new ListFragmentController();
				ft.add(R.id.entry_list_container, list_fragment);
			}
			navigation_bar = new EntryNavigationBarFragment();
			ft.add(R.id.entry_navigationbar_container, navigation_bar);
			ft.commit();
		}
		else
		{
			if (AssociationEndMultiplicity != ToONE)
				list_fragment = (ListFragmentController) getSupportFragmentManager().findFragmentById(R.id.entry_list_container);
			navigation_bar = (Fragment) getSupportFragmentManager().findFragmentById(R.id.entry_navigationbar_container);
			detail_fragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.entry_detail_container);
			showingDetail = savedInstanceState.getBoolean(SHOWINGDetail);
			restarted = savedInstanceState.getBoolean("RESTARTED");
		}
		
		
		if(ONCREATION)
			Transactions.StartTransaction();
		
		if(extras() != null)
		{
			if(extras().containsKey(Station_EntryAssociation))
			{
				station = Station.getStation((Integer)extras().getInt(STATIONObject));
				AssociationEndName = extras().getString(Station_EntryAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
					startActivity_ToMANY((Set<Entry>) station.records(), extras());
				Station.getAccess().setChangeListener(this);
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
			Entry.getAccess().setChangeListener(list_fragment);
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
				clickedEntry = (Entry) list_fragment.getListAdapter().getItem(list_fragment.getSelectedPosition());
				
			}
			
		}
		((NavigationBarFragment)navigation_bar).setViewingObject(clickedEntry);
		orientationSettings(clickedEntry);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Transactions.addSpecialCommand(new Command(EntryActivity.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
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
			Transactions.addSpecialCommand(new Command(EntryActivity.class, CommandType.BACK, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			super.onBackPressed();
		}
	}
	
	@Override
	public void onDestroy()
	{
		Entry.getAccess().removeChangeListener(list_fragment);
		super.onDestroy();
	}
	
	@Override
	public void onItemSelected(int listPos, Object object, boolean mSinglePanelongclick)
	{
		if(object instanceof Entry)
		{
			this.clickedEntry = (Entry) object;
			((NavigationBarFragment)navigation_bar).setViewingObject(clickedEntry);
			
			if (mTwoPane)
			{
				setDetailFragment(EntryDetailFragment.ARG_VIEW_DETAIL, clickedEntry);
			}
			else if(mSinglePanelongclick)
			{
				setDetailFragment(EntryDetailFragment.ARG_VIEW_DETAIL, clickedEntry);
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
				setDetailFragment(EntryDetailFragment.ARG_VIEW_NEW, null);
				break;
			case R.id.menu_confirm_oncreation:
				if(ONCREATION)
				{
					if(list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
					{
						if(station != null)
						{
							setResult(Activity.RESULT_OK, new Intent().putExtra(Station_EntryAssociation, clickedEntry.ID()));
							finish();
						}
					}
					else
						UtilNavigate.showWarning(this, "Wrong action", "Error - In order to confirm a Entry you must have a Entry selected\nSolution - In order to finish the association action\n\t1 - select a Entry\n\t2 - if no Entry is available create a new one by means of the plus icon");
				}
				break;
			case R.id.menu_edit:
				if (AssociationEndMultiplicity == ToONE || list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
				{
					setDetailFragment(EntryDetailFragment.ARG_VIEW_EDIT,	clickedEntry);
				}
				break;
			case R.id.menu_delete:
				if(clickedEntry != null)
				{
					if(Transactions.StartTransaction())
					{
						if (AssociationEndMultiplicity == ToMANY && list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
						{
							clickedEntry.delete();
							list_fragment.show();
						}
						else
						{
							clickedEntry.delete();
							finish();
						}
						clickedEntry = null;
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
			clickedEntry.insertAssociation((Station) Station.getStation((Integer) data.getExtras().get(Station_EntryAssociation)), Station_EntryAssociation);
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
	public void onDetailOK(boolean isNew, Entry newEntry)
	{
		if(Transactions.StartTransaction())
		{
			if(isNew)
			{
				 newEntry.insert();
				
				if(station != null && AssociationEndName.equals(Station_EntryAssociation) && !Transactions.isCanceled())
				{
					station.insertAssociation(newEntry, Station_EntryAssociation);
					list_fragment.add(newEntry);
				}
			}
			else
			{
				 newEntry.update();
			}
			
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
			else
			{
				clickedEntry = newEntry;
				setDetailFragment(EntryDetailFragment.ARG_VIEW_DETAIL,clickedEntry);
				if(AssociationEndMultiplicity == ToMANY)
				{
					list_fragment.setActivatedPosition(clickedEntry);
					list_fragment.setSelection(clickedEntry);
				}
			}
		}
		else
		{
			if(ONCREATION)
			{
				clickedEntry = newEntry;
				clickedEntry.insert();
				if(station != null)
				{
					setResult(Activity.RESULT_OK, new Intent().putExtra(Station_EntryAssociation, clickedEntry.ID()));
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
			if (clickedEntry != null)
				setDetailFragment(EntryDetailFragment.ARG_VIEW_DETAIL,clickedEntry);
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
			if (clickedEntry != null)
				setDetailFragment(EntryDetailFragment.ARG_VIEW_DETAIL,clickedEntry);
			else
				getSupportFragmentManager().beginTransaction().detach(detail_fragment).commit();
		}
	}
	
	@Override
	public void addToList(Class<?> caller, Object object,  Object neibor)
	{
		if(caller == Entry.class && station == null)
			list_fragment.add(object);
		if(caller == Station.class && station != null && station.records() == neibor)
			list_fragment.add(object);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent)
	{
		//FALTA AKI
	}
	
}
