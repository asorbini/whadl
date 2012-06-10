package org.mentalsmash.whadl.validation;

import java.util.Collection;
import java.util.Stack;

import org.mentalsmash.whadl.WhadlException;
import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.Equipment;
import org.mentalsmash.whadl.validation.processors.EntityProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhadlPreprocessor {
	private static final Logger log = LoggerFactory
			.getLogger(WhadlPreprocessor.class);

	private final Stack<EntityProcessor<Army>> armyProcessors = new Stack<EntityProcessor<Army>>();
	private final Stack<EntityProcessor<ArmyInstance>> armyInstanceProcessors = new Stack<EntityProcessor<ArmyInstance>>();
	private final Stack<EntityProcessor<Equipment>> equipmentProcessors = new Stack<EntityProcessor<Equipment>>();

	public WhadlPreprocessor() {

	}

	public void addArmyProcessor(EntityProcessor<Army> p) {
		log.debug("Adding ArmyInstance processor: " + p);
		this.armyProcessors.push(p);
		log.trace("Current Army processors: " + this.getArmyProcessors());
	}

	public void clearArmyProcessors() {
		log.debug("Clearing Army processors");
		log.trace("Current Army processors: " + this.getArmyProcessors());
		this.armyProcessors.clear();
	}

	public void addArmyInstanceProcessor(EntityProcessor<ArmyInstance> p) {
		log.debug("Adding ArmyInstance processor: " + p);
		this.armyInstanceProcessors.push(p);
		log.trace("Current ArmyInstance processors: "
				+ this.getArmyInstanceProcessors());
	}

	public void clearArmyInstanceProcessors() {
		log.debug("Clearing Army Instance processors");
		log.trace("Current ArmyInstance processors: "
				+ this.getArmyInstanceProcessors());
		this.armyInstanceProcessors.clear();
	}

	public void addEquipmentProcessor(EntityProcessor<Equipment> p) {
		log.debug("Adding Equipment processor: " + p);
		this.equipmentProcessors.push(p);
		log.trace("Current Equipment processors: "
				+ this.getEquipmentProcessors());
	}

	public void clearEquipmentProcessor() {
		log.debug("Clearing Equipment processors");
		log.trace("Current Equipment processors: "
				+ this.getEquipmentProcessors());
		this.equipmentProcessors.clear();
	}

	public Collection<EntityProcessor<ArmyInstance>> getArmyInstanceProcessors() {
		return this.armyInstanceProcessors;
	}

	public Collection<EntityProcessor<Army>> getArmyProcessors() {
		return this.armyProcessors;
	}

	public Collection<EntityProcessor<Equipment>> getEquipmentProcessors() {
		return this.equipmentProcessors;
	}

	public Army preprocess(Army a) throws WhadlException {
		log.debug("Preprocessing " + a);
		for (EntityProcessor<Army> p : this.armyProcessors) {
			log.debug("Applying processor " + p);
			a = p.process(a);
		}
		log.debug("Done.");
		return a;
	}

	public ArmyInstance preprocess(ArmyInstance ai) throws WhadlException {
		log.debug("Preprocessing " + ai);
		
		for (EntityProcessor<ArmyInstance> p : this.armyInstanceProcessors) {
			log.debug("Applying processor " + p);
			ai = p.process(ai);
		}
		log.debug("Done.");
		return ai;
	}

	public Equipment preprocess(Equipment e) throws WhadlException {
		log.debug("Preprocessing " + e);
		for (EntityProcessor<Equipment> p : this.equipmentProcessors) {
			log.debug("Applying processor " + p);
			e = p.process(e);
		}
		log.debug("Done.");
		return e;
	}
}
