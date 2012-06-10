package org.mentalsmash.whadl.model;

import org.mentalsmash.whadl.model.expressions.Expression;

public class SingleUpgrade extends SingleEquipment {
	
	public SingleUpgrade() {
		super();
	}

	public SingleUpgrade(String name) {
		super(name);
	}
	
	public SingleUpgrade(String name,Expression conditions) {
		super(name, conditions);
	}

}
