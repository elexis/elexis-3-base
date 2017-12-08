/*******************************************************************************
 * Copyright (c) 2007-2013, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.iatrix.widgets.EnhancedTextFieldRO;

import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Konsultation;
import ch.rgw.tools.VersionedResource;
import ch.rgw.tools.VersionedResource.ResourceItem;

public class ChooseKonsRevisionDialog extends TitleAreaDialog {
	/**
	 * Version value indicatinig that no version has been chosen or the choice has been cancelled.
	 */
	public static final int NONE = -1;

	private int selectedVersion = NONE;

	private List<WidgetRow> widgetRows = new ArrayList<>();

	private Konsultation konsultation;

	public ChooseKonsRevisionDialog(final Shell parent, final Konsultation konsultation){
		super(parent);
		this.konsultation = konsultation;
	}

	@Override
	protected Control createDialogArea(final Composite parent){
		// parent has GridLayout with 1 column

		ScrolledComposite scrolledComposite =
			new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		scrolledComposite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Composite mainArea = new Composite(scrolledComposite, SWT.NONE);

		scrolledComposite.setContent(mainArea);

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 10;
		mainArea.setLayout(gridLayout);

		/*
		 * createViewer(mainArea);
		 */
		createWidgetRows(mainArea);

		// 1)
		// mainArea.setSize(mainArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		// 2)
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinSize(mainArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		return scrolledComposite;
	}

	/**
	 * Selection listener handling single selection of radio buttons. SWT only handles mutal
	 * selection if all radio buttons have the same parent. This is not the case in your layout.
	 */
	class RadioGroupListener implements SelectionListener {
		List<Button> buttons = new ArrayList<>();

		@Override
		public void widgetSelected(SelectionEvent e){
			// deselect other radio buttons
			for (Button other : buttons) {
				if (!other.equals(e.widget)) {
					other.setSelection(false);
				}
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e){
			widgetSelected(e);
		}

		/**
		 * Add a radio button to this group listener. The methods registers ourselves as a
		 * SelectionListener to the button.
		 *
		 * @param button
		 */
		public void addButton(Button button){
			buttons.add(button);
			button.addSelectionListener(this);
		}
	};

	private void createWidgetRows(Composite parent){
		if (konsultation != null) {

			RadioGroupListener radioGroupListener = new RadioGroupListener();

			VersionedResource vr = konsultation.getEintrag();
			int last = vr.getHeadVersion();
			for (int i = last; i >= 0; i--) {
				ResourceItem ri = vr.getVersion(i);

				int version = i;
				String label = ri.getLabel();
				String data = ri.data;
				String labelText = "Rev " + version + ": " + label;

				Composite rowComposite = new Composite(parent, SWT.NONE);
				rowComposite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
				GridLayout gridLayout = new GridLayout(1, false);
				gridLayout.verticalSpacing = 0;
				rowComposite.setLayout(gridLayout);

				Button radioButton = new Button(rowComposite, SWT.RADIO);
				GridData gd = SWTHelper.getFillGridData(1, true, 1, false);
				gd.verticalAlignment = GridData.BEGINNING;
				radioButton.setLayoutData(gd);
				radioButton.setText(labelText);
				radioGroupListener.addButton(radioButton);

				/*
				 * Label labelWidget = new Label(rowComposite, SWT.LEFT);
				 * labelWidget.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
				 * labelWidget.setText(labelText);
				 */

				/*
				 * Composite filler = new Composite(rowComposite, SWT.NONE); gd =
				 * SWTHelper.getFillGridData(1, false, 1, false); gd.widthHint = 0; gd.heightHint =
				 * 0; filler.setLayoutData(gd);
				 */

				EnhancedTextFieldRO text = new EnhancedTextFieldRO(rowComposite);
				text.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
				text.setText(data);

				WidgetRow widgetRow = new WidgetRow(radioButton, version);
				widgetRows.add(widgetRow);
			}

			// check the first radio button
			if (widgetRows.size() > 0) {
				widgetRows.get(0).radio.setSelection(true);
			}
		}
	}

	@Override
	public void create(){
		super.create();
		setMessage("Text-Version wählen");
		setTitle("Wählen Sie die gewünschte Version aus.");
		getShell().setText("Text-Version wählen");
		setTitleImage(Images.IMG_LOGO.getImage()); //$NON-NLS-1$
	}

	@Override
	protected void okPressed(){
		selectedVersion = NONE;

		// find the selected version
		for (WidgetRow widgetRow : widgetRows) {
			if (widgetRow.radio.getSelection()) {
				selectedVersion = widgetRow.version;
				break;
			}
		}

		super.okPressed();
	}

	/**
	 * Return the selected version, or ChooseKonsRevisionDialog.NONE if no version has been
	 * selected. This value is only valid after OK has been pressed.
	 *
	 * @return
	 */
	public int getSelectedVersion(){
		return selectedVersion;
	}

	class WidgetRow {
		Button radio;
		int version;

		WidgetRow(Button radio, int version){
			this.radio = radio;
			this.version = version;
		}
	}
}
