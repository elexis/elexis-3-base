/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 ******************************************************************************/
package at.medevit.elexis.weblinks.model;

import org.apache.commons.lang3.StringUtils;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class WebLinkElement {
	private String text;
	private String link;
	String id;

	public WebLinkElement(String id) {
		this.setId(id);
		init();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private void init() {
		text = ConfigServiceHolder.getUser(WebLinkElementUtil.getTextConfig(id), StringUtils.EMPTY);
		link = ConfigServiceHolder.getUser(WebLinkElementUtil.getLinkConfig(id), StringUtils.EMPTY);
	}

	public void save() {
		ConfigServiceHolder.setUser(WebLinkElementUtil.getTextConfig(id), text);
		ConfigServiceHolder.setUser(WebLinkElementUtil.getLinkConfig(id), link);
	}

	public void delete() {
		ConfigServiceHolder.setUser(WebLinkElementUtil.getTextConfig(id), null);
		ConfigServiceHolder.setUser(WebLinkElementUtil.getLinkConfig(id), null);
	}
}
