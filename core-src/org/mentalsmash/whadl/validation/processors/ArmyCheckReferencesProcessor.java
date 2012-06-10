package org.mentalsmash.whadl.validation.processors;

import java.util.ArrayList;
import java.util.Set;

import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.model.Equipment;
import org.mentalsmash.whadl.model.EquipmentGroup;
import org.mentalsmash.whadl.model.SingleEquipment;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.expressions.BaseExpressionVisitor;
import org.mentalsmash.whadl.model.expressions.CollectionOperationExpression;
import org.mentalsmash.whadl.model.expressions.ConditionalExpression;
import org.mentalsmash.whadl.model.expressions.EntityReferenceExpression;
import org.mentalsmash.whadl.model.expressions.EqualityExpression;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.ExpressionVisitor;
import org.mentalsmash.whadl.model.expressions.ExpressionsCollection;
import org.mentalsmash.whadl.model.expressions.InstanceReferenceExpression;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.model.expressions.LogicalExpression;
import org.mentalsmash.whadl.model.expressions.MemberSelectorExpression;
import org.mentalsmash.whadl.model.expressions.NotExpression;
import org.mentalsmash.whadl.model.expressions.RelationalExpression;
import org.mentalsmash.whadl.model.expressions.BinaryExpression;
import org.mentalsmash.whadl.model.expressions.UnaryArithmeticExpression;
import org.mentalsmash.whadl.validation.EntityContext;
import org.mentalsmash.whadl.validation.UnknownReferenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.example.debug.expr.ExpressionParserConstants;

public class ArmyCheckReferencesProcessor extends BaseReferenceChecker
		implements EntityProcessor<Army> {

	private static final Logger log = LoggerFactory
			.getLogger(ArmyCheckReferencesProcessor.class);

	public ArmyCheckReferencesProcessor(EntityContext context) {
		super(context);
	}

	public boolean check(Army a) throws UnknownReferenceException {
		if (a == null) {
			error("null", "Unit Definition");
		}

		try {

			if (!context.contains(a)) {
				error(a.getName(), "Army " + a.getName() + " Definition");
			}

			ArmyCheckReferencesProcessor subcheck = new ArmyCheckReferencesProcessor(
					this.context.getSubContext(a));

			for (Unit u : a.getUnits()) {
				subcheck.check(u);
			}

			for (Equipment e : a.getEquipments()) {
				subcheck.check(e);
			}

		} catch (UnknownReferenceException ex) {
			error(ex.getReference(), "Army " + a.getName() + " Definition - "
					+ ex.getWhere());
		}

		return true;
	}

	public boolean check(Unit u) throws UnknownReferenceException {
		if (u == null) {
			error("null", "Unit Definition");
		}

		try {

			if (!context.contains(u)) {
				error(u.getReference(), "Unit " + u.getName() + " Definition");
			}

			// ArmyCheckReferencesProcessor subcheck = new
			// ArmyCheckReferencesProcessor(
			// this.context.getSubContext(u));

			this.check(u.getSlotsPattern(), u);

			this.check(u.getCompositionPattern(), u);

			this.check(u.getUpgradesPattern(), u);
			
			this.check(u.getLinkedUnitsPattern(), u);

			for (UnitMember um : u.getMembers()) {
				this.check(um);
			}

			this.check(u.getConditions(), u);

		} catch (UnknownReferenceException ex) {
			this.error(ex.getReference(), "Unit " + u.getName()
					+ " Definition - " + ex.getWhere());
		}

		return true;
	}

	public boolean check(UnitMember m) throws UnknownReferenceException {
		if (m == null) {
			error("null", "UnitMember Definition");
		}

		if (!this.context.contains(m)) {
			error(m.getReference(), "UnitMember " + m.getName() + " Definition");
		}

		try {

			this.check(m.getSuperPattern(), m);

			this.check(m.getEquipmentPattern(), m);
		} catch (UnknownReferenceException ex) {
			error(ex.getReference(), "UnitMember " + m.getName()
					+ " Definition - " + ex.getWhere());
		}

		return true;
	}

	public boolean check(Equipment e) throws UnknownReferenceException {
		if (e == null) {
			error("null", "Equipment Definition");
		}

		if (e instanceof EquipmentGroup) {
			return this.check((EquipmentGroup) e);
		} else {
			return this.check((SingleEquipment) e);
		}
	}

	public boolean check(EquipmentGroup e) throws UnknownReferenceException {
		if (e == null) {
			error("null", "EquipmentGroup Definition");
		}

		if (!this.context.contains(e)) {
			error(e.getReference(), "EquipmentGroup " + e.getName()
					+ " Definition");
		}

		ArmyCheckReferencesProcessor subcheck = new ArmyCheckReferencesProcessor(
				this.context.getSubContext(e));

		try {

			for (Equipment equipment : e) {
				subcheck.check(equipment);
			}

		} catch (UnknownReferenceException ex) {
			error(ex.getReference(), "EquipmentGroup " + e.getName()
					+ " Definition - " + ex.getWhere());
		}

		return true;
	}

	public boolean check(SingleEquipment e) throws UnknownReferenceException {
		if (e == null) {
			error("null", "SingleEquipment Definition");
		}

		if (!this.context.contains(e)) {
			error(e.getReference(), "SingleEquipment " + e.getName()
					+ " Definition");
		}

		if (!this.context.contains(e.getReference())) {

			error(e.getReference(), "SingleEquipment " + e.getName()
					+ " Definition");
		}

		return true;
	}

	public boolean check(Expression exp, Entity context)
			throws UnknownReferenceException {
		ReferencesCheckingVisitor visitor = new ReferencesCheckingVisitor(
				this.context.getSubContext(context));

		// log.info("CONDITIONS CHECK: "+exp+", "+context);
		exp.accept(visitor);

		if (visitor.getWrongReferences().size() > 0) {
			throw new UnknownReferenceException(visitor.getWrongReferences()
					.toString(), "Expression Checking: exp=" + exp
					+ ", context=" + context);
		}

		return true;
	}

	@Override
	public Army process(Army e) throws EntityProcessingException {
		try {
			this.check(e);
		} catch (UnknownReferenceException ex) {
			throw new EntityProcessingException(this, e, ex);
		}
		return e;
	}

	@Override
	public String toString() {
		return "PROC" + "-" + "ARMY" + "-" + "CHECK.REFERENCES";
	}

	private class ReferencesCheckingVisitor extends BaseExpressionVisitor {

		private final ArrayList<String> wrongRefs = new ArrayList<String>();
		private final ArrayList<String> correctRefs = new ArrayList<String>();
		private final EntityContext context;

		public ArrayList<String> getWrongReferences() {
			return wrongRefs;
		}

		public ArrayList<String> getCorrectReferences() {
			return correctRefs;
		}

		public ReferencesCheckingVisitor(EntityContext context) {
			super();
			this.context = context;
		}

		@Override
		public void visit(EntityReferenceExpression exp) {
			// log.info("Checking reference : " + exp);
			try {
				this.context.getEntity(exp.getReference());
				this.correctRefs.add(exp.getReference());
			} catch (Exception e) {
				this.wrongRefs.add(exp.getReference());
			}
		}

	}

}
