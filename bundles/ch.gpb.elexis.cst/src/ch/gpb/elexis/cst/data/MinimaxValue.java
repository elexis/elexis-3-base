/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst.data;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import ch.gpb.elexis.cst.service.CstService;

/**
 * @author daniel ludin ludin@swissonline.ch 27.06.2015
 *
 *         POJO for the display of the Minimal Maximal Result View
 */

public class MinimaxValue {

	double dRangeStart;
	double dRangeEnd;
	String sAbstract;

	private Date sDateStartOfSpan1;
	private Date sDateEndOfSpan1;

	private double dMaxOfSpan1;
	private double dMinOfSpan1;
	private Date sDateStartOfSpan2;
	private Date sDateEndOfSpan2;

	private double dMaxOfSpan2;
	private double dMinOfSpan2;
	private Date sDateStartOfSpan3;
	private Date sDateEndOfSpan3;

	private double dMinOfSpan3;
	private double dMaxOfSpan3;

	private String name = "no name set";
	private String text = StringUtils.EMPTY;

	public MinimaxValue(double dRangeStart, double dRangeEnd, String sAbstract, Date sDateStartOfSpan1,
			Date sDateEndOfSpan1, double dMaxOfSpan1, double dMinOfSpan1, Date sDateStartOfSpan2, Date sDateEndOfSpan2,
			double dMaxOfSpan2, double dMinOfSpan2, Date sDateStartOfSpan3, Date sDateEndOfSpan3, double dMinOfSpan3,
			double dMaxOfSpan3, String name) {
		super();
		this.dRangeStart = dRangeStart;
		this.dRangeEnd = dRangeEnd;
		this.sAbstract = sAbstract;

		this.sDateStartOfSpan1 = sDateStartOfSpan1;
		this.sDateEndOfSpan1 = sDateEndOfSpan1;
		this.dMaxOfSpan1 = dMaxOfSpan1;
		this.dMinOfSpan1 = dMinOfSpan1;
		this.sDateStartOfSpan2 = sDateStartOfSpan2;
		this.sDateEndOfSpan2 = sDateEndOfSpan2;
		this.dMaxOfSpan2 = dMaxOfSpan2;
		this.dMinOfSpan2 = dMinOfSpan2;
		this.sDateStartOfSpan3 = sDateStartOfSpan3;
		this.sDateEndOfSpan3 = sDateEndOfSpan3;
		this.dMinOfSpan3 = dMinOfSpan3;
		this.dMaxOfSpan3 = dMaxOfSpan3;
		this.name = name;
	}

	public double getMinOfSpan1() {
		return dMinOfSpan1;
	}

	public void setMinOfSpan1(double dMinOfSpan1) {
		this.dMinOfSpan1 = dMinOfSpan1;
	}

	public double getMinOfSpan2() {
		return dMinOfSpan2;
	}

	public void setMinOfSpan2(double dMinOfSpan2) {
		this.dMinOfSpan2 = dMinOfSpan2;
	}

	public double getMinOfSpan3() {
		return dMinOfSpan3;
	}

	public void setMinOfSpan3(double dMinOfSpan3) {
		this.dMinOfSpan3 = dMinOfSpan3;
	}

	public MinimaxValue() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateEndOfSpan1() {
		return sDateEndOfSpan1;
	}

	public void setDateEndOfSpan1(Date sDateEndOfSpan1) {
		this.sDateEndOfSpan1 = sDateEndOfSpan1;
	}

	public Date getDateEndOfSpan2() {
		return sDateEndOfSpan2;
	}

	public void setDateEndOfSpan2(Date sDateEndOfSpan2) {
		this.sDateEndOfSpan2 = sDateEndOfSpan2;
	}

	public Date getDateEndOfSpan3() {
		return sDateEndOfSpan3;
	}

	public void setDateEndOfSpan3(Date sDateEndOfSpan3) {
		this.sDateEndOfSpan3 = sDateEndOfSpan3;
	}

	public Date getDateStartOfSpan1() {
		return sDateStartOfSpan1;
	}

	public void setDateStartOfSpan1(Date sDateStartOfSpan1) {
		this.sDateStartOfSpan1 = sDateStartOfSpan1;
	}

	public double getMaxOfSpan1() {
		return dMaxOfSpan1;
	}

	public void setMaxOfSpan1(double dMaxOfSpan1) {
		this.dMaxOfSpan1 = dMaxOfSpan1;
	}

	public Date getDateStartOfSpan2() {
		return sDateStartOfSpan2;
	}

	public void setDateStartOfSpan2(Date sDateStartOfSpan2) {
		this.sDateStartOfSpan2 = sDateStartOfSpan2;
	}

	public double getMaxOfSpan2() {
		return dMaxOfSpan2;
	}

	public void setMaxOfSpan2(double dMaxOfSpan2) {
		this.dMaxOfSpan2 = dMaxOfSpan2;
	}

	public Date getDateStartOfSpan3() {
		return sDateStartOfSpan3;
	}

	public void setDateStartOfSpan3(Date sDateStartOfSpan3) {
		this.sDateStartOfSpan3 = sDateStartOfSpan3;
	}

	public double getMaxOfSpan3() {
		return dMaxOfSpan3;
	}

	public void setMaxOfSpan3(double dMaxOfSpan3) {
		this.dMaxOfSpan3 = dMaxOfSpan3;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("Spalte 1  von " + CstService.getCompactFromDate(sDateStartOfSpan1) + " bis "
				+ CstService.getCompactFromDate(sDateEndOfSpan1) + " Wert:" + dMaxOfSpan1 + "\r\n");
		result.append("Spalte 2  von " + CstService.getCompactFromDate(sDateStartOfSpan2) + " bis "
				+ CstService.getCompactFromDate(sDateEndOfSpan2) + " Wert:" + dMaxOfSpan2 + "\r\n");
		result.append("Spalte 3  von " + CstService.getCompactFromDate(sDateStartOfSpan3) + " bis "
				+ CstService.getCompactFromDate(sDateEndOfSpan3) + " Wert:" + dMaxOfSpan3 + "\r\n");

		return result.toString();

	}

	public double getRangeStart() {
		return dRangeStart;
	}

	public void setRangeStart(double dRangeStart) {
		this.dRangeStart = dRangeStart;
	}

	public double getRangeEnd() {
		return dRangeEnd;
	}

	public void setRangeEnd(double dRangeEnd) {
		this.dRangeEnd = dRangeEnd;
	}

	public String getAbstract() {
		return sAbstract;
	}

	public void setAbstract(String sAbstract) {
		this.sAbstract = sAbstract;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
