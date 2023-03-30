/*******************************************************************************
 * Copyright (c) 2006-2014, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    T. Huster - initial implementation
 *
 *******************************************************************************/
package ch.elexis.tarmedprefs;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.data.Mandant;
import ch.elexis.data.Query;

public class ResponsibleComposite extends Composite {

	private ComboViewer responsibleViewer;
	private Mandant mandant;

	private NoMandant noMandant;

	public ResponsibleComposite(Composite parent, int style) {
		super(parent, style);

		noMandant = new NoMandant();

		createContent();
	}

	private void createContent() {
		setLayout(new GridLayout());

		responsibleViewer = new ComboViewer(this);
		responsibleViewer.setContentProvider(new ArrayContentProvider());
		responsibleViewer.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		responsibleViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) responsibleViewer.getSelection();
				if (selection != null && !selection.isEmpty()) {
					Mandant selected = (Mandant) selection.getFirstElement();
					if (selected != noMandant) {
						mandant.setInfoElement(TarmedRequirements.RESPONSIBLE_INFO_KEY, selected.getId());
					} else {
						mandant.setInfoElement(TarmedRequirements.RESPONSIBLE_INFO_KEY, StringUtils.EMPTY);
					}
				}
			}
		});

		responsibleViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Mandant) {
					return ((Mandant) element).getLabel();
				}
				return super.getText(element);
			}
		});
	}

	public void setMandant(Mandant mandant) {
		this.mandant = mandant;
		updateViewer();
	}

	private void updateViewer() {
		if (mandant != null) {
			Query<Mandant> qbe = new Query<Mandant>(Mandant.class);
			List<Mandant> input = qbe.execute();
			input.add(0, noMandant);
			responsibleViewer.setInput(input);
			String responsibleId = (String) mandant.getInfoElement(TarmedRequirements.RESPONSIBLE_INFO_KEY);
			if (responsibleId != null && !responsibleId.isEmpty()) {
				Mandant responsible = Mandant.load(responsibleId);
				if (responsible != null && responsible.exists()) {
					responsibleViewer.setSelection(new StructuredSelection(responsible));
				}
			}
		} else {
			responsibleViewer.setInput(Collections.EMPTY_LIST);
		}
	}

	private class NoMandant extends Mandant {
		public NoMandant() {
		}

		@Override
		public String getLabel(boolean shortLabel) {
			return StringUtils.EMPTY;
		}
	}
}
