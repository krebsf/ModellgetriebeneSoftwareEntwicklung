/**********************************************************************
* Filename: Training.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
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

public class TrainingAccess extends ProjectAccess implements ModelMusts
{
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public TrainingAccess()
	{
	}
	
	private transient static TrainingAccess trainingAccess = new TrainingAccess();
	
	/**********************************************************************
	* @return the singleton instance
	**********************************************************************/
	public static TrainingAccess getAccess()
	{
		return trainingAccess;
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
	public synchronized void insert(Training object)
	{
		try{
			if(Database.get(Training.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.DATABASE, 0, null, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.VIEW, 0, null, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Insert", "this Training already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Insert", "Error ocurred while trying to save the Training");
		}
	}
	
	/**********************************************************************
	* @param the object that will be updated
	**********************************************************************/
	public synchronized void update(Training training)
	{
		try{
			Training object = Database.get(Training.class, training.ID());
			int oldID = training.ID();
			object.setID();
			if(Database.get(Training.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.DATABASE, oldID, training, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.VIEW, oldID, training, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Edit", "this Training already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Edit", "Error ocurred while trying to save the Training");
		}
	}
	
	/**********************************************************************
	* @param the object that will be deleted
	**********************************************************************/
	public synchronized void delete(Training training)
	{
		try{
			Training object = Database.get(Training.class, training.ID());
			if(object != null)
			{
				int oldID = training.ID();
				notifyDeletion(object);
				Transactions.getSession().delete(object);
				
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.DATABASE, oldID, object, null, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.VIEW, oldID, object, null, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Delete", "this Training does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Delete", "Error ocurred while trying to delete the Training");
		}
	}
	
	/**********************************************************************
	* @param the object that will receive a new association
	* @param the object that will be added as new association (neighbor)
	**********************************************************************/
	public synchronized void insertAssociation(Training training, Object neighbor, String AssociationID)
	{
		try{
			Training object = Database.get(Training.class, training.ID());
			if(object != null)
			{
				int oldID = training.ID();
				if(neighbor instanceof Qualification)
				{
					object.addTrained((Qualification) neighbor);
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, training, object, Qualification.class, ((Qualification) neighbor).ID(), null, neighbor));
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, training, object, Qualification.class, ((Qualification) neighbor).ID(), null, neighbor));
				}
				object.checkModelRestrictions();
				object.checkRestrictions();
				Transactions.getSession().store(object);
			}else{
				Transactions.CancelTransaction("Failed in InsertAssociation", "the Training does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in InsertAssociation", "Error ocurred while trying to associate the Training");
		}
	}
	
	/**********************************************************************
	* @param the object that will remove an old association
	* @param the object that will be removed as the old association (neighbor)
	**********************************************************************/
	public synchronized void deleteAssociation(Training training, Object neighbor, String AssociationID)
	{
		try{
			Training object = Database.get(Training.class, training.ID());
			if(object != null)
			{
				int oldID = training.ID();
				if(neighbor instanceof Qualification)
				{
					object.removeTrained((Qualification) neighbor);
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, training, object, Qualification.class, ((Qualification) neighbor).ID(), neighbor, null));
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, training, object, Qualification.class, ((Qualification) neighbor).ID(), neighbor, null));
				}
				object.checkModelRestrictions();
				object.checkRestrictions();
				Transactions.getSession().store(object);
			}else{
				Transactions.CancelTransaction("Failed in DeleteAssociation", "the Training does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in DeleteAssociation", "Error ocurred while trying to delete the association of Training");
		}
	}
	
	/**********************************************************************
	* @param the object that will be removed
	**********************************************************************/
	public synchronized void notifyDeletion(Training training)
	{
		for(Qualification x : training.trained())
			x.deleteAssociation(training, "TrainsAssociation");
		super.notifyDeletion(training);
	}
	
	/**********************************************************************
	* @param the object to be identified
	* @return the ID of the given object
	**********************************************************************/
	@Override
	public int ID(Object training)
	{
		return ((Training) training).ID();
	}
	
	/**********************************************************************
	* @return the type of the object which this class works with
	**********************************************************************/
	@Override
	public Class<?> getType()
	{
		return Training.class;
	}
	
	/**********************************************************************
	* @param the neibor object to be identified
	* @return the ID of the given neibor
	**********************************************************************/
	@Override
	public int getNeiborID(Object neibor)
	{
		if(neibor instanceof Qualification)
			return ((Qualification) neibor).ID();
		return 0;
	}
	
	/**********************************************************************
	* @param the neibor object which this class can work with
	* @return the type of the neibor which this class can work with
	**********************************************************************/
	@Override
	public Class<?> getNeiborType(Object neibor)
	{
		if(neibor instanceof Qualification)
			return Qualification.class;
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
		if(object instanceof Training){
			Training x = new Training(((Training) object).months(), ((Training) object).name(), ((Training) object).size(), ((Training) object).status());
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
		if(oldObject instanceof Training && newObject instanceof Training){
			((Training) oldObject).setMonths(((Training) newObject).months());
			((Training) oldObject).setName(((Training) newObject).name());
			((Training) oldObject).setSize(((Training) newObject).size());
			((Training) oldObject).setStatus(((Training) newObject).status());
			((Training) oldObject).setID();
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
		if(oldObject != null && newNeibor != null && oldObject instanceof Training && newNeibor instanceof Qualification)
		{
			boolean exists = false;
			for(Qualification x : ((Training) oldObject).trained())
				if(x.ID() == ((Qualification) newNeibor).ID())
					exists = true;
			if(!exists)
			{
				((Training) oldObject).addTrained((Qualification) newNeibor);
				((Training) oldObject).checkModelRestrictions();
				((Training) oldObject).checkRestrictions();
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
		if(oldObject != null && oldNeibor != null && oldObject instanceof Training && oldNeibor instanceof Qualification)
		{
			boolean exists = false;
			for(Qualification x : ((Training) oldObject).trained())
				if(x.ID() == ((Qualification) oldNeibor).ID())
					exists = true;
			if(exists)
			{
				((Training) oldObject).removeTrained((Qualification) oldNeibor);
			}
		}
	}
	
	//Server specific methods - End
}
