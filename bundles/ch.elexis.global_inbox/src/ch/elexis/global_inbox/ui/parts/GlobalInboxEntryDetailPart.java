
package ch.elexis.global_inbox.ui.parts;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.holder.EncounterServiceHolder;
import ch.elexis.core.time.TimeUtil;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.documents.composites.CategorySelectionEditComposite;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.viewers.IdentifiableLabelProvider;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.elexis.global_inbox.Preferences;
import ch.elexis.global_inbox.model.GlobalInboxEntry;
import ch.elexis.global_inbox.ui.parts.contentproposal.TitleContentProposalProvider;
import ch.elexis.global_inbox.ui.parts.contentproposal.TitleControlContentAdapter;
import ch.elexis.global_inbox.ui.parts.contentproposal.TitleEntryContentProposal;

@SuppressWarnings("restriction")
public class GlobalInboxEntryDetailPart {
	
	@Inject
	private ECommandService commandService;
	@Inject
	private EHandlerService handlerService;
	
	private GlobalInboxEntry globalInboxEntry;
	
	private Text txtTitle;
	private CategorySelectionEditComposite csec;
	private CDateTime archivingDate;
	private MultiDateSelector creationDateSelector;
	private ComboViewer cvPatient;
	private ComboViewer cvSender;
	private Text txtKeywords;
	private Button btnInfoTo;
	private ComboViewer cvInfoToReceiver;
	
