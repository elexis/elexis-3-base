/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.icpc.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import ch.elexis.core.data.interfaces.IDiagnose;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IDiagnosis;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.util.GenericObjectDragSource;
import ch.elexis.core.ui.util.GenericObjectDropTarget;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.icpc.Messages;
import ch.elexis.icpc.model.icpc.IcpcEpisode;
import ch.elexis.icpc.model.icpc.IcpcPackage;
import ch.elexis.icpc.service.IcpcModelServiceHolder;

public class EpisodesDisplay extends Composite {
	ScrolledForm form;
	IPatient actPatient;
	TreeViewer tvEpisodes;

	public EpisodesDisplay(final Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout());
		form = UiDesk.getToolkit().createScrolledForm(this);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite body = form.getBody();
		body.setLayout(new GridLayout());
		tvEpisodes = new TreeViewer(body);
		tvEpisodes.getControl().setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tvEpisodes.setLabelProvider(new EpisodesLabelProvider());
		tvEpisodes.setContentProvider(new EpisodecontentProvider());
		tvEpisodes.addSelectionChangedListener(GlobalEventDispatcher.getInstance().getDefaultListener());
		/* PersistentObjectDragSource pods= */new GenericObjectDragSource(tvEpisodes);
		// lvEpisodes.addDragSupport(DND.DROP_COPY, new Transfer[]
		// {TextTransfer.getInstance()},
		// pods);
		new GenericObjectDropTarget(tvEpisodes.getControl(), new Receiver());
		setPatient(ContextServiceHolder.get().getActivePatient().orElse(null));
	}

	public void setPatient(final IPatient pat) {
		actPatient = pat;
		if (pat != null) {
			tvEpisodes.setInput(pat);
		}
		tvEpisodes.refresh();
	}

	public IcpcEpisode getSelectedEpisode() {
		Tree widget = tvEpisodes.getTree();
		TreeItem[] sel = widget.getSelection();
		if ((sel == null) || (sel.length == 0)) {
			return null;
		}
		TreeItem f = sel[0];
		TreeItem p = f;
		do {
			f = p;
			p = f.getParentItem();
		} while (p != null);
		return getEpisodeFromItem(f);
	}

	private IcpcEpisode getEpisodeFromItem(final TreeItem t) {
		String etext = t.getText();
		for (Object o : ((ITreeContentProvider) tvEpisodes.getContentProvider()).getElements(actPatient)) {
			if (o instanceof IcpcEpisode) {
				IcpcEpisode ep = (IcpcEpisode) o;
				if (ep.getLabel().equals(etext)) {
					return ep;
				}
			}
		}
		return null;
	}

	class EpisodecontentProvider implements ITreeContentProvider {

		public Object[] getChildren(final Object parentElement) {
			if (parentElement instanceof IcpcEpisode) {
				IcpcEpisode ep = (IcpcEpisode) parentElement;
				ArrayList<String> ret = new ArrayList<String>();
				ret.add("Seit: " + ep.getStartDate());
				ret.add("Status: " + Messages.getStatusText(ep.getStatus()));
				List<IDiagnosis> diags = ep.getDiagnosis();
				for (IDiagnosis dg : diags) {
					ret.add(dg.getCodeSystemName() + ": " + dg.getLabel());
				}
				return ret.toArray();
			}
			return null;
		}

		public Object getParent(final Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasChildren(final Object element) {
			if (element instanceof IcpcEpisode) {
				return true;
			}
			return false;
		}

		public Object[] getElements(final Object inputElement) {
			if (actPatient != null) {
				IQuery<IcpcEpisode> query = IcpcModelServiceHolder.get().getQuery(IcpcEpisode.class);
				query.and(IcpcPackage.Literals.ICPC_EPISODE__PATIENT, COMPARATOR.EQUALS, actPatient);
				query.orderBy(IcpcPackage.Literals.ICPC_EPISODE__START_DATE, ORDER.ASC);
				return query.execute().toArray();
			}
			return new Object[0];

		}

		public void dispose() {
			// TODO Auto-generated method stub

		}

		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// TODO Auto-generated method stub

		}

	}

	class EpisodesLabelProvider extends LabelProvider implements IColorProvider {
		@Override
		public String getText(final Object element) {
			if (element instanceof IcpcEpisode) {
				return ((IcpcEpisode) element).getLabel();
			} else if (element instanceof String) {
				return element.toString();
			}
			return super.getText(element);
		}

		public Color getBackground(final Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		public Color getForeground(final Object element) {
			if (element instanceof IcpcEpisode) {
				IcpcEpisode e = (IcpcEpisode) element;
				if (e.getStatus() == 0) {
					return UiDesk.getColor(UiDesk.COL_LIGHTGREY);
				}
			}
			return UiDesk.getColor(UiDesk.COL_BLACK);
		}

	}

	class Receiver implements GenericObjectDropTarget.IReceiver {

		@Override
		public void dropped(List<Object> list, DropTargetEvent e) {
			Tree tree = tvEpisodes.getTree();
			Point point = tree.toControl(e.x, e.y);
			TreeItem item = tree.getItem(point);
			if (item != null) {
				IcpcEpisode hit = getEpisodeFromItem(item);
				if (hit != null) {
					for (Object object : list) {
						if (object instanceof IDiagnosis) {
							IDiagnosis id = (IDiagnosis) object;
							hit.addDiagnosis(id);
						}
						tvEpisodes.refresh();
					}
				}
			}
		}

		@Override
		public boolean accept(List<Object> list) {
			for (Object object : list) {
				if (object instanceof IDiagnose) {
					return true;
				}
			}
			return false;
		}

	}
}
