package org.mentalsmash.whadl.parser.visitor.ast;

import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.Equipment;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.patterns.EmptyPattern;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.parser.nodes.*;
import org.mentalsmash.whadl.parser.visitor.DepthFirstRetVisitor;

public class ArmyGenerator extends DepthFirstRetVisitor<Army> {

	@Override
	public Army visit(ArmyDefinition n) {

		String armyName = n.f1.tokenImage;
		Army a = new Army(armyName);

		if (n.f2.present()) {
			NodeSequence seqNode = (NodeSequence) n.f2.node;

			for (INode node : seqNode.nodes) {
				if (!(node instanceof NodeToken)) {
					NodeList armyEntries = (NodeList) node;

					for (INode aNode : armyEntries.nodes) {
						ArmyEntry entry = (ArmyEntry) aNode;

						switch (entry.f0.which) {
						// case 0: { // equipment definition
						// EquipmentASTGenerator equipVisitor = new
						// EquipmentASTGenerator();
						// Equipment eq = equipVisitor
						// .visit((EquipmentDefinition) entry.f0.choice);
						// a.addEquipment(eq);
						// break;
						// }
						case 0: { // unit definition
							UnitGenerator unitVisitor = new UnitGenerator();
							Unit u = unitVisitor
									.visit((UnitDefinition) entry.f0.choice);
							a.addUnit(u);
							break;
						}
						case 1: { // army conditions
							ExpressionGenerator expVisitor = new ExpressionGenerator();
							Conditions condNode = (Conditions) entry.f0.choice;
							Expression exp = expVisitor.parseAST(condNode.f2);
							a.setConditions(exp);
							

							// System.err.println(a.getName()+" (UNTOUCH): "+expVisitor.simpleParse(condNode.f2));
							// System.err.println(a.getName()+": "+exp);
							
							break;
						}
						default: {
							PatternGenerator patParser = new PatternGenerator();
							Extends extNode = (Extends) entry.f0.choice;
							Pattern supPattern = patParser.visit(extNode.f1);
							a.setSuperPattern(supPattern);
							break;
						}
						}
					}

				}
			}

		}

		return a;
	}

}
