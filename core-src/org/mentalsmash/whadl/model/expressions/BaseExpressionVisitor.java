package org.mentalsmash.whadl.model.expressions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseExpressionVisitor implements ExpressionVisitor {

	private static final Logger log = LoggerFactory
			.getLogger(BaseExpressionVisitor.class);

	@Override
	public void visit(Expression exp) {
		if (exp instanceof ArithmeticExpression) {
			// log.info("ARIT "+exp);
			this.visit((ArithmeticExpression) exp);
		} else if (exp instanceof CollectionOperationExpression) {
			// log.info("COLL "+exp);
			this.visit((CollectionOperationExpression) exp);
		} else if (exp instanceof ConditionalExpression) {
			// log.info("COND "+exp);
			this.visit((ConditionalExpression) exp);
		} else if (exp instanceof EntityReferenceExpression) {
			// log.info("REF "+exp);
			this.visit((EntityReferenceExpression) exp);
		} else if (exp instanceof EqualityExpression) {
			// log.info("EQUAL "+exp);
			this.visit((EqualityExpression) exp);
		} else if (exp instanceof ExpressionsCollection) {
			// log.info("EXPCOLL "+exp);
			this.visit((ExpressionsCollection) exp);
		} else if (exp instanceof InstanceReferenceExpression) {
			// log.info("INST "+exp);
			this.visit((InstanceReferenceExpression) exp);
		} else if (exp instanceof LiteralExpression) {
			// log.info("LITER "+exp);
			this.visit((LiteralExpression) exp);
		} else if (exp instanceof LogicalExpression) {
			// log.info("LOGIC "+exp);
			this.visit((LogicalExpression) exp);
		} else if (exp instanceof MemberSelectorExpression) {
			// log.info("MEMBSEL "+exp);
			this.visit((MemberSelectorExpression) exp);
		} else if (exp instanceof NotExpression) {
			// log.info("NOT "+exp);
			this.visit((NotExpression) exp);
		} else if (exp instanceof RelationalExpression) {
			// log.info("RELAT "+exp);
			this.visit((RelationalExpression) exp);
		} else if (exp instanceof BinaryExpression) {
			// log.info("TWOTERMS "+exp);
			this.visit((BinaryExpression) exp);
		} else if (exp instanceof UnaryArithmeticExpression) {
			// log.info("UNARYARIT "+exp);
			this.visit((UnaryArithmeticExpression) exp);
		} else if (exp instanceof UnaryExpression) {
			// log.info("UNARY "+exp);
			this.visit((UnaryExpression) exp);
		}

	}

	@Override
	public void visit(ArithmeticExpression exp) {
		exp.accept(this);
	}

	@Override
	public void visit(CollectionOperationExpression exp) {
		exp.accept(this);
	}

	@Override
	public void visit(ConditionalExpression exp) {
		exp.accept(this);
	}

	@Override
	public void visit(EntityReferenceExpression exp) {
		exp.accept(this);
	}

	@Override
	public void visit(EqualityExpression exp) {
		exp.accept(this);
	}

	@Override
	public void visit(ExpressionsCollection exp) {
		exp.accept(this);
	}

	@Override
	public void visit(InstanceReferenceExpression exp) {
		exp.accept(this);
	}

	@Override
	public void visit(LiteralExpression exp) {
		exp.accept(this);
	}

	@Override
	public void visit(LogicalExpression exp) {
		exp.accept(this);
	}

	@Override
	public void visit(MemberSelectorExpression exp) {
		exp.accept(this);
	}

	@Override
	public void visit(NotExpression exp) {
		exp.accept(this);
	}

	@Override
	public void visit(RelationalExpression exp) {
		exp.accept(this);
	}

	@Override
	public void visit(BinaryExpression exp) {
		exp.accept(this);
	}

	@Override
	public void visit(UnaryArithmeticExpression exp) {
		exp.accept(this);
	}

}
