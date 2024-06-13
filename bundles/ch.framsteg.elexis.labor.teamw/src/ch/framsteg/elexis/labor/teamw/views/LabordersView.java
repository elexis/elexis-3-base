package ch.framsteg.elexis.labor.teamw.views;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.ui.events.ElexisUiSyncEventListenerImpl;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.Fall;
import ch.elexis.data.Patient;
import ch.framsteg.elexis.labor.teamw.beans.LabOrder;
import ch.framsteg.elexis.labor.teamw.workers.MessageBuilder;
import ch.framsteg.elexis.labor.teamw.workers.Transmitter;

public class LabordersView extends ViewPart implements IRefreshable {

	final static private String UNSELECTED = "props.msg.txt.unselected.text";
	final static private String GROUP_PAT_SELECT = "props.msg.grp.pat.select.text";
	final static private String LBL_NEW_ORDER = "props.msg.lbl.new.order.text";
	final static private String LBL_PAT_ID_TEXT = "props.msg.lbl.pat.id.text";
	final static private String LBL_PAT_TITLE_TEXT = "props.msg.lbl.pat.title.text";
	final static private String LBL_PAT_NAME_TEXT = "props.msg.lbl.pat.name.text";
	final static private String LBL_PAT_PRENAME_TEXT = "props.msg.lbl.pat.prename.text";
	final static private String LBL_PAT_BIRTHDAY_TEXT = "props.msg.lbl.pat.birthday.text";
	final static private String LBL_PAT_SEX_TEXT = "props.msg.lbl.pat.sex.text";
	final static private String LBL_PAT_STREET_TEXT = "props.msg.lbl.pat.street.text";
	final static private String LBL_PAT_ZIP_TEXT = "props.msg.lbl.pat.zip.text";
	final static private String LBL_PAT_CITY_TEXT = "props.msg.lbl.pat.city.text";
	final static private String LBL_PAT_COIUNTRY_TEXT = "props.msg.lbl.pat.country.text";
	final static private String LBL_PAT_AHV_TEXT = "props.msg.lbl.pat.ahv.text";
	final static private String LBL_PAT_COVERCARD_TEXT = "props.msg.lbl.pat.covercard.text";
	final static private String LBL_PAT_MOBILE_TEXT = "props.msg.lbl.pat.mobile.text";
	final static private String LBL_PAT_EMAIL_TEXT = "props.msg.lbl.pat.email.text";
	final static private String LBL_CASE_TEXT = "props.msg.lbl.case.text";
	final static private String LBL_CASE_REASON_TEXT = "props.msg.lbl.case.reason.text";
	final static private String LBL_CASE_INSURANCE_TYPE_TEXT = "props.msg.lbl.case.insurance.type.text";
	final static private String LBL_CASE_INSURANCE_TEXT = "props.msg.lbl.case.insurance.text";
	final static private String LBL_CASE_INSURANCE_EAN_TEXT = "props.msg.lbl.case.insurance.ean.text";
	final static private String LBL_CASE_INSURANCE_NUMBER_TEXT = "props.msg.lbl.case.insurance.number.text";
	final static private String BTN_SEND_TEXT = "props.msg.btn.send.text";
	final static private String BTN_CLEAR_TEXT = "props.msg.btn.clear.text";
	final static private String TXT_MALE_TEXT = "props.msg.txt.male.text";
	final static private String TXT_FEMALE_TEXT = "props.msg.txt.female.text";
	final static private String TXT_MALE_ABBR = "props.msg.txt.male.abbr";
	final static private String MSG_TITLE_SUCCESS = "props.msg.title.success";
	final static private String MSG_TITLE_ERROR = "props.msg.title.error";
	final static private String MSG_SUCCESS = "props.msg.success";
	final static private String MSG_ERROR = "props.msg.error";
	final static private String MSG_EMPTY_FIELD = "props.msg.empty.field";
	final static private String MSG_MISSING_COUNTRY = "props.msg.missing.countries";

	final static private String APP_CFG_XID_EAN = "props.app.xid.costbearer.ean.domain";
	final static private String APP_CFG_XID_AHV = "props.app.xid.ahv.domain";
	final static private String APP_CFG_XID_INSURED_PERDON_NUMBER = "props.app.xid.insured.person.number";
	final static private String APP_CFG_XID_INSURED_NUMBER = "props.app.xid.insured.number";

	final static private String CFG_TEAMW_PATH = "props.app.teamw.config.path";

