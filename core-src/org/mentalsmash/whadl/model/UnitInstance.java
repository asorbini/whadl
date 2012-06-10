package org.mentalsmash.whadl.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.model.patterns.Pattern;

public class UnitInstance extends BaseEntity {

	private ArmyInstance army;
	private Unit type;

	private final CollectionEntity<UnitSlot> slots = new CollectionEntity<UnitSlot>();

	private int finalCost = 0;

	private String typeName = "NONE";
	private Pattern slotPattern = Pattern.DEFAULT_PATTERN;
	private Pattern compositionPattern = Pattern.DEFAULT_PATTERN;
	private Pattern upgradesPattern = Pattern.DEFAULT_PATTERN;
	private final CollectionEntity<UnitMemberInstance> members = new CollectionEntity<UnitMemberInstance>();
	// private final Map<UnitInstance, Collection<UnitMemberInstance>>
	// externalMembers = new HashMap<UnitInstance,
	// Collection<UnitMemberInstance>>();
	private Pattern linkedUnitsPattern = Pattern.DEFAULT_PATTERN;
	private final CollectionEntity<UnitInstance> linkedUnits = new CollectionEntity<UnitInstance>();

	public UnitInstance(String name) {
		super(name, new LiteralExpression(true));
	}

	public UnitInstance() {
		this("NONAMEUNIT" + (System.currentTimeMillis() + "").substring(11));

		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {

		}

	}

	public Pattern getCompositionPattern() {
		return this.compositionPattern;
	}

	public void setCompositionPattern(Pattern compositionPattern) {
		this.compositionPattern = compositionPattern;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String type) {
		if (this.typeName.equals("NONE")
				&& this.getName().startsWith("NONAMEUNIT")) {
			this.setName(type + this.getName().split("NONAMEUNIT")[1]);
			this.setReference(this.getName());
		}

		this.typeName = type;
	}

	public Pattern getSlotPattern() {
		return slotPattern;
	}

	public void setSlotPattern(Pattern slotPattern) {
		this.slotPattern = slotPattern;
	}

	public Pattern getUpgradesPattern() {
		return upgradesPattern;
	}

	public void setUpgradesPattern(Pattern upgradesPattern) {
		this.upgradesPattern = upgradesPattern;
	}

	public void addMember(UnitMemberInstance mem) {
		this.members.add(mem);
		mem.setUnit(this);
	}

	public void removeMember(UnitMemberInstance mem) {
		this.members.remove(mem);
		mem.setUnit(null);
	}

	public CollectionEntity<UnitMemberInstance> getMembers() {
		return members;
	}
	
	public UnitMemberInstance getMember(String name){
		for (UnitMemberInstance umi : this.members) {
			if (umi.getName().equals(name)) {
				return umi;
			}
		}
		
		return null;
	}

	public static final String ATTRIBUTE_UPGRADES = "upgrades";
	public static final String ATTRIBUTE_MEMBERS = "members";
	public static final String ATTRIBUTE_LINKED = "linked";
	public static final String ATTRIBUTE_TYPE = "type";
	public static final String ATTRIBUTE_SLOTS = "slots";
	public static final String ATTRIBUTE_COST = "cost";
	public static final String ATTRIBUTE_ARMY = "army";
	public static final String ATTRIBUTE_SIZE = "size";

	@Override
	public Object getAttribute(String selector) {
		if (selector.equals(ATTRIBUTE_UPGRADES)) {
			return this.getUpgrades();
		} else if (selector.equals(ATTRIBUTE_MEMBERS)) {
			CollectionEntity<Entity> members = new CollectionEntity<Entity>();
			for (UnitMemberInstance m : this.members) {
				members.add(m);
			}
			return members;
		} else if (selector.equals(ATTRIBUTE_TYPE)) {
			return this.getType();
		} else if (selector.equals(ATTRIBUTE_COST)) {
			return new ObjectWrapperEntity(this.getFinalCost());
		} else if (selector.equals(ATTRIBUTE_ARMY)) {
			return this.getArmy();
		} else if (selector.equals(ATTRIBUTE_SIZE)) {
			return new ObjectWrapperEntity(this.getMembers().size());
		} else if (selector.equals(ATTRIBUTE_SLOTS)) {

			return this.getSlots();

		} else if (selector.equals(ATTRIBUTE_LINKED)) {
			// CollectionEntity<Entity> members = new
			// CollectionEntity<Entity>();
			// for (UnitInstance ui : this.externalMembers.keySet()) {
			// Collection<UnitMemberInstance> membs = this.externalMembers
			// .get(ui);
			// members.addAll(membs);
			// }
			// return members;

			CollectionEntity<UnitInstance> linked = new CollectionEntity<UnitInstance>();
			for (UnitInstance ui : this.linkedUnits) {
				linked.add(ui);
			}
			return linked;
		} else {
			return super.getAttribute(selector);
		}
	}

