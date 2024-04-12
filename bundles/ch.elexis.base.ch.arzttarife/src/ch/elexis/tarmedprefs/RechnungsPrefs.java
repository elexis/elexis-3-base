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

package ch.elexis.tarmedprefs;

import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

import ch.elexis.TarmedRechnung.TarmedACL;
import ch.elexis.base.ch.arzttarife.importer.TrustCenters;
import ch.elexis.base.ch.arzttarife.tarmed.MandantType;
import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.constants.XidConstants;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.dialogs.KontaktExtDialog;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Organisation;
import ch.elexis.data.Query;
import ch.rgw.tools.StringTool;

public class RechnungsPrefs extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String PREF_ADDCHILDREN = "tarmed/addchildrentp";

	Combo cbMands;
	HashMap<String, Mandant> hMandanten;
	Hyperlink hTreat, hPost, hBank;
	IHyperlinkListener hDetailListener;
	FocusListener focusListener;
	Text tTreat, tPost, tBank;
	Button bPost;
	Button bBank;
	IMandator actMandant;
	IContact actBank;
	Button bUseTC;
	Combo cbTC;
	Button bBillsElec;
	// Button bUseEDA;
	// Button bWithImage;

	private ResponsibleComposite responsible;
	private ComboViewer cvMandantType;
	private Label lblFixProvider;

	static TarmedACL ta = TarmedACL.getInstance();

	static final String[] ExtFlds = { "Anrede=" + XidConstants.XID_KONTAKT_ANREDE, //$NON-NLS-1$
			"Kanton=" + XidConstants.XID_KONTAKT_KANTON, //$NON-NLS-1$
			"EAN=" + DOMAIN_EAN, "NIF=" + TarmedRequirements.DOMAIN_NIF, //$NON-NLS-1$ //$NON-NLS-2$
			"KSK=" + TarmedRequirements.DOMAIN_KSK, ta.ESR5OR9, ta.ESRPLUS, ta.TIERS, ta.SPEC, //$NON-NLS-1$
			ta.KANTON, ta.LOCAL, ta.DIAGSYS, "Rolle=" + XidConstants.XID_KONTAKT_ROLLE };

	public RechnungsPrefs() {
		super(Messages.RechnungsPrefs_BillPrefs); // $NON-NLS-1$
	}

	@Override
	public void dispose() {

	}

	@Override
	protected Control createContents(Composite parent) {
		Color blau = UiDesk.getColor(UiDesk.COL_BLUE);
		hDetailListener = new DetailListener();
		focusListener = new TextListener();
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());
		hMandanten = new HashMap<String, Mandant>();
		cbMands = new Combo(ret, SWT.READ_ONLY);
		cbMands.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		cbMands.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = cbMands.getSelectionIndex();
				if (i == -1) {
					return;
				}
				Mandant selectedMandant = hMandanten.get(cbMands.getItem(i));
				setMandant(NoPoUtil.loadAsIdentifiable(selectedMandant, IMandator.class).get());

			}

		});
		Query<Mandant> qbe = new Query<Mandant>(Mandant.class);
		List<Mandant> list = qbe.execute();

		Collections.sort(list, new Comparator<Mandant>() {
			@Override
			public int compare(Mandant o1, Mandant o2) {
				return o1.getLabel(true).compareToIgnoreCase(o2.getLabel(true));
			}

		});

		for (Mandant m : list) {
			cbMands.add(m.getLabel());
			hMandanten.put(m.getLabel(), m);
		}
		Group adrs = new Group(ret, SWT.NONE);
		adrs.setLayout(new GridLayout(2, false));
		adrs.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		adrs.setText(Messages.RechnungsPrefs_BillDetails); // $NON-NLS-1$
		hTreat = new Hyperlink(adrs, SWT.NONE);
		hTreat.setText(Messages.RechnungsPrefs_Treator); // $NON-NLS-1$
		hTreat.setForeground(blau);
		hTreat.addHyperlinkListener(hDetailListener);
		tTreat = new Text(adrs, SWT.BORDER | SWT.READ_ONLY);
		tTreat.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Label lMandantType = new Label(adrs, SWT.NONE);
		lMandantType.setText(Messages.RechnungsPrefs_MandantType); // $NON-NLS-1$
		lMandantType.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		cvMandantType = new ComboViewer(adrs);
		cvMandantType.setContentProvider(new ArrayContentProvider());
		cvMandantType.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof MandantType) {
					if (element == MandantType.SPECIALIST) {
						return Messages.RechnungsPrefs_MandantType_SPECIALIST;
					}
					if (element == MandantType.PRACTITIONER) {
						return Messages.RechnungsPrefs_MandantType_PRACTITIONER;
					}
					if (element == MandantType.TARPSYAPPRENTICE) {
						return Messages.RechnungsPrefs_MandantType_TARPSYAPPRENTICE;
					}
				}
				return element.toString();
			};
		});
		cvMandantType.setInput(MandantType.values());
		cvMandantType.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof StructuredSelection && !selection.isEmpty()) {
					Object element = ((StructuredSelection) selection).getFirstElement();
					if (element instanceof MandantType) {
						if (actMandant != null) {
							IMandator mandator = CoreModelServiceHolder.get().load(actMandant.getId(), IMandator.class)
									.get();
							ArzttarifeUtil.setMandantType(mandator, (MandantType) element);
							CoreModelServiceHolder.get().save(mandator);
						}
					}
				}
			}
		});
		cvMandantType.getCombo().setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		// bills electronically
		bBillsElec = new Button(adrs, SWT.CHECK);
		bBillsElec.setText("Bills electronically");
		if (actMandant != null) {
			bBillsElec.setSelection(
					ConfigServiceHolder.get().get(actMandant, PreferenceConstants.BILL_ELECTRONICALLY, false));
		}
		bBillsElec.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigServiceHolder.get().set(actMandant, PreferenceConstants.BILL_ELECTRONICALLY,
						bBillsElec.getSelection());
			}
		});

		// Finanzinstitut
		// TODO better layout

		GridData gd;

		Composite cFinanzinstitut = new Composite(adrs, SWT.NONE);
		cFinanzinstitut.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		cFinanzinstitut.setLayout(new GridLayout(2, false));

		Label lFinanzinstitut = new Label(cFinanzinstitut, SWT.NONE);
		lFinanzinstitut.setText(Messages.RechnungsPrefs_Financeinst); // $NON-NLS-1$
		lFinanzinstitut.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

		bPost = new Button(cFinanzinstitut, SWT.RADIO);
		gd = SWTHelper.getFillGridData(1, false, 1, false);
		gd.verticalAlignment = SWT.TOP;
		bPost.setLayoutData(gd);
		bPost.setText(Messages.RechnungsPrefs_post); // $NON-NLS-1$

		bPost.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (bPost.getSelection()) {
					// check if Bank has been chosen
					if (actBank != null) {
						// clear all bank data
						actBank = null;
						actMandant.setExtInfo(ta.RNBANK, StringUtils.EMPTY);
						// clear data set with BankLister dialog
						actMandant.setExtInfo(ta.ESRNUMBER, StringUtils.EMPTY);
						actMandant.setExtInfo(ta.ESRSUB, StringUtils.EMPTY); // $NON-NLS-1$
						actMandant.setExtInfo(Messages.RechnungsPrefs_department, StringUtils.EMPTY);
						actMandant.setExtInfo(Messages.RechnungsPrefs_POBox, StringUtils.EMPTY);
						actMandant.setExtInfo("IBAN", StringUtils.EMPTY);
						CoreModelServiceHolder.get().save(actMandant);
					}

					// check if Post account is already available
					if (StringTool.isNothing(actMandant.getExtInfo(ta.ESRNUMBER))) {
						new PostDialog(getShell()).open();
					}

					// update widgets
					setMandant(actMandant);
				}
			}
		});

		Composite cPost = new Composite(cFinanzinstitut, SWT.NONE);
		gd = SWTHelper.getFillGridData(1, true, 1, false);
		gd.verticalAlignment = SWT.TOP;
		cPost.setLayoutData(gd);
		cPost.setLayout(new GridLayout(1, false));
		hPost = new Hyperlink(cPost, SWT.NONE);
		hPost.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		hPost.setText(Messages.RechnungsPrefs_POAccount); // $NON-NLS-1$
		hPost.setForeground(blau);
		hPost.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				new PostDialog(getShell()).open();
				// update widgets
				setMandant(actMandant);
			}

		});
		tPost = new Text(cPost, SWT.BORDER | SWT.READ_ONLY);
		tPost.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		bBank = new Button(cFinanzinstitut, SWT.RADIO);
		gd = SWTHelper.getFillGridData(1, false, 1, false);
		gd.verticalAlignment = SWT.TOP;
		bBank.setLayoutData(gd);
		bBank.setText(Messages.RechnungsPrefs_bank); // $NON-NLS-1$
		bBank.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (bBank.getSelection()) {
					if (actBank == null) {
						// invalidate available post data
						actMandant.setExtInfo(ta.ESRNUMBER, StringUtils.EMPTY);
						CoreModelServiceHolder.get().save(actMandant);
						new BankLister(getShell()).open();
					}
				}
			}
		});

		Composite cBank = new Composite(cFinanzinstitut, SWT.NONE);
		gd = SWTHelper.getFillGridData(1, true, 1, false);
		gd.verticalAlignment = SWT.TOP;
		cBank.setLayoutData(gd);
		cBank.setLayout(new GridLayout(1, false));
		hBank = new Hyperlink(cBank, SWT.NONE);
		hBank.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		hBank.setText(Messages.RechnungsPrefs_bankconnection); // $NON-NLS-1$
		hBank.setForeground(blau);
		hBank.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				// KontaktSelektor dlg=new
				// KontaktSelektor(getShell(),Organisation.class,"Bankverbindung
				// auswählen","Bitte geben Sie an, auf welches Finanzinstitut\nIhre
				// Einuahlungsscheine lauten sollen");
				BankLister dlg = new BankLister(getShell());
				dlg.open();
			}

		});
		tBank = new Text(cBank, SWT.BORDER | SWT.READ_ONLY);
		tBank.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		// Trust Center

		Group gTC = new Group(ret, SWT.NONE);
		gTC.setText(Messages.RechnungsPrefs_trustcenter); // $NON-NLS-1$
		gTC.setLayout(new GridLayout());
		gTC.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		bUseTC = new Button(gTC, SWT.CHECK);
		bUseTC.setText(Messages.RechnungsPrefs_TrustCenterUsed); // $NON-NLS-1$
		bUseTC.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IMandator mandator = CoreModelServiceHolder.get().load(actMandant.getId(), IMandator.class)
						.orElse(null);
				if (mandator != null) {
					TarmedRequirements.setHasTCContract(mandator, bUseTC.getSelection());
					CoreModelServiceHolder.get().save(mandator);
				}
			}

		});
		cbTC = new Combo(gTC, SWT.NONE);
		for (String k : TrustCenters.tc.keySet()) {
			cbTC.add(k);
		}
		cbTC.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IMandator mandator = CoreModelServiceHolder.get().load(actMandant.getId(), IMandator.class)
						.orElse(null);
				if (mandator != null) {
					TarmedRequirements.setTC(mandator, cbTC.getText());
					CoreModelServiceHolder.get().save(mandator);
				}
			}
		});

		Group gResponsible = new Group(ret, SWT.NONE);
		gResponsible.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		gResponsible.setLayout(new FillLayout());
		gResponsible.setText(Messages.RechnungsPrefs_Responsible_Doctor);
		responsible = new ResponsibleComposite(gResponsible, SWT.NONE);

		/*
		 * bUseEDA=new Button(gTC,SWT.CHECK);
		 * bUseEDA.setText(Messages.RechnungsPrefs_TrustCewntereDA);
		 * bUseEDA.addSelectionListener(new SelectionAdapter(){
		 *
		 * @Override public void widgetSelected(SelectionEvent e) {
		 * actMandant.setInfoElement(PreferenceConstants.USEEDA,
		 * bUseEDA.getSelection()==true ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$ }
		 *
		 * }); bWithImage=new Button(gTC,SWT.CHECK); bWithImage.setText(Messages.
		 * getString("RechnungsPrefs.ImagesToTrustCenter")); //$NON-NLS-1$
		 * bWithImage.addSelectionListener(new SelectionAdapter(){
		 *
		 * @Override public void widgetSelected(SelectionEvent e) {
		 * actMandant.setInfoElement(PreferenceConstants.TCWITHIMAGE,
		 * bWithImage.getSelection()==true ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$ }
		 *
		 * });
		 */

		Group gAuto = new Group(ret, SWT.NONE);
		gAuto.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		gAuto.setLayout(new FillLayout());
		final Button bAddChildren = new Button(gAuto, SWT.CHECK);
		bAddChildren.setText("Kinderzuschläge automatisch verrechnen");
		bAddChildren.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigServiceHolder.setMandator(PREF_ADDCHILDREN, bAddChildren.getSelection());
			}

		});
		bAddChildren.setSelection(ConfigServiceHolder.getMandator(PREF_ADDCHILDREN, false));

		Group gFixProvider = new Group(ret, SWT.NONE);
		gFixProvider.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		gFixProvider.setLayout(new GridLayout(2, false));
		gFixProvider.setText("Fixer Leistungserbringer (für alle Mandanten)");

		Hyperlink fixProvider = new Hyperlink(gFixProvider, SWT.NONE);
		fixProvider.setText("Fixer Leistungserbringer");
		fixProvider.setForeground(blau);
		fixProvider.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				KontaktSelektor ks = new KontaktSelektor(UiDesk.getTopShell(), Kontakt.class, "Kontakt auswählen",
						"Bitte selektieren Sie den fixen Leistungserbringer", new String[] {});
				int ret = ks.open();
				if (ret == Window.OK) {
					Kontakt k = (Kontakt) ks.getSelection();
					ConfigServiceHolder.setGlobal(PreferenceConstants.TARMEDBIL_FIX_PROVIDER,
							(k != null) ? k.getId() : null);
					String label = (k != null) ? k.getLabel() : StringUtils.EMPTY;
					lblFixProvider.setText(label);
				} else {
					ConfigServiceHolder.setGlobal(PreferenceConstants.TARMEDBIL_FIX_PROVIDER, null);
					lblFixProvider.setText(StringConstants.EMPTY);
				}
				gFixProvider.layout();
			}
		});

		lblFixProvider = new Label(gFixProvider, SWT.NONE);
		lblFixProvider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		if (StringUtils.isNotBlank(ConfigServiceHolder.getGlobal(PreferenceConstants.TARMEDBIL_FIX_PROVIDER, null))) {
			Kontakt k = Kontakt.load(ConfigServiceHolder.getGlobal(PreferenceConstants.TARMEDBIL_FIX_PROVIDER, null));
			String label = (k != null) ? k.getLabel() : StringUtils.EMPTY;
			lblFixProvider.setText(label);
		}

		cbMands.select(0);
		Mandant selectedMandant = hMandanten.get(cbMands.getItem(0));
		setMandant(NoPoUtil.loadAsIdentifiable(selectedMandant, IMandator.class).get());
		return ret;
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Automatisch erstellter Methoden-Stub

	}

	class TextListener extends FocusAdapter {

		@Override
		public void focusLost(FocusEvent e) {
			Text t = (Text) e.getSource();
			String fld = (String) t.getData();
			actMandant.setExtInfo(fld, t.getText());
			CoreModelServiceHolder.get().save(actMandant);
		}

	}

	class DetailListener extends HyperlinkAdapter {

		@Override
		public void linkActivated(HyperlinkEvent e) {
			if (actMandant == null) {
				return;
			}
			KontaktExtDialog dlg = new KontaktExtDialog(getShell(), actMandant, ExtFlds);
			dlg.create();
			dlg.setTitle(Messages.RechnungsPrefs_MandatorDetails); // $NON-NLS-1$

			dlg.open();
		}

	}

	class BankLister extends TitleAreaDialog {
		final String[] flds = { Messages.RechnungsPrefs_department, Messages.RechnungsPrefs_POBox, ta.ESRNUMBER,
				ta.ESRSUB, "IBAN" };

		private Text tInvoiceInfo;
		private KontaktExtDialog.ExtInfoTable exTable;

		BankLister(Shell shell) {
			super(shell);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite ret = new Composite(parent, SWT.NONE);
			ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
			ret.setLayout(new GridLayout(2, false));
			Hyperlink hlBank = UiDesk.getToolkit().createHyperlink(ret, Messages.RechnungsPrefs_FinanceInst, SWT.NONE); // $NON-NLS-1$
			hlBank.addHyperlinkListener(new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					KontaktSelektor ksl = new KontaktSelektor(getShell(), Organisation.class,
							Messages.RechnungsPrefs_paymentinst, Messages.RechnungsPrefs_PleseChooseBank,
							new String[] { Organisation.FLD_NAME1, Organisation.FLD_NAME2 }); // $NON-NLS-1$
																								// //$NON-NLS-2$
					if (ksl.open() == Dialog.OK) {
						actBank = NoPoUtil.loadAsIdentifiable((Kontakt) ksl.getSelection(), IContact.class)
								.orElse(null);
						actMandant.setExtInfo(ta.RNBANK, actBank.getId());
						CoreModelServiceHolder.get().save(actMandant);
					}
					updateMandantContactHyper(hlBank, ta.RNBANK);
				}
			});
			updateMandantContactHyper(hlBank, ta.RNBANK);
			hlBank.setLayoutData(SWTHelper.getFillGridData(2, true, 1, true));

			Hyperlink hlOwner = UiDesk.getToolkit().createHyperlink(ret, "Kontoinhaber", SWT.NONE); //$NON-NLS-1$
			hlOwner.addHyperlinkListener(new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					KontaktSelektor ksl = new KontaktSelektor(getShell(), Kontakt.class, "Kontoinhaber",
							"Den Kontoinhaber auswählen, falls der nicht dem Mandanten entspricht.",
							new String[] { Kontakt.FLD_NAME1, Kontakt.FLD_NAME2 }); // $NON-NLS-1$ //$NON-NLS-2$
					if (ksl.open() == Dialog.OK) {
						Kontakt accountOwner = (Kontakt) ksl.getSelection();
						actMandant.setExtInfo(ta.RNACCOUNTOWNER, accountOwner.getId());
						CoreModelServiceHolder.get().save(actMandant);
					} else {
						actMandant.setExtInfo(ta.RNACCOUNTOWNER, null);
						CoreModelServiceHolder.get().save(actMandant);
					}
					updateMandantContactHyper(hlOwner, ta.RNACCOUNTOWNER);
				}

			});
			updateMandantContactHyper(hlOwner, ta.RNACCOUNTOWNER);
			hlOwner.setLayoutData(SWTHelper.getFillGridData(2, true, 1, true));

			Label lbl = new Label(ret, SWT.NONE);
			lbl.setText("Rechnungsinformationen");
			tInvoiceInfo = new Text(ret, SWT.BORDER | SWT.MULTI);
			GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
			gd.heightHint = 50;
			tInvoiceInfo.setLayoutData(gd);
			tInvoiceInfo.setToolTipText("Rechnungsinformationen (Optional, max. 140 Zeichen)");
			tInvoiceInfo.setTextLimit(140);
			if (actMandant.getExtInfo(ta.RNINFORMATION) != null) {
				tInvoiceInfo.setText((String) actMandant.getExtInfo(ta.RNINFORMATION));
			}

			exTable = new KontaktExtDialog.ExtInfoTable(parent, flds);
			exTable.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
			exTable.setKontakt(actMandant);
			return ret;
		}

		private void updateMandantContactHyper(Hyperlink hb, String objectKey) {
			String[] parts = hb.getText().split(" - ");
			if (actMandant != null && actMandant.getExtInfo(objectKey) != null) {
				IContact contact = CoreModelServiceHolder.get()
						.load((String) actMandant.getExtInfo(objectKey), IContact.class).orElse(null);
				if (contact != null) {
					if (parts.length == 1 || parts.length == 2) {
						hb.setText(parts[0] + " - " + contact.getLabel());
					}
					return;
				}
			}
			hb.setText(parts[0]);
		}

		@Override
		public void create() {
			super.create();
			getShell().setText(Messages.RechnungsPrefs_ChooseBank); // $NON-NLS-1$
			setTitle(actMandant.getLabel());
			setMessage(Messages.RechnungsPrefs_ChosseInst); // $NON-NLS-1$
		}

		@Override
		protected void okPressed() {
			if (StringUtils.isEmpty((String) actMandant.getExtInfo(ta.RNBANK))) {
				MessageDialog.openWarning(getShell(), Messages.RechnungsPrefs_ChooseBank,
						"Es wurde keine Finanzinstitut ausgewählt.");
				return;
			}
			exTable.okPressed(actMandant);
			setMandant(actMandant);
			// transfer IBAN information
			if (actMandant.getExtInfo(ta.RNACCOUNTOWNER) != null) {
				IContact accountOwner = CoreModelServiceHolder.get()
						.load((String) actMandant.getExtInfo(ta.RNACCOUNTOWNER), IContact.class).orElse(null);
				if (accountOwner != null) {
					accountOwner.setExtInfo("IBAN", actMandant.getExtInfo("IBAN"));
					CoreModelServiceHolder.get().save(accountOwner);
				}
			}
			if (StringUtils.isNotBlank(tInvoiceInfo.getText())) {
				actMandant.setExtInfo(ta.RNINFORMATION, tInvoiceInfo.getText());
			} else {
				actMandant.setExtInfo(ta.RNINFORMATION, null);
			}
			CoreModelServiceHolder.get().save(actMandant);
			super.okPressed();
		}

		@Override
		protected void cancelPressed() {
			setMandant(actMandant);
			super.cancelPressed();
		}
	}

	class PostDialog extends TitleAreaDialog {
		final String[] flds = { ta.ESRNUMBER, "IBAN" };
		KontaktExtDialog.ExtInfoTable exTable;

		PostDialog(Shell shell) {
			super(shell);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite ret = new Composite(parent, SWT.NONE);
			ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
			ret.setLayout(new GridLayout(2, false));
			exTable = new KontaktExtDialog.ExtInfoTable(parent, flds);
			exTable.setLayoutData(SWTHelper.getFillGridData(2, true, 1, true));
			exTable.setKontakt(actMandant);
			return ret;
		}

		@Override
		public void create() {
			super.create();
			getShell().setText(Messages.RechnungsPrefs_postAccount); // $NON-NLS-1$
			setTitle(actMandant.getLabel());
			setMessage(Messages.RechnungsPrefs_InfoPostAccount); // $NON-NLS-1$
		}

		@Override
		protected void okPressed() {
			exTable.okPressed(actMandant);
			setMandant(actMandant);
			super.okPressed();
		}

		@Override
		protected void cancelPressed() {
			setMandant(actMandant);
			super.cancelPressed();
		}
	}

	void setMandant(IMandator m) {
		actMandant = m;

		tTreat.setText(actMandant.getLabel());
		cvMandantType.setSelection(new StructuredSelection(ArzttarifeUtil.getMandantType(actMandant)));

		actBank = CoreModelServiceHolder.get().load((String) actMandant.getExtInfo(ta.RNBANK), IContact.class)
				.orElse(null);
		if (actBank != null) {
			tPost.setText(StringUtils.EMPTY);
			tBank.setText(actBank.getLabel());

			hBank.setEnabled(true);
			hPost.setEnabled(false);
		} else {
			tPost.setText(StringUtils.defaultString((String) actMandant.getExtInfo(ta.ESRNUMBER)));
			tBank.setText(StringUtils.EMPTY);

			hBank.setEnabled(false);
			hPost.setEnabled(true);
		}
		bPost.setSelection(actBank == null);
		bBank.setSelection(actBank != null);

		bUseTC.setSelection(TarmedRequirements
				.hasTCContract(CoreModelServiceHolder.get().load(actMandant.getId(), IMandator.class).orElse(null))); // $NON-NLS-1$
		// bUseEDA.setSelection(actMandant.getInfoString(PreferenceConstants.USEEDA).equals("1"));
		// //$NON-NLS-1$
		// bWithImage.setSelection(actMandant.getInfoString(PreferenceConstants.TCWITHIMAGE).equals("1"));
		// //$NON-NLS-1$

		String tcName = TarmedRequirements
				.getTCName(CoreModelServiceHolder.get().load(actMandant.getId(), IMandator.class).orElse(null));
		if (tcName != null) {
			cbTC.setText(tcName);
		} else {
			cbTC.setText(StringUtils.EMPTY);
		}

		bBillsElec.setSelection(
				ConfigServiceHolder.get().get(actMandant, PreferenceConstants.BILL_ELECTRONICALLY, false));

		responsible.setMandant(Mandant.load(m.getId()));
	}
}
