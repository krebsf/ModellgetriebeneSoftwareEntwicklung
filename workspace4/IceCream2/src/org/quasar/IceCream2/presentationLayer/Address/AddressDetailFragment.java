/**********************************************************************
* Filename: Address.java
* Created: 2016/01/31
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream2.presentationLayer.Address;

import org.quasar.IceCream2.R;
import org.quasar.IceCream2.utils.Transactions;
import org.quasar.IceCream2.utils.Command;
import org.quasar.IceCream2.utils.CommandType;
import org.quasar.IceCream2.utils.CommandTargetLayer;
import org.quasar.IceCream2.utils.UtilNavigate;
import org.quasar.IceCream2.utils.DetailFragment;
import org.quasar.IceCream2.utils.PropertyChangeEvent;
import org.quasar.IceCream2.utils.PropertyChangeListener;
import org.quasar.IceCream2.businessLayer.Address;

import android.view.ViewGroup;
import android.widget.EditText;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.support.v4.app.Fragment;
import android.app.Activity;

public class AddressDetailFragment extends Fragment implements PropertyChangeListener, DetailFragment{
	public static final String ARG_VIEW_DETAIL = "detail";
	public static final String ARG_VIEW_NEW = "new";
	public static final String ARG_VIEW_EDIT = "edit";
	public String ARG_VIEW = "";
	private Fragment fragment;
	private View rootView = null;
	private View activeView = null;
	private Address address = null;
	private int addressID = 0;
	private final String ADDRESSID = "ADDRESSID";
	private EditText streetView;
	private EditText postCodeView;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public AddressDetailFragment()
	{
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
		{
			address = Address.getAddress(savedInstanceState.getInt(ADDRESSID));
			ARG_VIEW = savedInstanceState.getString("ARG_VIEW");
		}
		else
		{
			if(getArguments() != null)
			{
				if (getArguments().containsKey(ARG_VIEW_DETAIL))
				{
				Transactions.addSpecialCommand(new Command(AddressDetailFragment.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					address = Address.getAddress(getArguments().getInt(ARG_VIEW_DETAIL));
					if(address != null)
						addressID = address.ID();
					ARG_VIEW = ARG_VIEW_DETAIL;
				}
				if(getArguments().containsKey(ARG_VIEW_EDIT))
				{
				Transactions.addSpecialCommand(new Command(AddressDetailFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					address = Address.getAddress(getArguments().getInt(ARG_VIEW_EDIT));
					if(address != null)
						addressID = address.ID();
					ARG_VIEW = ARG_VIEW_EDIT;
				}
				if(getArguments().containsKey(ARG_VIEW_NEW))
				{
				Transactions.addSpecialCommand(new Command(AddressDetailFragment.class, CommandType.WRITE, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					ARG_VIEW = ARG_VIEW_NEW;
				}
			}
		}
		fragment = this;
		Address.getAccess().setChangeListener(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(address != null)
		{
			outState.putInt(ADDRESSID, address.ID());
			outState.putString("ARG_VIEW", ARG_VIEW);
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Address.getAccess().removeChangeListener(this);
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
		if(container == null && address == null)
		{
			return inflater.inflate(R.layout.default_blank_fragment, container, false);
		}
		if(getArguments() != null)
		{
			if (getArguments().containsKey(ARG_VIEW_DETAIL))
			{
				if (!getArguments().containsKey("IsChildFragment"))
					rootView = inflater.inflate(R.layout.address_view_detail, container, false);
				setViewDetailData();
			}
			if(getArguments().containsKey(ARG_VIEW_NEW) || getArguments().containsKey(ARG_VIEW_EDIT))
			{
				if (!getArguments().containsKey("IsChildFragment"))
					rootView = inflater.inflate(R.layout.address_view_insertupdate, container, false);
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
	
	public void replaceObject(final String ARG_VIEW, final Address newAddress)
	{
		 getActivity().runOnUiThread(new Runnable() {
			public void run() {
				address = newAddress;
				addressID = newAddress.ID();
				if(ARG_VIEW.equals(ARG_VIEW_DETAIL))
				{
					fragment.onSaveInstanceState(UtilNavigate.setFragmentBundleArguments(ARG_VIEW_DETAIL, newAddress.ID()));
					setViewDetailData();
				}
				if(ARG_VIEW.equals(ARG_VIEW_EDIT))
				{
					fragment.onSaveInstanceState(UtilNavigate.setFragmentBundleArguments(ARG_VIEW_EDIT, newAddress.ID()));
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
		streetView = (EditText) activeView.findViewById(R.id.address_insertupdate_street_value);
		postCodeView = (EditText) activeView.findViewById(R.id.address_insertupdate_postcode_value);
	}
	
	public View setViewDetailData()
	{
		confirmActiveView();
		if (address != null)
		{
			((TextView) activeView.findViewById(R.id.address_detail_street_value)).setText("" + address.street());
			((TextView) activeView.findViewById(R.id.address_detail_postcode_value)).setText("" + address.postCode());
		}
		return rootView;
	}
	
	public void ActionViewDetail()
	{
		confirmActiveView();
		if(address != null)
		{
			
		}
	}
	
	public View setViewNewOrEditData()
	{
		confirmActiveView();
		if (address != null)
		{
			setInput();
			streetView.setText("" + address.street());
			postCodeView.setText("" + address.postCode());
		}
		return rootView;
	}
	
	public Address ActionViewNew()
	{
		confirmActiveView();
		setInput();
		int temp_postCode = 0;

		try{
			temp_postCode = Integer.parseInt(postCodeView.getText().toString());
		}catch(NumberFormatException e){
			UtilNavigate.showWarning(getActivity(), "Number Format Error", "Error in input:\n" + postCodeView.getText().toString());
			return null;
		}
		
		String temp_street = streetView.getText().toString();

		if (temp_street.equals("")){
			UtilNavigate.showWarning(getActivity(), "Text input error", "Error in input of street:\nThe input must have some text");
			return null;
		}
		
		return new Address(temp_postCode, temp_street);
	}
	
	public Address ActionViewEdit()
	{
		if (address != null)
		{
			confirmActiveView();
			String temp_street = streetView.getText().toString();

			if (!address.street().equals(streetView.getText().toString()))
				address.setStreet(temp_street);
			int temp_postCode = 0;

			if (address.postCode() != temp_postCode)
				address.setPostCode(temp_postCode);
			return address;
		}
		else
		{
			return null;
		}
	}
	
	public interface Callbacks
	{
		public void onDetailOK(boolean isNew, Address newAddress);
		public void onDetailCancel();
	}
	
	private Callbacks mCallbacks = new Callbacks()
	{
		public void onDetailOK(boolean isNew, Address newAddress) {	}
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
					address = ActionViewNew();
					if(address != null)
					{
						mCallbacks.onDetailOK(true, address);
					}
				}
				if(getArguments().containsKey(ARG_VIEW_EDIT))
				{
					if(ActionViewEdit() != null)
						mCallbacks.onDetailOK(false, address);
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
						if(addressID == event.getOldObjectID())
							address = (Address) event.getNewObject();
							addressID = address.ID();
							if (getArguments().containsKey(ARG_VIEW_DETAIL))
								setViewDetailData();
							else if(getArguments().containsKey(ARG_VIEW_EDIT))
								setViewNewOrEditData();
					break;
					case DELETE:
						if(addressID == event.getOldObjectID())
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
