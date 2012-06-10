package org.mentalsmash.whadl.model.patterns;

import java.util.Iterator;
import java.util.Map;

import org.mentalsmash.whadl.validation.EntityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternMatcher {

	private static final Logger log = LoggerFactory
			.getLogger(PatternMatcher.class);

	private int finalCost;
	private final EntityContext context;
	private int matchingMaxTries = 5;

	public PatternMatcher(EntityContext context) {
		this.context = context;
	}

	public PatternMatcher(EntityContext context, int matchingMaxTries) {
		this(context);
		this.matchingMaxTries = matchingMaxTries;
	}

	private TotalCostPatternVisitor visitor;

	public TotalCostPatternVisitor getVisitor() {
		return this.visitor;
	}

	public Pattern match(Pattern p, Map<String, Integer> elements)
			throws NoMatchException, PartialMatchException {

		Pattern bestResult = null;
		int bestResultSize = Integer.MAX_VALUE;
		int bestResultRemainingCost = Integer.MAX_VALUE;

		int maxTries = this.matchingMaxTries;
		int tries = 0;
		
		log.debug("MATCHING: "+elements+" WITH: "+p);

		p.startEvaluation();
		while ((tries++) < maxTries) {
			try {
				visitor = new TotalCostPatternVisitor(context);
				Pattern result = p.match(elements, visitor);

				Map<Pattern, Integer> resMap = visitor.getMatched();
				StringBuilder str = new StringBuilder();
				Iterator<Pattern> it = resMap.keySet().iterator();
				while (it.hasNext()) {
					Pattern key = it.next();
					str.append("<" + key + "," + resMap.get(key) + ">");
					if (it.hasNext()) {
						str.append(",");
					}
				}
				log.debug("Matched with: [" + str + "]");

				if (result == null) {
					log.debug("Returning Best Result: result=" + null);
					p.stopEvaluation();
					this.finalCost += visitor.getTotal();
					if (tries > 1) {
						log.debug("Tried " + tries + " times before matching");
					}
					return result;
				}

				int resultSize = result.toMap().getContentSize();
				int resultCost = visitor.getTotal();

				if (bestResult == null
						|| resultSize < bestResultSize
						|| ((resultSize == bestResultSize) && (resultCost < bestResultRemainingCost))) {
					bestResult = result;
					bestResultSize = resultSize;
					bestResultRemainingCost = resultCost;
					log.debug("Setting Best Result to: result=" + bestResult
							+ " - size=" + bestResultSize + ", cost="
							+ bestResultRemainingCost);
				} else {
					log.debug("NOT CHOSEN: " + result + " SIZE:" + resultSize
							+ ", COST:" + resultCost + " -- CURRENT: "
							+ bestResult + ", SIZE:" + bestResultSize
							+ ", COST:" + bestResultRemainingCost);
				}

				if (tries >= maxTries || bestResultSize == 0) {
					p.stopEvaluation();
					log.debug("Returning Best Result: result=" + bestResult
							+ " - size=" + bestResultSize + ", cost="
							+ bestResultRemainingCost);
					this.finalCost = bestResultRemainingCost;
					if (tries > 1) {
						log.debug("Tried " + tries + " times before matching");
					}
					return bestResult;
				}

			} catch (NoMatchException ex) {
				p.stopEvaluation();
				p.startEvaluation();
			} catch (PartialMatchException ex) {
				if (!p.isDeterministic()) {
					p.stopEvaluation();
					p.startEvaluation();
					// log.error(p + " has no alternatives!");
					// throw ex;
				}
			}
		}

		p.stopEvaluation();

		if (bestResult != null) {
			log.debug("** Returning Best Result: result=" + bestResult + " - size="
					+ bestResultSize + ", cost=" + bestResultRemainingCost);
			this.finalCost = bestResultRemainingCost;
			if (tries > 1) {
				log.debug("Tried " + tries + " times before matching");
			}
			return bestResult;
		}

		log.error("Tried " + tries + " times before giving up");
		throw new NoMatchException(p, elements);
	}

	public int getFinalCost() {
		return finalCost;
	}

}
