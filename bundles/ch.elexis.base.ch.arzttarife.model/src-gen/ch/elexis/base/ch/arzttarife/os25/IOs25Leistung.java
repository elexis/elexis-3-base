/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.os25;

import ch.elexis.core.model.IBillable;

import java.time.LocalDate;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IOs25 Leistung</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getValidFrom <em>Valid From</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getValidTo <em>Valid To</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getTP <em>TP</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getSubChapter <em>Sub Chapter</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getChapter <em>Chapter</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getHonorarEmpfaenger <em>Honorar Empfaenger</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getLeistung <em>Leistung</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getSpezifikation <em>Spezifikation</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getAnwendungsRegeln <em>Anwendungs Regeln</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getStufe <em>Stufe</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getMoeglicheKombination <em>Moegliche Kombination</em>}</li>
 *   <li>{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getLeistungsTyp <em>Leistungs Typ</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.arzttarife.os25.Os25Package#getIOs25Leistung()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface IOs25Leistung extends IBillable {
	/**
	 * Returns the value of the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid From</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.os25.Os25Package#getIOs25Leistung_ValidFrom()
	 * @model dataType="ch.elexis.core.types.LocalDate" changeable="false"
	 * @generated
	 */
	LocalDate getValidFrom();

	/**
	 * Returns the value of the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid To</em>' attribute.
	 * @see ch.elexis.base.ch.arzttarife.os25.Os25Package#getIOs25Leistung_ValidTo()
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
	 * @see ch.elexis.base.ch.arzttarife.os25.Os25Package#getIOs25Leistung_TP()
	 * @model
	 * @generated
	 */
	String getTP();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getTP <em>TP</em>}' attribute.
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
	 * @see ch.elexis.base.ch.arzttarife.os25.Os25Package#getIOs25Leistung_SubChapter()
	 * @model
	 * @generated
	 */
	String getSubChapter();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getSubChapter <em>Sub Chapter</em>}' attribute.
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
	 * @see ch.elexis.base.ch.arzttarife.os25.Os25Package#getIOs25Leistung_Chapter()
	 * @model
	 * @generated
	 */
	String getChapter();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getChapter <em>Chapter</em>}' attribute.
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
	 * @see ch.elexis.base.ch.arzttarife.os25.Os25Package#getIOs25Leistung_HonorarEmpfaenger()
	 * @model
	 * @generated
	 */
	String getHonorarEmpfaenger();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getHonorarEmpfaenger <em>Honorar Empfaenger</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Honorar Empfaenger</em>' attribute.
	 * @see #getHonorarEmpfaenger()
	 * @generated
	 */
	void setHonorarEmpfaenger(String value);

	/**
	 * Returns the value of the '<em><b>Leistung</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Leistung</em>' attribute.
	 * @see #setLeistung(String)
	 * @see ch.elexis.base.ch.arzttarife.os25.Os25Package#getIOs25Leistung_Leistung()
	 * @model
	 * @generated
	 */
	String getLeistung();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getLeistung <em>Leistung</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Leistung</em>' attribute.
	 * @see #getLeistung()
	 * @generated
	 */
	void setLeistung(String value);

	/**
	 * Returns the value of the '<em><b>Spezifikation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Spezifikation</em>' attribute.
	 * @see #setSpezifikation(String)
	 * @see ch.elexis.base.ch.arzttarife.os25.Os25Package#getIOs25Leistung_Spezifikation()
	 * @model
	 * @generated
	 */
	String getSpezifikation();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getSpezifikation <em>Spezifikation</em>}' attribute.
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
	 * @see ch.elexis.base.ch.arzttarife.os25.Os25Package#getIOs25Leistung_AnwendungsRegeln()
	 * @model
	 * @generated
	 */
	String getAnwendungsRegeln();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getAnwendungsRegeln <em>Anwendungs Regeln</em>}' attribute.
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
	 * @see ch.elexis.base.ch.arzttarife.os25.Os25Package#getIOs25Leistung_Stufe()
	 * @model
	 * @generated
	 */
	String getStufe();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getStufe <em>Stufe</em>}' attribute.
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
	 * @see ch.elexis.base.ch.arzttarife.os25.Os25Package#getIOs25Leistung_MoeglicheKombination()
	 * @model
	 * @generated
	 */
	String getMoeglicheKombination();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getMoeglicheKombination <em>Moegliche Kombination</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Moegliche Kombination</em>' attribute.
	 * @see #getMoeglicheKombination()
	 * @generated
	 */
	void setMoeglicheKombination(String value);

	/**
	 * Returns the value of the '<em><b>Leistungs Typ</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Leistungs Typ</em>' attribute.
	 * @see #setLeistungsTyp(String)
	 * @see ch.elexis.base.ch.arzttarife.os25.Os25Package#getIOs25Leistung_LeistungsTyp()
	 * @model
	 * @generated
	 */
	String getLeistungsTyp();

	/**
	 * Sets the value of the '{@link ch.elexis.base.ch.arzttarife.os25.IOs25Leistung#getLeistungsTyp <em>Leistungs Typ</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Leistungs Typ</em>' attribute.
	 * @see #getLeistungsTyp()
	 * @generated
	 */
	void setLeistungsTyp(String value);

} // IOs25Leistung
