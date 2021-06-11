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

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
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
import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.constants.XidConstants;
import ch.elexis.core.data.activator.CoreHub;
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
import ch.rgw.io.Settings;
import ch.rgw.tools.StringTool;

public class RechnungsPrefs extends PreferencePage implements IWorkbenchPreferencePage {
	
	// indices for cbESROCRFontWeight
	private static final int FONT_MIN_INDEX = 0;
	private static final int FONT_NORMAL_INDEX = 1;
	private static final int FONT_BOLD_INDEX = 2;
	public static final String PREF_ADDCHILDREN = "tarmed/addchildrentp";
	
	Combo cbMands;
	HashMap<String, Mandant> hMandanten;
	Hyperlink hTreat, hPost, hBank;
	IHyperlinkListener hDetailListener;
	FocusListener focusListener;
	Text tTreat, tPost, tBank;
	Text tESRNormalFontName;
	Text tESRNormalFontSize;
	Text tESROCRFontName;
	Text tESROCRFontSize;
	Text tESRPrinterCorrectionX;
	Text tESRPrinterCorrectionY;
	Text tESRPrintCorrectionBaseX;
	Text tESRPrintCorrectionBaseY;
	Combo cbESROCRFontWeight;
	Button bPost;
	Button bBank;
	Mandant actMandant;
	Kontakt actBank;
	Button bUseTC;
	Combo cbTC;
	Button bBillsElec;
	// Button bUseEDA;
	// Button bWithImage;
	
	private ResponsibleComposite responsible;
	private ComboViewer cvMandantType;
	private Label lblFixProvider;
	
	static TarmedACL ta = TarmedACL.getInstance();
	
	static final String[] ExtFlds = {
		"Anrede=" + XidConstants.XID_KONTAKT_ANREDE, "Kanton=" + XidConstants.XID_KONTAKT_KANTON, //$NON-NLS-1$//$NON-NLS-2$
		"EAN=" + DOMAIN_EAN, "NIF=" + TarmedRequirements.DOMAIN_NIF, //$NON-NLS-1$ //$NON-NLS-2$
		"KSK=" + TarmedRequirements.DOMAIN_KSK, ta.ESR5OR9, ta.ESRPLUS, ta.TIERS, ta.SPEC, //$NON-NLS-1$
		ta.KANTON, ta.LOCAL, ta.DIAGSYS, "Rolle=" + XidConstants.XID_KONTAKT_ROLLE
	};
	
	public RechnungsPrefs(){
		super(Messages.RechnungsPrefs_BillPrefs); //$NON-NLS-1$
	}
	
	@Override
	public void dispose(){
		
	}
	
