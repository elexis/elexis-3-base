package com.hilotec.elexis.kgview;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import com.hilotec.elexis.kgview.data.KonsData;

import ch.elexis.data.Konsultation;

public class KonsTimeView extends ViewPart {
	public static final String ID = "com.hilotec.elexis.kgview.KonsTimeView";
	
	Label timerLbl;
	MySelListener msl;

	KonsData konsData;
	long time;
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		
		Composite comp = new Composite(parent, 0);
		comp.setLayout(new GridLayout());
		
		timerLbl = new Label(comp, 0);
		createButtonControl(comp);

		setEnabled(false);
		
		msl = new MySelListener();
	}

	@Override
	public void setFocus() { }

	protected void createButtonControl(Composite parent) { }
	protected void setEnabled(boolean en) { }
	protected void stopTimer() { }

	/** Die ausgewaehlte Konsultation wurde deselektiert. */
	private void konsDeselected(Konsultation kons) {
		stopTimer();
		konsData.setKonsZeit(time);
		setEnabled(false);
		konsData = null;
		time = 0;
		updateLabel();
	}
	
	/** Konsultation wurde selektiert. */
	private void konsSelected(Konsultation kons) {
		konsData = new KonsData(kons);
		time = konsData.getKonsZeit();
		setEnabled(true);
		updateLabel();
	}
	
	protected void updateLabel() {
		if (timerLbl.isDisposed()) {
			return;
		}
		
		long secs = time / 1000;
		String text = String.format("%02d:%02d:%02d",
			(secs / 3600),
			((secs % 3600) / 60),
			(secs % 60));
		timerLbl.setText(text);
		timerLbl.pack();
		timerLbl.update();
	}

	@Override
	public void dispose() {
		msl.destroy();
		super.dispose();
	}

	
	/**
	 * Hilfsklasse um auf dem Laufenden zu bleiben bezueglich der
	 * ausgewaehlten Konsultation.
	 */
	private class MySelListener extends POSelectionListener<Konsultation> {
		public MySelListener() {
			init();
		}
		
		@Override
		protected void deselected(Konsultation kons) {
			konsDeselected(kons);
		}
		
		@Override
		protected void selected(Konsultation kons) {
			konsSelected(kons);
		}
	}
}
