/**
 * Copyright (c) 2010-2012, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 */
package ch.medelexis.templator.model;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SchemaFilterOutputStream extends FilterOutputStream {
	/**
	 *
	 */
	private final ProcessingSchema schema;
	private static final int AWAIT_START = 0;
	private static final int AWAIT_SECOND = 1;
	private static final int AWAIT_THIRD = 2;
	private static final int AWAIT_FOURTH = 4;

	private StringBuilder sb;
	private int state = AWAIT_START;
	private IProcessor proc;

	public SchemaFilterOutputStream(ProcessingSchema schema, OutputStream out, IProcessor proc) {
		super(out);
		this.schema = schema;
		this.proc = proc;
	}

	@Override
	public void flush() throws IOException {
		if (sb != null) {
			for (int i = 0; i < sb.length(); i++) {
				super.write(sb.charAt(i));
			}
			sb = null;
			state = AWAIT_START;
		}
		super.flush();
	}

	@Override
	public void write(int b) throws IOException {

		switch (state) {
		case AWAIT_START:
			if (b == '{') {
				sb = new StringBuilder("{");
				state = AWAIT_SECOND;
			} else {
				super.write(b);
			}
			break;
		case AWAIT_SECOND:
			if (b == '{') {
				sb.append("{");
				state = AWAIT_THIRD;
			} else {
				flush();
			}
			break;
		case AWAIT_THIRD:
			sb.append((char) b);
			if (b == '}') {
				state = AWAIT_FOURTH;
			}
			break;
		case AWAIT_FOURTH:
			sb.append((char) b);
			if (b != '}') {
				flush();
			} else {
				String fname = sb.substring(2, sb.length() - 2);
				String contents = schema.getFieldTextEscaped(fname);
				if (contents != null) {
					String replacement = proc.convert(contents);
					for (int c : replacement.getBytes()) {
						super.write(c);
					}
				}

				sb = null;
				state = AWAIT_START;
			}
		}
	}

}