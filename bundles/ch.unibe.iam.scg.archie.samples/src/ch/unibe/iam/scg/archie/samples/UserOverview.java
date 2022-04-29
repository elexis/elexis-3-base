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
package ch.unibe.iam.scg.archie.samples;

import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.elexis.data.Anwender;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Query;
import ch.unibe.iam.scg.archie.model.AbstractDataProvider;
import ch.unibe.iam.scg.archie.samples.i18n.Messages;

/**
 * <p>
 * Provides a simple System User Overview about all users in the system. Users
 * are listed with their username, birthday, gender and all user groups they are
 * in. Further more, the list shows whether an user account is valid or not.
 * </p>
 * 
 * $Id: UserOverview.java 766 2009-07-24 11:28:14Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 766 $
 */
public class UserOverview extends AbstractDataProvider {

	/**
	 * Constructs User Overview
	 */
	public UserOverview() {
		super(Messages.USER_OVERVIEW_TITLE);
	}

	@Override
	protected List<String> createHeadings() {
		final ArrayList<String> headings = new ArrayList<String>(5);

		headings.add(Messages.USER_OVERVIEW_USER);
		headings.add(Messages.USER_OVERVIEW_ENTRIES);
		headings.add(Messages.USER_OVERVIEW_BIRTHDAY);
		headings.add(Messages.USER_OVERVIEW_GENDER);
		headings.add(Messages.USER_OVERVIEW_VALID);
		headings.add(Messages.USER_OVERVIEW_GROUPS);

		return headings;
	}

	@Override
	protected IStatus createContent(IProgressMonitor monitor) {
		// initialize maps and lists
		final List<Comparable<?>[]> content = new ArrayList<Comparable<?>[]>(5);
		final HashMap<String, Integer> userEntryMap = new HashMap<String, Integer>();

		// create queries
		final Query<Anwender> userQuery = new Query<Anwender>(Anwender.class);
		final Query<Konsultation> consultQuery = new Query<Konsultation>(Konsultation.class);

		// execute queries
		final List<Anwender> users = userQuery.execute();
		final List<Konsultation> consults = consultQuery.execute();

		// start the task
		monitor.beginTask(Messages.CALCULATING, users.size() + consults.size());

		// sum up user entries
		monitor.subTask("Counting user entries");
		for (final Konsultation consult : consultQuery.execute()) {
			String author = consult.getAuthor();
			if (!author.equals(StringUtils.EMPTY)) {
				int count = 1;
				if (userEntryMap.containsKey(author)) {
					count = userEntryMap.get(author);
					count++;
				}
				userEntryMap.put(author, count);
				
				monitor.worked(1); // monitoring
			}
		}

		// iterate over users and create dataset
		monitor.subTask("Computing results");
		for (final Anwender anwender : users) {
			// check for cancelation
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			final String valid = (anwender.isValid() == true) ? Messages.USER_OVERVIEW_YES : Messages.USER_OVERVIEW_NO;
			final String group = (anwender.getInfoElement("Groups") != null) ? anwender.getInfoElement("Groups")
					.toString() : Messages.USER_OVERVIEW_UNDEFINED;

			final String username = anwender.getLabel(true);
			final Integer entryCount = userEntryMap.containsKey(username) ? userEntryMap.get(username) : new Integer(0);

			final Comparable<?>[] row = { username, entryCount, anwender.getGeburtsdatum(), anwender.getGeschlecht(),
					valid, group };

			content.add(row);

			monitor.worked(1); // monitoring
		}

		// set content
		this.dataSet.setContent(content);

		// job finished successfully
		monitor.done();
		return Status.OK_STATUS;
	}

	/**
	 * @see ch.unibe.iam.scg.archie.model.AbstractDataProvider#getDescription()
	 */
	@Override
	public String getDescription() {
		return Messages.USER_OVERVIEW_DESCRIPTION;
	}
}