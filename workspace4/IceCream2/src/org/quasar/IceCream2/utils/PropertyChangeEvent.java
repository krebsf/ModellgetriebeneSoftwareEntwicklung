package org.quasar.IceCream2.utils;

import org.quasar.IceCream2.utils.CommandType;

public class PropertyChangeEvent extends java.util.EventObject {

	private CommandType propertyName;
	private Object newObject;
	private Object oldObject;
	private int oldObjectID;
	private Object newNeibor;
	private Object oldNeibor;
	private int oldNeiborID;
	
	public PropertyChangeEvent(Object source, CommandType propertyName, int oldObjectID, Object oldObject, Object newObject, int oldNeiborID, Object oldNeibor, Object newNeibor) {
		super(source);
		this.propertyName = propertyName;
		this.newObject = newObject;
		this.oldObject = oldObject;
		this.oldObjectID = oldObjectID;
		this.newNeibor = newNeibor;
		this.oldNeibor = oldNeibor;
		this.oldNeiborID = oldNeiborID;
		
	}

	public Object getNewObject() {return newObject;}
	public Object getOldObject() {return oldObject;}
	public int getOldObjectID() {return oldObjectID;}
	public CommandType getCommandType() {return propertyName;}
	public Object getNewNeibor() {return newNeibor;}
	public Object getOldNeibor() {return oldNeibor;}
	public int getOldNeiborID() {return oldNeiborID;}

}
