/**********************************************************************
* Filename: Worker.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.presentationLayer.Worker;

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
import org.quasar.ProjectsWorld.businessLayer.Worker;
import org.quasar.ProjectsWorld.presentationLayer.Qualification.QualificationActivity;
import org.quasar.ProjectsWorld.presentationLayer.Member.MemberActivity;
import org.quasar.ProjectsWorld.presentationLayer.Training.TrainingActivity;
import org.quasar.ProjectsWorld.presentationLayer.Project.ProjectActivity;
import org.quasar.ProjectsWorld.presentationLayer.Company.CompanyActivity;

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

public class WorkerNavigationBarFragment extends Fragment implements PropertyChangeListener, NavigationBarFragment{

	private View rootView = null;
	private Worker clickedWorker = null;
	private int clickedWorkerID;
	private final String WORKERID = "WORKERID";
	private TextView number_objects;
	private View qualificationsView;
	private View memberView;
	private View projectsView;
	private View employerView;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public WorkerNavigationBarFragment()
	{
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
		{
			clickedWorker = Worker.getWorker(savedInstanceState.getInt(WORKERID));
		}
		Worker.getAccess().setChangeListener(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(clickedWorker != null)
		{
			outState.putInt(WORKERID, clickedWorker.ID());
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Worker.getAccess().removeChangeListener(this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		qualificationsView = (View) getActivity().findViewById(R.id.worker_navigationbar_association_qualifications);
		qualificationsView.setOnClickListener(ClickListener);
		qualificationsView.setOnLongClickListener(LongClickListener);
		
		memberView = (View) getActivity().findViewById(R.id.worker_navigationbar_association_member);
		memberView.setOnClickListener(ClickListener);
		memberView.setOnLongClickListener(LongClickListener);
		
		projectsView = (View) getActivity().findViewById(R.id.worker_navigationbar_association_projects);
		projectsView.setOnClickListener(ClickListener);
		projectsView.setOnLongClickListener(LongClickListener);
		
		employerView = (View) getActivity().findViewById(R.id.worker_navigationbar_association_employer);
		employerView.setOnClickListener(ClickListener);
		employerView.setOnLongClickListener(LongClickListener);
		
		setViewingObject(clickedWorker);
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.worker_view_navigationbar, container, false);
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
	
	public void setViewingObject(Object worker)
	{
		if(worker instanceof Worker)
		{
			clickedWorker = (Worker) worker;
			if(worker != null)
				clickedWorkerID = ((Worker) worker).ID();
			refreshNavigationBar(clickedWorker);
		}
		if(worker == null)
		{
			refreshNavigationBar(null);
		}
	}
	
	public void refreshNavigationBar(Worker worker)
	{
		if(worker != null)
		{
			number_objects = (TextView) rootView.findViewById(R.id.worker_navigationbar_association_qualifications_numberobjects);
			if(worker.qualifications().isEmpty())
			{
				prepareView(qualificationsView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(qualificationsView, true);
				setNumberAssociation(number_objects, clickedWorker.qualifications().size());
			}
			
			number_objects = (TextView) rootView.findViewById(R.id.worker_navigationbar_association_member_numberobjects);
			if(worker.member().isEmpty())
			{
				prepareView(memberView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(memberView, true);
				setNumberAssociation(number_objects, clickedWorker.member().size());
			}
			
			number_objects = (TextView) rootView.findViewById(R.id.worker_navigationbar_association_projects_numberobjects);
			if(worker.projects().isEmpty())
			{
				prepareView(projectsView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(projectsView, true);
				setNumberAssociation(number_objects, clickedWorker.projects().size());
			}
			
			number_objects = (TextView) rootView.findViewById(R.id.worker_navigationbar_association_employer_numberobjects);
			if(worker.employer() == null)
			{
				prepareView(employerView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(employerView, true);
				setNumberAssociation(number_objects, 1);
			}
			
			objectValidation();
		}
		else
		{
			number_objects = (TextView) rootView.findViewById(R.id.worker_navigationbar_association_qualifications_numberobjects);
			prepareView(qualificationsView, false);
			setNumberAssociation(number_objects, 0);
			qualificationsView.setBackgroundResource(R.drawable.navigationbar_selector);
			
			number_objects = (TextView) rootView.findViewById(R.id.worker_navigationbar_association_member_numberobjects);
			prepareView(memberView, false);
			setNumberAssociation(number_objects, 0);
			memberView.setBackgroundResource(R.drawable.navigationbar_selector);
			
			number_objects = (TextView) rootView.findViewById(R.id.worker_navigationbar_association_projects_numberobjects);
			prepareView(projectsView, false);
			setNumberAssociation(number_objects, 0);
			projectsView.setBackgroundResource(R.drawable.navigationbar_selector);
			
			number_objects = (TextView) rootView.findViewById(R.id.worker_navigationbar_association_employer_numberobjects);
			prepareView(employerView, false);
			setNumberAssociation(number_objects, 0);
			employerView.setBackgroundResource(R.drawable.navigationbar_selector);
			
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
		if(clickedWorker.validQualifications())
		{
			qualificationsView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			qualificationsView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
		if(clickedWorker.validMember())
		{
			memberView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			memberView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
		if(clickedWorker.validProjects())
		{
			projectsView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			projectsView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
		if(clickedWorker.validEmployer())
		{
			employerView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			employerView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
	}
	
	OnClickListener ClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			Transactions.addSpecialCommand(new Command(WorkerNavigationBarFragment.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isClickable())
				{
					if(clickedWorker != null)
					{
						if(view.getId() == R.id.worker_navigationbar_association_qualifications)
						{
							UtilNavigate.toActivity(getActivity(), QualificationActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("WORKERObject", clickedWorker.ID(), "AssociationEndMultiplicityKey", -1, "IsQualifiedAssociation", "IsQualifiedAssociation"));
						}
						if(view.getId() == R.id.worker_navigationbar_association_member)
						{
							UtilNavigate.toActivity(getActivity(), MemberActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("WORKERObject", clickedWorker.ID(), "AssociationEndMultiplicityKey", -1, "members_MemberAssociation", "members_MemberAssociation"));
						}
						if(view.getId() == R.id.worker_navigationbar_association_projects)
						{
							UtilNavigate.toActivity(getActivity(), ProjectActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("WORKERObject", clickedWorker.ID(), "AssociationEndMultiplicityKey", -1, "MemberAssociation", "MemberAssociation"));
						}
						if(view.getId() == R.id.worker_navigationbar_association_employer)
						{
							UtilNavigate.toActivity(getActivity(), CompanyActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("WORKERObject", clickedWorker.ID(), "AssociationEndMultiplicityKey", 1, "EmploysAssociation", "EmploysAssociation"));
						}
					}
				}
				else
				{
					if(view.getId() == R.id.worker_navigationbar_association_qualifications)
					{
						if(clickedWorker != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Worker does not have any qualifications.\nYou can do a longclick to add new qualifications to this Worker");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Worker selected.\nFirts you must select an Worker");
					}
					if(view.getId() == R.id.worker_navigationbar_association_member)
					{
						if(clickedWorker != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Worker does not have any projects.\nYou can do a longclick to add new projects to this Worker");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Worker selected.\nFirts you must select an Worker");
					}
					if(view.getId() == R.id.worker_navigationbar_association_projects)
					{
						if(clickedWorker != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Worker does not have any projects.\nYou can do a longclick to add new projects to this Worker");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Worker selected.\nFirts you must select an Worker");
					}
					if(view.getId() == R.id.worker_navigationbar_association_employer)
					{
						if(clickedWorker != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Worker does not have any employer.\nYou can do a longclick to add new employer to this Worker");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Worker selected.\nFirts you must select an Worker");
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
			Transactions.addSpecialCommand(new Command(WorkerNavigationBarFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isLongClickable())
				{
					if(clickedWorker != null)
					{
						if(view.getId() == R.id.worker_navigationbar_association_qualifications)
						{
							UtilNavigate.toActivityForResult(getActivity(), QualificationActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("WORKERObject", clickedWorker.ID(), "AssociationEndMultiplicityKey", -1, "IsQualifiedAssociation", "IsQualifiedAssociation"), MasterActivity.CREATION_CODE);
						}
						if(view.getId() == R.id.worker_navigationbar_association_member)
						{
							UtilNavigate.toActivityForResult(getActivity(), MemberActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("WORKERObject", clickedWorker.ID(), "AssociationEndMultiplicityKey", -1, "members_MemberAssociation", "members_MemberAssociation"), MasterActivity.CREATION_CODE);
						}
						if(view.getId() == R.id.worker_navigationbar_association_projects)
						{
							setSuperClassOffSpringsListeners(UtilNavigate.showInheritanceList(getActivity(), R.layout.worker_generalizationoptions_projects_view), "MemberAssociation");
						}
						if(view.getId() == R.id.worker_navigationbar_association_employer)
						{
							UtilNavigate.toActivityForResult(getActivity(), CompanyActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("WORKERObject", clickedWorker.ID(), "AssociationEndMultiplicityKey", -1, "EmploysAssociation", "EmploysAssociation"), MasterActivity.CREATION_CODE);
						}
					}else
						UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Worker selected.\nFirts you must select an Worker");
				}
			}
			return false;
		}
	};
	
	public void setSuperClassOffSpringsListeners(View view, final String AssociationSource)
	{
		if(view != null)
		{
			if(view.findViewById(R.id.worker_generalizationoptions_projects) != null)
			{
				view.findViewById(R.id.worker_generalizationoptions_projects).setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						UtilNavigate.toActivityForResult(getActivity(), ProjectActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("WORKERObject", clickedWorker.ID(), "AssociationEndMultiplicityKey", -1, AssociationSource, AssociationSource), MasterActivity.CREATION_CODE);
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
						UtilNavigate.toActivityForResult(getActivity(), TrainingActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("WORKERObject", clickedWorker.ID(), "AssociationEndMultiplicityKey", -1, AssociationSource, AssociationSource), MasterActivity.CREATION_CODE);
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
						setViewingObject((Worker) event.getNewObject());
					break;
					case INSERT_ASSOCIATION:
						setViewingObject((Worker) event.getNewObject());
					break;
					case UPDATE:
						//no need to change anything
					break;
					case DELETE:
						if(clickedWorkerID == event.getOldObjectID())
							setViewingObject(null);
					break;
					case DELETE_ASSOCIATION:
						if(clickedWorkerID == event.getOldObjectID())
							setViewingObject((Worker) event.getNewObject());
					break;
					default:
					break;
				}
			}
		});
	}
	
}
