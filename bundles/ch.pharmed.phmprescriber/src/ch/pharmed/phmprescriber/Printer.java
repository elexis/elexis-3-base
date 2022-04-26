/*******************************************************************************
 * Copyright (c) 2014, Pharmed Solutions GmbH
 * All rights reserved.
 *******************************************************************************/

package ch.pharmed.phmprescriber;

import java.awt.Graphics;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

import ch.elexis.data.Rezept;

public class Printer implements Printable {

	// Layout-Constants

	private final double HMARGINRATIO = 0.08;
	private final double WMARGINRATIO = 0.1;

	/**
	 * Constructor: Print prescription
	 * <p>
	 *
	 */
	public Printer(Physician ph, Rezept rp, String presID, String QRCode) {

		// Create a printerJob object
		PrinterJob printJob = PrinterJob.getPrinterJob();
		// Create printerJob to simulate the required number of pages
		PrinterJob printJobSimulation = PrinterJob.getPrinterJob();

		// Set the name of the PrinterJob
		printJob.setJobName(presID);

		// Define the page format including margins
		PageFormat pf = printJob.defaultPage();
		Paper paper = new Paper();

		double marginHeight = paper.getHeight() * HMARGINRATIO;
		double marginWidth = paper.getWidth() * WMARGINRATIO;

		paper.setImageableArea(marginWidth, marginHeight, paper.getWidth() - marginWidth * 2,
				paper.getHeight() - marginHeight * 2);

		pf.setPaper(paper);

		// Determine the number of pages and the last product of each page based on the
		// strings to be printed
		Book bkSimulation = new Book();
		Pagecounter pcounter = new Pagecounter(ph, rp, presID, QRCode);
		bkSimulation.append(pcounter, pf);

		printJobSimulation.setPageable(bkSimulation);

		// Do the simulation
		try {

			printJobSimulation.print();

		} catch (PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Get the indices (last item per page) from the simulation
		List<Integer> pageIndices = pcounter.getIndices();

		// Create a book with the determined number of pages
		Book bk = new Book();

		// Create now the pages of the book (new instances of PrintPage for each page)
		// Specify, which of the listed products is the first and the last on this page

		for (int i = 0; i < pageIndices.size() - 1; i = i + 1) {

			bk.append(new Page(ph, rp, presID, QRCode, pageIndices.get(i) + 1, pageIndices.get(i + 1)), pf);

		}

		// Assign the pages
		printJob.setPageable(bk);

		// Show print dialog and start printing if "yes"
		if (printJob.printDialog()) {

			try {

				printJob.print();

			} catch (Exception PrintException) {
				PrintException.printStackTrace();
			}
		}

	}

	public PageFormat getPageFormat(int p1) throws java.lang.IndexOutOfBoundsException {
		return PrinterJob.getPrinterJob().defaultPage();
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		// TODO Auto-generated method stub
		return 0;
	}

}
