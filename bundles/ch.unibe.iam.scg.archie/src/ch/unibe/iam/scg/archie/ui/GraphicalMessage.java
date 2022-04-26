/*******************************************************************************
 * Copyright (c) 2008 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *******************************************************************************/
package ch.unibe.iam.scg.archie.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * <p>
 * Messages with a graphical symbol.
 * </p>
 *
 * $Id: GraphicalMessage.java 747 2009-07-23 09:14:53Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class GraphicalMessage extends Composite {

	private Image image;
	private String message;
	private Composite parent;

	/**
	 * @param parent  Composite
	 * @param image   Image
	 * @param message String
	 */
	public GraphicalMessage(final Composite parent, final Image image, final String message) {
		super(parent, SWT.NONE);

		this.parent = parent;
		this.image = image;
		this.message = message;

		this.initialize();
	}

	private void initialize() {
		GridData data = new GridData();
		data.horizontalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;

		// holds the loading image and message
		this.setBackground(this.parent.getBackground());
		this.setLayout(new GridLayout());
		this.setLayoutData(data);

		// loading image
		Label imageLabel = new Label(this, SWT.CENTER);

		// set the label image
		imageLabel.setImage(this.image);
		imageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		imageLabel.setBackground(this.parent.getBackground());

		// loading message
		Label messageLabel = new Label(this, SWT.WRAP | SWT.CENTER);
		messageLabel.setBackground(this.parent.getBackground());
		messageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		messageLabel.setText(this.message);
	}

}