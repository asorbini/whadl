package org.mentalsmash.whadl.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mentalsmash.whadl.DataDumper;
import org.mentalsmash.whadl.WhadlException;
import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.Equipment;
import org.mentalsmash.whadl.model.EquipmentInstance;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.UnitMemberInstance;
import org.mentalsmash.whadl.model.UnitSlot;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.model.expressions.LogicalExpression;
import org.mentalsmash.whadl.model.expressions.Expression.Operator;
import org.mentalsmash.whadl.model.patterns.ConjunctEntityPatternSet;
import org.mentalsmash.whadl.model.patterns.NoMatchException;
import org.mentalsmash.whadl.model.patterns.PartialMatchException;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.model.patterns.PatternMatcher;
import org.mentalsmash.whadl.parser.ParserUtils;
import org.mentalsmash.whadl.validation.processors.ArmyAddToContextProcessor;
import org.mentalsmash.whadl.validation.processors.ArmyCheckMissingValuesProcessor;
import org.mentalsmash.whadl.validation.processors.ArmyCheckReferencesProcessor;
import org.mentalsmash.whadl.validation.processors.ArmyCreateImplicitEntitiesProcessor;
import org.mentalsmash.whadl.validation.processors.ArmyInstanceAddToContextProcessor;
import org.mentalsmash.whadl.validation.processors.ArmyInstanceCheckReferencesProcessor;
import org.mentalsmash.whadl.validation.processors.ArmyInstanceCreateImplicitEntitiesProcessor;
import org.mentalsmash.whadl.validation.processors.ArmyInstanceSetDefaultValuesProcessor;
import org.mentalsmash.whadl.validation.processors.ArmyInstanceSetEntitiesProcessor;
import org.mentalsmash.whadl.validation.processors.ArmyInstanceSetReferencedInstancesProcessor;
import org.mentalsmash.whadl.validation.processors.ArmySetDefaultValuesProcessor;
import org.mentalsmash.whadl.validation.processors.ArmySetExtendsDataProcessor;
import org.mentalsmash.whadl.validation.processors.EntityProcessingException;
import org.mentalsmash.whadl.validation.processors.EntityProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhadlValidator {
	private static final Logger log = LoggerFactory
			.getLogger(WhadlValidator.class);

	private UnitInstance currentUnitInstance;
	private ArmyInstance currentArmyInstance;
	private final DynamicEntityContext defaultContext;

	private final int matcherMaxTries;

	public WhadlValidator(DynamicEntityContext defaultContext,
			int matcherMaxTries) {
		this.defaultContext = defaultContext;
		this.matcherMaxTries = matcherMaxTries;
	}

	protected Army getVerificationModel(Army a, ArmyInstance inst) {
		log.debug("Computing ArmyInstance verification model...");
		Army result = new Army(a);

		Map<String, Unit> units = new HashMap<String, Unit>();
		for (Unit u : a.getUnits()) {
			units.put(u.getName(), u);
		}

		for (UnitInstance ui : inst.getUnits()) {
			Unit u = units.get(ui.getTypeName());
			if (u.getModifiedUnits().size() > 0) {
				this.applyModifications(result, u.getModifiedUnits());
			}

			if (!u.getArmyModifiedConditions().equals(
					new LiteralExpression(true))) {
				result.setConditions(new LogicalExpression(result
						.getConditions(), u.getArmyModifiedConditions(),
						Operator.AND));
			}
		}

		log.debug("Done.");
		return result;
	}

	private void applyModifications(Army a, Collection<Unit> modifications) {
		log.info("Modified units: " + modifications);

		Map<String, Unit> units = new HashMap<String, Unit>();
		for (Unit u : a.getUnits()) {
			units.put(u.getName(), u);
		}

		for (Unit mUn : modifications) {
			Unit u = units.get(mUn.getName());

			log.debug("PRE-MODIFY");
			if (log.isDebugEnabled())
				new DataDumper().dumpUnit(u, "");

			if (u == null) {
				a.addUnit(mUn);
				continue;
			}

			ConjunctEntityPatternSet union = new ConjunctEntityPatternSet();
			union.add(u.getSlotsPattern());
			union.add(mUn.getSlotsPattern());
			u.setSlotsPattern(union);

			mUn.getSlotsPattern().startEvaluation();
			Map<String, Integer> slots = mUn.getSlotsPattern().toMap();
			mUn.getSlotsPattern().stopEvaluation();

			for (String slot : slots.keySet()) {
				int q = slots.get(slot);
				while ((q--) > 0) {
					u.addSlot(new UnitSlot(slot));
				}
			}

			union = new ConjunctEntityPatternSet();
			union.add(u.getUpgradesPattern());
			union.add(mUn.getUpgradesPattern());
			u.setUpgradesPattern(union);

			union = new ConjunctEntityPatternSet();
			union.add(u.getCompositionPattern());
			union.add(mUn.getCompositionPattern());
			u.setCompositionPattern(union);

			union = new ConjunctEntityPatternSet();
			union.add(u.getSuperPattern());
			union.add(mUn.getSuperPattern());
			u.setSuperPattern(union);

			for (UnitMember modMember : mUn.getMembers()) {
				UnitMember m = u.getMember(modMember.getName());
				if (m != null) {
					union = new ConjunctEntityPatternSet();
					union.add(m.getSuperPattern());
					union.add(modMember.getSuperPattern());
					m.setSuperPattern(union);

					union = new ConjunctEntityPatternSet();
					union.add(m.getEquipmentPattern());
					union.add(modMember.getEquipmentPattern());
					m.setEquipmentPattern(union);
				} else {
					u.addMember(modMember);
				}
			}

			if (!u.getCostExpression().equals(mUn.getCostExpression())) {
				log.debug("Cost expressions will not be modified for unit " + u
						+ ": current=" + u.getCostExpression() + ", found="
						+ mUn.getCostExpression());
			}

			Expression uConds = u.getConditions();
			Expression muConds = mUn.getConditions();

			if (!muConds.equals(new LiteralExpression(true))) {

				LogicalExpression exp = new LogicalExpression(uConds, muConds,
						Operator.AND);
				u.setConditions(exp);

			}

			log.debug("POST-MODIFY");
			if (log.isDebugEnabled())
				new DataDumper().dumpUnit(u, "");

		}
	}

	public int validate(ArmyInstance instance)
			throws InstanceVerificationException {

		try {

			Army army = (Army) this.defaultContext.getEntity(instance
					.getTypeName());

			army = this.getVerificationModel(army, instance);

			ArmyInstance old = this.currentArmyInstance;
			this.currentArmyInstance = instance;

			try {
				log.info("VERIFYING ARMY BUILD: " + instance);

				int armyCost = 0;

				for (UnitInstance ui : instance.getUnits()) {
					try {

						Unit u = army.getUnit(ui.getTypeName());
						if (u == null) {
							throw new InstanceVerificationException(
									"UnitInstanceVerification",
									"Unknown unit type found",
									ui.getTypeName(), "ArmyInstance= "
											+ instance + ", UnitInstance= "
											+ ui);
						}

						armyCost += this.validate(u, ui);

					} catch (UnknownEntityException ex) {
						throw new InstanceVerificationException(
								"UnitInstanceVerification",
								"Found reference to unknown entity", ex
										.getReference(), "ArmyInstance="
										+ instance + ", UnitInstance=" + ui);
					} catch (InstanceVerificationException ex) {
						throw ex;
					} catch (Exception ex) {
						throw new InstanceVerificationException(
								"UnitInstanceVerification",
								"An unexpected exception was thrown", ex
										.toString(), "ArmyInstance=" + instance
										+ ", UnitInstance=" + ui);
					}
				}

				instance.setFinalCost(armyCost);

				log.info("ARMY BUILD TOTAL POINTS: " + armyCost);

				Expression armyConditions = army.getConditions();

				Boolean conditionsRes = (Boolean) armyConditions
						.evaluate(defaultContext.getSubContext(instance));

				if (!conditionsRes) {
					throw new InstanceVerificationException(
							"ArmyInstanceVerification",
							"ArmyInstance doesn't satisfy Army's conditions",
							armyConditions.toString(), "ArmyInstance= "
									+ instance);
				}

				log.info("ARMY BUILD " + instance + " VERIFIED.");

				return armyCost;
			} finally {
				this.currentArmyInstance = old;
			}

		} catch (UnknownEntityException ex) {
			throw new InstanceVerificationException("ArmyInstanceVerification",
					"Found reference to unknown entity", ex.getReference(),
					"ArmyInstance=" + instance, ex);
		} catch (InstanceVerificationException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InstanceVerificationException("ArmyInstanceVerification",
					"An unexpected exception was thrown", ex.toString(),
					"ArmyInstance=" + instance, ex);
		}
	}

	private int validate(Unit unit, UnitInstance instance)
			throws InstanceVerificationException {

		ArmyInstance ai = this.currentArmyInstance;

		UnitInstance oldValue = this.currentUnitInstance;
		this.currentUnitInstance = instance;

		log.info("VERIFYING UNIT: " + instance);

		boolean verified = false;

		try {

			int unitCost = (Integer) unit.getCostExpression().evaluate(
					this.defaultContext);

			log.debug("BASE COST: " + unitCost);

			Pattern unitSlotsPattern = unit.getSlotsPattern();

			Pattern instSlotPattern = instance.getSlotPattern();
			instSlotPattern.startEvaluation();
			Map<String, Integer> slots = instSlotPattern.toMap();
			instSlotPattern.stopEvaluation();

			log.debug("Checking Unit's slots: " + slots + " with "
					+ unitSlotsPattern);

			try {

				PatternMatcher matcher = new PatternMatcher(
						this.defaultContext, matcherMaxTries);

				matcher.match(unitSlotsPattern, slots);

				// if (unitSlotsPattern.getQuantity() != Integer.MAX_VALUE
				// && result != null && result.toMap().size() > 0) {
				// log.warn("Unit's definition didn't include "
				// + "some required slot: missing=" + result.toMap()
				// + ", found=" + slots + ", original="
				// + unitSlotsPattern.toMap());
				// }

				log.debug("Ok");

				unitCost += matcher.getFinalCost();

			} catch (NoMatchException ex) {
				throw new InstanceVerificationException(
						"UnitInstanceVerification", "Wrong Unit's slots",
						"Possible= " + unitSlotsPattern + ", Chosen= " + slots,
						"ArmyInstance= " + ai + ", UnitInstance= " + instance);
			} catch (PartialMatchException ex) {
				throw new InstanceVerificationException(
						"UnitInstanceVerification",
						"Partially wrong Unit's slots", "Possible= "
								+ unitSlotsPattern + ", Chosen= " + slots
								+ ", Unmatched= " + ex.getUnmatched(),
						"ArmyInstance= " + ai + ", UnitInstance= " + instance);
			}

			Collection<UnitSlot> availSlots = new ArrayList<UnitSlot>(unit
					.getSlots());

			for (UnitSlot slot : instance.getSlots()) {
				if (!availSlots.remove(slot)) {
					throw new InstanceVerificationException(
							"UnitInstanceVerification", "Wrong Unit's slots",
							"Possible= " + availSlots + ", Chosen= "
									+ instance.getSlots(), "ArmyInstance= "
									+ ai + ", UnitInstance= " + instance);
				}
			}

			for (UnitSlot slot : instance.getSlots()) {
				unit.removeSlot(slot);
			}

			String expr = "(this->units select ((this->linked select (this == $"
					+ instance.getName() + "))->size > 0))";


			Expression exp = ParserUtils.parseExpression(expr);
			ArmyInstanceSetReferencedInstancesProcessor processor = new ArmyInstanceSetReferencedInstancesProcessor(
					this.defaultContext);
			processor.setReferencedInstances(exp, ai);
			
			Collection<?> referringUnits = (Collection<?>) exp
					.evaluate(this.defaultContext.getSubContext(ai));

			for (Object o : referringUnits) {
				log.debug("UNIT " + instance + " REFERRED BY " + o);
			}

			if (referringUnits.size() > 1) {
				throw new InstanceVerificationException(
						"UnitInstanceVerification",
						"Unit's is referenced in the composition entry"
								+ " of more than one other unit",
						referringUnits.toString(), "ArmyInstance= " + ai
								+ ", UnitInstance= " + instance);
			} else if (instance.getSlots().contains(new UnitSlot("TRANSPORT"))
					&& referringUnits.size() != 1) {
				throw new InstanceVerificationException(
						"UnitInstanceVerification",
						"Unit's was selected as transport but no other unit references it.",
						instance.getName(), "ArmyInstance= " + ai
								+ ", UnitInstance= " + instance);
			}

			Pattern unitCompositionPattern = unit.getCompositionPattern();

			Pattern instanceCompPattern = instance.getCompositionPattern();

			Map<String, Integer> composition = null;

			instanceCompPattern.startEvaluation();
			composition = instanceCompPattern.toMap();
			instanceCompPattern.stopEvaluation();

			log.debug("Checking Unit's composition: " + composition);

			try {
				PatternMatcher matcher = new PatternMatcher(
						this.defaultContext, matcherMaxTries);

				Pattern result = matcher.match(unitCompositionPattern,
						composition);

				if (result != null && result.toMap().size() > 0) {
					log.warn("Unit's definition didn't include "
							+ "some of the default composition: missing="
							+ result.toMap() + ", composition=" + composition);

					throw new InstanceVerificationException(
							"Unit's definition didn't include "
									+ "some of the default composition: missing="
									+ result.toMap() + ", composition="
									+ composition);
				}

				log.debug("Ok. Cost:" + matcher.getFinalCost());

				unitCost += matcher.getFinalCost();

			} catch (NoMatchException ex) {
				throw new InstanceVerificationException(
						"UnitInstanceVerification", "Wrong Unit's composition",
						"Possible= " + unitCompositionPattern + ", Chosen= "
								+ composition, "ArmyInstance= " + ai
								+ ", UnitInstance= " + instance);
			} catch (PartialMatchException ex) {
				throw new InstanceVerificationException(
						"UnitInstanceVerification",
						"Partially wrong Unit's composition", "Possible= "
								+ unitCompositionPattern + ", Chosen= "
								+ composition + ", Unmatched= "
								+ ex.getUnmatched(), "ArmyInstance= " + ai
								+ ", UnitInstance= " + instance);
			}

			int totMembersFromComposition = 0;
			for (String key : composition.keySet()) {
				totMembersFromComposition += composition.get(key);
			}

			int membsDescriptions = instance.getMembers().size();

			if (totMembersFromComposition != membsDescriptions) {
				throw new InstanceVerificationException(
						"UnitInstanceVerification",
						"Unit's size doesn't match members descriptions",
						"size=" + totMembersFromComposition
								+ " members-descriptions=" + membsDescriptions,
						"ArmyInstance=" + this.currentArmyInstance
								+ ", UnitInstance=" + this.currentUnitInstance);

			} else {
				log.info("Unit's size: " + composition);
			}

			Pattern linkedUnitsPattern = unit.getLinkedUnitsPattern();

			Pattern instanceLinkUnitPattern = instance.getLinkedUnitsPattern();

			Map<String, Integer> linkedUnitsNames = null;

			instanceLinkUnitPattern.startEvaluation();
			linkedUnitsNames = instanceLinkUnitPattern.toMap();
			instanceLinkUnitPattern.stopEvaluation();

			log.debug("Checking Unit's linked units: " + linkedUnitsNames);

			try {
				PatternMatcher matcher = new PatternMatcher(
						this.defaultContext, matcherMaxTries);

				Pattern result = matcher.match(linkedUnitsPattern,
						linkedUnitsNames);

				if (result != null && result.toMap().size() > 0) {
					log.warn("Unit's definition didn't include "
							+ "some of the default linked units: missing="
							+ result.toMap() + ", linked=" + linkedUnitsNames);

					throw new InstanceVerificationException(
							"Unit's definition didn't include "
									+ "some of the default linked units : missing="
									+ result.toMap() + ", linked="
									+ linkedUnitsNames);
				}

				log.debug("Ok. Cost:" + matcher.getFinalCost());

				// log.info("PARTIAL COST: "+unitCost+" pattern: "+linkedUnitsPattern);
				unitCost += matcher.getFinalCost();
				// log.info("PARTIAL COST: "+unitCost);
			} catch (NoMatchException ex) {
				throw new InstanceVerificationException(
						"UnitInstanceVerification",
						"Wrong Unit's linked units", "Possible= "
								+ linkedUnitsPattern + ", Chosen= "
								+ linkedUnitsNames, "ArmyInstance= " + ai
								+ ", UnitInstance= " + instance);
			} catch (PartialMatchException ex) {
				throw new InstanceVerificationException(
						"UnitInstanceVerification",
						"Partially wrong Unit's linked units", "Possible= "
								+ linkedUnitsPattern + ", Chosen= "
								+ linkedUnitsNames + ", Unmatched= "
								+ ex.getUnmatched(), "ArmyInstance= " + ai
								+ ", UnitInstance= " + instance);
			}

			for (UnitMemberInstance m : instance.getMembers()) {
				try {
					UnitMember mem = unit.getMember(m.getTypeName());

					if (mem == null) {
						throw new InstanceVerificationException(
								"UnitMemberInstanceVerification",
								"Unknown unit member type found", m
										.getTypeName(), "ArmyInstance= " + ai
										+ ", UnitInstance= "
										+ this.currentUnitInstance
										+ ", UnitMemberInstance= " + m);
					}

					unitCost += this.verify(mem, m);
				} catch (UnknownEntityException ex) {
					throw new InstanceVerificationException(
							"UnitMemberInstanceVerification",
							"Found reference to unknown entity", ex
									.getReference(), "ArmyInstance="
									+ this.currentArmyInstance
									+ ", UnitInstance="
									+ this.currentUnitInstance
									+ ", UnitMemberInstance=" + m);
				} catch (InstanceVerificationException ex) {
					throw ex;
				} catch (Exception ex) {
					throw new InstanceVerificationException(
							"UnitMemberInstanceVerification",
							"An unexpected exception was thrown",
							ex.toString(), "ArmyInstance="
									+ this.currentArmyInstance
									+ ", UnitInstance="
									+ this.currentUnitInstance
									+ ", UnitMemberInstance=" + m);
				}
			}

			Pattern unitUpgradesPattern = unit.getUpgradesPattern();

			instance.getUpgradesPattern().startEvaluation();
			Map<String, Integer> chosenUpgrades = instance.getUpgradesPattern()
					.toMap();
			instance.getUpgradesPattern().stopEvaluation();

			log.debug("Checking Unit's upgrades : " + chosenUpgrades);

			try {
				PatternMatcher matcher = new PatternMatcher(
						this.defaultContext, matcherMaxTries);

				Pattern result = matcher.match(unitUpgradesPattern,
						chosenUpgrades);

				if (result != null && result.toMap().size() > 0) {
					log.warn("Unit's definition didn't include "
							+ "some of the default upgrades: missing="
							+ result.toMap());
				}

				log.debug("Ok. Cost: " + matcher.getFinalCost());

				unitCost += matcher.getFinalCost();

			} catch (NoMatchException ex) {
				throw new InstanceVerificationException(
						"UnitInstanceVerification", "Wrong Unit's upgrades",
						"Possible= " + unitUpgradesPattern + ", Chosen= "
								+ chosenUpgrades, "ArmyInstance= " + ai
								+ ", UnitInstance= " + instance);
			} catch (PartialMatchException ex) {
				throw new InstanceVerificationException(
						"UnitInstanceVerification",
						"Partially wrong unit's upgrades", "Possible= "
								+ unitUpgradesPattern + ", Chosen= "
								+ chosenUpgrades + ", Unmatched= "
								+ ex.getUnmatched(), "ArmyInstance= " + ai
								+ ", UnitInstance= " + instance);
			}

			instance.setFinalCost(unitCost);

			Expression unitConditions = unit.getConditions();

			EntityContext unitInstanceContext = this.defaultContext
					.getSubContext(instance);

			Boolean res = (Boolean) unitConditions
					.evaluate(unitInstanceContext);

			if (!res) {
				throw new InstanceVerificationException(
						"UnitInstanceVerification",
						"UnitInstance doesn't satisfy Unit's conditions",
						unitConditions.toString(), "ArmyInstance= " + ai
								+ ", UnitInstance= " + instance);
			}

			log.info("UNIT " + instance + " VERIFIED.");
			log.info("TOTAL COST: " + unitCost);

			verified = true;

			return unitCost;

		} finally {
			if (!verified) {
				log.error("ERRORS IN UNIT " + instance + "!");
			}

			this.currentUnitInstance = oldValue;
		}
	}

	private int verify(UnitMember member, UnitMemberInstance instance)
			throws InstanceVerificationException {
		log.debug("Verifying UnitMember: " + instance);

		ArmyInstance ai = this.currentArmyInstance;

		Pattern equipmentPattern = member.getEquipmentPattern();
		int memberCost = 0;

		instance.getEquipmentPattern().startEvaluation();
		Map<String, Integer> equipment = instance.getEquipmentPattern().toMap();
		instance.getEquipmentPattern().startEvaluation();

		log.debug("Checking member's equipment: " + equipment);

		try {
			PatternMatcher matcher = new PatternMatcher(this.defaultContext,
					matcherMaxTries);

			Pattern result = matcher.match(equipmentPattern, equipment);

			for (EquipmentInstance eq : instance.getEquipments()) {
				String name = eq.getName();
				Pattern p = equipmentPattern.extract(name);
				if (p == null) {
					throw new WhadlRuntimeException(
							"Found null when extracting equipment " + name
									+ " from " + equipmentPattern);
				}

				Expression costExp = p.getCostExpression();
				Object costObj = costExp.evaluate(this.defaultContext
						.getSubContext(instance));
				if (costObj instanceof Integer) {
					eq.setCost((Integer) costObj);
				} else {
					throw new WhadlRuntimeException(
							"Result of cost expression evaluation "
									+ "wasn't a number! found=" + costObj);
				}
			}

			if (result != null && result.toMap().size() > 0) {
				log.warn("UnitMember's definition didn't include "
						+ "some of the default equipment: missing="
						+ result.toMap());

				log.debug("RESULT: " + result);

				Map<String, Integer> missingEquipNames = result.toMap();
				for (String key : missingEquipNames.keySet()) {
					Equipment e = (Equipment) this.defaultContext
							.getSubContext(member).getEntity(key);
					Integer q = missingEquipNames.get(key);
					for (int i = 0; i < q; i++) {
						log.debug("Adding missing equipment to member: member="
								+ instance + ", equipment=" + e);
						EquipmentInstance eq = new EquipmentInstance(e, 0);
						Pattern p = result.extract(key);
						Object costObj = p.getCostExpression().evaluate(
								this.defaultContext.getSubContext(instance));
						if (costObj instanceof Integer) {
							eq.setCost((Integer) costObj);
							memberCost += (Integer) costObj;
						} else {
							throw new WhadlRuntimeException(
									"Result of cost expression evaluation "
											+ "wasn't a number! found="
											+ costObj);
						}
						instance.addEquipment(eq);
					}
				}
			}

			memberCost += matcher.getFinalCost();

			log.debug("Ok. Cost: " + memberCost);

		} catch (NoMatchException e) {
			throw new InstanceVerificationException(
					"UnitMemberInstanceVerification",
					"Wrong UnitMember's equipment", "Possible= "
							+ equipmentPattern + ", Chosen= " + equipment,
					"ArmyInstance= " + ai + ", UnitInstance= "
							+ this.currentUnitInstance
							+ ", UnitMemberInstance= " + instance);
		} catch (PartialMatchException e) {
			throw new InstanceVerificationException(
					"UnitMemberInstanceVerification",
					"Partially wrong UnitMember's equipment", "Possible= "
							+ equipmentPattern + ", Chosen= " + equipment
							+ ", Unmatched= " + e.getUnmatched(),
					"ArmyInstance= " + ai + ", UnitInstance= "
							+ this.currentUnitInstance
							+ ", UnitMemberInstance:" + instance);
		}

		instance.setFinalCost(memberCost);

		Expression memberConditions = member.getConditions();

		EntityContext context = this.defaultContext.getSubContext(instance);

		Boolean result = (Boolean) memberConditions.evaluate(context);

		if (!result) {
			throw new InstanceVerificationException(
					"UnitMemberInstanceVerification",
					"UnitMemberInstance doesn't satisfy UnitMember's conditions",
					memberConditions.toString(), "ArmyInstance= " + ai
							+ ", UnitInstance= " + this.currentUnitInstance
							+ ", UnitMemberInstance= " + instance);
		}

		// log.debug("UNIT'S MEMBER VERIFIED.");
		// log.debug("COST: "+ memberCost);

		return memberCost;

	}

	public void preprocess(Army a) throws EntityProcessingException,
			WhadlException {
		try {
			ArmySetDefaultValuesProcessor p1 = new ArmySetDefaultValuesProcessor();
			ArmyAddToContextProcessor p2 = new ArmyAddToContextProcessor(
					this.defaultContext, false);
			ArmySetExtendsDataProcessor p6 = new ArmySetExtendsDataProcessor(
					this.defaultContext);
			ArmyAddToContextProcessor p7 = new ArmyAddToContextProcessor(
					this.defaultContext, true);
			ArmyCreateImplicitEntitiesProcessor p3 = new ArmyCreateImplicitEntitiesProcessor(
					this.defaultContext);
			ArmyAddToContextProcessor p4 = new ArmyAddToContextProcessor(
					this.defaultContext, true);
			ArmyCheckReferencesProcessor p5 = new ArmyCheckReferencesProcessor(
					this.defaultContext);
			ArmyCheckMissingValuesProcessor p8 = new ArmyCheckMissingValuesProcessor();

			ArrayList<EntityProcessor<Army>> processors = new ArrayList<EntityProcessor<Army>>();
			processors.add(p1);
			processors.add(p2);
			processors.add(p6);
			processors.add(p7);
			processors.add(p3);
			processors.add(p4);
			processors.add(p5);
			processors.add(p8);

			WhadlPreprocessor preprocessor = new WhadlPreprocessor();
			for (EntityProcessor<Army> ep : processors) {
				preprocessor.addArmyProcessor(ep);
			}

			preprocessor.preprocess(a);
		} catch (UnknownEntityException ex) {
			throw new WhadlException("Reference to unknown entity found "
					+ "while preprocessing Army " + a.getName() + ": "
					+ ex.getMessage());
		} catch (Exception ex) {
			throw new WhadlException("An unexpected exception was raised "
					+ "while preprocessing Army " + a.getName() + ": "
					+ ex.toString());
		}
	}

	public void preprocess(ArmyInstance ai) throws EntityProcessingException,
			WhadlException {
		try {
			Army a = (Army) this.defaultContext.getEntity(ai.getTypeName());

			ArmyInstanceAddToContextProcessor p1 = new ArmyInstanceAddToContextProcessor(
					this.defaultContext.getSubContext(a, true), false);
			ArmyInstanceCheckReferencesProcessor p2 = new ArmyInstanceCheckReferencesProcessor(
					this.defaultContext.getSubContext(a, true));
			ArmyInstanceSetDefaultValuesProcessor p3 = new ArmyInstanceSetDefaultValuesProcessor(
					this.defaultContext.getSubContext(a, true));
			ArmyInstanceCreateImplicitEntitiesProcessor p4 = new ArmyInstanceCreateImplicitEntitiesProcessor(
					this.defaultContext.getSubContext(a, true));
			ArmyInstanceAddToContextProcessor p5 = new ArmyInstanceAddToContextProcessor(
					this.defaultContext.getSubContext(a, true), true);
			ArmyInstanceSetEntitiesProcessor p6 = new ArmyInstanceSetEntitiesProcessor(
					this.defaultContext.getSubContext(a, true));
			ArmyInstanceSetReferencedInstancesProcessor p7 = new ArmyInstanceSetReferencedInstancesProcessor(
					this.defaultContext.getSubContext(a, true));

			ArrayList<EntityProcessor<ArmyInstance>> processors = new ArrayList<EntityProcessor<ArmyInstance>>();
			processors.add(p1); // add to context
			processors.add(p2); // check references
			processors.add(p3); // set default values
			processors.add(p4); // create implicit entities
			processors.add(p5); // add to context
			processors.add(p6); // set entities (upgrades, equipments...)
			processors.add(p7); // set referenced instances

			WhadlPreprocessor preprocessor = new WhadlPreprocessor();
			for (EntityProcessor<ArmyInstance> ep : processors) {
				preprocessor.addArmyInstanceProcessor(ep);
			}

			preprocessor.preprocess(ai);
		} catch (UnknownEntityException ex) {
			throw new InstanceVerificationException("ArmyInstanceVerification",
					"Reference to unknown entity found", ex.getReference(),
					"ArmyInstance=" + ai, ex);
		} catch (EntityProcessingException ex) {
			throw new InstanceVerificationException("ArmyInstanceVerification",
					"Processor " + ex.getProcessor() + " returned an error", ex
							.toString(), "ArmyInstance=" + ai, ex);
		} catch (Exception ex) {
			throw new InstanceVerificationException("ArmyInstanceVerification",
					"An unexpected exception was raised "
							+ "while preprocessing ArmyInstance", ex
							.getMessage(), "ArmyInstance=" + ai, ex);
		}
	}

}
