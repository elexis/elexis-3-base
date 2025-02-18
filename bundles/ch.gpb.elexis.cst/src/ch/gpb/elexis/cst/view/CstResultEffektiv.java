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
package ch.gpb.elexis.cst.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.Log;
import ch.elexis.data.LabResult;
import ch.gpb.elexis.cst.Activator;
import ch.gpb.elexis.cst.Messages;
import ch.gpb.elexis.cst.data.CstAbstract;
import ch.gpb.elexis.cst.data.CstGroup;
import ch.gpb.elexis.cst.data.CstProfile;
import ch.gpb.elexis.cst.data.LabItemWrapper;
import ch.gpb.elexis.cst.data.ValueFinding;
import ch.gpb.elexis.cst.service.CstService;
import ch.gpb.elexis.cst.widget.CstDangerRangeCanvas;
import ch.gpb.elexis.cst.widget.CstVorwertCanvas;

/**
 *
 * @author daniel ludin ludin@swissonline.ch 27.06.2015
 *
 *         Class for displaying the Effektiv display mode.
 *
 *         Target width for display: 794px (H�lfte: 397 px) Target heigth for
 *         display: 1123px
 */

public class CstResultEffektiv extends CstResultPart {
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ch.gpb.elexis.cst.cstresultminimax";
	private int printHeigth = OUTPUTHEIGTH;

	// @Override
	public void layoutDisplayTest(CstProfile aProfile) {
		int heigth = 20000;
		Image pointer = UiDesk.getImage(Activator.IMG_TEST_NAME);

		baseComposite.setSize(OUTPUTWIDTH, heigth);
		baseComposite.setBounds(new Rectangle(0, 0, OUTPUTWIDTH, heigth));
		baseComposite.layout();

		for (Control control : baseComposite.getChildren()) {
			control.dispose();
		}

		// ResultatCanvasEffektiv rCanvas = new
		// ResultatCanvasEffektiv(baseComposite, SWT.NONE, aProfile);

		Label test = new Label(baseComposite, SWT.NONE);
		test.setImage(pointer);

		/*
		 * GridData gdCanvas = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		 * rCanvas.setLayoutData(gdCanvas); rCanvas.setSize(794, heigth);
		 * rCanvas.setBackground(WHITE);
		 */
		baseComposite.layout();
		/*
		 * rCanvas.redraw();
		 */

	}

