package at.medevit.elexis.epha.interactions.api.model;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.model.IArticle;

public class Substance {
	private String type;
	private String gtin;
	private String name;
	
	public String getType(){
		return type;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getGtin(){
		return gtin;
	}
	
	public void setGtin(String gtin){
		this.gtin = gtin;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public static Substance of(IArticle article){
		Substance ret = new Substance();
		ret.setType("drug");
		if (StringUtils.isNotBlank(article.getGtin())) {
			ret.setGtin(article.getGtin());
		} else {
			ret.setName(article.getName());
		}
		return ret;
	}
}
