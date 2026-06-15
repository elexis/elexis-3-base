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
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.mednet.webapi.core.config.MedNetConfig;
import ch.elexis.mednet.webapi.core.messages.Messages;
import ch.elexis.mednet.webapi.ui.Activator;

/**
 * Factory class for creating standardized buttons and toggle buttons for the
 * MedNet UI.
 */
public class ButtonFactory {

	private static final Logger log = LoggerFactory.getLogger(ButtonFactory.class);

	private static final int ICON_SIZE = 32;
	private static final int BUTTON_HEIGHT = 80;

	/**
	 * Creates a toggle button composite that switches between an active and
	 * inactive state.
	 *
	 * @param parent          the parent composite
	 * @param iconPathDefault the path to the default icon
	 * @param iconPathActive  the path to the active icon
	 * @param buttonText      the text displayed on the button
	 * @param customBlue      the background color of the button
	 * @return the created toggle button composite
	 */
	public static Composite createToggleButtonComposite(Composite parent, String iconPathDefault, String iconPathActive,
			String buttonText, Color customBlue) {

		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setBackground(customBlue);
		buttonComposite.setLayout(new GridLayout(1, false));

		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.heightHint = BUTTON_HEIGHT;
		buttonComposite.setLayoutData(gridData);

		Label iconLabel = new Label(buttonComposite, SWT.NONE);
		Image defaultIcon = ImageUtil.getScaledImage(parent.getDisplay(), iconPathDefault, ICON_SIZE, ICON_SIZE,
				iconLabel);
		Image activeIcon = ImageUtil.getScaledImage(parent.getDisplay(), iconPathActive, ICON_SIZE, ICON_SIZE,
				iconLabel);

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
			public void mouseUp(MouseEvent event) {
				isConnected[0] = !isConnected[0];
				if (isConnected[0]) {
					MedNetConfig config = MedNetConfig.load();
					String downloadPath = config.getDownloadPath();

					if (StringUtils.isBlank(downloadPath)) {
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
						parent.getDisplay().asyncExec(() -> {
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

		// General fallback error handler for scheduler issues outside of the click
		// event
		Activator.getInstance().setOnSchedulerError(() -> parent.getDisplay().asyncExec(() -> {
			isConnected[0] = false;
			iconLabel.setImage(defaultIcon);
			textLabel.setText(buttonText);
			textLabel.setToolTipText(Messages.MedNetMainComposite_disconnectedTooltip);
			MessageDialog.openError(parent.getShell(), Messages.MedNetMainComposite_schedulerErrorTitle,
					Messages.MedNetMainComposite_schedulerErrorMessage);
		}));

		buttonComposite.addDisposeListener(event -> {
			if (defaultIcon != null && !defaultIcon.isDisposed()) {
				defaultIcon.dispose();
			}
			if (activeIcon != null && !activeIcon.isDisposed()) {
				activeIcon.dispose();
			}
		});

		return buttonComposite;
	}

	/**
	 * Creates a standard clickable button composite with hover effects.
	 *
	 * @param parent          the parent composite
	 * @param iconPathDefault the path to the default icon
	 * @param iconPathHover   the path to the icon displayed on hover
	 * @param buttonText      the text displayed on the button
	 * @param customBlue      the background color of the button
	 * @param onClickAction   the action to execute when the button is clicked
	 * @return the created button composite
	 */
	public static Composite createButtonComposite(Composite parent, String iconPathDefault, String iconPathHover,
			String buttonText, Color customBlue, Runnable onClickAction) {

		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setBackground(customBlue);
		buttonComposite.setLayout(new GridLayout(1, false));

		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.heightHint = BUTTON_HEIGHT;
		buttonComposite.setLayoutData(gridData);

		Label iconLabel = new Label(buttonComposite, SWT.NONE);
		Image defaultIcon = ImageUtil.getScaledImage(parent.getDisplay(), iconPathDefault, ICON_SIZE, ICON_SIZE,
				iconLabel);
		Image hoverIcon = ImageUtil.getScaledImage(parent.getDisplay(), iconPathHover, ICON_SIZE, ICON_SIZE, iconLabel);

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

		buttonComposite.addDisposeListener(event -> {
			if (defaultIcon != null && !defaultIcon.isDisposed()) {
				defaultIcon.dispose();
			}
			if (hoverIcon != null && !hoverIcon.isDisposed()) {
				hoverIcon.dispose();
			}
		});

		return buttonComposite;
	}
}