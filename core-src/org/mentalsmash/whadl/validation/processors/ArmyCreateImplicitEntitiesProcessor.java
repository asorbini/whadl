package org.mentalsmash.whadl.validation.processors;

import java.util.Collection;

import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.CollectionEntity;
import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.model.Equipment;
import org.mentalsmash.whadl.model.EquipmentGroup;
import org.mentalsmash.whadl.model.SingleEquipment;
import org.mentalsmash.whadl.model.SingleUpgrade;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.patterns.MultiplePattern;
import org.mentalsmash.whadl.model.patterns.OptionalPattern;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.model.patterns.SinglePattern;
import org.mentalsmash.whadl.validation.EntityContext;
import org.mentalsmash.whadl.validation.UnknownEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArmyCreateImplicitEntitiesProcessor implements
		EntityProcessor<Army> {

	private final EntityContext context;

	public ArmyCreateImplicitEntitiesProcessor(EntityContext context) {
		this.context = context;
	}

	@Override
	public Army process(Army e) {
		this.createImplicitEntities(e);
		return e;
	}

	protected void createImplicitEntities(Army a) {
		Collection<Entity> result = new CollectionEntity<Entity>();

		for (Unit u : a.getUnits()) {
			result.addAll(this.createImplicitEntities(u));

		}

		for (Entity e : result) {
			if (e instanceof SingleEquipment || e instanceof EquipmentGroup) {
				a.addEquipment((Equipment) e);
			} else {
				// System.out.println("NEED TO FIND A PLACE TO PUT: "+e);
			}
		}

	}

	protected Collection<Entity> createImplicitEntities(Unit u) {
		CollectionEntity<Entity> equips = new CollectionEntity<Entity>();

		if (u.getUpgradesPattern() != null) {
			equips.addAll(this.createImplicitEntities(u.getUpgradesPattern(),
					u, SingleUpgrade.class));
		}

		for (UnitMember m : u.getMembers()) {
			if (m.getEquipmentPattern() != null) {
				equips.addAll(this.createImplicitEntities(m
						.getEquipmentPattern(), m, SingleEquipment.class));
			}

			if (m.getSuperPattern() != null) {
				equips.addAll(this.createImplicitEntities(m.getSuperPattern(),
						m, SingleEquipment.class));
			}
		}

		for (Unit un : u.getModifiedUnits()) {
			equips.addAll(this.createImplicitEntities(un));
		}

		return equips;
	}

	private final static Logger log = LoggerFactory
			.getLogger(ArmyCreateImplicitEntitiesProcessor.class);

	protected Collection<Entity> createImplicitEntities(Pattern ref,
			Entity context, Class<? extends Entity> entityClass) {

		log.debug("PATTERN: " + ref + " ECONTEXT:" + context + " CTX:"
				+ this.context);

		EntityContext localCtx = this.context.getSubContext(context, true);

		CollectionEntity<Entity> result = new CollectionEntity<Entity>();

		if (ref == null)
			return result;

		if (ref instanceof MultiplePattern) {
			for (Pattern er : (MultiplePattern) ref) {
				result.addAll(this.createImplicitEntities(er, context,
						entityClass));
			}
		} else if (ref instanceof OptionalPattern) {
			result.addAll(this.createImplicitEntities(((OptionalPattern) ref)
					.getOptionalPattern(), context, entityClass));

		} else if (ref instanceof SinglePattern) { // ref is a
													// SingleEntityReference
			SinglePattern r = (SinglePattern) ref;
			String selector = r.getName();
			try {

				localCtx.getEntity(selector);

			} catch (UnknownEntityException ex) {

				try {
					Entity ent = entityClass.newInstance();
					ent.setName(selector);
					ent.setReference(selector);
					result.add(ent);
				} catch (Exception e1) {
					throw new RuntimeException(
							"Cannot instantiate new instance of " + entityClass
									+ " : " + e1.getMessage());
				}

			}

		}

		return result;
	}

	@Override
	public String toString() {
		return "PROC" + "-" + "ARMY" + "-" + "CREATE.IMPLICIT.ENTITIES";
	}

}
