/**********************************************************************
* Filename: Member.java
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

public class MemberAccess implements ModelMusts
{
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public MemberAccess()
	{
	}
	
	private transient static MemberAccess memberAccess = new MemberAccess();
	
	/**********************************************************************
	* @return the singleton instance
	**********************************************************************/
	public static MemberAccess getAccess()
	{
		return memberAccess;
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
	public synchronized void insert(Member object)
	{
		try{
			if(Database.get(Member.class, object.ID()) == null)
			{
				object.checkModelRestrictions();
				object.checkRestrictions();
				
				Transactions.getSession().store(object);
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.DATABASE, 0, null, object, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT, CommandTargetLayer.VIEW, 0, null, object, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Insert", "this Member already exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Insert", "Error ocurred while trying to save the Member");
		}
	}
	
	/**********************************************************************
	* @param the object that will be updated
	**********************************************************************/
	public synchronized void update(Member member)
	{
		try{
			Member object = Database.get(Member.class, member.ID());
			int oldID = member.ID();
			object.setID();
			object.checkModelRestrictions();
			object.checkRestrictions();
			
			Transactions.getSession().store(object);
			Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.DATABASE, oldID, member, object, null, 0, null, null));
			Transactions.AddCommand(new Command(getAccess(), CommandType.UPDATE, CommandTargetLayer.VIEW, oldID, member, object, null, 0, null, null));
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Edit", "Error ocurred while trying to save the Member");
		}
	}
	
	/**********************************************************************
	* @param the object that will be deleted
	**********************************************************************/
	public synchronized void delete(Member member)
	{
		try{
			Member object = Database.get(Member.class, member.ID());
			if(object != null)
			{
				int oldID = member.ID();
				notifyDeletion(object);
				Transactions.getSession().delete(object);
				
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.DATABASE, oldID, object, null, null, 0, null, null));
				Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE, CommandTargetLayer.VIEW, oldID, object, null, null, 0, null, null));
			}else{
				Transactions.CancelTransaction("Failed in Delete", "this Member does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in Delete", "Error ocurred while trying to delete the Member");
		}
	}
	
	/**********************************************************************
	* @param the object that will receive a new association
	* @param the object that will be added as new association (neighbor)
	**********************************************************************/
	public synchronized void insertAssociation(Member member, Object neighbor, String AssociationID)
	{
		try{
			Member object = Database.get(Member.class, member.ID());
			if(object != null)
			{
				int oldID = member.ID();
				if(neighbor instanceof Worker)
				{
					object.setMembers((Worker) neighbor);
					object.setID();
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, member, object, Worker.class, ((Worker) neighbor).ID(), null, neighbor));
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, member, object, Worker.class, ((Worker) neighbor).ID(), null, neighbor));
				}
				if(neighbor instanceof Project)
				{
					object.setProjects((Project) neighbor);
					object.setID();
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, member, object, Project.class, ((Project) neighbor).ID(), null, neighbor));
					Transactions.AddCommand(new Command(getAccess(), CommandType.INSERT_ASSOCIATION, CommandTargetLayer.VIEW, oldID, member, object, Project.class, ((Project) neighbor).ID(), null, neighbor));
				}
				if((neighbor instanceof Worker || neighbor instanceof Project) && Database.get(Member.class, object.ID()) != null){
					Transactions.CancelTransaction("Failed in InsertAssociation", "this Member already exists");
				}
				else
				{
					object.checkModelRestrictions();
					object.checkRestrictions();
					Transactions.getSession().store(object);
				}
			}else{
				Transactions.CancelTransaction("Failed in InsertAssociation", "the Member does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in InsertAssociation", "Error ocurred while trying to associate the Member");
		}
	}
	
	/**********************************************************************
	* @param the object that will remove an old association
	* @param the object that will be removed as the old association (neighbor)
	**********************************************************************/
	public synchronized void deleteAssociation(Member member, Object neighbor, String AssociationID)
	{
		try{
			Member object = Database.get(Member.class, member.ID());
			if(object != null)
			{
				int oldID = member.ID();
				if(neighbor instanceof Worker)
				{
					object.setMembers(null);
					object.setID();
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, member, object, Worker.class, ((Worker) neighbor).ID(), neighbor, null));
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, member, object, Worker.class, ((Worker) neighbor).ID(), neighbor, null));
				}
				if(neighbor instanceof Project)
				{
					object.setProjects(null);
					object.setID();
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.DATABASE, oldID, member, object, Project.class, ((Project) neighbor).ID(), neighbor, null));
					Transactions.AddCommand(new Command(getAccess(), CommandType.DELETE_ASSOCIATION, CommandTargetLayer.VIEW, oldID, member, object, Project.class, ((Project) neighbor).ID(), neighbor, null));
				}
				object.checkModelRestrictions();
				object.checkRestrictions();
				Transactions.getSession().store(object);
			}else{
				Transactions.CancelTransaction("Failed in DeleteAssociation", "the Member does not exists");
			}
		}catch(Exception e){
			Transactions.CancelTransaction("Failed in DeleteAssociation", "Error ocurred while trying to delete the association of Member");
		}
	}
	
	/**********************************************************************
	* @param the object that will be removed
	**********************************************************************/
	public synchronized void notifyDeletion(Member member)
	{
		if(member.members() != null)
			member.members().deleteAssociation(member, "members_MemberAssociation");
		if(member.projects() != null)
			member.projects().deleteAssociation(member, "projects_MemberAssociation");
	}
	
	/**********************************************************************
	* @param the object to be identified
	* @return the ID of the given object
	**********************************************************************/
	@Override
	public int ID(Object member)
	{
		return ((Member) member).ID();
	}
	
	/**********************************************************************
	* @return the type of the object which this class works with
	**********************************************************************/
	@Override
	public Class<?> getType()
	{
		return Member.class;
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
		if(object instanceof Member){
			Member x = new Member(null, null, ((Member) object).endDate(), ((Member) object).startDate());
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
		if(oldObject instanceof Member && newObject instanceof Member){
			((Member) oldObject).setEndDate(((Member) newObject).endDate());
			((Member) oldObject).setStartDate(((Member) newObject).startDate());
			((Member) oldObject).setID();
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
		if(oldObject != null && newNeibor != null && oldObject instanceof Member && newNeibor instanceof Worker)
		{
			boolean exists = false;
			if(((Member) oldObject).members() != null && ((Member) oldObject).members().ID() == ((Worker) newNeibor).ID())
				exists = true;
			if(!exists)
			{
				((Member) oldObject).setMembers((Worker) newNeibor);
				((Member) oldObject).setID();
				((Member) oldObject).checkModelRestrictions();
				((Member) oldObject).checkRestrictions();
			}
		}
		if(oldObject != null && newNeibor != null && oldObject instanceof Member && newNeibor instanceof Project)
		{
			boolean exists = false;
			if(((Member) oldObject).projects() != null && ((Member) oldObject).projects().ID() == ((Project) newNeibor).ID())
				exists = true;
			if(!exists)
			{
				((Member) oldObject).setProjects((Project) newNeibor);
				((Member) oldObject).setID();
				((Member) oldObject).checkModelRestrictions();
				((Member) oldObject).checkRestrictions();
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
		if(oldObject != null && oldNeibor != null && oldObject instanceof Member && oldNeibor instanceof Worker)
		{
			boolean exists = false;
			if(((Member) oldObject).members() != null && ((Member) oldObject).members().ID() == ((Worker) oldNeibor).ID())
				exists = true;
			if(exists)
			{
				((Member) oldObject).setMembers(null);
			}
		}
		if(oldObject != null && oldNeibor != null && oldObject instanceof Member && oldNeibor instanceof Project)
		{
			boolean exists = false;
			if(((Member) oldObject).projects() != null && ((Member) oldObject).projects().ID() == ((Project) oldNeibor).ID())
				exists = true;
			if(exists)
			{
				((Member) oldObject).setProjects(null);
			}
		}
	}
	
	//Server specific methods - End
}
