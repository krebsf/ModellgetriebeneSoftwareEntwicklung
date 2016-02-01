/**********************************************************************
* Filename: Qualification.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.presentationLayer.Qualification;

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
import org.quasar.ProjectsWorld.businessLayer.Qualification;
import org.quasar.ProjectsWorld.businessLayer.Worker;
import org.quasar.ProjectsWorld.businessLayer.Project;
import org.quasar.ProjectsWorld.businessLayer.Training;

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

public class QualificationActivity extends MasterActivity implements
			ListFragmentController.Callbacks,
			QualificationDetailFragment.Callbacks,
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
	private Qualification clickedQualification;
	private Worker worker;
	private final String WORKERObject = "WORKERObject";
	private final String IsQualifiedAssociation = "IsQualifiedAssociation";
	private Project project;
	private final String PROJECTObject = "PROJECTObject";
	private final String RequiresAssociation = "RequiresAssociation";
	private Training training;
	private final String TRAININGObject = "TRAININGObject";
	private final String TrainsAssociation = "TrainsAssociation";
	
	public Bundle extras()
	{
		return getIntent().getExtras();
	}
	
	public void startActivity_ToONE(Qualification qualification)
	{
		if(!restarted)
		{
			if(ONCREATION)
				setDetailFragment(QualificationDetailFragment.ARG_VIEW_NEW, null);
			else
				setDetailFragment(QualificationDetailFragment.ARG_VIEW_DETAIL, qualification);
		}
	}
	
	public <T> void startActivity_ToMANY(Collection<T> subSet, Bundle savedInstanceState)
	{
		if(!restarted)
		{
			if(subSet != null)
			{
				list_fragment.setListAdapter(new ListAdapter(this, new QualificationListViewHolder(), subSet));
			}
			else
			{
				list_fragment.setListAdapter(new ListAdapter(this, new QualificationListViewHolder(), Qualification.allInstances()));
			}
		}
	}
	
	public void setDetailFragment(String View, Qualification qualification)
	{
		detail_fragment = new QualificationDetailFragment();
		if(qualification != null)
			UtilNavigate.replaceFragment(this, detail_fragment, R.id.qualification_detail_container, UtilNavigate.setFragmentBundleArguments(View,qualification.ID()));
		else
			UtilNavigate.replaceFragment(this, detail_fragment, R.id.qualification_detail_container, UtilNavigate.setFragmentBundleArguments(View,0));
		
		if(!mTwoPane && AssociationEndMultiplicity != ToONE)
		{
			list_fragment.hide();
			((DetailFragment) detail_fragment).show();
		}
		showingDetail = true;
	}
	
	public void orientationSettings(Qualification clickedQualification)
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
			 || extras().containsKey(RequiresAssociation)
			 || extras().containsKey(TrainsAssociation)
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
			setContentView(R.layout.qualification_layout_twopane);
		}
		else
			setContentView(R.layout.qualification_layout_onepane);
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if(savedInstanceState == null)
		{
			if (AssociationEndMultiplicity != ToONE)
			{
				list_fragment = new ListFragmentController();
				ft.add(R.id.qualification_list_container, list_fragment);
			}
			navigation_bar = new QualificationNavigationBarFragment();
			ft.add(R.id.qualification_navigationbar_container, navigation_bar);
			ft.commit();
		}
		else
		{
			if (AssociationEndMultiplicity != ToONE)
				list_fragment = (ListFragmentController) getSupportFragmentManager().findFragmentById(R.id.qualification_list_container);
			navigation_bar = (Fragment) getSupportFragmentManager().findFragmentById(R.id.qualification_navigationbar_container);
			detail_fragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.qualification_detail_container);
			showingDetail = savedInstanceState.getBoolean(SHOWINGDetail);
			restarted = savedInstanceState.getBoolean("RESTARTED");
		}
		
		
		if(ONCREATION)
			Transactions.StartTransaction();
		
		if(extras() != null)
		{
			if(extras().containsKey(IsQualifiedAssociation))
			{
				worker = Worker.getWorker((Integer)extras().getInt(WORKERObject));
				AssociationEndName = extras().getString(IsQualifiedAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
					startActivity_ToMANY((Set<Qualification>) worker.qualifications(), extras());
				Worker.getAccess().setChangeListener(this);
			}
			if(extras().containsKey(RequiresAssociation))
			{
				project = Project.getProject((Integer)extras().getInt(PROJECTObject));
				AssociationEndName = extras().getString(RequiresAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
					startActivity_ToMANY((Set<Qualification>) project.requirements(), extras());
				Project.getAccess().setChangeListener(this);
			}
			if(extras().containsKey(TrainsAssociation))
			{
				training = Training.getTraining((Integer)extras().getInt(TRAININGObject));
				AssociationEndName = extras().getString(TrainsAssociation);
				if(ONCREATION)
					startActivity_ToMANY(null, extras());
				else 
					startActivity_ToMANY((Set<Qualification>) training.trained(), extras());
				Training.getAccess().setChangeListener(this);
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
			Qualification.getAccess().setChangeListener(list_fragment);
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
				clickedQualification = (Qualification) list_fragment.getListAdapter().getItem(list_fragment.getSelectedPosition());
				
			}
			
		}
		((NavigationBarFragment)navigation_bar).setViewingObject(clickedQualification);
		orientationSettings(clickedQualification);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Transactions.addSpecialCommand(new Command(QualificationActivity.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
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
			Transactions.addSpecialCommand(new Command(QualificationActivity.class, CommandType.BACK, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			super.onBackPressed();
		}
	}
	
	@Override
	public void onDestroy()
	{
		Qualification.getAccess().removeChangeListener(list_fragment);
		super.onDestroy();
	}
	
	@Override
	public void onItemSelected(int listPos, Object object, boolean mSinglePanelongclick)
	{
		if(object instanceof Qualification)
		{
			this.clickedQualification = (Qualification) object;
			((NavigationBarFragment)navigation_bar).setViewingObject(clickedQualification);
			
			if (mTwoPane)
			{
				setDetailFragment(QualificationDetailFragment.ARG_VIEW_DETAIL, clickedQualification);
			}
			else if(mSinglePanelongclick)
			{
				setDetailFragment(QualificationDetailFragment.ARG_VIEW_DETAIL, clickedQualification);
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
				setDetailFragment(QualificationDetailFragment.ARG_VIEW_NEW, null);
				break;
			case R.id.menu_confirm_oncreation:
				if(ONCREATION)
				{
					if(list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
					{
						if(worker != null)
						{
							setResult(Activity.RESULT_OK, new Intent().putExtra(IsQualifiedAssociation, clickedQualification.ID()));
							finish();
						}
						if(project != null)
						{
							setResult(Activity.RESULT_OK, new Intent().putExtra(RequiresAssociation, clickedQualification.ID()));
							finish();
						}
						if(training != null)
						{
							setResult(Activity.RESULT_OK, new Intent().putExtra(TrainsAssociation, clickedQualification.ID()));
							finish();
						}
					}
					else
						UtilNavigate.showWarning(this, "Wrong action", "Error - In order to confirm a Qualification you must have a Qualification selected\nSolution - In order to finish the association action\n\t1 - select a Qualification\n\t2 - if no Qualification is available create a new one by means of the plus icon");
				}
				break;
			case R.id.menu_edit:
				if (AssociationEndMultiplicity == ToONE || list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
				{
					setDetailFragment(QualificationDetailFragment.ARG_VIEW_EDIT,	clickedQualification);
				}
				break;
			case R.id.menu_delete:
				if(clickedQualification != null)
				{
					if(Transactions.StartTransaction())
					{
						if (AssociationEndMultiplicity == ToMANY && list_fragment.getSelectedPosition() != ListView.INVALID_POSITION)
						{
							clickedQualification.delete();
							list_fragment.show();
						}
						else
						{
							clickedQualification.delete();
							finish();
						}
						clickedQualification = null;
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
			clickedQualification.insertAssociation((Worker) Worker.getWorker((Integer) data.getExtras().get(IsQualifiedAssociation)), IsQualifiedAssociation);
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
		}
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(RequiresAssociation))
		{
			clickedQualification.insertAssociation((Project) Project.getProject((Integer) data.getExtras().get(RequiresAssociation)), RequiresAssociation);
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
		}
		if (requestCode == CREATION_CODE && resultCode == Activity.RESULT_OK && data.getExtras().containsKey(TrainsAssociation))
		{
			clickedQualification.insertAssociation((Training) Training.getTraining((Integer) data.getExtras().get(TrainsAssociation)), TrainsAssociation);
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
	public void onDetailOK(boolean isNew, Qualification newQualification)
	{
		if(Transactions.StartTransaction())
		{
			if(isNew)
			{
				 newQualification.insert();
				
				if(worker != null && AssociationEndName.equals(IsQualifiedAssociation) && !Transactions.isCanceled())
				{
					worker.insertAssociation(newQualification, IsQualifiedAssociation);
					list_fragment.add(newQualification);
				}
				if(project != null && AssociationEndName.equals(RequiresAssociation) && !Transactions.isCanceled())
				{
					project.insertAssociation(newQualification, RequiresAssociation);
					list_fragment.add(newQualification);
				}
				if(training != null && AssociationEndName.equals(TrainsAssociation) && !Transactions.isCanceled())
				{
					training.insertAssociation(newQualification, TrainsAssociation);
					list_fragment.add(newQualification);
				}
			}
			else
			{
				 newQualification.update();
			}
			
			if(!Transactions.StopTransaction())
				Transactions.ShowErrorMessage(this);
			else
			{
				clickedQualification = newQualification;
				setDetailFragment(QualificationDetailFragment.ARG_VIEW_DETAIL,clickedQualification);
				if(AssociationEndMultiplicity == ToMANY)
				{
					list_fragment.setActivatedPosition(clickedQualification);
					list_fragment.setSelection(clickedQualification);
				}
			}
		}
		else
		{
			if(ONCREATION)
			{
				clickedQualification = newQualification;
				clickedQualification.insert();
				if(worker != null)
				{
					setResult(Activity.RESULT_OK, new Intent().putExtra(IsQualifiedAssociation, clickedQualification.ID()));
					finish();
				}
				if(project != null)
				{
					setResult(Activity.RESULT_OK, new Intent().putExtra(RequiresAssociation, clickedQualification.ID()));
					finish();
				}
				if(training != null)
				{
					setResult(Activity.RESULT_OK, new Intent().putExtra(TrainsAssociation, clickedQualification.ID()));
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
			if (clickedQualification != null)
				setDetailFragment(QualificationDetailFragment.ARG_VIEW_DETAIL,clickedQualification);
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
			if (clickedQualification != null)
				setDetailFragment(QualificationDetailFragment.ARG_VIEW_DETAIL,clickedQualification);
			else
				getSupportFragmentManager().beginTransaction().detach(detail_fragment).commit();
		}
	}
	
	@Override
	public void addToList(Class<?> caller, Object object,  Object neibor)
	{
		if(caller == Qualification.class && worker == null && project == null && training == null)
			list_fragment.add(object);
		if(caller == Worker.class && worker != null && worker.qualifications() == neibor)
			list_fragment.add(object);
		if(caller == Project.class && project != null && project.requirements() == neibor)
			list_fragment.add(object);
		if(caller == Training.class && training != null && training.trained() == neibor)
			list_fragment.add(object);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent)
	{
		//FALTA AKI
	}
	
}
