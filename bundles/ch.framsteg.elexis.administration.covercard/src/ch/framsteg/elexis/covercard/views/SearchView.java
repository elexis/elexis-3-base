package ch.framsteg.elexis.covercard.views;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.wizard.WizardDialog;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.HttpHostConnectException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.jdom2.JDOMException;
import org.osgi.service.component.annotations.Reference;
import org.xml.sax.SAXException;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.ui.events.ElexisUiSyncEventListenerImpl;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.Fall;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Xid;
import ch.framsteg.elexis.covercard.config.CovercardPreferencePage;
import ch.framsteg.elexis.covercard.control.CardInfoRetriever;
import ch.framsteg.elexis.covercard.control.PatientInfoRetriever;
import ch.framsteg.elexis.covercard.dao.CardInfoData;
import ch.framsteg.elexis.covercard.dao.PatientInfoData;
import ch.framsteg.elexis.covercard.exceptions.BlockedCardException;
import ch.framsteg.elexis.covercard.exceptions.InvalidCardException;
import ch.framsteg.elexis.covercard.exceptions.UnsupportedCardException;
import ch.framsteg.elexis.covercard.views.dialogs.CardInfoDialog;
import ch.framsteg.elexis.covercard.views.wizard.RegisterWizard;

public class SearchView extends ViewPart implements IRefreshable {

	private boolean defaultAsc;
	private boolean idAsc;
	private boolean nameAsc;
	private boolean prenameAsc;
	private boolean birthdayAsc;
	private boolean sexAsc;
	private boolean addressAsc;
	private boolean zipAsc;
	private boolean locationAsc;
	private boolean countryAsc;
	private boolean ahvAsc;
	private boolean mobileAsc;
	private boolean phoneAsc;
	private boolean emailAsc;
	private boolean insurantNrAsc;
	private boolean covercardNrAsc;
	private boolean idCardNrAsc;
	private boolean descriptionAsc;

	private Group listGroup;
	private Text txtCardNr;
	private Button btnUpdate;
	private Button btnShowDetail;
	private Button btnExport;
	private Table table;

	private TableColumn tableColumn0;
	private TableColumn tableColumn1;
	private TableColumn tableColumn2;
	private TableColumn tableColumn3;
	private TableColumn tableColumn4;
	private TableColumn tableColumn5;
	private TableColumn tableColumn6;
	private TableColumn tableColumn7;
	private TableColumn tableColumn8;
	private TableColumn tableColumn9;
	private TableColumn tableColumn10;
	private TableColumn tableColumn11;
	private TableColumn tableColumn12;
	private TableColumn tableColumn13;
	private TableColumn tableColumn14;
	private TableColumn tableColumn15;
	private TableColumn tableColumn16;

	private int patientCount;

	private int emptyPrenameCount;
	private int emptyBirthdayCount;
	private int emotySexCount;
	private int emptyAdressCount;
	private int emptyZipCount;
	private int emptyLocationCount;
	private int emptyCountryCount;
	private int emptyAHVCount;
	private int emptyMobileCount;
	private int emptyPhoneCount;
	private int emptyEmailCount;
	private int emptyInsuranceCount;
	private int emptyCovercardCount;
	private int emptyCardCount;

	private Properties applicationProperties;
	private Properties messagesProperties;

	private List<Patient> allPatients;
	private StringBuilder filteredTableContent;

	private final static String ID = "msg.PatientsInfoDialog.id";
	private final static String INFO = "msg.PatientsInfoDialog.info";
	private final static String NO_COVERCARD = "msg.PatientsInfoDialog.no.covercard";
	private final static String QUESTION = "msg.PatientsInfoDialog.question";
	private final static String INFORMATION_AVAILABLE = "msg.PatientsInfoDialog.information.available";
	private final static String NAME = "msg.PatientsInfoDialog.name";
	private final static String PRENAME = "msg.PatientsInfoDialog.prename";
	private final static String BIRTHDAY = "msg.PatientsInfoDialog.birthday";
	private final static String SEX = "msg.PatientsInfoDialog.sex";
	private final static String ADDRESS = "msg.PatientsInfoDialog.address";
	private final static String ZIP = "msg.PatientsInfoDialog.zip";
	private final static String LOCATION = "msg.PatientsInfoDialog.location";
	private final static String COUNTRY = "msg.PatientsInfoDialog.country";
	private final static String AHV = "msg.PatientsInfoDialog.ahv";
	private final static String MOBIL = "msg.PatientsInfoDialog.mobil";
	private final static String PHONE = "msg.PatientsInfoDialog.phone";
	private final static String EMAIL = "msg.PatientsInfoDialog.email";
	private final static String INSURANT_NUMBER = "msg.PatientsInfoDialog.insurant.number";
	private final static String CARD_NUMBER = "msg.PatientsInfoDialog.card.number";
	private final static String CARD_ID_NUMBER = "msg.PatientsInfoDialog.card.id.number";
	private final static String DESCRIPTION = "msg.PatientsInfoDialog.description";

	private static final String GRP_QUERY = "label.searchView.group.search";
	private static final String GRP_LIST = "label.searchView.group.list";
	private static final String TOOLTIP_ENTER_COVERCARD_NR = "tooltip.searchView.enter.covercard.nr";
	private static final String TXT_ONLINE_RESEARCH = "label.searchView.online.research";
	private static final String TXT_ONLINE_RESEARCH_BUTTON = "label.searchView.online.research.button";
	private static final String TOOLTIP_ONLINE_RESEARCH = "tooltip.searchView.online.research";
	private static final String TXT_CLEAR_TXT_FIELD = "label.searchView.clear.txt.field";
	private static final String TOOLTIP_CLEAR_TXT_FIELD = "tooltip.searchView.clear.txt.field";
	private static final String TXT_ALL_PATIENTS = "wizard.page2.btn.show.all";
	private static final String TOOLTIP_ALL_PATIENTS = "tooltip.searchView.all.patients";
	private static final String TXT_PROGRESS_BAR = "label.searchView.progressbar";
	private static final String TXT_FILE_DIALOG = "label.searchView.file.dialog";
	private static final String MSG_TITLE = "msg.dialog.title";
	private static final String MSG_TITLE_MSG = "msg.dialog.msg";

