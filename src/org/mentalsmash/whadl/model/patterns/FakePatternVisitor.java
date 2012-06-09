package org.mentalsmash.whadl.model.patterns;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakePatternVisitor implements PatternMatchObserver {
	private static final Logger log = LoggerFactory
			.getLogger(FakePatternVisitor.class);

	@Override
	public void match(Pattern rule, Map<String, Integer> entities,
			Pattern result) {
		log.trace("MATCHED: RULE=" + rule + "; ENTITIES=" + entities
				+ "; RESULT=" + result);

	}

	@Override
	public void none(Pattern rule, Map<String, Integer> entities) {
		log.trace("NO MATCH: RULE=" + rule + "; ENTITIES=" + entities);

	}

	@Override
	public void partial(Pattern rule, Map<String, Integer> entities,
			Map<String, Integer> unmatched, Pattern result) {
		log.trace("PARTIAL: RULE=" + rule + "; ENTITIES=" + entities
				+ "; UNMATCHED=" + unmatched + "; RESULT=" + result);

	}

}
