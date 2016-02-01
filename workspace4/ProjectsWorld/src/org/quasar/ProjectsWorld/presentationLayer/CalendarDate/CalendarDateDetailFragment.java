/**********************************************************************
* Filename: CalendarDate.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.presentationLayer.CalendarDate;

import org.quasar.ProjectsWorld.R;
import org.quasar.ProjectsWorld.utils.Transactions;
import org.quasar.ProjectsWorld.utils.Command;
import org.quasar.ProjectsWorld.utils.CommandType;
import org.quasar.ProjectsWorld.utils.CommandTargetLayer;
import org.quasar.ProjectsWorld.utils.UtilNavigate;
import org.quasar.ProjectsWorld.utils.DetailFragment;
import org.quasar.ProjectsWorld.utils.PropertyChangeEvent;
import org.quasar.ProjectsWorld.utils.PropertyChangeListener;
import org.quasar.ProjectsWorld.businessLayer.CalendarDate;
import org.quasar.ProjectsWorld.presentationLayer.Member.MemberActivity;
import org.quasar.ProjectsWorld.presentationLayer.Member.MemberActivity;

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

public class CalendarDateDetailFragment extends Fragment implements PropertyChangeListener, DetailFragment{
	public static final String ARG_VIEW_DETAIL = "detail";
	public static final String ARG_VIEW_NEW = "new";
	public static final String ARG_VIEW_EDIT = "edit";
	public String ARG_VIEW = "";
	private Fragment fragment;
	private View rootView = null;
	private View activeView = null;
	private CalendarDate calendardate = null;
	private int calendardateID = 0;
	private final String CALENDARDATEID = "CALENDARDATEID";
	private DatePicker calendardateView;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public CalendarDateDetailFragment()
	{
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
		{
			calendardate = CalendarDate.getCalendarDate(savedInstanceState.getInt(CALENDARDATEID));
			ARG_VIEW = savedInstanceState.getString("ARG_VIEW");
		}
		else
		{
			if(getArguments() != null)
			{
				if (getArguments().containsKey(ARG_VIEW_DETAIL))
				{
				Transactions.addSpecialCommand(new Command(CalendarDateDetailFragment.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					calendardate = CalendarDate.getCalendarDate(getArguments().getInt(ARG_VIEW_DETAIL));
					if(calendardate != null)
						calendardateID = calendardate.ID();
					ARG_VIEW = ARG_VIEW_DETAIL;
				}
				if(getArguments().containsKey(ARG_VIEW_EDIT))
				{
				Transactions.addSpecialCommand(new Command(CalendarDateDetailFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					calendardate = CalendarDate.getCalendarDate(getArguments().getInt(ARG_VIEW_EDIT));
					if(calendardate != null)
						calendardateID = calendardate.ID();
					ARG_VIEW = ARG_VIEW_EDIT;
				}
				if(getArguments().containsKey(ARG_VIEW_NEW))
				{
				Transactions.addSpecialCommand(new Command(CalendarDateDetailFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					ARG_VIEW = ARG_VIEW_NEW;
				}
			}
		}
		fragment = this;
		CalendarDate.getAccess().setChangeListener(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(calendardate != null)
		{
			outState.putInt(CALENDARDATEID, calendardate.ID());
			outState.putString("ARG_VIEW", ARG_VIEW);
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		CalendarDate.getAccess().removeChangeListener(this);
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
			if ((activity.getClass() != MemberActivity.class && activity.getClass() != MemberActivity.class) && !(activity instanceof Callbacks))
			{
				throw new IllegalStateException("Activity must implement fragment's callbacks.");
			}
			if ((activity.getClass() != MemberActivity.class && activity.getClass() != MemberActivity.class))
				mCallbacks = (Callbacks) activity;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		if(container == null && calendardate == null)
		{
			return inflater.inflate(R.layout.default_blank_fragment, container, false);
		}
		if(getArguments() != null)
		{
			if (getArguments().containsKey(ARG_VIEW_DETAIL))
			{
				if (!getArguments().containsKey("IsChildFragment"))
					rootView = inflater.inflate(R.layout.calendardate_view_detail, container, false);
				setViewDetailData();
			}
			if(getArguments().containsKey(ARG_VIEW_NEW) || getArguments().containsKey(ARG_VIEW_EDIT))
			{
				if (!getArguments().containsKey("IsChildFragment"))
					rootView = inflater.inflate(R.layout.calendardate_view_insertupdate, container, false);
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
		if(getArguments().containsKey(ARG_VIEW_DETAIL))
		{
		}
		if(getArguments().containsKey(ARG_VIEW_EDIT))
		{
		}
		if(getArguments().containsKey(ARG_VIEW_NEW))
		{
		}
	}
	
	public void replaceObject(final String ARG_VIEW, final CalendarDate newCalendarDate)
	{
		 getActivity().runOnUiThread(new Runnable() {
			public void run() {
				calendardate = newCalendarDate;
				calendardateID = newCalendarDate.ID();
				if(ARG_VIEW.equals(ARG_VIEW_DETAIL))
				{
					fragment.onSaveInstanceState(UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, newCalendarDate.ID()));
					setViewDetailData();
				}
				if(ARG_VIEW.equals(ARG_VIEW_EDIT))
				{
					fragment.onSaveInstanceState(UtilNavigate.setFragmentBundleArguments(ARG_VIEW_EDIT, newCalendarDate.ID()));
					setViewNewOrEditData();
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
		calendardateView = (DatePicker) activeView.findViewById(R.id.calendardate_insertupdate_calendardate_value);
	}
	
	public View setViewDetailData()
	{
		confirmActiveView();
		calendardateView = (DatePicker) activeView.findViewById(R.id.calendardate_detail_calendardate_value);
		if (calendardate != null)
		{
			((DatePicker) activeView.findViewById(R.id.calendardate_detail_calendardate_value)).init(calendardate.year(),calendardate.month(),calendardate.day(), null);
		}
		return rootView;
	}
	
	public void ActionViewDetail()
	{
		confirmActiveView();
		if(calendardate != null)
		{
			
		}
	}
	
	public View setViewNewOrEditData()
	{
		confirmActiveView();
		if (calendardate != null)
		{
			setInput();
			calendardateView.init(calendardate.year(),calendardate.month(),calendardate.day(), null);
		}
		return rootView;
	}
	
	public CalendarDate ActionViewNew()
	{
		confirmActiveView();
		setInput();
		int temp_day = 0;

		temp_day = calendardateView.getDayOfMonth();
		//if (temp_day  something to check){
		//	UtilNavigate.showWarning(getActivity(), "Date input error", "Error in input of day:\n error specific message");
		//	return null;
		//}
		
		int temp_month = 0;

		temp_month = calendardateView.getMonth();
		//if (temp_month  something to check){
		//	UtilNavigate.showWarning(getActivity(), "Date input error", "Error in input of month:\n error specific message");
		//	return null;
		//}
		
		int temp_year = 0;

		temp_year = calendardateView.getYear();
		//if (temp_year  something to check){
		//	UtilNavigate.showWarning(getActivity(), "Date input error", "Error in input of year:\n error specific message");
		//	return null;
		//}
		
		return new CalendarDate(temp_day, temp_month, temp_year);
	}
	
	public CalendarDate ActionViewEdit()
	{
		if (calendardate != null)
		{
			confirmActiveView();
			int temp_year = 0;

			temp_year = calendardateView.getYear();
			if (calendardate.year() != temp_year)
				calendardate.setYear(temp_year);
			int temp_month = 0;

			temp_month = calendardateView.getMonth();
			if (calendardate.month() != temp_month)
				calendardate.setMonth(temp_month);
			int temp_day = 0;

			temp_day = calendardateView.getDayOfMonth();
			if (calendardate.day() != temp_day)
				calendardate.setDay(temp_day);
			return calendardate;
		}
		else
		{
			return null;
		}
	}
	
	public interface Callbacks
	{
		public void onDetailOK(boolean isNew, CalendarDate newCalendarDate);
		public void onDetailCancel();
	}
	
	private Callbacks mCallbacks = new Callbacks()
	{
		public void onDetailOK(boolean isNew, CalendarDate newCalendarDate) {	}
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
					calendardate = ActionViewNew();
					if(calendardate != null)
					{
						mCallbacks.onDetailOK(true, calendardate);
					}
				}
				if(getArguments().containsKey(ARG_VIEW_EDIT))
				{
					if(ActionViewEdit() != null)
						mCallbacks.onDetailOK(false, calendardate);
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
					break;
					case UPDATE:
						if(calendardateID == event.getOldObjectID())
							calendardate = (CalendarDate) event.getNewObject();
							calendardateID = calendardate.ID();
							if (getArguments().containsKey(ARG_VIEW_DETAIL))
								setViewDetailData();
							else if(getArguments().containsKey(ARG_VIEW_EDIT))
								setViewNewOrEditData();
					break;
					case DELETE:
						if(calendardateID == event.getOldObjectID())
						{
							FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
							ft.remove(fragment);
							ft.commit();
						}
					break;
					case DELETE_ASSOCIATION:
					break;
					default:
					break;
				}
			}
		});
	}
	
}
