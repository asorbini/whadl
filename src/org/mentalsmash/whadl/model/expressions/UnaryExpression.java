package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.validation.EntityContext;

public abstract class UnaryExpression extends BaseExpression {
	protected Expression term;
	protected final Operator op;

	public UnaryExpression(Expression term, Operator op) {
		this.term = term;
		this.op = op;
	}

	public Expression getTerm() {
		return term;
	}

	public Expression.Operator getOp() {
		return op;
	}

	public String toString() {
		return op + " " + term;
	}

	@Override
	public Object evaluate(EntityContext context) {
		Object res = this.term.evaluate(context);

		try {
			return this.op.eval(res);
		} catch (Exception e) {
			throw new WhadlRuntimeException(
					"Error while evaluating expression " + this, e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((op == null) ? 0 : op.hashCode());
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UnaryExpression))
			return false;
		UnaryExpression other = (UnaryExpression) obj;
		if (op == null) {
			if (other.op != null)
				return false;
		} else if (!op.equals(other.op))
			return false;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		return true;
	}
	
	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this.term);
	}

	public void setTerm(Expression t) {
		this.term = t;
	}

}
