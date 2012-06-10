package org.mentalsmash.whadl.plugins;

import java.util.Collection;
import java.util.Map;

public interface WhadlPlugin {

	
	public Map<String,String> getArmyDefinitionFiles();
	public Map<String,String> getArmyBuildFiles();
	
}
