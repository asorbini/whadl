package org.mentalsmash.whadl;

import java.io.PrintStream;

import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.Equipment;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.UnitMemberInstance;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.model.patterns.EmptyPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataDumper {

	private final Logger log = LoggerFactory.getLogger(DataDumper.class);

	private PrintStream out = System.out;
	private boolean toLog = true;

	public DataDumper() {

	}

	public DataDumper(boolean toLog) {
		this.toLog = toLog;
	}

	public void setToLog() {
		this.toLog = true;
	}

	public void setToOut() {
		this.toLog = false;
	}

	private void dump(String str) {
		if (toLog) {
			this.log.info(str);
		} else {
			this.out.println(str);
		}
	}

	public void dumpArmy(Army army, String indent) {

		dump(indent + "ARMY " + army.getName());

		if (!(army.getSuperPattern() instanceof EmptyPattern)) {
			dump(indent + "  EXTENDS " + army.getSuperPattern());
		}

		for (Equipment eq : army.getEquipments()) {
			dump(indent + "  EQUIPMENT " + eq);
		}

		for (Unit u : army.getUnits()) {
			dumpUnit(u, indent + "  ");
		}
		
		if (!army.getConditions().equals(LiteralExpression.BOOLEAN_TRUE)){
			dump(indent+"  ARMY-CONDITIONS: "+army.getConditions());
		}
	}

	public void dumpUnit(Unit u, String indent) {
		dump(indent + "UNIT: " + u);

		if (!(u.getSuperPattern() instanceof EmptyPattern)) {
			dump(indent + "  EXTENDS " + u.getSuperPattern());
		}

		if (u.getCostExpression() != null) {
			dump(indent + "  COST: " + u.getCostExpression());
		}

		if (u.getSlotsPattern() != null) {
			dump(indent + "  SLOTS: " + u.getSlotsPattern());
		}

		if (u.getCompositionPattern() != null) {
			dump(indent + "  COMPOSITION: " + u.getCompositionPattern());
		}

		for (UnitMember mem : u.getMembers()) {
			if (mem == null)
				continue;
			dumpUnitMember(mem, indent + "    ");

		}

		if (u.getUpgradesPattern() != null) {
			dump(indent + "  UPGRADES:" + u.getUpgradesPattern());
		}

		if (u.getModifiedUnits().size() > 0) {
			dump(indent + "  SPECIAL:");
			for (Unit un : u.getModifiedUnits()) {
				dumpUnit(un, indent + "    ");
			}
			if (!u.getArmyModifiedConditions().equals(
					new LiteralExpression(true))) {
				dump(indent + "    ARMY-CONDITIONS: "
						+ u.getArmyModifiedConditions());
			}
		}
		
		if (!u.getConditions().equals(LiteralExpression.BOOLEAN_TRUE)){
			dump(indent+"  UNIT-CONDITIONS: "+u.getConditions());
		}
	}

	public void dumpUnitMember(UnitMember mem, String indent) {
		dump(indent + "MEMBER: " + mem);
		if (mem.getSuperPattern() != null
				&& !(mem.getSuperPattern() instanceof EmptyPattern)) {
			dump(indent + "  EXTENDS " + mem.getSuperPattern());
		}

		dump(indent + "  EQUIP: " + mem.getEquipmentPattern());
		if (!mem.getConditions().equals(LiteralExpression.BOOLEAN_TRUE)){
			dump(indent+"  MEMBER-CONDITIONS: "+mem.getConditions());
		}
	}

	public void dumpArmyInstance(ArmyInstance inst, String indent) {
		dump(indent + "BUILD " + inst.getName() + " {");

		indent = indent(indent);

		dump(indent + "TYPE: " + inst.getTypeName());

		dump(indent + "COST: " + inst.getFinalCost());

		for (UnitInstance unit : inst.getUnits()) {
			indent = dumpUnitInstance(indent, unit);
		}

		indent = deindent(indent);
		dump(indent + "}");
	}

	public String dumpUnitInstance(String indent, UnitInstance unit) {
		dump(indent + "UNIT " + unit.getName() + " {");

		indent = indent(indent);

		dump(indent + "TYPE: " + unit.getTypeName());
		dump(indent + "COST: " + unit.getFinalCost());
		dump(indent + "SLOT: " + unit.getSlotPattern());

		if (unit.getCompositionPattern() != null) {
			dump(indent + "COMPOSITION: " + unit.getCompositionPattern());
		}

		for (UnitMemberInstance mem : unit.getMembers()) {
			dumpUnitMemberInstance(indent, mem);
		}

		dump(indent+"LINKED: "+unit.getLinkedUnits());
		

//		if (unit.getUpgradesPattern() != null) {
//			dump(indent + "UPGRADES: " + unit.getUpgradesPattern());
//		}
		
		if (unit.getUpgrades().size() > 0) {
			dump(indent+"UPGRADES: "+unit.getUpgrades());
		}
		

		indent = deindent(indent);
		dump(indent + "}\n");
		return indent;
	}

	public void dumpUnitMemberInstance(String indent, UnitMemberInstance mem) {
		this.dumpUnitMemberInstance(indent, mem, false);
	}

	public void dumpUnitMemberInstance(String indent, UnitMemberInstance mem,
			boolean external) {
		String str = (external ? "EXTERNAL " : "") + "MEMBER";

		dump(indent + str + ": " + mem.getName()+ " (" + mem.getTypeName()+")");

		if (!external) {

			indent = indent(indent);
			// dump(indent + "SPECIFIED EQUIPMENT: " +
			// mem.getEquipmentPattern());
			dump(indent + "EQUIPMENT: " + mem.getEquipments());

			if (mem.getFinalCost() > 0) {
				dump(indent + "COST: " + mem.getFinalCost());
			}

			indent = deindent(indent);

		}
	}

	private String indent(String indent) {
		return indent + "  ";
	}

	private String deindent(String indent) {
		return indent.substring(0, indent.length() - 2);
	}
}
