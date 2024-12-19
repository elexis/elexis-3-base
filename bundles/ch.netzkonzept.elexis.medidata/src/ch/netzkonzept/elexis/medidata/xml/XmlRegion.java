package ch.netzkonzept.elexis.medidata.xml;

/****************************************************************************
*
* Copyright (c) 2012-2018, Vincent Zurczak - All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*****************************************************************************/

/**
 * A XML region, with a type, a start position (included) and an end position
 * (excluded).
 * <p>
 * A XML region is limited in the range [start, end[
 * </p>
 *
 * @author Vincent Zurczak
 * @version 1.0 (tag version)
 */
public class XmlRegion {

	public enum XmlRegionType {
		INSTRUCTION, COMMENT, CDATA, MARKUP, ATTRIBUTE, MARKUP_VALUE, ATTRIBUTE_VALUE, WHITESPACE, UNEXPECTED;
	}

	private final XmlRegionType xmlRegionType;
	private final int start;
	private int end;

	/**
	 * Constructor.
	 * 
	 * @param xmlRegionType
	 * @param start
	 */
	public XmlRegion(XmlRegionType xmlRegionType, int start) {
		this.xmlRegionType = xmlRegionType;
		this.start = start;
	}

	/**
	 * Constructor.
	 * 
	 * @param xmlRegionType
	 * @param start
	 * @param end
	 */
	public XmlRegion(XmlRegionType xmlRegionType, int start, int end) {
		this(xmlRegionType, start);
		this.end = end;
	}

	/**
	 * @return the end
	 */
	public int getEnd() {
		return this.end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * @return the xmlRegionType
	 */
	public XmlRegionType getXmlRegionType() {
		return this.xmlRegionType;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return this.start;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object #toString()
	 */
	@Override
	public String toString() {
		return this.xmlRegionType + " [" + this.start + ", " + this.end + "[";
	}
}
