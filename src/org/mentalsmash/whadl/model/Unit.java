package org.mentalsmash.whadl.model;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.model.patterns.Pattern;

public class Unit extends BaseEntity {

	private Army army;

	private final CollectionEntity<UnitSlot> availableSlots = new CollectionEntity<UnitSlot>();

	private Pattern superPattern = Pattern.DEFAULT_PATTERN;
	private final CollectionEntity<UnitMember> members = new CollectionEntity<UnitMember>();
	private Pattern slotsPattern = Pattern.DEFAULT_PATTERN;
	private Pattern compositionPattern = Pattern.DEFAULT_PATTERN;
	private Pattern upgradesPattern = Pattern.DEFAULT_PATTERN;
	private Expression costExpression = Expression.DEFAULT_EXPRESSION;
	private final CollectionEntity<Unit> modifiedUnits = new CollectionEntity<Unit>();
	private Expression armyModifiedConditions = new LiteralExpression(true);
	private Pattern linkedUnitsPattern = Pattern.DEFAULT_PATTERN;
	
	public Unit(String name) {
		super(name, new LiteralExpression(true));
	}
	
	
	public Unit(Unit u){
		super(u.getName(),u.getConditions());
		this.superPattern = u.superPattern;
		this.slotsPattern = u.slotsPattern;
		this.compositionPattern = u.compositionPattern;
		this.upgradesPattern = u.upgradesPattern;
		this.costExpression = u.costExpression;
		this.armyModifiedConditions = u.armyModifiedConditions;
		this.linkedUnitsPattern = u.linkedUnitsPattern;
		this.army = u.army;
		for (UnitMember mem : u.getMembers()) {
			UnitMember m = new UnitMember(mem);
			m.setUnit(this);
			this.addMember(m);
		}
		for (Unit modUnit : u.getModifiedUnits()) {
			this.addModifiedUnit(new Unit(modUnit));
		}
		for (UnitSlot slot : u.availableSlots) {
			this.availableSlots.add(slot);
		}
		
		this.setReference(u.getReference());
	}

	public Pattern getCompositionPattern() {
		return compositionPattern;
	}

	public Pattern getSlotsPattern() {
		return slotsPattern;
	}

	public void setSlotsPattern(Pattern slots) {
		this.slotsPattern = slots;
	}

	public Expression getCostExpression() {
		return costExpression;
	}

	public Pattern getUpgradesPattern() {
		return upgradesPattern;
	}

	public void setUpgradesPattern(Pattern upgradesPattern) {
		this.upgradesPattern = upgradesPattern;
	}

	public CollectionEntity<UnitMember> getMembers() {
		return members;
	}

	public CollectionEntity<Unit> getModifiedUnits() {
		return this.modifiedUnits;
	}

	public UnitMember getMember(String memberName) {
		for (UnitMember m : this.members) {
			if (m.getName().equals(memberName)) {
				return m;
			}
		}

		return null;
	}

	public static final String ATTRIBUTE_MEMBERS = "members";
	public static final String ATTRIBUTE_COMPOSITION = "composition";
	public static final String ATTRIBUTE_SLOTS = "slots";
	public static final String ATTRIBUTE_COST = "cost";
	public static final String ATTRIBUTE_UPGRADES = "upgrades";
	public static final String ATTRIBUTE_SPECIAL = "special";
	public static final String ATTRIBUTE_ARMY = "army";

	@Override
	public Object getAttribute(String selector) {
		if (selector.equals(ATTRIBUTE_MEMBERS)) {
			return this.getMembers();
		} else if (selector.equals(ATTRIBUTE_COMPOSITION)) {
			return this.getCompositionPattern();
		} else if (selector.equals(ATTRIBUTE_ARMY)) {
			return this.getArmy();
		} else if (selector.equals(ATTRIBUTE_SLOTS)) {
			return this.getSlots();
		} else if (selector.equals(ATTRIBUTE_COST)) {
			return this.getCostExpression();
		} else if (selector.equals(ATTRIBUTE_UPGRADES)) {
			return this.getUpgradesPattern();
		} else if (selector.equals(ATTRIBUTE_SPECIAL)) {
			return this.getModifiedUnits();
		} else {
			return super.getAttribute(selector);
		}
	}

	@Override
	public void setAttribute(String selector, Object value) {
		if (selector.equals(ATTRIBUTE_MEMBERS)
				|| selector.equals(ATTRIBUTE_COMPOSITION)
				|| selector.equals(ATTRIBUTE_SLOTS)
				|| selector.equals(ATTRIBUTE_COST)
				|| selector.equals(ATTRIBUTE_UPGRADES)
				|| selector.equals(ATTRIBUTE_SPECIAL)) {
			throw new WhadlRuntimeException("Read-Only attribute: " + selector);
		} else {
			super.setAttribute(selector, value);
		}
	}

	public void setCompositionPattern(Pattern compositionPattern) {
		this.compositionPattern = compositionPattern;

	}

	public void setCostExpression(Expression costExpression) {
		this.costExpression = costExpression;

	}

	public void setSuperPattern(Pattern superPattern) {
		this.superPattern = superPattern;
	}

	public Pattern getSuperPattern() {
		return superPattern;
	}

	public void addMember(UnitMember m) {
		this.members.add(m);
		m.setUnit(this);
	}

	public void removeMember(UnitMember m) {
		this.members.remove(m);
		m.setUnit(null);
	}

	public void addModifiedUnit(Unit modU) {
		this.modifiedUnits.add(modU);

	}

	public void removeModifiedUnit(Unit modU) {
		this.modifiedUnits.remove(modU);

	}

	public void setArmyModifiedConditions(Expression armyModifiedConditions) {
		this.armyModifiedConditions = armyModifiedConditions;
	}

	public Expression getArmyModifiedConditions() {
		return armyModifiedConditions;
	}

	public void setArmy(Army army) {
		this.army = army;
	}

	public Army getArmy() {
		return army;
	}

	public CollectionEntity<UnitSlot> getSlots() {
		return availableSlots;
	}

	public void addSlot(UnitSlot slot){
		this.availableSlots.add(slot);
	}
	
	public boolean removeSlot(UnitSlot slot){
		return this.availableSlots.remove(slot);
	}


	public void setLinkedUnitsPattern(Pattern linkedUnitsPattern) {
		this.linkedUnitsPattern = linkedUnitsPattern;
	}


	public Pattern getLinkedUnitsPattern() {
		return linkedUnitsPattern;
	}
	
}
