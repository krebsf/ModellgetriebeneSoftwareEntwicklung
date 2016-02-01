/**********************************************************************
* Filename: Worker.java
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

public class WorkerAccess implements ModelMusts
{
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public WorkerAccess()
	{
	}
	
	private transient static WorkerAccess workerAccess = new WorkerAccess();
	
	/**********************************************************************
	* @return the singleton instance
	**********************************************************************/
	public static WorkerAccess getAccess()
	{
		return workerAccess;
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
	public synchronized void insert(Worker object)
	{
		try{
			if(Database.get(Worker.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.DATABASE, 0, null, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.VIEW, 0, null, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Insert", "this Worker already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Insert", "Error ocurred while trying to save the Worker");
		}
	}
	
	/**********************************************************************
	* @param the object that will be updated
	**********************************************************************/
	public synchronized void update(Worker worker)
	{
		try{
			Worker object = Database.get(Worker.class, worker.ID());
			int oldID = worker.ID();
			object.setID();
			if(Database.get(Worker.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.DATABASE, oldID, worker, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.VIEW, oldID, worker, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Edit", "this Worker already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Edit", "Error ocurred while trying to save the Worker");
		}
	}
	
	/**********************************************************************
	* @param the object that will be deleted
	**********************************************************************/
	public synchronized void delete(Worker worker)
	{
		try{
			Worker object = Database.get(Worker.class, worker.ID());
			if(object != null)
			{
				int oldID = worker.ID();
				notifyDeletion(object);
				Transactions.getSession().delete(object);
				
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.DATABASE, oldID, object, null, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.VIEW, oldID, object, null, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Delete", "this Worker does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Delete", "Error ocurred while trying to delete the Worker");
		}
	}
	
	/**********************************************************************
	* @param the object that will receive a new association
	* @param the object that will be added as new association (neighbor)
	**********************************************************************/
	public synchronized void insertAssociation(Worker worker, Object neighbor, String AssociationID)
	{
		try{
			Worker object = Database.get(Worker.class, worker.ID());
			if(object != null)
			{
				int oldID = worker.ID();
				if(neighbor instanceof Qualification)
				{
					object.addQualifications((Qualification) neighbor);
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, worker, object, Qualification.class, ((Qualification) neighbor).ID(), null, neighbor));
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, worker, object, Qualification.class, ((Qualification) neighbor).ID(), null, neighbor));
				}
				if(neighbor instanceof Company)
				{
					object.setEmployer((Company) neighbor);
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, worker, object, Company.class, ((Company) neighbor).ID(), null, neighbor));
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, worker, object, Company.class, ((Company) neighbor).ID(), null, neighbor));
				}
				if(neighbor instanceof Member)
				{
					if(((Member) neighbor).members() != object)
						((Member) neighbor).insertAssociation(object, AssociationID);
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, worker, object, Member.class, ((Member) neighbor).ID(), null, neighbor));
				}
				if(neighbor instanceof Project)
				{
					if(((Project) neighbor).member() != null)
					{
						((Member) ((Project) neighbor).member()).insertAssociation(object, AssociationID);
						Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, worker, object, Project.class, ((Project) neighbor).ID(), null, neighbor));
					}
				}
				object.checkModelRestrictions();
				object.checkRestrictions();
				Transactions.getSession().store(object);
			}else{
				Transactions.CancelTransaction("Failed in InsertAssociation", "the Worker does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in InsertAssociation", "Error ocurred while trying to associate the Worker");
		}
	}
	
	/**********************************************************************
	* @param the object that will remove an old association
	* @param the object that will be removed as the old association (neighbor)
	**********************************************************************/
	public synchronized void deleteAssociation(Worker worker, Object neighbor, String AssociationID)
	{
		try{
			Worker object = Database.get(Worker.class, worker.ID());
			if(object != null)
			{
				int oldID = worker.ID();
				if(neighbor instanceof Qualification)
				{
					object.removeQualifications((Qualification) neighbor);
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, worker, object, Qualification.class, ((Qualification) neighbor).ID(), neighbor, null));
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, worker, object, Qualification.class, ((Qualification) neighbor).ID(), neighbor, null));
				}
				if(neighbor instanceof Company)
				{
					object.setEmployer(null);
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, worker, object, Company.class, ((Company) neighbor).ID(), neighbor, null));
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, worker, object, Company.class, ((Company) neighbor).ID(), neighbor, null));
				}
				if(neighbor instanceof Member)
				{
					((Member) neighbor).deleteAssociation(object, AssociationID);
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, worker, object, Member.class, ((Member) neighbor).ID(), neighbor, null));
				}
				if(neighbor instanceof Project)
				{
					if(((Project) neighbor).member() != null)
					{
						((Member) ((Project) neighbor).member()).deleteAssociation(object, AssociationID);
						Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, worker, object, null, 0, null, neighbor));
					}
				}
				object.checkModelRestrictions();
				object.checkRestrictions();
				Transactions.getSession().store(object);
			}else{
				Transactions.CancelTransaction("Failed in DeleteAssociation", "the Worker does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in DeleteAssociation", "Error ocurred while trying to delete the association of Worker");
		}
	}
	
	/**********************************************************************
	* @param the object that will be removed
	**********************************************************************/
	public synchronized void notifyDeletion(Worker worker)
	{
		for(Qualification x : worker.qualifications())
			x.deleteAssociation(worker, "IsQualifiedAssociation");
		if(worker.employer() != null)
			worker.employer().deleteAssociation(worker, "EmploysAssociation");
		for(Member x : worker.member())
			x.deleteAssociation(worker, "members_MemberAssociation");
	}
	
	/**********************************************************************
	* @param the object to be identified
	* @return the ID of the given object
	**********************************************************************/
	@Override
	public int ID(Object worker)
	{
		return ((Worker) worker).ID();
	}
	
	/**********************************************************************
	* @return the type of the object which this class works with
	**********************************************************************/
	@Override
	public Class<?> getType()
	{
		return Worker.class;
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
		if(neibor instanceof Company)
			return ((Company) neibor).ID();
		if(neibor instanceof Member)
			return ((Member) neibor).ID();
		if(neibor instanceof Project)
			return ((Project) neibor).ID();
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
		if(neibor instanceof Company)
			return Company.class;
		if(neibor instanceof Member)
			return Member.class;
		if(neibor instanceof Project)
			return Project.class;
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
		if(object instanceof Worker){
			Worker x = new Worker(((Worker) object).nickname(), ((Worker) object).salary());
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
		if(oldObject instanceof Worker && newObject instanceof Worker){
			((Worker) oldObject).setNickname(((Worker) newObject).nickname());
			((Worker) oldObject).setSalary(((Worker) newObject).salary());
			((Worker) oldObject).setID();
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
		if(oldObject != null && newNeibor != null && oldObject instanceof Worker && newNeibor instanceof Qualification)
		{
			boolean exists = false;
			for(Qualification x : ((Worker) oldObject).qualifications())
				if(x.ID() == ((Qualification) newNeibor).ID())
					exists = true;
			if(!exists)
			{
				((Worker) oldObject).addQualifications((Qualification) newNeibor);
				((Worker) oldObject).checkModelRestrictions();
				((Worker) oldObject).checkRestrictions();
			}
		}
		if(oldObject != null && newNeibor != null && oldObject instanceof Worker && newNeibor instanceof Company)
		{
			boolean exists = false;
			if(((Worker) oldObject).employer() != null && ((Worker) oldObject).employer().ID() == ((Company) newNeibor).ID())
				exists = true;
			if(!exists)
			{
				((Worker) oldObject).setEmployer((Company) newNeibor);
				((Worker) oldObject).checkModelRestrictions();
				((Worker) oldObject).checkRestrictions();
			}
		}
		
		//I'm not the holder class regarding the association between me and Member
		//i do not need to have code for this association insertion since the holder has it and 
		//and the proper command was added when this action was made locally
		
		//I'm not the holder class regarding the association between me and Project
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
		if(oldObject != null && oldNeibor != null && oldObject instanceof Worker && oldNeibor instanceof Qualification)
		{
			boolean exists = false;
			for(Qualification x : ((Worker) oldObject).qualifications())
				if(x.ID() == ((Qualification) oldNeibor).ID())
					exists = true;
			if(exists)
			{
				((Worker) oldObject).removeQualifications((Qualification) oldNeibor);
			}
		}
		if(oldObject != null && oldNeibor != null && oldObject instanceof Worker && oldNeibor instanceof Company)
		{
			boolean exists = false;
			if(((Worker) oldObject).employer() != null && ((Worker) oldObject).employer().ID() == ((Company) oldNeibor).ID())
				exists = true;
			if(exists)
			{
				((Worker) oldObject).setEmployer(null);
			}
		}
		
		//I'm not the holder class regarding the association between me and Member
		//i do not need to have code for this association deletion since the holder has it 
		//and the proper command was added when this action was made locally
		
		//I'm not the holder class regarding the association between me and Project
		//i do not need to have code for this association deletion since the holder has it 
		//and the proper command was added when this action was made locally
	}
	
	//Server specific methods - End
}
