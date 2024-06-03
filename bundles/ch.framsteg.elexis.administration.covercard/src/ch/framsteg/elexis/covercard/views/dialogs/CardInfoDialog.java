package ch.framsteg.elexis.covercard.views.dialogs;

import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.framsteg.elexis.covercard.dao.CardInfoData;

public class CardInfoDialog extends Dialog {

	private CardInfoData cardInfoData;
	private Composite container;

	private Properties messagesProperties;

	private final static String QUERY_GROUP = "label.CardInfoDialog.query.group";
	private final static String DATE_OF_VALIDATION = "label.CardInfoDialog.date.of.validation";
	private final static String CARD_VALID_UNTIL = "label.CardInfoDialog.card.vallid.until";
	private final static String INSURANT_GROUP = "label.CardInfoDialog.insurant.group";
	private final static String NAME = "label.CardInfoDialog.name";
	private final static String PRENAME = "label.CardInfoDialog.prename";
	private final static String BIRTHDAY = "label.CardInfoDialog.birthday";
	private final static String SEX = "label.CardInfoDialog.sex";
	private final static String AHV = "label.CardInfoDialog.ahv";
	private final static String ADDRESS = "label.CardInfoDialog.address";
	private final static String ZIP = "label.CardInfoDialog.zip";
	private final static String PLACE = "label.CardInfoDialog.place";
	private final static String INSURER_OKP_GROUP = "label.CardInfoDialog.insurer.okp.group";
	private final static String INSURER_OKP_NAME = "label.CardInfoDialog.insurer.okp.name";
	private final static String INSURER_OKP_BSV_NR = "label.CardInfoDialog.insurer.okp.bsv.nr";
	private final static String INSURER_OKP_GLN_NR = "label.CardInfoDialog.insurer.okp.gln.nr";
	private final static String INSURER_OKP_VSN_NR = "label.CardInfoDialog.insurer.okp.vsn.nr";
	private final static String INSURER_OKP_CC_NR = "label.CardInfoDialog.insurer.okp.cc.nr";
	private final static String INSURER_OKP_IDC_NR = "label.CardInfoDialog.insurer.okp.idc.nr";
	private final static String INSURER_OKP_PHONE_NR = "label.CardInfoDialog.insurer.okp.phone.nr";
	private final static String INSURER_OKP_MAIL = "label.CardInfoDialog.insurer.okp.mail";
	private final static String COVERAGE_GROUP = "label.CardInfoDialog.coverage.group";
	private final static String COVERAGE_OKPVD_MODEL = "label.CardInfoDialog.coverage.okpvd.model";
	private final static String COVERAGE_OKPVD_01 = "label.CardInfoDialog.coverage.okpvd.model.01";
	private final static String COVERAGE_OKPVD_02 = "label.CardInfoDialog.coverage.okpvd.model.02";
	private final static String COVERAGE_OKPVD_03 = "label.CardInfoDialog.coverage.okpvd.model.03";
	private final static String COVERAGE_OKPVD_04 = "label.CardInfoDialog.coverage.okpvd.model.04";
	private final static String COVERAGE_OKPVD_05 = "label.CardInfoDialog.coverage.okpvd.model.05";
	private final static String COVERAGE_OKPVD_99 = "label.CardInfoDialog.coverage.okpvd.model.99";
	private final static String COVERAGE_OKPVD_MODEL_NAME = "label.CardInfoDialog.coverage.okpvd.model.name";
	private final static String COVERAGE_OKPVD_CANTON = "label.CardInfoDialog.coverage.okpvd.canton";
	private final static String INSURER_VVG_GROUP = "label.CardInfoDialog.insurer.vvg.group";
	private final static String INSURER_VVG_NAME = "label.CardInfoDialog.insurer.vvg.name";
	private final static String INSURER_VVG_BSV_NR = "label.CardInfoDialog.insurer.vvg.bsv.nr";
	private final static String INSURER_VVG_GLN_NR = "label.CardInfoDialog.insurer.vvg.gln.nr";
	private final static String INSURER_VVG_VSN_NR = "label.CardInfoDialog.insurer.vvg.vsn.nr";
	private final static String INSURER_VVG_CC_NR = "label.CardInfoDialog.insurer.vvg.cc.nr";
	private final static String INSURER_VVG_IDC_NR = "label.CardInfoDialog.insurer.vvg.idc.nr";
	private final static String INSURER_VVG_PHONE_NR = "label.CardInfoDialog.insurer.vvg.phone.nr";
	private final static String INSURER_VVG_MAIL = "label.CardInfoDialog.insurer.vvg.mail";
	private final static String VVG_COVERAGE_GROUP = "label.CardInfoDialog.insurer.vvg.coverage.group";
	private final static String LVL_MEDICATION_HL = "label.CardInfoDialog.insurer.lvl.medication.hl";
	private final static String LVL_MEDICATION_ACCIDENT_COVERAGE_HL = "label.CardInfoDialog.insurer.lvl.medication.accident.coverage.hl";
	private final static String LVL_MEDICATION_KM = "label.CardInfoDialog.insurer.lvl.medication.km";
	private final static String LVL_MEDICATION_ACCIDENT_COVERAGE_KM = "label.CardInfoDialog.insurer.lvl.medication.accident.coverage.km";
	private final static String YES = "label.CardInfoDialog.yes";
	private final static String NO = "label.CardInfoDialog.no";
	private final static String CLOSE = "label.CardInfoDialog.close";
	private final static String MALE = "label.cardInfoDialog.male";
	private final static String FEMALE = "label.cardInfoDialog.female";
	private final static String TITLE = "label.CardInfoDialog.title";

