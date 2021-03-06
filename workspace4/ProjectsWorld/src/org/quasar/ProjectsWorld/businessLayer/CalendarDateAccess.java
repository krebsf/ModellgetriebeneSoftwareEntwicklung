/**********************************************************************
* Filename: CalendarDate.java
* Created: 2016/01/14
* @author Lu�s Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.businessLayer;

import java.util.List;
import java.util.ArrayList;
import org.quasar.ProjectsWorld.persistenceLayer.Database;
import org.quasar.ProjectsWorld.utils.Command;
import org.quasar.ProjectsWorld.utils.CommandTargetLayer;
import org.quasar.ProjectsWorld.utils.CommandType;
import org.quasar.ProjectsWorld.utils.Transactions;
import org.quasar.ProjectsWorld.utils.PropertyChangeEvent;
import org.quasar.ProjectsWorld.utils.PropertyChangeListener;

public class CalendarDateAccess implements ModelMusts
{
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public CalendarDateAccess()
	{
	}
	
	private transient static CalendarDateAccess calendardateAccess = new CalendarDateAccess();
	
	/**********************************************************************
	* @return the singleton instance
	**********************************************************************/
	public static CalendarDateAccess getAccess()
	{
		return calendardateAccess;
	}
	
	private transient List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();
	
	/**********************************************************************
	* @param the class that holds the listeners
	* @param the CommandType or persistence action
	* @param the previous version of the object
	* @param the new version of the object
	* @param the previous version of the neighbor object
	* @param the new version of the neighbor object
	**********************************************************************/
	@Override
	public synchronized void notifyObjectListener(Object object, CommandType property, int oldObjectID, Object oldObject, Object newObject, int oldNeiborID, Object oldNeighbor, Object newNeighbor)
	{
		for(PropertyChangeListener l : listener)
			l.propertyChange(new PropertyChangeEvent(object, property, oldObjectID, oldObject, newObject, oldNeiborID, oldNeighbor, newNeighbor));
	}
	
	/**********************************************************************
	* @param the class that will observe (this class must implement "PropertyChangeListener")
	**********************************************************************/
	public synchronized void setChangeListener(PropertyChangeListener listener)
	{
		if(!this.listener.contains(listener))
			this.listener.add(listener);
	}
	
	/**********************************************************************
	* @param the class that will stop observing (this class must implement "PropertyChangeListener")
	**********************************************************************/
	public synchronized void removeChangeListener(PropertyChangeListener listener)
	{
		if(this.listener.contains(listener))
			this.listener.remove(listener);
	}
	
	/**********************************************************************
	* @param the object that will be inserted
	**********************************************************************/
	public synchronized void insert(CalendarDate object)
	{
		try{
			if(Database.get(CalendarDate.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.DATABASE, 0, null, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.VIEW, 0, null, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Insert", "this CalendarDate already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Insert", "Error ocurred while trying to save the CalendarDate");
		}
	}
	
	/**********************************************************************
	* @param the object that will be updated
	**********************************************************************/
	public synchronized void update(CalendarDate calendardate)
	{
		try{
			CalendarDate object = Database.get(CalendarDate.class, calendardate.ID());
			int oldID = calendardate.ID();
			object.setID();
			if(Database.get(CalendarDate.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.DATABASE, oldID, calendardate, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.VIEW, oldID, calendardate, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Edit", "this CalendarDate already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Edit", "Error ocurred while trying to save the CalendarDate");
		}
	}
	
	/**********************************************************************
	* @param the object that will be deleted
	**********************************************************************/
	public synchronized void delete(CalendarDate calendardate)
	{
		try{
			CalendarDate object = Database.get(CalendarDate.class, calendardate.ID());
			if(object != null)
			{
				int oldID = calendardate.ID();
				Transactions.getSession().delete(object);
				
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.DATABASE, oldID, object, null, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.VIEW, oldID, object, null, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Delete", "this CalendarDate does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Delete", "Error ocurred while trying to delete the CalendarDate");
		}
	}
	
	/**********************************************************************
	* @param the object to be identified
	* @return the ID of the given object
	**********************************************************************/
	@Override
	public int ID(Object calendardate)
	{
		return ((CalendarDate) calendardate).ID();
	}
	
	/**********************************************************************
	* @return the type of the object which this class works with
	**********************************************************************/
	@Override
	public Class<?> getType()
	{
		return CalendarDate.class;
	}
	
	/**********************************************************************
	* @param the neibor object to be identified
	* @return the ID of the given neibor
	**********************************************************************/
	@Override
	public int getNeiborID(Object neibor)
	{
		return 0;
	}
	
	/**********************************************************************
	* @param the neibor object which this class can work with
	* @return the type of the neibor which this class can work with
	**********************************************************************/
	@Override
	public Class<?> getNeiborType(Object neibor)
	{
		return null;
	}
	
	//Server specific methods - Start
	/**********************************************************************
	* @param the object to be inserted
	* @return a cleaned of associations object that be inserted
	**********************************************************************/
	@Override
	public Object serverInsert(Object object)
	{
		if(object instanceof CalendarDate){
			CalendarDate x = new CalendarDate(((CalendarDate) object).day(), ((CalendarDate) object).month(), ((CalendarDate) object).year());
			x.checkModelRestrictions();
			x.checkRestrictions();
			return x;
		}else
			return null;
	}
	
	/**********************************************************************
	* @param the object to be update
	* @return updated version of the given object
	**********************************************************************/
	@Override
	public void serverUpdate(Object oldObject, Object newObject)
	{
		if(oldObject instanceof CalendarDate && newObject instanceof CalendarDate){
			((CalendarDate) oldObject).setDay(((CalendarDate) newObject).day());
			((CalendarDate) oldObject).setMonth(((CalendarDate) newObject).month());
			((CalendarDate) oldObject).setYear(((CalendarDate) newObject).year());
			((CalendarDate) oldObject).setID();
		}
	}
	
	/**********************************************************************
	* @param the database session
	* @param the object that will receive the new association
	* @param the neibor that will be associated to
	**********************************************************************/
	@Override
	public void serverInsertAssociation(Object oldObject, Object newNeibor)
	{
	}
	
	/**********************************************************************
	* @param the database session
	* @param the object to whom will be removed the association
	* @param the neibor that will be de-associated
	**********************************************************************/
	@Override
	public void serverDeleteAssociation(Object oldObject, Object oldNeibor)
	{
	}
	
	//Server specific methods - End
}
