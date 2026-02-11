/*******************************************************************************
 * Copyright (c) 2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    T. Huster - initial implementation
 *
 *******************************************************************************/

package ch.elexis.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.builder.IBilledBuilder;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.icons.Images;

public class ArzttarifRefcodesDialog extends Dialog {

	private IBilled billed;
	private Composite contentComposite;

	private List<RefCodeEditComposite> refcodesComposites;

	public ArzttarifRefcodesDialog(Shell shell, IBilled tl) {
		super(shell);
		refcodesComposites = new ArrayList<>();
		billed = tl;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		contentComposite = (Composite) super.createDialogArea(parent);
		contentComposite.setLayout(new GridLayout(2, false));

		Label lbl = new Label(contentComposite, SWT.NONE);
		lbl.setText(billed.getAmount() + "x " + billed.getText());
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		lbl = new Label(contentComposite, SWT.NONE);
		lbl.setText("Aufteilen zu Bezugsleistungen (selber Fall und selber Tag)");
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		ToolBarManager mgr = new ToolBarManager(SWT.RIGHT | SWT.FLAT);
		mgr.add(new Action() {
			@Override
			public ImageDescriptor getImageDescriptor() {
				return Images.IMG_NEW.getImageDescriptor();
			}

			@Override
			public void run() {
				addRefcodeEdit();
			}
		});
		mgr.createControl(contentComposite);

		return contentComposite;
	}

	private void addRefcodeEdit() {
		RefCodeEditComposite add = new RefCodeEditComposite(contentComposite, SWT.NONE);
		add.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		refcodesComposites.add(add);
		ArzttarifRefcodesDialog.this.getShell().pack(true);
	}

	private void removeRefcodeEdit(RefCodeEditComposite refCodeEditComposite) {
		((GridData) refCodeEditComposite.getLayoutData()).exclude = true;
		refCodeEditComposite.setVisible(false);
		refCodeEditComposite.dispose();
		refcodesComposites.remove(refCodeEditComposite);
		ArzttarifRefcodesDialog.this.getShell().pack(true);
	}

	@Override
	public void create() {
		super.create();
		getShell().setText(billed.getBillable().getCodeSystemName() + "-Bezüge herstellen: " + billed.getCode());
	}

	@Override
	protected void okPressed() {
		if (!refcodesComposites.isEmpty()) {
			if (isValid()) {
				refcodesComposites.forEach(rc -> rc.apply(billed));
				ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, billed.getEncounter());
				super.okPressed();
			} else {
				MessageDialog.openWarning(getShell(), "Warnung",
						"Summe der Bezüge ist grösser als die ursprüngliche Menge.");
			}
		} else {
			super.okPressed();
		}
	}

	private boolean isValid() {
		return refcodesComposites.stream().mapToInt(rc -> rc.amountSpinner.getSelection()).sum() <= billed.getAmount();
	}

	private class RefCodeEditComposite extends Composite {

		private ComboViewer refcodeCombo;
		private Spinner amountSpinner;

		public RefCodeEditComposite(Composite parent, int style) {
			super(parent, style);
			setLayout(new GridLayout(3, false));
			createContent();
		}

		public void apply(IBilled billed) {
			if (amountSpinner.getSelection() > 0 && !refcodeCombo.getSelection().isEmpty()) {
				String bezug = (String) ((StructuredSelection) refcodeCombo.getSelection()).getFirstElement();
				int amount = amountSpinner.getSelection();
				if (amount == billed.getAmount()) {
					billed.setExtInfo("Bezug", bezug);
					CoreModelServiceHolder.get().save(billed);
				} else {
					IContact biller = ContextServiceHolder.get().getActiveUserContact().get();
					IBilled copy = new IBilledBuilder(CoreModelServiceHolder.get(), billed.getBillable(),
							billed.getEncounter(), biller).build();
					billed.copy(copy);
					copy.setAmount(amount);
					billed.setAmount(billed.getAmount() - amount);
					copy.setExtInfo("Bezug", bezug);
					CoreModelServiceHolder.get().save(Arrays.asList(billed, copy));
				}
			}
		}

		private void createContent() {
			refcodeCombo = new ComboViewer(this, SWT.BORDER);
			refcodeCombo.setContentProvider(ArrayContentProvider.getInstance());
			refcodeCombo.setLabelProvider(new LabelProvider());
			refcodeCombo.setInput(getPossibleRefCodes());

			amountSpinner = new Spinner(this, SWT.BORDER);
			amountSpinner.setValues(0, 0, (int) ArzttarifRefcodesDialog.this.billed.getAmount(), 0, 1, 1);

			ToolBarManager mgr = new ToolBarManager(SWT.RIGHT | SWT.FLAT);
			mgr.add(new Action() {
				@Override
				public ImageDescriptor getImageDescriptor() {
					return Images.IMG_DELETE.getImageDescriptor();
				}

				@Override
				public void run() {
					removeRefcodeEdit(RefCodeEditComposite.this);
				}
			});
			mgr.createControl(this);
		}

		private List<String> getPossibleRefCodes() {
			IEncounter encounter = billed.getEncounter();

			IQuery<IEncounter> query = CoreModelServiceHolder.get().getQuery(IEncounter.class);
			query.and(ModelPackage.Literals.IENCOUNTER__COVERAGE, COMPARATOR.EQUALS, encounter.getCoverage());
			query.and(ModelPackage.Literals.IENCOUNTER__DATE, COMPARATOR.EQUALS, encounter.getDate());
			List<IEncounter> encounters = query.execute();
			if (!encounters.isEmpty()) {
				List<String> ret = new ArrayList<String>();
				HashSet<String> uniqueCodes = new HashSet<String>();
				Class<? extends IBillable> billableClazz = billed.getBillable().getClass();
				encounters.forEach(e -> {
					List<String> codes = e.getBilled().stream().filter(b -> billableClazz.isInstance(b.getBillable()))
							.map(b -> b.getCode()).collect(Collectors.toList());
					uniqueCodes.addAll(codes);
				});
				ret.addAll(uniqueCodes);
				Collections.sort(ret);
				return ret;
			}
			return Collections.emptyList();
		}
	}

	public static boolean isArzttarif(IBillable billable) {
		return billable instanceof ITardocLeistung || billable instanceof ITarmedLeistung;
	}
}