	public CardInfoDialog(Shell parentShell, CardInfoData cardInfoData, Properties messagesProperties) {
		super(parentShell);
		// setText("Von Covercard® abgefragte Patientendaten");
		this.cardInfoData = cardInfoData;
		this.messagesProperties = messagesProperties;
	}

	@Override
	public Control createDialogArea(Composite parent) {
		parent.getShell().setText(messagesProperties.getProperty(TITLE));
		
		container = new Composite(parent, SWT.NONE);

		GridLayout containerLayout = new GridLayout();
		containerLayout.numColumns=2;
		containerLayout.makeColumnsEqualWidth=true;
							
		container.setLayout(containerLayout);
		GridData containerData = new GridData(GridData.VERTICAL_ALIGN_END);
		containerData.grabExcessVerticalSpace=true;
		containerData.verticalAlignment=SWT.TOP;
		container.setLayoutData(containerData);
				
		GridData gridData1 = new GridData(GridData.FILL_BOTH);
		GridData gridData2 = new GridData(GridData.FILL_BOTH);
		GridData gridData4 = new GridData(GridData.FILL_BOTH);
		GridData gridData5 = new GridData(GridData.FILL_BOTH);
		GridData gridData6 = new GridData(GridData.FILL_BOTH);
		GridData gridData7 = new GridData(GridData.FILL_BOTH);

		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 2;
		
		Group queryGroup = new Group(container, SWT.BORDER);
		queryGroup.setLayout(groupLayout);
		queryGroup.setLayoutData(gridData1);
		queryGroup.setText(messagesProperties.getProperty(QUERY_GROUP));
		queryGroup.setFont(JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT));
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
	
		Label lbl_OA_dateOfValidation = new Label(queryGroup, SWT.NONE);
		lbl_OA_dateOfValidation.setText(messagesProperties.getProperty(DATE_OF_VALIDATION));

		Text txt_dateOfValidation = new Text(queryGroup, SWT.BORDER);
		txt_dateOfValidation.setText(getCardInfoData().getQualifyingData().substring(8, 10) + "."
				+ getCardInfoData().getQualifyingData().substring(5, 7) + "."
				+ getCardInfoData().getQualifyingData().substring(0, 4));
		txt_dateOfValidation.setEditable(false);
		txt_dateOfValidation.setLayoutData(gridData);

		Label lbl_OA_cardValidUntil = new Label(queryGroup, SWT.NONE);
		lbl_OA_cardValidUntil.setText(messagesProperties.getProperty(CARD_VALID_UNTIL));

