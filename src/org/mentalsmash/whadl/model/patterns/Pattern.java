package org.mentalsmash.whadl.model.patterns;

import java.util.Map;

import org.mentalsmash.whadl.model.expressions.Expression;

public interface Pattern {
	
	public static final Pattern DEFAULT_PATTERN = new EmptyPattern();
	
	public boolean isDefault();
	
	public static final int QUANTITY_ANY = 50;
	public static final String NULLREF = "null";

	public String getName();

	public int getQuantity();

	public Expression getCostExpression();

	public void startEvaluation();

	public boolean isEvaluating();

	public void stopEvaluation();
	
	public boolean isDeterministic();

	public Pattern match(Map<String, Integer> entities)
			throws NoMatchException, PartialMatchException;

	public Pattern match(Map<String, Integer> entities, PatternMatchObserver visitor)
			throws NoMatchException, PartialMatchException;

	public void pack();

	public PatternContentMap toMap();
	
	public Pattern extract(String key);
	
	public Map<String, String> getDescriptions();
	
	public void getDescription(String label, String description);
}
