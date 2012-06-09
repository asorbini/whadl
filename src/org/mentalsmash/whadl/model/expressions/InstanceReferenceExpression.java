package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.validation.EntityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceReferenceExpression extends BaseExpression {

	private final static Logger log = LoggerFactory
			.getLogger(InstanceReferenceExpression.class);

	private String reference;
	private Entity type;

	public InstanceReferenceExpression(String reference) {
		this.reference = reference;
		// System.err.println("******* " + reference);
	}

	@Override
	public Object evaluate(EntityContext context) {

		return context.getEntity(this.reference);

	}
	
	public String getReference() {
		return this.reference;
	}


	public void setType(Entity type) {
		log.trace("*** SETTING TYPE FOR INSTANCE REFERENCE "+this.reference+" : "+type);
		this.type = type;
		this.reference = type.getReference();
	}

	public Entity getType() {
		return type;
	}

	

	@Override
	public void accept(ExpressionVisitor visitor) {

	}
}
