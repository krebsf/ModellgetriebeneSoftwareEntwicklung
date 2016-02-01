/**********************************************************************
* Filename: IceCream.java
* Created: 2016/01/31
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream3;

import java.util.ArrayList;

import org.quasar.IceCream3.persistenceLayer.Database;
import org.quasar.IceCream3.utils.ServerActions;
import org.quasar.IceCream3.utils.UtilNavigate;

import org.quasar.IceCream3.presentationLayer.Address. AddressActivity;
import org.quasar.IceCream3.presentationLayer.Entry. EntryActivity;
import org.quasar.IceCream3.presentationLayer.Station. StationActivity;

import android.view.Menu;
import android.os.Bundle;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.widget.GridView;
import android.view.View;
import android.view.MenuItem;
import android.app.Activity;
import android.widget.AdapterView;

public class IceCreamLauncher extends Activity
{

	private LauncherGridViewAdapter mAdapter;
	private ArrayList<String> listObjects;
	private ArrayList<Integer> listImages;
	private GridView gridView;
	private IceCreamMemory icecreammemory;
	
	public void inicializer()
	{
		if(icecreammemory.db.getContext() == null)
			icecreammemory.setAppContextToDB();
		icecreammemory.db.OpenDB();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.icecream_launcher_activity);
		icecreammemory = (IceCreamMemory)getApplication();
		
		inicializer();
		if(savedInstanceState == null)
		{
			inicializer();
			if(getIntent().getExtras() != null)
				UtilNavigate.showWarning(this, getIntent().getExtras().getString("TITLE"), getIntent().getExtras().getString("MESSAGE"));
		}
		
		prepareList();
		
		mAdapter = new LauncherGridViewAdapter(this,listObjects, listImages);
		gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setAdapter(mAdapter);
		
		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				if(mAdapter.getItem(position).equals("Address"))
				{
					Intent intent = new Intent(IceCreamLauncher.this, AddressActivity.class);
					intent.setAction("READ");
					startActivity(intent);
				}
				if(mAdapter.getItem(position).equals("Entry"))
				{
					Intent intent = new Intent(IceCreamLauncher.this, EntryActivity.class);
					intent.setAction("READ");
					startActivity(intent);
				}
				if(mAdapter.getItem(position).equals("Station"))
				{
					Intent intent = new Intent(IceCreamLauncher.this, StationActivity.class);
					intent.setAction("READ");
					startActivity(intent);
				}
			}
		});
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
	public void onDestroy()
	{
		super.onDestroy();
		Database.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_launcher, menu);
		return true;
	}
	
	Activity activity = this;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.upload:
				ServerActions.sendChanges(this);
				break;
			case R.id.download:
				ServerActions.getChanges(this);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void prepareList()
	{
		listObjects = new ArrayList<String>();
		listImages = new ArrayList<Integer>();
		listObjects.add("Address");
		listImages.add(0);
		listObjects.add("Entry");
		listImages.add(0);
		listObjects.add("Station");
		listImages.add(0);
	}
}
