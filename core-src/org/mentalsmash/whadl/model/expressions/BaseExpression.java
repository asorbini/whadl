package org.mentalsmash.whadl.model.expressions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class BaseExpression implements Expression {

	protected final Map<String, String> descriptions = new HashMap<String, String>();
	
	protected String label = null;
	
	BaseExpression() {
		
	}
	
	BaseExpression(String label){
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return this.label;
	}
	
	@Override
	public void setLabel(String label){
		this.label = label;
	}
	
	@Override
	public Map<String, String> getDescriptions() {
		return this.descriptions;
	}

	@Override
	public void setDescription(String locale, String label) {
		this.descriptions.put(locale, label);
	}

}
