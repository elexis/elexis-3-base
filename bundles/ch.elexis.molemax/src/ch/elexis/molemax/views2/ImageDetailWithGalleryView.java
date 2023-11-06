package ch.elexis.molemax.views2;

import java.awt.Desktop;
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
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.animation.AnimationRunner;
import org.eclipse.nebula.animation.effects.MoveScrollBarEffect;
import org.eclipse.nebula.animation.movement.ExpoOut;
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
import org.eclipse.swt.graphics.Color;
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
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.slf4j.LoggerFactory;

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
	private Color blue = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
	private Image originalImage = null;
	public ImageDetailWithGalleryView(ImageOverview overview, Composite parent, String folderPath) {
		this.overviewInstance = overview;
		mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2, false));
		Label label = SWTHelper.createHyperlink(mainComposite, "Zurück zur Galerie", new HyperlinkAdapter() {
			public void linkActivated(final HyperlinkEvent e) {
				overviewInstance.switchToGalleryView(parent);
			}
		});
		label.setForeground(blue);
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
					String selectedThumbnailPath = (String) selection[0].getData();
					File thumbnailFile = new File(selectedThumbnailPath);
					File originalFile = new File(thumbnailFile.getParentFile().getParent(), thumbnailFile.getName());
					if (originalFile.exists() && originalFile.isFile()) {
						try {
							Image originalImage = new Image(Display.getDefault(), originalFile.getAbsolutePath());
							createdImages.add(originalImage);
							fullImageLabel.setImage(getScaledImageForLabel(originalImage));
							fullImageLabel.setData("originalImage", originalFile.getAbsolutePath());
							Display.getCurrent().asyncExec(() -> {
								if (!contentComposite.isDisposed() && !scrolledComposite.isDisposed()
										&& !mainComposite.isDisposed()) {
									contentComposite.layout(true, true);
									scrolledComposite
											.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
									mainComposite.layout(true, true);
								}
							});
						} catch (Exception e) {
							LoggerFactory.getLogger(getClass())
									.warn("Fehler beim Laden des Originalbildes: " + e.getMessage());
							e.printStackTrace();
						}
					} else {
						LoggerFactory.getLogger(getClass()).warn(
								"Originalbild nicht gefunden oder ist kein Datei: " + originalFile.getAbsolutePath());
					}
				}
			}
		});
		fullImageLabel.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				showMaximizedImage();
			}
		});
		gallery.addListener(SWT.MouseDoubleClick, new Listener() {
			@Override
			public void handleEvent(Event event) {
				showMaximizedImage();
			}
		});
		gallery.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN) {
					Display.getCurrent().syncExec(() -> {
						if (!gallery.isDisposed()) {
							GalleryItem[] selection = gallery.getSelection();
							if (selection.length > 0 && !selection[0].isDisposed()) {
								String selectedThumbnailPath = (String) selection[0].getData();
								File thumbnailFile = new File(selectedThumbnailPath);
								File originalFile = new File(thumbnailFile.getParentFile().getParent(),
										thumbnailFile.getName());
								if (originalFile.exists() && originalFile.isFile()) {
									if (originalImage != null && !originalImage.isDisposed()) {
										originalImage.dispose();
									}
									originalImage = new Image(Display.getDefault(), originalFile.getAbsolutePath());
									if (!fullImageLabel.isDisposed()) {
										fullImageLabel.setImage(getScaledImageForLabel(originalImage));
										fullImageLabel.setData("originalImage", originalFile.getAbsolutePath());
										contentComposite.layout(true, true);
										scrolledComposite
												.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
										mainComposite.layout(true, true);
									}
								} else {
									LoggerFactory.getLogger(getClass())
											.warn("Originalbild nicht gefunden oder ist keine Datei: "
											+ originalFile.getAbsolutePath());
								}
							}
						}
					});
				} else if (e.keyCode == SWT.ESC) {
					overviewInstance.switchToGalleryView(mainComposite.getParent());
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
			public void mouseUp(MouseEvent e) {
				if (e.button == 3) {
					GalleryItem item = gallery.getItem(new Point(e.x, e.y));
					gallery.setMenu(item != null ? galleryContextMenu : null);
				}
			}
		});
		fullImageLabel.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				if (e.button == 3) {
					fullImageLabel.setMenu(fullImageLabelContextMenu);
				}
			}
		});
		gallery.addMouseListener(new MouseAdapter() {
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
									e.printStackTrace();
									LoggerFactory.getLogger(getClass()).warn("Error while copying file: ", e,
											e.getMessage());
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
		gallery.removeAll();
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
		File thumbnailDirectory = new File(dir, "thumbnails");
		if (thumbnailDirectory.exists() && thumbnailDirectory.isDirectory()) {
			File[] imageFiles = thumbnailDirectory.listFiles(
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
	}

	public void selectImageInGallery(String imagePath) {
	    GalleryItem[] items = gallery.getItems();
	    for (GalleryItem item : items) {
	        String itemPath = (String) item.getData();
	        if (itemPath != null && itemPath.equals(imagePath)) {
	            gallery.setSelection(new GalleryItem[] {item});
	            break;
	        }
	    }
	}

	private void deleteSelectedItem(GalleryItem selectedItem) {
		String selectedItemPath = (String) selectedItem.getData();
		File fileToDelete = new File(selectedItemPath);
		if (fileToDelete.exists() && fileToDelete.delete()) {
			File originalFile = new File(fileToDelete.getParentFile().getParent(), fileToDelete.getName());
			if (originalFile.exists()) {
				originalFile.delete();
			}
			selectedItem.getImage().dispose();
			createdImages.remove(selectedItem.getImage());
			selectedItem.getParentItem().remove(selectedItem);
			fullImageLabel.redraw();
		}
		overviewInstance.reloadGallery();
	}

	private Menu createContextMenu(Control control) {
		Menu contextMenu = new Menu(control);
		MenuItem deleteItem = new MenuItem(contextMenu, SWT.NONE);
		deleteItem.setText(Messages.ImageSlot_delete);
		deleteItem.addSelectionListener(new SelectionAdapter() {
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
		if (image == null || image.isDisposed()) {
			return;
		}
		Image previousScaledImage = (Image) fullImageLabel.getData("scaledImage");
		if (previousScaledImage != null && !previousScaledImage.isDisposed()) {
			previousScaledImage.dispose();
		}
		Image scaledImage = getScaledImageForLabel(image);
		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		if (scaledImage != null && !scaledImage.isDisposed()) {
			fullImageLabel.setImage(scaledImage);
			fullImageLabel.setData("scaledImage", scaledImage); // Store reference to dispose later
		} else {
			fullImageLabel.setImage(image);
			fullImageLabel.setData("scaledImage", null);
		}
		fullImageLabel.setLayoutData(gd);
		Display.getCurrent().asyncExec(() -> {
			if (!contentComposite.isDisposed() && !scrolledComposite.isDisposed() && !mainComposite.isDisposed()) {
				scrolledComposite.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				contentComposite.layout(true, true);
				mainComposite.layout(true, true);
			}
		});
		if (!fullImageLabel.isDisposed()) {
			fullImageLabel.setData("originalImage", absoluteImagePath);
		}
	}

	public void selectGalleryItemByImagePath(String originalImagePath) {
		int itemHeight = 290;
		int headerHeight = 10;
		AnimationRunner runner = new AnimationRunner();
		String imagePath = convertToThumbnailPath(originalImagePath);
		for (GalleryItem group : gallery.getItems()) {
			for (GalleryItem item : group.getItems()) {
				String itemPath = (String) item.getData();
				if (itemPath != null && itemPath.equals(imagePath)) {
					gallery.setSelection(new GalleryItem[] { item });
					int groupIndex = gallery.indexOf(group);
					int itemIndex = group.indexOf(item);
					int totalHeightAbove = (group.getItemCount() * itemHeight + headerHeight) * groupIndex;
					int itemPosition = totalHeightAbove + itemHeight * itemIndex;
					int centerYPosition = itemPosition + (itemHeight / 5) - (gallery.getClientArea().height / 7);
					ScrollBar vBar = gallery.getVerticalBar();
					int currentSelection = vBar.getSelection();
					MoveScrollBarEffect effect = new MoveScrollBarEffect(vBar, currentSelection, centerYPosition, 500,
							new ExpoOut(),
							null, null
					);
					runner.runEffect(effect);
					return;
				}
			}
		}
	}
	private void showMaximizedImage() {
	    Display display = mainComposite.getDisplay();
	    display.asyncExec(() -> {
	        String imagePath = (String) fullImageLabel.getData("originalImage");
	        File imageFile = new File(imagePath);
	        if (imageFile.exists()) {
	            try {
	                Desktop.getDesktop().open(imageFile);
	            } catch (IOException e) {
	                e.printStackTrace();
					LoggerFactory.getLogger(getClass())
							.warn("Fehler beim Öffnen des Bildes mit dem Standard-Bildbetrachter.", e);
	            }
	        } else {
				LoggerFactory.getLogger(getClass()).warn("Bilddatei existiert nicht: " + imagePath);
	        }
	    });
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
			Image scaledImage = new Image(Display.getDefault(), convertToSWT(scaledBufferedImage));
			createdImages.add(scaledImage);
			original.dispose();
			return scaledImage;
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).warn("Image ready to dispos ", e);
			e.printStackTrace();
			original.dispose();
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

	private String convertToThumbnailPath(String originalImagePath) {
		File originalFile = new File(originalImagePath);
		File thumbnailDirectory = new File(originalFile.getParentFile(), "thumbnails");
		File thumbnailFile = new File(thumbnailDirectory, originalFile.getName());
		return thumbnailFile.getAbsolutePath();
	}
}