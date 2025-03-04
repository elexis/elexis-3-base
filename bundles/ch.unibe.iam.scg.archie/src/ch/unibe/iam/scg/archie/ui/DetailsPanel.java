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

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.actions.NewStatisticsAction;
import ch.unibe.iam.scg.archie.controller.ProviderManager;
import ch.unibe.iam.scg.archie.model.AbstractDataProvider;

/**
 * <p>
 * Is displayed in the sidebar. Shows description and parameters for chosen
 * statistic. Statistics can be run or canceled here.
 * </p>
 *
 * $Id: DetailsPanel.java 747 2009-07-23 09:14:53Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class DetailsPanel extends Composite implements Observer {

	private Text description;

	private ParametersPanel parameters;

	private ActionContributionItem startButton;

	private Button cancelButton;

	private NewStatisticsAction action;

	/**
	 *
	 * @param parent
	 * @param style
	 */
	public DetailsPanel(Composite parent, int style) {
		super(parent, style);

		// register as observer
		ProviderManager.getInstance().addObserver(this);

		// set layout
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 5;

		this.setLayout(layout);
		this.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Add the statistics description
		this.description = new Text(this, SWT.MULTI | SWT.WRAP);
		this.description.setEditable(false);
		this.description.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.description.setBackground(parent.getBackground());

		// add parameters
		this.parameters = new ParametersPanel(this, SWT.NONE);
		this.parameters.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// add button container
		Composite buttonContainer = new Composite(this, SWT.NONE);
		GridLayout buttonContainerLayout = new GridLayout();
		buttonContainerLayout.numColumns = 2;
		buttonContainerLayout.marginWidth = 0;
		buttonContainerLayout.marginHeight = 0;
		buttonContainer.setLayout(buttonContainerLayout);
		buttonContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// add buttons
		this.createQueryButton(buttonContainer);
		this.createCancelButton(buttonContainer);

		// Set initial state
		this.reset();
	}

	/**
	 * Create the new statistic button,
	 *
	 * @param parent
	 */
	private void createQueryButton(Composite parent) {
		// add the new query action and a button for it
		this.action = new NewStatisticsAction(this.parameters);

		this.startButton = new ActionContributionItem(this.action);
		this.startButton.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		this.startButton.fill(parent);
	}

	/**
	 * Create the cancel button.
	 *
	 * @param parent
	 */
	private void createCancelButton(Composite parent) {
		this.cancelButton = new Button(parent, SWT.NONE);
		this.cancelButton.setText(Messages.CANCEL);
		this.cancelButton.setImage(ArchieActivator.getImage(ArchieActivator.IMG_CANCEL));
		this.cancelButton.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				ProviderManager manager = ProviderManager.getInstance();
				if (manager.hasProvider()) {
					manager.getProvider().cancel();
				}
			}
		});
	}

	/**
	 * Resets the details panel and it's components to a start state, same as if no
	 * statistic were selected yet.
	 */
	public void reset() {
		this.description.setText(Messages.EMPTY_PROVIDER_DESCRIPTION);
		this.action.setEnabled(false);

		// dispose parameter
		for (Control child : this.parameters.getChildren()) {
			child.dispose();
		}

		this.layout();
	}

	/**
	 * Sets all children enabled according to the boolean passed to this function.
	 *
	 * @param enabled True if children should be enabled, false else.
	 */
	@Override
	public void setEnabled(boolean enabled) {
		this.description.setEnabled(enabled);
		this.parameters.setEnabled(enabled);

		// cancel button has reversed status
		this.setCancelButtonEnabled(!enabled);
	}

	/**
	 * Sets the cancel button either enabled or disabled.
	 *
	 * @param enabled State to set the cancel button to.
	 */
	public void setCancelButtonEnabled(boolean enabled) {
		this.cancelButton.setEnabled(enabled);
	}

	/**
	 * Sets the main query action enabled or disabled.
	 *
	 * @param enabled State to set the main action to.
	 */
	public void setActionEnabled(boolean enabled) {
		this.action.setEnabled(enabled);
	}

	/**
	 * Attache a property change listener to this object. This actually forwards the
	 * attachment to this panel's new query action.
	 *
	 * @param listener A property change listener.
	 */
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		this.action.addPropertyChangeListener(listener);
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable observable, Object arg) {
		if (ProviderManager.getInstance().hasProvider() && !this.isDisposed()) {
			AbstractDataProvider provider = ProviderManager.getInstance().getProvider();

			// set provider information in this panel
			this.description.setText(provider.getDescription());
			this.description.pack(true);

			// update parameters panel
			this.parameters.updateParameterList(provider);
			this.layout();
		}
	}
}