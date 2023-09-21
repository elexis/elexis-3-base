
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
import org.eclipse.swt.widgets.Shell;

import ch.elexis.data.Patient;
import ch.elexis.molemax.Messages;

public class ImageViewAll {
	private ImageOverview overviewInstance;
	private List<Image> createdImages = new ArrayList<>();
	private Patient aktuellerPatient;
	private static final int TOP_SCROLL_THRESHOLD = 20;
	private static final int BOTTOM_SCROLL_THRESHOLD = 20;

	public void setOverviewInstance(ImageOverview overview) {
		this.overviewInstance = overview;
	}

	String groupName = null;

	private Gallery gallery;

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
//		gr.setAnimationLength(120);
		gallery.setGroupRenderer(gr);
		DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();
		// Adding hover effect to the Gallery

		gallery.setItemRenderer(ir);

		DragSource source = new DragSource(gallery, DND.DROP_MOVE | DND.DROP_COPY);
		source.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		source.addDragListener(new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				Display.getCurrent().timerExec(100, scroller);

				// Überprüfen, ob sich ein Bild unter den aktuellen Mauskoordinaten befindet.
				Point mouseCoords = Display.getCurrent().getCursorLocation();
				mouseCoords = gallery.toControl(mouseCoords); // Konvertiert die globalen Koordinaten in lokale
																// Koordinaten der Galerie
				GalleryItem itemUnderMouse = gallery.getItem(mouseCoords);
				if (itemUnderMouse == null) {
					event.doit = false; // Abbrechen des Drag-Vorgangs, da sich kein Bild unter dem Mauszeiger befindet.
					return;
				}

				// Überprüfen, ob Bilder in der Galerie vorhanden sind.
				GalleryItem[] allItems = gallery.getItems();
				if (allItems == null || allItems.length == 0) {
					event.doit = false; // Abbrechen des Drag-Vorgangs, da keine Bilder vorhanden sind.
					return;
				}

				GalleryItem[] selection = gallery.getSelection();
				if (selection.length > 0) {
					File imgFile = new File((String) selection[0].getData(), selection[0].getText());
					event.data = new String[] { imgFile.getAbsolutePath() }; // Setze den Pfad des Bildes als Daten
				}
			}

			public void dragSetData(DragSourceEvent event) {

				GalleryItem[] selection = gallery.getSelection();
				if (selection.length > 0) {
					File imgFile = new File((String) selection[0].getData(), selection[0].getText());
					event.data = new String[] { imgFile.getAbsolutePath() };
					// setze den Pfad des Bildes als Daten
				}
			}

