/**********************************************************************
* Filename: CalendarDate.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.businessLayer;

import org.quasar.ProjectsWorld.persistenceLayer.Database;
import org.quasar.ProjectsWorld.utils.Utils;
import org.quasar.ProjectsWorld.utils.ModelContracts;

import java.util.Set;
import java.io.Serializable;

public class CalendarDate implements Comparable<Object>
{
	
	/***********************************************************
	* @return all instances of class CalendarDate
	***********************************************************/
	public static Set<CalendarDate> allInstances(){
		return Database.allInstances(CalendarDate.class);
	}
	
	private int ID;
	private int day;
	private int month;
	private int year;
	
	/**********************************************************************
	* Default constructor
	**********************************************************************/
	public CalendarDate()
	{
	}
	
	/**********************************************************************
	* Parameterized constructor
	* @param ID the ID to initialize
	* @param day the day to initialize
	* @param month the month to initialize
	* @param year the year to initialize
	**********************************************************************/
	public CalendarDate(int ID, int day, int month, int year)
	{
		this.ID = ID;
		this.day = day;
		this.month = month;
		this.year = year;
	}
	
	/**********************************************************************
	* Parameterized Attribute constructor
	* @param day the day to initialize
	* @param month the month to initialize
	* @param year the year to initialize
	**********************************************************************/
	public CalendarDate(int day, int month, int year)
	{
		this.ID = Utils.generateMD5Id(new Object[]{"CalendarDate",year, month, day});
		this.day = day;
		this.month = month;
		this.year = year;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the ID of the calendarDate
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
		this.ID = Utils.generateMD5Id(new Object[]{"CalendarDate",year, month, day});
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the day of the calendarDate
	**********************************************************************/
	public int day()
	{
		return day;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param day the day to set
	**********************************************************************/
	public void setDay(int day)
	{
		this.day = day;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the month of the calendarDate
	**********************************************************************/
	public int month()
	{
		return month;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param month the month to set
	**********************************************************************/
	public void setMonth(int month)
	{
		this.month = month;
	}
	
	/**********************************************************************
	* Standard attribute getter
	* @return the year of the calendarDate
	**********************************************************************/
	public int year()
	{
		return year;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param year the year to set
	**********************************************************************/
	public void setYear(int year)
	{
		this.year = year;
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
	
	/**********************************************************************
	* association state setter
	**********************************************************************/
	public void checkModelRestrictions()
	{
	}
	
	/**********************************************************************
	* general association state setter
	**********************************************************************/
	public void checkRestrictions()
	{
		setAssociationRestrictionsValid(true);
	}
	
	/**********************************************************************
	* general association state setter
	* @return a singleton instance to access the controll methods
	**********************************************************************/
	public static CalendarDateAccess getAccess()
	{
		return CalendarDateAccess.getAccess();
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
	* @param An ID
	* @return the object with the given ID or null
	**********************************************************************/
	public static CalendarDate getCalendarDate(int ID)
	{
		return Database.get(CalendarDate.class, ID);
	}
	
	/**********************************************************************
	* @return the type Class
	**********************************************************************/
	public Class<?> getType()
	{
		return CalendarDate.class;
	}
	
	/**********************************************************************
	* Object serializer
	**********************************************************************/
	public String toString()
	{
		return "CalendarDate [ID=" + ID + ", day=" + day + ", month=" + month + ", year=" + year + "]";
	}
	
	/**********************************************************************
	* @param other CalendarDate to compare to the current one
	* @return
	**********************************************************************/
	public int compareTo(Object other)
	{
		return this.hashCode() - ((CalendarDate) other).hashCode();
	}
	
}
