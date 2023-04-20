/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.base.ch.ebanking.esr;

import java.text.MessageFormat;
import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.elexis.base.ch.ebanking.model.IEsrRecord;
import ch.elexis.base.ch.ebanking.model.service.holder.ModelServiceHolder;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IPayment;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.InvoiceServiceHolder;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.icons.ImageSize;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.LabeledInputField;
import ch.elexis.core.ui.util.LabeledInputField.InputData;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.rgw.tools.Money;

/**
 * Change an ESR record manually
 *
 * @author gerry
 *
 */
public class ESRRecordDialog extends TitleAreaDialog {
	private IEsrRecord rec;
	private boolean bBooked;
	private Button bKeep, bBook, bUnbook, bDelete;
	private Label lFile;
	private LabeledInputField.AutoForm af;

	private InputData[] fields = {
			new InputData(Messages.ESRRecordDialog_readInDate, "importDateString", InputData.Typ.DATE, null), //$NON-NLS-1$
			new InputData(Messages.ESRRecordDialog_esrType, "code", InputData.Typ.STRING, null), //$NON-NLS-1$
			new InputData(Messages.ESRRecordDialog_bookedDate, "processingDateString", InputData.Typ.DATE, null), //$NON-NLS-1$
			new InputData(Messages.ESRRecordDialog_addedDate, "valutaDateString", InputData.Typ.DATE, null), //$NON-NLS-1$
			new InputData(Messages.ESRRecordDialog_receivedDate, "dateString", InputData.Typ.DATE, null), //$NON-NLS-1$
			new InputData(Messages.ESRRecordDialog_amount, "amount", InputData.Typ.CURRENCY, null), //$NON-NLS-1$
			new InputData(Messages.ESRRecordDialog_billNr, "invoice", new LabeledInputField.IContentProvider() { //$NON-NLS-1$
				public void displayContent(Object po, InputData ltf) {
					IInvoice rn = rec.getInvoice();
					if (rn == null) {
						ltf.setText("??"); //$NON-NLS-1$
					} else {
						ltf.setText(rn.getNumber());
					}
				}

				public void reloadContent(Object po, InputData ltf) {
					InputDialog id = new InputDialog(getShell(), Messages.ESRRecordDialog_changeBillNr,
							Messages.ESRRecordDialog_pleaseEnterNewBilNr, ltf.getText(), null);
					if (id.open() == Dialog.OK) {
						String rnid = new Query<Rechnung>(Rechnung.class).findSingle("RnNummer", "=", id //$NON-NLS-1$ //$NON-NLS-2$
								.getValue());
						int err = 0;
						if (rnid != null) {
							Rechnung r = Rechnung.load(rnid);
							if (r.isAvailable()) {
								Fall fall = r.getFall();
								if (fall.isAvailable()) {
									Patient pat = fall.getPatient();
									Mandant mn = r.getMandant();
									if (pat.isAvailable()) {
										rec.setInvoice(r.toIInvoice()); // $NON-NLS-1$
										// ltf.setText(r.getNr());
										rec.setPatient(pat.toIPatient()); // $NON-NLS-1$
										if (mn != null && mn.isValid()) {
											rec.setMandator(mn.toIContact()); // $NON-NLS-1$
										}
										CoreModelServiceHolder.get().save(rec);
										af.reload(rec);
									} else {
										err = 4;
									}
								} else {
									err = 3;
								}

							} else {
								err = 2;
							}

						} else {
							err = 1;
						}
						if (err != 0) {
							SWTHelper.showError(Messages.ESRRecordDialog_billNotFound,
									MessageFormat.format(Messages.ESRRecordDialog_noValidBillFound, id.getValue()));
						}
					}
				}

			}), new InputData(Messages.ESRRecordDialog_patient, "patient", new LabeledInputField.IContentProvider() { //$NON-NLS-1$

				public void displayContent(Object po, InputData ltf) {
					ltf.setText(rec.getPatient() != null ? rec.getPatient().getLabel() : StringUtils.EMPTY);
				}

				public void reloadContent(Object po, InputData ltf) {
					KontaktSelektor ksl = new KontaktSelektor(getShell(), Patient.class,
							Messages.ESRRecordDialog_selectPatient, Messages.ESRRecordDialog_pleaseSelectPatient,
							Patient.DEFAULT_SORT);
					if (ksl.open() == Dialog.OK) {
						Patient actPatient = (Patient) ksl.getSelection();
						rec.setPatient(actPatient.toIPatient());
						CoreModelServiceHolder.get().save(rec);
						ltf.setText(actPatient.getLabel());
					}
				}
			})

	};

