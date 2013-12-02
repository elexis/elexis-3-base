package ch.elexis.base.ch.ebanking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import ch.elexis.admin.ACE;
import ch.elexis.base.ch.ebanking.esr.ESRRecord;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.Hub;
import ch.rgw.tools.Money;

public class ESRContentProvider extends ArrayContentProvider {
	
	private Label _lblSUMME;
	private ACE _rights;
	private Money sum;
	private ESRRecord sumRecord;
	
	private List<Object> retList;
	
	public ESRContentProvider(Label lblSUMME, ACE rights){
		_lblSUMME = lblSUMME;
		_rights = rights;
	}
	
	@Override
	public Object[] getElements(Object inputElement){
		// Insufficient rights, return with empty list, and show status
		if (CoreHub.acl.request(_rights) == false) {
			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run(){
					_lblSUMME
						.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
					_lblSUMME.setText("Insufficient rights");
				}
			});
			return Collections.emptyList().toArray();
		}
		
		retList = new ArrayList<Object>(Arrays.asList(super.getElements(inputElement)));
		
		sum = new Money();
		
		Display.getCurrent().syncExec(new Runnable() {
			@Override
			public void run(){
				_lblSUMME.setText("");
			}
		});
		
		for (Iterator<Object> iterator = retList.iterator(); iterator.hasNext();) {
			ESRRecord rec = (ESRRecord) iterator.next();
			if (rec.getTyp().equals(ESRRecord.MODE.Summenrecord)) {
				sumRecord = rec;
				iterator.remove();
				continue;
			} else {
				sum.addMoney(rec.getBetrag());
			}
		}
		
		Display.getCurrent().syncExec(new Runnable() {
			@Override
			public void run(){
				_lblSUMME.setText(sum + "");
				if (sum.isNegative()) {
					_lblSUMME.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				} else {
					_lblSUMME.setForeground(Display.getCurrent().getSystemColor(
						SWT.COLOR_DARK_GREEN));
				}
			}
		});
		
		return retList.toArray();
	}
}
