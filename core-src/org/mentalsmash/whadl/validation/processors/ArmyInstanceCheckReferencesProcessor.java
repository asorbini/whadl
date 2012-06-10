package org.mentalsmash.whadl.validation.processors;

import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.UnitMemberInstance;
import org.mentalsmash.whadl.model.patterns.InstanceReferencePattern;
import org.mentalsmash.whadl.model.patterns.MultiplePattern;
import org.mentalsmash.whadl.model.patterns.OptionalPattern;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.validation.EntityContext;
import org.mentalsmash.whadl.validation.UnknownEntityException;
import org.mentalsmash.whadl.validation.UnknownReferenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArmyInstanceCheckReferencesProcessor extends BaseReferenceChecker
		implements EntityProcessor<ArmyInstance> {

	public ArmyInstanceCheckReferencesProcessor(EntityContext context) {
		super(context);
	}

	@Override
	public ArmyInstance process(ArmyInstance e)
			throws EntityProcessingException {
		try {
			this.check(e);
		} catch (UnknownReferenceException ex) {
			throw new EntityProcessingException(this, e, ex);
		}
		return e;
	}

	public boolean check(ArmyInstance ai) throws UnknownReferenceException {
		if (ai == null) {
			error("null", "ArmyInstance Definition");
		}

		if (!this.context.contains(ai)) {
			error(ai.getReference(), "ArmyInstance " + ai.getName()
					+ " Definition");
		}

		// ArmyInstanceCheckReferencesProcessor subcheck = new
		// ArmyInstanceCheckReferencesProcessor(
		// this.context.getSubContext(ai));

		try {
			for (UnitInstance ui : ai.getUnits()) {
				this.check(ai, ui);
			}
		} catch (UnknownReferenceException ex) {
			error(ex.getReference(), "ArmyInstance " + ai.getName()
					+ " Definition - " + ex.getWhere());
		}

		return true;
	}

	private static final Logger log = LoggerFactory
			.getLogger(ArmyInstanceCheckReferencesProcessor.class);

	public boolean check(ArmyInstance ai, UnitInstance ui)
			throws UnknownReferenceException {
		if (ui == null) {
			error("null", "UnitInstance Definition");
		}

		if (!this.context.contains(ui)) {
			error(ui.getReference(), "UnitInstance " + ui.getName()
					+ " Definition");
		}

		Army a = (Army) this.context.getEntity(ai.getTypeName());

		Entity uEnt = this.context.getSubContext(a).getEntity(ui.getTypeName());

		if (!(uEnt instanceof Unit)) {
			log.error("Found " + uEnt.getClass() + " - " + uEnt
					+ " when looking for " + ui.getTypeName());
			// this.context.dumpToLog();
		}

		Unit u = (Unit) uEnt;

		// ArmyInstanceCheckReferencesProcessor subcheck = new
		// ArmyInstanceCheckReferencesProcessor(
		// this.context.getSubContext(u));

		try {

			this.check(ui.getSlotPattern(), u);
			this.checkReferencesToInstances(ui.getSlotPattern(), ai);

			this.check(ui.getCompositionPattern(), u);
			this.checkReferencesToInstances(ui.getCompositionPattern(), ai);

			this.check(ui.getUpgradesPattern(), u);
			this.checkReferencesToInstances(ui.getUpgradesPattern(), ai);

			this.check(ui.getLinkedUnitsPattern(), u);
			this.checkReferencesToInstances(ui.getLinkedUnitsPattern(), ai);

			// subcheck = new ArmyInstanceCheckReferencesProcessor(this.context
			// .getSubContext(ui));

			for (UnitMemberInstance umi : ui.getMembers()) {
				this.check(ui, umi);
			}

		} catch (UnknownReferenceException ex) {
			error(ex.getReference(), "UnitInstance " + ui.getName()
					+ " Definition - " + ex.getWhere());
		}

		return true;
	}

	public boolean check(UnitInstance ui, UnitMemberInstance umi)
			throws UnknownReferenceException {
		if (umi == null) {
			error("null", "UnitMemberInstance Definition");
		}

		if (!this.context.contains(umi)) {
			error(umi.getReference(), "UnitMemberInstance " + umi.getName()
					+ " Definition");
		}

		Unit u = (Unit) this.context.getEntity(ui.getTypeName());
		UnitMember m = u.getMember(umi.getTypeName());

		if (m == null) {
			throw new UnknownEntityException(this.context, umi.getTypeName());
		}

		try {
			this.check(umi.getEquipmentPattern(), m);
		} catch (UnknownReferenceException ex) {
			error(ex.getReference(), "UnitMemberInstance " + umi.getName()
					+ " Definition - " + ex.getWhere());
		}

		return true;
	}

	@Override
	public String toString() {
		return "PROC" + "-" + "ARMYINST" + "-" + "CHECK.REFERENCES";
	}

	private void checkReferencesToInstances(Pattern p, Entity ctx)
			throws UnknownReferenceException {
		if (p instanceof InstanceReferencePattern) {
			InstanceReferencePattern instPat = (InstanceReferencePattern) p;
			String instName = instPat.getName();
			try {
				this.context.getSubContext(ctx).getEntity(instName);
			} catch (UnknownEntityException e) {
				throw new UnknownReferenceException(instName, "Pattern: " + p);
			}
		} else if (p instanceof MultiplePattern) {
			for (Pattern pat : (MultiplePattern) p) {
				this.checkReferencesToInstances(pat, ctx);
			}
		} else if (p instanceof OptionalPattern) {
			this.checkReferencesToInstances(((OptionalPattern) p)
					.getOptionalPattern(), ctx);
		}
	}
}
