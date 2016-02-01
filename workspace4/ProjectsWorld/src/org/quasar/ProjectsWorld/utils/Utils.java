package org.quasar.ProjectsWorld.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

	public synchronized static boolean equals(Object obj1, Object obj2){
		if(obj1 instanceof Integer && obj2 instanceof Integer)
			return obj1 == obj2;
		else if(obj1 instanceof String && obj2 instanceof String)
			return obj1.equals(obj2);
		else
			return false;
	}
	
	public synchronized static int generateMD5Id(Object... object){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ObjectOutputStream oos;
			oos = new ObjectOutputStream(baos);
			
			for(Object x : object){
				if(x == null)
					oos.writeChars("null");
				else if(x instanceof Integer)
					oos.writeInt((Integer) x);
				else if(x instanceof String)
					oos.writeChars((String) x);
				else if(x instanceof Double)
					oos.writeDouble((Double) x);
				else if(x instanceof Class)
					oos.writeChars(((Class<?>) x).getName());
			}
			
	        oos.close();
	        MessageDigest m = MessageDigest.getInstance("MD5");
	        m.update(baos.toByteArray());
	        BigInteger testObjectHash = new BigInteger(m.digest());
	        return testObjectHash.intValue();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
