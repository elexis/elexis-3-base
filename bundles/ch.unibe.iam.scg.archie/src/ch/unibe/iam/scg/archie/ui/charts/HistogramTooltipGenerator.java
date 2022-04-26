/*******************************************************************************
 * Copyright (c) 2008 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *******************************************************************************/
package ch.unibe.iam.scg.archie.ui.charts;

import java.text.MessageFormat;
import java.text.NumberFormat;

import org.jfree.chart.labels.AbstractCategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.data.category.CategoryDataset;

/**
 *
 * <p>
 * Custom tooltip generator for the age histogram chart. This basically reverts
 * all negative values so that all tooltips in the age histogram have positive
 * values, even the ones on the negative (left) side.
 * </p>
 *
 * $Id: HistogramTooltipGenerator.java 705 2009-01-03 17:48:46Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 705 $
 */
public class HistogramTooltipGenerator extends AbstractCategoryItemLabelGenerator implements CategoryToolTipGenerator {

	/**
	 * For serialization.
	 */
	private static final long serialVersionUID = -52348756900309688L;

	/** The default format string. */
	public static final String DEFAULT_TOOL_TIP_FORMAT_STRING = "({0}, {1}) = {2}";

	/**
	 * @param labelFormat
	 * @param formatter
	 */
	protected HistogramTooltipGenerator() {
		super(HistogramTooltipGenerator.DEFAULT_TOOL_TIP_FORMAT_STRING, NumberFormat.getInstance());
	}

	/**
	 * @{inheritDoc
	 */
	public String generateToolTip(CategoryDataset dataset, int row, int column) {
		if (dataset == null) {
			throw new IllegalArgumentException("Null 'dataset' argument.");
		}
		String result = null;
		Object[] items = createItemArray(dataset, row, column);
		result = MessageFormat.format(this.getLabelFormat(), items);
		return result;
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected Object[] createItemArray(CategoryDataset dataset, int row, int column) {
		Object[] result = new Object[4];
		String nullValueString = "-";

		result[0] = dataset.getRowKey(row).toString();
		result[1] = dataset.getColumnKey(column).toString();

		Number value = dataset.getValue(row, column);

		if (value != null) {
			// flip negative numbers
			result[2] = value.doubleValue() < 0 ? -value.doubleValue() : value.doubleValue();
		} else {
			result[2] = nullValueString;
		}

		return result;
	}
}