/*******************************************************************************
 * Copyright (c) 2007-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    bogdan314 - initial implementation
 * Sponsor:
 *    G. Weirich
 ******************************************************************************/
package ch.elexis.base.textplugin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class TextBox extends EStyledText implements PaintListener {

	public final static int MIN_SIZE = 15;

	private boolean highlight;

	public TextBox(Composite parent, ElexisEditor editor) {
		super(parent, editor, SWT.WRAP);
		setSize(200, 100);
		setRedraw(true);
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				redraw();
			}
		});
		addPaintListener(this);
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
		setBackground(getDisplay().getSystemColor(highlight ? SWT.COLOR_GRAY : SWT.COLOR_WHITE));
	}

	public void setLocation(int x, int y) {
	}

	public void forceLocation(int x, int y) {
		super.setLocation(x, y);
	}

	public void paintControl(PaintEvent e) {
		Point size = getSize();
		if (highlight && size.equals(getSize())) {
			e.gc.drawRectangle(0, 0, size.x - 1, size.y - 1);
		}
	}

	@Override
	public void readFrom(DataInputStream in) throws IOException {
		int x = in.readInt();
		int y = in.readInt();
		int w = in.readInt();
		int h = in.readInt();
		forceLocation(x, y);
		setSize(w, h);
		super.readFrom(in);
	}

	@Override
	public void writeTo(DataOutputStream out) throws IOException {
		out.writeInt(getLocation().x);
		out.writeInt(getLocation().y);
		out.writeInt(getSize().x);
		out.writeInt(getSize().y);
		super.writeTo(out);
	}

}
