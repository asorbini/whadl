package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.validation.EntityContext;

public abstract class BinaryExpression extends BaseExpression {
	protected Expression firstTerm;
	protected Expression secondTerm;
	protected final Expression.Operator op;

	public BinaryExpression(Expression firstTerm) {
		this.firstTerm = firstTerm;
		this.secondTerm = null;
		this.op = null;
	}

	public BinaryExpression(Expression firstTerm, Expression secondTerm,
			Operator op) {
		this.firstTerm = firstTerm;
		this.secondTerm = secondTerm;
		this.op = op;
	}

	public Expression getFirstTerm() {
		return firstTerm;
	}

	public Expression getSecondTerm() {
		return secondTerm;
	}
	
	public void setFirstTerm(Expression t){
		this.firstTerm = t;
	}
	
	public void setSecondTerm(Expression t){
		this.secondTerm = t;
	}

	public Expression.Operator getOp() {
		return op;
	}

	public String toString() {
		return "( " + firstTerm + " " + op + " " + secondTerm + " )";
	}

	@Override
	public Object evaluate(EntityContext context) {
		if (this.secondTerm == null) {
			return this.firstTerm.evaluate(context);
		}

		Object firstRes = this.firstTerm.evaluate(context);
		Object secondRes = this.secondTerm.evaluate(context);

		try {
			return this.op.eval(firstRes, secondRes);
		} catch (Exception e) {
			throw new WhadlRuntimeException(
					"Error while evaluating expression " + this, e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstTerm == null) ? 0 : firstTerm.hashCode());
		result = prime * result + ((op == null) ? 0 : op.hashCode());
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
		if (!(obj instanceof BinaryExpression))
			return false;
		BinaryExpression other = (BinaryExpression) obj;
		if (firstTerm == null) {
			if (other.firstTerm != null)
				return false;
		} else if (!firstTerm.equals(other.firstTerm))
			return false;
		if (op == null) {
			if (other.op != null)
				return false;
		} else if (!op.equals(other.op))
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
		visitor.visit(this.firstTerm);
		visitor.visit(this.secondTerm);
	}
	
	


}
