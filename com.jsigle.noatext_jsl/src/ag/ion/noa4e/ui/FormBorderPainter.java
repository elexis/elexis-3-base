/****************************************************************************
 *                                                                          *
 * NOA (Nice Office Access)                                     						*
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * The Contents of this file are made available subject to                  *
 * the terms of GNU Lesser General Public License Version 2.1.              *
 *                                                                          * 
 * GNU Lesser General Public License Version 2.1                            *
 * ======================================================================== *
 * Copyright 2003-2006 by IOn AG                                            *
 *                                                                          *
 * This library is free software; you can redistribute it and/or            *
 * modify it under the terms of the GNU Lesser General Public               *
 * License version 2.1, as published by the Free Software Foundation.       *
 *                                                                          *
 * This library is distributed in the hope that it will be useful,          *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        *
 * Lesser General Public License for more details.                          *
 *                                                                          *
 * You should have received a copy of the GNU Lesser General Public         *
 * License along with this library; if not, write to the Free Software      *
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,                    *
 * MA  02111-1307  USA                                                      *
 *                                                                          *
 * Contact us:                                                              *
 *  http://www.ion.ag																												*
 *  http://ubion.ion.ag                                                     *
 *  info@ion.ag                                                             *
 *                                                                          *
 ****************************************************************************/
 
/*
 * Last changes made by $Author: andreas $, $Date: 2006-08-07 13:09:58 +0200 (Mo, 07 Aug 2006) $
 */
package ag.ion.noa4e.ui;

import org.eclipse.swt.SWT;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableTree;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * Border painter for form widgets.
 * 
 * <b>The internal paint listener of the FormToolkit ignores
 * disabled widgets.</b>
 * 
 * This border painter will also paint the borders
 * of disabled widgets. In order to use this border painter do not call
 * the paintBordersFor(...) method of the FormToolkit. Use the paintBordersFor(...)
 * method of this border painter. 
 * 
 * @author Andreas Bröker
 * @version $Revision: 9195 $
 * @date 25.02.2006
 */
public class FormBorderPainter implements PaintListener {
	
	private static FormBorderPainter formPaintListener = null;
	
  //----------------------------------------------------------------------------
	/**
	 * Paints borders for all children of the submitted composite.
	 * 
	 * @param composite composite to be used
	 * 
	 * @author Andreas Bröker
	 * @date 25.02.2006
	 */
	public static void paintBordersFor(Composite composite) {
		if(composite == null)
			return;
		
		if(formPaintListener == null)
			formPaintListener = new FormBorderPainter();
		composite.addPaintListener(formPaintListener);
	}	
  //----------------------------------------------------------------------------
	/**
	 * Sent when a paint event occurs for the control.
	 *
	 * @param paintEvent an event containing information about the paint
	 * 
	 * @author Andreas Bröker
	 * @date 25.02.2006
	 */
	public void paintControl(PaintEvent paintEvent) {
		if(!(paintEvent.widget instanceof Composite))
			return;
		Composite composite = (Composite) paintEvent.widget;
		Control[] children = composite.getChildren();
		for (int i = 0; i < children.length; i++) {
			Control control = children[i];
			boolean inactiveBorder = false;
			boolean textBorder = false;
			if (!control.isVisible())
				continue;
			if (control instanceof Hyperlink)
				continue;
			Object flag = control.getData(FormToolkit.KEY_DRAW_BORDER);
			if (flag != null) {
				if (flag.equals(Boolean.FALSE))
					continue;
				if (flag.equals(FormToolkit.TREE_BORDER))
					inactiveBorder = true;
				else if (flag.equals(FormToolkit.TEXT_BORDER))
					textBorder = true;
			}
			if (NOAUIPlugin.getFormToolkit().getBorderStyle() == SWT.BORDER) {
				if (!inactiveBorder && !textBorder) {
					continue;
				}
				if (control instanceof Text || control instanceof Table
						|| control instanceof Tree || control instanceof TableTree)
					continue;
			}
			if (!inactiveBorder
					&& (control instanceof Text || control instanceof CCombo || textBorder)) {
				Rectangle b = control.getBounds();
				GC gc = paintEvent.gc;
				gc.setForeground(control.getBackground());
				gc.drawRectangle(b.x - 1, b.y - 1, b.width + 1,
						b.height + 1);
				// gc.setForeground(getBorderStyle() == SWT.BORDER ? colors
				// .getBorderColor() : colors.getForeground());
				gc.setForeground(NOAUIPlugin.getFormToolkit().getColors().getBorderColor());
				if (control instanceof CCombo)
					gc.drawRectangle(b.x - 1, b.y - 1, b.width + 1,
							b.height + 1);
				else
					gc.drawRectangle(b.x - 1, b.y - 2, b.width + 1,
							b.height + 3);
			} 
			else if (inactiveBorder || control instanceof Table
					|| control instanceof Tree || control instanceof TableTree) {
				Rectangle b = control.getBounds();
				GC gc = paintEvent.gc;
				gc.setForeground(NOAUIPlugin.getFormToolkit().getColors().getBorderColor());
				gc.drawRectangle(b.x - 1, b.y - 1, b.width + 1,
						b.height + 1);
			}
		}
	}
  //----------------------------------------------------------------------------
	
  //----------------------------------------------------------------------------
	/**
	 * Prevents instantiation.
	 *
	 * @author Andreas Bröker
	 * @date 25.02.2006
	 */
	private FormBorderPainter() {
	}	
  //----------------------------------------------------------------------------
	
}