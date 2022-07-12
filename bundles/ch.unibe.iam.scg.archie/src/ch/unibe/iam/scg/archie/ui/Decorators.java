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
package ch.unibe.iam.scg.archie.ui;

import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;

import ch.unibe.iam.scg.archie.ArchieActivator;

/**
 * <p>
 * Provides shortcuts to field decorators and adds custom ones.
 * </p>
 *
 * $Id: Decorators.java 747 2009-07-23 09:14:53Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public final class Decorators {

	/** Error */
	public static final int ERROR = 0;
	/** Warning */
	public static final int WARNING = 1;
	/** Valid */
	public static final int VALID = 2;
	/** Error, but quick-fix is available */
	public static final int QUICKFIX = 3;

	private static final String DEC_VALID = "DEC_VALID"; //$NON-NLS-1$
	private static final String DEC_IMG_ID = "decorationValid"; //$NON-NLS-1$

	private static FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();

	static {
		// Get Image from our own ImageRegistry
		registry.registerFieldDecoration(DEC_VALID, null, DEC_IMG_ID, ArchieActivator.getInstance().getImageRegistry());
	}

	/**
	 * @param type
	 * @param description
	 * @return FieldDecoration
	 */
	public static FieldDecoration getFieldDecoration(int type, String description) {
		switch (type) {
		case VALID:
			FieldDecoration validDecoration = registry.getFieldDecoration(DEC_VALID);
			validDecoration.setDescription(description);
			return validDecoration;
		case WARNING:
			FieldDecoration warningDecoration = registry.getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
			warningDecoration.setDescription(description);
			return warningDecoration;
		case ERROR:
			FieldDecoration errorDecoration = registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
			errorDecoration.setDescription(description);
			return errorDecoration;
		case QUICKFIX:
			FieldDecoration quickfixDecoration = registry
					.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR_QUICKFIX);
			quickfixDecoration.setDescription(description);
			return quickfixDecoration;
		default:
			return null;
		}
	}
}
