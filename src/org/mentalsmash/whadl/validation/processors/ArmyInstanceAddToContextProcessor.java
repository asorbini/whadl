package org.mentalsmash.whadl.validation.processors;

import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.validation.EntityContext;

public class ArmyInstanceAddToContextProcessor implements
		EntityProcessor<ArmyInstance> {

	private final EntityContext context;
	private boolean overwrite;

	public ArmyInstanceAddToContextProcessor(EntityContext context,
			boolean overwrite) {
		this.overwrite = overwrite;
		this.context = context;
	}

	@Override
	public ArmyInstance process(ArmyInstance e) {
		this.context.registerEntities(e, overwrite);
		// this.context.dumpToLog();
		return e;
	}

	@Override
	public String toString() {
		return "PROC" + "-" + "ARMYINST" + "-" + "ADD.TO.CONTEXT";
	}

}
