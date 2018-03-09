package com.hilotec.elexis.kgview;

import java.util.ArrayList;

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

import ch.elexis.core.ui.util.PersistentObjectDropTarget;
import ch.elexis.data.Konsultation;
import ch.elexis.data.PersistentObject;
import ch.elexis.icpc.IcpcCode;
import ch.rgw.tools.StringTool;

import com.hilotec.elexis.kgview.data.KonsData;

public abstract class KonsDataFView extends SimpleTextFView {
	protected final String dbfield;
	protected final String icpcfield;
	
	private KonsData data;
	private MyKonsListener listener;
	
	private List icpc_list;
	private ArrayList<IcpcCode> code_list;
	
	protected KonsDataFView(String field){
		dbfield = field;
		icpcfield = null;
	}
	
	protected KonsDataFView(String field, String icpc){
		dbfield = field;
		icpcfield = icpc;
	}
	
	/** Leert das ICPC-Feld im UI */
	protected void clearIcpc(){
		if (icpcfield == null)
			return;
		icpc_list.removeAll();
		code_list.clear();
	}
	
	/** Inhalt des ICPC-Felds in Datenbank ablegen */
	protected void storeIcpc(){
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
	protected void loadIcpc(){
		if (icpcfield == null)
			return;
		clearIcpc();
		
		String entries[] = StringTool.unNull(data.get(icpcfield)).split(",");
		for (String c : entries) {
			if (c.length() == 0)
				continue;
			IcpcCode code = IcpcCode.load(c);
			code_list.add(code);
			icpc_list.add(code.getLabel());
		}
	}
	
	/** Aktuell ausgewaehlten ICPC Code loeschen (im UI und in DB). */
	private void removeIcpcCode(){
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
	protected void initialize(){
		if (icpcfield != null) {
			GridData gd = new GridData();
			gd.horizontalAlignment = gd.verticalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = true;
			gd.heightHint = 40;
			
			code_list = new ArrayList<IcpcCode>();
			icpc_list = new List(area, SWT.V_SCROLL);
			icpc_list.setLayoutData(gd);
			icpc_list.addKeyListener(new KeyListener() {
				public void keyReleased(KeyEvent e){}
				
				public void keyPressed(KeyEvent e){
					if (e.keyCode != SWT.DEL)
						return;
					removeIcpcCode();
				}
			});
			
			Menu m = new Menu(icpc_list);
			MenuItem mi = new MenuItem(m, 0);
			mi.setText("Entfernen");
			mi.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e){
					removeIcpcCode();
				}
				
				public void widgetDefaultSelected(SelectionEvent e){}
			});
			icpc_list.setMenu(m);
			
			new PersistentObjectDropTarget(icpc_list, new PersistentObjectDropTarget.IReceiver() {
				public void dropped(PersistentObject o, DropTargetEvent e){
					IcpcCode code = (IcpcCode) o;
					icpc_list.add(code.getLabel());
					code_list.add(code);
					storeIcpc();
				}
				
				public boolean accept(PersistentObject o){
					if (!(o instanceof IcpcCode) || code_list.contains(o))
						return false;
					return isEnabled();
				}
			});
		}
		
		data = null;
		listener = new MyKonsListener();
	}
	
	@Override
	protected void fieldChanged(){
		super.fieldChanged();
		if (!isEnabled()) {
			return;
		}
		data.set(dbfield, getText());
	}
	
	@Override
	protected boolean isEmpty(){
		return super.isEmpty() && (code_list == null || code_list.isEmpty());
	}
	
	@Override
	protected void setEnabled(boolean en){
		super.setEnabled(en);
		
		clearIcpc();
		if (icpcfield != null)
			icpc_list.setEnabled(en && getCanEdit());
	}
	
	/** Konsultation wurde deselektiert */
	private void konsDeselected(Konsultation kons){
		setEnabled(false);
		data = null;
	}
	
	/** Konsultation wurde selektiert */
	private void konsSelected(Konsultation kons){
		data = new KonsData(kons);
		setCanEdit(data.isEditOK());
		setEnabled(true);
		
		loadIcpc();
		String text = StringTool.unNull(data.get(dbfield));
		setText(text);
	}
	
	@Override
	public void dispose(){
		listener.destroy();
		super.dispose();
	}
	
	/**
	 * Helper Klasse um auf dem Laufenden zu bleiben bezüglich der aktiven Konsultation.
	 */
	class MyKonsListener extends POSelectionListener<Konsultation> {
		public MyKonsListener(){
			init();
		}
		
		@Override
		protected void deselected(Konsultation kons){
			konsDeselected(kons);
		}
		
		@Override
		protected void selected(Konsultation kons){
			konsSelected(kons);
		}
	}
}
