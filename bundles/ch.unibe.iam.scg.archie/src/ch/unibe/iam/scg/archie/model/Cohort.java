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
 * <p>
 * A Cohort represents a certain age-group (e.g. all patients with ages from 10
 * to 20). lowerBound must always be smaller than upperBound
 * </p>
 *
 * $Id: Cohort.java 689 2008-12-17 20:51:28Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 689 $
 */
public class Cohort implements Comparable<Cohort> {

	/**
	 * Delimiter used for the title of a cohort.
	 */
	public final static String TITLE_DELIMITER = " - "; //$NON-NLS-1$

	private int lowerBound;
	private int upperBound;

	private Object value;

	/**
	 * Public constructor.
	 *
	 * @param lowerBound Lower bound of a cohort.
	 * @param upperBound Upper bound of a cohort.
	 * @param value      Value of the age group (cohort).
	 */
	public Cohort(final int lowerBound, final int upperBound, final Object value) {
		// Checking Preconditions:
		if (lowerBound > upperBound) {
			throw new IllegalArgumentException("lowerBound has to be smaller than upperBound!"); //$NON-NLS-1$
		}
		this.setLowerBound(lowerBound);
		this.setUpperBound(upperBound);
		this.setValue(value);
	}

	/**
	 * Returns the cohort size. The size is always 1 larger than the real
	 * difference, since a cohort includes both the lower and upper Bound.
	 *
	 * @return Returns the cohort size.
	 */
	public int getCohortSize() {
		return Math.abs(this.upperBound - this.lowerBound) + 1;
	}

	/**
	 * Sets a cohort's lower bound.
	 *
	 * @param lowerBound the lowerBound to set
	 */
	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}

	/**
	 * Returns a cohort's lower bound.
	 *
	 * @return The lower bound of a cohort.
	 */
	public int getLowerBound() {
		return this.lowerBound;
	}

	/**
	 * Sets a cohort's upper bound.
	 *
	 * @param upperBound Upperbound value.
	 */
	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}

	/**
	 * Returns a cohort's upper bound.
	 *
	 * @return A cohort's upper bound.
	 */
	public int getUpperBound() {
		return this.upperBound;
	}

	/**
	 * Sets a cohort's value.
	 *
	 * @param value The cohort's value.
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Returns a cohort's value.
	 *
	 * @return Returns a cohort's value.
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * To string representation of a cohort. The lower and upper bound are connected
	 * by the <code>TITLE_DELIMITER</code> of the <code>Cohort</code> class.
	 *
	 * @return Title of this cohort (made up of lower- and upper bound).
	 */
	@Override
	public String toString() {
		if (this.lowerBound == this.upperBound) {
			return ((Integer) this.lowerBound).toString();
		}
		return this.lowerBound + TITLE_DELIMITER + this.upperBound;
	}

	/**
	 * A Cohort is smaller than another if its lower bound is smaller. If the lower
	 * bound of two cohorts is equal, the cohort with the smaller cohort size is
	 * smaller.
	 *
	 * @param otherCohort
	 * @return -1 if this cohort is smaller, 0 if equal, 1 is larger
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Cohort otherCohort) {
		if (this.lowerBound == otherCohort.getLowerBound()) {
			if (this.getCohortSize() < otherCohort.getCohortSize()) {
				return -1;
			} else if (this.getCohortSize() > otherCohort.getCohortSize()) {
				return 1;
			}
			return 0;
		} else if (this.lowerBound < otherCohort.getLowerBound()) {
			return -1;
		} else if (this.lowerBound > otherCohort.getLowerBound()) {
			return 1;
		}
		return 0;
	}

	/**
	 * Checks if another cohort is equal to this one.
	 *
	 * @param object An object.
	 * @return True if this given object is a cohort and is equal (same lower- and
	 *         upperBound), false else.
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof Cohort) {
			Cohort otherCohort = ((Cohort) object);
			if (this.lowerBound == otherCohort.getLowerBound() && this.upperBound == otherCohort.getUpperBound()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the hash code for this cohort. The hash code is composed out of the
	 * name of a cohort.
	 *
	 * @return HashCode of the name of this Cohort.
	 */
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

}
