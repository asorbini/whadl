package org.mentalsmash.whadl.parser;

import java.io.StringReader;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.parser.nodes.ArmyBuildDefinition;
import org.mentalsmash.whadl.parser.nodes.ArmyDefinition;
import org.mentalsmash.whadl.parser.nodes.UnitDefinition;
import org.mentalsmash.whadl.parser.nodes.UnitInstanceDefinition;
import org.mentalsmash.whadl.parser.visitor.ast.ArmyGenerator;
import org.mentalsmash.whadl.parser.visitor.ast.ArmyInstanceGenerator;
import org.mentalsmash.whadl.parser.visitor.ast.PatternGenerator;
import org.mentalsmash.whadl.parser.visitor.ast.ExpressionGenerator;
import org.mentalsmash.whadl.parser.visitor.ast.UnitGenerator;
import org.mentalsmash.whadl.parser.visitor.ast.UnitInstanceGenerator;

public class ParserUtils {
	public static Expression parseExpression(String expr) {
		StringReader reader = new StringReader(expr);
		WhadlParser parser = new WhadlParser(reader);
		try {
			org.mentalsmash.whadl.parser.nodes.Expression expNode = parser.Expression();
			Expression exp = new ExpressionGenerator().parseAST(expNode);
			return exp;
		} catch (ParseException e) {
			throw new WhadlRuntimeException(
					"Cannot parse Expression from string: " + expr,e);
		}
	}

	public static Pattern parsePattern(String pattern) {
		StringReader reader = new StringReader(pattern);
		WhadlParser parser = new WhadlParser(reader);
		try {
			org.mentalsmash.whadl.parser.nodes.Pattern patNode = parser.Pattern();
			Pattern p = new PatternGenerator().visit(patNode);
			return p;
		} catch (ParseException e) {
			throw new WhadlRuntimeException(
					"Cannot parse Pattern from string: " + pattern);
		}
	}
	
	public static Army parseArmy(String armyDef) {
		StringReader reader = new StringReader(armyDef);
		WhadlParser parser = new WhadlParser(reader);
		try {
			ArmyDefinition armyNode = parser.ArmyDefinition();
			Army a = new ArmyGenerator().visit(armyNode);
			return a;
		} catch (ParseException e) {
			throw new WhadlRuntimeException(
					"Cannot parse Army from string: " + armyDef);
		}
	}
	
	public static Unit parseUnit(String unitDef) {
		StringReader reader = new StringReader(unitDef);
		WhadlParser parser = new WhadlParser(reader);
		try {
			UnitDefinition unitNode = parser.UnitDefinition();
			Unit u = new UnitGenerator().visit(unitNode);
			return u;
		} catch (ParseException e) {
			throw new WhadlRuntimeException(
					"Cannot parse Unit from string: " + unitDef);
		}
	}
	
	public static ArmyInstance parseArmyInstance(String armyDef) {
		StringReader reader = new StringReader(armyDef);
		WhadlParser parser = new WhadlParser(reader);
		try {
			ArmyBuildDefinition armyNode = parser.ArmyBuildDefinition();
			ArmyInstance a = new ArmyInstanceGenerator().visit(armyNode);
			return a;
		} catch (ParseException e) {
			throw new WhadlRuntimeException(
					"Cannot parse ArmyInstance from string: " + armyDef);
		}
	}
	
	public static UnitInstance parseUnitInstance(String unitDef) {
		StringReader reader = new StringReader(unitDef);
		WhadlParser parser = new WhadlParser(reader);
		try {
			UnitInstanceDefinition unitNode = parser.UnitInstanceDefinition();
			UnitInstance u = new UnitInstanceGenerator().visit(unitNode);
			return u;
		} catch (ParseException e) {
			throw new WhadlRuntimeException(
					"Cannot parse UnitInstance from string: " + unitDef);
		}
	}
}
