/**********************************************************************
* Filename: ProjectsWorld.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld;

import org.quasar.ProjectsWorld.persistenceLayer.Database;

import android.app.Activity;
import android.app.Application;

public class ProjectsWorldMemory extends Application
{

	private static Activity ActiveActivity;
	public Database db = new Database();
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		db.setContext(getApplicationContext());
		db.OpenDB();
	}
	
	public void setAppContextToDB()
	{
		db.setContext(getApplicationContext());
	}
	
	public synchronized static Activity getActiveActivity()
	{
		return ActiveActivity;
	}
	
	public synchronized static void setActiveActivity(Activity activity)
	{
		ActiveActivity = activity;
	}
	
}

