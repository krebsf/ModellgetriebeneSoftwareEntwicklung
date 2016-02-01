/**********************************************************************
* Filename: Worker.java
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

public class Worker implements Comparable<Object>
{
	
	/***********************************************************
	* @return all instances of class Worker
	***********************************************************/
	public static Set<Worker> allInstances(){
		return Database.allInstances(Worker.class);
	}
	
	private Set<Qualification> qualifications =  new HashSet<Qualification>();
	private Company employer;
	private int ID;
	private String nickname;
	private int salary;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public Worker()
	{
	}
	
	/**********************************************************************
	* Parameterized constructor
	* @param qualifications the qualifications to initialize
	* @param employer the employer to initialize
	* @param ID the ID to initialize
	* @param nickname the nickname to initialize
	* @param salary the salary to initialize
	**********************************************************************/
	public Worker(Set<Qualification> qualifications, Company employer, int ID, String nickname, int salary)
	{
		this.qualifications = qualifications;
		this.employer = employer;
		this.ID = ID;
		this.nickname = nickname;
		this.salary = salary;
	}
	
	/**********************************************************************
	* Parameterized Attribute constructor
	* @param nickname the nickname to initialize
	* @param salary the salary to initialize
	**********************************************************************/
	public Worker(String nickname, int salary)
	{
		this.ID = Utils.generateMD5Id(new Object[]{"Worker",nickname, salary});
		this.nickname = nickname;
		this.salary = salary;
	}
	
	/**********************************************************************
	* MANY2MANY getter for Worker[*] <-> Set(Qualification)[*]
	* @return the qualifications of the worker
	**********************************************************************/
	public Set<Qualification> qualifications()
	{
		return qualifications;
	}
	
	/**********************************************************************
	* MANY2MANY setter for Worker[*] <-> Set(Qualification)[*]
	* @param qualifications the qualifications to set
	**********************************************************************/
	public void setQualifications(Set<Qualification> qualifications)
	{
		this.qualifications = qualifications;
	}
	
	/**********************************************************************
	* MANY2MANY single setter for Worker[*] <-> Set(Qualification)[*]
	* @param qualification the qualification to add
	**********************************************************************/
	public void addQualifications(Qualification qualification)
	{
		this.qualifications.add(qualification);
	}
	
	/**********************************************************************
	* MANY2MANY single remover for Worker[*] <-> Set(Qualification)[*]
	* @param qualification the qualification to remove
	**********************************************************************/
	public void removeQualifications(Qualification qualification)
	{
		this.qualifications.remove(qualification);
	}
	
	/**********************************************************************
	* ONE2MANY getter for Worker[*] <-> Company[1]
	* @return the employer of the worker
	**********************************************************************/
	public Company employer()
	{
		return employer;
	}
	
	/**********************************************************************
	* ONE2MANY setter for Worker[*] <-> Company[1]
	* @param employer the employer to set
	**********************************************************************/
	public void setEmployer(Company employer)
	{
		this.employer = employer;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the ID of the worker
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
		this.ID = Utils.generateMD5Id(new Object[]{"Worker",nickname, salary});
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the nickname of the worker
	**********************************************************************/
	public String nickname()
	{
		return nickname;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param nickname the nickname to set
	**********************************************************************/
	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the salary of the worker
	**********************************************************************/
	public int salary()
	{
		return salary;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param salary the salary to set
	**********************************************************************/
	public void setSalary(int salary)
	{
		this.salary = salary;
	}
	
	/**********************************************************************
	* MEMBER2ASSOCIATIVE getter for Worker[1..*] <-> Member[*]
	* @return the member of the members
	**********************************************************************/
	public Set<Member> member()
	{
		Set<Member> result = new HashSet<Member>();
		for (Member x : Member.allInstances())
			if (x.members()  ==  this)
				result.add(x);
		return result;
	}
	
	/**********************************************************************
	* MEMBER2ASSOCIATIVE setter for Worker[1..*] <-> Member[*]
	* @param member the member to set
	**********************************************************************/
	public void setMember(Set<Member> member)
	{
		for (Member x : member)
			x.setMembers(this);
	}
	
	/**********************************************************************
	* MEMBER2MEMBER getter for Worker[1..*] <-> Project[*]
	* @return the projects of the members
	**********************************************************************/
	public Set<Project> projects()
	{
		Set<Project> result = new HashSet<Project>();
		for (Member x : Member.allInstances())
			if (x.members()  ==  this && x. projects() != null)
				result.add(x.projects());
		return result;
	}
	
	/**********************************************************************
	* MEMBER2MEMBER setter for Worker[1..*] <-> Project[*]
	* @param projects the projects to set
	**********************************************************************/
	public void setProjects(Set<Project> projects)
	{
		for (Project t : projects)
			for (Member x : Member.allInstances())
				if (x.members() == this)
					x.setProjects(t);
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
	
	private boolean validQualifications;
	
	/**********************************************************************
	* MANY2MANY state getter for Worker[*] <-> Qualification[1..*]
	* @return the state regarding the mandatory association qualifications
	**********************************************************************/
	public boolean validQualifications()
	{
		return validQualifications;
	}
	
	/**********************************************************************
	* MANY2MANY state setter for Worker[*] <-> Qualification[1..*]
	* @param the new state regarding the mandatory association qualifications
	**********************************************************************/
	public void setValidQualifications(boolean validQualifications)
	{
		this.validQualifications= validQualifications;
	}
	
	private boolean validEmployer;
	
	/**********************************************************************
	* ONE2MANY state getter for Worker[1..*] <-> Company[0..1]
	* @return the state regarding the mandatory association employer
	**********************************************************************/
	public boolean validEmployer()
	{
		return validEmployer;
	}
	
	/**********************************************************************
	* ONE2MANY state setter for Worker[1..*] <-> Company[0..1]
	* @param the new state regarding the mandatory association employer
	**********************************************************************/
	public void setValidEmployer(boolean validEmployer)
	{
		this.validEmployer= validEmployer;
	}
	
	private boolean validMember;
	
	/**********************************************************************
	* MEMBER2ASSOCIATIVE state getter for Worker[1..*] <-> Member[*]
	* @return the state regarding the mandatory association member
	**********************************************************************/
	public boolean validMember()
	{
		return validMember;
	}
	
	/**********************************************************************
	* MEMBER2ASSOCIATIVE state setter for Worker[1..*] <-> Member[*]
	* @param the new state regarding the mandatory association member
	**********************************************************************/
	public void setValidMember(boolean validMember)
	{
		this.validMember= validMember;
	}
	
	private boolean validProjects;
	
	/**********************************************************************
	* MEMBER2MEMBER state getter for Worker[1..*] <-> Project[*]
	* @return the state regarding the mandatory association projects
	**********************************************************************/
	public boolean validProjects()
	{
		return validProjects;
	}
	
	/**********************************************************************
	* MEMBER2MEMBER state setter for Worker[1..*] <-> Project[*]
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
		setValidQualifications(ModelContracts.Check(qualifications(), 1, -1));
		setValidEmployer(ModelContracts.Check(employer(), 0,1));
		setValidMember(ModelContracts.Check(member(), 0, -1));
		setValidProjects(ModelContracts.Check(projects(), 0, -1));
	}
	
	/**********************************************************************
	* general association state setter
	**********************************************************************/
	public void checkRestrictions()
	{
		if(validQualifications() && validEmployer() && validMember() && validProjects())
			setAssociationRestrictionsValid(true);
		else
			setAssociationRestrictionsValid(false);
	}
	
	/**********************************************************************
	* general association state setter
	* @return a singleton instance to access the controll methods
	**********************************************************************/
	public static WorkerAccess getAccess()
	{
		return WorkerAccess.getAccess();
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
	public static Worker getWorker(int ID)
	{
		return Database.get(Worker.class, ID);
	}
	
	/**********************************************************************
	* @return the type Class
	**********************************************************************/
	public Class<?> getType()
	{
		return Worker.class;
	}
	
	/**********************************************************************
	* Object serializer
	**********************************************************************/
	public String toString()
	{
		return "Worker [qualifications=" + qualifications + ", employer=" + employer + ", ID=" + ID + ", nickname=" + nickname + ", salary=" + salary + "]";
	}
	
	/**********************************************************************
	* @param other Worker to compare to the current one
	* @return
	**********************************************************************/
	public int compareTo(Object other)
	{
		return this.hashCode() - ((Worker) other).hashCode();
	}
	
}
