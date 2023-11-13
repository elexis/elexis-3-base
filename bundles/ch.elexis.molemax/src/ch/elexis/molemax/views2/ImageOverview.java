
package ch.elexis.molemax.views2;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.Anwender;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.molemax.Messages;
import ch.elexis.molemax.data.Tracker;

public class ImageOverview extends ViewPart implements IRefreshable {

	public static final String ID = "molemax.overview";
	Form form;
	FormToolkit tk;
	protected Tracker[][] trackers;
	private StackLayout stack;
	private Composite fullImageView, galleryComposite;
	private Label fullImageLabel;
	private ImageDetailWithGalleryView imageDetailWithGalleryView;

	private void createFullImageView(final Composite parent) {
		fullImageView = new Composite(parent, SWT.NONE);
		fullImageView.setLayout(new GridLayout());
		fullImageLabel = new Label(fullImageView, SWT.NONE);
		fullImageLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	private Composite stackComposite;
	Patient pat;
	String date;
	Composite outer;
	String pat2;
	ImageViewAll imageViewAll;
	
	public ImageOverview() {
		tk = UiDesk.getToolkit();
		trackers = new Tracker[12][];
	}

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);
	protected Composite dispAll;
	@Optional
	@Inject
	void activePatient(IPatient patient) {
		CoreUiUtil.runAsyncIfActive(() -> {
			setPatient((Patient) NoPoUtil.loadAsPersistentObject(patient), null);
		}, form);
	}
	public void createPartControl(final Composite parent) {
		form = tk.createForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite body = form.getBody();
		GridLayout layout = new GridLayout(4, false);
		body.setLayout(layout);
		Label patientLabel = new Label(body, SWT.NONE);
		patientLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		stack = new StackLayout();
		stackComposite = new Composite(body, SWT.NONE);
		stackComposite.setLayout(stack);
		stackComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		galleryComposite = new Composite(stackComposite, SWT.NONE);
		galleryComposite.setLayout(new GridLayout(1, false));
		imageViewAll = new ImageViewAll(galleryComposite);
		imageViewAll.setOverviewInstance(this);
		createFullImageView(stackComposite);
		stack.topControl = galleryComposite;
		getSite().getPage().addPartListener(udpateOnVisible);
		parent.getShell().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ESC) {
					parent.getShell().close();
				}
			}
		});
	}
	void setTopControl(final Control imageViewAll) {
		stack.topControl = imageViewAll;
	}

	private void updateGalleryForPatient() {
		Patient aktuellerPatient = getSelectedPatient();
		imageViewAll.updateGalleryForPatient(aktuellerPatient);
	}

	public void setPatient(final Patient p, String dat) {
		if (p == null) {
			form.setText(Messages.Overview_noPatient);
			return;
		}
		if (dat == null) {
			dat = Tracker.getLastSequenceDate(p);
		}
		if (p.equals(pat) && dat.equals(date)) {
			return;
		}
		for (int i = 0; i < 12; i++) {
			if (trackers[i] != null)
				Tracker.dispose(trackers[i]);
		}
		pat = p;
		date = dat;
		for (int i = 0; i < 12; i++) {
			Tracker base = Tracker.loadBase(p, date, i);
			trackers[i] = Tracker.getImageStack(base);
		}
		form.setText(p.getLabel());
		updateGalleryForPatient();
	}
	public void setFocus() {
		// TODO Auto-generated method stub
	}
	public void refresh() {
		activePatient(ContextServiceHolder.get().getActivePatient().orElse(null));
	}
	
	public void clearEvent(final Class<? extends PersistentObject> template) {
		if (template.equals(Patient.class)) {
			setPatient(null, null);
		}
	}
	
	public void switchToGalleryView(final Composite parent) {
		stack.topControl = galleryComposite;
		imageViewAll.getGallery().redraw();
		stackComposite.layout();
		stackComposite.redraw();
	}
	
	public void selectionEvent(final PersistentObject obj) {
		if (obj instanceof Anwender) {
		}
		if (obj instanceof Patient) {
			setPatient((Patient) obj, null);
		}
	}
	
	public void showFullImage(Image image, String folderPath, String absoluteImagePath, String thumbnailImagePath) {
		if (imageDetailWithGalleryView != null) {
			imageDetailWithGalleryView = null;
		}
		imageDetailWithGalleryView = new ImageDetailWithGalleryView(this, stackComposite, folderPath);
		imageDetailWithGalleryView.updateGalleryForSelectedGroup(folderPath);
		imageDetailWithGalleryView.setSelectedImage(image, absoluteImagePath);
		imageDetailWithGalleryView.selectGalleryItemByImagePath(thumbnailImagePath);
		stack.topControl = imageDetailWithGalleryView.getControl();
		stackComposite.layout();
	}

	public Patient getSelectedPatient() {
		return pat;
	}

	public static ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.molemax", path); //$NON-NLS-1$
	}

	public void reloadGallery() {
		Patient aktuellerPatient = getSelectedPatient();
		imageViewAll.updateGalleryForPatient(aktuellerPatient);
		stackComposite.layout();
		stackComposite.redraw();
	}

}