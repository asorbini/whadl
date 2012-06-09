package org.mentalsmash.whadl.validation.processors;

import java.util.Map;

import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.UnitMemberInstance;
import org.mentalsmash.whadl.model.UnitSlot;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.patterns.ConjunctEntityPatternSet;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.model.patterns.PatternContentMap;
import org.mentalsmash.whadl.validation.EntityContext;
import org.mentalsmash.whadl.validation.UnknownEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArmyInstanceSetDefaultValuesProcessor implements
		EntityProcessor<ArmyInstance> {

	private final Logger log = LoggerFactory
			.getLogger(ArmyInstanceSetDefaultValuesProcessor.class);

	private final EntityContext context;

	public ArmyInstanceSetDefaultValuesProcessor(EntityContext context) {
		super();
		this.context = context;
	}

	@Override
	public ArmyInstance process(ArmyInstance e)
			throws EntityProcessingException {
		this.setDefaultValues(e);
		return e;
	}

	protected void setDefaultValues(ArmyInstance ai)
			throws EntityProcessingException {
		Army a = (Army) this.context.getEntity(ai.getTypeName());

		for (UnitInstance ui : ai.getUnits()) {
			Unit u = a.getUnit(ui.getTypeName());

			if (u == null) {
				throw new UnknownEntityException(this.context, ui.getTypeName());
			}

			Pattern upsPattern = ui.getUpgradesPattern();

			if (upsPattern.isDefault()) {
				Pattern defUpsPattern = u.getUpgradesPattern();

				if (!defUpsPattern.isDeterministic()) {
					log.warn("Inferring default values for Unit " + ui
							+ "'s upgrades from non-deterministic"
							+ " pattern: pattern=" + defUpsPattern);
				}

				defUpsPattern.startEvaluation();
				Map<String, Integer> values = defUpsPattern.toMap();
				defUpsPattern.stopEvaluation();

				log.debug("Setting Unit " + ui
						+ "'s upgrades pattern to default values: " + values);
				ui
						.setUpgradesPattern(ConjunctEntityPatternSet
								.mapToSet(values));
			}

			Pattern compPattern = ui.getCompositionPattern();

			if (compPattern.isDefault()) {
				Pattern defCompPattern = u.getCompositionPattern();

				if (!defCompPattern.isDeterministic()) {
					log.warn("Inferring default values for Unit " + ui
							+ "'s composition from non-deterministic"
							+ " pattern: pattern=" + defCompPattern);
				}

				defCompPattern.startEvaluation();
				Map<String, Integer> values = defCompPattern.toMap();
				defCompPattern.stopEvaluation();

				log.debug("Setting Unit " + ui + "'s composition pattern to "
						+ "default values: " + values);
				ui.setCompositionPattern(ConjunctEntityPatternSet
						.mapToSet(values));
			}

			Pattern slotPattern = ui.getSlotPattern();

			if (slotPattern.isDefault()) {
				// Pattern defSlotPattern = u.getSlotsPattern();
				//
				// if (!defSlotPattern.isDeterministic()) {
				// log.warn("Inferring default values for Unit " + ui
				// + "'s slots from non-deterministic"
				// + " pattern: pattern=" + defSlotPattern);
				// }
				//
				// defSlotPattern.startEvaluation();
				// Map<String, Integer> values = defSlotPattern.toMap();
				// defSlotPattern.stopEvaluation();
				//
				// log.debug("Setting Unit " + ui
				// + "'s slot pattern to default values: " + values);
				// ui.setSlotPattern(ConjunctEntityPatternSet.mapToSet(values));

				throw new EntityProcessingException(this, ui,
						"Unit's definition doesn't include slot!");
			}

			slotPattern = ui.getSlotPattern();
			slotPattern.startEvaluation();
			Map<String, Integer> slots = slotPattern.toMap();
			slotPattern.stopEvaluation();

			for (String sl : slots.keySet()) {
				int q = slots.get(sl);
				while ((q--) > 0) {
					ui.addSlot(new UnitSlot(sl));
				}
			}

			Pattern linkedUnitsPattern = ui.getLinkedUnitsPattern();

			if (linkedUnitsPattern.isDefault()) {
				Pattern defLinkUnitPattern = u.getLinkedUnitsPattern();

				if (!defLinkUnitPattern.isDeterministic()) {
					log.warn("Inferring default values for Unit " + ui
							+ "'s composition from non-deterministic"
							+ " pattern: pattern=" + defLinkUnitPattern);
				}

				defLinkUnitPattern.startEvaluation();
				PatternContentMap values = defLinkUnitPattern.toMap();
				defLinkUnitPattern.stopEvaluation();

				if (values.getContentSize() > 0) {
					throw new EntityProcessingException(this, ui, "Unit " + ui
							+ " doesn't include some mandatory linked units: "
							+ values);
				}

				// log.debug("Setting Unit " + ui +
				// "'s linked units pattern to "
				// + "default values: " + values);
				// ui.setCompositionPattern(ConjunctEntityPatternSet
				// .mapToSet(values));
			}

			for (UnitMemberInstance umi : ui.getMembers()) {
				UnitMember m = u.getMember(umi.getTypeName());

				Pattern equipPattern = umi.getEquipmentPattern();

				if (equipPattern.isDefault()) {
					Pattern defEquipPattern = m.getEquipmentPattern();

					if (!defEquipPattern.isDeterministic()) {
						log.warn("Inferring default values for UnitMember " + m
								+ "'s equipment from non-deterministic"
								+ " pattern: pattern=" + defEquipPattern);
					}

					defEquipPattern.startEvaluation();
					Map<String, Integer> values = defEquipPattern.toMap();
					defEquipPattern.stopEvaluation();

					log.debug("Setting UnitMember " + umi
							+ "'s equipment pattern to default values: "
							+ values);
					umi.setEquipmentPattern(ConjunctEntityPatternSet
							.mapToSet(values));
				}
			}

		}
	}

	@Override
	public String toString() {
		return "PROC" + "-" + "ARMYINST" + "-" + "SET.DEFAULT.VALUES";
	}
}
