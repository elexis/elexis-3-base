package ch.netzkonzept.elexis.medidata.output;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.jdom.Document;
import org.slf4j.LoggerFactory;

import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.SWTHelper;

import ch.elexis.data.Fall;
import ch.elexis.data.Rechnung;
import ch.netzkonzept.elexis.medidata.config.MedidataPreferencePage;
import ch.rgw.tools.Result;

public class Outputter extends XMLExporter {

	private Properties applicationProperties;
	private Properties messagesProperties;

	private static final String PLUGIN_NAME = "plugin.name";

	private static final String SEND_DIR_KEY = "key.medidata.send.dir";
	private static final String EAN_TC_KEY = "key.medidata.ean.trustcenter";
	private static final String EAN_IM_KEY = "key.medidata.ean.intermediate";
	private static final String EAN_TG_KEY = "key.medidata.ean.tiers.garant";
	private static final String ERR_MSG_DIRECTORY_STRUCTURE_MISSING = "error.msg.directory.structure.missing";
	private static final String ERR_MSG_DIRECTORY_STRUCTURE_READONLY = "error.msg.directory.structre.readonly";
	private static final String ERR_MSG_IM_EAN_MISSING = "error.msg.intermediate.ean.missing";
	private static final String ERR_MSG_TC_EAN_MISSING = "error.msg.trusctcenter.ean.missing";
	private static final String ERR_MSG_TG_EAN_MISSING = "error.msg.tiers.garant.ean.missing";

	private static final String MSG_CAT_ERROR = "msg.cat.error";
	private static final String MSG_CAT_INFO = "msg.cat.info";

	private static final String MSG_STATUS_RUNNING_EXPORT = "msg.status.running.export";
	private static final String MSG_STATUS_DONE_EXPORT = "msg.status.done.export";

	private final SettingsPreferenceStore preferenceStore = new SettingsPreferenceStore(CoreHub.globalCfg);

	public Outputter() {
		loadProperties();
	}

