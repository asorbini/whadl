package org.mentalsmash.whadl.parser.visitor.ast;

import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.parser.nodes.Conditions;
import org.mentalsmash.whadl.parser.nodes.Extends;
import org.mentalsmash.whadl.parser.nodes.INode;
import org.mentalsmash.whadl.parser.nodes.NodeChoice;
import org.mentalsmash.whadl.parser.nodes.UnitBaseCostEntry;
import org.mentalsmash.whadl.parser.nodes.UnitCompositionEntry;
import org.mentalsmash.whadl.parser.nodes.UnitDefinition;
import org.mentalsmash.whadl.parser.nodes.UnitLinkedUnitsEntry;
import org.mentalsmash.whadl.parser.nodes.UnitMemberDefEntry;
import org.mentalsmash.whadl.parser.nodes.UnitMemberEntry;
import org.mentalsmash.whadl.parser.nodes.UnitSlotEntry;
import org.mentalsmash.whadl.parser.nodes.UnitSpecialsEntry;
import org.mentalsmash.whadl.parser.nodes.UnitUpgradesEntry;
import org.mentalsmash.whadl.parser.visitor.DepthFirstRetVisitor;

public class UnitGenerator extends DepthFirstRetVisitor<Unit> {

	private Unit inst;

	@Override
	public Unit visit(UnitDefinition n) {
		String name = n.f1.tokenImage;
		this.inst = new Unit(name);

		super.visit(n);

		return this.inst;
	}

	@Override
	public Unit visit(Conditions n) {
		ExpressionGenerator condVis = new ExpressionGenerator();
		Expression conditions = condVis.parseAST(n.f2);
		this.inst.setConditions(conditions);
		return this.inst;
	}

	@Override
	public Unit visit(Extends n) {
		PatternGenerator patParser = new PatternGenerator();
		Pattern superPattern = patParser.visit(n.f1);
		this.inst.setSuperPattern(superPattern);
		return this.inst;
	}

	@Override
	public Unit visit(UnitBaseCostEntry n) {
		ExpressionGenerator visitor = new ExpressionGenerator();
		Expression cost = visitor.parseAST(n.f1);
		this.inst.setCostExpression(cost);
		return this.inst;
	}

	@Override
	public Unit visit(UnitCompositionEntry n) {
		PatternGenerator refParser = new PatternGenerator();
		Pattern defComp = refParser.visit(n.f1);
		this.inst.setCompositionPattern(defComp);
		return this.inst;
	}

	@Override
	public Unit visit(UnitLinkedUnitsEntry n) {
		PatternGenerator patParser = new PatternGenerator();
		Pattern linkedUnitsPattern = patParser.visit(n.f1);
		this.inst.setLinkedUnitsPattern(linkedUnitsPattern);
		return this.inst;
	}

	@Override
	public Unit visit(UnitMemberEntry n) {
		// System.err.println("UnitMemberEntry for "+n.f1.f0.tokenImage);
		UnitMemberGenerator membVisitor = new UnitMemberGenerator();
		UnitMember memb = membVisitor.visit(n, null);
		this.inst.addMember(memb);
		return this.inst;
	}

	@Override
	public Unit visit(UnitSlotEntry n) {
		Pattern slots = null;
		PatternGenerator refParser = new PatternGenerator();
		slots = refParser.visit(n.f1);
		this.inst.setSlotsPattern(slots);
		return this.inst;
	}

	@Override
	public Unit visit(UnitSpecialsEntry n) {
		if (n.f2.present()) {

			for (INode uNode : n.f2.nodes) {
				NodeChoice choice = (NodeChoice) uNode;
				if (choice.which == 0) {
					UnitDefinition unitNode = (UnitDefinition) choice.choice;
					UnitGenerator unitParser = new UnitGenerator();
					Unit un = unitParser.visit(unitNode);
					this.inst.addModifiedUnit(un);
				} else {
					ExpressionGenerator expVisitor = new ExpressionGenerator();
					Conditions condNode = (Conditions) choice.choice;
					Expression armyModConditions = expVisitor
							.parseAST(condNode.f2);
					this.inst.setArmyModifiedConditions(armyModConditions);
				}
			}
		}

		return this.inst;
	}

	@Override
	public Unit visit(UnitUpgradesEntry n) {
		PatternGenerator patternParser = new PatternGenerator();
		Pattern upgrades = patternParser.visit(n.f1);
		this.inst.setUpgradesPattern(upgrades);
		return this.inst;
	}

}
