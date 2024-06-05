package ch.elexis.labororder.lg1_medicalvalues.order.model;

import org.apache.commons.lang3.StringUtils;
import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.NoSuchElementException;
import java.util.Objects;

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
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IXid;

import org.apache.http.client.utils.URIBuilder;
import ch.elexis.labororder.lg1_medicalvalues.order.model.exceptions.NoEncounterSelectedException;

public class Patient {
        String aisIdentifier = StringUtils.EMPTY;
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
        String email = StringUtils.EMPTY;
        String insurancenumber = StringUtils.EMPTY;
        String insurancename = StringUtils.EMPTY;
        String insurancegln = StringUtils.EMPTY;
        String billing = StringUtils.EMPTY;
        String reason = StringUtils.EMPTY;
        String socialSecurityNumber = StringUtils.EMPTY;
        String physicianGlnNumber = StringUtils.EMPTY;

        private static Pattern pattern = Pattern.compile("^([A-Za-z-ß\\s]+)(\\d+)$");

        public static Patient of(IPatient patient) throws NoEncounterSelectedException {
                Optional<IMandator> mandator = ContextServiceHolder.get().getActiveMandator();
                IMandator activeMandator = null;

                if (mandator.isPresent()) {
                        activeMandator = mandator.get();
                }

                Patient ret = new Patient();

                ret.aisIdentifier = patient.getPatientNr();
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
                ret.email = patient.getEmail();

                ICoverage coverage = null;

                try {
                        coverage = ContextServiceHolder.get().getActiveCoverage().get();
                } catch (NoSuchElementException e) {
                        throw new NoEncounterSelectedException();
                }

                if (coverage == null) {
                        throw new NoEncounterSelectedException();
                }

                ret.insurancenumber = coverage.getInsuranceNumber();

                IContact costBearer = coverage.getCostBearer();
                ret.insurancename = costBearer.getDescription1();

                IXid costBearerEAN = costBearer.getXid(XidConstants.EAN);
                if (costBearerEAN != null) {
                        ret.insurancegln = costBearerEAN.getDomainId();
                }

                ret.billing = getBilling(coverage);
                ret.reason = getReason(coverage);

                IXid patientAHV = patient.getXid(XidConstants.CH_AHV);
                if (patientAHV != null) {
                        ret.socialSecurityNumber = patientAHV.getDomainId();
                }

                if (activeMandator != null) {
                        IXid physicianEAN = activeMandator.getXid(XidConstants.EAN);
                        if (physicianEAN != null) {
                                ret.physicianGlnNumber = physicianEAN.getDomainId();
                        }
                }

                return ret;
        }

        public void toMedicalvaluesOrderCreationAPIQueryParams(URIBuilder builder) throws IllegalArgumentException {
                setOptionalParameter(builder, "physician_gln_number", this.physicianGlnNumber);

                setOptionalParameter(builder, "source_system_name", "Elexis");
                setOptionalParameter(builder, "source_system_patient_id", this.aisIdentifier);

                setRequiredParameterOrThrow(builder, "patient_name_given", "Vorname", this.firstname);
                setRequiredParameterOrThrow(builder, "patient_name_family", "Nachname", this.lastname);
                setRequiredParameterOrThrow(builder, "patient_birthDate", "Geburtsdatum", this.dateofbirth);
                setRequiredParameterOrThrow(builder, "patient_gender", "Geschlecht", this.gender);
                setRequiredParameterOrThrow(builder, "coverage_type", "Abrechnungsart", this.billing);

                setOptionalParameter(builder, "patient_name_title", this.title);
                setOptionalParameter(builder, "patient_social_security_number", this.socialSecurityNumber);
                setOptionalParameter(builder, "patient_address_street", this.street);
                setOptionalParameter(builder, "patient_address_house_number", this.houseNumber);
                setOptionalParameter(builder, "patient_address_postal_code", this.zip);
                setOptionalParameter(builder, "patient_address_city", this.city);
                setOptionalParameter(builder, "patient_address_country", this.country);
                setOptionalParameter(builder, "patient_telecom_home", this.telephoneNumberHome);
                setOptionalParameter(builder, "patient_telecom_mobile", this.mobilePhoneNumber);
                setOptionalParameter(builder, "patient_email", this.email);
                setOptionalParameter(builder, "coverage_payor_display", this.insurancename);
                setOptionalParameter(builder, "coverage_payor_identifier", this.insurancegln);
                setOptionalParameter(builder, "coverage_identifier", this.insurancenumber);
                setOptionalParameter(builder, "order_treatment_type", this.reason);
        }

        private void setRequiredParameterOrThrow(URIBuilder builder, String key, String readableKey, String value)
                        throws IllegalArgumentException {
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

        private static String getBilling(ICoverage coverage) {
                BillingLaw billingLaw = coverage.getBillingSystem().getLaw();
                if (billingLaw == BillingLaw.VVG ||
                                billingLaw == BillingLaw.MV ||
                                billingLaw == BillingLaw.IV ||
                                billingLaw == BillingLaw.KVG) {
                        return "SwissIns";
                }
                if (billingLaw == BillingLaw.UVG) {
                        return "SwissAccidentInsurance";
                }
                if (billingLaw == BillingLaw.privat) {
                        return "SEL";
                }

                return "SEL";
        }

        private static String getReason(ICoverage coverage) {
                String reason = coverage.getReason();

                if (Objects.equals(reason, "Krankheit")) {
                        return "1";
                }
                if (Objects.equals(reason, "Mutterschaft")) {
                        return "2";
                }
                if (Objects.equals(reason, "Unfall")) {
                        return "3";
                }
                if (Objects.equals(reason, "Prävention")) {
                        return "4";
                }

                return StringUtils.EMPTY;
        }
}
