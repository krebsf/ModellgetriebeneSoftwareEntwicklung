/**********************************************************************
* Filename: IceCream.java
* Created: 2016/01/30
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream;

import org.quasar.IceCream.persistenceLayer.Database;

import android.app.Activity;
import android.app.Application;

public class IceCreamMemory extends Application
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