	final static private String SOFTWARE = "props.teamw.gdt.value.software";

	final static private String DEFAULT_GUARANTOR_TYPE = "props.teamw.gdt.default.guarantor.type";
	final static private String DEFAULT_CONTACT_TYPE = "props.teamw.gdt.default.contact.type";

	final static private String MSG_MESSAGE_BUILT = "props.app.msg.message.built";
	final static private String MSG_MESSAGE_SENT = "props.app.msg.message.sent";

	final static private String ERR_CLIENT_PROTOCOL_EXCEPTION = "props.app.err.client.protocol.exception";
	final static private String ERR_UNSUPPORTED_OPERATION_EXCEPTION = "props.app.err.unsupported.operation.exception";
	final static private String ERR_IO_EXCEPTION = "props.app.err.io.exception";
	final static private String ERR_PARSER_CONFIGURATION_EXCEPTION = "props.app.err.parser.configuration.exception";
	final static private String ERR_SAX_EXCEPTION = "props.app.err.sax.exception";
	final static private String ERR_URI_SYNTAX_EXCEPTION = "props.app.err.uri.syntax.exception";
	final static private String ERR_TRANSFORMER_EXCEPTION = "props.app.err.transformer.exception";
	final static private String ERR_INVALID_KEY_EXCEPTION = "props.app.err.invalid.key.exception";
	final static private String ERR_NO_SUCH_ALGORITHM_EXCEPTION = "props.app.err.no.such.algorithm.exception";
	final static private String ERR_INVALID_KEY_SPEC_EXCEPTION = "props.app.err.invalid.key.spec.exception";
	final static private String ERR_SIGNATURE_EXCEPTION = "props.app.err.signature.exception";

	private Properties applicationProperties;
	private Properties messagesProperties;
	private Properties teamwProperties;

	private Label lblPatPID;
	private Label lblTitle;
	private Label lblPatTitle;
	private Label lblPatName;
	private Label lblPatPrename;
	private Label lblPatBirthday;
	private Label lblPatSex;
	private Label lblPatStreet;
	private Label lblPatZip;
	private Label lblPatCity;
	private Label lblPatCountry;
	private Label lblPatAHV;
	private Label lblPatCardNumber;
	private Label lblPatMobile;
	private Label lblPatEmail;
	private Label lblCaseTitle;
	private Label lblCaseReason;
	private Label lblCaseInsuranceType;
	private Label lblCaseInsuranceNumber;
	private Label lblCaseInsurance;
	private Label lblCaseInsuranceEAN;

	private Text txtPatPID;
	private Text txtPatTitle;
	private Text txtPatName;
	private Text txtPatPrename;
	private Text txtPatBirthday;
	private Text txtPatSex;
	private Text txtPatStreet;
	private Text txtPatZip;
	private Text txtPatCity;
	private Text txtPatCountry;
	private Text txtPatAHV;
	private Text txtPatCardNumber;
	private Text txtPatMobile;
	private Text txtPatEmail;
	private Text txtCaseTitle;
	private Text txtCaseReason;
	private Text txtCaseInsuranceType;
	private Text txtCaseInsuranceNumber;
	private Text txtCaseInsurance;
	private Text txtCaseInsuranceEAN;

	private Color markedBackgroundColor;
	private Color defaultBackgroundColor;

	private Button btnClear;
	private Button btnSend;

	Logger logger = LoggerFactory.getLogger(LabordersView.class);

	public LabordersView() {
		loadProperties();
		ElexisEventDispatcher.getInstance().addListeners(eeli_case_selected);
		ElexisEventDispatcher.getInstance().addListeners(eeli_case_updated);
		Display display = Display.getCurrent();
		Color markedBackgroundColor = new Color(display, new RGB(250, 150, 150));
		Color defaultBackgroundColor = new Color(display, new RGB(255, 255, 255));
		setMarkedBackgroundColor(markedBackgroundColor);
		setDefaultBackgroundColor(defaultBackgroundColor);
	}

