/**
 */
package ch.elexis.base.ch.labortarif;

import java.time.LocalDate;

import ch.elexis.core.model.IBillable;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ILabor Leistung</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ch.elexis.base.ch.labortarif.ILaborLeistung#getPoints <em>Points</em>}</li>
 *   <li>{@link ch.elexis.base.ch.labortarif.ILaborLeistung#getValidFrom <em>Valid From</em>}</li>
 *   <li>{@link ch.elexis.base.ch.labortarif.ILaborLeistung#getValidTo <em>Valid To</em>}</li>
 *   <li>{@link ch.elexis.base.ch.labortarif.ILaborLeistung#getChapter <em>Chapter</em>}</li>
 *   <li>{@link ch.elexis.base.ch.labortarif.ILaborLeistung#getSpeciality <em>Speciality</em>}</li>
 *   <li>{@link ch.elexis.base.ch.labortarif.ILaborLeistung#getLimitation <em>Limitation</em>}</li>
 * </ul>
 *
 * @see ch.elexis.base.ch.labortarif.LabortarifPackage#getILaborLeistung()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ILaborLeistung extends IBillable {

	/**
	 * Returns the value of the '<em><b>Points</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Points</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Points</em>' attribute.
	 * @see ch.elexis.base.ch.labortarif.LabortarifPackage#getILaborLeistung_Points()
	 * @model changeable="false"
	 *        annotation="http://elexis.info/jpa/entity/attribute/mapping attributeName='tp'"
	 * @generated
	 */
	int getPoints();

	/**
	 * Returns the value of the '<em><b>Valid From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Valid From</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid From</em>' attribute.
	 * @see ch.elexis.base.ch.labortarif.LabortarifPackage#getILaborLeistung_ValidFrom()
	 * @model dataType="ch.elexis.core.types.LocalDate" changeable="false"
	 *        annotation="http://elexis.info/jpa/entity/attribute/mapping attributeName='gueltigVon'"
	 * @generated
	 */
	LocalDate getValidFrom();

	/**
	 * Returns the value of the '<em><b>Valid To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Valid To</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Valid To</em>' attribute.
	 * @see ch.elexis.base.ch.labortarif.LabortarifPackage#getILaborLeistung_ValidTo()
	 * @model dataType="ch.elexis.core.types.LocalDate" changeable="false"
	 *        annotation="http://elexis.info/jpa/entity/attribute/mapping attributeName='gueltigBis'"
	 * @generated
	 */
	LocalDate getValidTo();

	/**
	 * Returns the value of the '<em><b>Chapter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Chapter</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Chapter</em>' attribute.
	 * @see ch.elexis.base.ch.labortarif.LabortarifPackage#getILaborLeistung_Chapter()
	 * @model changeable="false"
	 * @generated
	 */
	String getChapter();

	/**
	 * Returns the value of the '<em><b>Speciality</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Speciality</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Speciality</em>' attribute.
	 * @see ch.elexis.base.ch.labortarif.LabortarifPackage#getILaborLeistung_Speciality()
	 * @model changeable="false"
	 *        annotation="http://elexis.info/jpa/entity/attribute/mapping attributeName='fachbereich'"
	 * @generated
	 */
	String getSpeciality();

	/**
	 * Returns the value of the '<em><b>Limitation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Limitation</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Limitation</em>' attribute.
	 * @see ch.elexis.base.ch.labortarif.LabortarifPackage#getILaborLeistung_Limitation()
	 * @model changeable="false"
	 *        annotation="http://elexis.info/jpa/entity/attribute/mapping attributeName='limitatio'"
	 * @generated
	 */
	String getLimitation();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dateDataType="ch.elexis.core.types.LocalDate"
	 * @generated
	 */
	boolean isValidOn(LocalDate date);
	
} // ILaborLeistung
