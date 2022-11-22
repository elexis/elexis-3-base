/*******************************************************************************
 * Copyright (c) 2020-2022,  Fabian Schmid and Olivier Debenath
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Fabian <f.schmid@netzkonzept.ch> - initial implementation
 *    Olivier Debenath <olivier@debenath.ch>
 *
 *******************************************************************************/
package ch.netzkonzept.elexis.medidata.receive;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Iterator;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.elexis.core.constants.StringConstants;

public class TabData {

	private Composite composite;
	private Path baseDir;
	private SimpleDateFormat dtFormater;

	public TabData(Composite composite, Path baseDir) {
		this.composite = composite;
		this.baseDir = baseDir;
		dtFormater = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	}

	public Table buildResponseDocsTable() throws IOException {
		TableViewer viewer = new TableViewer(composite,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		createCol(viewer, "Datum", 100);
		createCol(viewer, "Dateiname", 250);
		createCol(viewer, "Pfad", 250);

		table.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				String pathToReceivedFile = table.getSelection()[0].getText(2);
				try {
					Program proggie = Program.findProgram(StringConstants.SPACE);
					if (proggie != null) {
						proggie.execute(pathToReceivedFile);
					} else {
						if (Program.launch(pathToReceivedFile) == false) {
							Runtime.getRuntime().exec(pathToReceivedFile);
						}
					}
				} catch (Exception ex) {
				}
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
			}

			@Override
			public void mouseUp(MouseEvent arg0) {
			}
		});

		System.out.println(baseDir.resolve("receive").toString());
		Iterator<Path> iterator = Files.list(baseDir.resolve("receive")).iterator();
		while (iterator.hasNext()) {
			TableItem item = new TableItem(table, SWT.NONE);
			Path p = iterator.next();
			item.setText(0, dtFormater.format(new Date(p.toFile().lastModified())));
			item.setText(1, p.getFileName().toString());
			item.setText(2, p.toAbsolutePath().toString());
		}

		return table;
	}

	public Table buildMessageLogsTable() throws FileNotFoundException, ParseException {
		TableViewer viewer = new TableViewer(composite,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		createCol(viewer, "Datum", 100);
		createCol(viewer, "ID", 100);
		createCol(viewer, "Titel", 400);
		createCol(viewer, "Nachricht", 400);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Reader reader = new FileReader(baseDir.resolve("messages.json").toFile());

		MessageLogEntry[] messageLog = gson.fromJson(reader, MessageLogEntry[].class);

		for (MessageLogEntry messageLogEntry : messageLog) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(1, (messageLogEntry.getId() != null ? messageLogEntry.getId() : "--"));
			item.setText(2,
					(messageLogEntry.getSubject() != null ? messageLogEntry.getSubject().getDe().toString() : "--"));
			item.setText(3,
					(messageLogEntry.getMessage() != null ? messageLogEntry.getMessage().getDe().toString() : "--"));
			item.setText(0,
					(messageLogEntry.getCreated() != null ? getDateFormated(messageLogEntry.getCreated()) : "--"));
		}

		return table;
	}

	public Table buildTransmissionLogsTable() throws FileNotFoundException, ParseException {
		TableViewer viewer = new TableViewer(composite,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		createCol(viewer, "Referenz", 250);
		createCol(viewer, "Erstellt", 100);
		createCol(viewer, "Geändert", 100);
		createCol(viewer, "Status", 100);
		createCol(viewer, "Dateiname", 200);

		Gson gson = new Gson();
		Reader reader = new FileReader(baseDir.resolve("transmissions.json").toFile());
		TransmissionLogEntry[] transLog = gson.fromJson(reader, TransmissionLogEntry[].class);

		for (TransmissionLogEntry transmissionLogEntry : transLog) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0,
					(transmissionLogEntry.getTransmissionReference() != null
							? transmissionLogEntry.getTransmissionReference()
							: "--"));
			item.setText(1,
					(transmissionLogEntry.getCreated() != null
							? getDateFormated(transmissionLogEntry.getCreated().toString())
							: "--"));
			item.setText(2,
					(transmissionLogEntry.getModified() != null
							? getDateFormated(transmissionLogEntry.getModified().toString())
							: "--"));
			item.setText(3, (transmissionLogEntry.getStatus() != null ? transmissionLogEntry.getStatus() : "--"));
			item.setText(4,
					(transmissionLogEntry.getInvoiceReference() != null ? transmissionLogEntry.getInvoiceReference()
							: "--"));
		}

		return table;
	}

	private void createCol(TableViewer viewer, String colName, int width) {
		TableViewerColumn tvc = new TableViewerColumn(viewer, SWT.NONE);
		tvc.getColumn().setWidth(width);
		tvc.getColumn().setText(colName);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String p = (String) element;
				return p;
			}
		});
	}

	private String getDateFormated(String date) throws ParseException {
		return dtFormater.format((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)));

	}
}
