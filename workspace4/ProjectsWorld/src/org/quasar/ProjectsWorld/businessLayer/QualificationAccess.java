/**********************************************************************
* Filename: Qualification.java
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

public class QualificationAccess implements ModelMusts
{
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public QualificationAccess()
	{
	}
	
	private transient static QualificationAccess qualificationAccess = new QualificationAccess();
	
	/**********************************************************************
	* @return the singleton instance
	**********************************************************************/
	public static QualificationAccess getAccess()
	{
		return qualificationAccess;
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
	public synchronized void insert(Qualification object)
	{
		try{
			if(Database.get(Qualification.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.DATABASE, 0, null, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.VIEW, 0, null, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Insert", "this Qualification already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Insert", "Error ocurred while trying to save the Qualification");
		}
	}
	
	/**********************************************************************
	* @param the object that will be updated
	**********************************************************************/
	public synchronized void update(Qualification qualification)
	{
		try{
			Qualification object = Database.get(Qualification.class, qualification.ID());
			int oldID = qualification.ID();
			object.setID();
			if(Database.get(Qualification.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.DATABASE, oldID, qualification, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.VIEW, oldID, qualification, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Edit", "this Qualification already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Edit", "Error ocurred while trying to save the Qualification");
		}
	}
	
	/**********************************************************************
	* @param the object that will be deleted
	**********************************************************************/
	public synchronized void delete(Qualification qualification)
	{
		try{
			Qualification object = Database.get(Qualification.class, qualification.ID());
			if(object != null)
			{
				int oldID = qualification.ID();
				notifyDeletion(object);
				Transactions.getSession().delete(object);
				
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.DATABASE, oldID, object, null, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.VIEW, oldID, object, null, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Delete", "this Qualification does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Delete", "Error ocurred while trying to delete the Qualification");
		}
	}
	
	/**********************************************************************
	* @param the object that will receive a new association
	* @param the object that will be added as new association (neighbor)
	**********************************************************************/
	public synchronized void insertAssociation(Qualification qualification, Object neighbor, String AssociationID)
	{
		try{
			Qualification object = Database.get(Qualification.class, qualification.ID());
			if(object != null)
			{
				int oldID = qualification.ID();
				if(neighbor instanceof Worker)
				{
					((Worker) neighbor).insertAssociation(object, AssociationID);
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, qualification, object, Worker.class, ((Worker) neighbor).ID(), null, neighbor));
				}
				if(neighbor instanceof Project)
				{
					((Project) neighbor).insertAssociation(object, AssociationID);
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, qualification, object, Project.class, ((Project) neighbor).ID(), null, neighbor));
				}
				if(neighbor instanceof Training)
				{
					((Training) neighbor).insertAssociation(object, AssociationID);
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, qualification, object, Training.class, ((Training) neighbor).ID(), null, neighbor));
				}
				object.checkModelRestrictions();
				object.checkRestrictions();
				Transactions.getSession().store(object);
			}else{
				Transactions.CancelTransaction("Failed in InsertAssociation", "the Qualification does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in InsertAssociation", "Error ocurred while trying to associate the Qualification");
		}
	}
	
	/**********************************************************************
	* @param the object that will remove an old association
	* @param the object that will be removed as the old association (neighbor)
	**********************************************************************/
	public synchronized void deleteAssociation(Qualification qualification, Object neighbor, String AssociationID)
	{
		try{
			Qualification object = Database.get(Qualification.class, qualification.ID());
			if(object != null)
			{
				int oldID = qualification.ID();
				if(neighbor instanceof Worker)
				{
					((Worker) neighbor).deleteAssociation(object, AssociationID);
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, qualification, object, Worker.class, ((Worker) neighbor).ID(), neighbor, null));
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, qualification, object, Worker.class, ((Worker) neighbor).ID(), neighbor, null));
				}
				if(neighbor instanceof Project)
				{
					((Project) neighbor).deleteAssociation(object, AssociationID);
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, qualification, object, Project.class, ((Project) neighbor).ID(), neighbor, null));
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, qualification, object, Project.class, ((Project) neighbor).ID(), neighbor, null));
				}
				if(neighbor instanceof Training)
				{
					((Training) neighbor).deleteAssociation(object, AssociationID);
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, qualification, object, Training.class, ((Training) neighbor).ID(), neighbor, null));
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, qualification, object, Training.class, ((Training) neighbor).ID(), neighbor, null));
				}
				object.checkModelRestrictions();
				object.checkRestrictions();
				Transactions.getSession().store(object);
			}else{
				Transactions.CancelTransaction("Failed in DeleteAssociation", "the Qualification does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in DeleteAssociation", "Error ocurred while trying to delete the association of Qualification");
		}
	}
	
	/**********************************************************************
	* @param the object that will be removed
	**********************************************************************/
	public synchronized void notifyDeletion(Qualification qualification)
	{
		for(Worker x : qualification.workers())
			x.deleteAssociation(qualification, "IsQualifiedAssociation");
		for(Project x : qualification.projects())
			x.deleteAssociation(qualification, "RequiresAssociation");
		for(Training x : qualification.trainings())
			x.deleteAssociation(qualification, "TrainsAssociation");
	}
	
	/**********************************************************************
	* @param the object to be identified
	* @return the ID of the given object
	**********************************************************************/
	@Override
	public int ID(Object qualification)
	{
		return ((Qualification) qualification).ID();
	}
	
	/**********************************************************************
	* @return the type of the object which this class works with
	**********************************************************************/
	@Override
	public Class<?> getType()
	{
		return Qualification.class;
	}
	
	/**********************************************************************
	* @param the neibor object to be identified
	* @return the ID of the given neibor
	**********************************************************************/
	@Override
	public int getNeiborID(Object neibor)
	{
		if(neibor instanceof Worker)
			return ((Worker) neibor).ID();
		if(neibor instanceof Project)
			return ((Project) neibor).ID();
		if(neibor instanceof Training)
			return ((Training) neibor).ID();
		return 0;
	}
	
	/**********************************************************************
	* @param the neibor object which this class can work with
	* @return the type of the neibor which this class can work with
	**********************************************************************/
	@Override
	public Class<?> getNeiborType(Object neibor)
	{
		if(neibor instanceof Worker)
			return Worker.class;
		if(neibor instanceof Project)
			return Project.class;
		if(neibor instanceof Training)
			return Training.class;
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
		if(object instanceof Qualification){
			Qualification x = new Qualification(((Qualification) object).description());
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
		if(oldObject instanceof Qualification && newObject instanceof Qualification){
			((Qualification) oldObject).setDescription(((Qualification) newObject).description());
			((Qualification) oldObject).setID();
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
		
		//I'm not the holder class regarding the association between me and Worker
		//i do not need to have code for this association insertion since the holder has it and 
		//and the proper command was added when this action was made locally
		
		//I'm not the holder class regarding the association between me and Project
		//i do not need to have code for this association insertion since the holder has it and 
		//and the proper command was added when this action was made locally
		
		//I'm not the holder class regarding the association between me and Training
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
		
		//I'm not the holder class regarding the association between me and Worker
		//i do not need to have code for this association deletion since the holder has it and 
		//and the proper command was added when this action was made locally
		
		//I'm not the holder class regarding the association between me and Project
		//i do not need to have code for this association deletion since the holder has it and 
		//and the proper command was added when this action was made locally
		
		//I'm not the holder class regarding the association between me and Training
		//i do not need to have code for this association deletion since the holder has it and 
		//and the proper command was added when this action was made locally
	}
	
	//Server specific methods - End
}
