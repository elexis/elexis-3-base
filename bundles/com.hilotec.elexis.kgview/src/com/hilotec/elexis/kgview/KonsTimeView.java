package com.hilotec.elexis.kgview;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import com.hilotec.elexis.kgview.data.KonsData;

import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.Konsultation;
import jakarta.inject.Inject;

public class KonsTimeView extends ViewPart implements IRefreshable {
	public static final String ID = "com.hilotec.elexis.kgview.KonsTimeView";

	Label timerLbl;

	KonsData konsData;
	long time;

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());

		Composite comp = new Composite(parent, 0);
		comp.setLayout(new GridLayout());

		timerLbl = new Label(comp, 0);
		createButtonControl(comp);

		setEnabled(false);
		getSite().getPage().addPartListener(udpateOnVisible);
	}

	@Override
	public void setFocus() {
	}

	protected void createButtonControl(Composite parent) {
	}

	protected void setEnabled(boolean en) {
	}

	protected void stopTimer() {
	}

	/** Die ausgewaehlte Konsultation wurde deselektiert. */
	private void konsDeselected() {
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
		String text = String.format("%02d:%02d:%02d", (secs / 3600), ((secs % 3600) / 60), (secs % 60));
		timerLbl.setText(text);
		timerLbl.pack();
		timerLbl.update();
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
		}, timerLbl);
	}

	@Override
	public void refresh() {
		activeEncounter(ContextServiceHolder.get().getTyped(IEncounter.class).orElse(null));
	}
}
