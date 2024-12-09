package ch.elexis.mednet.webapi.ui.util;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CompositeEffectHandler {

	public static void addHoverEffect(Composite composite, Label iconLabel, Label textLabel, Image defaultIcon,
			Image hoverIcon, Color customBlue) {
		MouseTrackAdapter hoverEffect = new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				composite.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
				iconLabel.setImage(hoverIcon);
				textLabel.setForeground(customBlue);
				textLabel.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
				iconLabel.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
			}

			@Override
			public void mouseExit(MouseEvent e) {
				composite.setBackground(customBlue);
				iconLabel.setImage(defaultIcon);
				textLabel.setForeground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
				textLabel.setBackground(customBlue);
				iconLabel.setBackground(customBlue);
			}
		};
		composite.addMouseTrackListener(hoverEffect);
		iconLabel.addMouseTrackListener(hoverEffect);
		textLabel.addMouseTrackListener(hoverEffect);
	}

	public static void addHoverEffectBreadcrumbNavigation(Composite composite, Label iconLabel, Label textLabel,
			Image defaultIcon,
			Image hoverIcon, Color customBlue) {
		MouseTrackAdapter hoverEffect = new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				composite.setBackground(customBlue);
				iconLabel.setImage(hoverIcon);
				textLabel.setForeground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
				textLabel.setBackground(customBlue);
				iconLabel.setBackground(customBlue);
			}

			@Override
			public void mouseExit(MouseEvent e) {
				composite.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
				iconLabel.setImage(defaultIcon);
				textLabel.setForeground(customBlue);
				textLabel.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
				iconLabel.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
			}
		};
		composite.addMouseTrackListener(hoverEffect);
		iconLabel.addMouseTrackListener(hoverEffect);
		textLabel.addMouseTrackListener(hoverEffect);
	}

	public static void addPressReleaseEffect(Composite composite, Label iconLabel, Label textLabel, Image hoverIcon,
			Runnable onClickAction, Color customBlue) {
		MouseAdapter pressReleaseEffect = new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				composite.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_GRAY));
				textLabel.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_GRAY));
				iconLabel.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_GRAY));
			}

			@Override
			public void mouseUp(MouseEvent e) {
				if (composite.getBounds().contains(composite.getDisplay().map(null, composite, e.x, e.y))) {
					composite.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
					textLabel.setForeground(customBlue);
					textLabel.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
					iconLabel.setImage(hoverIcon);
				} else {
					composite.setBackground(customBlue);
					iconLabel.setImage(hoverIcon);
					textLabel.setForeground(customBlue);
					textLabel.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
					iconLabel.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
				}
				onClickAction.run();
			}
		};
		composite.addMouseListener(pressReleaseEffect);
		iconLabel.addMouseListener(pressReleaseEffect);
		textLabel.addMouseListener(pressReleaseEffect);
	}

	public static void addHoverEffectWithoutChangingActiveState(Composite composite, Label iconLabel, Label textLabel,
			Image defaultIcon, Color customBlue, boolean[] isActive) {
		MouseTrackAdapter hoverEffect = new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				composite.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
				if (!isActive[0]) {
					iconLabel.setImage(defaultIcon);
				}
				textLabel.setForeground(customBlue);
				textLabel.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
				iconLabel.setBackground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
			}

			@Override
			public void mouseExit(MouseEvent e) {
				composite.setBackground(customBlue);
				if (!isActive[0]) {
					iconLabel.setImage(defaultIcon);
				}
				textLabel.setForeground(composite.getDisplay().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
				textLabel.setBackground(customBlue);
				iconLabel.setBackground(customBlue);
			}
		};
		composite.addMouseTrackListener(hoverEffect);
		iconLabel.addMouseTrackListener(hoverEffect);
		textLabel.addMouseTrackListener(hoverEffect);
	}

}
