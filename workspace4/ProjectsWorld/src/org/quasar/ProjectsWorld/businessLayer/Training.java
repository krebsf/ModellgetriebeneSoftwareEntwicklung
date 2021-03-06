/**********************************************************************
* Filename: Training.java
* Created: 2016/01/14
* @author Lu�s Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.businessLayer;

import org.quasar.ProjectsWorld.persistenceLayer.Database;
import org.quasar.ProjectsWorld.utils.Utils;
import org.quasar.ProjectsWorld.utils.ModelContracts;

import java.util.Set;
import java.io.Serializable;
import java.util.HashSet;

public class Training extends Project implements Comparable<Object>
{
	
	/***********************************************************
	* @return all instances of class Training
	***********************************************************/
	public static Set<Project> allInstances(){
		return Database.allInstances(Training.class);
	}
	
	private Set<Qualification> trained =  new HashSet<Qualification>();
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public Training()
	{
		super();
	}
	
	/**********************************************************************
	* Parameterized constructor
	* @param requirements the requirements to initialize (inherited)
	* @param company the company to initialize (inherited)
	* @param ID the ID to initialize (inherited)
	* @param months the months to initialize (inherited)
	* @param name the name to initialize (inherited)
	* @param size the size to initialize (inherited)
	* @param status the status to initialize (inherited)
	* @param trained the trained to initialize
	**********************************************************************/
	public Training(Set<Qualification> requirements, Company company, int ID, int months, String name, ProjectSize size, ProjectStatus status, Set<Qualification> trained)
	{
		super(requirements, company, ID, months, name, size, status);
		this.trained = trained;
	}
	
	/**********************************************************************
	* Parameterized Attribute constructor
	* @param months the months to initialize (inherited)
	* @param name the name to initialize (inherited)
	* @param size the size to initialize (inherited)
	* @param status the status to initialize (inherited)
	**********************************************************************/
	public Training(int months, String name, ProjectSize size, ProjectStatus status)
	{
		super(months, name, size, status);
		this.ID = Utils.generateMD5Id(new Object[]{"Project",name(), size(), status(), months()});
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param sets the ID
	**********************************************************************/
	public void setID()
	{
		this.ID = Utils.generateMD5Id(new Object[]{"Project",name(), size(), status(), months()});
	}
	
	/**********************************************************************
	* MANY2MANY getter for Training[*] <-> Set(Qualification)[*]
	* @return the trained of the training
	**********************************************************************/
	public Set<Qualification> trained()
	{
		return trained;
	}
	
	/**********************************************************************
	* MANY2MANY setter for Training[*] <-> Set(Qualification)[*]
	* @param trained the trained to set
	**********************************************************************/
	public void setTrained(Set<Qualification> trained)
	{
		this.trained = trained;
	}
	
	/**********************************************************************
	* MANY2MANY single setter for Training[*] <-> Set(Qualification)[*]
	* @param qualification the qualification to add
	**********************************************************************/
	public void addTrained(Qualification qualification)
	{
		this.trained.add(qualification);
	}
	
	/**********************************************************************
	* MANY2MANY single remover for Training[*] <-> Set(Qualification)[*]
	* @param qualification the qualification to remove
	**********************************************************************/
	public void removeTrained(Qualification qualification)
	{
		this.trained.remove(qualification);
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
	
	private boolean validTrained;
	
	/**********************************************************************
	* MANY2MANY state getter for Training[*] <-> Qualification[1..*]
	* @return the state regarding the mandatory association trained
	**********************************************************************/
	public boolean validTrained()
	{
		return validTrained;
	}
	
	/**********************************************************************
	* MANY2MANY state setter for Training[*] <-> Qualification[1..*]
	* @param the new state regarding the mandatory association trained
	**********************************************************************/
	public void setValidTrained(boolean validTrained)
	{
		this.validTrained= validTrained;
	}
	
	/**********************************************************************
	* association state setter
	**********************************************************************/
	public void checkModelRestrictions()
	{
		super.checkModelRestrictions();
		setValidTrained(ModelContracts.Check(trained(), 1, -1));
	}
	
	/**********************************************************************
	* general association state setter
	**********************************************************************/
	public void checkRestrictions()
	{
		if(super.isAssociationRestrictionsValid() && validTrained())
			setAssociationRestrictionsValid(true);
		else
			setAssociationRestrictionsValid(false);
	}
	
	/**********************************************************************
	* general association state setter
	* @return a singleton instance to access the controll methods
	**********************************************************************/
	public static TrainingAccess getAccess()
	{
		return TrainingAccess.getAccess();
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
		if(AssociationID.equals("projects_MemberAssociation") || AssociationID.equals("MemberAssociation") || AssociationID.equals("RequiresAssociation") || AssociationID.equals("CarriesOutAssociation"))
			super.insertAssociation(neibor, AssociationID);
		else
			getAccess().insertAssociation(this,neibor, AssociationID);
	}
	
	/**********************************************************************
	* deleteAssociation in the caller object
	* @param the neighbor object
	**********************************************************************/
	public void deleteAssociation(Object neibor, String AssociationID)
	{
		if(AssociationID.equals("projects_MemberAssociation") || AssociationID.equals("MemberAssociation") || AssociationID.equals("RequiresAssociation") || AssociationID.equals("CarriesOutAssociation"))
			super.deleteAssociation(neibor, AssociationID);
		else
			getAccess().deleteAssociation(this,neibor, AssociationID);
	}
	
	/**********************************************************************
	* @param An ID
	* @return the object with the given ID or null
	**********************************************************************/
	public static Training getTraining(int ID)
	{
		return Database.get(Training.class, ID);
	}
	
	/**********************************************************************
	* @return the type Class
	**********************************************************************/
	public Class<?> getType()
	{
		return Training.class;
	}
	
	/**********************************************************************
	* Object serializer
	**********************************************************************/
	public String toString()
	{
		return "Training [" + super.toString() + " trained=" + trained + "]";
	}
	
	/**********************************************************************
	* @param other Training to compare to the current one
	* @return
	**********************************************************************/
	public int compareTo(Object other)
	{
		return this.hashCode() - ((Training) other).hashCode();
	}
	
}
