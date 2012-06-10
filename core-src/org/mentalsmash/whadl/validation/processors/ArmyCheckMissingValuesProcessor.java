package org.mentalsmash.whadl.validation.processors;

import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitMember;

public class ArmyCheckMissingValuesProcessor implements EntityProcessor<Army> {


	@Override
	public Army process(Army e) throws EntityProcessingException {
		this.setDefaultValues(e);
		return e;
	}

	protected void setDefaultValues(Army a) throws EntityProcessingException{
		for (Unit u : a.getUnits()) {
			this.setDefaultValues(u);
		}
	}


	protected void setDefaultValues(Unit u) throws EntityProcessingException{
		// if (u.getSlotsPattern() == null) {
		// throw new EntityProcessingException(this, u,
		// "Missing Slots Pattern");
		// }
		//		
		// if (u.getUpgradesPattern() == null) {
		// throw new EntityProcessingException(this, u,
		// "Missing Upgrades Pattern");
		// }

		if (u.getCompositionPattern() == null) {
			throw new EntityProcessingException(this, u, "Missing Composition Pattern");
		}

		if (u.getCostExpression() == null) {
			throw new EntityProcessingException(this, u, "Missing Cost Expression");
		}

		for (UnitMember m : u.getMembers()) {
			this.setDefaultValues(m);
		}
	}

	protected void setDefaultValues(UnitMember m) throws EntityProcessingException{
		if (m.getEquipmentPattern() == null) {
			throw new EntityProcessingException(this, m, "Missing Equipment Pattern");
		}
	}
	
	@Override
	public String toString(){
		return "PROC"+"-"+"ARMY"+"-"+"CHECK.MISSING.VALUES";
	}

}
