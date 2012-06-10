package org.mentalsmash.whadl.model;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.model.patterns.Pattern;

public class Army extends BaseEntity {

	private Pattern superPattern = Pattern.DEFAULT_PATTERN;

	private final CollectionEntity<Equipment> equipments = new CollectionEntity<Equipment>();
	private final CollectionEntity<Unit> units = new CollectionEntity<Unit>();

	public Army(String name) {
		super(name, new LiteralExpression(true));
	}

	public Army(String name, Expression conditions) {
		super(name, conditions);
	}

	public Army(Army a) {
		super(a.getName(), a.getConditions());
		for (Unit u : a.getUnits()) {
			Unit newUnit = new Unit(u);
			newUnit.setArmy(this);
			this.units.add(newUnit);
		}
		this.equipments.addAll(a.equipments);
		this.superPattern = a.superPattern;
		this.setReference(a.getReference());
	}

	public Unit getUnit(String unitName) {
		for (Unit u : this.units) {
			if (u.getName().equals(unitName)) {
				return u;
			}
		}

		return null;
	}

	public CollectionEntity<Equipment> getEquipments() {
		return this.equipments;
	}

	public CollectionEntity<Unit> getUnits() {
		return this.units;
	}

	public void addEquipment(Equipment eq) {
		if (!this.equipments.contains(eq)) {
			this.equipments.add(eq);
		}
	}

	public void removeEquipment(Equipment eq) {
		if (this.equipments.contains(eq)) {
			this.equipments.remove(eq);
		}
	}

	public void addUnit(Unit u) {
		if (!this.units.contains(u)) {
			this.units.add(u);
			u.setArmy(this);
			
			
		}
	}

	public void removeUnit(Unit u) {
		if (this.units.contains(u)) {
			this.units.remove(u);
			u.setArmy(null);
		}
	}

	public static final String ATTRIBUTE_UNITS = "units";
	public static final String ATTRIBUTE_EQUIPMENTS = "equipments";

	@Override
	public Object getAttribute(String selector) {
		if (selector.equals(ATTRIBUTE_UNITS)) {
			return this.getUnits();
		} else if (selector.equals(ATTRIBUTE_EQUIPMENTS)) {
			return this.getEquipments();
		} else {
			return super.getAttribute(selector);
		}
	}

	@Override
	public void setAttribute(String selector, Object value) {
		if (selector.equals(ATTRIBUTE_UNITS)
				|| selector.equals(ATTRIBUTE_EQUIPMENTS)) {
			throw new WhadlRuntimeException("Read-Only attribute: " + selector);
		} else {
			super.setAttribute(selector, value);
		}
	}

	public void setSuperPattern(Pattern superPattern) {
		this.superPattern = superPattern;
	}

	public Pattern getSuperPattern() {
		return superPattern;
	}

}
