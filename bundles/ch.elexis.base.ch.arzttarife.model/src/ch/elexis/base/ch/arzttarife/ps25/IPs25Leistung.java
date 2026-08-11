package ch.elexis.base.ch.arzttarife.ps25;

import java.time.LocalDate;

import ch.elexis.core.model.IBillable;

public interface IPs25Leistung extends IBillable {

	LocalDate getValidFrom();

	void setValidFrom(LocalDate value);

	LocalDate getValidTo();

	void setValidTo(LocalDate value);

	String getTaxpunkte();

	void setTaxpunkte(String value);

	String getHonorarEmpfaenger();

	void setHonorarEmpfaenger(String value);

	String getFachgebietKapitel();

	void setFachgebietKapitel(String value);

	String getUnterkapitel();

	void setUnterkapitel(String value);

	String getFachaerztlicheMehrleistungBei();

	void setFachaerztlicheMehrleistungBei(String value);

	String getSpezifikation();

	void setSpezifikation(String value);

	String getAnwendungsregeln();

	void setAnwendungsregeln(String value);

	String getStufe();

	void setStufe(String value);

	String getMoeglicheKombination();

	void setMoeglicheKombination(String value);

	String getMehrleistungstyp();

	void setMehrleistungstyp(String value);

	String getMehrleistung();

	void setMehrleistung(String value);
}
