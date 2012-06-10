package org.mentalsmash.whadl.model.expressions;

public class NotExpression extends UnaryExpression {

	public NotExpression(Expression term) {
		super(term, Operator.NOT);
	}

}
