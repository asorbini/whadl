package org.mentalsmash.whadl.descriptions.parser.visitor.ast;

import java.util.ArrayList;
import java.util.Collection;

import org.mentalsmash.whadl.descriptions.BaseDescription;
import org.mentalsmash.whadl.descriptions.Description;
import org.mentalsmash.whadl.descriptions.parser.nodes.EntityDescription;
import org.mentalsmash.whadl.descriptions.parser.nodes.EntityId;
import org.mentalsmash.whadl.descriptions.parser.nodes.INode;
import org.mentalsmash.whadl.descriptions.parser.nodes.NodeChoice;
import org.mentalsmash.whadl.descriptions.parser.nodes.NodeSequence;
import org.mentalsmash.whadl.descriptions.parser.nodes.NodeToken;
import org.mentalsmash.whadl.descriptions.parser.visitor.DepthFirstRetVisitor;

public class DescriptionsASTGenerator extends
		DepthFirstRetVisitor<Collection<Description>> {

	private final ArrayList<Description> descriptions = new ArrayList<Description>();

	@Override
	public Collection<Description> visit(
			org.mentalsmash.whadl.descriptions.parser.nodes.Descriptions n) {
		this.descriptions.clear();

		super.visit(n);

		return this.descriptions;
	}

	@Override
	public Collection<Description> visit(EntityDescription n) {
		String id = this.parseId(n.f0);
		String text = n.f1.tokenImage;
		text = text.substring(5,text.length()-5).replaceAll("\t", "");
		Description d = new BaseDescription(id, text);
		this.descriptions.add(d);

		return this.descriptions;
	}

	private String parseId(EntityId n) {
		StringBuilder result = new StringBuilder();

		result.append(n.f0.tokenImage);

		if (n.f1.present()) {
			for (INode node : n.f1.nodes) {
				NodeSequence seq = (NodeSequence) node;
				String connector = ((NodeToken) ((NodeChoice) seq.elementAt(0)).choice).tokenImage;
				result.append(connector);
				String ident = ((NodeToken) seq.elementAt(1)).tokenImage;
				result.append(ident);
			}
		}

		return result.toString();
	}
}
