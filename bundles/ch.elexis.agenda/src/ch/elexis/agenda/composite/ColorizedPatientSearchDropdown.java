package ch.elexis.agenda.composite;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ISticker;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.StickerServiceHolder;
import ch.elexis.core.types.Gender;
import ch.elexis.core.ui.e4.fieldassist.PatientSearchToken;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.icons.Images;

public class ColorizedPatientSearchDropdown {

	private Shell popup;
	private TableViewer viewer;
	private Text textWidget;
	private boolean isSelecting = false;
	private String lastSearchTerm = StringUtils.EMPTY;
	private Consumer<IPatient> onPatientSelected;

	public ColorizedPatientSearchDropdown(Text textWidget, Consumer<IPatient> onPatientSelected) {
		this.textWidget = textWidget;
		this.onPatientSelected = onPatientSelected;

		textWidget.addModifyListener(e -> {
			if (!isSelecting)
				triggerSearch();
		});

		textWidget.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_DOWN && popup != null && popup.isVisible()) {
					viewer.getTable().setFocus();
					if (viewer.getTable().getSelectionIndex() < 0 && viewer.getTable().getItemCount() > 0) {
						viewer.getTable().setSelection(0);
					}
				} else if (e.keyCode == SWT.ESC && popup != null) {
					close();
				}
			}
		});

		textWidget.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				textWidget.getDisplay().timerExec(150, () -> {
					if (popup != null && !popup.isDisposed() && !popup.isFocusControl()
							&& !viewer.getTable().isFocusControl() && !textWidget.isFocusControl()) {
						close();
					}
				});
			}
		});
	}

	private void triggerSearch() {
		String term = textWidget.getText().trim();
		if (term.length() < 1) {
			close();
			return;
		}
		if (term.equals(lastSearchTerm))
			return;
		lastSearchTerm = term;

		CompletableFuture.supplyAsync(() -> {
			IQuery<IPatient> query = CoreModelServiceHolder.get().getQuery(IPatient.class);
			List<PatientSearchToken> searchParts = PatientSearchToken
					.getPatientSearchTokens(term.toLowerCase().split(StringUtils.SPACE));
			searchParts.forEach(st -> st.apply(query));
			query.limit(50);
			return query.execute();
		}).thenAccept(results -> {
			textWidget.getDisplay().asyncExec(() -> {
				if (textWidget.isDisposed() || !term.equals(lastSearchTerm))
					return;
				show(results);
			});
		});
	}

	private void show(List<IPatient> results) {
		if (results.isEmpty()) {
			close();
			return;
		}

		if (popup == null || popup.isDisposed()) {
			popup = new Shell(textWidget.getShell(), SWT.ON_TOP | SWT.TOOL | SWT.RESIZE);
			popup.setLayout(new FillLayout());
			viewer = new TableViewer(popup, SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION);

			Table table = viewer.getTable();
			TableColumn column = new TableColumn(table, SWT.NONE);

			table.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					Rectangle area = table.getClientArea();
					column.setWidth(area.width);
				}
			});

			viewer.setContentProvider(ArrayContentProvider.getInstance());
			viewer.setLabelProvider(new PatientProposalLabelProvider());
			viewer.getTable().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					selectAndClose();
				}
			});
			viewer.getTable().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent e) {
					selectAndClose();
				}
			});
			viewer.getTable().addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
						selectAndClose();
					} else if (e.keyCode == SWT.ESC) {
						close();
						textWidget.setFocus();
					}
				}
			});
		}

		viewer.setInput(results);
		Point size = textWidget.getSize();
		// Fix for Linux/GTK: Calculate coordinates relative to the parent
		// to ignore the internal offsets of the SWT.SEARCH widget.
		Point location = textWidget.getParent().toDisplay(textWidget.getLocation().x,
				textWidget.getLocation().y + size.y);
		popup.setBounds(location.x, location.y, size.x, 200);
		popup.setVisible(true);
	}

	private void selectAndClose() {
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		if (!sel.isEmpty()) {
			IPatient pat = (IPatient) sel.getFirstElement();
			isSelecting = true;
			if (onPatientSelected != null) {
				onPatientSelected.accept(pat);
			}
			isSelecting = false;
		}
		close();
		textWidget.setFocus();
	}

	private void close() {
		if (popup != null && !popup.isDisposed()) {
			popup.dispose();
		}
	}

	private class PatientProposalLabelProvider extends LabelProvider implements IColorProvider {
		@Override
		public String getText(Object element) {
			if (element instanceof IPatient) {
				return ((IPatient) element).getLabel();
			}
			return element == null ? StringUtils.EMPTY : element.toString();
		}

		@Override
		public Image getImage(Object element) {
			if (element instanceof IPatient) {
				IPatient pat = (IPatient) element;
				ISticker sticker = StickerServiceHolder.get().getSticker(pat).orElse(null);
				if (sticker != null && sticker.getImage() != null) {
					return CoreUiUtil.getImageAsIcon(sticker.getImage());
				} else if (pat.getGender() != null) {
					if (pat.getGender().equals(Gender.MALE)) {
						return Images.IMG_MANN.getImage();
					} else {
						return Images.IMG_FRAU.getImage();
					}
				}
			}
			return null;
		}

		@Override
		public Color getBackground(Object element) {
			if (element instanceof IPatient) {
				IPatient pat = (IPatient) element;
				ISticker sticker = StickerServiceHolder.get().getSticker(pat).orElse(null);
				if (sticker != null && sticker.getBackground() != null) {
					return CoreUiUtil.getColorForString(sticker.getBackground());
				}
			}
			return null;
		}

		@Override
		public Color getForeground(Object element) {
			if (element instanceof IPatient) {
				IPatient pat = (IPatient) element;
				ISticker sticker = StickerServiceHolder.get().getSticker(pat).orElse(null);
				if (sticker != null && sticker.getForeground() != null) {
					return CoreUiUtil.getColorForString(sticker.getForeground());
				}
			}
			return null;
		}
	}
}