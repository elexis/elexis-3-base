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
package ch.unibe.iam.scg.archie.samples.model;

import ch.elexis.data.Person;

/**
 * <p>Wrapper class for persons. This class is needed for a custom label provider.
 * Objects in the dataset need to implement the <code>Comparable</code>
 * interface. As the original <code>Person</code> class in Elexis does not
 * implement this interface, we had to write a quick wrapper in order to be able
 * to use persons in the dataset's content.</p>	
 * 
 * $Id: PersonWrapper.java 681 2008-12-16 18:47:18Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 681 $
 */
public class PersonWrapper implements Comparable<PersonWrapper> {

	private Person person;

	/**
	 * Public constructor.
	 * 
	 * @param person
	 *            A <code>Person</code> object that this class wraps.
	 */
	public PersonWrapper(Person person) {
		this.person = person;
	}

	/**
	 * Compares two <code>PersonWrapper</code> objects according to their
	 * person't label strings.
	 * @param wrapper 
	 * @return -1, 0 or 1: defining natural order.
	 */
	public int compareTo(PersonWrapper wrapper) {
		return this.person.getLabel().compareTo(wrapper.getPerson().getLabel());
	}

	/**
	 * Returns the wrapper person in this person wrapper.
	 * @return Returns the wrapper person in this person wrapper.
	 */
	public Person getPerson() {
		return this.person;
	}

	/**
	 * String representation of this object is the person's label.
	 * @see ch.elexis.data.Person#toString
	 */
	@Override
	public String toString() {
		return this.person.getLabel();
	}
}
