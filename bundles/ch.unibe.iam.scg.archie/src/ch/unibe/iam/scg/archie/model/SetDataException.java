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
package ch.unibe.iam.scg.archie.model;

/**
 * <p>Custom Exception which handles malformed input Strings for queries.</p>
 * 
 * $Id: SetDataException.java 747 2009-07-23 09:14:53Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class SetDataException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * This exception is thrown by a query if any of the annotated methods can't
	 * handle the input string. The message is printed to the output.
	 * 
	 * @param message
	 *            error message to be displayed to the user in the UI.
	 */
	public SetDataException(String message) {
		super(message);
	}
}
