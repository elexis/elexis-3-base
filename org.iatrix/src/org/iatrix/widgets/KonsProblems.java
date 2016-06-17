/*******************************************************************************
 * Copyright (c) 2007-2015, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *     Gerry Weirich - adapted for 2.1
 *     Niklaus Giger - small improvements, split into 20 classes
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.iatrix.actions.IatrixEventHelper;
import org.iatrix.data.Problem;
import org.iatrix.util.DateComparator;
import org.iatrix.views.IatrixViewTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.icpc.Encounter;
import ch.elexis.icpc.Episode;

public class KonsProblems implements IJournalArea {

	private Konsultation actKons = null;
	private FormToolkit tk;
	private static Logger log = LoggerFactory.getLogger(KonsProblems.class);
	private static CheckboxTableViewer problemAssignmentViewer;
	public Action unassignProblemAction;
	private static final DateComparator DATE_COMPARATOR = new DateComparator();
	private Label lProbleme;

	public KonsProblems(Composite assignmentComposite){
		tk = UiDesk.getToolkit();
		lProbleme = tk.createLabel(assignmentComposite, "Probleme", SWT.LEFT);
		lProbleme.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		problemAssignmentViewer = CheckboxTableViewer.newCheckList(assignmentComposite, SWT.SINGLE);
		Table problemAssignmentTable = problemAssignmentViewer.getTable();
		tk.adapt(problemAssignmentTable);
		makeActions();
		problemAssignmentViewer.getControl()
			.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		problemAssignmentViewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(Object inputElement){
				/*
				 * if (actKons != null) { List<Problem> problems = Problem
				 * .getProblemsOfKonsultation(actKons); return problems.toArray(); } return new
				 * Problem[0];
				 */

				if (actKons != null) {
					// get all problems of the current patient
					List<Problem> patientProblems =
						Problem.getProblemsOfPatient(actKons.getFall().getPatient());
					List<Problem> konsProblems = Problem.getProblemsOfKonsultation(actKons);

					// we only show active or assigned problems
					List<Problem> problems = new ArrayList<Problem>();

					// add active problems
					for (Problem problem : patientProblems) {
						if (problem.getStatus() == Episode.ACTIVE) {
							problems.add(problem);
						}
					}

					// add already assigned problems
					for (Problem problem : konsProblems) {
						if (!problems.contains(problem)) {
							problems.add(problem);
						}
					}

					// sort by date
					Collections.sort(problems, DATE_COMPARATOR);
					return problems.toArray();
				}

				return new Problem[] {};
			}

			@Override
			public void dispose(){
				// nothing to do
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
				// nothing to do
			}
		});
		problemAssignmentViewer.setLabelProvider(new ProblemAssignmentLabelProvider());

		problemAssignmentViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event){
				if (actKons == null) {
					return;
				}

				Object element = event.getElement();
				if (element instanceof Problem) {
					Problem problem = (Problem) element;
					if (event.getChecked()) {
						problem.addToKonsultation(actKons);
					} else {
						// remove problem. ask user if encounter still contains data.
						IatrixViewTool.removeProblemFromKonsultation(actKons, problem);
					}
				}
				updateProblemAssignmentViewer();
				ElexisEventDispatcher.fireSelectionEvent(actKons);
			}
		});
		problemAssignmentViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event){
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if (sel != null) {
					if (sel.size() == 1) {
						Object obj = sel.getFirstElement();
						if (obj instanceof Problem) {
							Problem problem = (Problem) obj;
							IatrixEventHelper.fireSelectionEventProblem(problem);

							// select corresponding encounter. This should actually be done by the
							// ICPC plugin via GlobalEvents
							Encounter encounter = problem.getEncounter(actKons);
							if (encounter != null) {
								ElexisEventDispatcher.fireSelectionEvent(encounter);
							} else {
								ElexisEventDispatcher.clearSelection(Encounter.class);
							}
						}
					}
				}
			}
		});

		problemAssignmentViewer.setInput(this);

	}

	private void makeActions(){
		unassignProblemAction = new Action("Problem entfernen") {
			{
				setToolTipText("Problem von Konsulation entfernen");
			}

			@Override
			public void run(){
				Object sel = ((IStructuredSelection) problemAssignmentViewer.getSelection())
					.getFirstElement();
				if (sel != null) {
					Problem problem = (Problem) sel;
					problem.removeFromKonsultation(actKons);
					updateProblemAssignmentViewer();
					logEvent("unassignProblemAction: " + problem.getTitle());
					ElexisEventDispatcher.fireSelectionEvents(actKons);
				}
			}
		};

	}

	private void updateProblemAssignmentViewer(){
		problemAssignmentViewer.refresh();

		// set selection
		if (actKons != null) {
			List<Problem> problems = Problem.getProblemsOfKonsultation(actKons);
			problemAssignmentViewer.setCheckedElements(problems.toArray());
			problemAssignmentViewer.refresh();

			lProbleme.setText("Probleme");
		} else {
			// empty selection
			problemAssignmentViewer.setCheckedElements(new Problem[] {});
			problemAssignmentViewer.refresh();
			lProbleme.setText("Probleme");
		}
	}

	class ProblemAssignmentLabelProvider extends LabelProvider implements ITableColorProvider {
		@Override
		public Color getForeground(Object element, int columnIndex){
			Color color = null;

			if (problemAssignmentViewer.getChecked(element)) {
				color = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
			} else {
				color = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
			}

			return color;
		}

		@Override
		public Color getBackground(Object element, int columnIndex){
			// we don't set the background
			return null;
		}

	}

	public CheckboxTableViewer getProblemAssignmentViewer(){
		return problemAssignmentViewer;
	}

	@Override
	public void setPatient(Patient newPatient){}

	@Override
	public void setKons(Konsultation newKons, KonsActions op){
		if (op == KonsActions.ACTIVATE_KONS) {
			actKons = newKons;
			updateProblemAssignmentViewer();
		}
	}

	@Override
	public void visible(boolean mode){}

	@Override
	public void activation(boolean mode){
		if (mode == true) {
			log.debug("activation " + mode);
			setKons((Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class), KonsActions.ACTIVATE_KONS);
		} else {
			setKons(null, KonsActions.ACTIVATE_KONS);
		}
	}

	private void logEvent(String msg){
		StringBuilder sb = new StringBuilder(msg + ": ");
		if (actKons == null) {
			sb.append("actKons null");
		} else {
			Patient pat = actKons.getFall().getPatient();
			sb.append(actKons.getId());
			sb.append(" kons vom " + actKons.getDatum());
			sb.append(" " + pat.getId() + ": " + pat.getPersonalia());
		}
		log.debug(sb.toString());
	}

}