	@Override
	protected Control createContents(Composite parent){
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
			public void widgetSelected(SelectionEvent e){
				int i = cbMands.getSelectionIndex();
				if (i == -1) {
					return;
				}
				setMandant((Mandant) hMandanten.get(cbMands.getItem(i)));
				
			}
			
		});
		Query<Mandant> qbe = new Query<Mandant>(Mandant.class);
		List<Mandant> list = qbe.execute();
		for (Mandant m : list) {
			cbMands.add(m.getLabel());
			hMandanten.put(m.getLabel(), m);
		}
		Group adrs = new Group(ret, SWT.NONE);
		adrs.setLayout(new GridLayout(2, false));
		adrs.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		adrs.setText(Messages.RechnungsPrefs_BillDetails); //$NON-NLS-1$
		hTreat = new Hyperlink(adrs, SWT.NONE);
		hTreat.setText(Messages.RechnungsPrefs_Treator); //$NON-NLS-1$
		hTreat.setForeground(blau);
		hTreat.addHyperlinkListener(hDetailListener);
		tTreat = new Text(adrs, SWT.BORDER | SWT.READ_ONLY);
		tTreat.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		
		Label lMandantType = new Label(adrs, SWT.NONE);
		lMandantType.setText(Messages.RechnungsPrefs_MandantType); //$NON-NLS-1$
		lMandantType.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		
		cvMandantType = new ComboViewer(adrs);
		cvMandantType.setContentProvider(new ArrayContentProvider());
		cvMandantType.setLabelProvider(new LabelProvider() {
			public String getText(Object element){
				if (element instanceof MandantType) {
					if (element == MandantType.SPECIALIST) {
						return Messages.RechnungsPrefs_MandantType_SPECIALIST;
					}
					if (element == MandantType.PRACTITIONER) {
						return Messages.RechnungsPrefs_MandantType_PRACTITIONER;
					}
				}
				return element.toString();
			};
		});
		cvMandantType.setInput(MandantType.values());
		cvMandantType.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event){
				ISelection selection = event.getSelection();
				if (selection instanceof StructuredSelection && !selection.isEmpty()) {
					Object element = ((StructuredSelection) selection).getFirstElement();
					if (element instanceof MandantType) {
						if (actMandant != null) {
							ArzttarifeUtil.setMandantType(
								CoreModelServiceHolder.get()
									.load(actMandant.getId(), IMandator.class).get(),
								(MandantType) element);
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
			bBillsElec.setSelection(CoreHub.getUserSetting(actMandant)
				.get(PreferenceConstants.BILL_ELECTRONICALLY, false));
		}
		bBillsElec.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				Settings settings = CoreHub.getUserSetting(actMandant);
				settings.set(PreferenceConstants.BILL_ELECTRONICALLY, bBillsElec.getSelection());
				settings.flush();
			}
		});
		
		// Finanzinstitut
		// TODO better layout
		
		GridData gd;
		
		Composite cFinanzinstitut = new Composite(adrs, SWT.NONE);
		cFinanzinstitut.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		cFinanzinstitut.setLayout(new GridLayout(2, false));
		
		Label lFinanzinstitut = new Label(cFinanzinstitut, SWT.NONE);
		lFinanzinstitut.setText(Messages.RechnungsPrefs_Financeinst); //$NON-NLS-1$
		lFinanzinstitut.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		
		bPost = new Button(cFinanzinstitut, SWT.RADIO);
		gd = SWTHelper.getFillGridData(1, false, 1, false);
		gd.verticalAlignment = SWT.TOP;
		bPost.setLayoutData(gd);
		bPost.setText(Messages.RechnungsPrefs_post); //$NON-NLS-1$
		
		bPost.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				if (bPost.getSelection()) {
					// check if Bank has been chosen
					if (actBank != null && actBank.isValid()) {
						// clear all bank data
						actBank = null;
						actMandant.setExtInfoStoredObjectByKey(ta.RNBANK, ""); //$NON-NLS-1$
						// clear data set with BankLister dialog
						actMandant.setExtInfoStoredObjectByKey(ta.ESRNUMBER, ""); //$NON-NLS-1$
						actMandant.setExtInfoStoredObjectByKey(ta.ESRSUB,
							Messages.RechnungsPrefs_13); //$NON-NLS-1$
						actMandant.setExtInfoStoredObjectByKey(Messages.RechnungsPrefs_department,
							""); //$NON-NLS-1$
						actMandant.setExtInfoStoredObjectByKey(Messages.RechnungsPrefs_POBox, ""); //$NON-NLS-1$
						actMandant.setExtInfoStoredObjectByKey("IBAN", ""); //$NON-NLS-1$ 
					}
					
					// check if Post account is already available
					if (StringTool
						.isNothing(actMandant.getExtInfoStoredObjectByKey(ta.ESRNUMBER))) {
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
		hPost.setText(Messages.RechnungsPrefs_POAccount); //$NON-NLS-1$
		hPost.setForeground(blau);
		hPost.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e){
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
		bBank.setText(Messages.RechnungsPrefs_bank); //$NON-NLS-1$
		bBank.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				if (bBank.getSelection()) {
					if (actBank == null) {
						// invalidate available post data
						actMandant.setExtInfoStoredObjectByKey(ta.ESRNUMBER, ""); //$NON-NLS-1$
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
		hBank.setText(Messages.RechnungsPrefs_bankconnection); //$NON-NLS-1$
		hBank.setForeground(blau);
		hBank.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e){
				// KontaktSelektor dlg=new
				// KontaktSelektor(getShell(),Organisation.class,"Bankverbindung auswählen","Bitte geben Sie an, auf welches Finanzinstitut\nIhre Einuahlungsscheine lauten sollen");
				BankLister dlg = new BankLister(getShell());
				dlg.open();
			}
			
		});
		tBank = new Text(cBank, SWT.BORDER | SWT.READ_ONLY);
		tBank.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		
		// Trust Center
		
		Group gTC = new Group(ret, SWT.NONE);
		gTC.setText(Messages.RechnungsPrefs_trustcenter); //$NON-NLS-1$
		gTC.setLayout(new GridLayout());
		gTC.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		
		bUseTC = new Button(gTC, SWT.CHECK);
		bUseTC.setText(Messages.RechnungsPrefs_TrustCenterUsed); //$NON-NLS-1$
		bUseTC.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				IMandator mandator = CoreModelServiceHolder.get()
						.load(actMandant.getId(), IMandator.class).orElse(null);
				if(mandator != null) {
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
			public void widgetSelected(SelectionEvent e){
				IMandator mandator = CoreModelServiceHolder.get()
						.load(actMandant.getId(), IMandator.class).orElse(null);
				if(mandator != null) {
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
		 * bUseEDA=new Button(gTC,SWT.CHECK); bUseEDA.setText(Messages.RechnungsPrefs_TrustCewntereDA);
		 * bUseEDA.addSelectionListener(new
		 * SelectionAdapter(){
		 * 
		 * @Override public void widgetSelected(SelectionEvent e) {
		 * actMandant.setInfoElement(PreferenceConstants.USEEDA, bUseEDA.getSelection()==true ? "1"
		 * : "0"); //$NON-NLS-1$ //$NON-NLS-2$ }
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
		
		// OCR font
		
		addFontsGroup(ret);
		
		Group gAuto = new Group(ret, SWT.NONE);
		gAuto.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		gAuto.setLayout(new FillLayout());
		final Button bAddChildren = new Button(gAuto, SWT.CHECK);
		bAddChildren.setText("Kinderzuschläge automatisch verrechnen");
		bAddChildren.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e){
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
			public void linkActivated(HyperlinkEvent e){
				KontaktSelektor ks = new KontaktSelektor(UiDesk.getTopShell(), Kontakt.class,
					"Kontakt auswählen", "Bitte selektieren Sie den fixen Leistungserbringer",
					new String[] {});
				int ret = ks.open();
				if (ret == Window.OK) {
					Kontakt k = (Kontakt) ks.getSelection();
					ConfigServiceHolder.setGlobal(PreferenceConstants.TARMEDBIL_FIX_PROVIDER,
						(k != null) ? k.getId() : null);
					String label = (k != null) ? k.getLabel() : "";
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
		
		if (StringUtils.isNotBlank(
			ConfigServiceHolder.getGlobal(PreferenceConstants.TARMEDBIL_FIX_PROVIDER, null))) {
			Kontakt k = Kontakt.load(
				ConfigServiceHolder.getGlobal(PreferenceConstants.TARMEDBIL_FIX_PROVIDER, null));
			String label = (k != null) ? k.getLabel() : "";
			lblFixProvider.setText(label);
		}
		
		cbMands.select(0);
		setMandant((Mandant) hMandanten.get(cbMands.getItem(0)));
		return ret;
	}
	
	private void addFontsGroup(Composite ret){
		Group fonts = new Group(ret, SWT.NONE);
		fonts.setText(Messages.RechnungsPrefs_FontSlip); //$NON-NLS-1$
		fonts.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		fonts.setLayout(new GridLayout(2, false));
		
		FontsTextListener fontsTextListener = new FontsTextListener();
		
		String warning = Messages.RechnungsPrefs_FontWarning //$NON-NLS-1$
			+ Messages.RechnungsPrefs_FontWarning2 //$NON-NLS-1$
			+ Messages.RechnungsPrefs_FontWarning3; //$NON-NLS-1$
		Label lWarning = new Label(fonts, SWT.NONE);
		lWarning.setText(warning);
		lWarning.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		
		new Label(fonts, SWT.NONE).setText(Messages.RechnungsPrefs_Font); //$NON-NLS-1$
		tESRNormalFontName = new Text(fonts, SWT.BORDER | SWT.SINGLE);
		tESRNormalFontName.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tESRNormalFontName.addFocusListener(fontsTextListener);
		
		new Label(fonts, SWT.NONE).setText(Messages.RechnungsPrefs_Size); //$NON-NLS-1$
		tESRNormalFontSize = new Text(fonts, SWT.BORDER | SWT.SINGLE);
		tESRNormalFontSize.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tESRNormalFontSize.addFocusListener(fontsTextListener);
		
		new Label(fonts, SWT.NONE).setText(Messages.RechnungsPrefs_fontCodingLine); //$NON-NLS-1$
		tESROCRFontName = new Text(fonts, SWT.BORDER | SWT.SINGLE);
		tESROCRFontName.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tESROCRFontName.addFocusListener(fontsTextListener);
		
		new Label(fonts, SWT.NONE).setText(Messages.RechnungsPrefs_SizeCondingLine); //$NON-NLS-1$
		tESROCRFontSize = new Text(fonts, SWT.BORDER | SWT.SINGLE);
		tESROCRFontSize.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tESROCRFontSize.addFocusListener(fontsTextListener);
		
		new Label(fonts, SWT.NONE).setText(Messages.RechnungsPrefs_Weight); //$NON-NLS-1$
		cbESROCRFontWeight = new Combo(fonts, SWT.READ_ONLY);
		cbESROCRFontWeight.setLayoutData(SWTHelper.getFillGridData(1, false, 1, false));
		
		cbESROCRFontWeight.add(Messages.RechnungsPrefs_light); // FONT_MIN_INDEX //$NON-NLS-1$
		cbESROCRFontWeight.add(Messages.RechnungsPrefs_normal); // FONT_NORMAL_INDEX //$NON-NLS-1$
		cbESROCRFontWeight.add(Messages.RechnungsPrefs_bold); // FONT_BOLD_INDEX //$NON-NLS-1$
		
		cbESROCRFontWeight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				storeFontsTextValues();
			}
		});
		
		new Label(fonts, SWT.NONE).setText(Messages.RechnungsPrefs_horzCorrCodingLine); //$NON-NLS-1$
		tESRPrinterCorrectionX = new Text(fonts, SWT.BORDER | SWT.SINGLE);
		tESRPrinterCorrectionX.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tESRPrinterCorrectionX.addFocusListener(fontsTextListener);
		
		new Label(fonts, SWT.NONE).setText(Messages.RechnungsPrefs_vertCorrCodingLine); //$NON-NLS-1$
		tESRPrinterCorrectionY = new Text(fonts, SWT.BORDER | SWT.SINGLE);
		tESRPrinterCorrectionY.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tESRPrinterCorrectionY.addFocusListener(fontsTextListener);
		
		new Label(fonts, SWT.NONE).setText(Messages.RechnungsPrefs_horrzBaseOffset); //$NON-NLS-1$
		tESRPrintCorrectionBaseX = new Text(fonts, SWT.BORDER | SWT.SINGLE);
		tESRPrintCorrectionBaseX.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tESRPrintCorrectionBaseX.addFocusListener(fontsTextListener);
		
		new Label(fonts, SWT.NONE).setText(Messages.RechnungenPrefs_vertBaseOffset); //$NON-NLS-1$
		tESRPrintCorrectionBaseY = new Text(fonts, SWT.BORDER | SWT.SINGLE);
		tESRPrintCorrectionBaseY.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tESRPrintCorrectionBaseY.addFocusListener(fontsTextListener);
		
		// set the initial font values
		setFontsTextValues();
	}
	
	public void init(IWorkbench workbench){
		// TODO Automatisch erstellter Methoden-Stub
		
	}
	
	class TextListener extends FocusAdapter {
		
		@Override
		public void focusLost(FocusEvent e){
			Text t = (Text) e.getSource();
			String fld = (String) t.getData();
			actMandant.setInfoElement(fld, t.getText());
		}
		
	}
	
	class DetailListener extends HyperlinkAdapter {
		
		@Override
		public void linkActivated(HyperlinkEvent e){
			if (actMandant == null) {
				return;
			}
			KontaktExtDialog dlg = new KontaktExtDialog(getShell(), actMandant, ExtFlds);
			dlg.create();
			dlg.setTitle(Messages.RechnungsPrefs_MandatorDetails); //$NON-NLS-1$
			
			dlg.open();
		}
		
	}
	
	class BankLister extends TitleAreaDialog {
		final String[] flds = {
			Messages.RechnungsPrefs_department, Messages.RechnungsPrefs_POBox, ta.ESRNUMBER,
			ta.ESRSUB, "IBAN"
		};
		Label banklabel;
		KontaktExtDialog.ExtInfoTable exTable;
		
		BankLister(Shell shell){
			super(shell);
		}
		
		@Override
		protected Control createDialogArea(Composite parent){
			Composite ret = new Composite(parent, SWT.NONE);
			ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
			ret.setLayout(new GridLayout(2, false));
			Hyperlink hb = UiDesk.getToolkit().createHyperlink(ret,
				Messages.RechnungsPrefs_FinanceInst, SWT.NONE); //$NON-NLS-1$
			hb.addHyperlinkListener(new HyperlinkAdapter() {
				
				@Override
				public void linkActivated(HyperlinkEvent e){
					KontaktSelektor ksl = new KontaktSelektor(getShell(), Organisation.class,
						Messages.RechnungsPrefs_paymentinst,
						Messages.RechnungsPrefs_PleseChooseBank, new String[] {
							Organisation.FLD_NAME1, Organisation.FLD_NAME2
					}); //$NON-NLS-1$ //$NON-NLS-2$
					if (ksl.open() == Dialog.OK) {
						actBank = (Kontakt) ksl.getSelection();
						actMandant.setExtInfoStoredObjectByKey(ta.RNBANK, actBank.getId());
					}
				}
				
			});
			banklabel = new Label(ret, SWT.NONE);
			banklabel.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
			exTable = new KontaktExtDialog.ExtInfoTable(parent, flds);
			exTable.setLayoutData(SWTHelper.getFillGridData(2, true, 1, true));
			exTable.setKontakt(actMandant);
			return ret;
		}
		
		@Override
		public void create(){
			super.create();
			getShell().setText(Messages.RechnungsPrefs_ChooseBank); //$NON-NLS-1$
			setTitle(actMandant.getLabel());
			setMessage(Messages.RechnungsPrefs_ChosseInst); //$NON-NLS-1$
		}
		
		@Override
		protected void okPressed(){
			exTable.okPressed(actMandant);
			setMandant(actMandant);
			super.okPressed();
		}
		
	}
	
	class PostDialog extends TitleAreaDialog {
		final String[] flds = {
			ta.ESRNUMBER
		};
		KontaktExtDialog.ExtInfoTable exTable;
		
		PostDialog(Shell shell){
			super(shell);
		}
		
		@Override
		protected Control createDialogArea(Composite parent){
			Composite ret = new Composite(parent, SWT.NONE);
			ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
			ret.setLayout(new GridLayout(2, false));
			exTable = new KontaktExtDialog.ExtInfoTable(parent, flds);
			exTable.setLayoutData(SWTHelper.getFillGridData(2, true, 1, true));
			exTable.setKontakt(actMandant);
			return ret;
		}
		
		@Override
		public void create(){
			super.create();
			getShell().setText(Messages.RechnungsPrefs_postAccount); //$NON-NLS-1$
			setTitle(actMandant.getLabel());
			setMessage(Messages.RechnungsPrefs_InfoPostAccount); //$NON-NLS-1$
		}
		
		@Override
		protected void okPressed(){
			exTable.okPressed(actMandant);
			setMandant(actMandant);
			super.okPressed();
		}
	}
	
	void setMandant(Mandant m){
		actMandant = m;
		
		tTreat.setText(actMandant.getLabel());
		cvMandantType
			.setSelection(new StructuredSelection(ArzttarifeUtil.getMandantType(
				CoreModelServiceHolder.get().load(actMandant.getId(), IMandator.class).get())));
		
		actBank = Kontakt.load(actMandant.getInfoString(ta.RNBANK));
		if (actBank != null && actBank.isValid()) {
			tPost.setText(""); //$NON-NLS-1$
			tBank.setText(actBank.getLabel());
		} else {
			tPost.setText(actMandant.getInfoString(ta.ESRNUMBER));
			tBank.setText(""); //$NON-NLS-1$
			
			actBank = null;
		}
		bPost.setSelection(actBank == null);
		bBank.setSelection(actBank != null);
		
		bUseTC.setSelection(TarmedRequirements.hasTCContract(
			CoreModelServiceHolder.get().load(actMandant.getId(), IMandator.class).orElse(null))); //$NON-NLS-1$
		//bUseEDA.setSelection(actMandant.getInfoString(PreferenceConstants.USEEDA).equals("1")); //$NON-NLS-1$
		//bWithImage.setSelection(actMandant.getInfoString(PreferenceConstants.TCWITHIMAGE).equals("1")); //$NON-NLS-1$

		String tcName = TarmedRequirements.getTCName(CoreModelServiceHolder.get().load(actMandant.getId(), IMandator.class).orElse(null));
		if(tcName != null) {
			cbTC.setText(tcName);
		} else {
			cbTC.setText("");
		}
		
		bBillsElec.setSelection(
			CoreHub.getUserSetting(actMandant).get(PreferenceConstants.BILL_ELECTRONICALLY, false));
		
		responsible.setMandant(m);
	}
	
	/**
	 * set fonts text values from configuration
	 */
	private void setFontsTextValues(){
		String normalFontName =
			CoreHub.localCfg.get(ESR.ESR_NORMAL_FONT_NAME, ESR.ESR_NORMAL_FONT_NAME_DEFAULT);
		int normalFontSize =
			CoreHub.localCfg.get(ESR.ESR_NORMAL_FONT_SIZE, ESR.ESR_NORMAL_FONT_SIZE_DEFAULT);
		String ocrFontName =
			CoreHub.localCfg.get(ESR.ESR_OCR_FONT_NAME, ESR.ESR_OCR_FONT_NAME_DEFAULT);
		int ocrFontSize =
			CoreHub.localCfg.get(ESR.ESR_OCR_FONT_SIZE, ESR.ESR_OCR_FONT_SIZE_DEFAULT);
		
		tESRNormalFontName.setText(normalFontName);
		tESRNormalFontSize.setText(new Integer(normalFontSize).toString());
		tESROCRFontName.setText(ocrFontName);
		tESROCRFontSize.setText(new Integer(ocrFontSize).toString());
		
		int ocrFontWeight =
			CoreHub.localCfg.get(ESR.ESR_OCR_FONT_WEIGHT, ESR.ESR_OCR_FONT_WEIGHT_DEFAULT);
		int index;
		switch (ocrFontWeight) {
		case SWT.MIN:
			index = FONT_MIN_INDEX;
			break;
		case SWT.NORMAL:
			index = FONT_NORMAL_INDEX;
			break;
		case SWT.BOLD:
			index = FONT_BOLD_INDEX;
			break;
		default:
			index = FONT_NORMAL_INDEX;
		}
		cbESROCRFontWeight.select(index);
		
		int printerCorrectionX = CoreHub.localCfg.get(ESR.ESR_PRINTER_CORRECTION_X,
			ESR.ESR_PRINTER_CORRECTION_X_DEFAULT);
		int printerCorrectionY = CoreHub.localCfg.get(ESR.ESR_PRINTER_CORRECTION_Y,
			ESR.ESR_PRINTER_CORRECTION_Y_DEFAULT);
		int printerBaseCorrectionX = CoreHub.localCfg.get(ESR.ESR_PRINTER_BASE_OFFSET_X,
			ESR.ESR_PRINTER_BASE_OFFSET_X_DEFAULT);
		int printerBaseCorrectionY = CoreHub.localCfg.get(ESR.ESR_PRINTER_BASE_OFFSET_Y,
			ESR.ESR_PRINTER_BASE_OFFSET_Y_DEFAULT);
		
		tESRPrinterCorrectionX.setText(new Integer(printerCorrectionX).toString());
		tESRPrinterCorrectionY.setText(new Integer(printerCorrectionY).toString());
		tESRPrintCorrectionBaseX.setText(Integer.toString(printerBaseCorrectionX));
		tESRPrintCorrectionBaseY.setText(new Integer(printerBaseCorrectionY).toString());
	}
	
	/**
	 * store fonts text values to configuration
	 */
	void storeFontsTextValues(){
		String normalFontName;
		int normalFontSize;
		String ocrFontName;
		int ocrFontSize;
		int ocrFontWeight;
		
		int printerCorrectionX;
		int printerCorrectionY;
		int printerBaseCorrectionX;
		int printerBaseCorrectionY;
		
		normalFontName = tESRNormalFontName.getText().trim();
		if (StringTool.isNothing(normalFontName)) {
			normalFontName = ESR.ESR_NORMAL_FONT_NAME_DEFAULT;
		}
		// parse entered font size
		try {
			normalFontSize = Integer.parseInt(tESRNormalFontSize.getText().trim());
		} catch (NumberFormatException ex) {
			normalFontSize = ESR.ESR_NORMAL_FONT_SIZE_DEFAULT;
		}
		
		ocrFontName = tESROCRFontName.getText().trim();
		if (StringTool.isNothing(ocrFontName)) {
			ocrFontName = ESR.ESR_OCR_FONT_NAME_DEFAULT;
		}
		// parse entered font size
		try {
			ocrFontSize = Integer.parseInt(tESROCRFontSize.getText().trim());
		} catch (NumberFormatException ex) {
			ocrFontSize = ESR.ESR_OCR_FONT_SIZE_DEFAULT;
		}
		
		// get font weight selection
		int index = cbESROCRFontWeight.getSelectionIndex();
		switch (index) {
		case FONT_MIN_INDEX:
			ocrFontWeight = SWT.MIN;
			break;
		case FONT_NORMAL_INDEX:
			ocrFontWeight = SWT.NORMAL;
			break;
		case FONT_BOLD_INDEX:
			ocrFontWeight = SWT.BOLD;
			break;
		default:
			ocrFontWeight = SWT.NORMAL;
		}
		
		// parse entered corrections
		try {
			printerCorrectionX = Integer.parseInt(tESRPrinterCorrectionX.getText().trim());
		} catch (NumberFormatException ex) {
			printerCorrectionX = ESR.ESR_PRINTER_CORRECTION_X_DEFAULT;
		}
		try {
			printerCorrectionY = Integer.parseInt(tESRPrinterCorrectionY.getText().trim());
		} catch (NumberFormatException ex) {
			printerCorrectionY = ESR.ESR_PRINTER_CORRECTION_Y_DEFAULT;
		}
		try {
			printerBaseCorrectionX = Integer.parseInt(tESRPrintCorrectionBaseX.getText().trim());
		} catch (NumberFormatException ex) {
			printerBaseCorrectionX = ESR.ESR_PRINTER_BASE_OFFSET_X_DEFAULT;
		}
		try {
			printerBaseCorrectionY = Integer.parseInt(tESRPrintCorrectionBaseY.getText().trim());
		} catch (NumberFormatException ex) {
			printerBaseCorrectionY = ESR.ESR_PRINTER_BASE_OFFSET_Y_DEFAULT;
		}
		CoreHub.localCfg.set(ESR.ESR_NORMAL_FONT_NAME, normalFontName);
		CoreHub.localCfg.set(ESR.ESR_NORMAL_FONT_SIZE, normalFontSize);
		CoreHub.localCfg.set(ESR.ESR_OCR_FONT_NAME, ocrFontName);
		CoreHub.localCfg.set(ESR.ESR_OCR_FONT_SIZE, ocrFontSize);
		CoreHub.localCfg.set(ESR.ESR_OCR_FONT_WEIGHT, ocrFontWeight);
		CoreHub.localCfg.set(ESR.ESR_PRINTER_CORRECTION_X, printerCorrectionX);
		CoreHub.localCfg.set(ESR.ESR_PRINTER_CORRECTION_Y, printerCorrectionY);
		CoreHub.localCfg.set(ESR.ESR_PRINTER_BASE_OFFSET_X, printerBaseCorrectionX);
		CoreHub.localCfg.set(ESR.ESR_PRINTER_BASE_OFFSET_Y, printerBaseCorrectionY);
	}
	
	/*
	 * Store the values from the font text fields
	 */
	class FontsTextListener extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent e){
			storeFontsTextValues();
		}
		
	}
}
