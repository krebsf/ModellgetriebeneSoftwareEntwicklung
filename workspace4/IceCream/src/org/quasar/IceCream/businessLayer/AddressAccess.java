/**********************************************************************
* Filename: Address.java
* Created: 2016/01/30
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream.businessLayer;

import java.util.List;
import java.util.ArrayList;
import org.quasar.IceCream.persistenceLayer.Database;
import org.quasar.IceCream.utils.Command;
import org.quasar.IceCream.utils.CommandTargetLayer;
import org.quasar.IceCream.utils.CommandType;
import org.quasar.IceCream.utils.Transactions;
import org.quasar.IceCream.utils.PropertyChangeEvent;
import org.quasar.IceCream.utils.PropertyChangeListener;

public class AddressAccess implements ModelMusts
{
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public AddressAccess()
	{
	}
	
	private transient static AddressAccess addressAccess = new AddressAccess();
	
	/**********************************************************************
	* @return the singleton instance
	**********************************************************************/
	public static AddressAccess getAccess()
	{
		return addressAccess;
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
	public synchronized void insert(Address object)
	{
		try{
			if(Database.get(Address.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.DATABASE, 0, null, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.VIEW, 0, null, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Insert", "this Address already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Insert", "Error ocurred while trying to save the Address");
		}
	}
	
	/**********************************************************************
	* @param the object that will be updated
	**********************************************************************/
	public synchronized void update(Address address)
	{
		try{
			Address object = Database.get(Address.class, address.ID());
			int oldID = address.ID();
			object.setID();
			if(Database.get(Address.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.DATABASE, oldID, address, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.VIEW, oldID, address, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Edit", "this Address already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Edit", "Error ocurred while trying to save the Address");
		}
	}
	
	/**********************************************************************
	* @param the object that will be deleted
	**********************************************************************/
	public synchronized void delete(Address address)
	{
		try{
			Address object = Database.get(Address.class, address.ID());
			if(object != null)
			{
				int oldID = address.ID();
				notifyDeletion(object);
				Transactions.getSession().delete(object);
				
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.DATABASE, oldID, object, null, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.VIEW, oldID, object, null, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Delete", "this Address does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Delete", "Error ocurred while trying to delete the Address");
		}
	}
	
	/**********************************************************************
	* @param the object that will receive a new association
	* @param the object that will be added as new association (neighbor)
	**********************************************************************/
	public synchronized void insertAssociation(Address address, Object neighbor, String AssociationID)
	{
		try{
			Address object = Database.get(Address.class, address.ID());
			if(object != null)
			{
				int oldID = address.ID();
				if(neighbor instanceof Station)
				{
					((Station) neighbor).insertAssociation(object, AssociationID);
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, address, object, Station.class, ((Station) neighbor).ID(), null, neighbor));
				}
				object.checkModelRestrictions();
				object.checkRestrictions();
				Transactions.getSession().store(object);
			}else{
				Transactions.CancelTransaction("Failed in InsertAssociation", "the Address does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in InsertAssociation", "Error ocurred while trying to associate the Address");
		}
	}
	
	/**********************************************************************
	* @param the object that will remove an old association
	* @param the object that will be removed as the old association (neighbor)
	**********************************************************************/
	public synchronized void deleteAssociation(Address address, Object neighbor, String AssociationID)
	{
		try{
			Address object = Database.get(Address.class, address.ID());
			if(object != null)
			{
				int oldID = address.ID();
				if(neighbor instanceof Station)
				{
					((Station) neighbor).deleteAssociation(object, AssociationID);
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, address, object, Station.class, ((Station) neighbor).ID(), neighbor, null));
				}
				object.checkModelRestrictions();
				object.checkRestrictions();
				Transactions.getSession().store(object);
			}else{
				Transactions.CancelTransaction("Failed in DeleteAssociation", "the Address does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in DeleteAssociation", "Error ocurred while trying to delete the association of Address");
		}
	}
	
	/**********************************************************************
	* @param the object that will be removed
	**********************************************************************/
	public synchronized void notifyDeletion(Address address)
	{
		if(address.station() != null)
			address.station().deleteAssociation(address, "Station_AddressAssociation");
	}
	
	/**********************************************************************
	* @param the object to be identified
	* @return the ID of the given object
	**********************************************************************/
	@Override
	public int ID(Object address)
	{
		return ((Address) address).ID();
	}
	
	/**********************************************************************
	* @return the type of the object which this class works with
	**********************************************************************/
	@Override
	public Class<?> getType()
	{
		return Address.class;
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
		if(object instanceof Address){
			Address x = new Address(((Address) object).postCode(), ((Address) object).street());
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
		if(oldObject instanceof Address && newObject instanceof Address){
			((Address) oldObject).setPostCode(((Address) newObject).postCode());
			((Address) oldObject).setStreet(((Address) newObject).street());
			((Address) oldObject).setID();
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
		
		//I'm not the holder class regarding the association between me and Station
		//i do not need to have code for this association insertion since the holder has it and 
		//and the proper command was added when this action was made locally
	}
	
	/**********************************************************************
	* @param the database session
	* @param the object to whom will be removed the association
	* @param the neibor that will be de-associated
	**********************************************************************/
	@Override
	public void serverDeleteAssociation(Object oldObject, Object oldNeibor)
	{
		
		//I'm not the holder class regarding the association between me and Station
		//i do not need to have code for this association deletion since the holder has it 
		//and the proper command was added when this action was made locally
	}
	
	//Server specific methods - End
}
