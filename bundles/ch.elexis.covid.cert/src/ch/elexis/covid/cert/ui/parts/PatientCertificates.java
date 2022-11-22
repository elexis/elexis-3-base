
package ch.elexis.covid.cert.ui.parts;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
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
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.format.PersonFormatUtil;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.ILocalDocumentService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.ui.dialogs.base.InputDialog;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.CoreUiUtil;
import ch.elexis.covid.cert.service.CertificateInfo;
import ch.elexis.covid.cert.service.CertificatesService;
import ch.elexis.covid.cert.service.rest.model.RecoveryModel;
import ch.elexis.covid.cert.service.rest.model.RevokeModel;
import ch.elexis.covid.cert.service.rest.model.TestModel;
import ch.elexis.covid.cert.service.rest.model.VaccinationModel;
import ch.elexis.covid.cert.ui.dialogs.RecoveryModelDialog;
import ch.elexis.covid.cert.ui.dialogs.TestModelDialog;
import ch.elexis.covid.cert.ui.dialogs.VaccinationModelDialog;
import ch.elexis.covid.cert.ui.handler.CovidHandlerUtil;
import ch.rgw.tools.Result;

@SuppressWarnings("restriction")
public class PatientCertificates {

	private Composite composite;

	private Form patientLabel;

	private TableViewer certificatesViewer;

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
			setPatient(patient);
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
	public PatientCertificates() {
	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());

		patientLabel = UiDesk.getToolkit().createForm(composite);
		patientLabel.getBody().setLayout(new GridLayout(1, true));
		patientLabel.setText(StringUtils.EMPTY);

		Composite btnComposite = new Composite(composite, SWT.NONE);
		btnComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		btnComposite.setLayout(new GridLayout(3, true));

