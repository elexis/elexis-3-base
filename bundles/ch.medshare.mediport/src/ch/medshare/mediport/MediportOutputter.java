/*******************************************************************************
 * 2.12.2008, T. Schaller (moved from immis code in V1.3.4)
 *    
 *******************************************************************************/

package ch.medshare.mediport;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.TarmedRechnung.XMLExporterUtil;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.elexis.data.RnStatus.REJECTCODE;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.medshare.mediport.config.Client;
import ch.medshare.mediport.config.ClientParam;
import ch.medshare.mediport.config.ConfigKeys;
import ch.medshare.mediport.config.MPCProperties;
import ch.medshare.mediport.util.MediPortHelper;
import ch.medshare.util.UtilFile;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Result;

public class MediportOutputter extends ch.elexis.TarmedRechnung.XMLExporter {
	
	protected static String TIER_PAYANT = "TP";
	protected static String TIER_GARANT = "TG";
	
	private final static int OUTPUT_MEDIPORT = 0;
	private final static int OUTPUT_ALTERNATIVE = 1;
	private final static int OUTPUT_ERROR = 2;
	private static Logger log = LoggerFactory.getLogger(MediportOutputter.class);
	
	static Combo cbParamNames;
	
	private boolean alternativeRun = false;
	
	MPCProperties props = null;
	
	ClientParam selectedParam;
	
	SettingsPreferenceStore prefs = new SettingsPreferenceStore(CoreHub.globalCfg);
	
	public Control createSettingsControl(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(2, false));
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		MPCProperties props = getProperties();
		if (props != null) {
			Client client = MediPortHelper.getCurrentClient();
			if (client == null) {
				addMessage(ret, Messages.MediportOutputter_error_msg1_NoMPCClient, //$NON-NLS-1$
					Messages.MediportOutputter_error_msg2_NoMPCClient); //$NON-NLS-1$
			} else {
				if (client.getParamNames().size() == 0) {
					addMessage(ret, Messages.MediportOutputter_error_msg1_NoMPCParam, //$NON-NLS-1$
						Messages.MediportOutputter_error_msg2_NoMPCParam); //$NON-NLS-1$
				} else {
					Label lblParam = new Label(ret, SWT.NONE);
					lblParam.setText(Messages.MediportOutputter_lbl_Parameter); //$NON-NLS-1$
					cbParamNames = new Combo(ret, SWT.BORDER | SWT.READ_ONLY);
					cbParamNames.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
					
					for (String name : client.getParamNames()) {
						cbParamNames.add(name);
					}
					if (client.getParamNames().size() > 0) {
						cbParamNames.select(0);
					}
					
					cbParamNames.setLayoutData(SWTHelper.getFillGridData(1, false, 1, false));
					
					// new ErrorInvoiceForm(ret, SWT.NONE, client);
				}
			}
		} else {
			addMessage(ret, Messages.MediportOutputter_error_msg1_NoMPCConfig, //$NON-NLS-1$
				Messages.MediportOutputter_error_msg2_NoMPCConfig); //$NON-NLS-1$
		}
		
