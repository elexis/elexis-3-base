/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.base.ch.ebanking.esr;

import static ch.elexis.base.ch.ebanking.EBankingACLContributor.DISPLAY_ESR;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.AbstractDataLoaderJob;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.actions.JobPool;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.CommonViewer.PoDoubleClickListener;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.LazyContentProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.data.AccountTransaction;
import ch.elexis.data.Anwender;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.elexis.data.Zahlung;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.Result;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class ESRView extends ViewPart implements IActivationListener {
	CommonViewer cv;
	ViewerConfigurer vc;
	ESRLoader esrloader;

	Query<ESRRecord> qbe;
	private Action loadESRFile;
	private ViewMenus menus;
	private ESRSelectionListener esrl;
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private TimeTool ttBooking = new TimeTool();

	private final ElexisEventListenerImpl eeli_user = new ElexisEventListenerImpl(Anwender.class,
			ElexisEvent.EVENT_USER_CHANGED) {

		@Override
		public void catchElexisEvent(ElexisEvent ev) {
			JobPool.getJobPool().activate("ESR-Loader", Job.SHORT); //$NON-NLS-1$
		}

	};

	@Override
	public void dispose() {
		GlobalEventDispatcher.removeActivationListener(this, getViewSite().getPart());
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		cv = new CommonViewer();
		qbe = new Query<ESRRecord>(ESRRecord.class);
		esrloader = (ESRLoader) JobPool.getJobPool().getJob("ESR-Loader"); //$NON-NLS-1$
		if (esrloader == null) {
			esrloader = new ESRLoader(qbe);
			JobPool.getJobPool().addJob(esrloader);
		}

		vc = new ViewerConfigurer(new LazyContentProvider(cv, esrloader, DISPLAY_ESR), new ESRLabelProvider(),
				new DefaultControlFieldProvider(cv, new String[] { "Datum" }),
				new ViewerConfigurer.DefaultButtonProvider(),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, cv));
		cv.create(vc, parent, SWT.None, getViewSite());
		JobPool.getJobPool().activate("ESR-Loader", Job.SHORT); //$NON-NLS-1$
		makeActions();
		menus = new ViewMenus(getViewSite());
		menus.createToolbar(loadESRFile);
		menus.createMenu(loadESRFile);
		esrl = new ESRSelectionListener();
		cv.addDoubleClickListener(new PoDoubleClickListener() {
			public void doubleClicked(PersistentObject obj, CommonViewer cv) {
				ESRRecordDialog erd = new ESRRecordDialog(getViewSite().getShell(), (ESRRecord) obj);
				if (erd.open() == Dialog.OK) {
					cv.notify(CommonViewer.Message.update);
				}
			}

		});
		GlobalEventDispatcher.addActivationListener(this, getViewSite().getPart());
	}

	private boolean isOldShown = false;

	@Override
	public void setFocus() {
		if (!isOldShown) {
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Ansicht veraltet",
					"Die Ansicht " + getTitle()
							+ " ist veraltet, und wird nicht mehr unterstützt. Bitte verwenden Sie die ESR Ansicht.");
			isOldShown = true;
		}
	}

	class ESRLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {
		DecimalFormat df = new DecimalFormat("###0.00"); //$NON-NLS-1$

		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof ESRRecord) {
				ESRRecord rec = (ESRRecord) element;
				if (rec.getTyp().equals(ESRRecord.MODE.Summenrecord)) {
					return "-- Datei eingelesen am: " + rec.get("Datum") + ", " + rec.getBetrag() //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
							+ " --"; //$NON-NLS-1$
				} else if (rec.getId().equals("1")) { //$NON-NLS-1$
					return Messages.ESRView_headline;
				}
				StringBuilder sb = new StringBuilder(100);
				Rechnung rn = rec.getRechnung();
				if (rn != null) {
					sb.append(rn.getNr()).append(": "); //$NON-NLS-1$
				}
				String betrag = rec.getBetrag().getAmountAsString();
				sb.append(rec.getEinlesedatatum()).append("/").append(rec.getVerarbeitungsdatum()) //$NON-NLS-1$
						.append("/").append(rec.getValuta()).append(" - ").append( //$NON-NLS-1$ //$NON-NLS-2$
								rec.getPatient().getLabel())
						.append(" - ").append(betrag); //$NON-NLS-1$
				String dat = rec.getGebucht();
				if (StringTool.isNothing(dat)) {
					sb.append(Messages.ESRView_not_booked);
				} else {
					sb.append(Messages.ESRView_booked).append(new TimeTool(dat).toString(TimeTool.DATE_GER));
				}
				return sb.toString();
			}
			return null;
		}

		public Color getForeground(Object element, int columnIndex) {
			return UiDesk.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		}

		public Color getBackground(Object element, int columnIndex) {
			if (element instanceof ESRRecord) {
				ESRRecord rec = (ESRRecord) element;
				if (rec.getTyp().equals(ESRRecord.MODE.Summenrecord)) {
					return UiDesk.getDisplay().getSystemColor(SWT.COLOR_GREEN);
				}
				String buch = rec.getGebucht();
				if (rec.getRejectCode().equals(ESRRecord.REJECT.OK)) {
					if (StringTool.isNothing(buch)) {
						return UiDesk.getDisplay().getSystemColor(SWT.COLOR_GRAY);
					}
					return UiDesk.getDisplay().getSystemColor(SWT.COLOR_WHITE);
				}
				return UiDesk.getDisplay().getSystemColor(SWT.COLOR_RED);
			}
			return UiDesk.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE);
		}

	}

	class ESRLoader extends AbstractDataLoaderJob {
		Query<ESRRecord> qbe;

		ESRLoader(Query<ESRRecord> qbe) {
			super("ESR-Loader", qbe, new String[] { //$NON-NLS-1$
					"Datum" //$NON-NLS-1$
			});
			this.qbe = qbe;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor) {
			monitor.beginTask(Messages.ESRView_loadESR, SWT.INDETERMINATE);

			qbe.clear();
			if (CoreHub.acl.request(AccessControlDefaults.ACCOUNTING_GLOBAL) == false) {
				if (CoreHub.actMandant == null) {
					return Status.CANCEL_STATUS;
				}
				qbe.startGroup();
				qbe.add("MandantID", "=", CoreHub.actMandant.getId()); //$NON-NLS-1$ //$NON-NLS-2$
				qbe.or();
				qbe.add("MandantID", StringUtils.EMPTY, null); //$NON-NLS-1$
				qbe.add("RejectCode", "<>", "0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				qbe.endGroup();
				qbe.and();
			}

			vc.getControlFieldProvider().setQuery(qbe);
			qbe.orderBy(true, new String[] { "Datum", "Gebucht" //$NON-NLS-1$ //$NON-NLS-2$
			});
			List<ESRRecord> list = qbe.execute();
			result = list.toArray();
			monitor.done();
			return Status.OK_STATUS;
		}

		@Override
		public int getSize() {
			return PersistentObject.getConnection().queryInt("SELECT COUNT(0) FROM ESRRECORDS"); //$NON-NLS-1$

		}

	}

	private void makeActions() {
		loadESRFile = new Action(Messages.ESRView_read_ESR) {
			{
				setToolTipText(Messages.ESRView_read_ESR_explain);
				setImageDescriptor(Images.IMG_IMPORT.getImageDescriptor());
			}

			@Override
			public void run() {
				FileDialog fld = new FileDialog(getViewSite().getShell(), SWT.OPEN);
				fld.setText(Messages.ESRView_selectESR);
				final String filename = fld.open();
				if (filename != null) {
					final ESRFile esrf = new ESRFile();
					final File file = new File(filename);
					try {
						PlatformUI.getWorkbench().getProgressService().busyCursorWhile(new IRunnableWithProgress() {

							public void run(IProgressMonitor monitor)
									throws InvocationTargetException, InterruptedException {
								monitor.beginTask(Messages.ESRView_reading_ESR, (int) (file.length() / 25));
								Result<List<ESRRecord>> result = esrf.read(file, monitor);
								if (result.isOK()) {
									for (ESRRecord rec : result.get()) {
										monitor.worked(1);
										if (rec.getRejectCode().equals(ESRRecord.REJECT.OK)) {
											if (rec.getTyp().equals(ESRRecord.MODE.Summenrecord)) {
												log.info(Messages.ESRView_ESR_finished + rec.getBetrag());
											} else if ((rec.getTyp().equals(ESRRecord.MODE.Storno_edv))
													|| (rec.getTyp().equals(ESRRecord.MODE.Storno_Schalter))) {
												Rechnung rn = rec.getRechnung();
												Money zahlung = rec.getBetrag().negate();
												Zahlung zahlungsObj = rn.addZahlung(zahlung,
														Messages.ESRView_storno_for + rn.getNr() + " / " //$NON-NLS-1$
																+ rec.getPatient().getPatCode(),
														new TimeTool(rec.getValuta()));
												if (zahlungsObj != null && ESR.getAccount() != null) {
													AccountTransaction transaction = zahlungsObj.getTransaction();
													transaction.setAccount(ESR.getAccount());
												}
												rec.setGebucht(null);
											} else {
												Rechnung rn = rec.getRechnung();
												if (rn.getStatus() == RnStatus.BEZAHLT) {
													if (SWTHelper.askYesNo(Messages.ESRView_paid,
															Messages.ESRView_rechnung + rn.getNr()
																	+ Messages.ESRView_ispaid) == false) {
														continue;
													}
												}
												Money zahlung = rec.getBetrag();
												Money offen = rn.getOffenerBetrag();
												if (zahlung.isMoreThan(offen)
														&& (zahlung.doubleValue() - offen.doubleValue() > 0.03)) {
													if (SWTHelper.askYesNo(Messages.ESRView_toohigh,
															Messages.ESRView_paymentfor + rn.getNr()
																	+ Messages.ESRView_morethan) == false) {
														continue;
													}
												}

												ttBooking.set(rec.getValuta());
												Zahlung zahlungsObj = rn.addZahlung(zahlung,
														Messages.ESRView_vesrfor + rn.getNr() + " / " //$NON-NLS-1$
																+ rec.getPatient().getPatCode(),
														ttBooking);
												if (zahlungsObj != null && ESR.getAccount() != null) {
													AccountTransaction transaction = zahlungsObj.getTransaction();
													transaction.setAccount(ESR.getAccount());
												}
												rec.setGebucht(ttBooking);
											}
										}
									}
									monitor.done();
								} else {
									ResultAdapter.displayResult(result, Messages.ESRView_errorESR);
								}
							}

						});
					} catch (InvocationTargetException e) {
						ExHandler.handle(e);
						SWTHelper.showError(Messages.ESRView_errorESR2, Messages.ESRView_errrorESR2,
								Messages.ESRView_couldnotread + e.getMessage() + e.getCause().getMessage());
					} catch (InterruptedException e) {
						ExHandler.handle(e);
						SWTHelper.showError("ESR interrupted", Messages.ESRView_interrupted, e //$NON-NLS-1$
								.getMessage());
					}

				}
				JobPool.getJobPool().activate("ESR-Loader", Job.SHORT); //$NON-NLS-1$
				// cv.notify(CommonViewer.Message.update);
			}
		};
	}

	public void activation(boolean mode) {
		// TODO Auto-generated method stub

	}

	public void visible(boolean mode) {
		if (mode) {
			ElexisEventDispatcher.getInstance().addListeners(eeli_user, esrl);
		} else {
			ElexisEventDispatcher.getInstance().removeListeners(eeli_user, esrl);
		}
		esrl.activate(mode);
	}

}
