/**********************************************************************
* Filename: MasterActivity.java
* Created: 2016/01/31
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream2;

import org.quasar.IceCream2.R;
import org.quasar.IceCream2.IceCreamLauncher;
import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

public class MasterActivity extends FragmentActivity
{
	public static final String ACTION_MODE_READ = "READ";
	public static final String ACTION_MODE_WRITE = "WRITE";
	public static final int ToMANY = -1;
	public static final int ToONE = 1;
	public boolean ACTION_MODE_WRITE_STARTER = false;
	public boolean ONCREATION = false;
	public static final int CREATION_CODE = 0;
	protected boolean showingDetail = false;
	protected final String SHOWINGDetail = "ShowingDetail";
	private ImageView menuOnCreationImage;
	private AnimationDrawable menuOnCreationAnimation;
	protected IceCreamMemory icecreammemory;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		icecreammemory = (IceCreamMemory)getApplication();
		
		Intent intent = getIntent();
		if (intent != null)
			if (intent.getAction().equals(ACTION_MODE_WRITE))
			{
				ONCREATION = true;
				getActionBar().setIcon(R.drawable.ic_android_mode_write);
			}
			else
				getActionBar().setIcon(R.drawable.ic_android_mode_read);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		IceCreamMemory.setActiveActivity(this);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
	
	public boolean isOnCreation()
	{
		return ONCREATION;
	}
	
	public void setOnCreation(boolean onCreation)
	{
		this.ONCREATION = onCreation;
		invalidateOptionsMenu();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		MenuInflater menuInflater = getMenuInflater();
		if(menu.size() == 0)
		{
			if(ONCREATION)
				menuInflater.inflate(R.menu.menu_write, menu);
			else
				menuInflater.inflate(R.menu.menu_read, menu);
			}
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_home:
				Intent upIntent = new Intent(this, IceCreamLauncher.class);
				upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(upIntent);
				finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}

