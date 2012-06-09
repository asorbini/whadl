package org.mentalsmash.whadl.model.patterns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlternativePattern extends MultiplePattern {

	private static final long serialVersionUID = -3789727160503380237L;

	@Override
	public String getName() {
		return "OR-SET";
	}

	@Override
	public int getQuantity() {
		int max = 0;

		for (Pattern p : this) {
			if (p.getQuantity() > max) {
				max = p.getQuantity();
			}
		}

		return max;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append("(");
		for (Iterator<Pattern> it = this.iterator(); it.hasNext();) {
			Pattern mem = it.next();
			str.append(mem);
			if (it.hasNext())
				str.append(" OR ");
		}
		str.append(")");

		return str.toString();
	}

	@Override
	public PatternContentMap toMap() {
		Pattern next = this.innerCollection.iterator().next();
		log.debug("Converting alternative pattern to Map,"
				+ " only the first element found will be considered: returned="
				+ next + ", pattern=" + this);

		return next.toMap();
	}

	@Override
	public void pack() {
		AlternativePattern packed = this.pack(this);
		this.clear();
		this.addAll(packed);
	}

	private AlternativePattern pack(AlternativePattern set) {
		AlternativePattern temp = new AlternativePattern();
		temp.addAll(set);

		ArrayList<Pattern> checked = new ArrayList<Pattern>();

		for (Pattern ref : set) {
			if (ref instanceof AlternativePattern) {
				AlternativePattern andSet = (AlternativePattern) ref;
				AlternativePattern packed = this.pack(andSet);
				temp.addAll(packed);
			}
		}

		AlternativePattern result = new AlternativePattern();
		result.addAll(temp);

		for (Pattern ref : temp) {
			if (checked.contains(ref))
				continue;

			if (ref instanceof SinglePattern) {
				SinglePattern singRef = (SinglePattern) ref;

				for (Pattern other : this) {
					if (singRef != other
							&& singRef.getName().equals(other.getName())) {
						singRef.setQuantity(singRef.getQuantity() + 1);
						result.remove(other);
					}
				}

				checked.add(ref);
			}
		}

		return temp;
	}

	private static final Logger log = LoggerFactory
			.getLogger(AlternativePattern.class);

	private final Collection<Pattern> tried = new ArrayList<Pattern>();

	@Override
	public Pattern match(Map<String, Integer> entities)
			throws NoMatchException, PartialMatchException {
		return this.match(entities, new FakePatternVisitor());
	}

	@Override
	public Pattern match(Map<String, Integer> entities,
			PatternMatchObserver visitor) throws NoMatchException,
			PartialMatchException {

		if (this.tried.size() == this.size()) {
			// visitor.none(this, entities);
			log.debug("No more choices available to match " + entities);
			throw new NoMatchException(this, entities);
		} else {

		}

		log.debug("Matching " + entities + " with " + this + "("
				+ (this.size() - this.tried.size()) + " choices left)");

		for (Pattern rule : this) {

			if (this.tried.contains(rule)) {
				continue;
			}

			try {
				log.debug("Match " + entities + " with sub-rule " + rule);
				Pattern res = rule.match(entities, visitor);
				log.debug("All entities matched by sub-rule, sub-rule result: "
						+ res);

				// visitor.match(this, entities, res);
				return res;
			} catch (NoMatchException ex) {
				log.debug("No entity matched by sub-rule.");

			} catch (PartialMatchException ex) {
				Pattern result = ex.getResult();
				Map<String, Integer> unmatched = ex.getUnmatched();

				log.debug("Partial match by sub-rule, unmatched: " + unmatched
						+ " , sub-rule result: " + result);

				// visitor.partial(this, entities, unmatched, result);
				throw new PartialMatchException(rule, entities, result,
						unmatched, visitor);

			} finally {
				this.tried.add(rule);
			}
		}

		// visitor.none(this, entities);
		log.debug("None of " + entities + " matched.");
		throw new NoMatchException(this, entities);
	}

	public Collection<Pattern> getTried() {
		return this.tried;
	}

	@Override
	public void startEvaluation() {
		super.startEvaluation();
		this.tried.clear();
	}

	@Override
	public boolean isDeterministic() {
		if (this.tried.size() == this.size()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isDefault() {
		return false;
	}

}
