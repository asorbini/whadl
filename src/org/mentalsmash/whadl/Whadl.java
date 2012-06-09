package org.mentalsmash.whadl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.BaseEntity;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.parser.ParseException;
import org.mentalsmash.whadl.parser.WhadlParser;
import org.mentalsmash.whadl.parser.nodes.ArmyBuildDefinition;
import org.mentalsmash.whadl.parser.nodes.ArmyDefinition;
import org.mentalsmash.whadl.parser.visitor.ast.ArmyGenerator;
import org.mentalsmash.whadl.parser.visitor.ast.ArmyInstanceGenerator;
import org.mentalsmash.whadl.validation.DynamicEntityContext;
import org.mentalsmash.whadl.validation.WhadlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import ch.qos.logback.classic.LoggerContext;
//import ch.qos.logback.classic.joran.JoranConfigurator;
//import ch.qos.logback.core.joran.spi.JoranException;
//import ch.qos.logback.core.util.StatusPrinter;

public class Whadl extends BaseEntity {

	private final static Logger log = LoggerFactory.getLogger(Whadl.class);

	private final Set<Army> validatedArmies = new HashSet<Army>();
	private final Set<ArmyInstance> validatedBuilds = new HashSet<ArmyInstance>();

	private final DynamicEntityContext defaultContext;
	private int defaultValidatorMaxTries;

	public static final int DEFAULT_MAX_VALIDATOR_TRIES = 10;
	private static final String DEFAULT_PREFIX = "Whadl";

	public Whadl() {
		this(DEFAULT_MAX_VALIDATOR_TRIES);
	}

	public Whadl(int validatorMaxTries) {
		super(DEFAULT_PREFIX, new LiteralExpression(true));
		this.defaultContext = new DynamicEntityContext(this);
		this.defaultValidatorMaxTries = validatorMaxTries;
	}

	public Whadl(String dataDir) throws WhadlException, FileNotFoundException {
		this(DEFAULT_MAX_VALIDATOR_TRIES, dataDir);
	}

	public Whadl(int validatorMaxTries, String dataDir) throws WhadlException,
			FileNotFoundException {
		this(validatorMaxTries);
		this.validateData(dataDir,true);
		// this.loadBuilds(builds);
	}

	public Collection<Army> getArmies() {
		return this.validatedArmies;
	}

	public Army getArmy(String name) {
		for (Army a : this.validatedArmies) {
			if (a.getName().equals(name)) {
				return a;
			}
		}

		return null;
	}

	public ArmyInstance getArmyInstance(String name) {
		for (ArmyInstance ai : this.validatedBuilds) {
			if (ai.getName().equals(name)) {
				return ai;
			}
		}

		return null;
	}

	public Collection<ArmyInstance> getArmyInstances() {
		return this.validatedBuilds;
	}

	public Army parseArmy(String definitionStr) throws SyntaxException,
			SemanticException {
		StringReader in = new StringReader(definitionStr);
		return this.parseArmy(in);
	}

	public Army parseArmy(File f) throws FileNotFoundException,
			SyntaxException, SemanticException {
		log.info("Loading Army from " + f);
		FileReader reader = new FileReader(f);
		return this.parseArmy(reader);
	}

	public Army parseArmy(Reader reader) throws SyntaxException,
			SemanticException {
		try {

			WhadlParser parser = new WhadlParser(reader);

			Army a = null;
			ArmyDefinition def = null;

			try {
				def = parser.ArmyDefinition();
			} catch (ParseException e) {
				throw new SyntaxException(e);
			}

			try {
				ArmyGenerator visitor = new ArmyGenerator();
				a = visitor.visit(def);
			} catch (Exception e) {
				throw new SemanticException(a, e);
			}

			return a;
		} catch (RuntimeException ex) {
			throw new WhadlRuntimeException(ex);
		}
	}

	public boolean validate(Army a, boolean load) throws SemanticException {

		DynamicEntityContext ctx = this.computeEntityContext(a);

		log.info("Computed Army context for " + a + ": ");
		// ctx.dumpToLog();

		WhadlValidator validator = new WhadlValidator(ctx,
				this.defaultValidatorMaxTries);

		try {
			validator.preprocess(a);
		} catch (WhadlException e) {
			throw new SemanticException(a, e);
		}

		log.info("Validated Army: " + a);

		if (load) {
			try {
				this.defaultContext.merge(ctx, false, false);
				this.validatedArmies.add(a);
				log.info("Loaded Army: " + a);
			} catch (WhadlException ex) {
				throw new SemanticException(a, ex);
			}
		}

		return true;
	}

	protected DynamicEntityContext computeEntityContext(Army a) {

		ArrayList<String> prefixes = new ArrayList<String>();

		Army baseArmy = this.getArmy("BaseArmy");
		if (baseArmy != null) {
			prefixes.add(baseArmy.getReference());
		} else {
			if (!a.getName().equals("BaseArmy")) {
				throw new WhadlRuntimeException(
						"Missing Whadl BaseData! Cannot proceed with "
								+ a.getName());
			}
		}

		if (!a.getSuperPattern().isDefault()) {
			for (String sup : a.getSuperPattern().toMap().keySet()) {
				if (!sup.startsWith(DEFAULT_PREFIX + ".")) {
					prefixes.add(DEFAULT_PREFIX + "." + sup);
				} else {
					prefixes.add(sup);
				}
			}
		}

		DynamicEntityContext ctx = this.defaultContext.extractContext(prefixes
				.toArray(new String[0]));

		return ctx;
	}