	private static final String ERR_MSG_PARSER_CONFIGURATION_EXCEPTION = "error.searchView.ParserConfigurationException";
	private static final String ERR_MSG_CLIENT_PROTOCOL_EXCEPTION = "error.searchView.ClientProtocolException";
	private static final String ERR_MSG_HTTP_HOST_CONNECT_EXCEPTION = "error.searchView.HttpHostConnectException";
	private static final String ERR_MSG_IO_EXCEPTION = "error.searchView.IOException";
	private static final String ERR_MSG_UNSUPPORTED_OPERATION_EXCEPTION = "error.searchView.UnsupportedOperationException";
	private static final String ERR_MSG_SAX_EXCEPTION = "error.searchView.SAXException";
	private static final String ERR_MSG_JDOM_EXCEPTION = "error.searchView.JDOMException";
	private static final String ERR_MSG_NULL_POINTER_EXCEPTION = "error.searchView.NullPointerException";
	private static final String ERR_MSG_SYNTAX_ERROR = "error.searchView.syntax.error";
	private static final String ERR_MSG_TITLE = "error.searchView.title";
	private static final String ERR_MSG_MISCONFIGURATION = "Das Plugin ist nicht oder nicht vollständig konfiguriert. Unter Einstellungen alle Angaben hinterlegen";
	private static final String ERR_MSG_INVALID_CARD = "error.searchView.invalid.card";
	private static final String ERR_MSG_UNSUPPORTED_CARD = "error.searchView.unsupported.card";
	private static final String ERR_MSG_BLOCKED_CARD = "error.searchView.blocked.card";
	private static final String ERR_MSG_EMPTY_LIST = "error.searchView.empty.list";
	private final static String ERR_MSG_ERR = "err.dialog.err";

	private static final String KEY_URL = "key.url";
	private static final String KEY_XML_PARAMETER = "key.parameter.xml";
	private static final String KEY_PROXY_SERVER = "key.proxy.server";
	private static final String KEY_PROXY_PORT = "key.proxy.port";

	private static final String KEY_REGEX_PATTERN = "key.cardreader.regex.pattern";

	private final static String DOMAIN_COVERCARD_AHV = "domain.covercard.ahv";
	private final static String DOMAIN_INSURED_NUMBER = "domain.covercard.insured.number";
	private final static String DOMAIN_CARDD_NUMBER = "domain.covercard.card.number";
	private final static String DOMAIN_INSURED_PERSON_NUMBER = "domain.covercard.insured.person.number";

	private final static String DELIMITER = "delilmiter";
	private final static String NEWLINE = "newline";
	private final static String NEWLINE_IDENTIFICATION = "file.export.regex.newline.identification";
	private final static String NEWLINE_REPLACEMENT = "file.export.regex.newline.replacement";
	private final static String EXTENSION = "file.export.extension";

	@Reference
	private IConfigService configService;

	public SearchView() {
		defaultAsc = true;
		nameAsc = true;
		prenameAsc = true;
		birthdayAsc = true;
		sexAsc = true;
		addressAsc = true;
		zipAsc = true;
		locationAsc = true;
		countryAsc = true;
		ahvAsc = true;
		mobileAsc = true;
		phoneAsc = true;
		insurantNrAsc = true;
		covercardNrAsc = true;
		idCardNrAsc = true;
		loadProperties();
		ElexisEventDispatcher.getInstance().addListeners(eeli_case_update);
		ElexisEventDispatcher.getInstance().addListeners(eeli_pat_sync);
	}

