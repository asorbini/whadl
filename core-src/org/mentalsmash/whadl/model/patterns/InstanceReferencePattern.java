package org.mentalsmash.whadl.model.patterns;

import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;

public class InstanceReferencePattern extends SinglePattern {

	private String reference;
	private Entity type;

	public InstanceReferencePattern(String reference) {
		this.reference = reference;
		// System.err.println("******* " + reference);
	}
	
	public void setType(Entity type) {
		// log.debug("*** SETTING TYPE FOR INSTANCE REFERENCE "+this.reference+" : "+type);
		this.type = type;
		this.reference = type.getReference();
	}

	public Entity getType() {
		return type;
	}

	
	@Override
	public String getName() {
		if (this.isEvaluating()) {
			if (type == null) {
				// throw new WhadlRuntimeException(
				// "No type set for instance reference " + this.reference);
				return "INSTANCE_REF";
			}

			Entity e = (Entity) type.getAttribute("type");
			return e.getName();
		} else {
			return this.reference;
		}
	}

	@Override
	public void startEvaluation() {
		// log.debug("***** STARTING INSTANCE EVALUATION : " + this.reference);
		super.startEvaluation();
	}

	@Override
	public void stopEvaluation() {
		// log.debug("***** STOPPING INSTANCE EVALUATION : " + this.reference);
		super.stopEvaluation();
	}

	@Override
	public Expression getCostExpression() {
		return new LiteralExpression(1000);
	}

}
