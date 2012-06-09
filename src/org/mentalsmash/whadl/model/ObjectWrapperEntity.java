package org.mentalsmash.whadl.model;

import org.mentalsmash.whadl.model.expressions.LiteralExpression;

public class ObjectWrapperEntity extends BaseEntity {

	private final Object obj;

	public ObjectWrapperEntity(Object obj) {
		super("OBJ_WRAP", new LiteralExpression(true));
		this.obj = obj;
	}

	public Object getObject() {
		return obj;
	}

	@Override
	public String toString() {
		return obj.toString();
	}

	public String getReference() {
		if (obj instanceof Entity) {
			return ((Entity) obj).getReference();
		} else {
			return super.getReference();
		}
	}

	@Override
	public int hashCode() {
		return this.obj.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof ObjectWrapperEntity) {
			ObjectWrapperEntity other = (ObjectWrapperEntity) obj;
			return this.obj.equals(other.obj);
		} else {
			return this.obj.equals(obj);
		}
	}

}
