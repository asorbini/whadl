package org.mentalsmash.whadl.validation.processors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.model.Equipment;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.UnitSlot;
import org.mentalsmash.whadl.model.expressions.ArithmeticExpression;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.model.expressions.LogicalExpression;
import org.mentalsmash.whadl.model.expressions.Expression.Operator;
import org.mentalsmash.whadl.model.patterns.ConjunctEntityPatternSet;
import org.mentalsmash.whadl.model.patterns.EmptyPattern;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.validation.EntityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArmySetExtendsDataProcessor implements EntityProcessor<Army> {

	private static final Logger log = LoggerFactory
			.getLogger(ArmySetExtendsDataProcessor.class);

	private final EntityContext context;

	private final Collection<Entity> processed = new HashSet<Entity>();

	public ArmySetExtendsDataProcessor(EntityContext context) {
		super();
		this.context = context;
	}

	@Override
	public Army process(Army e) throws EntityProcessingException {
		this.setExtendsData(e);
		return e;
	}

	protected void setExtendsData(Army a) throws EntityProcessingException {
		Pattern armySupPattern = a.getSuperPattern();
		if (!(armySupPattern instanceof EmptyPattern)) {
			if (!armySupPattern.isDeterministic()) {
				throw new EntityProcessingException(this, a,
						"Super pattern is non-deterministic: pattern="
								+ armySupPattern);
			}

			armySupPattern.startEvaluation();
			Map<String, Integer> armySupers = armySupPattern.toMap();
			armySupPattern.stopEvaluation();

			for (String supArmyName : armySupers.keySet()) {
				Entity supArmyEnt = this.context.getEntity(supArmyName);
				if (!(supArmyEnt instanceof Army)) {
					throw new EntityProcessingException(this, a,
							"Super-entity is not an Army: super=" + supArmyName);
				}
				Army supArmy = (Army) supArmyEnt;
				this.merge(a, supArmy);
			}

		}

		for (Unit u : a.getUnits()) {
			processUnit(a, u);
		}
	}

	private void processUnit(Army a, Unit u) throws EntityProcessingException {
		Pattern unitSupPattern = u.getSuperPattern();
		if (!(unitSupPattern.isDefault())) {
			if (!unitSupPattern.isDeterministic()) {
				throw new EntityProcessingException(this, u,
						"Super pattern is non-deterministic: pattern="
								+ unitSupPattern);
			}

			unitSupPattern.startEvaluation();
			Map<String, Integer> unitSupers = unitSupPattern.toMap();
			unitSupPattern.stopEvaluation();

			for (String supUnitName : unitSupers.keySet()) {
				Entity supUnitEnt = this.context.getSubContext(a).getEntity(
						supUnitName);
				if (!(supUnitEnt instanceof Unit)) {
					throw new EntityProcessingException(this, u,
							"Super-entity is not an Unit: super=" + supUnitName);
				}
				Unit supUnit = (Unit) supUnitEnt;
				this.merge(u, supUnit);
			}

		}

		for (UnitMember m : u.getMembers()) {
			Pattern superPattern = m.getSuperPattern();
			if (superPattern != null && !(superPattern instanceof EmptyPattern)) {

				if (!(superPattern.isDeterministic())) {
					throw new EntityProcessingException(this, m,
							"Super pattern is non-deterministic: super="
									+ superPattern);
				}

				superPattern.startEvaluation();
				Map<String, Integer> supers = superPattern.toMap();
				superPattern.stopEvaluation();

				for (String supMemKey : supers.keySet()) {
					Entity supEnt = this.context.getSubContext(a)
							.getSubContext(u).getEntity(supMemKey);

					if (!(supEnt instanceof UnitMember)) {
						throw new EntityProcessingException(this, m,
								"Super-entity is not a UnitMember: found="
										+ supEnt);
					}

					UnitMember supMember = (UnitMember) supEnt;

					merge(m, supMember);
				}

			}

		}

		for (Unit modUnit : u.getModifiedUnits()) {
			this.processUnit(a, modUnit);
		}
	}

	private void merge(Army a, Army supArmy) throws EntityProcessingException {
		log.debug("Merging " + a + " with " + supArmy);
		Expression supConditions = supArmy.getConditions();

		if (!supConditions.equals(new LiteralExpression(true))) {
			Expression aCondtions = a.getConditions();
			LogicalExpression union = new LogicalExpression(supConditions,
					aCondtions, Operator.AND);
			a.setConditions(union);
		}

		if (supArmy.getName().equals("BaseArmy")) {
			return;
		}

		for (Unit u : supArmy.getUnits()) {
			Unit existing = a.getUnit(u.getName());

			if (existing != null) {
				this.merge(existing, u);
			} else {
				Unit newUnit = new Unit(u);

				String armyRef = supArmy.getReference();
				String unitRef = u.getReference();
				String unitLocalRef = unitRef.substring(armyRef.length());

				String newRef = a.getReference() + unitLocalRef;

				newUnit.setReference(newRef);

				a.addUnit(newUnit);
			}
		}

		for (Equipment e : supArmy.getEquipments()) {
			a.addEquipment(e);
		}

		Pattern supArmyExtendsPattern = supArmy.getSuperPattern();

		if (!supArmyExtendsPattern.isDeterministic()) {
			throw new EntityProcessingException(this, supArmy,
					"Super pattern is non-deterministic: pattern="
							+ supArmyExtendsPattern);
		}

		if (!this.processed.contains(supArmy)) {
			supArmyExtendsPattern.startEvaluation();
			Map<String, Integer> supArmySupers = supArmyExtendsPattern.toMap();
			supArmyExtendsPattern.stopEvaluation();

			for (String supArmySuperName : supArmySupers.keySet()) {
				Entity supArmyEnt = this.context.getEntity(supArmySuperName);
				if (!(supArmyEnt instanceof Army)) {
					throw new EntityProcessingException(this, supArmy,
							"Super-entity is not an Army: super="
									+ supArmySuperName);
				}
				Army supArmySuper = (Army) supArmyEnt;
				this.merge(a, supArmySuper);
			}
		}
		
		this.processed.add(a);
	}

	private void merge(Unit u, Unit supUnit) throws EntityProcessingException {
		log.debug("Merging " + u + " with " + supUnit);
		Expression supConditions = supUnit.getConditions();

		if (!supConditions.equals(LiteralExpression.BOOLEAN_TRUE)) {
			if (u.getConditions().equals(LiteralExpression.BOOLEAN_TRUE)) {
				u.setConditions(supConditions);
			} else {
				Expression uCondtions = u.getConditions();
				LogicalExpression union = new LogicalExpression(supConditions,
						uCondtions, Operator.AND);
				u.setConditions(union);
			}
		}

		if (supUnit.getName().equals("BaseUnit")) {
			return;
		}

		for (UnitMember m : supUnit.getMembers()) {

			UnitMember existing = u.getMember(m.getName());

			if (existing != null) {
				this.merge(existing, m);
			} else {
				UnitMember newMemb = new UnitMember(m);

				String unitRef = supUnit.getReference();
				String membRef = m.getReference();
				String membLocalRef = membRef.substring(unitRef.length());

				String newRef = u.getReference() + membLocalRef;

				// System.err.println("NEW REF: " + newRef);
				newMemb.setReference(newRef);

				u.addMember(newMemb);
			}
		}

		Pattern compositionPattern = supUnit.getCompositionPattern();
		if (!compositionPattern.isDefault()) {
			if (!u.getCompositionPattern().isDefault()) {
				ConjunctEntityPatternSet un = new ConjunctEntityPatternSet();
				un.add(compositionPattern);
				un.add(u.getCompositionPattern());
				u.setCompositionPattern(un);
			} else {
				u.setCompositionPattern(compositionPattern);
			}
		}

		Pattern slotsPattern = supUnit.getSlotsPattern();
		// System.err.println("UNIT "+supUnit+" slots: "+slotsPattern);
		if (!slotsPattern.isDefault()) {
			if (!u.getSlotsPattern().isDefault()) {

				ConjunctEntityPatternSet un = new ConjunctEntityPatternSet();
				un = new ConjunctEntityPatternSet();
				un.add(slotsPattern);
				un.add(u.getSlotsPattern());
				u.setSlotsPattern(un);

			} else {
				u.setSlotsPattern(slotsPattern);
			}

			slotsPattern.startEvaluation();
			Map<String, Integer> slots = slotsPattern.toMap();
			slotsPattern.stopEvaluation();

			for (String slot : slots.keySet()) {
				int q = slots.get(slot);
				while ((q--) > 0) {
					u.addSlot(new UnitSlot(slot));
				}
			}
		}

		Pattern upgradesPattern = supUnit.getUpgradesPattern();
		if (!upgradesPattern.isDefault()) {
			if (!u.getUpgradesPattern().isDefault()) {
				ConjunctEntityPatternSet un = new ConjunctEntityPatternSet();
				un = new ConjunctEntityPatternSet();
				un.add(upgradesPattern);
				un.add(u.getUpgradesPattern());
				u.setUpgradesPattern(un);
			} else {
				u.setUpgradesPattern(upgradesPattern);
			}
		}
		
		Pattern linkedUnitsPattern = supUnit.getLinkedUnitsPattern();
		if (!linkedUnitsPattern.isDefault()) {
			if (!u.getLinkedUnitsPattern().isDefault()) {
				ConjunctEntityPatternSet un = new ConjunctEntityPatternSet();
				un = new ConjunctEntityPatternSet();
				un.add(linkedUnitsPattern);
				un.add(u.getLinkedUnitsPattern());
				u.setLinkedUnitsPattern(un);
			} else {
				u.setLinkedUnitsPattern(linkedUnitsPattern);
			}
		}

		Expression supModArmyExp = supUnit.getArmyModifiedConditions();
		if (!supModArmyExp.equals(LiteralExpression.BOOLEAN_TRUE)) {
			if (!u.getArmyModifiedConditions().equals(
					LiteralExpression.BOOLEAN_TRUE)) {
				Expression modArmyExp = u.getArmyModifiedConditions();
				Expression exp = new LogicalExpression(modArmyExp,
						supModArmyExp, Operator.AND);
				u.setArmyModifiedConditions(exp);
			} else {
				u.setArmyModifiedConditions(supModArmyExp);
			}
		}

		Expression supUnitCost = supUnit.getCostExpression();
		Expression unitCost = u.getCostExpression();
		if (!supUnitCost.equals(LiteralExpression.INTEGER_0)) {
			if (!unitCost.equals(LiteralExpression.INTEGER_0)) {
				Expression sum = new ArithmeticExpression(unitCost,
						supUnitCost, Operator.PLUS);
				u.setCostExpression(sum);
			} else {
				u.setCostExpression(supUnitCost);
			}
		}

		if (!this.processed.contains(supUnit)) {

			Pattern supUnitExtendsPattern = supUnit.getSuperPattern();

			if (!supUnitExtendsPattern.isDeterministic()) {
				throw new EntityProcessingException(this, supUnit,
						"Super pattern is non-deterministic: pattern="
								+ supUnitExtendsPattern);
			}

			supUnitExtendsPattern.startEvaluation();
			Map<String, Integer> supUnitSupers = supUnitExtendsPattern.toMap();
			supUnitExtendsPattern.stopEvaluation();

			for (String supUnitSuperName : supUnitSupers.keySet()) {
				Entity supUnitEnt = this.context.getEntity(supUnitSuperName);
				if (!(supUnitEnt instanceof Unit)) {
					throw new EntityProcessingException(this, supUnit,
							"Super-entity is not an Unit: super="
									+ supUnitSuperName);
				}
				Unit supUnitSuper = (Unit) supUnitEnt;
				this.merge(u, supUnitSuper);
			}
		}

		this.processed.add(u);

	}

	private void merge(UnitMember m, UnitMember supMember)
			throws EntityProcessingException {
		log.debug("Merging " + m + " with " + supMember);
		Expression supConditions = supMember.getConditions();
		if (!supConditions.equals(LiteralExpression.BOOLEAN_TRUE)) {
			if (!m.getConditions().equals(LiteralExpression.BOOLEAN_TRUE)) {
				Expression mCondtions = m.getConditions();
				LogicalExpression union = new LogicalExpression(supConditions,
						mCondtions, Operator.AND);
				m.setConditions(union);
			} else {
				m.setConditions(supConditions);
			}
		}

		if (supMember.getName().equals("BaseUnitMember")) {
			return;
		}

		Pattern supEquipPattern = supMember.getEquipmentPattern();

		if (!supEquipPattern.isDefault()) {
			if (!m.getEquipmentPattern().isDefault()) {
				ConjunctEntityPatternSet un = new ConjunctEntityPatternSet();
				un.add(supEquipPattern);
				un.add(m.getEquipmentPattern());
				// un.pack();
				m.setEquipmentPattern(un);
			} else {
				m.setEquipmentPattern(supEquipPattern);
			}
		}

		Pattern supMemberExtendsPattern = supMember.getSuperPattern();

		if (!(supMemberExtendsPattern.isDeterministic())) {
			throw new EntityProcessingException(this, supMember,
					"Super pattern is non-deterministic: super="
							+ supMemberExtendsPattern);
		}

		if (!this.processed.contains(supMember)) {

			supMemberExtendsPattern.startEvaluation();
			Map<String, Integer> supMemberSupers = supMemberExtendsPattern
					.toMap();
			supMemberExtendsPattern.stopEvaluation();

			for (String supMemberSuperName : supMemberSupers.keySet()) {
				Entity supMemberEnt = this.context.getSubContext(supMember)
						.getEntity(supMemberSuperName);
				if (!(supMemberEnt instanceof UnitMember)) {
					throw new EntityProcessingException(this, supMember,
							"Super-entity is not an UnitMember: super="
									+ supMemberSuperName);
				}
				UnitMember supMemberSuper = (UnitMember) supMemberEnt;
				this.merge(m, supMemberSuper);
			}
		}

		this.processed.add(m);
	}

	@Override
	public String toString() {
		return "PROC" + "-" + "ARMY" + "-" + "SET.EXTENDS.DATA";
	}

}
