/**********************************************************************
* Filename: Member.java
* Created: 2016/01/14
* @author Lu�s Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.presentationLayer.Member;

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
import org.quasar.ProjectsWorld.businessLayer.Member;
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

public class MemberNavigationBarFragment extends Fragment implements PropertyChangeListener, NavigationBarFragment{

	private View rootView = null;
	private Member clickedMember = null;
	private int clickedMemberID;
	private final String MEMBERID = "MEMBERID";
	private TextView number_objects;
	private View membersView;
	private View projectsView;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public MemberNavigationBarFragment()
	{
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
		{
			clickedMember = Member.getMember(savedInstanceState.getInt(MEMBERID));
		}
		Member.getAccess().setChangeListener(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(clickedMember != null)
		{
			outState.putInt(MEMBERID, clickedMember.ID());
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Member.getAccess().removeChangeListener(this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		membersView = (View) getActivity().findViewById(R.id.member_navigationbar_association_members);
		membersView.setOnClickListener(ClickListener);
		membersView.setOnLongClickListener(LongClickListener);
		
		projectsView = (View) getActivity().findViewById(R.id.member_navigationbar_association_projects);
		projectsView.setOnClickListener(ClickListener);
		projectsView.setOnLongClickListener(LongClickListener);
		
		setViewingObject(clickedMember);
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.member_view_navigationbar, container, false);
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
	
	public void setViewingObject(Object member)
	{
		if(member instanceof Member)
		{
			clickedMember = (Member) member;
			if(member != null)
				clickedMemberID = ((Member) member).ID();
			refreshNavigationBar(clickedMember);
		}
		if(member == null)
		{
			refreshNavigationBar(null);
		}
	}
	
	public void refreshNavigationBar(Member member)
	{
		if(member != null)
		{
			number_objects = (TextView) rootView.findViewById(R.id.member_navigationbar_association_members_numberobjects);
			if(member.members() == null)
			{
				prepareView(membersView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(membersView, true);
				setNumberAssociation(number_objects, 1);
			}
			
			number_objects = (TextView) rootView.findViewById(R.id.member_navigationbar_association_projects_numberobjects);
			if(member.projects() == null)
			{
				prepareView(projectsView, false);
				setNumberAssociation(number_objects, 0);
			}
			else
			{
				prepareView(projectsView, true);
				setNumberAssociation(number_objects, 1);
			}
			
			objectValidation();
		}
		else
		{
			number_objects = (TextView) rootView.findViewById(R.id.member_navigationbar_association_members_numberobjects);
			prepareView(membersView, false);
			setNumberAssociation(number_objects, 0);
			membersView.setBackgroundResource(R.drawable.navigationbar_selector);
			
			number_objects = (TextView) rootView.findViewById(R.id.member_navigationbar_association_projects_numberobjects);
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
		if(clickedMember.validMembers())
		{
			membersView.setBackgroundResource(R.drawable.navigationbar_selector);
		}
		else
		{
			membersView.setBackgroundResource(R.drawable.navigationbar_selector_error);
		}
		if(clickedMember.validProjects())
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
			Transactions.addSpecialCommand(new Command(MemberNavigationBarFragment.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isClickable())
				{
					if(clickedMember != null)
					{
						if(view.getId() == R.id.member_navigationbar_association_members)
						{
							UtilNavigate.toActivity(getActivity(), WorkerActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("MEMBERObject", clickedMember.ID(), "AssociationEndMultiplicityKey", 1, "members_MemberAssociation", "members_MemberAssociation"));
						}
						if(view.getId() == R.id.member_navigationbar_association_projects)
						{
							UtilNavigate.toActivity(getActivity(), ProjectActivity.class, MasterActivity.ACTION_MODE_READ, UtilNavigate.setActivityBundleArguments("MEMBERObject", clickedMember.ID(), "AssociationEndMultiplicityKey", 1, "projects_MemberAssociation", "projects_MemberAssociation"));
						}
					}
				}
				else
				{
					if(view.getId() == R.id.member_navigationbar_association_members)
					{
						if(clickedMember != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Member does not have any members.\nYou can do a longclick to add new members to this Member");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Member selected.\nFirts you must select an Member");
					}
					if(view.getId() == R.id.member_navigationbar_association_projects)
					{
						if(clickedMember != null)
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "The selected Member does not have any projects.\nYou can do a longclick to add new projects to this Member");
						else
							UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Member selected.\nFirts you must select an Member");
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
			Transactions.addSpecialCommand(new Command(MemberNavigationBarFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
			if (getActivity().getIntent().getAction().equals("WRITE"))
			{
				UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "You are in CREATION MODE.\nPlease finish this action (object association) before trying to further navigate.\n\nNOTE: The upper Android icon will turn green when this action is completed or canceled");
			}
			else
			{
				if(view.isLongClickable())
				{
					if(clickedMember != null)
					{
						if(view.getId() == R.id.member_navigationbar_association_members)
						{
							UtilNavigate.toActivityForResult(getActivity(), WorkerActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("MEMBERObject", clickedMember.ID(), "AssociationEndMultiplicityKey", -1, "members_MemberAssociation", "members_MemberAssociation"), MasterActivity.CREATION_CODE);
						}
						if(view.getId() == R.id.member_navigationbar_association_projects)
						{
							setSuperClassOffSpringsListeners(UtilNavigate.showInheritanceList(getActivity(), R.layout.member_generalizationoptions_projects_view), "projects_MemberAssociation");
						}
					}else
						UtilNavigate.showWarning(getActivity(), "Navigation Bar Warning", "There is not any Member selected.\nFirts you must select an Member");
				}
			}
			return false;
		}
	};
	
	public void setSuperClassOffSpringsListeners(View view, final String AssociationSource)
	{
		if(view != null)
		{
			if(view.findViewById(R.id.member_generalizationoptions_projects) != null)
			{
				view.findViewById(R.id.member_generalizationoptions_projects).setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						UtilNavigate.toActivityForResult(getActivity(), ProjectActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("MEMBERObject", clickedMember.ID(), "AssociationEndMultiplicityKey", -1, AssociationSource, AssociationSource), MasterActivity.CREATION_CODE);
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
						UtilNavigate.toActivityForResult(getActivity(), TrainingActivity.class, MasterActivity.ACTION_MODE_WRITE, UtilNavigate.setActivityBundleArguments("MEMBERObject", clickedMember.ID(), "AssociationEndMultiplicityKey", -1, AssociationSource, AssociationSource), MasterActivity.CREATION_CODE);
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
						setViewingObject((Member) event.getNewObject());
					break;
					case INSERT_ASSOCIATION:
						setViewingObject((Member) event.getNewObject());
					break;
					case UPDATE:
						//no need to change anything
					break;
					case DELETE:
						if(clickedMemberID == event.getOldObjectID())
							setViewingObject(null);
					break;
					case DELETE_ASSOCIATION:
						if(clickedMemberID == event.getOldObjectID())
							setViewingObject((Member) event.getNewObject());
					break;
					default:
					break;
				}
			}
		});
	}
	
}
