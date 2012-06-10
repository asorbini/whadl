package org.mentalsmash.whadl.model.patterns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConjunctEntityPatternSet extends MultiplePattern {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4801187990038513759L;

	@Override
	public String getName() {
		return "AND-SET";
	}

	@Override
	public int getQuantity() {
		int sum = 0;

		for (Pattern p : this) {
			sum += p.getQuantity();
		}

		return sum;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append("(");
		for (Iterator<Pattern> it = this.iterator(); it.hasNext();) {
			Pattern mem = it.next();
			str.append(mem);
			if (it.hasNext())
				str.append(", ");
		}
		str.append(")");

		return str.toString();
	}

	@Override
	public PatternContentMap toMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();

		for (Pattern er : this) {
			Map<String, Integer> subMap = er.toMap();
			for (String key : subMap.keySet()) {
				if (map.containsKey(key)) {
					map.put(key, map.get(key) + subMap.get(key));
				} else {
					map.put(key, subMap.get(key));
				}
			}

		}

		return new PatternContentMap(map);
	}

	@Override
	public void pack() {
		ConjunctEntityPatternSet packed = this.pack(this);
		this.clear();
		this.addAll(packed);
	}

	private ConjunctEntityPatternSet pack(ConjunctEntityPatternSet set) {
		ConjunctEntityPatternSet temp = new ConjunctEntityPatternSet();
		temp.addAll(set);

		for (Pattern ref : set) {
			if (ref instanceof ConjunctEntityPatternSet) {
				ConjunctEntityPatternSet andSet = (ConjunctEntityPatternSet) ref;
				temp.remove(andSet);

				for (Pattern p : andSet) {
					if (!(p instanceof ConjunctEntityPatternSet)) {
						p.pack();
						temp.add(p);
					} else {
						temp.addAll(this.pack((ConjunctEntityPatternSet) p));
					}
				}
			} else {
				ref.pack();
				temp.add(ref);
			}
		}

		return temp;
	}

	private static final Logger log = LoggerFactory
			.getLogger(ConjunctEntityPatternSet.class);

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

		return this.m(entities, visitor);

		// if (this.size() == 0) {
		// if (entities.size() != 0) {
		// // visitor.none(this, entities);
		// throw new NoMatchException(this, entities);
		// } else {
		// // visitor.match(this, entities, this);
		// log.debug("Matched empty rules set with empty elements");
		// return null;
		// }
		// }
		//
		// if (entities.size() == 0) {
		// // visitor.match(this, entities, this);
		// log.debug("Matched " + this + " with empty elements");
		// return this;
		// }
		//
		// Collection<Pattern> nondetPatterns = new ArrayList<Pattern>();
		// Collection<Pattern> optPatterns = new ArrayList<Pattern>();
		// Collection<Pattern> otherPattern = new HashSet<Pattern>();
		//
		// for (Pattern er : this) {
		// if (!er.isDeterministic()) {
		// nondetPatterns.add(er);
		// } else if (er instanceof OptionalPattern) {
		// optPatterns.add(er);
		// } else {
		// otherPattern.add(er);
		// }
		// }
		//
		// Map<String, Integer> elements = new HashMap<String,
		// Integer>(entities);
		//
		// ConjunctEntityPatternSet andSetResult = new
		// ConjunctEntityPatternSet();
		// andSetResult.addAll(this);
		//
		// boolean partialMatch = false;
		// boolean matchByOther = false;
		// boolean matchByOpt = false;
		// boolean matchByNonDet = false;
		//
		// log.debug("Trying deterministic, non-optional patterns first: "
		// + otherPattern + " with " + elements);
		//
		// for (Pattern rule : otherPattern) {
		// Map<String, Integer> unmatched = new HashMap<String, Integer>();
		//
		// Pattern result = this.matchSinglePattern(rule, elements, visitor,
		// unmatched);
		//
		// int totUnmatched = 0;
		// for (String key : unmatched.keySet()) {
		// totUnmatched += unmatched.get(key);
		// }
		//
		// int totEntities = 0;
		// for (String key : elements.keySet()) {
		// totEntities += elements.get(key);
		// }
		//
		// if (totUnmatched == 0) {
		// log.debug("RESULT: " + result + " - unmatched: "
		// + unmatched.size() + " " + unmatched);
		//
		// andSetResult.remove(rule);
		// if (result != null) {
		// andSetResult.add(result);
		// }
		// log.debug("All entities matched, result: " + andSetResult);
		// log.debug("Match by deterministic, non-optional patterns only");
		// return andSetResult;
		// } else if (totUnmatched > 0 && totUnmatched < totEntities) {
		// log.debug("RESULT: " + result + " - unmatched: "
		// + unmatched.size() + " " + unmatched);
		//
		// partialMatch = true;
		// matchByOther = true;
		// elements.clear();
		// elements.putAll(unmatched);
		// andSetResult.remove(rule);
		// if (result != null) {
		// andSetResult.add(result);
		// }
		//
		// log.debug("POST: " + andSetResult);
		//
		// } else {
		// log.debug("UNMATCHED: " + unmatched + "(" + unmatched.size()
		// + ") ENTITIES: " + elements + " (" + elements.size()
		// + ")");
		// }
		//
		// }
		//
		// log.debug("Trying deterministic, optional patterns: " + optPatterns
		// + " with " + elements);
		//
		// for (Pattern rule : optPatterns) {
		// Map<String, Integer> unmatched = new HashMap<String, Integer>();
		//
		// Pattern result = this.matchSinglePattern(rule, elements, visitor,
		// unmatched);
		//
		// if (unmatched.size() == 0) {
		// matchByOpt = true;
		//
		// log.debug("RESULT: " + result + " - unmatched: "
		// + unmatched.size() + " " + unmatched);
		//
		// andSetResult.remove(rule);
		// if (result != null) {
		// andSetResult.add(result);
		// }
		//
		// log.debug("All entities matched, result: " + andSetResult);
		// if (!matchByOther) {
		// log.debug("Matched by optional nodes only");
		// } else {
		// log.debug("Matched by deterministic, non-optional nodes");
		// log.debug("Matched by deterministic, optional nodes");
		// }
		//
		// return andSetResult;
		// } else if (unmatched.size() > 0
		// && (unmatched.size() < elements.size())) {
		//
		// log.debug("RESULT: " + result + " - unmatched: "
		// + unmatched.size() + " " + unmatched);
		//
		// partialMatch = true;
		// matchByOpt = true;
		// elements.clear();
		// elements.putAll(unmatched);
		// andSetResult.remove(rule);
		// if (result != null) {
		// andSetResult.add(result);
		// }
		//
		// log.debug("POST: " + andSetResult);
		// }
		//
		// }
		//
		// log.debug("Trying non-deterministic patterns: " + nondetPatterns
		// + " with " + elements);
		//
		// for (Pattern rule : nondetPatterns) {
		// Map<String, Integer> unmatched = new HashMap<String, Integer>();
		//
		// Pattern result = this.matchSinglePattern(rule, elements, visitor,
		// unmatched);
		//
		// if (unmatched.size() == 0) {
		// matchByNonDet = true;
		//
		// log.debug("RESULT: " + result + " - unmatched: "
		// + unmatched.size() + " " + unmatched);
		//
		// andSetResult.remove(rule);
		// if (result != null) {
		// andSetResult.add(result);
		// }
		// log.debug("All entities matched, result: " + andSetResult);
		// if (!matchByOther && !matchByOpt) {
		// log.debug("Matched by non-deterministic nodes only");
		// } else {
		// if (matchByOther) {
		// log
		// .debug("Matched by deterministic, non-optional nodes");
		// }
		//
		// if (matchByOpt) {
		// log.debug("Matched by deterministic, optional nodes");
		// }
		// }
		//
		// return andSetResult;
		// } else if (unmatched.size() > 0
		// && (unmatched.size() < elements.size())) {
		//
		// log.debug("RESULT: " + result + " - unmatched: "
		// + unmatched.size() + " " + unmatched);
		//
		// partialMatch = true;
		// matchByNonDet = true;
		// elements.clear();
		// elements.putAll(unmatched);
		// andSetResult.remove(rule);
		// if (result != null) {
		// andSetResult.add(result);
		// }
		//
		// log.debug("POST: " + andSetResult);
		// }
		//
		// }
		//
		// if (partialMatch) {
		// if (matchByOther) {
		// log.debug("Matched by deterministic, non-optional nodes");
		// }
		//
		// if (matchByOpt) {
		// log.debug("Matched by deterministic, optional nodes");
		// }
		//
		// if (matchByNonDet) {
		// log.debug("Matched by non-deterministic nodes");
		// }
		//
		// log.debug("Partial match, unmatched:" + entities.size() + " "
		// + entities + " , result: " + andSetResult);
		// throw new PartialMatchException(this, entities, andSetResult,
		// entities);
		// } else {
		// log.debug("None of " + entities + " matched.");
		// throw new NoMatchException(this, entities);
		// }
	}

	private Pattern m(Map<String, Integer> entities, PatternMatchObserver visitor)
			throws NoMatchException, PartialMatchException {

		log.debug("Matching " + entities + " with " + this);

		if (this.size() == 0) {
			if (entities.size() != 0) {
				throw new NoMatchException(this, entities);
			} else {
				log.debug("Matched empty rules set with empty elements");
				return null;
			}
		}

		if (entities.size() == 0) {
			log.debug("Matched " + this + " with empty elements");
			return this;
		}

		Collection<Pattern> nondetPatterns = new ArrayList<Pattern>();
		Collection<Pattern> optPatterns = new ArrayList<Pattern>();
		Collection<Pattern> otherPatterns = new HashSet<Pattern>();

		for (Pattern er : this) {
			if (!er.isDeterministic()) {
				nondetPatterns.add(er);
			} else if (er instanceof OptionalPattern) {
				optPatterns.add(er);
			} else {
				otherPatterns.add(er);
			}
		}

		Map<String, Integer> elements = new HashMap<String, Integer>(entities);

		ConjunctEntityPatternSet andSetResult = new ConjunctEntityPatternSet();

		Map<String, Integer> unmatched = new HashMap<String, Integer>();

		log.debug("TRYING DETERMINISTIC, NON-OPTIONAL, PATTERNS");

		andSetResult = this.matchWithPatterns(otherPatterns, elements, visitor,
				unmatched, this);

		log.debug("*** 1 RESULT: unmatched: " + unmatched + ", leftToMatch="
				+ andSetResult);
		//		
		//		

		if (unmatched.size() > 0) {

			log.debug("TRYING DETERMINISTIC, OPTIONAL, PATTERNS");

			elements = unmatched;
			unmatched = new HashMap<String, Integer>();
			andSetResult = this.matchWithPatterns(optPatterns, elements,
					visitor, unmatched, andSetResult);

			log.debug("*** 2 RESULT: unmatched: " + unmatched
					+ ", leftToMatch=" + andSetResult);

			if (unmatched.size() > 0) {

				log.debug("TRYING NON-DETERMINISTIC PATTERNS");

				elements = unmatched;
				unmatched = new HashMap<String, Integer>();
				andSetResult = this.matchWithPatterns(nondetPatterns, elements,
						visitor, unmatched, andSetResult);

				log.debug("*** 3 RESULT: unmatched: " + unmatched
						+ ", leftToMatch=" + andSetResult);

				if (unmatched.size() == elements.size()) {

					// let's try the whole thing again

					log.debug("TRYING ALL PATTERNS");

					andSetResult = this.matchWithPatterns(this, elements,
							visitor, unmatched, this);

					log.debug("*** 4 RESULT: unmatched: " + unmatched
							+ ", leftToMatch=" + andSetResult);

					if (unmatched.size() == elements.size()) {
						throw new NoMatchException(this, entities);
					} else if (unmatched.size() > 0) {
						throw new PartialMatchException(this, entities,
								andSetResult, unmatched, visitor);
					} else {
						return andSetResult;
					}

				} else if (unmatched.size() > 0) {
					throw new PartialMatchException(this, entities,
							andSetResult, unmatched, visitor);
				} else {
					return andSetResult;
				}

			} else {
				return andSetResult;
			}

		} else {
			return andSetResult;
		}

	}

	private ConjunctEntityPatternSet matchWithPatterns(
			Collection<Pattern> patterns, Map<String, Integer> elements,
			PatternMatchObserver visitor, Map<String, Integer> unmatched,
			ConjunctEntityPatternSet andSetResult) {

		ConjunctEntityPatternSet totResult = new ConjunctEntityPatternSet();
		totResult.addAll(andSetResult);

		Map<String, Integer> leftToMatch = new HashMap<String, Integer>(
				elements);

		for (Pattern rule : patterns) {
			Map<String, Integer> unmat = new HashMap<String, Integer>();

			Pattern result = this.matchSinglePattern(rule, leftToMatch,
					visitor, unmat);

			if (unmat.size() == 0) {

				// log.debug("RESULT: " + result + " - unmatched: "
				// + unmatched.size() + " " + unmat);

				totResult.remove(rule);

				if (result != null) {
					totResult.add(result);
				}
				// log.debug("All entities matched, result: " + totResult);

				return totResult;
			} else {

				// log.debug("RESULT: " + result + " - unmatched: " +
				// unmat.size()
				// + " " + unmat);

				leftToMatch.clear();
				leftToMatch.putAll(unmat);

				totResult.remove(rule);
				if (result != null) {
					totResult.add(result);
				}

			}

		}

		unmatched.putAll(leftToMatch);

		return totResult;
	}

	private Pattern matchSinglePattern(Pattern rule,
			Map<String, Integer> entities, PatternMatchObserver visitor,
			Map<String, Integer> unmatched) {
		try {
			log.debug("Matching " + entities + " with sub-rule " + rule);
			Pattern result = rule.match(entities, visitor);
			log.debug("All entities matched by sub-rule, sub-rule result: "
					+ result);
			return result;
		} catch (NoMatchException ex) {
			log.debug("No entity matched by sub-rule.");
			unmatched.putAll(entities);
			return rule;
		} catch (PartialMatchException ex) {
			unmatched.putAll(ex.getUnmatched());
			Pattern result = ex.getResult();
			log.debug("Partial match by sub-rule, unmatched: " + unmatched
					+ " , sub-rule result: " + result);
			return result;
		}
	}

	@Override
	public boolean isDeterministic() {
		boolean result = true;

		for (Pattern p : this) {
			result = result && p.isDeterministic();

			if (!result)
				return false;
		}

		return result;
	}

	public static ConjunctEntityPatternSet mapToSet(Map<String, Integer> values) {
		ConjunctEntityPatternSet result = new ConjunctEntityPatternSet();
		for (String key : values.keySet()) {
			int q = values.get(key);
			SinglePattern el = new SinglePattern(key, q);
			result.add(el);
		}
		return result;
	}

	@Override
	public boolean isDefault() {
		return false;
	}

}
