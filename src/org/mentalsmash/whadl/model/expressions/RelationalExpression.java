package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.WhadlRuntimeException;


public class RelationalExpression extends BinaryExpression {

	public RelationalExpression(Expression firstTerm) {
		super(firstTerm);
	}

	public RelationalExpression(Expression firstTerm, Expression secondTerm,
			Operator op) {
		super(firstTerm, secondTerm, op);

		if (op != Operator.LESS && op != Operator.LESSEQ && op != Operator.MORE
				&& op != Operator.MOREEQ) {
			throw new WhadlRuntimeException("Illegal operator : " + op);
		}
	}

}
