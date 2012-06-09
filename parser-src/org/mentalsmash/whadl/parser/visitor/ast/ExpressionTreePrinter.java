package org.mentalsmash.whadl.parser.visitor.ast;

import org.mentalsmash.whadl.parser.nodes.*;
import org.mentalsmash.whadl.parser.visitor.DepthFirstVoidVisitor;

public class ExpressionTreePrinter extends DepthFirstVoidVisitor {

	private String baseIndent;
	private int currIndent = 0;
	private String indentSpace;
	
	public ExpressionTreePrinter(String baseIndent, String indentSpace) {
		this.baseIndent = baseIndent;
		this.indentSpace = indentSpace;
	}
	
	private void indent() {
		this.currIndent++;
	}
	
	private void deindent() {
		this.currIndent--;
	}
	
	private String getIndent() {
		StringBuilder str = new StringBuilder();
		str.append(this.baseIndent);
		int i = 0;
		
		while (i++ < currIndent) {
			str.append(indentSpace);
		}
		
		return str.toString();
	}
	
	private void print(String str){
		System.out.println(this.getIndent()+str);
	}

	@Override
	public void visit(AdditiveExpression n) {
		if (n.f1.present()) this.print("AdditiveExpression{");
		if (n.f1.present()) this.indent();
		super.visit(n);
		if (n.f1.present()) this.deindent();
		if (n.f1.present()) this.print("} //AdditiveExpression");
	}

	@Override
	public void visit(BooleanLiteral n) {
//		this.print("BooleanLiteral{");
//		this.indent();
		super.visit(n);
//		this.deindent();
//		this.print("} //BooleanLiteral");
	}

	@Override
	public void visit(CollectionLiteral n) {
		this.print("CollectionLiteral{");
		this.indent();
		super.visit(n);
		this.deindent();
		this.print("} //CollectionLiteral");
	}

	@Override
	public void visit(CollectionOperationExpression n) {
		if (n.f1.present()) this.print("CollectionOperationExpression{ ");
		if (n.f1.present()) this.indent();
		super.visit(n);
		if (n.f1.present()) this.deindent();
		if (n.f1.present()) this.print("} //CollectionOperationExpression");
	}

	@Override
	public void visit(LogicalAndExpression n) {
		if (n.f1.present()) this.print("AndExpression{ ");
		if (n.f1.present()) this.indent();
		super.visit(n);
		if (n.f1.present()) this.deindent();
		if (n.f1.present()) this.print("} //AndExpression");
	}

	@Override
	public void visit(ConditionalExpression n) {
		if (n.f1.present()) this.print("ConditionalExpression{ ");
		if (n.f1.present()) this.indent();
		super.visit(n);
		if (n.f1.present()) this.deindent();
		if (n.f1.present()) this.print("} //ConditionalExpression");
	}

	@Override
	public void visit(LogicalOrExpression n) {
		if (n.f1.present()) this.print("OrExpression{ ");
		if (n.f1.present()) this.indent();
		super.visit(n);
		if (n.f1.present()) this.deindent();
		if (n.f1.present()) this.print("} //OrExpression");
	}

	@Override
	public void visit(EqualityExpression n) {
		if (n.f1.present()) this.print("EqualityExpression{ ");
		if (n.f1.present()) this.indent();
		super.visit(n);
		if (n.f1.present()) this.deindent();
		if (n.f1.present()) this.print("} //EqualityExpression");
	}

	@Override
	public void visit(Literal n) {
		this.print("Literal{");
		this.indent();
		super.visit(n);
		this.deindent();
		this.print("} //Literal");
	}

	@Override
	public void visit(MultiplicativeExpression n) {
		if (n.f1.present()) this.print("MultiplicativeExpression{ ");
		if (n.f1.present()) this.indent();
		super.visit(n);
		if (n.f1.present()) this.deindent();
		if (n.f1.present()) this.print("} //MultiplicativeExpression");
	}

	@Override
	public void visit(Reference n) {
		this.print("Reference{");
		this.indent();
		super.visit(n);
		this.deindent();
		this.print("} //Reference");
	}

	@Override
	public void visit(NodeChoice n) {
		super.visit(n);
	}

	@Override
	public void visit(NodeList n) {
		super.visit(n);
	}

	@Override
	public void visit(NodeListOptional n) {
		super.visit(n);
	}

	@Override
	public void visit(NodeOptional n) {
		super.visit(n);
	}

	@Override
	public void visit(NodeSequence n) {
		super.visit(n);
	}

	@Override
	public void visit(NodeToken n) {
		this.print("TOKEN: \""+n.tokenImage+"\"");
		super.visit(n);
	}

	@Override
	public void visit(PrimaryExpression n) {
//		this.print("PrimaryExpression{");
//		this.indent();
		super.visit(n);
//		this.deindent();
//		this.print("} //PrimaryExpression");
	}

	@Override
	public void visit(RelationalExpression n) {
		if (n.f1.present()) this.print("RelationalExpression{ ");
		if (n.f1.present()) this.indent();
		super.visit(n);
		if (n.f1.present()) this.deindent();
		if (n.f1.present()) this.print("} //RelationalExpression");
	}

	@Override
	public void visit(UnaryExpression n) {
//		this.print("UnaryExpression{ ");
//		this.indent();
		super.visit(n);
//		this.deindent();
//		this.print("} //UnaryExpression");
	}

	@Override
	public void visit(UnaryExpressionNotPlusMinus n) {
//		this.print("UnaryExpressionNotPlusMinus{ ");
//		this.indent();
		super.visit(n);
//		this.deindent();
//		this.print("} //UnaryExpressionNotPlusMinus");
	}
	
	
	
}
