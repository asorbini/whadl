package org.mentalsmash.whadl.parser.visitor.ast;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.model.patterns.SinglePattern;
import org.mentalsmash.whadl.parser.nodes.ArmyBuildDefinition;
import org.mentalsmash.whadl.parser.nodes.NodeSequence;
import org.mentalsmash.whadl.parser.nodes.NodeToken;
import org.mentalsmash.whadl.parser.nodes.UnitInstanceDefinition;
import org.mentalsmash.whadl.parser.visitor.DepthFirstRetVisitor;

public class ArmyInstanceGenerator extends DepthFirstRetVisitor<ArmyInstance> {

	private ArmyInstance inst;

	@Override
	public ArmyInstance visit(ArmyBuildDefinition n) {
		String armyName = (n.f2.present() ? ((NodeToken) ((NodeSequence) n.f2.node)
				.elementAt(1)).tokenImage
				: null);

		PatternGenerator pg = new PatternGenerator();

		Pattern p = pg.visit(n.f1);

		this.inst = new ArmyInstance(armyName);

		if (!(p instanceof SinglePattern)) {
			throw new WhadlRuntimeException("Multiple types are not supported "
					+ "for build definitions, as of now : found=" + p+", build="+this.inst);
		}

		this.inst.setTypeName(p.getName());

		this.visit(n.f4);

		return this.inst;
	}

	// @Override
	// public ArmyInstance visit(ArmyBuildEntries n) {
	// ExpressionGenerator nameVis = new ExpressionGenerator();
	// String type = nameVis.visit(n.f1).toString();
	//
	// this.inst.setTypeName(type);
	//
	// this.visit(n.f3);
	//
	// return this.inst;
	// }

	@Override
	public ArmyInstance visit(UnitInstanceDefinition n) {
		UnitInstanceGenerator parser = new UnitInstanceGenerator();

		UnitInstance unit = parser.visit(n);

		this.inst.addUnit(unit);

		return this.inst;
	}

}
