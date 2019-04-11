/**
 */
package ch.elexis.icpc.model.icpc;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see ch.elexis.icpc.model.icpc.IcpcPackage
 * @generated
 */
public interface IcpcFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	IcpcFactory eINSTANCE = ch.elexis.icpc.model.icpc.impl.IcpcFactoryImpl.init();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	IcpcPackage getIcpcPackage();

} //IcpcFactory
