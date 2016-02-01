/**********************************************************************
* Filename: CalendarDate.java
* Created: 2016/01/31
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream3.businessLayer;

import org.quasar.IceCream3.persistenceLayer.Database;
import org.quasar.IceCream3.utils.Utils;
import org.quasar.IceCream3.utils.ModelContracts;

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
	private CalendarDate now;
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
	* @param now the now to initialize
	* @param year the year to initialize
	**********************************************************************/
	public CalendarDate(int ID, int day, int month, CalendarDate now, int year)
	{
		this.ID = ID;
		this.day = day;
		this.month = month;
		this.now = now;
		this.year = year;
	}
	
	/**********************************************************************
	* Parameterized Attribute constructor
	* @param day the day to initialize
	* @param month the month to initialize
	* @param now the now to initialize
	* @param year the year to initialize
	**********************************************************************/
	public CalendarDate(int day, int month, CalendarDate now, int year)
	{
		this.ID = Utils.generateMD5Id(new Object[]{"CalendarDate",year, month, day});
		this.day = day;
		this.month = month;
		this.now = now;
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
	* @return the now of the calendarDate
	**********************************************************************/
	public CalendarDate now()
	{
		return now;
	}
	
	/**********************************************************************
	* Standard attribute setter
	* @param now the now to set
	**********************************************************************/
	public void setNow(CalendarDate now)
	{
		this.now = now;
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
	* User-defined operation specified in SOIL/OCL
	* @param day the day to set
	* @param month the month to set
	* @param year the year to set
	**********************************************************************/
	public void init(int day, int month, int year)
	{
		//	TODO
	}
	
	/**********************************************************************
	* User-defined operation specified in SOIL/OCL
	* @param date the date to set
	**********************************************************************/
	public void initS(String date)
	{
		//	TODO
	}
	
	/**********************************************************************
	* User-defined operation specified in SOIL/OCL
	* @param t the t to set
	**********************************************************************/
	public boolean isAfter(CalendarDate t)
	{
		//	TODO
		//	return if (self.year = t.year) then if (self.month = t.month) then (self.day > t.day) else (self.month > t.month) endif else (self.year > t.year) endif
		return true;
	}
	
	/**********************************************************************
	* User-defined operation specified in SOIL/OCL
	* @param t the t to set
	**********************************************************************/
	public boolean isBefore(CalendarDate t)
	{
		//	TODO
		//	return if (self.year = t.year) then if (self.month = t.month) then (self.day < t.day) else (self.month < t.month) endif else (self.year < t.year) endif
		return true;
	}
	
	/**********************************************************************
	* User-defined operation specified in SOIL/OCL
	* @param x the x to set
	* @param y the y to set
	**********************************************************************/
	public boolean isDivisible(int x, int y)
	{
		//	TODO
		//	return (((x div y) * y) = x)
		return true;
	}
	
	/**********************************************************************
	* User-defined operation specified in SOIL/OCL
	* @param t the t to set
	**********************************************************************/
	public boolean isEqual(CalendarDate t)
	{
		//	TODO
		//	return (((self.year = t.year) and (self.month = t.month)) and (self.day = t.day))
		return true;
	}
	
	/**********************************************************************
	* User-defined operation specified in SOIL/OCL
	**********************************************************************/
	public boolean isLeap()
	{
		//	TODO
		//	return if (self.isDivisible(self.year, 400) or self.isDivisible(self.year, 4)) then true else if self.isDivisible(self.year, 100) then false else if self.isDivisible(self.year, 4) then true else false endif endif endif
		return true;
	}
	
	/**********************************************************************
	* User-defined operation specified in SOIL/OCL
	* @param date the date to set
	**********************************************************************/
	public CalendarDate stringToDate(String date)
	{
		//	TODO
		return null;
	}
	
	/**********************************************************************
	* User-defined operation specified in SOIL/OCL
	**********************************************************************/
	public CalendarDate today()
	{
		//	TODO
		//	return self.now
		return null;
	}
	
	/**********************************************************************
	* User-defined operation specified in SOIL/OCL
	**********************************************************************/
	public boolean valid()
	{
		//	TODO
		//	return ((((self.month >= 1) and (self.month <= 12)) and (self.day >= 1)) and if self.isLeap() then (self.day <= Sequence {31,29,31,30,31,30,31,31,30,31,30,31}->at(self.month)) else (self.day <= Sequence {31,28,31,30,31,30,31,31,30,31,30,31}->at(self.month)) endif)
		return true;
	}
	
	/**********************************************************************
	* User-defined operation specified in SOIL/OCL
	* @param t the t to set
	**********************************************************************/
	public int yearsSince(CalendarDate t)
	{
		//	TODO
		//	return if ((self.month < t.month) or ((self.month = t.month) and (self.day < t.day))) then ((self.year - t.year) - 1) else (self.year - t.year) endif
		return -1;
	}
	
	/**********************************************************************
	* Object serializer
	**********************************************************************/
	public String toString()
	{
		return "CalendarDate [ID=" + ID + ", day=" + day + ", month=" + month + ", now=" + now + ", year=" + year + "]";
	}
	
	/**********************************************************************
	* @param other CalendarDate to compare to the current one
	* @return
	**********************************************************************/
	public int compareTo(Object other)
	{
		return this.hashCode() - ((CalendarDate) other).hashCode();
	}
	
	//	-------------------------------------------------------------------------------
	//	INVARIANTS (TODO)
	/*
	inv DateIsValid
		self.valid()
	
	inv CalendarDateObjectsContainDistinctDates
		CalendarDate.allInstances->isUnique($elem0 : CalendarDate | $elem0.year.toString.concat('/').concat($elem0.month.toString).concat('/').concat($elem0.day.toString))
	
	*/
}
