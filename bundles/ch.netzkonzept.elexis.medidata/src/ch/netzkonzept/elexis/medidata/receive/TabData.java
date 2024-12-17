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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.netzkonzept.elexis.medidata.receive.messageLog.MessageLogComparator;
import ch.netzkonzept.elexis.medidata.receive.messageLog.MessageLogEntry;
import ch.netzkonzept.elexis.medidata.receive.messageLog.MessageLogView;
import ch.netzkonzept.elexis.medidata.receive.responseDoc.ResponseDocEntry;
import ch.netzkonzept.elexis.medidata.receive.responseDoc.ResponseDocView;
import ch.netzkonzept.elexis.medidata.receive.transmissionLog.TransmissionLogEntry;
import ch.netzkonzept.elexis.medidata.receive.transmissionLog.TransmissionLogView;

public class TabData {

	private Composite composite;
	private Path baseDir;
	private SimpleDateFormat dtFormater;

	public TabData(Composite composite, Path baseDir) {
		this.composite = composite;
		this.baseDir = baseDir;
		dtFormater = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		new MessageLogComparator();
	}

	public Composite buildResponseDocTable(Composite parent) throws IOException {
		Iterator<Path> iterator = Files.list(baseDir.resolve("receive")).iterator();
		ArrayList<ResponseDocEntry> responseDocLog = new ArrayList<ResponseDocEntry>();

		while (iterator.hasNext()) {
			ResponseDocEntry responseDocEntry = new ResponseDocEntry();
			Path p = iterator.next();
			responseDocEntry.setCreated(dtFormater.format(new Date(p.toFile().lastModified())));
			responseDocEntry.setFilename(p.getFileName().toString());
			responseDocEntry.setPath(p.toAbsolutePath().toString());
			responseDocLog.add(responseDocEntry);
		}

		Composite tableComposite = new Composite(composite, SWT.NO_BACKGROUND);
		ResponseDocEntry[] responseDogEntry = new ResponseDocEntry[responseDocLog.size()];
		int i = 0;
		for (ResponseDocEntry rde : responseDocLog) {
			responseDogEntry[i] = rde;
			i++;
		}
		ResponseDocView rdv = new ResponseDocView(responseDogEntry, baseDir);
		rdv.createPartControl(tableComposite);
		return rdv.getComposite();
	}

	public Composite buildMessageTable(Composite parent) throws FileNotFoundException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Reader reader = new FileReader(baseDir.resolve("messages.json").toFile());
		MessageLogEntry[] messageLog = gson.fromJson(reader, MessageLogEntry[].class);

		Composite tableComposite = new Composite(composite, SWT.NO_BACKGROUND);
		MessageLogView mlv = new MessageLogView(messageLog);
		mlv.createPartControl(tableComposite);
		return mlv.getComposite();
	}
	
	public Composite buildTransmissionTable(Composite parent) throws FileNotFoundException {
		Gson gson = new Gson();
		Reader reader = new FileReader(baseDir.resolve("transmissions.json").toFile());
		TransmissionLogEntry[] transmissionLog = gson.fromJson(reader, TransmissionLogEntry[].class);
		Composite tableComposite = new Composite(composite, SWT.NO_BACKGROUND);
		TransmissionLogView tlv = new TransmissionLogView(transmissionLog, baseDir);
		tlv.createPartControl(tableComposite);
		return tlv.getComposite();
	}
}
