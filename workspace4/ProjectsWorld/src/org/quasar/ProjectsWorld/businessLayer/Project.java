/**********************************************************************
* Filename: Project.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.businessLayer;

import org.quasar.ProjectsWorld.persistenceLayer.Database;
import org.quasar.ProjectsWorld.utils.Utils;
import org.quasar.ProjectsWorld.utils.ModelContracts;

import java.util.Set;
import java.io.Serializable;
import java.util.HashSet;

public class Project implements Comparable<Object>
{
	
	/***********************************************************
	* @return all instances of class Project
	***********************************************************/
	public static Set<Project> allInstances(){
		return Database.allInstances(Project.class);
	}
	
	private Set<Qualification> requirements =  new HashSet<Qualification>();
	protected Company company;
	protected int ID;
	protected int months;
	protected String name;
	protected String size;
	protected String status;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public Project()
	{
	}
	
	/**********************************************************************
	* Parameterized constructor
	* @param requirements the requirements to initialize
	* @param company the company to initialize
	* @param ID the ID to initialize
	* @param months the months to initialize
	* @param name the name to initialize
	* @param size the size to initialize
	* @param status the status to initialize
	**********************************************************************/
	public Project(Set<Qualification> requirements, Company company, int ID, int months, String name, ProjectSize size, ProjectStatus status)
	{
		this.requirements = requirements;
		this.company = company;
		this.ID = ID;
		this.months = months;
		this.name = name;
		this.size = size.name();
		this.status = status.name();
	}
	
	/**********************************************************************
	* Parameterized Attribute constructor
	* @param months the months to initialize
	* @param name the name to initialize
	* @param size the size to initialize
	* @param status the status to initialize
	**********************************************************************/
	public Project(int months, String name, ProjectSize size, ProjectStatus status)
	{
		this.ID = Utils.generateMD5Id(new Object[]{"Project",name, size, status, months});
		this.months = months;
		this.name = name;
		this.size = size.name();
		this.status = status.name();
	}
	
	/**********************************************************************
	* MANY2MANY getter for Project[*] <-> Set(Qualification)[*]
	* @return the requirements of the project
	**********************************************************************/
	public Set<Qualification> requirements()
	{
		return requirements;
	}
	
	/**********************************************************************
	* MANY2MANY setter for Project[*] <-> Set(Qualification)[*]
	* @param requirements the requirements to set
	**********************************************************************/
	public void setRequirements(Set<Qualification> requirements)
	{
		this.requirements = requirements;
	}
	
	/**********************************************************************
	* MANY2MANY single setter for Project[*] <-> Set(Qualification)[*]
	* @param qualification the qualification to add
	**********************************************************************/
	public void addRequirements(Qualification qualification)
	{
		this.requirements.add(qualification);
	}
	
	/**********************************************************************
	* MANY2MANY single remover for Project[*] <-> Set(Qualification)[*]
	* @param qualification the qualification to remove
	**********************************************************************/
	public void removeRequirements(Qualification qualification)
	{
		this.requirements.remove(qualification);
	}
	
	/**********************************************************************
	* ONE2MANY getter for Project[*] <-> Company[1]
	* @return the company of the project
	**********************************************************************/
	public Company company()
	{
		return company;
	}
	
	/**********************************************************************
	* ONE2MANY setter for Project[*] <-> Company[1]
	* @param company the company to set
	**********************************************************************/
	public void setCompany(Company company)
	{
		this.company = company;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the ID of the project
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
		this.ID = Utils.generateMD5Id(new Object[]{"Project",name, size, status, months});
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the months of the project
	**********************************************************************/
	public int months()
	{
		return months;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param months the months to set
	**********************************************************************/
	public void setMonths(int months)
	{
		this.months = months;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the name of the project
	**********************************************************************/
	public String name()
	{
		return name;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param name the name to set
	**********************************************************************/
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the size of the project
	**********************************************************************/
	public ProjectSize size()
	{
		return ProjectSize.valueOf(size);
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param size the size to set
	**********************************************************************/
	public void setSize(ProjectSize size)
	{
		this.size = size.name();
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the status of the project
	**********************************************************************/
	public ProjectStatus status()
	{
		return ProjectStatus.valueOf(status);
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param status the status to set
	**********************************************************************/
	public void setStatus(ProjectStatus status)
	{
		this.status = status.name();
	}
	
	/**********************************************************************
	* MEMBER2ASSOCIATIVE getter for Project[*] <-> Member[1..*]
	* @return the member of the projects
	**********************************************************************/
	public Set<Member> member()
	{
		Set<Member> result = new HashSet<Member>();
		for (Member x : Member.allInstances())
			if (x.projects()  ==  this)
				result.add(x);
		return result;
	}
	
	/**********************************************************************
	* MEMBER2ASSOCIATIVE setter for Project[*] <-> Member[1..*]
	* @param member the member to set
	**********************************************************************/
	public void setMember(Set<Member> member)
	{
		for (Member x : member)
			x.setProjects(this);
	}
	
	/**********************************************************************
	* MEMBER2MEMBER getter for Project[*] <-> Worker[1..*]
	* @return the members of the projects
	**********************************************************************/
	public Set<Worker> members()
	{
		Set<Worker> result = new HashSet<Worker>();
		for (Member x : Member.allInstances())
			if (x.projects()  ==  this && x. members() != null)
				result.add(x.members());
		return result;
	}
	
	/**********************************************************************
	* MEMBER2MEMBER setter for Project[*] <-> Worker[1..*]
	* @param members the members to set
	**********************************************************************/
	public void setMembers(Set<Worker> members)
	{
		for (Worker t : members)
			for (Member x : Member.allInstances())
				if (x.projects() == this)
					x.setMembers(t);
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
	
	private boolean validMember;
	
	/**********************************************************************
	* MEMBER2ASSOCIATIVE state getter for Project[*] <-> Member[1..*]
	* @return the state regarding the mandatory association member
	**********************************************************************/
	public boolean validMember()
	{
		return validMember;
	}
	
	/**********************************************************************
	* MEMBER2ASSOCIATIVE state setter for Project[*] <-> Member[1..*]
	* @param the new state regarding the mandatory association member
	**********************************************************************/
	public void setValidMember(boolean validMember)
	{
		this.validMember= validMember;
	}
	
	private boolean validMembers;
	
	/**********************************************************************
	* MEMBER2MEMBER state getter for Project[*] <-> Worker[1..*]
	* @return the state regarding the mandatory association members
	**********************************************************************/
	public boolean validMembers()
	{
		return validMembers;
	}
	
	/**********************************************************************
	* MEMBER2MEMBER state setter for Project[*] <-> Worker[1..*]
	* @param the new state regarding the mandatory association members
	**********************************************************************/
	public void setValidMembers(boolean validMembers)
	{
		this.validMembers= validMembers;
	}
	
	private boolean validRequirements;
	
	/**********************************************************************
	* MANY2MANY state getter for Project[*] <-> Qualification[1..*]
	* @return the state regarding the mandatory association requirements
	**********************************************************************/
	public boolean validRequirements()
	{
		return validRequirements;
	}
	
	/**********************************************************************
	* MANY2MANY state setter for Project[*] <-> Qualification[1..*]
	* @param the new state regarding the mandatory association requirements
	**********************************************************************/
	public void setValidRequirements(boolean validRequirements)
	{
		this.validRequirements= validRequirements;
	}
	
	private boolean validCompany;
	
	/**********************************************************************
	* ONE2MANY state getter for Project[*] <-> Company[1]
	* @return the state regarding the mandatory association company
	**********************************************************************/
	public boolean validCompany()
	{
		return validCompany;
	}
	
	/**********************************************************************
	* ONE2MANY state setter for Project[*] <-> Company[1]
	* @param the new state regarding the mandatory association company
	**********************************************************************/
	public void setValidCompany(boolean validCompany)
	{
		this.validCompany= validCompany;
	}
	
	/**********************************************************************
	* association state setter
	**********************************************************************/
	public void checkModelRestrictions()
	{
		setValidMember(ModelContracts.Check(member(), 1, -1));
		setValidMembers(ModelContracts.Check(members(), 1, -1));
		setValidRequirements(ModelContracts.Check(requirements(), 1, -1));
		setValidCompany(ModelContracts.Check(company(), 1,1));
	}
	
	/**********************************************************************
	* general association state setter
	**********************************************************************/
	public void checkRestrictions()
	{
		if(validMember() && validMembers() && validRequirements() && validCompany())
			setAssociationRestrictionsValid(true);
		else
			setAssociationRestrictionsValid(false);
	}
	
	/**********************************************************************
	* general association state setter
	* @return a singleton instance to access the controll methods
	**********************************************************************/
	public static ProjectAccess getAccess()
	{
		return ProjectAccess.getAccess();
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
	public static Project getProject(int ID)
	{
		return Database.get(Project.class, ID);
	}
	
	/**********************************************************************
	* @return the type Class
	**********************************************************************/
	public Class<?> getType()
	{
		return Project.class;
	}
	
	/**********************************************************************
	* Object serializer
	**********************************************************************/
	public String toString()
	{
		return "Project [requirements=" + requirements + ", company=" + company + ", ID=" + ID + ", months=" + months + ", name=" + name + ", size=" + size + ", status=" + status + "]";
	}
	
	/**********************************************************************
	* @param other Project to compare to the current one
	* @return
	**********************************************************************/
	public int compareTo(Object other)
	{
		return this.hashCode() - ((Project) other).hashCode();
	}
	
}
