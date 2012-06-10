package org.mentalsmash.whadl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.mentalsmash.whadl.plugins.WhadlPlugin;
import org.mentalsmash.whadl.validation.DynamicEntityContext;
import org.mentalsmash.whadl.validation.WhadlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

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

	public static final String FILE_EXTENSION_ARMY_DEF = ".army";
	public static final String FILE_EXTENSION_ARMY_BUILD = ".build";
	public static final String BASE_DATA_FILE_NAME = "BaseData"
			+ FILE_EXTENSION_ARMY_DEF;

	public static final String LOGBACK_CFG_PATH = "org/mentalsmash/whadl/logback.xml";

	public Whadl() {
		this(DEFAULT_MAX_VALIDATOR_TRIES);
	}

	public Whadl(int validatorMaxTries) {
		super(DEFAULT_PREFIX, new LiteralExpression(true));
		this.defaultContext = new DynamicEntityContext(this);
		this.defaultValidatorMaxTries = validatorMaxTries;
	}

	@Deprecated
	public Whadl(String dataDir) throws WhadlException, FileNotFoundException {
		this(DEFAULT_MAX_VALIDATOR_TRIES, dataDir);
	}

	@Deprecated
	public Whadl(int validatorMaxTries, String dataDir) throws WhadlException,
			FileNotFoundException {
		this(validatorMaxTries);
		this.validateAndLoadDataDir(dataDir, true);
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

		// Army baseArmy = this.getArmy("BaseArmy");
		// if (baseArmy != null) {
		// prefixes.add(baseArmy.getReference());
		// } else {
		// if (!a.getName().equals("BaseArmy")) {
		// throw new WhadlRuntimeException(
		// "Missing Whadl BaseData! Cannot proceed with "
		// + a.getName());
		// }
		// }
		for (Army army : this.validatedArmies) {
			prefixes.add(army.getReference());
		}
		/* TODO Create default context that contains all loaded references */

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

		Army baseArmy = this.getArmy("BaseArmy");

		int i = 0;
		for (Army a : this.getArmies()) {
			System.err.println("LOADED ARMY " + i + ": " + a);
			i++;
		}
		if (i == 0) {
			System.err.println("NO ARMY LOADED!");
		}

		if (baseArmy == null) {
			throw new RuntimeException("BaseArmy not loaded!");
		}

		prefixes.add(baseArmy.getReference());

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

	public boolean validate(ArmyInstance ai, boolean store)
			throws SemanticException {
		return this.validate(ai, this.defaultValidatorMaxTries, store);
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
		log.info("ArmyInstance stored : " + ai);
	}

	public static void main(String[] args) throws Exception {
		Whadl.configureLog();

		Whadl w = new Whadl(10, "data");

		for (String buildFile : args) {
			ArmyInstance ai = w.parseArmyInstance(new File(buildFile));
			w.validate(ai);
		}

	}

	public static void configureLog() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);
			lc.reset();
			InputStream logbackInputStream = Whadl.class.getClassLoader()
					.getResourceAsStream(LOGBACK_CFG_PATH);
			if (logbackInputStream == null) {
				throw new FileNotFoundException(LOGBACK_CFG_PATH);
			}
			configurator.doConfigure(logbackInputStream);
		} catch (Exception ex) {
			// log.error("Error configuring logger",ex);
			ex.printStackTrace();
		}
		StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
	}

	public void loadArmyFile(String filePath) throws WhadlException,
			FileNotFoundException {
		InputStream armyInputStream = this.getClass().getClassLoader()
				.getResourceAsStream(filePath);

		if (armyInputStream != null) {
			InputStreamReader reader = new InputStreamReader(armyInputStream);
			Army a = this.parseArmy(reader);
			this.validate(a, true);
		} else {
			throw new FileNotFoundException(filePath);
		}

	}

	@Deprecated
	private void validateAndLoadDataDir(String dataDirPath, boolean load)
			throws WhadlException, FileNotFoundException {
		File dataDir = new File("data");

		if (!dataDir.exists() || !dataDir.isDirectory()) {
			throw new WhadlException("Can't find \"data/\" directory!");
		}

		File[] baseDataFileSearch = dataDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.equals(Whadl.BASE_DATA_FILE_NAME)) {
					return true;
				} else {
					return false;
				}
			}
		});

		if (baseDataFileSearch.length == 0) {
			throw new WhadlException("Can't find " + Whadl.BASE_DATA_FILE_NAME
					+ " in " + dataDir);
		}

		File baseDataFile = baseDataFileSearch[0];

		Army baseArmy = this.parseArmy(baseDataFile);
		this.validate(baseArmy, true);

		File[] filesToLoad = dataDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(Whadl.FILE_EXTENSION_ARMY_DEF)
						&& !name.equals(Whadl.BASE_DATA_FILE_NAME)) {
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
			try {
				this.loadArmyBuild(buildFileName);
			} catch (WhadlException e) {
				log.error("Cannot load build from file : " + buildFileName);
			}

		}
	}

	public void loadArmyBuild(String buildFileName) throws WhadlException {
		InputStream armyInputStream = this.getClass().getClassLoader()
				.getResourceAsStream(buildFileName);
		InputStreamReader reader = null;

		if (armyInputStream != null) {
			reader = new InputStreamReader(armyInputStream);
		} else {
			throw new WhadlException(new FileNotFoundException(buildFileName));
		}

		ArmyInstance ai = null;

		try {
			ai = this.parseArmyInstance(reader);
			log.info("FILE=" + buildFileName + " - LOADED - ArmyBuild="
					+ ai.getName());
		} catch (Exception ex) {
			log.error("FILE=" + buildFileName + " - NOT LOADED - ArmyBuild="
					+ ai.getName());
			throw new WhadlException(ex);
		}

		try {
			this.validate(ai, true);
			log.info("FILE=" + buildFileName + " - VALIDATED - ArmyBuild="
					+ ai.getName() + ", TOTAL POINTS: " + ai.getFinalCost());
		} catch (Exception ex) {
			log.error("FILE=" + buildFileName + " - NOT VALIDATED - ArmyBuild="
					+ ai.getName());
			throw new WhadlException(ex);
		}
	}

	public void loadPlugin(Class<?> pluginClass) throws WhadlException {
		WhadlPlugin plugin = null;

		try {

			plugin = (WhadlPlugin) pluginClass.getConstructor().newInstance();

		} catch (Exception ex) {

			log.error("Cannot instantiate WhadlPlugin from class "
					+ pluginClass.getCanonicalName(), ex);
			throw new WhadlException(ex);
		}

		for (String armyDefFile : plugin.getArmyDefinitionFiles().keySet()) {
			try {
				log.info("Loading Army definition from {}", armyDefFile);
				this.loadArmyFile(armyDefFile);
			} catch (FileNotFoundException e) {
				log.error("Cannot load Army definition file: " + armyDefFile, e);
			}
		}

		for (String armyBuildFile : plugin.getArmyBuildFiles().keySet()) {
			try {
				log.info("Loading Army build from {}", armyBuildFile);
				this.loadArmyBuild(armyBuildFile);
			} catch (Exception e) {
				log.error("Cannot load Army build file: " + armyBuildFile, e);
			}
		}
	}
}
