/**********************************************************************
* Filename: Company.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.presentationLayer.Company;

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
import org.quasar.ProjectsWorld.businessLayer.Company;
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

public class CompanyNavigationBarFragment extends Fragment implements PropertyChangeListener, NavigationBarFragment{

	private View rootView = null;
	private Company clickedCompany = null;
	private int clickedCompanyID;
	private final String COMPANYID = "COMPANYID";
	private TextView number_objects;
	private View employeesView;
	private View projectsView;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public CompanyNavigationBarFragment()
	{
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
		{
			clickedCompany = Company.getCompany(savedInstanceState.getInt(COMPANYID));
		}
		Company.getAccess().setChangeListener(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(clickedCompany != null)
		{
			outState.putInt(COMPANYID, clickedCompany.ID());
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Company.getAccess().removeChangeListener(this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		employeesView = (View) getActivity().findViewById(R.id.company_navigationbar_association_employees);
		employeesView.setOnClickListener(ClickListener);
		employeesView.setOnLongClickListener(LongClickListener);
		
		projectsView = (View) getActivity().findViewById(R.id.company_navigationbar_association_projects);
		projectsView.setOnClickListener(ClickListener);
		projectsView.setOnLongClickListener(LongClickListener);
		
		setViewingObject(clickedCompany);
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.company_view_navigationbar, container, false);
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
	
	public void setViewingObject(Object company)
	{
		if(company instanceof Company)
		{
			clickedCompany = (Company) company;
			if(company != null)
				clickedCompanyID = ((Company) company).ID();
			refreshNavigationBar(clickedCompany);
		}
		if(company == null)
		{
			refreshNavigationBar(null);
		}
	}
	
	public void refreshNavigationBar(Company company)
	{
		if(company != null)
		{
			number_objects = (TextView) rootView.findViewById(R.id.company_navigationbar_association_employees_numberobjects);
			if(company.employees().isEmpty())
			{
				prepareView(employeesView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(employeesView, true);
				setNumberAssociation(number_objects, clickedCompany.employees().size());
			}
			
			number_objects = (TextView) rootView.findViewById(R.id.company_navigationbar_association_projects_numberobjects);
			if(company.projects().isEmpty())
			{
				prepareView(projectsView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(projectsView, true);
				setNumberAssociation(number_objects, clickedCompany.projects().size());
			}
			
			objectValidation();
		}
		else
		{
			number_objects = (TextView) rootView.findViewById(R.id.company_navigationbar_association_employees_numberobjects);
			prepareView(employeesView, false);
			setNumberAssociation(number_objects, 0);
			employeesView.setBackgroundResource(R.drawable.navigationbar_selector);
			
			number_objects = (TextView) rootView.findViewById(R.id.company_navigationbar_association_projects_numberobjects);
			prepareView(projectsView, false);
			setNumberAssociation(number_objects, 0);
			projectsView.setBackgroundResource(R.drawable.navigationbar_selector);
			
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
		if(clickedCompany.validEmployees())
		{
			employeesView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			employeesView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
		if(clickedCompany.validProjects())
		{
			projectsView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			projectsView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
	}
	
	OnClickListener ClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			Transactions.addSpecialCommand(new Command(CompanyNavigationBarFragment.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isClickable())
				{
					if(clickedCompany != null)
					{
						if(view.getId() == R.id.company_navigationbar_association_employees)
						{
							UtilNavigate.toActivity(getActivity(), WorkerActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("COMPANYObject", clickedCompany.ID(), "AssociationEndMultiplicityKey", -1, "EmploysAssociation", "EmploysAssociation"));
						}
						if(view.getId() == R.id.company_navigationbar_association_projects)
						{
							UtilNavigate.toActivity(getActivity(), ProjectActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("COMPANYObject", clickedCompany.ID(), "AssociationEndMultiplicityKey", -1, "CarriesOutAssociation", "CarriesOutAssociation"));
						}
					}
				}
				else
				{
					if(view.getId() == R.id.company_navigationbar_association_employees)
					{
						if(clickedCompany != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Company does not have any employees.\nYou can do a longclick to add new employees to this Company");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Company selected.\nFirts you must select an Company");
					}
					if(view.getId() == R.id.company_navigationbar_association_projects)
					{
						if(clickedCompany != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Company does not have any projects.\nYou can do a longclick to add new projects to this Company");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Company selected.\nFirts you must select an Company");
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
			Transactions.addSpecialCommand(new Command(CompanyNavigationBarFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isLongClickable())
				{
					if(clickedCompany != null)
					{
						if(view.getId() == R.id.company_navigationbar_association_employees)
						{
							UtilNavigate.toActivityForResult(getActivity(), WorkerActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("COMPANYObject", clickedCompany.ID(), "AssociationEndMultiplicityKey", -1, "EmploysAssociation", "EmploysAssociation"), MasterActivity.CREATION_CODE);
						}
						if(view.getId() == R.id.company_navigationbar_association_projects)
						{
							setSuperClassOffSpringsListeners(UtilNavigate.showInheritanceList(getActivity(), R.layout.company_generalizationoptions_projects_view), "CarriesOutAssociation");
						}
					}else
						UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Company selected.\nFirts you must select an Company");
				}
			}
			return false;
		}
	};
	
	public void setSuperClassOffSpringsListeners(View view, final String AssociationSource)
	{
		if(view != null)
		{
			if(view.findViewById(R.id.company_generalizationoptions_projects) != null)
			{
				view.findViewById(R.id.company_generalizationoptions_projects).setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						UtilNavigate.toActivityForResult(getActivity(), ProjectActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("COMPANYObject", clickedCompany.ID(), "AssociationEndMultiplicityKey", -1, AssociationSource, AssociationSource), MasterActivity.CREATION_CODE);
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
						UtilNavigate.toActivityForResult(getActivity(), TrainingActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("COMPANYObject", clickedCompany.ID(), "AssociationEndMultiplicityKey", -1, AssociationSource, AssociationSource), MasterActivity.CREATION_CODE);
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
						setViewingObject((Company) event.getNewObject());
					break;
					case INSERT_ASSOCIATION:
						setViewingObject((Company) event.getNewObject());
					break;
					case UPDATE:
						//no need to change anything
					break;
					case DELETE:
						if(clickedCompanyID == event.getOldObjectID())
							setViewingObject(null);
					break;
					case DELETE_ASSOCIATION:
						if(clickedCompanyID == event.getOldObjectID())
							setViewingObject((Company) event.getNewObject());
					break;
					default:
					break;
				}
			}
		});
	}
	
}
