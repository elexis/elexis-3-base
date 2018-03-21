package ch.gpb.elexis.cst.data;

import java.util.ArrayList;
import java.util.List;

import ch.elexis.data.LabItem;

public class LabItemWrapper {
    LabItem labItem;
    String isDisplayOnce;

    public LabItemWrapper(LabItem labItem, String isDisplayOnce) {
	this.labItem = labItem;
	this.isDisplayOnce = isDisplayOnce;
    }

    public LabItemWrapper() {
	// TODO Auto-generated constructor stub
    }

    public LabItem getLabItem() {
	return labItem;
    }

    public void setLabItem(LabItem labItem) {
	this.labItem = labItem;
    }

    public boolean isDisplayOnce() {
	return isDisplayOnce.equals("1") ? true : false;

    }

    public String getDisplayOnce() {
	return this.isDisplayOnce;
    }
    public void setDisplayOnce(boolean isDisplayOnce) {
	this.isDisplayOnce = isDisplayOnce ? "1" : "0";
    }

    public static List<LabItemWrapper> wrap(List<LabItem> labItems) {
	List<LabItemWrapper> result = new ArrayList<LabItemWrapper>();
	for (LabItem labItem : labItems) {
	    result.add(new LabItemWrapper(labItem, "0"));
	}
	return result;
    }
}
