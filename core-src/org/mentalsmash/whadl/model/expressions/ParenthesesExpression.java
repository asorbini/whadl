package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.validation.EntityContext;

public class ParenthesesExpression extends BaseExpression {

	private Expression inner;
	
	public ParenthesesExpression(Expression inner) {
		this.inner = inner;
	}

	@Override
	public void accept(ExpressionVisitor visitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object evaluate(EntityContext context) {
		return inner.evaluate(context);
	}

	public Expression getInner() {
		return inner;
	}
	
	public String toString() {
		return "-(- "+this.inner+" -)-";
	}

	public void setInner(Expression inner) {
		this.inner = inner;
	}

}
