package org.mentalsmash.whadl.parser.visitor.ast;

import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.InstanceReferenceExpression;
import org.mentalsmash.whadl.model.patterns.ConjunctEntityPatternSet;
import org.mentalsmash.whadl.model.patterns.AlternativePattern;
import org.mentalsmash.whadl.model.patterns.InstanceReferencePattern;
import org.mentalsmash.whadl.model.patterns.OptionalPattern;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.model.patterns.SinglePattern;
import org.mentalsmash.whadl.parser.nodes.*;
import org.mentalsmash.whadl.parser.visitor.DepthFirstRetVisitor;

public class PatternGenerator extends
		DepthFirstRetVisitor<org.mentalsmash.whadl.model.patterns.Pattern> {

	@Override
	public org.mentalsmash.whadl.model.patterns.Pattern visit(
			org.mentalsmash.whadl.parser.nodes.Pattern n) {
		Pattern mem = this.visit(n.f0);
		return mem;
	}

	@Override
	public org.mentalsmash.whadl.model.patterns.Pattern visit(SetPatternExpr n) {
		if (n.f1.present()) {
			ConjunctEntityPatternSet set = new ConjunctEntityPatternSet();

			Pattern m1 = this.visit(n.f0);
			if (m1 instanceof ConjunctEntityPatternSet) {
				set.addAll((ConjunctEntityPatternSet) m1);
			} else {
				set.add(m1);
			}

			if (n.f1.present()) {

				for (INode inode : n.f1.nodes) {
					NodeSequence seq = (NodeSequence) inode;
					m1 = this.visit((AlternativeSetExpr) seq.elementAt(1));
					if (m1 instanceof ConjunctEntityPatternSet) {
						set.addAll((ConjunctEntityPatternSet) m1);
					} else {
						set.add(m1);
					}
				}

			}

			return set;
		} else {
			return this.visit(n.f0);
		}
	}

	@Override
	public org.mentalsmash.whadl.model.patterns.Pattern visit(
			AlternativeSetExpr n) {
		if (n.f1.present()) {
			AlternativePattern set = new AlternativePattern();

			Pattern m1 = this.visit(n.f0);
			if (m1 instanceof AlternativePattern) {
				set.addAll((AlternativePattern) m1);
			} else {
				set.add(m1);
			}

			if (n.f1.present()) {

				for (INode inode : n.f1.nodes) {
					NodeSequence seq = (NodeSequence) inode;
					m1 = this.visit((SinglePatternExpr) seq.elementAt(1));
					if (m1 instanceof AlternativePattern) {
						set.addAll((AlternativePattern) m1);
					} else {
						set.add(m1);
					}
				}

			}

			return set;
		} else {
			return this.visit(n.f0);
		}
	}

	@Override
	public org.mentalsmash.whadl.model.patterns.Pattern visit(
			SinglePatternExpr n) {
		switch (n.f0.which) {
		case 0:
			return this.visit((SingleEntityPatternExpr) n.f0.choice);
		case 1:
			return this
					.visit((org.mentalsmash.whadl.parser.nodes.Pattern) ((NodeSequence) n.f0.choice)
							.elementAt(1));
		default:
			return new OptionalPattern(
					this.visit((org.mentalsmash.whadl.parser.nodes.Pattern) ((NodeSequence) n.f0.choice)
							.elementAt(1)));
		}
	}

	@Override
	public org.mentalsmash.whadl.model.patterns.Pattern visit(
			SingleEntityPatternExpr n) {
		SinglePattern ref = null;
		if (n.f0.present()) {
			NodeChoice choice = ((Quantifier) n.f0.node).f0;

			switch (choice.which) {
			case 0: {
				int quantity = Integer
						.parseInt(((NodeToken) choice.choice).tokenImage);
				Expression nameExpr = new ExpressionGenerator().visit(n.f1);

				if (nameExpr instanceof InstanceReferenceExpression) {
					String reference = ((InstanceReferenceExpression) nameExpr)
							.getReference();
					ref = new InstanceReferencePattern(reference);
				} else {
					String name = nameExpr.toString();
					ref = new SinglePattern(name, quantity);
				}
				break;
			}
			default: {
				Expression nameExpr = new ExpressionGenerator().visit(n.f1);
				if (nameExpr instanceof InstanceReferenceExpression) {
					String reference = ((InstanceReferenceExpression) nameExpr)
							.getReference();
					ref = new InstanceReferencePattern(reference);
				} else {
					String name = nameExpr.toString();
					ref = new SinglePattern(name, Pattern.QUANTITY_ANY);
				}
				break;
			}
			}

		} else {
			Expression nameExpr = new ExpressionGenerator().visit(n.f1);
			if (nameExpr instanceof InstanceReferenceExpression) {
				String reference = ((InstanceReferenceExpression) nameExpr)
						.getReference();
				ref = new InstanceReferencePattern(reference);
			} else {
				String name = nameExpr.toString();
				ref = new SinglePattern(name);
			}
		}

		if (n.f2.present()) {
			org.mentalsmash.whadl.parser.nodes.Expression exprNode = (org.mentalsmash.whadl.parser.nodes.Expression) ((NodeSequence) n.f2.node)
					.elementAt(1);
			// System.out.println("expressionNode: " + exprNode);
			Expression costExpr = new ExpressionGenerator().parseAST(exprNode);
			// System.out.println("expression: " + costExpr);
			// if (costExpr != null) {
			ref.setCostExpression(costExpr);
			// }
		}

		return ref;
	}

}
