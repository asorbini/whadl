package org.mentalsmash.whadl.validation;

import java.util.Map;

import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.model.Equipment;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.UnitMemberInstance;

public interface EntityContext {

	public String qualifyReference(String ref);

	public Entity getDefaultContext();

	public void registerEntities(Army a, boolean overwrite)
			throws UnknownEntityException;

	public void registerEntities(Unit u, boolean overwrite)
			throws UnknownEntityException;

	public void registerEntities(Unit u, UnitMember m, boolean overwrite)
			throws UnknownEntityException;

	public void registerEntities(UnitInstance ui, boolean overwrite)
			throws UnknownEntityException;

	public void registerEntities(UnitInstance ui, UnitMemberInstance umi,
			boolean overwrite) throws UnknownEntityException;

	public void registerEntities(ArmyInstance ai, boolean overwrite)
			throws UnknownEntityException;

	public void registerEntities(Equipment e, boolean overwrite)
			throws UnknownEntityException;

	public void unregisterEntities(Army a);

	public void unregisterEntities(Unit u);

	public void unregisterEntities(UnitMember m);

	public void unregisterEntities(UnitInstance ui);

	public void unregisterEntities(UnitMemberInstance umi);

	public void unregisterEntities(ArmyInstance ai);

	public void unregisterEntities(Equipment e);

	public boolean contains(Entity e);

	public boolean contains(String selector);

	public Entity getEntity(String selector) throws UnknownEntityException;

	public void addEntity(Entity e, String selector, boolean overwrite);

	public void addEntity(Entity e, String selector);

	public boolean removeEntity(String selector) throws UnknownEntityException;

	public EntityContext getSubContext(Entity thisEntity)
			throws UnknownEntityException;

	public EntityContext getSubContext(Entity thisEntity, boolean writable)
			throws UnknownEntityException;

	public EntityContext getSubContext(Entity thisEntity, boolean writable,
			Map<String, Entity> specialEntities) throws UnknownEntityException;

	public EntityContext getSubContext(String subctx)
			throws UnknownEntityException;

	public EntityContext getSubContext(String subctx, boolean writable)
			throws UnknownEntityException;

	public EntityContext getSubContext(String subctx, boolean writable,
			Map<String, Entity> specialEntities) throws UnknownEntityException;

	public void dumpToLog();

}