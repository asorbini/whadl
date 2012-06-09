package org.mentalsmash.whadl.parser.visitor.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.expressions.ArithmeticExpression;
import org.mentalsmash.whadl.model.expressions.BinaryExpression;
import org.mentalsmash.whadl.model.expressions.EntityReferenceExpression;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.ExpressionsCollection;
import org.mentalsmash.whadl.model.expressions.InstanceReferenceExpression;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.model.expressions.LogicalExpression;
import org.mentalsmash.whadl.model.expressions.MemberSelectorExpression;
import org.mentalsmash.whadl.model.expressions.NotExpression;
import org.mentalsmash.whadl.model.expressions.ParenthesesExpression;
import org.mentalsmash.whadl.model.expressions.UnaryArithmeticExpression;
import org.mentalsmash.whadl.model.expressions.Expression.Operator;
import org.mentalsmash.whadl.parser.nodes.AdditiveExpression;
import org.mentalsmash.whadl.parser.nodes.AttributeSelector;
import org.mentalsmash.whadl.parser.nodes.BooleanLiteral;
import org.mentalsmash.whadl.parser.nodes.CollectionLiteral;
import org.mentalsmash.whadl.parser.nodes.CollectionOperationExpression;
import org.mentalsmash.whadl.parser.nodes.ConditionalExpression;
import org.mentalsmash.whadl.parser.nodes.EqualityExpression;
import org.mentalsmash.whadl.parser.nodes.INode;
import org.mentalsmash.whadl.parser.nodes.LabeledExpression;
import org.mentalsmash.whadl.parser.nodes.Literal;
import org.mentalsmash.whadl.parser.nodes.LogicalAndExpression;
import org.mentalsmash.whadl.parser.nodes.LogicalOrExpression;
import org.mentalsmash.whadl.parser.nodes.MultiplicativeExpression;
import org.mentalsmash.whadl.parser.nodes.NodeChoice;
import org.mentalsmash.whadl.parser.nodes.NodeListOptional;
import org.mentalsmash.whadl.parser.nodes.NodeSequence;
import org.mentalsmash.whadl.parser.nodes.NodeToken;
import org.mentalsmash.whadl.parser.nodes.Pattern;
import org.mentalsmash.whadl.parser.nodes.PrimaryExpression;
import org.mentalsmash.whadl.parser.nodes.Reference;
import org.mentalsmash.whadl.parser.nodes.RelationalExpression;
import org.mentalsmash.whadl.parser.nodes.UnaryExpression;
import org.mentalsmash.whadl.parser.nodes.UnaryExpressionNotPlusMinus;
import org.mentalsmash.whadl.parser.visitor.DepthFirstRetVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressionGenerator extends DepthFirstRetVisitor<Expression> {

	public Expression parseAST(org.mentalsmash.whadl.parser.nodes.Expression n) {
		// Expression result = this.visit(n);
		// return removeParentheses(fixAssociativity(result));
		return this.simpleParse(n);
	}

	public Expression simpleParse(
			org.mentalsmash.whadl.parser.nodes.Expression n) {
		return removeParentheses(this.visit(n));
		// this.visit(n);
	}

	@Override
	public Expression visit(LabeledExpression n) {
		String label = n.f1.tokenImage;
		Expression exp = this.visit(n.f3);
		exp.setLabel(label);
		return exp;
	}

	@Override
	public Expression visit(AdditiveExpression n) {
		if (n.f1.present()) {
			Iterator<INode> it = n.f1.nodes.iterator();

			INode a = n.f0;
			Expression arg1 = a.accept(this);

			do {
				INode next = it.next();
				INode b = ((NodeSequence) next).elementAt(1);
				String op = ((NodeToken) ((NodeChoice) ((NodeSequence) next)
						.elementAt(0)).choice).tokenImage;
				Expression arg2 = b.accept(this);
				arg1 = new ArithmeticExpression(arg1, arg2, Operator
						.getByString(op));
			} while (it.hasNext());

			return arg1;
		} else {
			return this.visit(n.f0);
		}
	}

	@Override
	public Expression visit(CollectionOperationExpression n) {
		if (n.f1.present()) {
			Iterator<INode> it = n.f1.nodes.iterator();

			INode a = n.f0;
			Expression arg1 = a.accept(this);

			do {
				INode next = it.next();

				INode b = ((NodeSequence) next).elementAt(1);
				String op = ((NodeToken) ((NodeChoice) ((NodeSequence) next)
						.elementAt(0)).choice).tokenImage;
				Expression arg2 = b.accept(this);
				arg1 = new org.mentalsmash.whadl.model.expressions.CollectionOperationExpression(
						arg1, arg2, Operator.getByString(op));
			} while (it.hasNext());

			return arg1;
		} else {
			return this.visit(n.f0);
		}
	}

	@Override
	public Expression visit(LogicalAndExpression n) {
		if (n.f1.present()) {
			Iterator<INode> it = n.f1.nodes.iterator();

			INode a = n.f0;
			Expression arg1 = a.accept(this);

			do {
				INode next = it.next();
				INode b = ((NodeSequence) next).elementAt(1);
				Expression arg2 = b.accept(this);
				arg1 = new LogicalExpression(arg1, arg2, Operator.AND);
			} while (it.hasNext());

			return arg1;
		} else {
			return this.visit(n.f0);
		}
	}

	@Override
	public Expression visit(ConditionalExpression n) {
		Expression result = null;

		if (n.f1.present()) {
			Expression cond = this.visit(n.f0);

			NodeSequence opt = (NodeSequence) n.f1.node;

			Expression a = this
					.visit((org.mentalsmash.whadl.parser.nodes.Expression) opt
							.elementAt(1));
			Expression b = this
					.visit((org.mentalsmash.whadl.parser.nodes.Expression) opt
							.elementAt(3));

			result = new org.mentalsmash.whadl.model.expressions.ConditionalExpression(
					a, b, cond);
		} else {
			result = this.visit(n.f0);
		}

		return result;
	}

	@Override
	public Expression visit(LogicalOrExpression n) {
		if (n.f1.present()) {
			Iterator<INode> it = n.f1.nodes.iterator();

			INode a = n.f0;
			Expression arg1 = a.accept(this);

			do {
				INode next = it.next();
				INode b = ((NodeSequence) next).elementAt(1);
				Expression arg2 = b.accept(this);
				arg1 = new LogicalExpression(arg1, arg2, Operator.OR);
			} while (it.hasNext());

			return arg1;
		} else {
			return this.visit(n.f0);
		}
	}

	@Override
	public Expression visit(EqualityExpression n) {
		if (n.f1.present()) {
			Iterator<INode> it = n.f1.nodes.iterator();

			INode a = n.f0;
			Expression arg1 = a.accept(this);

			do {
				INode next = it.next();
				INode b = ((NodeSequence) next).elementAt(1);
				String op = ((NodeToken) ((NodeChoice) ((NodeSequence) next)
						.elementAt(0)).choice).tokenImage;
				Expression arg2 = b.accept(this);
				arg1 = new org.mentalsmash.whadl.model.expressions.EqualityExpression(
						arg1, arg2, Operator.getByString(op));
			} while (it.hasNext());

			return arg1;
		} else {
			return this.visit(n.f0);
		}
	}

	@Override
	public Expression visit(MultiplicativeExpression n) {
		if (n.f1.present()) {
			Iterator<INode> it = n.f1.nodes.iterator();

			INode a = n.f0;
			Expression arg1 = a.accept(this);

			do {
				INode next = it.next();
				INode b = ((NodeSequence) next).elementAt(1);
				String op = ((NodeToken) ((NodeChoice) ((NodeSequence) next)
						.elementAt(0)).choice).tokenImage;
				Expression arg2 = b.accept(this);
				arg1 = new ArithmeticExpression(arg1, arg2, Operator
						.getByString(op));
			} while (it.hasNext());

			return arg1;
		} else {
			return this.visit(n.f0);
		}
	}

	@Override
	public Expression visit(RelationalExpression n) {
		if (n.f1.present()) {
			Iterator<INode> it = n.f1.nodes.iterator();

			INode a = n.f0;
			Expression arg1 = a.accept(this);

			do {
				INode next = it.next();
				INode b = ((NodeSequence) next).elementAt(1);
				String op = ((NodeToken) ((NodeChoice) ((NodeSequence) next)
						.elementAt(0)).choice).tokenImage;
				Expression arg2 = b.accept(this);
				arg1 = new org.mentalsmash.whadl.model.expressions.RelationalExpression(
						arg1, arg2, Operator.getByString(op));
			} while (it.hasNext());

			return arg1;
		} else {
			return this.visit(n.f0);
		}
	}

	@Override
	public Expression visit(UnaryExpression n) {
		if (n.f0.which == 0) {
			NodeSequence seq = (NodeSequence) n.f0.choice;
			String op = ((NodeToken) ((NodeChoice) seq.elementAt(0)).choice).tokenImage;
			Expression term = this.visit((UnaryExpression) seq.elementAt(1));

			return new UnaryArithmeticExpression(term, Operator.getByString(op));
		} else {
			return this.visit((UnaryExpressionNotPlusMinus) n.f0.choice);
		}
	}

	@Override
	public Expression visit(UnaryExpressionNotPlusMinus n) {
		if (n.f0.which == 0) {
			NodeSequence seq = (NodeSequence) n.f0.choice;
			Expression term = this.visit((UnaryExpression) seq.elementAt(1));

			return new NotExpression(term);
		} else {
			return this.visit((PrimaryExpression) n.f0.choice);
		}
	}

	@Override
	public Expression visit(PrimaryExpression n) {
		Expression context = null;

		if (n.f0.choice instanceof Reference) {
			context = this.visit((Reference) n.f0.choice);
		} else if (n.f0.choice instanceof Literal) {
			context = this.visit((Literal) n.f0.choice);
		} else if (n.f0.which == 3) {
			context = this.visit((Pattern) ((NodeSequence) n.f0.choice)
					.elementAt(2));
		} else {
			Expression inner = this
					.visit((org.mentalsmash.whadl.parser.nodes.Expression) ((NodeSequence) n.f0.choice)
							.elementAt(1));

			context = new ParenthesesExpression(inner);
		}

		if (n.f1.present()) {
			for (int i = 0; i < n.f1.nodes.size(); i++) {
				NodeSequence seq = (NodeSequence) n.f1.nodes.get(i);
				AttributeSelector selTok = (AttributeSelector) seq.elementAt(1);
				String selector = ((NodeToken) selTok.f0.choice).tokenImage;
				MemberSelectorExpression selExp = new MemberSelectorExpression(
						context, selector);
				context = selExp;
			}
		}

		return context;

	}

	@Override
	public Expression visit(Literal n) {
		switch (n.f0.which) {
		case 0: {
			NodeToken tok = (NodeToken) n.f0.choice;
			Integer val = Integer.parseInt(tok.tokenImage);
			return new LiteralExpression(val);
		}
		case 1: {
			NodeToken tok = (NodeToken) n.f0.choice;
			String val = tok.tokenImage.substring(1,
					tok.tokenImage.length() - 1);
			return new LiteralExpression(val);
		}
		case 2: {
			BooleanLiteral tok = (BooleanLiteral) n.f0.choice;
			Boolean val = Boolean
					.parseBoolean(((NodeToken) tok.f0.choice).tokenImage);
			return new LiteralExpression(val);
		}
		default:
			CollectionLiteral colLit = (CollectionLiteral) n.f0.choice;
			return this.visit(colLit);
		}
	}

	@Override
	public Expression visit(CollectionLiteral n) {
		ExpressionsCollection coll = new ExpressionsCollection();

		if (n.f1.present()) {
			NodeSequence seq = (NodeSequence) n.f1.node;
			Expression firsEl = this
					.visit((org.mentalsmash.whadl.parser.nodes.Expression) seq
							.elementAt(0));
			coll.add(firsEl);

			NodeListOptional optElements = (NodeListOptional) seq.elementAt(1);

			if (optElements.present()) {
				for (INode node : optElements.nodes) {
					NodeSequence nSeq = (NodeSequence) node;
					Expression el = this
							.visit((org.mentalsmash.whadl.parser.nodes.Expression) nSeq
									.elementAt(1));
					coll.add(el);
				}
			}
		}

		return coll;
	}

	@Override
	public Expression visit(Reference n) {
		StringBuilder str = new StringBuilder();

		str.append(n.f1.tokenImage);
		
		if (n.f2.present()) {
			for (INode inode : n.f2.nodes) {
				NodeSequence seq = (NodeSequence) inode;
				NodeToken tok = (NodeToken) seq.elementAt(1);
				str.append("."+tok);
			}
		}
		
		if (n.f0.present()) {
			return new InstanceReferenceExpression(str.toString());
		} else {
			return new EntityReferenceExpression(str.toString());
		}
	}

	private static final Logger log = LoggerFactory
			.getLogger(ExpressionGenerator.class);

	// private Expression fixAssociativity(Expression exp) {
	// Expression root = null;
	//
	// // System.out.println("**************");
	// // System.out.println("FIXING: " + exp);
	//
	// if (exp instanceof
	// org.mentalsmash.whadl.model.expressions.ConditionalExpression) {
	// org.mentalsmash.whadl.model.expressions.ConditionalExpression cexp =
	// (org.mentalsmash.whadl.model.expressions.ConditionalExpression) exp;
	//
	// Expression arg1 = cexp.getFirstTerm();
	// Expression arg2 = cexp.getSecondTerm();
	// Expression cond = cexp.getCondition();
	// cexp.setCondition(fixAssociativity(cond));
	// cexp.setFirstTerm(fixAssociativity(arg1));
	// cexp.setSecondTerm(fixAssociativity(arg2));
	//
	// } else if (exp instanceof BinaryExpression) {
	// BinaryExpression binExp = (BinaryExpression) exp;
	// Expression arg2 = binExp.getSecondTerm();
	//
	// if (arg2 instanceof
	// org.mentalsmash.whadl.model.expressions.ConditionalExpression) {
	// org.mentalsmash.whadl.model.expressions.ConditionalExpression condArg2 =
	// (org.mentalsmash.whadl.model.expressions.ConditionalExpression) arg2;
	// binExp.setSecondTerm(condArg2.getCondition());
	// condArg2.setCondition(binExp);
	// root = arg2;
	// } else if (arg2 instanceof BinaryExpression) {
	// BinaryExpression binArg2 = (BinaryExpression) arg2;
	//
	// Operator expOp = binExp.getOp();
	// Operator argOp = binArg2.getOp();
	//
	// int expPr = priority(expOp);
	// int argPr = priority(argOp);
	//
	// if (expPr >= argPr) {
	// System.err.println(expOp + "(" + expPr + ") >= " + argOp
	// + "(" + argPr + ")");
	// binExp.setSecondTerm(binArg2.getFirstTerm());
	// binArg2.setFirstTerm(binExp);
	// root = arg2;
	// } else {
	// System.err.println(expOp + "(" + expPr + ") < " + argOp
	// + "(" + argPr + ")");
	// }
	//
	// } else if (arg2 instanceof ParenthesesExpression) {
	// binExp.setSecondTerm(fixAssociativity(arg2));
	// }
	//
	// Expression arg1 = binExp.getFirstTerm();
	// if (arg1 instanceof ParenthesesExpression) {
	// binExp.setFirstTerm(fixAssociativity(arg1));
	// }
	// } else if (exp instanceof
	// org.mentalsmash.whadl.model.expressions.UnaryExpression) {
	// org.mentalsmash.whadl.model.expressions.UnaryExpression uexp =
	// (org.mentalsmash.whadl.model.expressions.UnaryExpression) exp;
	// uexp.setTerm(fixAssociativity(uexp.getTerm()));
	// } else if (exp instanceof ParenthesesExpression) {
	// ParenthesesExpression pexp = (ParenthesesExpression) exp;
	// pexp.setInner(fixAssociativity(pexp.getInner()));
	// }
	//
	// if (root != null) {
	// // System.out.println("STEP: " + root);
	// // System.out.println("************** RECURRING");
	// return fixAssociativity(root);
	// } else {
	// // System.out.println("FIXED: "+exp);
	// // System.out.println("************** ENDING");
	// return exp;
	// }
	//
	// }

	private Expression removeParentheses(Expression exp) {
		if (exp instanceof org.mentalsmash.whadl.model.expressions.ConditionalExpression) {
			org.mentalsmash.whadl.model.expressions.ConditionalExpression cexp = (org.mentalsmash.whadl.model.expressions.ConditionalExpression) exp;
			cexp.setCondition(removeParentheses(cexp.getCondition()));
			cexp.setFirstTerm(removeParentheses(cexp.getFirstTerm()));
			cexp.setSecondTerm(removeParentheses(cexp.getSecondTerm()));
			return cexp;
		} else if (exp instanceof BinaryExpression) {
			BinaryExpression binExp = (BinaryExpression) exp;
			binExp.setFirstTerm(removeParentheses(binExp.getFirstTerm()));
			binExp.setSecondTerm(removeParentheses(binExp.getSecondTerm()));
			return binExp;
		} else if (exp instanceof org.mentalsmash.whadl.model.expressions.UnaryExpression) {
			org.mentalsmash.whadl.model.expressions.UnaryExpression uexp = (org.mentalsmash.whadl.model.expressions.UnaryExpression) exp;
			uexp.setTerm(removeParentheses(uexp.getTerm()));
			return uexp;
		} else if (exp instanceof ParenthesesExpression) {
			ParenthesesExpression pexp = (ParenthesesExpression) exp;
			Expression inner = pexp.getInner();
			
			if (pexp.getLabel() != null) {
				if (inner.getLabel() != null
						&& !inner.getLabel().equals(pexp.getLabel())) {
					throw new WhadlRuntimeException(
							"Unmatching labels for parenthesis expression "
									+ "and its content: par=" + pexp.getLabel()
									+ "; inner=" + inner.getLabel());
				}
				
				
				inner.setLabel(pexp.getLabel());
			}
			
			return removeParentheses(inner);
		} else {
			return exp;
		}
	}

	private static ArrayList<Set<Operator>> priorities = new ArrayList<Set<Operator>>();

	static {
		for (int i = 0; i < 9; i++) {
			priorities.add(new HashSet<Operator>());
		}
		priorities.get(0).add(Operator.CONDITION);
		priorities.get(1).add(Operator.OR);
		priorities.get(2).add(Operator.AND);
		priorities.get(3).add(Operator.EQUALS);
		priorities.get(3).add(Operator.NOTEQUALS);
		priorities.get(4).add(Operator.MORE);
		priorities.get(4).add(Operator.MOREEQ);
		priorities.get(4).add(Operator.LESS);
		priorities.get(4).add(Operator.LESSEQ);
		priorities.get(5).add(Operator.PLUS);
		priorities.get(5).add(Operator.MINUS);
		priorities.get(6).add(Operator.MULTIPLY);
		priorities.get(6).add(Operator.DIVISION);
		priorities.get(7).add(Operator.SELECT);
		priorities.get(7).add(Operator.CONTAINS);
		priorities.get(7).add(Operator.UNION);
		priorities.get(7).add(Operator.INTERSECT);
		priorities.get(7).add(Operator.EACH);
	}

	private int priority(Operator op) {
		for (int i = 0; i < priorities.size(); i++) {
			Set<Operator> plevel = priorities.get(i);
			if (plevel.contains(op)) {
				return i;
			}
		}
		throw new WhadlRuntimeException("Unknown priority for operator : " + op);
	}

}
