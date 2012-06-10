package org.mentalsmash.whadl.validation.processors;

import java.util.Map;

import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.UnitSlot;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.model.patterns.SinglePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArmySetDefaultValuesProcessor implements EntityProcessor<Army> {

	private static final Logger log = LoggerFactory
			.getLogger(ArmySetDefaultValuesProcessor.class);

	@Override
	public Army process(Army e) throws EntityProcessingException {
		this.setDefaultValues(e);
		return e;
	}

	protected void setDefaultValues(Army a) {
		if (!a.getName().equals("BaseArmy") && a.getSuperPattern().isDefault()) {
			Pattern base = new SinglePattern("BaseArmy", 1);
			log
					.debug("Setting Army " + a + "'s " + " super pattern to "
							+ base);
			a.setSuperPattern(base);
		}

		for (Unit u : a.getUnits()) {
			this.setDefaultValues(u);
		}
	}

	protected void setDefaultValues(Unit u) {
		if (!u.getName().equals("BaseUnit") && u.getSuperPattern().isDefault()) {
			SinglePattern base = new SinglePattern("BaseArmy.BaseUnit", 1);
			log.debug("Setting Unit " + u + "'s  super pattern to " + base);

			u.setSuperPattern(base);
		}

		Pattern slotPattern = u.getSlotsPattern();
		slotPattern.startEvaluation();
		Map<String, Integer> slots = slotPattern.toMap();
		slotPattern.stopEvaluation();

		// System.err.println("UNIT "+u+" SLOTS: "+slots);

		for (String sl : slots.keySet()) {
			int q = slots.get(sl);
			while ((q--) > 0) {
				u.addSlot(new UnitSlot(sl));
			}
		}

		for (UnitMember m : u.getMembers()) {
			this.setDefaultValues(m);
		}
	}

	protected void setDefaultValues(UnitMember m) {
		if (!m.getName().equals("BaseUnitMember")
				&& m.getSuperPattern().isDefault()) {
			SinglePattern base = new SinglePattern(
					"BaseArmy.BaseUnit.BaseUnitMember", 1);
			log.debug("Setting UnitMember " + m + "'s  super pattern to "
					+ base);

			m.setSuperPattern(base);
		}
	}

	@Override
	public String toString() {
		return "PROC" + "-" + "ARMY" + "-" + "SET.DEFAULT.VALUES";
	}

}
