package ch.elexis.mednet.webapi.ui.parts;

import java.util.Stack;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.ResourceManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import ch.elexis.core.services.IConfigService;
import ch.elexis.mednet.webapi.core.IMednetAuthUi;
import ch.elexis.mednet.webapi.core.constants.IconConstants;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;
import ch.elexis.mednet.webapi.core.constants.URLConstants;
import ch.elexis.mednet.webapi.core.messages.Messages;
import ch.elexis.mednet.webapi.ui.Activator;
import ch.elexis.mednet.webapi.ui.handler.PatientFetcher;


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
	private boolean isConnected = false;


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
		logoLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		logoLabel.setBackground(customBlue);
		GridData logoGridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		logoLabel.setLayoutData(logoGridData);

		Composite toggleComposite = new Composite(navigationComposite, SWT.NONE);
		toggleComposite.setLayout(new GridLayout(2, false));
		toggleComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		toggleComposite.setBackground(customBlue);

		Image toggelRedImage = ResourceManager.getPluginImage(PreferenceConstants.MEDNET_PLUGIN_STRING,
				IconConstants.ICON_TOGGEL_RED);
		Image toggelGreenImage = ResourceManager.getPluginImage(PreferenceConstants.MEDNET_PLUGIN_STRING,
				IconConstants.ICON_TOGGEL_GREEN);
		Image warnungGelbImage = ResourceManager.getPluginImage(PreferenceConstants.MEDNET_PLUGIN_STRING,
				IconConstants.ICON_WARNUNG_GELB);
		Image warnungGruenImage = ResourceManager.getPluginImage(PreferenceConstants.MEDNET_PLUGIN_STRING,
				IconConstants.ICON_WARNUNG_GRUEN);

		Label toggleLabel = new Label(toggleComposite, SWT.NONE);
		toggleLabel.setBackground(customBlue);
		toggleLabel.setImage(toggelRedImage);

		GridData toggleLabelGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		toggleLabel.setLayoutData(toggleLabelGridData);

		Label warningLabel = new Label(toggleComposite, SWT.NONE);
		warningLabel.setBackground(customBlue);
		warningLabel.setImage(warnungGelbImage);
		warningLabel.setToolTipText("die gesendete Formulare werden in Omnivore NICHT importiert");
		GridData warningLabelGridData = new GridData(SWT.RIGHT, SWT.CENTER, true, false);

		warningLabel.setLayoutData(warningLabelGridData);
		toggleLabel.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseUp(MouseEvent e) {
		        isConnected = !isConnected;

		        if (isConnected) {
		            // Überprüfen, ob ein Pfad hinterlegt ist
		            String downloadPath = getDownloadStore();
		            if (downloadPath == null || downloadPath.trim().isEmpty()) {
		                // Setze auf Grün und zeige Warnung an
		                toggleLabel.setImage(toggelGreenImage);
		                warningLabel.setImage(warnungGelbImage);
						warningLabel.setToolTipText(Messages.MedNetMainComposite_noPathConfigured);

		                // Zeige Warnung und setze nach Bestätigung auf Rot
		                MessageDialog.openWarning(
		                        toggleLabel.getShell(),
								Messages.MedNetMainComposite_noPathWarningTitle,
								Messages.MedNetMainComposite_noPathWarningMessage
		                );

		                // Verbindung deaktivieren nach Warnung
		                toggleLabel.setImage(toggelRedImage);
		                warningLabel.setImage(warnungGelbImage);
						warningLabel.setToolTipText(Messages.MedNetMainComposite_disconnectedTooltip);
		                isConnected = false;
		                return;
		            }

		            // Verbindung aktivieren
		            toggleLabel.setImage(toggelGreenImage);
		            warningLabel.setImage(warnungGruenImage);
					warningLabel.setToolTipText(Messages.MedNetMainComposite_connectedTooltip);
		            Activator.getInstance().startScheduler();
		        } else {
		            // Verbindung deaktivieren
		            toggleLabel.setImage(toggelRedImage);
		            warningLabel.setImage(warnungGelbImage);
					warningLabel.setToolTipText(Messages.MedNetMainComposite_disconnectedTooltip);
		            Activator.getInstance().stopScheduler();
		        }
		    }
		});





		createButtonComposite(navigationComposite,
				IconConstants.ICON_BARCODE_WHITE, IconConstants.ICON_BARCODE_BLUE,
				Messages.MedNetMainComposite_formWithPatientData, customBlue, this::connect
		);
		createButtonComposite(navigationComposite, IconConstants.ICON_GROUP_WHITE,
				IconConstants.ICON_GROUP_BLUE, Messages.MedNetMainComposite_showPatients, customBlue,
				() -> openMednetDocuments(URLConstants.URL_PATIENTS)
		);
		createButtonComposite(navigationComposite, IconConstants.ICON_BARS_WHITE,
				IconConstants.ICON_BARS_BLUE, Messages.MedNetMainComposite_tasks, customBlue,
				() -> openMednetDocuments(URLConstants.URL_TASKS));
		createButtonComposite(navigationComposite,
				IconConstants.ICON_GOOGLE_DOCS_WHITE, IconConstants.ICON_GOOGLE_DOCS_BLUE,
				Messages.MedNetMainComposite_documents, customBlue,
				() -> openMednetDocuments(URLConstants.URL_DOCUMENTS));
		createButtonComposite(navigationComposite,
				IconConstants.ICON_ADDRESS_BOOK_REGULAR_WHITE, IconConstants.ICON_ADDRESS_BOOK_REGULAR_BLUE,
				Messages.MedNetMainComposite_therapy, customBlue, () -> openMednetDocuments(URLConstants.URL_THERAPY));
		createButtonComposite(navigationComposite, IconConstants.ICON_IMPORT_WHITE,
				IconConstants.ICON_IMPORT_BLUE, Messages.MedNetMainComposite_forms, customBlue,
				this::showSubmittedForms);
		medNetViewerComposite = new Composite(parent, SWT.NONE);
		GridData medNetViewerCompositeData = new GridData(SWT.FILL, SWT.FILL, true, true);
		medNetViewerComposite.setLayoutData(medNetViewerCompositeData);
		medNetViewerComposite.setLayout(new GridLayout(1, false));

		showInitialLogo();
		parent.addDisposeListener(e -> {
			customBlue.dispose();

			if (!imageLabel.isDisposed()) {
				imageLabel.dispose();
			}
		});

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

	private String getDownloadStore() {
		String downloadPath = "";

		try {
			BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();

			if (context != null) {
				ServiceReference<IConfigService> serviceReference = context.getServiceReference(IConfigService.class);

				if (serviceReference != null) {
					IConfigService configService = context.getService(serviceReference);

					if (configService != null) {
						downloadPath = configService.getActiveUserContact(PreferenceConstants.MEDNET_DOWNLOAD_PATH, "");
						return downloadPath;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return downloadPath;
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

	private void loadCustomers() {
		clearPreviousData();

		customerComposite = new CustomerComposite(medNetViewerComposite, this::loadProviders);
		customerComposite.show();
		medNetViewerComposite.layout();
	}

	private void showSubmittedForms() {
		clearPreviousData();

		submittedFormsComposite = new SubmittedFormsComposite(medNetViewerComposite);
		submittedFormsComposite.showSubmittedForms();
		medNetViewerComposite.layout();

	}

	private Image getScaledImage(Display display, String path, int width, int height, Label label) {
		Image originalImage = ResourceManager.getPluginImage(PreferenceConstants.MEDNET_PLUGIN_STRING, path);
		if (originalImage == null || originalImage.isDisposed()) {
			return null;
		}
		ImageData originalImageData = originalImage.getImageData();
		if (width <= 1 || height <= 1) {
			width = originalImageData.width;
			height = originalImageData.height;
	    }
	    ImageData scaledImageData = originalImageData.scaledTo(width, height);
	    Image scaledImage = new Image(display, scaledImageData);
	    label.addDisposeListener(e -> {
	        if (!scaledImage.isDisposed()) {
	            scaledImage.dispose();
	        }
	    });

	    return scaledImage;
	}

	private void loadProviders(Integer customerId) {
		clearPreviousData();

		providerComposite = new ProviderComposite(medNetViewerComposite, this::loadForms, patientFetcher);
		providerComposite.show(customerId);
		medNetViewerComposite.layout();
		navigationStack.push(new NavigationState("CUSTOMER", customerId, null)); //$NON-NLS-1$
	}

	private void loadForms(Integer providerId) {
		if (!navigationStack.isEmpty()) {
			NavigationState previousState = navigationStack.peek();
			if (previousState.customerId != null) {
				clearPreviousData();

				formComposite = new FormComposite(medNetViewerComposite);
				formComposite.show(previousState.customerId, providerId);
				medNetViewerComposite.layout();
				navigationStack
						.push(new NavigationState("PROVIDER", previousState.customerId, providerId)); //$NON-NLS-1$
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

	private void addHoverEffect(Composite composite, Label iconLabel, Label textLabel, Image defaultIcon,
			Image hoverIcon, Color customBlue) {
		MouseTrackAdapter hoverEffect = new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				iconLabel.setImage(hoverIcon);
				textLabel.setForeground(customBlue);
				textLabel.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				iconLabel.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			}
			@Override
			public void mouseExit(MouseEvent e) {
				composite.setBackground(customBlue);
				iconLabel.setImage(defaultIcon);
				textLabel.setForeground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				textLabel.setBackground(customBlue);
				iconLabel.setBackground(customBlue);
			}
		};
		composite.addMouseTrackListener(hoverEffect);
		iconLabel.addMouseTrackListener(hoverEffect);
		textLabel.addMouseTrackListener(hoverEffect);
	}

	private void addPressReleaseEffect(Composite composite, Label iconLabel, Label textLabel, Image hoverIcon,
			Runnable onClickAction, Color customBlue) {
		MouseAdapter pressReleaseEffect = new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_GRAY));
				textLabel.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_GRAY));
				iconLabel.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_GRAY));
			}

			@Override
			public void mouseUp(MouseEvent e) {
				if (composite.getBounds().contains(composite.getDisplay().map(null, composite, e.x, e.y))) {
					composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
					textLabel.setForeground(customBlue);
					textLabel.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
					iconLabel.setImage(hoverIcon);
				} else {
					composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
					iconLabel.setImage(hoverIcon);
					textLabel.setForeground(customBlue);
					textLabel.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
					iconLabel.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				}
				onClickAction.run();
			}
		};
		composite.addMouseListener(pressReleaseEffect);
		iconLabel.addMouseListener(pressReleaseEffect);
		textLabel.addMouseListener(pressReleaseEffect);
	}


	private Composite createButtonComposite(Composite parent, String iconPathDefault, String iconPathHover,
			String buttonText, Color customBlue, Runnable onClickAction) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setBackground(customBlue);
		buttonComposite.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.heightHint = 80;
		buttonComposite.setLayoutData(gridData);
		Label iconLabel = new Label(buttonComposite, SWT.NONE);
		Image defaultIcon = getScaledImage(parent.getDisplay(), iconPathDefault, 32, 32, iconLabel);
		Image hoverIcon = getScaledImage(parent.getDisplay(), iconPathHover, 32, 32, iconLabel);
		iconLabel.setImage(defaultIcon);
		iconLabel.setBackground(customBlue);
		iconLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		Label textLabel = new Label(buttonComposite, SWT.CENTER | SWT.WRAP);
		textLabel.setText(buttonText);
		textLabel.setBackground(customBlue);
		textLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		textLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		addHoverEffect(buttonComposite, iconLabel, textLabel, defaultIcon, hoverIcon, customBlue);
		addPressReleaseEffect(buttonComposite, iconLabel, textLabel, hoverIcon, onClickAction, customBlue);
		buttonComposite.addDisposeListener(e -> {
			if (defaultIcon != null && !defaultIcon.isDisposed()) {
				defaultIcon.dispose();
			}
			if (hoverIcon != null && !hoverIcon.isDisposed()) {
				hoverIcon.dispose();
			}
		});
		return buttonComposite;
	}

	private class NavigationState {
		String state;
		Integer customerId;
		Integer providerId;

		NavigationState(String state, Integer customerId, Integer providerId) {
			this.state = state;
			this.customerId = customerId;
			this.providerId = providerId;
		}
	}
}
