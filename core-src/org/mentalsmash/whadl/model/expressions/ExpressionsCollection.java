package org.mentalsmash.whadl.model.expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.mentalsmash.whadl.model.CollectionEntity;
import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.model.ObjectWrapperEntity;
import org.mentalsmash.whadl.validation.EntityContext;

public class ExpressionsCollection extends BaseExpression implements
		Expression, List<Expression> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6968056173174727250L;

	private final ArrayList<Expression> list = new ArrayList<Expression>();
	
	
	public Entity evaluate(EntityContext context) {
		CollectionEntity<Entity> result = new CollectionEntity<Entity>();

		for (Expression exp : this) {
			result.add(new ObjectWrapperEntity(exp.evaluate(context)));
		}

		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (Expression exp : this) {
			result = prime * result + exp.hashCode();
		}

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ExpressionsCollection))
			return false;
		ExpressionsCollection other = (ExpressionsCollection) obj;

		if (other.size() != this.size())
			return false;

		for (Expression exp : this) {
			if (!other.contains(exp))
				return false;
		}

		return true;
	}
	
	@Override
	public void accept(ExpressionVisitor visitor) {
		for (Expression exp : this) {
			visitor.visit(exp);
		}
	}

	@Override
	public boolean add(Expression o) {
		return this.list.add(o);
	}

	@Override
	public void add(int index, Expression element) {
		this.list.add(index,element);
	}

	@Override
	public boolean addAll(Collection<? extends Expression> c) {
		return this.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Expression> c) {
		return this.addAll(index, c);
	}

	@Override
	public void clear() {
		this.list.clear();
	}

	@Override
	public boolean contains(Object o) {
		return this.list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.list.containsAll(c);
	}

	@Override
	public Expression get(int index) {
		return this.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return this.list.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	@Override
	public Iterator<Expression> iterator() {
		return this.list.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.list.lastIndexOf(o);
	}

	@Override
	public ListIterator<Expression> listIterator() {
		return this.list.listIterator();
	}

	@Override
	public ListIterator<Expression> listIterator(int index) {
		return this.list.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		return this.list.remove(o);
	}
	

	@Override
	public Expression remove(int index) {
		return this.list.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.list.retainAll(c);
	}

	@Override
	public Expression set(int index, Expression element) {
		return this.list.set(index, element);
	}

	@Override
	public int size() {
		return this.list.size();
	}

	@Override
	public List<Expression> subList(int fromIndex, int toIndex) {
		return this.list.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return this.list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.toArray(a);
	}
	
	@Override
	public String toString(){
		StringBuilder result = new StringBuilder();
		
		result.append("{ ");
		Iterator<Expression> it = this.iterator();
		
		while (it.hasNext()) {
			result.append(it.next());
			if (it.hasNext()) {
				result.append(", ");
			}
		}
		
		
		result.append(" }");
		
		return result.toString();
	}
}
