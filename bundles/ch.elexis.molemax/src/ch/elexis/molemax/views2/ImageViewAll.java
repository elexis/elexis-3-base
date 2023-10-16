
package ch.elexis.molemax.views2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.animation.AnimationRunner;
import org.eclipse.nebula.animation.effects.MoveScrollBarEffect;
import org.eclipse.nebula.animation.movement.IMovement;
import org.eclipse.nebula.animation.movement.LinearInOut;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
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

public class ImageViewAll {
	private ImageOverview overviewInstance;
	private List<Image> createdImages = new ArrayList<>();
	private Patient aktuellerPatient;
	private static final int AUTO_SCROLL_MARGIN = 40;
	private static final int AUTO_SCROLL_SPEED = 75;
	private String groupName = null;
	private Gallery gallery;

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
						selection[0].dispose();
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
					Image selectedImage = selection[0].getImage();
					String folderPath = (String) selection[0].getData();
					String absoluteImagePath = (String) selection[0].getData() + "\\" + selection[0].getText();
					folderPath = remove_slot_from_path(folderPath);
					overviewInstance.showFullImage(selectedImage, folderPath, absoluteImagePath);
				}
			}
		});
		gallery.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					if (overviewInstance != null) {
						GalleryItem[] selection = gallery.getSelection();
						if (selection == null || selection.length == 0) {
							return;
						}
						if (selection.length > 0) {
							Image selectedImage = selection[0].getImage();
							String folderPath = (String) selection[0].getData();
							String absoluteImagePath = (String) selection[0].getData() + "\\" + selection[0].getText();
							folderPath = remove_slot_from_path(folderPath);
							overviewInstance.showFullImage(selectedImage, folderPath, absoluteImagePath);
						}
					}
				} else if (e.keyCode == SWT.ESC) {
					if (ImageViewAll.this.aktuellerPatient != null) {
						updateGalleryForPatient(ImageViewAll.this.aktuellerPatient);
					}
				} else if (e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_UP) {
					GalleryItem[] allItems = gallery.getItems();
					GalleryItem[] selection = gallery.getSelection();
					if (selection == null || selection.length == 0) {
						if (allItems != null && allItems.length > 0) {
							gallery.setSelection(new GalleryItem[] { allItems[0] });
							return;
						}
					}
					if (!gallery.isDisposed() && selection != null && selection.length > 0) {
						GalleryItem selectedItem = selection[0];
						if (selectedItem.getData() != null && !selectedItem.getData().toString().isEmpty()) {
							int selectedIndex = -1;
							for (int i = 0; allItems != null && i < allItems.length; i++) {
								if (allItems[i] == selectedItem) {
									selectedIndex = i;
									break;
								}
							}
							if (selectedIndex != -1) {
								int nextIndex = (e.keyCode == SWT.ARROW_DOWN) ? selectedIndex + 1 : selectedIndex - 1;
								if (allItems != null && nextIndex >= 0 && nextIndex < allItems.length) {
									gallery.setSelection(new GalleryItem[] { allItems[nextIndex] });
									return;
								}
							}
						}
					}
				} else if (e.keyCode == SWT.DEL) {
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
		gallery.setMenu(contextMenu);
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
								if (groupName == null) {
									groupName = java.time.LocalDate.now()
											.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
								}
								targetPath = Tracker.makeDescriptorImage(aktuellerPatient) + "\\" + groupName;
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
										} catch (IOException e) {
											LoggerFactory.getLogger(getClass()).warn("File no copy ", e);
											e.printStackTrace();
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
													e.printStackTrace();
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
										e.printStackTrace();
									}
								}
								GalleryItem parentGroup = gallery.getItem(new Point(event.x, event.y));
								if (parentGroup != null && parentGroup.getParentItem() == null) {
									GalleryItem newItem = new GalleryItem(parentGroup, SWT.NONE);
									newItem.setImage(image);
									newItem.setData(targetPath);
									createdImages.add(image);
									lastAddedItem = newItem;
								} else {
									GalleryItem newItem = new GalleryItem(gallery, SWT.NONE);
									newItem.setImage(image);
									newItem.setData(targetPath);
									createdImages.add(image);
									lastAddedItem = newItem;
								}
							} finally {
								if (image != null && !image.isDisposed()) {
									image.dispose();
								}
							}
						}
						updateGalleryForPatient(aktuellerPatient);
						for (GalleryItem group : gallery.getItems()) {
							if (group.getText().equals(groupName)) {
								group.setExpanded(true);
							} else {
								group.setExpanded(false);
							}
						}
						if (lastAddedItem != null && Arrays.asList(gallery.getItems()).contains(lastAddedItem)) {
							gallery.setSelection(new GalleryItem[] { lastAddedItem });
						}
					} else {
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
		File fileToDelete = new File((String) selectedItem.getData() + "\\" + selectedItem.getText());
		if (fileToDelete.exists() && fileToDelete.delete()) {
			selectedItem.getImage().dispose();
			createdImages.remove(selectedItem.getImage());
			selectedItem.getParentItem().remove(selectedItem);
			File parentDir = fileToDelete.getParentFile();
			if (isEmptyDirectory(parentDir)) {
				parentDir.delete();
				updateGalleryForPatient(aktuellerPatient);
			}
		}
	}

	private String remove_slot_from_path(String path) {
		String[] parts = path.split("\\\\");
		if (parts[parts.length - 1].matches("[0-9]+") && Integer.parseInt(parts[parts.length - 1]) >= 0
				&& Integer.parseInt(parts[parts.length - 1]) <= 11) {
			return String.join("\\\\", Arrays.copyOf(parts, parts.length - 1));
		} else {
			return path;
		}
	}

	public void updateGalleryForPatient(Patient aktuellerPatient) {
		this.aktuellerPatient = aktuellerPatient;
		disposeAllImages();
		gallery.removeAll();
		String mainDirectoryPath = Tracker.makeDescriptorImage(aktuellerPatient);
		File mainDirectory = new File(mainDirectoryPath);
		if (mainDirectory.exists() && mainDirectory.isDirectory()) {
			File[] groupDirectories = mainDirectory.listFiles(File::isDirectory);
			if (groupDirectories != null) {
				Arrays.sort(groupDirectories, (file1, file2) -> file2.getName().compareTo(file1.getName()));
				for (File groupDir : groupDirectories) {
					GalleryItem group = new GalleryItem(gallery, SWT.NONE);
					new HoverListener(group, Display.getCurrent().getSystemColor(SWT.COLOR_WHITE),
							Display.getCurrent().getSystemColor(SWT.COLOR_GRAY), 500, 500);
					group.setText(groupDir.getName());
					group.setData(groupDir.getAbsolutePath());
					addImagesToGalleryFromDirectory(groupDir, group);
					File[] imageDirectories = groupDir.listFiles(File::isDirectory);
					if (imageDirectories != null && imageDirectories.length > 0) {
						for (File imgDir : imageDirectories) {
							addImagesToGalleryFromDirectory(imgDir, group);
						}
					}
				}
			}
		}
		gallery.redraw();
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
				item.setData(dir.getAbsolutePath() != null ? dir.getAbsolutePath() : "");
			}
		}
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
		int duration = 100;
		MoveScrollBarEffect effect = new MoveScrollBarEffect(scrollBar, start, end, duration, movement, null, null);
		runner.runEffect(effect);
	}

}
