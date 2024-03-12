package ch.elexis.labororder.lg1_medicalvalues.order.model;

import org.apache.commons.lang3.StringUtils;
import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.time.format.DateTimeFormatter;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.types.Gender;
import ch.elexis.core.constants.XidConstants;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.labororder.lg1_medicalvalues.messages.Messages;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.TimeTool;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ICoverage;

import org.apache.http.client.utils.URIBuilder;

public class Patient {
        String id = StringUtils.EMPTY;
        String dateofbirth = StringUtils.EMPTY;
        String gender = StringUtils.EMPTY;
        String title = StringUtils.EMPTY;
        String lastname = StringUtils.EMPTY;
        String firstname = StringUtils.EMPTY;
        String street = StringUtils.EMPTY;
        String houseNumber = StringUtils.EMPTY;
        String zip = StringUtils.EMPTY;
        String city = StringUtils.EMPTY;
        String country = StringUtils.EMPTY;
        String telephoneNumberHome = StringUtils.EMPTY;
        String mobilePhoneNumber = StringUtils.EMPTY;
        String insurancenumber = StringUtils.EMPTY;
        String insurancename = StringUtils.EMPTY;
        String insurancegln = StringUtils.EMPTY;
        String billing = StringUtils.EMPTY;
        String socialSecurityNumber = StringUtils.EMPTY;
        String physicianId = StringUtils.EMPTY;
        String laboratoryCustomerId = StringUtils.EMPTY;

        private static Pattern pattern = Pattern.compile("^([A-Za-z-ß\\s]+)(\\d+)$");

        public static Patient of(IPatient patient){
                IContact activeUser = ContextServiceHolder.get().getActiveUserContact().get();
                Patient ret = new Patient();

                ret.id = patient.getId();
                ret.dateofbirth = patient.getDateOfBirth().format(DateTimeFormatter.ISO_LOCAL_DATE);
                ret.gender = patient.getGender() == Gender.FEMALE ? "female" : "male";
                ret.title = patient.getTitel();
                ret.lastname = patient.getLastName();
                ret.firstname = patient.getFirstName();

                String street = patient.getStreet().trim();

                try {
                        Matcher matcher = pattern.matcher(street);
                        matcher.find();
                        ret.street = matcher.group(1).trim();
                        ret.houseNumber = matcher.group(2).trim();
                } catch (Exception e) {
                        ret.street = street;
                        ret.houseNumber = StringUtils.EMPTY;
                }

                ret.zip = patient.getZip();
                ret.city = patient.getCity();
                ret.country = patient.getCountry().value();

                ret.telephoneNumberHome = patient.getPhone1();
                ret.mobilePhoneNumber = patient.getMobile();

                ICoverage coverage = ContextServiceHolder.get().getActiveCoverage().get();

                if (coverage == null) {
                        MessageDialog.openError(Display.getDefault().getActiveShell(),
                                Messages.LabOrderAction_errorTitleNoFallSelected,
                                Messages.LabOrderAction_errorMessageNoFallSelected);
                }

                ret.insurancenumber = coverage.getInsuranceNumber();

                IContact costBearer = coverage.getCostBearer();
                ret.insurancename = costBearer.getDescription1();
                ret.insurancegln = costBearer.getXid(XidConstants.EAN).getDomainId();

                ret.billing = getBilling(coverage);
                ret.socialSecurityNumber = patient.getXid(XidConstants.CH_AHV).getDomainId();
                ret.physicianId = activeUser.getXid(XidConstants.EAN).getDomainId();
                ret.laboratoryCustomerId = activeUser.getXid(XidConstants.DOMAIN_BSVNUM).getDomainId();

                return ret;
        }

        public void toMedicalvaluesOrderCreationAPIQueryParams(URIBuilder builder) throws IllegalArgumentException {
                setRequiredParameterOrThrow(builder, "laboratoryCustomerId", "BSV-Nummer des Einsenders", this.laboratoryCustomerId);
                setRequiredParameterOrThrow(builder, "physicianId", "GLN-Nummer (EAN) des Arztes", this.physicianId);

                setRequiredParameterOrThrow(builder, "patientIdentifier", "Patienten ID", this.id);
                setRequiredParameterOrThrow(builder, "patient_name_given", "Vorname", this.firstname);
                setRequiredParameterOrThrow(builder, "patient_name_family", "Nachname", this.lastname);
                setRequiredParameterOrThrow(builder, "patient_birthDate", "Geburtsdatum", this.dateofbirth);
                setRequiredParameterOrThrow(builder, "patient_gender", "Geschlecht",this.gender);
                setRequiredParameterOrThrow(builder, "coverage_type", "Abrechnungsart", this.billing);

                setOptionalParameter(builder, "patient_name_title", this.title);
                setOptionalParameter(builder, "patient_socialSecurityNumber", this.socialSecurityNumber);
                setOptionalParameter(builder, "patient_address_street", this.street);
                setOptionalParameter(builder, "patient_address_housenumber", this.houseNumber);
                setOptionalParameter(builder, "patient_address_postalCode", this.zip);
                setOptionalParameter(builder, "patient_address_city", this.city);
                setOptionalParameter(builder, "patient_address_country", this.country);
                setOptionalParameter(builder, "patient_telecom_home", this.telephoneNumberHome);
                setOptionalParameter(builder, "patient_telecom_mobile", this.mobilePhoneNumber);
                setOptionalParameter(builder, "coverage_payor_display", this.insurancename);
                setOptionalParameter(builder, "coverage_payor_identifier", this.insurancegln);
        }

        private void setRequiredParameterOrThrow(URIBuilder builder, String key, String readableKey, String value) throws IllegalArgumentException {
                if (value == null || value.isEmpty()) {
                        throw new IllegalArgumentException(readableKey);
                }

                builder.setParameter(key, value);
        }

        private void setOptionalParameter(URIBuilder builder, String key, String value) {
                if (value != null && !value.isEmpty()) {
                        builder.setParameter(key, value);
                }
        }

        private static String getBilling(ICoverage coverage){
                IContact costBearer = coverage.getCostBearer();
                IContact guarantor = coverage.getGuarantor();

                if (costBearer == null) {
                        costBearer = coverage.getGuarantor();
                }

                if (costBearer != null ) {
                    if (costBearer.isOrganization()) {
                        return "SwissIns";
                    }

                    if (costBearer.isPerson()) {
                        return "SEL";
                    }
                }

                // default
                return "SEL";
        }
}
