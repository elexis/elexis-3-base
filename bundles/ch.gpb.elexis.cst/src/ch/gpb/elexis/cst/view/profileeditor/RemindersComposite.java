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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.ui.UiDesk;
import ch.gpb.elexis.cst.Activator;
import ch.gpb.elexis.cst.Messages;
import ch.gpb.elexis.cst.data.CstProfile;
import ch.gpb.elexis.cst.data.CstStateItem;
import ch.gpb.elexis.cst.data.CstStateItem.StateType;
import ch.gpb.elexis.cst.dialog.CstReminderDialog;
import ch.gpb.elexis.cst.service.CstService;

public class RemindersComposite extends CstComposite implements HeartListener/* IActivationListener */ {

	CstProfile aProfile;
	TreeViewer treeviewer;
	Action actionAddObject;
	Action actionDeleteObject;
	Action actionEditObject;

	Image imgExclam = UiDesk.getImage(Activator.IMG_EXCLAM_NAME);

	List<Image> imageList = new ArrayList<Image>();

	Label lblHeart;
	Label lblCheckingForActions;

	public RemindersComposite(Composite parent) {
		super(parent, SWT.NONE);

		GridLayout gridLayout = new GridLayout(4, false);
		setLayout(gridLayout);

		createLayout(this);

		treeviewer = new TreeViewer(this, SWT.BORDER);
		Tree tree_1 = treeviewer.getTree();
		GridData gd_tree_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
		gd_tree_1.heightHint = 230;
		gd_tree_1.widthHint = 500;
		tree_1.setLayoutData(gd_tree_1);
		treeviewer.setContentProvider(new ViewContentProvider());
		treeviewer.setLabelProvider(new ViewLabelProvider());

		Button btnNewAction = new Button(this, SWT.NONE);
		btnNewAction.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnNewAction.setText("Start new event chain");
		btnNewAction.addSelectionListener(new NewItemListener());

		Button btnExpandAll = new Button(this, SWT.NONE);
		btnExpandAll.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnExpandAll.setText("Expand All");
		btnExpandAll.addSelectionListener(new ExpandAllListener());

		MenuManager menuMgr = new MenuManager();
		Menu menu = menuMgr.createContextMenu(treeviewer.getControl());
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (treeviewer.getSelection().isEmpty()) {
					return;
				}

				if (treeviewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) treeviewer.getSelection();
					manager.add(actionAddObject);
					manager.add(actionDeleteObject);
					manager.add(actionEditObject);

				}
			}
		});

		menuMgr.setRemoveAllWhenShown(true);
		treeviewer.getControl().setMenu(menu);
		treeviewer.getTree().setHeaderVisible(true);
		ColumnViewerToolTipSupport.enableFor(treeviewer);

		makeActions();

		CoreHub.heart.addListener(this);
		new Label(this, SWT.NONE);

		lblHeart = new Label(this, SWT.NONE);
		lblHeart.setText(Messages.RemindersComposite_lblHeart_text);
		GridData gd_lblHeart = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_lblHeart.heightHint = 50;
		gd_lblHeart.widthHint = 50;
		lblHeart.setLayoutData(gd_lblHeart);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		lblCheckingForActions = new Label(this, SWT.NONE);
		lblCheckingForActions.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblCheckingForActions.setText(Messages.RemindersComposite_lblCheckingForActions_text);

		treeviewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				TreeViewer viewer = (TreeViewer) event.getViewer();
				IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
				Object selectedNode = thisSelection.getFirstElement();
				viewer.setExpandedState(selectedNode, !viewer.getExpandedState(selectedNode));
			}
		});

		imageList = Arrays.asList(imgHeart1, imgHeart2, imgHeart3, imgHeartA, imgHeartB, imgHeartC, imgHeartD,
				imgHeartE);

		lblCheckingForActions.setVisible(false);
		lblHeart.setVisible(false);
	}

	// dynamic Layout elements
	private void createLayout(Composite parent) {

		Label labelTherapievorschlag = new Label(parent, SWT.NONE);
		labelTherapievorschlag.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 4, 1));
		labelTherapievorschlag.setText(Messages.RemindersComposite_title_reminder);
		labelTherapievorschlag.setSize(200, 20);

	}

	@Override
	public void dispose() {
		super.dispose();
	}

	/**
	 * checking for actions coming due Adding the Hearbeat Listener is done by
	 * activation of CstProfileEditor, even when there is no profile selected yet.
	 * thus the return statement.
	 */
	@Override
	public void heartbeat() {
		// System.out.println("HEARTBEAT");

		if (aProfile == null) {
			return;
		}
		new HeartbeatThread().start();
		// UiDesk.asyncExec(new HeartbeatThread());

	}

	class NewItemListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent e) {
			addObject(null);
			treeviewer.setInput(CstStateItem.getRootItems(aProfile));
		}
	}

	class ExpandAllListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent e) {
			expandAll();
		}
	}

	private void showMessage(String title, String msg) {
		MessageDialog.openInformation(UiDesk.getTopShell(), title, msg);
	}

	public void addObject(CstStateItem selItem) {

		if (aProfile == null) {
			showMessage("No Profile", "Bitte w�hlen Sie ein Profil");
			return;
		}
		CstReminderDialog dialog = new CstReminderDialog(getShell(), CoreHub.actMandant);

		StateType selType = null;
		String name = null;
		dialog.create();
		if (dialog.open() == Window.OK) {
			selType = dialog.getItemType();
			name = dialog.getGroupName();
		} else {
			return;
		}

		if (selItem != null) {

			CstStateItem item = new CstStateItem(CstService.getCompactFromDate(new Date()), name, selType,
					aProfile.getId(), selItem.getId(), CoreHub.actMandant.getId());
			System.out.println("created CstStateItem with parent: " + item.getId());
		} else {
			CstStateItem item = new CstStateItem(CstService.getCompactFromDate(new Date()), name, selType,
					aProfile.getId(), null, CoreHub.actMandant.getId());
			System.out.println("created CstStateItem without parent: " + item.getId());
		}
	}

	private void makeActions() {
		actionAddObject = new Action() {
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) treeviewer.getSelection();
				System.out.println("sel tree: " + selection.toString());
				CstStateItem selItem = (CstStateItem) selection.getFirstElement();

				addObject(selItem);
				treeviewer.refresh();
				// expandAll();
				treeviewer.setExpandedState(selItem, true);

			}
		};
		actionAddObject.setText("Add Item");
		actionAddObject.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

		actionEditObject = new Action() {
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) treeviewer.getSelection();
				System.out.println("sel tree: " + selection.toString());
				CstStateItem selItem = (CstStateItem) selection.getFirstElement();

				CstReminderDialog dialog = new CstReminderDialog(getShell(), CoreHub.actMandant);
				dialog.create();

				dialog.setName(selItem.getName());
				dialog.setDescription(selItem.getDescription());
				dialog.setType(selItem.getItemType());
				dialog.setDate(CstService.getDateFromCompact(selItem.getDate()));

				StateType selType = null;
				String name = null;
				String desc = null;
				Date date = null;

				if (dialog.open() == Window.OK) {
					selType = dialog.getItemType();
					name = dialog.getGroupName();
					desc = dialog.getGroupDescription();
					date = dialog.getDate();

				} else {
					return;
				}

				selItem.setName(name);
				selItem.setDescription(desc);
				selItem.setItemType(selType);
				selItem.setDate(CstService.getCompactFromDate(date));

				treeviewer.refresh();
				// expandAll();

			}
		};
		actionEditObject.setText("Edit Item");
		actionEditObject.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));

		actionDeleteObject = new Action() {
			public void run() {
				TreeSelection selection = (TreeSelection) treeviewer.getSelection();
				System.out.println("sel tree: " + selection.toString());
				CstStateItem selItem = (CstStateItem) selection.getFirstElement();
				// selection.getPaths();
				// CstStateItem parent = (CstStateItem) selItem.getParent();

				// TreeItem treeItem = (TreeItem) selection.getFirstElement();

				List<CstStateItem> result = new ArrayList<CstStateItem>();
				List<CstStateItem> itemsToDelete = getChildrenToDelete(selItem, result);
				itemsToDelete.add(selItem);

				for (CstStateItem cstStateItem : itemsToDelete) {
					cstStateItem.delete();
				}

				treeviewer.setInput(CstStateItem.getRootItems(aProfile));
				expandAll();
				//

				// treeviewer.setExpandedState(CstStateItem.getParent(selItem), true);

			}
		};
		actionDeleteObject.setText("Delete Item");
		actionDeleteObject.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));

	}

	private static List<CstStateItem> getChildrenToDelete(CstStateItem parent, List<CstStateItem> result) {
		List<CstStateItem> items = CstStateItem.getChildren(parent);

		for (CstStateItem item : items) {
			result.add(item);

			if (!CstStateItem.getChildren(item).isEmpty()) {
				RemindersComposite.getChildrenToDelete(item, result);
			}

		}

		return result;
	}

	private void expandAll() {
		treeviewer.expandAll();
	}

	class ViewLabelProvider extends StyledCellLabelProvider {
		@Override
		public void update(ViewerCell cell) {

			CstStateItem element = (CstStateItem) cell.getElement();
			StyledString text = new StyledString();
			text.append(element.getItemType().name() + ": " + element.getName());

			cell.setText(text.toString());
			cell.setStyleRanges(text.getStyleRanges());

			// A, D, R, T
			switch (element.getItemType().ordinal()) {
			case 0:
				cell.setForeground(ORANGE);
				cell.setImage(imgAction);
				break;
			case 1:
				cell.setForeground(COLOR_RED);
				cell.setImage(imgDecision);
				break;
			case 2:
				cell.setText(element.getItemType().name() + StringUtils.EMPTY + "   f�llig am:"
						+ CstService.getGermanFromCompact(element.getDate()));

				if (new Date().after(CstService.getDateFromCompact(element.getDate()))) {
					cell.setImage(imgReminder);
					cell.setBackground(COLOR_RED);
					/*
					 * cell.setText(text.toString() + "  (f�llig am: " +
					 * CstService.getGermanFromCompact(element.getDate()) + ") ");
					 */

				} else {
					cell.setBackground(WHITE);
					cell.setImage(imgReminder);
				}
				cell.setForeground(VIOLET);

				break;
			case 3:
				cell.setForeground(GREEN);
				cell.setImage(imgTrigger);
				break;

			default:
				break;
			}

			super.update(cell);

		}

		@Override
		public String getToolTipText(Object element) {
			CstStateItem item = (CstStateItem) element;
			return "ID: " + item.getId() + " (" + CstService.getGermanFromCompact(item.getDate()) + ")";
		}

		@Override
		public Point getToolTipShift(Object object) {
			return new Point(5, 5);
		}

		@Override
		public int getToolTipTimeDisplayed(Object object) {
			return 2000;
		}

		@Override
		public int getToolTipDisplayDelayTime(Object object) {
			return 200;
		}
	}

	class ViewContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			LinkedList<CstStateItem> list = (LinkedList<CstStateItem>) inputElement;
			return list.toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			CstStateItem file = (CstStateItem) parentElement;

			List<CstStateItem> children = CstStateItem.getChildren(file);
			return children.toArray();

		}

		@Override
		public Object getParent(Object element) {

			System.out.println("getParent class: " + element.getClass());
			if (element instanceof LinkedList) {
				LinkedList<CstStateItem> list = (LinkedList<CstStateItem>) element;
				Iterator<CstStateItem> it = list.iterator();

				if (it.hasNext()) {
					CstStateItem child2 = it.next();
					return child2;
				} else {
					return null;
				}

			}

			CstStateItem child = (CstStateItem) element;
			return CstStateItem.getParent(child);

		}

		@Override
		public boolean hasChildren(Object element) {
			CstStateItem child = (CstStateItem) element;
			List<CstStateItem> children = CstStateItem.getChildren(child);
			return !children.isEmpty();

		}

	}

	public void clear() {
	}

	public void setProfile(CstProfile aProfile) {
		this.aProfile = aProfile;
		this.treeviewer.setInput(CstStateItem.getRootItems(aProfile));
	}

	/**
	 * class to display the execution of the heart beat with a pulsating heart (what
	 * else?)
	 *
	 * @author daniel
	 *
	 */
	public class HeartbeatThread extends Thread {
		int pulse = 300;

		public void run() {

			try {
				HeartbeatThread.sleep(1000);
				UiDesk.asyncExec(new Runnable() {
					public void run() {
						lblCheckingForActions.setVisible(true);
						lblHeart.setVisible(true);
					}
				});

				for (final Image image : imageList) {

					HeartbeatThread.sleep(pulse);
					UiDesk.asyncExec(new Runnable() {
						public void run() {
							lblHeart.setImage(image);
						}
					});
				}

				UiDesk.asyncExec(new Runnable() {
					public void run() {
						lblCheckingForActions.setVisible(false);
						lblHeart.setVisible(false);
					}
				});
				HeartbeatThread.sleep(400);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}