		certificatesViewer = new TableViewer(composite, SWT.V_SCROLL);
		certificatesViewer.setContentProvider(ArrayContentProvider.getInstance());
		certificatesViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof CertificateInfo) {
					return ((CertificateInfo) element).getType().getLabel() + ", erstellt " + DateTimeFormatter
							.ofPattern("dd.MM.yyyy HH:mm").format(((CertificateInfo) element).getTimestamp());
				}
				return super.getText(element);
			}
		});
		certificatesViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (certificatesViewer.getStructuredSelection() != null
						&& !certificatesViewer.getStructuredSelection().isEmpty()) {
					CertificateInfo info = (CertificateInfo) certificatesViewer.getStructuredSelection()
							.getFirstElement();
					openCertDocument(info);
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
				CertificateInfo info = (CertificateInfo) certificatesViewer.getStructuredSelection().getFirstElement();
				RevokeModel model = new RevokeModel().initDefault(info, service.getOtp());
				Result<String> result = service.revokeCertificate(patient, info, model);
				if (result.isOK()) {
					ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, patient);
				} else {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
							"Es ist folgender Fehler aufgetreten.\n\n" + result.getMessages().stream()
									.map(m -> m.getText()).collect(Collectors.joining(", ")));
				}
			}

			@Override
			public boolean isEnabled() {
				return certificatesViewer.getSelection() != null && !certificatesViewer.getSelection().isEmpty();
			}
		});
		Menu contextMenu = menuManager.createContextMenu(certificatesViewer.getTable());
		certificatesViewer.getTable().setMenu(contextMenu);
		menuManager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				IContributionItem[] items = manager.getItems();
				for (IContributionItem iContributionItem : items) {
					iContributionItem.update();
				}
			}
		});
		certificatesViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Button btn = new Button(btnComposite, SWT.PUSH);
		btn.setImage(Images.IMG_NEW.getImage());
		btn.setText(CertificateInfo.Type.VACCINATION.getLabel());
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				VaccinationModel model = getVaccinationModel();
				if (model != null) {
					try {
						Result<String> result = service.createVaccinationCertificate(patient, model);
						if (result.isOK()) {
							CertificateInfo newCert = CertificateInfo.of(patient).stream()
									.filter(c -> c.getUvci().equals(result.get())).findFirst().orElse(null);
							if (newCert != null) {
								openCertDocument(newCert);
							}
							ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, patient);
							CovidHandlerUtil.showResultInfos(result);
						} else {
							MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
									"Es ist folgender Fehler aufgetreten.\n\n" + result.getMessages().stream()
											.map(m -> m.getText()).collect(Collectors.joining(", ")));
						}
					} catch (Exception ex) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
								"Es ist ein Fehler beim Aufruf der API aufgetreten.");
						LoggerFactory.getLogger(getClass()).error("Error getting vaccination certificate", ex);
					}
				}
			}

		});
		btn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		btn = new Button(btnComposite, SWT.PUSH);
		btn.setImage(Images.IMG_NEW.getImage());
		btn.setText(CertificateInfo.Type.TEST.getLabel());
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TestModel model = getTestModel();
				if (model != null) {
					try {
						Result<String> result = service.createTestCertificate(patient, model);
						if (result.isOK()) {
							CertificateInfo newCert = CertificateInfo.of(patient).stream()
									.filter(c -> c.getUvci().equals(result.get())).findFirst().orElse(null);
							if (newCert != null) {
								openCertDocument(newCert);
								executeTestBilling();
							}
							ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, patient);
							CovidHandlerUtil.showResultInfos(result);
						} else {
							MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
									"Es ist folgender Fehler aufgetreten.\n\n" + result.getMessages().stream()
											.map(m -> m.getText()).collect(Collectors.joining(", ")));
						}
					} catch (Exception ex) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
								"Es ist ein Fehler beim Aufruf der API aufgetreten.");
						LoggerFactory.getLogger(getClass()).error("Error getting test certificate", ex);
					}
				}
			}
		});
		btn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		btn = new Button(btnComposite, SWT.PUSH);
		btn.setImage(Images.IMG_NEW.getImage());
		btn.setText(CertificateInfo.Type.RECOVERY.getLabel());
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RecoveryModel model = getRecoveryModel();
				if (model != null) {
					try {
						Result<String> result = service.createRecoveryCertificate(patient, model);
						if (result.isOK()) {
							CertificateInfo newCert = CertificateInfo.of(patient).stream()
									.filter(c -> c.getUvci().equals(result.get())).findFirst().orElse(null);
							if (newCert != null) {
								openCertDocument(newCert);
							}
							ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, patient);
							CovidHandlerUtil.showResultInfos(result);
						} else {
							MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
									"Es ist folgender Fehler aufgetreten.\n\n" + result.getMessages().stream()
											.map(m -> m.getText()).collect(Collectors.joining(", ")));
						}
					} catch (Exception ex) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
								"Es ist ein Fehler beim Aufruf der API aufgetreten.");
						LoggerFactory.getLogger(getClass()).error("Error getting recovery certificate", ex);
					}
				}
			}
		});
		btn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Label sep = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.heightHint = 5;
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
			patientLabel.setText("Zertifikate von " + PersonFormatUtil.getFullName(patient));
			patientLabel.layout();

			certificatesViewer.setInput(CertificateInfo.of(patient));
			setButtonsEnabled(composite, true);
		} else {
			patientLabel.setText(StringUtils.EMPTY);
			patientLabel.layout();

			certificatesViewer.setInput(Collections.emptyList());
			setButtonsEnabled(composite, false);
		}
		certificatesViewer.refresh();
		Display.getDefault().asyncExec(() -> {
			if (composite != null && !composite.isDisposed()) {
				composite.getParent().layout(true, true);

			}
		});
	}

	private void openCertDocument(CertificateInfo newCert) {
		java.util.Optional<IDocument> document = omnivoreStore.loadDocument(newCert.getDocumentId());
		if (document.isPresent()) {
			java.util.Optional<File> file = localDocumentService.getTempFile(document.get());
			if (file.isPresent()) {
				Program.launch(file.get().getAbsolutePath());
			} else {
				MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.Core_Error,
						Messages.Core_Document_Not_Opened_Locally);
			}
		}
	}

	private VaccinationModel getVaccinationModel() {
		VaccinationModel ret = new VaccinationModel().initDefault(patient, service.getOtp());
		VaccinationModelDialog dialog = new VaccinationModelDialog(ret, Display.getDefault().getActiveShell());
		if (dialog.open() == Dialog.OK) {
			return ret;
		}
		return null;
	}

	private TestModel getTestModel() {
		TestModel ret = new TestModel().initDefault(patient, service.getOtp());
		TestModelDialog dialog = new TestModelDialog(ret, Display.getDefault().getActiveShell());
		if (dialog.open() == Dialog.OK) {
			return ret;
		}
		return null;
	}

	private RecoveryModel getRecoveryModel() {
		RecoveryModel ret = new RecoveryModel().initDefault(patient, service.getOtp());
		RecoveryModelDialog dialog = new RecoveryModelDialog(ret, Display.getDefault().getActiveShell());
		if (dialog.open() == Dialog.OK) {
			return ret;
		}
		return null;
	}

	private void executeTestBilling() {
		executeCommand("ch.elexis.covid.cert.command.covidtest.bill");
	}

	private Object executeCommand(String commandId) {
		try {
			ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
					.getService(ICommandService.class);

			Command cmd = commandService.getCommand(commandId);
			ExecutionEvent ee = new ExecutionEvent(cmd, Collections.EMPTY_MAP, null, null);
			return cmd.executeWithChecks(ee);
		} catch (Exception e) {
			LoggerFactory.getLogger(PatientCertificates.class).error("cannot execute command with id: " + commandId, e);
		}
		return null;
	}
}