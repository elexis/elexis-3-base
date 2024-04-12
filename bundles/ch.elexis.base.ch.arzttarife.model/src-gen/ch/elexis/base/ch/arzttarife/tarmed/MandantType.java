/**
 * Copyright Text	Copyright (c) 2018 MEDEVIT <office@medevit.at>....
 */
package ch.elexis.base.ch.arzttarife.tarmed;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Mandant Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see ch.elexis.base.ch.arzttarife.tarmed.TarmedPackage#getMandantType()
 * @model
 * @generated
 */
public enum MandantType implements Enumerator {
	/**
	 * The '<em><b>SPECIALIST</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SPECIALIST_VALUE
	 * @generated
	 * @ordered
	 */
	SPECIALIST(0, "SPECIALIST", "SPECIALIST"),

	/**
	 * The '<em><b>PRACTITIONER</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PRACTITIONER_VALUE
	 * @generated
	 * @ordered
	 */
	PRACTITIONER(1, "PRACTITIONER", "PRACTITIONER"), /**
	 * The '<em><b>TARPSYAPPRENTICE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TARPSYAPPRENTICE_VALUE
	 * @generated
	 * @ordered
	 */
	TARPSYAPPRENTICE(2, "TARPSYAPPRENTICE", "TARPSYAPPRENTICE");

	/**
	 * The '<em><b>SPECIALIST</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>SPECIALIST</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #SPECIALIST
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SPECIALIST_VALUE = 0;

	/**
	 * The '<em><b>PRACTITIONER</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>PRACTITIONER</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PRACTITIONER
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int PRACTITIONER_VALUE = 1;

	/**
	 * The '<em><b>TARPSYAPPRENTICE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TARPSYAPPRENTICE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int TARPSYAPPRENTICE_VALUE = 2;

	/**
	 * An array of all the '<em><b>Mandant Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final MandantType[] VALUES_ARRAY =
		new MandantType[] {
			SPECIALIST,
			PRACTITIONER,
			TARPSYAPPRENTICE,
		};

	/**
	 * A public read-only list of all the '<em><b>Mandant Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<MandantType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Mandant Type</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static MandantType get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			MandantType result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Mandant Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static MandantType getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			MandantType result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Mandant Type</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static MandantType get(int value) {
		switch (value) {
			case SPECIALIST_VALUE: return SPECIALIST;
			case PRACTITIONER_VALUE: return PRACTITIONER;
			case TARPSYAPPRENTICE_VALUE: return TARPSYAPPRENTICE;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private MandantType(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getValue() {
	  return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
	  return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getLiteral() {
	  return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}
	
} //MandantType
