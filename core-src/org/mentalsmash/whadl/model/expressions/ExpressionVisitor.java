package org.mentalsmash.whadl.model.expressions;

public interface ExpressionVisitor {

	public void visit(Expression exp);
	public void visit(ArithmeticExpression exp);
	public void visit(CollectionOperationExpression exp);
	public void visit(ConditionalExpression exp);
	public void visit(EntityReferenceExpression exp);
	public void visit(EqualityExpression exp);
	public void visit(ExpressionsCollection exp);
	public void visit(InstanceReferenceExpression exp);
	public void visit(LiteralExpression exp);
	public void visit(LogicalExpression exp);
	public void visit(MemberSelectorExpression exp);
	public void visit(NotExpression exp);
	public void visit(RelationalExpression exp);
	public void visit(BinaryExpression exp);
	public void visit(UnaryArithmeticExpression exp);
	
}
