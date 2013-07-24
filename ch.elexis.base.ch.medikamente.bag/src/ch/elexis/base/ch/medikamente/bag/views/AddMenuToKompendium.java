/*******************************************************************************
 * Copyright (c) 2007-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis.base.ch.medikamente.bag.views;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.KompendiumView;
import ch.rgw.tools.ExHandler;

public class AddMenuToKompendium extends ExtensionContributionFactory {
	
	public AddMenuToKompendium(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void createContributionItems(IServiceLocator serviceLocator, IContributionRoot additions){
		Action action = new Action("Pull") {
			{
				setImageDescriptor(Images.IMG_IMPORT.getImageDescriptor());
				setToolTipText("Text zu Medikament übernehmen");
			}
			
			@Override
			public void run(){
				String text = KompendiumView.getText();
				SAXBuilder builder = new SAXBuilder();
				try {
					Pattern pattern = Pattern.compile(".+<body.*?>(.+)</body>.*", Pattern.DOTALL);
					Matcher m = pattern.matcher(text);
					if (m.matches()) {
						String cont = m.group(1);
						Document doc = builder.build(new StringReader(cont));
						Element eRoot = doc.getRootElement();
						XPath xpath = XPath.newInstance("//h2");
						List<Element> res = xpath.selectNodes(eRoot);
						for (Element e : res) {
							System.out.println(e.getText());
						}
						
					} else {
						SWTHelper
							.showError("Parse Fehler",
								"Der Text konnte nicht korrekt gelesen oder interpretiert werden. Versuchen Sie es noch einmal.");
					}
					
				} catch (JDOMException e) {
					ExHandler.handle(e);
					SWTHelper
						.showError(
							"XML Fehler",
							"Der Text konnte nicht korrekt gelesen oder interpretiert werden. Versuchen Sie es noch einmal. Fehlermeldung: "
								+ e.getMessage());
				} catch (IOException e) {
					ExHandler.handle(e);
					SWTHelper.showError("IO Fehler", "Fehler beim Lesen " + e.getMessage());
					
				}
				System.out.println(text);
			}
			
		};
		additions.addContributionItem(new ActionContributionItem(action), null);
		
	}
	
}
