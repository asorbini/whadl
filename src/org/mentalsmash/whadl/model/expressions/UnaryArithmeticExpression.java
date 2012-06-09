package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.validation.EntityContext;

public class UnaryArithmeticExpression extends UnaryExpression {

	public UnaryArithmeticExpression(Expression term, Operator op) {
		super(term, op);

		if (op != Operator.PLUS && op != Operator.MINUS) {
			throw new WhadlRuntimeException("Illegal operator : " + op);
		}
	}

	@Override
	public Object evaluate(EntityContext context) {
		Object res = this.term.evaluate(context);

		try {
			return this.op.eval(0, res);
		} catch (Exception e) {
			throw new WhadlRuntimeException(
					"Error while evaluating expression " + this, e);
		}
	}

}
