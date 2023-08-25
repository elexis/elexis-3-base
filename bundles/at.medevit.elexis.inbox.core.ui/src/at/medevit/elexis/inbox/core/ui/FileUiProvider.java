/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.inbox.core.ui;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.program.Program;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;

public class FileUiProvider implements IInboxElementUiProvider {

	FileLabelProvider fileLabelProvider;

	public FileUiProvider() {
		fileLabelProvider = new FileLabelProvider();
	}

	@Override
	public ImageDescriptor getFilterImage(ViewerFilter filter) {
		return null;
	}

	@Override
	public List<ViewerFilter> getFilters() {
		return Collections.emptyList();
	}

	@Override
	public LocalDate getObjectDate(IInboxElement element) {
		return null;
	}

	@Override
	public LabelProvider getLabelProvider() {
		return fileLabelProvider;
	}

	@Override
	public IColorProvider getColorProvider() {
		return fileLabelProvider;
	}

	@Override
	public IToolTipProvider getToolTipProvider() {
		return null;
	}

	@Override
	public boolean isProviderFor(IInboxElement element) {
		Object obj = element.getObject();
		if (obj instanceof Path) {
			return true;
		}
		return false;
	}

	@Override
	public void doubleClicked(IInboxElement element) {
		Path path = (Path) element.getObject();
		Program.launch(path.toFile().getAbsolutePath());
	}
}
