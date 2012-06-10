package org.mentalsmash.whadl.model.patterns;

import java.util.HashMap;
import java.util.Map;

import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;

public class EmptyPattern extends BasePattern {
	
	public static final Pattern DEFAULT_PATTERN = new EmptyPattern();

	@Override
	public Expression getCostExpression() {
		return new LiteralExpression(0);
	}

	@Override
	public String getName() {
		return "NO-PATTERN";
	}

	@Override
	public int getQuantity() {
		return 0;
	}

	@Override
	public boolean isDeterministic() {
		return true;
	}

	private boolean evaluating = false;
	
	@Override
	public boolean isEvaluating() {
		return this.evaluating;
	}

	@Override
	public Pattern match(Map<String, Integer> entities)
			throws NoMatchException, PartialMatchException {
		if (entities.size() > 0) {
			throw new NoMatchException(this, entities);
		} else {
			return new EmptyPattern();
		}
	}

	@Override
	public Pattern match(Map<String, Integer> entities,
			PatternMatchObserver visitor) throws NoMatchException,
			PartialMatchException {
		if (entities.size() > 0) {
			throw new NoMatchException(this, entities);
		} else {
			return new EmptyPattern();
		}
	}

	@Override
	public void pack() {

	}

	@Override
	public void startEvaluation() {
		this.evaluating = true;

	}

	@Override
	public void stopEvaluation() {
		this.evaluating = false;

	}

	@Override
	public PatternContentMap toMap() {
		return new PatternContentMap(new HashMap<String, Integer>());
	}

	@Override
	public String toString() {
		return "NONE";
	}

	@Override
	public boolean isDefault() {
		return this.equals(Pattern.DEFAULT_PATTERN);
	}

	@Override
	public Pattern extract(String key) {
		return null;
	}

	
}
