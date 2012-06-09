package org.mentalsmash.whadl.model.patterns;

import java.util.Map;

public interface PatternMatchObserver {
	public void match(Pattern rule, Map<String, Integer> entities,
			Pattern result);

	public void partial(Pattern rule, Map<String, Integer> entities,
			Map<String, Integer> unmatched, Pattern result);
	
	public void none(Pattern rule, Map<String, Integer> entities);
}
