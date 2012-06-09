package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.validation.EntityContext;

public class MemberSelectorExpression extends UnaryExpression {

	private final String selector;

	public MemberSelectorExpression(Expression context, String selector) {
		super(context, Operator.SELECTATTRIBUTE);
		this.selector = selector;
	}

	@Override
	public Object evaluate(EntityContext context) {
		Object ctx = this.term.evaluate(context);

		try {
			return this.op.eval(ctx, this.selector);
		} catch (Exception e) {
			throw new WhadlRuntimeException(
					"Error while evaluating expression " + this, e);
		}
	}

	public String getSelector() {
		return selector;
	}


	@Override
	public String toString() {
		return this.term + "." + selector;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		result = prime * result
				+ ((selector == null) ? 0 : selector.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MemberSelectorExpression))
			return false;
		MemberSelectorExpression other = (MemberSelectorExpression) obj;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		if (selector == null) {
			if (other.selector != null)
				return false;
		} else if (!selector.equals(other.selector))
			return false;
		return true;
	}

}