		Text txt_dateValidUntiln = new Text(queryGroup, SWT.BORDER);
		txt_dateValidUntiln.setText(getCardInfoData().getAdministrativeData().getExpiryDate().substring(8, 10) + "."
				+ getCardInfoData().getAdministrativeData().getExpiryDate().substring(5, 7) + "."
				+ getCardInfoData().getAdministrativeData().getExpiryDate().substring(0, 4));
		txt_dateValidUntiln.setEditable(false);
		txt_dateValidUntiln.setLayoutData(gridData);
		
		Group insurantGroup = new Group(container, SWT.BORDER);
		insurantGroup.setText(messagesProperties.getProperty(INSURANT_GROUP));
		insurantGroup.setLayout(groupLayout);
		insurantGroup.setLayoutData(gridData2);
		insurantGroup.setFont(JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT));

		Label lbl_IDV_name = new Label(insurantGroup, SWT.NONE);
		lbl_IDV_name.setText(messagesProperties.getProperty(NAME));

		Text txt_name = new Text(insurantGroup, SWT.BORDER);
		txt_name.setText(getCardInfoData().getName().getOfficialName());
		txt_name.setEditable(false);
		txt_name.setLayoutData(gridData);

		Label lbl_IDV_prename = new Label(insurantGroup, SWT.NONE);
		lbl_IDV_prename.setText(messagesProperties.getProperty(PRENAME));

		Text txt_prename = new Text(insurantGroup, SWT.BORDER);
		txt_prename.setText(getCardInfoData().getName().getFirstName());
		txt_prename.setEditable(false);
		txt_prename.setLayoutData(gridData);

		Label lbl_IDV_birthdate = new Label(insurantGroup, SWT.NONE);
		lbl_IDV_birthdate.setText(messagesProperties.getProperty(BIRTHDAY));

		Text txt_birthdate = new Text(insurantGroup, SWT.BORDER);
		txt_birthdate.setText(getCardInfoData().getDateOfBirth().getYearMonthDay().substring(8, 10) + "."
				+ getCardInfoData().getDateOfBirth().getYearMonthDay().substring(5, 7) + "."
				+ getCardInfoData().getDateOfBirth().getYearMonthDay().substring(0, 4));
		txt_birthdate.setEditable(false);
		txt_birthdate.setLayoutData(gridData);

		Label lbl_IDV_sex = new Label(insurantGroup, SWT.NONE);
		lbl_IDV_sex.setText(messagesProperties.getProperty(SEX));

		Text txt_sex = new Text(insurantGroup, SWT.BORDER);
		txt_sex.setText(getCardInfoData().getIdentificationData().getSex().equalsIgnoreCase("2")
				? messagesProperties.getProperty(MALE)
				: messagesProperties.getProperty(FEMALE));
		txt_sex.setEditable(false);
		txt_sex.setLayoutData(gridData);

		Label lbl_IDV_AHVNr = new Label(insurantGroup, SWT.NONE);
		lbl_IDV_AHVNr.setText(messagesProperties.getProperty(AHV));

		Text txt_IDV_AHVNr = new Text(insurantGroup, SWT.BORDER);
		txt_IDV_AHVNr.setText(getCardInfoData().getIdentificationData().getCardholderIdentifier().substring(0, 3) + "."
				+ getCardInfoData().getIdentificationData().getCardholderIdentifier().substring(3, 7) + "."
				+ getCardInfoData().getIdentificationData().getCardholderIdentifier().substring(7, 11) + "."
				+ getCardInfoData().getIdentificationData().getCardholderIdentifier().substring(11, 13));
		txt_IDV_AHVNr.setEditable(false);
		txt_IDV_AHVNr.setLayoutData(gridData);

		Label lbl_AAD_address = new Label(insurantGroup, SWT.NONE);
		lbl_AAD_address.setText(messagesProperties.getProperty(ADDRESS));

		Text txt_AAD_address = new Text(insurantGroup, SWT.BORDER);
		txt_AAD_address.setText(getCardInfoData().getMailAddress().getAddressLine1());
		txt_AAD_address.setEditable(false);
		txt_AAD_address.setLayoutData(gridData);

		Label lbl_AAD_zip = new Label(insurantGroup, SWT.NONE);
		lbl_AAD_zip.setText(messagesProperties.getProperty(ZIP));

		Text txt_AAD_zip = new Text(insurantGroup, SWT.BORDER);
		txt_AAD_zip.setText(getCardInfoData().getMailAddress().getSwissZipCode());
		txt_AAD_zip.setEditable(false);
		txt_AAD_zip.setLayoutData(gridData);

		Label lbl_AAD_place = new Label(insurantGroup, SWT.NONE);
		lbl_AAD_place.setText(messagesProperties.getProperty(PLACE));

		Text txt_AAD_place = new Text(insurantGroup, SWT.BORDER);
		txt_AAD_place.setText(getCardInfoData().getMailAddress().getTown());
		txt_AAD_place.setEditable(false);
		txt_AAD_place.setLayoutData(gridData);
		
		Group insurerOKPGroup = new Group(container, SWT.BORDER);
		insurerOKPGroup.setText(messagesProperties.getProperty(INSURER_OKP_GROUP));
		insurerOKPGroup.setLayout(groupLayout);
		insurerOKPGroup.setLayoutData(gridData4);
		insurerOKPGroup.setFont(JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT));

		Label lbl_OKPV_name = new Label(insurerOKPGroup, SWT.NONE);
		lbl_OKPV_name.setText(messagesProperties.getProperty(INSURER_OKP_NAME));

		Text txt_OKPV_name = new Text(insurerOKPGroup, SWT.BORDER);
		txt_OKPV_name.setText(getCardInfoData().getAdministrativeData().getNameOfTheInstitution());
		txt_OKPV_name.setEditable(false);
		txt_OKPV_name.setLayoutData(gridData);

		Label lbl_OKPV_BSVNr = new Label(insurerOKPGroup, SWT.NONE);
		lbl_OKPV_BSVNr.setText(messagesProperties.getProperty(INSURER_OKP_BSV_NR));

		Text txt_OKPV_BSVNr = new Text(insurerOKPGroup, SWT.BORDER);
		txt_OKPV_BSVNr.setText(getCardInfoData().getAdministrativeData().getIdentificationNumberOfTheInstitution());
		txt_OKPV_BSVNr.setEditable(false);
		txt_OKPV_BSVNr.setLayoutData(gridData);

		Label lbl_OKPV_EANNr = new Label(insurerOKPGroup, SWT.NONE);
		lbl_OKPV_EANNr.setText(messagesProperties.getProperty(INSURER_OKP_GLN_NR));

		Text txt_OKPV_EANNr = new Text(insurerOKPGroup, SWT.BORDER);
		txt_OKPV_EANNr.setText(getCardInfoData().getInsurerInformation().getContactEanNumber());
		txt_OKPV_EANNr.setEditable(false);
		txt_OKPV_EANNr.setLayoutData(gridData);

		Label lbl_OKPV_VSNr = new Label(insurerOKPGroup, SWT.NONE);
		lbl_OKPV_VSNr.setText(messagesProperties.getProperty(INSURER_OKP_VSN_NR));

		Text txt_OKPV_VSNr = new Text(insurerOKPGroup, SWT.BORDER);
		txt_OKPV_VSNr.setText(getCardInfoData().getAdministrativeData().getInsuredNumber());
		txt_OKPV_VSNr.setEditable(false);
		txt_OKPV_VSNr.setLayoutData(gridData);

		Label lbl_OKPV_CCNr = new Label(insurerOKPGroup, SWT.NONE);
		lbl_OKPV_CCNr.setText(messagesProperties.getProperty(INSURER_OKP_CC_NR));

		Text txt_OKPV_CCNr = new Text(insurerOKPGroup, SWT.BORDER);
		txt_OKPV_CCNr.setText(getCardInfoData().getAdministrativeData().getCoverCardNo());
		txt_OKPV_CCNr.setEditable(false);
		txt_OKPV_CCNr.setLayoutData(gridData);

		Label lbl_OKPV_IDCNr = new Label(insurerOKPGroup, SWT.NONE);
		lbl_OKPV_IDCNr.setText(messagesProperties.getProperty(INSURER_OKP_IDC_NR));

		Text txt_OKPV_IDCNr = new Text(insurerOKPGroup, SWT.BORDER);
		txt_OKPV_IDCNr.setText(getCardInfoData().getAdministrativeData().getInsuredPersonNumber());
		txt_OKPV_IDCNr.setEditable(false);
		txt_OKPV_IDCNr.setLayoutData(gridData);

		Label lbl_OKPV_tel = new Label(insurerOKPGroup, SWT.NONE);
		lbl_OKPV_tel.setText(messagesProperties.getProperty(INSURER_OKP_PHONE_NR));

		/* Creates number like +41 (0)61 732 23 23 */
		String formatedNUmber;
		if (getCardInfoData().getInsurerInformation().getContactNumberGerman() != null) {
			formatedNUmber = (!getCardInfoData().getInsurerInformation().getContactNumberGerman().getNumber()
					.contains("-")
					&& (!getCardInfoData().getInsurerInformation().getContactNumberGerman().getNumber()
							.equalsIgnoreCase("--")))
									? "+" + getCardInfoData()
											.getInsurerInformation().getContactNumberGerman().getInternationalCode()
											+ " " + "("
											+ getCardInfoData().getInsurerInformation().getContactNumberGerman()
													.getLocalCode().substring(0, 1)
											+ ")"
											+ getCardInfoData().getInsurerInformation().getContactNumberGerman()
													.getLocalCode().substring(1, 3)
											+ " "
											+ getCardInfoData().getInsurerInformation().getContactNumberGerman()
													.getNumber().substring(0, 3)
											+ " "
											+ getCardInfoData().getInsurerInformation().getContactNumberGerman()
													.getNumber().substring(3, 5)
											+ " "
											+ getCardInfoData().getInsurerInformation().getContactNumberGerman()
													.getNumber().substring(5, 7)
									: "";
		} else {
			formatedNUmber = "";
		}

		Text txt_OKPV_tel = new Text(insurerOKPGroup, SWT.BORDER);
		txt_OKPV_tel.setText(formatedNUmber);
		txt_OKPV_tel.setEditable(false);
		txt_OKPV_tel.setLayoutData(gridData);

		Label lbl_OKPV_mail = new Label(insurerOKPGroup, SWT.NONE);
		lbl_OKPV_mail.setText(messagesProperties.getProperty(INSURER_OKP_MAIL));

		Text txt_OKPV_mail = new Text(insurerOKPGroup, SWT.BORDER);
		txt_OKPV_mail.setText(getCardInfoData().getInsurerInformation().getContactEmailAddress().toLowerCase());
		txt_OKPV_mail.setEditable(false);
		txt_OKPV_mail.setLayoutData(gridData);

		Group coverageGroup = new Group(container, SWT.BORDER);
		coverageGroup.setText(messagesProperties.getProperty(COVERAGE_GROUP));
		coverageGroup.setLayout(groupLayout);
		coverageGroup.setLayoutData(gridData5);
		coverageGroup.setFont(JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT));

		Label lbl_OKPVD_gModel = new Label(coverageGroup, SWT.NONE);
		lbl_OKPVD_gModel.setText(messagesProperties.getProperty(COVERAGE_OKPVD_MODEL));

		String model = getCardInfoData().getKvgInformation().getKVGModel();
		String modelHR = new String();
		if (model.equalsIgnoreCase("01")) {
			modelHR = messagesProperties.getProperty(COVERAGE_OKPVD_01);
		}
		if (model.equalsIgnoreCase("02")) {
			modelHR = messagesProperties.getProperty(COVERAGE_OKPVD_02);
		}
		if (model.equalsIgnoreCase("03")) {
			modelHR = messagesProperties.getProperty(COVERAGE_OKPVD_03);
		}
		if (model.equalsIgnoreCase("04")) {
			modelHR = messagesProperties.getProperty(COVERAGE_OKPVD_04);
		}
		if (model.equalsIgnoreCase("05")) {
			modelHR = messagesProperties.getProperty(COVERAGE_OKPVD_05);
		}
		if (model.equalsIgnoreCase("99")) {
			modelHR = messagesProperties.getProperty(COVERAGE_OKPVD_99);
		}

		Text txt_OKPVD_gModel = new Text(coverageGroup, SWT.BORDER);
		txt_OKPVD_gModel.setText(modelHR);
		txt_OKPVD_gModel.setEditable(false);
		txt_OKPVD_gModel.setLayoutData(gridData);

		Label lbl_OKPVD_gModelName = new Label(coverageGroup, SWT.NONE);
		lbl_OKPVD_gModelName.setText(messagesProperties.getProperty(COVERAGE_OKPVD_MODEL_NAME));

		Text txt_OKPVD_gModelName = new Text(coverageGroup, SWT.BORDER);
		txt_OKPVD_gModelName.setText(getCardInfoData().getKvgInformation().getKVGModelText());
		txt_OKPVD_gModelName.setEditable(false);
		txt_OKPVD_gModelName.setLayoutData(gridData);

		Label lbl_OKPVD_canton = new Label(coverageGroup, SWT.NONE);
		lbl_OKPVD_canton.setText(messagesProperties.getProperty(COVERAGE_OKPVD_CANTON));

		Text txt_OKPVD_canton = new Text(coverageGroup, SWT.BORDER);
		txt_OKPVD_canton.setText((getCardInfoData().getKvgInformation().getKvgCanton() != null
				&& getCardInfoData().getKvgInformation().getKvgCanton().getKvgCanton() != null)
						? getCardInfoData().getKvgInformation().getKvgCanton().getKvgCanton()
						: "--");
		txt_OKPVD_canton.setEditable(false);
		txt_OKPVD_canton.setLayoutData(gridData);

		Group insurerVVGGroup = new Group(container, SWT.BORDER);
		insurerVVGGroup.setText(messagesProperties.getProperty(INSURER_VVG_GROUP));
		insurerVVGGroup.setLayout(groupLayout);
		insurerVVGGroup.setLayoutData(gridData6);
		insurerVVGGroup.setFont(JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT));

		Label lbl_VVG_name = new Label(insurerVVGGroup, SWT.NONE);
		lbl_VVG_name.setText(messagesProperties.getProperty(INSURER_VVG_NAME));

		Text txt_VVG_name = new Text(insurerVVGGroup, SWT.BORDER);
		txt_VVG_name.setText(getCardInfoData().getVvgInformation().getVvgInsurerInformation().getNameOfVVGInsurer());
		txt_VVG_name.setEditable(false);
		txt_VVG_name.setLayoutData(gridData);

		Label lbl_VVG_BSVNr = new Label(insurerVVGGroup, SWT.NONE);
		lbl_VVG_BSVNr.setText(messagesProperties.getProperty(INSURER_VVG_BSV_NR));

		Text txt_VVG_BSVNr = new Text(insurerVVGGroup, SWT.BORDER);
		txt_VVG_BSVNr.setText(
				getCardInfoData().getVvgInformation().getVvgInsurerInformation().getIdentificationNumberOfVVGInsurer());
		txt_VVG_BSVNr.setEditable(false);
		txt_VVG_BSVNr.setLayoutData(gridData);

		Label lbl_VVG_EANNr = new Label(insurerVVGGroup, SWT.NONE);
		lbl_VVG_EANNr.setText(messagesProperties.getProperty(INSURER_VVG_GLN_NR));

		Text txt_VVG_EANNr = new Text(insurerVVGGroup, SWT.BORDER);
		txt_VVG_EANNr.setText(getCardInfoData().getVvgInformation().getVvgInsurerInformation().getInsurerInformation()
				.getContactEanNumber());
		txt_VVG_EANNr.setEditable(false);
		txt_VVG_EANNr.setLayoutData(gridData);

		Label lbl_VVG_VSNr = new Label(insurerVVGGroup, SWT.NONE);
		lbl_VVG_VSNr.setText(messagesProperties.getProperty(INSURER_VVG_VSN_NR));

		Text txt_VVG_VSNr = new Text(insurerVVGGroup, SWT.BORDER);
		txt_VVG_VSNr.setText(getCardInfoData().getVvgInformation().getVvgInsurerInformation().getInsuredNumber());
		txt_VVG_VSNr.setEditable(false);
		txt_VVG_VSNr.setLayoutData(gridData);

		Label lbl_VVG_CCNr = new Label(insurerVVGGroup, SWT.NONE);
		lbl_VVG_CCNr.setText(messagesProperties.getProperty(INSURER_VVG_CC_NR));

		Text txt_VVG_CCNr = new Text(insurerVVGGroup, SWT.BORDER);
		txt_VVG_CCNr.setText(getCardInfoData().getVvgInformation().getVvgInsurerInformation().getCoverCardNo());
		txt_VVG_CCNr.setEditable(false);
		txt_VVG_CCNr.setLayoutData(gridData);

		Label lbl_VVG_IDCNr = new Label(insurerVVGGroup, SWT.NONE);
		lbl_VVG_IDCNr.setText(messagesProperties.getProperty(INSURER_VVG_IDC_NR));

		Text txt_VVG_IDCNr = new Text(insurerVVGGroup, SWT.BORDER);
		txt_VVG_IDCNr
				.setText(getCardInfoData().getVvgInformation().getVvgInsurerInformation().getInsuredPersonNumber());
		txt_VVG_IDCNr.setEditable(false);
		txt_VVG_IDCNr.setLayoutData(gridData);

		Label lbl_VVG_tel = new Label(insurerVVGGroup, SWT.NONE);
		lbl_VVG_tel.setText(messagesProperties.getProperty(INSURER_VVG_PHONE_NR));

		String formatedTelNumber;
		if (getCardInfoData().getVvgInformation().getVvgInsurerInformation().getInsurerInformation()
				.getContactNumberGerman() != null) {
			/* Creates number like +41 (0)61 732 23 23 */
			formatedTelNumber = !getCardInfoData().getVvgInformation().getVvgInsurerInformation()
					.getInsurerInformation().getContactNumberGerman().getNumber().contains("-") ? "+"
							+ getCardInfoData().getVvgInformation().getVvgInsurerInformation().getInsurerInformation()
									.getContactNumberGerman().getInternationalCode()
							+ " " + "(" + getCardInfoData().getVvgInformation().getVvgInsurerInformation()
									.getInsurerInformation().getContactNumberGerman().getLocalCode().substring(0, 1)
							+ ")" + getCardInfoData().getVvgInformation().getVvgInsurerInformation()
									.getInsurerInformation().getContactNumberGerman().getLocalCode().substring(1, 3)
							+ " "
							+ getCardInfoData().getVvgInformation().getVvgInsurerInformation().getInsurerInformation()
									.getContactNumberGerman().getNumber().substring(0, 3)
							+ " "
							+ getCardInfoData().getVvgInformation().getVvgInsurerInformation().getInsurerInformation()
									.getContactNumberGerman().getNumber().substring(3, 5)
							+ " " + getCardInfoData().getVvgInformation().getVvgInsurerInformation()
									.getInsurerInformation().getContactNumberGerman().getNumber().substring(5, 7)
							: "";
		} else {
			formatedTelNumber = "";
		}
		Text txt_VVG_tel = new Text(insurerVVGGroup, SWT.BORDER);
		txt_VVG_tel.setText(formatedTelNumber);
		txt_VVG_tel.setEditable(false);
		txt_VVG_tel.setLayoutData(gridData);

		Label lbl_VVG_mail = new Label(insurerVVGGroup, SWT.NONE);
		lbl_VVG_mail.setText(messagesProperties.getProperty(INSURER_VVG_MAIL));

		Text txt_VVG_mail = new Text(insurerVVGGroup, SWT.BORDER);
		txt_VVG_mail.setText(getCardInfoData().getVvgInformation().getVvgInsurerInformation().getInsurerInformation()
				.getContactEmailAddress());
		txt_VVG_mail.setEditable(false);
		txt_VVG_mail.setLayoutData(gridData);

		Group vvgCoverageGroup = new Group(container, SWT.BORDER);
		vvgCoverageGroup.setText(messagesProperties.getProperty(VVG_COVERAGE_GROUP));
		vvgCoverageGroup.setLayout(groupLayout);
		vvgCoverageGroup.setLayoutData(gridData7);
		vvgCoverageGroup.setFont(JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT));

		Label lvl_medicationHL = new Label(vvgCoverageGroup, SWT.NONE);
		lvl_medicationHL.setText(messagesProperties.getProperty(LVL_MEDICATION_HL));

		Text txt_medicationHL = new Text(vvgCoverageGroup, SWT.BORDER);
		txt_medicationHL.setText(getCardInfoData().getVvgInformation().getMedicationHL().equalsIgnoreCase("00")
				? messagesProperties.getProperty(NO)
				: messagesProperties.getProperty(YES));
		txt_medicationHL.setEditable(false);
		txt_medicationHL.setLayoutData(gridData);

		Label lvl_medicationAccidentCoverageHL = new Label(vvgCoverageGroup, SWT.NONE);
		lvl_medicationAccidentCoverageHL.setText(messagesProperties.getProperty(LVL_MEDICATION_ACCIDENT_COVERAGE_HL));

		Text txt_medicationAccidentCoverageHL = new Text(vvgCoverageGroup, SWT.BORDER);
		txt_medicationAccidentCoverageHL
				.setText(getCardInfoData().getVvgInformation().getMedicationAccidentCoverageHL().equalsIgnoreCase("00")
						? messagesProperties.getProperty(NO)
						: messagesProperties.getProperty(YES));
		txt_medicationAccidentCoverageHL.setEditable(false);
		txt_medicationAccidentCoverageHL.setLayoutData(gridData);

		Label lvl_medicationKM = new Label(vvgCoverageGroup, SWT.NONE);
		lvl_medicationKM.setText(messagesProperties.getProperty(LVL_MEDICATION_KM));

		Text txt_medicationKM = new Text(vvgCoverageGroup, SWT.BORDER);
		txt_medicationKM.setText(getCardInfoData().getVvgInformation().getMedicationKM().equalsIgnoreCase("00")
				? messagesProperties.getProperty(NO)
				: messagesProperties.getProperty(YES));
		txt_medicationKM.setEditable(false);
		txt_medicationKM.setLayoutData(gridData);

		Label lvl_medicationAccidentCoverageKM = new Label(vvgCoverageGroup, SWT.NONE);
		lvl_medicationAccidentCoverageKM.setText(messagesProperties.getProperty(LVL_MEDICATION_ACCIDENT_COVERAGE_KM));

		Text txt_medicationAccidentCoverageKM = new Text(vvgCoverageGroup, SWT.BORDER);
		txt_medicationAccidentCoverageKM
				.setText(getCardInfoData().getVvgInformation().getMedicationAccidentCoverageKM().equalsIgnoreCase("00")
						? messagesProperties.getProperty(NO)
						: messagesProperties.getProperty(YES));
		txt_medicationAccidentCoverageKM.setEditable(false);
		txt_medicationAccidentCoverageKM.setLayoutData(gridData);
		Button btnClose = new Button(container, SWT.PUSH);
		btnClose.setText(messagesProperties.getProperty(CLOSE));
		btnClose.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.getShell().close();
			}
		});

		parent.getShell().setText(messagesProperties.getProperty(TITLE));
		container.pack();
		container.getShell().pack();
		return container;
	}

	public CardInfoData getCardInfoData() {
		return cardInfoData;
	}

	public void setCardInfoData(CardInfoData cardInfoData) {
		this.cardInfoData = cardInfoData;
	}
}
