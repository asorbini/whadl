package org.mentalsmash.whadl.parser.visitor.ast;

import org.mentalsmash.whadl.model.Equipment;
import org.mentalsmash.whadl.model.EquipmentGroup;
import org.mentalsmash.whadl.model.SingleEquipment;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.parser.nodes.*;
import org.mentalsmash.whadl.parser.visitor.DepthFirstRetVisitor;

/*
 void EquipmentDefinition() :
 {

 }
 {
 < EQUIPMENT > < IDENTIFIER > EquipmentCostDefinition() [ Conditions() ] < SEMICOLON >
 }

 void EquipmentCostDefinition() :
 {
 }
 {
 Expression() [ < OR > ( Expression() | < NONE > ) ]
 }
 */

public class EquipmentGenerator extends DepthFirstRetVisitor<Equipment> {

	// public Equipment visit(EquipmentDefinition n) {
	// String name = n.f1.tokenImage;
	// ExpressionGenerator visitor = new ExpressionGenerator();
	// Expression costExp = visitor.parseAST(n.f2.f0);
	// Expression singleWoundCost = null;
	//
	// if (n.f2.f1.present()) {
	// NodeChoice choice = (NodeChoice) ((NodeSequence) n.f2.f1.node)
	// .elementAt(1);
	// if (choice.which == 0) {
	// singleWoundCost = visitor
	// .parseAST((org.mentalsmash.whadl.parser.nodes.Expression) choice.choice);
	// }
	// } else {
	// singleWoundCost = costExp;
	// }
	//
	// Expression conditions = new LiteralExpression(true);
	//
	// if (n.f3.present()) {
	// Conditions condNode = (Conditions) n.f3.node;
	// ExpressionGenerator vis = new ExpressionGenerator();
	// conditions = vis.parseAST(condNode.f2);
	// }
	//
	// SingleEquipment e = new SingleEquipment(name, conditions);
	// e.setBaseCost(costExp);
	// e.setSingleWoundCost(singleWoundCost);
	//
	// return e;
	// }

}
