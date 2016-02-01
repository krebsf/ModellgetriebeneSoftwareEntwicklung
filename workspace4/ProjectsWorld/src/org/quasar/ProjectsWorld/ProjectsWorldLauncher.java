/**********************************************************************
* Filename: ProjectsWorld.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld;

import java.util.ArrayList;

import org.quasar.ProjectsWorld.persistenceLayer.Database;
import org.quasar.ProjectsWorld.utils.ServerActions;
import org.quasar.ProjectsWorld.utils.UtilNavigate;

import org.quasar.ProjectsWorld.presentationLayer.Company. CompanyActivity;
import org.quasar.ProjectsWorld.presentationLayer.Member. MemberActivity;
import org.quasar.ProjectsWorld.presentationLayer.Project. ProjectActivity;
import org.quasar.ProjectsWorld.presentationLayer.Qualification. QualificationActivity;
import org.quasar.ProjectsWorld.presentationLayer.Training. TrainingActivity;
import org.quasar.ProjectsWorld.presentationLayer.Worker. WorkerActivity;

import android.view.Menu;
import android.os.Bundle;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.widget.GridView;
import android.view.View;
import android.view.MenuItem;
import android.app.Activity;
import android.widget.AdapterView;

public class ProjectsWorldLauncher extends Activity
{

	private LauncherGridViewAdapter mAdapter;
	private ArrayList<String> listObjects;
	private ArrayList<Integer> listImages;
	private GridView gridView;
	private ProjectsWorldMemory projectsworldmemory;
	
	public void inicializer()
	{
		if(projectsworldmemory.db.getContext() == null)
			projectsworldmemory.setAppContextToDB();
		projectsworldmemory.db.OpenDB();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.projectsworld_launcher_activity);
		projectsworldmemory = (ProjectsWorldMemory)getApplication();
		
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
				if(mAdapter.getItem(position).equals("Companies"))
				{
					Intent intent = new Intent(ProjectsWorldLauncher.this, CompanyActivity.class);
					intent.setAction("READ");
					startActivity(intent);
				}
				if(mAdapter.getItem(position).equals("Member"))
				{
					Intent intent = new Intent(ProjectsWorldLauncher.this, MemberActivity.class);
					intent.setAction("READ");
					startActivity(intent);
				}
				if(mAdapter.getItem(position).equals("Projects"))
				{
					Intent intent = new Intent(ProjectsWorldLauncher.this, ProjectActivity.class);
					intent.setAction("READ");
					startActivity(intent);
				}
				if(mAdapter.getItem(position).equals("Qualifications"))
				{
					Intent intent = new Intent(ProjectsWorldLauncher.this, QualificationActivity.class);
					intent.setAction("READ");
					startActivity(intent);
				}
				if(mAdapter.getItem(position).equals("Training Courses"))
				{
					Intent intent = new Intent(ProjectsWorldLauncher.this, TrainingActivity.class);
					intent.setAction("READ");
					startActivity(intent);
				}
				if(mAdapter.getItem(position).equals("Workers"))
				{
					Intent intent = new Intent(ProjectsWorldLauncher.this, WorkerActivity.class);
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
		ProjectsWorldMemory.setActiveActivity(this);
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
		listObjects.add("Companies");
		listImages.add(R.drawable.ic_launcher);
		listObjects.add("Member");
		listImages.add(0);
		listObjects.add("Projects");
		listImages.add(R.drawable.ic_launcher);
		listObjects.add("Qualifications");
		listImages.add(R.drawable.ic_launcher);
		listObjects.add("Training Courses");
		listImages.add(R.drawable.ic_launcher);
		listObjects.add("Workers");
		listImages.add(R.drawable.ic_launcher);
	}
}
