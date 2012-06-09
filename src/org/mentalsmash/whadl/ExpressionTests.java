package org.mentalsmash.whadl;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.mentalsmash.whadl.model.expressions.ArithmeticExpression;
import org.mentalsmash.whadl.model.expressions.CollectionOperationExpression;
import org.mentalsmash.whadl.model.expressions.ConditionalExpression;
import org.mentalsmash.whadl.model.expressions.EntityReferenceExpression;
import org.mentalsmash.whadl.model.expressions.EqualityExpression;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.ExpressionsCollection;
import org.mentalsmash.whadl.model.expressions.InstanceReferenceExpression;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.model.expressions.LogicalExpression;
import org.mentalsmash.whadl.model.expressions.MemberSelectorExpression;
import org.mentalsmash.whadl.model.expressions.NotExpression;
import org.mentalsmash.whadl.model.expressions.RelationalExpression;
import org.mentalsmash.whadl.model.expressions.BinaryExpression;
import org.mentalsmash.whadl.model.expressions.UnaryArithmeticExpression;
import org.mentalsmash.whadl.model.expressions.UnaryExpression;
import org.mentalsmash.whadl.parser.ParserUtils;
import org.mentalsmash.whadl.parser.WhadlParser;
import org.mentalsmash.whadl.parser.visitor.ast.ExpressionGenerator;

public class ExpressionTests {

	private static class ExpASTPrinter {
		
		private int _indent = 0;
		private int _indentUnit = 3;
		
		private Set<Integer> lines = new HashSet<Integer>();
		
		private void line(){
			this.lines.add(_indent);
		}
		
		private void unline(){
			this.lines.remove(_indent);
		}
		
		private void indent() {
			_indent += _indentUnit;
		}
		
		private void deindent() {
			_indent -= (_indent > 0)? _indentUnit : 0;
		}
		
		private void out(String line) {
			for (int i = 0; i < this._indent; i++) {
				if (lines.contains(i)) {
					System.out.print("|");
				} else {
					System.out.print(" ");
				}
			}
			System.out.println(line);
		}
		
		
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

		
		public void visit(ArithmeticExpression exp) {
			out("ARITM("+exp.getOp()+")");
				this.line();
				out("- OP1:");
					this.indent();
					this.visit(exp.getFirstTerm());
					this.deindent();
				out("- OP2:");
					this.indent();
					this.visit(exp.getSecondTerm());
					this.deindent();
				this.unline();
//			out("____");
		}

		
		public void visit(CollectionOperationExpression exp) {
			out("COLLECT("+exp.getOp()+")");
				this.line();
				out("- OP1:");
					this.indent();
					this.visit(exp.getFirstTerm());
					this.deindent();
				out("- OP2:");
					this.indent();
					this.visit(exp.getSecondTerm());
					this.deindent();
				this.unline();
//			out("____");
		}

		
		public void visit(ConditionalExpression exp) {
			out("CONDITIONAL");
				this.line();
				out("- COND:");
					this.indent();
					this.visit(exp.getCondition());
					this.deindent();
				out("- OP1:");
					this.indent();
					this.visit(exp.getFirstTerm());
					this.deindent();
				out("- OP2:");
					this.indent();
					this.visit(exp.getSecondTerm());
					this.deindent();
				this.unline();
//			out("____");
		}

		
		public void visit(EntityReferenceExpression exp) {
			out("REF("+exp+")");
		}

		
		public void visit(EqualityExpression exp) {
			out("- EQ("+exp.getOp()+")");
				this.line();
				out("- OP1:");
					this.indent();
					this.visit(exp.getFirstTerm());
					this.deindent();
				out("- OP2:");
					this.indent();
					this.visit(exp.getSecondTerm());
					this.deindent();
				this.unline();
//			out("____");
		}

		
		public void visit(ExpressionsCollection exp) {
			out("COLLECTION");
		}

		
		public void visit(InstanceReferenceExpression exp) {
			out("INSTREF("+exp+")");
		}

		
		public void visit(LiteralExpression exp) {
			out("LITERAL("+exp+")");
		}

		
		public void visit(LogicalExpression exp) {
			out("LOGIC("+exp.getOp()+")");
				this.line();
				out("- OP1:");
					this.indent();
					this.visit(exp.getFirstTerm());
					this.deindent();
				out("- OP2:");
					this.indent();
					this.visit(exp.getSecondTerm());
					this.deindent();
				this.unline();
//			out("____");
		}

		
		public void visit(MemberSelectorExpression exp) {
			out("ATTR("+exp.getOp()+")");
				this.line();
				out("- SEL: "+exp.getSelector());
				out("- OBJ:");
					this.indent();
					this.visit(exp.getTerm());
					this.deindent();
				this.unline();
//			out("____");
		}

		
		public void visit(NotExpression exp) {
			out("NOT");
				out("- OP:");
					this.indent();
					this.visit(exp.getTerm());
					this.deindent();
//			out("____");
		}

		
		public void visit(RelationalExpression exp) {
			out("REL("+exp.getOp()+")");
				this.line();
				out("- OP1:");
					this.indent();
					this.visit(exp.getFirstTerm());
					this.deindent();
				out("- OP2:");
					this.indent();
					this.visit(exp.getSecondTerm());
					this.deindent();
				this.unline();
//			out("____");
		}

		
		public void visit(UnaryArithmeticExpression exp) {
			out("UNARYARIT("+exp.getOp()+")");
				this.indent();
				out("- OP:");
					this.indent();
					this.visit(exp.getTerm());
					this.deindent();
//			out("____");
		}
		
		
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		//simpleTest();
		
		FileInputStream in = new FileInputStream("temp/exptests.whadl");
		WhadlParser parser = new WhadlParser(in);
		org.mentalsmash.whadl.parser.nodes.Expression n = parser.Expression();
		ExpressionGenerator generator = new ExpressionGenerator(); 
		Expression exp = generator.parseAST(n);
		Expression original = generator.simpleParse(n);
		ExpASTPrinter printer = new ExpASTPrinter();
		
//		System.out.println("*** ORIGINAL ***");
//		printer.visit(original);
//		System.out.println("****************");
//		System.out.println("*** PROCESSED ***");
//		printer.visit(exp);
//		System.out.println("*****************");
		
//		System.out.println(original);
//		System.out.println(exp);
		
	}

	private static void simpleTest() {
		String[] exprs = new String[] {
			"A ? B == C ? 1 : 2 : B == C ? 3 : 4",
			"A + B + C",
			"A * B * C - D - A",
			"( Pippo->blabla == ABC == BC )?"+
				"(Pluto > Topolino ? ABC : Pippo->blabla) > 3 :"+
				"5 + R",
			"( (Pippo->blabla == ABC) == BC )?  (((Pluto > Topolino) ? (ABC) : (Pippo->blabla)) > 3) : (5 + R)"
		};

		int i = 0;
		String res1 = null;
		String res2 = null;
		for (String str : exprs) {
			Expression exp = ParserUtils.parseExpression(str);
			System.out.println(str + " -> " + exp);
			if (i == 3) {
				res1 = exp.toString();
			} else if (i == 4) {
				res2 = exp.toString();
			}
			i++;
		}
		
		System.out.println(res1.equals(res2) ? "OK" : "NO");
	}

}
