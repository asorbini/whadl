        package org.mentalsmash.whadl.model;


import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;

public class ArmyInstance extends BaseEntity {

	
	private Army type;
	
	public void setType(Army type) {
		this.type = type;
	}

	private int finalCost = 0;
	private String typePattern = "NONE";

	private final CollectionEntity<UnitInstance> units = new CollectionEntity<UnitInstance>();

	public ArmyInstance(String name) {
		super(name,new LiteralExpression(true));
	}
	

	public CollectionEntity<UnitInstance> getUnits() {
		return this.units;
	}
	
	public UnitInstance getUnit(String name){
		for (UnitInstance ui : this.units) {
			if (ui.getName().equals(name)) {
				return ui;
			}
		}
		
		return null;
	}

	public void addUnit(UnitInstance unit) {
		this.units.add(unit);
		unit.setArmy(this);
	}

	public void removeUnit(UnitInstance unit) {
		this.units.remove(unit);
		unit.setArmy(null);
	}

	public String getTypeName() {
		return typePattern;
	}

	public void setTypeName(String t) {
		this.typePattern = t;
	}

	public static final String ATTRIBUTE_TYPE = "type";
	public static final String ATTRIBUTE_UNITS = "units";
	public static final String ATTRIBUTE_COST = "cost";

	@Override
	public Object getAttribute(String selector) {
		if (selector.equals(ATTRIBUTE_UNITS)) {
			return this.getUnits();
		} else if (selector.equals(ATTRIBUTE_TYPE)) {
			return this.type;
		}  else if (selector.equals(ATTRIBUTE_COST)) {
			return new ObjectWrapperEntity(this.getFinalCost());
		}else {
			return super.getAttribute(selector);
		}
	}

	@Override
	public void setAttribute(String selector, Object value) {
		if (selector.equals(ATTRIBUTE_UNITS) || selector.equals(ATTRIBUTE_TYPE)) {
			throw new WhadlRuntimeException("Read-Only attribute: "
					+ selector);
		} else {
			super.setAttribute(selector, value);
		}
	}

	@Override
	public String toString() {
		return this.getReference();
	}



	public void setFinalCost(int finalCost) {
		this.finalCost = finalCost;
	}



	public int getFinalCost() {
		return finalCost;
	}



	public Army getType() {
		return type;
	}	
	
}
