/**********************************************************************
* Filename: Member.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.presentationLayer.Member;

import org.quasar.ProjectsWorld.R;
import org.quasar.ProjectsWorld.utils.Transactions;
import org.quasar.ProjectsWorld.utils.Command;
import org.quasar.ProjectsWorld.utils.CommandType;
import org.quasar.ProjectsWorld.utils.CommandTargetLayer;
import org.quasar.ProjectsWorld.utils.UtilNavigate;
import org.quasar.ProjectsWorld.utils.DetailFragment;
import org.quasar.ProjectsWorld.utils.PropertyChangeEvent;
import org.quasar.ProjectsWorld.utils.PropertyChangeListener;
import org.quasar.ProjectsWorld.businessLayer.Member;
import org.quasar.ProjectsWorld.businessLayer.Worker;
import org.quasar.ProjectsWorld.presentationLayer.Worker.WorkerDetailFragment;
import org.quasar.ProjectsWorld.businessLayer.Project;
import org.quasar.ProjectsWorld.presentationLayer.Project.ProjectDetailFragment;

import android.view.ViewGroup;
import android.widget.EditText;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;
import android.widget.DatePicker;
import android.view.View.OnClickListener;
import android.view.View;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.app.Activity;
import org.quasar.ProjectsWorld.businessLayer.CalendarDate;
import org.quasar.ProjectsWorld.presentationLayer.CalendarDate.CalendarDateDetailFragment;

public class MemberDetailFragment extends Fragment implements PropertyChangeListener, DetailFragment{
	public static final String ARG_VIEW_DETAIL = "detail";
	public static final String ARG_VIEW_NEW = "new";
	public static final String ARG_VIEW_EDIT = "edit";
	public String ARG_VIEW = "";
	private Fragment fragment;
	private Fragment fragmentWorker;
	private Fragment fragmentProject;
	private View rootView = null;
	private View activeView = null;
	private Member member = null;
	private int memberID = 0;
	private final String MEMBERID = "MEMBERID";
	private CalendarDateDetailFragment endDateView;
	private CalendarDateDetailFragment startDateView;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public MemberDetailFragment()
	{
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
		{
			member = Member.getMember(savedInstanceState.getInt(MEMBERID));
			ARG_VIEW = savedInstanceState.getString("ARG_VIEW");
		}
		else
		{
			if(getArguments() != null)
			{
				if (getArguments().containsKey(ARG_VIEW_DETAIL))
				{
				Transactions.addSpecialCommand(new Command(MemberDetailFragment.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					member = Member.getMember(getArguments().getInt(ARG_VIEW_DETAIL));
					if(member != null)
						memberID = member.ID();
					fragmentWorker = new WorkerDetailFragment();
					fragmentProject = new ProjectDetailFragment();
					ARG_VIEW = ARG_VIEW_DETAIL;
				}
				if(getArguments().containsKey(ARG_VIEW_EDIT))
				{
				Transactions.addSpecialCommand(new Command(MemberDetailFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					member = Member.getMember(getArguments().getInt(ARG_VIEW_EDIT));
					if(member != null)
						memberID = member.ID();
					ARG_VIEW = ARG_VIEW_EDIT;
				}
				if(getArguments().containsKey(ARG_VIEW_NEW))
				{
				Transactions.addSpecialCommand(new Command(MemberDetailFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					ARG_VIEW = ARG_VIEW_NEW;
				}
				endDateView = new CalendarDateDetailFragment();
				startDateView = new CalendarDateDetailFragment();
			}
		}
		fragment = this;
		Member.getAccess().setChangeListener(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(member != null)
		{
			outState.putInt(MEMBERID, member.ID());
			outState.putString("ARG_VIEW", ARG_VIEW);
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
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		if(getArguments() == null || !getArguments().containsKey(ARG_VIEW_DETAIL))
		{
			if (!(activity instanceof Callbacks))
			{
				throw new IllegalStateException("Activity must implement fragment's callbacks.");
			}
			mCallbacks = (Callbacks) activity;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		if(container == null && member == null)
		{
			return inflater.inflate(R.layout.default_blank_fragment, container, false);
		}
		if(getArguments() != null)
		{
			if (getArguments().containsKey(ARG_VIEW_DETAIL))
			{
				if (!getArguments().containsKey("IsChildFragment"))
					rootView = inflater.inflate(R.layout.member_view_detail, container, false);
				setViewDetailData();
			}
			if(getArguments().containsKey(ARG_VIEW_NEW) || getArguments().containsKey(ARG_VIEW_EDIT))
			{
				if (!getArguments().containsKey("IsChildFragment"))
					rootView = inflater.inflate(R.layout.member_view_insertupdate, container, false);
				setViewNewOrEditData();
				if (!getArguments().containsKey("IsChildFragment"))
				{
					rootView.findViewById(R.id.okButton).setOnClickListener(ClickListener);
					rootView.findViewById(R.id.cancelButton).setOnClickListener(ClickListener);
					if(getArguments().containsKey(ARG_VIEW_NEW))
					{
						((TextView) rootView.findViewById(R.id.okButton)).setText("Create");
					}
					else
					{
						((TextView) rootView.findViewById(R.id.okButton)).setText("Update");
					}
				}
			}
		}
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		endDateView = new CalendarDateDetailFragment();
		startDateView = new CalendarDateDetailFragment();
		if(getArguments().containsKey(ARG_VIEW_DETAIL))
		{
			if(member != null && member.members() != null)
				UtilNavigate.addFragment(fragment, fragmentWorker, R.id.worker_detail_container, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, member.members().ID()));
			else
				UtilNavigate.addFragment(fragment, fragmentWorker, R.id.worker_detail_container, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, 0));
			if(member != null && member.projects() != null)
				UtilNavigate.addFragment(fragment, fragmentProject, R.id.project_detail_container, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, member.projects().ID()));
			else
				UtilNavigate.addFragment(fragment, fragmentProject, R.id.project_detail_container, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, 0));
			if(member != null && member.endDate() != null)
				UtilNavigate.replaceFragment(fragment, endDateView, R.id.calendardate_detail_container_enddate, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, member.endDate().ID(), true, R.id.calendardate_detail_container_enddate));
			else
				UtilNavigate.replaceFragment(fragment, endDateView, R.id.calendardate_detail_container_enddate, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, 0, true, R.id.calendardate_detail_container_enddate));
			if(member != null && member.startDate() != null)
				UtilNavigate.replaceFragment(fragment, startDateView, R.id.calendardate_detail_container_startdate, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, member.startDate().ID(), true, R.id.calendardate_detail_container_startdate));
			else
				UtilNavigate.replaceFragment(fragment, startDateView, R.id.calendardate_detail_container_startdate, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, 0, true, R.id.calendardate_detail_container_startdate));
		}
		if(getArguments().containsKey(ARG_VIEW_EDIT))
		{
			if(member != null && member.endDate() != null)
				UtilNavigate.replaceFragment(fragment, endDateView, R.id.calendardate_detail_container_enddate, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_EDIT, member.endDate().ID(), true, R.id.calendardate_detail_container_enddate));
			else
				UtilNavigate.replaceFragment(fragment, endDateView, R.id.calendardate_detail_container_enddate, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_EDIT, 0, true, R.id.calendardate_detail_container_enddate));
			if(member != null && member.startDate() != null)
				UtilNavigate.replaceFragment(fragment, startDateView, R.id.calendardate_detail_container_startdate, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_EDIT, member.startDate().ID(), true, R.id.calendardate_detail_container_startdate));
			else
				UtilNavigate.replaceFragment(fragment, startDateView, R.id.calendardate_detail_container_startdate, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_EDIT, 0, true, R.id.calendardate_detail_container_startdate));
		}
		if(getArguments().containsKey(ARG_VIEW_NEW))
		{
			UtilNavigate.replaceFragment(fragment, endDateView, R.id.calendardate_detail_container_enddate, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_NEW, 0, true, R.id.calendardate_detail_container_enddate));
			UtilNavigate.replaceFragment(fragment, startDateView, R.id.calendardate_detail_container_startdate, UtilNavigate.setFragmentBundleArguments(ARG_VIEW_NEW, 0, true, R.id.calendardate_detail_container_startdate));
		}
	}
	
	public void replaceObject(final String ARG_VIEW, final Member newMember)
	{
		 getActivity().runOnUiThread(new Runnable() {
			public void run() {
				member = newMember;
				memberID = newMember.ID();
				if(ARG_VIEW.equals(ARG_VIEW_DETAIL))
				{
					fragment.onSaveInstanceState(UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, newMember.ID()));
					setViewDetailData();
					endDateView.replaceObject(ARG_VIEW, member.endDate());
					startDateView.replaceObject(ARG_VIEW, member.startDate());
				}
				if(ARG_VIEW.equals(ARG_VIEW_EDIT))
				{
					fragment.onSaveInstanceState(UtilNavigate.setFragmentBundleArguments(ARG_VIEW_EDIT, newMember.ID()));
					setViewNewOrEditData();
					endDateView.replaceObject(ARG_VIEW, member.endDate());
					startDateView.replaceObject(ARG_VIEW, member.startDate());
				}
				if(ARG_VIEW.equals(ARG_VIEW_NEW))
				{
					fragment.onSaveInstanceState(UtilNavigate.setFragmentBundleArguments(ARG_VIEW_NEW, 0));
				}
			}
		});
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
	
	private void confirmActiveView()
	{
		activeView = rootView;
		if(getArguments() != null && getArguments().containsKey("IsChildFragment"))
			activeView = getParentFragment().getView().findViewById(getArguments().getInt("container"));
	}
	
	public void setInput()
	{
	}
	
	public View setViewDetailData()
	{
		confirmActiveView();
		if (member != null)
		{
		}
		return rootView;
	}
	
	public void ActionViewDetail()
	{
		confirmActiveView();
		if(member != null)
		{
			
		}
	}
	
	public View setViewNewOrEditData()
	{
		confirmActiveView();
		if (member != null)
		{
			setInput();
		}
		return rootView;
	}
	
	public Member ActionViewNew()
	{
		confirmActiveView();
		setInput();
		CalendarDate temp_endDate = endDateView.ActionViewNew();
		
		CalendarDate temp_startDate = startDateView.ActionViewNew();
		
		return new Member(null, null, temp_endDate, temp_startDate);
	}
	
	public Member ActionViewEdit()
	{
		if (member != null)
		{
			confirmActiveView();
			endDateView.ActionViewEdit().setID();
			startDateView.ActionViewEdit().setID();
			return member;
		}
		else
		{
			return null;
		}
	}
	
	public interface Callbacks
	{
		public void onDetailOK(boolean isNew, Member newMember);
		public void onDetailCancel();
	}
	
	private Callbacks mCallbacks = new Callbacks()
	{
		public void onDetailOK(boolean isNew, Member newMember) {	}
		public void onDetailCancel() {	}
	};
	
	OnClickListener ClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(v.getId() == R.id.okButton)
			{
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				if(getActivity().getCurrentFocus() != null)
					inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				if(getArguments().containsKey(ARG_VIEW_NEW))
				{
					member = ActionViewNew();
					if(member != null)
					{
						mCallbacks.onDetailOK(true, member);
					}
				}
				if(getArguments().containsKey(ARG_VIEW_EDIT))
				{
					if(ActionViewEdit() != null)
						mCallbacks.onDetailOK(false, member);
				}
			}
			if(v.getId() == R.id.cancelButton)
			{
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				if(getActivity().getCurrentFocus() != null)
					inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				mCallbacks.onDetailCancel();
			}
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
						//no need to change anything
					break;
					case INSERT_ASSOCIATION:
						if(event.getNewNeibor() != null)
						{
							if(event.getNewNeibor().getClass() == Worker.class)
								if(fragmentWorker != null)
									((WorkerDetailFragment) fragmentWorker).replaceObject(ARG_VIEW, (Worker) event.getNewNeibor());
							if(event.getNewNeibor().getClass() == Project.class)
								if(fragmentProject != null)
									((ProjectDetailFragment) fragmentProject).replaceObject(ARG_VIEW, (Project) event.getNewNeibor());
						}
					break;
					case UPDATE:
						if(memberID == event.getOldObjectID())
							member = (Member) event.getNewObject();
							memberID = member.ID();
							if (getArguments().containsKey(ARG_VIEW_DETAIL))
								setViewDetailData();
							else if(getArguments().containsKey(ARG_VIEW_EDIT))
								setViewNewOrEditData();
					break;
					case DELETE:
						if(memberID == event.getOldObjectID())
						{
							FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
							ft.remove(fragment);
							ft.commit();
						}
					break;
					case DELETE_ASSOCIATION:
						if(event.getNewNeibor() != null)
						{
							if(event.getNewNeibor().getClass() == Worker.class)
								if(fragmentWorker != null)
									((WorkerDetailFragment) fragmentWorker).replaceObject(ARG_VIEW, null);
							if(event.getNewNeibor().getClass() == Project.class)
								if(fragmentProject != null)
									((ProjectDetailFragment) fragmentProject).replaceObject(ARG_VIEW, null);
						}
					break;
					default:
					break;
				}
			}
		});
	}
	
}
