package ch.elexis.mednet.webapi.ui.util;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import ch.elexis.core.services.IConfigService;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;
import ch.elexis.mednet.webapi.core.messages.Messages;
import ch.elexis.mednet.webapi.ui.Activator;

public class ButtonFactory {

	public static Composite createToggleButtonComposite(Composite parent, String iconPathDefault, String iconPathActive,
			String buttonText, Color customBlue) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setBackground(customBlue);
		buttonComposite.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.heightHint = 80;
		buttonComposite.setLayoutData(gridData);

		Label iconLabel = new Label(buttonComposite, SWT.NONE);
		Image defaultIcon = ImageUtil.getScaledImage(parent.getDisplay(), iconPathDefault, 32, 32, iconLabel);
		Image activeIcon = ImageUtil.getScaledImage(parent.getDisplay(), iconPathActive, 32, 32, iconLabel);
		iconLabel.setImage(defaultIcon);
		iconLabel.setBackground(customBlue);
		iconLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

		Label textLabel = new Label(buttonComposite, SWT.CENTER | SWT.WRAP);
		textLabel.setText(buttonText);
		textLabel.setBackground(customBlue);
		textLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		textLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

		final boolean[] isConnected = { false };

		CompositeEffectHandler.addHoverEffectWithoutChangingActiveState(buttonComposite, iconLabel, textLabel,
				defaultIcon, customBlue, isConnected);

		MouseAdapter clickListener = new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				isConnected[0] = !isConnected[0];
				if (isConnected[0]) {
					String downloadPath = getDownloadStore();
					if (downloadPath == null || downloadPath.trim().isEmpty()) {
						iconLabel.setImage(defaultIcon);
						textLabel.setText(buttonText);
						MessageDialog.openWarning(buttonComposite.getShell(),
								Messages.MedNetMainComposite_noPathWarningTitle,
								Messages.MedNetMainComposite_noPathWarningMessage);
						isConnected[0] = false;
						return;
					}
					iconLabel.setImage(activeIcon);
					textLabel.setText(Messages.MedNetMainComposite_deactivateImport);
					textLabel.setToolTipText(Messages.MedNetMainComposite_connectedTooltip);
					Activator.getInstance().startScheduler();
					Activator.getInstance().setOnSchedulerError(() -> {
						Display.getDefault().asyncExec(() -> {
							iconLabel.setImage(defaultIcon);
							textLabel.setText(buttonText);
							textLabel.setToolTipText(Messages.MedNetMainComposite_schedulerErrorMessage);
							isConnected[0] = false;
						});
					});
				} else {
					iconLabel.setImage(defaultIcon);
					textLabel.setText(buttonText);
					textLabel.setToolTipText(Messages.MedNetMainComposite_disconnectedTooltip);
					Activator.getInstance().stopScheduler();
				}
			}
		};

		buttonComposite.addMouseListener(clickListener);
		iconLabel.addMouseListener(clickListener);
		textLabel.addMouseListener(clickListener);

		Activator.getInstance().setOnSchedulerError(() -> parent.getDisplay().asyncExec(() -> {
			isConnected[0] = false;
			iconLabel.setImage(defaultIcon);
			textLabel.setText(buttonText);
			textLabel.setToolTipText(Messages.MedNetMainComposite_disconnectedTooltip);
			MessageDialog.openError(parent.getShell(), Messages.MedNetMainComposite_schedulerErrorTitle,
					Messages.MedNetMainComposite_schedulerErrorMessage);
		}));

		buttonComposite.addDisposeListener(e -> {
			if (defaultIcon != null && !defaultIcon.isDisposed()) {
				defaultIcon.dispose();
			}
			if (activeIcon != null && !activeIcon.isDisposed()) {
				activeIcon.dispose();
			}
		});

		return buttonComposite;
	}

	public static Composite createButtonComposite(Composite parent, String iconPathDefault, String iconPathHover,
			String buttonText, Color customBlue, Runnable onClickAction) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setBackground(customBlue);
		buttonComposite.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.heightHint = 80;
		buttonComposite.setLayoutData(gridData);
		Label iconLabel = new Label(buttonComposite, SWT.NONE);
		Image defaultIcon = ImageUtil.getScaledImage(parent.getDisplay(), iconPathDefault, 32, 32, iconLabel);
		Image hoverIcon = ImageUtil.getScaledImage(parent.getDisplay(), iconPathHover, 32, 32, iconLabel);
		iconLabel.setImage(defaultIcon);
		iconLabel.setBackground(customBlue);
		iconLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		Label textLabel = new Label(buttonComposite, SWT.CENTER | SWT.WRAP);
		textLabel.setText(buttonText);
		textLabel.setBackground(customBlue);
		textLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		textLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		CompositeEffectHandler.addHoverEffect(buttonComposite, iconLabel, textLabel, defaultIcon, hoverIcon,
				customBlue);
		CompositeEffectHandler.addPressReleaseEffect(buttonComposite, iconLabel, textLabel, hoverIcon, onClickAction,
				customBlue);
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

	private static String getDownloadStore() {
		String downloadPath = StringUtils.EMPTY;
		try {
			BundleContext context = FrameworkUtil.getBundle(ButtonFactory.class).getBundleContext();
			if (context != null) {
				ServiceReference<IConfigService> serviceReference = context.getServiceReference(IConfigService.class);
				if (serviceReference != null) {
					IConfigService configService = context.getService(serviceReference);
					if (configService != null) {
						downloadPath = configService.getActiveUserContact(PreferenceConstants.MEDNET_DOWNLOAD_PATH,
								StringUtils.EMPTY);
						return downloadPath;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return downloadPath;
	}
}
