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

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.elexis.befunde.Messwert;
import ch.gpb.elexis.cst.Messages;
import ch.rgw.tools.StringTool;

public class BefundSelectionComposite extends CstComposite {

	public BefundSelectionComposite(Composite parent) {
		super(parent, SWT.NONE);

		GridLayout gridLayout = new GridLayout(2, true);
		setLayout(gridLayout);

		Label lblAuswahlBefundparameter = new Label(this, SWT.NONE);
		lblAuswahlBefundparameter.setText("Auswahl Befundparameter");

		Label lblSeparator = new Label(this, SWT.NONE);
		lblSeparator.setText("Separator");

		createLayout(this);
		Label lblHint = new Label(this, SWT.NONE);
		lblHint.setText(Messages.Cst_Text_tooltip_befundauswahl);
		GridData gdHint = new GridData();
		gdHint.horizontalSpan = 2;
		gdHint.verticalIndent = 40;
	}

	// dynamic Layout elements
	private void createLayout(Composite parent) {

		try {
			Messwert setup = Messwert.getSetup();

			Map<String, String> hash = setup.getMap(Messwert.FLD_BEFUNDE);

			String names = (String) hash.get(Messwert.HASH_NAMES);
			if (!StringTool.isNothing(names)) {
				for (String sNameBefund : names.split(Messwert.SETUP_SEPARATOR)) {

					String fields = (String) hash.get(sNameBefund + Messwert._FIELDS);
					// Print the Befund name only if it has Fields
					if (fields != null) {

						Label lblTitle = new Label(parent, SWT.NONE);
						lblTitle.setText(sNameBefund);
						GridData gdTitle = new GridData();
						gdTitle.horizontalSpan = 2;

						lblTitle.setLayoutData(gdTitle);
						// TODO: dispose color
						// lblTitle.setForeground(new Color(getDisplay(), 255, 20, 40));
						lblTitle.setForeground(COLOR_RED);
						String[] mNames = fields.split(Messwert.SETUP_SEPARATOR);

						for (int i = 0; i < mNames.length; i++) {
							Button bField = new Button(parent, SWT.CHECK);

							bField.setText((mNames[i].split(Messwert.SETUP_CHECKSEPARATOR))[0]);
							bField.setData(sNameBefund);

							Text tSep2 = new Text(parent, SWT.SINGLE);

							String sSep2 = "separator_" + mNames[i].split(Messwert.SETUP_CHECKSEPARATOR)[0];
							tSep2.setData(sSep2);
							tSep2.setToolTipText(Messages.Cst_Text_tooltip_befund_separator);

						}

					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Get the selections from the gui
	 *
	 * @param mAuswahl the existing Befunde Auswahl map
	 * @return the updated Befunde map
	 */
	public Map<Object, Object> getSelection(Map<Object, Object> mAuswahl) {
		Map<String, String> hash;

		Messwert setup = Messwert.getSetup();
		hash = setup.getMap(Messwert.FLD_BEFUNDE);
		String names = (String) hash.get(Messwert.HASH_NAMES);
		if (!StringTool.isNothing(names)) {

			for (String sNameBefund : names.split(Messwert.SETUP_SEPARATOR)) {

				String fields = (String) hash.get(sNameBefund + Messwert._FIELDS);
				if (fields == null) {
					continue;
				}

				String[] mNames = fields.split(Messwert.SETUP_SEPARATOR);
				for (int i = 0; i < mNames.length; i++) {

					for (Control control : this.getChildren()) {

						if (control instanceof Button) {
							if (((Button) control).getText()
									.equals(mNames[i].split(Messwert.SETUP_CHECKSEPARATOR)[0])) {
								boolean selected = ((Button) control).getSelection();

								// mAuswahl.put(mNames[i].split(Messwert.SETUP_CHECKSEPARATOR)[0], new
								// Boolean(selected));

								if (selected) {
									mAuswahl.put(mNames[i].split(Messwert.SETUP_CHECKSEPARATOR)[0],
											new String(sNameBefund));

								} else {
									mAuswahl.put(mNames[i].split(Messwert.SETUP_CHECKSEPARATOR)[0],
											new String("false"));

								}

							}
						}

						if (control instanceof Text) {
							String txtData = ((Text) control).getData().toString();

							String sepKey = "separator_" + mNames[i].split(Messwert.SETUP_CHECKSEPARATOR)[0];

							if (txtData.equals(sepKey)) {
								String separator = ((Text) control).getText();
								mAuswahl.put(((Text) control).getData().toString(), separator);
							}
						}

					}
				}

			}

		}
		return mAuswahl;
	}

	/**
	 * Set the buttons selected according to the map passed as parameter
	 *
	 * @param mapAuswahl
	 */
	public void setSelection(Map<String, Object> mapAuswahl) {

		for (Control control : this.getChildren()) {

			if (control instanceof Button) {
				((Button) control).setSelection(false);
				Iterator<String> itKeys = mapAuswahl.keySet().iterator();
				while (itKeys.hasNext()) {
					Object key = (Object) itKeys.next();
					if (key.equals(((Button) control).getText())) {

						// ((Button) control).setSelection(((Boolean)
						// mapAuswahl.get(key)).booleanValue());
						if (mapAuswahl.get(key).toString().equals("false")) {
							((Button) control).setSelection(false);
						} else {
							((Button) control).setSelection(true);
						}

					}
				}
			}

			if (control instanceof Text) {
				((Text) control).setText(StringUtils.EMPTY);
				Iterator<String> itKeys = mapAuswahl.keySet().iterator();
				while (itKeys.hasNext()) {
					Object key = (Object) itKeys.next();

					if (key.equals(((Text) control).getData())) {
						((Text) control).setText(((String) mapAuswahl.get(key)));
					}
				}

			}

		}

	}

}
