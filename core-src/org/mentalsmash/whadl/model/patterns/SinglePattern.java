package org.mentalsmash.whadl.model.patterns;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.expressions.EntityReferenceExpression;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SinglePattern extends BasePattern {

	public SinglePattern() {

	}

	public SinglePattern(String name) {
		this(name, 1);
	}

	public SinglePattern(String name, int quantity) {
		this(name, quantity, new LiteralExpression(0));
	}

	public SinglePattern(String name, int quantity, Expression costExpression) {
		this.name = name;
		this.quantity = quantity;
		this.setCostExpression(costExpression);
	}

	public SinglePattern(SinglePattern p) {
		this.name = p.name;
		this.quantity = p.quantity;
		this.setCostExpression(p.costExpression);
	}

	private int quantity = 1;
	private String name = Pattern.NULLREF;
	private Expression costExpression = new LiteralExpression(0);

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getQuantity() {
		// int q = (this.quantity == Pattern.QUANTITY_ANY) ? Integer.MAX_VALUE
		// : this.quantity;
		
		return this.quantity;
	}

	public Expression getCostExpression() {
		return this.costExpression;
	}

	public void setCostExpression(Expression costExpression) {
		if (costExpression == null)
			throw new WhadlRuntimeException("Null cost expression!");

		if (costExpression instanceof EntityReferenceExpression) {
			// TODO CostExpression of SinglePattern with quantity=ANY
			this.costExpression = new LiteralExpression(0);
		} else {

			this.costExpression = costExpression;
		}
	}

	@Override
	public String toString() {
		return (this.getQuantity() == Pattern.QUANTITY_ANY ? "* " : (this
				.getQuantity() == 1 ? "" : this.getQuantity() + " "))
				+ this.getName();
	}

	@Override
	public PatternContentMap toMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();

		map.put(this.getName(), this.getQuantity());

		return new PatternContentMap(map);
	}

	@Override
	public void pack() {

	}

	private boolean evaluating = false;

	@Override
	public boolean isEvaluating() {
		return this.evaluating;
	}

	@Override
	public void startEvaluation() {
		// log.debug(this+" STARTING EVALUATION!");
		this.evaluating = true;
	}

	@Override
	public void stopEvaluation() {
		this.evaluating = false;
	}

	private static final Logger log = LoggerFactory
			.getLogger(SinglePattern.class);

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

			if (entities.size() <= 1) {
				if (entities.size() == 0) {
					visitor.match(this, entities, this);
					log.debug("Matched with no entities");
					return this;
				}

				Entry<String, Integer> el = entities.entrySet().iterator()
						.next();

				if (el.getKey().equals(this.getName())) {
					if (this.getQuantity() == Pattern.QUANTITY_ANY) {

						visitor.match(this, entities, this);
						log.debug("Matched, result: " + this);
						return new SinglePattern(this);

					} else if (el.getValue() <= this.getQuantity()) {
						SinglePattern result = null;

						if (el.getValue() < this.getQuantity()) {
							result = new SinglePattern(this.getName(), this
									.getQuantity()
									- el.getValue());

						}

						log.debug("Matched, result: " + result);
						visitor.match(this, entities, result);
						return result;
					} else {
						Map<String, Integer> unmatched = new HashMap<String, Integer>();
						unmatched.put(el.getKey(), el.getValue()
								- this.getQuantity());

						log.debug("Partial matched, unmatched: " + unmatched
								+ " , result: " + null);
						visitor.partial(this, entities, unmatched, null);
						throw new PartialMatchException(this, entities, null,
								unmatched, visitor);
					}
				}
			} else {
				SinglePattern result = null;
				Map<String, Integer> unmatched = new HashMap<String, Integer>();
				int availQuant = this.getQuantity();
				String name = this.getName();

				boolean partialMatch = false;

				for (String key : entities.keySet()) {

					int q = entities.get(key);

					// log.error(name+" : "+key+ "  ("+(key.equals(name))+")");

					if (key.equals(name)) {

						if (availQuant != Pattern.QUANTITY_ANY) {
							if (q < availQuant) {

								// log.error(key+": "+availQuant + "-" + q);
								availQuant = availQuant - q;
								// log.error(availQuant + "");

							} else if (q > availQuant) {
								int missing = q - availQuant;
								availQuant = 0;
								// log.debug("Unmatched: " + key + "," +
								// missing);
								unmatched.put(name, missing);
							} else if (q == availQuant) {
								// log.error("Whole quantity available consumed ("
								// + availQuant + ")");
								availQuant = 0;
							}
						}

						partialMatch = true;
					} else {
						log.debug("Unmatched: " + key + "," + q);
						unmatched.put(key, q);
					}

				}

				if (availQuant > 0) {
					result = new SinglePattern(name, availQuant);
				}

				if (partialMatch) {

					visitor.partial(this, entities, unmatched, result);
					log.debug("Partial match, unmatched: " + unmatched
							+ " , result: " + result);
					throw new PartialMatchException(this, entities, result,
							unmatched, visitor);
				}

			}

		} catch (PartialMatchException ex) {
			throw ex;
		}

		visitor.none(this, entities);
		log.debug("None of " + entities + " matched");
		throw new NoMatchException(this, entities);
	}

	@Override
	public boolean isDeterministic() {
		return true;
	}

	@Override
	public boolean isDefault() {
		return false;
	}

	@Override
	public Pattern extract(String key) {
		if (key.equals(this.name)) {
			return new SinglePattern(this.name, this.quantity,
					this.costExpression);
		} else {
			return null;
		}
	}

}
