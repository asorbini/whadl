package org.mentalsmash.whadl.model.patterns;

import java.util.HashMap;
import java.util.Map;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.validation.EntityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TotalCostPatternVisitor implements PatternMatchObserver {

	private static final Logger log = LoggerFactory
			.getLogger(TotalCostPatternVisitor.class);

	private final EntityContext context;
	private int total = 0;

	private final Map<Pattern, Integer> matchingPatterns = new HashMap<Pattern, Integer>();

	public Map<Pattern, Integer> getMatched() {
		return matchingPatterns;
	}

	public TotalCostPatternVisitor(EntityContext context) {
		this.context = context;
	}

	@Override
	public void match(Pattern rule, Map<String, Integer> entities,
			Pattern result) {

		if (rule instanceof SinglePattern) {
			int cost = 0;

			String entityStr = rule.getName();
			Integer q = entities.get(entityStr);

			if (q == null) {
				cost += 0;
			} else {

				for (int i = 0; i < q; i++) {
					Expression costExpr = rule.getCostExpression();
					Object res = costExpr.evaluate(context);
					if (res == null) {
						log.error("Null result from " + costExpr);
						throw new WhadlRuntimeException(
								"Expression returned a null result instead of an Integer: "
										+ costExpr);
					}
					cost += (Integer) res;
				}
			}

			log.trace("MATCHING COST: " + rule + " -> " + cost);

			total += cost;

			this.matchingPatterns.put(rule, cost);
		}

	}

	@Override
	public void none(Pattern rule, Map<String, Integer> entities) {

	}

	@Override
	public void partial(Pattern rule, Map<String, Integer> entities,
			Map<String, Integer> unmatched, Pattern result) {

		if (rule instanceof SinglePattern) {
			int cost = 0;
			String entityStr = rule.getName();
			Integer q = entities.get(entityStr);

			if (unmatched.containsKey(entityStr)) {
				q = q - unmatched.get(entityStr);
			}

			for (int i = 0; i < q; i++) {
				Expression costExpr = rule.getCostExpression();
				Object res = costExpr.evaluate(context);
				if (res == null) {
					log.error("Null result from " + costExpr);
					throw new WhadlRuntimeException(
							"Expression returned a null result instead of an Integer: "
									+ costExpr);
				}
				cost += (Integer) res;
			}

			log.trace("MATCHING COST: " + rule + " -> " + cost);
			total += cost;

			this.matchingPatterns.put(rule, cost);
		}

	}

	public int getTotal() {
		return this.total;
	}

}