	// @Override
	public void layoutDisplay(CstProfile aProfile) {
		if (aProfile != null) {

			// false = a4hoch
			if (aProfile.getAusgabeRichtung()) {
				baseComposite.setSize(OUTPUTHEIGTH, OUTPUTHEIGTH);
				printHeigth = OUTPUTWIDTH;
			} else {
				baseComposite.setSize(OUTPUTWIDTH, OUTPUTHEIGTH);

			}

			log.info("Anzeigetyp:" + aProfile.getAnzeigeTyp());

			// First remove all previous widgets from the display
			for (Control control : baseComposite.getChildren()) {
				control.dispose();
			}

			HashMap<String, HashMap<String, HashMap<String, List<LabResult>>>> labResults = LabResult
					.getGrouped(patient);

			// CstService.printLaborwerte(labResults);

			// the bottom most entry is the newest date
			List<String> sortedDates = CstService.getDistinctDates(labResults);

			if (sortedDates == null || sortedDates.isEmpty()) {
				return;
			}

			int newHeigth = 0;

			baseComposite.setSize(OUTPUTWIDTH, 800);
			baseComposite.setBounds(new Rectangle(0, 0, OUTPUTWIDTH, OUTPUTHEIGTH));
			baseComposite.layout();

			baseComposite.setBackground(WHITE);

			Label labelPatientName = new Label(baseComposite, SWT.NONE);
			labelPatientName.setLayoutData(new GridData());

			labelPatientName.setText(getHeader(patient));

			labelPatientName.setSize(600, 40);
			labelPatientName.setFont(fontMedium);

			Label labelProfileData = new Label(baseComposite, SWT.NONE);
			labelProfileData.setLayoutData(new GridData());
			labelProfileData.setText(getSubTitle(patient, aProfile));

			labelProfileData.setSize(600, 40);
			labelProfileData.setFont(fontSmall);

			// Sort the list of CstGroups of this profile according to its
			// ranking
			@SuppressWarnings("unchecked")
			Map<String, Integer> itemRanking = aProfile.getMap(CstGroup.ITEMRANKING);
			GroupSorter groupSorter = new GroupSorter(itemRanking);

			List<CstGroup> cstGroups = aProfile.getCstGroups();
			Collections.sort(cstGroups, groupSorter);

			int count = 0;

			for (CstGroup group : cstGroups) {

				Label l1 = new Label(baseComposite, SWT.NONE);
				// GridData gd = new GridData(300, 22);
				GridData gd = new GridData(SWT.DEFAULT, 22);
				l1.setLayoutData(gd);
				l1.setText(StringUtils.SPACE + group.getName() + StringUtils.SPACE);
				l1.setFont(fontBig);
				l1.setBackground(GRAY);
				l1.setForeground(WHITE);
				List<LabItemWrapper> labitems = group.getLabitems();

				@SuppressWarnings("unchecked")
				Map<String, Integer> itemRanking2 = group.getMap(CstGroup.ITEMRANKING);

				LabItemSorter labItemSorter = new LabItemSorter(itemRanking2);

				Collections.sort(labitems, labItemSorter);

				for (LabItemWrapper labItem : labitems) {

					Label l2 = new Label(baseComposite, SWT.NONE);
					l2.setLayoutData(new GridData(794, 20));

					String txL2 = "         " + String.valueOf(++count) + ": " + labItem.getLabItem().getName()
							+ StringUtils.SPACE;

					if (labItem.getLabItem().getEinheit().length() > 0) {
						txL2 += " (" + labItem.getLabItem().getEinheit() + ")";

					} /*
						 * if (labItem.getKuerzel() != null) { txL2 += "  " + labItem.getKuerzel(); }
						 */

					l2.setText(txL2);
					l2.setFont(fontMedium);
					l2.setBackground(LIGHTGRAY);

					Composite lineCompo = new Composite(baseComposite, SWT.FILL);

					GridLayout lineLayout = new GridLayout();
					lineLayout.numColumns = 2;
					lineCompo.setLayout(lineLayout);
					GridData lineData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
					lineData.grabExcessHorizontalSpace = true;
					lineData.grabExcessVerticalSpace = true;
					lineCompo.setSize(OUTPUTWIDTH, 100);
					lineCompo.setBackground(WHITE);
					lineCompo.setLayoutData(lineData);

					Composite leftCompo = new Composite(lineCompo, SWT.NONE);
					GridLayout leftLayout = new GridLayout();
					leftCompo.setLayout(leftLayout);
					GridData leftData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
					leftData.grabExcessHorizontalSpace = false;
					leftData.grabExcessVerticalSpace = true;
					leftCompo.setSize(400, 100);
					leftCompo.setLayoutData(leftData);
					leftCompo.setBackground(WHITE);

					Composite rightCompo = new Composite(lineCompo, SWT.NONE);
					GridLayout rightLayout = new GridLayout();
					rightLayout.numColumns = 1;
					rightCompo.setLayout(rightLayout);
					rightCompo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
					rightCompo.setSize(400, 100);
					rightCompo.setBackground(WHITE);

					newHeigth += (lineCompo.getSize().y);
					/*
					 * if (!CstService.hasValueForName(labItem.getLabItem().getName (),
					 * labItem.getLabItem().getKuerzel(), labResults)) { // No Values Label
					 * addNoValuesLabel(leftCompo);
					 *
					 * } else {
					 */
					if (!labItem.isDisplayOnce()) {
						if (!CstService.hasValueForName(labItem.getLabItem().getName(),
								labItem.getLabItem().getKuerzel(), labResults)) {
							// No Values Label
							addNoValuesLabel(leftCompo);
						} else {

							// neuestes Datum aus der Liste der in den
							// LabResults vorhandenen Daten holen
							String sDateOfLatestLabresult = sortedDates.get(sortedDates.size() - 1);
							log.info(
									"Searching result for date:  " + sDateOfLatestLabresult + "\tLabitem: "
											+ labItem.getLabItem().getName() + "\tPat.ID:" + aProfile.getKontaktId(),
									Log.INFOS);

							// Algorithm of Lab Result selection:
							// If the Crawlback is set to 0, we take the newest
							// date from the dates with labresults,
							// an use this date for the DangerRangeCanvas even
							// if there is no result on this date and labitem.
							// The dates before this latest date are used for
							// VorwertCanvas as far as crawlback goes back.
							//
							// If the Crawlback is greater than 0, we take the
							// first date that has a Labresult for the
							// DangerRangeCanvas, and the remaining Labresults
							// for the VorwertCanvas as far as crawlback goes
							// back.s

							LabResult labResultLatest = CstService.getValueForNameAndDate(
									labItem.getLabItem().getName(), sDateOfLatestLabresult,
									labItem.getLabItem().getKuerzel(), labResults);

							ArrayList<String> datesForVorwert = new ArrayList<String>(sortedDates);

							// if the crawlback is not set to 0, we search for
							// the next date before
							// containing a result
							// else we leave the result for DangerRangeCanvas on
							// the latest lab date, which is zero,
							// and provide all dates except the newest for the
							// Findings loop of VorwertCanvas

							if (aProfile.getCrawlBack() > 0) {

								String sNewestDate = sortedDates.get(sortedDates.size() - 1);

								if (labResultLatest == null) {

									for (int i = sortedDates.size() - 1; i >= 0; i--) {

										// starts with bottom most date (=
										// newest)
										String sDateAtIndex = sortedDates.get(i);

										datesForVorwert.remove(i);

										long daysBetween = CstService.getDayCountFromCompact(sDateAtIndex, sNewestDate);
										long crawlBack = aProfile.getCrawlBack();

										if (daysBetween > crawlBack) {
											// the date where the crawlback
											// interrupts marks the date (and
											// all newer dates)
											// that must be removed from the
											// date list for the findings loop
											// that gets values
											// for the vorwertcanvas.

											break;
										}

										LabResult labResultIndex = CstService.getValueForNameAndDate(
												labItem.getLabItem().getName(), sDateAtIndex,
												labItem.getLabItem().getKuerzel(), labResults);

										if (labResultIndex != null) {
											labResultLatest = labResultIndex;
											sDateOfLatestLabresult = sDateAtIndex;
											break;
										}
									}
								} else {
									// there is a value already on the newest
									// date, so we remove this from the date
									// list
									datesForVorwert.remove(datesForVorwert.size() - 1);

								}
							} else {
								datesForVorwert.remove(datesForVorwert.size() - 1);
							}

							// Formatting the Ref values
							double[] dRanges = extractRefValues(labItem.getLabItem());
							double dRangeStart = dRanges[0];
							double dRangeEnd = dRanges[1];

							// Formatting the Result values
							String sResult = StringUtils.EMPTY;

							try {
								if (labResultLatest != null && labResultLatest.getResult() != null) {
									sResult = labResultLatest.getResult();
									log.info("raw result: " + sResult);
								}
							} catch (Exception e1) {
								log.info("Error opening result view: " + e1.getMessage() + StringUtils.SPACE
										+ labItem.getLabItem().getName(), Log.INFOS);
							}

							double dResult = -1;

							dResult = CstService.getNumericFromLabResult(sResult);

							// Fetch the LabResults for the Vorwert Graphic
							List<ValueFinding> findings = new ArrayList<ValueFinding>();

							Collections.reverse(datesForVorwert);

							for (String fDate : datesForVorwert) {

								Date dateResult = CstService.getDateFromCompact(fDate);
								Date startDateProfile = CstService.getDateFromCompact(profile.getValidFrom());
								if (dateResult.compareTo(startDateProfile) < 0) {
									continue;
								}

								LabResult resultVorwert = CstService.getValueForNameAndDate(
										labItem.getLabItem().getName(), fDate, labItem.getLabItem().getKuerzel(),
										labResults);

								// might be null, not every date has a value
								if (resultVorwert == null) {
									// log.info("No LabResult for: " +
									// labItem.getName() + "/" + fDate,
									// Log.INFOS);
									continue;

								}

								String sResultV = null;

								try {
									sResultV = resultVorwert.getResult();
								} catch (Exception e) {
									log.error("Error getting result effektiv: " + e.getMessage(), Log.ERRORS);
									continue;
								}

								double dResultV = 0;
								dResultV = CstService.getNumericFromLabResult(sResultV);

								ValueFinding f = new ValueFinding();
								if (patient.getGeschlecht().toLowerCase().equals("m")) {
									f.setRefMstart(dRangeStart);
									f.setRefMend(dRangeEnd);
									f.setRefFstart(0);
									f.setRefFend(0);
								} else {
									f.setRefFstart(dRangeStart);
									f.setRefFend(dRangeEnd);
									f.setRefMstart(0);
									f.setRefMend(0);

								}

								f.setValue(dResultV);
								f.setDateOfFinding(CstService.getDateFromCompact(fDate));
								f.setParam(sResultV);
								findings.add(f);

							}

							CstVorwertCanvas vCanvas = new CstVorwertCanvas(leftCompo, profile.getAusgabeRichtung(),
									SWT.NONE);
							vCanvas.setFindings(findings);
							GridLayout vorwertLayout = new GridLayout();
							vCanvas.setLayout(vorwertLayout);
							GridData vorwertData = new GridData();
							vorwertData.horizontalAlignment = GridData.FILL;
							vorwertData.grabExcessHorizontalSpace = true;
							vCanvas.setLayoutData(vorwertData);

							if (dResult == -1) {
								// Label label = new Label(rightCompo,
								// SWT.NONE);
								Label label = new Label(leftCompo, SWT.NONE);
								label.setText("No result for Lab Item " + labItem.getLabItem().getName() + " on "
										+ CstService.getGermanFromCompact(sDateOfLatestLabresult));
								GridData gdLabelNoValue = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1);
								label.setLayoutData(gdLabelNoValue);
							} else {

								CstDangerRangeCanvas drc2 = new CstDangerRangeCanvas(leftCompo,
										profile.getAusgabeRichtung(), SWT.NONE, dRangeStart, dRangeEnd, dResult,
										sResult, labItem.getLabItem().getName(),
										CstService.getGermanFromCompact(sDateOfLatestLabresult));
								GridLayout drcLayout = new GridLayout();
								GridData drcData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1);
								drcData.verticalIndent = 30;
								drcData.horizontalAlignment = SWT.BEGINNING;
								drc2.setLayout(drcLayout);
								drc2.setLayoutData(drcData);

							}
						}

					} else {

						// Display Once Label
						StringBuffer lblText = new StringBuffer(Messages.CstResultEffektiv_hinweis_einmal_im_leben);
						Label lblDisplayOnce = new Label(leftCompo, SWT.NONE);
						GridData gdDisplayOnce = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
						gdDisplayOnce.grabExcessHorizontalSpace = true;
						gdDisplayOnce.horizontalAlignment = SWT.FILL;
						gdDisplayOnce.verticalAlignment = SWT.TOP;
						if (aProfile.getAusgabeRichtung()) {
							gdDisplayOnce.widthHint = 858;
						} else {
							gdDisplayOnce.widthHint = 530;
						}
						lblDisplayOnce.setLayoutData(gdDisplayOnce);
						lblDisplayOnce.setBackground(WHITE);

						int countValues = 0;
						for (String date : sortedDates) {
							LabResult labResultOnce = CstService.getValueForNameAndDate(labItem.getLabItem().getName(),
									date, labItem.getLabItem().getKuerzel(), labResults);
							if (labResultOnce != null) {
								countValues++;
							}

						}

						LabResult labResultOnce = null;
						Collections.reverse(sortedDates);
						for (String date : sortedDates) {
							labResultOnce = CstService.getValueForNameAndDate(labItem.getLabItem().getName(), date,
									labItem.getLabItem().getKuerzel(), labResults);
							if (labResultOnce != null) {
								break;
							}

						}

						if (labResultOnce == null) {
							lblText.append(Messages.CstResultEffektiv_resultat_nie_ermittelt);
						} else {
							if (countValues > 1) {
								lblText.append(Messages.CstResultEffktiv_hinweis_immer_anzeigen);

							}
							lblText.append("\n\n");
							lblText.append("Resultat:\t\t");
							lblText.append(labResultOnce.getResult());
							lblText.append("\nDatum:\t\t");
							lblText.append(labResultOnce.getDate());
							lblText.append("\nReferenz:\t");
							if (patient.getGeschlecht().toLowerCase().equals("m")) {
								lblText.append(labItem.getLabItem().getRefM());
							} else {
								lblText.append(labItem.getLabItem().getRefW());

							}

						}

						lblDisplayOnce.setText(lblText.toString());

					}
					// ----

					Text txtAbstract = new Text(rightCompo, /*
															 * SWT.MULTI |SWT.BORDER | SWT.V_SCROLL |
															 */
							SWT.READ_ONLY | SWT.WRAP);
					txtAbstract.setFont(fontSmall);
					txtAbstract.setSize(210, 190);
					GridData gdTxtAbstract = new GridData(GridData.FILL_VERTICAL);
					gdTxtAbstract.verticalAlignment = SWT.TOP;
					gdTxtAbstract.horizontalAlignment = SWT.BEGINNING;
					gdTxtAbstract.widthHint = 210;
					gdTxtAbstract.heightHint = 190;
					gdTxtAbstract.grabExcessVerticalSpace = true;
					txtAbstract.setLayoutData(gdTxtAbstract);
					txtAbstract.setBackground(LIGHTGRAY);
					CstAbstract cabstract = CstAbstract.getByLaboritemId(labItem.getLabItem().getId());
					if (cabstract != null) {
						txtAbstract.setText(cabstract.getDescription1());
					} else {
						txtAbstract.setText(Messages.Cst_Text_no_abstract_available);
					}
					newHeigth += (lineCompo.getSize().y + 40);

					checkPageBreak(baseComposite);

				} // end loop lab items
			} // end loop cst groups

			// fill up the page before adding findings
			baseComposite.pack();
			int currentHeigth = baseComposite.getSize().y;

			int pageCnt = currentHeigth / printHeigth;
			int rmn = ((pageCnt + 1) * printHeigth) - currentHeigth;

			if (rmn < printHeigth) {
				addLine(baseComposite, rmn);

			}
			addBefunde(baseComposite);

			baseComposite.pack();

		}

	}

	@Override
	public void visible(boolean mode) {
		// super.visible(mode);
	}

}
