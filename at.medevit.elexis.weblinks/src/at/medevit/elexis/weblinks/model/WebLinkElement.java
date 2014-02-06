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

import ch.elexis.core.data.activator.CoreHub;

public class WebLinkElement {
	private String text;
	private String link;
	String id;
	
	public WebLinkElement(String id){
		this.setId(id);
		init();
	}
	
	public String getText(){
		return text;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public String getLink(){
		return link;
	}
	
	public void setLink(String link){
		this.link = link;
	}
	
	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	private void init(){
		text = CoreHub.userCfg.get(WebLinkElementUtil.getTextConfig(id), "");
		link = CoreHub.userCfg.get(WebLinkElementUtil.getLinkConfig(id), "");
	}
	
	public void save(){
		CoreHub.userCfg.set(WebLinkElementUtil.getTextConfig(id), text);
		CoreHub.userCfg.set(WebLinkElementUtil.getLinkConfig(id), link);
		CoreHub.userCfg.flush();
	}
	
	public void delete(){
		CoreHub.userCfg.remove(WebLinkElementUtil.getTextConfig(id));
		CoreHub.userCfg.remove(WebLinkElementUtil.getLinkConfig(id));
		CoreHub.userCfg.flush();
	}
}
