/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst.view.profileeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.ui.UiDesk;
import ch.gpb.elexis.cst.Activator;

/**
 * this is the base composite for the custom composites that make up the profile editor
 * @author daniel
 *
 */
public abstract class CstComposite extends Composite {
    Color COLOR_RED;
    Color GREEN;
    Color BLACK;
    Color WHITE;
    Color VIOLET;
    Color ORANGE;
    Color BLUE;

    Font titelFont;
    Font fontBold; //$NON-NLS-1$
    Font fontNormal; //$NON-NLS-1$

    Color titelColor = UiDesk.getColorFromRGB("D90A0A");

    Image imgArrowUp = UiDesk.getImage(Activator.IMG_ARROW_UP_NAME);
    Image imgArrowDown = UiDesk.getImage(Activator.IMG_PNG_NAME);
    Image img = UiDesk.getImage(Activator.IMG_PDF_NAME);

    Image imgAction = UiDesk.getImage(Activator.IMG_REMINDER_ACTION_NAME);
    Image imgDecision = UiDesk.getImage(Activator.IMG_REMINDER_DECISION_NAME);
    Image imgReminder = UiDesk.getImage(Activator.IMG_REMINDER_REMINDER_NAME);
    Image imgTrigger = UiDesk.getImage(Activator.IMG_REMINDER_TRIGGER_NAME);
    Image imgHeart1 = UiDesk.getImage(Activator.IMG_HEART_1_NAME);
    Image imgHeart2 = UiDesk.getImage(Activator.IMG_HEART_2_NAME);
    Image imgHeart3 = UiDesk.getImage(Activator.IMG_HEART_3_NAME);

    Image imgHeartA = UiDesk.getImage(Activator.IMG_HEART_A_NAME);
    Image imgHeartB = UiDesk.getImage(Activator.IMG_HEART_B_NAME);
    Image imgHeartC = UiDesk.getImage(Activator.IMG_HEART_C_NAME);
    Image imgHeartD = UiDesk.getImage(Activator.IMG_HEART_D_NAME);
    Image imgHeartE = UiDesk.getImage(Activator.IMG_HEART_E_NAME);

    protected Logger log = LoggerFactory.getLogger(CstComposite.class.getName());

    public CstComposite(Composite parent, int style) {
	super(parent, style);

	// TODO Auto-generated constructor stub
	COLOR_RED = UiDesk.getColorFromRGB("D90A0A");
	GREEN = UiDesk.getColorFromRGB("77C742");
	BLACK = UiDesk.getColorFromRGB("000000");
	WHITE = UiDesk.getColorFromRGB("FFFFFF");
	VIOLET = UiDesk.getColorFromRGB("FF99FF");
	ORANGE = UiDesk.getColorFromRGB("FFCC33");
	BLUE = UiDesk.getColorFromRGB("9999FF");

	//titelFont = UiDesk.getFont(Preferences.USR_SMALLFONT);
	titelFont = UiDesk.getFont("Helvetica", 12, SWT.BOLD); //$NON-NLS-1$
	fontBold = UiDesk.getFont("Helvetica", 12, SWT.BOLD); //$NON-NLS-1$
	fontNormal = UiDesk.getFont("Helvetica", 9, SWT.NORMAL); //$NON-NLS-1$

    }


}
