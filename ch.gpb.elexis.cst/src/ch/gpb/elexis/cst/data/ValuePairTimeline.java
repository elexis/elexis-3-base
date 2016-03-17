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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author daniel ludin ludin@swissonline.ch
 * 27.06.2015
 * 
 */

public class ValuePairTimeline {

    String sDate = new String(); // Compact Date yyyyMMdd
    double wert1 = 0;
    double wert2 = 0;

    public ValuePairTimeline(String sDate, double wert1, double wert2) {
	super();
	this.sDate = sDate;
	this.wert1 = wert1;
	this.wert2 = wert2;

    }

    public String getDate() {
	return sDate;
    }

    public void setDate(String sDate) {
	this.sDate = sDate;
    }

    public double getWert1() {
	return wert1;
    }

    public void setWert1(double syst) {
	this.wert1 = syst;
    }

    public double getWert2() {
	return wert2;
    }

    public void setWert2(double diast) {
	this.wert2 = diast;
    }

    public static double getSystValue(double limit) {
	Random rand = new Random();
	double random = rand.nextDouble();

	return random;

    }

    public static List<ValuePairTimeline> getTestData() {
	List<ValuePairTimeline> result = new ArrayList<ValuePairTimeline>();

	double syst = getSystValue(100);
	double diast = getSystValue(150);
	result.add(new ValuePairTimeline("20130223", Math.min(syst, diast), Math.max(syst, diast)));

	syst = getSystValue(100);
	diast = getSystValue(150);
	result.add(new ValuePairTimeline("20130523", Math.min(syst, diast), Math.max(syst, diast)));

	syst = getSystValue(100);
	diast = getSystValue(150);
	result.add(new ValuePairTimeline("20131114", Math.min(syst, diast), Math.max(syst, diast)));

	syst = getSystValue(100);
	diast = getSystValue(150);
	result.add(new ValuePairTimeline("20140223", Math.min(syst, diast), Math.max(syst, diast)));

	syst = getSystValue(100);
	diast = getSystValue(150);
	result.add(new ValuePairTimeline("20140617", Math.min(syst, diast), Math.max(syst, diast)));

	return result;
    }

    public static List<ValuePairTimeline> getTestData2() {
	List<ValuePairTimeline> result = new ArrayList<ValuePairTimeline>();

	double syst = getSystValue(100);
	double diast = getSystValue(150);
	result.add(new ValuePairTimeline("20130223", Math.min(syst, diast), Math.max(syst, diast)));

	syst = getSystValue(100);
	diast = getSystValue(150);
	result.add(new ValuePairTimeline("20130523", Math.min(syst, diast), Math.max(syst, diast)));

	syst = getSystValue(100);
	diast = getSystValue(150);
	result.add(new ValuePairTimeline("20131114", Math.min(syst, diast), Math.max(syst, diast)));

	syst = getSystValue(100);
	diast = getSystValue(150);
	result.add(new ValuePairTimeline("20140223", Math.min(syst, diast), Math.max(syst, diast)));

	syst = getSystValue(100);
	diast = getSystValue(150);
	result.add(new ValuePairTimeline("20140617", Math.min(syst, diast), Math.max(syst, diast)));

	syst = getSystValue(100);
	diast = getSystValue(150);
	result.add(new ValuePairTimeline("20140731", Math.min(syst, diast), Math.max(syst, diast)));

	syst = getSystValue(100);
	diast = getSystValue(150);
	result.add(new ValuePairTimeline("20140619", Math.min(syst, diast), Math.max(syst, diast)));

	syst = getSystValue(100);
	diast = getSystValue(150);
	result.add(new ValuePairTimeline("20141012", Math.min(syst, diast), Math.max(syst, diast)));

	syst = getSystValue(100);
	diast = getSystValue(150);
	result.add(new ValuePairTimeline("20141217", Math.min(syst, diast), Math.max(syst, diast)));

	return result;
    }

    public static List<ValuePairTimeline> getTestData3() {
	List<ValuePairTimeline> result = new ArrayList<ValuePairTimeline>();

	double syst = getSystValue(100);
	double diast = getSystValue(150);
	result.add(new ValuePairTimeline("20130223", Math.min(syst, diast), Math.max(syst, diast)));

	return result;
    }
}
