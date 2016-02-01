package org.quasar.ProjectsWorld.businessLayer;

import org.quasar.ProjectsWorld.utils.CommandType;

public interface ModelMusts {

	public int ID(Object object);
	public int getNeiborID(Object neibor);
	public Class<?> getType();
	public Class<?> getNeiborType(Object object);
	public void notifyObjectListener(Object object, CommandType property, int oldObjectID, Object oldObject, Object newObject,int oldNeiborID, Object oldNeibor, Object newNeibor);
	public Object serverInsert(Object object);
	public void serverUpdate(Object oldObject, Object newObject);
	public void serverInsertAssociation(Object oldObject, Object newNeibor);
	public void serverDeleteAssociation(Object server_oldObject, Object server_oldNeibor);
}
