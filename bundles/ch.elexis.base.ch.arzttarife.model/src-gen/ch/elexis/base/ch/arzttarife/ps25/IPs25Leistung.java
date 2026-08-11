/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.ps25;

import java.time.LocalDate;

import ch.elexis.core.model.IBillable;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IPs25 Leistung</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getValidFrom <em>Valid From</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getValidTo <em>Valid To</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getTP <em>TP</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getSubChapter <em>Sub Chapter</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getChapter <em>Chapter</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getHonorarEmpfaenger <em>Honorar Empfaenger</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMehrleistungBei <em>Mehrleistung Bei</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getSpezifikation <em>Spezifikation</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getAnwendungsRegeln <em>Anwendungs Regeln</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getStufe <em>Stufe</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMoeglicheKombination <em>Moegliche Kombination</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMehrleistungsTyp <em>Mehrleistungs Typ</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMehrleistung <em>Mehrleistung</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Package#getIPs25Leistung()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface IPs25Leistung extends IBillable {
	/**
	 * Returns the value of the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid From</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Package#getIPs25Leistung_ValidFrom()
	 * @model dataType="ch.elexis.core.types.LocalDate" changeable="false"
	 * @generated
	 */
	LocalDate getValidFrom();

	/**
	 * Returns the value of the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid To</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Package#getIPs25Leistung_ValidTo()
	 * @model dataType="ch.elexis.core.types.LocalDate" changeable="false"
	 * @generated
	 */
	LocalDate getValidTo();

	/**
	 * Returns the value of the '<em><b>TP</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>TP</em>' attribute.
	 * @see #setTP(String)
	 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Package#getIPs25Leistung_TP()
	 * @model
	 * @generated
	 */
	String getTP();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getTP <em>TP</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>TP</em>' attribute.
	 * @see #getTP()
	 * @generated
	 */
	void setTP(String value);

	/**
	 * Returns the value of the '<em><b>Sub Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sub Chapter</em>' attribute.
	 * @see #setSubChapter(String)
	 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Package#getIPs25Leistung_SubChapter()
	 * @model
	 * @generated
	 */
	String getSubChapter();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getSubChapter <em>Sub Chapter</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sub Chapter</em>' attribute.
	 * @see #getSubChapter()
	 * @generated
	 */
	void setSubChapter(String value);

	/**
	 * Returns the value of the '<em><b>Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Chapter</em>' attribute.
	 * @see #setChapter(String)
	 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Package#getIPs25Leistung_Chapter()
	 * @model
	 * @generated
	 */
	String getChapter();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getChapter <em>Chapter</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Chapter</em>' attribute.
	 * @see #getChapter()
	 * @generated
	 */
	void setChapter(String value);

	/**
	 * Returns the value of the '<em><b>Honorar Empfaenger</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Honorar Empfaenger</em>' attribute.
	 * @see #setHonorarEmpfaenger(String)
	 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Package#getIPs25Leistung_HonorarEmpfaenger()
	 * @model
	 * @generated
	 */
	String getHonorarEmpfaenger();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getHonorarEmpfaenger <em>Honorar Empfaenger</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Honorar Empfaenger</em>' attribute.
	 * @see #getHonorarEmpfaenger()
	 * @generated
	 */
	void setHonorarEmpfaenger(String value);

	/**
	 * Returns the value of the '<em><b>Mehrleistung Bei</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mehrleistung Bei</em>' attribute.
	 * @see #setMehrleistungBei(String)
	 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Package#getIPs25Leistung_MehrleistungBei()
	 * @model
	 * @generated
	 */
	String getMehrleistungBei();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMehrleistungBei <em>Mehrleistung Bei</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mehrleistung Bei</em>' attribute.
	 * @see #getMehrleistungBei()
	 * @generated
	 */
	void setMehrleistungBei(String value);

	/**
	 * Returns the value of the '<em><b>Spezifikation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Spezifikation</em>' attribute.
	 * @see #setSpezifikation(String)
	 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Package#getIPs25Leistung_Spezifikation()
	 * @model
	 * @generated
	 */
	String getSpezifikation();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getSpezifikation <em>Spezifikation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Spezifikation</em>' attribute.
	 * @see #getSpezifikation()
	 * @generated
	 */
	void setSpezifikation(String value);

	/**
	 * Returns the value of the '<em><b>Anwendungs Regeln</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Anwendungs Regeln</em>' attribute.
	 * @see #setAnwendungsRegeln(String)
	 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Package#getIPs25Leistung_AnwendungsRegeln()
	 * @model
	 * @generated
	 */
	String getAnwendungsRegeln();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getAnwendungsRegeln <em>Anwendungs Regeln</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Anwendungs Regeln</em>' attribute.
	 * @see #getAnwendungsRegeln()
	 * @generated
	 */
	void setAnwendungsRegeln(String value);

	/**
	 * Returns the value of the '<em><b>Stufe</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Stufe</em>' attribute.
	 * @see #setStufe(String)
	 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Package#getIPs25Leistung_Stufe()
	 * @model
	 * @generated
	 */
	String getStufe();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getStufe <em>Stufe</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Stufe</em>' attribute.
	 * @see #getStufe()
	 * @generated
	 */
	void setStufe(String value);

	/**
	 * Returns the value of the '<em><b>Moegliche Kombination</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Moegliche Kombination</em>' attribute.
	 * @see #setMoeglicheKombination(String)
	 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Package#getIPs25Leistung_MoeglicheKombination()
	 * @model
	 * @generated
	 */
	String getMoeglicheKombination();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMoeglicheKombination <em>Moegliche Kombination</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Moegliche Kombination</em>' attribute.
	 * @see #getMoeglicheKombination()
	 * @generated
	 */
	void setMoeglicheKombination(String value);

	/**
	 * Returns the value of the '<em><b>Mehrleistungs Typ</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mehrleistungs Typ</em>' attribute.
	 * @see #setMehrleistungsTyp(String)
	 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Package#getIPs25Leistung_MehrleistungsTyp()
	 * @model
	 * @generated
	 */
	String getMehrleistungsTyp();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMehrleistungsTyp <em>Mehrleistungs Typ</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mehrleistungs Typ</em>' attribute.
	 * @see #getMehrleistungsTyp()
	 * @generated
	 */
	void setMehrleistungsTyp(String value);

	/**
	 * Returns the value of the '<em><b>Mehrleistung</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mehrleistung</em>' attribute.
	 * @see #setMehrleistung(String)
	 * @see ch.elexis.base.ch.arzttarife.ps25.Ps25Package#getIPs25Leistung_Mehrleistung()
	 * @model
	 * @generated
	 */
	String getMehrleistung();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung#getMehrleistung <em>Mehrleistung</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mehrleistung</em>' attribute.
	 * @see #getMehrleistung()
	 * @generated
	 */
	void setMehrleistung(String value);

} // IPs25Leistung