		return ret;
	}
	
	@Override
	public Result<Rechnung> doOutput(final IRnOutputter.TYPE type, final Collection<Rechnung> rnn,
		final Properties props){
		IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		final Result<Rechnung> res = new Result<Rechnung>();
		
		final ClientParam param = getSelectedParam();
		if (param == null) {
			return res;
		}
		
		IRnOutputter alternativeAusgabe = null;
		if (!alternativeRun) {
			if (!clientParamsOk(param)) {
				return res;
			}
			String selectedOutput = prefs.getString(MediPortAbstractPrefPage.MPC_AUSGABE);
			if (selectedOutput != null) {
				// Alternative Ausgabe
				List<IRnOutputter> lo = MediPortHelper.getRnOutputter();
				for (IRnOutputter ro : lo) {
					if (selectedOutput.equals(ro.getDescription())) {
						alternativeAusgabe = ro;
					}
				}
			}
		}
		final boolean alternativeIsMediPort = (alternativeAusgabe instanceof MediportOutputter);
		
		final Collection<Rechnung> alternativeRnn = new Vector<Rechnung>();
		
		try {
			progressService.runInUI(PlatformUI.getWorkbench().getProgressService(),
				new IRunnableWithProgress() {
					@Override
					public void run(final IProgressMonitor monitor){
						monitor.beginTask(Messages
							.MediportOutputter_info_exportiereRechnungen, rnn.size()); //$NON-NLS-1$
						
						Log log = Log.get("MediPortOutputter");
						
						for (Rechnung rn : rnn) {
							// getSenderEAN(rn.getMandant());
							
							boolean isMediPortParticipant =
								partnerInfoContainsEan(getRecipientEAN(rn));
							boolean fromDefaultOutputter =
								"asDefault".equals(props
									.getProperty(IRnOutputter.PROP_OUTPUT_METHOD));
							
							// Ausgabe Varianten:
							// Outputter | MediPort Teilnehmer | MediPort ist Alternative | Ausgabe
							// nach
							// Mediport | Ja | Ja | MediPort
							// Mediport | Ja | Nein | MediPort
							// Mediport | Nein | Ja | MediPort
							// Mediport | Nein | Nein | Error
							// Fall Standard | Ja | Ja | MediPort
							// Fall Standard | Ja | Nein | MediPort
							// Fall Standard | Nein | Ja | MediPort
							// Fall Standard | Nein | Nein | Alternative Ausgabe
							int outputMethod = 0;
							if (!fromDefaultOutputter && !isMediPortParticipant
								&& !alternativeIsMediPort)
								outputMethod = OUTPUT_ERROR;
							if (fromDefaultOutputter && !isMediPortParticipant
								&& !alternativeIsMediPort)
								outputMethod = OUTPUT_ALTERNATIVE;
							
							log.log("doLicensedOutput: rn.getNr()=" + rn.getNr()
								+ "; getRecipientEAN(rn)=" + getRecipientEAN(rn)
								+ "; TarmedRequirements.getEAN(getKostentraeger(rn))="
								+ TarmedRequirements.getEAN(CoreModelServiceHolder.get()
									.load(getKostentraeger(rn).getId(), IContact.class)
									.orElse(null))
								+ "; partnerInfoContainsEan(getRecipientEAN(rn))="
								+ partnerInfoContainsEan(getRecipientEAN(rn))
								+ "; isMediPortParticipant=" + isMediPortParticipant
								+ "; fromDefaultOutputter=" + fromDefaultOutputter
								+ "; alternativeRun=" + alternativeRun + "; alternativeIsMediPort="
								+ alternativeIsMediPort + "; outputMethod=" + outputMethod,
								Log.DEBUGMSG);
							
							if (outputMethod == OUTPUT_ALTERNATIVE) {
								log.log("MediPortOutputter.doLicensedOutput: alternativeRun="
									+ alternativeRun, Log.DEBUGMSG);
								alternativeRnn.add(rn);
							}
							
							if (outputMethod == OUTPUT_ERROR) {
								log.log(
									"MediPortOutputter.doLicensedOutput: isMediPortParticipant="
										+ isMediPortParticipant, Log.DEBUGMSG);
								String errorMsg =
									Messages
										.MediportOutputter_error_NotMediPortParticipant; //$NON-NLS-1$
								if (rn.getStatus() == RnStatus.OFFEN) {
									rn.reject(REJECTCODE.INTERNAL_ERROR, errorMsg);
								} else {
									String title =
										Messages.MediportOutputter_error_Rechnung; //$NON-NLS-1$
									title = title + " " + rn.getNr();
									MessageDialog.openError(new Shell(), title, errorMsg);
								}
							}
							
							if (outputMethod == OUTPUT_MEDIPORT) {
								String billFilenamePath =
									param.getDir() + File.separator + rn.getNr() + ".xml"; //$NON-NLS-1$
								log.log("doLicensedOutput: rn.getNr()=" + rn.getNr()
									+ "; billFilenamePath=" + billFilenamePath, Log.DEBUGMSG);
								if (checkTier(rn, param.getDocattr())) {
									if (doExport(rn, billFilenamePath, type, true) == null) {
										res.add(Result.SEVERITY.ERROR, 1,
											Messages.MediportOutputter_error_Rechnung //$NON-NLS-1$
												+ rn.getNr(), rn, true);
									}
								} else {
									String errorMsg =
										Messages.MediportOutputter_error_WrongTier; //$NON-NLS-1$
									if (rn.getStatus() == RnStatus.OFFEN) {
										rn.reject(REJECTCODE.INTERNAL_ERROR, errorMsg);
									} else {
										String title =
											Messages.MediportOutputter_error_Rechnung; //$NON-NLS-1$
										title = title + " " + rn.getNr();
										MessageDialog.openError(new Shell(), title, errorMsg);
									}
								}
								log.log(
									"MediPortOutputter.doLicensedOutput: rn.getStatus()="
										+ rn.getStatus(), Log.DEBUGMSG);
								if (rn.getStatus() == RnStatus.FEHLERHAFT) {
									res.add(Result.SEVERITY.ERROR, 1,
										Messages.MediportOutputter_error_Rechnung //$NON-NLS-1$
											+ rn.getNr(), rn, true);
								}
								monitor.worked(1);
								if (monitor.isCanceled()) {
									break;
								}
							}
							monitor.done();
						}
					}
				}, null);
			
			Log log = Log.get("MediPortOutputter");
			if (alternativeAusgabe != null && alternativeRnn.size() > 0) {
				boolean abort = false;
				log.log("doLicensedOutput: alternativeAusgabe.doOutput alternativeRnn.size()="
					+ alternativeRnn.size() + "; type=" + type.toString(), Log.DEBUGMSG);
				if (alternativeAusgabe instanceof MediportOutputter) {
					((MediportOutputter) alternativeAusgabe).setAlternative();
				} else {
					final IRnOutputter iro = alternativeAusgabe;
					SWTHelper.SimpleDialog dlg =
						new SWTHelper.SimpleDialog(new SWTHelper.IControlProvider() {
							@Override
							public Control getControl(Composite parent){
								parent.getShell().setText(iro.getDescription());
								return (Control) iro.createSettingsControl(parent);
								
							}
							
							@Override
							public void beforeClosing(){
								iro.saveComposite();
							}
						});
					if (dlg.open() != Dialog.OK) {
						abort = true;
					}
				}
				if (!abort) {
					alternativeAusgabe.doOutput(type, alternativeRnn, props);
				}
			}
		} catch (Exception ex) {
			ExHandler.handle(ex);
			res.add(Result.SEVERITY.ERROR, 2, ex.getMessage(), null, true);
		}
		
		if (!res.isOK()) {
			ErrorDialog
				.openError(
					null,
					Messages.MediportOutputter_error_Output, //$NON-NLS-1$
					Messages.MediportOutputter_error_MediPortTransmit, ResultAdapter.getResultAsStatus(res)); //$NON-NLS-1$
		}
		
		return res;
	}
	
	@Override
	public boolean canStorno(final Rechnung rn){
		// 26.11.2008 ts:
		// Storno ist jetzt für TP grundsätzlich unterstützt
		// Offen bleibt aber die Frage zu Stornos bei TG
		// Nach Rücksprache mit MediData (Jan Gehrig) ist Storno von MediData gar nicht empfohlen.
		// Wir bauen das also vorderhand nicht ein.
		return false;
	}
	
	@Override
	public String getDescription(){
		return Messages.MediportOutputter_description_MediPortTransmit; //$NON-NLS-1$
	}
	
	@Override
	public void saveComposite(){
		super.saveComposite();
		String paramName = null;
		if (cbParamNames != null && !cbParamNames.isDisposed()) {
			if (cbParamNames.getSelectionIndex() >= 0) {
				paramName = cbParamNames.getItem(cbParamNames.getSelectionIndex());
			}
			selectedParam = MediPortHelper.getCurrentParam(paramName);
		}
	}
	
	@Override
	protected String getIntermediateEAN(final ICoverage fall){
		String retVal = prefs.getString(MediPortAbstractPrefPage.MPC_INTERMEDIAER_EAN);
		if (retVal == null) {
			retVal = super.getIntermediateEAN(fall);
		}
		return retVal;
	}
	
	@Override
	protected String getRole(final ICoverage fall){
		String retVal = prefs.getString(MediportMainPrefPage.MPC_SERVER);
		if (retVal == null) {
			retVal = super.getRole(fall);
		}
		return retVal;
	}
	
	@Override
	protected Element buildGuarantor(IContact garant, IContact patient){
		// Hinweis:
		// XML Standard: http://www.forum-datenaustausch.ch/mdinvoicerequest_xml4.00_v1.2_d.pdf
		// Dort steht beim Feld 11310: Gesetzlicher Vertreter des Patienten.
		Element guarantor = new Element("guarantor", nsinvoice);
		guarantor.addContent(XMLExporterUtil.buildAdressElement(patient, true)); // use "Anschrift" instead of
		// contact details (e.g. for
		// "gesetzliche Vertretung")
		return guarantor;
	}
	
	@Override
	protected String getSenderEAN(IMandator actMandant){
		String senderEan = null;
		MPCProperties props = getProperties();
		if (props != null) {
			Client client = MediPortHelper.getCurrentClient();
			senderEan = client.getEan();
		}
		if (senderEan == null) {
			senderEan = TarmedRequirements.getEAN(actMandant);
		}
		return senderEan;
	}
	
	private MPCProperties getProperties(){
		if (props == null) {
			try {
				props = MPCProperties.getCurrent();
			} catch (IOException e) {
				log.warn(e.getMessage());
			}
		}
		return props;
	}
	
	private void addMessage(Composite parent, String msg1, String msg2){
		Label lblConfigError1 = new Label(parent, SWT.NONE);
		lblConfigError1.setText(msg1);
		Label lblConfigError2 = new Label(parent, SWT.NONE);
		lblConfigError2.setText(msg2);
	}
	
	private ClientParam getSelectedParam(){
		if (selectedParam == null) {
			SWTHelper.SimpleDialog dlg =
				new SWTHelper.SimpleDialog(new SWTHelper.IControlProvider() {
					@Override
					public Control getControl(Composite parent){
						parent.getShell().setText(getDescription());
						return createSettingsControl(parent);
					}
					
					@Override
					public void beforeClosing(){
						saveComposite();
					}
				});
			if (dlg.open() != Dialog.OK) {
				return null;
			}
		}
		return selectedParam;
	}
	
	private boolean clientParamsOk(ClientParam param){
		if (param == null) {
			MessageDialog.openError(new Shell(), getDescription(),
				Messages.MediportOutputter_error_msg_unknownParameter); //$NON-NLS-1$
			return false;
		}
		
		String message = null;
		
		MPCProperties props = getProperties();
		if (props != null) {
			String serverIp = props.getProperty(ConfigKeys.MEDIPORT_IP);
			if (MediportMainPrefPage.VALUE_SERVER_URL_TEST.equals(serverIp)) {
				message = Messages.MediportOutputter_info_testServer; //$NON-NLS-1$
			} else if (MediportMainPrefPage.LBL_SERVER_TEST.equals(prefs
				.getString(MediportMainPrefPage.MPC_SERVER))) {
				message = Messages.MediportOutputter_info_test; //$NON-NLS-1$
			}
		}
		
		if (message != null) {
			return MessageDialog.openQuestion(new Shell(), getDescription(), message
				+ "\n" + Messages.MediportOutputter_question_Fortfahren); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return true;
	}
	
	// private String getSenderEAN(Rechnung rn){
	// String senderEan = null;
	// MPCProperties props = getProperties();
	// if (props != null) {
	// Client client = MediPortHelper.getCurrentClient();
	// senderEan = client.getEan();
	// }
	// if (senderEan == null) {
	// senderEan = TarmedRequirements.getEAN(rn.getMandant());
	// }
	// return senderEan;
	// }
	
	private String getRecipientEAN(Rechnung rn){
		String retVal = TarmedRequirements.getRecipientEAN(CoreModelServiceHolder.get()
			.load(getKostentraeger(rn).getId(), IContact.class).orElse(null));
		if (retVal.equals("unknown")) {
			retVal = TarmedRequirements.getEAN(CoreModelServiceHolder.get()
				.load(getKostentraeger(rn).getId(), IContact.class).orElse(null));
		}
		return retVal;
	}
	
	private boolean partnerInfoContainsEan(String ean){
		final Client client = MediPortHelper.getCurrentClient();
		if (client != null) {
			log.debug("partnerInfoContainsEan: client.getPartner_file()="
				+ client.getPartner_file());
			File partnerInfoFile = new File(client.getPartner_file());
			if (partnerInfoFile.exists()) {
				String content;
				try {
					content = UtilFile.readTextFile(partnerInfoFile.getAbsolutePath());
					log.debug("partnerInfoContainsEan: ean=" + ean + "; content.indexOf(ean)="
						+ content.indexOf(ean));
					// log.log("----- content -----------", Log.DEBUGMSG);
					// log.log(content, Log.DEBUGMSG);
					// log.log("----- end of content -----------", Log.DEBUGMSG);
					return content.indexOf(ean) >= 0;
				} catch (IOException e) {
					log.warn(e.getMessage());
				}
				
			}
		}
		
		log.debug("partnerInfoContainsEan: return false");
		return false;
	}
	
	private boolean checkTier(Rechnung rn, String mediPortTiers){
		String tierRn = getTier(rn);
		String tierMP = "XX";
		if (MediPortAbstractPrefPage.TIER_PAYANT.equals(mediPortTiers)) {
			tierMP = TIER_PAYANT;
		} else if (MediPortAbstractPrefPage.TIER_GARANT_MANUELL.equals(mediPortTiers)) {
			tierMP = TIER_GARANT;
		} else if (MediPortAbstractPrefPage.TIER_GARANT_DIRECT.equals(mediPortTiers)) {
			tierMP = TIER_GARANT;
		}
		return (tierRn == tierMP);
	}
	
	private void setAlternative(){
		this.alternativeRun = true;
	}
	
	private Kontakt getKostentraeger(Rechnung rn){
		Fall tempFall = rn.getFall();
		Kontakt tempPatient = tempFall.getPatient();
		
		Kontakt kostentraeger = tempFall.getCostBearer();
		Kontakt rnAdressat = tempFall.getGarant();
		
		if (kostentraeger == null || !kostentraeger.isValid()) {
			kostentraeger = rnAdressat;
		}
		
		if (kostentraeger == null) {
			kostentraeger = tempPatient;
		}
		
		return kostentraeger;
	}
	
	/**
	 * We try to figure out whether we should use Tiers Payant or Tiers Garant. if unsure, we make
	 * it TG
	 */
	private String getTier(Rechnung rn){
		Fall tempFall = rn.getFall();
		
		Kontakt kostentraeger = tempFall.getCostBearer();
		Kontakt rnAdressat = tempFall.getGarant();
		String tiers = TIER_GARANT;
		
		if ((kostentraeger != null) && (kostentraeger.isValid())) {
			if (rnAdressat.equals(kostentraeger)) {
				tiers = TIER_PAYANT;
			} else {
				tiers = TIER_GARANT;
			}
		} else {
			tiers = TIER_GARANT;
		}
		
		return tiers;
	}
	
}
