package org.mentalsmash.whadl.validation.processors;

import java.util.ArrayList;
import java.util.Map;

import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.UnitMemberInstance;
import org.mentalsmash.whadl.model.patterns.ConjunctEntityPatternSet;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.validation.EntityContext;
import org.mentalsmash.whadl.validation.UnknownEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArmyInstanceCreateImplicitEntitiesProcessor implements
		EntityProcessor<ArmyInstance> {

	private static final Logger log = LoggerFactory
			.getLogger(ArmyInstanceCreateImplicitEntitiesProcessor.class);

	private final EntityContext context;

	public ArmyInstanceCreateImplicitEntitiesProcessor(EntityContext context) {
		this.context = context;
	}

	@Override
	public ArmyInstance process(ArmyInstance e)
			throws EntityProcessingException {
		this.createImplicitEntities(e);
		return e;
	}

	protected void createImplicitEntities(ArmyInstance ai)
			throws EntityProcessingException {
		for (UnitInstance ui : ai.getUnits()) {
			this.createImplicitEntities(ai, ui);
		}

	}

	private final ArrayList<UnitInstance> temp = new ArrayList<UnitInstance>();

	protected void createImplicitEntities(ArmyInstance ai, UnitInstance ui)
			throws EntityProcessingException {
		Army a = (Army) this.context.getEntity(ai.getTypeName());
		Unit u = (Unit) this.context.getSubContext(a).getEntity(
				ui.getTypeName());

		Pattern compositionPattern = ui.getCompositionPattern();
		Map<String, Integer> members = compositionPattern.toMap();

		for (String membTypeName : members.keySet()) {
			int missing = members.get(membTypeName);

			for (UnitMemberInstance membInst : ui.getMembers()) {
				if (membInst.getTypeName().equals(membTypeName)) {
					missing--;
				}
			}

			if (missing < 0) {
				throw new EntityProcessingException(this, ui,
						"Definition includes " + (-missing) + " "
								+ membTypeName
								+ " members more than the ones specified"
								+ "in its composition entry: composition="
								+ compositionPattern);
			}

//			try {
				Entity e = this.context.getSubContext(u)
						.getEntity(membTypeName);
				if (e instanceof UnitMember) {
					UnitMember membType = (UnitMember) e;
					while ((missing--) > 0) {
						UnitMemberInstance membInst = new UnitMemberInstance(
								membTypeName);
						membType.getEquipmentPattern().startEvaluation();
						membInst.setEquipmentPattern(ConjunctEntityPatternSet
								.mapToSet(membType.getEquipmentPattern()
										.toMap()));
						membType.getEquipmentPattern().stopEvaluation();

						ui.addMember(membInst);

						log.info("Created missing member: member=" + membInst
								+ ", unit=" + ui);
					}
				} else {
					throw new EntityProcessingException(this, ui,
							"Unit's composition includes a non-member type: type="
									+ membTypeName + " (" + e.getClass() + ")");
				}

//			} catch (UnknownEntityException ex) {
//				log.info("Cannot find UnitMember " + membTypeName
//						+ ", looking for an instance from other units.");
//				Entity e = this.context.getSubContext(ai).getEntity(
//						membTypeName);
//
//				if (e instanceof UnitMemberInstance) {
//					log.debug("Member is a UnitMember's instance,"
//							+ " adding it to unit: member=" + e + ", unit="
//							+ ui);
//
//					ui.addMember((UnitMemberInstance) e);
//
//				} else if (e instanceof UnitInstance) {
//
//					if (temp.contains(e)) {
//						log.error("Circular reference detected. Unit " + ui
//								+ " references " + e
//								+ ", but this didn't include "
//								+ "complete informations..."
//								+ "This way, we'll never get a result!");
//						throw new EntityProcessingException(this, e,
//								"Circular reference detected. Unit " + ui
//										+ " references " + e
//										+ ", but this didn't include "
//										+ "complete informations..."
//										+ "This way, we'll never get a result!");
//					} else {
//						temp.add((UnitInstance) e);
//						this.createImplicitEntities(ai, (UnitInstance) e);
//						temp.remove(e);
//					}
//
//					log.info("Member is a Unit's instance,"
//							+ " adding its members to unit's"
//							+ " external members: found=" + e + ", members="
//							+ ((UnitInstance) e).getMembers() + ", unit=" + ui);
//
//					for (UnitMemberInstance umi : ((UnitInstance) e)
//							.getMembers()) {
//						ui.addExternalMember(ui, umi);
//					}
//				}
//
//			}

		}
	}
	
	@Override
	public String toString(){
		return "PROC"+"-"+"ARMYINST"+"-"+"CREATE.IMPLICIT.ENTITIES";
	}
}
