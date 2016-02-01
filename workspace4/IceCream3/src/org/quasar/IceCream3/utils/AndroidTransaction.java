package org.quasar.IceCream3.utils;

import java.util.ArrayList;
import java.util.List;

import org.quasar.IceCream3.persistenceLayer.Database;


public class AndroidTransaction {

	public AndroidTransaction (){
		if(Database.OpenDB().query(AndroidTransaction.class).isEmpty()){
			this.ID = 1;
		}else{
			this.ID = Database.OpenDB().query(AndroidTransaction.class).size() + 1;
		}
		this.commands = new ArrayList<Command>();
		this.ErrorTittle = "";
		this.ErrorMessage = new StringBuffer();
	}
	
	public List<Command> getCommands() {
		return commands;
	}
	public void setCommands(List<Command> newCommands) {
		this.commands = newCommands;
	}

	
	private int ID;
	private String ErrorTittle;
	private StringBuffer ErrorMessage;
	private List<Command> commands;
	
	public String toString(){
		return "transaction - " + this.ID;
	}

	public String getErrorTittle(){
		return ErrorTittle;
	}
	public StringBuffer getErrorMessage() {
		return ErrorMessage;
	}

	public void addErrorMessage(String errorMessage) {
		ErrorMessage.append(errorMessage);
	}

	public void setErrorTittle(String errorTittle) {
		this.ErrorTittle = errorTittle;
	}
}
