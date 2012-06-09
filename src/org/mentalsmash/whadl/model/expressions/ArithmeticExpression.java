package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.WhadlRuntimeException;


public class ArithmeticExpression extends BinaryExpression {

	public ArithmeticExpression(Expression firstTerm) {
		super(firstTerm);
	}

	public ArithmeticExpression(Expression firstTerm, Expression secondTerm,
			Operator op) {
		super(firstTerm,secondTerm,op);
		
		if (op != Operator.PLUS && op != Operator.MINUS && op != Operator.MULTIPLY
				&& op != Operator.DIVISION) {
			throw new WhadlRuntimeException("Illegal operator : " + op);
		}
	}

}
