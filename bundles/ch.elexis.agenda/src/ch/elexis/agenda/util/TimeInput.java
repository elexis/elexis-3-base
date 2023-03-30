/*******************************************************************************
 * Copyright (c) 2006-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation, adapted from JavaAgenda
 *
 *******************************************************************************/

package ch.elexis.agenda.util;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class TimeInput extends Composite {
	Text text;
	Label label;
	ArrayList<TimeInputListener> listeners;

	public interface TimeInputListener {
		public void changed();
	}

	public TimeInput(Composite parent, String lbl) {
		super(parent, SWT.NONE);
		this.setData("org.eclipse.e4.ui.css.CssClassName", "CustomComposite");
		listeners = new ArrayList<TimeInputListener>();
		setLayout(new GridLayout());
		label = new Label(this, SWT.NONE);
		label.setText(lbl);
		text = new Text(this, SWT.BORDER | SWT.SINGLE);
		text.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				if (!isAcceptable(text.getText())) {
					e.doit = false;
				}

			}
		});
		text.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent arg0) {
				for (TimeInputListener s : listeners) {
					s.changed();
				}
			}

		});
		text.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
	}

	public String getText() {
		return text.getText();
	}

	public TimeTool setTimeTool(TimeTool day) {
		day.set(text.getText());
		return day;

	}

	public void setColor(Color col) {
		label.setForeground(col);
	}

	public int getTimeAsMinutes() {
		return TimeTool.minutesStringToInt(text.getText());
	}

	public boolean setText(String text) {
		if (isAcceptable(text)) {
			this.text.setText(text);
			return true;
		}
		return false;
	}

	public void setTime(TimeTool ti) {
		ti.set(text.getText());
	}

	public void setTimeInMinutes(int min) {
		String h = Integer.toString(min / 60);
		String m = Integer.toString(min % 60);
		text.setText(h + ":" + StringTool.pad(StringTool.LEFT, '0', m, 2)); //$NON-NLS-1$

	}

	private boolean isAcceptable(String input) {
		return true;
		/*
		 * String in=input.replaceFirst("[:\\.]", StringUtils.EMPTY); if(in.length()>4){
		 * return false; } if(in.length()==0){ return true; } try{ int hour,minute;
		 * if(in.length()<3){ hour=Integer.parseInt(in); if( (hour>0) && (hour<24)){
		 * return true; } }else{ hour=Integer.parseInt(in.substring(0,2));
		 * minute=Integer.parseInt(in.substring(2)); if( (hour<0) || (hour>24)){ return
		 * false; } if( (minute<0) || ( minute>60)){ return false; } } return true;
		 * }catch(NumberFormatException ex){ return false; }
		 */

	}

	public void addListener(TimeInputListener l) {
		listeners.add(l);
	}

	public void removeListener(TimeInputListener l) {
		listeners.remove(l);
	}
}
