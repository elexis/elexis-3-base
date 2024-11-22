package ch.elexis.mednet.webapi.ui.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;
import ch.elexis.mednet.webapi.core.messages.Messages;
import ch.elexis.mednet.webapi.ui.parts.DocumentsSelectionDialog;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DataHandler {

	private static final Logger logger = LoggerFactory.getLogger(DataHandler.class);
	private static final Gson gson = new Gson();
	private static boolean isEpdSelected = false;

	public static void fetchAndDisplayProviders(Table providerTable, int customerID) {
		providerTable.removeAll();
		Optional<String> authToken = ServiceHelper.getAuthToken("mednet"); //$NON-NLS-1$

		if (authToken.isPresent()) {
			String token = authToken.get();
			PatientFetcher fetcher = new PatientFetcher(token);
			String providerResponse = fetcher.fetchProvidersId(customerID);

			Type providerListType = new TypeToken<List<Map<String, Object>>>() {
			}.getType();
			List<Map<String, Object>> providers = gson.fromJson(providerResponse, providerListType);

			String[] keys = { "id", "lastName", "specialty" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			String[] defaults = { "Unknown ID", "Unknown Last Name", "N/A" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			TableHelper.fillTableFromList(providerTable, providers, providerTable.getDisplay(), keys, defaults);
			logger.info("Providers fetched and displayed successfully."); //$NON-NLS-1$
		} else {
			logger.warn("No authentication token available."); //$NON-NLS-1$
		}
	}

	public static boolean loadSubmittedFormsData(Table submittedFormsTable, Integer customerId) {
		Optional<String> authToken = ServiceHelper.getAuthToken("mednet"); //$NON-NLS-1$

		if (authToken.isPresent()) {
			String token = authToken.get();
			PatientFetcher fetcher = new PatientFetcher(token);
			String submittedFormsResponse = fetcher.fetchSubmitFormsId(customerId);

			if (submittedFormsResponse.startsWith("Error")) { //$NON-NLS-1$
				logger.error("Error loading forms: " + submittedFormsResponse); //$NON-NLS-1$
				return false;
			}

			try {
				Type formListType = new TypeToken<List<Map<String, Object>>>() {
				}.getType();
				List<Map<String, Object>> forms = gson.fromJson(submittedFormsResponse, formListType);
				if (forms != null && !forms.isEmpty()) {
					String[] keys = { "referenceNr", "downloadUrl", "externalPatientId", "patientLastName", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"patientDateOfBirth", "packageType", "title", "customer.lastName", "provider.lastName", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
							"patientFirstName", "packageId" }; //$NON-NLS-1$ //$NON-NLS-2$
					String[] defaults = { "Unknown ID", "Unknown URL", "None Set", "Unknown Last Name", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"Unknown Birthdate", "Unknown Type", "No Title", "Unknown Sender", "Unknown Receiver", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
							"Unknown First Name", "No PackageId" }; //$NON-NLS-1$ //$NON-NLS-2$

					TableHelper.fillTableFromList(submittedFormsTable, forms, submittedFormsTable.getDisplay(), keys,
							defaults);
					logger.info("Submitted forms loaded successfully."); //$NON-NLS-1$
					return true;
				} else {
					logger.info("No submitted forms available."); //$NON-NLS-1$
				}
			} catch (JsonSyntaxException ex) {
				logger.error("Error parsing JSON response: " + ex.getMessage()); //$NON-NLS-1$
			}
		} else {
			logger.warn("No authentication token available."); //$NON-NLS-1$
		}

		return false;
	}

	public static void fetchAndDisplayFormsForProvider(Table formTable, Integer providerId, Integer customerId) {
		formTable.removeAll();
		Optional<String> authToken = ServiceHelper.getAuthToken("mednet"); //$NON-NLS-1$

		if (authToken.isPresent()) {
			String token = authToken.get();
			PatientFetcher fetcher = new PatientFetcher(token);
			String formsResponse = fetcher.fetchFormsByProviderIdWithRetry(customerId, providerId);

			Type formListType = new TypeToken<List<Map<String, Object>>>() {
			}.getType();
			List<Map<String, Object>> forms = gson.fromJson(formsResponse, formListType);

			String[] keys = { "id", "title", "description" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			String[] defaults = { "Unknown ID", "Unknown Title", "N/A" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			TableHelper.fillTableFromList(formTable, forms, formTable.getDisplay(), keys, defaults);
			logger.info("Forms fetched for provider and displayed successfully."); //$NON-NLS-1$
		} else {
			logger.warn("No authentication token available."); //$NON-NLS-1$
		}
	}

	public static void fetchAndDisplayCustomers(Table customerTable) {
		Optional<String> authToken = ServiceHelper.getAuthToken("mednet"); //$NON-NLS-1$

		if (authToken.isPresent()) {
			String token = authToken.get();
			PatientFetcher fetcher = new PatientFetcher(token);
			String customerResponse = fetcher.fetchCustomerId();

			Type customerListType = new TypeToken<List<Map<String, Object>>>() {
			}.getType();
			List<Map<String, Object>> customers = gson.fromJson(customerResponse, customerListType);

			String[] keys = { "id", "firstName", "lastName" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			String[] defaults = { "Unknown ID", "Unknown First Name", "Unknown Last Name" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			TableHelper.fillTableFromList(customerTable, customers, customerTable.getDisplay(), keys, defaults);
			logger.info("Customers fetched and displayed successfully."); //$NON-NLS-1$
		} else {
			logger.warn("No authentication token available."); //$NON-NLS-1$
		}
	}
	public static void fillPatientData(Integer customerId, Integer providerId, Integer formId) {
		Optional<IPatient> pat = ContextServiceHolder.get().getActivePatient();

		if (!pat.isPresent()) {
			Display.getDefault().asyncExec(() -> {
				Shell shell = Display.getDefault().getActiveShell();
				MessageDialog.openWarning(shell, Messages.DataHandler_noPatientSelectedTitle,
						Messages.DataHandler_noPatientSelectedMessage);
			});
			logger.warn("No active patient selected."); //$NON-NLS-1$
			return;
		}

		IPatient ipatient = pat.get();
		List<IDocument> selectedDocuments = null;

		boolean sendDocuments = MessageDialog.openQuestion(
			    Display.getDefault().getActiveShell(),
			    Messages.DataHandler_sendDocumentsTitle,
			    Messages.DataHandler_sendDocumentsMessage
			);

		if (sendDocuments) {

			DocumentsSelectionDialog dialog = new DocumentsSelectionDialog(Display.getDefault().getActiveShell());
			if (dialog.open() == DocumentsSelectionDialog.OK) {
				selectedDocuments = dialog.getSelectedDocuments();
				isEpdSelected = dialog.isEpdCheckboxSelected();
				logger.info("Documents selected to be sent: " + selectedDocuments.size()); //$NON-NLS-1$

			} else {
				logger.info("No documents selected."); //$NON-NLS-1$
				return;
			}
		}

		Optional<String> authToken = ServiceHelper.getAuthToken("mednet"); //$NON-NLS-1$

		if (authToken.isPresent()) {
			String token = authToken.get();
			PatientFetcher fetcher = new PatientFetcher(token);

			Map<String, Object> patientData = Map.of(FHIRConstants.FHIRKeys.CUSTOMER_ID, customerId,
					FHIRConstants.FHIRKeys.PROVIDER_ID, providerId, FHIRConstants.FHIRKeys.FORM_ID, formId);

			try {
				JsonObject patientJson = gson.toJsonTree(patientData).getAsJsonObject();
				String response = fetcher.fillPatientData(ipatient, patientJson, selectedDocuments, isEpdSelected);
				logger.info("Patient data sent successfully: {}", response); //$NON-NLS-1$
			} catch (Exception ex) {
				logger.error("Error sending patient data", ex); //$NON-NLS-1$
			}
		} else {
			logger.warn("No authentication token available."); //$NON-NLS-1$
		}
	}


	public static void loadCustomersFromApi(CCombo customerCombo) {
		Optional<String> authToken = ServiceHelper.getAuthToken("mednet"); //$NON-NLS-1$

		if (authToken.isPresent()) {
			String token = authToken.get();
			PatientFetcher fetcher = new PatientFetcher(token);
			String customerResponse = fetcher.fetchCustomerId();

			Type customerListType = new TypeToken<List<Map<String, Object>>>() {
			}.getType();
			List<Map<String, Object>> customers = gson.fromJson(customerResponse, customerListType);

			for (int i = 0; i < customers.size(); i++) {
				Map<String, Object> customer = customers.get(i);
				int customerId = ((Double) customer.get("id")).intValue(); //$NON-NLS-1$
				String customerName = (String) customer.getOrDefault("lastName", "Unknown Name"); //$NON-NLS-1$ //$NON-NLS-2$
				String customerFirstName = (String) customer.getOrDefault("firstName", "Unknown First Name"); //$NON-NLS-1$ //$NON-NLS-2$
				customerCombo.add(customerId + " - " + customerFirstName + " " + customerName); //$NON-NLS-1$ //$NON-NLS-2$

			}
			customerCombo.select(0);
			logger.info("Customers loaded from API and displayed in the combo box."); //$NON-NLS-1$
		} else {
			logger.warn("No authentication token available."); //$NON-NLS-1$
		}
	}
}
