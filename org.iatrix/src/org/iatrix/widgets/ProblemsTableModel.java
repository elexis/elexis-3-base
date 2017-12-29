/*******************************************************************************
 * Copyright (c) 2007-2015, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *     Gerry Weirich - adapted for 2.1
 *     Niklaus Giger - small improvements, split into 20 classes
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.iatrix.Iatrix;
import org.iatrix.actions.IatrixEventHelper;
import org.iatrix.data.Problem;
import org.iatrix.util.Constants;
import org.iatrix.util.DateComparator;
import org.iatrix.util.NumberComparator;
import org.iatrix.util.StatusComparator;
import org.iatrix.views.JournalView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Prescription;
import ch.elexis.icpc.Episode;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableModel;
import de.kupzog.ktable.renderers.FixedCellRenderer;

public class ProblemsTableModel implements KTableModel {

	private Patient actPatient;

	private static Logger log = LoggerFactory.getLogger(ProblemsTableModel.class);
	private MyKTable problemsKTable;
	private Color highlightColor;
	private ProblemsTableColorProvider problemsTableColorProvider = new ProblemsTableColorProvider();
	private Object[] problems = null;

	private final Hashtable<Integer, Integer> colWidths = new Hashtable<>();
	private final Hashtable<Integer, Integer> rowHeights = new Hashtable<>();

	private final KTableCellRenderer fixedRenderer =
		new FixedCellRenderer(FixedCellRenderer.STYLE_PUSH | FixedCellRenderer.INDICATION_SORT
			| FixedCellRenderer.INDICATION_FOCUS | FixedCellRenderer.INDICATION_CLICKED);

	private final KTableCellRenderer textRenderer = new ProblemsTableTextCellRenderer();
	private final KTableCellRenderer imageRenderer = new ProblemsTableImageCellRenderer();
	private final KTableCellRenderer therapyRenderer = new ProblemsTableTherapyCellRenderer();

	private static final DateComparator DATE_COMPARATOR = new DateComparator();
	private static final NumberComparator NUMBER_COMPARATOR = new NumberComparator();
	private static final StatusComparator STATUS_COMPARATOR = new StatusComparator();
	private static Comparator<Problem> comparator = new DateComparator();

	private boolean highlightSelection = false;
	private boolean highlightRow = false;

	public void refresh(){
		//		problemsKTable.updateScrollbarVisibility();
		problemsKTable.redraw();
	}

	/**
	 * Base class for our cell editors Especially, we need to take care of heartbeat management
	 */
	abstract class BaseCellEditor extends KTableCellEditor {
		@Override
		public void open(KTable table, int col, int row, Rectangle rect){
			org.iatrix.util.Heartbeat.getInstance().setHeartbeatProblemEnabled(false);
			super.open(table, col, row, rect);
		}

		@Override
		public void close(boolean save){
			super.close(save);
			org.iatrix.util.Heartbeat.getInstance().setHeartbeatProblemEnabled(true);
		}
	}

	/**
	 * Replacement for KTableCellEditorText2 We don't want to have the editor vertically centered
	 *
	 * @author danlutz
	 *
	 */
	public class MyKTableCellEditorText2 extends BaseCellEditor {
		protected Text m_Text;

		protected KeyAdapter keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e){
				try {
					onKeyPressed(e);
				} catch (Exception ex) {
					ex.printStackTrace();
					// Do nothing
				}
			}
		};

		protected TraverseListener travListener = new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e){
				onTraverse(e);
			}
		};

		@Override
		public void open(KTable table, int col, int row, Rectangle rect){
			super.open(table, col, row, rect);
			m_Text.setText(m_Model.getContentAt(m_Col, m_Row).toString());
			m_Text.selectAll();
			m_Text.setVisible(true);
			m_Text.setFocus();
		}

		@Override
		public void close(boolean save){
			if (save)
				m_Model.setContentAt(m_Col, m_Row, m_Text.getText());
			m_Text.removeKeyListener(keyListener);
			m_Text.removeTraverseListener(travListener);
			super.close(save);
			m_Text = null;
		}

		@Override
		protected Control createControl(){
			m_Text = new Text(m_Table, SWT.NONE);
			m_Text.addKeyListener(keyListener);
			m_Text.addTraverseListener(travListener);
			return m_Text;
		}

		/**
		 * Implement In-Textfield navigation with the keys...
		 *
		 * @see de.kupzog.ktable.KTableCellEditor#onTraverse(org.eclipse.swt.events.TraverseEvent)
		 */
		@Override
		protected void onTraverse(TraverseEvent e){
			if (e.keyCode == SWT.ARROW_LEFT) {
				// handel the event within the text widget!
			} else if (e.keyCode == SWT.ARROW_RIGHT) {
				// handle the event within the text widget!
			} else
				super.onTraverse(e);
		}

		@Override
		protected void onKeyPressed(KeyEvent e){
			if ((e.character == '\r') && ((e.stateMask & SWT.SHIFT) == 0)) {
				close(true);
				// move one row below!
				// if (m_Row<m_Model.getRowCount())
				// m_Table.setSelection(m_Col, m_Row+1, true);
			} else
				super.onKeyPressed(e);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.kupzog.ktable.KTableCellEditor#setContent(java.lang.Object)
		 */
		@Override
		public void setContent(Object content){
			m_Text.setText(content.toString());
			m_Text.setSelection(content.toString().length());
		}
	}

	public class KTableDiagnosisCellEditor extends BaseCellEditor {
		private Combo combo;

		private final KeyAdapter keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e){
				try {
					onKeyPressed(e);
				} catch (Exception ex) {
					// Do nothing
				}
			}
		};

		private final TraverseListener travListener = new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e){
				onTraverse(e);
			}
		};

		@Override
		public void open(KTable table, int col, int row, Rectangle rect){
			super.open(table, col, row, rect);

			String text = "";
			Object obj = m_Model.getContentAt(m_Col, m_Row);
			if (obj instanceof Problem) {
				text = "test";
			}

			combo.setText(text);
			combo.setVisible(true);
			combo.setFocus();
		}

		@Override
		public void close(boolean save){
			if (save)
				m_Model.setContentAt(m_Col, m_Row, combo.getText());
			combo.removeKeyListener(keyListener);
			combo.removeTraverseListener(travListener);
			combo = null;
			super.close(save);
		}

		@Override
		protected Control createControl(){
			combo = new Combo(m_Table, SWT.DROP_DOWN);
			combo.addKeyListener(keyListener);
			combo.addTraverseListener(travListener);
			return combo;
		}

		/**
		 * Implement In-Textfield navigation with the keys...
		 *
		 * @see de.kupzog.ktable.KTableCellEditor#onTraverse(org.eclipse.swt.events.TraverseEvent)
		 */
		@Override
		protected void onTraverse(TraverseEvent e){
			if (e.keyCode == SWT.ARROW_LEFT) {
				// handel the event within the text widget!
			} else if (e.keyCode == SWT.ARROW_RIGHT) {
				// handle the event within the text widget!
			} else
				super.onTraverse(e);
		}

		/*
		 * overridden from superclass
		 */
		@Override
		public void setBounds(Rectangle rect){
			super.setBounds(new Rectangle(rect.x, rect.y, rect.width, rect.height));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.kupzog.ktable.KTableCellEditor#setContent(java.lang.Object)
		 */
		@Override
		public void setContent(Object content){
			combo.setText(content.toString());
		}

	}

	public class KTableTherapyCellEditor extends BaseCellEditor {
		private Text m_Text;

		private final KeyAdapter keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e){
				try {
					onKeyPressed(e);
				} catch (Exception ex) {
					// Do nothing
				}
			}
		};

		private final TraverseListener travListener = new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e){
				onTraverse(e);
			}
		};

		@Override
		public void open(KTable table, int col, int row, Rectangle rect){
			super.open(table, col, row, rect);

			String text = "";
			Object obj = m_Model.getContentAt(m_Col, m_Row);
			if (obj instanceof Problem) {
				Problem problem = (Problem) obj;
				text = problem.getProcedere();
			}

			m_Text.setText(PersistentObject.checkNull(text));
			m_Text.selectAll();
			m_Text.setVisible(true);
			m_Text.setFocus();
		}

		@Override
		public void close(boolean save){
			if (save)
				m_Model.setContentAt(m_Col, m_Row, m_Text.getText());
			m_Text.removeKeyListener(keyListener);
			m_Text.removeTraverseListener(travListener);
			m_Text = null;
			super.close(save);
		}

		@Override
		protected Control createControl(){
			m_Text = new Text(m_Table, SWT.MULTI | SWT.V_SCROLL);
			m_Text.addKeyListener(keyListener);
			m_Text.addTraverseListener(travListener);
			return m_Text;
		}

		/**
		 * Implement In-Textfield navigation with the keys...
		 *
		 * @see de.kupzog.ktable.KTableCellEditor#onTraverse(org.eclipse.swt.events.TraverseEvent)
		 */
		@Override
		protected void onTraverse(TraverseEvent e){
			if (e.keyCode == SWT.ARROW_LEFT) {
				// handel the event within the text widget!
			} else if (e.keyCode == SWT.ARROW_RIGHT) {
				// handle the event within the text widget!
			} else
				super.onTraverse(e);
		}

		/*
		 * overridden from superclass
		 */
		@Override
		public void setBounds(Rectangle rect){
			super.setBounds(new Rectangle(rect.x, rect.y, rect.width, rect.height));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.kupzog.ktable.KTableCellEditor#setContent(java.lang.Object)
		 */
		@Override
		public void setContent(Object content){
			m_Text.setText(content.toString());
			m_Text.setSelection(content.toString().length());
		}

	}

	class ProblemsTableImageCellRenderer extends ProblemsTableCellRendererBase {
		private final Display display;

		public ProblemsTableImageCellRenderer(){
			display = Display.getCurrent();
		}

		@Override
		public int getOptimalWidth(GC gc, int col, int row, Object content, boolean fixed,
			KTableModel model){
			if (content instanceof Image) {
				Image image = (Image) content;
				return image.getBounds().width;
			} else {
				return 0;
			}
		}

		@Override
		public void drawCell(GC gc, Rectangle rect, int col, int row, Object content, boolean focus,
			boolean fixed, boolean clicked, KTableModel model){
			Color backColor;
			Color borderColor;

			Image image = null;
			if (content instanceof Image) {
				image = (Image) content;
			}

			if (isSelected(row) && ((ProblemsTableModel) model).isHighlightRow()) {
				backColor = highlightColor;
			} else if (focus && ((ProblemsTableModel) model).isHighlightSelection()) {
				backColor = highlightColor;
			} else {
				backColor = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
			}

			borderColor = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

			gc.setForeground(borderColor);
			gc.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height);

			gc.setForeground(borderColor);
			gc.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height);

			gc.setBackground(backColor);

			gc.fillRectangle(rect);

			if (image != null) {
				// center image
				Rectangle imageBounds = image.getBounds();
				int imageWidth = imageBounds.width;
				int imageHeight = imageBounds.height;
				int xOffset = (rect.width - imageWidth) / 2;
				int yOffset = (rect.height - imageHeight) / 2;

				Rectangle oldClipping = gc.getClipping();
				gc.setClipping(rect);
				gc.drawImage(image, rect.x + xOffset, rect.y + yOffset);
				gc.setClipping(oldClipping);
			}

			if (focus) {
				gc.drawFocus(rect.x, rect.y, rect.width, rect.height);
			}
		}
	}

	abstract class ProblemsTableCellRendererBase implements KTableCellRenderer {
		protected Problem getSelectedProblem(){
			Point[] selection = problemsKTable.getCellSelection();
			if (selection == null || selection.length == 0) {
				return null;
			} else {
				int rowIndex = selection[0].y - getFixedHeaderRowCount();
				Problem problem = getProblem(rowIndex);
				return problem;
			}

		}

		protected boolean isSelected(int row){
			if (problemsKTable.isRowSelectMode()) {
				int[] selectedRows = problemsKTable.getRowSelection();
				if (selectedRows != null) {
					for (int r : selectedRows) {
						if (r == row) {
							return true;
						}
					}
				}
			} else {
				Point[] selectedCells = problemsKTable.getCellSelection();
				if (selectedCells != null) {
					for (Point cell : selectedCells) {
						if (cell.y == row) {
							return true;
						}
					}
				}
			}

			return false;
		}

	}

	class ProblemsTableTextCellRenderer extends ProblemsTableCellRendererBase {
		private final Display display;

		public ProblemsTableTextCellRenderer(){
			display = Display.getCurrent();
		}

		@Override
		public int getOptimalWidth(GC gc, int col, int row, Object content, boolean fixed,
			KTableModel model){
			if (content instanceof String) {
				String text = (String) content;
				return gc.textExtent(text).x + 8;
			} else {
				return 0;
			}
		}

		@Override
		public void drawCell(GC gc, Rectangle rect, int col, int row, Object content, boolean focus,
			boolean fixed, boolean clicked, KTableModel model){
			Color textColor;
			Color backColor;
			Color borderColor;

			String text;
			if (content instanceof String) {
				text = (String) content;
			} else {
				text = "";
			}

			if (focus) {
				textColor = display.getSystemColor(SWT.COLOR_BLUE);
			} else {
				textColor = problemsTableColorProvider.getForegroundColor(col, row);
			}

			if (isSelected(row) && ((ProblemsTableModel) model).isHighlightRow()) {
				backColor = highlightColor;
			} else if (focus && ((ProblemsTableModel) model).isHighlightSelection()) {
				backColor = highlightColor;
			} else {
				backColor = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
			}
			borderColor = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

			gc.setForeground(borderColor);
			gc.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height);

			gc.setForeground(borderColor);
			gc.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height);

			gc.setBackground(backColor);
			gc.setForeground(textColor);

			gc.fillRectangle(rect);
			Rectangle oldClipping = gc.getClipping();
			gc.setClipping(rect);
			gc.drawText((text), rect.x + 3, rect.y);
			gc.setClipping(oldClipping);

			if (focus) {
				gc.drawFocus(rect.x, rect.y, rect.width, rect.height);
			}
		}
	}

	/**
	 * Renderer for Therapy cell. Shows the procedere of the problem. If there are prescriptions,
	 * they are shown above the procedere, separated by a line.
	 *
	 * @author danlutz
	 */
	class ProblemsTableTherapyCellRenderer extends ProblemsTableCellRendererBase {
		private static final int MARGIN = 8;
		private static final int PADDING = 3;

		private final Display display;

		public ProblemsTableTherapyCellRenderer(){
			display = Display.getCurrent();
		}

		private boolean hasPrescriptions(Problem problem){
			List<Prescription> prescriptions = problem.getPrescriptions();
			return (prescriptions.size() > 0);
		}

		private boolean hasProcedere(Problem problem){
			if (!StringTool.isNothing(PersistentObject.checkNull(problem.getProcedere()))) {
				return true;
			} else {
				return false;
			}
		}

		private String getPrescriptionsText(Problem problem){
			String prescriptions = PersistentObject.checkNull(problem.getPrescriptionsAsText());
			String lineSeparator = System.getProperty("line.separator");
			String prescriptionsText =
				prescriptions.replaceAll(Problem.TEXT_SEPARATOR, lineSeparator);

			return prescriptionsText;
		}

		private String getProcedereText(Problem problem){
			return PersistentObject.checkNull(problem.getProcedere());
		}

		public int getOptimalHeight(GC gc, Problem problem){
			int height = 0;

			int prescriptionsHeight = 0;
			if (hasPrescriptions(problem)) {
				String prescriptionsText = getPrescriptionsText(problem);
				prescriptionsHeight = gc.textExtent(prescriptionsText).y;
			}

			int procedereHeight = 0;
			if (hasProcedere(problem)) {
				String procedereText = getProcedereText(problem);
				procedereHeight = gc.textExtent(procedereText).y;
			}

			if (prescriptionsHeight > 0 && procedereHeight > 0) {
				height = prescriptionsHeight + PADDING + procedereHeight;
			} else if (prescriptionsHeight > 0) {
				height = prescriptionsHeight;
			} else if (procedereHeight > 0) {
				height = procedereHeight;
			}

			if (height == 0) {
				// default height
				height = gc.textExtent("").y;
			}

			return height;
		}

		@Override
		public int getOptimalWidth(GC gc, int col, int row, Object content, boolean fixed,
			KTableModel model){
			if (content instanceof Problem) {
				Problem problem = (Problem) content;

				String prescriptionsText = getPrescriptionsText(problem);
				String procedereText = getProcedereText(problem);

				int width1 = gc.textExtent(prescriptionsText).x;
				int width2 = gc.textExtent(procedereText).x;
				int width = Math.max(width1, width2);

				return width + MARGIN;
			} else {
				return 0;
			}
		}

		@Override
		public void drawCell(GC gc, Rectangle rect, int col, int row, Object content, boolean focus,
			boolean fixed, boolean clicked, KTableModel model){
			Color textColor;
			Color backColor;
			Color borderColor;

			String prescriptionsText = "";
			String procedereText = "";
			boolean hasPrescriptions = false;
			boolean hasProcedere = false;

			if (content instanceof Problem) {
				Problem problem = (Problem) content;

				prescriptionsText = getPrescriptionsText(problem);
				procedereText = getProcedereText(problem);
				hasPrescriptions = hasPrescriptions(problem);
				hasProcedere = hasProcedere(problem);
			}

			if (focus) {
				textColor = display.getSystemColor(SWT.COLOR_BLUE);
			} else {
				textColor = problemsTableColorProvider.getForegroundColor(col, row);
			}

			if (isSelected(row) && ((ProblemsTableModel) model).isHighlightRow()) {
				backColor = highlightColor;
			} else if (focus && ((ProblemsTableModel) model).isHighlightSelection()) {
				backColor = highlightColor;
			} else {
				backColor = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
			}

			borderColor = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

			gc.setForeground(borderColor);
			gc.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height);

			gc.setForeground(borderColor);
			gc.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height);

			gc.setBackground(backColor);
			gc.setForeground(textColor);

			gc.fillRectangle(rect);

			Rectangle oldClipping = gc.getClipping();
			gc.setClipping(rect);

			if (hasPrescriptions && hasProcedere) {
				// draw prescriptions and procedre, separated by a line
				int prescriptionsHeight = gc.textExtent(prescriptionsText).y;

				gc.setForeground(borderColor);
				gc.drawLine(rect.x, rect.y + prescriptionsHeight + 1, rect.x + rect.width,
					rect.y + prescriptionsHeight + 1);

				gc.setBackground(backColor);
				gc.setForeground(textColor);

				gc.drawText(prescriptionsText, rect.x + 3, rect.y);
				gc.drawText(procedereText, rect.x + 3, rect.y + prescriptionsHeight + PADDING);
			} else {
				String text;
				if (hasPrescriptions) {
					// prescriptions only
					text = prescriptionsText;
				} else if (hasProcedere) {
					// procedere only
					text = procedereText;
				} else {
					// nothing
					text = "";
				}

				gc.setBackground(backColor);
				gc.setForeground(textColor);

				gc.drawText(text, rect.x + 3, rect.y);
			}

			gc.setClipping(oldClipping);

			if (focus) {
				gc.drawFocus(rect.x, rect.y, rect.width, rect.height);
			}
		}
	}

	/*
	 * Heartbeat activation management The heartbeat events are only processed if these variables
	 * are set to true. They may be set to false if heartbeat processing would distrub, e. g. in
	 * case of editing a problem or the consultation text.
	 */
	private boolean heartbeatProblemEnabled = true;

	private Konsultation actKons;

	public void heartbeatProblem(){
		log.debug("heartbeatProblem enabled " + heartbeatProblemEnabled);
		if (heartbeatProblemEnabled) {
			// backup selection

			boolean isRowSelectMode = problemsKTable.isRowSelectMode();

			Problem selectedProblem = null;
			int currentColumn = -1;

			if (isRowSelectMode) {
				// full row selection
				// not supported
			} else {
				// single cell selection

				Point[] cells = problemsKTable.getCellSelection();
				if (cells != null && cells.length > 0) {
					int row = cells[0].y;
					int rowIndex = row - getFixedHeaderRowCount();
					selectedProblem = getProblem(rowIndex);
					currentColumn = cells[0].x;
				}
			}

			// restore selection
			if (selectedProblem != null) {
				if (isRowSelectMode) {
					// full row selection
					// not supported
				} else {
					// single cell selection
					int rowIndex = getIndexOf(selectedProblem);
					if (rowIndex >= 0) {
						// problem found, i. e. still in list

						int row = rowIndex + getFixedHeaderRowCount();
						if (currentColumn == -1) {
							currentColumn = getFixedHeaderColumnCount();
						}
						problemsKTable.setSelection(currentColumn, row, true);
					}
				}
			}
		}
	}

	public Problem getProblem(int index){
		Problem problem = null;

		if (problems != null) {
			if (index >= 0 && index < problems.length) {
				Object element = problems[index];
				if (element instanceof Problem) {
					problem = (Problem) element;
				}
			}
		}

		return problem;
	}

	/**
	 * Finds the index of the given problem (array index, not row)
	 *
	 * @param problem
	 * @return the index, or -1 if not found
	 */
	public int getIndexOf(Problem problem){
		if (problems != null) {
			for (int i = 0; i < problems.length; i++) {
				Object element = problems[i];
				if (element instanceof Problem) {
					Problem p = (Problem) element;
					if (p.getId().equals(problem.getId())) {
						return i;
					}
				}
			}
		}

		return -1;
	}

	/**
	 * Returns the KTable index corresponding to our model index (mapping)
	 *
	 * @param rowIndex
	 *            the index of a problem
	 * @return the problem's index as a KTable index
	 */
	public int modelIndexToTableIndex(int rowIndex){
		return rowIndex + getFixedHeaderRowCount();

	}

	/**
	 * Returns the model index corresponding to the KTable index (mapping)
	 *
	 * @param row
	 *            the KTable index of a problem
	 * @return the problem's index of the model
	 */
	public int tableIndexToRowIndex(int row){
		return row - getFixedHeaderRowCount();
	}

	@Override
	public Point belongsToCell(int col, int row){
		return new Point(col, row);
	}

	@Override
	public KTableCellEditor getCellEditor(int col, int row){
		if (row < getFixedHeaderRowCount() || col < getFixedHeaderColumnCount()) {
			return null;
		}

		int colIndex = col - getFixedHeaderColumnCount();

		if (colIndex == Constants.BEZEICHNUNG || colIndex == Constants.NUMMER
			|| colIndex == Constants.DATUM) {
			return new MyKTableCellEditorText2();
		} else if (colIndex == Constants.THERAPIE) {
			return new KTableTherapyCellEditor();
		} else {
			return null;
		}
	}

	@Override
	public KTableCellRenderer getCellRenderer(int col, int row){
		if (row < getFixedHeaderRowCount() || col < getFixedHeaderColumnCount()) {
			return fixedRenderer;
		}

		int colIndex = col - getFixedHeaderColumnCount();

		if (colIndex == Constants.STATUS) {
			return imageRenderer;
		}

		if (colIndex == Constants.THERAPIE) {
			return therapyRenderer;
		}

		return textRenderer;
	}

	@Override
	public int getColumnCount(){
		return getFixedHeaderColumnCount() + Constants.COLUMN_TEXT.length;
	}

	@Override
	public int getRowCount(){
		loadElements();
		return getFixedHeaderRowCount() + problems.length;
	}

	@Override
	public int getFixedHeaderColumnCount(){
		return 1;
	}

	@Override
	public int getFixedSelectableColumnCount(){
		return 0;
	}

	@Override
	public int getFixedHeaderRowCount(){
		return 1;
	}

	@Override
	public int getFixedSelectableRowCount(){
		return 0;
	}

	private int getInitialColumnWidth(int col){
		if (col < getFixedHeaderColumnCount()) {
			return 20;
		}

		int colIndex = col - getFixedHeaderColumnCount();
		if (colIndex >= 0 && colIndex < Constants.COLUMN_TEXT.length) {
			int width =
				CoreHub.localCfg.get(Constants.COLUMN_CFG_KEY[colIndex], Constants.DEFAULT_COLUMN_WIDTH[colIndex]);
			return width;
		} else {
			// invalid column
			return 0;
		}
	}

	@Override
	public int getColumnWidth(int col){
		Integer width = colWidths.get(new Integer(col));
		if (width == null) {
			width = new Integer(getInitialColumnWidth(col));
			colWidths.put(new Integer(col), width);
		}

		return width.intValue();
	}

	private int getHeaderRowHeight(){
		// TODO
		return 22;
	}

	@Override
	public int getRowHeightMinimum(){
		// TODO
		return 10;
	}

	@Override
	public int getRowHeight(int row){
		Integer height = rowHeights.get(new Integer(row));
		if (height == null) {
			height = new Integer(getOptimalRowHeight(row));
			rowHeights.put(new Integer(row), height);
		}

		return height.intValue();
	}

	private int getOptimalRowHeight(int row){
		if (row < getFixedHeaderRowCount()) {
			return getHeaderRowHeight();
		} else {
			int height = 0;

			GC gc = new GC(problemsKTable);
			for (int i = 0; i < Constants.COLUMN_TEXT.length; i++) {
				int col = i + getFixedHeaderColumnCount();
				int currentHeight = 0;
				Object obj = getContentAt(col, row);
				if (obj instanceof String) {
					String text = (String) obj;
					currentHeight = gc.textExtent(text).y;
				} else if (obj instanceof Image) {
					Image image = (Image) obj;
					currentHeight = image.getBounds().height;
				} else if (obj instanceof Problem && i == Constants.THERAPIE) {
					Problem problem = (Problem) obj;
					ProblemsTableTherapyCellRenderer cellRenderer =
						(ProblemsTableTherapyCellRenderer) getCellRenderer(col, row);

					currentHeight = cellRenderer.getOptimalHeight(gc, problem);
				}

				if (currentHeight > height) {
					height = currentHeight;
				}
			}
			gc.dispose();

			return height;
		}
	}

	@Override
	public void setColumnWidth(int col, int width){
		colWidths.put(new Integer(col), new Integer(width));

		// store new column with in localCfg
		int colIndex = col - getFixedHeaderColumnCount();
		if (colIndex >= 0 && colIndex < Constants.COLUMN_TEXT.length) {
			CoreHub.localCfg.set(Constants.COLUMN_CFG_KEY[colIndex], width);
		}
	}

	@Override
	public void setRowHeight(int row, int height){
		rowHeights.put(new Integer(row), new Integer(height));
	}

	private void loadElements(){
		if (problems == null) {
			List<Object> elements = new ArrayList<>();

			if (actPatient != null) {
				List<Problem> problems = Problem.getProblemsOfPatient(actPatient);
				if (comparator != null) {
					Collections.sort(problems, comparator);
				}
				elements.addAll(problems);

				// add dummy element
				elements.add(new DummyProblem());
			}

			problems = elements.toArray();
		}
	}

	private void addElement(Object element){
		Object[] newProblems = new Object[problems.length + 1];
		System.arraycopy(problems, 0, newProblems, 0, problems.length);
		newProblems[newProblems.length - 1] = element;
	}

	public void reload(){
		// force elements to be reloaded
		problems = null;

		// force heights to be re-calculated
		rowHeights.clear();

	}

	private Object getHeaderContentAt(int col){
		int colIndex = col - getFixedHeaderColumnCount();

		if (colIndex >= 0 && colIndex < Constants.COLUMN_TEXT.length) {
			return Constants.COLUMN_TEXT[colIndex];
		} else {
			return "";
		}
	}

	@Override
	public Object getContentAt(int col, int row){
		if (row < getFixedHeaderRowCount()) {
			// header

			return getHeaderContentAt(col);
		}

		// rows

		// load problems if required
		loadElements();

		int colIndex = col - getFixedHeaderColumnCount();
		int rowIndex = row - getFixedHeaderRowCount(); // consider header row
		if (rowIndex >= 0 && rowIndex < problems.length) {
			Object element = problems[rowIndex];
			if (element instanceof Problem) {
				Problem problem = (Problem) element;

				String text;
				String lineSeparator;

				switch (colIndex) {
				case Constants.BEZEICHNUNG:
					return problem.getTitle();
				case Constants.NUMMER:
					return problem.getNumber();
				case Constants.DATUM:
					return problem.getStartDate();
				case Constants.DIAGNOSEN:
					String diagnosen = problem.getDiagnosenAsText();
					lineSeparator = System.getProperty("line.separator");
					text = diagnosen.replaceAll(Problem.TEXT_SEPARATOR, lineSeparator);
					return text;
				/*
				 * case GESETZ: return problem.getGesetz();
				 */
				/*
				 * case RECHNUNGSDATEN: return "not yet implemented";
				 */
				case Constants.THERAPIE:
					/*
					 * String prescriptions = problem.getPrescriptionsAsText(); lineSeparator =
					 * System.getProperty("line.separator"); text =
					 * prescriptions.replaceAll(Problem.TEXT_SEPARATOR, lineSeparator); return
					 * text;
					 */
					return problem;
				/*
				 * case PROCEDERE: return problem.getProcedere();
				 */
				case Constants.STATUS:
					if (problem.getStatus() == Episode.ACTIVE) {
						return UiDesk.getImage(Iatrix.IMG_ACTIVE);
					} else {
						return UiDesk.getImage(Iatrix.IMG_INACTIVE);
					}
				default:
					return "";
				}
			} else {
				// DummyProblem

				if (col < getFixedHeaderColumnCount()) {
					return "*";
				} else {
					return "";
				}
			}
		} else {
			// row index out of bound
			return "";
		}
	}

	@Override
	public String getTooltipAt(int col, int row){
		if (col < Constants.TOOLTIP_TEXT.length)
			return Constants.TOOLTIP_TEXT[col];
		else
			return "Tooltip für col " + col;
	}

	@Override
	public boolean isColumnResizable(int col){
		return true;
	}

	@Override
	public boolean isRowResizable(int row){
		return true;
	}

	@Override
	public void setContentAt(int col, int row, Object value){
		// don't do anything if there are no problems
		if (problems == null) {
			return;
		}

		// only accept String values
		if (!(value instanceof String)) {
			return;
		}

		String text = (String) value;

		int colIndex = col - getFixedHeaderColumnCount();
		int rowIndex = row - getFixedHeaderRowCount();

		if (rowIndex >= 0 && rowIndex < problems.length) {
			boolean isNew = false;

			Problem problem;
			if (problems[rowIndex] instanceof Problem) {
				problem = (Problem) problems[rowIndex];
			} else {
				// replace dummy object with real object

				if (actPatient == null) {
					// shuldn't happen; silently ignore
					return;
				}

				problem = new Problem(actPatient, "");
				String currentDate = new TimeTool().toString(TimeTool.DATE_ISO);
				problem.setStartDate(currentDate);
				IatrixEventHelper.fireSelectionEventProblem(problem);

				problems[rowIndex] = problem;

				addElement(new DummyProblem());

				isNew = true;
			}

			switch (colIndex) {
			case Constants.BEZEICHNUNG:
				problem.setTitle(text);
				break;
			case Constants.NUMMER:
				problem.setNumber(text);
				break;
			case Constants.DATUM:
				problem.setStartDate(text);
				break;
			case Constants.THERAPIE:
				problem.setProcedere(text);
				break;
			}

			if (isNew) {
				reload();
				refresh();
				problemsKTable.refresh();
			}
			JournalView.updateAllKonsAreas(actKons.getFall().getPatient(), actKons, IJournalArea.KonsActions.EVENT_UPDATE);
		}
	}

	public void setComparator(int col, int row){
		if (row < getFixedHeaderRowCount()) {
			int colIndex = col - getFixedHeaderColumnCount();

			switch (colIndex) {
			case Constants.DATUM:
				comparator = DATE_COMPARATOR;
				break;
			case Constants.NUMMER:
				comparator = NUMBER_COMPARATOR;
				break;
			case Constants.STATUS:
				comparator = STATUS_COMPARATOR;
				break;
			}
		}

	}

	public void setHighlightSelection(boolean highlight, boolean row){
		this.highlightSelection = highlight;
		this.highlightRow = row;
	}

	public boolean isHighlightSelection(){
		return highlightSelection;
	}

	public boolean isHighlightRow(){
		return highlightSelection && highlightRow;
	}

	class ProblemsTableColorProvider {
		public Color getForegroundColor(int col, int row){
			int rowIndex = row - getFixedHeaderRowCount();
			Problem problem = getProblem(rowIndex);
			if (problem != null && problem.getStatus() == Episode.ACTIVE) {
				return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
			} else {
				return Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
			}
		}
	}

	public KTable getProblemsKTable(){
		return problemsKTable;
	}

	public void setProblemsKTable(MyKTable problemsKTable2){
		problemsKTable = problemsKTable2;
	}

	static class DummyProblem {}

	public void setKons(Patient newPatient, Konsultation newKons) {
		actKons = newKons;
		actPatient = newPatient;
	}
}
