package org.mentalsmash.whadl.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.mentalsmash.whadl.WhadlException;
import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.model.Equipment;
import org.mentalsmash.whadl.model.EquipmentGroup;
import org.mentalsmash.whadl.model.SingleEquipment;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.UnitMemberInstance;
import org.mentalsmash.whadl.model.UnitSlot;
import org.mentalsmash.whadl.model.UnitSlot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicEntityContext implements EntityContext {

	private final static Logger log = LoggerFactory
			.getLogger(DynamicEntityContext.class);

	protected Map<String, Entity> entities = new HashMap<String, Entity>();
	protected final Entity defaultContext;

	public DynamicEntityContext(Entity defaultContext) {
		this.defaultContext = defaultContext;
		this.addSpecialEntities();
	}

	private void addSpecialEntities() {
		this.entities.put(defaultContext.getReference(), defaultContext);

		for (Value val : Value.values()) {
			UnitSlot slot = new UnitSlot(val);
			this.entities.put(qualifyReference(slot.getName()), slot);
		}
		this.entities.put("INSTANCE_REF",new SingleEquipment("INSTANCE_REF_FAKE_EQUIPMENT"));
	}

	@Override
	public Entity getDefaultContext() {
		return this.defaultContext;
	}

	@Override
	public String qualifyReference(String ref) {
		if (this.defaultContext.getReference().length() == 0)
			return ref;

		if (!ref.startsWith(this.defaultContext.getReference())) {
			return this.defaultContext.getReference() + "." + ref;
		} else {
			return ref;
		}
	}

	@Override
	public Entity getEntity(String selector) throws UnknownEntityException {

		if (selector == null) {
			throw new WhadlRuntimeException("Null selector!");
		}

		if (selector.equals("this")
				|| selector.equals(this.getDefaultContext().getReference()))
			return this.defaultContext;

		String qualified = this.qualifyReference(selector);

		log.trace(this + " 1: " + qualified);
		Entity e = this.entities.get(qualified);

		if (e != null) {
			log.trace(this + " FOUND IT");
			return e;
		}

		log.trace(this + " 2: " + selector);
		e = this.entities.get(selector);

		if (e != null) {
			log.trace(this + " FOUND IT");
			return e;
		}

		String[] superctxs = this.defaultContext.getReference().split("\\.");
		if (superctxs.length > 1) {
			StringBuilder superCtxSelector = new StringBuilder();

			int len = superctxs.length - 1;

			for (int i = 0; i < len; i++) {
				superCtxSelector.append(superctxs[i]);
				if (i < (len - 1)) {
					superCtxSelector.append(".");
				}
			}

			log.trace(this + " EXPLORING SUPER-CONTEXT " + superCtxSelector);

			Entity superCtxEntity = this.entities.get(superCtxSelector
					.toString());

			if (superCtxEntity == null) {
//				this.dumpToLog();
				throw new WhadlRuntimeException(
						"Cannot find SuperContext entity: " + superCtxSelector);
			}

			EntityContext superContext = new SuperContext(superCtxEntity,
					entities);
			return superContext.getEntity(selector);
		} else {
			log.trace("NO SUPER-CONTEXT TO EXPLORE (current context: "
					+ this.defaultContext + " - splits: " + superctxs.length
					+ ")");
		}

		throw new UnknownEntityException(this, selector);
	}

	@Override
	public void addEntity(Entity e, String selector, boolean overwrite) {
		String qualif = this.qualifyReference(selector);

		if (this.contains(qualif)) {
			if (overwrite) {
				log.debug("Overwriting entity: " + qualif + " -> "
						+ this.entities.get(qualif));
			} else {
				throw new WhadlRuntimeException(
						"Can't overwrite existing entity: selector=" + qualif
								+ ", to-add=" + e + ", existing="
								+ this.getEntity(qualif));
			}
		}

		this.entities.put(qualif, e);
		e.setReference(qualif);
		log.debug("Registered entity: " + qualif + " -> " + e);
	}

	@Override
	public void addEntity(Entity e, String selector) {
		this.addEntity(e, selector, false);
	}

	@Override
	public boolean removeEntity(String selector) throws UnknownEntityException {
		Entity e = this.getEntity(selector);
		this.entities.remove(e.getReference());
		return true;
	}

	@Override
	public String toString() {
		return "DYN-CTX-[" + this.defaultContext + "]";
	}

	@Override
	public EntityContext getSubContext(Entity thisEntity)
			throws UnknownEntityException {
		return this.getSubContext(thisEntity.getReference(), false);
	}

	@Override
	public EntityContext getSubContext(Entity thisEntity, boolean writable)
			throws UnknownEntityException {
		return this.getSubContext(thisEntity.getReference(), writable,
				new HashMap<String, Entity>());
	}

	@Override
	public EntityContext getSubContext(Entity thisEntity, boolean writable,
			Map<String, Entity> specialEntities) throws UnknownEntityException {
		return this.getSubContext(thisEntity.toString(), writable,
				specialEntities);
	}

	@Override
	public EntityContext getSubContext(String subctx)
			throws UnknownEntityException {
		return this.getSubContext(subctx, false);
	}

	@Override
	public EntityContext getSubContext(String subctx, boolean writable)
			throws UnknownEntityException {
		return this.getSubContext(subctx, writable,
				new HashMap<String, Entity>());
	}

	@Override
	public EntityContext getSubContext(String subctx, boolean writable,
			Map<String, Entity> specialEntities) throws UnknownEntityException {
		Entity e = this.getEntity(subctx);
		return new SubContext(this, e, this.entities, writable);
	}

	@Override
	public boolean contains(Entity e) {
		return this.contains(e.getReference());
	}

	@Override
	public boolean contains(String selector) {
		try {
			this.getEntity(selector);
			return true;
		} catch (UnknownEntityException ex) {
			return false;
		}
	}

	private void registerModifiedUnits(Army a, Unit un, boolean overwrite) {
		for (Unit modU : un.getModifiedUnits()) {
			try {
				Unit u = (Unit) this.getSubContext(a).getEntity(modU.getName());
				modU.setReference(u.getReference());
				for (Unit unit : u.getModifiedUnits()) {
					this.registerModifiedUnits(a, unit, overwrite);
				}
			} catch (UnknownEntityException ex) {
				// this.dumpToLog();
				log.warn("Considering " + modU + " as an additional Army unit");
				this.getSubContext(a, true).registerEntities(modU, overwrite);
				this.registerModifiedUnits(a, modU, overwrite);
			}
		}
	}

	@Override
	public void registerEntities(Army a, boolean overwrite)
			throws UnknownEntityException {
		this.addEntity(a, a.getName(), overwrite);

		for (Unit u : a.getUnits()) {
			this.getSubContext(a, true).registerEntities(u, overwrite);
		}

		// Set correct references in modified units
		// Collection<Unit> modUnits = new ArrayList<Unit>();

		for (Unit u : a.getUnits()) {
			this.registerModifiedUnits(a, u, overwrite);
		}

		for (Equipment e : a.getEquipments()) {
			this.getSubContext(a, true).registerEntities(e, overwrite);
		}

	}

	@Override
	public void registerEntities(ArmyInstance ai, boolean overwrite)
			throws UnknownEntityException {
		String type = ai.getTypeName();
		Army a = (Army) this.getEntity(type);

		if (a == null) {
			throw new IllegalArgumentException("Cannot find Army instance "
					+ type + " (type of ArmyInstance " + ai);
		}

		this.getSubContext(a, true).addEntity(ai, ai.getName(), overwrite);

		for (UnitInstance u : ai.getUnits()) {
			this.getSubContext(ai, true).registerEntities(u, overwrite);
		}
	}

	@Override
	public void registerEntities(Equipment e, boolean overwrite)
			throws UnknownEntityException {
		if (e instanceof EquipmentGroup) {
			EquipmentGroup eg = (EquipmentGroup) e;

			this.addEntity(eg, eg.getName(), overwrite);

			for (Equipment eq : eg) {
				this.getSubContext(eg, true).registerEntities(eq, overwrite);
			}
		} else {
			this.addEntity(e, e.getName(), overwrite);
		}
	}

	@Override
	public void registerEntities(Unit u, boolean overwrite)
			throws UnknownEntityException {
		this.addEntity(u, u.getName(), overwrite);

		for (UnitMember m : u.getMembers()) {
			this.getSubContext(u, true).registerEntities(u, m, overwrite);
		}
	}

	@Override
	public void registerEntities(Unit u, UnitMember m, boolean overwrite)
			throws UnknownEntityException {

		this.addEntity(m, m.getName(), overwrite);

	}

	@Override
	public void registerEntities(UnitInstance ui, boolean overwrite)
			throws UnknownEntityException {
		this.addEntity(ui, ui.getName(), overwrite);

		for (UnitMemberInstance m : ui.getMembers()) {
			this.getSubContext(ui, true).registerEntities(ui, m, overwrite);
		}
	}

	@Override
	public void registerEntities(UnitInstance ui, UnitMemberInstance umi,
			boolean overwrite) throws UnknownEntityException {

		this.addEntity(umi, umi.getName(), overwrite);
	}

	@Override
	public void unregisterEntities(Army a) {
		try {
			this.removeEntity(a.getReference());

			for (Unit u : a.getUnits()) {
				this.getSubContext(a, true).unregisterEntities(u);
			}

			for (Equipment e : a.getEquipments()) {
				this.getSubContext(a, true).unregisterEntities(e);
			}
		} catch (UnknownEntityException ex) {
			// if the army wasn't registered, its contents won't be either.
		}

	}

	@Override
	public void unregisterEntities(ArmyInstance ai)
			throws UnknownEntityException {
		String type = ai.getTypeName();
		Army a = (Army) this.getEntity(type);

		if (a == null) {
			throw new IllegalArgumentException("Cannot find Army instance "
					+ type + " (type of ArmyInstance " + ai);
		}

		try {
			this.getSubContext(a, true).removeEntity(ai.getReference());

			for (UnitInstance u : ai.getUnits()) {
				this.getSubContext(ai, true).unregisterEntities(u);
			}

		} catch (UnknownEntityException ex) {

		}
	}

	@Override
	public void unregisterEntities(Equipment e) {
		if (e instanceof EquipmentGroup) {
			EquipmentGroup eg = (EquipmentGroup) e;

			this.removeEntity(eg.getReference());

			for (Equipment eq : eg) {
				this.getSubContext(eg, true).unregisterEntities(eq);
			}
		} else {
			this.removeEntity(e.getReference());
		}
	}

	@Override
	public void unregisterEntities(Unit u) {
		try {

			this.removeEntity(u.getReference());

			for (UnitMember m : u.getMembers()) {
				this.getSubContext(u, true).unregisterEntities(m);
			}

		} catch (UnknownEntityException ex) {

		}
	}

	@Override
	public void unregisterEntities(UnitMember m) {
		try {

			this.removeEntity(m.getReference());

		} catch (UnknownEntityException ex) {

		}

	}

	@Override
	public void unregisterEntities(UnitInstance ui) {
		try {
			this.removeEntity(ui.getReference());

			for (UnitMemberInstance m : ui.getMembers()) {
				this.getSubContext(ui, true).unregisterEntities(m);
			}

		} catch (UnknownEntityException ex) {

		}
	}

	@Override
	public void unregisterEntities(UnitMemberInstance umi) {
		try {
			this.removeEntity(umi.getReference());
		} catch (UnknownEntityException ex) {

		}
	}

	public void merge(DynamicEntityContext ctx, boolean overwrite,
			boolean errorOnConflict) throws WhadlException {

		for (String key : ctx.entities.keySet()) {
			boolean contained = this.entities.containsKey(key);

			if (contained && !overwrite && errorOnConflict
					&& !this.entities.get(key).equals(ctx.entities.get(key))) {
				throw new WhadlException("Conflicts between contexts: key="
						+ key + ", current=" + this.entities.get(key)
						+ ", found=" + ctx.entities.get(key));
			}
		}
		
		for (String key : ctx.entities.keySet()) {
			boolean contained = this.entities.containsKey(key);

			if ((contained && overwrite) || !contained) {
				this.entities.put(key, ctx.entities.get(key));
			}
		}
	}

	public DynamicEntityContext extractContext(String... prefixes) {
		DynamicEntityContext result = new DynamicEntityContext(
				this.defaultContext);

		ArrayList<String> prefs = new ArrayList<String>();
		for (String str : prefixes) {
			prefs.add(str);
		}

		for (String key : this.entities.keySet()) {
			boolean included = false;
			for (String pref : prefs) {
				if (key.startsWith(pref)) {
					included = true;
					break;
				}
			}

			if (included) {
				result.entities.put(key, this.entities.get(key));
			}
		}

		return result;
	}

	private static class SubContext extends DynamicEntityContext {

		private final EntityContext main;
		private final boolean writable;

		public SubContext(EntityContext main, Entity defaultContext,
				Map<String, Entity> entities, boolean writable) {
			super(defaultContext);
			this.main = main;
			this.entities = entities;
			this.writable = writable;
		}

		public boolean isReadOnly() {
			return !writable;
		}

		@Override
		public String toString() {
			return (this.isReadOnly() ? "RO" : "RW") + "-CTX-["
					+ this.defaultContext + "]";
		}

		@Override
		public boolean contains(String selector) {
			String qualified = this.qualifyReference(selector);
			boolean contained = super.contains(qualified);
			if (!contained) {
				contained = super.contains(selector);

				if (!contained) {
					return this.main.contains(selector);
				}

			}

			return true;
		}

		@Override
		public void addEntity(Entity e, String selector, boolean overwrite) {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}

			super.addEntity(e, this.qualifyReference(selector), overwrite);
		}

		@Override
		public void registerEntities(Army a, boolean overwrite)
				throws UnknownEntityException {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}
			super.registerEntities(a, overwrite);
		}

		@Override
		public void registerEntities(ArmyInstance ai, boolean overwrite)
				throws UnknownEntityException {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}
			super.registerEntities(ai, overwrite);
		}

		@Override
		public void registerEntities(Equipment e, boolean overwrite)
				throws UnknownEntityException {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}
			super.registerEntities(e, overwrite);
		}

		@Override
		public void registerEntities(Unit u, boolean overwrite)
				throws UnknownEntityException {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}
			super.registerEntities(u, overwrite);
		}

		@Override
		public void registerEntities(UnitInstance ui, boolean overwrite)
				throws UnknownEntityException {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}
			super.registerEntities(ui, overwrite);
		}

		@Override
		public void registerEntities(Unit u, UnitMember m, boolean overwrite)
				throws UnknownEntityException {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}
			super.registerEntities(u, m, overwrite);
		}

		@Override
		public void registerEntities(UnitInstance ui, UnitMemberInstance umi,
				boolean overwrite) throws UnknownEntityException {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}
			super.registerEntities(ui, umi, overwrite);
		}

		@Override
		public boolean removeEntity(String selector)
				throws UnknownEntityException {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}
			return super.removeEntity(selector);
		}

		@Override
		public void unregisterEntities(Army a) {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}
			super.unregisterEntities(a);
		}

		@Override
		public void unregisterEntities(ArmyInstance ai)
				throws UnknownEntityException {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}
			super.unregisterEntities(ai);
		}

		@Override
		public void unregisterEntities(Equipment e) {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}
			super.unregisterEntities(e);
		}

		@Override
		public void unregisterEntities(Unit u) {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}
			super.unregisterEntities(u);
		}

		@Override
		public void unregisterEntities(UnitInstance ui) {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}
			super.unregisterEntities(ui);
		}

		@Override
		public void unregisterEntities(UnitMember m) {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}
			super.unregisterEntities(m);
		}

		@Override
		public void unregisterEntities(UnitMemberInstance umi) {
			if (this.isReadOnly()) {
				throw new UnsupportedOperationException("read only context!");
			}
			super.unregisterEntities(umi);
		}

	}

	private static final class SuperContext extends DynamicEntityContext {

		public SuperContext(Entity defaultContext, Map<String, Entity> entities) {
			super(defaultContext);
			this.entities = entities;
		}

		@Override
		public String toString() {
			return "SUPER-CTX-[" + this.defaultContext + "]";
		}

	}

	@Override
	public void dumpToLog() {
		log.info("BEGINNING DUMP OF " + this);

		for (String key : this.entities.keySet()) {
			log.info(key + " -> " + this.entities.get(key) + " ("
					+ this.entities.get(key).getClass().getName() + ")");
		}

		log.info("END OF DUMP");
	}

}
