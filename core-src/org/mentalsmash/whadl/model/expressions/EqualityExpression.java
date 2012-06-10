package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.WhadlRuntimeException;


public class EqualityExpression extends BinaryExpression {

	public EqualityExpression(Expression firstTerm) {
		super(firstTerm);
	}

	public EqualityExpression(Expression firstTerm, Expression secondTerm,
			Operator op) {
		super(firstTerm, secondTerm, op);

		if (op != Operator.EQUALS && op != Operator.NOTEQUALS) {
			throw new WhadlRuntimeException("Illegal operator : " + op);
		}
	}

}
