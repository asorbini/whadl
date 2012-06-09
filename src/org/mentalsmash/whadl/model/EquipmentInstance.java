package org.mentalsmash.whadl.model;


import org.mentalsmash.whadl.model.expressions.LiteralExpression;

public class EquipmentInstance extends BaseEntity {
	private int cost;
	private Equipment type;

	public EquipmentInstance(Equipment type, int cost) {
		super(type.getName(), new LiteralExpression(true));
		this.setType(type);
		this.setCost(cost);
		this.setReference(type.getReference());
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getCost() {
		return cost;
	}

	public void setType(Equipment type) {
		this.type = type;
	}

	public Equipment getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return this.type.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof EquipmentInstance) {
			return this.type.equals(((EquipmentInstance)obj).type);
		} else if (obj instanceof Equipment){
			return this.type.equals(obj);
		} else if (obj instanceof ObjectWrapperEntity) {
			return this.equals(((ObjectWrapperEntity)obj).getObject());
		}
		
		return false;
	}

	@Override
	public String toString() {
		return this.type + " [" + cost + "]";
	}

	public static final String ATTRIBUTE_TYPE = "type";
	public static final String ATTRIBUTE_COST = "cost";

	@Override
	public Object getAttribute(String selector) {
		if (selector.equals(ATTRIBUTE_TYPE)) {
			return this.getType();
		} else if (selector.equals(ATTRIBUTE_COST)) {
			return new ObjectWrapperEntity(this.getCost());
		} else {
			return super.getAttribute(selector);
		}
	}
}
