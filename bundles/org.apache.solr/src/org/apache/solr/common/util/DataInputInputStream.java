/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.solr.common.util;

import java.io.DataInput;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * An abstract DataInput that extends InputStream
 */
public abstract class DataInputInputStream extends InputStream implements DataInput {

	/**
	 * If possible, read UTF8 bytes directly from the underlying buffer
	 *
	 * @param utf8 the utf8 ubject to read into
	 * @param len  length of the utf8 stream
	 * @return whether it is possible to do a direct read or not
	 */
	boolean readDirectUtf8(ByteArrayUtf8CharSequence utf8, int len) {
		return false;
	}

	/**
	 * If possible, read ByteBuffer directly from the underlying buffer
	 *
	 * @param sz the size of the buffer to be read
	 */
	public ByteBuffer readDirectByteBuffer(int sz) {
		return null;
	};
}
