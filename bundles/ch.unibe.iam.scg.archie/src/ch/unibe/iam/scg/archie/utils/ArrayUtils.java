/*******************************************************************************
 * Copyright (c) 2008 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *******************************************************************************/
package ch.unibe.iam.scg.archie.utils;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * Array utility class providing convenience methods for arrays.
 * </p>
 *
 * $Id: ArrayUtils.java 709 2009-01-04 10:20:27Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 709 $
 */
public class ArrayUtils {

	/**
	 * Prints out an int array in human readable form.
	 *
	 * @param array An int array.
	 */
	public static final void print(int[] array) {
		System.out.println(ArrayUtils.toString(array));
	}

	/**
	 * Generates a string representation of an int array. An example string
	 * representation could look like [3,2,7].
	 *
	 * @param array Array of ints.
	 * @return String representation of an array.
	 */
	public static final String toString(int[] array) {
		StringBuffer buffer = new StringBuffer();

		if (array == null) {
			return buffer.append("null").toString(); //$NON-NLS-1$
		}

		for (int i = 0; i < array.length; i++) {
			String prefix = (i == 0) ? "[" : StringUtils.EMPTY; //$NON-NLS-1$
			String suffix = (i == array.length - 1) ? "]" : ","; //$NON-NLS-1$ //$NON-NLS-2$

			buffer.append(prefix);
			buffer.append(array[i]);
			buffer.append(suffix);
		}
		return buffer.toString();
	}

	/**
	 *
	 * @param array
	 * @param needle
	 * @return True if the given needle is in the array.
	 */
	public static final boolean inArray(int[] array, int needle) {
		boolean found = false;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == needle) {
				found = true;
				break;
			}
		}
		return found;
	}

	/**
	 * @param interfaces
	 * @param needle
	 * @return True if the given class is in the interfaces array.
	 */
	public static final boolean hasInterface(Class<?>[] interfaces, Class<?> needle) {
		boolean found = false;
		for (int i = 0; i < interfaces.length; i++) {
			String interfaceName = interfaces[i].getName();
			if (interfaceName.equals(needle.getName())) {
				found = true;
				break;
			}
		}
		return found;
	}

	/**
	 * Checks for a given method name in the array of methods.
	 *
	 * @param methods Array of <code>Method</code> objects.
	 * @param string  Needle, the method name to search for.
	 * @return True if a method with the given name exists in the array, false else.
	 */
	public static boolean hasMethod(Method[] methods, String needle) {
		for (int i = 0; i < methods.length; i++) {
			String methodName = methods[i].getName();
			if (methodName.equals(needle)) {
				return true;
			}
		}
		return false;
	}
}