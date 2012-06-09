package org.mentalsmash.whadl.model;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.model.patterns.Pattern;

public class UnitMember extends BaseEntity {
	
	private Unit unit;
	
	private Pattern equipmentPattern = Pattern.DEFAULT_PATTERN;
	private Pattern superPattern = Pattern.DEFAULT_PATTERN;

	public UnitMember(String name) {
		super(name, new LiteralExpression(true));
	}
	
	public UnitMember(UnitMember mem) {
		super(mem.getName(),mem.getConditions());
		this.equipmentPattern = mem.equipmentPattern;
		this.superPattern = mem.superPattern;
		this.unit = mem.unit;
		this.setReference(mem.getReference());
	}

	public UnitMember(String name, Expression conditions) {
		super(name, conditions);
	}

	public Pattern getEquipmentPattern() {
		return equipmentPattern;
	}

	public void setEquipmentPattern(Pattern eq) {
		this.equipmentPattern = eq;
	}

	public static final String ATTRIBUTE_EQUIPMENT = "equipment";
	public static final String ATTRIBUTE_UNIT = "unit";

	
	@Override
	public Object getAttribute(String selector) {
		if (selector.equals(ATTRIBUTE_EQUIPMENT)) {
			return this.equipmentPattern;
		} if (selector.equals(ATTRIBUTE_UNIT)) {
			return this.equipmentPattern;
		} else {
			return super.getAttribute(selector);
		}
	}

	@Override
	public void setAttribute(String selector, Object value) {
		if (selector.equals(ATTRIBUTE_EQUIPMENT)) {
			throw new WhadlRuntimeException("Read-Only attribute: " + selector);
		} else {
			super.setAttribute(selector, value);
		}
	}

	public Pattern getSuperPattern() {
		return this.superPattern;
	}

	public void setSuperPattern(Pattern sup) {
		this.superPattern = sup;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Unit getUnit() {
		return unit;
	}

}
