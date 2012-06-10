package org.mentalsmash.whadl.model;

import java.util.Map;

import org.mentalsmash.whadl.model.expressions.Expression;

public interface Entity {
	public String getName();
	public void setName(String name);
	public Object getAttribute(String selector);
	public void setAttribute(String selector, Object value);
	public Expression getConditions();
	public void setReference(String entityRef);
	public String getReference();
	public Map<String, String> getDescriptions();
	public void setDescription(String label, String description);
}
