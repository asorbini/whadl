package org.mentalsmash.whadl.plugins;

import java.io.File;

import org.mentalsmash.whadl.Whadl;
import org.mentalsmash.whadl.WhadlException;
import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.plugins.gw40k.armies.GW40kArmiesWhadlPlugin;
import org.mentalsmash.whadl.plugins.gw40k.core.GW40kCoreWhadlPlugin;

public class WhadlPlus {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(WhadlPlus.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Whadl.configureLog();

		Whadl w = new Whadl(10);
		
		try {
			w.loadPlugin(GW40kCoreWhadlPlugin.class);
		} catch (WhadlException ex) {
			log.error("Cannot load plugin " + GW40kCoreWhadlPlugin.class, ex);
			throw new WhadlRuntimeException(ex);
		}
		try {
			w.loadPlugin(GW40kArmiesWhadlPlugin.class);
		} catch (WhadlException ex) {
			log.error("Cannot load plugin " + GW40kArmiesWhadlPlugin.class, ex);
			throw new WhadlRuntimeException(ex);
		}

		for (String buildFile : args) {
			try {
				ArmyInstance ai = w.parseArmyInstance(new File(buildFile));
				w.validate(ai);
			} catch (Exception ex) {
				log.error("Error parsing file " + buildFile, ex);
			}
		}

	}

}
