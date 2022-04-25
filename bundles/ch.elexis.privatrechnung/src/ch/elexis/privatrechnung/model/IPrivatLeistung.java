package ch.elexis.privatrechnung.model;

import java.time.LocalDate;

import ch.elexis.core.model.IBillable;
import ch.rgw.tools.Money;

public interface IPrivatLeistung extends IBillable {

	Money getNetPrice();

	Money getPrice();

	boolean isValidOn(LocalDate date);

	void setParent(String string);

	void setCost(String string);

	void setPrice(String string);

	void setTime(String string);

	void setValidFrom(String string);

	void setValidTo(String string);

}