	public ESRRecordDialog(Shell shell, IEsrRecord element) {
		super(shell);
		rec = element;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		TableWrapLayout twl = new TableWrapLayout();
		ret.setLayout(twl);
		af = new LabeledInputField.AutoForm(ret, fields);
		af.setModelService(ModelServiceHolder.get());
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		af.setLayoutData(twd);
		lFile = new Label(ret, SWT.NONE);
		lFile.setText(Messages.ESRRecordDialog_file + rec.getFile());
		TableWrapData tw3 = new TableWrapData();
		tw3.grabHorizontal = true;
		lFile.setLayoutData(tw3);
		Composite cChoices = new Composite(ret, SWT.BORDER);
		TableWrapData tw2 = new TableWrapData();
		tw2.grabHorizontal = true;
		cChoices.setLayoutData(tw2);
		RowLayout rl = new RowLayout(SWT.VERTICAL);
		rl.fill = true;
		cChoices.setLayout(rl);
		bKeep = new Button(cChoices, SWT.RADIO);
		bKeep.setText(Messages.ESRRecordDialog_dontchange);
		bBook = new Button(cChoices, SWT.RADIO);
		bBook.setText(Messages.ESRRecordDialog_bookRecord);
		bUnbook = new Button(cChoices, SWT.RADIO);
		bUnbook.setText(Messages.ESRRecordDialog_dontBookRecord);
		bDelete = new Button(cChoices, SWT.RADIO);
		bDelete.setText(Messages.ESRRecordDialog_deleteRecord);
		bBooked = !LocalDate.EPOCH.equals(rec.getBookedDate());
		bKeep.setSelection(true);

		af.reload(rec);
		ret.pack();
		return ret;
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.ESRRecordDialog_editRecord);
		setMessage(Messages.ESRRecordDialog_warningEditing);
		setTitleImage(Images.IMG_LOGO.getImage(ImageSize._75x66_TitleDialogIconSize));
		getShell().setText(Messages.ESRRecordDialog_detailsForESRRecord);

	}

	@Override
	protected void okPressed() {
		if (bBook.getSelection()) {
			if (!bBooked) {
				Money zahlung = rec.getAmount();
				IInvoice rn = rec.getInvoice();
				IPayment zahlungsObj = InvoiceServiceHolder.get().addPayment(rn, zahlung,
						Messages.ESRRecordDialog_vESRForBill + rn.getNumber() + " / " //$NON-NLS-1$
						+ rec.getPatient().getCode());
				zahlungsObj.setDate(rec.getValutaDate());
				CoreModelServiceHolder.get().save(zahlungsObj);
				if (zahlungsObj != null && ESR.getAccount() != null) {
					InvoiceServiceHolder.get().getAccountTransaction(zahlungsObj).ifPresent(transaction -> {
						transaction.setAccount(ESR.getAccount());
						CoreModelServiceHolder.get().save(transaction);
					});
				}
				rec.setBookedDate(rec.getValutaDate());
				ModelServiceHolder.get().save(rec);
			}
		} else if (bUnbook.getSelection()) {
			if (bBooked) {
				Money zahlung = rec.getAmount();
				IInvoice rn = rec.getInvoice();
				IPayment zahlungsObj = InvoiceServiceHolder.get().addPayment(rn, zahlung.negate(),
						Messages.ESRRecordDialog_stornoESR + rn.getNumber() + " / " //$NON-NLS-1$
								+ rec.getPatient().getCode());
				if (zahlungsObj != null && ESR.getAccount() != null) {
					InvoiceServiceHolder.get().getAccountTransaction(zahlungsObj).ifPresent(transaction -> {
						transaction.setAccount(ESR.getAccount());
						CoreModelServiceHolder.get().save(transaction);
					});
				}
				rec.setBookedDate(null);
				ModelServiceHolder.get().save(rec);
			}
		} else if (bDelete.getSelection()) {
			ModelServiceHolder.get().delete(rec);
		}
		super.okPressed();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}
