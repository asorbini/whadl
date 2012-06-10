package org.mentalsmash.whadl.model;

import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;

public class SingleEquipment extends BaseEntity implements Equipment {
	
	private Expression baseCost = new LiteralExpression(0);
	private Expression singleWoundCost = new LiteralExpression(0);

	public SingleEquipment(){
		super();
	}
	
	public SingleEquipment(String name) {
		super(name,new LiteralExpression(true));
	}
	
	public SingleEquipment(String name, Expression conditions) {
		super(name, conditions);
	}

	public void setBaseCost(Expression baseCost) {
		this.baseCost = baseCost;
	}

	public Expression getBaseCost() {
		return baseCost;
	}

	public void setSingleWoundCost(Expression singleWoundCost) {
		this.singleWoundCost = singleWoundCost;
	}

	public Expression getSingleWoundCost() {
		return singleWoundCost;
	}
	
	public static final String ATTRIBUTE_COST = "cost";
	public static final String ATTRIBUTE_SWCOST = "swcost";

	@Override
	public Object getAttribute(String selector) {
		if (selector.equals(ATTRIBUTE_SWCOST)) {
			return this.getSingleWoundCost();
		} else if (selector.equals(ATTRIBUTE_COST)) {
			return this.getBaseCost();
		} else {
			return super.getAttribute(selector);
		}
	}

	@Override
	public void setAttribute(String selector, Object value) {
		if (selector.equals(ATTRIBUTE_SWCOST)
				|| selector.equals(ATTRIBUTE_COST)) {
			throw new IllegalArgumentException("Read-Only attribute: "
					+ selector);
		} else {
			super.setAttribute(selector, value);
		}
	}
}
