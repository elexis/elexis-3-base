package ch.framsteg.elexis.finance.analytics.beans;

import java.math.BigDecimal;

public class Delivery extends Line {

	private String id;
	private String treatmentId;
	private String deliveryCode;
	private String deliveryClass;
	private String deliveryDescription;
	private int deliveryPoints;
	private float clearingFactor;
	private float clearingScale;
	private BigDecimal clearingPrice;
	
	private Treatment treatment;
			
	private String[] input;
	
	public Delivery(String[] input) {
		setInput(input);
		init();
	}
	
	private void init() {
		this.setTreatmentId(getInput()[TREATMENT_ID]);
		this.setDeliveryCode(getInput()[DELIVERY_CODE]);
		this.setDeliveryClass(getInput()[DELIVERY_CLASS]);
		this.setDeliveryDescription(getInput()[DELIVERY_DESCRIPTION]);
		this.setDeliveryPoints(Integer.parseInt(getInput()[DELIVERY_POINTS]));
		this.setClearingFactor(Float.parseFloat(getInput()[CLEARING_FACTOR]));
		this.setClearingScale(Float.parseFloat(getInput()[CLEARING_SCALE]));
		this.setClearingPrice(new BigDecimal(getInput()[CLEARING_PRICE]));
		this.setId(getInput()[ID]);
	}

	public String getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(String treatmentId) {
		this.treatmentId = treatmentId;
	}

	public String getDeliveryCode() {
		return deliveryCode;
	}

	public void setDeliveryCode(String deliveryCode) {
		this.deliveryCode = deliveryCode;
	}

	public String getDeliveryClass() {
		return deliveryClass;
	}

	public void setDeliveryClass(String deliveryClass) {
		this.deliveryClass = deliveryClass;
	}

	public String getDeliveryDescription() {
		return deliveryDescription;
	}

	public void setDeliveryDescription(String deliveryDescription) {
		this.deliveryDescription = deliveryDescription;
	}

	public int getDeliveryPoints() {
		return deliveryPoints;
	}

	public void setDeliveryPoints(int deliveryPoints) {
		this.deliveryPoints = deliveryPoints;
	}

	public float getClearingFactor() {
		return clearingFactor;
	}

	public void setClearingFactor(float clearingFactor) {
		this.clearingFactor = clearingFactor;
	}

	public float getClearingScale() {
		return clearingScale;
	}

	public void setClearingScale(float clearingScale) {
		this.clearingScale = clearingScale;
	}

	public BigDecimal getClearingPrice() {
		return clearingPrice;
	}

	public void setClearingPrice(BigDecimal clearingPrice) {
		this.clearingPrice = clearingPrice;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getParentId() {
		return getTreatmentId();
	}
		
	public String[] getInput() {
		return input;
	}

	public void setInput(String[] input) {
		this.input = input;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("ID: ");
		stringBuilder.append(getId());
		stringBuilder.append("\r");
		stringBuilder.append("Treatment: ");
		stringBuilder.append(getTreatmentId());
		stringBuilder.append("\r");
		stringBuilder.append("Delivery Code: ");
		stringBuilder.append(getDeliveryCode());
		stringBuilder.append("\r");
		stringBuilder.append("Delivery Class: ");
		stringBuilder.append(getDeliveryClass());
		stringBuilder.append("\r");
		stringBuilder.append("Delivery Description: ");
		stringBuilder.append(getDeliveryDescription());
		stringBuilder.append("\r");
		stringBuilder.append("Delivery Points: ");
		stringBuilder.append(getDeliveryPoints());
		stringBuilder.append("\r");
		stringBuilder.append("Clearing Factor: ");
		stringBuilder.append(getClearingFactor());
		stringBuilder.append("\r");
		stringBuilder.append("Clearing Scale: ");
		stringBuilder.append(getClearingScale());
		stringBuilder.append("\r");
		stringBuilder.append("Clearing Price: ");
		stringBuilder.append(getClearingPrice());
		stringBuilder.append("\r");
		return stringBuilder.toString();
	}

	public Treatment getTreatment() {
		return treatment;
	}

	public void setTreatment(Treatment treatment) {
		this.treatment = treatment;
	}
}
