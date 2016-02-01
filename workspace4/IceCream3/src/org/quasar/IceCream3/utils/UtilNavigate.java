package org.quasar.IceCream3.utils;

import java.io.Serializable;

import org.quasar.IceCream3.utils.InheritanceListFragment;
import org.quasar.IceCream3.utils.WarningDialogFragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class UtilNavigate {

	public static void toHome(Activity activity, Class<?> c, boolean hasMessage, String title, String message){
		Intent upIntent = new Intent(activity, c.getClass());
		upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if(hasMessage){
			Bundle bundle = new Bundle();
			bundle.putString("TITLE", title);
			bundle.putString("MESSAGE", message);
			upIntent.putExtras(bundle);
		}
		activity.startActivity(upIntent);
		activity.finish();
	}
	
	public static void toActivity(Activity activity, Class<?> destination, String AssociationSource, String action) {
		Intent intent = new Intent(activity, destination);
		intent.setAction(action);
		intent.setAction(AssociationSource);
		activity.startActivity(intent);
	}
	
	public static void toActivity(Activity activity, Class<?> destination, String action) {
		Intent intent = new Intent(activity, destination);
		intent.setAction(action);
		activity.startActivity(intent);
	}
	
	public static void toActivity(Activity activity, Class<?> destination, String action, Bundle bundle) {
		Intent intent = new Intent(activity, destination);
		intent.setAction(action);
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}

	public static void toActivityForResult(Activity activity, Class<?> destination, String action, int code) {
		Intent intent = new Intent(activity, destination);
		intent.setAction(action);
		activity.startActivityForResult(intent, code);
	}

	public static void toActivityForResult(Activity activity, Class<?> destination, String action, Bundle extras, int code) {
		Intent intent = new Intent(activity, destination);
		intent.setAction(action);
		intent.putExtras(extras);
		activity.startActivityForResult(intent, code);
	}

	public static void replaceFragment(FragmentActivity activity, Fragment fragment, int container, Bundle extras) {
		fragment.setArguments(extras);
		FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

		transaction.replace(container, fragment);
//		transaction.addToBackStack(null);
		transaction.commit();
		activity.getSupportFragmentManager().executePendingTransactions();
	}
	
	public static void replaceFragment(Fragment host, Fragment fragment, int container, Bundle extras) {
		fragment.setArguments(extras);
		FragmentTransaction transaction = host.getChildFragmentManager().beginTransaction();

		transaction.replace(container, fragment);
		transaction.commit();
		host.getChildFragmentManager().executePendingTransactions();
	}

	public static Bundle setFragmentBundleArguments(String objectKey, int id) {
		Bundle arguments = new Bundle();
		arguments.putInt(objectKey, id);
		return arguments;
	}

	public static Bundle setFragmentBundleArguments(String objectKey, int id, boolean isChildFragment) {
		Bundle arguments = new Bundle();
		arguments.putInt(objectKey, id);
		arguments.putBoolean("IsChildFragment", isChildFragment);
		return arguments;
	}
	
	public static Bundle setFragmentBundleArguments(String objectKey, int id, boolean isChildFragment, int container) {
		Bundle arguments = new Bundle();
		arguments.putInt(objectKey, id);
		arguments.putBoolean("IsChildFragment", isChildFragment);
		arguments.putInt("container", container);
		return arguments;
	}
	
	public static void addFragment(FragmentActivity activity, Fragment fragment, int container, Bundle extras) {
		fragment.setArguments(extras);
		activity.getSupportFragmentManager().beginTransaction().add(container, fragment).commit();
	}
	
	public static void addFragment(Fragment host, Fragment fragment, int container, Bundle extras) {
		fragment.setArguments(extras);
		host.getChildFragmentManager().beginTransaction().add(container, fragment).commit();
	}

	public static Bundle setBundles(Bundle... extras) {
		Bundle arguments = new Bundle();
		for(Bundle x : extras)
			arguments.putAll(x);
		return arguments;
	}

	public static Bundle setAssociationBundleArguments(String Key, String value) {
		Bundle arguments = new Bundle();
		arguments.putString(Key, value);
		return arguments;
	}
	
	public static Bundle setActivityBundleArguments(String objectKey, Object key, String associationKey, int associationQuant) {
		Bundle arguments = new Bundle();
		if (key instanceof Integer)
			arguments.putInt(objectKey, (Integer) key);
		if (key instanceof String)
			arguments.putString(objectKey, (String) key);
		arguments.putInt(associationKey, associationQuant);
		return arguments;
	}
	
	public static Bundle setActivityBundleArguments(String objectKey, Object object, String associationMultKey, int associationQuant, String associationNameKey, String associationName) {
		Bundle arguments = new Bundle();
		if (object instanceof Integer)
			arguments.putInt(objectKey, (Integer) object);
		if (object instanceof String)
			arguments.putString(objectKey, (String) object);
		arguments.putString(associationNameKey, associationName);
		arguments.putInt(associationMultKey, associationQuant);
		return arguments;
	}

	public static View showWarning(final Activity activity, String title, String message) {
		// Create and show the dialog.
		final DialogFragment newFragment = WarningDialogFragment.newInstance(title, message);
		activity.runOnUiThread(new Runnable() {
		    public void run() {
				newFragment.show(activity.getFragmentManager(), "dialog");
				activity.getFragmentManager().executePendingTransactions();	
		    }
		});
		return newFragment.getView();
	}

	public static View showInheritanceList(final Activity activity, int contentSource) {
		final DialogFragment newFragment = InheritanceListFragment.newInstance(contentSource);
		//newFragment.show(activity.getFragmentManager(), "dialog");
		//this next line is used to force the finalization of the fragment creation
		//example, if this line is not here the view returned will be null (the oncreateView is still not called)
		//this is because we will execute every piece of code until we pass to this step
		//since we want to set the listeners on the proper fragment in this case the navigation bar
		//will set the listeners for this dialog fragment
		//in the beginning this line was not needed because we had the fragment tag in the xml
		//therefor it was static therefore it was compiled at buildtime and not runtime
		//activity.getFragmentManager().executePendingTransactions();
		activity.runOnUiThread(new Runnable() {
		    public void run() {
				newFragment.show(activity.getFragmentManager(), "dialog");
				activity.getFragmentManager().executePendingTransactions();	
		    }
		});
		return newFragment.getView();
	}

//	private static FragmentTransaction prepareDialogFragment(Activity activity, Fragment frag) {
//		// DialogFragment.show() will take care of adding the fragment
//		// in a transaction. We also want to remove any currently showing
//		// dialog, so make our own transaction and take care of that here.
//		FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
//		Fragment prev = activity.getFragmentManager().findFragmentByTag("dialog");
//		if (prev != null)
//			ft.remove(prev);
//
//		ft.add(frag, "dialog");
//		ft.commit();
//		return ft;
//	}

}
