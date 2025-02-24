/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst.widget;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import ch.gpb.elexis.cst.Messages;
import ch.gpb.elexis.cst.data.CstGastroColo;
import ch.gpb.elexis.cst.service.CstService;

public class GastroColoCanvas extends CstCanvas {

	int iPixX = 780; // Anzeigebreite in pix
	int iPixY = 120; // Anzeigeh�he in pix
	int xoffBase = 4; // x offset
	int yoffBase = 140; // y offset
	int werteBereich = 160; // Wertebereich der in iPixY/X angezeigt werden soll
	CstGastroColo cstGastroColo = null;

	public GastroColoCanvas(Composite parent, int style, CstGastroColo cstGastroColo) {
		super(parent, style);

		this.cstGastroColo = cstGastroColo;
		setBackground(WHITE);

		setSize(440, 500);

		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				GastroColoCanvas.this.paintControl(e);
			}
		});
	}

	void paintControl(PaintEvent e) {

		int yRow1 = 5;

		GC gc = e.gc;
		gc.setFont(fontSmall);

		gc.drawRectangle(0, 0, iPixX - 1, iPixY - 1);

		gc.drawText(Messages.Cst_Text_Gastroduodenoskopie + "\r\n" + Messages.Cst_Text_Magenspiegelung + "\r\n"
				+ Messages.Cst_Text_am + StringUtils.SPACE
				+ CstService.getReadableFromCompact(cstGastroColo.getDatumGastro()), 10, yRow1);

		gc.drawText(Messages.Cst_Text_Makrobefund, 125, yRow1);

		gc.drawRectangle(160, yRow1 + 4, 8, 8);
		gc.drawRectangle(160, yRow1 + 14, 8, 8);
		gc.drawRectangle(160, yRow1 + 24, 8, 8);

		if (cstGastroColo.getGastroMakroBefund() == '0') {
			gc.drawText("X", 162, yRow1 + 2, true);
		} else if (cstGastroColo.getGastroMakroBefund() == '1') {
			gc.drawText("X", 162, yRow1 + 12, true);
		} else if (cstGastroColo.getGastroMakroBefund() == '2') {
			gc.drawText("X", 162, yRow1 + 22, true);
		}

		gc.drawText(Messages.Cst_Text_normal_pathologisch, 180, yRow1);

		gc.drawText(cstGastroColo.getText1(), 240, yRow1, true);

		// x -60
		gc.drawText(Messages.Cst_Text_Histologie, 550, yRow1);

		gc.drawRectangle(602, yRow1 + 4, 8, 8);
		gc.drawRectangle(602, yRow1 + 14, 8, 8);
		gc.drawRectangle(602, yRow1 + 24, 8, 8);

		if (cstGastroColo.getGastroHistoBefund() == '0') {
			gc.drawText("X", 604, yRow1 + 2, true);
		} else if (cstGastroColo.getGastroHistoBefund() == '1') {
			gc.drawText("X", 604, yRow1 + 12, true);
		} else if (cstGastroColo.getGastroHistoBefund() == '2') {
			gc.drawText("X", 604, yRow1 + 22, true);
		}

		gc.drawText(Messages.Cst_Text_normal_pathologisch, 625, yRow1);

		gc.drawText(cstGastroColo.getText2(), 685, yRow1, true);

		gc.drawLine(120, 0, 120, yoffBase);
		gc.drawLine(540, 0, 540, yoffBase);
		gc.drawLine(0, iPixY / 2, iPixX, iPixY / 2);

		int yRow2 = 65;

		// x -100
		gc.drawText(Messages.Cst_Text_Coloskopie_Dickdarmspiegelung + Messages.Cst_Text_am + StringUtils.SPACE
				+ CstService.getReadableFromCompact(cstGastroColo.getDatumColo()), 10, yRow2);

		gc.drawText(Messages.Cst_Text_Makrobefund, 125, yRow2);

		gc.drawRectangle(160, yRow2 + 4, 8, 8);
		gc.drawRectangle(160, yRow2 + 14, 8, 8);
		gc.drawRectangle(160, yRow2 + 24, 8, 8);

		if (cstGastroColo.getColoMakroBefund() == '0') {
			gc.drawText("X", 162, yRow2 + 2, true);
		} else if (cstGastroColo.getColoMakroBefund() == '1') {
			gc.drawText("X", 162, yRow2 + 12, true);
		} else if (cstGastroColo.getColoMakroBefund() == '2') {
			gc.drawText("X", 162, yRow2 + 22, true);
		}

		gc.drawText(Messages.Cst_Text_normal_pathologisch, 180, yRow2);

		gc.drawText(cstGastroColo.getText3(), 240, yRow2, true);

		// x -60
		gc.drawText(Messages.Cst_Text_Histologie, 550, yRow2);

		gc.drawRectangle(602, yRow2 + 4, 8, 8);
		gc.drawRectangle(602, yRow2 + 14, 8, 8);
		gc.drawRectangle(602, yRow2 + 24, 8, 8);

		gc.drawText(Messages.Cst_Text_normal_pathologisch, 625, yRow2);

		gc.drawText(cstGastroColo.getText4(), 685, yRow2, true);

		if (cstGastroColo.getColoHistoBefund() == '0') {
			gc.drawText("X", 604, yRow2 + 2, true);
		} else if (cstGastroColo.getColoHistoBefund() == '1') {
			gc.drawText("X", 604, yRow2 + 12, true);
		} else if (cstGastroColo.getColoHistoBefund() == '2') {
			gc.drawText("X", 604, yRow2 + 12, true);
		}

		gc.dispose();
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(780, 120);
	}

}
