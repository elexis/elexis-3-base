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

import ch.elexis.Desk;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.data.Patient;
import ch.elexis.icpc.Episode;
import ch.elexis.icpc.Messages;
import ch.elexis.util.SWTHelper;

public class EditEpisodeDialog extends TitleAreaDialog {
	private static final int ACTIVE_INDEX = 0;
	private static final int INACTIVE_INDEX = 1;
	
	private Episode episode;
	
	private Text tStartDate;
	private Text tTitle;
	private Text tNumber;
	private Combo cStatus;
	
	/**
	 * Create a new dialog for editing episodes. Passing <code>null</code> as episode creates a new
	 * episode.
	 * 
	 * @param parentShell
	 *            the parent shell
	 * @param episode
	 *            the episode to edit, or null if a new episode should be created
	 */
	public EditEpisodeDialog(Shell parentShell, Episode episode){
		super(parentShell);
		this.episode = episode;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
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
	private void initialize(){
		if (episode == null) {
			// new episode
			
			cStatus.select(ACTIVE_INDEX);
		} else {
			// existing episode
			
			String startDate = episode.get("StartDate");
			String title = episode.get("Title");
			String number = episode.get("Number");
			int status = episode.getStatus();
			
			tStartDate.setText(startDate);
			tTitle.setText(title);
			tNumber.setText(number);
			
			if (status == Episode.ACTIVE) {
				cStatus.select(ACTIVE_INDEX);
			} else {
				cStatus.select(INACTIVE_INDEX);
			}
			
		}
	}
	
	@Override
	public void create(){
		super.create();
		
		getShell().setText(Messages.EpisodeEditDialog_Title);
		
		if (episode == null) {
			setTitle(Messages.EpisodeEditDialog_Create);
		} else {
			setTitle(Messages.EpisodeEditDialog_Edit);
		}
		setMessage(Messages.EpisodeEditDialog_EnterData);
		
		setTitleImage(Desk.getImage(Desk.IMG_LOGO48));
	}
	
	@Override
	protected void okPressed(){
		String startDate = tStartDate.getText();
		String title = tTitle.getText();
		String number = tNumber.getText();
		
		int status;
		if (cStatus.getSelectionIndex() == ACTIVE_INDEX) {
			status = Episode.ACTIVE;
		} else {
			status = Episode.INACTIVE;
		}
		
		if (episode == null) {
			// create new episode
			
			Patient actPatient = ElexisEventDispatcher.getSelectedPatient();
			if (actPatient != null) {
				episode = new Episode(actPatient, title);
				episode.set(new String[] {
					"StartDate", "Number"
				}, new String[] {
					startDate, number
				});
				episode.setStatus(status);
				ElexisEventDispatcher.update(episode);
			}
		} else {
			// modify existing episode
			
			episode.set(new String[] {
				"Title", "StartDate", "Number"
			}, new String[] {
				title, startDate, number
			});
			episode.setStatus(status);
			ElexisEventDispatcher.update(episode);
		}
		
		super.okPressed();
	}
	
}