	private void loadProperties() {
		try {
			setApplicationProperties(new Properties());
			setMessagesProperties(new Properties());
			setTeamwProperties(new Properties());

			getApplicationProperties().load(
					LabordersView.class.getClassLoader().getResourceAsStream("/resources/application.properties"));
			getMessagesProperties()
					.load(LabordersView.class.getClassLoader().getResourceAsStream("/resources/messages.properties"));
			getTeamwProperties().load((new FileInputStream(getApplicationProperties().getProperty(CFG_TEAMW_PATH))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private final ElexisEventListener eeli_case_updated = new ElexisUiSyncEventListenerImpl(Fall.class,
			ElexisEvent.EVENT_UPDATE) {
		@Override
		public void runInUi(ElexisEvent ev) {
			updateControls(ev);
		}
	};

	private final ElexisEventListener eeli_case_selected = new ElexisUiSyncEventListenerImpl(Fall.class,
			ElexisEvent.EVENT_SELECTED) {
		@Override
		public void runInUi(ElexisEvent ev) {
			updateControls(ev);
		}
	};

	private void clearFields() {
		txtPatPID.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatPID.setBackground(getDefaultBackgroundColor());
		txtPatTitle.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatTitle.setBackground(getDefaultBackgroundColor());
		txtPatName.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatPID.setBackground(getDefaultBackgroundColor());
		txtPatPrename.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatPrename.setBackground(getDefaultBackgroundColor());
		txtPatBirthday.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatBirthday.setBackground(getDefaultBackgroundColor());
		txtPatSex.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatSex.setBackground(getDefaultBackgroundColor());
		txtPatStreet.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatStreet.setBackground(getDefaultBackgroundColor());
		txtPatZip.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatZip.setBackground(getDefaultBackgroundColor());
		txtPatCity.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatCity.setBackground(getDefaultBackgroundColor());
		txtPatCountry.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatCountry.setBackground(getDefaultBackgroundColor());
		txtPatAHV.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatAHV.setBackground(getDefaultBackgroundColor());
		txtPatCardNumber.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatCardNumber.setBackground(getDefaultBackgroundColor());
		txtPatMobile.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatMobile.setBackground(getDefaultBackgroundColor());
		txtPatEmail.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatEmail.setBackground(getDefaultBackgroundColor());
		txtCaseInsuranceNumber.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtCaseInsuranceNumber.setBackground(getDefaultBackgroundColor());
		txtCaseInsurance.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtCaseInsurance.setBackground(getDefaultBackgroundColor());
		txtCaseInsuranceEAN.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtCaseInsuranceEAN.setBackground(getDefaultBackgroundColor());
		txtCaseTitle.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtCaseTitle.setBackground(getDefaultBackgroundColor());
		txtCaseReason.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtCaseReason.setBackground(getDefaultBackgroundColor());
		txtCaseInsuranceType.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtCaseInsuranceType.setBackground(getDefaultBackgroundColor());
		txtPatCardNumber.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatCardNumber.setBackground(getDefaultBackgroundColor());
		btnSend.setEnabled(false);
	}

	private void updateControls(ElexisEvent ev) {

		Fall fall = (Fall) ev.getObject();
		Patient patient = fall.getPatient();
		txtPatPID.setText(patient.getPatCode());
		if (txtPatPID.getText().isEmpty()) {
			txtPatPID.setBackground(getMarkedBackgroundColor());
		} else {
			txtPatPID.setBackground(getDefaultBackgroundColor());
		}

		txtPatTitle.setText(patient.getGeschlecht().equalsIgnoreCase(getMessagesProperties().getProperty(TXT_MALE_ABBR))
				? getMessagesProperties().getProperty(TXT_MALE_TEXT)
				: getMessagesProperties().getProperty(TXT_FEMALE_TEXT));
		if (txtPatTitle.getText().isEmpty()) {
			txtPatTitle.setBackground(getMarkedBackgroundColor());
			txtPatTitle.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtPatTitle.setBackground(getDefaultBackgroundColor());
		}
		txtPatName.setText(patient.getName());
		if (txtPatName.getText().isEmpty()) {
			txtPatName.setBackground(getMarkedBackgroundColor());
			txtPatName.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtPatName.setBackground(getDefaultBackgroundColor());
		}
		txtPatPrename.setText(patient.getVorname());
		if (txtPatPrename.getText().isEmpty()) {
			txtPatPrename.setBackground(getMarkedBackgroundColor());
			txtPatPrename.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtPatPrename.setBackground(getDefaultBackgroundColor());
		}
		txtPatBirthday.setText(patient.getGeburtsdatum());
		if (txtPatBirthday.getText().isEmpty()) {
			txtPatBirthday.setBackground(getMarkedBackgroundColor());
			txtPatBirthday.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtPatBirthday.setBackground(getDefaultBackgroundColor());
		}
		txtPatSex.setText(patient.getGeschlecht());
		if (txtPatSex.getText().isEmpty()) {
			txtPatSex.setBackground(getMarkedBackgroundColor());
			txtPatSex.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtPatSex.setBackground(getDefaultBackgroundColor());
		}
		txtPatStreet.setText(patient.getAnschrift().getStrasse());
		if (txtPatStreet.getText().isEmpty()) {
			txtPatStreet.setBackground(getMarkedBackgroundColor());
			txtPatStreet.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtPatStreet.setBackground(getDefaultBackgroundColor());
		}
		txtPatZip.setText(patient.getAnschrift().getPlz());
		if (txtPatZip.getText().isEmpty()) {
			txtPatZip.setBackground(getMarkedBackgroundColor());
			txtPatZip.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtPatZip.setBackground(getDefaultBackgroundColor());
		}
		txtPatCity.setText(patient.getAnschrift().getOrt());
		if (txtPatCity.getText().isEmpty()) {
			txtPatCity.setBackground(getMarkedBackgroundColor());
			txtPatCity.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtPatCity.setBackground(getDefaultBackgroundColor());
		}
		txtPatCountry.setText(patient.getAnschrift().getLand());
		System.out.println(patient.getAnschrift().getLand());
		if (txtPatCountry.getText().isEmpty() || txtPatCountry.getText().isBlank()) {
			txtPatCountry.setBackground(getMarkedBackgroundColor());
			txtPatCountry.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtPatCountry.setBackground(getDefaultBackgroundColor());
		}
		txtPatAHV.setText(patient.getXid(getApplicationProperties().getProperty(APP_CFG_XID_AHV)));
		if (txtPatAHV.getText().isEmpty()) {
			txtPatAHV.setBackground(getMarkedBackgroundColor());
			txtPatAHV.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtPatAHV.setBackground(getDefaultBackgroundColor());
		}
		txtPatCardNumber
				.setText(patient.getXid(getApplicationProperties().getProperty(APP_CFG_XID_INSURED_PERDON_NUMBER)));
		if (txtPatCardNumber.getText().isEmpty()) {
			txtPatCardNumber.setBackground(getMarkedBackgroundColor());
			txtPatCardNumber.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtPatCardNumber.setBackground(getDefaultBackgroundColor());
		}
		txtPatMobile.setText(patient.getNatel());
		if (txtPatMobile.getText().isEmpty()) {
			txtPatMobile.setBackground(getMarkedBackgroundColor());
			txtPatMobile.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtPatMobile.setBackground(getDefaultBackgroundColor());
		}
		txtPatEmail.setText(patient.getMailAddress());
		if (txtPatEmail.getText().isEmpty()) {
			txtPatEmail.setBackground(getMarkedBackgroundColor());
			txtPatEmail.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtPatEmail.setBackground(getDefaultBackgroundColor());
		}
		txtCaseInsuranceNumber
				.setText(patient.getXid(getApplicationProperties().getProperty(APP_CFG_XID_INSURED_NUMBER)));
		if (txtCaseInsuranceNumber.getText().isEmpty()) {
			txtCaseInsuranceNumber.setBackground(getMarkedBackgroundColor());
			txtCaseInsuranceNumber.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtCaseInsuranceNumber.setBackground(getDefaultBackgroundColor());
		}
		if (fall.getCostBearer() != null) {
			txtCaseInsurance.setText(fall.getCostBearer().getLabel(true));
		}
		if (txtCaseInsurance.getText().isEmpty()) {
			txtCaseInsurance.setBackground(getMarkedBackgroundColor());
			txtCaseInsurance.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtCaseInsurance.setBackground(getDefaultBackgroundColor());
		}
		if (fall.getCostBearer() != null) {
			txtCaseInsuranceEAN
					.setText(fall.getCostBearer().getXid(getApplicationProperties().getProperty(APP_CFG_XID_EAN)));
		}
		if (txtCaseInsuranceEAN.getText().isEmpty()) {
			txtCaseInsuranceEAN.setBackground(getMarkedBackgroundColor());
			txtCaseInsuranceEAN.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtCaseInsuranceEAN.setBackground(getDefaultBackgroundColor());
		}
		txtCaseTitle.setText(fall.getLabel());
		if (txtCaseTitle.getText().isEmpty()) {
			txtCaseTitle.setBackground(getMarkedBackgroundColor());
			txtCaseTitle.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtCaseTitle.setBackground(getDefaultBackgroundColor());
		}
		txtCaseReason.setText(fall.getGrund());
		if (txtCaseReason.getText().isEmpty()) {
			txtCaseReason.setBackground(getMarkedBackgroundColor());
			txtCaseReason.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtCaseReason.setBackground(getDefaultBackgroundColor());
		}
		txtCaseInsuranceType.setText(fall.getAbrechnungsSystem());
		if (txtCaseInsuranceType.getText().isEmpty()) {
			txtCaseInsuranceType.setBackground(getMarkedBackgroundColor());
			txtCaseInsuranceType.setText(getMessagesProperties().getProperty(MSG_EMPTY_FIELD));
		} else {
			txtCaseInsuranceType.setBackground(getDefaultBackgroundColor());
		}
		btnSend.setEnabled(true);
		btnClear.setEnabled(true);
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout rootLayout = new GridLayout();
		rootLayout.numColumns = 1;

		GridLayout groupPatientLayout = new GridLayout();
		groupPatientLayout.numColumns = 2;

		GridData rootGridData = new GridData();
		rootGridData.grabExcessHorizontalSpace = true;
		rootGridData.grabExcessVerticalSpace = true;
		rootGridData.horizontalAlignment = SWT.FILL;
		rootGridData.verticalAlignment = SWT.FILL;

		GridData groupPatientGridData = new GridData();
		groupPatientGridData.grabExcessHorizontalSpace = true;
		groupPatientGridData.horizontalAlignment = SWT.FILL;
		groupPatientGridData.verticalAlignment = SWT.FILL;

		Composite rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(rootLayout);
		rootComposite.setLayoutData(rootGridData);

		lblTitle = new Label(rootComposite, SWT.None);
		lblTitle.setText(getMessagesProperties().getProperty(LBL_NEW_ORDER));

		Group patientGroup = new Group(rootComposite, SWT.FILL);
		patientGroup.setText(getMessagesProperties().getProperty(GROUP_PAT_SELECT));
		patientGroup.setLayout(rootLayout);
		patientGroup.setLayoutData(rootGridData);

		Composite groupPatientComposite = new Composite(patientGroup, SWT.NONE);
		groupPatientComposite.setLayout(groupPatientLayout);
		groupPatientComposite.setLayoutData(groupPatientGridData);

		lblPatPID = new Label(groupPatientComposite, SWT.NONE);
		lblPatPID.setText(getMessagesProperties().getProperty(LBL_PAT_ID_TEXT));

		txtPatPID = new Text(groupPatientComposite, SWT.BORDER);
		txtPatPID.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatPID.setEditable(false);
		txtPatPID.setLayoutData(rootGridData);

		lblPatTitle = new Label(groupPatientComposite, SWT.NONE);
		lblPatTitle.setText(getMessagesProperties().getProperty(LBL_PAT_TITLE_TEXT));

		txtPatTitle = new Text(groupPatientComposite, SWT.BORDER);
		txtPatTitle.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatTitle.setEditable(false);
		txtPatTitle.setLayoutData(rootGridData);

		lblPatName = new Label(groupPatientComposite, SWT.NONE);
		lblPatName.setText(getMessagesProperties().getProperty(LBL_PAT_NAME_TEXT));

		txtPatName = new Text(groupPatientComposite, SWT.BORDER);
		txtPatName.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatName.setEditable(false);
		txtPatName.setLayoutData(rootGridData);

		lblPatPrename = new Label(groupPatientComposite, SWT.NONE);
		lblPatPrename.setText(getMessagesProperties().getProperty(LBL_PAT_PRENAME_TEXT));

		txtPatPrename = new Text(groupPatientComposite, SWT.BORDER);
		txtPatPrename.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatPrename.setEditable(false);
		txtPatPrename.setLayoutData(rootGridData);

		lblPatBirthday = new Label(groupPatientComposite, SWT.NONE);
		lblPatBirthday.setText(getMessagesProperties().getProperty(LBL_PAT_BIRTHDAY_TEXT));

		txtPatBirthday = new Text(groupPatientComposite, SWT.BORDER);
		txtPatBirthday.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatBirthday.setEditable(false);
		txtPatBirthday.setLayoutData(rootGridData);

		lblPatSex = new Label(groupPatientComposite, SWT.NONE);
		lblPatSex.setText(getMessagesProperties().getProperty(LBL_PAT_SEX_TEXT));

		txtPatSex = new Text(groupPatientComposite, SWT.BORDER);
		txtPatSex.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatSex.setEditable(false);
		txtPatSex.setLayoutData(rootGridData);

		lblPatStreet = new Label(groupPatientComposite, SWT.NONE);
		lblPatStreet.setText(getMessagesProperties().getProperty(LBL_PAT_STREET_TEXT));

		txtPatStreet = new Text(groupPatientComposite, SWT.BORDER);
		txtPatStreet.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatStreet.setEditable(false);
		txtPatStreet.setLayoutData(rootGridData);

		lblPatZip = new Label(groupPatientComposite, SWT.NONE);
		lblPatZip.setText(getMessagesProperties().getProperty(LBL_PAT_ZIP_TEXT));

		txtPatZip = new Text(groupPatientComposite, SWT.BORDER);
		txtPatZip.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatZip.setEditable(false);
		txtPatZip.setLayoutData(rootGridData);

		lblPatCity = new Label(groupPatientComposite, SWT.NONE);
		lblPatCity.setText(getMessagesProperties().getProperty(LBL_PAT_CITY_TEXT));

		txtPatCity = new Text(groupPatientComposite, SWT.BORDER);
		txtPatCity.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatCity.setEditable(false);
		txtPatCity.setLayoutData(rootGridData);

		lblPatCountry = new Label(groupPatientComposite, SWT.NONE);
		lblPatCountry.setText(getMessagesProperties().getProperty(LBL_PAT_COIUNTRY_TEXT));

		txtPatCountry = new Text(groupPatientComposite, SWT.BORDER);
		txtPatCountry.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatCountry.setEditable(false);
		txtPatCountry.setLayoutData(rootGridData);

		lblPatAHV = new Label(groupPatientComposite, SWT.NONE);
		lblPatAHV.setText(getMessagesProperties().getProperty(LBL_PAT_AHV_TEXT));

		txtPatAHV = new Text(groupPatientComposite, SWT.BORDER);
		txtPatAHV.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatAHV.setEditable(false);
		txtPatAHV.setLayoutData(rootGridData);

		lblPatCardNumber = new Label(groupPatientComposite, SWT.NONE);
		lblPatCardNumber.setText(getMessagesProperties().getProperty(LBL_PAT_COVERCARD_TEXT));

		txtPatCardNumber = new Text(groupPatientComposite, SWT.BORDER);
		txtPatCardNumber.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatCardNumber.setEditable(false);
		txtPatCardNumber.setLayoutData(rootGridData);

		lblPatMobile = new Label(groupPatientComposite, SWT.NONE);
		lblPatMobile.setText(getMessagesProperties().getProperty(LBL_PAT_MOBILE_TEXT));

		txtPatMobile = new Text(groupPatientComposite, SWT.BORDER);
		txtPatMobile.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatMobile.setEditable(false);
		txtPatMobile.setLayoutData(rootGridData);

		lblPatEmail = new Label(groupPatientComposite, SWT.NONE);
		lblPatEmail.setText(getMessagesProperties().getProperty(LBL_PAT_EMAIL_TEXT));

		txtPatEmail = new Text(groupPatientComposite, SWT.BORDER);
		txtPatEmail.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtPatEmail.setEditable(false);
		txtPatEmail.setLayoutData(rootGridData);

		lblCaseTitle = new Label(groupPatientComposite, SWT.NONE);
		lblCaseTitle.setText(getMessagesProperties().getProperty(LBL_CASE_TEXT));

		txtCaseTitle = new Text(groupPatientComposite, SWT.BORDER);
		txtCaseTitle.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtCaseTitle.setEditable(false);
		txtCaseTitle.setLayoutData(rootGridData);

		lblCaseReason = new Label(groupPatientComposite, SWT.NONE);
		lblCaseReason.setText(getMessagesProperties().getProperty(LBL_CASE_REASON_TEXT));

		txtCaseReason = new Text(groupPatientComposite, SWT.BORDER);
		txtCaseReason.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtCaseReason.setEditable(false);
		txtCaseReason.setLayoutData(rootGridData);

		lblCaseInsuranceType = new Label(groupPatientComposite, SWT.NONE);
		lblCaseInsuranceType.setText(getMessagesProperties().getProperty(LBL_CASE_INSURANCE_TYPE_TEXT));

		txtCaseInsuranceType = new Text(groupPatientComposite, SWT.BORDER);
		txtCaseInsuranceType.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtCaseInsuranceType.setEditable(false);
		txtCaseInsuranceType.setLayoutData(rootGridData);

		lblCaseInsurance = new Label(groupPatientComposite, SWT.NONE);
		lblCaseInsurance.setText(getMessagesProperties().getProperty(LBL_CASE_INSURANCE_TEXT));

		txtCaseInsurance = new Text(groupPatientComposite, SWT.BORDER);
		txtCaseInsurance.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtCaseInsurance.setEditable(false);
		txtCaseInsurance.setLayoutData(rootGridData);

		lblCaseInsuranceEAN = new Label(groupPatientComposite, SWT.NONE);
		lblCaseInsuranceEAN.setText(getMessagesProperties().getProperty(LBL_CASE_INSURANCE_EAN_TEXT));

		txtCaseInsuranceEAN = new Text(groupPatientComposite, SWT.BORDER);
		txtCaseInsuranceEAN.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtCaseInsuranceEAN.setEditable(false);
		txtCaseInsuranceEAN.setLayoutData(rootGridData);

		lblCaseInsuranceNumber = new Label(groupPatientComposite, SWT.NONE);
		lblCaseInsuranceNumber.setText(getMessagesProperties().getProperty(LBL_CASE_INSURANCE_NUMBER_TEXT));

		txtCaseInsuranceNumber = new Text(groupPatientComposite, SWT.BORDER);
		txtCaseInsuranceNumber.setText(getMessagesProperties().getProperty(UNSELECTED));
		txtCaseInsuranceNumber.setEditable(false);
		txtCaseInsuranceNumber.setLayoutData(rootGridData);

		GridLayout buttonsLayout = new GridLayout();
		buttonsLayout.numColumns = 2;

		GridData buttonsGridData = new GridData();
		buttonsGridData.horizontalAlignment = SWT.FILL;
		buttonsGridData.heightHint = 50;

		Composite buttonComposite = new Composite(patientGroup, SWT.NONE);
		buttonComposite.setLayout(buttonsLayout);
		buttonComposite.setLayoutData(buttonsGridData);

		GridData buttonGridData = new GridData();
		buttonGridData.grabExcessHorizontalSpace = true;
		buttonGridData.horizontalAlignment = SWT.FILL;
		buttonGridData.grabExcessVerticalSpace = true;
		buttonGridData.heightHint = 50;

		btnSend = new Button(buttonComposite, SWT.PUSH);
		btnSend.setText(getMessagesProperties().getProperty(BTN_SEND_TEXT));
		btnSend.setLayoutData(buttonGridData);
		btnSend.setEnabled(false);
		btnSend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!txtPatCountry.getText().equalsIgnoreCase(getMessagesProperties().getProperty(MSG_EMPTY_FIELD))) {
					try {
						LabOrder labOrder = createLabOrder();
						MessageBuilder messageBuilder = new MessageBuilder(getApplicationProperties(),
								getTeamwProperties());
						logger.info(getApplicationProperties().getProperty(MSG_MESSAGE_BUILT));
						String message = messageBuilder.build(labOrder);
						Transmitter transmitter = new Transmitter(getApplicationProperties(), getTeamwProperties(),
								getMessagesProperties(), txtPatName.getText(), txtPatPrename.getText());
						logger.info(getApplicationProperties().getProperty(MSG_MESSAGE_SENT));
						if (transmitter.transmit(message) == 200) {
							MessageDialog.openInformation(Display.getDefault().getActiveShell(),
									getMessagesProperties().getProperty(MSG_TITLE_SUCCESS),
									getMessagesProperties().getProperty(MSG_SUCCESS));
							clearFields();
							btnSend.setEnabled(false);
							btnClear.setEnabled(false);
						} else {
							MessageDialog.openError(Display.getDefault().getActiveShell(),
									getMessagesProperties().getProperty(MSG_TITLE_ERROR),
									getMessagesProperties().getProperty(MSG_ERROR));
						}
					} catch (ClientProtocolException e1) {
						logger.error(getApplicationProperties().getProperty(ERR_CLIENT_PROTOCOL_EXCEPTION));
						e1.printStackTrace();
					} catch (UnsupportedOperationException e1) {
						logger.error(getApplicationProperties().getProperty(ERR_UNSUPPORTED_OPERATION_EXCEPTION));
						e1.printStackTrace();
					} catch (IOException e1) {
						logger.error(getApplicationProperties().getProperty(ERR_IO_EXCEPTION));
						e1.printStackTrace();
					} catch (ParserConfigurationException e1) {
						logger.error(getApplicationProperties().getProperty(ERR_PARSER_CONFIGURATION_EXCEPTION));
						e1.printStackTrace();
					} catch (SAXException e1) {
						logger.error(getApplicationProperties().getProperty(ERR_SAX_EXCEPTION));
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						logger.error(getApplicationProperties().getProperty(ERR_URI_SYNTAX_EXCEPTION));
						e1.printStackTrace();
					} catch (TransformerException e1) {
						logger.error(getApplicationProperties().getProperty(ERR_TRANSFORMER_EXCEPTION));
						e1.printStackTrace();
					} catch (InvalidKeyException e1) {
						logger.error(getApplicationProperties().getProperty(ERR_INVALID_KEY_EXCEPTION));
						e1.printStackTrace();
					} catch (NoSuchAlgorithmException e1) {
						logger.error(getApplicationProperties().getProperty(ERR_NO_SUCH_ALGORITHM_EXCEPTION));
						e1.printStackTrace();
					} catch (InvalidKeySpecException e1) {
						logger.error(getApplicationProperties().getProperty(ERR_INVALID_KEY_SPEC_EXCEPTION));
						e1.printStackTrace();
					} catch (SignatureException e1) {
						logger.error(getApplicationProperties().getProperty(ERR_SIGNATURE_EXCEPTION));
						e1.printStackTrace();
					}
				} else {
					MessageDialog.openError(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_TITLE_ERROR),
							getMessagesProperties().getProperty(MSG_MISSING_COUNTRY));
				}
			}
		});

