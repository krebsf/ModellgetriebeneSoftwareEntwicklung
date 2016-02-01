/**********************************************************************
* Filename: Qualification.java
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

public class Qualification implements Comparable<Object>
{
	
	/***********************************************************
	* @return all instances of class Qualification
	***********************************************************/
	public static Set<Qualification> allInstances(){
		return Database.allInstances(Qualification.class);
	}
	
	private int ID;
	private String description;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public Qualification()
	{
	}
	
	/**********************************************************************
	* Parameterized constructor
	* @param ID the ID to initialize
	* @param description the description to initialize
	**********************************************************************/
	public Qualification(int ID, String description)
	{
		this.ID = ID;
		this.description = description;
	}
	
	/**********************************************************************
	* Parameterized Attribute constructor
	* @param description the description to initialize
	**********************************************************************/
	public Qualification(String description)
	{
		this.ID = Utils.generateMD5Id(new Object[]{"Qualification",description});
		this.description = description;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the ID of the qualification
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
		this.ID = Utils.generateMD5Id(new Object[]{"Qualification",description});
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the description of the qualification
	**********************************************************************/
	public String description()
	{
		return description;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param description the description to set
	**********************************************************************/
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**********************************************************************
	* MANY2MANY getter for Qualification[1..*] <-> Worker[*]
	* @return the workers of the qualifications
	**********************************************************************/
	public Set<Worker> workers()
	{
		Set<Worker> result = new HashSet<Worker>();
		for (Worker x : Worker.allInstances())
			if (x.qualifications() != null && x.qualifications().contains(this))
				result.add(x);
		return result;
	}
	
	/**********************************************************************
	* MANY2MANY multiple setter for Qualification[1..*] <-> Worker[*]
	* @param workers the workers to set
	**********************************************************************/
	public void setWorkers(Set<Worker> workers)
	{
		for (Worker x : workers)
			x.qualifications().add(this);
	}
	
	/**********************************************************************
	* MANY2MANY single setter for Qualification[1..*] <-> Worker[*]
	* @param worker the worker to add
	**********************************************************************/
	public void addWorkers(Worker worker)
	{
		worker.addQualifications(this);
	}
	
	/**********************************************************************
	* MANY2MANY single setter for Qualification[1..*] <-> Worker[*]
	* @param worker the worker to remove
	**********************************************************************/
	public void removeWorkers(Worker worker)
	{
		worker.removeQualifications(this);
	}
	
	/**********************************************************************
	* MANY2MANY getter for Qualification[1..*] <-> Project[*]
	* @return the projects of the requirements
	**********************************************************************/
	public Set<Project> projects()
	{
		Set<Project> result = new HashSet<Project>();
		for (Project x : Project.allInstances())
			if (x.requirements() != null && x.requirements().contains(this))
				result.add(x);
		return result;
	}
	
	/**********************************************************************
	* MANY2MANY multiple setter for Qualification[1..*] <-> Project[*]
	* @param projects the projects to set
	**********************************************************************/
	public void setProjects(Set<Project> projects)
	{
		for (Project x : projects)
			x.requirements().add(this);
	}
	
	/**********************************************************************
	* MANY2MANY single setter for Qualification[1..*] <-> Project[*]
	* @param project the project to add
	**********************************************************************/
	public void addProjects(Project project)
	{
		project.addRequirements(this);
	}
	
	/**********************************************************************
	* MANY2MANY single setter for Qualification[1..*] <-> Project[*]
	* @param project the project to remove
	**********************************************************************/
	public void removeProjects(Project project)
	{
		project.removeRequirements(this);
	}
	
	/**********************************************************************
	* MANY2MANY getter for Qualification[1..*] <-> Training[*]
	* @return the trainings of the trained
	**********************************************************************/
	public Set<Training> trainings()
	{
		Set<Training> result = new HashSet<Training>();
		for (Project x : Training.allInstances())
			if (((Training) x).trained() != null && ((Training) x).trained().contains(this))
				result.add((Training) x);
		return result;
	}
	
	/**********************************************************************
	* MANY2MANY multiple setter for Qualification[1..*] <-> Training[*]
	* @param trainings the trainings to set
	**********************************************************************/
	public void setTrainings(Set<Training> trainings)
	{
		for (Training x : trainings)
			x.trained().add(this);
	}
	
	/**********************************************************************
	* MANY2MANY single setter for Qualification[1..*] <-> Training[*]
	* @param training the training to add
	**********************************************************************/
	public void addTrainings(Training training)
	{
		training.addTrained(this);
	}
	
	/**********************************************************************
	* MANY2MANY single setter for Qualification[1..*] <-> Training[*]
	* @param training the training to remove
	**********************************************************************/
	public void removeTrainings(Training training)
	{
		training.removeTrained(this);
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
	
	private boolean validWorkers;
	
	/**********************************************************************
	* MANY2MANY state getter for Qualification[1..*] <-> Worker[*]
	* @return the state regarding the mandatory association workers
	**********************************************************************/
	public boolean validWorkers()
	{
		return validWorkers;
	}
	
	/**********************************************************************
	* MANY2MANY state setter for Qualification[1..*] <-> Worker[*]
	* @param the new state regarding the mandatory association workers
	**********************************************************************/
	public void setValidWorkers(boolean validWorkers)
	{
		this.validWorkers= validWorkers;
	}
	
	private boolean validProjects;
	
	/**********************************************************************
	* MANY2MANY state getter for Qualification[1..*] <-> Project[*]
	* @return the state regarding the mandatory association projects
	**********************************************************************/
	public boolean validProjects()
	{
		return validProjects;
	}
	
	/**********************************************************************
	* MANY2MANY state setter for Qualification[1..*] <-> Project[*]
	* @param the new state regarding the mandatory association projects
	**********************************************************************/
	public void setValidProjects(boolean validProjects)
	{
		this.validProjects= validProjects;
	}
	
	private boolean validTrainings;
	
	/**********************************************************************
	* MANY2MANY state getter for Qualification[1..*] <-> Training[*]
	* @return the state regarding the mandatory association trainings
	**********************************************************************/
	public boolean validTrainings()
	{
		return validTrainings;
	}
	
	/**********************************************************************
	* MANY2MANY state setter for Qualification[1..*] <-> Training[*]
	* @param the new state regarding the mandatory association trainings
	**********************************************************************/
	public void setValidTrainings(boolean validTrainings)
	{
		this.validTrainings= validTrainings;
	}
	
	/**********************************************************************
	* association state setter
	**********************************************************************/
	public void checkModelRestrictions()
	{
		setValidWorkers(ModelContracts.Check(workers(), 0, -1));
		setValidProjects(ModelContracts.Check(projects(), 0, -1));
		setValidTrainings(ModelContracts.Check(trainings(), 0, -1));
	}
	
	/**********************************************************************
	* general association state setter
	**********************************************************************/
	public void checkRestrictions()
	{
		if(validWorkers() && validProjects() && validTrainings())
			setAssociationRestrictionsValid(true);
		else
			setAssociationRestrictionsValid(false);
	}
	
	/**********************************************************************
	* general association state setter
	* @return a singleton instance to access the controll methods
	**********************************************************************/
	public static QualificationAccess getAccess()
	{
		return QualificationAccess.getAccess();
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
	public static Qualification getQualification(int ID)
	{
		return Database.get(Qualification.class, ID);
	}
	
	/**********************************************************************
	* @return the type Class
	**********************************************************************/
	public Class<?> getType()
	{
		return Qualification.class;
	}
	
	/**********************************************************************
	* Object serializer
	**********************************************************************/
	public String toString()
	{
		return "Qualification [ID=" + ID + ", description=" + description + "]";
	}
	
	/**********************************************************************
	* @param other Qualification to compare to the current one
	* @return
	**********************************************************************/
	public int compareTo(Object other)
	{
		return this.hashCode() - ((Qualification) other).hashCode();
	}
	
}
