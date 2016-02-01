package org.quasar.IceCream3.utils;

public class Command {
	
	private CommandType type;
	private CommandTargetLayer targetLayer;
	//estou a ver que é mm necessario para os deletes
	private int oldObjectID;
	//para os association deletes
	private int oldNeiborID;
	private Class<?> oldNeiborType;
	
	private Object oldObject;
	private Object newObject;
	
//	adicionei estes dois atributos para usar nos listeners
//	como tal nao vou gravar pois ainda neo vejo utilidade destes para o lado servidor apenas localmente
	private Object source;
	private Object oldNeibor;
	private Object newNeibor;
	
	public Command(Object source, CommandType type, CommandTargetLayer targetLayer, int oldObjectID, Object oldObject ,Object object, Class<?> oldNeiborType, int oldNeiborID, Object oldNeibor, Object neibor){
		this.source = source;
		this.type = type;
		this.targetLayer = targetLayer;
		this.oldObjectID = oldObjectID;
		this.oldNeiborID = oldNeiborID;
		this.oldNeiborType = oldNeiborType;
		this.oldObject = oldObject;
		this.newObject = object;
		this.oldNeibor = oldNeibor;
		this.newNeibor = neibor;
	}

	public Object getSource() {return source;}
	public void setSource(Object source) {this.source = source;}

	public CommandType getType() {return type;}
	public void setType(CommandType type) {this.type = type;}
	
	public CommandTargetLayer getTargetLayer() {return targetLayer;}
	public void setTargetLayer(CommandTargetLayer targetLayer) {this.targetLayer = targetLayer;}

	public int getoldObjectID() {return oldObjectID;}
	public void setoldObjectID(int objectID) {this.oldObjectID = objectID;}
	
	public int getoldNeiborID() {return oldNeiborID;}
	public void setoldNeiborID(int neiborID) {this.oldNeiborID = neiborID;}
	
	public Class<?> getoldNeiborType() {return oldNeiborType;}
	public void setoldNeiborType(Class<?> oldNeiborType) {this.oldNeiborType = oldNeiborType;}
	
	public Object getoldObject() {return oldObject;}
	public void setoldObject(Object object) {this.oldObject = object;}
	
	public Object getObject() {return newObject;}
	public void setObject(Object object) {this.newObject = object;}
	
	public Object getOldNeibor() {return oldNeibor;}
	public void setOldNeibor(Object oldNeibor) {this.oldNeibor = oldNeibor;}

	public Object getNeibor() {return newNeibor;}
	public void setNeibor(Object neibor) {this.newNeibor = neibor;}
	
	public String toString(){
//		if(getObject() != null && getNeibor() != null)
//			return "type: " + type + " ---- newObject: " + getObject().toString() + " ---- newNeibor: " + getNeibor().toString();
//		else if(getObject() != null && getNeibor() == null){
//			if(getObject() instanceof Receipt)
//				Log.i("", "" + ((Receipt) getObject()).getID());
//			return "type: " + type + " ---- newObject: " + getObject().toString() + " ---- newNeibor: " + "i do not have any neibor";
//		}else if(getObject() == null && getNeibor() != null)
//			return "type: " + type + " ---- newObject: " + "devo apenas ter o oldObject" + " ---- newNeibor: " + getNeibor().toString();
//		else if(getType().toString().equals(CommandType.DELETE.toString()))
//			return getType().toString() + " - objecto - " + getoldObjectID();
//		else
			return "command toString outro caso nao previsto";
	}
}
