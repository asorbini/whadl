package org.mentalsmash.whadl.validation.processors;

import java.util.Map;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.model.Equipment;
import org.mentalsmash.whadl.model.EquipmentInstance;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.UnitMemberInstance;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.model.patterns.PatternContentMap;
import org.mentalsmash.whadl.validation.EntityContext;

public class ArmyInstanceSetEntitiesProcessor implements
		EntityProcessor<ArmyInstance> {

	private final EntityContext context;

	public ArmyInstanceSetEntitiesProcessor(EntityContext context) {
		super();
		this.context = context;
	}

	@Override
	public ArmyInstance process(ArmyInstance e)
			throws EntityProcessingException {
		this.setEntitiesData(e);
		return e;
	}

	protected void setEntitiesData(ArmyInstance ai) {
		String type = ai.getTypeName();
		Army a = (Army) this.context.getEntity(type);

		EntityContext ctx = this.context.getSubContext(a);

		for (UnitInstance ui : ai.getUnits()) {

			Map<String, Integer> upgrades = ui.getUpgradesPattern().toMap();
			for (String key : upgrades.keySet()) {
				Entity eqEnt = ctx.getEntity(key);

				if (!(eqEnt instanceof Equipment)) {
					throw new WhadlRuntimeException("The entity " + eqEnt
							+ " is not an Equipment!");
				}

				Equipment equip = (Equipment) eqEnt;

				int q = upgrades.get(key);
				while ((q--) > 0) {
					ui.addUpgrades(equip);
				}

			}

			Pattern linkedUnitsPattern = ui.getLinkedUnitsPattern();

			if (!linkedUnitsPattern.isDefault()) {
				PatternContentMap linkUnitsMap = linkedUnitsPattern.toMap();
				for (String unitName : linkUnitsMap.keySet()) {
					int q = linkUnitsMap.get(unitName);

					for (int i = 0; i < q; i++) {
						UnitInstance linkedUnit = (UnitInstance) this.context
								.getSubContext(ai).getEntity(unitName);
						ui.addLinkedUnits(linkedUnit);
					}

				}
			}

			String unitType = ui.getTypeName();
			Unit u = (Unit) ctx.getEntity(unitType);
			ui.setType(u);

			for (UnitMemberInstance umi : ui.getMembers()) {

				String membType = umi.getTypeName();
				// System.err.println("MEMTYPE: "+membType);
				UnitMember m = (UnitMember) ctx.getSubContext(u).getEntity(
						membType);
				umi.setType(m);

				Map<String, Integer> equipment = umi.getEquipmentPattern()
						.toMap();
				for (String key : equipment.keySet()) {
					Entity eqEnt = ctx.getEntity(key);

					if (!(eqEnt instanceof Equipment)) {
						throw new WhadlRuntimeException("The entity " + eqEnt
								+ " is not an Equipment!");
					}

					Equipment equip = (Equipment) eqEnt;

					int q = equipment.get(key);

					// System.err.println("ADDING "+q+" "+equip+" TO "+umi+" (PATTERN: "+umi.getEquipmentPattern()+")");

					while ((q--) > 0) {
						EquipmentInstance eqInst = new EquipmentInstance(equip,
								0);

						umi.addEquipment(eqInst);
					}
				}

			}

		}
	}

	@Override
	public String toString() {
		return "PROC" + "-" + "ARMYINST" + "-" + "SET.ENTITIES";
	}

}
