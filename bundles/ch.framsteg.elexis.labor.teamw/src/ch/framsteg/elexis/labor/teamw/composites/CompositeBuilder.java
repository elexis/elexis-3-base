/*******************************************************************************
 * Copyright 2024 Framsteg GmbH / olivier.debenath@framsteg.ch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ch.framsteg.elexis.labor.teamw.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class CompositeBuilder {

	public static Composite createStandardComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		return composite;
	}

	public static Group createGroup(Composite composite, int numColumns, String groupName) {

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns;

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = false;
		gridData.verticalAlignment = SWT.FILL;

		Group group = new Group(composite, SWT.BORDER);
		group.setText(groupName);
		group.setLayout(gridLayout);
		group.setLayoutData(gridData);

		return group;
	}

	public static void createActivatedLine(Composite composite, String labelText) {

		GridData textData = new GridData();
		textData.grabExcessHorizontalSpace = true;
		textData.horizontalAlignment = SWT.FILL;
		textData.widthHint = 500;
		textData.minimumWidth = 100;

		GridData labelData = new GridData();
		labelData.grabExcessHorizontalSpace = true;
		labelData.horizontalAlignment = SWT.FILL;
		labelData.widthHint = 150;
		labelData.minimumWidth = 100;

		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(labelData);
		label.setText(labelText);
		Text text = new Text(composite, SWT.BORDER);
		text.setLayoutData(textData);

	}

	public static void createDeactivatedLine(Composite composite, String labelText) {

		GridData textData = new GridData();
		textData.grabExcessHorizontalSpace = true;
		textData.horizontalAlignment = SWT.FILL;
		textData.widthHint = 500;
		textData.minimumWidth = 100;

		GridData labelData = new GridData();
		labelData.grabExcessHorizontalSpace = true;
		labelData.widthHint = 150;
		labelData.minimumWidth = 100;

		Label label = new Label(composite, SWT.FILL);
		label.setLayoutData(labelData);
		label.setText(labelText);
		Text text = new Text(composite, SWT.BORDER);
		text.setLayoutData(textData);
		text.setEnabled(false);

	}

	public static void createPasswordLine(Composite composite, String labelText) {

		GridData textData = new GridData();
		textData.grabExcessHorizontalSpace = true;
		textData.horizontalAlignment = SWT.FILL;
		textData.widthHint = 500;
		textData.minimumWidth = 100;

		GridData labelData = new GridData();
		labelData.grabExcessHorizontalSpace = true;
		labelData.horizontalAlignment = SWT.FILL;
		labelData.widthHint = 150;
		labelData.minimumWidth = 100;

		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(labelData);
		label.setText(labelText);
		Text text = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		text.setLayoutData(textData);

	}
}
