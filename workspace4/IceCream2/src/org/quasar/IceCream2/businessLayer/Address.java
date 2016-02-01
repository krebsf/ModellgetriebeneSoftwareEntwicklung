/**********************************************************************
* Filename: Address.java
* Created: 2016/01/31
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream2.businessLayer;

import org.quasar.IceCream2.persistenceLayer.Database;
import org.quasar.IceCream2.utils.Utils;
import org.quasar.IceCream2.utils.ModelContracts;

import java.util.Set;
import java.io.Serializable;

public class Address implements Comparable<Object>
{
	
	/***********************************************************
	* @return all instances of class Address
	***********************************************************/
	public static Set<Address> allInstances(){
		return Database.allInstances(Address.class);
	}
	
	private int ID;
	private int postCode;
	private String street;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public Address()
	{
	}
	
	/**********************************************************************
	* Parameterized constructor
	* @param ID the ID to initialize
	* @param postCode the postCode to initialize
	* @param street the street to initialize
	**********************************************************************/
	public Address(int ID, int postCode, String street)
	{
		this.ID = ID;
		this.postCode = postCode;
		this.street = street;
	}
	
	/**********************************************************************
	* Parameterized Attribute constructor
	* @param postCode the postCode to initialize
	* @param street the street to initialize
	**********************************************************************/
	public Address(int postCode, String street)
	{
		this.ID = Utils.generateMD5Id(new Object[]{"Address",street, postCode});
		this.postCode = postCode;
		this.street = street;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the ID of the address
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
		this.ID = Utils.generateMD5Id(new Object[]{"Address",street, postCode});
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the postCode of the address
	**********************************************************************/
	public int postCode()
	{
		return postCode;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param postCode the postCode to set
	**********************************************************************/
	public void setPostCode(int postCode)
	{
		this.postCode = postCode;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the street of the address
	**********************************************************************/
	public String street()
	{
		return street;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param street the street to set
	**********************************************************************/
	public void setStreet(String street)
	{
		this.street = street;
	}
	
	/**********************************************************************
	* ONE2ONE getter for Address[0..1] <-> Station[1]
	* @return the station of the place
	**********************************************************************/
	public Station station()
	{
		for (Station x : Station.allInstances())
			if (x.place() == this)
				return x;
		return null;
	}
	
	/**********************************************************************
	* ONE2ONE setter for Address[0..1] <-> Station[1]
	* @param station the station to set
	**********************************************************************/
	public void setStation(Station station)
	{
		station.setPlace(this);
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
	
	private boolean validStation;
	
	/**********************************************************************
	* ONE2ONE state getter for Address[0..1] <-> Station[1]
	* @return the state regarding the mandatory association station
	**********************************************************************/
	public boolean validStation()
	{
		return validStation;
	}
	
	/**********************************************************************
	* ONE2ONE state setter for Address[0..1] <-> Station[1]
	* @param the new state regarding the mandatory association station
	**********************************************************************/
	public void setValidStation(boolean validStation)
	{
		this.validStation= validStation;
	}
	
	/**********************************************************************
	* association state setter
	**********************************************************************/
	public void checkModelRestrictions()
	{
		setValidStation(ModelContracts.Check(station(), 1, 1));
	}
	
	/**********************************************************************
	* general association state setter
	**********************************************************************/
	public void checkRestrictions()
	{
		if(validStation())
			setAssociationRestrictionsValid(true);
		else
			setAssociationRestrictionsValid(false);
	}
	
	/**********************************************************************
	* general association state setter
	* @return a singleton instance to access the controll methods
	**********************************************************************/
	public static AddressAccess getAccess()
	{
		return AddressAccess.getAccess();
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
	public static Address getAddress(int ID)
	{
		return Database.get(Address.class, ID);
	}
	
	/**********************************************************************
	* @return the type Class
	**********************************************************************/
	public Class<?> getType()
	{
		return Address.class;
	}
	
	/**********************************************************************
	* Object serializer
	**********************************************************************/
	public String toString()
	{
		return "Address [ID=" + ID + ", postCode=" + postCode + ", street=" + street + "]";
	}
	
	/**********************************************************************
	* @param other Address to compare to the current one
	* @return
	**********************************************************************/
	public int compareTo(Object other)
	{
		return this.hashCode() - ((Address) other).hashCode();
	}
	
}
