/**********************************************************************
* Filename: Member.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.presentationLayer.Member;

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
import org.quasar.ProjectsWorld.businessLayer.Member;
import org.quasar.ProjectsWorld.businessLayer.Worker;
import org.quasar.ProjectsWorld.presentationLayer.Worker.WorkerActivity;
import org.quasar.ProjectsWorld.businessLayer.Project;
import org.quasar.ProjectsWorld.presentationLayer.Project.ProjectActivity;

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

public class MemberActivity extends MasterActivity implements
			ListFragmentController.Callbacks,
			MemberDetailFragment.Callbacks,
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
	private Member clickedMember;
	private Worker worker;
	private final String WORKERObject = "WORKERObject";
	private final String members_MemberAssociation = "members_MemberAssociation";
	private Project project;
	private final String PROJECTObject = "PROJECTObject";
	private final String projects_MemberAssociation = "projects_MemberAssociation";
	private final String MemberAssociation = "MemberAssociation";
	
	public Bundle extras()
	{
		return getIntent().getExtras();
	}
	
	public void startActivity_ToONE(Member member)
	{
		if(!restarted)
		{
			if(ONCREATION)
				setDetailFragment(MemberDetailFragment.ARG_VIEW_NEW, null);
			else
				setDetailFragment(MemberDetailFragment.ARG_VIEW_DETAIL, member);
		}
	}
	
	public <T> void startActivity_ToMANY(Collection<T> subSet, Bundle savedInstanceState)
	{
		if(!restarted)
		{
			if(subSet != null)
			{
				list_fragment.setListAdapter(new ListAdapter(this, new MemberListViewHolder(), subSet));
			}
			else
			{
				list_fragment.setListAdapter(new ListAdapter(this, new MemberListViewHolder(), Member.allInstances()));
			}
		}
	}
	
	public void setDetailFragment(String View, Member member)
	{
		detail_fragment = new MemberDetailFragment();
		if(member != null)
			UtilNavigate.replaceFragment(this, detail_fragment, R.id.member_detail_container, UtilNavigate.setFragmentBundleArguments(View,member.ID()));
		else
			UtilNavigate.replaceFragment(this, detail_fragment, R.id.member_detail_container, UtilNavigate.setFragmentBundleArguments(View,0));
		
		if(!mTwoPane && AssociationEndMultiplicity != ToONE)
		{
			list_fragment.hide();
			((DetailFragment) detail_fragment).show();
		}
		showingDetail = true;
	}
	
	public void orientationSettings(Member clickedMember)
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
			if(extras().containsKey(members_MemberAssociation)
			 || extras().containsKey(projects_MemberAssociation)
			 || extras().containsKey(MemberAssociation)
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
			setContentView(R.layout.member_layout_twopane);
		}
		else
			setContentView(R.layout.member_layout_onepane);
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if(savedInstanceState == null)
		{
			if (AssociationEndMultiplicity != ToONE)
			{
				list_fragment = new ListFragmentController();
				ft.add(R.id.member_list_container, list_fragment);
			}
			navigation_bar = new MemberNavigationBarFragment();
			ft.add(R.id.member_navigationbar_container, navigation_bar);
			ft.commit();
		}
		else
		{
			if (AssociationEndMultiplicity != ToONE)
				list_fragment = (ListFragmentController) getSupportFragmentManager().findFragmentById(R.id.member_list_container);
			navigation_bar = (Fragment) getSupportFragmentManager().findFragmentById(R.id.member_navigationbar_container);
			detail_fragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.member_detail_container);
			showingDetail = savedInstanceState.getBoolean(SHOWINGDetail);
			restarted = savedInstanceState.getBoolean("RESTARTED");
		}
		
		
		if(ONCREATION)
			Transactions.StartTransaction();
		
		if(extras() != null)
		{
			if(extras().containsKey(members_MemberAssociation))
			{
				worker = Worker.getWorker((Integer)extras().getInt(WORKERObject));
				AssociationEndName = extras().getString(members_MemberAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
					startActivity_ToMANY((Set<Member>) worker.member(), extras());
				Worker.getAccess().setChangeListener(this);
			}
			if(extras().containsKey(projects_MemberAssociation))
			{
				project = Project.getProject((Integer)extras().getInt(PROJECTObject));
				AssociationEndName = extras().getString(projects_MemberAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
					startActivity_ToMANY((Set<Member>) project.member(), extras());
				Project.getAccess().setChangeListener(this);
			}
			if(extras().containsKey(MemberAssociation))
			{
				if(extras().containsKey(WORKERObject))
					worker = Worker.getWorker((Integer)extras().getInt(WORKERObject));
				if(extras().containsKey(PROJECTObject))
					project = Project.getProject((Integer)extras().getInt(PROJECTObject));
				AssociationEndName = extras().getString(MemberAssociation);
				if(ONCREATION)
				{
					startActivity_ToMANY(null, extras());
					if(extras().containsKey(WORKERObject))
						UtilNavigate.showWarning(this, "Action - Association between associative members", "You are trying to associate a Worker to a Project\nBefore you can complete this action (association to Project) you must firts select or create a Member in order to make of the Project a projects in the Worker");
					if(extras().containsKey(PROJECTObject))
						UtilNavigate.showWarning(this, "Action - Association between associative members", "You are trying to associate a Project to a Worker\nBefore you can complete this action (association to Worker) you must firts select or create a Member in order to make of the Worker a members in the Project");
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
			Member.getAccess().setChangeListener(list_fragment);
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
				clickedMember = (Member) list_fragment.getListAdapter().getItem(list_fragment.getSelectedPosition());
				
			}
			
		}
		((NavigationBarFragment)navigation_bar).setViewingObject(clickedMember);
		orientationSettings(clickedMember);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Transactions.addSpecialCommand(new Command(MemberActivity.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
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
			Transactions.addSpecialCommand(new Command(MemberActivity.class, CommandType.BACK, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			super.onBackPressed();
		}
	}
	
	@Override
	public void onDestroy()
	{
		Member.getAccess().removeChangeListener(list_fragment);
		super.onDestroy();
	}
	
	@Override
	public void onItemSelected(int listPos, Object object, boolean mSinglePanelongclick)
	{
		if(object instanceof Member)
		{
			this.clickedMember = (Member) object;
			((NavigationBarFragment)navigation_bar).setViewingObject(clickedMember);
			
			if (mTwoPane)
			{
				setDetailFragment(MemberDetailFragment.ARG_VIEW_DETAIL, clickedMember);
			}
			else if(mSinglePanelongclick)
			{
				setDetailFragment(MemberDetailFragment.ARG_VIEW_DETAIL, clickedMember);
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
				setDetailFragment(MemberDetailFragment.ARG_VIEW_NEW, null);
				break;
			case R.id.menu_confirm_oncreation:
				if(ONCREATION)
				{
					if(list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
					{
						if(worker != null)
						{
							if(extras().containsKey(MemberAssociation))
							{
								UtilNavigate.toActivityForResult(this, ProjectActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("MEMBERObject", clickedMember.ID(), "AssociationEndMultiplicityKey", -1, "projects_MemberAssociation", "projects_MemberAssociation"), MasterActivity.CREATION_CODE);
							}
							else
							{
								setResult(Activity.RESULT_OK, new Intent().putExtra(members_MemberAssociation, clickedMember.ID()));
								finish();
							}
						}
						if(project != null)
						{
							if(extras().containsKey(MemberAssociation))
							{
								UtilNavigate.toActivityForResult(this, WorkerActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("MEMBERObject", clickedMember.ID(), "AssociationEndMultiplicityKey", -1, "members_MemberAssociation", "members_MemberAssociation"), MasterActivity.CREATION_CODE);
							}
							else
							{
								setResult(Activity.RESULT_OK, new Intent().putExtra(projects_MemberAssociation, clickedMember.ID()));
								finish();
							}
						}
					}
					else
						UtilNavigate.showWarning(this, "Wrong action", "Error - In order to confirm a Member you must have a Member selected\nSolution - In order to finish the association action\n\t1 - select a Member\n\t2 - if no Member is available create a new one by means of the plus icon");
				}
				break;
			case R.id.menu_edit:
				if (AssociationEndMultiplicity == ToONE || list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
				{
					setDetailFragment(MemberDetailFragment.ARG_VIEW_EDIT,	clickedMember);
				}
				break;
			case R.id.menu_delete:
				if(clickedMember != null)
				{
					if(Transactions.StartTransaction())
					{
						if (AssociationEndMultiplicity == ToMANY && list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
						{
							clickedMember.delete();
							list_fragment.show();
						}
						else
						{
							clickedMember.delete();
							finish();
						}
						clickedMember = null;
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
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(members_MemberAssociation))
		{
			clickedMember.insertAssociation((Worker) Worker.getWorker((Integer) data.getExtras().get(members_MemberAssociation)), members_MemberAssociation);
			if(project != null){
				setResult(Activity.RESULT_OK, new Intent().putExtra(projects_MemberAssociation, clickedMember.ID()));
				finish();
			}else
				if(!Transactions.StopTransaction())
					Transactions.ShowErrorMessage(this);
		}
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(projects_MemberAssociation))
		{
			clickedMember.insertAssociation((Project) Project.getProject((Integer) data.getExtras().get(projects_MemberAssociation)), projects_MemberAssociation);
			if(worker != null){
				setResult(Activity.RESULT_OK, new Intent().putExtra(members_MemberAssociation, clickedMember.ID()));
				finish();
			}else
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
	public void onDetailOK(boolean isNew, Member newMember)
	{
		if(Transactions.StartTransaction())
		{
			if(isNew)
			{
				 newMember.insert();
				
				if(worker != null && AssociationEndName.equals(members_MemberAssociation) && !Transactions.isCanceled())
				{
					worker.insertAssociation(newMember, members_MemberAssociation);
					list_fragment.add(newMember);
				}
				if(project != null && AssociationEndName.equals(projects_MemberAssociation) && !Transactions.isCanceled())
				{
					project.insertAssociation(newMember, projects_MemberAssociation);
					list_fragment.add(newMember);
				}
			}
			else
			{
				 newMember.update();
			}
			
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
			else
			{
				clickedMember = newMember;
				setDetailFragment(MemberDetailFragment.ARG_VIEW_DETAIL,clickedMember);
				if(AssociationEndMultiplicity == ToMANY)
				{
					list_fragment.setActivatedPosition(clickedMember);
					list_fragment.setSelection(clickedMember);
				}
			}
		}
		else
		{
			if(ONCREATION)
			{
				clickedMember = newMember;
				clickedMember.insert();
				if(worker != null)
				{
					if(extras().containsKey(MemberAssociation))
					{
						UtilNavigate.toActivityForResult(this, ProjectActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("MEMBERObject", clickedMember.ID(), "AssociationEndMultiplicityKey", -1, "projects_MemberAssociation", "projects_MemberAssociation"), MasterActivity.CREATION_CODE);
					}
					else
					{
						setResult(Activity.RESULT_OK, new Intent().putExtra(members_MemberAssociation, clickedMember.ID()));
						finish();
					}
				}
				if(project != null)
				{
					if(extras().containsKey(MemberAssociation))
					{
						UtilNavigate.toActivityForResult(this, WorkerActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("MEMBERObject", clickedMember.ID(), "AssociationEndMultiplicityKey", -1, "members_MemberAssociation", "members_MemberAssociation"), MasterActivity.CREATION_CODE);
					}
					else
					{
						setResult(Activity.RESULT_OK, new Intent().putExtra(projects_MemberAssociation, clickedMember.ID()));
						finish();
					}
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
			if (clickedMember != null)
				setDetailFragment(MemberDetailFragment.ARG_VIEW_DETAIL,clickedMember);
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
			if (clickedMember != null)
				setDetailFragment(MemberDetailFragment.ARG_VIEW_DETAIL,clickedMember);
			else
				getSupportFragmentManager().beginTransaction().detach(detail_fragment).commit();
		}
	}
	
	@Override
	public void addToList(Class<?> caller, Object object,  Object neibor)
	{
		if(caller == Member.class && worker == null && project == null)
			list_fragment.add(object);
		if(caller == Worker.class && worker != null && worker.projects() == neibor)
			list_fragment.add(object);
		if(caller == Project.class && project != null && project.members() == neibor)
			list_fragment.add(object);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent)
	{
		//FALTA AKI
	}
	
}
