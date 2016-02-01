/**********************************************************************
* Filename: Member.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.businessLayer;

import org.quasar.ProjectsWorld.persistenceLayer.Database;
import org.quasar.ProjectsWorld.utils.Utils;
import org.quasar.ProjectsWorld.utils.ModelContracts;

import java.util.Set;
import java.io.Serializable;

public class Member implements Comparable<Object>
{
	
	/***********************************************************
	* @return all instances of class Member
	***********************************************************/
	public static Set<Member> allInstances(){
		return Database.allInstances(Member.class);
	}
	
	private Project projects;
	private Worker members;
	private int ID;
	private CalendarDate endDate;
	private CalendarDate startDate;
	
	/**********************************************************************
	* Associative constructor
	* @param projects the projects to initialize
	* @param members the members to initialize
	**********************************************************************/
	public Member(Project projects, Worker members)
	{
		this.projects = projects;
		this.members = members;
		this.ID = Utils.generateMD5Id(new Object[]{projects != null ? projects.ID() : null,members != null ? members.ID() : null});
	}
	
	/**********************************************************************
	* Parameterized constructor
	* @param projects the projects to initialize
	* @param members the members to initialize
	* @param ID the ID to initialize
	* @param endDate the endDate to initialize
	* @param startDate the startDate to initialize
	**********************************************************************/
	public Member(Project projects, Worker members, int ID, CalendarDate endDate, CalendarDate startDate)
	{
		this.projects = projects;
		this.members = members;
		this.ID = ID;
		this.endDate = endDate;
		this.startDate = startDate;
	}
	
	/**********************************************************************
	* Parameterized Attribute constructor
	* @param projects the projects to initialize
	* @param members the members to initialize
	* @param endDate the endDate to initialize
	* @param startDate the startDate to initialize
	**********************************************************************/
	public Member(Project projects, Worker members, CalendarDate endDate, CalendarDate startDate)
	{
		this.projects = projects;
		this.members = members;
		this.ID = Utils.generateMD5Id(new Object[]{"Member",projects != null ? projects.ID() : null, members != null ? members.ID() : null});
		this.endDate = endDate;
		this.startDate = startDate;
	}
	
	/**********************************************************************
	* ASSOCIATIVE2MEMBER getter for Member[*] <-> Project[1]
	* @return the projects of the member
	**********************************************************************/
	public Project projects()
	{
		return projects;
	}
	
	/**********************************************************************
	* ASSOCIATIVE2MEMBER setter for Member[*] <-> Project[1]
	* @param projects the projects to set
	**********************************************************************/
	public void setProjects(Project projects)
	{
		this.projects = projects;
	}
	
	/**********************************************************************
	* ASSOCIATIVE2MEMBER getter for Member[*] <-> Worker[1]
	* @return the members of the member
	**********************************************************************/
	public Worker members()
	{
		return members;
	}
	
	/**********************************************************************
	* ASSOCIATIVE2MEMBER setter for Member[*] <-> Worker[1]
	* @param members the members to set
	**********************************************************************/
	public void setMembers(Worker members)
	{
		this.members = members;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the ID of the member
	**********************************************************************/
	public int ID()
	{
		return ID;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param sets the ID
	**********************************************************************/
	public void setID()
	{
		this.ID = Utils.generateMD5Id(new Object[]{"Member",projects != null ? projects.ID() : null, members != null ? members.ID() : null});
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the endDate of the member
	**********************************************************************/
	public CalendarDate endDate()
	{
		return endDate;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param endDate the endDate to set
	**********************************************************************/
	public void setEndDate(CalendarDate endDate)
	{
		this.endDate = endDate;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the startDate of the member
	**********************************************************************/
	public CalendarDate startDate()
	{
		return startDate;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param startDate the startDate to set
	**********************************************************************/
	public void setStartDate(CalendarDate startDate)
	{
		this.startDate = startDate;
	}
	
	private boolean AssociationRestrictionsValid = false;
	
	/**********************************************************************
	*general association state getter 
	* @return the state regarding all mandatory associations 
	**********************************************************************/
	public boolean isAssociationRestrictionsValid()
	{
		return AssociationRestrictionsValid;
	}
	
	/**********************************************************************
	* general association state setter
	* @param the new state regarding all mandatory association 
	**********************************************************************/
	public void setAssociationRestrictionsValid(boolean AssociationRestrictionsValid)
	{
		this. AssociationRestrictionsValid= AssociationRestrictionsValid;
	}
	
	private boolean validMembers;
	
	/**********************************************************************
	* ASSOCIATIVE2MEMBER state getter for Member[*] <-> Worker[1]
	* @return the state regarding the mandatory association members
	**********************************************************************/
	public boolean validMembers()
	{
		return validMembers;
	}
	
	/**********************************************************************
	* ASSOCIATIVE2MEMBER state setter for Member[*] <-> Worker[1]
	* @param the new state regarding the mandatory association members
	**********************************************************************/
	public void setValidMembers(boolean validMembers)
	{
		this.validMembers= validMembers;
	}
	
	private boolean validProjects;
	
	/**********************************************************************
	* ASSOCIATIVE2MEMBER state getter for Member[1..*] <-> Project[1]
	* @return the state regarding the mandatory association projects
	**********************************************************************/
	public boolean validProjects()
	{
		return validProjects;
	}
	
	/**********************************************************************
	* ASSOCIATIVE2MEMBER state setter for Member[1..*] <-> Project[1]
	* @param the new state regarding the mandatory association projects
	**********************************************************************/
	public void setValidProjects(boolean validProjects)
	{
		this.validProjects= validProjects;
	}
	
	/**********************************************************************
	* association state setter
	**********************************************************************/
	public void checkModelRestrictions()
	{
		setValidMembers(ModelContracts.Check(members(), 1, -1));
		setValidProjects(ModelContracts.Check(projects(), 1, -1));
	}
	
	/**********************************************************************
	* general association state setter
	**********************************************************************/
	public void checkRestrictions()
	{
		if(validMembers() && validProjects())
			setAssociationRestrictionsValid(true);
		else
			setAssociationRestrictionsValid(false);
	}
	
	/**********************************************************************
	* general association state setter
	* @return a singleton instance to access the controll methods
	**********************************************************************/
	public static MemberAccess getAccess()
	{
		return MemberAccess.getAccess();
	}
	
	/**********************************************************************
	* insert the caller object
	**********************************************************************/
	public void insert()
	{
		getAccess().insert(this);
	}
	
	/**********************************************************************
	* update the caller object
	**********************************************************************/
	public void update()
	{
		getAccess().update(this);
	}
	
	/**********************************************************************
	* delete the caller object
	**********************************************************************/
	public void delete()
	{
		getAccess().delete(this);
	}
	
	/**********************************************************************
	* insertAssociation in the caller object
	* @param the neighbor object
	**********************************************************************/
	public void insertAssociation(Object neibor, String AssociationID)
	{
		getAccess().insertAssociation(this,neibor, AssociationID);
	}
	
	/**********************************************************************
	* deleteAssociation in the caller object
	* @param the neighbor object
	**********************************************************************/
	public void deleteAssociation(Object neibor, String AssociationID)
	{
		getAccess().deleteAssociation(this,neibor, AssociationID);
	}
	
	/**********************************************************************
	* @param An ID
	* @return the object with the given ID or null
	**********************************************************************/
	public static Member getMember(int ID)
	{
		return Database.get(Member.class, ID);
	}
	
	/**********************************************************************
	* @return the type Class
	**********************************************************************/
	public Class<?> getType()
	{
		return Member.class;
	}
	
	/**********************************************************************
	* Object serializer
	**********************************************************************/
	public String toString()
	{
		return "Member [projects=" + projects + ", members=" + members + ", ID=" + ID + ", endDate=" + endDate + ", startDate=" + startDate + "]";
	}
	
	/**********************************************************************
	* @param other Member to compare to the current one
	* @return
	**********************************************************************/
	public int compareTo(Object other)
	{
		return this.hashCode() - ((Member) other).hashCode();
	}
	
}