	@SuppressWarnings("unchecked")
	@Inject
	public GlobalInboxEntryDetailPart(Composite parent, IConfigService configService,
		EHandlerService handlerService){
		parent.setLayout(new GridLayout(2, false));
		
		Label label = new Label(parent, SWT.None);
		label.setText("Titel");
		
		txtTitle = new Text(parent, SWT.BORDER);
		txtTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtTitle.addModifyListener(e -> {
			if (globalInboxEntry != null) {
				globalInboxEntry.setTitle(txtTitle.getText());
			}
		});
		ContentProposalAdapter titleContentProposalAdapter =
			new ContentProposalAdapter(txtTitle, new TitleControlContentAdapter(txtTitle),
				new TitleContentProposalProvider(txtTitle), null, null);
		titleContentProposalAdapter.addContentProposalListener(proposal -> {
			TitleEntryContentProposal _proposal = (TitleEntryContentProposal) proposal;
			txtTitle.setText(_proposal.getTitleEntry().getTitle());
			csec.setCategoryByName(_proposal.getTitleEntry().getCategoryName());
			archivingDate.setFocus();
		});
		
		label = new Label(parent, SWT.None);
		label.setText("Kategorie");
		
		csec = new CategorySelectionEditComposite(parent, SWT.None, "ch.elexis.data.store.omnivore",
			true);
		csec.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		csec.addSelectionChangeListener(sc -> {
			ICategory category = (ICategory) sc.getStructuredSelection().getFirstElement();
			if (globalInboxEntry != null) {
				globalInboxEntry.setCategory(category.getName());
			}
		});
		
		label = new Label(parent, SWT.None);
		label.setText("Ablagedatum");
		archivingDate =
			new CDateTime(parent, CDT.DATE_SHORT | CDT.DROP_DOWN | SWT.BORDER | CDT.TAB_FIELDS);
		GridData gd_archivingDate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_archivingDate.widthHint = 100;
		archivingDate.setLayoutData(gd_archivingDate);
		archivingDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				if (globalInboxEntry != null) {
					globalInboxEntry.setArchivingDate(archivingDate.getSelection());
				}
			};
		});
		
		Label lblHintCreationDateCandidateImage = new Label(parent, SWT.NONE);
		GridData gdlblHintCreationDateCandidateImage =
			new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gdlblHintCreationDateCandidateImage.exclude = true;
		lblHintCreationDateCandidateImage.setLayoutData(gdlblHintCreationDateCandidateImage);
		
		label = new Label(parent, SWT.None);
		label.setText("Erstelldatum");
		creationDateSelector = new MultiDateSelector(parent, SWT.None);
		GridData gd_multiDateSelector = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		creationDateSelector.setLayoutData(gd_multiDateSelector);
		creationDateSelector.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				if (globalInboxEntry != null) {
					globalInboxEntry.setCreationDate(creationDateSelector.getSelection());
				}
			};
		});
		
		Label lblHintPatientCandiateInfo = new Label(parent, SWT.NONE);
		GridData gdlblHintPatientCandiateInfo =
			new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gdlblHintPatientCandiateInfo.exclude = true;
		lblHintPatientCandiateInfo.setLayoutData(gdlblHintPatientCandiateInfo);
		
		Link linkPatient = new Link(parent, SWT.None);
		linkPatient.setText("<a>Patient</a>");
		linkPatient.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				KontaktSelektor kontaktSelektor = new KontaktSelektor(linkPatient.getShell(),
					Patient.class, Messages.HL7_SelectPatient, Messages.HL7_SelectPatient,
					Patient.DEFAULT_SORT);
				if (kontaktSelektor.open() == KontaktSelektor.OK) {
					Patient patient = (Patient) kontaktSelektor.getSelection();
					IPatient iPatient = patient.toIPatient();
					cvPatient.add(iPatient);
					cvPatient.setSelection(new StructuredSelection(iPatient));
					if (globalInboxEntry != null) {
						globalInboxEntry.getPatientCandidates().add(iPatient);
						globalInboxEntry.setPatient(iPatient);
					}
				}
			}
		});
		
		cvPatient = new ComboViewer(parent, SWT.NONE);
		cvPatient.setContentProvider(ArrayContentProvider.getInstance());
		cvPatient.setLabelProvider(new IdentifiableLabelProvider());
		cvPatient.addSelectionChangedListener(sc -> {
			IPatient patient = (IPatient) sc.getStructuredSelection().getFirstElement();
			if (globalInboxEntry != null) {
				globalInboxEntry.setPatient(patient);
			}
			if (patient != null) {
				IContact familyDoctor = patient.getFamilyDoctor();
				IMandator mandator;
				if (familyDoctor != null) {
					mandator = CoreModelServiceHolder.get()
						.load(familyDoctor.getId(), IMandator.class).orElse(null);
				} else {
					mandator = EncounterServiceHolder.get().getLatestEncounter(patient)
						.map(enc -> enc.getMandator()).orElse(null);
				}
				if (mandator != null) {
					cvInfoToReceiver.setSelection(new StructuredSelection(mandator));
				}
			}
		});
		Combo ccvPatient = cvPatient.getCombo();
		ccvPatient.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Link linkSender = new Link(parent, SWT.None);
		linkSender.setText("<a>Absender</a>");
		linkSender.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				KontaktSelektor kontaktSelektor = new KontaktSelektor(linkPatient.getShell(),
					Kontakt.class, Messages.KontaktSelectionComposite_title,
					Messages.KontaktSelectionComposite_message, Patient.DEFAULT_SORT);
				if (kontaktSelektor.open() == KontaktSelektor.OK) {
					Kontakt contact = (Kontakt) kontaktSelektor.getSelection();
					IContact iContact = contact.toIContact();
					cvSender.add(iContact);
					cvSender.setSelection(new StructuredSelection(iContact));
					if (globalInboxEntry != null) {
						globalInboxEntry.setSender(iContact);
					}
				}
			}
		});
		
		cvSender = new ComboViewer(parent, SWT.NONE);
		cvSender.setContentProvider(ArrayContentProvider.getInstance());
		cvSender.setLabelProvider(new IdentifiableLabelProvider());
		Combo ccvSender = cvSender.getCombo();
		ccvSender.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cvSender.addSelectionChangedListener(sc -> {
			IContact sender = (IContact) cvSender.getStructuredSelection().getFirstElement();
			if (globalInboxEntry != null) {
				globalInboxEntry.setSender(sender);
			}
		});
		
		label = new Label(parent, SWT.None);
		label.setText("Stichwörter");
		
		txtKeywords = new Text(parent, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		GridData gd_txtKeywords = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtKeywords.heightHint = 40;
		txtKeywords.setLayoutData(gd_txtKeywords);
		txtKeywords.addModifyListener(e -> {
			if (globalInboxEntry != null) {
				globalInboxEntry.setKeywords(txtKeywords.getText());
			}
		});
		
		label = new Label(parent, SWT.None);
		label.setText("Info");
		
		Composite infoComposite = new Composite(parent, SWT.NONE);
		infoComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_infoComposite = new GridLayout(2, false);
		gl_infoComposite.marginWidth = 0;
		gl_infoComposite.marginHeight = 0;
		infoComposite.setLayout(gl_infoComposite);
		btnInfoTo = new Button(infoComposite, SWT.CHECK);
		btnInfoTo.setText("Info an Stammarzt");
		btnInfoTo.setSelection(configService.getLocal(Preferences.PREF_INFO_IN_INBOX, false));
		btnInfoTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				if (globalInboxEntry != null) {
					globalInboxEntry.setSendInfoTo(btnInfoTo.getSelection());
				}
			}
		});
		cvInfoToReceiver = new ComboViewer(infoComposite, SWT.NONE);
		Combo comboInfoToReceiver = cvInfoToReceiver.getCombo();
		comboInfoToReceiver.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cvInfoToReceiver.setContentProvider(ArrayContentProvider.getInstance());
		cvInfoToReceiver.setLabelProvider(new IdentifiableLabelProvider());
		List<IMandator> mandators =
			CoreModelServiceHolder.get().getQuery(IMandator.class).execute();
		cvInfoToReceiver.setInput(mandators);
		cvInfoToReceiver.addSelectionChangedListener(sc -> {
			if (globalInboxEntry != null) {
				globalInboxEntry.setInfoTo(sc.getStructuredSelection().toList());
			}
		});
		
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, false));
		buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		
		Button btnView = new Button(buttonComposite, SWT.NONE);
		btnView.setText("Anzeigen");
		btnView.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				ParameterizedCommand cmd = commandService
					.createCommand("ch.elexis.global_inbox.command.globalinboxentryview");
				if (handlerService.canExecute(cmd)) {
					handlerService.executeHandler(cmd);
				}
			}
		});
		
		Button btnAcceptAndNext = new Button(buttonComposite, SWT.NONE);
		btnAcceptAndNext.setText("Ablegen und nächstes");
		btnAcceptAndNext.addSelectionListener(new AcceptAndNextSelectionHandler());
	}
	
	@Focus
	public void setFocus(){
		txtTitle.setFocus();
	}
	
	@Inject
	public void setGlobalInboxEntry(@Optional @Named(IServiceConstants.ACTIVE_SELECTION)
	GlobalInboxEntry globalInboxEntry){
		
		this.globalInboxEntry = globalInboxEntry;
		
		if (globalInboxEntry == null) {
			txtTitle.setText("");
			csec.setCategoryByName(null);
			archivingDate.setSelection(null);
			creationDateSelector.setSelectionOptionsAndDefault(Collections.emptyList(), null);
			cvPatient.setInput(null);
			cvSender.setInput(null);
			txtKeywords.setText("");
			return;
		}
		
		txtTitle.setText(globalInboxEntry.getTitle());
		csec.setCategoryByName(globalInboxEntry.getCategory());
		txtKeywords.setText(
			globalInboxEntry.getKeywords() != null ? this.globalInboxEntry.getKeywords() : "");
		btnInfoTo.setSelection(globalInboxEntry.isSendInfoTo());
		
		Date selectedArchivingDate = globalInboxEntry.getArchivingDate();
		if (selectedArchivingDate != null) {
			archivingDate.setSelection(selectedArchivingDate);
		} else {
			archivingDate.setSelection(new Date());
		}
		
		Date creationDate = globalInboxEntry.getCreationDate();
		if (creationDate == null) {
			creationDate = TimeUtil.toDate(globalInboxEntry.getCreationDateCandidate());
		}
		
		Date creationDatePreselection = creationDateSelector
			.setSelectionOptionsAndDefault(globalInboxEntry.getDateTokens(), creationDate);
		globalInboxEntry.setCreationDate(creationDatePreselection);
		
		IPatient selectedPatient = globalInboxEntry.getPatient();
		List<IPatient> patientCandidates = globalInboxEntry.getPatientCandidates();
		cvPatient.setInput(patientCandidates);
		if (selectedPatient == null && !patientCandidates.isEmpty()) {
			cvPatient.setSelection(new StructuredSelection(patientCandidates.get(0)));
		} else {
			ISelection selection =
				(selectedPatient != null) ? new StructuredSelection(selectedPatient) : null;
			cvPatient.setSelection(selection);
		}
		
		IContact selectedSender = globalInboxEntry.getSender();
		List<IContact> senderCandidates = globalInboxEntry.getSenderCandidates();
		if (selectedSender != null) {
			senderCandidates.add(selectedSender);
		}
		cvSender.setInput(senderCandidates);
		if (selectedSender == null && !senderCandidates.isEmpty()) {
			cvSender.setSelection(new StructuredSelection(senderCandidates.get(0)));
		} else {
			ISelection selection =
				(selectedSender != null) ? new StructuredSelection(selectedSender) : null;
			cvSender.setSelection(selection);
		}
	}
	
	private class AcceptAndNextSelectionHandler extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent e){
			if (globalInboxEntry == null) {
				return;
			}
			
			ParameterizedCommand cmd = commandService
				.createCommand("ch.elexis.global_inbox.command.globalinboxentryimport");
			if (handlerService.canExecute(cmd)) {
				handlerService.executeHandler(cmd);
				setGlobalInboxEntry(null);
			} else {
				SWTHelper.showError("Could not import", "Patient or category value is missing");
			}
		}
	}
	
}