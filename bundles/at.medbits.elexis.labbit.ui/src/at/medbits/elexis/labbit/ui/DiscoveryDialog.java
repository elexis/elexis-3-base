/*******************************************************************************
 * Copyright (c) 2019 Medbits GmbH.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Thomas Huster - initial API and implementation
 *******************************************************************************/
package at.medbits.elexis.labbit.ui;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;

import at.medbits.elexis.labbit.discovery.DiscoveryInfo;

public class DiscoveryDialog extends Dialog {
	
	private TableViewer viewer;
	
	private Timer discoveryTimer;
	
	private List<DiscoveryInfo> discoveryInfo;
	
	protected DiscoveryDialog(Shell parentShell){
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.MODELESS | SWT.RESIZE);
		this.discoveryInfo = new ArrayList<>();
		this.discoveryTimer = new Timer(true);
		discoveryTimer.schedule(new TimerTask() {
			
			@Override
			public void run(){
				List<DiscoveryInfo> discovered = DiscoveryServiceHolder.get().getDiscovered();
				discovered =
					discovered.stream().filter(d -> isLabbit(d)).collect(Collectors.toList());
				for (DiscoveryInfo info : discovered) {
					if (!discoveryInfo.contains(info)) {
						discoveryInfo.add(info);
					}
				}
				Iterator<DiscoveryInfo> iter = discoveryInfo.iterator();
				while (iter.hasNext()) {
					DiscoveryInfo info = iter.next();
					if (!discovered.contains(info)) {
						iter.remove();
					}
				}
				Display.getDefault().asyncExec(() -> {
					viewer.setInput(discoveryInfo);
				});
			}
			
			private boolean isLabbit(DiscoveryInfo info){
				return info.getName().toLowerCase().contains("lab");
			}
			
		}, 1000, 5000);
	}
	
	@Override
	public void create(){
		super.create();
		getShell().setText("Labor Geräte Auswahl");
	}
	
	@Override
	public boolean close(){
		if (discoveryTimer != null) {
			discoveryTimer.cancel();
			discoveryTimer = null;
		}
		return super.close();
	}
	
	protected Control createDialogArea(Composite container){
		Composite parent = (Composite) super.createDialogArea(container);
		
		ToolBarManager menuManager = new ToolBarManager();
		List<String> addresses = DiscoveryServiceHolder.get().getNetworkAddresses();
		for (String string : addresses) {
			menuManager.add(new RefreshNetworkAction(string));
		}
		ToolBar toolbar = menuManager.createControl(parent);
		toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		
		Label lbl = new Label(parent, SWT.NONE);
		lbl.setText(
			"Folgende Geräte wurden gefunden, und können durch Doppelklick im Browser geöffnet werden.");
		viewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new DiscoveryLabelProvider());
		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event){
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (!selection.isEmpty()) {
					URI uri = ((DiscoveryInfo) selection.getFirstElement()).getLocation();
					Program.launch(uri.toString());
				}
			}
		});
		GridData gd = new GridData(GridData.FILL_BOTH);
		Table table = viewer.getTable();
		table.setLayoutData(gd);
		return parent;
	}
	
	@Override
	protected Control createButtonBar(Composite parent){
		Composite composite = new Composite(parent, SWT.NONE);
		GridData data =
			new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		data.exclude = true;
		composite.setLayoutData(data);
		return composite;
	}
}
