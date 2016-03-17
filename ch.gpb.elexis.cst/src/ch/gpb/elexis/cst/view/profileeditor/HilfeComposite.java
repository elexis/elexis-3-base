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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ch.gpb.elexis.cst.preferences.Messages;


public class HilfeComposite extends CstComposite {

    public HilfeComposite(Composite parent) {
	super(parent, SWT.NONE);

	//SWTResourceManager.
	GridLayout gridLayout = new GridLayout(2, false);
	setLayout(gridLayout);

	Label lblHilfeTitel = new Label(this, SWT.NONE);
	lblHilfeTitel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
	lblHilfeTitel.setText(Messages.HilfeComposite_hilfe_titel);
	lblHilfeTitel.setFont(fontBold);
	lblHilfeTitel.setForeground(titelColor);

	createLayout(this);

	Label lblNewLabel = new Label(this, SWT.NONE);
	lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
	lblNewLabel.setText(Messages.HilfeComposite_hilfe_text_plugin);
	lblNewLabel.setFont(fontNormal);

	Label lblTitelCstGroups = new Label(this, SWT.NONE);
	lblTitelCstGroups.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
	lblTitelCstGroups.setText(Messages.HilfeComposite_hilfe_titel_cstgroups);
	lblTitelCstGroups.setFont(fontNormal);
	lblTitelCstGroups.setForeground(titelColor);

	Label lblTextCstGroups = new Label(this, SWT.NONE);
	lblTextCstGroups.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
	lblTextCstGroups.setText(Messages.HilfeComposite_hilfe_text_cstgroups);
	lblTextCstGroups.setFont(fontNormal);

	Label lblTitelCstProfiles = new Label(this, SWT.NONE);
	lblTitelCstProfiles.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
	lblTitelCstProfiles.setText(Messages.HilfeComposite_hilfe_titel_cstprofiles);
	lblTitelCstProfiles.setFont(fontNormal);
	lblTitelCstProfiles.setForeground(titelColor);
	//lblTitelCstProfiles.setImage(UiDesk.getImage(Activator.IMG_CSTPROFILE_NAME));

	Label lblTextCstProfiles = new Label(this, SWT.NONE);
	lblTextCstProfiles.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
	lblTextCstProfiles.setText(Messages.HilfeComposite_hilfe_text_cstprofiles);
	lblTextCstProfiles.setFont(fontNormal);

	Label lblTitelCstDouments = new Label(this, SWT.NONE);
	lblTitelCstDouments.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
	lblTitelCstDouments.setText(Messages.HilfeComposite_hilfe_titel_cstdocuments);
	lblTitelCstDouments.setForeground(titelColor);
	lblTitelCstDouments.setFont(fontNormal);

	Label lblTextCstDouments = new Label(this, SWT.NONE);
	lblTextCstDouments.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
	lblTextCstDouments.setText(Messages.HilfeComposite_hilfe_text_cstdocuments);
	lblTextCstDouments.setFont(fontNormal);

    }

    // dynamic Layout elements
    private void createLayout(Composite parent) {
    }


    
}
