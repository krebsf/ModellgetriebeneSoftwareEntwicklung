/**********************************************************************
* Filename: Station.java
* Created: 2016/01/30
* @author Lu�s Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream.businessLayer;

import org.quasar.IceCream.persistenceLayer.Database;
import org.quasar.IceCream.utils.Utils;
import org.quasar.IceCream.utils.ModelContracts;

import java.util.Set;
import java.io.Serializable;
import java.util.HashSet;

public class Station implements Comparable<Object>
{
	
	/***********************************************************
	* @return all instances of class Station
	***********************************************************/
	public static Set<Station> allInstances(){
		return Database.allInstances(Station.class);
	}
	
	private Address place;
	private int ID;
	private String name;
	private int numberOfEntries;
	private int target;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public Station()
	{
	}
	
	/**********************************************************************
	* Parameterized constructor
	* @param place the place to initialize
	* @param ID the ID to initialize
	* @param name the name to initialize
	* @param numberOfEntries the numberOfEntries to initialize
	* @param target the target to initialize
	**********************************************************************/
	public Station(Address place, int ID, String name, int numberOfEntries, int target)
	{
		this.place = place;
		this.ID = ID;
		this.name = name;
		this.numberOfEntries = numberOfEntries;
		this.target = target;
	}
	
	/**********************************************************************
	* Parameterized Attribute constructor
	* @param name the name to initialize
	* @param numberOfEntries the numberOfEntries to initialize
	* @param target the target to initialize
	**********************************************************************/
	public Station(String name, int numberOfEntries, int target)
	{
		this.ID = Utils.generateMD5Id(new Object[]{"Station",name});
		this.name = name;
		this.numberOfEntries = numberOfEntries;
		this.target = target;
	}
	
	/**********************************************************************
	* ONE2ONE getter for Station[1] <-> Address[1]
	* @return the place of the station
	**********************************************************************/
	public Address place()
	{
		return place;
	}
	
	/**********************************************************************
	* ONE2ONE setter for Station[1] <-> Address[1]
	* @param place the place to set
	**********************************************************************/
	public void setPlace(Address place)
	{
		this.place = place;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the ID of the station
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
		this.ID = Utils.generateMD5Id(new Object[]{"Station",name});
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the name of the station
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
	* @return the numberOfEntries of the station
	**********************************************************************/
	public int numberOfEntries()
	{
		return numberOfEntries;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param numberOfEntries the numberOfEntries to set
	**********************************************************************/
	public void setNumberOfEntries(int numberOfEntries)
	{
		this.numberOfEntries = numberOfEntries;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the target of the station
	**********************************************************************/
	public int target()
	{
		return target;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param target the target to set
	**********************************************************************/
	public void setTarget(int target)
	{
		this.target = target;
	}
	
	/**********************************************************************
	* ONE2MANY getter for Station[1] <-> Entry[*]
	* @return the records of the station
	**********************************************************************/
	public Set<Entry> records()
	{
		Set<Entry> result = new HashSet<Entry>();
		for (Entry x : Entry.allInstances())
			if (x.station()  ==  this)
				result.add(x);
		return result;
	}
	
	/**********************************************************************
	* ONE2MANY multiple setter for Station[1] <-> Entry[*]
	* @param records the records to set
	**********************************************************************/
	public void setRecords(Set<Entry> records)
	{
		for (Entry x : records)
			x.setStation(this);
	}
	
	/**********************************************************************
	* ONE2MANY single setter for Station[1] <-> Entry[*]
	* @param entry the entry to add
	**********************************************************************/
	public void addRecords(Entry entry)
	{
		entry.setStation(this);
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
	
	private boolean validRecords;
	
	/**********************************************************************
	* ONE2MANY state getter for Station[1] <-> Entry[*]
	* @return the state regarding the mandatory association records
	**********************************************************************/
	public boolean validRecords()
	{
		return validRecords;
	}
	
	/**********************************************************************
	* ONE2MANY state setter for Station[1] <-> Entry[*]
	* @param the new state regarding the mandatory association records
	**********************************************************************/
	public void setValidRecords(boolean validRecords)
	{
		this.validRecords= validRecords;
	}
	
	private boolean validPlace;
	
	/**********************************************************************
	* ONE2ONE state getter for Station[1] <-> Address[0..1]
	* @return the state regarding the mandatory association place
	**********************************************************************/
	public boolean validPlace()
	{
		return validPlace;
	}
	
	/**********************************************************************
	* ONE2ONE state setter for Station[1] <-> Address[0..1]
	* @param the new state regarding the mandatory association place
	**********************************************************************/
	public void setValidPlace(boolean validPlace)
	{
		this.validPlace= validPlace;
	}
	
	/**********************************************************************
	* association state setter
	**********************************************************************/
	public void checkModelRestrictions()
	{
		setValidRecords(ModelContracts.Check(records(), 0, -1));
		setValidPlace(ModelContracts.Check(place(), 0, 1));
	}
	
	/**********************************************************************
	* general association state setter
	**********************************************************************/
	public void checkRestrictions()
	{
		if(validRecords() && validPlace())
			setAssociationRestrictionsValid(true);
		else
			setAssociationRestrictionsValid(false);
	}
	
	/**********************************************************************
	* general association state setter
	* @return a singleton instance to access the controll methods
	**********************************************************************/
	public static StationAccess getAccess()
	{
		return StationAccess.getAccess();
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
	public static Station getStation(int ID)
	{
		return Database.get(Station.class, ID);
	}
	
	/**********************************************************************
	* @return the type Class
	**********************************************************************/
	public Class<?> getType()
	{
		return Station.class;
	}
	
	/**********************************************************************
	* User-defined operation specified in SOIL/OCL
	**********************************************************************/
	public Set<Entry> entries()
	{
		//	TODO
		//	return self.records->asSet
		return null;
	}
	
	/**********************************************************************
	* User-defined operation specified in SOIL/OCL
	**********************************************************************/
	public double meanActualValue()
	{
		//	TODO
		//	return (self.entries()->iterate(iterator : Entry; result : Real = 0 | (result + iterator.actual)) / self.numberOfEntries)
		return -1;
	}
	
	//	PRE-CONDITIONS (TODO)
	/*
	pre pre1
		(self.numberOfEntries > 0)
	
	*/
	
	/**********************************************************************
	* User-defined operation specified in SOIL/OCL
	**********************************************************************/
	public double meanVarianceValue()
	{
		//	TODO
		//	return (self.entries()->iterate(iterator : Entry; result : Real = 0 | (result + iterator.variance)) / self.numberOfEntries)
		return -1;
	}
	
	//	PRE-CONDITIONS (TODO)
	/*
	pre pre2
		(self.numberOfEntries > 0)
	
	*/
	
	/**********************************************************************
	* Object serializer
	**********************************************************************/
	public String toString()
	{
		return "Station [place=" + place + ", ID=" + ID + ", name=" + name + ", numberOfEntries=" + numberOfEntries + ", target=" + target + "]";
	}
	
	/**********************************************************************
	* @param other Station to compare to the current one
	* @return
	**********************************************************************/
	public int compareTo(Object other)
	{
		return this.hashCode() - ((Station) other).hashCode();
	}
	
	//	-------------------------------------------------------------------------------
	//	INVARIANTS (TODO)
	/*
	inv TargetValueCannotBeNegative
		(self.target >= 0)
	
	*/
}
