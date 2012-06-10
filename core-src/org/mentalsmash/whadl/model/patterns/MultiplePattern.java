package org.mentalsmash.whadl.model.patterns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.InstanceReferenceExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MultiplePattern extends BasePattern implements Collection<Pattern> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8472024303500847065L;

	protected Expression costExpression;
	protected final Collection<Pattern> innerCollection = new ArrayList<Pattern>();
	
	@Override
	public Expression getCostExpression() {
		return this.costExpression;
	}
	
	public void setCostExpression(Expression costExpression) {
		this.costExpression = costExpression;
	}
	
	protected boolean evaluating = false;

	@Override
	public boolean isEvaluating() {
		return this.evaluating;
	}
	
	private static Logger log = LoggerFactory.getLogger(MultiplePattern.class);

	@Override
	public void startEvaluation() {
		log.trace("Starting evaluation...");
		for (Pattern p : this) {
			p.startEvaluation();
		}
		this.evaluating = true;
	}

	@Override
	public void stopEvaluation() {
		log.trace("Stopping evaluation...");
		for (Pattern p : this) {
			p.stopEvaluation();
		}
		this.evaluating = false;
	}

	@Override
	public boolean add(Pattern o) {
		if (o instanceof InstanceReferenceExpression) {
//			log.debug("++++ ADDING INSTANCE REFERENCE: "+o);
		}
		return this.innerCollection.add(o);
	}
	
	
	@Override
	public Iterator<Pattern> iterator(){
		return new NonDeterministicIterator(this);
	}

	@Override
	public boolean addAll(Collection<? extends Pattern> c) {
		return this.innerCollection.addAll(c);
	}

	@Override
	public void clear() {
		this.innerCollection.clear();
	}

	@Override
	public boolean contains(Object o) {
		return this.innerCollection.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.innerCollection.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return this.innerCollection.isEmpty();
	}

	@Override
	public boolean remove(Object o) {
		return this.innerCollection.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.innerCollection.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.innerCollection.retainAll(c);
	}

	@Override
	public int size() {
		return this.innerCollection.size();
	}

	@Override
	public Object[] toArray() {
		return this.innerCollection.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.innerCollection.toArray(a);
	}

	@Override
	public String getName() {
		return "MULTIPLE-PATTERN";
	}

	@Override
	public int getQuantity() {
		int q = 0;
		for (Pattern p : this.innerCollection) {
			q += p.getQuantity();
		}
		return q;
	}

	@Override
	public boolean isDeterministic() {
		boolean result = true;
		for (Pattern p : this.innerCollection) {
			result = result && p.isDeterministic();
			if (!result){
				return false;
			}
		}
		return true;
	}

	
	@Override
	public void pack() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PatternContentMap toMap() {
		Map<String, Integer> values = new HashMap<String, Integer>();
		for (Pattern p : this.innerCollection) {
			values.putAll(p.toMap());
		}
		return new PatternContentMap(values);
	}

	@Override
	public Pattern extract(String key) {
		for (Pattern p : this) {
			Pattern res = p.extract(key);
			if (res != null) {
				return res;
			}
		}
		
		return null;
	}
	
	
	
}
