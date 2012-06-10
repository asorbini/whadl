package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.validation.EntityContext;

public class EntityReferenceExpression extends BaseExpression {
	private final String ref;

	public EntityReferenceExpression(String ref) {
		this.ref = ref;
	}

	@Override
	public Object evaluate(EntityContext context) {
		Entity o = context.getEntity(this.ref);
		return o;
	}

	public String getReference() {
		return ref;
	}

	public String toString() {
		return ref;
	}
	
	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

}