	private void loadProperties() {
		try {
			setApplicationProperties(new Properties());
			setMessagesProperties(new Properties());
			String separator = FileSystems.getDefault().getSeparator();
			getApplicationProperties().load(Outputter.class.getClassLoader()
					.getResourceAsStream(separator + "resources" + separator + "application.properties"));
			getMessagesProperties().load(Outputter.class.getClassLoader()
					.getResourceAsStream(separator + "resources" + separator + "messages_de.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Result<Rechnung> doOutput(final IRnOutputter.TYPE type, final Collection<Rechnung> rechnungen,
			Properties props) {
		Result<Rechnung> result = new Result<Rechnung>();
		LoggerFactory.getLogger(Outputter.class).info("Start Extraction of " + rechnungen.size() + "bills");
		if (checkPluginConfiguration(preferenceStore)) {
			ProgressMonitorDialog progress = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
			try {
				progress.run(true, true, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						monitor.beginTask(
								MessageFormat.format(getMessagesProperties().getProperty(MSG_STATUS_RUNNING_EXPORT),
										rechnungen.size()),
								rechnungen.size());
						for (Rechnung rechnung : rechnungen) {
							LoggerFactory.getLogger(Outputter.class).info("Start exporting bill " + rechnung.getNr());

							String destinationPath = preferenceStore
									.getString(getApplicationProperties().getProperty(SEND_DIR_KEY))
									+ System.getProperty("file.separator") + rechnung.getNr() + ".xml";

							String JSONdestinationPath = preferenceStore
									.getString(getApplicationProperties().getProperty(SEND_DIR_KEY))
									+ System.getProperty("file.separator") + rechnung.getNr() + ".json";

							boolean isTP = rechnung.getFall().getCostBearer().equals(rechnung.getFall().getGarant());

							LoggerFactory.getLogger(Outputter.class)
									.info(isTP ? "Bill " + rechnung.getNr() + "is of type Tiers Payant"
											: "Bill " + rechnung.getNr() + "is of type Tiers Garant");
							setPrintAtIntermediate(!isTP);
							Document bill = doExport(rechnung, destinationPath, type, true);

							LoggerFactory.getLogger(Outputter.class).info("Bill written to " + destinationPath);
							if (!isTP) {
								doJSONExport(rechnung,
										preferenceStore.getString(getApplicationProperties().getProperty(EAN_TG_KEY)),
										JSONdestinationPath);
							}
							LoggerFactory.getLogger(Outputter.class).info(
									"JDOM Document has been created and will be pushed forward to the properties adjustments");

							monitor.worked(1);
							if (monitor.isCanceled()) {
								break;
							}
						}
						monitor.done();
					}
				});
				MessageDialog.openInformation(Display.getDefault().getActiveShell(),
						getMessagesProperties().getProperty(MSG_CAT_INFO), MessageFormat.format(
								getMessagesProperties().getProperty(MSG_STATUS_DONE_EXPORT), rechnungen.size()));

			} catch (InvocationTargetException | InterruptedException | NullPointerException e) {
				LoggerFactory.getLogger(Outputter.class).error("Error outputting bills. The process was stpped.", e);
				MessageDialog.openError(Display.getDefault().getActiveShell(),
						getMessagesProperties().getProperty(MSG_CAT_ERROR), e.getMessage());
			}
		}
		return result;
	}

	//@Override
	//protected String getIntermediateEAN(final Fall fall) {
	//	String retVal = preferenceStore.getString(getApplicationProperties().getProperty(EAN_IM_KEY));

	//	if (retVal == null) {
	//		retVal = super.getIntermediateEAN(fall);
	//	}
	//	return retVal;
	//}

	private void doJSONExport(final Rechnung rechnung, String toOrganisation, final String dest) {

		StringBuffer jSONCOntent = new StringBuffer();

		jSONCOntent.append("{");
		jSONCOntent.append(System.getProperty("line.separator"));
		jSONCOntent.append("\"toOrganization\":\"" + toOrganisation + "\"");
		jSONCOntent.append(System.getProperty("line.separator"));

		jSONCOntent.append("}");

		try {
			Files.write(Paths.get(dest), jSONCOntent.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDescription() {
		return getApplicationProperties().getProperty(PLUGIN_NAME);
	}

	@Override
	public void saveComposite() {
		super.saveComposite();
	}

	private boolean checkPluginConfiguration(SettingsPreferenceStore preferenceStore) {

		boolean directoryStructureExists = false;
		boolean direcoryStructureWriteable = true;
		boolean intermediateEANExists = false;
		boolean trustCenterEANExists = true;
		boolean tiersGarantEANExists = false;

		directoryStructureExists = Files
				.exists(Paths.get(preferenceStore.getString(getApplicationProperties().getProperty(SEND_DIR_KEY))));

		intermediateEANExists = !preferenceStore.getString(getApplicationProperties().getProperty(EAN_IM_KEY))
				.isEmpty();
		trustCenterEANExists = !preferenceStore.getString(getApplicationProperties().getProperty(EAN_TC_KEY)).isEmpty();
		tiersGarantEANExists = !preferenceStore.getString(getApplicationProperties().getProperty(EAN_TG_KEY)).isEmpty();

		if (!directoryStructureExists) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					getMessagesProperties().getProperty(MSG_CAT_ERROR),
					getMessagesProperties().getProperty(ERR_MSG_DIRECTORY_STRUCTURE_MISSING));
		}

		if (directoryStructureExists && !direcoryStructureWriteable) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					getMessagesProperties().getProperty(MSG_CAT_ERROR),
					getMessagesProperties().getProperty(ERR_MSG_DIRECTORY_STRUCTURE_READONLY));
		}

		if (!intermediateEANExists) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					getMessagesProperties().getProperty(MSG_CAT_ERROR),
					getMessagesProperties().getProperty(ERR_MSG_IM_EAN_MISSING));
		}

		if (!trustCenterEANExists) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					getMessagesProperties().getProperty(MSG_CAT_ERROR),
					getMessagesProperties().getProperty(ERR_MSG_TC_EAN_MISSING));
		}

		if (!tiersGarantEANExists) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					getMessagesProperties().getProperty(MSG_CAT_ERROR),
					getMessagesProperties().getProperty(ERR_MSG_TG_EAN_MISSING));
		}

		return directoryStructureExists && direcoryStructureWriteable && intermediateEANExists && trustCenterEANExists
				&& tiersGarantEANExists;
	}

	@Override
	public Control createSettingsControl(final Object parent) {
		final Composite parentInc = (Composite) parent;
		Composite ret = new Composite(parentInc, SWT.NONE);
		return ret;
	}

	public Properties getMessagesProperties() {
		return messagesProperties;
	}

	public void setMessagesProperties(Properties messagesProperties) {
		this.messagesProperties = messagesProperties;
	}

	public Properties getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}
}
