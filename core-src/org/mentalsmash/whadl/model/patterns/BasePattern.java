package org.mentalsmash.whadl.model.patterns;

import java.util.HashMap;
import java.util.Map;

public abstract class BasePattern implements Pattern {

	protected final Map<String, String> descriptions = new HashMap<String, String>();
	
	@Override
	public void getDescription(String label, String description) {
		this.descriptions.put(label, description);
	}

	@Override
	public Map<String, String> getDescriptions() {
		return this.descriptions;
	}

}
