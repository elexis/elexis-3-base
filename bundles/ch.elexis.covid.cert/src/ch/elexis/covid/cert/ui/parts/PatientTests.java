
package ch.elexis.covid.cert.ui.parts;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Service;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.slf4j.LoggerFactory;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IDocumentLetter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.format.PersonFormatUtil;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.ILocalDocumentService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.dialogs.base.InputDialog;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.covid.cert.service.CertificateInfo;
import ch.elexis.covid.cert.service.CertificateInfo.Type;
import ch.elexis.covid.cert.service.CertificatesService;
import ch.elexis.covid.cert.service.rest.model.RevokeModel;
import ch.elexis.covid.cert.ui.handler.CovidHandlerUtil;
import ch.rgw.tools.Result;

public class PatientTests {

	private Composite composite;

	private Form patientLabel;

	private TableViewer testsViewer;

	@Inject
	private CertificatesService service;

	@Inject
	@Service(filterExpression = "(storeid=ch.elexis.data.store.omnivore)")
	private IDocumentStore omnivoreStore;

	@Inject
	private ILocalDocumentService localDocumentService;

	@Inject
	private IConfigService configService;

	private IPatient patient;

	private Button btnOtp;

	@Optional
	@Inject
	void updatePatient(@UIEventTopic(ElexisEventTopics.EVENT_UPDATE) IPatient patient) {
		if (composite != null && !composite.isDisposed()) {
			// only update with info of selected patient
			if (patient != null && patient.equals(ContextServiceHolder.get().getActivePatient().orElse(null))) {
				setPatient(patient);
			}
		}
	}

	@Optional
	@Inject
	void activePatient(IPatient patient) {
		if (composite != null && !composite.isDisposed()) {
			Display.getDefault().asyncExec(() -> {
				setPatient(patient);
			});
		}
	}

	@Inject
	void setMandator(@Optional IMandator mandator) {
		if (composite != null && !composite.isDisposed()) {
			Display.getDefault().asyncExec(() -> {
				updateOtp();
			});
		}
	}

	private void updateOtp() {
		java.util.Optional<IMandator> activeMandator = ContextServiceHolder.get().getActiveMandator();
		if (activeMandator.isPresent()) {
			btnOtp.setText("OTP von " + activeMandator.get().getLabel());
			String otp = configService.getActiveMandator(CertificatesService.CFG_OTP, null);
			if (StringUtils.isNotBlank(otp)) {
				btnOtp.setImage(Images.IMG_TICK.getImage());
			} else {
				btnOtp.setImage(Images.IMG_ACHTUNG.getImage());
			}
		} else {
			btnOtp.setImage(Images.IMG_ACHTUNG.getImage());
			btnOtp.setText("kein Mandant");
		}
		composite.layout();
	}

	@Inject
	public PatientTests() {
	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());

		Composite btnComposite = new Composite(composite, SWT.NONE);
		btnComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		btnComposite.setLayout(new GridLayout(2, true));

		patientLabel = UiDesk.getToolkit().createForm(composite);
		patientLabel.getBody().setLayout(new GridLayout(1, true));
		patientLabel.setText(StringUtils.EMPTY);

