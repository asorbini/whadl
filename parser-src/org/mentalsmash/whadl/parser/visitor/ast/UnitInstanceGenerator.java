package org.mentalsmash.whadl.parser.visitor.ast;

import java.util.Map;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.UnitMemberInstance;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.model.patterns.SinglePattern;
import org.mentalsmash.whadl.parser.nodes.NodeSequence;
import org.mentalsmash.whadl.parser.nodes.NodeToken;
import org.mentalsmash.whadl.parser.nodes.UnitInstanceCompositionEntry;
import org.mentalsmash.whadl.parser.nodes.UnitInstanceDefinition;
import org.mentalsmash.whadl.parser.nodes.UnitInstanceLinkedEntry;
import org.mentalsmash.whadl.parser.nodes.UnitInstanceSlotEntry;
import org.mentalsmash.whadl.parser.nodes.UnitInstanceTypeEntry;
import org.mentalsmash.whadl.parser.nodes.UnitInstanceUpgradesEntry;
import org.mentalsmash.whadl.parser.nodes.UnitMemberInstanceEntry;
import org.mentalsmash.whadl.parser.visitor.DepthFirstRetVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitInstanceGenerator extends
		DepthFirstRetVisitor<UnitInstance> {
	private UnitInstance inst;

	private final static Logger log = LoggerFactory
			.getLogger(UnitInstanceGenerator.class);

	@Override
	public UnitInstance visit(UnitInstanceDefinition n) {
		if (n.f2.present()) {
			this.inst = new UnitInstance(
					((NodeToken) ((NodeSequence) n.f2.node).elementAt(1)).tokenImage);
		} else {
			this.inst = new UnitInstance();
		}
		
		PatternGenerator pg = new PatternGenerator();

		Pattern p = pg.visit(n.f1);

		if (!(p instanceof SinglePattern)) {
			throw new WhadlRuntimeException("Multiple types are not supported "
					+ "for unit definitions, as of now : found=" + p+", unit="+this.inst);
		}

		this.inst.setTypeName(p.getName());
		

		log.debug("CREATED UNIT INSTANCE: " + this.inst);

		this.visit(n.f4);

		return this.inst;
	}

	@Override
	public UnitInstance visit(UnitInstanceLinkedEntry n) {
		PatternGenerator patParser = new PatternGenerator();
		Pattern linkedUnitsPattern = patParser.visit(n.f1);
		this.inst.setLinkedUnitsPattern(linkedUnitsPattern);
		return this.inst;
	}

	@Override
	public UnitInstance visit(UnitInstanceSlotEntry n) {
		PatternGenerator patParser = new PatternGenerator();
		Pattern slotPattern = patParser.visit(n.f1);
		this.inst.setSlotPattern(slotPattern);
		return this.inst;
	}

	@Override
	public UnitInstance visit(UnitInstanceTypeEntry n) {
		ExpressionGenerator expParser = new ExpressionGenerator();
		Expression name = expParser.visit(n.f1);
		this.inst.setTypeName(name.toString());
		return this.inst;
	}

	@Override
	public UnitInstance visit(UnitInstanceCompositionEntry n) {
		PatternGenerator patParser = new PatternGenerator();
		Pattern compositionPattern = patParser.visit(n.f1);
		this.inst.setCompositionPattern(compositionPattern);
		return this.inst;
	}

	@Override
	public UnitInstance visit(UnitInstanceUpgradesEntry n) {
		PatternGenerator patParser = new PatternGenerator();
		Pattern upgradesPattern = patParser.visit(n.f1);
		this.inst.setUpgradesPattern(upgradesPattern);
		return this.inst;
	}

	@Override
	public UnitInstance visit(UnitMemberInstanceEntry n) {

		PatternGenerator patParser = new PatternGenerator();
		Pattern compositionPattern = patParser.visit(n.f1);
		String name = null;
		if (n.f2.present()) {
			name = ((NodeToken) ((NodeSequence) n.f2.node).elementAt(1)).tokenImage;
		}

		Pattern equipmentPattern = patParser.visit(n.f4);

		if (compositionPattern.getQuantity() > 1) {
			Map<String, Integer> values = compositionPattern.toMap();
			for (String key : values.keySet()) {
				Integer q = values.get(key);

				for (int i = 0; i < q; i++) {
					UnitMemberInstance mem = new UnitMemberInstance(key);

					if (name != null) {
						mem.setName(name);
					}

					mem.setEquipmentPattern(equipmentPattern);

					// log.debug("CREATED UNIT MEMBER: "+mem);

					this.inst.addMember(mem);
				}
			}
		} else {
			UnitMemberInstance mem = new UnitMemberInstance(compositionPattern
					.getName());

			if (name != null) {
				mem.setName(name);
			}

			mem.setEquipmentPattern(equipmentPattern);

			// log.debug("CREATED UNIT MEMBER: " + mem);

			this.inst.addMember(mem);
		}

		return this.inst;
	}

}
