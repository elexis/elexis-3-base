package ch.elexis.mednet.webapi.ui.parts;

import java.util.Stack;

import jakarta.annotation.PostConstruct;

import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.ResourceManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import ch.elexis.mednet.webapi.core.IMednetAuthUi;
import ch.elexis.mednet.webapi.core.constants.IconConstants;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;
import ch.elexis.mednet.webapi.core.constants.StateConstants;
import ch.elexis.mednet.webapi.core.constants.URLConstants;
import ch.elexis.mednet.webapi.core.messages.Messages;
import ch.elexis.mednet.webapi.ui.handler.PatientFetcher;
import ch.elexis.mednet.webapi.ui.navigation.NavigationState;
import ch.elexis.mednet.webapi.ui.util.ButtonFactory;
import ch.elexis.mednet.webapi.ui.util.CompositeEffectHandler;

public class MedNetMainComposite {

	@org.osgi.service.component.annotations.Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
	private IMednetAuthUi authUi;

	private Composite medNetViewerComposite;
	private Stack<NavigationState> navigationStack = new Stack<>();
	private CustomerComposite customerComposite;
	private ProviderComposite providerComposite;
	private FormComposite formComposite;
	private SubmittedFormsComposite submittedFormsComposite;
	private PatientFetcher patientFetcher;
	private Color customBlue;
	private Composite navigationBreadcrumbComposite;

	@PostConstruct
	public void createControls(Composite parent, EMenuService menuService) {
		parent.setLayout(new GridLayout(2, false));
		Composite navigationComposite = new Composite(parent, SWT.NONE);
		GridData navGridData = new GridData(SWT.FILL, SWT.FILL, false, true);
		navGridData.widthHint = 200;
		navigationComposite.setLayoutData(navGridData);
		navigationComposite.setLayout(new GridLayout(1, false));
		this.customBlue = new Color(parent.getDisplay(), new RGB(42, 108, 155));
		navigationComposite.setBackground(customBlue);
		Composite mainContentComposite = new Composite(parent, SWT.NONE);
		mainContentComposite.setLayout(new GridLayout(1, false));
		mainContentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		navigationBreadcrumbComposite = new Composite(mainContentComposite, SWT.NONE);
		GridLayout breadcrumbLayout = new GridLayout(10, false);
		breadcrumbLayout.marginHeight = 5;
		breadcrumbLayout.marginWidth = 5;
		navigationBreadcrumbComposite.setLayout(breadcrumbLayout);
		GridData breadcrumbGridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		breadcrumbGridData.heightHint = 30;
		navigationBreadcrumbComposite.setLayoutData(breadcrumbGridData);
		medNetViewerComposite = new Composite(mainContentComposite, SWT.NONE);
		medNetViewerComposite.setLayout(new GridLayout(1, false));
		medNetViewerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createLogoAndButtons(navigationComposite);
		showInitialLogo();
		parent.addDisposeListener(e -> {
			customBlue.dispose();
		});
	}

