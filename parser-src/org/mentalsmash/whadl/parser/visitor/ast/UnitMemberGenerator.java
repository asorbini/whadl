package org.mentalsmash.whadl.parser.visitor.ast;

import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.parser.nodes.*;
import org.mentalsmash.whadl.parser.visitor.DepthFirstRetArguVisitor;

public class UnitMemberGenerator extends
		DepthFirstRetArguVisitor<UnitMember, UnitMember> {

	@Override
	public UnitMember visit(UnitMemberEntry n, UnitMember m) {
		String name = n.f1.tokenImage;
		UnitMember mem = new UnitMember(name);

		if (n.f2.which == 0) {
			this.visit((NodeSequence) n.f2.choice, mem);
		}

		return mem;
	}

	@Override
	public UnitMember visit(MemberEquipmentEntry n, UnitMember m) {
		PatternGenerator parser = new PatternGenerator();
		org.mentalsmash.whadl.model.patterns.Pattern e = parser.visit(n.f1);
		m.setEquipmentPattern(e);
		return m;
	}

	
	@Override
	public UnitMember visit(Conditions n, UnitMember m){
		ExpressionGenerator vis = new ExpressionGenerator();
		Expression exp = vis.parseAST(n.f2);
		m.setConditions(exp);
		return m;
	}
	
	@Override
	public UnitMember visit(Extends n, UnitMember m){
		PatternGenerator refParser = new PatternGenerator();
		org.mentalsmash.whadl.model.patterns.Pattern ref = refParser.visit(n.f1);
		m.setSuperPattern(ref);
		return m;
	}

}
