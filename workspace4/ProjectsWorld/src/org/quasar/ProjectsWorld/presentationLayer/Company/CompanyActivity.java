/**********************************************************************
* Filename: Company.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.presentationLayer.Company;

import org.quasar.ProjectsWorld.R;
import org.quasar.ProjectsWorld.MasterActivity;
import org.quasar.ProjectsWorld.utils.Command;
import org.quasar.ProjectsWorld.utils.CommandType;
import org.quasar.ProjectsWorld.utils.CommandTargetLayer;
import org.quasar.ProjectsWorld.utils.UtilNavigate;
import org.quasar.ProjectsWorld.utils.ListFragmentController;
import org.quasar.ProjectsWorld.utils.Transactions;
import org.quasar.ProjectsWorld.utils.ListAdapter;
import org.quasar.ProjectsWorld.utils.PropertyChangeEvent;
import org.quasar.ProjectsWorld.utils.PropertyChangeListener;
import org.quasar.ProjectsWorld.utils.NavigationBarFragment;
import org.quasar.ProjectsWorld.utils.DetailFragment;
import org.quasar.ProjectsWorld.businessLayer.Company;
import org.quasar.ProjectsWorld.businessLayer.Worker;
import org.quasar.ProjectsWorld.businessLayer.Project;

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

public class CompanyActivity extends MasterActivity implements
			ListFragmentController.Callbacks,
			CompanyDetailFragment.Callbacks,
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
	private Company clickedCompany;
	private Worker worker;
	private final String WORKERObject = "WORKERObject";
	private final String EmploysAssociation = "EmploysAssociation";
	private Project project;
	private final String PROJECTObject = "PROJECTObject";
	private final String CarriesOutAssociation = "CarriesOutAssociation";
	
	public Bundle extras()
	{
		return getIntent().getExtras();
	}
	
	public void startActivity_ToONE(Company company)
	{
		if(!restarted)
		{
			if(ONCREATION)
				setDetailFragment(CompanyDetailFragment.ARG_VIEW_NEW, null);
			else
				setDetailFragment(CompanyDetailFragment.ARG_VIEW_DETAIL, company);
		}
	}
	
	public <T> void startActivity_ToMANY(Collection<T> subSet, Bundle savedInstanceState)
	{
		if(!restarted)
		{
			if(subSet != null)
			{
				list_fragment.setListAdapter(new ListAdapter(this, new CompanyListViewHolder(), subSet));
			}
			else
			{
				list_fragment.setListAdapter(new ListAdapter(this, new CompanyListViewHolder(), Company.allInstances()));
			}
		}
	}
	
	public void setDetailFragment(String View, Company company)
	{
		detail_fragment = new CompanyDetailFragment();
		if(company != null)
			UtilNavigate.replaceFragment(this, detail_fragment, R.id.company_detail_container, UtilNavigate.setFragmentBundleArguments(View,company.ID()));
		else
			UtilNavigate.replaceFragment(this, detail_fragment, R.id.company_detail_container, UtilNavigate.setFragmentBundleArguments(View,0));
		
		if(!mTwoPane && AssociationEndMultiplicity != ToONE)
		{
			list_fragment.hide();
			((DetailFragment) detail_fragment).show();
		}
		showingDetail = true;
	}
	
	public void orientationSettings(Company clickedCompany)
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
			if(extras().containsKey(EmploysAssociation)
			 || extras().containsKey(CarriesOutAssociation)
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
			setContentView(R.layout.company_layout_twopane);
		}
		else
			setContentView(R.layout.company_layout_onepane);
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if(savedInstanceState == null)
		{
			if (AssociationEndMultiplicity != ToONE)
			{
				list_fragment = new ListFragmentController();
				ft.add(R.id.company_list_container, list_fragment);
			}
			navigation_bar = new CompanyNavigationBarFragment();
			ft.add(R.id.company_navigationbar_container, navigation_bar);
			ft.commit();
		}
		else
		{
			if (AssociationEndMultiplicity != ToONE)
				list_fragment = (ListFragmentController) getSupportFragmentManager().findFragmentById(R.id.company_list_container);
			navigation_bar = (Fragment) getSupportFragmentManager().findFragmentById(R.id.company_navigationbar_container);
			detail_fragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.company_detail_container);
			showingDetail = savedInstanceState.getBoolean(SHOWINGDetail);
			restarted = savedInstanceState.getBoolean("RESTARTED");
		}
		
		
		if(ONCREATION)
			Transactions.StartTransaction();
		
		if(extras() != null)
		{
			if(extras().containsKey(EmploysAssociation))
			{
				worker = Worker.getWorker((Integer)extras().getInt(WORKERObject));
				AssociationEndName = extras().getString(EmploysAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
				{
					mTwoPane = false;
					clickedCompany = (Company)worker.employer();
					startActivity_ToONE(clickedCompany);
				}
			}
			if(extras().containsKey(CarriesOutAssociation))
			{
				project = Project.getProject((Integer)extras().getInt(PROJECTObject));
				AssociationEndName = extras().getString(CarriesOutAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
				{
					mTwoPane = false;
					clickedCompany = (Company)project.company();
					startActivity_ToONE(clickedCompany);
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
			Company.getAccess().setChangeListener(list_fragment);
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
				clickedCompany = (Company) list_fragment.getListAdapter().getItem(list_fragment.getSelectedPosition());
				
			}
			
		}
		((NavigationBarFragment)navigation_bar).setViewingObject(clickedCompany);
		orientationSettings(clickedCompany);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Transactions.addSpecialCommand(new Command(CompanyActivity.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
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
			Transactions.addSpecialCommand(new Command(CompanyActivity.class, CommandType.BACK, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			super.onBackPressed();
		}
	}
	
	@Override
	public void onDestroy()
	{
		Company.getAccess().removeChangeListener(list_fragment);
		super.onDestroy();
	}
	
	@Override
	public void onItemSelected(int listPos, Object object, boolean mSinglePanelongclick)
	{
		if(object instanceof Company)
		{
			this.clickedCompany = (Company) object;
			((NavigationBarFragment)navigation_bar).setViewingObject(clickedCompany);
			
			if (mTwoPane)
			{
				setDetailFragment(CompanyDetailFragment.ARG_VIEW_DETAIL, clickedCompany);
			}
			else if(mSinglePanelongclick)
			{
				setDetailFragment(CompanyDetailFragment.ARG_VIEW_DETAIL, clickedCompany);
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
				setDetailFragment(CompanyDetailFragment.ARG_VIEW_NEW, null);
				break;
			case R.id.menu_confirm_oncreation:
				if(ONCREATION)
				{
					if(list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
					{
						if(worker != null)
						{
							setResult(Activity.RESULT_OK, new Intent().putExtra(EmploysAssociation, clickedCompany.ID()));
							finish();
						}
						if(project != null)
						{
							setResult(Activity.RESULT_OK, new Intent().putExtra(CarriesOutAssociation, clickedCompany.ID()));
							finish();
						}
					}
					else
						UtilNavigate.showWarning(this, "Wrong action", "Error - In order to confirm a Company you must have a Company selected\nSolution - In order to finish the association action\n\t1 - select a Company\n\t2 - if no Company is available create a new one by means of the plus icon");
				}
				break;
			case R.id.menu_edit:
				if (AssociationEndMultiplicity == ToONE || list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
				{
					setDetailFragment(CompanyDetailFragment.ARG_VIEW_EDIT,	clickedCompany);
				}
				break;
			case R.id.menu_delete:
				if(clickedCompany != null)
				{
					if(Transactions.StartTransaction())
					{
						if (AssociationEndMultiplicity == ToMANY && list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
						{
							clickedCompany.delete();
							list_fragment.show();
						}
						else
						{
							clickedCompany.delete();
							finish();
						}
						clickedCompany = null;
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
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(EmploysAssociation))
		{
			clickedCompany.insertAssociation((Worker) Worker.getWorker((Integer) data.getExtras().get(EmploysAssociation)), EmploysAssociation);
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
		}
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(CarriesOutAssociation))
		{
			clickedCompany.insertAssociation((Project) Project.getProject((Integer) data.getExtras().get(CarriesOutAssociation)), CarriesOutAssociation);
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
	public void onDetailOK(boolean isNew, Company newCompany)
	{
		if(Transactions.StartTransaction())
		{
			if(isNew)
			{
				 newCompany.insert();
				
				if(worker != null && AssociationEndName.equals(EmploysAssociation) && !Transactions.isCanceled())
				{
					worker.insertAssociation(newCompany, EmploysAssociation);
					list_fragment.add(newCompany);
				}
				if(project != null && AssociationEndName.equals(CarriesOutAssociation) && !Transactions.isCanceled())
				{
					project.insertAssociation(newCompany, CarriesOutAssociation);
					list_fragment.add(newCompany);
				}
			}
			else
			{
				 newCompany.update();
			}
			
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
			else
			{
				clickedCompany = newCompany;
				setDetailFragment(CompanyDetailFragment.ARG_VIEW_DETAIL,clickedCompany);
				if(AssociationEndMultiplicity == ToMANY)
				{
					list_fragment.setActivatedPosition(clickedCompany);
					list_fragment.setSelection(clickedCompany);
				}
			}
		}
		else
		{
			if(ONCREATION)
			{
				clickedCompany = newCompany;
				clickedCompany.insert();
				if(worker != null)
				{
					setResult(Activity.RESULT_OK, new Intent().putExtra(EmploysAssociation, clickedCompany.ID()));
					finish();
				}
				if(project != null)
				{
					setResult(Activity.RESULT_OK, new Intent().putExtra(CarriesOutAssociation, clickedCompany.ID()));
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
			if (clickedCompany != null)
				setDetailFragment(CompanyDetailFragment.ARG_VIEW_DETAIL,clickedCompany);
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
			if (clickedCompany != null)
				setDetailFragment(CompanyDetailFragment.ARG_VIEW_DETAIL,clickedCompany);
			else
				getSupportFragmentManager().beginTransaction().detach(detail_fragment).commit();
		}
	}
	
	@Override
	public void addToList(Class<?> caller, Object object,  Object neibor)
	{
		if(caller == Company.class && worker == null && project == null)
			list_fragment.add(object);
		if(caller == Worker.class && worker != null && worker == neibor)
			list_fragment.add(object);
		if(caller == Project.class && project != null && project == neibor)
			list_fragment.add(object);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent)
	{
		//FALTA AKI
	}
	
}
