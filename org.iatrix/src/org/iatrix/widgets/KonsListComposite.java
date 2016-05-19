/*******************************************************************************
 * Copyright (c) 2007-2013, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *     G. Weirich - adapted to API- Changes
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ILayoutExtension;
import org.iatrix.data.Problem;
import org.iatrix.views.JournalView;
import org.iatrix.widgets.IJournalArea.KonsActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.data.Konsultation;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Verrechnet;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;

/**
 * Special composite with a special layout. Don't set a layout!
 *
 * @author danlutz
 */
public class KonsListComposite {
	private static Logger log = LoggerFactory.getLogger(org.iatrix.widgets.KonsListComposite.class);

	private final Composite composite;

	private final FormToolkit toolkit;

	private final Label loadingLabel;
	private final List<WidgetRow> widgetRows;
	private final Sash sashLeft;
	private final Sash sashRight;

	private static final String CFG_SASH_X_PERCENT_LEFT =
		"org.iatrix/widgets/konslistcomposite/sash_x_percent_left";
	private static final String CFG_SASH_X_PERCENT_RIGHT =
		"org.iatrix/widgets/konslistcomposite/sash_x_percent_right";
	private static final int SASH_X_DEFAULT_PERCENT_LEFT = 10;
	private static final int SASH_X_DEFAULT_PERCENT_RIGHT = 75;
	private static final int SASH_X_NOTSET = -1;
	// current horizontal sash position.
	private int currentSashXPercentLeft = SASH_X_NOTSET;
	private int currentSashXPercentRight = SASH_X_NOTSET;

	private static final String TEXT_NOT_SHOWN = "?";

	private List<KonsData> konsultationen;
	private static LabelProvider verrechnetLabelProvider;

	{
		verrechnetLabelProvider = new LabelProvider() {
			@Override
			public String getText(Object element){
				if (!(element instanceof Verrechnet)) {
					return "";
				}

				Verrechnet verrechnet = (Verrechnet) element;
				String name = verrechnet.getText();
				IVerrechenbar verrechenbar = verrechnet.getVerrechenbar();
				if (verrechenbar != null) {
					String vClass = verrechenbar.getClass().getName();
					if (vClass.equals("ch.elexis.data.TarmedLeistung")) {
						String nick = ((PersistentObject) verrechnet.getVerrechenbar()).get("Nick");
						if (!StringTool.isNothing(nick)) {
							name = nick;
						}
					}
				} else {
					// verrechenbar is null
					log.debug("Invalid Verrechenbar: " + verrechnet.getText());
				}

				StringBuilder sb = new StringBuilder();
				int z = verrechnet.getZahl();
				Money preis = new Money(verrechnet.getEffPreis()).multiply(z);
				sb.append(z).append(" ").append(name).append(" (").append(preis.getAmountAsString())
					.append(")");
				return sb.toString();
			}
		};
	}

