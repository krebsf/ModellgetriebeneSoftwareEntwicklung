/**********************************************************************
* Filename: Station.java
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

public class StationAccess implements ModelMusts
{
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public StationAccess()
	{
	}
	
	private transient static StationAccess stationAccess = new StationAccess();
	
	/**********************************************************************
	* @return the singleton instance
	**********************************************************************/
	public static StationAccess getAccess()
	{
		return stationAccess;
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
	public synchronized void insert(Station object)
	{
		try{
			if(Database.get(Station.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.DATABASE, 0, null, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.VIEW, 0, null, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Insert", "this Station already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Insert", "Error ocurred while trying to save the Station");
		}
	}
	
	/**********************************************************************
	* @param the object that will be updated
	**********************************************************************/
	public synchronized void update(Station station)
	{
		try{
			Station object = Database.get(Station.class, station.ID());
			int oldID = station.ID();
			object.setID();
			if(Database.get(Station.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.DATABASE, oldID, station, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.VIEW, oldID, station, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Edit", "this Station already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Edit", "Error ocurred while trying to save the Station");
		}
	}
	
	/**********************************************************************
	* @param the object that will be deleted
	**********************************************************************/
	public synchronized void delete(Station station)
	{
		try{
			Station object = Database.get(Station.class, station.ID());
			if(object != null)
			{
				int oldID = station.ID();
				notifyDeletion(object);
				Transactions.getSession().delete(object);
				
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.DATABASE, oldID, object, null, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.VIEW, oldID, object, null, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Delete", "this Station does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Delete", "Error ocurred while trying to delete the Station");
		}
	}
	
	/**********************************************************************
	* @param the object that will receive a new association
	* @param the object that will be added as new association (neighbor)
	**********************************************************************/
	public synchronized void insertAssociation(Station station, Object neighbor, String AssociationID)
	{
		try{
			Station object = Database.get(Station.class, station.ID());
			if(object != null)
			{
				int oldID = station.ID();
				if(neighbor instanceof Entry)
				{
					((Entry) neighbor).insertAssociation(object, AssociationID);
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, station, object, Entry.class, ((Entry) neighbor).ID(), null, neighbor));
				}
				if(neighbor instanceof Address)
				{
					object.setPlace((Address) neighbor);
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, station, object, Address.class, ((Address) neighbor).ID(), null, neighbor));
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, station, object, Address.class, ((Address) neighbor).ID(), null, neighbor));
				}
				object.checkModelRestrictions();
				object.checkRestrictions();
				Transactions.getSession().store(object);
			}else{
				Transactions.CancelTransaction("Failed in InsertAssociation", "the Station does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in InsertAssociation", "Error ocurred while trying to associate the Station");
		}
	}
	
	/**********************************************************************
	* @param the object that will remove an old association
	* @param the object that will be removed as the old association (neighbor)
	**********************************************************************/
	public synchronized void deleteAssociation(Station station, Object neighbor, String AssociationID)
	{
		try{
			Station object = Database.get(Station.class, station.ID());
			if(object != null)
			{
				int oldID = station.ID();
				if(neighbor instanceof Entry)
				{
					((Entry) neighbor).deleteAssociation(object, AssociationID);
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, station, object, Entry.class, ((Entry) neighbor).ID(), neighbor, null));
				}
				if(neighbor instanceof Address)
				{
					object.setPlace(null);
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, station, object, Address.class, ((Address) neighbor).ID(), neighbor, null));
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, station, object, Address.class, ((Address) neighbor).ID(), neighbor, null));
				}
				object.checkModelRestrictions();
				object.checkRestrictions();
				Transactions.getSession().store(object);
			}else{
				Transactions.CancelTransaction("Failed in DeleteAssociation", "the Station does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in DeleteAssociation", "Error ocurred while trying to delete the association of Station");
		}
	}
	
	/**********************************************************************
	* @param the object that will be removed
	**********************************************************************/
	public synchronized void notifyDeletion(Station station)
	{
		for(Entry x : station.records())
			x.deleteAssociation(station, "Station_EntryAssociation");
		if(station.place() != null)
			station.place().deleteAssociation(station, "Station_AddressAssociation");
	}
	
	/**********************************************************************
	* @param the object to be identified
	* @return the ID of the given object
	**********************************************************************/
	@Override
	public int ID(Object station)
	{
		return ((Station) station).ID();
	}
	
	/**********************************************************************
	* @return the type of the object which this class works with
	**********************************************************************/
	@Override
	public Class<?> getType()
	{
		return Station.class;
	}
	
	/**********************************************************************
	* @param the neibor object to be identified
	* @return the ID of the given neibor
	**********************************************************************/
	@Override
	public int getNeiborID(Object neibor)
	{
		if(neibor instanceof Entry)
			return ((Entry) neibor).ID();
		if(neibor instanceof Address)
			return ((Address) neibor).ID();
		return 0;
	}
	
	/**********************************************************************
	* @param the neibor object which this class can work with
	* @return the type of the neibor which this class can work with
	**********************************************************************/
	@Override
	public Class<?> getNeiborType(Object neibor)
	{
		if(neibor instanceof Entry)
			return Entry.class;
		if(neibor instanceof Address)
			return Address.class;
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
		if(object instanceof Station){
			Station x = new Station(((Station) object).meanActualValue(), ((Station) object).meanVarianceValue(), ((Station) object).name(), ((Station) object).numberOfEntries(), ((Station) object).target());
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
		if(oldObject instanceof Station && newObject instanceof Station){
			((Station) oldObject).setMeanActualValue(((Station) newObject).meanActualValue());
			((Station) oldObject).setMeanVarianceValue(((Station) newObject).meanVarianceValue());
			((Station) oldObject).setName(((Station) newObject).name());
			((Station) oldObject).setNumberOfEntries(((Station) newObject).numberOfEntries());
			((Station) oldObject).setTarget(((Station) newObject).target());
			((Station) oldObject).setID();
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
		
		//I'm not the holder class regarding the association between me and Entry
		//i do not need to have code for this association insertion since the holder has it and 
		//and the proper command was added when this action was made locally
		if(oldObject != null && newNeibor != null && oldObject instanceof Station && newNeibor instanceof Address)
		{
			boolean exists = false;
			if(((Station) oldObject).place() != null && ((Station) oldObject).place().ID() == ((Address) newNeibor).ID())
				exists = true;
			if(!exists)
			{
				((Station) oldObject).setPlace((Address) newNeibor);
				((Station) oldObject).checkModelRestrictions();
				((Station) oldObject).checkRestrictions();
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
		
		//I'm not the holder class regarding the association between me and Entry
		//i do not need to have code for this association deletion since the holder has it and 
		//and the proper command was added when this action was made locally
		if(oldObject != null && oldNeibor != null && oldObject instanceof Station && oldNeibor instanceof Address)
		{
			boolean exists = false;
			if(((Station) oldObject).place() != null && ((Station) oldObject).place().ID() == ((Address) oldNeibor).ID())
				exists = true;
			if(exists)
			{
				((Station) oldObject).setPlace(null);
			}
		}
	}
	
	//Server specific methods - End
}
