package ch.elexis.molemax.views2;

import org.eclipse.nebula.animation.AnimationRunner;
import org.eclipse.nebula.animation.effects.SetColorEffect;
import org.eclipse.nebula.animation.effects.SetColorEffect.IColoredObject;
import org.eclipse.nebula.animation.movement.ExpoOut;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;

public class HoverListener implements MouseMoveListener, MouseTrackListener, MouseWheelListener {

	private static final String ANIMATION_DATA = "hoverAnimation";
	private Object itemOrGroup;
	private Color backgroundColor = null;
	private Color hoverColor = null;
	GalleryItem current = null;
	Gallery gallery = null;
	int durationIn = 1000;
	int durationOut = 1000;
	GalleryItem galleryItem;

	public class AnimationDataCleaner implements Runnable {

		private GalleryItem item;

		public AnimationDataCleaner(GalleryItem item) {
			this.item = item;
		}

		public void run() {
			item.setData(ANIMATION_DATA, null);
		}
	}

	public class GalleryItemBackgroundColorAdapter implements IColoredObject {
		GalleryItem item;

		public GalleryItemBackgroundColorAdapter(GalleryItem item) {
			this.item = item;
		}

		public Color getColor() {
			if (!item.isDisposed()) {
				return item.getBackground();
			}
			return null;
		}

		public void setColor(Color c) {
			if (!item.isDisposed()) {
				item.setBackground(c);
			}
		}

	}

	public HoverListener(Gallery gallery, Color background, Color hover, int durationIn, int durationOut) {
		init(background, hover, durationIn, durationOut);
		this.gallery = gallery;
		gallery.addMouseMoveListener(this);
		gallery.addMouseTrackListener(this);
		gallery.addMouseWheelListener(this);
	}

	public HoverListener(GalleryItem galleryItem, Color background, Color hover, int durationIn, int durationOut) {
		init(background, hover, durationIn, durationOut);
		this.galleryItem = galleryItem;
		this.gallery = galleryItem.getParent();
	}

	private void init(Color background, Color hover, int durationIn, int durationOut) {
		this.setBackgroundColor(background);
		this.setHoverColor(hover);
		this.durationIn = durationIn;
		this.durationOut = durationOut;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getHoverColor() {
		return hoverColor;
	}

	public void setHoverColor(Color hoverColor) {
		this.hoverColor = hoverColor;
	}

	public void mouseMove(MouseEvent e) {
		updateHover(e);
	}

	public void mouseEnter(MouseEvent e) {
		updateHover(e);
	}

	public void mouseExit(MouseEvent e) {
		if (current != null && !current.isDisposed()) {
			animateBackgroundColor(current, backgroundColor, durationOut);
		}
		current = null;

	}

	public void mouseHover(MouseEvent e) {

	}

	private void animateBackgroundColor(final GalleryItem item, Color color, int duration) {
		if (item == null || item.isDisposed()) {
			return;
		}
		if (item != null) {

			Object o = item.getData(ANIMATION_DATA);
			if (o != null && o instanceof AnimationRunner) {
				((AnimationRunner) o).cancel();
			}

			Color bg = item.getBackground();
			if (bg == null)
				bg = backgroundColor;

			AnimationRunner animation = new AnimationRunner();
			item.setData(ANIMATION_DATA, animation);
			animation.runEffect(new SetColorEffect(new GalleryItemBackgroundColorAdapter(item), bg, color, duration,
					new ExpoOut(), new AnimationDataCleaner(item), new AnimationDataCleaner(item)));
		}
	}

	private void updateHover(MouseEvent e) {
		GalleryItem item = ((Gallery) e.widget).getItem(new Point(e.x, e.y));
		if (item != current) {
			animateBackgroundColor(current, backgroundColor, durationOut);
			animateBackgroundColor(item, hoverColor, durationIn);
			current = item;
		}
	}

	public void mouseScrolled(MouseEvent e) {
		updateHover(e);
	}

}
