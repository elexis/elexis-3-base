package ch.elexis.views;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import ch.elexis.base.ch.arzttarife.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung;
import ch.elexis.core.services.INamedQuery;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;

public class TardocCodeSelectorContentProvider implements ICommonViewerContentProvider, ITreeContentProvider {

	private List<ITardocLeistung> roots;

	private CommonViewer commonViewer;

	private boolean isFiltered;

	private HashMap<String, List<ITardocLeistung>> filteredLeafs;

	private RefreshExecutor refreshExecutor;

	private String currentZiffer;
	private String currentText;

	private INamedQuery<ITardocLeistung> childrenQuery;

	private INamedQuery<ITardocLeistung> childrenChapterQuery;

	public TardocCodeSelectorContentProvider(CommonViewer commonViewer) {
		this.commonViewer = commonViewer;

		this.childrenQuery = ArzttarifeModelServiceHolder.get().getNamedQuery(ITardocLeistung.class, "parent");
		this.childrenChapterQuery = ArzttarifeModelServiceHolder.get().getNamedQuery(ITardocLeistung.class, "parent",
				"chapter");

		filteredLeafs = new HashMap<>();

		refreshExecutor = new RefreshExecutor();
	}

	@Override
	public void changed(HashMap<String, String> values) {
		currentZiffer = values.get("Ziffer");
		currentText = values.get("Text");
		if (shouldFilter(currentZiffer, currentText)) {
			if (!isFiltered) {
				isFiltered = true;
			}
			refreshExecutor.add(new RefreshRunnable(currentZiffer, currentText));
		} else if (isFiltered) {
			isFiltered = false;
			commonViewer.getViewerWidget().getControl().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					StructuredViewer viewer = commonViewer.getViewerWidget();
					viewer.setSelection(new StructuredSelection());
					viewer.getControl().setRedraw(false);
					viewer.refresh();
					viewer.getControl().setRedraw(true);
				}
			});
		}
	}

	private void refreshLeafs(String queryZiffer, String queryText) {
		filteredLeafs.clear();
		// prepare query
		IQuery<ITardocLeistung> leafsQuery = ArzttarifeModelServiceHolder.get().getQuery(ITardocLeistung.class);
		if (queryZiffer != null && queryZiffer.length() > 2) {
			leafsQuery.and("code_", COMPARATOR.LIKE, queryZiffer.toUpperCase() + "%");
		}
		if (queryText != null && queryText.length() > 2) {
			leafsQuery.and("tx255", COMPARATOR.LIKE, "%" + queryText + "%");
		}
		leafsQuery.and("isChapter", COMPARATOR.EQUALS, false);
		leafsQuery.orderBy("code_", ORDER.ASC);
		// execute query and populate filtered leafs map
		List<ITardocLeistung> leafs = leafsQuery.execute();
		for (ITardocLeistung tarmedLeistung : leafs) {
			String parentId = tarmedLeistung.getParent().getId();
			List<ITardocLeistung> list = filteredLeafs.get(parentId);
			if (list == null) {
				list = new ArrayList<>();
			}
			list.add(tarmedLeistung);
			filteredLeafs.put(parentId, list);
		}
	}

	private boolean shouldFilter(String ziffer, String text) {
		return ziffer.length() > 2 || text.length() > 2;
	}

	@Override
	public void reorder(String field) {
		// TODO Auto-generated method stub
		System.out.println(field);
	}

	@Override
	public void selected() {
	}

	@Override
	public void init() {
	}

	@Override
	public void startListening() {
		commonViewer.getConfigurer().getControlFieldProvider().addChangeListener(this);
	}

	@Override
	public void stopListening() {
		commonViewer.getConfigurer().getControlFieldProvider().removeChangeListener(this);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (roots == null) {
			roots = getRoots();
		}
		List<ITardocLeistung> ret = roots;
		if (isFiltered) {
			ret = purgeRoots(ret);
		}
		return ret.toArray();
	}

	private List<ITardocLeistung> getRoots() {
		return childrenQuery.executeWithParameters(childrenQuery.getParameterMap("parent", "NIL"));
	}

	private List<ITardocLeistung> purgeRoots(List<ITardocLeistung> roots) {
		ArrayList<ITardocLeistung> ret = new ArrayList<>();
		for (ITardocLeistung root : roots) {
			if (currentZiffer != null && currentZiffer.length() > 2) {
				if (root.getCode().equalsIgnoreCase(currentZiffer.substring(0, 2))) {
					ret.add(root);
				}
			} else {
				if (subChaptersHaveChildren(root)) {
					ret.add(root);
				}
			}
		}
		return ret;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ITardocLeistung) {
			ITardocLeistung parentLeistung = (ITardocLeistung) parentElement;
			if (!isFiltered) {
				return childrenQuery
						.executeWithParameters(childrenQuery.getParameterMap("parent", parentLeistung.getId()))
						.toArray();
			} else {
				if (subChaptersHaveChildren(parentLeistung)) {
					return getFilteredChapterChildren(parentLeistung).toArray();
				}
			}
		}
		return null;
	}

	private boolean subChaptersHaveChildren(ITardocLeistung parentLeistung) {
		List<ITardocLeistung> children = getFilteredChapterChildren(parentLeistung);
		for (ITardocLeistung tarmedLeistung : children) {
			if (tarmedLeistung.isChapter()) {
				if (subChaptersHaveChildren(tarmedLeistung)) {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof ITardocLeistung) {
			ITardocLeistung leistung = (ITardocLeistung) element;
			return leistung.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object parentElement) {
		if (parentElement instanceof ITardocLeistung) {
			ITardocLeistung parentLeistung = (ITardocLeistung) parentElement;
			if (!isFiltered) {
				return !childrenQuery
						.executeWithParameters(childrenQuery.getParameterMap("parent", parentLeistung.getId()))
						.isEmpty();
			} else {
				List<ITardocLeistung> filteredChildren = getFilteredChapterChildren(parentLeistung);
				return !filteredChildren.isEmpty();
			}
		}
		return false;
	}

	private List<ITardocLeistung> getFilteredChapterChildren(ITardocLeistung parentLeistung) {
		List<ITardocLeistung> ret = new ArrayList<>();
		List<ITardocLeistung> chapterChildren = getChapterChildren(parentLeistung);
		if (!chapterChildren.isEmpty()) {
			for (ITardocLeistung chapter : chapterChildren) {
				if (subChaptersHaveChildren(chapter)) {
					ret.add(chapter);
				}
			}
		}
		List<ITardocLeistung> leafs = filteredLeafs.get(parentLeistung.getId());
		if (leafs != null && !leafs.isEmpty()) {
			ret.addAll(leafs);
		}
		return ret;
	}

	private List<ITardocLeistung> getChapterChildren(ITardocLeistung parentLeistung) {
		return childrenChapterQuery.executeWithParameters(
				childrenChapterQuery.getParameterMap("parent", parentLeistung.getId(), "chapter", true));
	}

	/**
	 * Executor for the {@link RefreshRunnable} to refresh the viewer async. Will
	 * start a timer checking for new filter every 500ms. The timer expires after 30
	 * runs without a change.
	 *
	 * @author thomas
	 *
	 */
	private class RefreshExecutor {
		private Executor executor = Executors.newSingleThreadExecutor();
		private boolean isRunning = false;

		private Timer timer;
		private int timerCountDown;

		private RefreshRunnable currentRunnable;
		private long currentRunnableMs;

		public void add(RefreshRunnable refreshRunnable) {
			checkTimer();
			synchronized (RefreshExecutor.class) {
				currentRunnable = refreshRunnable;
				currentRunnable.setExecutor(this);
				currentRunnableMs = System.currentTimeMillis();
			}
		}

		private void checkTimer() {
			synchronized (RefreshExecutor.class) {
				if (timer == null) {
					timer = new Timer();
					timerCountDown = 30;
					timer.scheduleAtFixedRate(new TimerTask() {
						@Override
						public void run() {
							synchronized (RefreshExecutor.class) {
								if (!isRunning && currentRunnable != null
										&& (System.currentTimeMillis() - currentRunnableMs) > 750) {
									executor.execute(currentRunnable);
									setIsRunning(true);
									currentRunnable = null;
								}
								if (--timerCountDown == 0) {
									timer.cancel();
									timer = null;
								}
							}
						}
					}, 250, 250);
				}
			}
		}

		public void setIsRunning(boolean value) {
			this.isRunning = value;
			timerCountDown = 30;
		}
	}

	/**
	 * Refresh the content and the viewer.
	 *
	 * @author thomas
	 *
	 */
	private class RefreshRunnable implements Runnable {
		private RefreshExecutor refreshExecutor;

		private String queryZiffer;
		private String queryText;

		private Display display;

		public RefreshRunnable(String queryZiffer, String queryText) {
			this.queryText = queryText;
			this.queryZiffer = queryZiffer;
			this.display = commonViewer.getViewerWidget().getControl().getDisplay();
		}

		public void setExecutor(RefreshExecutor refreshExecutor) {
			this.refreshExecutor = refreshExecutor;
		}

		@Override
		public void run() {
			if (queryText != null && queryText.length() > 2) {
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						ProgressMonitorDialog pmd = new ProgressMonitorDialog(display.getActiveShell());
						try {
							pmd.run(true, false, new IRunnableWithProgress() {

								@Override
								public void run(IProgressMonitor monitor)
										throws InvocationTargetException, InterruptedException {
									monitor.beginTask("Text Suche nach (" + queryText + ")", IProgressMonitor.UNKNOWN);
									doWork();
								}
							});
						} catch (InvocationTargetException | InterruptedException e) {
							// ignore !?
						}
					}
				});
			} else {
				doWork();
			}
		}

		private void doWork() {
			refreshLeafs(queryZiffer, queryText);
			if (this.refreshExecutor != null) {
				this.refreshExecutor.setIsRunning(false);
			}
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					StructuredViewer viewer = commonViewer.getViewerWidget();
					viewer.setSelection(new StructuredSelection());
					viewer.getControl().setRedraw(false);
					viewer.refresh();
					if ((queryZiffer != null && queryZiffer.length() > 4)
							|| (queryText != null && queryText.length() > 4)) {
						if (viewer instanceof TreeViewer) {
							((TreeViewer) viewer).expandAll();
						}
					} else {
						((TreeViewer) viewer).collapseAll();
					}
					viewer.getControl().setRedraw(true);
				}
			});
		}
	}
}
