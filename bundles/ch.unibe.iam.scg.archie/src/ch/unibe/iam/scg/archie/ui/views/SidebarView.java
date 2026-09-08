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
package ch.unibe.iam.scg.archie.ui.views;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.model.IUser;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.unibe.iam.scg.archie.ArchieActivator;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.acl.ArchieACL;
import ch.unibe.iam.scg.archie.actions.NewStatisticsAction;
import ch.unibe.iam.scg.archie.controller.ProviderManager;
import ch.unibe.iam.scg.archie.model.AbstractDataProvider;
import ch.unibe.iam.scg.archie.preferences.PreferenceConstants;
import ch.unibe.iam.scg.archie.ui.DetailsPanel;
import ch.unibe.iam.scg.archie.ui.widgets.ContainsContentProposalProvider;
import ch.unibe.iam.scg.archie.ui.widgets.FavoriteStatistics;
import jakarta.inject.Inject;

/**
 * <p>
 * In this View a user can chose a statistic, set options for it and run it.
 * </p>
 *
 * $Id: SidebarView.java 774 2010-01-29 05:47:10Z gerry.weirich $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 774 $
 */
public class SidebarView extends ViewPart implements IPropertyChangeListener {

	/**
	 * ID of this view.
	 */
	public static final String ID = ArchieActivator.PLUGIN_ID + ".ui.views.StatisticsSidebarView"; //$NON-NLS-1$

	protected TableCombo list;

	protected DetailsPanel details;

	private Button favoriteButton;

	private final IPreferenceStore favoritePreferenceStore = new ConfigServicePreferenceStore(Scope.USER);

	private final Set<String> favoriteTitles = new HashSet<>();

	private boolean refreshingStatistics;

