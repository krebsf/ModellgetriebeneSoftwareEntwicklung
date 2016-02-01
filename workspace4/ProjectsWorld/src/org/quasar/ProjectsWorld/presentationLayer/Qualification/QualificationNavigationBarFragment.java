/**********************************************************************
* Filename: Qualification.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.presentationLayer.Qualification;

import org.quasar.ProjectsWorld.R;
import org.quasar.ProjectsWorld.utils.Transactions;
import org.quasar.ProjectsWorld.utils.Command;
import org.quasar.ProjectsWorld.utils.CommandType;
import org.quasar.ProjectsWorld.utils.CommandTargetLayer;
import org.quasar.ProjectsWorld.MasterActivity;
import org.quasar.ProjectsWorld.utils.UtilNavigate;
import org.quasar.ProjectsWorld.utils.PropertyChangeEvent;
import org.quasar.ProjectsWorld.utils.PropertyChangeListener;
import org.quasar.ProjectsWorld.utils.NavigationBarFragment;
import org.quasar.ProjectsWorld.businessLayer.Qualification;
import org.quasar.ProjectsWorld.presentationLayer.Worker.WorkerActivity;
import org.quasar.ProjectsWorld.presentationLayer.Training.TrainingActivity;
import org.quasar.ProjectsWorld.presentationLayer.Project.ProjectActivity;

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

public class QualificationNavigationBarFragment extends Fragment implements PropertyChangeListener, NavigationBarFragment{

	private View rootView = null;
	private Qualification clickedQualification = null;
	private int clickedQualificationID;
	private final String QUALIFICATIONID = "QUALIFICATIONID";
	private TextView number_objects;
	private View workersView;
	private View projectsView;
	private View trainingsView;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public QualificationNavigationBarFragment()
	{
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
		{
			clickedQualification = Qualification.getQualification(savedInstanceState.getInt(QUALIFICATIONID));
		}
		Qualification.getAccess().setChangeListener(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(clickedQualification != null)
		{
			outState.putInt(QUALIFICATIONID, clickedQualification.ID());
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Qualification.getAccess().removeChangeListener(this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		workersView = (View) getActivity().findViewById(R.id.qualification_navigationbar_association_workers);
		workersView.setOnClickListener(ClickListener);
		workersView.setOnLongClickListener(LongClickListener);
		
		projectsView = (View) getActivity().findViewById(R.id.qualification_navigationbar_association_projects);
		projectsView.setOnClickListener(ClickListener);
		projectsView.setOnLongClickListener(LongClickListener);
		
		trainingsView = (View) getActivity().findViewById(R.id.qualification_navigationbar_association_trainings);
		trainingsView.setOnClickListener(ClickListener);
		trainingsView.setOnLongClickListener(LongClickListener);
		
		setViewingObject(clickedQualification);
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.qualification_view_navigationbar, container, false);
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
	
	public void setViewingObject(Object qualification)
	{
		if(qualification instanceof Qualification)
		{
			clickedQualification = (Qualification) qualification;
			if(qualification != null)
				clickedQualificationID = ((Qualification) qualification).ID();
			refreshNavigationBar(clickedQualification);
		}
		if(qualification == null)
		{
			refreshNavigationBar(null);
		}
	}
	
	public void refreshNavigationBar(Qualification qualification)
	{
		if(qualification != null)
		{
			number_objects = (TextView) rootView.findViewById(R.id.qualification_navigationbar_association_workers_numberobjects);
			if(qualification.workers().isEmpty())
			{
				prepareView(workersView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(workersView, true);
				setNumberAssociation(number_objects, clickedQualification.workers().size());
			}
			
			number_objects = (TextView) rootView.findViewById(R.id.qualification_navigationbar_association_projects_numberobjects);
			if(qualification.projects().isEmpty())
			{
				prepareView(projectsView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(projectsView, true);
				setNumberAssociation(number_objects, clickedQualification.projects().size());
			}
			
			number_objects = (TextView) rootView.findViewById(R.id.qualification_navigationbar_association_trainings_numberobjects);
			if(qualification.trainings().isEmpty())
			{
				prepareView(trainingsView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(trainingsView, true);
				setNumberAssociation(number_objects, clickedQualification.trainings().size());
			}
			
			objectValidation();
		}
		else
		{
			number_objects = (TextView) rootView.findViewById(R.id.qualification_navigationbar_association_workers_numberobjects);
			prepareView(workersView, false);
			setNumberAssociation(number_objects, 0);
			workersView.setBackgroundResource(R.drawable.navigationbar_selector);
			
			number_objects = (TextView) rootView.findViewById(R.id.qualification_navigationbar_association_projects_numberobjects);
			prepareView(projectsView, false);
			setNumberAssociation(number_objects, 0);
			projectsView.setBackgroundResource(R.drawable.navigationbar_selector);
			
			number_objects = (TextView) rootView.findViewById(R.id.qualification_navigationbar_association_trainings_numberobjects);
			prepareView(trainingsView, false);
			setNumberAssociation(number_objects, 0);
			trainingsView.setBackgroundResource(R.drawable.navigationbar_selector);
			
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
		if(clickedQualification.validWorkers())
		{
			workersView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			workersView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
		if(clickedQualification.validProjects())
		{
			projectsView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			projectsView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
		if(clickedQualification.validTrainings())
		{
			trainingsView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			trainingsView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
	}
	
	OnClickListener ClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			Transactions.addSpecialCommand(new Command(QualificationNavigationBarFragment.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isClickable())
				{
					if(clickedQualification != null)
					{
						if(view.getId() == R.id.qualification_navigationbar_association_workers)
						{
							UtilNavigate.toActivity(getActivity(), WorkerActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("QUALIFICATIONObject", clickedQualification.ID(), "AssociationEndMultiplicityKey", -1, "IsQualifiedAssociation", "IsQualifiedAssociation"));
						}
						if(view.getId() == R.id.qualification_navigationbar_association_projects)
						{
							UtilNavigate.toActivity(getActivity(), ProjectActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("QUALIFICATIONObject", clickedQualification.ID(), "AssociationEndMultiplicityKey", -1, "RequiresAssociation", "RequiresAssociation"));
						}
						if(view.getId() == R.id.qualification_navigationbar_association_trainings)
						{
							UtilNavigate.toActivity(getActivity(), TrainingActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("QUALIFICATIONObject", clickedQualification.ID(), "AssociationEndMultiplicityKey", -1, "TrainsAssociation", "TrainsAssociation"));
						}
					}
				}
				else
				{
					if(view.getId() == R.id.qualification_navigationbar_association_workers)
					{
						if(clickedQualification != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Qualification does not have any workers.\nYou can do a longclick to add new workers to this Qualification");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Qualification selected.\nFirts you must select an Qualification");
					}
					if(view.getId() == R.id.qualification_navigationbar_association_projects)
					{
						if(clickedQualification != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Qualification does not have any projects.\nYou can do a longclick to add new projects to this Qualification");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Qualification selected.\nFirts you must select an Qualification");
					}
					if(view.getId() == R.id.qualification_navigationbar_association_trainings)
					{
						if(clickedQualification != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Qualification does not have any trainings.\nYou can do a longclick to add new trainings to this Qualification");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Qualification selected.\nFirts you must select an Qualification");
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
			Transactions.addSpecialCommand(new Command(QualificationNavigationBarFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isLongClickable())
				{
					if(clickedQualification != null)
					{
						if(view.getId() == R.id.qualification_navigationbar_association_workers)
						{
							UtilNavigate.toActivityForResult(getActivity(), WorkerActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("QUALIFICATIONObject", clickedQualification.ID(), "AssociationEndMultiplicityKey", -1, "IsQualifiedAssociation", "IsQualifiedAssociation"), MasterActivity.CREATION_CODE);
						}
						if(view.getId() == R.id.qualification_navigationbar_association_projects)
						{
							setSuperClassOffSpringsListeners(UtilNavigate.showInheritanceList(getActivity(), R.layout.qualification_generalizationoptions_projects_view), "RequiresAssociation");
						}
						if(view.getId() == R.id.qualification_navigationbar_association_trainings)
						{
							UtilNavigate.toActivityForResult(getActivity(), TrainingActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("QUALIFICATIONObject", clickedQualification.ID(), "AssociationEndMultiplicityKey", -1, "TrainsAssociation", "TrainsAssociation"), MasterActivity.CREATION_CODE);
						}
					}else
						UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Qualification selected.\nFirts you must select an Qualification");
				}
			}
			return false;
		}
	};
	
	public void setSuperClassOffSpringsListeners(View view, final String AssociationSource)
	{
		if(view != null)
		{
			if(view.findViewById(R.id.qualification_generalizationoptions_projects) != null)
			{
				view.findViewById(R.id.qualification_generalizationoptions_projects).setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						UtilNavigate.toActivityForResult(getActivity(), ProjectActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("QUALIFICATIONObject", clickedQualification.ID(), "AssociationEndMultiplicityKey", -1, AssociationSource, AssociationSource), MasterActivity.CREATION_CODE);
					}
				});
				}
			if(view.findViewById(R.id.project_generalizationoptions_training) != null)
			{
				view.findViewById(R.id.project_generalizationoptions_training).setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						UtilNavigate.toActivityForResult(getActivity(), TrainingActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("QUALIFICATIONObject", clickedQualification.ID(), "AssociationEndMultiplicityKey", -1, AssociationSource, AssociationSource), MasterActivity.CREATION_CODE);
					}
				});
			}
		}
	}
	
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
						setViewingObject((Qualification) event.getNewObject());
					break;
					case INSERT_ASSOCIATION:
						setViewingObject((Qualification) event.getNewObject());
					break;
					case UPDATE:
						//no need to change anything
					break;
					case DELETE:
						if(clickedQualificationID == event.getOldObjectID())
							setViewingObject(null);
					break;
					case DELETE_ASSOCIATION:
						if(clickedQualificationID == event.getOldObjectID())
							setViewingObject((Qualification) event.getNewObject());
					break;
					default:
					break;
				}
			}
		});
	}
	
}