	private void createLogoAndButtons(Composite navigationComposite) {
		Composite logoComposite = new Composite(navigationComposite, SWT.NONE);
		logoComposite.setLayout(new GridLayout(2, false));
		logoComposite.setBackground(customBlue);
		GridData logoCompositeGridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		logoCompositeGridData.heightHint = 80;
		logoComposite.setLayoutData(logoCompositeGridData);
		Label imageLabel = new Label(logoComposite, SWT.NONE);
		imageLabel.setImage(
				ResourceManager.getPluginImage(PreferenceConstants.MEDNET_PLUGIN_STRING, IconConstants.ICON_HEARTBEAT));
		imageLabel.setBackground(customBlue);
		Label logoLabel = new Label(logoComposite, SWT.NONE);
		logoLabel.setImage(ResourceManager.getPluginImage(PreferenceConstants.MEDNET_PLUGIN_STRING,
				IconConstants.ICON_LOGO_MEDNET_WHITE));
		logoLabel.setForeground(navigationComposite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		logoLabel.setBackground(customBlue);
		logoLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

		ButtonFactory.createToggleButtonComposite(navigationComposite, IconConstants.ICON_TOGGEL_RED,
				IconConstants.ICON_TOGGEL_GREEN, Messages.MedNetMainComposite_activateImport, customBlue);
		ButtonFactory.createButtonComposite(navigationComposite, IconConstants.ICON_BARCODE_WHITE,
				IconConstants.ICON_BARCODE_BLUE, Messages.MedNetMainComposite_formWithPatientData, customBlue,
				this::connect);
		ButtonFactory.createButtonComposite(navigationComposite, IconConstants.ICON_GROUP_WHITE,
				IconConstants.ICON_GROUP_BLUE, Messages.MedNetMainComposite_showPatients, customBlue,
				() -> openMednetDocuments(URLConstants.getBaseApiUrl() + URLConstants.URL_PATIENTS));
		ButtonFactory.createButtonComposite(navigationComposite, IconConstants.ICON_BARS_WHITE,
				IconConstants.ICON_BARS_BLUE, Messages.MedNetMainComposite_tasks, customBlue,
				() -> openMednetDocuments(URLConstants.getBaseApiUrl() + URLConstants.URL_TASKS));
		ButtonFactory.createButtonComposite(navigationComposite, IconConstants.ICON_GOOGLE_DOCS_WHITE,
				IconConstants.ICON_GOOGLE_DOCS_BLUE, Messages.MedNetMainComposite_documents, customBlue,
				() -> openMednetDocuments(URLConstants.getBaseApiUrl() + URLConstants.URL_DOCUMENTS));
		ButtonFactory.createButtonComposite(navigationComposite, IconConstants.ICON_ADDRESS_BOOK_REGULAR_WHITE,
				IconConstants.ICON_ADDRESS_BOOK_REGULAR_BLUE, Messages.MedNetMainComposite_therapy, customBlue,
				() -> openMednetDocuments(URLConstants.getBaseApiUrl() + URLConstants.URL_THERAPY));
		ButtonFactory.createButtonComposite(navigationComposite, IconConstants.ICON_IMPORT_WHITE,
				IconConstants.ICON_IMPORT_BLUE, Messages.MedNetMainComposite_forms, customBlue,
				this::showSubmittedForms);
	}

	private void openMednetDocuments(String link) {
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		ServiceReference<IMednetAuthUi> serviceReference = context.getServiceReference(IMednetAuthUi.class);
		authUi = context.getService(serviceReference);
		if (authUi != null) {
			authUi.openBrowser(link);
		} else {
			MessageDialog.openError(medNetViewerComposite.getShell(), Messages.MedNetMainComposite_error,
					Messages.MedNetMainComposite_browserError);
		}
	}

	private void showInitialLogo() {
		Composite logoContainer = new Composite(medNetViewerComposite, SWT.NONE);
		logoContainer.setLayout(new GridLayout(1, false));
		GridData logoContainerData = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		logoContainer.setLayoutData(logoContainerData);
		Label logoLabel = new Label(logoContainer, SWT.NONE);
		logoLabel.setBackground(medNetViewerComposite.getBackground());
		Image logoImage = ResourceManager.getPluginImage(PreferenceConstants.MEDNET_PLUGIN_STRING,
				IconConstants.ICON_LOGO_MEDNET_FULLCOLOR);
		logoLabel.setImage(logoImage);
		GridData logoGridData = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		logoLabel.setLayoutData(logoGridData);
		medNetViewerComposite.layout();
	}

	public void loadCustomers() {
		clearPreviousData();
		customerComposite = new CustomerComposite(medNetViewerComposite, this::onCustomerSelected);
		customerComposite.show();
		navigationStack.clear();
		updateBreadcrumbNavigation();
		medNetViewerComposite.layout();
	}

	private void onCustomerSelected(Integer customerId, String customerName) {
		loadProviders(customerId, customerName);
	}

	private void showSubmittedForms() {
		clearPreviousData();
		submittedFormsComposite = new SubmittedFormsComposite(medNetViewerComposite);
		submittedFormsComposite.showSubmittedForms();
		medNetViewerComposite.layout();
	}

	public void loadProviders(Integer customerId, String customerName) {
		clearPreviousData();
		removeStatesAfter(StateConstants.CUSTOMER);
		if (!navigationStack.isEmpty() && StateConstants.CUSTOMER.equals(navigationStack.peek().state)) {
			NavigationState customerState = navigationStack.peek();
			customerState.customerId = customerId;
			customerState.customerName = customerName;
		} else {

			navigationStack.push(
					new NavigationState(StateConstants.CUSTOMER, customerId, null, null, null, customerName, null));
		}
		providerComposite = new ProviderComposite(medNetViewerComposite, this::onProviderSelected, patientFetcher);
		providerComposite.show(customerId);
		updateBreadcrumbNavigation();
		medNetViewerComposite.layout();
	}

	private void onProviderSelected(Integer providerId, String providerName) {
		loadForms(providerId, providerName);
	}

	public void loadForms(Integer providerId, String providerName) {
		if (!navigationStack.isEmpty()) {
			NavigationState previousState = navigationStack.peek();
			if (previousState.customerId != null) {
				clearPreviousData();
				if (StateConstants.PROVIDER.equals(previousState.state)
						&& previousState.providerId.equals(providerId)) {
				} else {
					removeStatesAfter(StateConstants.CUSTOMER);
					navigationStack.push(new NavigationState(StateConstants.PROVIDER, previousState.customerId,
							providerId, null, null, previousState.customerName, providerName));
				}
				formComposite = new FormComposite(medNetViewerComposite,
						(customerId, providerId1, formId, formName) -> {
							removeStatesAfter(StateConstants.PROVIDER);
							navigationStack.push(new NavigationState(StateConstants.FORM, customerId, providerId1,
									formId, formName, previousState.customerName, providerName));
							updateBreadcrumbNavigation();
						});
				formComposite.show(previousState.customerId, providerId);
				updateBreadcrumbNavigation();
				medNetViewerComposite.layout();
			}
		}
	}

	private void connect() {
		loadCustomers();
	}

	private void clearPreviousData() {
		for (Control child : medNetViewerComposite.getChildren()) {
			child.dispose();
		}
	}

	private void updateBreadcrumbNavigation() {
		for (Control child : navigationBreadcrumbComposite.getChildren()) {
			child.dispose();
		}
		for (int i = 0; i < navigationStack.size(); i++) {
			NavigationState state = navigationStack.get(i);
			final int stateIndex = i;
			Composite breadcrumbComposite = new Composite(navigationBreadcrumbComposite, SWT.NONE);
			breadcrumbComposite.setLayout(new GridLayout(2, false));
			breadcrumbComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			breadcrumbComposite
					.setBackground(navigationBreadcrumbComposite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			Label iconLabel = new Label(breadcrumbComposite, SWT.NONE);
			iconLabel.setBackground(breadcrumbComposite.getBackground());
			Image leftArrowIcon = ResourceManager.getPluginImage(PreferenceConstants.MEDNET_PLUGIN_STRING,
					IconConstants.ICON_LEFT_ARROW);
			Image leftArrowIconWhite = ResourceManager.getPluginImage(PreferenceConstants.MEDNET_PLUGIN_STRING,
					IconConstants.ICON_LEFT_ARROW_WHITE);
			iconLabel.setImage(leftArrowIcon);
			Label textLabel = new Label(breadcrumbComposite, SWT.NONE);
			textLabel.setText(getBreadcrumbText(state));
			textLabel.setBackground(breadcrumbComposite.getBackground());
			textLabel.setForeground(customBlue);
			textLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));

			CompositeEffectHandler.addHoverEffectBreadcrumbNavigation(breadcrumbComposite, iconLabel, textLabel,
					leftArrowIcon, leftArrowIconWhite, customBlue);
			CompositeEffectHandler.addPressReleaseEffect(breadcrumbComposite, iconLabel, textLabel, leftArrowIconWhite,
					() -> navigateToState(stateIndex), breadcrumbComposite.getBackground());
		}

		navigationBreadcrumbComposite.layout();
	}

