/*******************************************************************************
 * Copyright (c) 2006-2017, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    T. Huster - copied from ch.elexis.base.ch.artikel
 *    
 *******************************************************************************/
package ch.elexis.base.ch.migel.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.opencsv.CSVReader;

import ch.elexis.artikel_ch.data.service.MiGelCodeElementService;
import ch.elexis.base.ch.migel.Messages;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.builder.IArticleBuilder;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.ArticleTyp;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;

public class MiGelImporter extends ImporterPage {
	private static final String SRC_ENCODING = "iso-8859-1"; //$NON-NLS-1$
	boolean bDelete = false;
	Button bClear;
	String mode;
	
	private enum ImportFields {
			POSNUMER(0), NAME(1), UNIT(2), PRICE(3), CATEGORY(4), SUBCATEGORY(5), AMOUNT(6);
		
		private int index;
		
		ImportFields(int index){
			this.index = index;
		}
		
		private boolean exists(String[] line){
			return line.length > index;
		}
		
		public String getStringValue(String[] line){
			if (exists(line)) {
				if (this == NAME && line[index].contains("\n")) {
					line[index] = getJoinedFirstLines(line[index]);
				}
				return line[index];
			} else {
				return "";
			}
		}
		
		private String getJoinedFirstLines(String string){
			String[] parts = string.split("\n");
			if (parts.length > 1) {
				StringBuilder ret = new StringBuilder();
				if (!parts[1].isEmpty()) {
					if (parts[0].endsWith(",")) {
						ret.append(parts[0] + " " + parts[1]);
					} else {
						ret.append(parts[0] + ", " + parts[1]);
					}
				} else {
					ret.append(parts[0] + "\n");
				}
				StringJoiner rest = new StringJoiner("\n");
				for (int i = 2; i < parts.length; i++) {
					rest.add(parts[i]);
				}
				if (rest.length() > 0) {
					ret.append("\n").append(rest);
				}
				return ret.toString();
			} else {
				return string;
			}
		}
		
		public Money getMoneyValue(String[] line){
			if (exists(line)) {
				try {
					return new Money(getStringValue(line));
				} catch (ParseException e) {
					// ignore
				}
			}
			return new Money();
		}
		
	}
	
	public MiGelImporter(){}
	
	@Override
	public String getTitle(){
		return "MiGeL"; //$NON-NLS-1$
	}
	
	@Override
	public String getDescription(){
		return Messages.MiGelImporter_PleaseSelectFile;
	}
	
	@Override
	public IStatus doImport(final IProgressMonitor monitor) throws Exception{
		mode = Messages.MiGelImporter_ModeUpdateAdd;
		if (bDelete == true) {
			IQuery<IArticle> query = CoreModelServiceHolder.get().getQuery(IArticle.class, true);
			query.and(ModelPackage.Literals.IARTICLE__TYP, COMPARATOR.EQUALS, ArticleTyp.MIGEL);
			List<IArticle> existing = query.execute();
			existing.forEach(a -> CoreModelServiceHolder.get().remove(a));
			mode = Messages.MiGelImporter_ModeCreateNew;
		}
		try {
			File file = new File(results[0]);
			long l = file.length();
			monitor.beginTask("MiGeL Import " + mode, (int) l / 100); //$NON-NLS-1$
			if (file.getName().toLowerCase().endsWith("csv")) { //$NON-NLS-1$
				return importCSV(file, monitor);
			}
		} catch (Exception ex) {
			ExHandler.handle(ex);
		}
		return Status.CANCEL_STATUS;
	}
	
	@Override
	public void collect(){
		bDelete = bClear.getSelection();
	}
	
	@Override
	public Composite createPage(final Composite parent){
		Composite ret = new ImporterPage.FileBasedImporter(parent, this);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		bClear = new Button(parent, SWT.CHECK | SWT.WRAP);
		bClear.setText(Messages.MiGelImporter_ClearAllData);
		bClear.setSelection(true);
		bClear.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		return ret;
		
	}
	
	static Pattern pattern = Pattern.compile("([a-z0-9A-Z])([A-Z][a-z])");
	
	private IStatus importCSV(final File file, final IProgressMonitor monitor)
		throws FileNotFoundException, IOException{
		InputStreamReader isr = new InputStreamReader(new FileInputStream(file), SRC_ENCODING);
		CSVReader reader = new CSVReader(isr);
		String[] line;
		monitor.subTask(Messages.MiGelImporter_ReadMigel);
		while ((line = reader.readNext()) != null) {
			if (isFieldsLine(line) && line.length >= 3) {
				StringBuilder text = new StringBuilder();
				String category = ImportFields.SUBCATEGORY.getStringValue(line);
				if (category.isEmpty()) {
					category = ImportFields.CATEGORY.getStringValue(line);
				}
				// category only 1 line and max 80 char
				if(!category.isEmpty()) {
					text.append(StringTool.getFirstLine(category, 80, "[\\n\\r]")).append(" - ");
				}
				text.append(ImportFields.NAME.getStringValue(line));
				
				String amount = ImportFields.AMOUNT.getStringValue(line);
				String unit = ImportFields.UNIT.getStringValue(line);
				// try to parse amount from unit
				if (amount.isEmpty() && !unit.isEmpty() && Character.isDigit(unit.charAt(0))) {
					String[] parts = unit.split(" ");
					if (parts != null && parts.length > 1) {
						amount = parts[0];
						StringJoiner unitWithoutDigit = new StringJoiner(" ");
						for (int i = 1; i < parts.length; i++) {
							unitWithoutDigit.add(parts[i]);
						}
						unit = unitWithoutDigit.toString();
					}
				}
				
				String code = ImportFields.POSNUMER.getStringValue(line);
				String shortname = getShortname(text.toString());
				
				IArticle migelArticle =
					new IArticleBuilder(CoreModelServiceHolder.get(), shortname,
						ImportFields.POSNUMER.getStringValue(line), ArticleTyp.MIGEL).build();
				
				CoreModelServiceHolder.get().setEntityProperty("id",
					MiGelCodeElementService.MIGEL_NAME + code, migelArticle);
				migelArticle.setPackageUnit(unit);
				migelArticle.setSellingPrice(ImportFields.PRICE.getMoneyValue(line));
				migelArticle.setExtInfo("FullText", text.toString());
				
				if (!amount.isEmpty()) {
					try {
						double amountDbl = Double.parseDouble(amount);
						migelArticle.setPackageSize((int) amountDbl);
					} catch (NumberFormatException e) {
						// ignore
					}
				}
				CoreModelServiceHolder.get().save(migelArticle);
				monitor.worked(1);
			}
		}
		monitor.done();
		return Status.OK_STATUS;
	}
	
	private String getShortname(String text){
		String shortname = StringTool.getFirstLine(text, 120, "[\\n\\r]");
		Matcher matcher = pattern.matcher(shortname);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1) + " " + matcher.group(2));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
	
	private boolean isFieldsLine(String[] line){
		// line[0] contains the code, which always contains digits, so if not its is probably the description
		return containsDigits(line[0]);
	}
	
	private boolean containsDigits(String string){
		for (char character : string.toCharArray()) {
			if (Character.isDigit(character)) {
				return true;
			}
		}
		return false;
	}
}