	protected DynamicEntityContext computeEntityContext(ArmyInstance ai) {

		ArrayList<String> prefixes = new ArrayList<String>();

		// this.defaultContext.dumpToLog();

		prefixes.add(this.getArmy("BaseArmy").getReference());

		if (!ai.getTypeName().startsWith(DEFAULT_PREFIX + ".")) {
			prefixes.add(DEFAULT_PREFIX + "." + ai.getTypeName());
		} else {
			prefixes.add(ai.getTypeName());
		}

		DynamicEntityContext ctx = this.defaultContext.extractContext(prefixes
				.toArray(new String[0]));

		return ctx;
	}

	public ArmyInstance parseArmyInstance(String definitionStr)
			throws SyntaxException, SemanticException {
		log.info("Loading Build from supplied string");
		StringReader in = new StringReader(definitionStr);
		return this.parseArmyInstance(in);
	}

	public boolean validate(ArmyInstance ai) throws SemanticException {
		return this.validate(ai, this.defaultValidatorMaxTries, false);
	}

	public ArmyInstance parseArmyInstance(File buildFile)
			throws FileNotFoundException, SyntaxException, SemanticException {
		log.info("Loading Build from " + buildFile);
		FileReader in = new FileReader(buildFile);
		return this.parseArmyInstance(in);
	}

	public ArmyInstance parseArmyInstance(Reader in) throws SyntaxException,
			SemanticException {
		try {

			WhadlParser parser = new WhadlParser(in);
			ArmyInstance ai = null;
			ArmyBuildDefinition def = null;

			try {
				def = parser.ArmyBuildDefinition();
			} catch (ParseException e) {
				throw new SyntaxException(e);
			}

			try {
				ArmyInstanceGenerator visitor = new ArmyInstanceGenerator();
				ai = visitor.visit(def);
			} catch (Exception e) {
				throw new SemanticException(ai, e);
			}

			return ai;
		} catch (RuntimeException ex) {
			throw new WhadlRuntimeException(ex);
		}
	}

	public boolean validate(ArmyInstance ai, int maxValidatorTries, boolean save)
			throws SemanticException {

		DynamicEntityContext ctx = this.computeEntityContext(ai);

		WhadlValidator validator = new WhadlValidator(ctx, maxValidatorTries);

		try {
			validator.preprocess(ai);
			validator.validate(ai);
		} catch (Exception e) {
			throw new SemanticException(ai, e);
		}

		log.info("ArmyInstance validated: " + ai);

		if (save) {
			store(ai);
		}

		return true;
	}

	private void store(ArmyInstance ai) {
		this.validatedBuilds.add(ai);
		log.info("ArmyInstance stored : "+ai);
	}

	public static void main(String[] args) throws Exception {
//		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//
//		try {
//			JoranConfigurator configurator = new JoranConfigurator();
//			configurator.setContext(lc);
//			lc.reset();
//			configurator.doConfigure("data/logback.xml");
//		} catch (JoranException je) {
//			// StatusPrinter will handle this
//		}
//		StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

		Whadl w = new Whadl(10, "data");

		for (String buildFile : args) {
			ArmyInstance ai = w.parseArmyInstance(new File(buildFile));
			w.validate(ai);
		}

	}

	private void validateData(String dataDirPath, boolean load)
			throws WhadlException, FileNotFoundException {
		File dataDir = new File("data");

		if (!dataDir.exists() || !dataDir.isDirectory()) {
			throw new WhadlException("Can't find \"data/\" directory!");
		}

		final String baseDataFileName = "BaseData.whadl";

		File[] baseDataFileSearch = dataDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.equals(baseDataFileName)) {
					return true;
				} else {
					return false;
				}
			}
		});

		if (baseDataFileSearch.length == 0) {
			throw new WhadlException("Can't find " + baseDataFileName + " in "
					+ dataDir);
		}

		File baseDataFile = baseDataFileSearch[0];

		Army baseArmy = this.parseArmy(baseDataFile);
		this.validate(baseArmy, true);

		File[] filesToLoad = dataDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".whadl") && !name.equals(baseDataFileName)) {
					return true;
				} else {
					return false;
				}
			}
		});

		for (File armyFile : filesToLoad) {

			Army a = this.parseArmy(armyFile);
			this.validate(a, load);
			System.out.println("FILE=" + armyFile.getName() + " - LOADED Army="
					+ a.getName());

		}
	}

	public void loadBuilds(String[] builds) {
		for (String buildFileName : builds) {
			ArmyInstance ai = null;

			try {
				ai = this.parseArmyInstance(new File(buildFileName));
				log.info("FILE=" + buildFileName + " - LOADED - ArmyInstance="
						+ ai.getName());
			} catch (Exception ex) {
				log.error("FILE=" + buildFileName
						+ " - NOT LOADED - ArmyInstance=" + ai.getName());
			}

			try {
				this.validate(ai);
				log.info("FILE=" + buildFileName
						+ " - VALIDATED - ArmyInstance=" + ai.getName()
						+ ", TOTAL POINTS: " + ai.getFinalCost());
			} catch (Exception ex) {
				log.error("FILE=" + buildFileName
						+ " - NOT VALIDATED - ArmyInstance=" + ai.getName());
			}

		}
	}
}
