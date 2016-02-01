/**********************************************************************
* Filename: Training.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.presentationLayer.Training;

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
import org.quasar.ProjectsWorld.businessLayer.Training;
import org.quasar.ProjectsWorld.businessLayer.Project;
import org.quasar.ProjectsWorld.presentationLayer.Member.MemberActivity;
import org.quasar.ProjectsWorld.presentationLayer.Worker.WorkerActivity;
import org.quasar.ProjectsWorld.presentationLayer.Qualification.QualificationActivity;
import org.quasar.ProjectsWorld.presentationLayer.Company.CompanyActivity;
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

public class TrainingNavigationBarFragment extends Fragment implements PropertyChangeListener, NavigationBarFragment{

	private View rootView = null;
	private Training clickedTraining = null;
	private int clickedTrainingID;
	private final String TRAININGID = "TRAININGID";
	private TextView number_objects;
	private View memberView;
	private View membersView;
	private View requirementsView;
	private View companyView;
	private View trainedView;
	private View ProjectView;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public TrainingNavigationBarFragment()
	{
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
		{
			clickedTraining = Training.getTraining(savedInstanceState.getInt(TRAININGID));
		}
		Training.getAccess().setChangeListener(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(clickedTraining != null)
		{
			outState.putInt(TRAININGID, clickedTraining.ID());
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Training.getAccess().removeChangeListener(this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		memberView = (View) getActivity().findViewById(R.id.project_navigationbar_association_member);
		memberView.setOnClickListener(ClickListener);
		memberView.setOnLongClickListener(LongClickListener);
		
		membersView = (View) getActivity().findViewById(R.id.project_navigationbar_association_members);
		membersView.setOnClickListener(ClickListener);
		membersView.setOnLongClickListener(LongClickListener);
		
		requirementsView = (View) getActivity().findViewById(R.id.project_navigationbar_association_requirements);
		requirementsView.setOnClickListener(ClickListener);
		requirementsView.setOnLongClickListener(LongClickListener);
		
		companyView = (View) getActivity().findViewById(R.id.project_navigationbar_association_company);
		companyView.setOnClickListener(ClickListener);
		companyView.setOnLongClickListener(LongClickListener);
		
		trainedView = (View) getActivity().findViewById(R.id.training_navigationbar_association_trained);
		trainedView.setOnClickListener(ClickListener);
		trainedView.setOnLongClickListener(LongClickListener);
		
		ProjectView = (View) getActivity().findViewById(R.id.training_navigationbar_association_project);
		ProjectView.setOnClickListener(ClickListener);
		ProjectView.setOnLongClickListener(LongClickListener);
		
		setViewingObject(clickedTraining);
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.training_view_navigationbar, container, false);
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
	
	public void setViewingObject(Object training)
	{
		if(training instanceof Training)
		{
			clickedTraining = (Training) training;
			if(training != null)
				clickedTrainingID = ((Training) training).ID();
			refreshNavigationBar(clickedTraining);
		}
		if(training == null)
		{
			refreshNavigationBar(null);
		}
	}
	
	public void refreshNavigationBar(Training training)
	{
		if(training != null)
		{
			number_objects = (TextView) rootView.findViewById(R.id.project_navigationbar_association_member_numberobjects);
			if(training.member().isEmpty())
			{
				prepareView(memberView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(memberView, true);
				setNumberAssociation(number_objects, clickedTraining.member().size());
			}
			
			number_objects = (TextView) rootView.findViewById(R.id.project_navigationbar_association_members_numberobjects);
			if(training.members().isEmpty())
			{
				prepareView(membersView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(membersView, true);
				setNumberAssociation(number_objects, clickedTraining.members().size());
			}
			
			number_objects = (TextView) rootView.findViewById(R.id.project_navigationbar_association_requirements_numberobjects);
			if(training.requirements().isEmpty())
			{
				prepareView(requirementsView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(requirementsView, true);
				setNumberAssociation(number_objects, clickedTraining.requirements().size());
			}
			
			number_objects = (TextView) rootView.findViewById(R.id.project_navigationbar_association_company_numberobjects);
			if(training.company() == null)
			{
				prepareView(companyView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(companyView, true);
				setNumberAssociation(number_objects, 1);
			}
			
			number_objects = (TextView) rootView.findViewById(R.id.training_navigationbar_association_trained_numberobjects);
			if(training.trained().isEmpty())
			{
				prepareView(trainedView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(trainedView, true);
				setNumberAssociation(number_objects, clickedTraining.trained().size());
			}
			
			objectValidation();
		}
		else
		{
			number_objects = (TextView) rootView.findViewById(R.id.project_navigationbar_association_member_numberobjects);
			prepareView(memberView, false);
			setNumberAssociation(number_objects, 0);
			memberView.setBackgroundResource(R.drawable.navigationbar_selector);
			
			number_objects = (TextView) rootView.findViewById(R.id.project_navigationbar_association_members_numberobjects);
			prepareView(membersView, false);
			setNumberAssociation(number_objects, 0);
			membersView.setBackgroundResource(R.drawable.navigationbar_selector);
			
			number_objects = (TextView) rootView.findViewById(R.id.project_navigationbar_association_requirements_numberobjects);
			prepareView(requirementsView, false);
			setNumberAssociation(number_objects, 0);
			requirementsView.setBackgroundResource(R.drawable.navigationbar_selector);
			
			number_objects = (TextView) rootView.findViewById(R.id.project_navigationbar_association_company_numberobjects);
			prepareView(companyView, false);
			setNumberAssociation(number_objects, 0);
			companyView.setBackgroundResource(R.drawable.navigationbar_selector);
			
			number_objects = (TextView) rootView.findViewById(R.id.training_navigationbar_association_trained_numberobjects);
			prepareView(trainedView, false);
			setNumberAssociation(number_objects, 0);
			trainedView.setBackgroundResource(R.drawable.navigationbar_selector);
			
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
		if(clickedTraining.validMember())
		{
			memberView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			memberView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
		if(clickedTraining.validMembers())
		{
			membersView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			membersView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
		if(clickedTraining.validRequirements())
		{
			requirementsView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			requirementsView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
		if(clickedTraining.validCompany())
		{
			companyView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			companyView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
		if(clickedTraining.validTrained())
		{
			trainedView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			trainedView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
	}
	
	OnClickListener ClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			Transactions.addSpecialCommand(new Command(TrainingNavigationBarFragment.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isClickable())
				{
					if(clickedTraining != null)
					{
						if(view.getId() == R.id.project_navigationbar_association_member)
						{
							UtilNavigate.toActivity(getActivity(), MemberActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("PROJECTObject", clickedTraining.ID(), "AssociationEndMultiplicityKey", -1, "projects_MemberAssociation", "projects_MemberAssociation"));
						}
						if(view.getId() == R.id.project_navigationbar_association_members)
						{
							UtilNavigate.toActivity(getActivity(), WorkerActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("PROJECTObject", clickedTraining.ID(), "AssociationEndMultiplicityKey", -1, "MemberAssociation", "MemberAssociation"));
						}
						if(view.getId() == R.id.project_navigationbar_association_requirements)
						{
							UtilNavigate.toActivity(getActivity(), QualificationActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("PROJECTObject", clickedTraining.ID(), "AssociationEndMultiplicityKey", -1, "RequiresAssociation", "RequiresAssociation"));
						}
						if(view.getId() == R.id.project_navigationbar_association_company)
						{
							UtilNavigate.toActivity(getActivity(), CompanyActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("PROJECTObject", clickedTraining.ID(), "AssociationEndMultiplicityKey", 1, "CarriesOutAssociation", "CarriesOutAssociation"));
						}
						if(view.getId() == R.id.training_navigationbar_association_trained)
						{
							UtilNavigate.toActivity(getActivity(), QualificationActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("TRAININGObject", clickedTraining.ID(), "AssociationEndMultiplicityKey", -1, "TrainsAssociation", "TrainsAssociation"));
						}
						if(view.getId() == R.id.training_navigationbar_association_project)
						{
							UtilNavigate.toActivity(getActivity(), ProjectActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("PROJECTObject", clickedTraining.ID(), "AssociationEndMultiplicityKey", 1, "PROJECTAssociation", "PROJECTAssociation"));
						}
					}
				}
				else
				{
					if(view.getId() == R.id.project_navigationbar_association_member)
					{
						if(clickedTraining != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Training does not have any members.\nYou can do a longclick to add new members to this Training");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Training selected.\nFirts you must select an Training");
					}
					if(view.getId() == R.id.project_navigationbar_association_members)
					{
						if(clickedTraining != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Training does not have any members.\nYou can do a longclick to add new members to this Training");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Training selected.\nFirts you must select an Training");
					}
					if(view.getId() == R.id.project_navigationbar_association_requirements)
					{
						if(clickedTraining != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Training does not have any requirements.\nYou can do a longclick to add new requirements to this Training");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Training selected.\nFirts you must select an Training");
					}
					if(view.getId() == R.id.project_navigationbar_association_company)
					{
						if(clickedTraining != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Training does not have any company.\nYou can do a longclick to add new company to this Training");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Training selected.\nFirts you must select an Training");
					}
					if(view.getId() == R.id.training_navigationbar_association_trained)
					{
						if(clickedTraining != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Training does not have any trained.\nYou can do a longclick to add new trained to this Training");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Training selected.\nFirts you must select an Training");
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
			Transactions.addSpecialCommand(new Command(TrainingNavigationBarFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isLongClickable())
				{
					if(clickedTraining != null)
					{
						if(view.getId() == R.id.project_navigationbar_association_member)
						{
							UtilNavigate.toActivityForResult(getActivity(), MemberActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("PROJECTObject", clickedTraining.ID(), "AssociationEndMultiplicityKey", -1, "projects_MemberAssociation", "projects_MemberAssociation"), MasterActivity.CREATION_CODE);
						}
						if(view.getId() == R.id.project_navigationbar_association_members)
						{
							UtilNavigate.toActivityForResult(getActivity(), MemberActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("PROJECTObject", clickedTraining.ID(), "AssociationEndMultiplicityKey", -1, "MemberAssociation", "MemberAssociation"), MasterActivity.CREATION_CODE);
						}
						if(view.getId() == R.id.project_navigationbar_association_requirements)
						{
							UtilNavigate.toActivityForResult(getActivity(), QualificationActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("PROJECTObject", clickedTraining.ID(), "AssociationEndMultiplicityKey", -1, "RequiresAssociation", "RequiresAssociation"), MasterActivity.CREATION_CODE);
						}
						if(view.getId() == R.id.project_navigationbar_association_company)
						{
							UtilNavigate.toActivityForResult(getActivity(), CompanyActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("PROJECTObject", clickedTraining.ID(), "AssociationEndMultiplicityKey", -1, "CarriesOutAssociation", "CarriesOutAssociation"), MasterActivity.CREATION_CODE);
						}
						if(view.getId() == R.id.training_navigationbar_association_trained)
						{
							UtilNavigate.toActivityForResult(getActivity(), QualificationActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("TRAININGObject", clickedTraining.ID(), "AssociationEndMultiplicityKey", -1, "TrainsAssociation", "TrainsAssociation"), MasterActivity.CREATION_CODE);
						}
						if(view.getId() == R.id.training_navigationbar_association_project)
						{
							UtilNavigate.showWarning(getActivity(), "Action - Association creation", "this action is not define since is a navigation to a super class");
						}
					}else
						UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Training selected.\nFirts you must select an Training");
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
						setViewingObject((Training) event.getNewObject());
					break;
					case INSERT_ASSOCIATION:
						setViewingObject((Training) event.getNewObject());
					break;
					case UPDATE:
						//no need to change anything
					break;
					case DELETE:
						if(clickedTrainingID == event.getOldObjectID())
							setViewingObject(null);
					break;
					case DELETE_ASSOCIATION:
						if(clickedTrainingID == event.getOldObjectID())
							setViewingObject((Training) event.getNewObject());
					break;
					default:
					break;
				}
			}
		});
	}
	
}
