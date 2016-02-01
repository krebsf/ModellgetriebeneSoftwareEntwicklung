/**********************************************************************
* Filename: Company.java
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

public class Company implements Comparable<Object>
{
	
	/***********************************************************
	* @return all instances of class Company
	***********************************************************/
	public static Set<Company> allInstances(){
		return Database.allInstances(Company.class);
	}
	
	private int ID;
	private String name;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public Company()
	{
	}
	
	/**********************************************************************
	* Parameterized constructor
	* @param ID the ID to initialize
	* @param name the name to initialize
	**********************************************************************/
	public Company(int ID, String name)
	{
		this.ID = ID;
		this.name = name;
	}
	
	/**********************************************************************
	* Parameterized Attribute constructor
	* @param name the name to initialize
	**********************************************************************/
	public Company(String name)
	{
		this.ID = Utils.generateMD5Id(new Object[]{"Company",name});
		this.name = name;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the ID of the company
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
		this.ID = Utils.generateMD5Id(new Object[]{"Company",name});
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the name of the company
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
	* ONE2MANY getter for Company[0..1] <-> Worker[1..*]
	* @return the employees of the employer
	**********************************************************************/
	public Set<Worker> employees()
	{
		Set<Worker> result = new HashSet<Worker>();
		for (Worker x : Worker.allInstances())
			if (x.employer()  ==  this)
				result.add(x);
		return result;
	}
	
	/**********************************************************************
	* ONE2MANY multiple setter for Company[0..1] <-> Worker[1..*]
	* @param employees the employees to set
	**********************************************************************/
	public void setEmployees(Set<Worker> employees)
	{
		for (Worker x : employees)
			x.setEmployer(this);
	}
	
	/**********************************************************************
	* ONE2MANY single setter for Company[0..1] <-> Worker[1..*]
	* @param worker the worker to add
	**********************************************************************/
	public void addEmployees(Worker worker)
	{
		worker.setEmployer(this);
	}
	
	/**********************************************************************
	* ONE2MANY getter for Company[1] <-> Project[*]
	* @return the projects of the company
	**********************************************************************/
	public Set<Project> projects()
	{
		Set<Project> result = new HashSet<Project>();
		for (Project x : Project.allInstances())
			if (x.company()  ==  this)
				result.add(x);
		return result;
	}
	
	/**********************************************************************
	* ONE2MANY multiple setter for Company[1] <-> Project[*]
	* @param projects the projects to set
	**********************************************************************/
	public void setProjects(Set<Project> projects)
	{
		for (Project x : projects)
			x.setCompany(this);
	}
	
	/**********************************************************************
	* ONE2MANY single setter for Company[1] <-> Project[*]
	* @param project the project to add
	**********************************************************************/
	public void addProjects(Project project)
	{
		project.setCompany(this);
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
	
	private boolean validEmployees;
	
	/**********************************************************************
	* ONE2MANY state getter for Company[0..1] <-> Worker[1..*]
	* @return the state regarding the mandatory association employees
	**********************************************************************/
	public boolean validEmployees()
	{
		return validEmployees;
	}
	
	/**********************************************************************
	* ONE2MANY state setter for Company[0..1] <-> Worker[1..*]
	* @param the new state regarding the mandatory association employees
	**********************************************************************/
	public void setValidEmployees(boolean validEmployees)
	{
		this.validEmployees= validEmployees;
	}
	
	private boolean validProjects;
	
	/**********************************************************************
	* ONE2MANY state getter for Company[1] <-> Project[*]
	* @return the state regarding the mandatory association projects
	**********************************************************************/
	public boolean validProjects()
	{
		return validProjects;
	}
	
	/**********************************************************************
	* ONE2MANY state setter for Company[1] <-> Project[*]
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
		setValidEmployees(ModelContracts.Check(employees(), 1, -1));
		setValidProjects(ModelContracts.Check(projects(), 0, -1));
	}
	
	/**********************************************************************
	* general association state setter
	**********************************************************************/
	public void checkRestrictions()
	{
		if(validEmployees() && validProjects())
			setAssociationRestrictionsValid(true);
		else
			setAssociationRestrictionsValid(false);
	}
	
	/**********************************************************************
	* general association state setter
	* @return a singleton instance to access the controll methods
	**********************************************************************/
	public static CompanyAccess getAccess()
	{
		return CompanyAccess.getAccess();
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
	public static Company getCompany(int ID)
	{
		return Database.get(Company.class, ID);
	}
	
	/**********************************************************************
	* @return the type Class
	**********************************************************************/
	public Class<?> getType()
	{
		return Company.class;
	}
	
	/**********************************************************************
	* Object serializer
	**********************************************************************/
	public String toString()
	{
		return "Company [ID=" + ID + ", name=" + name + "]";
	}
	
	/**********************************************************************
	* @param other Company to compare to the current one
	* @return
	**********************************************************************/
	public int compareTo(Object other)
	{
		return this.hashCode() - ((Company) other).hashCode();
	}
	
}