	@Override
	public void setAttribute(String selector, Object value) {
		if (selector.equals(ATTRIBUTE_UPGRADES)
				|| selector.equals(ATTRIBUTE_MEMBERS)
				|| selector.equals(ATTRIBUTE_TYPE)
				|| selector.equals(ATTRIBUTE_SLOTS)) {
			throw new WhadlRuntimeException("Read-Only attribute: " + selector);
		} else {
			super.setAttribute(selector, value);
		}
	}

	private final Map<Equipment, Integer> upgrades = new HashMap<Equipment, Integer>();

	public CollectionEntity<Equipment> getUpgrades() {
		CollectionEntity<Equipment> ups = new CollectionEntity<Equipment>();
		for (Equipment eq : this.upgrades.keySet()) {
			Integer q = this.upgrades.get(eq);
			while ((q--) > 0) {
				ups.add(eq);
			}
		}
		return ups;
	}

	public void addUpgrades(Equipment e) {
		if (this.upgrades.containsKey(e)) {
			this.upgrades.put(e, this.upgrades.get(e) + 1);
		} else {
			this.upgrades.put(e, 1);
		}
	}

	public void removeUpgrades(Equipment e) {
		if (this.upgrades.containsKey(e)) {
			int q = this.upgrades.get(e);
			if (q > 1) {
				this.upgrades.put(e, q - 1);
			} else {
				this.upgrades.remove(e);
			}
		}
	}

	public void setFinalCost(int finalCost) {
		this.finalCost = finalCost;
	}

	public int getFinalCost() {
		return finalCost;
	}

	// public Map<UnitInstance, Collection<UnitMemberInstance>>
	// getExternalMembers() {
	// return externalMembers;
	// }

	// public void addExternalMember(UnitInstance ui, UnitMemberInstance mem) {
	// Collection<UnitMemberInstance> membs = this.externalMembers.get(ui);
	//
	// if (membs == null) {
	// membs = new ArrayList<UnitMemberInstance>();
	// this.externalMembers.put(ui, membs);
	// }
	//
	// membs.add(mem);
	// }

	// public void removeMember(UnitInstance ui, UnitMemberInstance mem) {
	// if (this.externalMembers.containsKey(ui)) {
	// this.externalMembers.get(ui).remove(mem);
	// }
	// }

	public void setArmy(ArmyInstance army) {
		this.army = army;
	}

	public ArmyInstance getArmy() {
		return army;
	}

	public void setType(Unit type) {
		this.type = type;
	}

	public Unit getType() {
		return type;
	}

	public CollectionEntity<UnitSlot> getSlots() {
		return slots;
	}

	public void addSlot(UnitSlot slot) {
		this.slots.add(slot);
	}

	public void removeSlot(UnitSlot slot) {
		this.slots.remove(slot);
	}

	public void setLinkedUnitsPattern(Pattern linkedUnitsPattern) {
		this.linkedUnitsPattern = linkedUnitsPattern;
	}

	public Pattern getLinkedUnitsPattern() {
		return linkedUnitsPattern;
	}

	public CollectionEntity<UnitInstance> getLinkedUnits() {
		return linkedUnits;
	}

	public void addLinkedUnits(UnitInstance unit) {
		this.linkedUnits.add(unit);
	}

	public void removeLinkedUnits(UnitInstance unit) {
		this.linkedUnits.remove(unit);
	}

}
