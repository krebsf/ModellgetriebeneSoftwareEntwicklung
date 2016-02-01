/**********************************************************************
* Filename: Worker.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.presentationLayer.Worker;

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
import org.quasar.ProjectsWorld.businessLayer.Worker;
import org.quasar.ProjectsWorld.businessLayer.Qualification;
import org.quasar.ProjectsWorld.businessLayer.Member;
import org.quasar.ProjectsWorld.businessLayer.Project;
import org.quasar.ProjectsWorld.businessLayer.Company;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;

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

public class WorkerActivity extends MasterActivity implements
			ListFragmentController.Callbacks,
			WorkerDetailFragment.Callbacks,
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
	private Worker clickedWorker;
	private Qualification qualification;
	private final String QUALIFICATIONObject = "QUALIFICATIONObject";
	private final String IsQualifiedAssociation = "IsQualifiedAssociation";
	private Member member;
	private final String MEMBERObject = "MEMBERObject";
	private final String members_MemberAssociation = "members_MemberAssociation";
	private Project project;
	private final String PROJECTObject = "PROJECTObject";
	private final String MemberAssociation = "MemberAssociation";
	private Company company;
	private final String COMPANYObject = "COMPANYObject";
	private final String EmploysAssociation = "EmploysAssociation";
	
	public Bundle extras()
	{
		return getIntent().getExtras();
	}
	
	public void startActivity_ToONE(Worker worker)
	{
		if(!restarted)
		{
			if(ONCREATION)
				setDetailFragment(WorkerDetailFragment.ARG_VIEW_NEW, null);
			else
				setDetailFragment(WorkerDetailFragment.ARG_VIEW_DETAIL, worker);
		}
	}
	
	public <T> void startActivity_ToMANY(Collection<T> subSet, Bundle savedInstanceState)
	{
		if(!restarted)
		{
			if(subSet != null)
			{
				list_fragment.setListAdapter(new ListAdapter(this, new WorkerListViewHolder(), subSet));
			}
			else
			{
				list_fragment.setListAdapter(new ListAdapter(this, new WorkerListViewHolder(), Worker.allInstances()));
			}
		}
	}
	
	public void setDetailFragment(String View, Worker worker)
	{
		detail_fragment = new WorkerDetailFragment();
		if(worker != null)
			UtilNavigate.replaceFragment(this, detail_fragment, R.id.worker_detail_container, UtilNavigate.setFragmentBundleArguments(View,worker.ID()));
		else
			UtilNavigate.replaceFragment(this, detail_fragment, R.id.worker_detail_container, UtilNavigate.setFragmentBundleArguments(View,0));
		
		if(!mTwoPane && AssociationEndMultiplicity != ToONE)
		{
			list_fragment.hide();
			((DetailFragment) detail_fragment).show();
		}
		showingDetail = true;
	}
	
	public void orientationSettings(Worker clickedWorker)
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
			if(extras().containsKey(IsQualifiedAssociation)
			 || extras().containsKey(members_MemberAssociation)
			 || extras().containsKey(MemberAssociation)
			 || extras().containsKey(EmploysAssociation)
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
			setContentView(R.layout.worker_layout_twopane);
		}
		else
			setContentView(R.layout.worker_layout_onepane);
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if(savedInstanceState == null)
		{
			if (AssociationEndMultiplicity != ToONE)
			{
				list_fragment = new ListFragmentController();
				ft.add(R.id.worker_list_container, list_fragment);
			}
			navigation_bar = new WorkerNavigationBarFragment();
			ft.add(R.id.worker_navigationbar_container, navigation_bar);
			ft.commit();
		}
		else
		{
			if (AssociationEndMultiplicity != ToONE)
				list_fragment = (ListFragmentController) getSupportFragmentManager().findFragmentById(R.id.worker_list_container);
			navigation_bar = (Fragment) getSupportFragmentManager().findFragmentById(R.id.worker_navigationbar_container);
			detail_fragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.worker_detail_container);
			showingDetail = savedInstanceState.getBoolean(SHOWINGDetail);
			restarted = savedInstanceState.getBoolean("RESTARTED");
		}
		
		
		if(ONCREATION)
			Transactions.StartTransaction();
		
		if(extras() != null)
		{
			if(extras().containsKey(IsQualifiedAssociation))
			{
				qualification = Qualification.getQualification((Integer)extras().getInt(QUALIFICATIONObject));
				AssociationEndName = extras().getString(IsQualifiedAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
					startActivity_ToMANY((Set<Worker>) qualification.workers(), extras());
				Qualification.getAccess().setChangeListener(this);
			}
			if(extras().containsKey(members_MemberAssociation))
			{
				member = Member.getMember((Integer)extras().getInt(MEMBERObject));
				AssociationEndName = extras().getString(members_MemberAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
				{
					mTwoPane = false;
					clickedWorker = (Worker)member.members();
					startActivity_ToONE(clickedWorker);
				}
			}
			if(extras().containsKey(MemberAssociation))
			{
				project = Project.getProject((Integer)extras().getInt(PROJECTObject));
				AssociationEndName = extras().getString(MemberAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
					startActivity_ToMANY((Set<Worker>) project.members(), extras());
				Project.getAccess().setChangeListener(this);
			}
			if(extras().containsKey(EmploysAssociation))
			{
				company = Company.getCompany((Integer)extras().getInt(COMPANYObject));
				AssociationEndName = extras().getString(EmploysAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
					startActivity_ToMANY((Set<Worker>) company.employees(), extras());
				Company.getAccess().setChangeListener(this);
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
			Worker.getAccess().setChangeListener(list_fragment);
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
				clickedWorker = (Worker) list_fragment.getListAdapter().getItem(list_fragment.getSelectedPosition());
				
			}
			
		}
		((NavigationBarFragment)navigation_bar).setViewingObject(clickedWorker);
		orientationSettings(clickedWorker);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Transactions.addSpecialCommand(new Command(WorkerActivity.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
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
			Transactions.addSpecialCommand(new Command(WorkerActivity.class, CommandType.BACK, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			super.onBackPressed();
		}
	}
	
	@Override
	public void onDestroy()
	{
		Worker.getAccess().removeChangeListener(list_fragment);
		super.onDestroy();
	}
	
	@Override
	public void onItemSelected(int listPos, Object object, boolean mSinglePanelongclick)
	{
		if(object instanceof Worker)
		{
			this.clickedWorker = (Worker) object;
			((NavigationBarFragment)navigation_bar).setViewingObject(clickedWorker);
			
			if (mTwoPane)
			{
				setDetailFragment(WorkerDetailFragment.ARG_VIEW_DETAIL, clickedWorker);
			}
			else if(mSinglePanelongclick)
			{
				setDetailFragment(WorkerDetailFragment.ARG_VIEW_DETAIL, clickedWorker);
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
				setDetailFragment(WorkerDetailFragment.ARG_VIEW_NEW, null);
				break;
			case R.id.menu_confirm_oncreation:
				if(ONCREATION)
				{
					if(list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
					{
						if(qualification != null)
						{
							setResult(Activity.RESULT_OK, new Intent().putExtra(IsQualifiedAssociation, clickedWorker.ID()));
							finish();
						}
						if(member != null)
						{
							setResult(Activity.RESULT_OK, new Intent().putExtra(members_MemberAssociation, clickedWorker.ID()));
							finish();
						}
						if(project != null)
						{
							setResult(Activity.RESULT_OK, new Intent().putExtra(MemberAssociation, clickedWorker.ID()));
							finish();
						}
						if(company != null)
						{
							setResult(Activity.RESULT_OK, new Intent().putExtra(EmploysAssociation, clickedWorker.ID()));
							finish();
						}
					}
					else
						UtilNavigate.showWarning(this, "Wrong action", "Error - In order to confirm a Worker you must have a Worker selected\nSolution - In order to finish the association action\n\t1 - select a Worker\n\t2 - if no Worker is available create a new one by means of the plus icon");
				}
				break;
			case R.id.menu_edit:
				if (AssociationEndMultiplicity == ToONE || list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
				{
					setDetailFragment(WorkerDetailFragment.ARG_VIEW_EDIT,	clickedWorker);
				}
				break;
			case R.id.menu_delete:
				if(clickedWorker != null)
				{
					if(Transactions.StartTransaction())
					{
						if (AssociationEndMultiplicity == ToMANY && list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
						{
							clickedWorker.delete();
							list_fragment.show();
						}
						else
						{
							clickedWorker.delete();
							finish();
						}
						clickedWorker = null;
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
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(IsQualifiedAssociation))
		{
			clickedWorker.insertAssociation((Qualification) Qualification.getQualification((Integer) data.getExtras().get(IsQualifiedAssociation)), IsQualifiedAssociation);
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
		}
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(members_MemberAssociation))
		{
			clickedWorker.insertAssociation((Member) Member.getMember((Integer) data.getExtras().get(members_MemberAssociation)), members_MemberAssociation);
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
		}
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(MemberAssociation))
		{
			clickedWorker.insertAssociation((Project) Project.getProject((Integer) data.getExtras().get(MemberAssociation)), MemberAssociation);
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
		}
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(EmploysAssociation))
		{
			clickedWorker.insertAssociation((Company) Company.getCompany((Integer) data.getExtras().get(EmploysAssociation)), EmploysAssociation);
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
	public void onDetailOK(boolean isNew, Worker newWorker)
	{
		if(Transactions.StartTransaction())
		{
			if(isNew)
			{
				 newWorker.insert();
				
				if(qualification != null && AssociationEndName.equals(IsQualifiedAssociation) && !Transactions.isCanceled())
				{
					qualification.insertAssociation(newWorker, IsQualifiedAssociation);
					list_fragment.add(newWorker);
				}
				if(member != null && AssociationEndName.equals(members_MemberAssociation) && !Transactions.isCanceled())
				{
					member.insertAssociation(newWorker, members_MemberAssociation);
					list_fragment.add(newWorker);
				}
				if(project != null && AssociationEndName.equals(MemberAssociation) && !Transactions.isCanceled())
				{
					new Member(project, newWorker).insert();
					list_fragment.add(newWorker);
				}
				if(company != null && AssociationEndName.equals(EmploysAssociation) && !Transactions.isCanceled())
				{
					company.insertAssociation(newWorker, EmploysAssociation);
					list_fragment.add(newWorker);
				}
			}
			else
			{
				 newWorker.update();
			}
			
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
			else
			{
				clickedWorker = newWorker;
				setDetailFragment(WorkerDetailFragment.ARG_VIEW_DETAIL,clickedWorker);
				if(AssociationEndMultiplicity == ToMANY)
				{
					list_fragment.setActivatedPosition(clickedWorker);
					list_fragment.setSelection(clickedWorker);
				}
			}
		}
		else
		{
			if(ONCREATION)
			{
				clickedWorker = newWorker;
				clickedWorker.insert();
				if(qualification != null)
				{
					setResult(Activity.RESULT_OK, new Intent().putExtra(IsQualifiedAssociation, clickedWorker.ID()));
					finish();
				}
				if(member != null)
				{
					setResult(Activity.RESULT_OK, new Intent().putExtra(members_MemberAssociation, clickedWorker.ID()));
					finish();
				}
				if(project != null)
				{
					setResult(Activity.RESULT_OK, new Intent().putExtra(MemberAssociation, clickedWorker.ID()));
					finish();
				}
				if(company != null)
				{
					setResult(Activity.RESULT_OK, new Intent().putExtra(EmploysAssociation, clickedWorker.ID()));
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
			if (clickedWorker != null)
				setDetailFragment(WorkerDetailFragment.ARG_VIEW_DETAIL,clickedWorker);
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
			if (clickedWorker != null)
				setDetailFragment(WorkerDetailFragment.ARG_VIEW_DETAIL,clickedWorker);
			else
				getSupportFragmentManager().beginTransaction().detach(detail_fragment).commit();
		}
	}
	
	@Override
	public void addToList(Class<?> caller, Object object,  Object neibor)
	{
		if(caller == Worker.class && qualification == null && member == null && project == null && company == null)
			list_fragment.add(object);
		if(caller == Qualification.class && qualification != null && qualification.workers() == neibor)
			list_fragment.add(object);
		if(caller == Member.class && member != null && member == neibor)
			list_fragment.add(object);
		if(caller == Project.class && project != null && project.members() == neibor)
			list_fragment.add(object);
		if(caller == Company.class && company != null && company.employees() == neibor)
			list_fragment.add(object);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent)
	{
		//FALTA AKI
	}
	
}