		btnClear = new Button(buttonComposite, SWT.PUSH);
		btnClear.setText(getMessagesProperties().getProperty(BTN_CLEAR_TEXT));
		btnClear.setLayoutData(buttonGridData);

		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearFields();
			}
		});
	}

	private LabOrder createLabOrder() {
		LabOrder labOrder = new LabOrder(getTeamwProperties());
		labOrder.setSentenceId(Long.toString(Calendar.getInstance().getTimeInMillis()));
		labOrder.setSoftware(getTeamwProperties().getProperty(SOFTWARE));
		labOrder.setPatientNumberLabel(txtPatPID.getText());
		labOrder.setPatientName(txtPatName.getText());
		labOrder.setPatientPrename(txtPatPrename.getText());
		labOrder.setPatientBirthday(txtPatBirthday.getText());
		labOrder.setPatientTitle(txtPatTitle.getText());
		labOrder.setPatientAhv(txtPatAHV.getText());
		labOrder.setPatientResidence(txtPatZip.getText() + txtPatCity.getText());
		labOrder.setPatientStreet(txtPatStreet.getText());

		labOrder.setPatientSex(txtPatSex.getText());
		labOrder.setPatientZip(txtPatZip.getText());
		labOrder.setPatientCity(txtPatCity.getText());

		labOrder.setPatientCountry(txtPatCountry.getText());
		labOrder.setPatientCardNumber(txtPatCardNumber.getText());
		labOrder.setGuarantorType(getTeamwProperties().getProperty(DEFAULT_GUARANTOR_TYPE));
		labOrder.setContactType(getTeamwProperties().getProperty(DEFAULT_CONTACT_TYPE));
		labOrder.setInsuranceName(txtCaseInsurance.getText());
		labOrder.setInsuranceEan(txtCaseInsuranceEAN.getText());

		labOrder.setInsuranceType(txtCaseInsuranceType.getText());
		labOrder.setPatientInsuranceNumber(txtCaseInsuranceNumber.getText());
		labOrder.setPatientPrivateMobile(txtPatMobile.getText());
		labOrder.setPatientEmail(txtPatEmail.getText());
		return labOrder;
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {
		ElexisEventDispatcher.getInstance().removeListeners(eeli_case_selected);
		ElexisEventDispatcher.getInstance().removeListeners(eeli_case_updated);
		super.dispose();
	}

	@Override
	public void refresh() {
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

	public Properties getTeamwProperties() {
		return teamwProperties;
	}

	public void setTeamwProperties(Properties teamwProperties) {
		this.teamwProperties = teamwProperties;
	}

	public Color getMarkedBackgroundColor() {
		return markedBackgroundColor;
	}

	public void setMarkedBackgroundColor(Color markedBackgroundColor) {
		this.markedBackgroundColor = markedBackgroundColor;
	}

	public Color getDefaultBackgroundColor() {
		return defaultBackgroundColor;
	}

	public void setDefaultBackgroundColor(Color defaultBackgroundColor) {
		this.defaultBackgroundColor = defaultBackgroundColor;
	}
}
