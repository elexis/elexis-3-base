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
package ch.unibe.iam.scg.archie.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Marks a method as setter. Used by the view to determine which elements can be
 * changed by the user. The index determines the order in which the setter
 * methods will be invoked. This is only important for exception handling. If
 * the validity of a setter value depends on another value (e.g. marking a
 * starting and ending point for a time period where the end point has to be
 * greater than the starting point) the index can be used to control the
 * invocation order.
 * </p>
 *
 * $Id: SetProperty.java 705 2009-01-03 17:48:46Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 705 $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SetProperty {

	/**
	 * The name of this annotation. This has to match the value of a GetProperty
	 * annotation used to mark a getter method. If there is none, an
	 * NullPointerException will occur.
	 */
	public String name();

	/**
	 * Determines the order of invocation. Use this if a setter value's validity is
	 * dependent on another value. This has not to be necessarily the same value as
	 * in the GetProperty annotation. The default value of -1 ensures that methods
	 * without an index annotated will be invoked first since they do not depend on
	 * the other data they are not critical and can independently called.
	 */
	public int index() default -1;
}
