package at.medevit.elexis.agenda.ui.composite;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;

public abstract class AsyncContentProposalProvider<T extends Identifiable>
		implements IContentProposalProvider {
	
	private List<IdentifiableContentProposal<T>> proposals =
		new LinkedList<IdentifiableContentProposal<T>>();
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private String[] dbFields = null;
	private ContentProposalAdapter adapter = null;
	
	private static final int PROPOSAL_MONITORING_DELAY = 1000;
	private int lastQueriedContentHash = 0;
	private String contents = null;
	private boolean startMonitoring = false;
	
	public AsyncContentProposalProvider(String... dbFields){
		
		this.dbFields = dbFields;
		getWidget().addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e){
				stopMonitoringProposalChanges();
				if (executor != null) {
					executor.shutdown();
				}
			}
		});
	}
	
	public abstract IQuery<T> createBaseQuery();
	
	public abstract Text getWidget();
	
	/**
	 * Starts proposal change monitoring if not started already
	 */
	private void monitorProposalChanges(){
		if (!startMonitoring) {
			startMonitoring = true;
			Objects.requireNonNull(adapter, "no adapter configured");
			CompletableFuture.runAsync(() -> {
				
				while (startMonitoring) {
					try {
						// delay - wait for any other contents
						Thread.sleep(PROPOSAL_MONITORING_DELAY);
					} catch (InterruptedException e) {
						// ignore
					}
					if (contents.hashCode() != lastQueriedContentHash) {
						//content changed - query content
						IQuery<T> query = createBaseQuery();
						
						String[] searchParts = contents.toLowerCase().split(" ");
						lastQueriedContentHash = contents.hashCode();
						int i = 0;
						for (String searchPart : searchParts) {
							if (i < dbFields.length) {
								if ("dob".equals(dbFields[i])) {
									query.and(dbFields[i], COMPARATOR.LIKE,
										getElexisDateSearchString(searchPart), true);
								} else {
									query.and(dbFields[i], COMPARATOR.LIKE, searchPart + "%", true);
								}
							}
							i++;
						}
						query.limit(100);
						proposals.clear();
						
						for (T o : query.execute()) {
							if (o != null) {
								proposals.add(new IdentifiableContentProposal<T>(o.getLabel(), o));
							}
						}
						Display.getDefault().syncExec(() -> {
							// trigger call getProposals
							Event event = new Event();
							event.character = ' ';
							adapter.getControl().notifyListeners(SWT.KeyDown, event);
							
							event = new Event();
							event.character = ' ';
							adapter.getControl().notifyListeners(SWT.Modify, event);
						});
					} else {
						// stop monitoring - content equals queried content
						stopMonitoringProposalChanges();
					}
				}
			}, executor);
		}
	}
	
	private void stopMonitoringProposalChanges(){
		startMonitoring = false;
	}
	
	@Override
	public IContentProposal[] getProposals(String contents, int position){
		if (contents == null || contents.length() < 1)
			return null;
		this.contents = contents;
		monitorProposalChanges();
		return proposals.toArray(new ContentProposal[] {});
	}
	
	/**
	 * The label to add on the content proposal.
	 * 
	 * @param a
	 * @return
	 */
	public String getLabelForObject(T a){
		return a.getLabel();
	}
	
	/**
	 * Get a database search String for a Elexis date database value. <br />
	 * Used for S:D: mapped values in Query#add, copied and slightly adapted.
	 * 
	 * @param value
	 * @return
	 */
	public static String getElexisDateSearchString(String value){
		StringBuilder sb = null;
		String ret = value.replaceAll("%", "");
		final String filler = "%%%%%%%%";
		// are we looking for the year?
		if (ret.matches("[0-9]{3,}")) {
			sb = new StringBuilder(ret);
			sb.append(filler);
			ret = sb.substring(0, 8);
		} else {
			// replace single digits as in 1.2.1932 with double digits
			// as in 01.02.1932
			int dotCount = ret.length() - ret.replace(".", "").length();
			String[] parts = ret.split("\\.");
			StringJoiner sj = new StringJoiner("");
			for (String string : parts) {
				if (string.length() == 1 && Character.isDigit(string.charAt(0))) {
					sj.add("0" + string);
				} else {
					sj.add(string);
				}
			}
			// remove dots
			sb = new StringBuilder(sj.toString());
			int lengthNoDots = sb.length();
			// String must consist of 8 or more digits (ddmmYYYY)
			sb.append(filler);
			if (dotCount == 1 && lengthNoDots == 6) {
				// convert to YYYYmmdd format
				ret = sb.substring(2, 6) + sb.substring(0, 2) + sb.substring(6, 8);
			} else {
				// convert to YYYYmmdd format
				ret = sb.substring(4, 8) + sb.substring(2, 4) + sb.substring(0, 2);
			}
		}
		return ret;
	}
	
	public void configureContentProposalAdapter(ContentProposalAdapter adapter){
		this.adapter = adapter;
		
	}
}
