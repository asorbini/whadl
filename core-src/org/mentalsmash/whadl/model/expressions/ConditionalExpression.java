package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.validation.EntityContext;

public class ConditionalExpression extends BinaryExpression {
	private Expression condition;

	public ConditionalExpression(Expression firstTerm, Expression secondTerm,
			Expression condition) {
		super(firstTerm, secondTerm, Operator.CONDITION);
		this.condition = condition;
	}

	public Expression getCondition() {
		return condition;
	}
	
	public void setCondition(Expression c){
		this.condition = c;
	}

	@Override
	public Object evaluate(EntityContext context) {
		Object res = this.condition.evaluate(context);
		Object a = this.firstTerm.evaluate(context);
		Object b = this.secondTerm.evaluate(context);
		try {
			return this.op.eval(res, a, b);
		} catch (Exception e) {
			throw new WhadlRuntimeException(
					"Error while evaluating expression " + this, e);
		}
	}

	public String toString() {
		return "( " + condition + " ? " + firstTerm + " : " + secondTerm + " )";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((condition == null) ? 0 : condition.hashCode());
		result = prime * result
				+ ((firstTerm == null) ? 0 : firstTerm.hashCode());
		result = prime * result
				+ ((secondTerm == null) ? 0 : secondTerm.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ConditionalExpression))
			return false;
		ConditionalExpression other = (ConditionalExpression) obj;
		if (condition == null) {
			if (other.condition != null)
				return false;
		} else if (!condition.equals(other.condition))
			return false;
		if (firstTerm == null) {
			if (other.firstTerm != null)
				return false;
		} else if (!firstTerm.equals(other.firstTerm))
			return false;
		if (secondTerm == null) {
			if (other.secondTerm != null)
				return false;
		} else if (!secondTerm.equals(other.secondTerm))
			return false;
		return true;
	}

	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this.condition);
		super.accept(visitor);
	}

}
