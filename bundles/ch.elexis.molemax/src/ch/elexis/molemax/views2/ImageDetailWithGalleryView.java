package ch.elexis.molemax.views2;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.molemax.Messages;

public class ImageDetailWithGalleryView {
	private ImageOverview overviewInstance;
	private Gallery gallery;
	private Label fullImageLabel;
	private Composite mainComposite, contentComposite;
	private List<Image> createdImages = new ArrayList<>();
	private ScrolledComposite scrolledComposite;
	private Menu contextMenu;

	public ImageDetailWithGalleryView(ImageOverview overview, Composite parent, String folderPath) {
		this.overviewInstance = overview;

		mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2, false));
		Label label = (Label) SWTHelper.createHyperlink(mainComposite, "Zurück zur Galerie", new HyperlinkAdapter() {
			@Override
			public void linkActivated(final HyperlinkEvent e) {
				overviewInstance.switchToGalleryView(parent);
				overviewInstance.reloadGallery();
			}
		});

		// Schriftart für das Label setzen
		FontData[] fontData = label.getFont().getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(14);
			fontData[i].setStyle(SWT.BOLD);
		}
		Font newFont = new Font(mainComposite.getDisplay(), fontData);
		label.setFont(newFont);

		label.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				newFont.dispose();
			}
		});

		Label placeholder = new Label(mainComposite, SWT.NONE);
		gallery = new Gallery(mainComposite, SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
		GridData galleryGridData = new GridData();
		galleryGridData.widthHint = 310;
		galleryGridData.verticalAlignment = GridData.FILL;
		gallery.setLayoutData(galleryGridData);

		DefaultGalleryGroupRenderer gr = new DefaultGalleryGroupRenderer();
		gr.setItemHeight(300);
		gr.setItemWidth(300);
		gr.setAutoMargin(true);
		gr.setMinMargin(1);
		gr.setExpanded(true);
		gr.setAlwaysExpanded(true);
		gallery.setGroupRenderer(gr);
		DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();
		gallery.setItemRenderer(ir);
		scrolledComposite = new ScrolledComposite(mainComposite,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		scrolledComposite.setLayoutData(gridData);
		contentComposite = new Composite(scrolledComposite, SWT.NONE);
		contentComposite.setLayout(new FillLayout());
		contentComposite.layout(true, true);
		scrolledComposite.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		mainComposite.layout(true, true);
		fullImageLabel = new Label(contentComposite, SWT.NONE);
		scrolledComposite.setContent(contentComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		gallery.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event event) {
				GalleryItem[] selection = gallery.getSelection();
				if (selection.length > 0) {
					Image selectedImage = selection[0].getImage();
					String selectedImagePath = (String) selection[0].getData();
					if (selectedImage != null) {
						fullImageLabel.setImage(getScaledImageForLabel(selectedImage));
						fullImageLabel.setData("originalImage", selectedImagePath);

						Display.getCurrent().asyncExec(() -> {
							if (!contentComposite.isDisposed() && !scrolledComposite.isDisposed()
									&& !mainComposite.isDisposed()) {
								contentComposite.layout(true, true);
								scrolledComposite.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
								mainComposite.layout(true, true);
							}
						});

					}
				}
			}
		});
		fullImageLabel.addListener(SWT.MouseDoubleClick, new Listener() {
			@Override
			public void handleEvent(Event event) {
				disposeCreatedImages();
				showMaximizedImage();
			}
		});

		gallery.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN) {
					Display.getCurrent().asyncExec(() -> {
						GalleryItem[] selection = gallery.getSelection();
						if (selection.length > 0) {
							Image selectedImage = selection[0].getImage();
							String selectedImagePath = (String) selection[0].getData();
							if (selectedImage != null) {
								fullImageLabel.setImage(getScaledImageForLabel(selectedImage));
								fullImageLabel.setData("originalImage", selectedImagePath);

								contentComposite.layout(true, true);
								scrolledComposite.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
								mainComposite.layout(true, true);
							}
						}
					});
				} else if (e.keyCode == SWT.ESC) {
					disposeCreatedImages();
					overviewInstance.switchToGalleryView(mainComposite.getParent());
					overviewInstance.reloadGallery();
					gallery.dispose();
				} else if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					showMaximizedImage();
				} else if (e.keyCode == SWT.DEL) {
					deleteSelectedImages();
				}
			}
		});

		Menu galleryContextMenu = createContextMenu(gallery);
		Menu fullImageLabelContextMenu = createContextMenu(fullImageLabel);

		gallery.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 3) {
					GalleryItem item = gallery.getItem(new Point(e.x, e.y));
					gallery.setMenu(item != null ? galleryContextMenu : null);
				}
			}
		});

		fullImageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 3) {
					fullImageLabel.setMenu(fullImageLabelContextMenu);
				}
			}
		});

		gallery.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 3) {
					GalleryItem item = gallery.getItem(new Point(e.x, e.y));
					if (item != null) {
						gallery.setMenu(contextMenu);
					} else {
						gallery.setMenu(null);
					}
				}
			}
		});

		DropTarget target = new DropTarget(gallery, DND.DROP_COPY | DND.DROP_MOVE);
		target.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		target.addDropListener(new DropTargetListener() {
			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					event.detail = (event.operations & DND.DROP_COPY) != 0 ? DND.DROP_COPY : DND.DROP_NONE;

				}
			}

			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
			}

			public void dropAccept(DropTargetEvent event) {
			}

			public void dragOperationChanged(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					event.detail = (event.operations & DND.DROP_COPY) != 0 ? DND.DROP_COPY : DND.DROP_NONE;
				}
			}

			public void dragLeave(DropTargetEvent event) {
			}

			public void drop(DropTargetEvent event) {

				if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {

					String[] files = (String[]) event.data;
					for (String file : files) {

						String currentImagePath = (String) fullImageLabel.getData("originalImage");
						if (currentImagePath != null) {

							File currentImageFile = new File(currentImagePath);

							File targetDirectory = currentImageFile.getParentFile();
							if (targetDirectory != null) {

								File destinationFile = new File(targetDirectory, new File(file).getName());
								try {
									Files.copy(new File(file).toPath(), destinationFile.toPath(),
											StandardCopyOption.REPLACE_EXISTING);

								} catch (IOException e) {
									System.out.println("Error while copying file: " + e.getMessage());
									e.printStackTrace();
								}

								updateGalleryForSelectedGroup(targetDirectory.getAbsolutePath());

							} else {

							}
						} else {

						}
					}
				} else {

				}
				if (overviewInstance != null) {
					overviewInstance.reloadGallery();
				}
			}

		});
	}

	public void updateGalleryForSelectedGroup(String folderPath) {
		for (GalleryItem item : gallery.getItems()) {
			item.dispose();
		}

		File directory = new File(folderPath);
		if (directory.exists() && directory.isDirectory()) {
			GalleryItem group = new GalleryItem(gallery, SWT.NONE);
			group.setText(directory.getName());
			group.setExpanded(true);
			addImagesToGalleryFromDirectory(directory, group);
			File[] imageDirectories = directory.listFiles(File::isDirectory);
			if (imageDirectories != null && imageDirectories.length > 0) {
				for (File imgDir : imageDirectories) {
					addImagesToGalleryFromDirectory(imgDir, group);
				}
			}
		}
	}

	public void selectAndShowFirstImage() {
		if (gallery.getItemCount() > 0) {
			GalleryItem firstItem = gallery.getItem(0);
			gallery.setSelection(new GalleryItem[] { firstItem });

		}
	}

	private void addImagesToGalleryFromDirectory(File dir, GalleryItem parentGroup) {
		File[] imageFiles = dir.listFiles(
				file -> file.isFile() && (file.getName().endsWith(".png") || file.getName().endsWith(".jpg")));

		if (imageFiles != null) {
			for (File imgFile : imageFiles) {
				GalleryItem item = new GalleryItem(parentGroup, SWT.NONE);
				Image image = new Image(Display.getDefault(), imgFile.getAbsolutePath());

				createdImages.add(image);
				item.setImage(image);
				item.setText(imgFile.getName());
				item.setData(imgFile.getAbsolutePath());

			}

		}

	}

	public void selectImageInGallery(String absoluteImagePath) {
		if (gallery == null || gallery.isDisposed()) {
			return;
		}

		for (GalleryItem group : gallery.getItems()) {
			for (GalleryItem item : group.getItems()) {
				String itemPath = (String) item.getData();
				if (itemPath != null && itemPath.equals(absoluteImagePath)) {
					gallery.setSelection(new GalleryItem[] { item });

					return;
				}
			}
		}
	}

	private void deleteSelectedItem(GalleryItem selectedItem) {
		File fileToDelete = new File((String) selectedItem.getData());
		if (fileToDelete.exists() && fileToDelete.delete()) {
			selectedItem.getImage().dispose();
			createdImages.remove(selectedItem.getImage());
			selectedItem.getParentItem().remove(selectedItem);
			fullImageLabel.redraw();
		}
	}

	private Menu createContextMenu(Control control) {
		Menu contextMenu = new Menu(control);
		MenuItem deleteItem = new MenuItem(contextMenu, SWT.NONE);
		deleteItem.setText(Messages.ImageSlot_delete);
		deleteItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteSelectedImages();
			}
		});
		control.setMenu(contextMenu);
		return contextMenu;
	}

	private void deleteSelectedImages() {
		GalleryItem[] selection = gallery.getSelection();
		if (selection.length > 0) {
			boolean confirm = MessageDialog.openConfirm(gallery.getShell(), Messages.ImageSlot_imageDel,
					selection.length > 1
							? Messages.ImageSlot_these + selection.length + " " + Messages.ImageSlot_imagesdelete
							: Messages.ImageSlot_reallydelete);
			if (confirm) {
				for (GalleryItem selectedItem : selection) {
					deleteSelectedItem(selectedItem);
				}
				fullImageLabel.redraw();
			}
		}
	}

	public void disposeAllImages() {
		for (Image image : createdImages) {
			if (image != null && !image.isDisposed()) {
				image.dispose();
			}
		}
		createdImages.clear();
	}

	public void dispose() {
		disposeAllImages();
		mainComposite.dispose();
	}

	public Composite getControl() {
		return mainComposite;
	}

	public void setSelectedImage(Image image, String absoluteImagePath) {
		Image scaledImage = getScaledImageForLabel(image);
		GridData gd;

		if (scaledImage != null) {
			if (scaledImage.getBounds().width <= scrolledComposite.getBounds().width
					&& scaledImage.getBounds().height <= scrolledComposite.getBounds().height) {
				gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
			} else {
				gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
			}
			fullImageLabel.setImage(scaledImage);
		} else {
			gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
			fullImageLabel.setImage(image);

		}

		fullImageLabel.setLayoutData(gd);

		Display.getCurrent().asyncExec(() -> {
			if (!contentComposite.isDisposed() && !scrolledComposite.isDisposed() && !mainComposite.isDisposed()) {
				contentComposite.layout(true, true);
				scrolledComposite.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				mainComposite.layout(true, true);

			}
		});
		fullImageLabel.setData("originalImage", absoluteImagePath);
	}

	public void selectAndCenterGalleryItemBasedOnPath(String imagePath) {
		for (GalleryItem group : gallery.getItems()) {
			for (GalleryItem item : group.getItems()) {
				String itemPath = (String) item.getData();
				if (imagePath.equals(itemPath)) {
					gallery.setSelection(new GalleryItem[] { item });
					return;
				}
			}
		}
	}

	public void selectGalleryItemByImagePath(String imagePath) {
		int itemHeight = 300;
		int headerHeight = 20;

		for (GalleryItem group : gallery.getItems()) {
			for (GalleryItem item : group.getItems()) {
				String itemPath = (String) item.getData();
				if (itemPath != null && itemPath.equals(imagePath)) {

					gallery.setSelection(new GalleryItem[] { item });
					int groupIndex = gallery.indexOf(group);
					int itemIndex = group.indexOf(item);
					int totalHeightAbove = (group.getItemCount() * itemHeight + headerHeight) * groupIndex;
					int itemPosition = totalHeightAbove + itemHeight * itemIndex;

					int offsetY = itemPosition - (gallery.getBounds().height / 3) + itemHeight / 3;
					if (offsetY < 0) {
						offsetY = 0;
					}
					int maxScroll = gallery.getVerticalBar().getMaximum() - gallery.getVerticalBar().getThumb();
					if (offsetY > maxScroll) {
						offsetY = maxScroll;
					}
					gallery.getVerticalBar().setSelection(offsetY);

					return;
				}
			}
		}
	}

	private void showMaximizedImage() {

		Shell dialog = new Shell(mainComposite.getShell(),
				SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE | SWT.V_SCROLL | SWT.H_SCROLL);
		dialog.setLayout(new GridLayout());
		String imagePath = (String) fullImageLabel.getData("originalImage");

		Image originalImage = new Image(mainComposite.getDisplay(), imagePath);
		createdImages.add(originalImage);

		ScrolledComposite scrolledComp = new ScrolledComposite(dialog, SWT.V_SCROLL | SWT.H_SCROLL);
		scrolledComp.setExpandHorizontal(true);
		scrolledComp.setExpandVertical(true);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledComp.setLayoutData(gridData); // Set layout data for the ScrolledComposite

		Composite container = new Composite(scrolledComp, SWT.NONE);
		container.setLayout(new GridLayout());
		scrolledComp.setContent(container);

		dialog.setText("Bild in voller Größe");

		Label imageLabel = new Label(container, SWT.CENTER); // Set the label alignment to CENTER
		imageLabel.setImage(originalImage);

		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		imageLabel.setLayoutData(gridData); // Set layout data for the imageLabel to center it

		scrolledComp.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		dialog.setMaximized(true);
		dialog.open();

	}

	private Image getScaledImageForLabel(Image original) {
		try {
			int originalWidth = original.getBounds().width;
			int originalHeight = original.getBounds().height;

			if (originalWidth <= 960 && originalHeight <= 810) {
				return original;
			}

			float scaleFactorX = 960f / originalWidth;
			float scaleFactorY = 810f / originalHeight;
			float scaleFactor = Math.min(scaleFactorX, scaleFactorY);
			int newWidth = Math.round(originalWidth * scaleFactor);
			int newHeight = Math.round(originalHeight * scaleFactor);

			BufferedImage bufferedImage = convertToAWT(original.getImageData());

			BufferedImage scaledBufferedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2D = scaledBufferedImage.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2D.drawImage(bufferedImage, 0, 0, newWidth, newHeight, null);
			graphics2D.dispose();

			Image scaledImage = new Image(Display.getDefault(), convertToSWT(scaledBufferedImage));
			createdImages.add(scaledImage); // Das Bild wird zur Liste hinzugefügt
			return scaledImage;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static BufferedImage convertToAWT(ImageData data) {
		DirectColorModel colorModel = new DirectColorModel(24, 0xFF0000, 0xFF00, 0xFF);
		BufferedImage bufferedImage = new BufferedImage(data.width, data.height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < data.height; y++) {
			for (int x = 0; x < data.width; x++) {
				int pixel = data.getPixel(x, y);
				RGB rgb = data.palette.getRGB(pixel);
				bufferedImage.setRGB(x, y, rgb.red << 16 | rgb.green << 8 | rgb.blue);
			}
		}
		return bufferedImage;
	}

	private static ImageData convertToSWT(BufferedImage bufferedImage) {
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(),
					colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
					data.setPixel(x, y, pixel);
				}
			}
			return data;
		} else {
			return null;
		}
	}

	public boolean isDisposed() {
		return mainComposite.isDisposed();
	}

	private void disposeCreatedImages() {
		Image currentImage = fullImageLabel.getImage();
		Iterator<Image> iterator = createdImages.iterator();
		while (iterator.hasNext()) {
			Image img = iterator.next();
			if (img != null && !img.isDisposed() && img != currentImage) {
				img.dispose();
				iterator.remove();
			}
		}
	}

}