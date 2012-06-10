package org.mentalsmash.whadl.model;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;

public class UnitSlot extends BaseEntity {

	private Value val;
	
	public UnitSlot(Value val) {
		this(val.toString());
		this.val = val;
	}

	public UnitSlot(String name) {
		super(name, new LiteralExpression(true));

		try {
			val = Value.valueOf(name);
		} catch (Exception e) {
			throw new WhadlRuntimeException("Unknown UnitSlot value: " + name);
		}
	}

	public enum Value {
		HQ, TROOP, ELITE, HEAVY, FAST, FREE, TRANSPORT
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof UnitSlot))
			return false;
		UnitSlot other = (UnitSlot) o;
		return this.val.equals(other.val);
	}
	
	@Override
	public int hashCode(){
		return this.val.hashCode();
	}

}
