/**********************************************************************
* Filename: Database.java
* Created: 2016/01/14
* @author Lu�s Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.persistenceLayer;

import org.quasar.ProjectsWorld.businessLayer.Training;
import org.quasar.ProjectsWorld.businessLayer.Member;
import java.io.IOException;
import com.db4o.query.Query;
import com.db4o.Db4oEmbedded;
import com.db4o.cs.config.ClientConfiguration;
import com.db4o.ObjectContainer;
import org.quasar.ProjectsWorld.businessLayer.Project;
import org.quasar.ProjectsWorld.businessLayer.CalendarDate;
import java.util.Set;
import com.db4o.config.AndroidSupport;
import com.db4o.ObjectSet;
import java.io.File;
import android.content.Context;
import android.util.Log;

import com.db4o.cs.Db4oClientServer;
import org.quasar.ProjectsWorld.businessLayer.Qualification;
import com.db4o.config.EmbeddedConfiguration;
import org.quasar.ProjectsWorld.businessLayer.Worker;
import java.util.Collection;
import java.util.HashSet;
import org.quasar.ProjectsWorld.businessLayer.Company;

public class Database{
	private static ObjectContainer oc = null;
	private static Context context;
	private final static String DataBaseName = "Database";
	private final static String DataBaseExtension = ".db4o";
	
	
	/***********************************************************
	* Create, open and close the database
	***********************************************************/
	public synchronized static ObjectContainer OpenDB(){
		try{
			
			if (oc == null || oc.ext().isClosed())
				oc = Db4oEmbedded.openFile(dbConfig(), db4oDBFullPath(context));
			return oc;
		} catch (Exception ie) {
			
			Log.e("DB40", ie.getMessage(), ie);
			return null;
		}
	}
	
	/***********************************************************
	* return the app context
	***********************************************************/
	public synchronized Context getContext(){
		return context;
	}
	
	/***********************************************************
	* sets the app context
	***********************************************************/
	public synchronized static void setContext(Context context){
		Database.context = context;
	}
	
	/***********************************************************
	* returns the database android path
	***********************************************************/
	public static String db4oDBFullPath(Context ctx){
		//return ctx.getDir("data", Context.MODE_WORLD_WRITEABLE) + "/" + DataBaseName + DataBaseExtension;
		return ctx.getFilesDir()+ DataBaseName+DataBaseExtension;
	}
	
	/***********************************************************
	* Deletes the database
	***********************************************************/
	public static synchronized void DeleteDB(){
		new File(db4oDBFullPath(context)).delete();
	}
	
	/***********************************************************
	* Closes the database
	***********************************************************/
	public synchronized static void close(){
		if (oc != null)
			oc.close();
	}
	
	/***********************************************************
	* returns the server database configuration
	***********************************************************/
	public static ClientConfiguration dbServerConfig(){
		ClientConfiguration configuration = Db4oClientServer.newClientConfiguration();
		configuration.common().objectClass(CalendarDate.class).objectField("ID").indexed(true);
		configuration.common().objectClass(CalendarDate.class).updateDepth(1);
		configuration.common().objectClass(Company.class).objectField("ID").indexed(true);
		configuration.common().objectClass(Company.class).updateDepth(1);
		configuration.common().objectClass(Member.class).objectField("ID").indexed(true);
		configuration.common().objectClass(Member.class).updateDepth(2);
		configuration.common().objectClass(Project.class).objectField("ID").indexed(true);
		configuration.common().objectClass(Project.class).updateDepth(2);
		configuration.common().objectClass(Qualification.class).objectField("ID").indexed(true);
		configuration.common().objectClass(Qualification.class).updateDepth(1);
		configuration.common().objectClass(Training.class).objectField("ID").indexed(true);
		configuration.common().objectClass(Training.class).updateDepth(2);
		configuration.common().objectClass(Worker.class).objectField("ID").indexed(true);
		configuration.common().objectClass(Worker.class).updateDepth(2);
		return null;
	}
	
	/***********************************************************
	* returns the local database configuration
	***********************************************************/
	public static EmbeddedConfiguration dbConfig(){
		EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
		configuration.file().lockDatabaseFile(false); 
		configuration.common().objectClass(CalendarDate.class).objectField("ID").indexed(true);
		configuration.common().objectClass(CalendarDate.class).updateDepth(1);
		configuration.common().objectClass(Company.class).objectField("ID").indexed(true);
		configuration.common().objectClass(Company.class).updateDepth(1);
		configuration.common().objectClass(Member.class).objectField("ID").indexed(true);
		configuration.common().objectClass(Member.class).updateDepth(2);
		configuration.common().objectClass(Project.class).objectField("ID").indexed(true);
		configuration.common().objectClass(Project.class).updateDepth(2);
		configuration.common().objectClass(Qualification.class).objectField("ID").indexed(true);
		configuration.common().objectClass(Qualification.class).updateDepth(1);
		configuration.common().objectClass(Training.class).objectField("ID").indexed(true);
		configuration.common().objectClass(Training.class).updateDepth(2);
		configuration.common().objectClass(Worker.class).objectField("ID").indexed(true);
		configuration.common().objectClass(Worker.class).updateDepth(2);
		return configuration;
	}
	
	
	/***********************************************************
	* @return all instances of the given class
	***********************************************************/
	public synchronized static <T, Y> Set<T> allInstances(Class<Y> prototype){
		return new HashSet<T>((Collection<? extends T>)OpenDB().query(prototype));
	}
	
	/***********************************************************
	* @return all instances of the given class in an ordered (by modification) manner
	***********************************************************/
	public synchronized static <T, Y> ObjectSet<T> allInstancesOrdered(Class<Y> prototype){
		return (ObjectSet<T>) OpenDB().query(prototype);
	}
	
	/***********************************************************
	* returns an object based on type (class), a field name and respective object constraint
	***********************************************************/
	public synchronized static <T> T get(Class<T> c, String fieldName, Object constraint){
		Query q = OpenDB().query();
		q.constrain(c);
		q.descend(fieldName).constrain(constraint);
		ObjectSet<T> result = q.execute();
		if (result.hasNext())
			return result.next();
		return null;
	}
	
	/***********************************************************
	* returns an object based on type (class), a field name and respective string constraint
	***********************************************************/
	public synchronized static <T> T get(Class<T> c, String fieldName, String constraint){
		Query q = OpenDB().query();
		q.constrain(c);
		q.descend(fieldName).constrain(constraint);
		ObjectSet<T> result = q.execute();
		if (result.hasNext())
			return result.next();
		return null;
	}
	
	/***********************************************************
	* returns an object based on type (class), and constraint based on the field 'ID' 
	***********************************************************/
	public synchronized static <T> T get(Class<T> c, int constraint){
		Query q = OpenDB().query();
		q.constrain(c);
		q.descend("ID").constrain(constraint);
		ObjectSet<T> result = q.execute();
		if (result.hasNext())
			return result.next();
		return null;
	}
	
	/***********************************************************
	* returns an object based on type (class), constraint based on the field 'ID', and session
	***********************************************************/
	public synchronized static <T> T get(ObjectContainer oc, Class<T> c, int constraint){
		Query q = oc.query();
		q.constrain(c);
		q.descend("ID").constrain(constraint);
		ObjectSet<T> result = q.execute();
		if (result.hasNext())
			return result.next();
		return null;
	}
	
	/***********************************************************
	* returns an object based on an example object
	***********************************************************/
	public static Object get(Object object){
		return OpenDB().queryByExample(object);
	}
	
}
