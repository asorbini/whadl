package org.mentalsmash.whadl.descriptions;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Map;

import org.mentalsmash.whadl.Whadl;
import org.mentalsmash.whadl.descriptions.parser.DDLParser;
import org.mentalsmash.whadl.descriptions.parser.visitor.ast.DescriptionsASTGenerator;
import org.mentalsmash.whadl.model.Army;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		FileInputStream in = new FileInputStream("descriptions/test.ddl");
		DDLParser p = new DDLParser(in);
		DescriptionsASTGenerator g = new DescriptionsASTGenerator();
		Collection<Description> descriptions = g.visit(p.Descriptions());

		for (Description d : descriptions) {
			System.out.println(d);
		}

		Whadl w = new Whadl("data");
		Army a = w.getArmy("SpaceMarinesRevised");
		DescriptionsManager dm = new LocalDescriptionsManager("Whadl",
				new File("descriptions"));
		
		dm.loadDescriptions(a);
		
		printDescriptions(a,a.getDescriptions());
		printDescriptions(a.getConditions(),a.getConditions().getDescriptions());
		
		System.out.println(a.getReference()+".conditions label is: "+a.getConditions().getLabel());
	}
	
	public static void printDescriptions(Object o, Map<String, String> descriptions) {
		System.out.println("DESCRIPTIONS FOR "+o);
		for (String locale : descriptions.keySet()) {
			System.out.println(locale+" ->\n"+descriptions.get(locale));
			System.out.println();
		}
	}

}
