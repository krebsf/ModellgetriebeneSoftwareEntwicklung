package org.quasar.IceCream2.utils;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InheritanceListFragment extends DialogFragment {
	private int contentSource;
	
	public InheritanceListFragment() {    }

	
    public static InheritanceListFragment newInstance(int contentSource) {
    	InheritanceListFragment f = new InheritanceListFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("contentSource", contentSource);
        f.setArguments(args);
        
        return f;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentSource = getArguments().getInt("contentSource");
        setStyle(STYLE_NO_TITLE, getTheme());
    }

    @Override
    public void onDestroy(){
    	super.onDestroy();
//    	getFragmentManager().beginTransaction().remove(this).commit();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(contentSource, container, false);
        return v;
    }

}
