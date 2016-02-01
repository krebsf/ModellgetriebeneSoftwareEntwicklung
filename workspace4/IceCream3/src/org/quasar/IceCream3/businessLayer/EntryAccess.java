/**********************************************************************
* Filename: Entry.java
* Created: 2016/01/31
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream3.businessLayer;

import java.util.List;
import java.util.ArrayList;
import org.quasar.IceCream3.persistenceLayer.Database;
import org.quasar.IceCream3.utils.Command;
import org.quasar.IceCream3.utils.CommandTargetLayer;
import org.quasar.IceCream3.utils.CommandType;
import org.quasar.IceCream3.utils.Transactions;
import org.quasar.IceCream3.utils.PropertyChangeEvent;
import org.quasar.IceCream3.utils.PropertyChangeListener;

public class EntryAccess implements ModelMusts
{
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public EntryAccess()
	{
	}
	
	private transient static EntryAccess entryAccess = new EntryAccess();
	
	/**********************************************************************
	* @return the singleton instance
	**********************************************************************/
	public static EntryAccess getAccess()
	{
		return entryAccess;
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
	public synchronized void insert(Entry object)
	{
		try{
			if(Database.get(Entry.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.DATABASE, 0, null, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.VIEW, 0, null, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Insert", "this Entry already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Insert", "Error ocurred while trying to save the Entry");
		}
	}
	
	/**********************************************************************
	* @param the object that will be updated
	**********************************************************************/
	public synchronized void update(Entry entry)
	{
		try{
			Entry object = Database.get(Entry.class, entry.ID());
			int oldID = entry.ID();
			object.setID();
			if(Database.get(Entry.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.DATABASE, oldID, entry, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.VIEW, oldID, entry, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Edit", "this Entry already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Edit", "Error ocurred while trying to save the Entry");
		}
	}
	
	/**********************************************************************
	* @param the object that will be deleted
	**********************************************************************/
	public synchronized void delete(Entry entry)
	{
		try{
			Entry object = Database.get(Entry.class, entry.ID());
			if(object != null)
			{
				int oldID = entry.ID();
				notifyDeletion(object);
				Transactions.getSession().delete(object);
				
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.DATABASE, oldID, object, null, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.VIEW, oldID, object, null, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Delete", "this Entry does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Delete", "Error ocurred while trying to delete the Entry");
		}
	}
	
	/**********************************************************************
	* @param the object that will receive a new association
	* @param the object that will be added as new association (neighbor)
	**********************************************************************/
	public synchronized void insertAssociation(Entry entry, Object neighbor, String AssociationID)
	{
		try{
			Entry object = Database.get(Entry.class, entry.ID());
			if(object != null)
			{
				int oldID = entry.ID();
				if(neighbor instanceof Station)
				{
					object.setStation((Station) neighbor);
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, entry, object, Station.class, ((Station) neighbor).ID(), null, neighbor));
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, entry, object, Station.class, ((Station) neighbor).ID(), null, neighbor));
				}
				object.checkModelRestrictions();
				object.checkRestrictions();
				Transactions.getSession().store(object);
			}else{
				Transactions.CancelTransaction("Failed in InsertAssociation", "the Entry does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in InsertAssociation", "Error ocurred while trying to associate the Entry");
		}
	}
	
	/**********************************************************************
	* @param the object that will remove an old association
	* @param the object that will be removed as the old association (neighbor)
	**********************************************************************/
	public synchronized void deleteAssociation(Entry entry, Object neighbor, String AssociationID)
	{
		try{
			Entry object = Database.get(Entry.class, entry.ID());
			if(object != null)
			{
				int oldID = entry.ID();
				if(neighbor instanceof Station)
				{
					object.setStation(null);
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, entry, object, Station.class, ((Station) neighbor).ID(), neighbor, null));
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, entry, object, Station.class, ((Station) neighbor).ID(), neighbor, null));
				}
				object.checkModelRestrictions();
				object.checkRestrictions();
				Transactions.getSession().store(object);
			}else{
				Transactions.CancelTransaction("Failed in DeleteAssociation", "the Entry does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in DeleteAssociation", "Error ocurred while trying to delete the association of Entry");
		}
	}
	
	/**********************************************************************
	* @param the object that will be removed
	**********************************************************************/
	public synchronized void notifyDeletion(Entry entry)
	{
		if(entry.station() != null)
			entry.station().deleteAssociation(entry, "Station_EntryAssociation");
	}
	
	/**********************************************************************
	* @param the object to be identified
	* @return the ID of the given object
	**********************************************************************/
	@Override
	public int ID(Object entry)
	{
		return ((Entry) entry).ID();
	}
	
	/**********************************************************************
	* @return the type of the object which this class works with
	**********************************************************************/
	@Override
	public Class<?> getType()
	{
		return Entry.class;
	}
	
	/**********************************************************************
	* @param the neibor object to be identified
	* @return the ID of the given neibor
	**********************************************************************/
	@Override
	public int getNeiborID(Object neibor)
	{
		if(neibor instanceof Station)
			return ((Station) neibor).ID();
		return 0;
	}
	
	/**********************************************************************
	* @param the neibor object which this class can work with
	* @return the type of the neibor which this class can work with
	**********************************************************************/
	@Override
	public Class<?> getNeiborType(Object neibor)
	{
		if(neibor instanceof Station)
			return Station.class;
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
		if(object instanceof Entry){
			Entry x = new Entry(((Entry) object).actual(), ((Entry) object).date(), ((Entry) object).variance());
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
		if(oldObject instanceof Entry && newObject instanceof Entry){
			((Entry) oldObject).setActual(((Entry) newObject).actual());
			((Entry) oldObject).setDate(((Entry) newObject).date());
			((Entry) oldObject).setVariance(((Entry) newObject).variance());
			((Entry) oldObject).setID();
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
		if(oldObject != null && newNeibor != null && oldObject instanceof Entry && newNeibor instanceof Station)
		{
			boolean exists = false;
			if(((Entry) oldObject).station() != null && ((Entry) oldObject).station().ID() == ((Station) newNeibor).ID())
				exists = true;
			if(!exists)
			{
				((Entry) oldObject).setStation((Station) newNeibor);
				((Entry) oldObject).checkModelRestrictions();
				((Entry) oldObject).checkRestrictions();
			}
		}
	}
	
	/**********************************************************************
	* @param the database session
	* @param the object to whom will be removed the association
	* @param the neibor that will be de-associated
	**********************************************************************/
	@Override
	public void serverDeleteAssociation(Object oldObject, Object oldNeibor)
	{
		if(oldObject != null && oldNeibor != null && oldObject instanceof Entry && oldNeibor instanceof Station)
		{
			boolean exists = false;
			if(((Entry) oldObject).station() != null && ((Entry) oldObject).station().ID() == ((Station) oldNeibor).ID())
				exists = true;
			if(exists)
			{
				((Entry) oldObject).setStation(null);
			}
		}
	}
	
	//Server specific methods - End
}
