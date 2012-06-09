package org.mentalsmash.whadl.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;

public class CollectionEntity<T extends Entity> extends BaseEntity implements Collection<T> {

	private final Collection<T> collection = new ArrayList<T>();
	
	private static final long serialVersionUID = 6581507865014871190L;

	
	public CollectionEntity(){
		super("CollectionOfEntities",new LiteralExpression(true));
	}
	
	public CollectionEntity(Expression conditions) {
		super("CollectionOfEntities",conditions);
	}
	
	@Override
	public boolean add(T o) {
		return this.collection.add(o);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return this.collection.addAll(c);
	}

	@Override
	public void clear() {
		this.collection.clear();
	}

	@Override
	public boolean contains(Object o) {
		return this.collection.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.collection.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return this.collection.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return this.collection.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return this.collection.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.collection.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.collection.retainAll(c);
	}

	@Override
	public int size() {
		return this.collection.size();
	}

	@Override
	public Object[] toArray() {
		return this.collection.toArray();
	}

	@Override
	public <V> V[] toArray(V[] a) {
		return this.collection.toArray(a);
	}

	public static final String ATTRIBUTE_SIZE = "size";
	public static final String ATTRIBUTE_ITEMS= "items";
	public static final String ATTRIBUTE_TAIL= "tail";
	public static final String ATTRIBUTE_HEAD= "head";

	@Override
	public Object getAttribute(String selector) {
		if (selector.equals(ATTRIBUTE_SIZE)) {
			return this.size();
		} else if (selector.equals(ATTRIBUTE_ITEMS)) {
			return this.collection;
		} else if (selector.equals(ATTRIBUTE_HEAD)) {
			return this.iterator().next();
		} else if (selector.equals(ATTRIBUTE_TAIL)) {
			CollectionEntity<T> tail = new CollectionEntity<T>();
			Iterator<T> it = this.iterator();
			it.next();
			while (it.hasNext()) {
				tail.add(it.next());
			}
			
			return tail;
		} else {
			return super.getAttribute(selector);
		}
	}

	@Override
	public void setAttribute(String selector, Object value) {
		if (selector.equals(ATTRIBUTE_SIZE)
				|| selector.equals(ATTRIBUTE_ITEMS)) {
			throw new WhadlRuntimeException("Read-Only attribute: "
					+ selector);
		} else {
			super.setAttribute(selector, value);
		}
	}
	
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("{");
		for (Iterator<T> it = this.iterator(); it.hasNext();) {
			T obj = it.next();
			str.append(obj+"");
			
			if (it.hasNext()) {
				str.append(",");
			}
			
		}
		
		str.append("}");
		return str.toString();
	}
}
