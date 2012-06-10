package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.WhadlRuntimeException;

public class LogicalExpression extends BinaryExpression {

	public LogicalExpression(Expression firstTerm) {
		super(firstTerm);
	}

	public LogicalExpression(Expression firstTerm, Expression secondTerm,
			Operator op) {
		super(firstTerm, secondTerm, op);
		
		if (op != Operator.AND && op != Operator.OR) {
			throw new WhadlRuntimeException("Illegal operator : "+op);
		}
	}

}
