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

import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * <p>
 * This class provides a very simple job scheduling rule. Use this rule for jobs
 * that that cannot be run concurrently but have to be run sequentially.
 * </p>
 *
 * $Id: MutexRule.java 747 2009-07-23 09:14:53Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class MutexRule implements ISchedulingRule {

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(ISchedulingRule rule) {
		return this == rule;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isConflicting(ISchedulingRule rule) {
		return this == rule;
	}

}