	private void navigateToState(int stateIndex) {
		while (navigationStack.size() > stateIndex + 1) {
			navigationStack.pop();
		}
		NavigationState targetState = navigationStack.peek();
		if (StateConstants.CUSTOMER.equals(targetState.state)) {
			loadCustomers();
		} else if (StateConstants.PROVIDER.equals(targetState.state)) {
			loadProviders(targetState.customerId, targetState.customerName);
		} else if (StateConstants.FORM.equals(targetState.state)) {
			loadForms(targetState.providerId, targetState.providerName);
		}
		updateBreadcrumbNavigation();
	}

	private void removeStatesAfter(String stateName) {
		int index = -1;
		for (int i = 0; i < navigationStack.size(); i++) {
			if (stateName.equals(navigationStack.get(i).state)) {
				index = i;
				break;
			}
		}
		if (index >= 0) {
			while (navigationStack.size() > index + 1) {
				navigationStack.pop();
			}
		}
	}

	private String getBreadcrumbText(NavigationState state) {
		if (StateConstants.CUSTOMER.equals(state.state)) {
			return state.customerName != null ? state.customerName : Messages.BreadcrumbNavigation_customers;
		} else if (StateConstants.PROVIDER.equals(state.state)) {
			return state.providerName != null ? state.providerName : Messages.BreadcrumbNavigation_providers;
		} else if (StateConstants.FORM.equals(state.state)) {
			return state.formName != null ? state.formName : Messages.BreadcrumbNavigation_forms;
		}
		return "";
	}
}
