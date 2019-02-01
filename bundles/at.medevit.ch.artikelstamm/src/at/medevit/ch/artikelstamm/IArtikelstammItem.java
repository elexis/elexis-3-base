/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm;

import at.medevit.ch.artikelstamm.ArtikelstammConstants.TYPE;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.types.VatInfo;
import ch.rgw.tools.Money;

/**
 * Interface defining the requirements for the {@link DetailComposite} data-binding
 */
public interface IArtikelstammItem extends IArticle {
	
	/**
	 * @return human readable label
	 */
	public String getLabel();
	
	/**
	 * @return pharmacode
	 */
	public String getPHAR();
	
	/**
	 * @return {@link TYPE}
	 */
	public TYPE getType();
	
	/**
	 * @return human readable string of the manufacturer
	 */
	public String getManufacturerLabel();
	
	/**
	 * @return <code>true</code> if the article is part of the Spezialitaetenliste
	 */
	public boolean isInSLList();
	
	/**
	 * @return the swissmedic category this article is allocated to
	 */
	public String getSwissmedicCategory();
	
	/**
	 * @return the generica type, O if original, G if generica, may be <code>null</code>
	 */
	public String getGenericType();
	
	/**
	 * @return the percentage amount the patient has to pay ("Selbstbehalt")
	 */
	public Integer getDeductible();
	
	/**
	 * @return <code>true</code> if this article is narcotic
	 */
	public boolean isNarcotic();
	
	/**
	 * @return <code>true</code> if article is in LPPV list
	 */
	public boolean isInLPPV();
	
	/**
	 * 
	 * @return is this article limited?
	 */
	public boolean isLimited();
	
	/**
	 * 
	 * @return the number of limitation points
	 */
	public String getLimitationPoints();
	
	/**
	 * 
	 * @return textual description of the limitation
	 */
	public String getLimitationText();
	
	/**
	 * 
	 * @return whether this public price is calculated out of the ex-factory price plus margin
	 */
	public boolean isCalculatedPrice();
	
	/**
	 * @return whether this price is set by the user
	 */
	public boolean isUserDefinedPrice();
	
	/**
	 * Set the price as user-defined (i.e. overridden) price. This will internally store the price
	 * as negative value.
	 * 
	 * @param value
	 */
	public void setUserDefinedPriceValue(Money value);
	
	/**
	 * Restore the original price, if was overridden, effectively deleting a manually overridden
	 * selling price
	 * 
	 * @param selection
	 */
	public void restoreOriginalSellingPrice();
	
	/**
	 * Get an addition description text.
	 * 
	 * @return
	 */
	public String getAdditionalDescription();
	
	/**
	 * Set the additional description text.
	 * 
	 * @param value
	 * @return
	 */
	public void setAdditionalDescription(String value);
	
	/**
	 * Test if the item is marked as black boxed.
	 * 
	 * @return
	 */
	public boolean isBlackBoxed();
	
	/**
	 * Override the VatInfo of the item. See . Setting null results in default {@link VatInfo} for
	 * {@link IBillable#getVatInfo()} and false for {@link IArtikelstammItem#isOverrideVatInfo()}.
	 * 
	 * @param vatInfo
	 */
	public void overrideVatInfo(VatInfo vatInfo);
	
	/**
	 * Test if the {@link VatInfo} of the item is overridden.
	 * 
	 * @return
	 */
	public boolean isOverrideVatInfo();
}
