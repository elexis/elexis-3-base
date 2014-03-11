package com.hilotec.elexis.kgview;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.util.SWTHelper;

public class KonsTimerView extends KonsTimeView implements Runnable {
	public static final String ID = "com.hilotec.elexis.kgview.KonsTimerView";
	private static final int DELAY = 1000;
	
	Button startBtn;
	Button setBtn;
	Button resetBtn;
	
	boolean timerRunning;
	
	@Override
	protected void createButtonControl(Composite parent){
		Composite btns = new Composite(parent, 0);
		btns.setLayout(new GridLayout(2, false));
		
		startBtn = new Button(btns, 0);
		startBtn.setText("Start/Stop");
		startBtn.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e){
				toggleTimer();
			}
			
			public void mouseUp(MouseEvent e){}
			
			public void mouseDoubleClick(MouseEvent e){}
		});
		
		setBtn = new Button(btns, 0);
		setBtn.setText("Ändern");
		setBtn.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e){
				IInputValidator iv = new IInputValidator() {
					@Override
					public String isValid(String newText){
						if (!newText.matches("(\\d{1,2}:)?\\d{1,2}:\\d{1,2}"))
							return "Format hh:mm:ss erwartet!";
						return null;
					}
				};
				InputDialog id =
					new InputDialog(getSite().getShell(), "Zeit ändern", "Zeit des Timers ändern",
						timerLbl.getText(), iv);
				if (id.open() == InputDialog.OK) {
					String[] parts = id.getValue().split(":");
					long f = 1000;
					time = 0;
					for (int i = parts.length - 1; i >= 0; i--) {
						time += Integer.parseInt(parts[i]) * f;
						f *= 60;
					}
					konsData.setKonsZeit(time);
					updateLabel();
				}
			}
			
			public void mouseDown(MouseEvent e){}
			
			public void mouseDoubleClick(MouseEvent e){}
		});
		
		resetBtn = new Button(btns, 0);
		resetBtn.setText("Reset");
		resetBtn.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e){
				if (konsData != null
					&& konsData.getKonsZeit() > 0
					&& SWTHelper.askYesNo("Timer zurücksetzen",
						"Soll der Konsultationstimer wirklich auf 00:00:00 "
							+ "zurückgesetzt werden?")) {
					resetTimer();
				}
			}
			
			public void mouseDoubleClick(MouseEvent e){}
			
			public void mouseUp(MouseEvent e){}
		});
	}
	
	protected void setEnabled(boolean en){
		if (!en) {
			timerLbl.setText("");
		}
		startBtn.setEnabled(en);
		resetBtn.setEnabled(en);
		setBtn.setEnabled(en);
	}
	
	private void update(){
		time += DELAY;
		konsData.setKonsZeit(time);
		updateLabel();
	}
	
	protected void stopTimer(){
		Hub.getActiveShell().getDisplay().timerExec(-1, this);
		timerRunning = false;
		setBtn.setEnabled(true);
	}
	
	protected void toggleTimer(){
		if (timerRunning) {
			stopTimer();
		} else if (konsData != null) {
			Hub.getActiveShell().getDisplay().timerExec(DELAY, this);
			timerRunning = true;
			setBtn.setEnabled(false);
		}
	}
	
	protected void resetTimer(){
		stopTimer();
		if (konsData != null) {
			time = 0;
			konsData.setKonsZeit(time);
			updateLabel();
		}
	}
	
	// Called by timer
	public void run(){
		if (timerLbl.isDisposed())
			return;
		update();
		Hub.getActiveShell().getDisplay().timerExec(DELAY, this);
	}
}
