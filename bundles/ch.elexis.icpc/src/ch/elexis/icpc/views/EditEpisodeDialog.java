/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.icpc.views;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.icpc.Messages;
import ch.elexis.icpc.model.icpc.IcpcEpisode;
import ch.elexis.icpc.service.IcpcModelServiceHolder;

public class EditEpisodeDialog extends TitleAreaDialog {
	private static final int ACTIVE_INDEX = 0;
	private static final int INACTIVE_INDEX = 1;

	private IcpcEpisode episode;

	private Text tStartDate;
	private Text tTitle;
	private Text tNumber;
	private Combo cStatus;

	/**
	 * Create a new dialog for editing episodes. Passing <code>null</code> as
	 * episode creates a new episode.
	 *
	 * @param parentShell the parent shell
	 * @param episode     the episode to edit, or null if a new episode should be
	 *                    created
	 */
	public EditEpisodeDialog(Shell parentShell, IcpcEpisode episode) {
		super(parentShell);
		this.episode = episode;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout(2, false));

		new Label(ret, SWT.NONE).setText(Messages.StartDate);
		tStartDate = new Text(ret, SWT.NONE);
		tStartDate.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		new Label(ret, SWT.NONE).setText(Messages.Title);
		tTitle = new Text(ret, SWT.NONE);
		tTitle.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		new Label(ret, SWT.NONE).setText(Messages.Number);
		tNumber = new Text(ret, SWT.NONE);
		tNumber.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		new Label(ret, SWT.NONE).setText(Messages.Status);
		cStatus = new Combo(ret, SWT.SINGLE);
		cStatus.add(Messages.Active); // ACTIVE_INDEX
		cStatus.add(Messages.Inactive); // INACTIVE_INDEX

		initialize();

		return ret;
	}

	/**
	 * Set initial values of controls
	 */
	private void initialize() {
		if (episode == null) {
			// new episode

			cStatus.select(ACTIVE_INDEX);
		} else {
			// existing episode

			String startDate = episode.getStartDate();
			String title = episode.getTitle();
			String number = episode.getNumber();
			int status = episode.getStatus();

			tStartDate.setText(startDate);
			tTitle.setText(title);
			tNumber.setText(number);

			if (status == 1) {
				cStatus.select(ACTIVE_INDEX);
			} else {
				cStatus.select(INACTIVE_INDEX);
			}

		}
	}

	@Override
	public void create() {
		super.create();

		getShell().setText(Messages.EpisodeEditDialog_Title);

		if (episode == null) {
			setTitle(Messages.EpisodeEditDialog_Create);
		} else {
			setTitle(Messages.EpisodeEditDialog_Edit);
		}
		setMessage(Messages.EpisodeEditDialog_EnterData);

		setTitleImage(Images.IMG_LOGO.getImage());
	}

	@Override
	protected void okPressed() {
		String startDate = tStartDate.getText();
		String title = tTitle.getText();
		String number = tNumber.getText();

		int status;
		if (cStatus.getSelectionIndex() == ACTIVE_INDEX) {
			status = 1;
		} else {
			status = 0;
		}

		if (episode == null) {
			// create new episode

			IPatient actPatient = ContextServiceHolder.get().getActivePatient().orElse(null);
			if (actPatient != null) {
				episode = IcpcModelServiceHolder.get().create(IcpcEpisode.class);
				// new Episode(actPatient, title);
				episode.setPatient(actPatient);
				episode.setTitle(title);
				episode.setStartDate(startDate);
				episode.setNumber(number);
				episode.setStatus(status);
				IcpcModelServiceHolder.get().save(episode);
				ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, episode);
			}
		} else {
			// modify existing episode
			episode.setTitle(title);
			episode.setStartDate(startDate);
			episode.setNumber(number);
			episode.setStatus(status);
			IcpcModelServiceHolder.get().save(episode);
			ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, episode);
		}

		super.okPressed();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}
