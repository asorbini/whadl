package org.mentalsmash.whadl.validation.processors;

import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.UnitMemberInstance;
import org.mentalsmash.whadl.model.expressions.ConditionalExpression;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.InstanceReferenceExpression;
import org.mentalsmash.whadl.model.expressions.BinaryExpression;
import org.mentalsmash.whadl.model.expressions.UnaryExpression;
import org.mentalsmash.whadl.model.patterns.InstanceReferencePattern;
import org.mentalsmash.whadl.model.patterns.MultiplePattern;
import org.mentalsmash.whadl.model.patterns.OptionalPattern;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.validation.EntityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArmyInstanceSetReferencedInstancesProcessor implements
		EntityProcessor<ArmyInstance> {

	private static final Logger log = LoggerFactory
			.getLogger(ArmyInstanceSetReferencedInstancesProcessor.class);

	private final EntityContext context;

	public ArmyInstanceSetReferencedInstancesProcessor(EntityContext context) {
		super();
		this.context = context;
	}

	@Override
	public ArmyInstance process(ArmyInstance e)
			throws EntityProcessingException {
		this.setReferencedInstances(e);
		return e;
	}

	protected void setReferencedInstances(ArmyInstance ai) {
		for (UnitInstance ui : ai.getUnits()) {
			this.setReferencedInstances(ai, ui);
		}
	}

	protected void setReferencedInstances(ArmyInstance ai, UnitInstance ui) {
		Pattern compositionPattern = ui.getCompositionPattern();

		this.setReferencedInstances(compositionPattern, ai);

		Pattern upgradesPattern = ui.getUpgradesPattern();

		this.setReferencedInstances(upgradesPattern, ai);

		this.setReferencedInstances(ui.getLinkedUnitsPattern(), ai);

		for (UnitMemberInstance umi : ui.getMembers()) {
			Pattern equipPattern = umi.getEquipmentPattern();

			this.setReferencedInstances(equipPattern, umi);

		}

	}

	public void setReferencedInstances(Pattern p, Entity entityContext) {
	
		if (p instanceof InstanceReferencePattern) {
			this.setReferencedInstances((InstanceReferencePattern)p,entityContext);
		} else if (p instanceof MultiplePattern) {
			for (Pattern pat : (MultiplePattern) p) {
				this.setReferencedInstances(pat, entityContext);
			}
		} else if (p instanceof OptionalPattern) {
			this.setReferencedInstances(((OptionalPattern) p)
					.getOptionalPattern(), entityContext);
		}
	}
	
	public void setReferencedInstances(Expression exp, Entity entityContext) {
		if (exp instanceof InstanceReferenceExpression) {
			this.setReferencedInstances((InstanceReferenceExpression)exp,entityContext);
		}else if (exp instanceof ConditionalExpression) {
			ConditionalExpression cexp = (ConditionalExpression) exp;
			this.setReferencedInstances(cexp.getFirstTerm(), entityContext);
			this.setReferencedInstances(cexp.getSecondTerm(), entityContext);
			this.setReferencedInstances(cexp.getCondition(), entityContext);
		} else if (exp instanceof BinaryExpression) {
			BinaryExpression ttexp = (BinaryExpression) exp;
			this.setReferencedInstances(ttexp.getFirstTerm(), entityContext);
			this.setReferencedInstances(ttexp.getSecondTerm(), entityContext);
		} else if (exp instanceof UnaryExpression) {
			UnaryExpression utexp = (UnaryExpression) exp;
			this.setReferencedInstances(utexp.getTerm(), entityContext);
		}
	}
	
	protected void setReferencedInstances(InstanceReferenceExpression erp,Entity entityContext) {
		EntityContext context = this.context.getSubContext(entityContext);
		
		String selector = erp.getReference();
		Entity e = context.getEntity(selector);
		erp.setType(e);
		//erp.setReference(e.getReference());
		log.debug("Setting instance reference " + selector + " to "
				+ e.getReference());
	}
	
	protected void setReferencedInstances(InstanceReferencePattern erp,Entity entityContext) {
		EntityContext context = this.context.getSubContext(entityContext);
		
		String selector = erp.getName();
		Entity e = context.getEntity(selector);
		erp.setType(e);
		//erp.setReference(e.getReference());
		log.debug("Setting instance reference " + selector + " to "
				+ e.getReference());
	}

	@Override
	public String toString() {
		return "PROC" + "-" + "ARMYINST" + "-" + "SET.REFERENCED.INSTANCES";
	}

}