		testsViewer = new TableViewer(composite, SWT.V_SCROLL);
		testsViewer.setContentProvider(ArrayContentProvider.getInstance());
		testsViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof CertificateInfo) {
					return ((CertificateInfo) element).getType().getLabel() + ", erstellt " + DateTimeFormatter
							.ofPattern("dd.MM.yyyy HH:mm").format(((CertificateInfo) element).getTimestamp());
				} else if (element instanceof IDocumentLetter) {
					return "Bescheinigung " + ((IDocumentLetter) element).getTitle() + ", erstellt "
							+ DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(LocalDateTime.ofInstant(
									((IDocumentLetter) element).getCreated().toInstant(), ZoneId.systemDefault()));
				}
				return super.getText(element);
			}
		});
		testsViewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object l, Object r) {
				LocalDateTime lDateTime = LocalDateTime.of(1970, 1, 1, 0, 0);
				LocalDateTime rDateTime = LocalDateTime.of(1970, 1, 1, 0, 0);
				if (l instanceof CertificateInfo) {
					lDateTime = ((CertificateInfo) l).getTimestamp();
				} else if (l instanceof IDocumentLetter) {
					lDateTime = LocalDateTime.ofInstant(((IDocumentLetter) l).getCreated().toInstant(),
							ZoneId.systemDefault());
				}
				if (r instanceof CertificateInfo) {
					rDateTime = ((CertificateInfo) r).getTimestamp();
				} else if (r instanceof IDocumentLetter) {
					rDateTime = LocalDateTime.ofInstant(((IDocumentLetter) r).getCreated().toInstant(),
							ZoneId.systemDefault());
				}
				return rDateTime.compareTo(lDateTime);
			}
		});
		testsViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (testsViewer.getStructuredSelection() != null && !testsViewer.getStructuredSelection().isEmpty()) {
					if (testsViewer.getStructuredSelection().getFirstElement() instanceof CertificateInfo) {
						CertificateInfo info = (CertificateInfo) testsViewer.getStructuredSelection().getFirstElement();
						CovidHandlerUtil.openCertDocument(info, omnivoreStore, localDocumentService);
					} else if (testsViewer.getStructuredSelection().getFirstElement() instanceof IDocumentLetter) {
						CovidHandlerUtil.openLetter(
								(IDocumentLetter) testsViewer.getStructuredSelection().getFirstElement(),
								localDocumentService);
					}
				}
			}
		});
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Action() {
			@Override
			public String getText() {
				return "Zertifikat entfernen";
			}

			@Override
			public ImageDescriptor getImageDescriptor() {
				return Images.IMG_DELETE.getImageDescriptor();
			}

			@Override
			public void run() {
				if (testsViewer.getStructuredSelection().getFirstElement() instanceof CertificateInfo) {
					CertificateInfo info = (CertificateInfo) testsViewer.getStructuredSelection().getFirstElement();
					RevokeModel model = new RevokeModel().initDefault(info, service.getOtp());
					Result<String> result = service.revokeCertificate(patient, info, model);
					if (result.isOK()) {
						ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, patient);
					} else {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
								"Es ist folgender Fehler aufgetreten.\n\n" + result.getMessages().stream()
										.map(m -> m.getText()).collect(Collectors.joining(", ")));
					}
				} else if (testsViewer.getStructuredSelection().getFirstElement() instanceof IDocumentLetter) {
					IDocumentLetter letter = (IDocumentLetter) testsViewer.getStructuredSelection().getFirstElement();
					CoreModelServiceHolder.get().delete(letter);
					ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, patient);
				}
			}

			@Override
			public boolean isEnabled() {
				return testsViewer.getSelection() != null && !testsViewer.getSelection().isEmpty();
			}
		});
		Menu contextMenu = menuManager.createContextMenu(testsViewer.getTable());
		testsViewer.getTable().setMenu(contextMenu);
		menuManager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				IContributionItem[] items = manager.getItems();
				for (IContributionItem iContributionItem : items) {
					iContributionItem.update();
				}
			}
		});
		testsViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Button btn = new Button(btnComposite, SWT.PUSH);
		btn.setImage(Images.IMG_NEW.getImage());
		btn.setText("Antigen Kasse");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				executeCommand("ch.elexis.covid.cert.command.covidtest.antigen.kk");
			}

		});
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.heightHint = 75;
		btn.setLayoutData(gd);

		btn = new Button(btnComposite, SWT.PUSH);
		btn.setImage(Images.IMG_NEW.getImage());
		btn.setText("Antigen Selbstzahler");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				executeCommand("ch.elexis.covid.cert.command.covidtest.antigen.sz");
			}
		});
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.heightHint = 75;
		btn.setLayoutData(gd);

		btn = new Button(btnComposite, SWT.PUSH);
		btn.setImage(Images.IMG_NEW.getImage());
		btn.setText("PCR Kasse");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				executeCommand("ch.elexis.covid.cert.command.covidtest.pcr.kk");
			}
		});
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.heightHint = 75;
		btn.setLayoutData(gd);

		btn = new Button(btnComposite, SWT.PUSH);
		btn.setImage(Images.IMG_NEW.getImage());
		btn.setText("PCR Selbstzahler");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				executeCommand("ch.elexis.covid.cert.command.covidtest.pcr.sz");
			}
		});
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.heightHint = 75;
		btn.setLayoutData(gd);

		btn = new Button(btnComposite, SWT.PUSH);
		btn.setImage(Images.IMG_NEW.getImage());
		btn.setText("Bescheinigung Kasse");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				executeCommand("ch.elexis.covid.cert.command.covidtest.letter.kk");
			}
		});
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.heightHint = 75;
		btn.setLayoutData(gd);

		btn = new Button(btnComposite, SWT.PUSH);
		btn.setImage(Images.IMG_NEW.getImage());
		btn.setText("Bescheinigung Selbstzahler");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				executeCommand("ch.elexis.covid.cert.command.covidtest.letter.sz");
			}
		});
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.heightHint = 75;
		btn.setLayoutData(gd);

		Label sep = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		sep.setLayoutData(gd);

		btnOtp = new Button(composite, SWT.PUSH);
		btnOtp.setText("...");
		btnOtp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				java.util.Optional<IMandator> activeMandator = ContextServiceHolder.get().getActiveMandator();
				if (activeMandator.isPresent()) {
					String otp = configService.getActiveMandator(CertificatesService.CFG_OTP, StringUtils.EMPTY);
					InputDialog otpDialog = new InputDialog(Display.getDefault().getActiveShell(), "COVID Zert OTP",
							"Das one time password (OTP) von " + activeMandator.get().getLabel() + " setzen.", otp,
							null, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
					otpDialog.setWidthHint(400);
					if (otpDialog.open() == Window.OK) {
						String newOtp = otpDialog.getValue();
						configService.setActiveMandator(CertificatesService.CFG_OTP, newOtp);
						ConfigServiceHolder.get().setActiveMandator(CertificatesService.CFG_OTP_TIMESTAMP,
								LocalDateTime.now().toString());
					}
					updateOtp();
				}
			}
		});
		Hyperlink otplink = new Hyperlink(composite, SWT.NONE);
		otplink.setText("OTP Seite öffnen");
		otplink.setForeground(CoreUiUtil.getColorForString("0000ff"));
		otplink.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkExited(HyperlinkEvent e) {
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
			}

			@Override
			public void linkActivated(HyperlinkEvent e) {
				Program.launch(service.getOtpUrl());
			}
		});

		this.composite = composite;
		setPatient(ContextServiceHolder.get().getActivePatient().orElse(null));
		setMandator(ContextServiceHolder.get().getActiveMandator().orElse(null));
	}

	private void setButtonsEnabled(Composite composite, boolean value) {
		for (Control child : composite.getChildren()) {
			if (child instanceof Button) {
				child.setEnabled(value);
			} else if (child instanceof Composite) {
				setButtonsEnabled((Composite) child, value);
			}
		}
	}

	private void setPatient(IPatient patient) {
		this.patient = patient;
		if (patient != null) {
			patientLabel.setText("Tests von " + PersonFormatUtil.getFullName(patient));
			patientLabel.layout();
			List<CertificateInfo> certificates = CertificateInfo.of(patient).stream()
					.filter(ci -> ci.getType() == Type.TEST).collect(Collectors.toList());
			List<IDocumentLetter> letters = CovidHandlerUtil.getLettersAt(patient, null, new String[] {
					CovidHandlerUtil.ATTEST_POSITIV_LETTER_NAME, CovidHandlerUtil.ATTEST_NEGATIV_LETTER_NAME });
			List<Object> input = new ArrayList<>();
			input.addAll(certificates);
			input.addAll(letters);
			testsViewer.setInput(input);
			setButtonsEnabled(composite, true);
		} else {
			patientLabel.setText(StringUtils.EMPTY);
			patientLabel.layout();

			testsViewer.setInput(Collections.emptyList());
			setButtonsEnabled(composite, false);
		}
		testsViewer.refresh();
		// limit height
		if (testsViewer.getTable().computeSize(SWT.DEFAULT, SWT.DEFAULT).y > 250) {
			((GridData) testsViewer.getTable().getLayoutData()).heightHint = 250;
		}
		Display.getDefault().asyncExec(() -> {
			if (composite != null && !composite.isDisposed()) {
				composite.getParent().layout(true, true);

			}
		});
	}

	private Object executeCommand(String commandId) {
		try {
			ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
					.getService(ICommandService.class);

			Command cmd = commandService.getCommand(commandId);
			ExecutionEvent ee = new ExecutionEvent(cmd, Collections.EMPTY_MAP, null, null);
			return cmd.executeWithChecks(ee);
		} catch (Exception e) {
			LoggerFactory.getLogger(PatientTests.class).error("cannot execute command with id: " + commandId, e);
		}
		return null;
	}
}