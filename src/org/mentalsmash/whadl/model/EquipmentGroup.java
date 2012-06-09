package org.mentalsmash.whadl.model;

import java.util.Collection;
import java.util.Iterator;

import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;

public class EquipmentGroup extends BaseEntity implements Equipment, Collection<Equipment>{

	public EquipmentGroup(String name) {
		super(name,new LiteralExpression(true));
	}
	
	public EquipmentGroup(String name, Expression conditions) {
		super(name,conditions);
	}
	
	private final CollectionEntity<Equipment> items = new CollectionEntity<Equipment>();
	
	public void addEquipment(Equipment eq){
		this.items.add(eq);
	}
	
	public void removeEquipment(Equipment eq){
		this.items.remove(eq);
	}
	
	public CollectionEntity<Equipment> getEquipments(){
		return this.items;
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("GROUP "+this.getReference()+" (");
		for (Iterator<Equipment> it = this.items.iterator(); it.hasNext();) {
			Equipment eq = it.next();
			str.append(eq);
			
			if (it.hasNext()) {
				str.append(", ");
			}
		}
		str.append(")");
		if (!this.getConditions().equals(new LiteralExpression(true))) {
			str.append(" CONDITIONS "+this.getConditions());
		}
		return str.toString();
		
	}
	
	public static final String ATTRIBUTE_ITEMS = "composition";
	public static final String ATTRIBUTE_CONDITIONS = "slots";

	@Override
	public Object getAttribute(String selector) {
		if (selector.equals(ATTRIBUTE_ITEMS)) {
			return this.getEquipments();
		} else if (selector.equals(ATTRIBUTE_CONDITIONS)) {
			return this.getConditions();
		} else {
			return super.getAttribute(selector);
		}
	}

	@Override
	public void setAttribute(String selector, Object value) {
		if (selector.equals(ATTRIBUTE_CONDITIONS)
				|| selector.equals(ATTRIBUTE_ITEMS)) {
			throw new IllegalArgumentException("Read-Only attribute: "
					+ selector);
		} else {
			super.setAttribute(selector, value);
		}
	}

	@Override
	public boolean add(Equipment o) {
		return this.items.add(o);
	}

	@Override
	public boolean addAll(Collection<? extends Equipment> c) {
		return this.items.addAll(c);
	}

	@Override
	public void clear() {
		this.items.clear();
	}

	@Override
	public boolean contains(Object o) {
		return this.items.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.items.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return this.items.isEmpty();
	}

	@Override
	public Iterator<Equipment> iterator() {
		return this.items.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return this.items.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.items.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.items.retainAll(c);
	}

	@Override
	public int size() {
		return this.items.size();
	}

	@Override
	public Object[] toArray() {
		return this.items.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.items.toArray(a);
	}
}
