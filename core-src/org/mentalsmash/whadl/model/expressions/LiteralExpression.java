package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.validation.EntityContext;

public class LiteralExpression extends BaseExpression {
	private final Object literal;
	
	
	public static final LiteralExpression BOOLEAN_TRUE = new LiteralExpression(true);
	public static final LiteralExpression INTEGER_0 = new LiteralExpression(0);
	public LiteralExpression(Object literal) {
		this.literal = literal;
	}
	
	
	@Override
	public Object evaluate(EntityContext context) {
		return this.literal;
	}


	public Object getLiteral() {
		return literal;
	}

	public String toString() {
		return literal.toString();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((literal == null) ? 0 : literal.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LiteralExpression))
			return false;
		LiteralExpression other = (LiteralExpression) obj;
		if (literal == null) {
			if (other.literal != null)
				return false;
		} else if (!literal.equals(other.literal))
			return false;
		return true;
	}
	
	@Override
	public void accept(ExpressionVisitor visitor) {
		
	}
	
}
