
package ch.elexis.global_inbox.ui.parts;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
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
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.documents.composites.CategorySelectionEditComposite;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.viewers.IdentifiableLabelProvider;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.elexis.global_inbox.Preferences;
import ch.elexis.global_inbox.model.GlobalInboxEntry;

@SuppressWarnings("restriction")
public class GlobalInboxEntryDetailPart {
	
	@Inject
	private ECommandService commandService;
	@Inject
	private EHandlerService handlerService;
	@Inject
	private IConfigService configService;
	
	private GlobalInboxEntry globalInboxEntry;
	
	private Text txtTitle;
	private CategorySelectionEditComposite csec;
	private CDateTime archivingDate;
	private CDateTime creationDate;
	private ComboViewer cvPatient;
	private ComboViewer cvSender;
	private Text txtKeywords;
	private Button btnInfoTo;
	private ComboViewer cvInfoToReceiver;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void postConstruct(Composite parent){
		parent.setLayout(new GridLayout(2, false));
		
		Label label = new Label(parent, SWT.None);
		label.setText("Titel");
		
		txtTitle = new Text(parent, SWT.BORDER);
		txtTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtTitle.addModifyListener(e -> globalInboxEntry.setTitle(txtTitle.getText()));
		
		label = new Label(parent, SWT.None);
		label.setText("Kategorie");
		
		csec = new CategorySelectionEditComposite(parent, SWT.None, "ch.elexis.data.store.omnivore",
			true);
		csec.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		csec.addSelectionChangeListener(sc -> {
			ICategory category = (ICategory) sc.getStructuredSelection().getFirstElement();
			globalInboxEntry.setCategory(category.getName());
		});
		
		label = new Label(parent, SWT.None);
		label.setText("Ablagedatum");
		archivingDate =
			new CDateTime(parent, CDT.DATE_SHORT | CDT.DROP_DOWN | SWT.BORDER | CDT.TAB_FIELDS);
		archivingDate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				globalInboxEntry.setArchivingDate(archivingDate.getSelection());
			};
		});
		
		Label lblHintCreationDateCandidateImage = new Label(parent, SWT.NONE);
		GridData gdlblHintCreationDateCandidateImage =
			new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gdlblHintCreationDateCandidateImage.exclude = true;
		lblHintCreationDateCandidateImage.setLayoutData(gdlblHintCreationDateCandidateImage);
		
		label = new Label(parent, SWT.None);
		label.setText("Erstelldatum");
		creationDate =
			new CDateTime(parent, CDT.DATE_SHORT | CDT.DROP_DOWN | SWT.BORDER | CDT.TAB_FIELDS);
		creationDate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				globalInboxEntry.setCreationDate(creationDate.getSelection());
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
					globalInboxEntry.setPatient(iPatient);
				}
			}
		});
		
		cvPatient = new ComboViewer(parent, SWT.NONE);
		cvPatient.setContentProvider(ArrayContentProvider.getInstance());
		cvPatient.setLabelProvider(new IdentifiableLabelProvider());
		cvPatient.addSelectionChangedListener(sc -> {
			IPatient patient = (IPatient) sc.getStructuredSelection().getFirstElement();
			globalInboxEntry.setPatient(patient);
		});
		Combo ccvPatient = cvPatient.getCombo();
		ccvPatient.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Link linkSender = new Link(parent, SWT.None);
		linkSender.setText("<a>Absender</a>");
		linkSender.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				KontaktSelektor kontaktSelektor = new KontaktSelektor(linkPatient.getShell(),
					Kontakt.class, Messages.HL7_SelectPatient, Messages.HL7_SelectPatient,
					Patient.DEFAULT_SORT);
				if (kontaktSelektor.open() == KontaktSelektor.OK) {
					Kontakt contact = (Kontakt) kontaktSelektor.getSelection();
					IContact iContact = contact.toIContact();
					cvSender.add(iContact);
					cvSender.setSelection(new StructuredSelection(iContact));
					globalInboxEntry.setSenderId(iContact.getId());
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
			globalInboxEntry.setSenderId(sender != null ? sender.getId() : null);
		});
		
		label = new Label(parent, SWT.None);
		label.setText("Stichwörter");
		
		txtKeywords = new Text(parent, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		GridData gd_txtKeywords = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtKeywords.heightHint = 40;
		txtKeywords.setLayoutData(gd_txtKeywords);
		txtKeywords.addModifyListener(e -> globalInboxEntry.setKeywords(txtKeywords.getText()));
		
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
				globalInboxEntry.setSendInfoTo(btnInfoTo.getSelection());
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
		cvInfoToReceiver.addSelectionChangedListener(
			sc -> globalInboxEntry.setInfoTo(sc.getStructuredSelection().toList()));
		
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
	public void setGlobalInboxEntry(
		@Optional @Named(IServiceConstants.ACTIVE_SELECTION) GlobalInboxEntry globalInboxEntry){
		
		this.globalInboxEntry = globalInboxEntry;
		
		if (globalInboxEntry == null) {
			return;
		}
		
		txtTitle.setText(globalInboxEntry.getTitle());
		csec.setCategoryByName(globalInboxEntry.getCategory());
		archivingDate.setSelection(new Date());
		txtKeywords.setText(
			globalInboxEntry.getKeywords() != null ? this.globalInboxEntry.getKeywords() : "");
		btnInfoTo.setSelection(globalInboxEntry.isSendInfoTo());
		
		List<LocalDate> creationDateCandidates = globalInboxEntry.getCreationDateCandidates();
		if (!creationDateCandidates.isEmpty()) {
			LocalDate _creationDateCandidate = creationDateCandidates.get(0);
			Date creationDateCandidate = Date.from(
				_creationDateCandidate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
			creationDate.setSelection(creationDateCandidate);
		}
		
		List<IPatient> patientCandidates = globalInboxEntry.getPatientCandidates();
		cvPatient.setInput(patientCandidates);
		if (!patientCandidates.isEmpty()) {
			cvPatient.setSelection(new StructuredSelection(patientCandidates.get(0)));
		} else {
			cvPatient.setSelection(null);
		}
		
		List<IContact> senderCandidates = globalInboxEntry.getSenderCandidates();
		cvSender.setInput(senderCandidates);
		if (!senderCandidates.isEmpty()) {
			cvSender.setSelection(new StructuredSelection(senderCandidates.get(0)));
		} else {
			cvSender.setSelection(null);
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
			} else {
				SWTHelper.showError("Could not import", "Patient or category value is missing");
			}
			
			// TODO send reload to main part
		}
	}
	
}