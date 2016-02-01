/**********************************************************************
* Filename: Project.java
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

public class ProjectAccess implements ModelMusts
{
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public ProjectAccess()
	{
	}
	
	private transient static ProjectAccess projectAccess = new ProjectAccess();
	
	/**********************************************************************
	* @return the singleton instance
	**********************************************************************/
	public static ProjectAccess getAccess()
	{
		return projectAccess;
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
	public synchronized void insert(Project object)
	{
		try{
			if(Database.get(Project.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.DATABASE, 0, null, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.VIEW, 0, null, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Insert", "this Project already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Insert", "Error ocurred while trying to save the Project");
		}
	}
	
	/**********************************************************************
	* @param the object that will be updated
	**********************************************************************/
	public synchronized void update(Project project)
	{
		try{
			Project object = Database.get(Project.class, project.ID());
			int oldID = project.ID();
			object.setID();
			if(Database.get(Project.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.DATABASE, oldID, project, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.VIEW, oldID, project, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Edit", "this Project already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Edit", "Error ocurred while trying to save the Project");
		}
	}
	
	/**********************************************************************
	* @param the object that will be deleted
	**********************************************************************/
	public synchronized void delete(Project project)
	{
		try{
			Project object = Database.get(Project.class, project.ID());
			if(object != null)
			{
				int oldID = project.ID();
				notifyDeletion(object);
				Transactions.getSession().delete(object);
				
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.DATABASE, oldID, object, null, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.VIEW, oldID, object, null, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Delete", "this Project does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Delete", "Error ocurred while trying to delete the Project");
		}
	}
	
	/**********************************************************************
	* @param the object that will receive a new association
	* @param the object that will be added as new association (neighbor)
	**********************************************************************/
	public synchronized void insertAssociation(Project project, Object neighbor, String AssociationID)
	{
		try{
			Project object = Database.get(Project.class, project.ID());
			if(object != null)
			{
				int oldID = project.ID();
				if(neighbor instanceof Member)
				{
					if(((Member) neighbor).projects() != object)
						((Member) neighbor).insertAssociation(object, AssociationID);
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, project, object, Member.class, ((Member) neighbor).ID(), null, neighbor));
				}
				if(neighbor instanceof Worker)
				{
					if(((Worker) neighbor).member() != null)
					{
						((Member) ((Worker) neighbor).member()).insertAssociation(object, AssociationID);
						Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, project, object, Worker.class, ((Worker) neighbor).ID(), null, neighbor));
					}
				}
				if(neighbor instanceof Qualification)
				{
					object.addRequirements((Qualification) neighbor);
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, project, object, Qualification.class, ((Qualification) neighbor).ID(), null, neighbor));
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, project, object, Qualification.class, ((Qualification) neighbor).ID(), null, neighbor));
				}
				if(neighbor instanceof Company)
				{
					object.setCompany((Company) neighbor);
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, project, object, Company.class, ((Company) neighbor).ID(), null, neighbor));
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, project, object, Company.class, ((Company) neighbor).ID(), null, neighbor));
				}
				object.checkModelRestrictions();
				object.checkRestrictions();
				Transactions.getSession().store(object);
			}else{
				Transactions.CancelTransaction("Failed in InsertAssociation", "the Project does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in InsertAssociation", "Error ocurred while trying to associate the Project");
		}
	}
	
	/**********************************************************************
	* @param the object that will remove an old association
	* @param the object that will be removed as the old association (neighbor)
	**********************************************************************/
	public synchronized void deleteAssociation(Project project, Object neighbor, String AssociationID)
	{
		try{
			Project object = Database.get(Project.class, project.ID());
			if(object != null)
			{
				int oldID = project.ID();
				if(neighbor instanceof Member)
				{
					((Member) neighbor).deleteAssociation(object, AssociationID);
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, project, object, Member.class, ((Member) neighbor).ID(), neighbor, null));
				}
				if(neighbor instanceof Worker)
				{
					if(((Worker) neighbor).member() != null)
					{
						((Member) ((Worker) neighbor).member()).deleteAssociation(object, AssociationID);
						Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, project, object, null, 0, null, neighbor));
					}
				}
				if(neighbor instanceof Qualification)
				{
					object.removeRequirements((Qualification) neighbor);
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, project, object, Qualification.class, ((Qualification) neighbor).ID(), neighbor, null));
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, project, object, Qualification.class, ((Qualification) neighbor).ID(), neighbor, null));
				}
				if(neighbor instanceof Company)
				{
					object.setCompany(null);
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, project, object, Company.class, ((Company) neighbor).ID(), neighbor, null));
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, project, object, Company.class, ((Company) neighbor).ID(), neighbor, null));
				}
				object.checkModelRestrictions();
				object.checkRestrictions();
				Transactions.getSession().store(object);
			}else{
				Transactions.CancelTransaction("Failed in DeleteAssociation", "the Project does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in DeleteAssociation", "Error ocurred while trying to delete the association of Project");
		}
	}
	
	/**********************************************************************
	* @param the object that will be removed
	**********************************************************************/
	public synchronized void notifyDeletion(Project project)
	{
		for(Member x : project.member())
			x.deleteAssociation(project, "projects_MemberAssociation");
		for(Qualification x : project.requirements())
			x.deleteAssociation(project, "RequiresAssociation");
		if(project.company() != null)
			project.company().deleteAssociation(project, "CarriesOutAssociation");
	}
	
	/**********************************************************************
	* @param the object to be identified
	* @return the ID of the given object
	**********************************************************************/
	@Override
	public int ID(Object project)
	{
		return ((Project) project).ID();
	}
	
	/**********************************************************************
	* @return the type of the object which this class works with
	**********************************************************************/
	@Override
	public Class<?> getType()
	{
		return Project.class;
	}
	
	/**********************************************************************
	* @param the neibor object to be identified
	* @return the ID of the given neibor
	**********************************************************************/
	@Override
	public int getNeiborID(Object neibor)
	{
		if(neibor instanceof Member)
			return ((Member) neibor).ID();
		if(neibor instanceof Worker)
			return ((Worker) neibor).ID();
		if(neibor instanceof Qualification)
			return ((Qualification) neibor).ID();
		if(neibor instanceof Company)
			return ((Company) neibor).ID();
		return 0;
	}
	
	/**********************************************************************
	* @param the neibor object which this class can work with
	* @return the type of the neibor which this class can work with
	**********************************************************************/
	@Override
	public Class<?> getNeiborType(Object neibor)
	{
		if(neibor instanceof Member)
			return Member.class;
		if(neibor instanceof Worker)
			return Worker.class;
		if(neibor instanceof Qualification)
			return Qualification.class;
		if(neibor instanceof Company)
			return Company.class;
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
		if(object instanceof Project){
			Project x = new Project(((Project) object).months(), ((Project) object).name(), ((Project) object).size(), ((Project) object).status());
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
		if(oldObject instanceof Project && newObject instanceof Project){
			((Project) oldObject).setMonths(((Project) newObject).months());
			((Project) oldObject).setName(((Project) newObject).name());
			((Project) oldObject).setSize(((Project) newObject).size());
			((Project) oldObject).setStatus(((Project) newObject).status());
			((Project) oldObject).setID();
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
		
		//I'm not the holder class regarding the association between me and Member
		//i do not need to have code for this association insertion since the holder has it and 
		//and the proper command was added when this action was made locally
		
		//I'm not the holder class regarding the association between me and Worker
		//i do not need to have code for this association insertion since the holder has it and 
		//and the proper command was added when this action was made locally
		if(oldObject != null && newNeibor != null && oldObject instanceof Project && newNeibor instanceof Qualification)
		{
			boolean exists = false;
			for(Qualification x : ((Project) oldObject).requirements())
				if(x.ID() == ((Qualification) newNeibor).ID())
					exists = true;
			if(!exists)
			{
				((Project) oldObject).addRequirements((Qualification) newNeibor);
				((Project) oldObject).checkModelRestrictions();
				((Project) oldObject).checkRestrictions();
			}
		}
		if(oldObject != null && newNeibor != null && oldObject instanceof Project && newNeibor instanceof Company)
		{
			boolean exists = false;
			if(((Project) oldObject).company() != null && ((Project) oldObject).company().ID() == ((Company) newNeibor).ID())
				exists = true;
			if(!exists)
			{
				((Project) oldObject).setCompany((Company) newNeibor);
				((Project) oldObject).checkModelRestrictions();
				((Project) oldObject).checkRestrictions();
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
		
		//I'm not the holder class regarding the association between me and Member
		//i do not need to have code for this association deletion since the holder has it 
		//and the proper command was added when this action was made locally
		
		//I'm not the holder class regarding the association between me and Worker
		//i do not need to have code for this association deletion since the holder has it 
		//and the proper command was added when this action was made locally
		if(oldObject != null && oldNeibor != null && oldObject instanceof Project && oldNeibor instanceof Qualification)
		{
			boolean exists = false;
			for(Qualification x : ((Project) oldObject).requirements())
				if(x.ID() == ((Qualification) oldNeibor).ID())
					exists = true;
			if(exists)
			{
				((Project) oldObject).removeRequirements((Qualification) oldNeibor);
			}
		}
		if(oldObject != null && oldNeibor != null && oldObject instanceof Project && oldNeibor instanceof Company)
		{
			boolean exists = false;
			if(((Project) oldObject).company() != null && ((Project) oldObject).company().ID() == ((Company) oldNeibor).ID())
				exists = true;
			if(exists)
			{
				((Project) oldObject).setCompany(null);
			}
		}
	}
	
	//Server specific methods - End
}