	private void loadProperties() {
		try {
			setApplicationProperties(new Properties());
			setMessagesProperties(new Properties());

			getApplicationProperties().load(CovercardPreferencePage.class.getClassLoader()
					.getResourceAsStream("/resources/application.properties"));
			getMessagesProperties().load(CovercardPreferencePage.class.getClassLoader()
					.getResourceAsStream("/resources/messages_de.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		GridLayout rootLayout = new GridLayout();
		rootLayout.numColumns = 1;

		GridData rootGridData = new GridData();
		rootGridData.grabExcessHorizontalSpace = true;
		rootGridData.horizontalAlignment = SWT.FILL;

		Composite rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(rootLayout);
		rootComposite.setLayoutData(rootGridData);

		GridLayout searchGroupLayout = new GridLayout();
		searchGroupLayout.numColumns = 1;

		GridLayout listGroupLayout = new GridLayout();
		listGroupLayout.numColumns = 1;

		GridLayout tableCompositeLayout = new GridLayout();
		tableCompositeLayout.numColumns = 1;

		GridLayout searchButtonsLayout = new GridLayout();
		searchButtonsLayout.numColumns = 2;

		GridLayout listButtonsLayout = new GridLayout();
		listButtonsLayout.numColumns = 4;

		GridData searchGroupGridData = new GridData();
		searchGroupGridData.horizontalAlignment = SWT.FILL;
		searchGroupGridData.verticalAlignment = SWT.FILL;

		GridData listGroupGridData = new GridData();
		listGroupGridData.grabExcessHorizontalSpace = true;
		listGroupGridData.grabExcessVerticalSpace = true;
		listGroupGridData.horizontalAlignment = SWT.FILL;
		listGroupGridData.verticalAlignment = SWT.FILL;

		GridData searchTxtGridData = new GridData();
		searchTxtGridData.grabExcessHorizontalSpace = true;
		searchTxtGridData.horizontalAlignment = SWT.FILL;
		searchTxtGridData.verticalAlignment = SWT.FILL;
		searchTxtGridData.heightHint = 19;

		GridData searchButtonsGridData = new GridData();
		searchButtonsGridData.grabExcessHorizontalSpace = true;
		searchButtonsGridData.horizontalAlignment = SWT.FILL;
		searchButtonsGridData.grabExcessVerticalSpace = true;
		searchButtonsGridData.verticalAlignment = SWT.TOP;
		searchButtonsGridData.heightHint = 50;

		GridData searchButtonGridData = new GridData();
		searchButtonGridData.grabExcessHorizontalSpace = true;
		searchButtonGridData.horizontalAlignment = SWT.FILL;
		searchButtonGridData.grabExcessVerticalSpace = true;
		searchButtonGridData.verticalAlignment = SWT.TOP;
		searchButtonGridData.heightHint = 50;

		GridData listButtonsGridData = new GridData();
		listButtonsGridData.horizontalAlignment = SWT.FILL;
		listButtonsGridData.verticalAlignment = SWT.FILL;
		listButtonsGridData.heightHint = 50;

		GridData listButtonGridData = new GridData();
		listButtonGridData.grabExcessHorizontalSpace = true;
		listButtonGridData.horizontalAlignment = SWT.FILL;
		listButtonGridData.grabExcessVerticalSpace = true;
		listButtonGridData.verticalAlignment = SWT.TOP;
		listButtonGridData.heightHint = 50;

		GridData tableCompositeGridData = new GridData();
		tableCompositeGridData.grabExcessHorizontalSpace = true;
		tableCompositeGridData.horizontalAlignment = SWT.FILL;
		tableCompositeGridData.grabExcessVerticalSpace = true;
		tableCompositeGridData.verticalAlignment = SWT.FILL;

		Group searchGroup = new Group(rootComposite, SWT.FILL);
		searchGroup.setText(getMessagesProperties().getProperty(GRP_QUERY));
		searchGroup.setLayout(searchGroupLayout);
		searchGroup.setLayoutData(searchGroupGridData);

		Label searchLabel = new Label(searchGroup, SWT.FILL);
		searchLabel.setText(getMessagesProperties().getProperty(TXT_ONLINE_RESEARCH));
		searchLabel.setToolTipText(getMessagesProperties().getProperty(TOOLTIP_ONLINE_RESEARCH));

		txtCardNr = new Text(searchGroup, SWT.BORDER | SWT.FILL);
		txtCardNr.setToolTipText(getMessagesProperties().getProperty(TOOLTIP_ENTER_COVERCARD_NR));
		txtCardNr.setFocus();
		txtCardNr.setEnabled(true);
		txtCardNr.setLayoutData(searchTxtGridData);

		Composite searchButtonsComposite = new Composite(searchGroup, SWT.NONE);
		searchButtonsComposite.setLayout(searchButtonsLayout);
		searchButtonsComposite.setLayoutData(searchButtonsGridData);

		Button btnLookup = new Button(searchButtonsComposite, SWT.PUSH);
		btnLookup.setText(getMessagesProperties().getProperty(TXT_ONLINE_RESEARCH_BUTTON));
		btnLookup.setToolTipText(getMessagesProperties().getProperty(TOOLTIP_ONLINE_RESEARCH));
		btnLookup.setLayoutData(searchButtonGridData);

		Button btnClear = new Button(searchButtonsComposite, SWT.PUSH);
		btnClear.setText(getMessagesProperties().getProperty(TXT_CLEAR_TXT_FIELD));
		btnClear.setToolTipText(getMessagesProperties().getProperty(TOOLTIP_CLEAR_TXT_FIELD));
		btnClear.setLayoutData(searchButtonGridData);

		listGroup = new Group(rootComposite, SWT.NONE);
		listGroup.setText(getMessagesProperties().getProperty(GRP_LIST));
		listGroup.setLayout(listGroupLayout);
		listGroup.setLayoutData(listGroupGridData);

		Composite tableComposite = new Composite(listGroup, SWT.NONE);
		tableComposite.setLayout(tableCompositeLayout);
		tableComposite.setLayoutData(tableCompositeGridData);

		Table table = new Table(tableComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setLinesVisible(true);
		table.setVisible(true);

		GridData tableGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		tableGridData.heightHint = listGroup.getSize().y;
		table.setLayoutData(tableGridData);

		Composite listButtonsComposite = new Composite(listGroup, SWT.NONE);
		listButtonsComposite.setLayout(listButtonsLayout);
		listButtonsComposite.setLayoutData(listButtonsGridData);

		btnUpdate = new Button(listButtonsComposite, SWT.PUSH);
		btnUpdate.setText("Ausgewählter Patient aktualisieren");
		btnUpdate.setToolTipText(getMessagesProperties().getProperty(TOOLTIP_ONLINE_RESEARCH));
		btnUpdate.setLayoutData(listButtonGridData);
		btnUpdate.setEnabled(false);

		btnShowDetail = new Button(listButtonsComposite, SWT.PUSH);
		btnShowDetail.setText("Ausgewählter Patient anzeigen");
		btnShowDetail.setToolTipText(getMessagesProperties().getProperty(TOOLTIP_ONLINE_RESEARCH));
		btnShowDetail.setLayoutData(listButtonGridData);
		btnShowDetail.setEnabled(false);

		btnExport = new Button(listButtonsComposite, SWT.PUSH);
		btnExport.setText("Alle Patienten exportieren (csv)");
		btnExport.setToolTipText(getMessagesProperties().getProperty(TOOLTIP_CLEAR_TXT_FIELD));
		btnExport.setLayoutData(listButtonGridData);
		btnExport.setEnabled(false);

		Button btnShowAll = new Button(listButtonsComposite, SWT.PUSH);
		btnShowAll.setText(getMessagesProperties().getProperty(TXT_ALL_PATIENTS));
		btnShowAll.setToolTipText(getMessagesProperties().getProperty(TOOLTIP_ALL_PATIENTS));
		btnShowAll.setLayoutData(listButtonGridData);

		tableColumn0 = new TableColumn(table, SWT.LEFT);
		tableColumn1 = new TableColumn(table, SWT.LEFT);
		tableColumn2 = new TableColumn(table, SWT.LEFT);
		tableColumn3 = new TableColumn(table, SWT.LEFT);
		tableColumn4 = new TableColumn(table, SWT.LEFT);
		tableColumn5 = new TableColumn(table, SWT.LEFT);
		tableColumn6 = new TableColumn(table, SWT.LEFT);
		tableColumn7 = new TableColumn(table, SWT.LEFT);
		tableColumn8 = new TableColumn(table, SWT.LEFT);
		tableColumn9 = new TableColumn(table, SWT.LEFT);
		tableColumn10 = new TableColumn(table, SWT.LEFT);
		tableColumn11 = new TableColumn(table, SWT.LEFT);
		tableColumn12 = new TableColumn(table, SWT.LEFT);
		tableColumn13 = new TableColumn(table, SWT.LEFT);
		tableColumn14 = new TableColumn(table, SWT.LEFT);
		tableColumn15 = new TableColumn(table, SWT.LEFT);
		tableColumn16 = new TableColumn(table, SWT.LEFT);

		tableColumn0.setText(messagesProperties.getProperty(ID));
		tableColumn0.setAlignment(SWT.LEFT);
		tableColumn0.addListener(SWT.Selection, idSortListener);
		tableColumn1.setText(messagesProperties.getProperty(NAME));
		tableColumn1.setAlignment(SWT.LEFT);
		tableColumn1.addListener(SWT.Selection, nameSortListener);
		tableColumn2.setText(messagesProperties.getProperty(PRENAME));
		tableColumn2.setAlignment(SWT.LEFT);
		tableColumn2.addListener(SWT.Selection, prenameSortListener);
		tableColumn3.setText(messagesProperties.getProperty(BIRTHDAY));
		tableColumn3.setAlignment(SWT.LEFT);
		tableColumn3.addListener(SWT.Selection, birthdaySortListener);
		tableColumn4.setText(messagesProperties.getProperty(SEX));
		tableColumn4.setAlignment(SWT.LEFT);
		tableColumn4.addListener(SWT.Selection, sexSortListener);
		tableColumn5.setText(messagesProperties.getProperty(ADDRESS));
		tableColumn5.setAlignment(SWT.LEFT);
		tableColumn5.addListener(SWT.Selection, addressSortListener);
		tableColumn6.setText(messagesProperties.getProperty(ZIP));
		tableColumn6.setAlignment(SWT.LEFT);
		tableColumn6.addListener(SWT.Selection, zipSortListener);
		tableColumn7.setText(messagesProperties.getProperty(LOCATION));
		tableColumn7.setAlignment(SWT.LEFT);
		tableColumn7.addListener(SWT.Selection, locationSortListener);
		tableColumn8.setText(messagesProperties.getProperty(COUNTRY));
		tableColumn8.setAlignment(SWT.LEFT);
		tableColumn8.addListener(SWT.Selection, countrySortListener);
		tableColumn9.setText(messagesProperties.getProperty(AHV));
		tableColumn9.setAlignment(SWT.LEFT);
		tableColumn9.addListener(SWT.Selection, ahvSortListener);
		tableColumn10.setText(messagesProperties.getProperty(MOBIL));
		tableColumn10.setAlignment(SWT.LEFT);
		tableColumn10.addListener(SWT.Selection, mobileSortListener);
		tableColumn11.setText(messagesProperties.getProperty(PHONE));
		tableColumn11.setAlignment(SWT.LEFT);
		tableColumn11.addListener(SWT.Selection, phoneSortListener);
		tableColumn12.setText(messagesProperties.getProperty(EMAIL));
		tableColumn12.setAlignment(SWT.LEFT);
		tableColumn12.addListener(SWT.Selection, emailSortListener);
		tableColumn13.setText(messagesProperties.getProperty(INSURANT_NUMBER));
		tableColumn13.setAlignment(SWT.LEFT);
		tableColumn13.addListener(SWT.Selection, insurantNrSortListener);
		tableColumn14.setText(messagesProperties.getProperty(CARD_NUMBER));
		tableColumn14.setAlignment(SWT.LEFT);
		tableColumn14.addListener(SWT.Selection, covercardNrSortListener);
		tableColumn15.setText(messagesProperties.getProperty(CARD_ID_NUMBER));
		tableColumn15.setAlignment(SWT.LEFT);
		tableColumn15.addListener(SWT.Selection, idCardNrSortListener);
		tableColumn16.setText(messagesProperties.getProperty(DESCRIPTION));
		tableColumn16.setAlignment(SWT.LEFT);
		tableColumn16.addListener(SWT.Selection, descriptionSortListener);

		table.setHeaderVisible(true);

		for (TableColumn tableColumn : table.getColumns()) {
			tableColumn.pack();
		}
		table.setLinesVisible(true);
		table.pack();
		table.deselectAll();

		btnLookup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!applicationProperties.getProperty(KEY_URL).isEmpty()
						&& !applicationProperties.getProperty(KEY_XML_PARAMETER).isEmpty()
						&& !applicationProperties.getProperty(KEY_PROXY_SERVER).isEmpty()
						&& !applicationProperties.getProperty(KEY_PROXY_PORT).isEmpty()
						&& !(Xid.getDomain("www.xid.ch/framsteg/covercard/insured-number") == null)
						&& !(Xid.getDomain("www.xid.ch/framsteg/covercard/card-number") == null)
						&& !(Xid.getDomain("www.xid.ch/framsteg/covercard/insured-person-number") == null)) {

					String searchString = txtCardNr.getText();
					String patternString = configService.get(getApplicationProperties().getProperty(KEY_REGEX_PATTERN),
							"");

					Pattern pattern = Pattern.compile(patternString);
					Matcher matcher = pattern.matcher(searchString);

					if (matcher.find()) {
						searchString = matcher.group(0);
						try {
							CardInfoRetriever cardInfoRetriever = new CardInfoRetriever(applicationProperties,
									messagesProperties);
							CardInfoData cardInfoData = cardInfoRetriever.getCardInfo(searchString);
							PatientInfoRetriever patientInfoRetriever = new PatientInfoRetriever(cardInfoData,
									applicationProperties, messagesProperties);
							PatientInfoData patientInfoData = patientInfoRetriever.getPatientInfo();
							WizardDialog wizardDialog = new WizardDialog(parent.getShell(),
									new RegisterWizard(searchString, getApplicationProperties(),
											getMessagesProperties(), cardInfoData, patientInfoData));
							wizardDialog.open();
						} catch (ParserConfigurationException pce) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_PARSER_CONFIGURATION_EXCEPTION));
							pce.printStackTrace();
						} catch (ClientProtocolException cpe) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_CLIENT_PROTOCOL_EXCEPTION));
							cpe.printStackTrace();
						} catch (HttpHostConnectException hhce) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_HTTP_HOST_CONNECT_EXCEPTION));
							hhce.printStackTrace();
						} catch (IOException ioe) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_IO_EXCEPTION));
							ioe.printStackTrace();
						} catch (UnsupportedOperationException uoe) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_UNSUPPORTED_OPERATION_EXCEPTION));
							uoe.printStackTrace();
						} catch (SAXException sae) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_SAX_EXCEPTION));
							sae.printStackTrace();
						} catch (JDOMException jde) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_JDOM_EXCEPTION));
							jde.printStackTrace();
						} catch (NullPointerException npe) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_NULL_POINTER_EXCEPTION));
							npe.printStackTrace();
						} catch (InvalidCardException ice) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_INVALID_CARD));
							ice.printStackTrace();
						} catch (UnsupportedCardException usc) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_UNSUPPORTED_CARD));
							usc.printStackTrace();
						} catch (BlockedCardException bce) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_BLOCKED_CARD));
							bce.printStackTrace();
						}
					} else {
						MessageDialog.openInformation(parent.getShell(),
								getMessagesProperties().getProperty(ERR_MSG_TITLE),
								getMessagesProperties().getProperty(ERR_MSG_SYNTAX_ERROR));
					}
				} else {
					MessageDialog.openError(parent.getShell(), "Fehler",
							messagesProperties.getProperty(ERR_MSG_MISCONFIGURATION));
				}
			}
		});

		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtCardNr.setText("");
				txtCardNr.setFocus();
			}
		});

		btnShowDetail.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] item = table.getSelection();
				String patID = new Query<Patient>(Patient.class).findSingle(Patient.FLD_PATID, Query.EQUALS,
						item[0].getText());
				Patient patient = Patient.load(patID);
				String cardNumber = patient.getXid(applicationProperties.getProperty(DOMAIN_INSURED_PERSON_NUMBER));
				CardInfoRetriever cardInfoRetriever = new CardInfoRetriever(applicationProperties, messagesProperties);
				try {
					CardInfoData cardInfoData = cardInfoRetriever.getCardInfo(cardNumber);
					CardInfoDialog cardInfoDialog = new CardInfoDialog(parent.getShell(), cardInfoData,
							messagesProperties);
					cardInfoDialog.open();
				} catch (ParserConfigurationException pce) {
					MessageDialog.openInformation(parent.getShell(), getMessagesProperties().getProperty(ERR_MSG_TITLE),
							getMessagesProperties().getProperty(ERR_MSG_PARSER_CONFIGURATION_EXCEPTION));
					pce.printStackTrace();
				} catch (ClientProtocolException cpe) {
					MessageDialog.openInformation(parent.getShell(), getMessagesProperties().getProperty(ERR_MSG_TITLE),
							getMessagesProperties().getProperty(ERR_MSG_CLIENT_PROTOCOL_EXCEPTION));
					cpe.printStackTrace();
				} catch (HttpHostConnectException hhce) {
					MessageDialog.openInformation(parent.getShell(), getMessagesProperties().getProperty(ERR_MSG_TITLE),
							getMessagesProperties().getProperty(ERR_MSG_HTTP_HOST_CONNECT_EXCEPTION));
					hhce.printStackTrace();
				} catch (IOException ioe) {
					MessageDialog.openInformation(parent.getShell(), getMessagesProperties().getProperty(ERR_MSG_TITLE),
							getMessagesProperties().getProperty(ERR_MSG_IO_EXCEPTION));
					ioe.printStackTrace();
				} catch (UnsupportedOperationException uoe) {
					MessageDialog.openInformation(parent.getShell(), getMessagesProperties().getProperty(ERR_MSG_TITLE),
							getMessagesProperties().getProperty(ERR_MSG_UNSUPPORTED_OPERATION_EXCEPTION));
					uoe.printStackTrace();
				} catch (SAXException sae) {
					MessageDialog.openInformation(parent.getShell(), getMessagesProperties().getProperty(ERR_MSG_TITLE),
							getMessagesProperties().getProperty(ERR_MSG_SAX_EXCEPTION));
					sae.printStackTrace();
				} catch (JDOMException jde) {
					MessageDialog.openInformation(parent.getShell(), getMessagesProperties().getProperty(ERR_MSG_TITLE),
							getMessagesProperties().getProperty(ERR_MSG_JDOM_EXCEPTION));
					jde.printStackTrace();
				} catch (NullPointerException npe) {
					MessageDialog.openInformation(parent.getShell(), getMessagesProperties().getProperty(ERR_MSG_TITLE),
							getMessagesProperties().getProperty(ERR_MSG_NULL_POINTER_EXCEPTION));
					npe.printStackTrace();
				} catch (InvalidCardException ice) {
					MessageDialog.openInformation(parent.getShell(), getMessagesProperties().getProperty(ERR_MSG_TITLE),
							getMessagesProperties().getProperty(ERR_MSG_INVALID_CARD));
					ice.printStackTrace();
				} catch (UnsupportedCardException usc) {
					MessageDialog.openInformation(parent.getShell(), getMessagesProperties().getProperty(ERR_MSG_TITLE),
							getMessagesProperties().getProperty(ERR_MSG_UNSUPPORTED_CARD));
					usc.printStackTrace();
				} catch (BlockedCardException bce) {
					MessageDialog.openInformation(parent.getShell(), getMessagesProperties().getProperty(ERR_MSG_TITLE),
							getMessagesProperties().getProperty(ERR_MSG_BLOCKED_CARD));
					bce.printStackTrace();
				}
			}
		});

		btnShowAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadTable(table, parent);
			}
		});

		btnUpdate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				TableItem[] item = table.getSelection();
				String patID = new Query<Patient>(Patient.class).findSingle(Patient.FLD_PATID, Query.EQUALS,
						item[0].getText());
				Patient patient = Patient.load(patID);

				String cardnumber = patient.getXid(applicationProperties.getProperty(DOMAIN_INSURED_PERSON_NUMBER));
				if (cardnumber.isEmpty()) {
					MessageDialog.openInformation(rootComposite.getShell(), messagesProperties.getProperty(INFO),
							messagesProperties.getProperty(NO_COVERCARD));
				} else {
					boolean decision = MessageDialog.openQuestion(rootComposite.getShell(),
							messagesProperties.getProperty(QUESTION),
							MessageFormat.format(messagesProperties.getProperty(INFORMATION_AVAILABLE), cardnumber));
					if (decision) {
						CardInfoRetriever cardInfoRetriever = new CardInfoRetriever(applicationProperties,
								messagesProperties);
						try {
							CardInfoData cardInfoData = cardInfoRetriever.getCardInfo(cardnumber);
							PatientInfoRetriever patientInfoRetriever = new PatientInfoRetriever(cardInfoData,
									applicationProperties, messagesProperties);
							PatientInfoData patientInfoData = patientInfoRetriever.getPatientInfo();
							WizardDialog wizardDialog = new WizardDialog(parent.getShell(),
									new RegisterWizard(cardnumber, applicationProperties, messagesProperties,
											cardInfoData, patientInfoData));
							wizardDialog.open();
						} catch (ParserConfigurationException pce) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_PARSER_CONFIGURATION_EXCEPTION));
							pce.printStackTrace();
						} catch (ClientProtocolException cpe) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_CLIENT_PROTOCOL_EXCEPTION));
							cpe.printStackTrace();
						} catch (HttpHostConnectException hhce) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_HTTP_HOST_CONNECT_EXCEPTION));
							hhce.printStackTrace();
						} catch (IOException ioe) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_IO_EXCEPTION));
							ioe.printStackTrace();
						} catch (UnsupportedOperationException uoe) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_UNSUPPORTED_OPERATION_EXCEPTION));
							uoe.printStackTrace();
						} catch (SAXException sae) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_SAX_EXCEPTION));
							sae.printStackTrace();
						} catch (JDOMException jde) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_JDOM_EXCEPTION));
							jde.printStackTrace();
						} catch (NullPointerException npe) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_NULL_POINTER_EXCEPTION));
							npe.printStackTrace();

						} catch (InvalidCardException ice) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_INVALID_CARD));
							ice.printStackTrace();
						} catch (UnsupportedCardException usc) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_UNSUPPORTED_CARD));
							usc.printStackTrace();
						} catch (BlockedCardException bce) {
							MessageDialog.openInformation(parent.getShell(),
									getMessagesProperties().getProperty(ERR_MSG_TITLE),
									getMessagesProperties().getProperty(ERR_MSG_BLOCKED_CARD));
							bce.printStackTrace();
						}
					}
				}
			}
		});

		btnExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table.getItemCount() > 0) {
					FileDialog fileDialog = new FileDialog(rootComposite.getShell(), SWT.SAVE);
					fileDialog.setText(messagesProperties.getProperty(TXT_FILE_DIALOG));
					fileDialog.setFilterPath(System.getProperty("user.home"));
					String[] filterExt = { applicationProperties.getProperty(EXTENSION) };
					fileDialog.setFilterExtensions(filterExt);
					String selected = fileDialog.open();

					StringBuffer stringBuffer = new StringBuffer();
					stringBuffer.append(messagesProperties.getProperty(ID));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(NAME));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(PRENAME));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(BIRTHDAY));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(SEX));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(ADDRESS));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(ZIP));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(LOCATION));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(COUNTRY));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(AHV));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(MOBIL));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(PHONE));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(EMAIL));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(INSURANT_NUMBER));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(CARD_NUMBER));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(CARD_ID_NUMBER));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(messagesProperties.getProperty(DESCRIPTION));
					stringBuffer.append(applicationProperties.getProperty(DELIMITER));
					stringBuffer.append(applicationProperties.getProperty(NEWLINE));
					filteredTableContent = new StringBuilder();

					defaultAsc = true;
					defaultAsc = sortTable(table, 1, defaultAsc);

					TableItem[] items = table.getItems();
					for (int i = 1; i < items.length; i++) {

						filteredTableContent.append(items[i].getText(0));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(1));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(2));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(3));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(4));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(5));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(6));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(7));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(8));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(9));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(10));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(11));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(12));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(13));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(14));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(15));
						filteredTableContent.append(applicationProperties.getProperty(DELIMITER));
						filteredTableContent.append(items[i].getText(16).replaceAll(
								applicationProperties.getProperty(NEWLINE_IDENTIFICATION),
								applicationProperties.getProperty(NEWLINE_REPLACEMENT)));
						filteredTableContent.append(applicationProperties.getProperty(NEWLINE));
					}

					stringBuffer.append(filteredTableContent);
					stringBuffer.append(applicationProperties.getProperty(NEWLINE));

					Path p = Paths.get(selected);
					try (BufferedWriter writer = Files.newBufferedWriter(p, Charset.forName("UTF-8"))) {
						writer.write(stringBuffer.toString());
						MessageDialog.openInformation(parent.getShell(), getMessagesProperties().getProperty(MSG_TITLE),
								getMessagesProperties().getProperty(MSG_TITLE_MSG));
					} catch (IOException ex) {
						MessageDialog.openError(parent.getShell(), getMessagesProperties().getProperty(ERR_MSG_ERR),
								getMessagesProperties().getProperty(ERR_MSG_ERR));
						ex.printStackTrace();
					}
				} else {
					MessageDialog.openInformation(rootComposite.getShell(),
							messagesProperties.getProperty(ERR_MSG_TITLE),
							messagesProperties.getProperty(ERR_MSG_EMPTY_LIST));
				}
			}
		});

		// Load patient in the detail view
		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {

				Point pt = new Point(event.x, event.y);
				TableItem item = table.getItem(pt);
				int columnCount = 17;
				if (item == null)
					return;
				for (int i = 0; i < columnCount; i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						String patientID = item.getText(0);
						String patID = new Query<Patient>(Patient.class).findSingle(Patient.FLD_PATID, Query.EQUALS,
								patientID);
						Patient patient = Patient.load(patID);
						ElexisEventDispatcher.fireSelectionEvent(patient);
					}
				}
				btnShowDetail.setEnabled(true);
				btnUpdate.setEnabled(true);
			}
		});
		table.setLinesVisible(true);
		this.table = table;
	}

	private void loadTable(Table table, Composite parent) {
		table.clearAll();
		table.setLinesVisible(true);
		btnShowDetail.setEnabled(false);
		btnUpdate.setEnabled(false);
		btnExport.setEnabled(false);
		if (table.getItemCount() > 0) {
			table.removeAll();
		}
		ArrayList<String[]> rowData = new ArrayList<String[]>();

		emptyPrenameCount = 0;
		emptyBirthdayCount = 0;
		emotySexCount = 0;
		emptyAdressCount = 0;
		emptyZipCount = 0;
		emptyLocationCount = 0;
		emptyCountryCount = 0;
		emptyAHVCount = 0;
		emptyMobileCount = 0;
		emptyPhoneCount = 0;
		emptyEmailCount = 0;
		emptyInsuranceCount = 0;
		emptyCovercardCount = 0;
		emptyCardCount = 0;

		try {
			ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
			monitorDialog.open();
			monitorDialog.run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					Query<Patient> query = new Query<>(Patient.class);
					allPatients = query.execute();
					patientCount = allPatients.size();
					monitor.beginTask(
							MessageFormat.format(messagesProperties.getProperty(TXT_PROGRESS_BAR), patientCount),
							patientCount);
					for (Patient patient : allPatients) {

						emptyPrenameCount = patient.getVorname().isEmpty() ? emptyPrenameCount + 1 : emptyPrenameCount;
						emptyBirthdayCount = patient.getGeburtsdatum().isEmpty() ? emptyBirthdayCount + 1
								: emptyBirthdayCount;
						emotySexCount = patient.getGeschlecht().isEmpty() ? emotySexCount + 1 : emotySexCount;
						emptyAdressCount = patient.getAnschrift().getStrasse().isEmpty() ? emptyAdressCount + 1
								: emptyAdressCount;
						emptyZipCount = patient.getAnschrift().getPlz().isEmpty() ? emptyZipCount + 1 : emptyZipCount;
						emptyLocationCount = patient.getAnschrift().getOrt().isEmpty() ? emptyLocationCount + 1
								: emptyLocationCount;
						emptyCountryCount = patient.getAnschrift().getLand().isEmpty() ? emptyCountryCount + 1
								: emptyCountryCount;
						emptyAHVCount = patient.getXid(applicationProperties.getProperty(DOMAIN_COVERCARD_AHV))
								.isEmpty() ? emptyAHVCount + 1 : emptyAHVCount;
						emptyMobileCount = patient.getNatel().isEmpty() ? emptyMobileCount + 1 : emptyMobileCount;
						emptyPhoneCount = patient.get(Patient.FLD_PHONE1).isEmpty() ? emptyPhoneCount + 1
								: emptyPhoneCount;
						emptyEmailCount = !patient.getMailAddress().contains("@") ? emptyEmailCount + 1
								: emptyEmailCount;
						emptyInsuranceCount = patient.getXid(applicationProperties.getProperty(DOMAIN_COVERCARD_AHV))
								.isEmpty() ? emptyInsuranceCount + 1 : emptyInsuranceCount;
						emptyCovercardCount = patient.getXid(applicationProperties.getProperty(DOMAIN_CARDD_NUMBER))
								.isEmpty() ? emptyCovercardCount + 1 : emptyCovercardCount;
						emptyCardCount = patient.getXid(applicationProperties.getProperty(DOMAIN_INSURED_PERSON_NUMBER))
								.isEmpty() ? emptyCardCount + 1 : emptyCardCount;

						rowData.add(new String[] { patient.getPatCode(), patient.getName(), patient.getVorname(),
								patient.getGeburtsdatum(), patient.getGeschlecht(), patient.getAnschrift().getStrasse(),
								patient.getAnschrift().getPlz(), patient.getAnschrift().getOrt(),
								patient.getAnschrift().getLand(),
								patient.getXid(applicationProperties.getProperty(DOMAIN_COVERCARD_AHV)),
								patient.getNatel(), patient.get(Patient.FLD_PHONE1), patient.getMailAddress(),
								patient.getXid(applicationProperties.getProperty(DOMAIN_INSURED_NUMBER)),
								patient.getXid(applicationProperties.getProperty(DOMAIN_CARDD_NUMBER)),
								patient.getXid(applicationProperties.getProperty(DOMAIN_INSURED_PERSON_NUMBER)),
								patient.getBemerkung() });

						monitor.worked(1);
					}
					monitor.done();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (String[] row : rowData) {
			TableItem item = new TableItem(table, SWT.HOME);
			item.setText(row);
		}
		listGroup.setText(listGroup.getText() + " (" + patientCount + ") ");

		tableColumn2.setText(messagesProperties.getProperty(PRENAME) + " (" + emptyPrenameCount + ")");
		tableColumn3.setText(messagesProperties.getProperty(BIRTHDAY) + " (" + emptyBirthdayCount + ")");
		tableColumn4.setText(messagesProperties.getProperty(SEX) + " (" + emotySexCount + ")");
		tableColumn5.setText(messagesProperties.getProperty(ADDRESS) + " (" + emptyAdressCount + ")");
		tableColumn6.setText(messagesProperties.getProperty(ZIP) + " (" + emptyZipCount + ")");
		tableColumn7.setText(messagesProperties.getProperty(LOCATION) + " (" + emptyLocationCount + ")");
		tableColumn8.setText(messagesProperties.getProperty(COUNTRY) + " (" + emptyCountryCount + ")");
		tableColumn9.setText(messagesProperties.getProperty(AHV) + " (" + emptyAHVCount + ")");
		tableColumn10.setText(messagesProperties.getProperty(MOBIL) + " (" + emptyMobileCount + ")");
		tableColumn11.setText(messagesProperties.getProperty(PHONE) + " (" + emptyPhoneCount + ")");
		tableColumn12.setText("E-Mail (" + emptyEmailCount + ")");
		tableColumn13.setText(messagesProperties.getProperty(INSURANT_NUMBER) + " (" + emptyInsuranceCount + ")");
		tableColumn14.setText(messagesProperties.getProperty(CARD_NUMBER) + " (" + emptyCovercardCount + ")");
		tableColumn15.setText(messagesProperties.getProperty(CARD_ID_NUMBER) + " (" + emptyCardCount + ")");

		for (TableColumn tc : table.getColumns()) {
			tc.pack();
		}
		table.getShell().layout(new Control[] { table });
		defaultAsc = true;
		defaultAsc = sortTable(table, 1, defaultAsc);
		btnExport.setEnabled(true);
		MessageDialog.openInformation(Display.getDefault().getActiveShell(),
				getMessagesProperties().getProperty("Finito"), "Alle Patienten geladen");
	}

	private boolean sortTable(Table table, int columnNumber, boolean asc) {
		TableItem[] items = table.getItems();
		Collator collator = Collator.getInstance(Locale.getDefault());
		// Sets the column1 as default sorted
		TableColumn column = table.getColumn(columnNumber);
		int index = columnNumber;
		if (asc) {
			asc = false;
			for (int i = 1; i < items.length; i++) {
				String value1 = items[i].getText(index);
				for (int j = 0; j < i; j++) {
					String value2 = items[j].getText(index);
					if (collator.compare(value1, value2) < 0) {
						String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
								items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
								items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
								items[i].getText(11), items[i].getText(12), items[i].getText(13), items[i].getText(14),
								items[i].getText(15), items[i].getText(16), items[i].getText(16) };
						items[i].dispose();
						TableItem item = new TableItem(table, SWT.NONE, j);
						item.setText(values);
						item.setBackground(8, new Color(248, 248, 248));
						item.setBackground(10, new Color(248, 248, 248));
						item.setBackground(11, new Color(248, 248, 248));
						item.setBackground(12, new Color(248, 248, 248));
						item.setBackground(16, new Color(248, 248, 248));
						items = table.getItems();
						break;
					}
				}
			}
		} else {
			asc = true;
			for (int i = 1; i < items.length; i++) {
				String value1 = items[i].getText(index);
				for (int j = 0; j < i; j++) {

					String value2 = items[j].getText(index);
					if (collator.compare(value1, value2) > 0) {
						String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
								items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
								items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
								items[i].getText(11), items[i].getText(12), items[i].getText(13), items[i].getText(14),
								items[i].getText(15), items[i].getText(16), items[i].getText(16) };
						items[i].dispose();
						TableItem item = new TableItem(table, SWT.NONE, j);
						item.setText(values);
						item.setBackground(8, new Color(248, 248, 248));
						item.setBackground(10, new Color(248, 248, 248));
						item.setBackground(11, new Color(248, 248, 248));
						item.setBackground(12, new Color(248, 248, 248));
						item.setBackground(16, new Color(248, 248, 248));
						items = table.getItems();
						break;
					}
				}
			}
		}
		table.setSortColumn(column);
		table.deselectAll();
		table.setTopIndex(0);
		table.setLinesVisible(true);
		return asc;
	}

	/* Listeners */
	private final ElexisEventListener eeli_pat_sync = new ElexisUiSyncEventListenerImpl(Patient.class,
			ElexisEvent.EVENT_UPDATE) {
		@Override
		public void runInUi(ElexisEvent ev) {
			Patient pat = (Patient) ev.getObject();
			String patId = pat.getPatCode();
			TableItem[] items = table.getItems();
			TableItem item;
			for (int i = 0; i < items.length; i++) {
				if (patId.equalsIgnoreCase(items[i].getText(0))) {
					item = items[i];
					item.setText(0, pat.getPatCode());
					item.setText(1, pat.getName());
					item.setText(2, pat.getVorname());
					item.setText(3, pat.getGeburtsdatum());
					item.setText(4, pat.getGeschlecht());
					item.setText(5, pat.getAnschrift().getStrasse());
					item.setText(6, pat.getAnschrift().getPlz());
					item.setText(7, pat.getAnschrift().getOrt());
					item.setText(8, pat.getAnschrift().getLand());
					item.setText(9, pat.getXid(applicationProperties.getProperty(DOMAIN_COVERCARD_AHV)));
					item.setText(10, pat.getNatel());
					item.setText(11, pat.get(Patient.FLD_PHONE1));
					item.setText(12, pat.getMailAddress());
					item.setText(13, pat.getXid(applicationProperties.getProperty(DOMAIN_INSURED_NUMBER)));
					item.setText(14, pat.getXid(applicationProperties.getProperty(DOMAIN_CARDD_NUMBER)));
					item.setText(15, pat.getXid(applicationProperties.getProperty(DOMAIN_INSURED_PERSON_NUMBER)));
					item.setText(16, pat.getBemerkung());
					break;
				}
			}
		}
	};

	private final ElexisEventListener eeli_case_update = new ElexisUiSyncEventListenerImpl(Fall.class,
			ElexisEvent.EVENT_UPDATE) {
		@Override
		public void runInUi(ElexisEvent ev) {
			Fall fall = (Fall) ev.getObject();
			Patient patient = fall.getPatient();
			String insuranceNumber = patient.getXid("www.xid.ch/framsteg/covercard/insured-number");
			if (fall.getAbrechnungsSystem().equalsIgnoreCase("KVG")) {
				if (fall.getInfoString("Versicherungsnummer").isEmpty()) {
					fall.setInfoString("Versicherungsnummer", insuranceNumber);
				}
			}
		}
	};

	Listener idSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			idAsc = sortTable(table, 0, idAsc);
		}
	};

	Listener nameSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			nameAsc = sortTable(table, 1, nameAsc);
		}
	};

	Listener prenameSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			prenameAsc = sortTable(table, 2, prenameAsc);
		}
	};

	Listener birthdaySortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			birthdayAsc = sortTable(table, 3, birthdayAsc);
		}
	};

	Listener sexSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			sexAsc = sortTable(table, 4, sexAsc);
		}
	};

	Listener addressSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			addressAsc = sortTable(table, 5, addressAsc);
		}
	};

	Listener zipSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			zipAsc = sortTable(table, 6, zipAsc);
		}
	};

	Listener locationSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			locationAsc = sortTable(table, 7, locationAsc);
		}
	};

	Listener countrySortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			countryAsc = sortTable(table, 8, countryAsc);
		}
	};

	Listener ahvSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			ahvAsc = sortTable(table, 9, ahvAsc);
		}
	};

	Listener mobileSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			mobileAsc = sortTable(table, 10, mobileAsc);
		}
	};

	Listener phoneSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			phoneAsc = sortTable(table, 11, phoneAsc);
		}
	};

	Listener emailSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			emailAsc = sortTable(table, 12, emailAsc);
		}
	};

	Listener insurantNrSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			insurantNrAsc = sortTable(table, 13, insurantNrAsc);
		}
	};

	Listener covercardNrSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			covercardNrAsc = sortTable(table, 14, covercardNrAsc);
		}
	};

	Listener idCardNrSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			idCardNrAsc = sortTable(table, 15, idCardNrAsc);
		}
	};

	Listener descriptionSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			descriptionAsc = sortTable(table, 16, descriptionAsc);
		}
	};

	@Override
	public void setFocus() {
	}

	public Properties getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	public Properties getMessagesProperties() {
		return messagesProperties;
	}

	public void setMessagesProperties(Properties messagesProperties) {
		this.messagesProperties = messagesProperties;
	}

	@Override
	public void refresh() {
	}

	@Override
	public void dispose() {
		ElexisEventDispatcher.getInstance().removeListeners(eeli_pat_sync);
		ElexisEventDispatcher.getInstance().removeListeners(eeli_case_update);
		super.dispose();
	}
}
