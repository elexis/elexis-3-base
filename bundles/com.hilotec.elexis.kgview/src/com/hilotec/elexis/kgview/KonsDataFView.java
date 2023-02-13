package com.hilotec.elexis.kgview;

import java.util.ArrayList;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.hilotec.elexis.kgview.data.IcpcModelServiceHolder;
import com.hilotec.elexis.kgview.data.KonsData;

import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.util.CoreUiUtil;
import ch.elexis.core.ui.util.GenericObjectDropTarget;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.Konsultation;
import ch.elexis.icpc.model.icpc.IcpcCode;
import ch.rgw.tools.StringTool;

public abstract class KonsDataFView extends SimpleTextFView implements IRefreshable {
	protected final String dbfield;
	protected final String icpcfield;

	private KonsData data;

	private List icpc_list;
	private ArrayList<IcpcCode> code_list;

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);

	protected KonsDataFView(String field) {
		dbfield = field;
		icpcfield = null;
	}

	protected KonsDataFView(String field, String icpc) {
		dbfield = field;
		icpcfield = icpc;
	}

	/** Leert das ICPC-Feld im UI */
	protected void clearIcpc() {
		if (icpcfield == null)
			return;
		icpc_list.removeAll();
		code_list.clear();
	}

	/** Inhalt des ICPC-Felds in Datenbank ablegen */
	protected void storeIcpc() {
		if (icpcfield == null)
			return;
		StringBuffer sb = new StringBuffer();
		for (IcpcCode c : code_list) {
			sb.append(c.getCode());
			sb.append(",");
		}
		if (sb.length() > 0)
			sb.setLength(sb.length() - 1);
		data.set(icpcfield, sb.toString());
		setEmpty();
	}

	/** ICPC-Feld aus Datenbank laden */
	protected void loadIcpc() {
		if (icpcfield == null)
			return;
		clearIcpc();

		String entries[] = StringTool.unNull(data.get(icpcfield)).split(",");
		for (String c : entries) {
			if (c.length() == 0)
				continue;
			IcpcCode code = IcpcModelServiceHolder.get().load(c, ch.elexis.icpc.model.icpc.IcpcCode.class).orElse(null);
			code_list.add(code);
			icpc_list.add(code.getLabel());
		}
	}

	/** Aktuell ausgewaehlten ICPC Code loeschen (im UI und in DB). */
	private void removeIcpcCode() {
		if (icpcfield == null)
			return;
		int i = icpc_list.getSelectionIndex();
		if (i >= 0) {
			code_list.remove(i);
			icpc_list.remove(i);
			storeIcpc();
		}
		setEmpty();
	}

	@Override
	protected void initialize() {
		if (icpcfield != null) {
			GridData gd = new GridData();
			gd.horizontalAlignment = gd.verticalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = true;
			gd.heightHint = 40;

			code_list = new ArrayList<IcpcCode>();
			icpc_list = new List(area, SWT.V_SCROLL);
			icpc_list.setLayoutData(gd);
			icpc_list.addKeyListener(new KeyListener() {
				public void keyReleased(KeyEvent e) {
				}

				public void keyPressed(KeyEvent e) {
					if (e.keyCode != SWT.DEL)
						return;
					removeIcpcCode();
				}
			});

			Menu m = new Menu(icpc_list);
			MenuItem mi = new MenuItem(m, 0);
			mi.setText("Entfernen");
			mi.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					removeIcpcCode();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			icpc_list.setMenu(m);

			new GenericObjectDropTarget(icpc_list, new GenericObjectDropTarget.IReceiver() {

				@Override
				public void dropped(java.util.List<Object> list, DropTargetEvent e) {
					for (Object o : list) {
						IcpcCode code = (IcpcCode) o;
						icpc_list.add(code.getLabel());
						code_list.add(code);
						storeIcpc();
					}
				}

				@Override
				public boolean accept(java.util.List<Object> list) {
					for (Object o : list) {
						if (!(o instanceof IcpcCode) || code_list.contains(o))
							return false;
					}
					return isEnabled();
				}
			});
		}

		data = null;

		getSite().getPage().addPartListener(udpateOnVisible);
	}

	@Override
	protected void fieldChanged() {
		super.fieldChanged();
		if (!isEnabled()) {
			return;
		}
		data.set(dbfield, getText());
	}

	@Override
	protected boolean isEmpty() {
		return super.isEmpty() && (code_list == null || code_list.isEmpty());
	}

	@Override
	protected void setEnabled(boolean en) {
		super.setEnabled(en);

		clearIcpc();
		if (icpcfield != null)
			icpc_list.setEnabled(en && getCanEdit());
	}

	/** Konsultation wurde deselektiert */
	private void konsDeselected() {
		setEnabled(false);
		data = null;
	}

	/** Konsultation wurde selektiert */
	private void konsSelected(Konsultation kons) {
		data = new KonsData(kons);
		setCanEdit(data.isEditOK());
		setEnabled(true);

		loadIcpc();
		String text = StringTool.unNull(data.get(dbfield));
		setText(text);
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(udpateOnVisible);
		super.dispose();
	}

	@Inject
	void activeEncounter(@Optional IEncounter encounter) {
		CoreUiUtil.runAsyncIfActive(() -> {
			Konsultation k = (Konsultation) NoPoUtil.loadAsPersistentObject(encounter);
			if (k != null) {
				konsSelected(k);
			} else {
				konsDeselected();
			}
		}, icpc_list);
	}

	@Override
	public void refresh() {
		activeEncounter(ContextServiceHolder.get().getTyped(IEncounter.class).orElse(null));
	}
}
