package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.validation.EntityContext;

public class CollectionOperationExpression extends BinaryExpression {

	public CollectionOperationExpression(Expression firstTerm,
			Expression secondTerm, Operator op) {
		super(firstTerm, secondTerm, op);

		if (op != Operator.CONTAINS && op != Operator.UNION
				&& op != Operator.INTERSECT && op != Operator.EACH
				&& op != Operator.SELECT) {
			throw new WhadlRuntimeException("Illegal operator : " + op);
		}
	}

	public CollectionOperationExpression(Expression firstTerm) {
		super(firstTerm);
	}

	@Override
	public Object evaluate(EntityContext context) {
		if (this.secondTerm == null) {
			return this.firstTerm.evaluate(context);
		}

		Object collObj = this.firstTerm.evaluate(context);

		try {

			if (this.op == Operator.SELECT || this.op == Operator.EACH) {
				return this.op.eval(collObj, this.secondTerm, context);
			} else {
				Object b = this.secondTerm.evaluate(context);
				return this.op.eval(collObj, b);
			}

		} catch (Exception e) {
			throw new WhadlRuntimeException(
					"Error while evaluating expression " + this, e);
		}

	}

}
