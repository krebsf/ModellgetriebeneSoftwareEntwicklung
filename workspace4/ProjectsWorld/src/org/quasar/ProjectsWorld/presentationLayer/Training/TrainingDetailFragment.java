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
import org.quasar.ProjectsWorld.utils.UtilNavigate;
import org.quasar.ProjectsWorld.utils.DetailFragment;
import org.quasar.ProjectsWorld.utils.PropertyChangeEvent;
import org.quasar.ProjectsWorld.utils.PropertyChangeListener;
import org.quasar.ProjectsWorld.businessLayer.Training;

import android.widget.EditText;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.app.Activity;
import org.quasar.ProjectsWorld.businessLayer.ProjectStatus;
import android.view.ViewGroup;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import org.quasar.ProjectsWorld.businessLayer.ProjectSize;
import android.view.View;
import android.content.Context;

public class TrainingDetailFragment extends Fragment implements PropertyChangeListener, DetailFragment{
	public static final String ARG_VIEW_DETAIL = "detail";
	public static final String ARG_VIEW_NEW = "new";
	public static final String ARG_VIEW_EDIT = "edit";
	public String ARG_VIEW = "";
	private Fragment fragment;
	private View rootView = null;
	private View activeView = null;
	private Training training = null;
	private int trainingID = 0;
	private final String TRAININGID = "TRAININGID";
	private EditText nameView;
	private EditText monthsView;
	private Spinner sizeView;
	private Spinner statusView;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public TrainingDetailFragment()
	{
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
		{
			training = Training.getTraining(savedInstanceState.getInt(TRAININGID));
			ARG_VIEW = savedInstanceState.getString("ARG_VIEW");
		}
		else
		{
			if(getArguments() != null)
			{
				if (getArguments().containsKey(ARG_VIEW_DETAIL))
				{
				Transactions.addSpecialCommand(new Command(TrainingDetailFragment.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					training = Training.getTraining(getArguments().getInt(ARG_VIEW_DETAIL));
					if(training != null)
						trainingID = training.ID();
					ARG_VIEW = ARG_VIEW_DETAIL;
				}
				if(getArguments().containsKey(ARG_VIEW_EDIT))
				{
				Transactions.addSpecialCommand(new Command(TrainingDetailFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					training = Training.getTraining(getArguments().getInt(ARG_VIEW_EDIT));
					if(training != null)
						trainingID = training.ID();
					ARG_VIEW = ARG_VIEW_EDIT;
				}
				if(getArguments().containsKey(ARG_VIEW_NEW))
				{
				Transactions.addSpecialCommand(new Command(TrainingDetailFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					ARG_VIEW = ARG_VIEW_NEW;
				}
			}
		}
		fragment = this;
		Training.getAccess().setChangeListener(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(training != null)
		{
			outState.putInt(TRAININGID, training.ID());
			outState.putString("ARG_VIEW", ARG_VIEW);
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
		if(container == null && training == null)
		{
			return inflater.inflate(R.layout.default_blank_fragment, container, false);
		}
		if(getArguments() != null)
		{
			if (getArguments().containsKey(ARG_VIEW_DETAIL))
			{
				if (!getArguments().containsKey("IsChildFragment"))
					rootView = inflater.inflate(R.layout.training_view_detail, container, false);
				setViewDetailData();
			}
			if(getArguments().containsKey(ARG_VIEW_NEW) || getArguments().containsKey(ARG_VIEW_EDIT))
			{
				if (!getArguments().containsKey("IsChildFragment"))
					rootView = inflater.inflate(R.layout.training_view_insertupdate, container, false);
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
	
	public void replaceObject(final String ARG_VIEW, final Training newTraining)
	{
		 getActivity().runOnUiThread(new Runnable() {
			public void run() {
				training = newTraining;
				trainingID = newTraining.ID();
				if(ARG_VIEW.equals(ARG_VIEW_DETAIL))
				{
					fragment.onSaveInstanceState(UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, newTraining.ID()));
					setViewDetailData();
				}
				if(ARG_VIEW.equals(ARG_VIEW_EDIT))
				{
					fragment.onSaveInstanceState(UtilNavigate.setFragmentBundleArguments(ARG_VIEW_EDIT, newTraining.ID()));
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
		nameView = (EditText) activeView.findViewById(R.id.project_insertupdate_name_value);
		monthsView = (EditText) activeView.findViewById(R.id.project_insertupdate_months_value);
		sizeView = (Spinner) activeView.findViewById(R.id.project_insertupdate_size_value);
		statusView = (Spinner) activeView.findViewById(R.id.project_insertupdate_status_value);
	}
	
	public View setViewDetailData()
	{
		confirmActiveView();
		if (training != null)
		{
			((TextView) activeView.findViewById(R.id.project_detail_name_value)).setText("" + training.name());
			((TextView) activeView.findViewById(R.id.project_detail_size_value)).setText("" + training.size());
			((TextView) activeView.findViewById(R.id.project_detail_status_value)).setText("" + training.status());
			((TextView) activeView.findViewById(R.id.project_detail_months_value)).setText("" + training.months());
		}
		return rootView;
	}
	
	public void ActionViewDetail()
	{
		confirmActiveView();
		if(training != null)
		{
			
		}
	}
	
	public View setViewNewOrEditData()
	{
		confirmActiveView();
		if (training != null)
		{
			setInput();
			nameView.setText("" + training.name());
			monthsView.setText("" + training.months());
			sizeView.setSelection(ProjectSize.valueOf(training.size().toString()).ordinal());
			statusView.setSelection(ProjectStatus.valueOf(training.status().toString()).ordinal());
		}
		return rootView;
	}
	
	public Training ActionViewNew()
	{
		confirmActiveView();
		setInput();
		int temp_months = 0;

		try{
			temp_months = Integer.parseInt(monthsView.getText().toString());
		}catch(NumberFormatException e){
			UtilNavigate.showWarning(getActivity(), "Number Format Error", "Error in input:\n" + monthsView.getText().toString());
			return null;
		}
		
		String temp_name = nameView.getText().toString();

		if (temp_name.equals("")){
			UtilNavigate.showWarning(getActivity(), "Text input error", "Error in input of name:\nThe input must have some text");
			return null;
		}
		
		ProjectSize temp_size = ProjectSize.values()[sizeView.getSelectedItemPosition()];

		
		
		ProjectStatus temp_status = ProjectStatus.values()[statusView.getSelectedItemPosition()];

		
		
		return new Training(temp_months, temp_name, temp_size, temp_status);
	}
	
	public Training ActionViewEdit()
	{
		if (training != null)
		{
			confirmActiveView();
			String temp_name = nameView.getText().toString();

			if (!training.name().equals(nameView.getText().toString()))
				training.setName(temp_name);
			int temp_months = 0;

			if (training.months() != temp_months)
				training.setMonths(temp_months);
			ProjectSize temp_size = ProjectSize.values()[sizeView.getSelectedItemPosition()];

			if (training.size() != temp_size)
				training.setSize(temp_size);
			ProjectStatus temp_status = ProjectStatus.values()[statusView.getSelectedItemPosition()];

			if (training.status() != temp_status)
				training.setStatus(temp_status);
			return training;
		}
		else
		{
			return null;
		}
	}
	
	public interface Callbacks
	{
		public void onDetailOK(boolean isNew, Training newTraining);
		public void onDetailCancel();
	}
	
	private Callbacks mCallbacks = new Callbacks()
	{
		public void onDetailOK(boolean isNew, Training newTraining) {	}
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
					training = ActionViewNew();
					if(training != null)
					{
						mCallbacks.onDetailOK(true, training);
					}
				}
				if(getArguments().containsKey(ARG_VIEW_EDIT))
				{
					if(ActionViewEdit() != null)
						mCallbacks.onDetailOK(false, training);
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
						if(trainingID == event.getOldObjectID())
							training = (Training) event.getNewObject();
							trainingID = training.ID();
							if (getArguments().containsKey(ARG_VIEW_DETAIL))
								setViewDetailData();
							else if(getArguments().containsKey(ARG_VIEW_EDIT))
								setViewNewOrEditData();
					break;
					case DELETE:
						if(trainingID == event.getOldObjectID())
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
