package com.hilotec.elexis.toolbar;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import ch.elexis.core.data.activator.CoreHub;

import org.eclipse.swt.graphics.Image;

public class PerspektivenContribution extends ContributionItem {
		
	@Override
	public void fill(ToolBar parent, int index) {
		IPerspectiveRegistry pr = PlatformUI.getWorkbench().
			getPerspectiveRegistry();
		
		String cfg = CoreHub.localCfg.get(Preferences.CFG_PERSPEKTIVEN, "");
		String[] ids = cfg.split(",");
		if (cfg == "") return;
	
		for (String id: ids) {
			IPerspectiveDescriptor pd = pr.findPerspectiveWithId(id);
			if (pd == null) continue;
			
			ToolItem ti = new ToolItem(parent, SWT.PUSH);
			ImageDescriptor idesc = pd.getImageDescriptor();
			Image im = (idesc != null ? idesc.createImage() : null);
			if (im != null)
				ti.setImage(im);
			else
				ti.setText(pd.getLabel());
			ti.setToolTipText(pd.getLabel());
			ti.addSelectionListener(new TISelListener(pd.getId()));
		}

		parent.update();
	}
	
	public boolean isDynamic() {
		return true;
	}
	
	private class TISelListener implements SelectionListener {
		private String id;
		
		public TISelListener(String pid) {
			id = pid;
		}

		public void widgetDefaultSelected(SelectionEvent e) {}
		public void widgetSelected(SelectionEvent e) {
			IWorkbench wb = PlatformUI.getWorkbench();
			try {
				wb.showPerspective(id, wb.getActiveWorkbenchWindow());
			} catch (WorkbenchException e1) { }
		}
	}
}
