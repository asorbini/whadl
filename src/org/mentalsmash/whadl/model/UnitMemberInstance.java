package org.mentalsmash.whadl.model;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.model.patterns.Pattern;

public class UnitMemberInstance extends BaseEntity {

	private UnitInstance unit;
	private UnitMember type;

	private int finalCost = 0;

	private final CollectionEntity<EquipmentInstance> equipments = new CollectionEntity<EquipmentInstance>();

	public CollectionEntity<EquipmentInstance> getEquipments() {
		return this.equipments;
	}

	public void addEquipment(EquipmentInstance e) {
		this.equipments.add(e);
	}

	public void removeEquipment(EquipmentInstance e) {
		this.equipments.remove(e);
	}

	private Pattern equipmentPattern = Pattern.DEFAULT_PATTERN;
	private String typeName = "NONE";

	public UnitMemberInstance(String type) {
		super(type+ (System.currentTimeMillis()+"").substring(11),
				new LiteralExpression(true));
		this.typeName = type;
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {

		}
	}

	public UnitMemberInstance(String type, String name) {
		super(name, new LiteralExpression(true));
		this.typeName = type;
	}

	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName(String type) {
		this.typeName = type;
	}

	public void setEquipmentPattern(Pattern equipmentPattern) {
		this.equipmentPattern = equipmentPattern;
	}

	public Pattern getEquipmentPattern() {
		return this.equipmentPattern;
	}

	public static final String ATTRIBUTE_TYPE = "type";
	public static final String ATTRIBUTE_EQUIPMENT = "equipment";
	public static final String ATTRIBUTE_COST = "cost";
	public static final String ATTRIBUTE_UNIT = "unit";

	@Override
	public Object getAttribute(String selector) {
		if (selector.equals(ATTRIBUTE_TYPE)) {
			return this.getType();
		} else if (selector.equals(ATTRIBUTE_EQUIPMENT)) {
			return this.getEquipments();
		} else if (selector.equals(ATTRIBUTE_COST)) {
			return new ObjectWrapperEntity(this.getFinalCost());
		} else if (selector.equals(ATTRIBUTE_UNIT)) {
			return this.getUnit();
		} else {
			return super.getAttribute(selector);
		}
	}

	@Override
	public void setAttribute(String selector, Object value) {
		if (selector.equals(ATTRIBUTE_TYPE)
				|| selector.equals(ATTRIBUTE_EQUIPMENT)) {
			throw new WhadlRuntimeException("Read-Only attribute: " + selector);
		} else {
			super.setAttribute(selector, value);
		}
	}

	public void setFinalCost(int finalCost) {
		this.finalCost = finalCost;
	}

	public int getFinalCost() {
		return finalCost;
	}

	public void setUnit(UnitInstance unit) {
		this.unit = unit;
	}

	public UnitInstance getUnit() {
		return unit;
	}

	public void setType(UnitMember type) {
		this.type = type;
	}

	public UnitMember getType() {
		return type;
	}

	// @Override
	// public String toString() {
	// StringBuilder str = new StringBuilder();
	// str.append(this.getType() + " ");
	// str.append(this.getName() + " ");
	//
	// str.append("(" + this.equipmentPattern + ")");
	//
	// return str.toString();
	// }

}