	@Inject
	void activeUser(@Optional IUser user) {
		Display.getDefault().asyncExec(() -> {
			if (user != null) {
				loadFavoriteTitles();
				if (list != null && !list.isDisposed()) {
					String selectedTitle = isValidProviderTitle(list.getText()) ? list.getText() : null;
					refreshAvailableStatistics(selectedTitle);
				}

				// Set enabled according to ACL.
				boolean accessEnabled = ArchieACL.userHasAccess();
				setEnabled(accessEnabled);

				// If a user has no access at all, disable everything.
				if (!accessEnabled) {
					details.setCancelButtonEnabled(accessEnabled);
					details.setActionEnabled(accessEnabled);
					// If there's a provider currently selected, enable the
					// query
					// action.
				} else if (list.getSelectionIndex() != -1) {
					details.setActionEnabled(accessEnabled);
				}

				// Cancel any previous job if running.
				if (ProviderManager.getInstance().hasProvider()) {
					ProviderManager.getInstance().getProvider().cancel();
				}
			}
		});
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(final Composite parent) {
		// create a new container for sidebar controls
		Composite container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		// Create a simple field for auto complete.
		Group availableStatistics = new Group(container, SWT.NONE);
		availableStatistics.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		availableStatistics.setLayout(new GridLayout(2, false));
		availableStatistics.setText(Messages.STATISTICS_LIST_TITLE);

		// Create a searchable statistics dropdown.
		this.list = new TableCombo(availableStatistics, SWT.BORDER);
		this.list.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		this.list.setClosePopupAfterSelection(true);

		this.favoriteButton = new Button(availableStatistics, SWT.TOGGLE);
		this.favoriteButton.setToolTipText("Ausgewählte Statistik als Favorit markieren"); //$NON-NLS-1$
		this.favoriteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				toggleFavorite();
			}
		});

		this.loadFavoriteTitles();
		this.refreshAvailableStatistics(null);

		// add listeners
		this.list.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String title = SidebarView.this.list.getText();

				if (SidebarView.this.isValidProviderTitle(title)) {
					AbstractDataProvider provider = ArchieActivator.getInstance().getProviderTable().get(title);
					ProviderManager.getInstance().setProvider(provider);
				} else {
					SidebarView.this.details.reset();
				}
				if (!SidebarView.this.refreshingStatistics && !SidebarView.this.isValidProviderTitle(title)) {
					SidebarView.this.refreshAvailableStatistics(null);
					if (!title.isEmpty()) {
						SidebarView.this.list.setTableVisible(true);
					}
				}
				SidebarView.this.updateFavoriteButton();
			}
		});
		this.list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String title = SidebarView.this.list.getText();
				if (!SidebarView.this.refreshingStatistics && SidebarView.this.isValidProviderTitle(title)) {
					SidebarView.this.refreshAvailableStatistics(title);
				}
			}
		});

		// Add parameters group.
		Group statisticParameters = new Group(container, SWT.NONE);
		statisticParameters.setLayoutData(new GridData(GridData.FILL_BOTH));
		statisticParameters.setLayout(layout);
		statisticParameters.setText(Messages.STATISTIC_PARAMETERS_TITLE);

		// Add details panel containing details and parameters.
		this.details = new DetailsPanel(statisticParameters, SWT.NONE);
		this.details.addPropertyChangeListener(this);

		// Disable by default if user has no access rights.
		this.setEnabled(ArchieACL.userHasAccess());
	}

	/**
	 * Checks whether the title passed to this function is a valid data provider we
	 * have in the statistics table.
	 *
	 * @param title Provider title.
	 * @return True if the there is a provider with the given title, false else.
	 */
	protected boolean isValidProviderTitle(String title) {
		return ArchieActivator.getInstance().getProviderTable().get(title) != null;
	}

	private void toggleFavorite() {
		String title = this.list.getText();
		if (!this.isValidProviderTitle(title)) {
			return;
		}

		if (!this.favoriteTitles.add(title)) {
			this.favoriteTitles.remove(title);
		}
		this.favoritePreferenceStore.setValue(PreferenceConstants.P_FAVORITE_STATISTICS,
				FavoriteStatistics.serialize(this.favoriteTitles));
		this.refreshAvailableStatistics(title);
	}

	private void loadFavoriteTitles() {
		this.favoriteTitles.clear();
		this.favoriteTitles.addAll(FavoriteStatistics
				.deserialize(this.favoritePreferenceStore.getString(PreferenceConstants.P_FAVORITE_STATISTICS)));
	}

	private void refreshAvailableStatistics(String selectedTitle) {
		TreeMap<String, AbstractDataProvider> providers = ArchieActivator.getInstance().getProviderTable();
		List<String> sortedTitles = FavoriteStatistics.sort(providers.keySet(), this.favoriteTitles);
		List<String> visibleTitles = new ArrayList<>();
		String filter = selectedTitle == null ? this.list.getText() : ""; //$NON-NLS-1$
		this.list.getTable().removeAll();

		for (String title : sortedTitles) {
			if (ContainsContentProposalProvider.matches(title, filter)) {
				TableItem item = new TableItem(this.list.getTable(), SWT.NONE);
				item.setText(title);
				item.setImage(this.favoriteTitles.contains(title) ? Images.IMG_STAR.getImage()
						: Images.IMG_STAR_EMPTY.getImage());
				visibleTitles.add(title);
			}
		}
		this.list.setVisibleItemCount(Math.max(1, visibleTitles.size()));

		if (selectedTitle != null) {
			for (int index = 0; index < visibleTitles.size(); index++) {
				if (visibleTitles.get(index).equals(selectedTitle)) {
					this.refreshingStatistics = true;
					this.list.select(index);
					this.refreshingStatistics = false;
					break;
				}
			}
		}
		this.updateFavoriteButton();
	}

	private void updateFavoriteButton() {
		if (this.favoriteButton == null || this.favoriteButton.isDisposed()) {
			return;
		}
		String title = this.list.getText();
		boolean validTitle = this.isValidProviderTitle(title);
		this.favoriteButton.setEnabled(this.list.isEnabled() && validTitle);
		this.favoriteButton.setSelection(validTitle && this.favoriteTitles.contains(title));
		boolean favorite = validTitle && this.favoriteTitles.contains(title);
		this.favoriteButton.setImage(favorite ? Images.IMG_STAR.getImage() : Images.IMG_STAR_EMPTY.getImage());
		this.favoriteButton.setToolTipText(favorite ? "Aus Favoriten entfernen" : "Als Favorit markieren"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Nothing is done on focus here.
	 *
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// do nothing
	}

	/**
	 * Sets all children enabled according to the boolean passed to this function.
	 *
	 * @param enabled True if children should be enabled, false else.
	 */
	public void setEnabled(boolean enabled) {
		if (this.list != null && this.details != null) {
			this.list.setEnabled(enabled);
			this.details.setEnabled(enabled);
			this.updateFavoriteButton();
		}
	}

	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange
	 *      (org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(NewStatisticsAction.JOB_RUNNING)) {
			this.setEnabled(false);
		}
		if (event.getProperty().equals(NewStatisticsAction.JOB_DONE)) {
			this.setEnabled(true);
		}
	}

	/**
	 * Removes any currently managed provider from the provider manager.
	 *
	 * @see ProviderManager
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		ProviderManager.getInstance().setProvider(null);
		super.dispose();
	}

}
