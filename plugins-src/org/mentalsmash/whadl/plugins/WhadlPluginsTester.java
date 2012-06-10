package org.mentalsmash.whadl.plugins;

import org.mentalsmash.whadl.Whadl;
import org.mentalsmash.whadl.plugins.gw40k.armies.GW40kArmiesWhadlPlugin;
import org.mentalsmash.whadl.plugins.gw40k.builds.GW40kBuildsWhadlPlugin;
import org.mentalsmash.whadl.plugins.gw40k.core.GW40kCoreWhadlPlugin;

public class WhadlPluginsTester {
	public static void main(String[] args) {
		Whadl.configureLog();
		Whadl w = new Whadl();

		try {
			w.loadPlugin(GW40kCoreWhadlPlugin.class);
			w.loadPlugin(GW40kArmiesWhadlPlugin.class);
			w.loadPlugin(GW40kBuildsWhadlPlugin.class);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