			public void dragFinished(DragSourceEvent event) {

				Display.getCurrent().timerExec(-1, scroller);
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

			private Runnable scroller = new Runnable() {
				@Override
				public void run() {
					Point cursorLocation = Display.getCurrent().getCursorLocation();
					Point galleryLocation = gallery.toDisplay(0, 0);
					int yOffset = cursorLocation.y - galleryLocation.y;

					// Wenn sich das gezogene Element nahe dem oberen Rand befindet
					if (yOffset < TOP_SCROLL_THRESHOLD) {
						gallery.getVerticalBar().setSelection(gallery.getVerticalBar().getSelection() - 10);
					}
					// Wenn sich das gezogene Element nahe dem unteren Rand befindet
					else if (yOffset > gallery.getBounds().height - BOTTOM_SCROLL_THRESHOLD) {
						gallery.getVerticalBar().setSelection(gallery.getVerticalBar().getSelection() + 10);
					}
					gallery.redraw();
					gallery.update();

					// Timer erneut planen, um weiter zu prüfen
					Display.getCurrent().timerExec(100, this);
				}
			};

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

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) { // Reagiert auf die Enter-Taste
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
				} else if (e.keyCode == SWT.ESC) { // Reagiert auf die ESC-Taste
					if (ImageViewAll.this.aktuellerPatient != null) {
						updateGalleryForPatient(ImageViewAll.this.aktuellerPatient);
					}
				} else if (e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_UP) {

					GalleryItem[] allItems = gallery.getItems();
					GalleryItem[] selection = gallery.getSelection();

					// Überprüfen, ob aktuell ein Element ausgewählt ist
					if (selection == null || selection.length == 0) {
						// Wenn nicht, das erste Bild auswählen
						if (allItems != null && allItems.length > 0) {
							gallery.setSelection(new GalleryItem[] { allItems[0] });
							return; // Brechen Sie den weiteren Codeablauf ab, da Sie bereits eine Auswahl getroffen
									// haben
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
								}
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

			@Override
			public void widgetSelected(SelectionEvent e) {
				GalleryItem[] selection = gallery.getSelection();
				if (selection.length > 0) {
					// Bestätigungsnachricht anzeigen
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

		gallery.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) { // Entf-Taste
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
					event.detail = (event.operations & DND.DROP_MOVE) != 0 ? DND.DROP_MOVE : DND.DROP_NONE;
				}
			}

			public void dragOver(DropTargetEvent event) {

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

								targetPath = ImageTracker.makeDescriptor(aktuellerPatient) + "\\" + groupName;

								File targetDir = new File(targetPath);
								if (!targetDir.exists()) {
									targetDir.mkdirs();
								}

								File targetFile = new File(targetPath, new File(file).getName());
								// Überprüfen, ob die Datei bereits existiert
								if (targetFile.exists()) {
									CustomFileDialog dialog = new CustomFileDialog(gallery.getShell());
									int result = dialog.open();
									switch (result) {
									case CustomFileDialog.OVERWRITE_ID:
										// Direkt überschreiben
										try {
											copyFile(new File(file), targetFile);
										} catch (IOException e) {
											e.printStackTrace();
										}
										break;
									case CustomFileDialog.RENAME_ID:
										// Code, um die Datei umzubenennen
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
													e.printStackTrace();
												}
											}
										}
										break;

									case CustomFileDialog.CANCEL_ID:
										// Code, um die Aktion abzubrechen
										continue;
									}
								} else {
									try {
										copyFile(new File(file), targetFile);
									} catch (IOException e) {
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
							// Überprüfen Sie, ob diese Gruppe die Zielgruppe ist
							if (group.getText().equals(groupName)) {
								// Diese Gruppe sollte erweitert werden
								group.setExpanded(true);
							} else {
								// Alle anderen Gruppen sollten minimiert werden
								group.setExpanded(false);
							}
						}
						if (lastAddedItem != null && Arrays.asList(gallery.getItems()).contains(lastAddedItem)) {
							gallery.setSelection(new GalleryItem[] { lastAddedItem });
						}

					} else {

					}

				} else {

					String sourcePath = (String) event.data; // Das könnte zu einem Fehler führen, da event.data
																// möglicherweise nicht ein String ist.
					GalleryItem targetGroup = gallery.getItem(new Point(event.x, event.y));
					if (targetGroup != null) {
						// ... [Hier könnte weiterer Code eingefügt werden, wenn nötig] ...
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

			// Überprüfen, ob der übergeordnete Ordner jetzt leer ist und ihn ggf. löschen
			File parentDir = fileToDelete.getParentFile();
			if (isEmptyDirectory(parentDir)) {
				parentDir.delete();

				// Galerie aktualisieren, um die Änderungen zu reflektieren
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

		String mainDirectoryPath = ImageTracker.makeDescriptor(aktuellerPatient);
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

					// Zuerst Bilder aus dem Gruppenverzeichnis hinzufügen
					addImagesToGalleryFromDirectory(groupDir, group);

					// Dann Bilder aus den Unterordnern des Gruppenverzeichnisses hinzufügen
					File[] imageDirectories = groupDir.listFiles(File::isDirectory);
					if (imageDirectories != null && imageDirectories.length > 0) {

						for (File imgDir : imageDirectories) {

							addImagesToGalleryFromDirectory(imgDir, group);
						}
					} else {

					}
				}
			} else {

			}
		} else {

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
}
