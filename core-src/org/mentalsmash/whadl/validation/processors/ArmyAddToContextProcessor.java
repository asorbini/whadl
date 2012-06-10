package org.mentalsmash.whadl.validation.processors;

import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.validation.EntityContext;

public class ArmyAddToContextProcessor implements
		EntityProcessor<Army> {

	private final EntityContext context;
	private final boolean overwrite;
	
	public ArmyAddToContextProcessor(EntityContext context,boolean overwrite) {
		this.context = context;
		this.overwrite = overwrite;
	}

	@Override
	public Army process(Army e) {
		this.context.registerEntities(e,overwrite);
		return e;
	}
	
	@Override
	public String toString(){
		return "PROC"+"-"+"ARMY"+"-"+"ADD.TO.CONTEXT";
	}

}
