package org.mentalsmash.whadl.model.patterns;

import java.util.HashMap;
import java.util.Map;

import org.mentalsmash.whadl.model.expressions.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptionalPattern extends BasePattern {

	private static final Logger log = LoggerFactory
			.getLogger(OptionalPattern.class);

	private Pattern optionalPattern = Pattern.DEFAULT_PATTERN;
	private final String name = "OPTIONAL";
	private Expression costExpression;

	public OptionalPattern(Pattern optPattern) {
		this.optionalPattern = optPattern;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getQuantity() {
		return 0;
	}

	@Override
	public void pack() {
		this.optionalPattern.pack();

		if (this.optionalPattern instanceof OptionalPattern) {
			this.optionalPattern = ((OptionalPattern) this.optionalPattern)
					.getOptionalPattern();
		}
	}

	public Pattern getOptionalPattern() {
		return this.optionalPattern;
	}

	@Override
	public PatternContentMap toMap() {
		return new PatternContentMap(new HashMap<String, Integer>());
	}

	@Override
	public Expression getCostExpression() {
		return this.costExpression;
	}

	public void setCostExpression(Expression costExpression) {
		this.costExpression = costExpression;
	}

	private boolean evaluating = false;

	@Override
	public Pattern match(Map<String, Integer> entities)
			throws NoMatchException, PartialMatchException {
		return this.match(entities, new FakePatternVisitor());
	}

	@Override
	public Pattern match(Map<String, Integer> entities,
			PatternMatchObserver visitor) throws NoMatchException,
			PartialMatchException {

		log.debug("Matching " + entities + " with " + this);

		try {
			Pattern result = this.optionalPattern.match(entities, visitor);
			log.debug("All entities matched, result:" + result);
			// visitor.match(this, entities, result);
			return new OptionalPattern(result);
		} catch (NoMatchException ex) {
			// visitor.none(this, entities);
			log.debug("None of " + entities + " matched");
			throw new NoMatchException(this, entities);
		} catch (PartialMatchException ex) {
			Pattern optResult = ex.getResult();
			Map<String, Integer> unmatched = ex.getUnmatched();

			OptionalPattern result = new OptionalPattern(optResult);

			log.debug("Partial match, unmatched:" + unmatched + " , result: "
					+ result);
			// visitor.partial(this, entities, unmatched, result);
			throw new PartialMatchException(this, entities, result, unmatched,
					visitor);
		}
	}

	@Override
	public void startEvaluation() {
		log.trace("Starting evaluation...");
		this.optionalPattern.startEvaluation();
		this.evaluating = true;
	}

	@Override
	public void stopEvaluation() {
		log.trace("Stopping evaluation...");
		this.optionalPattern.stopEvaluation();
		this.evaluating = false;
	}

	@Override
	public boolean isEvaluating() {
		return this.evaluating;
	}

	@Override
	public String toString() {
		return "[ " + this.optionalPattern + " ]";
	}

	@Override
	public boolean isDeterministic() {
		return this.optionalPattern.isDeterministic();
	}

	@Override
	public boolean isDefault() {
		return false;
	}

	@Override
	public Pattern extract(String key) {
		if (this.optionalPattern != null)
			return this.optionalPattern.extract(key);

		return null;
	}

}
