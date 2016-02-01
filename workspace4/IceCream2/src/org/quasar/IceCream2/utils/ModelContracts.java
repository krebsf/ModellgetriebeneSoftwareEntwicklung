package org.quasar.IceCream2.utils;

import java.util.Collection;

public class ModelContracts {

	public static boolean Check(Object object, int minimum, int maximum) {
		if (minimum == 1)
			if (object == null)
				return false;
		
		if (object instanceof Collection)
			if (((Collection<?>) object).size() < minimum
					|| (maximum != -1 && ((Collection<?>) object).size() > maximum))
				return false;

		return true;
	}

	public static boolean CheckMinimum(Object object, int minimum){
		if (minimum == 1)
			if (object == null)
				return false;
		
		if (object instanceof Collection)
			if (((Collection<?>) object).size() < minimum)
				return false;

		return true;
	}

	public static boolean CheckMaximun(Object object, int maximum){
		if (object instanceof Collection)
			if ((maximum != -1 && ((Collection<?>) object).size() > maximum))
				return false;

		return true;
	}
}
