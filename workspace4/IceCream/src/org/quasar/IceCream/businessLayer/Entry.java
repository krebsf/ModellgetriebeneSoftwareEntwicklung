/**********************************************************************
 * Filename: Entry.java
 * Created: 2016/01/30
 * @author Luís Pires da Silva and Fernando Brito e Abreu
 **********************************************************************/
package org.quasar.IceCream.businessLayer;

import org.quasar.IceCream.persistenceLayer.Database;
import org.quasar.IceCream.utils.Utils;
import org.quasar.IceCream.utils.ModelContracts;

import java.util.Set;
import java.io.Serializable;

public class Entry implements Comparable<Object>
{

	/***********************************************************
	 * @return all instances of class Entry
	 ***********************************************************/
	public static Set<Entry> allInstances(){
		return Database.allInstances(Entry.class);
	}

	private Station station;
	private int ID;
	private int actual;
	private CalendarDate date;
	private int variance;

	/**********************************************************************
	 * Default constructor
	 **********************************************************************/
	public Entry()
	{
	}

	/**********************************************************************
	 * Parameterized constructor
	 * @param station the station to initialize
	 * @param ID the ID to initialize
	 * @param actual the actual to initialize
	 * @param date the date to initialize
	 * @param variance the variance to initialize
	 **********************************************************************/
	public Entry(Station station, int ID, int actual, CalendarDate date, int variance)
	{
		this.station = station;
		this.ID = ID;
		this.actual = actual;
		this.date = date;
		this.variance = variance;
	}

	/**********************************************************************
	 * Parameterized Attribute constructor
	 * @param actual the actual to initialize
	 * @param date the date to initialize
	 * @param variance the variance to initialize
	 **********************************************************************/
	public Entry(int actual, CalendarDate date, int variance)
	{
		this.ID = Utils.generateMD5Id(new Object[]{"Entry",date.ID()});
		this.actual = actual;
		this.date = date;
		this.variance = variance;
	}

	/**********************************************************************
	 * ONE2MANY getter for Entry[*] <-> Station[1]
	 * @return the station of the entry
	 **********************************************************************/
	public Station station()
	{
		return station;
	}

	/**********************************************************************
	 * ONE2MANY setter for Entry[*] <-> Station[1]
	 * @param station the station to set
	 **********************************************************************/
	public void setStation(Station station)
	{
		this.station = station;
	}

	/**********************************************************************
	 * Standard attribute getter
	 * @return the ID of the entry
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
		this.ID = Utils.generateMD5Id(new Object[]{"Entry",date.ID()});
	}

	/**********************************************************************
	 * Standard attribute getter
	 * @return the actual of the entry
	 **********************************************************************/
	public int actual()
	{
		return actual;
	}

	/**********************************************************************
	 * Standard attribute setter
	 * @param actual the actual to set
	 **********************************************************************/
	public void setActual(int actual)
	{
		this.actual = actual;
	}

	/**********************************************************************
	 * Standard attribute getter
	 * @return the date of the entry
	 **********************************************************************/
	public CalendarDate date()
	{
		return date;
	}

	/**********************************************************************
	 * Standard attribute setter
	 * @param date the date to set
	 **********************************************************************/
	public void setDate(CalendarDate date)
	{
		this.date = date;
	}

	/**********************************************************************
	 * Standard attribute getter
	 * @return the variance of the entry
	 **********************************************************************/
	public int variance()
	{
		return calculateVariance();
	}

	/**********************************************************************
	 * Standard attribute setter
	 * @param variance the variance to set
	 **********************************************************************/
	public void setVariance(int variance)
	{
		this.variance = variance;
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
	 * ONE2MANY state getter for Entry[*] <-> Station[1]
	 * @return the state regarding the mandatory association station
	 **********************************************************************/
	public boolean validStation()
	{
		return validStation;
	}

	/**********************************************************************
	 * ONE2MANY state setter for Entry[*] <-> Station[1]
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
		setValidStation(ModelContracts.Check(station(), 1,1));
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
	public static EntryAccess getAccess()
	{
		return EntryAccess.getAccess();
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
	public static Entry getEntry(int ID)
	{
		return Database.get(Entry.class, ID);
	}

	/**********************************************************************
	 * @return the type Class
	 **********************************************************************/
	public Class<?> getType()
	{
		return Entry.class;
	}

	/**********************************************************************
	 * User-defined operation specified in SOIL/OCL
	 **********************************************************************/
	public int calculateVariance()
	{
		//	PRE-CONDITIONS (TODO)
		/*
		pre pre3
			(self.station <> oclUndefined(OclVoid))

		Implementation
		 */

		if(this.station != null)
		{
			return (this.actual - this.station.target());
		}
		else
			return -1 ;
	}



	/**********************************************************************
	 * Object serializer
	 **********************************************************************/
	public String toString()
	{
		return "Entry [station=" + station + ", ID=" + ID + ", actual=" + actual + ", date=" + date + ", variance=" + variance + "]";
	}

	/**********************************************************************
	 * @param other Entry to compare to the current one
	 * @return
	 **********************************************************************/
	public int compareTo(Object other)
	{
		return this.hashCode() - ((Entry) other).hashCode();
	}

	//	-------------------------------------------------------------------------------
	//	INVARIANTS (TODO)
	/*
	inv ActualValueCannotBeNegative
		(self.actual >= 0)

	 */
}