	public KonsListComposite(Composite parent, FormToolkit toolkit){
		composite = toolkit.createComposite(parent);
		this.toolkit = toolkit;

		composite.setLayout(new MyLayout());

		loadingLabel = toolkit.createLabel(composite, "Lade Konsultationen...");
		loadingLabel.setVisible(false);

		widgetRows = new ArrayList<WidgetRow>();

		sashLeft = new Sash(composite, SWT.VERTICAL);
		sashLeft.setVisible(false);

		sashLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event){
				int sashX = event.x;
				currentSashXPercentLeft = absoluteToPercent(composite.getSize().x, sashX);
				CoreHub.localCfg.set(CFG_SASH_X_PERCENT_LEFT, currentSashXPercentLeft);
				composite.layout();
			}
		});

		sashRight = new Sash(composite, SWT.VERTICAL);
		sashRight.setVisible(false);

		sashRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event){
				int sashX = event.x;
				currentSashXPercentRight = absoluteToPercent(composite.getSize().x, sashX);
				CoreHub.localCfg.set(CFG_SASH_X_PERCENT_RIGHT, currentSashXPercentRight);
				composite.layout();
			}
		});
	}

	public void setLayoutData(Object layoutData){
		composite.setLayoutData(layoutData);
	}

	public void setKonsultationen(List<KonsData> konsultationen){
		this.konsultationen = konsultationen;
		refresh();
	}

	// refresh layout and all elements
	private void refresh(){
		// clear all widget rows
		for (WidgetRow row : widgetRows) {
			row.setKonsData(null);
		}

		List<WidgetRow> availableRows = new ArrayList<WidgetRow>();
		availableRows.addAll(widgetRows);

		if (konsultationen != null) {
			for (KonsData konsData : konsultationen) {
				WidgetRow row;
				if (availableRows.size() > 0) {
					row = availableRows.remove(0);
				} else {
					row = new WidgetRow(composite);
					widgetRows.add(row);
				}
				row.setKonsData(konsData);
			}

			loadingLabel.setVisible(false);
			sashLeft.setVisible(konsultationen.size() > 0);
			sashRight.setVisible(konsultationen.size() > 0);
		} else {
			loadingLabel.setVisible(true);
			sashLeft.setVisible(false);
			sashRight.setVisible(false);
		}

		composite.layout(true);
	}

	private int percentToAbsolute(int base, int percent){
		return base * percent / 100;
	}

	private int absoluteToPercent(int base, int absolute){
		return absolute * 100 / base;
	}

	/**
	 * This class encapsulates the required widgets for a row. It assumes that
	 */
	private class WidgetRow {
		Hyperlink hTitle;
		Label lFall;
		Text problems;
		EnhancedTextFieldRO etf;
		Text verrechnung;

		Label horizontalSeparator;

		KonsData konsData;

		// collect controls for disposal in dispose()
		List<Control> controls;

		WidgetRow(Composite parent){
			/*
			 * Important: Add all created controls to "controls" for later disposal.
			 */

			controls = new ArrayList<Control>();

			// header

			hTitle = toolkit.createHyperlink(parent, "", SWT.NONE);
			hTitle.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e){
					log.debug("linkActivated: " + e + " " + e.getSource());
					if (konsData != null) {
						JournalView.saveActKonst();
						JournalView.updateAllKonsAreas(null, KonsActions.ACTIVATE_KONS);
						log.debug(
							"fireSelectionEvent: rev. " + konsData.konsultation.getHeadVersion()
								+ " " + konsData.konsultation.getDatum() + " "
								+ konsData.konsultation.getFall().getPatient().getPersonalia() + " "
								+ konsData.konsultation);
						ElexisEventDispatcher.fireSelectionEvent(konsData.konsultation);
					}
				}
			});
			controls.add(hTitle);

			lFall = toolkit.createLabel(parent, "");
			controls.add(lFall);

			problems = toolkit.createText(parent, "", SWT.MULTI | SWT.READ_ONLY);
			controls.add(problems);

			etf = new EnhancedTextFieldRO(parent);
			controls.add(etf);
			toolkit.adapt(etf);

			verrechnung = toolkit.createText(parent, "", SWT.MULTI | SWT.READ_ONLY);
			controls.add(verrechnung);

			horizontalSeparator = toolkit.createLabel(parent, "", SWT.SEPARATOR | SWT.HORIZONTAL);
			controls.add(horizontalSeparator);

			konsData = null;
			showControls(false);
		}

		public void setKonsData(KonsData konsData){
			this.konsData = konsData;
			if (konsData != null) {
				showControls(true);
			} else {
				showControls(false);
			}
			refresh();
		}

		// set the text of the controls
		private void refresh(){
			if (konsData != null) {
				hTitle.setText(konsData.konsTitle);
				lFall.setText(konsData.fallTitle);
				problems.setText(konsData.problemsText);
				etf.setText(konsData.konsText);
				verrechnung.setText(konsData.verrechnungenText);
			} else {
				hTitle.setText("");
				lFall.setText("");
				problems.setText("");
				etf.setText("");
				verrechnung.setText("");
			}
		}

		void showControls(boolean visible){
			for (Control control : controls) {
				if (control != null) {
					control.setVisible(visible);
				}
			}
		}

		void dispose(){
			// dispose all used controls
			for (Control control : controls) {
				if (control != null) {
					control.dispose();
				}
			}
			controls.clear();

			konsData = null;
		}
	}

	public class MyLayout extends Layout implements ILayoutExtension {
		private static final int TITLE_SPACING = 2;
		private static final int ROW_SPACING = 4;

		// size caches
		private int minWidthCache = -1;
		private int maxWidthCache = -1;
		private Point labelSizeCache = null;
		private Point rowSizeTotalCache = null;

		// ILayoutExtension

		/**
		 * Computes the minimum width of the parent. All widgets capable of word wrapping should
		 * return the width of the longest word that cannot be broken any further.
		 *
		 * @param parent
		 *            the parent composite
		 * @param changed
		 *            <code>true</code> if the cached information should be flushed,
		 *            <code>false</code> otherwise.
		 * @return the minimum width of the parent composite
		 */
		@Override
		public int computeMinimumWidth(Composite parent, boolean changed){
			return computeMinimumMaximumWidth(parent, changed, false);
		}

		/**
		 * Computes the maximum width of the parent. All widgets capable of word wrapping should
		 * return the length of the entire text with wrapping turned off.
		 *
		 * @param parent
		 *            the parent composite
		 * @param changed
		 *            <code>true</code> if the cached information should be flushed,
		 *            <code>false</code> otherwise.
		 * @return the maximum width of the parent composite
		 */
		@Override
		public int computeMaximumWidth(Composite parent, boolean changed){
			return computeMinimumMaximumWidth(parent, changed, true);
		}

		private int computeMinimumMaximumWidth(Composite parent, boolean changed, boolean max){
			// use cached values
			if (!changed) {
				if (max) {
					if (maxWidthCache != -1) {
						return maxWidthCache;
					}
				} else {
					if (minWidthCache != -1) {
						return minWidthCache;
					}
				}
			}

			// clear caches
			maxWidthCache = -1;
			minWidthCache = -1;

			// recalculate

			int sashWidthLeft = sashLeft.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			int sashWidthRight = sashRight.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;

			int leftWidth = 0;
			int middleWidth = 0;
			int rightWidth = 0;
			int totalWidth = 0;

			for (WidgetRow row : widgetRows) {
				if (row.konsData == null) {
					// ignore
					continue;
				}

				int width;

				// for hTitle and lFall, min/max are identical
				width = row.hTitle.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed).x
					+ row.lFall.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed).x;
				if (width > totalWidth) {
					totalWidth = width;
				}

				// left control (problems)
				if (max) {
					width = row.problems.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
				} else {
					width = row.problems.computeSize(5, SWT.DEFAULT, true).x;
				}
				if (width > leftWidth) {
					leftWidth = width;
				}

				// middle control (etf)
				if (max) {
					width = row.etf.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
				} else {
					width = row.etf.computeSize(5, SWT.DEFAULT).x;
				}
				if (width > middleWidth) {
					middleWidth = width;
				}

				// right control (verrechnung)
				if (max) {
					width = row.verrechnung.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
				} else {
					width = row.verrechnung.computeSize(5, SWT.DEFAULT).x;
				}
				if (width > rightWidth) {
					rightWidth = width;
				}
			}

			int width = Math.max(totalWidth, leftWidth + middleWidth + rightWidth);
			width += sashWidthLeft + sashWidthRight;

			if (max) {
				maxWidthCache = width;
			} else {
				minWidthCache = width;
			}

			return width;
		}

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache){
			Point size = layout(false, flushCache);
			return size;
		}

		@Override
		protected void layout(Composite composite, boolean flushCache){
			layout(true, flushCache);
		}

		private Point layout(boolean move, boolean flushCache){
			int width = composite.getSize().x;

			if (loadingLabel.isVisible()) {
				return layoutLoadingLabel(move, width, flushCache);
			} else {
				return layoutRows(move, width, flushCache);
			}
		}

		private Point layoutLoadingLabel(boolean move, int width, boolean flushCache){
			Point size;

			if (!flushCache && labelSizeCache != null) {
				size = new Point(labelSizeCache.x, labelSizeCache.y);
			} else {
				size = loadingLabel.computeSize(width, SWT.DEFAULT, flushCache);
				labelSizeCache = new Point(size.x, size.y);
			}

			if (move) {
				loadingLabel.setSize(size);
			}

			return size;

		}

		/**
		 * Set caches to null if flushCache is true Remain caches if flushCache is false
		 *
		 * @param flushCache
		 *            true or false
		 */
		private void initializeCaches(boolean flushCache){
			if (flushCache) {
				rowSizeTotalCache = null;
			}
		}

		private Point layoutRows(boolean move, int width, boolean flushCache){
			initializeCaches(flushCache);

			if (!move && !flushCache && rowSizeTotalCache != null) {
				return new Point(rowSizeTotalCache.x, rowSizeTotalCache.y);
			}

			if (widgetRows == null) {
				rowSizeTotalCache = new Point(0, 0);
				return new Point(0, 0);
			}

			int sashWidthLeft = sashLeft.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			int sashWidthRight = sashRight.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;

			int sashXLeft;
			if (currentSashXPercentLeft != SASH_X_NOTSET) {
				sashXLeft = percentToAbsolute(width, currentSashXPercentLeft);
			} else {
				// not yet set

				int cfgSashXPercentLeft =
					CoreHub.localCfg.get(CFG_SASH_X_PERCENT_LEFT, SASH_X_NOTSET);
				if (cfgSashXPercentLeft != SASH_X_NOTSET && cfgSashXPercentLeft < 100) {
					sashXLeft = percentToAbsolute(width, cfgSashXPercentLeft);
				} else {
					// default
					sashXLeft = percentToAbsolute(width, SASH_X_DEFAULT_PERCENT_LEFT);
				}
			}

			int sashXRight;
			if (currentSashXPercentRight != SASH_X_NOTSET) {
				sashXRight = percentToAbsolute(width, currentSashXPercentRight);
			} else {
				// not yet set

				int cfgSashXPercentRight =
					CoreHub.localCfg.get(CFG_SASH_X_PERCENT_RIGHT, SASH_X_NOTSET);
				if (cfgSashXPercentRight != SASH_X_NOTSET && cfgSashXPercentRight < 100) {
					sashXRight = percentToAbsolute(width, cfgSashXPercentRight);
				} else {
					// default
					sashXRight = percentToAbsolute(width, SASH_X_DEFAULT_PERCENT_RIGHT);
				}
			}

			int leftX = 0;
			int middleX = sashXLeft + sashWidthLeft;
			int rightX = sashXRight + sashWidthRight;

			int leftWidth = sashXLeft;
			int middleWidth = sashXRight - middleX;
			int rightWidth = width - rightX;

			int y = 0;

			for (WidgetRow row : widgetRows) {
				if (row.konsData == null) {
					// ignore
					continue;
				}

				int currentHeight;

				Point konsTitleSize = row.hTitle.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
				Point fallTitleSize = row.lFall.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);

				int konsTitleWidth = konsTitleSize.x;
				int fallTitleWidth = Math.min(width - konsTitleWidth, fallTitleSize.x);
				currentHeight = Math.max(konsTitleSize.y, fallTitleSize.y);

				if (move) {
					row.hTitle.setBounds(leftX, y, konsTitleWidth, currentHeight);
					row.lFall.setBounds(width - fallTitleWidth, y, fallTitleWidth, currentHeight);

					// set z-order
					row.hTitle.moveAbove(sashLeft);
					row.lFall.moveAbove(sashRight);
				}

				y += currentHeight + TITLE_SPACING;

				Point problemsSize = row.problems.computeSize(leftWidth, SWT.DEFAULT, flushCache);
				Point etfSize = row.etf.computeSize(middleWidth, SWT.DEFAULT, flushCache);
				Point verrechnungSize =
					row.verrechnung.computeSize(rightWidth, SWT.DEFAULT, flushCache);
				currentHeight = Math.max(Math.max(problemsSize.y, etfSize.y), verrechnungSize.y);

				if (move) {
					row.problems.setBounds(leftX, y, leftWidth, currentHeight);
					row.etf.setBounds(middleX, y, middleWidth, currentHeight);
					row.verrechnung.setBounds(rightX, y, rightWidth, currentHeight);

					row.horizontalSeparator.setBounds(leftX, y + currentHeight, width, 1);

					// set z-order
					row.horizontalSeparator.moveAbove(sashLeft);
					row.horizontalSeparator.moveAbove(sashRight);
				}

				y += currentHeight + 1 + ROW_SPACING; // grow including border
			}

			int height = y - ROW_SPACING; // the last ROW_SPACING is too much

			if (move) {
				sashLeft.setBounds(sashXLeft, 0, sashWidthLeft, height);
				sashRight.setBounds(sashXRight, 0, sashWidthRight, height);
			}

			Point size = new Point(width, height);
			rowSizeTotalCache = new Point(width, height);
			return size;
		}
	}

	public static class KonsData {
		Konsultation konsultation;

		boolean showCharges;

		// cache fields
		String konsTitle;
		String fallTitle;
		String problemsText;
		String konsText;
		String verrechnungenText;

		public KonsData(Konsultation konsultation, boolean showCharges){
			this.konsultation = konsultation;
			this.showCharges = showCharges;

			updateCacheFields();
		}

		private void updateCacheFields(){
			if (konsultation != null) {
				String lineSeparator = System.getProperty("line.separator");

				konsTitle = konsultation.getLabel();
				fallTitle = konsultation.getFall().getLabel();

				List<Problem> problems = Problem.getProblemsOfKonsultation(konsultation);
				problemsText = assembleProblemsText(problems);

				konsText = konsultation.getEintrag().getHead();
				if (konsText == null) {
					konsText = "";
				}

				if (showCharges) {
					List<Verrechnet> leistungen = konsultation.getLeistungen();
					List<String> leistungenLabels = replaceBlocks(leistungen);

					StringBuffer sb = new StringBuffer();
					boolean isFirst = true;
					for (String leistungLabel : leistungenLabels) {
						if (isFirst) {
							isFirst = false;
						} else {
							sb.append(lineSeparator);
						}
						sb.append(leistungLabel);
					}

					verrechnungenText = sb.toString();
				} else {
					verrechnungenText = TEXT_NOT_SHOWN;
				}
			} else {
				konsTitle = "";
				fallTitle = "";
				problemsText = "";
				konsText = "";
				verrechnungenText = "";
			}
		}

		private String assembleProblemsText(List<Problem> problems){
			String lineSeparator = System.getProperty("line.separator");

			StringBuffer sb = new StringBuffer();
			if (problems != null) {
				boolean isFirst = true;
				for (Problem problem : problems) {
					if (isFirst) {
						isFirst = false;
					} else {
						sb.append(lineSeparator);
					}

					sb.append(problem.getTitle());
				}
			}

			return sb.toString();
		}

		private List<String> replaceBlocks(List<Verrechnet> leistungen){
			List<String> labels = new ArrayList<String>();

			/*
			 * List<Verrechnet> unassigned = new ArrayList<Verrechnet>();
			 * unassigned.addAll(leistungen); List<Verrechnet> assigned = new
			 * ArrayList<Verrechnet>();
			 */

			// TODO consider number of elements in blocks
			/*
			 * Query<Leistungsblock> query = new Query<Leistungsblock>(Leistungsblock.class);
			 * query.orderBy(false, "Name"); List<Leistungsblock> blocks = query.execute(); if
			 * (blocks != null) { for (Leistungsblock block : blocks) { if
			 * (containsBlock(unassigned, block)) { removeBlock(unassigned, block); // TODO sum
			 * labels.add(block.getName()); } } }
			 */

			// add remaining leistungen
			// for (Verrechnet leistung : unassigned) {

			for (Verrechnet leistung : leistungen) {
				labels.add(verrechnetLabelProvider.getText(leistung));
			}

			return labels;
		}
	}
}
