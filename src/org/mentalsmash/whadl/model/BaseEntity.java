package org.mentalsmash.whadl.model;

import java.util.HashMap;
import java.util.Map;

import org.mentalsmash.whadl.model.expressions.Expression;

public class BaseEntity implements Entity {

	private final Map<String, Object> attributes = new HashMap<String, Object>();
	private Expression conditions;
	private String entityRef;
	private String name;
	
	private final Map<String, String> descriptions = new HashMap<String, String>();

	public BaseEntity() {

	}

	public BaseEntity(String name, Expression conditions) {
		this.conditions = conditions;
		this.name = name;
		this.setReference(name);
		// System.out.println("New entity created: "+this.getReference());
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public final static String ATTRIBUTE_NAME = "name";
	public final static String ATTRIBUTE_REF = "ref";

	@Override
	public Object getAttribute(String selector) {
		if (selector.equals(ATTRIBUTE_NAME)) {
			return this.getName();
		} else if (selector.equals(ATTRIBUTE_REF)) {
			return this.getReference();
		}

		return this.attributes.get(selector);
	}

	@Override
	public void setAttribute(String selector, Object value) {
		if (selector.equals(ATTRIBUTE_NAME)) {
			this.setName((String) value);
		} else if (selector.equals(ATTRIBUTE_REF)) {
			this.setReference((String) value);
		}

		this.attributes.put(selector, value);
	}

	@Override
	public Expression getConditions() {
		return this.conditions;
	}

	public void setConditions(Expression conditions) {
		this.conditions = conditions;
	}

	@Override
	public void setReference(String entityRef) {
		this.entityRef = entityRef;
	}

	@Override
	public String getReference() {
		return this.entityRef;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.getReference() == null) ? 0 : this.getReference()
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Entity))
			return false;
		Entity other = (Entity) obj;
		
		
		if (this.getReference() == null) {
			if (other.getReference() != null)
				return false;
		} else if (!this.getReference().equals(other.getReference())) {

			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public Map<String, String> getDescriptions() {
		return this.descriptions;
	}

	@Override
	public void setDescription(String label, String description) {
		this.descriptions.put(label, description);
	}

}
