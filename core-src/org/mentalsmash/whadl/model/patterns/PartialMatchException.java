package org.mentalsmash.whadl.model.patterns;

import java.util.Map;

public class PartialMatchException extends Exception {

	private static final long serialVersionUID = 7076911768741598500L;

	private final Pattern result;
	private final Map<String, Integer> unmatched;
	private final Pattern rule;
	private final Map<String, Integer> elements;
	private final PatternMatchObserver visitor;

	public PartialMatchException(Pattern rule, Map<String, Integer> elements,
			Pattern result, Map<String, Integer> unmatched, PatternMatchObserver visitor) {
		super("Partial matching of " + rule + " with " + elements
				+ " (result: " + result + ", unmatched: " + unmatched + ")");
		this.result = result;
		this.unmatched = unmatched;
		this.rule = rule;
		this.elements = elements;
		this.visitor = visitor;
	}

	public Pattern getResult() {
		return result;
	}

	public Map<String, Integer> getUnmatched() {
		return unmatched;
	}

	public Pattern getRule() {
		return rule;
	}

	public Map<String, Integer> getElements() {
		return elements;
	}

	public PatternMatchObserver getVisitor() {
		return visitor;
	}

}
