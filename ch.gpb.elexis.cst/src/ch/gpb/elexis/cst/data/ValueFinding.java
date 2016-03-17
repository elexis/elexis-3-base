package ch.gpb.elexis.cst.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ValueFinding {

    Date dateOfFinding;
    String sParam;
    double value;
    double refMstart;
    double refMend;
    double refFstart;
    double refFend;
    static ArrayList<Timestamp> dates;

    public ValueFinding() {
	super();

    }

    public ValueFinding(Date dateOfFinding, String sParam, double value,
	    double refMstart, double refMend, double refFstart, double refFend) {
	super();
	this.dateOfFinding = dateOfFinding;
	this.sParam = sParam;
	this.value = value;
	this.refMstart = refMstart;
	this.refMend = refMend;
	this.refFstart = refFstart;
	this.refFend = refFend;
    }

    public static List<ValueFinding> getFindings1() {

	initializeDates();

	ArrayList<ValueFinding> result = new ArrayList<ValueFinding>();

	for (Timestamp t : dates) {
	    ValueFinding finding = new ValueFinding();
	    finding.setParam("Parameter-1");
	    finding.setDateOfFinding(t);

	    finding.setRefMstart(150);
	    finding.setRefMend(250);

	    finding.setRefFstart(100);
	    finding.setRefFend(200);
	    finding.setValue(randInt(100, 200));
	    result.add(finding);
	}

	return result;
    }

    public static List<ValueFinding> getFindings2() {

	initializeDates();

	ArrayList<ValueFinding> result = new ArrayList<ValueFinding>();

	for (Timestamp t : dates) {
	    ValueFinding finding = new ValueFinding();
	    finding.setParam("Parameter-2");
	    finding.setDateOfFinding(t);

	    finding.setRefMstart(15);
	    finding.setRefMend(25);

	    finding.setRefFstart(10);
	    finding.setRefFend(20);
	    finding.setValue(randInt(15, 25));
	    result.add(finding);
	}

	return result;
    }

    public static List<ValueFinding> getFindings3() {

	initializeDates();

	ArrayList<ValueFinding> result = new ArrayList<ValueFinding>();

	for (Timestamp t : dates) {
	    //System.out.println("t: " + t.toGMTString());

	    ValueFinding finding = new ValueFinding();
	    finding.setParam("Parameter-3");
	    finding.setDateOfFinding(t);

	    finding.setRefMstart(15);
	    finding.setRefMend(25);

	    finding.setRefFstart(10);
	    finding.setRefFend(20);
	    finding.setValue(randInt(10, 20));
	    result.add(finding);

	}

	return result;
    }

    public static List<ValueFinding> getFindings4() {

	initializeDates();

	ArrayList<ValueFinding> result = new ArrayList<ValueFinding>();

	for (Timestamp t : dates) {
	    //System.out.println("t: " + t.toGMTString());

	    ValueFinding finding = new ValueFinding();
	    finding.setParam("Parameter-4");
	    finding.setDateOfFinding(t);

	    finding.setRefMstart(1500);
	    finding.setRefMend(2500);

	    finding.setRefFstart(1000);
	    finding.setRefFend(2000);
	    finding.setValue(randInt(1400, 2800));
	    result.add(finding);

	}

	return result;
    }

    public static List<ValueFinding> getFindings5() {

	initializeDates();

	ArrayList<ValueFinding> result = new ArrayList<ValueFinding>();

	for (Timestamp t : dates) {
	    //System.out.println("t: " + t.toGMTString());

	    ValueFinding finding = new ValueFinding();
	    finding.setParam("Parameter-5");
	    finding.setDateOfFinding(t);

	    finding.setRefMstart(1.5);
	    finding.setRefMend(12.5);

	    finding.setRefFstart(1.5);
	    finding.setRefFend(8.5);
	    double dRand = randDouble(0.5, 14.5);
	    dRand = roundToDecimals(dRand, 2);
	    finding.setValue(dRand);
	    result.add(finding);

	}

	return result;
    }

    public static void initializeDates() {
	if (dates == null || dates.size() == 0) {

	    ArrayList<Timestamp> newdates = new ArrayList<Timestamp>();
	    for (int idx = 1; idx <= 5; ++idx) {

		long offset = Timestamp.valueOf("2012-01-01 00:00:00").getTime();
		long end = Timestamp.valueOf("2014-11-28 00:00:00").getTime();
		long diff = end - offset + 1;
		Timestamp rand = new Timestamp(offset + (long) (Math.random() * diff));

		newdates.add(rand);
	    }

	    dates = (ArrayList<Timestamp>) newdates.clone();
	    Collections.sort(dates);
	}
    }

    public static List<ValueFinding> getFindings(String parameter) {

	ArrayList<ValueFinding> result = new ArrayList<ValueFinding>();

	ArrayList<Timestamp> dates = new ArrayList<Timestamp>();
	Random randomGenerator = new Random();

	for (int idx = 1; idx <= 7; ++idx) {

	    long offset = Timestamp.valueOf("2012-01-01 00:00:00").getTime();
	    long end = Timestamp.valueOf("2014-11-28 00:00:00").getTime();
	    long diff = end - offset + 1;
	    Timestamp rand = new Timestamp(offset
		    + (long) (Math.random() * diff));

	    // System.out.println("Done."+ rand.toGMTString());
	    dates.add(rand);
	}

	Collections.sort(dates);

	for (Timestamp t : dates) {
	    System.out.println("t: " + t.toGMTString());

	    ValueFinding finding = new ValueFinding();
	    finding.setParam("Parameter-1");
	    finding.setDateOfFinding(t);

	    finding.setRefMstart(150);
	    finding.setRefMend(250);

	    finding.setRefFstart(100);
	    finding.setRefFend(200);
	    finding.setValue(randInt(100, 200));
	    result.add(finding);

	    finding = new ValueFinding();
	    finding.setParam("Parameter-2");
	    finding.setDateOfFinding(t);

	    finding.setRefMstart(15);
	    finding.setRefMend(25);

	    finding.setRefFstart(10);
	    finding.setRefFend(20);
	    finding.setValue(randInt(15, 25));
	    result.add(finding);

	    finding = new ValueFinding();
	    finding.setParam("Parameter-3");
	    finding.setDateOfFinding(t);

	    finding.setRefMstart(15);
	    finding.setRefMend(25);

	    finding.setRefFstart(10);
	    finding.setRefFend(20);
	    finding.setValue(randInt(10, 20));
	    result.add(finding);

	    finding = new ValueFinding();
	    finding.setParam("Parameter-4");
	    finding.setDateOfFinding(t);

	    finding.setRefMstart(1500);
	    finding.setRefMend(2500);

	    finding.setRefFstart(1000);
	    finding.setRefFend(2000);
	    finding.setValue(randInt(1400, 2800));
	    result.add(finding);

	    finding = new ValueFinding();
	    finding.setParam("Parameter-5");
	    finding.setDateOfFinding(t);

	    finding.setRefMstart(1.5);
	    finding.setRefMend(12.5);

	    finding.setRefFstart(1.5);
	    finding.setRefFend(8.5);
	    double dRand = randDouble(0.5, 14.5);
	    dRand = roundToDecimals(dRand, 2);
	    finding.setValue(dRand);
	    result.add(finding);

	}

	return result;
    }

    public static int randInt(int min, int max) {

	// NOTE: Usually this should be a field rather than a method
	// variable so that it is not re-seeded every call.
	Random rand = new Random();

	// nextInt is normally exclusive of the top value,
	// so add 1 to make it inclusive
	int randomNum = rand.nextInt((max - min) + 1) + min;

	return randomNum;
    }

    public static double randDouble(double min, double max) {

	double random = new Random().nextDouble();
	double result = min + (random * (max - min));
	System.out.println(result);

	return result;
    }

    public static double roundToDecimals(double d, int c) {
	int temp = (int) (d * Math.pow(10, c));
	return ((double) temp) / Math.pow(10, c);
    }

    public Date getDateOfFinding() {
	return dateOfFinding;
    }

    public void setDateOfFinding(Date dateOfFinding) {
	this.dateOfFinding = dateOfFinding;
    }

    public double getValue() {
	return value;
    }

    public void setValue(double value) {
	this.value = value;
    }

    public String getParam() {
	return sParam;
    }

    public void setParam(String sParam) {
	this.sParam = sParam;
    }

    public double getRefMstart() {
	return refMstart;
    }

    public void setRefMstart(double refMstart) {
	this.refMstart = refMstart;
    }

    public double getRefMend() {
	return refMend;
    }

    public void setRefMend(double refMend) {
	this.refMend = refMend;
    }

    public double getRefFstart() {
	return refFstart;
    }

    public void setRefFstart(double refFstart) {
	this.refFstart = refFstart;
    }

    public double getRefFend() {
	return refFend;
    }

    public void setRefFend(double refFend) {
	this.refFend = refFend;
    }

}
