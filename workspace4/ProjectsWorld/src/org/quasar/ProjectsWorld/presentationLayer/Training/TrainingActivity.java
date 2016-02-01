/**********************************************************************
* Filename: Training.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.presentationLayer.Training;

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
import org.quasar.ProjectsWorld.businessLayer.Training;
import org.quasar.ProjectsWorld.businessLayer.Member;
import org.quasar.ProjectsWorld.businessLayer.Worker;
import org.quasar.ProjectsWorld.businessLayer.Qualification;
import org.quasar.ProjectsWorld.businessLayer.Company;
import org.quasar.ProjectsWorld.presentationLayer.Project.ProjectListViewHolder;
import org.quasar.ProjectsWorld.businessLayer.Project;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;

import android.widget.ListView;
import android.view.Menu;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.app.Activity;
import android.util.Log;

public class TrainingActivity extends MasterActivity implements
			ListFragmentController.Callbacks,
			TrainingDetailFragment.Callbacks,
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
	private Training clickedTraining;
	private Member member;
	private final String MEMBERObject = "MEMBERObject";
	private final String projects_MemberAssociation = "projects_MemberAssociation";
	private Worker worker;
	private final String WORKERObject = "WORKERObject";
	private final String MemberAssociation = "MemberAssociation";
	private Qualification qualification;
	private final String QUALIFICATIONObject = "QUALIFICATIONObject";
	private final String RequiresAssociation = "RequiresAssociation";
	private Company company;
	private final String COMPANYObject = "COMPANYObject";
	private final String CarriesOutAssociation = "CarriesOutAssociation";
	private final String TrainsAssociation = "TrainsAssociation";
	private Project project;
	private final String PROJECTObject = "PROJECTObject";
	private final String PROJECTAssociation = "PROJECTAssociation";
	
	
	public Bundle extras()
	{
		return getIntent().getExtras();
	}
	
	public void startActivity_ToONE(Training training)
	{
		if(!restarted)
		{
			if(ONCREATION)
				setDetailFragment(TrainingDetailFragment.ARG_VIEW_NEW, null);
			else
				setDetailFragment(TrainingDetailFragment.ARG_VIEW_DETAIL, training);
		}
	}
	
	public <T> void startActivity_ToMANY(Collection<T> subSet, Bundle savedInstanceState)
	{
		if(!restarted)
		{
			Class<T> clazz;
			if(subSet == null)
				clazz = (Class<T>) Training.class;
			else
				clazz = (Class<T>) subSet.getClass();
			if(subSet != null)
			{
				if(clazz.getClass() == Project.class.getClass())
					list_fragment.setListAdapter(new ListAdapter(this, new ProjectListViewHolder(), subSet));
				if(clazz.getClass() == Training.class.getClass())
					list_fragment.setListAdapter(new ListAdapter(this, new TrainingListViewHolder(), subSet));
			}
			else
			{
				list_fragment.setListAdapter(new ListAdapter(this, new TrainingListViewHolder(), Training.allInstances()));
			}
		}
	}
	
	public void setDetailFragment(String View, Training training)
	{
		detail_fragment = new TrainingDetailFragment();
		if(training != null)
			UtilNavigate.replaceFragment(this, detail_fragment, R.id.training_detail_container, UtilNavigate.setFragmentBundleArguments(View,training.ID()));
		else
			UtilNavigate.replaceFragment(this, detail_fragment, R.id.training_detail_container, UtilNavigate.setFragmentBundleArguments(View,0));
		
		if(!mTwoPane && AssociationEndMultiplicity != ToONE)
		{
			list_fragment.hide();
			((DetailFragment) detail_fragment).show();
		}
		showingDetail = true;
	}
	
	public void orientationSettings(Training clickedTraining)
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
			if(extras().containsKey(projects_MemberAssociation)
			 || extras().containsKey(MemberAssociation)
			 || extras().containsKey(RequiresAssociation)
			 || extras().containsKey(CarriesOutAssociation)
			 || extras().containsKey(TrainsAssociation)
			 || extras().containsKey(PROJECTAssociation)
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
			setContentView(R.layout.training_layout_twopane);
		}
		else
			setContentView(R.layout.training_layout_onepane);
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if(savedInstanceState == null)
		{
			if (AssociationEndMultiplicity != ToONE)
			{
				list_fragment = new ListFragmentController();
				ft.add(R.id.training_list_container, list_fragment);
			}
			navigation_bar = new TrainingNavigationBarFragment();
			ft.add(R.id.training_navigationbar_container, navigation_bar);
			ft.commit();
		}
		else
		{
			if (AssociationEndMultiplicity != ToONE)
				list_fragment = (ListFragmentController) getSupportFragmentManager().findFragmentById(R.id.training_list_container);
			navigation_bar = (Fragment) getSupportFragmentManager().findFragmentById(R.id.training_navigationbar_container);
			detail_fragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.training_detail_container);
			showingDetail = savedInstanceState.getBoolean(SHOWINGDetail);
			restarted = savedInstanceState.getBoolean("RESTARTED");
		}
		
		
		if(ONCREATION)
			Transactions.StartTransaction();
		
		if(extras() != null)
		{
			if(extras().containsKey(projects_MemberAssociation))
			{
				member = Member.getMember((Integer)extras().getInt(MEMBERObject));
				AssociationEndName = extras().getString(projects_MemberAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
				{
					mTwoPane = false;
					clickedTraining = (Training)member.projects();
					startActivity_ToONE(clickedTraining);
				}
			}
			if(extras().containsKey(MemberAssociation))
			{
				worker = Worker.getWorker((Integer)extras().getInt(WORKERObject));
				AssociationEndName = extras().getString(MemberAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
					startActivity_ToMANY((Set<Project>) worker.projects(), extras());
				Worker.getAccess().setChangeListener(this);
			}
			if(extras().containsKey(RequiresAssociation))
			{
				qualification = Qualification.getQualification((Integer)extras().getInt(QUALIFICATIONObject));
				AssociationEndName = extras().getString(RequiresAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
					startActivity_ToMANY((Set<Project>) qualification.projects(), extras());
				Qualification.getAccess().setChangeListener(this);
			}
			if(extras().containsKey(CarriesOutAssociation))
			{
				company = Company.getCompany((Integer)extras().getInt(COMPANYObject));
				AssociationEndName = extras().getString(CarriesOutAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
					startActivity_ToMANY((Set<Project>) company.projects(), extras());
				Company.getAccess().setChangeListener(this);
			}
			if(extras().containsKey(TrainsAssociation))
			{
				qualification = Qualification.getQualification((Integer)extras().getInt(QUALIFICATIONObject));
				AssociationEndName = extras().getString(TrainsAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
					startActivity_ToMANY((Set<Training>) qualification.trainings(), extras());
				Qualification.getAccess().setChangeListener(this);
			}
			if(extras().containsKey(PROJECTAssociation))
			{
				project = Project.getProject((Integer)extras().getInt(PROJECTObject));
				AssociationEndName = extras().getString(PROJECTAssociation);
				startActivity_ToMANY(null, extras());
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
			Training.getAccess().setChangeListener(list_fragment);
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
				clickedTraining = (Training) list_fragment.getListAdapter().getItem(list_fragment.getSelectedPosition());
				
			}
			
		}
		((NavigationBarFragment)navigation_bar).setViewingObject(clickedTraining);
		orientationSettings(clickedTraining);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Transactions.addSpecialCommand(new Command(TrainingActivity.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
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
			Transactions.addSpecialCommand(new Command(TrainingActivity.class, CommandType.BACK, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			super.onBackPressed();
		}
	}
	
	@Override
	public void onDestroy()
	{
		Training.getAccess().removeChangeListener(list_fragment);
		super.onDestroy();
	}
	
	@Override
	public void onItemSelected(int listPos, Object object, boolean mSinglePanelongclick)
	{
		if(object instanceof Training)
		{
			this.clickedTraining = (Training) object;
			((NavigationBarFragment)navigation_bar).setViewingObject(clickedTraining);
			
			if (mTwoPane)
			{
				setDetailFragment(TrainingDetailFragment.ARG_VIEW_DETAIL, clickedTraining);
			}
			else if(mSinglePanelongclick)
			{
				setDetailFragment(TrainingDetailFragment.ARG_VIEW_DETAIL, clickedTraining);
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
				setDetailFragment(TrainingDetailFragment.ARG_VIEW_NEW, null);
				break;
			case R.id.menu_confirm_oncreation:
				if(ONCREATION)
				{
					if(list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
					{
						if(member != null)
						{
							setResult(Activity.RESULT_OK, new Intent().putExtra(projects_MemberAssociation, clickedTraining.ID()));
							finish();
						}
						if(worker != null)
						{
							setResult(Activity.RESULT_OK, new Intent().putExtra(MemberAssociation, clickedTraining.ID()));
							finish();
						}
						if(qualification != null)
						{
							if(AssociationEndName.equals(RequiresAssociation))
							{
								setResult(Activity.RESULT_OK, new Intent().putExtra(RequiresAssociation, clickedTraining.ID()));
								finish();
							}
							if(AssociationEndName.equals(TrainsAssociation))
							{
								setResult(Activity.RESULT_OK, new Intent().putExtra(TrainsAssociation, clickedTraining.ID()));
								finish();
							}
						}
						if(company != null)
						{
							setResult(Activity.RESULT_OK, new Intent().putExtra(CarriesOutAssociation, clickedTraining.ID()));
							finish();
						}
					}
					else
						UtilNavigate.showWarning(this, "Wrong action", "Error - In order to confirm a Training you must have a Training selected\nSolution - In order to finish the association action\n\t1 - select a Training\n\t2 - if no Training is available create a new one by means of the plus icon");
				}
				break;
			case R.id.menu_edit:
				if (AssociationEndMultiplicity == ToONE || list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
				{
					setDetailFragment(TrainingDetailFragment.ARG_VIEW_EDIT,	clickedTraining);
				}
				break;
			case R.id.menu_delete:
				if(clickedTraining != null)
				{
					if(Transactions.StartTransaction())
					{
						if (AssociationEndMultiplicity == ToMANY && list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
						{
							clickedTraining.delete();
							list_fragment.show();
						}
						else
						{
							clickedTraining.delete();
							finish();
						}
						clickedTraining = null;
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
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(projects_MemberAssociation))
		{
			clickedTraining.insertAssociation((Member) Member.getMember((Integer) data.getExtras().get(projects_MemberAssociation)), projects_MemberAssociation);
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
		}
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(MemberAssociation))
		{
			clickedTraining.insertAssociation((Worker) Worker.getWorker((Integer) data.getExtras().get(MemberAssociation)), MemberAssociation);
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
		}
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(RequiresAssociation))
		{
			clickedTraining.insertAssociation((Qualification) Qualification.getQualification((Integer) data.getExtras().get(RequiresAssociation)), RequiresAssociation);
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
		}
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(CarriesOutAssociation))
		{
			clickedTraining.insertAssociation((Company) Company.getCompany((Integer) data.getExtras().get(CarriesOutAssociation)), CarriesOutAssociation);
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
		}
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(TrainsAssociation))
		{
			clickedTraining.insertAssociation((Qualification) Qualification.getQualification((Integer) data.getExtras().get(TrainsAssociation)), TrainsAssociation);
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
	public void onDetailOK(boolean isNew, Training newTraining)
	{
		if(Transactions.StartTransaction())
		{
			if(isNew)
			{
				 newTraining.insert();
				
				if(member != null && AssociationEndName.equals(projects_MemberAssociation) && !Transactions.isCanceled())
				{
					member.insertAssociation(newTraining, projects_MemberAssociation);
					list_fragment.add(newTraining);
				}
				if(worker != null && AssociationEndName.equals(MemberAssociation) && !Transactions.isCanceled())
				{
					new Member(newTraining, worker).insert();
					list_fragment.add(newTraining);
				}
				if(qualification != null && AssociationEndName.equals(RequiresAssociation) && !Transactions.isCanceled())
				{
					qualification.insertAssociation(newTraining, RequiresAssociation);
					list_fragment.add(newTraining);
				}
				if(company != null && AssociationEndName.equals(CarriesOutAssociation) && !Transactions.isCanceled())
				{
					company.insertAssociation(newTraining, CarriesOutAssociation);
					list_fragment.add(newTraining);
				}
				if(qualification != null && AssociationEndName.equals(TrainsAssociation) && !Transactions.isCanceled())
				{
					qualification.insertAssociation(newTraining, TrainsAssociation);
					list_fragment.add(newTraining);
				}
			}
			else
			{
				 newTraining.update();
			}
			
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
			else
			{
				clickedTraining = newTraining;
				setDetailFragment(TrainingDetailFragment.ARG_VIEW_DETAIL,clickedTraining);
				if(AssociationEndMultiplicity == ToMANY)
				{
					list_fragment.setActivatedPosition(clickedTraining);
					list_fragment.setSelection(clickedTraining);
				}
			}
		}
		else
		{
			if(ONCREATION)
			{
				clickedTraining = newTraining;
				clickedTraining.insert();
				if(member != null)
				{
					setResult(Activity.RESULT_OK, new Intent().putExtra(projects_MemberAssociation, clickedTraining.ID()));
					finish();
				}
				if(worker != null)
				{
					setResult(Activity.RESULT_OK, new Intent().putExtra(MemberAssociation, clickedTraining.ID()));
					finish();
				}
				if(qualification != null)
				{
					if(AssociationEndName.equals(RequiresAssociation))
					{
						setResult(Activity.RESULT_OK, new Intent().putExtra(RequiresAssociation, clickedTraining.ID()));
						finish();
					}
					if(AssociationEndName.equals(TrainsAssociation))
					{
						setResult(Activity.RESULT_OK, new Intent().putExtra(TrainsAssociation, clickedTraining.ID()));
						finish();
					}
				}
				if(company != null)
				{
					setResult(Activity.RESULT_OK, new Intent().putExtra(CarriesOutAssociation, clickedTraining.ID()));
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
			if (clickedTraining != null)
				setDetailFragment(TrainingDetailFragment.ARG_VIEW_DETAIL,clickedTraining);
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
			if (clickedTraining != null)
				setDetailFragment(TrainingDetailFragment.ARG_VIEW_DETAIL,clickedTraining);
			else
				getSupportFragmentManager().beginTransaction().detach(detail_fragment).commit();
		}
	}
	
	@Override
	public void addToList(Class<?> caller, Object object,  Object neibor)
	{
		if(caller == Training.class && member == null && worker == null && qualification == null && company == null && qualification == null)
			list_fragment.add(object);
		if(caller == Member.class && member != null && member == neibor)
			list_fragment.add(object);
		if(caller == Worker.class && worker != null && worker.projects() == neibor)
			list_fragment.add(object);
		if(caller == Qualification.class && qualification != null && qualification.projects() == neibor)
			list_fragment.add(object);
		if(caller == Company.class && company != null && company.projects() == neibor)
			list_fragment.add(object);
		if(caller == Qualification.class && qualification != null && qualification.trainings() == neibor)
			list_fragment.add(object);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent)
	{
		//FALTA AKI
	}
	
}
