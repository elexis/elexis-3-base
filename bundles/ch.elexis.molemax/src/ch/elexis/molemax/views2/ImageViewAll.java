package ch.elexis.molemax.views2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.nebula.animation.AnimationRunner;
import org.eclipse.nebula.animation.effects.MoveScrollBarEffect;
import org.eclipse.nebula.animation.movement.IMovement;
import org.eclipse.nebula.animation.movement.LinearInOut;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import ch.elexis.data.Patient;
import ch.elexis.molemax.Messages;
import ch.elexis.molemax.data.Tracker;
import ch.elexis.molemax.handler.ThumbnailHandler;

public class ImageViewAll {
	private ImageOverview overviewInstance;
	private List<Image> createdImages = new ArrayList<>();
	private Patient aktuellerPatient;
	private static final int AUTO_SCROLL_MARGIN = 40;
	private static final int AUTO_SCROLL_SPEED = 75;
	private String groupName = null;
	private Gallery gallery;
	private GalleryItem lastClickedGroupItem = null;
	public void setOverviewInstance(ImageOverview overview) {
		this.overviewInstance = overview;
	}

	public ImageViewAll(Composite parent) {
		gallery = new Gallery(parent, SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
		gallery.setLayoutData(new GridData(GridData.FILL_BOTH));
		new HoverListener(gallery, Display.getCurrent().getSystemColor(SWT.COLOR_WHITE),
				Display.getCurrent().getSystemColor(SWT.COLOR_GRAY), 500, 500);
		DefaultGalleryGroupRenderer gr = new DefaultGalleryGroupRenderer();
		gr.setItemHeight(250);
		gr.setItemWidth(250);
		gr.setAutoMargin(true);
		gr.setAnimation(true);
		gallery.setGroupRenderer(gr);
		DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();
		gallery.setItemRenderer(ir);
		DragSource source = new DragSource(gallery, DND.DROP_MOVE | DND.DROP_COPY);
		source.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		source.addDragListener(new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				Point mouseCoords = Display.getCurrent().getCursorLocation();
				mouseCoords = gallery.toControl(mouseCoords);
				GalleryItem itemUnderMouse = gallery.getItem(mouseCoords);
				if (itemUnderMouse == null) {
					event.doit = false;
					return;
				}
				GalleryItem[] allItems = gallery.getItems();
				if (allItems == null || allItems.length == 0) {
					event.doit = false;
					return;
				}
				GalleryItem[] selection = gallery.getSelection();
				if (selection.length > 0) {
					File imgFile = new File((String) selection[0].getData(), selection[0].getText());
					event.data = new String[] { imgFile.getAbsolutePath() };
				}
			}
			public void dragSetData(DragSourceEvent event) {
				GalleryItem[] selection = gallery.getSelection();
				if (selection.length > 0) {
					File imgFile = new File((String) selection[0].getData(), selection[0].getText());
					event.data = new String[] { imgFile.getAbsolutePath() };
				}
			}
			public void dragFinished(DragSourceEvent event) {
				if (event.detail == DND.DROP_MOVE) {
					GalleryItem[] selection = gallery.getSelection();
					if (selection.length > 0) {
						Image img = selection[0].getImage();
						if (img != null && !img.isDisposed()) {
							img.dispose();
							createdImages.remove(img);
						}
					}
				}
			}
		});
		gallery.addListener(SWT.MouseDoubleClick, event -> {
			if (overviewInstance != null) {
				GalleryItem[] selection = gallery.getSelection();
				if (selection.length > 0) {
					String folderPath = (String) selection[0].getData();
					String thumbnailImagePath = folderPath + File.separator + selection[0].getText();
					File originalImageFile = new File(thumbnailImagePath);
					if (originalImageFile.exists()) {
						Image originalImage = new Image(Display.getDefault(), originalImageFile.getAbsolutePath());
						overviewInstance.showFullImage(originalImage, folderPath, originalImageFile.getAbsolutePath(),
								thumbnailImagePath);
					} else {
						LoggerFactory.getLogger(getClass()).warn("Expected path to original image not found: "
								+ originalImageFile.getAbsolutePath());
					}
				}
			}
		});
		gallery.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					if (overviewInstance != null) {
						GalleryItem[] selection = gallery.getSelection();
						if (selection != null && selection.length > 0) {
							String folderPath = (String) selection[0].getData();
							String thumbnailImagePath = folderPath + File.separator + selection[0].getText();
							File originalImageFile = new File(thumbnailImagePath);
							if (originalImageFile.exists()) {
								Image originalImage = null;
								try {
									originalImage = new Image(Display.getDefault(),
											originalImageFile.getAbsolutePath());
									overviewInstance.showFullImage(originalImage, folderPath,
											originalImageFile.getAbsolutePath(), thumbnailImagePath);
								} catch (SWTException swtEx) {
									LoggerFactory.getLogger(getClass())
											.warn("Error loading the image: " + swtEx.getMessage());
									if (originalImage != null && !originalImage.isDisposed()) {
										originalImage.dispose();
									}
								}
							} else {
								LoggerFactory.getLogger(getClass())
										.warn("Expected path to original image not found: "
												+ originalImageFile.getAbsolutePath());
							}
						}
					}
				} else if (e.keyCode == SWT.ESC) {
					if (ImageViewAll.this.aktuellerPatient != null) {
						updateGalleryForPatient(ImageViewAll.this.aktuellerPatient);
					}
				} 
				else if (e.keyCode == SWT.DEL) {
					GalleryItem[] selection = gallery.getSelection();
					if (selection == null || selection.length == 0) {
						return;
					}
					if (selection.length > 0) {
						boolean confirm = MessageDialog.openConfirm(gallery.getShell(), Messages.ImageSlot_imageDel,
								selection.length > 1
										? Messages.ImageSlot_these + selection.length + " "
												+ Messages.ImageSlot_imagesdelete
										: Messages.ImageSlot_reallydelete);
						if (confirm) {
							for (GalleryItem selectedItem : selection) {
								deleteSelectedItem(selectedItem);
							}
						}
					}
				}
			}
		});

		Menu contextMenu = new Menu(gallery);
		MenuItem deleteItem = new MenuItem(contextMenu, SWT.NONE);
		deleteItem.setText(Messages.ImageSlot_delete);
		deleteItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				GalleryItem[] selection = gallery.getSelection();
				if (selection.length > 0) {
					boolean confirm = MessageDialog.openConfirm(gallery.getShell(), Messages.ImageSlot_imageDel,
							selection.length > 1
									? Messages.ImageSlot_these + selection.length + " "
											+ Messages.ImageSlot_imagesdelete
									: Messages.ImageSlot_reallydelete);
					if (confirm) {
						for (GalleryItem selectedItem : selection) {
							deleteSelectedItem(selectedItem);
						}
					}
				}
			}
		});
		gallery.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				if (e.button == 3) {
					GalleryItem clickedGroupItem = null;
					for (GalleryItem item : gallery.getItems()) {
						if (item.getBounds().contains(new Point(e.x, e.y))) {
							clickedGroupItem = item;

							break;
						}
					}
					if (clickedGroupItem != null && clickedGroupItem.getParentItem() == null) {
						lastClickedGroupItem = clickedGroupItem;
						gallery.setMenu(contextMenu);
					} else {
						gallery.setMenu(null);
					}
				}
			}
		});

		MenuItem renameItem = new MenuItem(contextMenu, SWT.NONE);
		renameItem.setText(Messages.BriefAuswahlRenameButtonText);
		renameItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (lastClickedGroupItem != null) {
					String currentName = lastClickedGroupItem.getText();
					InputDialog renameDialog = new InputDialog(gallery.getShell(), Messages.BefundePrefs_enterRenameCaption,
							Messages.Rename_Group_Text, currentName, null);
					if (renameDialog.open() == InputDialog.OK) {
						String newName = renameDialog.getValue();
						if (newName != null && !newName.trim().isEmpty() && !newName.equals(currentName)) {
							boolean success = renameGroupDirectory(currentName, newName);
							if (success) {
								lastClickedGroupItem.setText(newName);
								updateGalleryForPatient(aktuellerPatient);
							} else {
								MessageDialog.openError(gallery.getShell(), Messages.Core_Error,
										Messages.Rename_Folder_Error);
							}
						}
					}
				}
			}
		});
		gallery.setMenu(contextMenu);

		DropTarget target = new DropTarget(gallery, DND.DROP_COPY | DND.DROP_MOVE);
		target.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		target.addDropListener(new DropTargetListener() {
			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					event.detail = (event.operations & DND.DROP_MOVE) != 0 ? DND.DROP_MOVE : DND.DROP_NONE;
				}
			}
			public void dragOver(DropTargetEvent event) {
				Point mouseCoords = Display.getCurrent().getCursorLocation();
				mouseCoords = gallery.toControl(mouseCoords);
				ScrollBar verticalScrollBar = gallery.getVerticalBar();
				int scrollPosition = verticalScrollBar.getSelection();
				if (mouseCoords.y < AUTO_SCROLL_MARGIN) {
					int newScrollPosition = scrollPosition - AUTO_SCROLL_SPEED;
					animateScrollBar(verticalScrollBar, scrollPosition, newScrollPosition);
				} else if (mouseCoords.y > gallery.getBounds().height - AUTO_SCROLL_MARGIN) {
					int newScrollPosition = scrollPosition + AUTO_SCROLL_SPEED;
					animateScrollBar(verticalScrollBar, scrollPosition, newScrollPosition);
				}
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
				DropTarget dropTarget = (DropTarget) event.widget;
				Control control = dropTarget.getControl();
				Point coords = control.toControl(new Point(event.x, event.y));
				groupName = null;
				for (GalleryItem group : gallery.getItems()) {
					if (group.getBounds().contains(coords)) {
						groupName = group.getText();
						break;
					}
				}
				if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
					if (event.data instanceof String[]) {
						String[] files = (String[]) event.data;
						GalleryItem lastAddedItem = null;
						for (String file : files) {
							Image image = null;
							try {
								image = new Image(Display.getDefault(), file);
								String targetPath;
								
			                    Path filePath = Paths.get(file);
			                    BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
			                    LocalDate fileModifiedDate = LocalDate.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneId.systemDefault());
			                    LocalDate today = LocalDate.now();
			                    
			                    if (groupName == null) {
			                        if (fileModifiedDate.equals(today)) {
			                            groupName = today.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
			                        } else {
			                            groupName = fileModifiedDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
			                        }
			                    }
								targetPath = Tracker.makeDescriptorImage(aktuellerPatient) + File.separator + groupName;
								File targetDir = new File(targetPath);
								if (!targetDir.exists()) {
									targetDir.mkdirs();
								}
								File targetFile = new File(targetPath, new File(file).getName());
								if (targetFile.exists()) {
									CustomFileDialog dialog = new CustomFileDialog(gallery.getShell());
									int result = dialog.open();
									switch (result) {
									case CustomFileDialog.OVERWRITE_ID:
										try {
											copyFile(new File(file), targetFile);
											File thumbnailDirectory = new File(targetFile.getParent(), "thumbnails");
											if (!thumbnailDirectory.exists()) {
												thumbnailDirectory.mkdirs();
											}
											File thumbnailFile = new File(thumbnailDirectory, targetFile.getName());
											createOrUpdateThumbnail(targetFile, thumbnailFile);
											updateThumbnailInUI(thumbnailFile);
										} catch (IOException e) {
											LoggerFactory.getLogger(getClass()).warn("File no copy ", e);
										}
										break;
									case CustomFileDialog.RENAME_ID:
										InputDialog renameDialog = new InputDialog(gallery.getShell(),
												"Neuen Dateinamen eingeben",
												"Bitte geben Sie den neuen Dateinamen ein:", targetFile.getName(),
												null);
										if (renameDialog.open() == InputDialog.OK) {
											String newName = renameDialog.getValue();
											if (newName != null && !newName.trim().isEmpty()) {
												targetFile = new File(targetPath, newName);
												try {
													copyFile(new File(file), targetFile);
												} catch (IOException e) {
													LoggerFactory.getLogger(getClass()).warn("File no copy ", e);
												}
											}
										}
										break;
									case CustomFileDialog.CANCEL_ID:
										continue;
									}
								} else {
									try {
										copyFile(new File(file), targetFile);
									} catch (IOException e) {
										LoggerFactory.getLogger(getClass()).warn("File no copy ", e);
									}
								}
								GalleryItem parentGroup = gallery.getItem(new Point(event.x, event.y));
								GalleryItem newItem;
								if (parentGroup != null && parentGroup.getParentItem() == null) {
									newItem = new GalleryItem(parentGroup, SWT.NONE);
								} else {
									newItem = new GalleryItem(gallery, SWT.NONE);
								}
								newItem.setImage(image);
								newItem.setData(targetPath);
								createdImages.add(image);
								lastAddedItem = newItem;
							} catch (Exception e) {
								LoggerFactory.getLogger(getClass()).warn("Error processing image file", e);
								if (image != null && !image.isDisposed()) {
									image.dispose();
								}
							}
						}
						updateGalleryForPatient(aktuellerPatient);
						for (GalleryItem group : gallery.getItems()) {
							group.setExpanded(group.getText().equals(groupName));
						}
						if (lastAddedItem != null && Arrays.asList(gallery.getItems()).contains(lastAddedItem)) {
							gallery.setSelection(new GalleryItem[] { lastAddedItem });
						}
					}
				} else {
					String sourcePath = (String) event.data;
					GalleryItem targetGroup = gallery.getItem(new Point(event.x, event.y));
					if (targetGroup != null) {
				}
				}
			}
		});
	}

	private void deleteSelectedItem(GalleryItem selectedItem) {
		String filePath = (String) selectedItem.getData() + File.separator + selectedItem.getText();
		File fileToDelete = new File(filePath);
		File thumbnailToDelete = new File(fileToDelete.getParentFile(), "thumbnails/" + fileToDelete.getName());
		if (fileToDelete.exists() && fileToDelete.delete()) {
			if (thumbnailToDelete.exists()) {
				thumbnailToDelete.delete();
			}
			Image img = selectedItem.getImage();
			if (img != null && !img.isDisposed()) {
				img.dispose();
				createdImages.remove(img);
			}
			selectedItem.getParentItem().remove(selectedItem);
			File parentDir = fileToDelete.getParentFile();
			if (isEmptyDirectory(parentDir)) {
				parentDir.delete();
				updateGalleryForPatient(aktuellerPatient);
			}
		}
	}

	public void updateGalleryForPatient(Patient aktuellerPatient) {
		this.aktuellerPatient = aktuellerPatient;
		initializeGallery();
		processDirectories();
		updateUI();
	}

	private void initializeGallery() {
		disposeAllImages();
		gallery.removeAll();
	}

	private void processDirectories() {
		String mainDirectoryPath = Tracker.makeDescriptorImage(aktuellerPatient);
		File mainDirectory = new File(mainDirectoryPath);
		if (mainDirectory.exists() && mainDirectory.isDirectory()) {
			File[] groupDirectories = mainDirectory.listFiles(File::isDirectory);
			if (groupDirectories != null) {
				Arrays.sort(groupDirectories, (file1, file2) -> file2.getName().compareTo(file1.getName()));
				for (File groupDir : groupDirectories) {
					processGroupDirectory(groupDir);
				}
			}
		}
	}

	private void processGroupDirectory(File groupDir) {
		GalleryItem group = new GalleryItem(gallery, SWT.NONE);
		group.setText(groupDir.getName());
		group.setData(groupDir.getAbsolutePath());
		addImagesToGalleryFromDirectory(groupDir, group);
		File[] imageDirectories = groupDir.listFiles(File::isDirectory);
		if (imageDirectories != null && imageDirectories.length > 0) {
			for (File imgDir : imageDirectories) {
				addImagesToGalleryFromDirectory(imgDir, group);
			}
		}
		new HoverListener(group, Display.getCurrent().getSystemColor(SWT.COLOR_WHITE),
				Display.getCurrent().getSystemColor(SWT.COLOR_GRAY), 500, 500);
	}

	private void updateUI() {
		Display.getDefault().asyncExec(() -> {
			if (gallery.getItemCount() > 0) {
				GalleryItem firstGroup = gallery.getItem(0);
				firstGroup.setExpanded(true);
				if (firstGroup.getItemCount() > 0) {
					gallery.setSelection(new GalleryItem[] { firstGroup.getItem(0) });
				}
			}
		});

		gallery.redraw();
	}

	private void addImagesToGalleryFromDirectory(File dir, GalleryItem parentGroup) {
		File[] imageFiles = getImageFiles(dir);
		if (imageFiles == null || imageFiles.length == 0)
			return;
		List<File> imagesToProcess = processImageFiles(imageFiles, parentGroup);
		createThumbnails(imagesToProcess, parentGroup);
	}

	private File[] getImageFiles(File dir) {
		return dir.listFiles(file -> ThumbnailHandler.isSupportedImageFormat(file.getName()));
	}

	private List<File> processImageFiles(File[] imageFiles, GalleryItem parentGroup) {
		List<File> imagesToProcess = new ArrayList<>();
		for (File imgFile : imageFiles) {
			File parentDir = imgFile.getParentFile();
			if ("thumbnails".equals(parentDir.getName()))
				continue;

			File thumbnailDirectory = new File(parentDir, "thumbnails");
			File thumbnailFile = new File(thumbnailDirectory, imgFile.getName());
			if (!thumbnailFile.exists()) {
				imagesToProcess.add(imgFile);
			} else {
				addImageToGallery(imgFile, parentGroup, thumbnailFile);
			}
		}
		return imagesToProcess;
	}

	private void createThumbnails(List<File> imagesToProcess, GalleryItem parentGroup) {
		if (imagesToProcess.isEmpty())
			return;
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(gallery.getShell());
		try {
			dialog.run(true, true, monitor -> {
				monitor.beginTask("Thumbnails erstellen", imagesToProcess.size());
				for (File imgFile : imagesToProcess) {
					if (monitor.isCanceled())
						return;
					monitor.subTask("Erstelle Thumbnail für " + imgFile.getName());
					addImageToGallery(imgFile, parentGroup, null);
					monitor.worked(1);
				}
				monitor.done();
			});
		} catch (InvocationTargetException | InterruptedException e) {
			LoggerFactory.getLogger(getClass()).warn("Error when creating thumbnails", e);
		}
	}

	private void addImageToGallery(File imgFile, GalleryItem parentGroup, File thumbnailFile) {
		Display.getDefault().asyncExec(() -> {
			GalleryItem item = new GalleryItem(parentGroup, SWT.NONE);
			item.setText(imgFile.getName());
			item.setData(imgFile.getParentFile().getAbsolutePath());

			File finalThumbnailFile = thumbnailFile;
			if (finalThumbnailFile == null) {
				try {
					File originalImage = imgFile.getCanonicalFile();
					File thumbnailDirectory = new File(originalImage.getParentFile(), "thumbnails");
					if (!thumbnailDirectory.exists() && !thumbnailDirectory.mkdirs()) {
						return;
					}
					finalThumbnailFile = ThumbnailHandler.createThumbnail(originalImage);
				} catch (IOException e) {
					LoggerFactory.getLogger(getClass()).warn("Thumbnail could not be created", e);
				}
			}

			if (finalThumbnailFile != null) {
				Image thumbnailImage = new Image(Display.getDefault(), finalThumbnailFile.getAbsolutePath());
				item.setImage(thumbnailImage);
				createdImages.add(thumbnailImage);
			}
			gallery.redraw();
		});

	}

	public void removeAll() {
		gallery.removeAll();
	}

	private void disposeAllImages() {
		for (Image image : createdImages) {
			if (!image.isDisposed()) {
				image.dispose();
			}
		}
		createdImages.clear();
	}

	public void redraw() {
		gallery.redraw();
	}

	public Gallery getGallery() {
		return gallery;
	}

	public void copyFile(File source, File target) throws IOException {
		Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	private boolean isEmptyDirectory(File directory) {
		String[] contents = directory.list();
		return contents == null || contents.length == 0;
	}

	public class CustomFileDialog extends MessageDialog {
		public static final int OVERWRITE_ID = 0;
		public static final int RENAME_ID = 1;
		public static final int CANCEL_ID = 2;

		public CustomFileDialog(Shell parentShell) {
			super(parentShell, "Dateiaktion", null, "Die Datei existiert bereits. Was möchten Sie tun?",
					MessageDialog.QUESTION, new String[] { "Überschreiben", "Umbenennen", "Abbrechen" }, 0);
		}
		public int open() {
			return super.open();
		}
	}

	private void animateScrollBar(ScrollBar scrollBar, int start, int end) {
		AnimationRunner runner = new AnimationRunner();
		IMovement movement = new LinearInOut();
		int duration = 500;
		MoveScrollBarEffect effect = new MoveScrollBarEffect(scrollBar, start, end, duration, movement, null, null);
		runner.runEffect(effect);
	}

	public void createOrUpdateThumbnail(File originalImageFile, File thumbnailFile) throws IOException {
		ThumbnailHandler.createThumbnail(originalImageFile);
	}

	public void updateThumbnailInUI(File thumbnailFile) {
		Display.getDefault().asyncExec(() -> {
			for (GalleryItem item : gallery.getItems()) {
				if (item.getData().equals(thumbnailFile.getParent())
						&& item.getText().equals(thumbnailFile.getName())) {
					Image newThumbnailImage = new Image(Display.getDefault(), thumbnailFile.getAbsolutePath());
					item.setImage(newThumbnailImage);
					gallery.redraw();
					break;
				}
			}
		});
	}

	private boolean renameGroupDirectory(String oldName, String newName) {
		String basePath = Tracker.makeDescriptorImage(aktuellerPatient);
		File oldDir = new File(basePath, oldName);
		File newDir = new File(basePath, newName);

		if (!oldDir.exists() || newDir.exists()) {
			return false;
		}

		return oldDir.renameTo(newDir);
	}
}